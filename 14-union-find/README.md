# 14. 유니온 파인드 (Union-Find / Disjoint Set Union)

## 📋 문제 정의

**서로소 집합(Disjoint Set)**을 효율적으로 관리하는 유니온 파인드를 구현하세요.

유니온 파인드는 원소들을 서로 겹치지 않는 집합들로 분할하고,
두 집합을 합치거나(Union) 특정 원소가 어느 집합에 속하는지 찾는(Find) 연산을 제공합니다.

---

## 🎯 학습 목표

- 서로소 집합(Disjoint Set) 개념
- 경로 압축(Path Compression) 최적화
- 랭크/크기 기반 합치기(Union by Rank/Size)
- 거의 상수 시간 복잡도 O(α(n))
- 그래프 연결성 판단

---

## 📝 요구사항

### 기본 연산

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `makeSet(x)` | x를 포함하는 새 집합 생성 | O(1) |
| `find(x)` | x가 속한 집합의 대표 원소 반환 | O(α(n)) |
| `union(x, y)` | x와 y가 속한 집합을 합침 | O(α(n)) |
| `connected(x, y)` | x와 y가 같은 집합인지 확인 | O(α(n)) |

### 추가 연산

| 메서드 | 설명 |
|--------|------|
| `getSize(x)` | x가 속한 집합의 크기 |
| `getSetCount()` | 전체 집합의 개수 |
| `getComponents()` | 모든 집합 목록 반환 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
UnionFind uf = new UnionFind(10);  // 0~9 원소

// 초기: 각 원소가 자신만의 집합
System.out.println(uf.find(0));     // 0
System.out.println(uf.find(1));     // 1
System.out.println(uf.connected(0, 1)); // false

// 합치기
uf.union(0, 1);
System.out.println(uf.connected(0, 1)); // true

uf.union(2, 3);
uf.union(0, 2);
System.out.println(uf.connected(1, 3)); // true (0-1-2-3 연결)
```

### 예제 2: 집합 크기
```java
UnionFind uf = new UnionFind(6);

uf.union(0, 1);
uf.union(1, 2);
System.out.println(uf.getSize(0)); // 3 (0, 1, 2)

uf.union(3, 4);
System.out.println(uf.getSize(3)); // 2 (3, 4)

uf.union(0, 3);
System.out.println(uf.getSize(0)); // 5 (0, 1, 2, 3, 4)
System.out.println(uf.getSetCount()); // 2 ({0,1,2,3,4}, {5})
```

### 예제 3: 트리 구조 시각화
```
초기 상태:
[0] [1] [2] [3] [4]   (5개 집합)

union(0, 1):
  0
  |
  1
[0,1] [2] [3] [4]     (4개 집합)

union(2, 3):
  0     2
  |     |
  1     3
[0,1] [2,3] [4]       (3개 집합)

union(1, 3):  (경로 압축 후)
    0
  / | \
 1  2  3
[0,1,2,3] [4]         (2개 집합)
```

### 예제 4: 그래프 연결 컴포넌트
```java
// 그래프 간선: (0,1), (1,2), (3,4)
UnionFind uf = new UnionFind(5);

int[][] edges = {{0,1}, {1,2}, {3,4}};
for (int[] edge : edges) {
    uf.union(edge[0], edge[1]);
}

System.out.println(uf.getSetCount()); // 2 (두 개의 연결 컴포넌트)
System.out.println(uf.connected(0, 2)); // true
System.out.println(uf.connected(0, 3)); // false
```

---

## 🔍 핵심 개념

### 경로 압축 (Path Compression)
```
find(4) 호출 시:

압축 전:           압축 후:
    0                 0
    |               / | \
    1              1  2  4
    |              |
    2              3
    |
    3
    |
    4

→ 다음 find(4)는 O(1)
```

### Union by Rank/Size
```
작은 트리를 큰 트리 아래에 붙이기:

    0        3         0
   /|\       |    →   /|\\ 
  1 2        4       1 2 3
                         |
                         4

→ 트리 높이를 최소화
```

### 역 아커만 함수 α(n)
```
α(n) ≤ 4 for all practical n (n < 10^600)

α(n)은 사실상 상수:
- α(1) = 0
- α(2) = 1
- α(4) = 2
- α(16) = 3
- α(65536) = 4
- α(2^65536) = 5
```

---

## 💡 힌트

### 기본 구조
```java
public class UnionFind {
    private int[] parent;
    private int[] rank;  // 또는 size
    private int count;   // 집합 개수
    
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        count = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;  // 자기 자신이 부모
            rank[i] = 0;    // 또는 size[i] = 1
        }
    }
}
```

### Find (경로 압축)
```java
public int find(int x) {
    if (parent[x] != x) {
        parent[x] = find(parent[x]);  // 경로 압축
    }
    return parent[x];
}

// 반복문 버전
public int find(int x) {
    int root = x;
    while (parent[root] != root) {
        root = parent[root];
    }
    // 경로 압축
    while (parent[x] != root) {
        int next = parent[x];
        parent[x] = root;
        x = next;
    }
    return root;
}
```

### Union (랭크 기반)
```java
public void union(int x, int y) {
    int rootX = find(x);
    int rootY = find(y);
    
    if (rootX == rootY) return;
    
    // 랭크 기반 합치기
    if (rank[rootX] < rank[rootY]) {
        parent[rootX] = rootY;
    } else if (rank[rootX] > rank[rootY]) {
        parent[rootY] = rootX;
    } else {
        parent[rootY] = rootX;
        rank[rootX]++;
    }
    
    count--;
}
```

---

## ✅ 체크리스트

- [ ] 기본 find, union 구현
- [ ] 경로 압축 구현
- [ ] Union by Rank 구현
- [ ] Union by Size 구현
- [ ] 집합 크기 조회
- [ ] 집합 개수 조회
- [ ] 제네릭 버전 (선택)

---

## 📚 활용 예시

- **Kruskal's MST 알고리즘**: 사이클 탐지
- **네트워크 연결**: 컴퓨터 네트워크 연결성
- **동적 연결성**: 온라인 쿼리 처리
- **이미지 세그멘테이션**: 연결된 픽셀 그룹화
- **소셜 네트워크**: 친구 그룹 찾기
