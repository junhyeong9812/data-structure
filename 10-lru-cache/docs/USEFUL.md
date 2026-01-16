# LRU 캐시 구현에 유용한 Java API

## 📦 Map 관련

### HashMap<K, V>
```java
import java.util.Map;
import java.util.HashMap;

Map<Integer, Node> cache = new HashMap<>();

// 기본 연산
cache.put(key, node);       // O(1) 삽입/업데이트
cache.get(key);             // O(1) 조회, 없으면 null
cache.remove(key);          // O(1) 삭제
cache.containsKey(key);     // O(1) 키 존재 확인
cache.size();               // O(1) 크기
cache.isEmpty();            // O(1) 비어있는지
cache.clear();              // O(n) 전체 삭제

// 조건부 연산
cache.putIfAbsent(key, node);    // 없을 때만 삽입
cache.getOrDefault(key, defaultVal);  // 없으면 기본값

// 편리한 메서드
cache.computeIfAbsent(key, k -> new Node(k));  // 없으면 생성
cache.compute(key, (k, v) -> ...);             // 값 변환
```

### LinkedHashMap<K, V> (LRU 간편 구현)
```java
import java.util.LinkedHashMap;

// 접근 순서 유지 (accessOrder = true)
LinkedHashMap<K, V> map = new LinkedHashMap<>(capacity, 0.75f, true);

// LRU 캐시로 확장
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;
    
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);  // accessOrder = true
        this.capacity = capacity;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;  // 용량 초과 시 가장 오래된 항목 제거
    }
}

// 사용
Map<Integer, String> cache = new LRUCache<>(3);
cache.put(1, "a");
cache.put(2, "b");
cache.put(3, "c");
cache.get(1);       // 1을 가장 최근으로 이동
cache.put(4, "d");  // 2가 제거됨 (가장 오래됨)
```

### Map.Entry<K, V>
```java
// 불변 Entry 생성
Map.Entry<K, V> entry = Map.entry(key, value);

// Entry 순회
for (Map.Entry<K, V> entry : map.entrySet()) {
    K key = entry.getKey();
    V value = entry.getValue();
}
```

---

## 🔗 이중 연결 리스트 구현

### 노드 클래스
```java
// 기본 노드
class Node {
    int key, value;
    Node prev, next;
    
    Node() {}  // 더미 노드용
    
    Node(int key, int value) {
        this.key = key;
        this.value = value;
    }
}

// 제네릭 노드
class Node<K, V> {
    K key;
    V value;
    Node<K, V> prev, next;
    
    Node() {}
    
    Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
```

### 더미 노드 초기화
```java
Node head = new Node();  // 더미 head
Node tail = new Node();  // 더미 tail

head.next = tail;
tail.prev = head;

// 실제 데이터는 head와 tail 사이에 위치
// head <-> [data1] <-> [data2] <-> tail
```

### 리스트 연산
```java
// 맨 앞에 추가 (head 바로 뒤)
void addToHead(Node node) {
    node.prev = head;
    node.next = head.next;
    head.next.prev = node;
    head.next = node;
}

// 노드 제거
void removeNode(Node node) {
    node.prev.next = node.next;
    node.next.prev = node.prev;
}

// 맨 앞으로 이동
void moveToHead(Node node) {
    removeNode(node);
    addToHead(node);
}

// 맨 뒤 노드 제거 및 반환 (tail 바로 앞)
Node removeTail() {
    Node lru = tail.prev;
    removeNode(lru);
    return lru;
}
```

---

## 🛡️ 동시성 처리

### synchronized 블록
```java
public class ThreadSafeLRUCache<K, V> {
    private final LRUCache<K, V> cache;
    private final Object lock = new Object();
    
    public V get(K key) {
        synchronized (lock) {
            return cache.get(key);
        }
    }
    
    public void put(K key, V value) {
        synchronized (lock) {
            cache.put(key, value);
        }
    }
}
```

### ReentrantReadWriteLock
```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentLRUCache<K, V> {
    private final LRUCache<K, V> cache;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public V get(K key) {
        // 읽기는 공유 가능
        lock.readLock().lock();
        try {
            return cache.peek(key);  // peek은 순서 변경 안 함
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public V getAndTouch(K key) {
        // 순서 변경이 필요하면 쓰기 락
        lock.writeLock().lock();
        try {
            return cache.get(key);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            cache.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

### ConcurrentHashMap + ConcurrentLinkedDeque
```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

// 완전한 동시성은 복잡 (락 프리 LRU는 어려움)
// 실무에서는 Caffeine, Guava Cache 등 라이브러리 사용 권장
```

---

## 📋 Collections 유틸리티

### Collections.synchronizedMap
```java
import java.util.Collections;

// LinkedHashMap을 동기화 래퍼로 감싸기
Map<K, V> syncCache = Collections.synchronizedMap(
    new LinkedHashMap<K, V>(capacity, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }
);
```

### Deque (양방향 큐)
```java
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.LinkedList;

Deque<Integer> deque = new ArrayDeque<>();
Deque<Integer> deque = new LinkedList<>();

deque.addFirst(item);   // 앞에 추가
deque.addLast(item);    // 뒤에 추가
deque.removeFirst();    // 앞에서 제거
deque.removeLast();     // 뒤에서 제거
deque.peekFirst();      // 앞 조회
deque.peekLast();       // 뒤 조회
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldEvictLRUItem() {
    LRUCache<Integer, String> cache = new LRUCache<>(2);
    cache.put(1, "one");
    cache.put(2, "two");
    cache.put(3, "three");  // 1 제거
    
    assertThat(cache.get(1)).isNull();
    assertThat(cache.get(2)).isEqualTo("two");
    assertThat(cache.get(3)).isEqualTo("three");
}

@Test
void getShouldUpdateAccessOrder() {
    LRUCache<Integer, String> cache = new LRUCache<>(2);
    cache.put(1, "one");
    cache.put(2, "two");
    cache.get(1);           // 1을 최근으로
    cache.put(3, "three");  // 2 제거 (가장 오래됨)
    
    assertThat(cache.get(1)).isEqualTo("one");
    assertThat(cache.get(2)).isNull();
    assertThat(cache.get(3)).isEqualTo("three");
}

@Test
void keysOrderShouldBeMostRecentFirst() {
    LRUCache<Integer, String> cache = new LRUCache<>(3);
    cache.put(1, "one");
    cache.put(2, "two");
    cache.put(3, "three");
    cache.get(1);  // 1을 최근으로
    
    assertThat(cache.keys()).containsExactly(1, 3, 2);
}
```

---

## 📚 Java 21 관련

### Record로 캐시 엔트리
```java
public record CacheEntry<K, V>(K key, V value, long accessTime) {}
```

### Pattern Matching
```java
public void processEntry(Object entry) {
    if (entry instanceof CacheEntry<?, ?> ce) {
        System.out.println("Key: " + ce.key() + ", Value: " + ce.value());
    }
}
```

### Sequenced Collections (Java 21)
```java
// SequencedMap - 순서가 있는 Map
SequencedMap<K, V> seqMap = new LinkedHashMap<>();
seqMap.firstEntry();     // 첫 번째 엔트리
seqMap.lastEntry();      // 마지막 엔트리
seqMap.pollFirstEntry(); // 첫 번째 제거 및 반환
seqMap.pollLastEntry();  // 마지막 제거 및 반환
seqMap.reversed();       // 역순 뷰
```

---

## ⚡ 성능 팁

### 1. 초기 용량 설정
```java
// 용량을 알면 미리 설정 (리해싱 방지)
Map<K, V> cache = new HashMap<>(capacity * 4 / 3 + 1);
```

### 2. 불필요한 객체 생성 피하기
```java
// 비효율: 매번 Entry 생성
cache.keys().stream()
    .map(k -> Map.entry(k, cache.get(k)))
    .toList();

// 효율: 직접 순회
List<Map.Entry<K, V>> entries = new ArrayList<>();
Node<K, V> current = head.next;
while (current != tail) {
    entries.add(Map.entry(current.key, current.value));
    current = current.next;
}
```

### 3. 캐시 적중률 모니터링
```java
public class MonitoredLRUCache<K, V> {
    private final LRUCache<K, V> cache;
    private long hits = 0;
    private long misses = 0;
    
    public V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            hits++;
        } else {
            misses++;
        }
        return value;
    }
    
    public double hitRate() {
        long total = hits + misses;
        return total == 0 ? 0 : (double) hits / total;
    }
}
```

---

## 🔄 다른 캐시 정책

### LFU (Least Frequently Used)
```java
// 가장 적게 사용된 항목 제거
// 사용 횟수 추적 필요
class LFUCache {
    Map<K, Node> cache;
    Map<Integer, DoublyLinkedList> freqMap;  // 빈도별 리스트
    int minFreq;
}
```

### FIFO (First In First Out)
```java
// 가장 먼저 들어온 항목 제거
// 단순 큐로 구현 가능
Queue<K> order = new LinkedList<>();
```

### TTL (Time To Live)
```java
// 시간 기반 만료
class TTLCache {
    Map<K, ValueWithExpiry> cache;
    
    record ValueWithExpiry(V value, long expiryTime) {}
    
    V get(K key) {
        ValueWithExpiry entry = cache.get(key);
        if (entry == null || System.currentTimeMillis() > entry.expiryTime) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }
}
```
