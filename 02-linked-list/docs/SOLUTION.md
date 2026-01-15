# 연결 리스트 풀이 해설

## 📌 핵심 아이디어

연결 리스트는 **노드들이 포인터로 연결**된 선형 자료구조입니다.
배열과 달리 메모리상에 연속적으로 저장되지 않아 삽입/삭제가 효율적입니다.

---

## 🔑 핵심 개념

### 1. 노드 구조
```
단일 연결 리스트:
+------+------+    +------+------+    +------+------+
| data | next | -> | data | next | -> | data | null |
+------+------+    +------+------+    +------+------+

이중 연결 리스트:
+------+------+------+    +------+------+------+    +------+------+------+
| null | data | next | <-> | prev | data | next | <-> | prev | data | null |
+------+------+------+    +------+------+------+    +------+------+------+
```

### 2. Head와 Tail 포인터
```java
// Tail 없이
addLast: O(n) - 매번 끝까지 순회

// Tail 포인터 유지
addLast: O(1) - 직접 접근
```

### 3. Sentinel(더미) 노드 패턴
```java
// Sentinel 없이 - 복잡한 null 체크
public void addFirst(E element) {
    Node newNode = new Node(element);
    if (head == null) {
        head = tail = newNode;
    } else {
        newNode.next = head;
        head = newNode;
    }
}

// Sentinel 사용 - 단순화
// head와 tail은 항상 더미 노드
public void addFirst(E element) {
    Node newNode = new Node(element);
    newNode.next = head.next;
    newNode.prev = head;
    head.next.prev = newNode;
    head.next = newNode;
}
```

---

## 📝 POP 구현 해설 (단일 연결 리스트)
```java
public class SinglyLinkedList {
    private Node head;
    private Node tail;
    private int size;
    
    static class Node {
        int data;
        Node next;
        
        Node(int data) {
            this.data = data;
        }
    }
    
    // 맨 앞에 추가: O(1)
    public void addFirst(int element) {
        Node newNode = new Node(element);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }
    
    // 맨 뒤에 추가: O(1) - tail 포인터 사용
    public void addLast(int element) {
        Node newNode = new Node(element);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }
    
    // 특정 위치에 삽입: O(n)
    public void add(int index, int element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            addFirst(element);
            return;
        }
        if (index == size) {
            addLast(element);
            return;
        }
        
        Node prev = getNode(index - 1);
        Node newNode = new Node(element);
        newNode.next = prev.next;
        prev.next = newNode;
        size++;
    }
    
    // 인덱스로 노드 찾기: O(n)
    private Node getNode(int index) {
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }
    
    // 리스트 뒤집기: O(n)
    public void reverse() {
        Node prev = null;
        Node current = head;
        tail = head;
        
        while (current != null) {
            Node next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        head = prev;
    }
}
```

---

## 📝 OOP 구현 해설 (이중 연결 리스트 + Sentinel)
```java
public class DoublyLinkedList<E> implements List<E>, Iterable<E> {
    private final Node<E> head;  // Sentinel head
    private final Node<E> tail;  // Sentinel tail
    private int size;
    
    private static class Node<E> {
        E data;
        Node<E> prev;
        Node<E> next;
        
        Node(E data, Node<E> prev, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }
    
    public DoublyLinkedList() {
        head = new Node<>(null, null, null);
        tail = new Node<>(null, head, null);
        head.next = tail;
    }
    
    // Sentinel 덕분에 null 체크 불필요
    public void addFirst(E element) {
        addBetween(element, head, head.next);
    }
    
    public void addLast(E element) {
        addBetween(element, tail.prev, tail);
    }
    
    private void addBetween(E element, Node<E> predecessor, Node<E> successor) {
        Node<E> newNode = new Node<>(element, predecessor, successor);
        predecessor.next = newNode;
        successor.prev = newNode;
        size++;
    }
    
    public E removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();
        return remove(head.next);
    }
    
    public E removeLast() {
        if (isEmpty()) throw new NoSuchElementException();
        return remove(tail.prev);
    }
    
    private E remove(Node<E> node) {
        E data = node.data;
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
        return data;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new LinkedListIterator();
    }
    
    private class LinkedListIterator implements Iterator<E> {
        private Node<E> current = head.next;
        
        @Override
        public boolean hasNext() {
            return current != tail;
        }
        
        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            E data = current.data;
            current = current.next;
            return data;
        }
    }
}
```

---

## ⏱️ 복잡도 분석

### 단일 연결 리스트

| 연산 | 시간복잡도 | 설명 |
|------|-----------|------|
| addFirst | O(1) | Head 포인터만 수정 |
| addLast | O(1)* | Tail 포인터 사용 시 |
| add(index) | O(n) | index까지 순회 필요 |
| removeFirst | O(1) | Head 포인터만 수정 |
| removeLast | O(n) | 이전 노드 찾기 위해 순회 |
| get(index) | O(n) | index까지 순회 |
| contains | O(n) | 전체 순회 |

*Tail 포인터 유지 시

### 이중 연결 리스트

| 연산 | 시간복잡도 | 설명 |
|------|-----------|------|
| addFirst | O(1) | - |
| addLast | O(1) | - |
| removeFirst | O(1) | - |
| removeLast | O(1) | prev 포인터로 직접 접근 |
| get(index) | O(n) | 양방향 탐색 가능 (n/2) |

---

## 🎯 최적화 포인트

### 1. 양방향 탐색 (이중 연결 리스트)
```java
private Node<E> getNode(int index) {
    Node<E> node;
    if (index < size / 2) {
        // 앞에서부터 탐색
        node = head.next;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
    } else {
        // 뒤에서부터 탐색
        node = tail.prev;
        for (int i = size - 1; i > index; i--) {
            node = node.prev;
        }
    }
    return node;
}
```

### 2. 순환 연결 리스트
```java
// 마지막 노드가 첫 노드를 가리킴
tail.next = head;
head.prev = tail;  // 이중 연결일 때
```

---

## ❌ 흔한 실수

### 1. 포인터 순서 실수
```java
// 잘못됨 - 원래 노드 참조 손실
head = newNode;
newNode.next = head;  // newNode가 자기 자신을 가리킴!

// 올바름
newNode.next = head;
head = newNode;
```

### 2. Tail 포인터 갱신 누락
```java
// 잘못됨 - tail 갱신 안 함
public void addLast(int element) {
    tail.next = new Node(element);
    // tail = tail.next; 누락!
}
```

### 3. 빈 리스트 처리 누락
```java
// 반드시 체크
if (head == null) {
    head = tail = newNode;
} else {
    // ...
}
```

### 4. size 갱신 누락
```java
// 모든 삽입/삭제에서 size 갱신 필수
size++;  // 삽입 시
size--;  // 삭제 시
```

---

## 🔗 관련 문제

- LeetCode 206: Reverse Linked List
- LeetCode 21: Merge Two Sorted Lists
- LeetCode 141: Linked List Cycle
- LeetCode 19: Remove Nth Node From End
- LeetCode 2: Add Two Numbers
