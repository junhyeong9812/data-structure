# Red-Black 트리 풀이 해설

## 📌 핵심 아이디어

Red-Black 트리는 **색상 규칙**을 통해 균형을 유지합니다.
AVL 트리보다 덜 엄격한 균형을 유지하여 삽입/삭제 시 회전 횟수가 적습니다.

**핵심 보장**:
- 최장 경로 ≤ 2 × 최단 경로
- 모든 연산 O(log n) 보장

---

## 🔑 핵심 개념

### 1. 5가지 속성
```
1. 모든 노드는 빨강 또는 검정
2. 루트는 검정
3. 모든 NIL(리프)는 검정
4. 빨강 노드의 자식은 검정 (No Red-Red)
5. 모든 경로의 검정 노드 수 동일 (Black Height)

예시:
        20(B)           bh=2
       /    \
    10(R)   30(B)       bh=1 (30에서)
   /    \      \
 5(B)  15(B)  40(R)     bh=0 (리프에서)
```

### 2. 회전 연산
```
좌회전 (x 기준):
     P               P
     |               |
     x       →       y
    / \             / \
   a   y           x   c
      / \         / \
     b   c       a   b

우회전 (y 기준):
     P               P
     |               |
     y       →       x
    / \             / \
   x   c           a   y
  / \                 / \
 a   b               b   c
```

### 3. 삽입 Fix-up 케이스
```
새 노드 z는 항상 빨강으로 삽입

Case 1: 삼촌 U가 빨강
     G(B)              G(R)
    /    \    →       /    \
   P(R)  U(R)       P(B)  U(B)
   /                 /
  z(R)              z(R)
  
  → G에서 재귀적으로 fix-up

Case 2: 삼촌 U가 검정, z가 오른쪽 자식 (꺾인 형태)
     G(B)              G(B)
    /    \    →       /    \
   P(R)  U(B)       z(R)  U(B)
     \               /
     z(R)          P(R)
     
  → P에서 좌회전 후 Case 3

Case 3: 삼촌 U가 검정, z가 왼쪽 자식 (직선 형태)
     G(B)              P(B)
    /    \    →       /    \
   P(R)  U(B)       z(R)  G(R)
   /                        \
  z(R)                      U(B)
  
  → G에서 우회전, 색상 교환
```

---

## 📝 POP 구현 해설

### 노드 클래스
```java
public class RedBlackTree<K extends Comparable<K>> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    
    private Node<K> root;
    private final Node<K> NIL;  // 센티넬 노드
    private int size;
    
    private static class Node<K> {
        K key;
        Node<K> left, right, parent;
        boolean color;
        
        Node(K key, boolean color, Node<K> nil) {
            this.key = key;
            this.color = color;
            this.left = nil;
            this.right = nil;
        }
    }
    
    public RedBlackTree() {
        NIL = new Node<>(null, BLACK, null);
        NIL.left = NIL;
        NIL.right = NIL;
        root = NIL;
    }
}
```

### 회전 연산
```java
private void leftRotate(Node<K> x) {
    Node<K> y = x.right;
    
    // y의 왼쪽 서브트리를 x의 오른쪽으로
    x.right = y.left;
    if (y.left != NIL) {
        y.left.parent = x;
    }
    
    // y를 x의 부모에 연결
    y.parent = x.parent;
    if (x.parent == null) {
        root = y;
    } else if (x == x.parent.left) {
        x.parent.left = y;
    } else {
        x.parent.right = y;
    }
    
    // x를 y의 왼쪽 자식으로
    y.left = x;
    x.parent = y;
}

private void rightRotate(Node<K> y) {
    Node<K> x = y.left;
    
    // x의 오른쪽 서브트리를 y의 왼쪽으로
    y.left = x.right;
    if (x.right != NIL) {
        x.right.parent = y;
    }
    
    // x를 y의 부모에 연결
    x.parent = y.parent;
    if (y.parent == null) {
        root = x;
    } else if (y == y.parent.left) {
        y.parent.left = x;
    } else {
        y.parent.right = x;
    }
    
    // y를 x의 오른쪽 자식으로
    x.right = y;
    y.parent = x;
}
```

### 삽입
```java
public void insert(K key) {
    Node<K> z = new Node<>(key, RED, NIL);
    
    Node<K> y = null;
    Node<K> x = root;
    
    // BST 삽입 위치 찾기
    while (x != NIL) {
        y = x;
        if (key.compareTo(x.key) < 0) {
            x = x.left;
        } else {
            x = x.right;
        }
    }
    
    z.parent = y;
    
    if (y == null) {
        root = z;
    } else if (key.compareTo(y.key) < 0) {
        y.left = z;
    } else {
        y.right = z;
    }
    
    size++;
    insertFixup(z);
}

private void insertFixup(Node<K> z) {
    while (z.parent != null && z.parent.color == RED) {
        if (z.parent == z.parent.parent.left) {
            Node<K> uncle = z.parent.parent.right;
            
            if (uncle.color == RED) {
                // Case 1
                z.parent.color = BLACK;
                uncle.color = BLACK;
                z.parent.parent.color = RED;
                z = z.parent.parent;
            } else {
                if (z == z.parent.right) {
                    // Case 2
                    z = z.parent;
                    leftRotate(z);
                }
                // Case 3
                z.parent.color = BLACK;
                z.parent.parent.color = RED;
                rightRotate(z.parent.parent);
            }
        } else {
            // 대칭 케이스 (left ↔ right)
            Node<K> uncle = z.parent.parent.left;
            
            if (uncle.color == RED) {
                z.parent.color = BLACK;
                uncle.color = BLACK;
                z.parent.parent.color = RED;
                z = z.parent.parent;
            } else {
                if (z == z.parent.left) {
                    z = z.parent;
                    rightRotate(z);
                }
                z.parent.color = BLACK;
                z.parent.parent.color = RED;
                leftRotate(z.parent.parent);
            }
        }
    }
    root.color = BLACK;
}
```

### 삭제
```java
public void delete(K key) {
    Node<K> z = searchNode(key);
    if (z == NIL) return;
    
    Node<K> y = z;
    Node<K> x;
    boolean yOriginalColor = y.color;
    
    if (z.left == NIL) {
        x = z.right;
        transplant(z, z.right);
    } else if (z.right == NIL) {
        x = z.left;
        transplant(z, z.left);
    } else {
        y = minimum(z.right);  // 후임자
        yOriginalColor = y.color;
        x = y.right;
        
        if (y.parent == z) {
            x.parent = y;
        } else {
            transplant(y, y.right);
            y.right = z.right;
            y.right.parent = y;
        }
        
        transplant(z, y);
        y.left = z.left;
        y.left.parent = y;
        y.color = z.color;
    }
    
    size--;
    
    if (yOriginalColor == BLACK) {
        deleteFixup(x);
    }
}

private void deleteFixup(Node<K> x) {
    while (x != root && x.color == BLACK) {
        if (x == x.parent.left) {
            Node<K> w = x.parent.right;  // 형제
            
            if (w.color == RED) {
                // Case 1
                w.color = BLACK;
                x.parent.color = RED;
                leftRotate(x.parent);
                w = x.parent.right;
            }
            
            if (w.left.color == BLACK && w.right.color == BLACK) {
                // Case 2
                w.color = RED;
                x = x.parent;
            } else {
                if (w.right.color == BLACK) {
                    // Case 3
                    w.left.color = BLACK;
                    w.color = RED;
                    rightRotate(w);
                    w = x.parent.right;
                }
                // Case 4
                w.color = x.parent.color;
                x.parent.color = BLACK;
                w.right.color = BLACK;
                leftRotate(x.parent);
                x = root;
            }
        } else {
            // 대칭 케이스
            Node<K> w = x.parent.left;
            
            if (w.color == RED) {
                w.color = BLACK;
                x.parent.color = RED;
                rightRotate(x.parent);
                w = x.parent.left;
            }
            
            if (w.right.color == BLACK && w.left.color == BLACK) {
                w.color = RED;
                x = x.parent;
            } else {
                if (w.left.color == BLACK) {
                    w.right.color = BLACK;
                    w.color = RED;
                    leftRotate(w);
                    w = x.parent.left;
                }
                w.color = x.parent.color;
                x.parent.color = BLACK;
                w.left.color = BLACK;
                rightRotate(x.parent);
                x = root;
            }
        }
    }
    x.color = BLACK;
}

private void transplant(Node<K> u, Node<K> v) {
    if (u.parent == null) {
        root = v;
    } else if (u == u.parent.left) {
        u.parent.left = v;
    } else {
        u.parent.right = v;
    }
    v.parent = u.parent;
}

private Node<K> minimum(Node<K> x) {
    while (x.left != NIL) {
        x = x.left;
    }
    return x;
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 최대 회전 수 |
|------|-----------|-------------|
| search | O(log n) | 0 |
| insert | O(log n) | 2 |
| delete | O(log n) | 3 |

### 높이 보장
```
n개 노드일 때:
h ≤ 2 × log₂(n + 1)

증명: Black height ≤ log₂(n+1)
      total height ≤ 2 × black height
```

### AVL vs Red-Black

| 특성 | AVL | Red-Black |
|------|-----|-----------|
| 균형 기준 | 높이 차 ≤ 1 | 검정 높이 동일 |
| 검색 | 더 빠름 | 약간 느림 |
| 삽입/삭제 | 더 많은 회전 | 적은 회전 |
| 용도 | 검색 위주 | 삽입/삭제 빈번 |

---

## ❌ 흔한 실수

### 1. NIL 노드 처리
```java
// 잘못됨: null 사용
if (node.left == null) ...

// 올바름: NIL 센티넬 사용
if (node.left == NIL) ...
```

### 2. 부모 포인터 업데이트
```java
// 잘못됨: 부모 업데이트 누락
y.left = x;
// x.parent = y;  누락!

// 올바름
y.left = x;
x.parent = y;
```

### 3. 루트 색상
```java
// 삽입/삭제 후 항상 확인!
root.color = BLACK;
```

### 4. 대칭 케이스 누락
```java
// 왼쪽 자식일 때와 오른쪽 자식일 때
// 완전히 대칭적인 코드 필요!
```

---

## 🔗 관련 문제

- LeetCode (직접적 RB 트리 문제는 없음)
- Java TreeMap/TreeSet 소스 분석
- 시스템 설계: 정렬된 맵/셋 필요시
