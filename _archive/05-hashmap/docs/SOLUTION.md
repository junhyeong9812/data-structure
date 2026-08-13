# 해시맵 풀이 해설

## 📌 핵심 아이디어

해시맵은 **해시 함수**를 사용하여 키를 배열 인덱스로 변환하고,
이를 통해 평균 O(1) 시간에 데이터를 저장/조회/삭제합니다.

---

## 🔑 핵심 개념

### 1. 해시 함수 동작
```
키: "apple"
    ↓ hashCode()
해시값: 93029210
    ↓ & (capacity - 1)  // capacity = 16
버킷 인덱스: 10

buckets[10]에 ("apple", value) 저장
```

### 2. 충돌 해결: 체이닝
```
buckets[3]:  [A:1] → [B:2] → [C:3] → null
             ↑ 같은 버킷에 여러 엔트리가 연결 리스트로 연결

검색 시: 버킷 찾기 O(1) + 리스트 순회 O(k)
         k = 버킷 내 요소 수 (로드 팩터가 낮으면 k ≈ 1)
```

### 3. 충돌 해결: 개방 주소법 (선형 탐사)
```
put("A", 1)  →  buckets[3] = A
put("B", 2)  →  buckets[3] 충돌! → buckets[4] = B
put("C", 3)  →  buckets[3] 충돌! → buckets[4] 충돌! → buckets[5] = C

  0     1     2     3     4     5     6
┌─────┬─────┬─────┬─────┬─────┬─────┬─────┐
│     │     │     │  A  │  B  │  C  │     │
└─────┴─────┴─────┴─────┴─────┴─────┴─────┘
```

### 4. 로드 팩터와 리해싱
```java
loadFactor = size / capacity

// 로드 팩터가 임계값(0.75) 초과 시 리해싱
if (size > capacity * 0.75) {
    resize(capacity * 2);  // 용량 2배로 확장
    // 모든 요소 재배치 (해시값 재계산)
}
```

---

## 📝 POP 구현 해설 (체이닝)
```java
public class ChainingHashMap {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    
    private Entry[] buckets;
    private int size;
    
    static class Entry {
        String key;
        int value;
        Entry next;
        
        Entry(String key, int value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
    
    public ChainingHashMap() {
        this.buckets = new Entry[DEFAULT_CAPACITY];
        this.size = 0;
    }
    
    private int hash(String key) {
        if (key == null) return 0;
        return key.hashCode() & (buckets.length - 1);
    }
    
    public void put(String key, int value) {
        if (size > buckets.length * LOAD_FACTOR) {
            resize();
        }
        
        int index = hash(key);
        Entry current = buckets[index];
        
        // 키가 이미 존재하면 값 업데이트
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        
        // 새 엔트리 추가 (맨 앞에)
        buckets[index] = new Entry(key, value, buckets[index]);
        size++;
    }
    
    public Integer get(String key) {
        int index = hash(key);
        Entry current = buckets[index];
        
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }
    
    public Integer remove(String key) {
        int index = hash(key);
        Entry current = buckets[index];
        Entry prev = null;
        
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                if (prev == null) {
                    buckets[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }
    
    private void resize() {
        Entry[] oldBuckets = buckets;
        buckets = new Entry[oldBuckets.length * 2];
        size = 0;
        
        for (Entry bucket : oldBuckets) {
            Entry current = bucket;
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }
}
```

---

## 📝 POP 구현 해설 (개방 주소법 - 선형 탐사)
```java
public class LinearProbingHashMap {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.5f;  // 개방 주소법은 더 낮게
    
    private String[] keys;
    private Integer[] values;
    private int size;
    private int capacity;
    
    public LinearProbingHashMap() {
        this.capacity = DEFAULT_CAPACITY;
        this.keys = new String[capacity];
        this.values = new Integer[capacity];
        this.size = 0;
    }
    
    private int hash(String key) {
        if (key == null) return 0;
        return (key.hashCode() & 0x7FFFFFFF) % capacity;
    }
    
    public void put(String key, int value) {
        if (size > capacity * LOAD_FACTOR) {
            resize();
        }
        
        int index = hash(key);
        
        while (keys[index] != null) {
            if (Objects.equals(keys[index], key)) {
                values[index] = value;  // 업데이트
                return;
            }
            index = (index + 1) % capacity;  // 선형 탐사
        }
        
        keys[index] = key;
        values[index] = value;
        size++;
    }
    
    public Integer get(String key) {
        int index = hash(key);
        
        while (keys[index] != null) {
            if (Objects.equals(keys[index], key)) {
                return values[index];
            }
            index = (index + 1) % capacity;
        }
        return null;
    }
    
    public Integer remove(String key) {
        int index = hash(key);
        
        while (keys[index] != null) {
            if (Objects.equals(keys[index], key)) {
                Integer oldValue = values[index];
                keys[index] = null;
                values[index] = null;
                size--;
                
                // 연속된 요소 재배치 (클러스터 유지)
                index = (index + 1) % capacity;
                while (keys[index] != null) {
                    String rehashKey = keys[index];
                    Integer rehashValue = values[index];
                    keys[index] = null;
                    values[index] = null;
                    size--;
                    put(rehashKey, rehashValue);
                    index = (index + 1) % capacity;
                }
                
                return oldValue;
            }
            index = (index + 1) % capacity;
        }
        return null;
    }
    
    private void resize() {
        String[] oldKeys = keys;
        Integer[] oldValues = values;
        
        capacity *= 2;
        keys = new String[capacity];
        values = new Integer[capacity];
        size = 0;
        
        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != null) {
                put(oldKeys[i], oldValues[i]);
            }
        }
    }
}
```

---

## 📝 OOP 구현 해설
```java
public class HashMap<K, V> implements Map<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    
    private Node<K, V>[] table;
    private int size;
    
    private static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;
        
        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
        @Override
        public K getKey() { return key; }
        
        @Override
        public V getValue() { return value; }
        
        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
    
    @SuppressWarnings("unchecked")
    public HashMap() {
        this.table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
    }
    
    private int hash(Object key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return h ^ (h >>> 16);  // 상위 비트 혼합
    }
    
    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }
    
    @Override
    public V put(K key, V value) {
        if (size > table.length * LOAD_FACTOR) {
            resize();
        }
        
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        
        for (Node<K, V> e = table[index]; e != null; e = e.next) {
            if (e.hash == hash && Objects.equals(e.key, key)) {
                V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }
        
        table[index] = new Node<>(hash, key, value, table[index]);
        size++;
        return null;
    }
    
    @Override
    public V get(Object key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        
        for (Node<K, V> e = table[index]; e != null; e = e.next) {
            if (e.hash == hash && Objects.equals(e.key, key)) {
                return e.value;
            }
        }
        return null;
    }
    
    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Node<K, V> bucket : table) {
            for (Node<K, V> e = bucket; e != null; e = e.next) {
                keys.add(e.key);
            }
        }
        return keys;
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 평균 | 최악 | 설명 |
|------|------|------|------|
| put | O(1) | O(n) | 모든 키가 같은 버킷일 때 |
| get | O(1) | O(n) | 체이닝 리스트 순회 |
| remove | O(1) | O(n) | 체이닝 리스트 순회 |
| containsKey | O(1) | O(n) | get과 동일 |
| containsValue | O(n) | O(n) | 전체 순회 필요 |
| resize | O(n) | O(n) | 모든 요소 재배치 |

---

## ❌ 흔한 실수

### 1. hashCode와 equals 불일치
```java
// 규칙: equals가 true면 hashCode도 같아야 함
class Person {
    String name;
    
    // 잘못됨: equals만 오버라이드
    @Override
    public boolean equals(Object o) {
        if (o instanceof Person p) {
            return name.equals(p.name);
        }
        return false;
    }
    // hashCode 미구현 → HashMap에서 문제!
}

// 올바름: 둘 다 오버라이드
@Override
public int hashCode() {
    return Objects.hash(name);
}
```

### 2. 가변 키 사용
```java
// 위험: 키가 변경되면 해시값이 달라져 찾을 수 없음
List<Integer> key = new ArrayList<>();
key.add(1);
map.put(key, "value");
key.add(2);  // 키 변경!
map.get(key);  // null! (해시값이 달라짐)

// 권장: 불변 객체를 키로 사용 (String, Integer, record 등)
```

### 3. 개방 주소법 삭제 시 문제
```java
// 단순 삭제 시 검색 체인이 끊김
// A → B → C (같은 해시)
// B 삭제하면 C를 찾을 수 없음!

// 해결: 삭제 표시(tombstone) 또는 재배치
```

---

## 🔗 관련 문제

- LeetCode 1: Two Sum (해시맵 활용)
- LeetCode 706: Design HashMap
- LeetCode 49: Group Anagrams
- LeetCode 128: Longest Consecutive Sequence
- LeetCode 380: Insert Delete GetRandom O(1)
