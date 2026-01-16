# B-트리 풀이 해설

## 📌 핵심 아이디어

B-트리는 **디스크 접근을 최소화**하기 위해 설계된 자가 균형 트리입니다.
각 노드가 여러 키를 가지므로 트리가 넓고 낮아집니다.

**핵심 원리**:
- 노드당 많은 키 → 적은 디스크 접근
- 항상 균형 유지 → O(log n) 보장
- 분할/병합으로 균형 유지

---

## 🔑 핵심 개념

### 1. B-트리 속성
```
차수 t = 3인 B-트리:
- 최소 키: t-1 = 2 (루트 제외)
- 최대 키: 2t-1 = 5
- 최소 자식: t = 3 (루트 제외)
- 최대 자식: 2t = 6

예시:
           [30, 60]
          /   |    \
    [10,20] [40,50] [70,80,90]
    
모든 리프는 같은 레벨!
```

### 2. 검색 과정
```
search(45):

1. 루트 [30, 60] 검사
   30 < 45 < 60 → 중간 자식으로

2. 자식 [40, 50] 검사
   40 < 45 < 50 → 키 사이에 있음
   리프이고 45 != 40, 45 != 50 → 없음

결과: false
```

### 3. 삽입 과정
```
insert(45), t=2:

초기: [10, 20, 30]

Step 1: 위치 찾기 → 30 다음
Step 2: 노드 가득 참 (2t-1=3)
Step 3: 분할 필요!

분할:
[10, 20, 30, 45]
       ↓
     [20]        ← 새 루트
    /    \
 [10]  [30,45]
```

### 4. 삭제 과정
```
delete(20):

경우 1: 리프에서 직접 삭제
경우 2: 내부 노드 → 전임자/후임자로 대체
경우 3: 키 부족 → 형제에서 빌리기 또는 병합
```

---

## 📝 POP 구현 해설

### 노드 클래스
```java
public class BTree {
    private final int t;  // 최소 차수
    private BTreeNode root;
    
    private static class BTreeNode {
        int[] keys;
        BTreeNode[] children;
        int n;  // 현재 키 개수
        boolean leaf;
        
        BTreeNode(int t, boolean leaf) {
            this.leaf = leaf;
            this.keys = new int[2 * t - 1];
            this.children = new BTreeNode[2 * t];
            this.n = 0;
        }
    }
    
    public BTree(int t) {
        this.t = t;
        this.root = new BTreeNode(t, true);
    }
}
```

### 검색
```java
public boolean search(int key) {
    return search(root, key) != null;
}

private BTreeNode search(BTreeNode x, int key) {
    int i = 0;
    
    // 키 위치 찾기
    while (i < x.n && key > x.keys[i]) {
        i++;
    }
    
    // 발견
    if (i < x.n && key == x.keys[i]) {
        return x;
    }
    
    // 리프인데 없음
    if (x.leaf) {
        return null;
    }
    
    // 자식으로 재귀
    return search(x.children[i], key);
}
```

### 삽입
```java
public void insert(int key) {
    BTreeNode r = root;
    
    // 루트가 가득 찼으면 분할
    if (r.n == 2 * t - 1) {
        BTreeNode s = new BTreeNode(t, false);
        root = s;
        s.children[0] = r;
        splitChild(s, 0);
        insertNonFull(s, key);
    } else {
        insertNonFull(r, key);
    }
}

// 가득 차지 않은 노드에 삽입
private void insertNonFull(BTreeNode x, int key) {
    int i = x.n - 1;
    
    if (x.leaf) {
        // 리프: 올바른 위치에 삽입
        while (i >= 0 && key < x.keys[i]) {
            x.keys[i + 1] = x.keys[i];
            i--;
        }
        x.keys[i + 1] = key;
        x.n++;
    } else {
        // 내부 노드: 적절한 자식 찾기
        while (i >= 0 && key < x.keys[i]) {
            i--;
        }
        i++;
        
        // 자식이 가득 찼으면 분할
        if (x.children[i].n == 2 * t - 1) {
            splitChild(x, i);
            if (key > x.keys[i]) {
                i++;
            }
        }
        insertNonFull(x.children[i], key);
    }
}

// 자식 노드 분할
private void splitChild(BTreeNode parent, int i) {
    BTreeNode fullChild = parent.children[i];
    BTreeNode newChild = new BTreeNode(t, fullChild.leaf);
    newChild.n = t - 1;
    
    // 키 복사 (후반부 → 새 노드)
    for (int j = 0; j < t - 1; j++) {
        newChild.keys[j] = fullChild.keys[j + t];
    }
    
    // 자식 복사 (내부 노드인 경우)
    if (!fullChild.leaf) {
        for (int j = 0; j < t; j++) {
            newChild.children[j] = fullChild.children[j + t];
        }
    }
    
    fullChild.n = t - 1;
    
    // 부모 노드 조정
    for (int j = parent.n; j > i; j--) {
        parent.children[j + 1] = parent.children[j];
    }
    parent.children[i + 1] = newChild;
    
    for (int j = parent.n - 1; j >= i; j--) {
        parent.keys[j + 1] = parent.keys[j];
    }
    parent.keys[i] = fullChild.keys[t - 1];
    parent.n++;
}
```

### 삭제 (복잡!)
```java
public void delete(int key) {
    if (root == null) return;
    
    delete(root, key);
    
    // 루트가 비었으면 첫 번째 자식이 새 루트
    if (root.n == 0) {
        root = root.leaf ? null : root.children[0];
    }
}

private void delete(BTreeNode x, int key) {
    int idx = findKey(x, key);
    
    // 키가 이 노드에 있음
    if (idx < x.n && x.keys[idx] == key) {
        if (x.leaf) {
            removeFromLeaf(x, idx);
        } else {
            removeFromNonLeaf(x, idx);
        }
    } else {
        // 키가 자식에 있음
        if (x.leaf) {
            return;  // 키 없음
        }
        
        boolean lastChild = (idx == x.n);
        
        // 자식이 키가 부족하면 채우기
        if (x.children[idx].n < t) {
            fill(x, idx);
        }
        
        // fill로 마지막 자식이 병합됐을 수 있음
        if (lastChild && idx > x.n) {
            delete(x.children[idx - 1], key);
        } else {
            delete(x.children[idx], key);
        }
    }
}

// 리프에서 삭제
private void removeFromLeaf(BTreeNode x, int idx) {
    for (int i = idx + 1; i < x.n; i++) {
        x.keys[i - 1] = x.keys[i];
    }
    x.n--;
}

// 내부 노드에서 삭제
private void removeFromNonLeaf(BTreeNode x, int idx) {
    int key = x.keys[idx];
    
    // 왼쪽 자식이 충분하면 전임자로 대체
    if (x.children[idx].n >= t) {
        int pred = getPredecessor(x, idx);
        x.keys[idx] = pred;
        delete(x.children[idx], pred);
    }
    // 오른쪽 자식이 충분하면 후임자로 대체
    else if (x.children[idx + 1].n >= t) {
        int succ = getSuccessor(x, idx);
        x.keys[idx] = succ;
        delete(x.children[idx + 1], succ);
    }
    // 둘 다 최소면 병합 후 삭제
    else {
        merge(x, idx);
        delete(x.children[idx], key);
    }
}

// 자식 노드가 키 부족하면 채우기
private void fill(BTreeNode x, int idx) {
    // 왼쪽 형제에서 빌리기
    if (idx > 0 && x.children[idx - 1].n >= t) {
        borrowFromPrev(x, idx);
    }
    // 오른쪽 형제에서 빌리기
    else if (idx < x.n && x.children[idx + 1].n >= t) {
        borrowFromNext(x, idx);
    }
    // 형제와 병합
    else {
        if (idx < x.n) {
            merge(x, idx);
        } else {
            merge(x, idx - 1);
        }
    }
}
```

### 순회
```java
public void traverse() {
    if (root != null) {
        traverse(root);
    }
}

private void traverse(BTreeNode x) {
    int i;
    for (i = 0; i < x.n; i++) {
        if (!x.leaf) {
            traverse(x.children[i]);
        }
        System.out.print(x.keys[i] + " ");
    }
    if (!x.leaf) {
        traverse(x.children[i]);
    }
}

public List<Integer> toList() {
    List<Integer> result = new ArrayList<>();
    collectKeys(root, result);
    return result;
}

private void collectKeys(BTreeNode x, List<Integer> result) {
    if (x == null) return;
    
    for (int i = 0; i < x.n; i++) {
        if (!x.leaf) {
            collectKeys(x.children[i], result);
        }
        result.add(x.keys[i]);
    }
    if (!x.leaf) {
        collectKeys(x.children[x.n], result);
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 디스크 접근 |
|------|-----------|------------|
| search | O(t log_t n) | O(log_t n) |
| insert | O(t log_t n) | O(log_t n) |
| delete | O(t log_t n) | O(log_t n) |
| traverse | O(n) | O(n/t) |

### 높이 분석
```
n개 키, 차수 t일 때:
h ≤ log_t((n+1)/2)

t=100일 때:
- 100만 키 → 높이 ≤ 3
- 10억 키 → 높이 ≤ 5
```

---

## ❌ 흔한 실수

### 1. 분할 시점
```java
// 잘못됨: 삽입 후 분할 (bottom-up)
// 부모로 키를 올릴 때 부모도 가득 찼을 수 있음!

// 올바름: 내려가면서 미리 분할 (top-down)
if (x.children[i].n == 2 * t - 1) {
    splitChild(x, i);
    // 그 후 insertNonFull
}
```

### 2. 배열 인덱스
```java
// 잘못됨: 자식은 키보다 하나 더 많음!
children = new BTreeNode[2 * t - 1];  // 키 개수

// 올바름
children = new BTreeNode[2 * t];  // 자식 개수 = 키 + 1
```

### 3. 루트 특수 처리
```java
// 루트는 최소 키 개수 규칙 적용 안 됨!
// 루트는 1개 키만 있어도 됨 (빈 것만 아니면)

if (root.n == 0) {
    root = root.leaf ? null : root.children[0];
}
```

---

## 🔗 B+ 트리 차이

| 특성 | B-트리 | B+ 트리 |
|------|--------|---------|
| 데이터 위치 | 모든 노드 | 리프만 |
| 리프 연결 | 없음 | 연결 리스트 |
| 범위 쿼리 | 비효율적 | 효율적 |
| 내부 노드 | 키+데이터 | 키만 |

---

## 🔗 관련 문제

- 데이터베이스 인덱스 구현
- 파일 시스템 설계
- LeetCode (직접적 B-트리 문제는 적음)
- 시스템 설계 면접
