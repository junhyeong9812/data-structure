# 해시맵 구현에 유용한 Java API

## 📦 해시 관련

### Object.hashCode()
모든 객체의 해시코드 반환
```java
String s = "hello";
int hash = s.hashCode();  // 99162322

// 주요 타입별 해시코드
"hello".hashCode();       // 문자열: 각 문자 기반 계산
Integer.valueOf(42).hashCode();  // Integer: 값 자체
new Object().hashCode();  // Object: 메모리 주소 기반
```

### Objects.hash() (Java 7+)
여러 필드의 해시코드 조합
```java
import java.util.Objects;

// 여러 필드를 조합한 해시코드
@Override
public int hashCode() {
    return Objects.hash(name, age, email);
}

// 내부적으로 Arrays.hashCode() 사용
// 31 * result + element.hashCode() 패턴
```

### Objects.hashCode()
null-safe 해시코드
```java
// null이면 0 반환
Objects.hashCode(null);  // 0
Objects.hashCode("hi");  // "hi".hashCode()
```

### 해시 분산 개선
```java
// Java HashMap의 해시 분산 기법
static int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
// 상위 16비트를 하위 16비트와 XOR
// 작은 테이블에서도 상위 비트가 영향을 미치도록
```

---

## 🔄 동등성 비교

### Objects.equals() (Java 7+)
null-safe 동등성 비교
```java
import java.util.Objects;

Objects.equals(null, null);      // true
Objects.equals(null, "hello");   // false
Objects.equals("a", "a");        // true

// HashMap에서 키 비교 시 필수
if (Objects.equals(entry.key, key)) {
    return entry.value;
}
```

### equals/hashCode 계약
```java
// 1. equals가 true면 hashCode도 같아야 함
// 2. hashCode가 같아도 equals는 false일 수 있음 (충돌)
// 3. equals는 대칭적, 추이적, 일관적이어야 함

// Record는 자동으로 올바른 equals/hashCode 생성
record Person(String name, int age) {}
```

---

## 🔢 비트 연산

### 버킷 인덱스 계산
```java
// capacity가 2의 거듭제곱일 때
int index = hash & (capacity - 1);  // % 연산보다 빠름

// 예: capacity = 16 (0b10000)
// capacity - 1 = 15 (0b01111)
// hash & 15 → 하위 4비트만 사용

// 음수 해시 처리
int index = (hash & 0x7FFFFFFF) % capacity;  // 양수로 변환
```

### 2의 거듭제곱 확인/계산
```java
// 2의 거듭제곱 확인
boolean isPowerOfTwo = (n & (n - 1)) == 0 && n > 0;

// 다음 2의 거듭제곱
int tableSizeFor(int cap) {
    int n = cap - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : n + 1;
}

// Integer 유틸리티
Integer.highestOneBit(n);  // 가장 높은 1비트
Integer.bitCount(n);       // 1비트 개수
```

---

## 📐 배열/컬렉션 관련

### 배열 생성 및 복사
```java
// 제네릭 배열 생성
@SuppressWarnings("unchecked")
Node<K,V>[] table = (Node<K,V>[]) new Node[capacity];

// 배열 복사 (리해싱 시)
Node<K,V>[] newTable = Arrays.copyOf(table, newCapacity);
```

### Set/Collection 구현
```java
import java.util.Set;
import java.util.Collection;
import java.util.AbstractSet;
import java.util.AbstractCollection;

// keySet 구현 예
public Set<K> keySet() {
    return new AbstractSet<K>() {
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public int size() {
            return HashMap.this.size;
        }
        
        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }
    };
}
```

---

## 🔁 Iterator 구현

### 해시맵 Iterator
```java
abstract class HashIterator {
    Node<K,V> next;
    Node<K,V> current;
    int index;
    
    HashIterator() {
        // 첫 번째 비어있지 않은 버킷 찾기
        Node<K,V>[] t = table;
        current = next = null;
        index = 0;
        if (t != null && size > 0) {
            while (index < t.length && (next = t[index++]) == null);
        }
    }
    
    public boolean hasNext() {
        return next != null;
    }
    
    Node<K,V> nextNode() {
        Node<K,V> e = next;
        if (e == null)
            throw new NoSuchElementException();
        current = e;
        if ((next = e.next) == null) {
            Node<K,V>[] t = table;
            while (index < t.length && (next = t[index++]) == null);
        }
        return e;
    }
}

class KeyIterator extends HashIterator implements Iterator<K> {
    public K next() { return nextNode().key; }
}

class ValueIterator extends HashIterator implements Iterator<V> {
    public V next() { return nextNode().value; }
}
```

---

## 🗂️ Map 인터페이스 관련

### Map.Entry<K,V>
```java
import java.util.Map;

// Entry 구현
class Node<K,V> implements Map.Entry<K,V> {
    final K key;
    V value;
    
    @Override
    public K getKey() { return key; }
    
    @Override
    public V getValue() { return value; }
    
    @Override
    public V setValue(V newValue) {
        V oldValue = value;
        value = newValue;
        return oldValue;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Map.Entry<?,?> e) {
            return Objects.equals(key, e.getKey()) &&
                   Objects.equals(value, e.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }
}
```

### Map 기본 메서드 (Java 8+)
```java
// getOrDefault
V value = map.getOrDefault(key, defaultValue);

// putIfAbsent
map.putIfAbsent(key, value);  // 키 없을 때만 추가

// computeIfAbsent
map.computeIfAbsent(key, k -> new ArrayList<>());

// computeIfPresent
map.computeIfPresent(key, (k, v) -> v + 1);

// merge
map.merge(key, 1, Integer::sum);  // 카운팅에 유용

// forEach
map.forEach((k, v) -> System.out.println(k + "=" + v));
```

---

## 🧪 테스트 관련

### JUnit 5 + AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldPutAndGet() {
    HashMap<String, Integer> map = new HashMap<>();
    map.put("a", 1);
    map.put("b", 2);
    
    assertThat(map.get("a")).isEqualTo(1);
    assertThat(map.get("b")).isEqualTo(2);
    assertThat(map.get("c")).isNull();
}

@Test
void shouldHandleCollisions() {
    // 같은 해시값을 가지는 키 테스트
    HashMap<CollisionKey, Integer> map = new HashMap<>();
    // ...
}

@Test
void shouldResizeCorrectly() {
    HashMap<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < 1000; i++) {
        map.put(i, i);
    }
    
    assertThat(map.size()).isEqualTo(1000);
    for (int i = 0; i < 1000; i++) {
        assertThat(map.get(i)).isEqualTo(i);
    }
}
```

---

## 📚 Java 21 관련

### Record를 키로 사용
```java
// Record는 자동으로 equals/hashCode 구현
record Point(int x, int y) {}

HashMap<Point, String> map = new HashMap<>();
map.put(new Point(1, 2), "A");
map.put(new Point(1, 2), "B");  // 같은 키로 인식

assertThat(map.size()).isEqualTo(1);
assertThat(map.get(new Point(1, 2))).isEqualTo("B");
```

### SequencedMap (Java 21)
```java
// LinkedHashMap이 SequencedMap 구현
SequencedMap<String, Integer> map = new LinkedHashMap<>();
map.put("a", 1);
map.put("b", 2);
map.put("c", 3);

map.firstEntry();  // a=1
map.lastEntry();   // c=3
map.pollFirstEntry();  // a=1 제거 및 반환
map.reversed();    // 역순 뷰
```

### Pattern Matching
```java
public V get(Object key) {
    Node<K,V> e = getNode(hash(key), key);
    return e == null ? null : e.value;
}

// instanceof 패턴 매칭
if (o instanceof Map.Entry<?,?> entry) {
    return Objects.equals(key, entry.getKey()) &&
           Objects.equals(value, entry.getValue());
}
```

---

## ⚡ 성능 팁

### 1. 초기 용량 지정
```java
// 예상 크기를 알면 미리 할당 (리해싱 방지)
int expectedSize = 1000;
int initialCapacity = (int) (expectedSize / 0.75f) + 1;
HashMap<K,V> map = new HashMap<>(initialCapacity);
```

### 2. 좋은 해시 함수
```java
// 나쁜 해시 함수 (충돌 많음)
public int hashCode() {
    return 1;  // 모든 객체가 같은 버킷!
}

// 좋은 해시 함수 (균등 분포)
public int hashCode() {
    return Objects.hash(field1, field2, field3);
}
```

### 3. String 키 최적화
```java
// String.hashCode()는 캐시됨 (첫 호출 후)
// 동일 문자열 반복 사용 시 효율적
```
