# 유니온 파인드 구현에 유용한 Java API

## 📦 배열 초기화

### Arrays 클래스
```java
import java.util.Arrays;

// 배열 초기화
int[] parent = new int[n];
Arrays.fill(parent, -1);  // 모두 -1로

// 연속 값으로 초기화 (0, 1, 2, ...)
for (int i = 0; i < n; i++) {
    parent[i] = i;
}

// Java 8+ Stream 사용
int[] parent = IntStream.range(0, n).toArray();

// 배열 복사
int[] copy = Arrays.copyOf(parent, parent.length);

// 디버깅 출력
System.out.println(Arrays.toString(parent));
```

### 2차원 배열 (격자 문제용)
```java
// 2D 좌표를 1D 인덱스로
int rows = 5, cols = 5;
int index = row * cols + col;

// 1D 인덱스를 2D 좌표로
int row = index / cols;
int col = index % cols;

// 4방향 이동
int[] dx = {0, 0, 1, -1};
int[] dy = {1, -1, 0, 0};

for (int d = 0; d < 4; d++) {
    int nx = x + dx[d];
    int ny = y + dy[d];
    if (nx >= 0 && nx < rows && ny >= 0 && ny < cols) {
        // 유효한 좌표
    }
}
```

---

## 🔢 정수 연산

### Integer 클래스
```java
// 범위 확인
Integer.MAX_VALUE;  // 2147483647
Integer.MIN_VALUE;  // -2147483648

// 비트 연산 (해시용)
Integer.hashCode(x);
Integer.compare(a, b);  // a < b: -1, a == b: 0, a > b: 1

// 문자열 변환
Integer.toString(x);
Integer.parseInt("123");
```

### Math 클래스
```java
// 최대/최소
Math.max(a, b);
Math.min(a, b);

// 절대값
Math.abs(x);

// 로그 (높이 분석용)
Math.log(n);                    // 자연로그
Math.log(n) / Math.log(2);      // log₂(n)
(int)(Math.log(n) / Math.log(2)); // 정수 버전
```

---

## 📊 컬렉션

### Map (원소 → 인덱스 매핑)
```java
import java.util.HashMap;
import java.util.Map;

// 문자열 원소를 인덱스로 매핑
Map<String, Integer> indexMap = new HashMap<>();

public int getIndex(String element) {
    return indexMap.computeIfAbsent(element, 
        k -> indexMap.size());
}

// 사용
int idx1 = getIndex("Alice");  // 0
int idx2 = getIndex("Bob");    // 1
int idx3 = getIndex("Alice");  // 0 (이미 존재)
```

### List (결과 수집)
```java
import java.util.ArrayList;
import java.util.List;

// 각 집합의 원소들
public List<List<Integer>> getComponents() {
    Map<Integer, List<Integer>> groups = new HashMap<>();
    
    for (int i = 0; i < parent.length; i++) {
        int root = find(i);
        groups.computeIfAbsent(root, k -> new ArrayList<>())
              .add(i);
    }
    
    return new ArrayList<>(groups.values());
}
```

### Set (중복 제거)
```java
import java.util.HashSet;
import java.util.Set;

// 유일한 루트 개수
public int getSetCount() {
    Set<Integer> roots = new HashSet<>();
    for (int i = 0; i < parent.length; i++) {
        roots.add(find(i));
    }
    return roots.size();
}
```

---

## 🎯 제네릭 유니온 파인드

### 제네릭 버전
```java
import java.util.HashMap;
import java.util.Map;

public class GenericUnionFind<T> {
    private Map<T, T> parent = new HashMap<>();
    private Map<T, Integer> rank = new HashMap<>();
    
    public void makeSet(T x) {
        if (!parent.containsKey(x)) {
            parent.put(x, x);
            rank.put(x, 0);
        }
    }
    
    public T find(T x) {
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent.get(x)));
        }
        return parent.get(x);
    }
    
    public void union(T x, T y) {
        makeSet(x);
        makeSet(y);
        
        T rootX = find(x);
        T rootY = find(y);
        
        if (rootX.equals(rootY)) return;
        
        if (rank.get(rootX) < rank.get(rootY)) {
            parent.put(rootX, rootY);
        } else if (rank.get(rootX) > rank.get(rootY)) {
            parent.put(rootY, rootX);
        } else {
            parent.put(rootY, rootX);
            rank.merge(rootX, 1, Integer::sum);
        }
    }
    
    public boolean connected(T x, T y) {
        if (!parent.containsKey(x) || !parent.containsKey(y)) {
            return false;
        }
        return find(x).equals(find(y));
    }
}
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldUnionAndFind() {
    UnionFind uf = new UnionFind(5);
    
    assertThat(uf.connected(0, 1)).isFalse();
    
    uf.union(0, 1);
    assertThat(uf.connected(0, 1)).isTrue();
    assertThat(uf.find(0)).isEqualTo(uf.find(1));
}

@Test
void shouldTrackSetCount() {
    UnionFind uf = new UnionFind(5);
    
    assertThat(uf.getSetCount()).isEqualTo(5);
    
    uf.union(0, 1);
    uf.union(2, 3);
    assertThat(uf.getSetCount()).isEqualTo(3);
    
    uf.union(1, 2);
    assertThat(uf.getSetCount()).isEqualTo(2);
}

@Test
void shouldTrackSetSize() {
    UnionFind uf = new UnionFind(5);
    
    uf.union(0, 1);
    uf.union(1, 2);
    
    assertThat(uf.getSize(0)).isEqualTo(3);
    assertThat(uf.getSize(1)).isEqualTo(3);
    assertThat(uf.getSize(2)).isEqualTo(3);
    assertThat(uf.getSize(3)).isEqualTo(1);
}
```

### 대용량 테스트
```java
@Test
void shouldHandleLargeInput() {
    int n = 100000;
    UnionFind uf = new UnionFind(n);
    
    // 모두 연결
    for (int i = 0; i < n - 1; i++) {
        uf.union(i, i + 1);
    }
    
    assertThat(uf.getSetCount()).isEqualTo(1);
    assertThat(uf.connected(0, n - 1)).isTrue();
}
```

---

## 📚 Java 21 관련

### Record로 결과 표현
```java
public record UnionResult(boolean success, int newRoot, int setSize) {}

public UnionResult unionWithInfo(int x, int y) {
    int rootX = find(x);
    int rootY = find(y);
    
    if (rootX == rootY) {
        return new UnionResult(false, rootX, size[rootX]);
    }
    
    // union 로직...
    int newRoot = /* 새 루트 */;
    return new UnionResult(true, newRoot, size[newRoot]);
}
```

### Pattern Matching
```java
public void processQuery(Query query) {
    switch (query) {
        case UnionQuery(int x, int y) -> union(x, y);
        case FindQuery(int x) -> System.out.println(find(x));
        case ConnectedQuery(int x, int y) -> 
            System.out.println(connected(x, y));
    }
}

sealed interface Query permits UnionQuery, FindQuery, ConnectedQuery {}
record UnionQuery(int x, int y) implements Query {}
record FindQuery(int x) implements Query {}
record ConnectedQuery(int x, int y) implements Query {}
```

---

## ⚡ 성능 팁

### 1. Half Path Compression (2단계 압축)
```java
// 완전한 경로 압축보다 간단하지만 효과적
public int find(int x) {
    while (parent[x] != x) {
        parent[x] = parent[parent[x]];  // 할아버지로 연결
        x = parent[x];
    }
    return x;
}
```

### 2. 배열 직접 접근
```java
// Map 대신 배열 사용 (인덱스가 정수일 때)
// 훨씬 빠름!

// 느림
Map<Integer, Integer> parent = new HashMap<>();

// 빠름
int[] parent = new int[n];
```

### 3. 초기화 최적화
```java
// -1을 자기 자신의 부모로 사용
// size를 음수로 저장하여 배열 하나로 관리

private int[] parent;  // parent[i] < 0이면 루트, |parent[i]| = size

public UnionFind(int n) {
    parent = new int[n];
    Arrays.fill(parent, -1);  // 모두 루트, 크기 1
}

public int find(int x) {
    if (parent[x] < 0) return x;
    return parent[x] = find(parent[x]);
}

public boolean union(int x, int y) {
    int rootX = find(x);
    int rootY = find(y);
    if (rootX == rootY) return false;
    
    // 크기 기반 (음수 절대값이 큰 쪽이 큼)
    if (parent[rootX] > parent[rootY]) {
        int temp = rootX; rootX = rootY; rootY = temp;
    }
    parent[rootX] += parent[rootY];  // 크기 합산
    parent[rootY] = rootX;
    return true;
}

public int getSize(int x) {
    return -parent[find(x)];
}
```

---

## 🔀 응용 패턴

### 격자에서 섬 개수
```java
// LeetCode 200: Number of Islands
public int numIslands(char[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    UnionFind uf = new UnionFind(rows * cols);
    
    int water = 0;
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            if (grid[i][j] == '0') {
                water++;
                continue;
            }
            int idx = i * cols + j;
            if (i > 0 && grid[i-1][j] == '1') {
                uf.union(idx, (i-1) * cols + j);
            }
            if (j > 0 && grid[i][j-1] == '1') {
                uf.union(idx, i * cols + (j-1));
            }
        }
    }
    
    return uf.getSetCount() - water;
}
```

### 사이클 탐지 (무방향 그래프)
```java
public boolean hasCycle(int n, int[][] edges) {
    UnionFind uf = new UnionFind(n);
    
    for (int[] edge : edges) {
        if (!uf.union(edge[0], edge[1])) {
            return true;  // 이미 연결됨 = 사이클
        }
    }
    return false;
}
```

### Kruskal's MST
```java
public int kruskal(int n, int[][] edges) {
    // edges: [u, v, weight]
    Arrays.sort(edges, (a, b) -> a[2] - b[2]);
    
    UnionFind uf = new UnionFind(n);
    int totalWeight = 0;
    int edgeCount = 0;
    
    for (int[] edge : edges) {
        if (uf.union(edge[0], edge[1])) {
            totalWeight += edge[2];
            if (++edgeCount == n - 1) break;
        }
    }
    
    return totalWeight;
}
```
