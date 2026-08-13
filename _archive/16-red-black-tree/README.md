# 16. Red-Black 트리 (Red-Black Tree)

## 📋 문제 정의

**자가 균형 이진 탐색 트리**인 Red-Black 트리를 구현하세요.

Red-Black 트리는 색상 규칙과 회전 연산을 통해 트리의 균형을 유지하여
삽입, 삭제, 검색 모두 O(log n) 시간복잡도를 보장합니다.

---

## 🎯 학습 목표

- Red-Black 트리의 5가지 속성 이해
- 색상 변경(Recoloring) 연산
- 회전(Rotation) 연산: 좌회전, 우회전
- 삽입 후 균형 복구 (Fix-up)
- 삭제 후 균형 복구 (Fix-up)

---

## 📝 요구사항

### Red-Black 트리 5가지 속성

| # | 속성 | 설명 |
|---|------|------|
| 1 | 색상 | 모든 노드는 빨강(Red) 또는 검정(Black) |
| 2 | 루트 | 루트는 항상 검정 |
| 3 | 리프(NIL) | 모든 리프(NIL)는 검정 |
| 4 | 빨강 규칙 | 빨강 노드의 자식은 모두 검정 (연속 빨강 불가) |
| 5 | 검정 높이 | 모든 경로에서 검정 노드 수 동일 |

### 기본 연산

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `insert(key)` | 키 삽입 | O(log n) |
| `delete(key)` | 키 삭제 | O(log n) |
| `search(key)` | 키 검색 | O(log n) |
| `contains(key)` | 키 존재 여부 | O(log n) |

### 추가 연산

| 메서드 | 설명 |
|--------|------|
| `getMin()` | 최소 키 |
| `getMax()` | 최대 키 |
| `getHeight()` | 트리 높이 |
| `getBlackHeight()` | 검정 높이 |
| `size()` | 총 노드 수 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
RedBlackTree<Integer> tree = new RedBlackTree<>();

tree.insert(10);
tree.insert(20);
tree.insert(30);  // 회전 발생!
tree.insert(15);

System.out.println(tree.search(15));  // true
System.out.println(tree.search(25));  // false

tree.delete(20);
System.out.println(tree.search(20));  // false
```

### 예제 2: 삽입 시 회전
```
삽입 순서: 10, 20, 30

Step 1: insert(10)     Step 2: insert(20)
    10(B)                  10(B)
                             \
                             20(R)

Step 3: insert(30) - 좌회전 필요!
    10(B)                  20(B)
      \          →        /    \
      20(R)            10(R)  30(R)
        \
        30(R)
```

### 예제 3: 색상 시각화
```
      20(B)
     /    \
  10(R)   30(R)
   /  \      \
 5(B) 15(B)  40(B)

(B) = Black, (R) = Red
검정 높이 = 2 (루트 제외 모든 경로에서)
```

### 예제 4: 삭제 케이스
```
삭제 전:          삭제 후 (10 삭제):
    20(B)              20(B)
   /    \             /    \
 10(B)  30(B)       15(B)  30(B)
   \
   15(R)
```

---

## 🔍 핵심 개념

### 회전 연산
```
좌회전 (Left Rotate):
    x                y
   / \     →       / \
  a   y           x   c
     / \         / \
    b   c       a   b

우회전 (Right Rotate):
      y              x
     / \    →      / \
    x   c         a   y
   / \               / \
  a   b             b   c
```

### 삽입 Fix-up 케이스
```
Case 1: 삼촌이 빨강
  → 부모와 삼촌을 검정, 조부모를 빨강으로
  → 조부모에서 재귀

Case 2: 삼촌이 검정, 꺾인 형태
  → 회전으로 Case 3으로 변환

Case 3: 삼촌이 검정, 직선 형태
  → 부모-조부모 회전 + 색상 교환
```

### 삭제 Fix-up 케이스
```
Case 1: 형제가 빨강
  → 형제를 검정, 부모를 빨강, 회전
  
Case 2: 형제가 검정, 형제의 자식 둘 다 검정
  → 형제를 빨강, 부모로 이동

Case 3: 형제가 검정, 형제의 먼 자식이 검정
  → 회전으로 Case 4로 변환

Case 4: 형제가 검정, 형제의 먼 자식이 빨강
  → 회전 + 색상 조정
```

---

## 💡 힌트

### 노드 구조
```java
class Node<K extends Comparable<K>> {
    K key;
    Node<K> left, right, parent;
    boolean red;  // true = Red, false = Black
    
    Node(K key) {
        this.key = key;
        this.red = true;  // 새 노드는 빨강
    }
}
```

### 좌회전
```java
void leftRotate(Node<K> x) {
    Node<K> y = x.right;
    x.right = y.left;
    
    if (y.left != NIL) {
        y.left.parent = x;
    }
    
    y.parent = x.parent;
    
    if (x.parent == null) {
        root = y;
    } else if (x == x.parent.left) {
        x.parent.left = y;
    } else {
        x.parent.right = y;
    }
    
    y.left = x;
    x.parent = y;
}
```

### 삽입 Fix-up
```java
void insertFixup(Node<K> z) {
    while (z.parent != null && z.parent.red) {
        if (z.parent == z.parent.parent.left) {
            Node<K> uncle = z.parent.parent.right;
            
            if (uncle.red) {
                // Case 1: 삼촌이 빨강
                z.parent.red = false;
                uncle.red = false;
                z.parent.parent.red = true;
                z = z.parent.parent;
            } else {
                if (z == z.parent.right) {
                    // Case 2: 꺾인 형태
                    z = z.parent;
                    leftRotate(z);
                }
                // Case 3: 직선 형태
                z.parent.red = false;
                z.parent.parent.red = true;
                rightRotate(z.parent.parent);
            }
        } else {
            // 대칭 (left ↔ right)
        }
    }
    root.red = false;
}
```

---

## ✅ 체크리스트

- [ ] 노드 구조 정의 (색상 포함)
- [ ] 좌회전, 우회전 구현
- [ ] 삽입 구현
- [ ] 삽입 Fix-up 구현
- [ ] 삭제 구현
- [ ] 삭제 Fix-up 구현
- [ ] 5가지 속성 검증 메서드

---

## 📚 참고

- Java TreeMap, TreeSet 내부 구현
- Linux Kernel의 rbtree
- [Red-Black Tree Visualization](https://www.cs.usfca.edu/~galles/visualization/RedBlack.html)
- CLRS (Introduction to Algorithms) Chapter 13
