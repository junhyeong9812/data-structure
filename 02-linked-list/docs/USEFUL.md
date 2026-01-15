# 연결 리스트 구현에 유용한 Java API

## 📦 기본 연결 리스트 관련

### java.util.LinkedList<E>
Java 표준 이중 연결 리스트 (참고용)
```java
import java.util.LinkedList;

LinkedList<Integer> list = new LinkedList<>();

// Deque 인터페이스 메서드
list.addFirst(1);      // 맨 앞에 추가
list.addLast(2);       // 맨 뒤에 추가
list.removeFirst();    // 맨 앞 삭제 및 반환
list.removeLast();     // 맨 뒤 삭제 및 반환
list.getFirst();       // 맨 앞 조회
list.getLast();        // 맨 뒤 조회
list.peekFirst();      // 맨 앞 조회 (없으면 null)
list.peekLast();       // 맨 뒤 조회 (없으면 null)
list.pollFirst();      // 맨 앞 삭제 및 반환 (없으면 null)
list.pollLast();       // 맨 뒤 삭제 및 반환 (없으면 null)

// List 인터페이스 메서드
list.add(index, element);
list.get(index);
list.set(index, element);
list.remove(index);

// 스택 연산
list.push(element);    // addFirst와 동일
list.pop();            // removeFirst와 동일

// 검색
list.contains(element);
list.indexOf(element);
list.lastIndexOf(element);
```

---

## 🔄 Iterator 관련

### java.util.Iterator<E>
```java
import java.util.Iterator;

public class MyLinkedList<E> implements Iterable<E> {
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> current = head;
            
            @Override
            public boolean hasNext() {
                return current != null;
            }
            
            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                E data = current.data;
                current = current.next;
                return data;
            }
            
            @Override
            public void remove() {
                // 선택적 구현
                throw new UnsupportedOperationException();
            }
        };
    }
}

// 사용
for (E element : myList) {
    System.out.println(element);
}
```

### java.util.ListIterator<E>
양방향 순회 지원
```java
import java.util.ListIterator;

ListIterator<E> iter = list.listIterator();
iter.hasNext();      // 다음 요소 존재 여부
iter.next();         // 다음 요소
iter.hasPrevious();  // 이전 요소 존재 여부
iter.previous();     // 이전 요소
iter.nextIndex();    // 다음 요소의 인덱스
iter.previousIndex(); // 이전 요소의 인덱스
iter.add(element);   // 현재 위치에 삽입
iter.set(element);   // 마지막 반환 요소 교체
iter.remove();       // 마지막 반환 요소 삭제
```

---

## ⚠️ 예외 클래스

### NoSuchElementException
빈 컬렉션에서 요소 접근 시
```java
import java.util.NoSuchElementException;

public E removeFirst() {
    if (isEmpty()) {
        throw new NoSuchElementException("List is empty");
    }
    // ...
}
```

### IndexOutOfBoundsException
유효하지 않은 인덱스 접근 시
```java
public E get(int index) {
    if (index < 0 || index >= size) {
        throw new IndexOutOfBoundsException(
            "Index: " + index + ", Size: " + size
        );
    }
    // ...
}
```

### NullPointerException
null 체크
```java
import java.util.Objects;

public boolean contains(Object o) {
    // null-safe 비교
    for (Node<E> n = head; n != null; n = n.next) {
        if (Objects.equals(n.data, o)) {
            return true;
        }
    }
    return false;
}
```

---

## 🛠️ Objects 유틸리티

### Objects.equals()
null-safe 동등성 비교
```java
import java.util.Objects;

// null도 안전하게 비교
Objects.equals(null, null);      // true
Objects.equals(null, "hello");   // false
Objects.equals("hello", "hello"); // true

// indexOf 구현에 유용
public int indexOf(Object o) {
    int index = 0;
    for (Node<E> n = head; n != null; n = n.next) {
        if (Objects.equals(n.data, o)) {
            return index;
        }
        index++;
    }
    return -1;
}
```

### Objects.requireNonNull()
null 체크 및 예외 발생
```java
public void add(E element) {
    Objects.requireNonNull(element, "Element cannot be null");
    // ...
}
```

### Objects.checkIndex() (Java 9+)
인덱스 범위 검증
```java
public E get(int index) {
    Objects.checkIndex(index, size);  // 0 <= index < size
    return getNode(index).data;
}
```

---

## 🔢 제네릭 관련

### 내부 클래스 정의
```java
public class LinkedList<E> {
    // 정적 내부 클래스 권장 (메모리 효율)
    private static class Node<E> {
        E data;
        Node<E> next;
        Node<E> prev;
        
        Node(E data) {
            this.data = data;
        }
        
        Node(E data, Node<E> prev, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }
    
    private Node<E> head;
    private Node<E> tail;
    private int size;
}
```

### @SuppressWarnings
```java
@SuppressWarnings("unchecked")
public E[] toArray(E[] a) {
    if (a.length < size) {
        a = (E[]) java.lang.reflect.Array.newInstance(
            a.getClass().getComponentType(), size
        );
    }
    // ...
}
```

---

## 📐 디버깅 유틸리티

### toString() 구현
```java
@Override
public String toString() {
    if (isEmpty()) return "[]";
    
    StringBuilder sb = new StringBuilder("[");
    Node<E> current = head;
    while (current != null) {
        sb.append(current.data);
        if (current.next != null) {
            sb.append(" -> ");
        }
        current = current.next;
    }
    sb.append("]");
    return sb.toString();
}
// 출력: [1 -> 2 -> 3]
```

### StringJoiner 사용
```java
import java.util.StringJoiner;

@Override
public String toString() {
    StringJoiner sj = new StringJoiner(" -> ", "[", "]");
    for (Node<E> n = head; n != null; n = n.next) {
        sj.add(String.valueOf(n.data));
    }
    return sj.toString();
}
```

---

## ⚡ 성능 팁

### 1. 불필요한 순회 피하기
```java
// 나쁨: size()가 O(n)이면 매번 순회
for (int i = 0; i < list.size(); i++) { ... }

// 좋음: size를 필드로 관리하여 O(1)
private int size;  // 삽입/삭제 시 갱신
```

### 2. 참조 초기화로 GC 돕기
```java
public E remove(int index) {
    Node<E> target = getNode(index);
    E data = target.data;
    
    // 참조 제거 (GC 도움)
    target.data = null;
    target.prev = null;
    target.next = null;
    
    return data;
}
```

### 3. 일괄 작업
```java
// 나쁨: 매번 순회
for (int val : array) {
    list.addLast(val);  // tail 있으면 O(1)
}

// 좋음: addAll 제공
public void addAll(Collection<? extends E> c) {
    for (E element : c) {
        addLast(element);
    }
}
```

---

## 🧪 테스트 관련

### JUnit 5 + AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldAddAndRetrieveElements() {
    LinkedList<Integer> list = new LinkedList<>();
    list.addLast(1);
    list.addLast(2);
    list.addLast(3);
    
    assertThat(list.size()).isEqualTo(3);
    assertThat(list.get(0)).isEqualTo(1);
    assertThat(list.getFirst()).isEqualTo(1);
    assertThat(list.getLast()).isEqualTo(3);
}

@Test
void shouldThrowOnEmptyList() {
    LinkedList<Integer> list = new LinkedList<>();
    
    assertThatThrownBy(() -> list.removeFirst())
        .isInstanceOf(NoSuchElementException.class);
}

@Test
void shouldIterateInOrder() {
    LinkedList<Integer> list = new LinkedList<>();
    list.addLast(1);
    list.addLast(2);
    list.addLast(3);
    
    assertThat(list).containsExactly(1, 2, 3);
}
```

---

## 📚 Java 21 관련

### Record로 불변 노드 (읽기 전용 스냅샷)
```java
public record NodeSnapshot<E>(E data, int index) {}

public List<NodeSnapshot<E>> snapshot() {
    List<NodeSnapshot<E>> result = new ArrayList<>();
    int index = 0;
    for (Node<E> n = head; n != null; n = n.next) {
        result.add(new NodeSnapshot<>(n.data, index++));
    }
    return result;
}
```

### SequencedCollection (Java 21)
```java
// LinkedList가 이미 구현
list.getFirst();
list.getLast();
list.addFirst(e);
list.addLast(e);
list.removeFirst();
list.removeLast();
list.reversed();  // 역순 뷰 반환
```

### Pattern Matching
```java
public void printInfo(Object obj) {
    if (obj instanceof Node<?> node) {
        System.out.println("Node data: " + node.data);
    }
}
```
