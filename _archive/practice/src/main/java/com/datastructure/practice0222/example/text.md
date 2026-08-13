**# 자료구조 학습 요약

## 1. 이중 연결 리스트 (Doubly Linked List)

### reverse() 메서드의 핵심
- for문은 각 노드의 **prev/next 포인터(화살표 방향)**만 뒤집는다.
- 노드 객체 자체는 메모리에 그대로 있고, 연결 방향만 바뀐다.
- `head`와 `tail`은 리스트의 멤버 변수이므로 **별도로 갱신**해줘야 한다.
- for문이 포인터를 바꾸고, `head = tail` / `tail = originalHead`로 입구 표지판을 바꿔주는 구조.

---

## 2. 스택 (Stack) - LinkedStackImpl

### search() 메서드
- `count`는 top부터 순차적으로 0, 1, 2... 카운팅한다.
- `count + 1` → top 기준 거리 (Java 표준 Stack.search()와 동일)
- `size - count` → bottom 기준 거리
- 이 둘은 **서로 다른 값**이다. (방향이 반대)

### toArray() 주의사항
- `new Objects[size]` ❌ → `new Object[size]` ✅
- `Objects`는 유틸리티 클래스라 배열 생성 불가.

---

## 3. 트리 (Tree) 기본 개념

### `E extends Comparable<E>`인 이유
- 트리(특히 BST)는 값의 크고 작음을 비교해서 왼쪽/오른쪽에 배치해야 하므로 `compareTo()` 메서드가 필요하다.

### compareTo() 규약
- 음수: element가 node.value보다 작음
- 0: 같음
- 양수: element가 node.value보다 큼
- Integer는 뺄셈 결과를 반환하기도 하지만, String 등은 다른 방식으로 비교. **부호(음/0/양)만 의미 있음.**

### BST의 중복 처리
- BST는 기본적으로 중복을 허용하지 않는다.
- 중복을 처리하려면: 노드에 `int count` 필드를 두거나, 별도 배열/자료구조를 추가.

### protected static class Node 사용 이유
- `private` → 하위 클래스(BST, AVL)에서 접근 불가
- `public` → 외부에 내부 구조 노출 (불필요)
- `protected` → 하위 클래스에서만 접근 가능 (적절)

---

## 4. 재귀 탐색 (findNode)
- 루트부터 compareTo로 비교하며 왼쪽/오른쪽으로 내려감.
- 값을 찾으면 `return node`, 못 찾고 null까지 내려가면 `return null`.
- 중간 노드들은 아래에서 받은 결과를 **그대로 위로 전달(택배 전달)**하는 구조.
- 콜스택을 타고 최종 결과가 최초 호출자에게 반환된다.

---

## 5. findMin / findMax
- `findMin`: 가장 왼쪽 노드 = 트리 전체의 **최솟값**
- `findMax`: 가장 오른쪽 노드 = 트리 전체의 **최댓값**
- BST 규칙이 "왼쪽 < 부모 < 오른쪽"이기 때문.
- 가장 왼쪽/오른쪽 노드가 반드시 리프 노드는 아닐 수 있다.

---

## 6. 트리 종류 비교

| 구분 | BST | AVL | 힙 (Heap) |
|------|-----|-----|-----------|
| 규칙 | 왼쪽 < 부모 < 오른쪽 | BST와 동일 + 균형 유지 | 부모 < 자식 (최소힙) 또는 부모 > 자식 (최대힙) |
| 루트 | 처음 삽입된 값 (중간값 아님) | 회전으로 중간값에 가까워짐 | 최솟값 또는 최댓값 |
| 특징 | 편향 시 O(n) | 회전으로 항상 O(log n) 보장 | 최솟값/최댓값 빠르게 꺼냄 |
| 용도 | 탐색/정렬 | 탐색/정렬 (균형 보장) | 우선순위 큐 (PriorityQueue) |

---

## 7. 중위 순회 (inOrder Traversal)

### 동작 원리
- "왼쪽 → 자기 자신 → 오른쪽" 순서로 재귀 탐색.
- 왼쪽 끝(null)에 도달하면 return되고, 바로 자기 값을 result에 추가한 뒤 오른쪽으로 이동.
- 자식이 없으면(null) 그냥 return되고 바로 자기 값을 추가하는 구조.

### 시각화 예시
```
        5
       / \
      3    7
     / \
    1   4
```

호출 흐름:
```
inOrder(5)
  inOrder(3)
    inOrder(1)
      inOrder(null) → return     ← 왼쪽 끝
      result.add(1) ✅
      inOrder(null) → return     ← 오른쪽도 없음
    result.add(3) ✅
    inOrder(4)
      inOrder(null) → return
      result.add(4) ✅
      inOrder(null) → return
  result.add(5) ✅
  inOrder(7)
    inOrder(null) → return
    result.add(7) ✅
    inOrder(null) → return
```

결과: `[1, 3, 4, 5, 7]` → BST에서 inOrder 순회는 **오름차순 정렬** 결과를 만든다.

---

## 8. 전위 순회 (preOrder Traversal)

### 동작 원리
- "자기 자신 → 왼쪽 → 오른쪽" 순서로 재귀 탐색.
- 루트부터 먼저 추가하고 왼쪽으로 내려간 뒤 오른쪽으로 이동.
- 오름차순 정렬이 아니며, **트리 구조 자체를 복사하거나 저장**할 때 주로 사용.
- 이 순서대로 BST에 다시 삽입하면 원래 트리 구조가 그대로 복원된다.

### 시각화 예시
```
        5
       / \
      3    7
     / \
    1   4
```

호출 흐름:
```
preOrder(5)
  result.add(5) ✅
  preOrder(3)
    result.add(3) ✅
    preOrder(1)
      result.add(1) ✅
      preOrder(null) → return
      preOrder(null) → return
    preOrder(4)
      result.add(4) ✅
      preOrder(null) → return
      preOrder(null) → return
  preOrder(7)
    result.add(7) ✅
    preOrder(null) → return
    preOrder(null) → return
```

결과: `[5, 3, 1, 4, 7]` → 정렬이 아닌 **트리 구조 순서**.

---

## 9. 후위 순회 (postOrder Traversal)

### 동작 원리
- "왼쪽 → 오른쪽 → 자기 자신" 순서로 재귀 탐색.
- 자식을 먼저 처리하고 부모를 나중에 처리하는 구조.
- 내림차순 정렬이 아니며, **트리를 삭제**할 때 주로 사용. (자식 노드를 먼저 해제하고 마지막에 부모를 해제해야 안전하기 때문)
- 참고: 내림차순을 얻으려면 inOrder의 역순(오른쪽 → 자기 자신 → 왼쪽)을 하면 된다.

### 시각화 예시
```
        5
       / \
      3    7
     / \
    1   4
```

호출 흐름:
```
postOrder(5)
  postOrder(3)
    postOrder(1)
      postOrder(null) → return
      postOrder(null) → return
      result.add(1) ✅
    postOrder(4)
      postOrder(null) → return
      postOrder(null) → return
      result.add(4) ✅
    result.add(3) ✅
  postOrder(7)
    postOrder(null) → return
    postOrder(null) → return
    result.add(7) ✅
  result.add(5) ✅
```

결과: `[1, 4, 3, 7, 5]` → 정렬이 아닌 **자식 먼저, 부모 나중** 순서.

---

## 10. 레벨 순회 (levelOrder Traversal / BFS)

### 동작 원리
- 트리를 **층별로 왼쪽에서 오른쪽으로** 읽는 방식 (너비 우선 탐색, BFS).
- 큐(Queue)의 FIFO 특성을 이용해 같은 레벨의 노드를 먼저 처리하고, 그 다음 레벨로 넘어간다.
- 앞선 순회들(inOrder, preOrder, postOrder)은 재귀(스택)를 사용하는 **DFS**, 이건 큐를 사용하는 **BFS**.

### 시각화 예시
```
        5          ← level 0
       / \
      3    7       ← level 1
     / \
    1   4          ← level 2
```

큐의 흐름:
```
큐: [5]        → poll 5, add(5)  → 자식 3,7 넣음
큐: [3, 7]     → poll 3, add(3)  → 자식 1,4 넣음
큐: [7, 1, 4]  → poll 7, add(7)  → 자식 없음
큐: [1, 4]     → poll 1, add(1)  → 자식 없음
큐: [4]        → poll 4, add(4)  → 자식 없음
```

결과: `[5, 3, 7, 1, 4]` → 위에서 아래로, 왼쪽에서 오른쪽으로.

### 순회 방식 비교 (DFS vs BFS)
| 구분 | inOrder / preOrder / postOrder | levelOrder |
|------|-------------------------------|------------|
| 탐색 방식 | DFS (깊이 우선) | BFS (너비 우선) |
| 사용 자료구조 | 재귀 (콜스택) | 큐 (Queue) |
| 탐색 방향 | 위→아래로 깊이 파고듦 | 층별로 옆으로 훑음 |

---

## 11. BST 삽입(insert)과 삭제(delete) 시각화

### 삽입 (insert) - 5, 3, 7, 1, 4 순서로 삽입

```
insert(5): 루트가 null → 새 노드 생성
        5

insert(3): 3 < 5 → 왼쪽으로
        5
       /
      3

insert(7): 7 > 5 → 오른쪽으로
        5
       / \
      3   7

insert(1): 1 < 5 → 왼쪽, 1 < 3 → 왼쪽
        5
       / \
      3   7
     /
    1

insert(4): 4 < 5 → 왼쪽, 4 > 3 → 오른쪽
        5
       / \
      3   7
     / \
    1   4
```

재귀 흐름 (insert(root, 4) 예시):
```
insert(5, 4)
  4 < 5 → node.left = insert(3, 4)
    4 > 3 → node.right = insert(null, 4)
      null이므로 new Node(4) 반환 ← 여기서 노드 생성
    node.right = Node(4) ← 3의 오른쪽에 연결
    return node(3)
  node.left = node(3) ← 기존 연결 유지
  return node(5)
```
> 핵심: 재귀가 올라오면서 `node.left = ...` / `node.right = ...`로 부모-자식 연결을 갱신한다.
> 새 노드가 생기는 곳만 실제로 변경되고, 나머지는 기존 참조를 그대로 다시 넣는 것.

---

### 삭제 (delete) - 3가지 경우

#### Case 1: 자식이 0개 (리프 노드 삭제) - delete(1)
```
삭제 전:              삭제 후:
        5                     5
       / \                   / \
      3   7                 3   7
     / \                     \
    1   4                     4

1을 찾아감: 1 < 5 → 왼쪽, 1 < 3 → 왼쪽
node.left == null, node.right == null
→ return null (부모의 left가 null이 됨)
```

#### Case 2: 자식이 1개 - delete(7) (7에 왼쪽 자식 6만 있다고 가정)
```
삭제 전:              삭제 후:
        5                     5
       / \                   / \
      3   7                 3   6
     / \  /                / \
    1  4  6               1   4

7을 찾아감: 7 > 5 → 오른쪽
node.left = 6, node.right == null
→ return node.left (6이 7의 자리를 대체)
```

#### Case 3: 자식이 2개 - delete(3)
```
삭제 전:              후속자 찾기:           대체 후:
        5             오른쪽 서브트리에서       5
       / \            최솟값 = 4              / \
      3   7                                  4   7
     / \                                    /
    1   4                                  1

과정:
1. 삭제 대상 노드(3) 발견
2. 오른쪽 서브트리에서 최솟값(후속자) 찾기 → findMin(node.right) → 4
3. 노드 값을 후속자 값으로 대체: node.value = 4
4. 오른쪽 서브트리에서 후속자(4) 삭제: node.right = delete(node.right, 4)
```

재귀 흐름 (delete(root, 3)):
```
delete(5, 3)
  3 < 5 → node.left = delete(3, 3)
    cmp == 0 → 삭제 대상 발견!
    자식 2개 → 후속자 = findMin(node.right) = 4
    node.value = 4로 대체
    size++ (보정: 아래 delete에서 또 size--하므로)
    node.right = delete(4, 4)
      cmp == 0, 리프 노드 → return null
    return node (값이 4로 바뀐 노드)
  return node(5)
```

> **size++ 보정 이유**: delete 메서드 진입 시 `size--`를 하는데, 
> 후속자를 재귀 삭제할 때 `delete`가 한 번 더 호출되어 `size--`가 중복 실행된다. 
> 실제로는 노드 1개만 삭제하므로 미리 `size++`로 보정하는 것.

---

## 12. AVL 트리 (자가 균형 이진 탐색 트리)

### 핵심 개념
- BST와 동일한 규칙(왼쪽 < 부모 < 오른쪽) + **자동 균형 유지**
- 균형 인수(Balance Factor) = 왼쪽 높이 - 오른쪽 높이
- |BF| > 1 이면 회전으로 재균형
- 시간 복잡도: 삽입/탐색/삭제 모두 **항상 O(log n) 보장**

### 삽입 흐름
BST와 동일하게 재귀 삽입 후, 돌아오면서 `rebalance()` 호출.

```
insert(30), insert(20), insert(10) 순서:

Step 1: insert(30)       Step 2: insert(20)      Step 3: insert(10)
        30                       30                       30  ← BF=2 (불균형!)
                                /                        /
                              20                       20
                                                      /
                                                    10

→ LL 케이스 → 우회전(rotateRight):
        20
       /  \
     10    30
```

### 4가지 회전 케이스

#### LL (왼쪽-왼쪽) → 우회전
```
불균형:         회전 후:
    y              x
   / \            / \
  x   C    →    A    y
 / \                / \
A   B              B   C

조건: balance > 1 && 왼쪽 자식의 BF >= 0
```

#### RR (오른쪽-오른쪽) → 좌회전
```
불균형:         회전 후:
  x                y
 / \              / \
A   y      →    x    C
   / \         / \
  B   C       A   B

조건: balance < -1 && 오른쪽 자식의 BF <= 0
```

#### LR (왼쪽-오른쪽) → 왼쪽 자식 좌회전 후 우회전
```
불균형:         1단계(좌회전):     2단계(우회전):
    z              z                  y
   /              /                  / \
  x       →     y          →      x    z
   \           /
    y         x

조건: balance > 1 && 왼쪽 자식의 BF < 0
```

#### RL (오른쪽-왼쪽) → 오른쪽 자식 우회전 후 좌회전
```
불균형:         1단계(우회전):     2단계(좌회전):
  z              z                    y
   \              \                  / \
    x      →      y        →      z    x
   /                \
  y                  x

조건: balance < -1 && 오른쪽 자식의 BF > 0
```

### 삭제 흐름
BST와 동일하게 재귀 삭제 후, 돌아오면서 `rebalance()` 호출.

```
삭제 예시: delete(30)
        20                    20
       /  \                  /  \
     10    30      →       10    40
          / \                   /
        25   40               25

삭제 후 불균형 발생 시 → rebalance()로 회전 수행
```

### rebalance() 재귀 흐름
```
insert 또는 delete 재귀가 콜스택을 타고 올라오면서
매 노드마다:
  1. updateHeight(node)  ← 높이 갱신
  2. balanceFactor(node) ← BF 계산
  3. |BF| > 1이면 → 적절한 회전 수행
  4. 아니면 → 그냥 node 반환

이 과정이 루트까지 반복되므로 트리 전체 균형이 유지된다.
```

### BST vs AVL 삽입 비교
```
1, 2, 3, 4, 5 순서로 삽입 시:

BST (편향):           AVL (균형):
1                          2
 \                        / \
  2                      1   4
   \                        / \
    3                      3   5
     \
      4
       \
        5
→ O(n)                → O(log n)
```

---

## 13. 자주 쓰이는 유틸리티 메서드 / API 정리

### Objects 클래스 (java.util.Objects)

```java
// null 검사 - null이면 NullPointerException 던짐
Objects.requireNonNull(element);
// 메서드 진입 시 파라미터 검증 용도

// null-safe 동등 비교 - a가 null이어도 안전
Objects.equals(node.element, target);
// a == null이면 NPE 안 나고 false 반환

// 여러 필드로 hashCode 생성
@Override
public int hashCode() {
    return Objects.hash(name, age, email);
}

// null이면 기본값 반환
String result = Objects.toString(name, "unknown");
// name이 null이면 "unknown" 반환
```

### Collections 클래스 (java.util.Collections)

```java
// 리스트 오름차순 정렬
List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5);
Collections.sort(numbers);
// [1, 1, 3, 4, 5]

// 리스트 뒤집기
Collections.reverse(numbers);
// [5, 4, 3, 1, 1]

// 수정 불가 리스트 반환 - 외부에 읽기 전용으로 제공할 때
List<String> readOnly = Collections.unmodifiableList(original);
// readOnly.add("x") → UnsupportedOperationException

// 빈 불변 리스트 - null 대신 반환
List<String> empty = Collections.emptyList();

// 원소 1개짜리 불변 리스트
List<String> single = Collections.singletonList("hello");
```

### Arrays 클래스 (java.util.Arrays)

```java
// 배열 정렬
int[] arr = {5, 2, 8, 1};
Arrays.sort(arr);
// [1, 2, 5, 8]

// 배열 → 리스트 변환
List<Integer> list = Arrays.asList(1, 2, 3);

// 배열 복사 (길이 조절 가능) - 배열 확장 시 사용
int[] copied = Arrays.copyOf(arr, 10);
// 원래 4개 + 나머지 0으로 채움

// 배열 전체를 특정 값으로 채움
int[] zeros = new int[5];
Arrays.fill(zeros, 0);
// [0, 0, 0, 0, 0]

// 배열 내용을 문자열로 출력 - 디버깅용
System.out.println(Arrays.toString(arr));
// [1, 2, 5, 8]
```

### Math 클래스 (java.lang.Math)

```java
// 둘 중 큰 값 - AVL 높이 계산 시 사용
int maxHeight = Math.max(leftHeight, rightHeight);

// 둘 중 작은 값
int min = Math.min(a, b);

// 절댓값 - AVL 균형 인수 계산 시 사용
int absDiff = Math.abs(leftHeight - rightHeight);
```

### Comparable vs Comparator

```java
// Comparable - 객체 자체에 비교 기준 내장
// BST, AVL 등 트리의 타입 제약으로 사용
public class Student implements Comparable<Student> {
    int age;

    @Override
    public int compareTo(Student other) {
        return this.age - other.age; // 나이 기준 비교
    }
}

// Comparator - 외부에서 비교 기준 주입
// 정렬 기준을 유연하게 바꿀 때 사용
List<Student> students = ...;
students.sort(Comparator.comparing(s -> s.name)); // 이름 기준 정렬
students.sort(Comparator.comparing(s -> s.age));   // 나이 기준 정렬
```

### Queue 주요 메서드 (java.util.Queue)

```java
Queue<Integer> queue = new LinkedList<>();

// 삽입 - 실패 시 false 반환 (예외 안 던짐)
queue.offer(1);
queue.offer(2);

// 꺼내기(제거) - 비어있으면 null 반환 (예외 안 던짐)
Integer value = queue.poll(); // 1

// 조회만(제거 안 함) - 비어있으면 null 반환
Integer peek = queue.peek(); // 2

// 아래는 실패 시 예외를 던지는 버전
queue.add(3);       // 삽입 - 실패 시 예외
queue.remove();     // 꺼내기 - 비어있으면 NoSuchElementException

// 일반적으로 offer/poll/peek을 더 많이 사용한다.
```

---

### 핵심 차이
- **BST vs AVL**: AVL은 BST에 자동 균형(회전)을 추가한 것. 좌우 높이 차이 1 이하 유지.
- **트리 vs 힙**: 트리는 좌우 구분이 있고 탐색에 특화. 힙은 좌우 구분 없이 위아래만 중요하고 최솟값/최댓값 추출에 특화.
- Java에서 힙은 `PriorityQueue`로 구현되어 있으며 트리와는 별개의 자료구조.**