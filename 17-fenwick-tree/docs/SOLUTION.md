# 펜윅 트리 풀이 해설

## 📌 핵심 아이디어

펜윅 트리는 **비트 조작**을 활용하여 효율적인 prefix sum을 계산합니다.
각 인덱스가 담당하는 범위가 LSB(Lowest Set Bit)로 결정됩니다.

**핵심 통찰**:
- tree[i]는 (i - LSB(i) + 1) ~ i 범위의 합을 저장
- LSB(i) = i & (-i)

---

## 🔑 핵심 개념

### 1. 인덱스별 담당 범위
```
n = 16일 때:

tree[1]  = arr[1]                  (1개)
tree[2]  = arr[1..2]               (2개)
tree[3]  = arr[3]                  (1개)
tree[4]  = arr[1..4]               (4개)
tree[5]  = arr[5]                  (1개)
tree[6]  = arr[5..6]               (2개)
tree[7]  = arr[7]                  (1개)
tree[8]  = arr[1..8]               (8개)
tree[9]  = arr[9]                  (1개)
tree[10] = arr[9..10]              (2개)
tree[11] = arr[11]                 (1개)
tree[12] = arr[9..12]              (4개)
tree[13] = arr[13]                 (1개)
tree[14] = arr[13..14]             (2개)
tree[15] = arr[15]                 (1개)
tree[16] = arr[1..16]              (16개)

패턴: tree[i]가 담당하는 원소 수 = LSB(i)
```

### 2. 쿼리 동작
```
query(13) - prefix sum [1, 13]:

13 = 1101 → tree[13] (범위 [13,13])
12 = 1100 → tree[12] (범위 [9,12])
8  = 1000 → tree[8]  (범위 [1,8])
0  = 종료

결과: tree[13] + tree[12] + tree[8]
    = arr[13] + arr[9..12] + arr[1..8]
    = arr[1..13]
```

### 3. 업데이트 동작
```
update(5, delta) - 5를 포함하는 모든 구간 갱신:

5  = 0101 → tree[5] += delta
6  = 0110 → tree[6] += delta
8  = 1000 → tree[8] += delta
16 = ...  → tree[16] += delta (n 이하일 때)
```

---

## 📝 POP 구현 해설

### 기본 구현
```java
public class FenwickTree {
    private long[] tree;
    private int n;
    
    public FenwickTree(int n) {
        this.n = n;
        this.tree = new long[n + 1];  // 1-indexed
    }
    
    // 배열로 초기화
    public FenwickTree(int[] arr) {
        this.n = arr.length - 1;  // arr[0] 미사용 가정
        this.tree = new long[n + 1];
        
        // 방법 1: 각 원소에 대해 update (O(n log n))
        for (int i = 1; i <= n; i++) {
            update(i, arr[i]);
        }
        
        // 방법 2: O(n) 빌드 (아래 참고)
    }
    
    // LSB (Lowest Set Bit)
    private int lsb(int i) {
        return i & (-i);
    }
    
    // 점 업데이트: arr[i] += delta
    public void update(int i, long delta) {
        while (i <= n) {
            tree[i] += delta;
            i += lsb(i);
        }
    }
    
    // Prefix Sum: arr[1] + arr[2] + ... + arr[i]
    public long query(int i) {
        long sum = 0;
        while (i > 0) {
            sum += tree[i];
            i -= lsb(i);
        }
        return sum;
    }
    
    // 구간 합: arr[l] + arr[l+1] + ... + arr[r]
    public long rangeQuery(int l, int r) {
        return query(r) - query(l - 1);
    }
    
    // i번째 원소 값 조회
    public long get(int i) {
        return rangeQuery(i, i);
    }
    
    // i번째 원소를 value로 설정
    public void set(int i, long value) {
        long current = get(i);
        update(i, value - current);
    }
}
```

### O(n) 빌드
```java
// O(n) 시간에 트리 구축
public void build(int[] arr) {
    // 먼저 그대로 복사
    for (int i = 1; i <= n; i++) {
        tree[i] = arr[i];
    }
    
    // 부모 노드에 값 전파
    for (int i = 1; i <= n; i++) {
        int parent = i + lsb(i);
        if (parent <= n) {
            tree[parent] += tree[i];
        }
    }
}
```

### findKth (k번째 원소 찾기)
```java
// 누적 합이 k 이상인 최소 인덱스
// (모든 원소가 양수일 때만 동작)
public int findKth(long k) {
    int pos = 0;
    int logN = (int) (Math.log(n) / Math.log(2));
    
    for (int i = logN; i >= 0; i--) {
        int next = pos + (1 << i);
        if (next <= n && tree[next] < k) {
            pos = next;
            k -= tree[next];
        }
    }
    
    return pos + 1;
}
```

---

## 📝 2D 펜윅 트리
```java
public class FenwickTree2D {
    private long[][] tree;
    private int rows, cols;
    
    public FenwickTree2D(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.tree = new long[rows + 1][cols + 1];
    }
    
    public void update(int r, int c, long delta) {
        for (int i = r; i <= rows; i += i & (-i)) {
            for (int j = c; j <= cols; j += j & (-j)) {
                tree[i][j] += delta;
            }
        }
    }
    
    public long query(int r, int c) {
        long sum = 0;
        for (int i = r; i > 0; i -= i & (-i)) {
            for (int j = c; j > 0; j -= j & (-j)) {
                sum += tree[i][j];
            }
        }
        return sum;
    }
    
    // (r1, c1) ~ (r2, c2) 영역 합
    public long rangeQuery(int r1, int c1, int r2, int c2) {
        return query(r2, c2) 
             - query(r1 - 1, c2) 
             - query(r2, c1 - 1) 
             + query(r1 - 1, c1 - 1);
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 공간복잡도 |
|------|-----------|-----------|
| build | O(n) 또는 O(n log n) | O(n) |
| update | O(log n) | O(1) |
| query | O(log n) | O(1) |
| rangeQuery | O(log n) | O(1) |
| get | O(log n) | O(1) |
| set | O(log n) | O(1) |

### 세그먼트 트리 vs 펜윅 트리

| 특성 | 세그먼트 트리 | 펜윅 트리 |
|------|--------------|----------|
| 공간 | O(4n) | O(n) |
| 구현 | 복잡 | 간단 |
| 점 쿼리 | O(1) | O(log n) |
| 구간 업데이트 | 가능 (Lazy) | 제한적 |
| 다양한 연산 | 가능 | 합계만 |

---

## ❌ 흔한 실수

### 1. 0-indexed vs 1-indexed
```java
// 잘못됨: 0부터 시작
for (int i = 0; i < n; i++) {
    update(i, arr[i]);  // 무한 루프!
}

// 올바름: 1부터 시작
for (int i = 1; i <= n; i++) {
    update(i, arr[i]);
}
```

### 2. LSB 계산
```java
// 잘못됨
int lsb = i & (i - 1);  // 이건 LSB를 제거함!

// 올바름
int lsb = i & (-i);
```

### 3. 경계 조건
```java
// 잘못됨: l이 1보다 작을 수 있음
public long rangeQuery(int l, int r) {
    return query(r) - query(l - 1);  // l=0이면 문제!
}

// 올바름: 경계 체크
public long rangeQuery(int l, int r) {
    if (l < 1) l = 1;
    if (r > n) r = n;
    if (l > r) return 0;
    return query(r) - query(l - 1);
}
```

---

## 🔗 관련 문제

- LeetCode 307: Range Sum Query - Mutable
- LeetCode 315: Count of Smaller Numbers After Self
- LeetCode 493: Reverse Pairs
- BOJ 2042: 구간 합 구하기
- BOJ 11438: 역수의 개수
