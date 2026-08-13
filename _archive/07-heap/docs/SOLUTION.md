# 힙 / 우선순위 큐 풀이 해설

## 📌 핵심 아이디어

힙은 **완전 이진 트리**를 **배열**로 표현하며,
부모와 자식 간의 대소 관계(힙 속성)를 유지하는 자료구조입니다.

---

## 🔑 핵심 개념

### 1. 완전 이진 트리의 배열 표현
```
       16(0)
      /     \
    14(1)   10(2)
   /   \    /
  8(3) 7(4) 9(5)

배열: [16, 14, 10, 8, 7, 9]

인덱스 관계 (0-based):
- 부모(i) = (i - 1) / 2
- 왼쪽 자식(i) = 2 * i + 1
- 오른쪽 자식(i) = 2 * i + 2
```

### 2. 힙 속성 (Heap Property)
```
최대 힙: parent >= children
       16
      /  \
    14    10   ✓ 16 >= 14, 16 >= 10

최소 힙: parent <= children
       1
      / \
     3   2    ✓ 1 <= 3, 1 <= 2
```

### 3. Sift-up (상향 이동) - 삽입 시
```
삽입: 17
[16, 14, 10, 8, 7, 9, 17]
                       ↑
17 > 10 → swap
[16, 14, 17, 8, 7, 9, 10]
         ↑
17 > 16 → swap
[17, 14, 16, 8, 7, 9, 10]
 ↑ 완료!
```

### 4. Sift-down (하향 이동) - 삭제 시
```
extractMax 후 마지막 요소를 루트로:
[10, 14, 16, 8, 7, 9]
 ↓
max(14, 16) = 16 > 10 → swap
[16, 14, 10, 8, 7, 9]
         ↓
max(9) < 10 → 완료!
```

---

## 📝 POP 구현 해설
```java
public class MaxHeap {
    private int[] heap;
    private int size;
    private int capacity;
    
    public MaxHeap(int capacity) {
        this.capacity = capacity;
        this.heap = new int[capacity];
        this.size = 0;
    }
    
    private int parent(int i) { return (i - 1) / 2; }
    private int leftChild(int i) { return 2 * i + 1; }
    private int rightChild(int i) { return 2 * i + 2; }
    
    private void swap(int i, int j) {
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
    
    // 삽입: O(log n)
    public void insert(int value) {
        if (size == capacity) {
            grow();
        }
        
        heap[size] = value;
        siftUp(size);
        size++;
    }
    
    private void siftUp(int i) {
        while (i > 0 && heap[parent(i)] < heap[i]) {
            swap(i, parent(i));
            i = parent(i);
        }
    }
    
    // 최댓값 제거: O(log n)
    public int extractMax() {
        if (size == 0) throw new NoSuchElementException();
        
        int max = heap[0];
        heap[0] = heap[size - 1];
        size--;
        siftDown(0);
        
        return max;
    }
    
    private void siftDown(int i) {
        int maxIndex = i;
        
        int left = leftChild(i);
        if (left < size && heap[left] > heap[maxIndex]) {
            maxIndex = left;
        }
        
        int right = rightChild(i);
        if (right < size && heap[right] > heap[maxIndex]) {
            maxIndex = right;
        }
        
        if (i != maxIndex) {
            swap(i, maxIndex);
            siftDown(maxIndex);
        }
    }
    
    // 최댓값 조회: O(1)
    public int peek() {
        if (size == 0) throw new NoSuchElementException();
        return heap[0];
    }
    
    // 배열로부터 힙 생성: O(n)
    public static MaxHeap heapify(int[] arr) {
        MaxHeap heap = new MaxHeap(arr.length);
        heap.heap = Arrays.copyOf(arr, arr.length);
        heap.size = arr.length;
        
        // 마지막 비-리프 노드부터 siftDown
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            heap.siftDown(i);
        }
        
        return heap;
    }
    
    private void grow() {
        capacity = capacity + (capacity >> 1);
        heap = Arrays.copyOf(heap, capacity);
    }
}
```

---

## 📝 힙 정렬 구현
```java
public class HeapSort {
    
    public static void sort(int[] arr) {
        int n = arr.length;
        
        // 1단계: heapify (최대 힙 구성)
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDown(arr, n, i);
        }
        
        // 2단계: 정렬
        for (int i = n - 1; i > 0; i--) {
            // 루트(최댓값)를 맨 뒤로 이동
            swap(arr, 0, i);
            // 힙 크기를 줄이고 힙 속성 복구
            siftDown(arr, i, 0);
        }
    }
    
    private static void siftDown(int[] arr, int heapSize, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        
        if (left < heapSize && arr[left] > arr[largest]) {
            largest = left;
        }
        if (right < heapSize && arr[right] > arr[largest]) {
            largest = right;
        }
        
        if (largest != i) {
            swap(arr, i, largest);
            siftDown(arr, heapSize, largest);
        }
    }
    
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
```

---

## 📝 OOP 구현 해설
```java
public class PriorityQueue<E> implements Queue<E> {
    private Object[] heap;
    private int size;
    private final Comparator<? super E> comparator;
    
    public PriorityQueue() {
        this(11, null);
    }
    
    public PriorityQueue(Comparator<? super E> comparator) {
        this(11, comparator);
    }
    
    public PriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        this.heap = new Object[initialCapacity];
        this.comparator = comparator;
    }
    
    @Override
    public boolean offer(E e) {
        Objects.requireNonNull(e);
        ensureCapacity();
        
        heap[size] = e;
        siftUp(size);
        size++;
        return true;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public E poll() {
        if (size == 0) return null;
        
        E result = (E) heap[0];
        heap[0] = heap[--size];
        heap[size] = null;
        
        if (size > 0) {
            siftDown(0);
        }
        
        return result;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        return size == 0 ? null : (E) heap[0];
    }
    
    @SuppressWarnings("unchecked")
    private int compare(Object a, Object b) {
        if (comparator != null) {
            return comparator.compare((E) a, (E) b);
        }
        return ((Comparable<E>) a).compareTo((E) b);
    }
    
    private void siftUp(int i) {
        Object element = heap[i];
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (compare(element, heap[parent]) >= 0) break;
            heap[i] = heap[parent];
            i = parent;
        }
        heap[i] = element;
    }
    
    private void siftDown(int i) {
        Object element = heap[i];
        int half = size / 2;
        
        while (i < half) {
            int left = 2 * i + 1;
            int right = left + 1;
            int smallest = left;
            
            if (right < size && compare(heap[right], heap[left]) < 0) {
                smallest = right;
            }
            
            if (compare(element, heap[smallest]) <= 0) break;
            
            heap[i] = heap[smallest];
            i = smallest;
        }
        heap[i] = element;
    }
}
```

---

## 🎯 응용 알고리즘

### 1. Top-K 요소 (최소 힙 사용)
```java
public static int[] topK(int[] arr, int k) {
    // 크기 k인 최소 힙 유지
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);
    
    for (int num : arr) {
        if (minHeap.size() < k) {
            minHeap.offer(num);
        } else if (num > minHeap.peek()) {
            minHeap.poll();
            minHeap.offer(num);
        }
    }
    
    int[] result = new int[k];
    for (int i = k - 1; i >= 0; i--) {
        result[i] = minHeap.poll();
    }
    return result;
}
```

### 2. 중앙값 스트림
```java
public class MedianFinder {
    // 작은 절반 (최대 힙)
    private PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    // 큰 절반 (최소 힙)
    private PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    
    public void addNum(int num) {
        maxHeap.offer(num);
        minHeap.offer(maxHeap.poll());
        
        // 크기 균형 유지
        if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }
    
    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        }
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }
}
```

### 3. K번째 큰 요소 (스트림)
```java
public class KthLargest {
    private PriorityQueue<Integer> minHeap;
    private int k;
    
    public KthLargest(int k, int[] nums) {
        this.k = k;
        this.minHeap = new PriorityQueue<>(k);
        for (int num : nums) {
            add(num);
        }
    }
    
    public int add(int val) {
        if (minHeap.size() < k) {
            minHeap.offer(val);
        } else if (val > minHeap.peek()) {
            minHeap.poll();
            minHeap.offer(val);
        }
        return minHeap.peek();
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 설명 |
|------|-----------|------|
| insert/offer | O(log n) | sift-up |
| extractMax/poll | O(log n) | sift-down |
| peek/getMax | O(1) | 루트 반환 |
| heapify (배열→힙) | O(n) | 상향식 구성 |
| 힙 정렬 | O(n log n) | n번 extractMax |
| increaseKey | O(log n) | sift-up |
| decreaseKey | O(log n) | sift-down |

### Heapify가 O(n)인 이유
```
높이 h에 있는 노드 수: n / 2^(h+1)
높이 h에서의 작업량: O(h)

총 작업량 = Σ (n / 2^(h+1)) * O(h)
         = O(n) * Σ (h / 2^h)
         = O(n) * 2  (급수 합)
         = O(n)
```

---

## ❌ 흔한 실수

### 1. 인덱스 계산 오류
```java
// 0-based vs 1-based 혼동
// 0-based:
int parent = (i - 1) / 2;
int left = 2 * i + 1;
int right = 2 * i + 2;

// 1-based (교과서 스타일):
int parent = i / 2;
int left = 2 * i;
int right = 2 * i + 1;
```

### 2. siftDown에서 범위 체크 누락
```java
// 잘못됨
int left = leftChild(i);
if (heap[left] > heap[maxIndex]) { ... }  // 범위 체크 없음!

// 올바름
int left = leftChild(i);
if (left < size && heap[left] > heap[maxIndex]) { ... }
```

### 3. 최소 힙 vs 최대 힙 비교 방향
```java
// 최대 힙: 부모 > 자식
if (heap[parent(i)] < heap[i]) swap(...);  // siftUp

// 최소 힙: 부모 < 자식
if (heap[parent(i)] > heap[i]) swap(...);  // siftUp
```

---

## 🔗 관련 문제

- LeetCode 215: Kth Largest Element in an Array
- LeetCode 347: Top K Frequent Elements
- LeetCode 295: Find Median from Data Stream
- LeetCode 703: Kth Largest Element in a Stream
- LeetCode 23: Merge K Sorted Lists
- LeetCode 973: K Closest Points to Origin
