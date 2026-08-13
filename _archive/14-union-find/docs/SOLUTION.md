# 유니온 파인드 풀이 해설

## 📌 핵심 아이디어

유니온 파인드는 **트리 기반**으로 집합을 표현합니다.
각 집합은 트리이며, 트리의 **루트가 집합의 대표**입니다.

**두 가지 최적화**로 거의 상수 시간을 달성:
1. 경로 압축 (Path Compression)
2. 랭크/크기 기반 합치기 (Union by Rank/Size)

---

## 🔑 핵심 개념

### 1. 트리로 집합 표현
```
집합 {0, 1, 2, 3}:

parent = [0, 0, 0, 2]

트리 구조:
    0 (루트 = 대표)
   /|\
  1 2
    |
    3

find(3) = 0 (루트 반환)
find(1) = 0 (같은 집합!)
```

### 2. 경로 압축
```
find(x) 호출 시 경로의 모든 노드를 루트에 직접 연결

압축 전:        압축 후:
    0              0
    |            / | \
    1           1  2  3
    |
    2
    |
    3

find(3) 후: parent = [0, 0, 0, 0]
```

### 3. 랭크 기반 합치기
```
rank = 트리의 높이 상한

Union(작은 랭크 트리 → 큰 랭크 트리):

rank[A] = 2, rank[B] = 1

    A(r=2)    B(r=1)        A(r=2)
   / \         |      →    / | \
  ...  ...     ...        ... ... B
                                  |
                                 ...

트리 높이 증가 최소화!
```

---

## 📝 POP 구현 해설

### 기본 유니온 파인드
```java
public class UnionFind {
    private int[] parent;
    private int[] rank;
    private int[] size;
    private int count;  // 집합 개수
    
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        size = new int[n];
        count = n;
        
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
            size[i] = 1;
        }
    }
    
    // Find with Path Compression
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);  // 재귀적 경로 압축
        }
        return parent[x];
    }
    
    // Union by Rank
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        
        if (rootX == rootY) {
            return false;  // 이미 같은 집합
        }
        
        // 랭크가 작은 트리를 큰 트리 아래에 붙임
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
            size[rootY] += size[rootX];
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
            size[rootX] += size[rootY];
        } else {
            parent[rootY] = rootX;
            size[rootX] += size[rootY];
            rank[rootX]++;
        }
        
        count--;
        return true;
    }
    
    // 같은 집합인지 확인
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
    
    // x가 속한 집합의 크기
    public int getSize(int x) {
        return size[find(x)];
    }
    
    // 전체 집합 개수
    public int getSetCount() {
        return count;
    }
}
```

### 반복문 버전 Find
```java
public int find(int x) {
    // 루트 찾기
    int root = x;
    while (parent[root] != root) {
        root = parent[root];
    }
    
    // 경로 압축 (방문한 모든 노드를 루트에 연결)
    while (parent[x] != root) {
        int next = parent[x];
        parent[x] = root;
        x = next;
    }
    
    return root;
}
```

### Union by Size (크기 기반)
```java
public boolean union(int x, int y) {
    int rootX = find(x);
    int rootY = find(y);
    
    if (rootX == rootY) {
        return false;
    }
    
    // 작은 트리를 큰 트리에 붙임
    if (size[rootX] < size[rootY]) {
        parent[rootX] = rootY;
        size[rootY] += size[rootX];
    } else {
        parent[rootY] = rootX;
        size[rootX] += size[rootY];
    }
    
    count--;
    return true;
}
```

---

## 📝 Weighted Union-Find (가중치)
```java
public class WeightedUnionFind {
    private int[] parent;
    private double[] weight;  // parent까지의 가중치
    
    public WeightedUnionFind(int n) {
        parent = new int[n];
        weight = new double[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            weight[i] = 0.0;
        }
    }
    
    public int find(int x) {
        if (parent[x] != x) {
            int root = find(parent[x]);
            weight[x] += weight[parent[x]];  // 가중치 누적
            parent[x] = root;
        }
        return parent[x];
    }
    
    // x / y = value 관계 설정
    public void union(int x, int y, double value) {
        int rootX = find(x);
        int rootY = find(y);
        
        if (rootX == rootY) return;
        
        // rootX → rootY 연결
        // weight[rootX] = weight[y] + value - weight[x]
        parent[rootX] = rootY;
        weight[rootX] = weight[y] + value - weight[x];
    }
    
    // x / y 계산
    public double query(int x, int y) {
        if (find(x) != find(y)) {
            return -1.0;  // 연결 안 됨
        }
        return weight[x] - weight[y];
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 최적화 없음 | 경로 압축만 | 랭크만 | 둘 다 |
|------|------------|-----------|-------|------|
| find | O(n) | O(log n) 분할상환 | O(log n) | O(α(n)) |
| union | O(n) | O(log n) 분할상환 | O(log n) | O(α(n)) |

### 역 아커만 함수 α(n)
```
실질적으로 상수:
- 우주의 원자 수 (~10^80)에 대해서도 α(n) ≤ 5

수학적으로:
α(n) = min{k : A(k, k) ≥ n}
A = 아커만 함수

A(1,1) = 3
A(2,2) = 7
A(3,3) = 61
A(4,4) = 2^2^2^65536 - 3  (천문학적)
```

---

## ❌ 흔한 실수

### 1. 경로 압축 누락
```java
// 잘못됨: 단순히 루트만 반환
public int find(int x) {
    while (parent[x] != x) {
        x = parent[x];
    }
    return x;
}

// 올바름: 경로 압축 포함
public int find(int x) {
    if (parent[x] != x) {
        parent[x] = find(parent[x]);
    }
    return parent[x];
}
```

### 2. 크기/랭크 업데이트 위치
```java
// 잘못됨: 루트가 아닌 노드의 size 업데이트
size[x] += size[y];  // x는 루트가 아닐 수 있음!

// 올바름: 항상 루트의 size 업데이트
int rootX = find(x);
int rootY = find(y);
size[rootX] += size[rootY];
```

### 3. 이미 같은 집합인 경우
```java
// 잘못됨: 검사 없이 union
public void union(int x, int y) {
    parent[find(x)] = find(y);
    count--;  // count가 음수가 될 수 있음!
}

// 올바름: 같은 집합이면 조기 반환
public boolean union(int x, int y) {
    int rootX = find(x);
    int rootY = find(y);
    if (rootX == rootY) return false;
    // ...
    count--;
    return true;
}
```

---

## 🔗 관련 문제

- LeetCode 200: Number of Islands
- LeetCode 547: Number of Provinces
- LeetCode 684: Redundant Connection
- LeetCode 721: Accounts Merge
- LeetCode 990: Satisfiability of Equality Equations
- LeetCode 399: Evaluate Division (가중치)
- BOJ 1717: 집합의 표현
- BOJ 1976: 여행 가자
