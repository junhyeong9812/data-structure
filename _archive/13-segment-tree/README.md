# 13. 구간 트리 (Segment Tree)

## 📋 문제 정의

**구간 쿼리**와 **점 업데이트**를 효율적으로 처리하는 구간 트리를 구현하세요.

구간 트리는 배열의 특정 구간에 대한 쿼리(합, 최소, 최대 등)를
O(log n) 시간에 처리할 수 있는 트리 자료구조입니다.

---

## 🎯 학습 목표

- 완전 이진 트리 기반 구간 트리 구조
- 구간 쿼리 알고리즘 (Range Query)
- 점 업데이트 (Point Update)
- 지연 전파 (Lazy Propagation)
- 구간 업데이트 (Range Update)

---

## 📝 요구사항

### 기본 연산 (점 업데이트)

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `build(arr)` | 배열로 트리 구축 | O(n) |
| `query(left, right)` | [left, right] 구간 쿼리 | O(log n) |
| `update(index, value)` | index 위치 값 업데이트 | O(log n) |

### 지연 전파 (Range Update)

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `updateRange(left, right, value)` | 구간 [left, right] 전체에 value 적용 | O(log n) |
| `queryLazy(left, right)` | 지연 전파 적용 후 구간 쿼리 | O(log n) |

### 지원 연산 유형

| 연산 | 설명 |
|------|------|
| Sum | 구간 합 |
| Min | 구간 최소값 |
| Max | 구간 최대값 |
| GCD | 구간 최대공약수 |

---

## 📊 입출력 예시

### 예제 1: 구간 합 쿼리
```java
int[] arr = {1, 3, 5, 7, 9, 11};

SegmentTree tree = new SegmentTree(arr);

// 구간 합 쿼리
System.out.println(tree.query(1, 3));  // 3+5+7 = 15
System.out.println(tree.query(0, 5));  // 1+3+5+7+9+11 = 36

// 점 업데이트
tree.update(1, 10);  // arr[1] = 10

System.out.println(tree.query(1, 3));  // 10+5+7 = 22
```

### 예제 2: 구간 최소값
```java
int[] arr = {5, 2, 8, 1, 9, 3};

MinSegmentTree tree = new MinSegmentTree(arr);

System.out.println(tree.query(0, 3));  // min(5,2,8,1) = 1
System.out.println(tree.query(2, 5));  // min(8,1,9,3) = 1

tree.update(3, 10);  // arr[3] = 10

System.out.println(tree.query(0, 3));  // min(5,2,8,10) = 2
```

### 예제 3: 지연 전파 (구간 업데이트)
```java
int[] arr = {1, 2, 3, 4, 5};

LazySegmentTree tree = new LazySegmentTree(arr);

// 구간 [1, 3]에 10 더하기
tree.updateRange(1, 3, 10);
// arr = {1, 12, 13, 14, 5}

System.out.println(tree.query(0, 4));  // 1+12+13+14+5 = 45
System.out.println(tree.query(1, 3));  // 12+13+14 = 39
```

### 예제 4: 트리 구조 시각화
```
배열: [1, 3, 5, 7, 9, 11]

          [36]           <- 전체 합
         /    \
      [9]      [27]      <- 좌/우 절반
      / \      /  \
    [4] [5]  [16] [11]   <- 4등분
    /\       /\
  [1][3]   [7][9]        <- 개별 원소
```

---

## 🔍 핵심 개념

### 트리 인덱싱
```
배열 기반 트리 (1-indexed):
- 부모: i / 2
- 왼쪽 자식: i * 2
- 오른쪽 자식: i * 2 + 1

배열 기반 트리 (0-indexed):
- 부모: (i - 1) / 2
- 왼쪽 자식: i * 2 + 1
- 오른쪽 자식: i * 2 + 2
```

### 트리 크기
```
n개 원소 → 트리 크기: 4 * n (넉넉하게)
또는 2 * nextPowerOfTwo(n)
```

### 지연 전파
```
구간 업데이트 시 즉시 모든 노드 업데이트 대신,
쿼리가 해당 구간에 접근할 때 지연 값을 전파

lazy[node]: 이 노드의 자식들에게 전파할 값
```

---

## 💡 힌트

### 빌드
```java
void build(int[] arr, int node, int start, int end) {
    if (start == end) {
        tree[node] = arr[start];
    } else {
        int mid = (start + end) / 2;
        build(arr, 2*node, start, mid);
        build(arr, 2*node+1, mid+1, end);
        tree[node] = tree[2*node] + tree[2*node+1];
    }
}
```

### 쿼리
```java
int query(int node, int start, int end, int l, int r) {
    if (r < start || l > end) {
        return 0;  // 범위 밖
    }
    if (l <= start && end <= r) {
        return tree[node];  // 완전 포함
    }
    int mid = (start + end) / 2;
    return query(2*node, start, mid, l, r) +
           query(2*node+1, mid+1, end, l, r);
}
```

### 업데이트
```java
void update(int node, int start, int end, int idx, int val) {
    if (start == end) {
        tree[node] = val;
    } else {
        int mid = (start + end) / 2;
        if (idx <= mid) {
            update(2*node, start, mid, idx, val);
        } else {
            update(2*node+1, mid+1, end, idx, val);
        }
        tree[node] = tree[2*node] + tree[2*node+1];
    }
}
```

---

## ✅ 체크리스트

- [ ] 구간 합 Segment Tree 구현
- [ ] 구간 최소값/최대값 구현
- [ ] 점 업데이트 구현
- [ ] 지연 전파 구현
- [ ] 구간 업데이트 구현
- [ ] 반복문 버전 구현 (선택)
- [ ] 2D Segment Tree (선택)

---

## 📚 참고

- [Visualgo - Segment Tree](https://visualgo.net/en/segmenttree)
- Competitive Programming 필수 자료구조
- 펜윅 트리와의 비교
- 활용: 구간 쿼리가 빈번한 문제
