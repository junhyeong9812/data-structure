# 02. 연결 리스트 (Linked List)

## 📋 문제 정의

노드와 포인터를 사용하여 **단일 연결 리스트**와 **이중 연결 리스트**를 구현하세요.

배열과 달리 메모리상에 연속되지 않은 데이터를 포인터로 연결하는 자료구조입니다.

---

## 🎯 학습 목표

- 노드(Node) 개념 이해
- 포인터/참조 조작
- 단일 vs 이중 연결 리스트 차이점
- Head/Tail 포인터 관리
- 순회 알고리즘

---

## 📝 요구사항

### 단일 연결 리스트 (Singly Linked List)

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `addFirst(element)` | 맨 앞에 요소 추가 | O(1) |
| `addLast(element)` | 맨 뒤에 요소 추가 | O(1)* |
| `add(index, element)` | 특정 위치에 요소 삽입 | O(n) |
| `removeFirst()` | 맨 앞 요소 삭제 | O(1) |
| `removeLast()` | 맨 뒤 요소 삭제 | O(n) |
| `remove(index)` | 특정 위치 요소 삭제 | O(n) |
| `get(index)` | 특정 위치 요소 반환 | O(n) |
| `set(index, element)` | 특정 위치 요소 수정 | O(n) |
| `size()` | 요소 개수 반환 | O(1) |
| `isEmpty()` | 비어있는지 확인 | O(1) |
| `contains(element)` | 요소 존재 여부 확인 | O(n) |
| `indexOf(element)` | 요소의 인덱스 반환 | O(n) |
| `clear()` | 모든 요소 삭제 | O(1) |
| `reverse()` | 리스트 뒤집기 | O(n) |

*Tail 포인터 유지 시

### 이중 연결 리스트 (Doubly Linked List)

위 연산에 추가로:
- 각 노드가 이전(prev), 다음(next) 노드를 모두 참조
- `removeLast()` O(1) 가능
- 양방향 순회 가능

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
LinkedList<Integer> list = new LinkedList<>();
list.addLast(1);
list.addLast(2);
list.addLast(3);
System.out.println(list.get(1));  // 출력: 2
// 리스트: 1 -> 2 -> 3
```

### 예제 2: 삽입과 삭제
```java
LinkedList<String> list = new LinkedList<>();
list.addLast("A");
list.addLast("B");
list.addLast("C");
list.addFirst("Z");   // Z -> A -> B -> C
list.add(2, "X");     // Z -> A -> X -> B -> C
list.remove(1);       // Z -> X -> B -> C
System.out.println(list.get(0)); // 출력: Z
```

### 예제 3: 리스트 뒤집기
```java
LinkedList<Integer> list = new LinkedList<>();
list.addLast(1);
list.addLast(2);
list.addLast(3);
list.reverse();
// 리스트: 3 -> 2 -> 1
System.out.println(list.get(0)); // 출력: 3
```

---

## 🔍 제약 조건

- 인덱스는 0부터 시작
- 유효하지 않은 인덱스 접근 시 `IndexOutOfBoundsException` 발생
- 빈 리스트에서 삭제 시 `NoSuchElementException` 발생
- `null` 요소 저장 가능

---

## 💡 힌트

### POP 구현 힌트
```java
// 단일 연결 리스트 노드
class Node {
    int data;
    Node next;
}

// 이중 연결 리스트 노드
class Node {
    int data;
    Node prev;
    Node next;
}
```

### OOP 구현 힌트
- 내부 클래스로 Node 정의
- `Iterable<E>` 인터페이스 구현
- Sentinel(더미) 노드 사용으로 엣지 케이스 단순화

---

## ✅ 체크리스트

- [ ] 단일 연결 리스트 기본 연산
- [ ] 이중 연결 리스트 기본 연산
- [ ] Head/Tail 포인터 관리
- [ ] 경계 조건 처리 (빈 리스트, 단일 요소)
- [ ] reverse() 구현
- [ ] Iterator 구현 (OOP)

---

## 📚 참고

- [Java LinkedList 소스코드](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/LinkedList.java)
- Sentinel Node 패턴
