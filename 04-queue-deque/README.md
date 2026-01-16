# 04. 큐와 덱 (Queue & Deque)

## 📋 문제 정의

**FIFO(First In First Out)** 원칙을 따르는 큐와 양쪽 끝에서 삽입/삭제가 가능한 **덱(Double-Ended Queue)**을 구현하세요.

---

## 🎯 학습 목표

- FIFO 원칙 이해
- 원형 배열(Circular Array) 구현
- 큐와 덱의 차이점
- BFS 알고리즘에서의 큐 활용
- 슬라이딩 윈도우 문제에서 덱 활용

---

## 📝 요구사항

### 큐 (Queue) 기본 연산

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `enqueue(element)` / `offer(element)` | 큐 뒤에 요소 추가 | O(1) |
| `dequeue()` / `poll()` | 큐 앞 요소 제거 및 반환 | O(1) |
| `peek()` / `front()` | 큐 앞 요소 조회 (제거 안함) | O(1) |
| `isEmpty()` | 큐가 비어있는지 확인 | O(1) |
| `size()` | 큐의 요소 개수 반환 | O(1) |
| `clear()` | 모든 요소 제거 | O(1) |

### 덱 (Deque) 기본 연산

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `addFirst(element)` | 앞에 요소 추가 | O(1) |
| `addLast(element)` | 뒤에 요소 추가 | O(1) |
| `removeFirst()` | 앞 요소 제거 및 반환 | O(1) |
| `removeLast()` | 뒤 요소 제거 및 반환 | O(1) |
| `peekFirst()` | 앞 요소 조회 | O(1) |
| `peekLast()` | 뒤 요소 조회 | O(1) |

### 응용 문제

1. **원형 큐**: 고정 크기 원형 배열 기반 큐
2. **슬라이딩 윈도우 최댓값**: 덱을 활용한 O(n) 풀이
3. **최근 요청 카운터**: 시간 기반 큐 활용

---

## 📊 입출력 예시

### 예제 1: 기본 큐 사용
```java
Queue<Integer> queue = new Queue<>();
queue.enqueue(1);
queue.enqueue(2);
queue.enqueue(3);
System.out.println(queue.dequeue());  // 출력: 1 (FIFO)
System.out.println(queue.peek());     // 출력: 2
System.out.println(queue.size());     // 출력: 2
```

### 예제 2: 덱 사용
```java
Deque<Integer> deque = new Deque<>();
deque.addFirst(1);   // [1]
deque.addLast(2);    // [1, 2]
deque.addFirst(0);   // [0, 1, 2]
System.out.println(deque.removeFirst());  // 출력: 0
System.out.println(deque.removeLast());   // 출력: 2
```

### 예제 3: 원형 큐
```java
CircularQueue queue = new CircularQueue(3);
queue.enqueue(1);  // [1, _, _]
queue.enqueue(2);  // [1, 2, _]
queue.enqueue(3);  // [1, 2, 3] - Full
queue.enqueue(4);  // false (가득 참)
queue.dequeue();   // 1, [_, 2, 3]
queue.enqueue(4);  // [4, 2, 3] - 원형으로 재사용
```

### 예제 4: 슬라이딩 윈도우 최댓값
```java
int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
int k = 3;
int[] result = maxSlidingWindow(nums, k);
// 윈도우: [1,3,-1], [3,-1,-3], [-1,-3,5], [-3,5,3], [5,3,6], [3,6,7]
// 최댓값:    3,        3,        5,         5,        6,       7
// result = [3, 3, 5, 5, 6, 7]
```

---

## 🔍 제약 조건

- 빈 큐/덱에서 `dequeue()`, `removeFirst()`, `removeLast()` 시 예외 발생
- `poll()`, `peekFirst()`, `peekLast()`는 빈 경우 `null` 반환
- 원형 큐는 고정 크기, 가득 차면 삽입 실패
- `null` 요소 저장 가능 (일반 큐/덱)

---

## 💡 힌트

### 원형 배열 구현 힌트
```java
// 인덱스 순환
int next = (current + 1) % capacity;
int prev = (current - 1 + capacity) % capacity;

// 가득 찼는지 확인
boolean isFull = (rear + 1) % capacity == front;

// 비어있는지 확인  
boolean isEmpty = front == rear;
```

### 연결 리스트 기반 덱 힌트
```java
// 이중 연결 리스트 사용
class Node<E> {
    E data;
    Node<E> prev, next;
}
// 양쪽 끝에서 O(1) 삽입/삭제
```

---

## ✅ 체크리스트

- [ ] 배열 기반 큐 구현
- [ ] 연결 리스트 기반 큐 구현
- [ ] 원형 큐 구현
- [ ] 배열 기반 덱 구현
- [ ] 연결 리스트 기반 덱 구현
- [ ] 슬라이딩 윈도우 최댓값 알고리즘
- [ ] 최근 요청 카운터 구현

---

## 📚 참고

- [Java ArrayDeque 소스코드](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/ArrayDeque.java)
- BFS 알고리즘과 큐
- 프로듀서-컨슈머 패턴
