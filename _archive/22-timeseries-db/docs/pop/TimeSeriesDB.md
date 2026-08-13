# pop/TimeSeriesDB.java

태그 기반 메트릭 저장소. ConcurrentSkipListMap으로 시간 정렬. write/query/aggregate/downsample/retention.

```java
package com.datastructure.timeseriesdb.pop;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class TimeSeriesDB {

    public enum AggregateFunction { AVG, SUM, MIN, MAX, COUNT, FIRST, LAST }

    public static class DataPoint {
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

        @Override
        public String toString() {
            return metric + tags + "@" + timestamp + "=" + value;
        }
    }

    public static class TagSet {
        private final Map<String, String> tags;
        private final int hash;

        public TagSet(Map<String, String> tags) {
            this.tags = Map.copyOf(tags);
            this.hash = this.tags.hashCode();
        }

        public boolean matches(Map<String, String> filter) {
            for (Map.Entry<String, String> e : filter.entrySet()) {
                if (!Objects.equals(e.getValue(), tags.get(e.getKey()))) return false;
            }
            return true;
        }

        public Map<String, String> asMap() { return tags; }

        @Override
        public boolean equals(Object o) {
            return o instanceof TagSet && ((TagSet) o).tags.equals(tags);
        }

        @Override
        public int hashCode() { return hash; }
    }

    private final Map<String, Map<TagSet, ConcurrentSkipListMap<Instant, Double>>> data
            = new ConcurrentHashMap<>();

    public void write(String metric, Map<String, String> tags, Instant timestamp, double value) {
        Objects.requireNonNull(metric);
        TagSet ts = new TagSet(tags == null ? Map.of() : tags);
        data.computeIfAbsent(metric, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(ts, k -> new ConcurrentSkipListMap<>())
                .put(timestamp, value);
    }

    public List<DataPoint> query(String metric, Map<String, String> tagFilter,
                                 Instant start, Instant end) {
        List<DataPoint> result = new ArrayList<>();
        Map<TagSet, ConcurrentSkipListMap<Instant, Double>> series = data.get(metric);
        if (series == null) return result;

        Map<String, String> filter = tagFilter == null ? Map.of() : tagFilter;
        for (Map.Entry<TagSet, ConcurrentSkipListMap<Instant, Double>> entry : series.entrySet()) {
            if (!entry.getKey().matches(filter)) continue;
            NavigableMap<Instant, Double> sub = entry.getValue().subMap(start, true, end, true);
            for (Map.Entry<Instant, Double> p : sub.entrySet()) {
                result.add(new DataPoint(metric, entry.getKey().asMap(), p.getKey(), p.getValue()));
            }
        }
        result.sort(Comparator.comparing(p -> p.timestamp));
        return result;
    }

    public OptionalDouble aggregate(String metric, Map<String, String> tagFilter,
                                    Instant start, Instant end, AggregateFunction fn) {
        List<DataPoint> points = query(metric, tagFilter, start, end);
        if (points.isEmpty()) return OptionalDouble.empty();
        return OptionalDouble.of(applyAggregate(points, fn));
    }

    public List<DataPoint> downsample(String metric, Map<String, String> tagFilter,
                                      Instant start, Instant end,
                                      Duration interval, AggregateFunction fn) {
        List<DataPoint> result = new ArrayList<>();
        long stepMs = interval.toMillis();
        if (stepMs <= 0) throw new IllegalArgumentException("interval > 0");

        Instant bucketStart = start;
        while (!bucketStart.isAfter(end)) {
            Instant bucketEnd = bucketStart.plusMillis(stepMs - 1);
            if (bucketEnd.isAfter(end)) bucketEnd = end;

            List<DataPoint> bucket = query(metric, tagFilter, bucketStart, bucketEnd);
            if (!bucket.isEmpty()) {
                result.add(new DataPoint(metric,
                        tagFilter == null ? Map.of() : tagFilter,
                        bucketStart, applyAggregate(bucket, fn)));
            }
            bucketStart = bucketStart.plusMillis(stepMs);
        }
        return result;
    }

    private double applyAggregate(List<DataPoint> points, AggregateFunction fn) {
        switch (fn) {
            case SUM:
                return points.stream().mapToDouble(p -> p.value).sum();
            case AVG:
                return points.stream().mapToDouble(p -> p.value).average().orElse(0.0);
            case MIN:
                return points.stream().mapToDouble(p -> p.value).min().orElse(Double.NaN);
            case MAX:
                return points.stream().mapToDouble(p -> p.value).max().orElse(Double.NaN);
            case COUNT:
                return points.size();
            case FIRST:
                return points.get(0).value;
            case LAST:
                return points.get(points.size() - 1).value;
            default:
                throw new IllegalArgumentException();
        }
    }

    /** retention 기간 이전 데이터 삭제 */
    public int applyRetention(String metric, Duration retention) {
        Instant cutoff = Instant.now().minus(retention);
        Map<TagSet, ConcurrentSkipListMap<Instant, Double>> series = data.get(metric);
        if (series == null) return 0;
        int removed = 0;
        for (ConcurrentSkipListMap<Instant, Double> tsMap : series.values()) {
            NavigableMap<Instant, Double> head = tsMap.headMap(cutoff, false);
            removed += head.size();
            head.clear();
        }
        return removed;
    }

    public Set<String> getMetrics() {
        return data.keySet();
    }
}
```
