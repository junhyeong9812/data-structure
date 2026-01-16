# 힙 / 우선순위 큐 구현에 유용한 Java API

## 📦 기본 우선순위 큐

### java.util.PriorityQueue<E>
```java
import java.util.PriorityQueue;

// 기본: 최소 힙 (자연 순서)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// 최대 힙
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());

// 초기 용량 지정
PriorityQueue<Integer> pq = new PriorityQueue<>(100);

// 기본 연산
pq.offer(5);        // 삽입 O(log n)
pq.poll();          // 최솟값 제거 및 반환 O(log n), 비어있으면 null
pq.peek();          // 최솟값 조회 O(1), 비어있으면 null
pq.add(5);          // offer와 동일, 실패 시 예외
pq.remove();        // poll과 동일, 비어있으면 예외
pq.element();       // peek과 동일, 비어있으면 예외

pq.size();          // 크기
pq.isEmpty();       // 비어있는지
pq.clear();         // 전체 삭제
pq.contains(5);     // 포함 여부 O(n)
pq.remove(5);       // 특정 요소 삭제 O(n)

// 배열로 변환
Integer[] arr = pq.toArray(new Integer[0]);

// 컬렉션으로 초기화
PriorityQueue<Integer> pq = new PriorityQueue<>(Arrays.asList(3, 1, 4, 1, 5));
```

---

## 🔄 Comparator 관련

### Comparator 생성
```java
import java.util.Comparator;

// 자연 순서 (최소 힙)
Comparator<Integer> natural = Comparator.naturalOrder();

// 역순 (최대 힙)
Comparator<Integer> reverse = Comparator.reverseOrder();

// 람다
Comparator<Integer> byValue = (a, b) -> a - b;  // 주의: 오버플로우

// 메서드 참조
Comparator<Integer> safe = Integer::compare;

// 객체 필드 기준
Comparator<Person> byAge = Comparator.comparingInt(Person::getAge);
Comparator<Person> byName = Comparator.comparing(Person::getName);

// 복합 정렬
Comparator<Person> byAgeAndName = Comparator
    .comparingInt(Person::getAge)
    .thenComparing(Person::getName);

// null 처리
Comparator<String> nullsFirst = Comparator.nullsFirst(Comparator.naturalOrder());
Comparator<String> nullsLast = Comparator.nullsLast(Comparator.naturalOrder());
```

### 커스텀 객체 우선순위 큐
```java
// 방법 1: Comparable 구현
record Task(int priority, String name) implements Comparable<Task> {
    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority);
    }
}
PriorityQueue<Task> pq = new PriorityQueue<>();

// 방법 2: Comparator 제공
record Task(int priority, String name) {}
PriorityQueue<Task> pq = new PriorityQueue<>(
    Comparator.comparingInt(Task::priority)
);
```

---

## 🔢 인덱스 계산

### 부모/자식 관계
```java
// 0-based 인덱스
int parent(int i) { return (i - 1) / 2; }
int leftChild(int i) { return 2 * i + 1; }
int rightChild(int i) { return 2 * i + 2; }

// 리프 노드 판별
boolean isLeaf(int i, int size) { return i >= size / 2; }

// 마지막 비-리프 노드
int lastNonLeaf(int size) { return size / 2 - 1; }
```

### 비트 연산 최적화
```java
// 곱셈/나눗셈 대신 비트 연산
int parent(int i) { return (i - 1) >> 1; }
int leftChild(int i) { return (i << 1) + 1; }
int rightChild(int i) { return (i << 1) + 2; }
```

---

## 📐 배열 관련

### Arrays 클래스
```java
import java.util.Arrays;

// 배열 복사
int[] copy = Arrays.copyOf(arr, newLength);

// 범위 복사
int[] slice = Arrays.copyOfRange(arr, from, to);

// 배열 정렬
Arrays.sort(arr);  // 힙 정렬과 비교용

// 배열 출력
System.out.println(Arrays.toString(arr));

// 배열 채우기
Arrays.fill(arr, 0);
```

### 스왑 유틸리티
```java
// 배열 요소 교환
private void swap(int[] arr, int i, int j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
}

// XOR 스왑 (실무에서 비권장)
arr[i] ^= arr[j];
arr[j] ^= arr[i];
arr[i] ^= arr[j];
```

---

## 🧮 수학 관련

### 로그 계산 (힙 높이)
```java
// 힙 높이 = floor(log2(n))
int height = (int) (Math.log(n) / Math.log(2));

// 또는 비트 연산
int height = 31 - Integer.numberOfLeadingZeros(n);

// Integer 유틸리티
Integer.highestOneBit(n);      // 가장 높은 1비트
Integer.numberOfLeadingZeros(n); // 선행 0 개수
```

### Math 클래스
```java
Math.max(a, b);
Math.min(a, b);
Math.abs(a);
Math.floor(x);
Math.ceil(x);
```

---

## 🧪 테스트 관련

### 힙 속성 검증
```java
// 최대 힙 속성 검증
public boolean isMaxHeap(int[] heap, int size) {
    for (int i = 0; i < size / 2; i++) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        
        if (left < size && heap[i] < heap[left]) return false;
        if (right < size && heap[i] < heap[right]) return false;
    }
    return true;
}
```

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldExtractInOrder() {
    MaxHeap heap = new MaxHeap();
    heap.insert(3);
    heap.insert(1);
    heap.insert(4);
    heap.insert(1);
    heap.insert(5);
    
    assertThat(heap.extractMax()).isEqualTo(5);
    assertThat(heap.extractMax()).isEqualTo(4);
    assertThat(heap.extractMax()).isEqualTo(3);
}

@Test
void shouldThrowOnEmptyExtract() {
    MaxHeap heap = new MaxHeap();
    
    assertThatThrownBy(() -> heap.extractMax())
        .isInstanceOf(NoSuchElementException.class);
}

@Test
void heapSortShouldSortArray() {
    int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
    HeapSort.sort(arr);
    
    assertThat(arr).isSorted();
    assertThat(arr).containsExactly(1, 1, 2, 3, 4, 5, 6, 9);
}
```

---

## 📚 Java 21 관련

### Record로 힙 요소
```java
// 우선순위와 데이터를 함께 저장
public record HeapEntry<T>(int priority, T value) 
    implements Comparable<HeapEntry<T>> {
    
    @Override
    public int compareTo(HeapEntry<T> other) {
        return Integer.compare(this.priority, other.priority);
    }
}

PriorityQueue<HeapEntry<String>> pq = new PriorityQueue<>();
pq.offer(new HeapEntry<>(1, "low"));
pq.offer(new HeapEntry<>(10, "high"));
```

### Pattern Matching
```java
public void processHeapElement(Object element) {
    if (element instanceof Integer i) {
        heap.insert(i);
    } else if (element instanceof int[] arr) {
        for (int val : arr) {
            heap.insert(val);
        }
    }
}
```

### Switch Expression
```java
String heapType = switch (comparator) {
    case null -> "Min Heap (natural order)";
    case Comparator c when c == Comparator.reverseOrder() -> "Max Heap";
    default -> "Custom Heap";
};
```

---

## ⚡ 성능 팁

### 1. 초기 용량 지정
```java
// 예상 크기를 알면 미리 할당
PriorityQueue<Integer> pq = new PriorityQueue<>(1000);
```

### 2. 불필요한 오토박싱 피하기
```java
// 기본형 전용 힙 (성능 중요 시)
public class IntMaxHeap {
    private int[] heap;  // Integer[] 대신
}
```

### 3. Heapify vs 반복 삽입
```java
// 비효율: O(n log n)
for (int num : arr) {
    heap.insert(num);
}

// 효율: O(n)
MaxHeap heap = MaxHeap.heapify(arr);
```

### 4. 반복 siftDown 대신 최적화된 siftDown
```java
// 최적화: 스왑 대신 이동
private void siftDown(int i) {
    int element = heap[i];  // 시작 요소 저장
    int half = size / 2;
    
    while (i < half) {
        int largest = leftChild(i);
        int right = largest + 1;
        
        if (right < size && heap[right] > heap[largest]) {
            largest = right;
        }
        
        if (element >= heap[largest]) break;
        
        heap[i] = heap[largest];  // 스왑 대신 이동
        i = largest;
    }
    heap[i] = element;  // 최종 위치에 저장
}
```

---

## 🎯 일반적인 패턴

### Top-K 패턴
```java
// K개의 가장 큰 요소: 크기 K인 최소 힙 사용
PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);
for (int num : arr) {
    minHeap.offer(num);
    if (minHeap.size() > k) {
        minHeap.poll();  // 가장 작은 것 제거
    }
}
// minHeap에 K개의 가장 큰 요소가 남음

// K개의 가장 작은 요소: 크기 K인 최대 힙 사용
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(k, Collections.reverseOrder());
```

### 두 힙 패턴 (중앙값)
```java
PriorityQueue<Integer> small = new PriorityQueue<>(Collections.reverseOrder()); // 최대 힙
PriorityQueue<Integer> large = new PriorityQueue<>(); // 최소 힙

// small의 모든 요소 <= large의 모든 요소
// |small.size() - large.size()| <= 1
```

### 병합 패턴 (K개 정렬 리스트)
```java
PriorityQueue<int[]> pq = new PriorityQueue<>(
    (a, b) -> lists[a[0]][a[1]] - lists[b[0]][b[1]]
);
// {리스트 인덱스, 요소 인덱스}
```
