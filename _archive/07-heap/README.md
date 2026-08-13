# 07. 힙 / 우선순위 큐 (Heap / Priority Queue)

## 📋 문제 정의

**완전 이진 트리** 기반의 힙과 이를 활용한 **우선순위 큐**를 구현하세요.

부모 노드가 항상 자식 노드보다 크거나(최대 힙) 작은(최소 힙) 속성을 유지하는 자료구조입니다.

---

## 🎯 학습 목표

- 완전 이진 트리의 배열 표현 이해
- 힙 속성(Heap Property) 이해
- Heapify (sift-up, sift-down) 연산
- 우선순위 큐와 힙의 관계
- 힙 정렬(Heap Sort) 알고리즘
- Top-K 문제 해결 패턴

---

## 📝 요구사항

### 기본 연산

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `insert(element)` / `offer(element)` | 요소 삽입 | O(log n) |
| `extractMax()` / `poll()` | 최댓값 제거 및 반환 | O(log n) |
| `peek()` / `getMax()` | 최댓값 조회 (제거 안함) | O(1) |
| `size()` | 요소 개수 | O(1) |
| `isEmpty()` | 비어있는지 확인 | O(1) |
| `clear()` | 모든 요소 제거 | O(1) |

### 추가 연산

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `heapify(array)` | 배열을 힙으로 변환 | O(n) |
| `increaseKey(index, newValue)` | 키 값 증가 | O(log n) |
| `decreaseKey(index, newValue)` | 키 값 감소 | O(log n) |
| `delete(index)` | 특정 위치 요소 삭제 | O(log n) |
| `merge(otherHeap)` | 두 힙 병합 | O(n) |

### 응용

1. **힙 정렬**: O(n log n) 정렬 알고리즘
2. **Top-K 요소**: K개의 최대/최소 요소 찾기
3. **중앙값 스트림**: 두 힙을 사용한 중앙값 유지
4. **K번째 큰 요소**: 실시간 K번째 큰 요소 추적

---

## 📊 입출력 예시

### 예제 1: 최대 힙 기본 사용
```java
MaxHeap heap = new MaxHeap();
heap.insert(5);
heap.insert(3);
heap.insert(8);
heap.insert(1);

System.out.println(heap.peek());       // 출력: 8
System.out.println(heap.extractMax()); // 출력: 8
System.out.println(heap.extractMax()); // 출력: 5
System.out.println(heap.size());       // 출력: 2
```

### 예제 2: 최소 힙 (우선순위 큐)
```java
MinHeap heap = new MinHeap();
heap.insert(5);
heap.insert(3);
heap.insert(8);
heap.insert(1);

System.out.println(heap.extractMin()); // 출력: 1
System.out.println(heap.extractMin()); // 출력: 3
```

### 예제 3: 배열로부터 힙 생성
```java
int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
MaxHeap heap = MaxHeap.heapify(arr);

System.out.println(heap.extractMax()); // 출력: 9
System.out.println(heap.extractMax()); // 출력: 6
```

### 예제 4: 힙 정렬
```java
int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
HeapSort.sort(arr);
// arr = {1, 1, 2, 3, 4, 5, 6, 9}
```

### 예제 5: Top-K 요소
```java
int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
int[] topK = HeapProblems.topK(arr, 3);
// topK = {9, 6, 5} (가장 큰 3개)
```

---

## 🔍 제약 조건

- 배열 기반 구현 (완전 이진 트리 표현)
- 빈 힙에서 `extractMax()` / `peek()` 시 예외 발생
- 인덱스는 0부터 시작
- null 요소 허용하지 않음

---

## 💡 힌트

### 완전 이진 트리의 배열 표현
```
       9(0)              인덱스 0: 루트
      /    \
    6(1)   8(2)          부모 i의 자식: 2i+1, 2i+2
   /  \   /              자식 i의 부모: (i-1)/2
  3(3) 5(4) 7(5)

배열: [9, 6, 8, 3, 5, 7]
```

### 힙 연산
```java
// 부모/자식 인덱스 계산
int parent(int i) { return (i - 1) / 2; }
int leftChild(int i) { return 2 * i + 1; }
int rightChild(int i) { return 2 * i + 2; }

// Sift-up (삽입 시): 부모와 비교하며 위로 이동
// Sift-down (삭제 시): 자식과 비교하며 아래로 이동
```

---

## ✅ 체크리스트

- [ ] 최대 힙 구현
- [ ] 최소 힙 구현
- [ ] 배열로부터 힙 생성 (O(n) heapify)
- [ ] 힙 정렬 구현
- [ ] 우선순위 큐 인터페이스 구현
- [ ] Top-K 문제 해결
- [ ] 중앙값 스트림 문제 해결

---

## 📚 참고

- [Java PriorityQueue 소스코드](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/PriorityQueue.java)
- Binary Heap vs Fibonacci Heap
- 다익스트라 알고리즘에서의 힙 활용
