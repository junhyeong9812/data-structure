# 타임시리즈 DB 구현에 유용한 Java API

## 📦 시간 관련

### java.time.Instant
```java
import java.time.Instant;

// 현재 시간
Instant now = Instant.now();

// 파싱
Instant parsed = Instant.parse("2024-01-01T10:00:00Z");

// epoch 밀리초
long millis = instant.toEpochMilli();
Instant fromMillis = Instant.ofEpochMilli(millis);

// 연산
Instant later = now.plus(Duration.ofHours(1));
Instant earlier = now.minus(Duration.ofMinutes(30));

// 비교
boolean isBefore = a.isBefore(b);
boolean isAfter = a.isAfter(b);
int cmp = a.compareTo(b);
```

### java.time.Duration
```java
import java.time.Duration;

// 생성
Duration oneHour = Duration.ofHours(1);
Duration fiveMinutes = Duration.ofMinutes(5);
Duration between = Duration.between(start, end);

// 변환
long millis = duration.toMillis();
long seconds = duration.getSeconds();
long minutes = duration.toMinutes();

// 연산
Duration doubled = duration.multipliedBy(2);
Duration halved = duration.dividedBy(2);
```

### 시간 절삭
```java
import java.time.temporal.ChronoUnit;

// 분 단위로 절삭
Instant truncated = instant.truncatedTo(ChronoUnit.MINUTES);

// 시간 단위로 절삭
Instant hourStart = instant.truncatedTo(ChronoUnit.HOURS);

// 일 단위
Instant dayStart = instant.truncatedTo(ChronoUnit.DAYS);
```

---

## 📊 NavigableMap (TreeMap)

### 기본 사용
```java
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

// 생성
NavigableMap<Instant, Double> timeSeries = new TreeMap<>();
NavigableMap<Instant, Double> concurrent = new ConcurrentSkipListMap<>();

// 삽입/조회
timeSeries.put(timestamp, value);
Double value = timeSeries.get(timestamp);
```

### 범위 쿼리 (핵심!)
```java
// subMap: 범위 조회
// fromKey, fromInclusive, toKey, toInclusive
NavigableMap<Instant, Double> range = 
    timeSeries.subMap(start, true, end, true);

// headMap: 특정 키 미만/이하
SortedMap<Instant, Double> before = timeSeries.headMap(cutoff);
NavigableMap<Instant, Double> beforeInc = timeSeries.headMap(cutoff, true);

// tailMap: 특정 키 이상/초과
SortedMap<Instant, Double> after = timeSeries.tailMap(start);
NavigableMap<Instant, Double> afterExc = timeSeries.tailMap(start, false);
```

### 네비게이션
```java
// 가장 가까운 엔트리
Map.Entry<Instant, Double> floor = timeSeries.floorEntry(timestamp);   // 이하
Map.Entry<Instant, Double> ceiling = timeSeries.ceilingEntry(timestamp); // 이상
Map.Entry<Instant, Double> lower = timeSeries.lowerEntry(timestamp);   // 미만
Map.Entry<Instant, Double> higher = timeSeries.higherEntry(timestamp); // 초과

// 첫/마지막
Map.Entry<Instant, Double> first = timeSeries.firstEntry();
Map.Entry<Instant, Double> last = timeSeries.lastEntry();

// 키만
Instant firstKey = timeSeries.firstKey();
Instant lastKey = timeSeries.lastKey();
```

### 삭제
```java
// 범위 삭제
timeSeries.subMap(start, end).clear();

// 이전 데이터 삭제 (retention 구현)
timeSeries.headMap(cutoff).clear();
```

---

## 🔐 동시성

### ConcurrentSkipListMap
```java
import java.util.concurrent.ConcurrentSkipListMap;

// 스레드 안전한 NavigableMap
NavigableMap<Instant, Double> timeSeries = new ConcurrentSkipListMap<>();

// 모든 연산이 원자적
timeSeries.put(timestamp, value);
timeSeries.subMap(start, end);  // 스냅샷 뷰
```

### ConcurrentHashMap
```java
import java.util.concurrent.ConcurrentHashMap;

// 메트릭별 데이터 저장
Map<String, Map<TagSet, NavigableMap<Instant, Double>>> data = 
    new ConcurrentHashMap<>();

// computeIfAbsent로 원자적 생성
data.computeIfAbsent(metric, k -> new ConcurrentHashMap<>())
    .computeIfAbsent(tagSet, k -> new ConcurrentSkipListMap<>())
    .put(timestamp, value);
```

---

## 📈 Stream API

### 집계 연산
```java
import java.util.stream.*;

List<Double> values = ...;

// 평균
double avg = values.stream()
    .mapToDouble(Double::doubleValue)
    .average()
    .orElse(0.0);

// 합계
double sum = values.stream()
    .mapToDouble(Double::doubleValue)
    .sum();

// 최소/최대
double min = values.stream()
    .mapToDouble(Double::doubleValue)
    .min()
    .orElse(0.0);

double max = values.stream()
    .mapToDouble(Double::doubleValue)
    .max()
    .orElse(0.0);

// 개수
long count = values.stream().count();

// 통계 요약
DoubleSummaryStatistics stats = values.stream()
    .mapToDouble(Double::doubleValue)
    .summaryStatistics();
// stats.getAverage(), getMin(), getMax(), getSum(), getCount()
```

### 그룹화
```java
// 버킷별 그룹화
Map<Instant, List<DataPoint>> byBucket = points.stream()
    .collect(Collectors.groupingBy(
        p -> getBucketStart(p.timestamp(), origin, interval)
    ));

// 태그별 그룹화
Map<Map<String, String>, List<DataPoint>> byTags = points.stream()
    .collect(Collectors.groupingBy(DataPoint::tags));
```

---

## 🧪 테스트

### AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldWriteAndQuery() {
    TimeSeriesDB db = new TimeSeriesDB();
    
    Instant t1 = Instant.parse("2024-01-01T10:00:00Z");
    Instant t2 = Instant.parse("2024-01-01T10:01:00Z");
    
    db.write("cpu", Map.of("host", "s1"), t1, 45.0);
    db.write("cpu", Map.of("host", "s1"), t2, 50.0);
    
    List<DataPoint> result = db.query("cpu", Map.of("host", "s1"), t1, t2);
    
    assertThat(result).hasSize(2);
    assertThat(result.get(0).value()).isEqualTo(45.0);
    assertThat(result.get(1).value()).isEqualTo(50.0);
}

@Test
void shouldCalculateAverage() {
    TimeSeriesDB db = new TimeSeriesDB();
    
    // 데이터 추가
    for (int i = 0; i < 10; i++) {
        db.write("metric", Map.of(), 
            Instant.now().plusSeconds(i), (double) i);
    }
    
    double avg = db.aggregate("metric", Map.of(), 
        Instant.MIN, Instant.MAX, AggregateFunction.AVG);
    
    assertThat(avg).isEqualTo(4.5);  // 0~9의 평균
}

@Test
void shouldDownsample() {
    TimeSeriesDB db = new TimeSeriesDB();
    Instant base = Instant.parse("2024-01-01T10:00:00Z");
    
    // 1분 간격으로 10개 데이터
    for (int i = 0; i < 10; i++) {
        db.write("metric", Map.of(), 
            base.plus(Duration.ofMinutes(i)), (double) i);
    }
    
    // 5분 평균으로 다운샘플링
    List<DataPoint> result = db.downsample(
        "metric", Map.of(),
        base, base.plus(Duration.ofMinutes(10)),
        Duration.ofMinutes(5),
        AggregateFunction.AVG
    );
    
    assertThat(result).hasSize(2);
    assertThat(result.get(0).value()).isEqualTo(2.0);  // avg(0,1,2,3,4)
    assertThat(result.get(1).value()).isEqualTo(7.0);  // avg(5,6,7,8,9)
}
```

---

## 📚 Java 21 관련

### Record
```java
// 데이터 포인트
public record DataPoint(
    String metric,
    Map<String, String> tags,
    Instant timestamp,
    double value
) {
    public DataPoint {
        tags = Map.copyOf(tags);
    }
}

// 쿼리 결과
public record QueryResult(
    String metric,
    Map<String, String> tags,
    List<DataPoint> points,
    Duration timeRange
) {}

// 집계 결과
public record AggregateResult(
    String metric,
    Map<String, String> tags,
    Instant start,
    Instant end,
    AggregateFunction function,
    double value
) {}
```

### Pattern Matching
```java
public double calculate(List<Double> values, AggregateFunction func) {
    if (values.isEmpty()) return 0;
    
    return switch (func) {
        case AVG -> values.stream().mapToDouble(d -> d).average().orElse(0);
        case SUM -> values.stream().mapToDouble(d -> d).sum();
        case MIN -> values.stream().mapToDouble(d -> d).min().orElse(0);
        case MAX -> values.stream().mapToDouble(d -> d).max().orElse(0);
        case COUNT -> values.size();
        case FIRST -> values.getFirst();
        case LAST -> values.getLast();
    };
}
```

### Sequenced Collections (Java 21)
```java
// getFirst(), getLast() 사용 가능
List<Double> values = new ArrayList<>();
double first = values.getFirst();  // Java 21
double last = values.getLast();    // Java 21

// reversed() 뷰
List<Double> reversed = values.reversed();
```

---

## ⚡ 성능 팁

### 1. 배치 쓰기
```java
// 개별 쓰기 (느림)
for (DataPoint p : points) {
    db.write(metric, tags, p.timestamp(), p.value());
}

// 배치 쓰기 (빠름)
NavigableMap<Instant, Double> series = getOrCreateSeries(metric, tags);
for (DataPoint p : points) {
    series.put(p.timestamp(), p.value());
}
```

### 2. 불변 TagSet
```java
// 생성 시 복사하여 불변 보장
public TagSet(Map<String, String> tags) {
    this.tags = Map.copyOf(tags);  // 불변 맵
    this.hashCode = this.tags.hashCode();  // 해시 캐싱
}
```

### 3. 적절한 자료구조
```java
// 시간순 조회가 많음 → TreeMap/ConcurrentSkipListMap
NavigableMap<Instant, Double> timeSeries = new ConcurrentSkipListMap<>();

// 메트릭/태그 조회가 많음 → HashMap
Map<String, ...> metricIndex = new ConcurrentHashMap<>();
```
