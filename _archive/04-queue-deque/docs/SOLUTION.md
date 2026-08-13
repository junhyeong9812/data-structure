# 큐와 덱 풀이 해설

## 📌 핵심 아이디어

큐는 **FIFO(First In First Out)** 원칙을 따르는 선형 자료구조입니다.
덱은 양쪽 끝에서 삽입과 삭제가 모두 가능한 확장된 큐입니다.

---

## 🔑 핵심 개념

### 1. 큐 vs 스택 vs 덱
```
스택 (LIFO):     큐 (FIFO):        덱 (양방향):
  push↓ pop↑    enqueue→ dequeue→   ←→ addFirst/removeFirst
    ┌───┐         ┌───┬───┬───┐      addLast/removeLast ←→
    │ 3 │         │ 1 │ 2 │ 3 │     ┌───┬───┬───┐
    │ 2 │         └───┴───┴───┘     │ 1 │ 2 │ 3 │
    │ 1 │         front↑    rear↑   └───┴───┴───┘
    └───┘                           front↑      rear↑
```

### 2. 원형 배열 (Circular Array)

일반 배열의 문제점:
```
dequeue 시 모든 요소 이동 필요 → O(n)
[_, _, 3, 4, 5]  →  [3, 4, 5, _, _]
```

원형 배열 해결책:
```
front와 rear 포인터만 이동 → O(1)

      0   1   2   3   4
    ┌───┬───┬───┬───┬───┐
    │ 6 │   │   │ 4 │ 5 │
    └───┴───┴───┴───┴───┘
      ↑rear     ↑front

다음 인덱스 = (현재 + 1) % 용량
```

### 3. 가득 참 vs 비어있음 구분
```java
// 방법 1: 한 칸 비워두기
isEmpty: front == rear
isFull:  (rear + 1) % capacity == front

// 방법 2: size 변수 별도 관리
isEmpty: size == 0
isFull:  size == capacity
```

---

## 📝 POP 구현 해설

### 원형 큐 (Circular Queue)
```java
public class CircularQueue {
    private int[] data;
    private int front;  // 첫 요소 위치
    private int rear;   // 다음 삽입 위치
    private int size;
    private int capacity;
    
    public CircularQueue(int capacity) {
        this.capacity = capacity;
        this.data = new int[capacity];
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }
    
    public boolean enqueue(int element) {
        if (isFull()) return false;
        data[rear] = element;
        rear = (rear + 1) % capacity;
        size++;
        return true;
    }
    
    public int dequeue() {
        if (isEmpty()) throw new NoSuchElementException();
        int element = data[front];
        front = (front + 1) % capacity;
        size--;
        return element;
    }
    
    public int peek() {
        if (isEmpty()) throw new NoSuchElementException();
        return data[front];
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public boolean isFull() {
        return size == capacity;
    }
}
```

### 배열 기반 덱 (ArrayDeque)
```java
public class ArrayDeque {
    private int[] data;
    private int front;  // 첫 요소 위치
    private int rear;   // 마지막 요소 다음 위치
    private int size;
    
    public ArrayDeque(int capacity) {
        this.data = new int[capacity];
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }
    
    public void addFirst(int element) {
        ensureCapacity();
        front = (front - 1 + data.length) % data.length;
        data[front] = element;
        size++;
    }
    
    public void addLast(int element) {
        ensureCapacity();
        data[rear] = element;
        rear = (rear + 1) % data.length;
        size++;
    }
    
    public int removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();
        int element = data[front];
        front = (front + 1) % data.length;
        size--;
        return element;
    }
    
    public int removeLast() {
        if (isEmpty()) throw new NoSuchElementException();
        rear = (rear - 1 + data.length) % data.length;
        size--;
        return data[rear];
    }
    
    private void ensureCapacity() {
        if (size == data.length) {
            int[] newData = new int[data.length * 2];
            for (int i = 0; i < size; i++) {
                newData[i] = data[(front + i) % data.length];
            }
            data = newData;
            front = 0;
            rear = size;
        }
    }
}
```

---

## 📝 OOP 구현 해설
```java
public class LinkedDeque<E> implements Deque<E> {
    private Node<E> front;
    private Node<E> rear;
    private int size;
    
    private static class Node<E> {
        E data;
        Node<E> prev;
        Node<E> next;
        
        Node(E data) {
            this.data = data;
        }
    }
    
    @Override
    public void addFirst(E element) {
        Node<E> newNode = new Node<>(element);
        if (isEmpty()) {
            front = rear = newNode;
        } else {
            newNode.next = front;
            front.prev = newNode;
            front = newNode;
        }
        size++;
    }
    
    @Override
    public void addLast(E element) {
        Node<E> newNode = new Node<>(element);
        if (isEmpty()) {
            front = rear = newNode;
        } else {
            newNode.prev = rear;
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }
    
    @Override
    public E removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();
        E data = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        } else {
            front.prev = null;
        }
        size--;
        return data;
    }
    
    @Override
    public E removeLast() {
        if (isEmpty()) throw new NoSuchElementException();
        E data = rear.data;
        rear = rear.prev;
        if (rear == null) {
            front = null;
        } else {
            rear.next = null;
        }
        size--;
        return data;
    }
}
```

---

## 🎯 응용 알고리즘

### 1. 슬라이딩 윈도우 최댓값 (Monotonic Deque)
```java
public int[] maxSlidingWindow(int[] nums, int k) {
    if (nums.length == 0) return new int[0];
    
    int[] result = new int[nums.length - k + 1];
    Deque<Integer> deque = new ArrayDeque<>();  // 인덱스 저장
    
    for (int i = 0; i < nums.length; i++) {
        // 윈도우 범위를 벗어난 요소 제거
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.removeFirst();
        }
        
        // 현재 요소보다 작은 요소들 제거 (단조 감소 유지)
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.removeLast();
        }
        
        deque.addLast(i);
        
        // 결과 저장 (윈도우가 완성된 후)
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    
    return result;
}
```

### 2. 최근 요청 카운터 (Recent Counter)
```java
public class RecentCounter {
    private Queue<Integer> requests;
    
    public RecentCounter() {
        requests = new LinkedList<>();
    }
    
    // 최근 3000ms 이내의 요청 수 반환
    public int ping(int t) {
        requests.offer(t);
        
        // 3000ms 이전 요청 제거
        while (requests.peek() < t - 3000) {
            requests.poll();
        }
        
        return requests.size();
    }
}
```

### 3. 원형 덱 (Circular Deque)
```java
public class MyCircularDeque {
    private int[] data;
    private int front, rear, size, capacity;
    
    public MyCircularDeque(int k) {
        capacity = k;
        data = new int[k];
        front = 0;
        rear = 0;
        size = 0;
    }
    
    public boolean insertFront(int value) {
        if (isFull()) return false;
        front = (front - 1 + capacity) % capacity;
        data[front] = value;
        size++;
        return true;
    }
    
    public boolean insertLast(int value) {
        if (isFull()) return false;
        data[rear] = value;
        rear = (rear + 1) % capacity;
        size++;
        return true;
    }
    
    public boolean deleteFront() {
        if (isEmpty()) return false;
        front = (front + 1) % capacity;
        size--;
        return true;
    }
    
    public boolean deleteLast() {
        if (isEmpty()) return false;
        rear = (rear - 1 + capacity) % capacity;
        size--;
        return true;
    }
    
    public int getFront() {
        return isEmpty() ? -1 : data[front];
    }
    
    public int getRear() {
        return isEmpty() ? -1 : data[(rear - 1 + capacity) % capacity];
    }
    
    public boolean isEmpty() { return size == 0; }
    public boolean isFull() { return size == capacity; }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 배열 큐 | 원형 큐 | 연결 리스트 큐 |
|------|--------|--------|--------------|
| enqueue | O(1)* | O(1) | O(1) |
| dequeue | O(n) | O(1) | O(1) |
| peek | O(1) | O(1) | O(1) |
| isEmpty | O(1) | O(1) | O(1) |

*배열 기반 일반 큐는 dequeue 시 요소 이동 필요

| 연산 | 배열 덱 | 연결 리스트 덱 |
|------|--------|--------------|
| addFirst | O(1)* | O(1) |
| addLast | O(1)* | O(1) |
| removeFirst | O(1) | O(1) |
| removeLast | O(1) | O(1) |

*원형 배열 사용 시, 확장 시 O(n)

---

## ❌ 흔한 실수

### 1. 원형 인덱스 계산 오류
```java
// 잘못됨 - 음수 인덱스
front = (front - 1) % capacity;  // -1 % 5 = -1 (Java)

// 올바름
front = (front - 1 + capacity) % capacity;
```

### 2. 가득 참/비어있음 혼동
```java
// 한 칸 비워두는 방식에서
// 비어있음: front == rear
// 가득 참: (rear + 1) % capacity == front

// 둘 다 front == rear면 구분 불가!
// → size 변수 따로 관리 권장
```

### 3. null 반환 vs 예외
```java
// Java 컬렉션 프레임워크 규칙
// remove/element: 비어있으면 예외
// poll/peek: 비어있으면 null

public E poll() {
    return isEmpty() ? null : removeFirst();
}

public E remove() {
    if (isEmpty()) throw new NoSuchElementException();
    return removeFirst();
}
```

---

## 🔗 관련 문제

- LeetCode 622: Design Circular Queue
- LeetCode 641: Design Circular Deque
- LeetCode 239: Sliding Window Maximum
- LeetCode 933: Number of Recent Calls
- LeetCode 346: Moving Average from Data Stream
