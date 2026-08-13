# 타임시리즈 DB 풀이 해설

## 📌 핵심 아이디어

타임시리즈 DB는 **시간 순서**로 정렬된 데이터에 최적화됩니다.
NavigableMap(TreeMap)을 사용하여 효율적인 범위 쿼리를 지원합니다.

**핵심 특징**:
- 시간 기반 인덱싱
- 범위 쿼리 최적화
- 집계 연산 지원
- 다운샘플링으로 저장 공간 절약

---

## 🔑 핵심 개념

### 1. 데이터 구조
```java
// 3단계 맵 구조
// Level 1: Metric 이름
// Level 2: Tag 조합
// Level 3: Timestamp → Value

Map<String,                           // metric
    Map<TagSet,                       // tags
        NavigableMap<Instant, Double>>> // time → value

예:
{
  "cpu_usage": {
    {host:s1}: {10:00→45, 10:01→50, 10:02→48},
    {host:s2}: {10:00→30, 10:01→35, 10:02→32}
  },
  "memory_usage": {
    {host:s1}: {10:00→1024, 10:01→1100}
  }
}
```

### 2. 범위 쿼리
```java
// TreeMap/ConcurrentSkipListMap의 subMap 활용
NavigableMap<Instant, Double> timeSeries = getTimeSeries(metric, tags);

// start(포함) ~ end(포함) 범위
SortedMap<Instant, Double> range = timeSeries.subMap(start, true, end, true);

// 또는 NavigableMap 메서드
NavigableMap<Instant, Double> range = timeSeries.subMap(start, true, end, true);
```

### 3. 집계 함수
```java
public enum AggregateFunction {
    AVG, SUM, MIN, MAX, COUNT, FIRST, LAST
}

public double aggregate(List<Double> values, AggregateFunction func) {
    return switch (func) {
        case AVG -> values.stream().mapToDouble(d -> d).average().orElse(0);
        case SUM -> values.stream().mapToDouble(d -> d).sum();
        case MIN -> values.stream().mapToDouble(d -> d).min().orElse(0);
        case MAX -> values.stream().mapToDouble(d -> d).max().orElse(0);
        case COUNT -> values.size();
        case FIRST -> values.isEmpty() ? 0 : values.get(0);
        case LAST -> values.isEmpty() ? 0 : values.get(values.size() - 1);
    };
}
```

---

## 📝 POP 구현 해설

### 완전한 구현
```java
public class TimeSeriesDB {
    private final Map<String, Map<TagSet, NavigableMap<Instant, Double>>> data;
    private final Map<String, Duration> retentionPolicies;
    
    public TimeSeriesDB() {
        this.data = new ConcurrentHashMap<>();
        this.retentionPolicies = new ConcurrentHashMap<>();
    }
    
    // 데이터 쓰기
    public void write(String metric, Map<String, String> tags, 
                      Instant timestamp, double value) {
        TagSet tagSet = new TagSet(tags);
        
        data.computeIfAbsent(metric, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(tagSet, k -> new ConcurrentSkipListMap<>())
            .put(timestamp, value);
    }
    
    // 배치 쓰기
    public void writeBatch(String metric, Map<String, String> tags,
                           List<DataPoint> points) {
        TagSet tagSet = new TagSet(tags);
        NavigableMap<Instant, Double> timeSeries = 
            data.computeIfAbsent(metric, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(tagSet, k -> new ConcurrentSkipListMap<>());
        
        for (DataPoint point : points) {
            timeSeries.put(point.timestamp(), point.value());
        }
    }
    
    // 시간 범위 쿼리
    public List<DataPoint> query(String metric, Map<String, String> tagFilter,
                                 Instant start, Instant end) {
        List<DataPoint> result = new ArrayList<>();
        
        Map<TagSet, NavigableMap<Instant, Double>> metricData = data.get(metric);
        if (metricData == null) return result;
        
        for (Map.Entry<TagSet, NavigableMap<Instant, Double>> entry : 
             metricData.entrySet()) {
            
            TagSet tagSet = entry.getKey();
            if (!tagSet.matches(tagFilter)) continue;
            
            NavigableMap<Instant, Double> range = 
                entry.getValue().subMap(start, true, end, true);
            
            for (Map.Entry<Instant, Double> point : range.entrySet()) {
                result.add(new DataPoint(
                    metric, tagSet.getTags(), 
                    point.getKey(), point.getValue()
                ));
            }
        }
        
        // 시간순 정렬
        result.sort(Comparator.comparing(DataPoint::timestamp));
        return result;
    }
    
    // 집계 쿼리
    public double aggregate(String metric, Map<String, String> tagFilter,
                            Instant start, Instant end,
                            AggregateFunction func) {
        List<DataPoint> points = query(metric, tagFilter, start, end);
        List<Double> values = points.stream()
            .map(DataPoint::value)
            .toList();
        
        return calculate(values, func);
    }
    
    // 다운샘플링
    public List<DataPoint> downsample(String metric, Map<String, String> tagFilter,
                                       Instant start, Instant end,
                                       Duration interval,
                                       AggregateFunction func) {
        List<DataPoint> points = query(metric, tagFilter, start, end);
        List<DataPoint> result = new ArrayList<>();
        
        // 버킷별로 그룹화
        Map<Instant, List<Double>> buckets = new TreeMap<>();
        
        for (DataPoint point : points) {
            Instant bucket = getBucketStart(point.timestamp(), start, interval);
            buckets.computeIfAbsent(bucket, k -> new ArrayList<>())
                   .add(point.value());
        }
        
        // 각 버킷 집계
        for (Map.Entry<Instant, List<Double>> bucket : buckets.entrySet()) {
            double aggregated = calculate(bucket.getValue(), func);
            result.add(new DataPoint(
                metric, tagFilter, bucket.getKey(), aggregated
            ));
        }
        
        return result;
    }
    
    private Instant getBucketStart(Instant timestamp, Instant origin, 
                                    Duration interval) {
        long millisSinceOrigin = timestamp.toEpochMilli() - origin.toEpochMilli();
        long bucketIndex = millisSinceOrigin / interval.toMillis();
        return origin.plusMillis(bucketIndex * interval.toMillis());
    }
    
    private double calculate(List<Double> values, AggregateFunction func) {
        if (values.isEmpty()) return 0;
        
        return switch (func) {
            case AVG -> values.stream().mapToDouble(d -> d).average().orElse(0);
            case SUM -> values.stream().mapToDouble(d -> d).sum();
            case MIN -> values.stream().mapToDouble(d -> d).min().orElse(0);
            case MAX -> values.stream().mapToDouble(d -> d).max().orElse(0);
            case COUNT -> values.size();
            case FIRST -> values.get(0);
            case LAST -> values.get(values.size() - 1);
        };
    }
    
    // 보관 정책 설정
    public void setRetentionPolicy(String metric, Duration retention) {
        retentionPolicies.put(metric, retention);
    }
    
    // 만료 데이터 삭제
    public void enforceRetention() {
        Instant now = Instant.now();
        
        for (Map.Entry<String, Duration> policy : retentionPolicies.entrySet()) {
            String metric = policy.getKey();
            Duration retention = policy.getValue();
            Instant cutoff = now.minus(retention);
            
            Map<TagSet, NavigableMap<Instant, Double>> metricData = data.get(metric);
            if (metricData == null) continue;
            
            for (NavigableMap<Instant, Double> timeSeries : metricData.values()) {
                // cutoff 이전 데이터 삭제
                timeSeries.headMap(cutoff).clear();
            }
        }
    }
}
```

### DataPoint Record
```java
public record DataPoint(
    String metric,
    Map<String, String> tags,
    Instant timestamp,
    double value
) {
    public DataPoint {
        tags = Map.copyOf(tags);  // 불변 보장
    }
}
```

### TagSet
```java
public class TagSet {
    private final Map<String, String> tags;
    private final int hashCode;
    
    public TagSet(Map<String, String> tags) {
        this.tags = Map.copyOf(tags);
        this.hashCode = this.tags.hashCode();
    }
    
    public Map<String, String> getTags() {
        return tags;
    }
    
    public boolean matches(Map<String, String> filter) {
        if (filter.isEmpty()) return true;
        
        for (Map.Entry<String, String> e : filter.entrySet()) {
            String value = tags.get(e.getKey());
            if (value == null || !value.equals(e.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagSet other)) return false;
        return tags.equals(other.tags);
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }
    
    @Override
    public String toString() {
        return tags.toString();
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 |
|------|-----------|
| write | O(log n) |
| query (범위) | O(log n + k) |
| aggregate | O(log n + k) |
| downsample | O(log n + k) |

n = 해당 시리즈의 데이터 포인트 수
k = 결과 크기

---

## ❌ 흔한 실수

### 1. TagSet equals/hashCode
```java
// 잘못됨: equals/hashCode 미구현
class TagSet {
    private Map<String, String> tags;
    // equals, hashCode 없음!
}
// HashMap에서 제대로 동작 안 함

// 올바름: 반드시 구현
@Override
public boolean equals(Object o) { ... }

@Override
public int hashCode() { ... }
```

### 2. 태그 필터 null 체크
```java
// 잘못됨: null 체크 누락
public boolean matches(Map<String, String> filter) {
    for (var e : filter.entrySet()) {
        if (!tags.get(e.getKey()).equals(e.getValue())) {
            return false;  // NPE 가능!
        }
    }
}

// 올바름: null 체크
String value = tags.get(e.getKey());
if (value == null || !value.equals(e.getValue())) {
    return false;
}
```

### 3. 버킷 계산
```java
// 잘못됨: 버킷 경계 오류
Instant bucket = timestamp.truncatedTo(ChronoUnit.MINUTES);
// 5분 단위가 아닌 분 단위로 잘림

// 올바름: 인터벌 기준 계산
long millis = timestamp.toEpochMilli();
long bucketMillis = (millis / intervalMillis) * intervalMillis;
Instant bucket = Instant.ofEpochMilli(bucketMillis);
```

---

## 🔗 관련 문제

- 모니터링 시스템 설계
- IoT 데이터 처리
- 금융 데이터 분석
- 로그 집계 시스템
