# oop/TimeSeriesStore.java

OOP 인터페이스 + Builder 기반 구현. 배치 write 지원.

```java
package com.datastructure.timeseriesdb.oop;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

public interface TimeSeriesStore {
    enum AggregateFunction { AVG, SUM, MIN, MAX, COUNT, FIRST, LAST }

    void write(String metric, Map<String, String> tags, Instant timestamp, double value);
    void writeBatch(List<DataPoint> points);
    List<DataPoint> query(String metric, Map<String, String> tagFilter,
                          Instant start, Instant end);
    OptionalDouble aggregate(String metric, Map<String, String> tagFilter,
                             Instant start, Instant end, AggregateFunction fn);
    List<DataPoint> downsample(String metric, Map<String, String> tagFilter,
                               Instant start, Instant end,
                               Duration interval, AggregateFunction fn);
    int applyRetention(String metric, Duration retention);

    class DataPoint {
        public final String metric;
        public final Map<String, String> tags;
        public final Instant timestamp;
        public final double value;

        public DataPoint(String metric, Map<String, String> tags,
                         Instant timestamp, double value) {
            this.metric = metric;
            this.tags = tags;
            this.timestamp = timestamp;
            this.value = value;
        }
    }
}
```

---

# oop/InMemoryTimeSeriesStore.java

```java
package com.datastructure.timeseriesdb.oop;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class InMemoryTimeSeriesStore implements TimeSeriesStore {

    private static class TagSet {
        final Map<String, String> tags;
        final int hash;

        TagSet(Map<String, String> tags) {
            this.tags = Map.copyOf(tags);
            this.hash = this.tags.hashCode();
        }

        boolean matches(Map<String, String> filter) {
            for (Map.Entry<String, String> e : filter.entrySet()) {
                if (!Objects.equals(e.getValue(), tags.get(e.getKey()))) return false;
            }
            return true;
        }

        @Override public boolean equals(Object o) {
            return o instanceof TagSet && ((TagSet) o).tags.equals(tags);
        }
        @Override public int hashCode() { return hash; }
    }

    private final Map<String, Map<TagSet, ConcurrentSkipListMap<Instant, Double>>> data
            = new ConcurrentHashMap<>();

    @Override
    public void write(String metric, Map<String, String> tags, Instant ts, double value) {
        TagSet key = new TagSet(tags == null ? Map.of() : tags);
        data.computeIfAbsent(metric, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(key, k -> new ConcurrentSkipListMap<>())
                .put(ts, value);
    }

    @Override
    public void writeBatch(List<DataPoint> points) {
        for (DataPoint p : points) write(p.metric, p.tags, p.timestamp, p.value);
    }

    @Override
    public List<DataPoint> query(String metric, Map<String, String> filter,
                                 Instant start, Instant end) {
        List<DataPoint> out = new ArrayList<>();
        Map<TagSet, ConcurrentSkipListMap<Instant, Double>> series = data.get(metric);
        if (series == null) return out;
        Map<String, String> f = filter == null ? Map.of() : filter;
        for (Map.Entry<TagSet, ConcurrentSkipListMap<Instant, Double>> e : series.entrySet()) {
            if (!e.getKey().matches(f)) continue;
            for (Map.Entry<Instant, Double> p : e.getValue().subMap(start, true, end, true).entrySet()) {
                out.add(new DataPoint(metric, e.getKey().tags, p.getKey(), p.getValue()));
            }
        }
        out.sort(Comparator.comparing(p -> p.timestamp));
        return out;
    }

    @Override
    public OptionalDouble aggregate(String metric, Map<String, String> filter,
                                    Instant start, Instant end, AggregateFunction fn) {
        List<DataPoint> pts = query(metric, filter, start, end);
        if (pts.isEmpty()) return OptionalDouble.empty();
        return OptionalDouble.of(apply(pts, fn));
    }

    @Override
    public List<DataPoint> downsample(String metric, Map<String, String> filter,
                                      Instant start, Instant end,
                                      Duration interval, AggregateFunction fn) {
        long stepMs = interval.toMillis();
        if (stepMs <= 0) throw new IllegalArgumentException();
        List<DataPoint> out = new ArrayList<>();
        Instant cur = start;
        while (!cur.isAfter(end)) {
            Instant bucketEnd = cur.plusMillis(stepMs - 1);
            if (bucketEnd.isAfter(end)) bucketEnd = end;
            List<DataPoint> bucket = query(metric, filter, cur, bucketEnd);
            if (!bucket.isEmpty()) {
                out.add(new DataPoint(metric, filter == null ? Map.of() : filter,
                        cur, apply(bucket, fn)));
            }
            cur = cur.plusMillis(stepMs);
        }
        return out;
    }

    @Override
    public int applyRetention(String metric, Duration retention) {
        Instant cutoff = Instant.now().minus(retention);
        Map<TagSet, ConcurrentSkipListMap<Instant, Double>> series = data.get(metric);
        if (series == null) return 0;
        int removed = 0;
        for (ConcurrentSkipListMap<Instant, Double> m : series.values()) {
            NavigableMap<Instant, Double> head = m.headMap(cutoff, false);
            removed += head.size();
            head.clear();
        }
        return removed;
    }

    private double apply(List<DataPoint> pts, AggregateFunction fn) {
        switch (fn) {
            case SUM: return pts.stream().mapToDouble(p -> p.value).sum();
            case AVG: return pts.stream().mapToDouble(p -> p.value).average().orElse(0.0);
            case MIN: return pts.stream().mapToDouble(p -> p.value).min().orElse(Double.NaN);
            case MAX: return pts.stream().mapToDouble(p -> p.value).max().orElse(Double.NaN);
            case COUNT: return pts.size();
            case FIRST: return pts.get(0).value;
            case LAST: return pts.get(pts.size() - 1).value;
            default: throw new IllegalArgumentException();
        }
    }
}
```
