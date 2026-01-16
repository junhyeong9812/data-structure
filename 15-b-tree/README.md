# 15. B-트리 (B-Tree)

## 📋 문제 정의

**디스크 기반 저장소에 최적화된** B-트리를 구현하세요.

B-트리는 자가 균형 다진 탐색 트리로, 데이터베이스와 파일 시스템에서
대용량 데이터를 효율적으로 저장하고 검색하는 데 사용됩니다.

---

## 🎯 학습 목표

- B-트리의 구조와 속성 이해
- 노드 분할(Split) 알고리즘
- 노드 병합(Merge) 알고리즘
- 차수(Order)와 최소/최대 키 개수
- 디스크 I/O 최적화 원리

---

## 📝 요구사항

### B-트리 속성 (차수 t)

| 속성 | 설명 |
|------|------|
| 최소 키 개수 | 루트 제외 모든 노드: t-1개 이상 |
| 최대 키 개수 | 모든 노드: 2t-1개 이하 |
| 최소 자식 개수 | 루트 제외 내부 노드: t개 이상 |
| 최대 자식 개수 | 모든 내부 노드: 2t개 이하 |
| 리프 깊이 | 모든 리프는 같은 깊이 |

### 기본 연산

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `search(key)` | 키 검색 | O(log n) |
| `insert(key)` | 키 삽입 | O(log n) |
| `delete(key)` | 키 삭제 | O(log n) |
| `traverse()` | 모든 키 순회 | O(n) |

### 추가 연산

| 메서드 | 설명 |
|--------|------|
| `getMin()` | 최소 키 |
| `getMax()` | 최대 키 |
| `getHeight()` | 트리 높이 |
| `getSize()` | 총 키 개수 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용 (t=2, 2-3-4 트리)
```java
BTree tree = new BTree(2);  // 최소 차수 2

tree.insert(10);
tree.insert(20);
tree.insert(5);
tree.insert(6);
tree.insert(12);
tree.insert(30);
tree.insert(7);
tree.insert(17);

System.out.println(tree.search(6));   // true
System.out.println(tree.search(15));  // false

tree.traverse();  // 5 6 7 10 12 17 20 30
```

### 예제 2: 노드 분할 시각화
```
t=3 (최대 5키, 최소 2키)

삽입 전: [10, 20, 30, 40, 50] (가득 참)

insert(25) 후:
        [30]
       /    \
[10,20,25] [40,50]

중간값 30이 위로 올라가고 노드 분할
```

### 예제 3: 삭제 케이스
```
삭제 전:
        [30]
       /    \
[10,20]    [40,50]

delete(40) 후:
      [30]
     /    \
[10,20]  [50]

delete(50) 후 (형제에서 빌리기):
      [20]
     /    \
  [10]   [30]
```

### 예제 4: 높이 변화
```
t=2일 때 높이와 최대 키 개수:
높이 0: 최대 3키
높이 1: 최대 15키
높이 2: 최대 63키
높이 h: 최대 (2t)^(h+1) - 1 키
```

---

## 🔍 핵심 개념

### B-트리 vs 이진 트리
```
이진 탐색 트리:
    30
   /  \
  20   40
 / \   / \
10 25 35 50

B-트리 (t=2):
      [20, 35]
     /   |    \
[10] [25,30] [40,50]

→ B-트리가 더 넓고 낮음 (디스크 접근 최소화)
```

### 노드 구조
```java
class BTreeNode {
    int[] keys;           // 키 배열
    BTreeNode[] children; // 자식 포인터 배열
    int n;                // 현재 키 개수
    boolean leaf;         // 리프 여부
}
```

### 분할 (Split)
```
노드가 가득 찼을 때 (2t-1개 키):

[k1, k2, k3, k4, k5]  (t=3)
        ↓
      [k3] (부모로)
     /    \
[k1,k2] [k4,k5]
```

---

## 💡 힌트

### 검색
```java
BTreeNode search(BTreeNode x, int key) {
    int i = 0;
    while (i < x.n && key > x.keys[i]) {
        i++;
    }
    
    if (i < x.n && key == x.keys[i]) {
        return x;  // 발견
    }
    
    if (x.leaf) {
        return null;  // 리프인데 없음
    }
    
    return search(x.children[i], key);
}
```

### 삽입 (분할 선행)
```java
void insert(int key) {
    if (root.n == 2*t - 1) {
        // 루트가 가득 찼으면 먼저 분할
        BTreeNode newRoot = new BTreeNode(t, false);
        newRoot.children[0] = root;
        splitChild(newRoot, 0, root);
        root = newRoot;
    }
    insertNonFull(root, key);
}
```

### 자식 분할
```java
void splitChild(BTreeNode parent, int i, BTreeNode fullChild) {
    BTreeNode newNode = new BTreeNode(t, fullChild.leaf);
    newNode.n = t - 1;
    
    // 키 복사 (후반부)
    for (int j = 0; j < t - 1; j++) {
        newNode.keys[j] = fullChild.keys[j + t];
    }
    
    // 자식 복사 (리프가 아니면)
    if (!fullChild.leaf) {
        for (int j = 0; j < t; j++) {
            newNode.children[j] = fullChild.children[j + t];
        }
    }
    
    fullChild.n = t - 1;
    
    // 부모에 중간 키 삽입
    for (int j = parent.n; j > i; j--) {
        parent.children[j + 1] = parent.children[j];
    }
    parent.children[i + 1] = newNode;
    
    for (int j = parent.n - 1; j >= i; j--) {
        parent.keys[j + 1] = parent.keys[j];
    }
    parent.keys[i] = fullChild.keys[t - 1];
    parent.n++;
}
```

---

## ✅ 체크리스트

- [ ] 노드 구조 정의
- [ ] search 구현
- [ ] insert 구현 (분할 포함)
- [ ] delete 구현 (병합/재분배 포함)
- [ ] traverse (중위 순회)
- [ ] getMin, getMax
- [ ] B+ 트리 변형 (선택)

---

## 📚 참고

- 데이터베이스 인덱스 (MySQL InnoDB)
- 파일 시스템 (NTFS, ext4)
- B+ 트리: 리프에만 데이터, 리프 연결
- [B-Tree Visualization](https://www.cs.usfca.edu/~galles/visualization/BTree.html)
