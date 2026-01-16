# 06. 이진 탐색 트리 (Binary Search Tree)

## 📋 문제 정의

**BST 속성**을 만족하는 이진 탐색 트리를 구현하세요.

모든 노드에서 왼쪽 서브트리의 값 < 현재 노드의 값 < 오른쪽 서브트리의 값을 만족하는 트리입니다.

---

## 🎯 학습 목표

- BST 속성 이해
- 재귀적 트리 순회 (전위, 중위, 후위, 레벨)
- 삽입, 삭제, 검색 알고리즘
- 균형 트리의 필요성 이해
- 트리 기반 집합/맵 구현

---

## 📝 요구사항

### 기본 연산

| 메서드 | 설명 | 평균 시간복잡도 | 최악 시간복잡도 |
|--------|------|----------------|----------------|
| `insert(value)` | 값 삽입 | O(log n) | O(n) |
| `search(value)` | 값 검색 | O(log n) | O(n) |
| `delete(value)` | 값 삭제 | O(log n) | O(n) |
| `contains(value)` | 존재 여부 확인 | O(log n) | O(n) |
| `min()` | 최솟값 반환 | O(log n) | O(n) |
| `max()` | 최댓값 반환 | O(log n) | O(n) |
| `size()` | 노드 개수 | O(1) | O(1) |
| `isEmpty()` | 비어있는지 확인 | O(1) | O(1) |
| `clear()` | 모든 노드 삭제 | O(1) | O(1) |

### 순회 (Traversal)

| 메서드 | 순서 | 용도 |
|--------|------|------|
| `inorder()` | 왼쪽 → 루트 → 오른쪽 | **정렬된 순서** 출력 |
| `preorder()` | 루트 → 왼쪽 → 오른쪽 | 트리 복사, 직렬화 |
| `postorder()` | 왼쪽 → 오른쪽 → 루트 | 트리 삭제, 후위 표기법 |
| `levelorder()` | 레벨 순서 (BFS) | 레벨별 처리 |

### 추가 연산

| 메서드 | 설명 |
|--------|------|
| `height()` | 트리 높이 |
| `floor(value)` | value 이하의 최댓값 |
| `ceiling(value)` | value 이상의 최솟값 |
| `rank(value)` | value보다 작은 키 개수 |
| `select(k)` | k번째로 작은 값 |
| `predecessor(value)` | 바로 이전 값 (중위 순회 기준) |
| `successor(value)` | 바로 다음 값 (중위 순회 기준) |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
BST<Integer> bst = new BST<>();
bst.insert(5);
bst.insert(3);
bst.insert(7);
bst.insert(1);
bst.insert(9);

//       5
//      / \
//     3   7
//    /     \
//   1       9

System.out.println(bst.search(3));  // true
System.out.println(bst.search(6));  // false
System.out.println(bst.min());      // 1
System.out.println(bst.max());      // 9
```

### 예제 2: 순회
```java
BST<Integer> bst = new BST<>();
// 삽입: 5, 3, 7, 1, 4, 6, 9

bst.inorder();    // [1, 3, 4, 5, 6, 7, 9] - 정렬된 순서!
bst.preorder();   // [5, 3, 1, 4, 7, 6, 9]
bst.postorder();  // [1, 4, 3, 6, 9, 7, 5]
bst.levelorder(); // [5, 3, 7, 1, 4, 6, 9]
```

### 예제 3: 삭제
```java
BST<Integer> bst = new BST<>();
bst.insert(5);
bst.insert(3);
bst.insert(7);

// Case 1: 리프 노드 삭제
bst.delete(3);   // 3 제거

// Case 2: 자식 하나인 노드 삭제
bst.delete(7);   // 7 제거, 자식이 있으면 승계

// Case 3: 자식 둘인 노드 삭제
bst.delete(5);   // 후계자(successor)로 대체
```

### 예제 4: floor/ceiling
```java
BST<Integer> bst = new BST<>();
// 삽입: 10, 20, 30, 40, 50

bst.floor(25);    // 20 (25 이하 최대)
bst.ceiling(25);  // 30 (25 이상 최소)
bst.floor(10);    // 10
bst.ceiling(50);  // 50
bst.floor(5);     // null (없음)
```

---

## 🔍 제약 조건

- 중복 값 허용하지 않음 (Set 시맨틱)
- null 값 허용하지 않음
- Comparable 구현 또는 Comparator 제공 필요
- 최악의 경우 (편향 트리) O(n) 성능

---

## 💡 힌트

### 노드 구조
```java
class Node<T> {
    T value;
    Node<T> left;
    Node<T> right;
    // 선택: Node<T> parent, int size, int height
}
```

### 삭제 알고리즘 (3가지 케이스)
```java
// Case 1: 리프 노드 → 그냥 삭제
// Case 2: 자식 하나 → 자식으로 대체
// Case 3: 자식 둘 → 후계자(오른쪽 서브트리의 최솟값)로 대체
```

### 재귀 vs 반복
```java
// 재귀 (간결)
Node<T> insert(Node<T> node, T value) {
    if (node == null) return new Node<>(value);
    if (value < node.value)
        node.left = insert(node.left, value);
    else
        node.right = insert(node.right, value);
    return node;
}

// 반복 (스택 오버플로우 방지)
void insert(T value) {
    if (root == null) { root = new Node<>(value); return; }
    Node<T> curr = root;
    while (true) {
        if (value < curr.value) {
            if (curr.left == null) { curr.left = new Node<>(value); return; }
            curr = curr.left;
        } else {
            if (curr.right == null) { curr.right = new Node<>(value); return; }
            curr = curr.right;
        }
    }
}
```

---

## ✅ 체크리스트

- [ ] 삽입, 검색, 삭제 기본 연산
- [ ] 4가지 순회 구현
- [ ] min, max, height 구현
- [ ] floor, ceiling 구현
- [ ] predecessor, successor 구현
- [ ] rank, select 구현
- [ ] Iterator 구현 (중위 순회)

---

## 📚 참고

- [Java TreeMap 소스코드](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/TreeMap.java)
- 균형 트리의 필요성 (AVL, Red-Black)
- 이진 탐색과의 관계
