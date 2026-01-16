# LRU 캐시 풀이 해설

## 📌 핵심 아이디어

**해시맵**으로 O(1) 조회를 보장하고,
**이중 연결 리스트**로 O(1) 순서 변경(최근 사용 갱신)을 구현합니다.

---

## 🔑 핵심 개념

### 1. 자료구조 조합
```
┌─────────────────────────────────────────────────────┐
│  HashMap<Key, Node>                                 │
│  ┌───────────────────────────────────────────────┐  │
│  │  key1 ──→ Node1                               │  │
│  │  key2 ──→ Node2                               │  │
│  │  key3 ──→ Node3                               │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────┐
│  Doubly Linked List (순서 = 사용 시간)               │
│                                                     │
│  head ←→ Node1 ←→ Node2 ←→ Node3 ←→ tail           │
│  (더미)  (최근)            (오래됨)   (더미)         │
│                              ↑                      │
│                           LRU (제거 대상)           │
└─────────────────────────────────────────────────────┘
```

### 2. 더미 노드 사용 이유
```
// 더미 노드 없이: null 체크 필요
if (node.prev != null) {
    node.prev.next = node.next;
} else {
    head = node.next;  // node가 head인 경우
}

// 더미 노드 사용: 일관된 코드
node.prev.next = node.next;
node.next.prev = node.prev;
```

### 3. 연산 흐름
```
get(key):
1. HashMap에서 Node 조회
2. 없으면 -1 반환
3. 있으면 Node를 head로 이동 (최근 사용)
4. 값 반환

put(key, value):
1. HashMap에서 기존 Node 확인
2. 있으면: 값 업데이트 + head로 이동
3. 없으면:
   a. 용량 초과 시 tail 앞 노드(LRU) 제거
   b. 새 Node 생성
   c. head 뒤에 추가
   d. HashMap에 저장
```

---

## 📝 POP 구현 해설
```java
public class LRUCache {
    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head;  // 더미 head (최근)
    private final Node tail;  // 더미 tail (오래됨)
    
    private static class Node {
        int key, value;
        Node prev, next;
        
        Node() {}
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        
        // 더미 노드 초기화
        head = new Node();
        tail = new Node();
        head.next = tail;
        tail.prev = head;
    }
    
    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
        }
        
        // 최근 사용으로 이동
        moveToHead(node);
        return node.value;
    }
    
    public void put(int key, int value) {
        Node node = cache.get(key);
        
        if (node != null) {
            // 기존 키 업데이트
            node.value = value;
            moveToHead(node);
        } else {
            // 새 키 삽입
            Node newNode = new Node(key, value);
            
            // 용량 확인
            if (cache.size() >= capacity) {
                // LRU 제거
                Node lru = removeTail();
                cache.remove(lru.key);
            }
            
            addToHead(newNode);
            cache.put(key, newNode);
        }
    }
    
    public boolean remove(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return false;
        }
        
        removeNode(node);
        cache.remove(key);
        return true;
    }
    
    // === 이중 연결 리스트 연산 ===
    
    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }
    
    private Node removeTail() {
        Node lru = tail.prev;
        removeNode(lru);
        return lru;
    }
    
    public int size() {
        return cache.size();
    }
    
    public int capacity() {
        return capacity;
    }
    
    public void clear() {
        cache.clear();
        head.next = tail;
        tail.prev = head;
    }
}
```

---

## 📝 OOP 구현 (제네릭)
```java
public class LRUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final Node<K, V> head;
    private final Node<K, V> tail;
    
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev, next;
        
        Node() {}
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.cache = new HashMap<>();
        
        head = new Node<>();
        tail = new Node<>();
        head.next = tail;
        tail.prev = head;
    }
    
    @Override
    public V get(K key) {
        Objects.requireNonNull(key);
        
        Node<K, V> node = cache.get(key);
        if (node == null) {
            return null;
        }
        
        moveToHead(node);
        return node.value;
    }
    
    @Override
    public V put(K key, V value) {
        Objects.requireNonNull(key);
        
        Node<K, V> node = cache.get(key);
        
        if (node != null) {
            V oldValue = node.value;
            node.value = value;
            moveToHead(node);
            return oldValue;
        }
        
        Node<K, V> newNode = new Node<>(key, value);
        
        if (cache.size() >= capacity) {
            Node<K, V> lru = removeTail();
            cache.remove(lru.key);
        }
        
        addToHead(newNode);
        cache.put(key, newNode);
        return null;
    }
    
    // peek: 사용 기록 갱신 없이 조회
    public V peek(K key) {
        Node<K, V> node = cache.get(key);
        return node == null ? null : node.value;
    }
    
    // 가장 오래된 항목 조회
    public Map.Entry<K, V> getOldest() {
        if (cache.isEmpty()) return null;
        Node<K, V> lru = tail.prev;
        return Map.entry(lru.key, lru.value);
    }
    
    // 가장 최근 항목 조회
    public Map.Entry<K, V> getNewest() {
        if (cache.isEmpty()) return null;
        Node<K, V> mru = head.next;
        return Map.entry(mru.key, mru.value);
    }
    
    // 모든 키 (최근 순)
    public List<K> keys() {
        List<K> keys = new ArrayList<>();
        Node<K, V> current = head.next;
        while (current != tail) {
            keys.add(current.key);
            current = current.next;
        }
        return keys;
    }
    
    // 모든 값 (최근 순)
    public List<V> values() {
        List<V> values = new ArrayList<>();
        Node<K, V> current = head.next;
        while (current != tail) {
            values.add(current.value);
            current = current.next;
        }
        return values;
    }
    
    // === 내부 메서드 ===
    
    private void addToHead(Node<K, V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    
    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addToHead(node);
    }
    
    private Node<K, V> removeTail() {
        Node<K, V> lru = tail.prev;
        removeNode(lru);
        return lru;
    }
}
```

---

## 📝 LinkedHashMap 활용
```java
// Java의 LinkedHashMap을 활용한 간단한 구현
public class SimpleLRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;
    
    public SimpleLRUCache(int capacity) {
        // accessOrder = true: 접근 순서로 정렬
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}

// 사용
LRUCache<Integer, String> cache = new SimpleLRUCache<>(3);
cache.put(1, "one");
cache.put(2, "two");
cache.put(3, "three");
cache.get(1);  // 1을 최근으로 이동
cache.put(4, "four");  // 2 제거 (가장 오래됨)
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 설명 |
|------|-----------|------|
| get | O(1) | HashMap 조회 + 리스트 이동 |
| put | O(1) | HashMap 삽입 + 리스트 추가 |
| remove | O(1) | HashMap 삭제 + 리스트 제거 |
| size | O(1) | HashMap size |
| clear | O(1)* | HashMap clear |

### 공간 복잡도
- O(capacity) - 최대 capacity개의 노드 저장

---

## ❌ 흔한 실수

### 1. 더미 노드 없이 구현
```java
// 잘못됨: 엣지 케이스 복잡
void removeNode(Node node) {
    if (node.prev != null) node.prev.next = node.next;
    else head = node.next;  // node가 head인 경우
    
    if (node.next != null) node.next.prev = node.prev;
    else tail = node.prev;  // node가 tail인 경우
}

// 올바름: 더미 노드로 일관된 처리
void removeNode(Node node) {
    node.prev.next = node.next;
    node.next.prev = node.prev;
}
```

### 2. put에서 이동 누락
```java
// 잘못됨: 기존 키 업데이트 시 이동 안 함
if (node != null) {
    node.value = value;  // 이동 안 함!
}

// 올바름: 업데이트 + 최근으로 이동
if (node != null) {
    node.value = value;
    moveToHead(node);  // 최근 사용!
}
```

### 3. 제거 시 HashMap 정리 누락
```java
// 잘못됨: 리스트에서만 제거
Node lru = removeTail();
// cache.remove(lru.key) 누락!

// 올바름: HashMap에서도 제거
Node lru = removeTail();
cache.remove(lru.key);
```

### 4. 용량 체크 타이밍
```java
// 잘못됨: 삽입 후 제거
addToHead(newNode);
cache.put(key, newNode);
if (cache.size() > capacity) {  // 이미 초과!
    // ...
}

// 올바름: 삽입 전 제거
if (cache.size() >= capacity) {
    Node lru = removeTail();
    cache.remove(lru.key);
}
addToHead(newNode);
cache.put(key, newNode);
```

---

## 🔗 관련 문제

- LeetCode 146: LRU Cache
- LeetCode 460: LFU Cache
- LeetCode 432: All O`one Data Structure
- LeetCode 355: Design Twitter (타임라인 캐싱)
