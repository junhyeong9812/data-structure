# 스킵 리스트 구현에 유용한 Java API

## 📦 Random (확률적 레벨)

### Random 클래스
```java
import java.util.Random;

Random random = new Random();
Random random = new Random(seed);  // 시드 지정 (테스트용)

// 기본 메서드
random.nextDouble();    // 0.0 이상 1.0 미만
random.nextInt();       // 모든 int 범위
random.nextInt(bound);  // 0 이상 bound 미만
random.nextBoolean();   // true/false
random.nextLong();

// 레벨 결정
private int randomLevel() {
    int level = 0;
    while (random.nextDouble() < 0.5 && level < MAX_LEVEL) {
        level++;
    }
    return level;
}

// 또는 비트 연산으로 더 효율적
private int randomLevel() {
    int level = 0;
    int r = random.nextInt();
    while ((r & 1) == 1 && level < MAX_LEVEL) {
        level++;
        r >>= 1;
    }
    return level;
}
```

### ThreadLocalRandom (멀티스레드)
```java
import java.util.concurrent.ThreadLocalRandom;

// 스레드별 독립적인 Random
double d = ThreadLocalRandom.current().nextDouble();
int i = ThreadLocalRandom.current().nextInt(10);
```

---

## 📊 Comparable 인터페이스

### 기본 사용
```java
// Comparable 제네릭 바운드
public class SkipList<K extends Comparable<K>> {
    // K 타입은 비교 가능해야 함
}

// 비교
int cmp = key1.compareTo(key2);
// cmp < 0: key1 < key2
// cmp == 0: key1 == key2
// cmp > 0: key1 > key2

// 사용 예
while (current.forward[i] != null &&
       current.forward[i].key.compareTo(key) < 0) {
    current = current.forward[i];
}
```

### Comparator 지원
```java
public class SkipList<K> {
    private final Comparator<K> comparator;
    
    public SkipList(Comparator<K> comparator) {
        this.comparator = comparator;
    }
    
    private int compare(K k1, K k2) {
        if (comparator != null) {
            return comparator.compare(k1, k2);
        }
        @SuppressWarnings("unchecked")
        Comparable<K> c = (Comparable<K>) k1;
        return c.compareTo(k2);
    }
}
```

---

## 🔗 제네릭 배열 처리

### @SuppressWarnings 사용
```java
@SuppressWarnings("unchecked")
public class SkipList<K extends Comparable<K>> {
    
    @SuppressWarnings("unchecked")
    private Node<K>[] createArray(int size) {
        return (Node<K>[]) new Node[size];
    }
    
    static class Node<K> {
        K key;
        Node<K>[] forward;
        
        @SuppressWarnings("unchecked")
        Node(K key, int level) {
            this.key = key;
            this.forward = new Node[level + 1];
        }
    }
}
```

### Object 배열 활용
```java
// 제네릭 배열 대신 Object 배열 사용
Object[] elements = new Object[size];

@SuppressWarnings("unchecked")
K getKey(int index) {
    return (K) elements[index];
}
```

---

## 📋 List/Iterator

### ArrayList
```java
import java.util.ArrayList;
import java.util.List;

// 범위 쿼리 결과
public List<K> range(K from, K to) {
    List<K> result = new ArrayList<>();
    // ...
    result.add(key);
    return result;
}
```

### Iterator 구현
```java
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SkipList<K extends Comparable<K>> implements Iterable<K> {
    
    @Override
    public Iterator<K> iterator() {
        return new SkipListIterator();
    }
    
    private class SkipListIterator implements Iterator<K> {
        private Node<K> current = head.forward[0];
        
        @Override
        public boolean hasNext() {
            return current != null;
        }
        
        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            K key = current.key;
            current = current.forward[0];
            return key;
        }
    }
}

// 사용
for (K key : skipList) {
    System.out.println(key);
}
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldSearchInsertedElements() {
    SkipList<Integer> list = new SkipList<>();
    list.add(5);
    list.add(10);
    list.add(3);
    
    assertThat(list.search(5)).isTrue();
    assertThat(list.search(10)).isTrue();
    assertThat(list.search(7)).isFalse();
}

@Test
void shouldMaintainSortedOrder() {
    SkipList<Integer> list = new SkipList<>();
    list.add(5);
    list.add(2);
    list.add(8);
    list.add(1);
    
    List<Integer> elements = new ArrayList<>();
    for (int x : list) {
        elements.add(x);
    }
    
    assertThat(elements).containsExactly(1, 2, 5, 8);
}

@Test
void rangeShouldReturnElementsInRange() {
    SkipList<Integer> list = new SkipList<>();
    for (int i = 1; i <= 10; i++) {
        list.add(i);
    }
    
    assertThat(list.range(3, 7)).containsExactly(3, 4, 5, 6, 7);
}
```

### 확률적 테스트
```java
@Test
void averageHeightShouldBeLogarithmic() {
    // 많은 샘플로 평균 확인
    int samples = 10000;
    int totalLevel = 0;
    Random random = new Random(42);  // 고정 시드
    
    for (int i = 0; i < samples; i++) {
        int level = 0;
        while (random.nextDouble() < 0.5 && level < 16) {
            level++;
        }
        totalLevel += level;
    }
    
    double avgLevel = (double) totalLevel / samples;
    // 기대값: 약 1 (geometric distribution mean = p/(1-p) = 1)
    assertThat(avgLevel).isBetween(0.8, 1.2);
}
```

---

## 📚 Java 21 관련

### Record로 Entry
```java
public record Entry<K, V>(K key, V value) {}

// 범위 쿼리 결과
public List<Entry<K, V>> rangeEntries(K from, K to) {
    List<Entry<K, V>> result = new ArrayList<>();
    // ...
    result.add(new Entry<>(node.key, node.value));
    return result;
}
```

### Sealed Class (노드 타입)
```java
sealed interface SkipListNode<K> permits HeadNode, DataNode {
    Node<K>[] forward();
}

final class HeadNode<K> implements SkipListNode<K> {
    private final Node<K>[] forward;
    // ...
}

final class DataNode<K> implements SkipListNode<K> {
    private final K key;
    private final Node<K>[] forward;
    // ...
}
```

### Pattern Matching
```java
public void printNode(Object node) {
    switch (node) {
        case HeadNode<?> h -> System.out.println("HEAD");
        case DataNode<?> d -> System.out.println("Data: " + d.key());
        case null -> System.out.println("NULL");
        default -> System.out.println("Unknown");
    }
}
```

---

## 🔄 NavigableSet 인터페이스

### 참고용 메서드들
```java
// Java의 NavigableSet 인터페이스 (TreeSet이 구현)
NavigableSet<E> interface {
    E floor(E e);      // e 이하 최대
    E ceiling(E e);    // e 이상 최소
    E lower(E e);      // e 미만 최대
    E higher(E e);     // e 초과 최소
    
    E first();         // 최소
    E last();          // 최대
    
    E pollFirst();     // 최소 제거 및 반환
    E pollLast();      // 최대 제거 및 반환
    
    NavigableSet<E> subSet(E from, boolean fromInclusive, 
                           E to, boolean toInclusive);
    NavigableSet<E> headSet(E to, boolean inclusive);
    NavigableSet<E> tailSet(E from, boolean inclusive);
    
    NavigableSet<E> descendingSet();  // 역순 뷰
    Iterator<E> descendingIterator();
}
```

---

## ⚡ 성능 팁

### 1. 레벨 생성 최적화
```java
// 비트 연산 버전 (더 빠름)
private int randomLevel() {
    int x = random.nextInt();
    int level = 0;
    while (((x >> level) & 1) == 1 && level < MAX_LEVEL) {
        level++;
    }
    return level;
}

// 또는 Integer.numberOfTrailingZeros 사용
private int randomLevel() {
    int x = random.nextInt();
    if (x == 0) return MAX_LEVEL;
    return Math.min(Integer.numberOfTrailingZeros(x), MAX_LEVEL);
}
```

### 2. 배열 재사용 (update)
```java
// 클래스 필드로 선언하여 재사용
private final Node<K>[] update;

public SkipList() {
    this.update = createArray(MAX_LEVEL + 1);
}

// 주의: 멀티스레드에서는 사용 불가
```

### 3. 검색 경로 재사용
```java
// 삽입/삭제 시 검색 경로를 반환하여 재사용
private Node<K> search(K key, Node<K>[] update) {
    Node<K> current = head;
    
    for (int i = level; i >= 0; i--) {
        while (current.forward[i] != null &&
               current.forward[i].key.compareTo(key) < 0) {
            current = current.forward[i];
        }
        if (update != null) {
            update[i] = current;
        }
    }
    
    return current.forward[0];
}
```

---

## 🔀 ConcurrentSkipListMap

### Java 표준 라이브러리 참고
```java
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

// Thread-safe Skip List
ConcurrentSkipListMap<String, Integer> map = new ConcurrentSkipListMap<>();
map.put("apple", 1);
map.get("apple");
map.floorKey("banana");
map.ceilingKey("aardvark");

ConcurrentSkipListSet<Integer> set = new ConcurrentSkipListSet<>();
set.add(5);
set.floor(4);  // 4 이하 최대
set.ceiling(6); // 6 이상 최소
```

### Lock-Free 알고리즘 아이디어
```java
// CAS (Compare-And-Swap) 사용
AtomicReference<Node<K>> forward = new AtomicReference<>();

// 논리적 삭제 후 물리적 삭제
boolean marked;  // 삭제 표시

// 자세한 구현은 Doug Lea의 논문 참고
```
