# 큐/덱 구현에 유용한 Java API

## 📦 기본 큐/덱 관련

### java.util.Queue<E> 인터페이스
```java
import java.util.Queue;
import java.util.LinkedList;

Queue<Integer> queue = new LinkedList<>();

// 삽입 (실패 시 예외 vs false)
queue.add(1);      // 실패 시 IllegalStateException
queue.offer(1);    // 실패 시 false 반환

// 삭제 (비어있을 때 예외 vs null)
queue.remove();    // 비어있으면 NoSuchElementException
queue.poll();      // 비어있으면 null 반환

// 조회 (비어있을 때 예외 vs null)
queue.element();   // 비어있으면 NoSuchElementException
queue.peek();      // 비어있으면 null 반환

queue.isEmpty();   // 비어있는지 확인
queue.size();      // 크기
```

### java.util.Deque<E> 인터페이스
```java
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.LinkedList;

Deque<Integer> deque = new ArrayDeque<>();  // 배열 기반 (권장)
Deque<Integer> deque = new LinkedList<>();  // 연결 리스트 기반

// 앞쪽 연산
deque.addFirst(1);     // 예외 발생 가능
deque.offerFirst(1);   // false 반환 가능
deque.removeFirst();   // 예외 발생 가능
deque.pollFirst();     // null 반환 가능
deque.getFirst();      // 예외 발생 가능
deque.peekFirst();     // null 반환 가능

// 뒤쪽 연산
deque.addLast(1);
deque.offerLast(1);
deque.removeLast();
deque.pollLast();
deque.getLast();
deque.peekLast();

// 스택처럼 사용
deque.push(1);         // addFirst와 동일
deque.pop();           // removeFirst와 동일

// 큐처럼 사용
deque.offer(1);        // offerLast와 동일
deque.poll();          // pollFirst와 동일
```

### ArrayDeque vs LinkedList
```java
// ArrayDeque - 배열 기반
// 장점: 메모리 효율적, 캐시 친화적, 빠름
// 단점: null 저장 불가

// LinkedList - 연결 리스트 기반
// 장점: null 저장 가능
// 단점: 메모리 오버헤드, 노드 할당 비용

// 일반적으로 ArrayDeque 권장!
Deque<Integer> deque = new ArrayDeque<>();
```

---

## 🔢 수학 연산 (원형 인덱스용)

### 모듈러 연산
```java
// 다음 인덱스 (순환)
int next = (current + 1) % capacity;

// 이전 인덱스 (순환) - 주의!
int prev = (current - 1 + capacity) % capacity;

// Java의 % 연산은 음수를 반환할 수 있음
-1 % 5  // = -1 (Java)
// Python에서는 4

// Math.floorMod 사용 (Java 8+)
Math.floorMod(-1, 5);  // = 4 (항상 양수)
```

### 비트 연산 (2의 거듭제곱 용량일 때)
```java
// capacity가 2의 거듭제곱이면 비트 연산으로 최적화
int next = (current + 1) & (capacity - 1);  // % 대신
int prev = (current - 1) & (capacity - 1);

// 2의 거듭제곱 확인
boolean isPowerOfTwo = (n & (n - 1)) == 0 && n > 0;

// 다음 2의 거듭제곱
int nextPow2 = Integer.highestOneBit(n - 1) << 1;
```

---

## 📐 배열 관련

### Arrays 클래스
```java
import java.util.Arrays;

// 배열 복사 (확장)
int[] newData = Arrays.copyOf(data, newCapacity);

// 범위 복사
int[] slice = Arrays.copyOfRange(data, from, to);

// 배열 채우기
Arrays.fill(data, 0);
Arrays.fill(data, from, to, value);

// 배열 출력
System.out.println(Arrays.toString(data));
```

### System.arraycopy
```java
// 순환 배열을 일반 배열로 복사
// front부터 size개 요소를 새 배열에 복사
int[] newData = new int[size];
if (front + size <= capacity) {
    System.arraycopy(data, front, newData, 0, size);
} else {
    // 두 부분으로 나눠서 복사
    int firstPart = capacity - front;
    System.arraycopy(data, front, newData, 0, firstPart);
    System.arraycopy(data, 0, newData, firstPart, size - firstPart);
}
```

---

## ⚠️ 예외 클래스

### NoSuchElementException
빈 큐/덱에서 요소 접근 시
```java
import java.util.NoSuchElementException;

public E removeFirst() {
    if (isEmpty()) {
        throw new NoSuchElementException("Deque is empty");
    }
    // ...
}
```

### IllegalStateException
용량 제한 초과 시
```java
public boolean add(E element) {
    if (size >= capacity) {
        throw new IllegalStateException("Queue full");
    }
    // ...
}
```

---

## 🔄 제네릭 관련

### Object 배열 사용
```java
public class ArrayDeque<E> {
    private Object[] elements;
    
    @SuppressWarnings("unchecked")
    public E removeFirst() {
        E result = (E) elements[front];
        elements[front] = null;  // GC 도움
        front = (front + 1) % elements.length;
        return result;
    }
}
```

### null 처리
```java
// ArrayDeque는 null을 허용하지 않음
public void addFirst(E e) {
    if (e == null) {
        throw new NullPointerException();
    }
    // ...
}

// LinkedList는 null 허용
// null과 비어있음을 구분하려면 별도 처리 필요
```

---

## 🧪 테스트 관련

### JUnit 5 + AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldFollowFIFO() {
    Queue<Integer> queue = new ArrayQueue<>();
    queue.offer(1);
    queue.offer(2);
    queue.offer(3);
    
    assertThat(queue.poll()).isEqualTo(1);  // FIFO
    assertThat(queue.poll()).isEqualTo(2);
    assertThat(queue.poll()).isEqualTo(3);
}

@Test
void shouldHandleCircular() {
    CircularQueue queue = new CircularQueue(3);
    queue.enqueue(1);
    queue.enqueue(2);
    queue.enqueue(3);
    queue.dequeue();  // 1 제거
    queue.enqueue(4); // 원형으로 재사용
    
    assertThat(queue.dequeue()).isEqualTo(2);
    assertThat(queue.dequeue()).isEqualTo(3);
    assertThat(queue.dequeue()).isEqualTo(4);
}

@Test
void shouldThrowOnEmptyDequeue() {
    Queue<Integer> queue = new ArrayQueue<>();
    
    assertThatThrownBy(() -> queue.remove())
        .isInstanceOf(NoSuchElementException.class);
}
```

---

## 📚 Java 21 관련

### SequencedCollection (Java 21)
```java
// Deque가 SequencedCollection 구현
Deque<Integer> deque = new ArrayDeque<>();
deque.addLast(1);
deque.addLast(2);
deque.addLast(3);

Integer first = deque.getFirst();  // 1
Integer last = deque.getLast();    // 3

// 역순 뷰
SequencedCollection<Integer> reversed = deque.reversed();
for (Integer i : reversed) {
    System.out.println(i);  // 3, 2, 1
}
```

### Record (불변 결과용)
```java
// 슬라이딩 윈도우 결과
public record WindowResult(int windowStart, int maxValue) {}

public List<WindowResult> maxSlidingWindowWithIndex(int[] nums, int k) {
    List<WindowResult> results = new ArrayList<>();
    // ...
    results.add(new WindowResult(i - k + 1, maxVal));
    return results;
}
```

### Pattern Matching
```java
public void processElement(Object obj) {
    if (obj instanceof Integer i) {
        queue.offer(i);
    } else if (obj instanceof String s) {
        queue.offer(Integer.parseInt(s));
    }
}
```

---

## ⚡ 성능 팁

### 1. 초기 용량 지정
```java
// 예상 크기를 알면 미리 할당
Deque<Integer> deque = new ArrayDeque<>(1000);
```

### 2. 2의 거듭제곱 용량
```java
// ArrayDeque는 내부적으로 2의 거듭제곱 용량 사용
// 직접 구현 시에도 이 방식 권장 (비트 연산 최적화)
private int calculateCapacity(int minCapacity) {
    int n = minCapacity - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return n + 1;
}
```

### 3. null 제거로 GC 도움
```java
public E poll() {
    E result = elements[front];
    elements[front] = null;  // 참조 제거
    front = (front + 1) % capacity;
    return result;
}
```

---

## 🎯 응용 패턴

### Monotonic Deque (단조 덱)
```java
// 슬라이딩 윈도우 최댓값에 사용
Deque<Integer> deque = new ArrayDeque<>();

for (int i = 0; i < nums.length; i++) {
    // 현재 값보다 작은 요소들 제거 (뒤에서)
    while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
        deque.pollLast();
    }
    deque.offerLast(i);
    
    // 윈도우 범위 벗어난 요소 제거 (앞에서)
    while (deque.peekFirst() <= i - k) {
        deque.pollFirst();
    }
}
```

### Double-ended 우선순위 큐 대용
```java
// 최소/최대 둘 다 O(1)으로 접근하고 싶을 때
// TreeMap 사용
TreeMap<Integer, Integer> map = new TreeMap<>();  // 값 -> 개수

map.firstKey();  // 최솟값
map.lastKey();   // 최댓값
```
