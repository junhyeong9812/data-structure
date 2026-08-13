# 일관된 해싱 구현에 유용한 Java API

## 📦 TreeMap (정렬된 맵)

### 기본 사용
```java
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.NavigableMap;

TreeMap<Long, String> ring = new TreeMap<>();

// 삽입
ring.put(100L, "server1");
ring.put(200L, "server2");
ring.put(300L, "server3");

// 조회
ring.get(100L);  // "server1"
ring.containsKey(150L);  // false
```

### 네비게이션 메서드 (핵심!)
```java
// ceilingEntry: key 이상인 가장 작은 엔트리
ring.ceilingEntry(150L);  // 200 → "server2"
ring.ceilingEntry(100L);  // 100 → "server1"
ring.ceilingEntry(350L);  // null (없음)

// floorEntry: key 이하인 가장 큰 엔트리
ring.floorEntry(150L);  // 100 → "server1"

// higherEntry: key 초과인 가장 작은 엔트리
ring.higherEntry(100L);  // 200 → "server2"

// lowerEntry: key 미만인 가장 큰 엔트리
ring.lowerEntry(200L);  // 100 → "server1"

// 첫 번째/마지막
ring.firstEntry();  // 100 → "server1"
ring.lastEntry();   // 300 → "server3"
ring.firstKey();    // 100
ring.lastKey();     // 300
```

### 부분 맵
```java
// tailMap: key 이상의 모든 엔트리
SortedMap<Long, String> tail = ring.tailMap(150L);
// {200="server2", 300="server3"}

// headMap: key 미만의 모든 엔트리
SortedMap<Long, String> head = ring.headMap(200L);
// {100="server1"}

// subMap: from(포함) ~ to(미포함)
SortedMap<Long, String> sub = ring.subMap(100L, 300L);
// {100="server1", 200="server2"}
```

### 순회
```java
// 키 순회
for (Long key : ring.keySet()) { ... }

// 값 순회
for (String value : ring.values()) { ... }

// 엔트리 순회
for (Map.Entry<Long, String> entry : ring.entrySet()) {
    Long hash = entry.getKey();
    String node = entry.getValue();
}

// 역순 순회
for (Long key : ring.descendingKeySet()) { ... }
```

---

## 🔐 해시 함수

### MessageDigest (MD5, SHA)
```java
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public long hashMD5(String key) {
    try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
        
        // 8바이트를 long으로 변환
        long hash = 0;
        for (int i = 0; i < 8; i++) {
            hash = (hash << 8) | (digest[i] & 0xFF);
        }
        return hash;
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    }
}

// SHA-256
MessageDigest md = MessageDigest.getInstance("SHA-256");
```

### ByteBuffer로 변환
```java
import java.nio.ByteBuffer;

byte[] digest = md.digest(key.getBytes());

// long으로 변환 (간단)
long hash = ByteBuffer.wrap(digest).getLong();

// int로 변환
int hash = ByteBuffer.wrap(digest).getInt();
```

### String.hashCode() (간단)
```java
// 간단하지만 분포가 좋지 않을 수 있음
public long hash(String key) {
    return key.hashCode() & 0xFFFFFFFFL;  // unsigned 32-bit
}
```

### MurmurHash (외부 라이브러리)
```java
// Google Guava
import com.google.common.hash.Hashing;

long hash = Hashing.murmur3_128()
    .hashString(key, StandardCharsets.UTF_8)
    .asLong();

// 또는 32비트
int hash = Hashing.murmur3_32_fixed()
    .hashString(key, StandardCharsets.UTF_8)
    .asInt();
```

---

## 📊 컬렉션 유틸

### HashMap
```java
import java.util.HashMap;
import java.util.Map;

// 노드별 가상 노드 위치 저장
Map<String, Set<Long>> nodePositions = new HashMap<>();

// computeIfAbsent
nodePositions.computeIfAbsent(node, k -> new HashSet<>()).add(hash);

// getOrDefault
int count = distribution.getOrDefault(node, 0);

// merge (카운팅)
distribution.merge(node, 1, Integer::sum);
```

### HashSet
```java
import java.util.HashSet;
import java.util.Set;

Set<Long> positions = new HashSet<>();
positions.add(hash);
positions.contains(hash);
positions.remove(hash);

// 중복 노드 제거용
Set<String> seen = new HashSet<>();
if (seen.add(node)) {  // add는 새로 추가되면 true
    result.add(node);
}
```

### ArrayList
```java
import java.util.ArrayList;
import java.util.List;

List<String> nodes = new ArrayList<>();
nodes.add(node);
nodes.size();
nodes.get(0);
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldRouteToCorrectNode() {
    ConsistentHashing ch = new ConsistentHashing(100);
    ch.addNode("server1");
    ch.addNode("server2");
    
    String node = ch.getNode("key1");
    assertThat(node).isIn("server1", "server2");
}

@Test
void shouldDistributeEvenly() {
    ConsistentHashing ch = new ConsistentHashing(150);
    ch.addNode("s1");
    ch.addNode("s2");
    ch.addNode("s3");
    
    Map<String, Integer> dist = new HashMap<>();
    for (int i = 0; i < 10000; i++) {
        String node = ch.getNode("key" + i);
        dist.merge(node, 1, Integer::sum);
    }
    
    // 각 노드가 약 33% ± 5%
    for (int count : dist.values()) {
        assertThat(count).isBetween(2800, 3800);
    }
}

@Test
void shouldMinimizeRedistribution() {
    ConsistentHashing ch = new ConsistentHashing(100);
    ch.addNode("s1");
    ch.addNode("s2");
    ch.addNode("s3");
    
    // 노드 추가 전 키 매핑
    Map<String, String> before = new HashMap<>();
    for (int i = 0; i < 1000; i++) {
        before.put("key" + i, ch.getNode("key" + i));
    }
    
    // 노드 추가
    ch.addNode("s4");
    
    // 변경된 키 수 확인
    int moved = 0;
    for (int i = 0; i < 1000; i++) {
        String key = "key" + i;
        if (!before.get(key).equals(ch.getNode(key))) {
            moved++;
        }
    }
    
    // 약 25% (1000/4) 정도만 이동해야 함
    assertThat(moved).isBetween(150, 350);
}
```

### 분포 시각화 (디버깅)
```java
public void printRing() {
    System.out.println("=== Hash Ring ===");
    for (Map.Entry<Long, String> e : ring.entrySet()) {
        System.out.printf("%20d → %s%n", e.getKey(), e.getValue());
    }
}

public void printDistribution(int sampleSize) {
    Map<String, Integer> dist = new HashMap<>();
    for (int i = 0; i < sampleSize; i++) {
        String node = getNode("sample-" + i);
        dist.merge(node, 1, Integer::sum);
    }
    
    System.out.println("=== Distribution ===");
    for (Map.Entry<String, Integer> e : dist.entrySet()) {
        double percent = e.getValue() * 100.0 / sampleSize;
        System.out.printf("%s: %d (%.1f%%)%n", 
            e.getKey(), e.getValue(), percent);
    }
}
```

---

## 📚 Java 21 관련

### Record
```java
// 노드 정보
public record Node(String id, int weight, String address) {}

// 라우팅 결과
public record RoutingResult(String node, long hash, int virtualIndex) {}

// 분포 통계
public record DistributionStats(
    Map<String, Integer> counts,
    double mean,
    double stdDev
) {
    public static DistributionStats calculate(Map<String, Integer> dist) {
        double mean = dist.values().stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0);
        
        double variance = dist.values().stream()
            .mapToDouble(c -> Math.pow(c - mean, 2))
            .average()
            .orElse(0);
        
        return new DistributionStats(dist, mean, Math.sqrt(variance));
    }
}
```

### Sealed Classes
```java
public sealed interface HashFunction 
    permits MD5Hash, MurmurHash, SHA256Hash {
    
    long hash(String key);
}

public final class MD5Hash implements HashFunction {
    @Override
    public long hash(String key) { ... }
}

public final class MurmurHash implements HashFunction {
    @Override
    public long hash(String key) { ... }
}
```

---

## ⚡ 성능 팁

### 1. 해시 함수 캐싱
```java
// MessageDigest는 스레드 안전하지 않음
// ThreadLocal 사용
private static final ThreadLocal<MessageDigest> MD5 = 
    ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    });

public long hash(String key) {
    MessageDigest md = MD5.get();
    md.reset();
    byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
    return ByteBuffer.wrap(digest).getLong();
}
```

### 2. 가상 노드 수 최적화
```java
// 너무 적음: 불균등 분포
// 너무 많음: 메모리 낭비, 느린 추가/제거

// 권장: 노드당 100~200개
// 노드 수가 적을수록 더 많은 가상 노드 필요
int virtualNodes = Math.max(150, 1000 / physicalNodeCount);
```

### 3. 동시성
```java
// 읽기 위주면 ConcurrentSkipListMap
private final ConcurrentNavigableMap<Long, String> ring = 
    new ConcurrentSkipListMap<>();

// 또는 ReadWriteLock
private final ReadWriteLock lock = new ReentrantReadWriteLock();

public String getNode(String key) {
    lock.readLock().lock();
    try {
        // ...
    } finally {
        lock.readLock().unlock();
    }
}

public void addNode(String node) {
    lock.writeLock().lock();
    try {
        // ...
    } finally {
        lock.writeLock().unlock();
    }
}
```

---

## 🔀 Builder 패턴
```java
public class ConsistentHashing {
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private int virtualNodeCount = 150;
        private HashFunction hashFunction = new MD5Hash();
        private List<String> initialNodes = new ArrayList<>();
        
        public Builder virtualNodeCount(int count) {
            this.virtualNodeCount = count;
            return this;
        }
        
        public Builder hashFunction(HashFunction fn) {
            this.hashFunction = fn;
            return this;
        }
        
        public Builder addNode(String node) {
            this.initialNodes.add(node);
            return this;
        }
        
        public Builder addNodes(String... nodes) {
            Collections.addAll(this.initialNodes, nodes);
            return this;
        }
        
        public ConsistentHashing build() {
            ConsistentHashing ch = new ConsistentHashing(
                virtualNodeCount, hashFunction);
            for (String node : initialNodes) {
                ch.addNode(node);
            }
            return ch;
        }
    }
}

// 사용
ConsistentHashing ch = ConsistentHashing.builder()
    .virtualNodeCount(200)
    .hashFunction(new MurmurHash())
    .addNodes("server1", "server2", "server3")
    .build();
```
