# 22. 타임시리즈 DB (Time Series Database)

## 📋 문제 정의

**시간 순서 데이터를 효율적으로 저장하고 조회**하는 
타임시리즈 데이터베이스를 구현하세요.

타임시리즈 DB는 IoT 센서 데이터, 모니터링 메트릭, 금융 데이터 등
시간에 따라 변하는 데이터를 처리하는 데 최적화되어 있습니다.

---

## 🎯 학습 목표

- 시간 기반 데이터 인덱싱
- 시간 범위 쿼리 최적화
- 데이터 압축 (다운샘플링)
- 집계 함수 (평균, 최대, 최소 등)
- 데이터 보관 정책 (Retention Policy)

---

## 📝 요구사항

### 핵심 개념

| 개념 | 설명 |
|------|------|
| **Metric** | 측정 항목 (예: cpu_usage, temperature) |
| **Tag** | 메타데이터 (예: host=server1, region=us) |
| **Timestamp** | 데이터 포인트의 시간 |
| **Value** | 측정값 |

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `write(metric, tags, timestamp, value)` | 데이터 포인트 저장 |
| `query(metric, tags, start, end)` | 시간 범위 조회 |
| `aggregate(metric, tags, start, end, func)` | 집계 쿼리 |
| `downsample(metric, interval, func)` | 다운샘플링 |

### 집계 함수

| 함수 | 설명 |
|------|------|
| `AVG` | 평균 |
| `SUM` | 합계 |
| `MIN` | 최소값 |
| `MAX` | 최대값 |
| `COUNT` | 개수 |
| `FIRST` | 첫 번째 값 |
| `LAST` | 마지막 값 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
TimeSeriesDB db = new TimeSeriesDB();

// 데이터 쓰기
db.write("cpu_usage", 
    Map.of("host", "server1", "region", "us-east"),
    Instant.parse("2024-01-01T10:00:00Z"),
    45.5);

db.write("cpu_usage",
    Map.of("host", "server1", "region", "us-east"),
    Instant.parse("2024-01-01T10:01:00Z"),
    52.3);

// 시간 범위 조회
List<DataPoint> points = db.query(
    "cpu_usage",
    Map.of("host", "server1"),
    Instant.parse("2024-01-01T10:00:00Z"),
    Instant.parse("2024-01-01T11:00:00Z")
);
```

### 예제 2: 집계 쿼리
```java
// 1시간 동안의 평균 CPU 사용률
double avgCpu = db.aggregate(
    "cpu_usage",
    Map.of("host", "server1"),
    start, end,
    AggregateFunction.AVG
);

// 최대값
double maxCpu = db.aggregate(
    "cpu_usage",
    Map.of("host", "server1"),
    start, end,
    AggregateFunction.MAX
);
```

### 예제 3: 다운샘플링
```java
// 1분 단위 데이터를 5분 평균으로 다운샘플링
List<DataPoint> downsampled = db.downsample(
    "cpu_usage",
    Map.of("host", "server1"),
    start, end,
    Duration.ofMinutes(5),
    AggregateFunction.AVG
);

// 결과: 5분 간격의 평균값들
// 10:00 → avg(10:00~10:05)
// 10:05 → avg(10:05~10:10)
// ...
```

### 예제 4: 태그 필터링
```java
// 모든 서버의 CPU 사용률
List<DataPoint> allServers = db.query(
    "cpu_usage",
    Map.of(),  // 빈 태그 = 모든 태그
    start, end
);

// 특정 지역의 서버들
List<DataPoint> usEast = db.query(
    "cpu_usage",
    Map.of("region", "us-east"),
    start, end
);
```

---

## 🔍 핵심 개념

### 데이터 모델
```
┌─────────────────────────────────────────────────┐
│ Metric: cpu_usage                                │
├─────────────────────────────────────────────────┤
│ Tags: {host: server1, region: us-east}          │
├────────────────────┬────────────────────────────┤
│ Timestamp          │ Value                      │
├────────────────────┼────────────────────────────┤
│ 2024-01-01 10:00   │ 45.5                       │
│ 2024-01-01 10:01   │ 52.3                       │
│ 2024-01-01 10:02   │ 48.7                       │
│ ...                │ ...                        │
└────────────────────┴────────────────────────────┘
```

### 인덱스 구조
```
                  TimeSeriesDB
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
    cpu_usage      memory_usage    disk_io
        │
        ├── {host:s1, region:us}
        │       └── [timestamp → value]
        │
        └── {host:s2, region:eu}
                └── [timestamp → value]
```

### 다운샘플링
```
원본 데이터 (1분 간격):
10:00=45, 10:01=50, 10:02=48, 10:03=52, 10:04=47

5분 평균으로 다운샘플링:
10:00 = avg(45, 50, 48, 52, 47) = 48.4

저장 공간: 5배 절약!
```

---

## 💡 힌트

### 기본 구조
```java
public class TimeSeriesDB {
    // metric → (tagSet → TreeMap<timestamp, value>)
    private final Map<String, Map<TagSet, NavigableMap<Instant, Double>>> data;
    
    public TimeSeriesDB() {
        this.data = new ConcurrentHashMap<>();
    }
    
    public void write(String metric, Map<String, String> tags, 
                      Instant timestamp, double value) {
        TagSet tagSet = new TagSet(tags);
        
        data.computeIfAbsent(metric, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(tagSet, k -> new ConcurrentSkipListMap<>())
            .put(timestamp, value);
    }
}
```

### TagSet
```java
public class TagSet {
    private final Map<String, String> tags;
    
    public TagSet(Map<String, String> tags) {
        this.tags = Map.copyOf(tags);  // 불변
    }
    
    // 태그 매칭 (부분 일치)
    public boolean matches(Map<String, String> filter) {
        for (Map.Entry<String, String> e : filter.entrySet()) {
            if (!e.getValue().equals(tags.get(e.getKey()))) {
                return false;
            }
        }
        return true;
    }
    
    // equals, hashCode 구현 필수!
}
```

### 시간 범위 쿼리
```java
public List<DataPoint> query(String metric, Map<String, String> tagFilter,
                             Instant start, Instant end) {
    List<DataPoint> result = new ArrayList<>();
    
    Map<TagSet, NavigableMap<Instant, Double>> metricData = data.get(metric);
    if (metricData == null) return result;
    
    for (Map.Entry<TagSet, NavigableMap<Instant, Double>> entry : 
         metricData.entrySet()) {
        
        if (entry.getKey().matches(tagFilter)) {
            // subMap으로 시간 범위 조회
            NavigableMap<Instant, Double> subMap = 
                entry.getValue().subMap(start, true, end, true);
            
            for (Map.Entry<Instant, Double> point : subMap.entrySet()) {
                result.add(new DataPoint(
                    metric, entry.getKey(), 
                    point.getKey(), point.getValue()
                ));
            }
        }
    }
    
    return result;
}
```

---

## ✅ 체크리스트

- [ ] write (데이터 저장)
- [ ] query (시간 범위 조회)
- [ ] 태그 필터링
- [ ] aggregate (집계 함수)
- [ ] downsample (다운샘플링)
- [ ] 보관 정책 (Retention)
- [ ] 배치 쓰기 (선택)
- [ ] 압축 저장 (선택)

---

## 📚 참고

- InfluxDB
- Prometheus
- TimescaleDB
- OpenTSDB
