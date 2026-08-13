# 구간 트리 풀이 해설

## 📌 핵심 아이디어

구간 트리는 **분할 정복** 원리를 사용하여 배열을 재귀적으로 반으로 나누고,
각 구간의 결과를 미리 계산해 저장합니다.

**핵심 특성**:
- 구간 쿼리: O(log n)
- 점 업데이트: O(log n)
- 구간 업데이트 (Lazy): O(log n)

---

## 🔑 핵심 개념

### 1. 트리 구조
```
배열: [2, 4, 5, 7, 8, 9]

완전 이진 트리:
                  [35]              idx=1, [0,5]
                 /    \
             [11]      [24]         idx=2,3
             /  \      /  \
           [6]  [5]  [15]  [9]      idx=4,5,6,7
           / \       / \
         [2][4]    [7][8]           idx=8,9,12,13

트리 배열: [_, 35, 11, 24, 6, 5, 15, 9, 2, 4, _, _, 7, 8, _, _]
           idx: 0  1   2   3  4  5  6   7  8  9 10 11 12 13 14 15
```

### 2. 구간 표현
```
노드 idx가 담당하는 구간 [start, end]:
- 루트 (idx=1): [0, n-1]
- 왼쪽 자식: [start, mid]
- 오른쪽 자식: [mid+1, end]

mid = (start + end) / 2
```

### 3. 쿼리 케이스
```
query(l, r) 호출 시:

Case 1: 완전 포함 (l <= start && end <= r)
        → tree[node] 반환

Case 2: 완전 불포함 (r < start || l > end)
        → 항등원 반환 (합: 0, 최소: INF, 최대: -INF)

Case 3: 부분 포함
        → 좌우 자식 재귀 호출 후 병합
```

---

## 📝 POP 구현 해설

### 구간 합 Segment Tree
```java
public class SegmentTree {
    private int[] tree;
    private int n;
    
    public SegmentTree(int[] arr) {
        this.n = arr.length;
        this.tree = new int[4 * n];  // 충분한 크기
        build(arr, 1, 0, n - 1);
    }
    
    // 트리 구축: O(n)
    private void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            // 리프 노드
            tree[node] = arr[start];
        } else {
            int mid = (start + end) / 2;
            build(arr, 2 * node, start, mid);        // 왼쪽 자식
            build(arr, 2 * node + 1, mid + 1, end);  // 오른쪽 자식
            tree[node] = tree[2 * node] + tree[2 * node + 1];  // 병합
        }
    }
    
    // 구간 쿼리: O(log n)
    public int query(int left, int right) {
        return query(1, 0, n - 1, left, right);
    }
    
    private int query(int node, int start, int end, int left, int right) {
        // Case 2: 범위 밖
        if (right < start || left > end) {
            return 0;  // 합의 항등원
        }
        
        // Case 1: 완전 포함
        if (left <= start && end <= right) {
            return tree[node];
        }
        
        // Case 3: 부분 포함
        int mid = (start + end) / 2;
        int leftSum = query(2 * node, start, mid, left, right);
        int rightSum = query(2 * node + 1, mid + 1, end, left, right);
        return leftSum + rightSum;
    }
    
    // 점 업데이트: O(log n)
    public void update(int index, int value) {
        update(1, 0, n - 1, index, value);
    }
    
    private void update(int node, int start, int end, int index, int value) {
        if (start == end) {
            // 리프 노드
            tree[node] = value;
        } else {
            int mid = (start + end) / 2;
            if (index <= mid) {
                update(2 * node, start, mid, index, value);
            } else {
                update(2 * node + 1, mid + 1, end, index, value);
            }
            tree[node] = tree[2 * node] + tree[2 * node + 1];  // 재계산
        }
    }
    
    // 값 추가 (기존 값에 delta 더하기)
    public void add(int index, int delta) {
        add(1, 0, n - 1, index, delta);
    }
    
    private void add(int node, int start, int end, int index, int delta) {
        if (start == end) {
            tree[node] += delta;
        } else {
            int mid = (start + end) / 2;
            if (index <= mid) {
                add(2 * node, start, mid, index, delta);
            } else {
                add(2 * node + 1, mid + 1, end, index, delta);
            }
            tree[node] = tree[2 * node] + tree[2 * node + 1];
        }
    }
}
```

### 구간 최소값 Segment Tree
```java
public class MinSegmentTree {
    private int[] tree;
    private int n;
    private static final int INF = Integer.MAX_VALUE;
    
    public MinSegmentTree(int[] arr) {
        this.n = arr.length;
        this.tree = new int[4 * n];
        Arrays.fill(tree, INF);
        build(arr, 1, 0, n - 1);
    }
    
    private void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start];
        } else {
            int mid = (start + end) / 2;
            build(arr, 2 * node, start, mid);
            build(arr, 2 * node + 1, mid + 1, end);
            tree[node] = Math.min(tree[2 * node], tree[2 * node + 1]);
        }
    }
    
    public int query(int left, int right) {
        return query(1, 0, n - 1, left, right);
    }
    
    private int query(int node, int start, int end, int left, int right) {
        if (right < start || left > end) {
            return INF;  // 최소의 항등원
        }
        if (left <= start && end <= right) {
            return tree[node];
        }
        int mid = (start + end) / 2;
        return Math.min(
            query(2 * node, start, mid, left, right),
            query(2 * node + 1, mid + 1, end, left, right)
        );
    }
    
    public void update(int index, int value) {
        update(1, 0, n - 1, index, value);
    }
    
    private void update(int node, int start, int end, int index, int value) {
        if (start == end) {
            tree[node] = value;
        } else {
            int mid = (start + end) / 2;
            if (index <= mid) {
                update(2 * node, start, mid, index, value);
            } else {
                update(2 * node + 1, mid + 1, end, index, value);
            }
            tree[node] = Math.min(tree[2 * node], tree[2 * node + 1]);
        }
    }
}
```

---

## 📝 지연 전파 (Lazy Propagation)
```java
public class LazySegmentTree {
    private long[] tree;
    private long[] lazy;
    private int n;
    
    public LazySegmentTree(int[] arr) {
        this.n = arr.length;
        this.tree = new long[4 * n];
        this.lazy = new long[4 * n];
        build(arr, 1, 0, n - 1);
    }
    
    private void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start];
        } else {
            int mid = (start + end) / 2;
            build(arr, 2 * node, start, mid);
            build(arr, 2 * node + 1, mid + 1, end);
            tree[node] = tree[2 * node] + tree[2 * node + 1];
        }
    }
    
    // 지연 값 전파
    private void pushDown(int node, int start, int end) {
        if (lazy[node] != 0) {
            tree[node] += lazy[node] * (end - start + 1);
            
            if (start != end) {  // 리프가 아니면 자식에게 전파
                lazy[2 * node] += lazy[node];
                lazy[2 * node + 1] += lazy[node];
            }
            
            lazy[node] = 0;
        }
    }
    
    // 구간 업데이트: [left, right]에 value 더하기
    public void updateRange(int left, int right, long value) {
        updateRange(1, 0, n - 1, left, right, value);
    }
    
    private void updateRange(int node, int start, int end, int left, int right, long value) {
        pushDown(node, start, end);
        
        if (right < start || left > end) {
            return;
        }
        
        if (left <= start && end <= right) {
            // 완전 포함: 지연 값 설정
            lazy[node] += value;
            pushDown(node, start, end);
            return;
        }
        
        int mid = (start + end) / 2;
        updateRange(2 * node, start, mid, left, right, value);
        updateRange(2 * node + 1, mid + 1, end, left, right, value);
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }
    
    // 구간 쿼리
    public long query(int left, int right) {
        return query(1, 0, n - 1, left, right);
    }
    
    private long query(int node, int start, int end, int left, int right) {
        pushDown(node, start, end);
        
        if (right < start || left > end) {
            return 0;
        }
        
        if (left <= start && end <= right) {
            return tree[node];
        }
        
        int mid = (start + end) / 2;
        return query(2 * node, start, mid, left, right) +
               query(2 * node + 1, mid + 1, end, left, right);
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 공간복잡도 |
|------|-----------|-----------|
| build | O(n) | O(n) |
| query | O(log n) | O(log n) 스택 |
| update (점) | O(log n) | O(log n) 스택 |
| updateRange (lazy) | O(log n) | O(log n) 스택 |

### 왜 O(log n)?
```
트리 높이: log₂(n)
쿼리 시 각 레벨에서 최대 4개 노드만 방문
→ 4 × log(n) = O(log n)
```

---

## ❌ 흔한 실수

### 1. 트리 크기 부족
```java
// 잘못됨: 크기 부족
int[] tree = new int[2 * n];  // 불완전 이진 트리면 부족!

// 올바름: 여유 있게
int[] tree = new int[4 * n];
```

### 2. 인덱스 혼동
```java
// 1-indexed vs 0-indexed 혼용 주의

// 1-indexed (권장)
왼쪽 자식: 2 * node
오른쪽 자식: 2 * node + 1

// 0-indexed
왼쪽 자식: 2 * node + 1
오른쪽 자식: 2 * node + 2
```

### 3. 지연 전파 누락
```java
// 잘못됨: pushDown 호출 안 함
private long query(...) {
    // pushDown(node, start, end);  // 누락!
    if (right < start || left > end) return 0;
    ...
}

// 올바름: 항상 먼저 전파
private long query(...) {
    pushDown(node, start, end);
    if (right < start || left > end) return 0;
    ...
}
```

### 4. 구간 경계 오류
```java
// 잘못됨: mid 계산 오류
int mid = (start + end) / 2;
query(..., start, mid - 1, ...);  // mid 포함 안 됨!
query(..., mid, end, ...);

// 올바름
int mid = (start + end) / 2;
query(..., start, mid, ...);      // [start, mid]
query(..., mid + 1, end, ...);    // [mid+1, end]
```

---

## 🔗 관련 문제

- LeetCode 307: Range Sum Query - Mutable
- LeetCode 315: Count of Smaller Numbers After Self
- LeetCode 327: Count of Range Sum
- BOJ 2042: 구간 합 구하기
- BOJ 10868: 최솟값
- BOJ 11505: 구간 곱 구하기
