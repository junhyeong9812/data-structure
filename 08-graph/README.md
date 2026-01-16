# 08. 그래프 (Graph)

## 📋 문제 정의

**정점(Vertex)**과 **간선(Edge)**으로 구성된 그래프 자료구조와 핵심 알고리즘을 구현하세요.

그래프는 네트워크, 경로 찾기, 관계 모델링 등 다양한 문제를 해결하는 핵심 자료구조입니다.

---

## 🎯 학습 목표

- 그래프 표현 방식 (인접 행렬 vs 인접 리스트)
- 그래프 순회 (BFS, DFS)
- 최단 경로 알고리즘 (다익스트라, 벨만-포드)
- 최소 신장 트리 (프림, 크루스칼)
- 위상 정렬 (Topological Sort)
- 사이클 탐지

---

## 📝 요구사항

### 그래프 기본 연산

| 메서드 | 설명 | 시간복잡도 (인접 리스트) |
|--------|------|------------------------|
| `addVertex(v)` | 정점 추가 | O(1) |
| `addEdge(u, v)` | 간선 추가 | O(1) |
| `addEdge(u, v, weight)` | 가중치 간선 추가 | O(1) |
| `removeVertex(v)` | 정점 제거 | O(V + E) |
| `removeEdge(u, v)` | 간선 제거 | O(E) |
| `hasVertex(v)` | 정점 존재 확인 | O(1) |
| `hasEdge(u, v)` | 간선 존재 확인 | O(degree) |
| `getNeighbors(v)` | 인접 정점 조회 | O(1) |
| `vertexCount()` | 정점 개수 | O(1) |
| `edgeCount()` | 간선 개수 | O(1) |

### 그래프 순회

| 알고리즘 | 설명 | 시간복잡도 |
|---------|------|-----------|
| `bfs(start)` | 너비 우선 탐색 | O(V + E) |
| `dfs(start)` | 깊이 우선 탐색 | O(V + E) |
| `bfsIterative(start)` | BFS 반복 구현 | O(V + E) |
| `dfsIterative(start)` | DFS 반복 구현 | O(V + E) |

### 경로 탐색

| 알고리즘 | 설명 | 시간복잡도 |
|---------|------|-----------|
| `shortestPath(start, end)` | 최단 경로 (비가중치) | O(V + E) |
| `dijkstra(start)` | 다익스트라 (양의 가중치) | O((V+E) log V) |
| `bellmanFord(start)` | 벨만-포드 (음의 가중치) | O(V * E) |
| `floydWarshall()` | 모든 쌍 최단 경로 | O(V³) |

### 기타 알고리즘

| 알고리즘 | 설명 | 시간복잡도 |
|---------|------|-----------|
| `hasCycle()` | 사이클 존재 여부 | O(V + E) |
| `topologicalSort()` | 위상 정렬 (DAG) | O(V + E) |
| `connectedComponents()` | 연결 요소 찾기 | O(V + E) |
| `isBipartite()` | 이분 그래프 판별 | O(V + E) |
| `prim()` | 프림 MST | O((V+E) log V) |
| `kruskal()` | 크루스칼 MST | O(E log E) |

---

## 📊 입출력 예시

### 예제 1: 기본 그래프 생성
```java
Graph graph = new Graph();
graph.addVertex(0);
graph.addVertex(1);
graph.addVertex(2);
graph.addEdge(0, 1);
graph.addEdge(1, 2);
graph.addEdge(0, 2);

//   0 --- 1
//    \   /
//     \ /
//      2

System.out.println(graph.hasEdge(0, 1));  // true
System.out.println(graph.getNeighbors(0)); // [1, 2]
```

### 예제 2: BFS/DFS 순회
```java
// 그래프:
//   0 - 1 - 3
//   |   |
//   2 - 4

List<Integer> bfsOrder = graph.bfs(0);
// [0, 1, 2, 3, 4] (레벨 순서)

List<Integer> dfsOrder = graph.dfs(0);
// [0, 1, 3, 4, 2] (깊이 우선)
```

### 예제 3: 최단 경로
```java
// 비가중치 그래프
List<Integer> path = graph.shortestPath(0, 4);
// [0, 1, 4] 또는 [0, 2, 4]

// 가중치 그래프 (다익스트라)
graph.addEdge(0, 1, 4);
graph.addEdge(0, 2, 1);
graph.addEdge(2, 1, 2);

Map<Integer, Integer> distances = graph.dijkstra(0);
// {0: 0, 1: 3, 2: 1}
```

### 예제 4: 위상 정렬
```java
// DAG (방향 비순환 그래프)
//   5 → 0 ← 4
//   ↓   ↓   ↓
//   2 → 3 → 1

DirectedGraph dag = new DirectedGraph();
dag.addEdge(5, 0);
dag.addEdge(5, 2);
dag.addEdge(4, 0);
dag.addEdge(4, 1);
dag.addEdge(2, 3);
dag.addEdge(3, 1);
dag.addEdge(0, 3);

List<Integer> sorted = dag.topologicalSort();
// [5, 4, 2, 0, 3, 1] 또는 [4, 5, 2, 0, 3, 1] 등
```

### 예제 5: 사이클 탐지
```java
DirectedGraph graph = new DirectedGraph();
graph.addEdge(0, 1);
graph.addEdge(1, 2);
graph.addEdge(2, 0);  // 사이클!

System.out.println(graph.hasCycle());  // true
```

---

## 🔍 제약 조건

- 정점은 정수 또는 제네릭 타입
- 자기 루프(self-loop) 허용 여부는 구현에 따라 결정
- 다중 간선(multi-edge) 허용하지 않음
- 가중치는 정수 또는 실수

---

## 💡 힌트

### 인접 리스트 표현
```java
// Map 기반
Map<Integer, List<Integer>> adjList = new HashMap<>();

// 배열 기반 (정점이 0~n-1일 때)
List<Integer>[] adjList = new ArrayList[n];
```

### 인접 행렬 표현
```java
// 정점이 0~n-1일 때
int[][] adjMatrix = new int[n][n];
// adjMatrix[i][j] = 1 (간선 존재) 또는 가중치
```

### BFS 템플릿
```java
Queue<Integer> queue = new LinkedList<>();
Set<Integer> visited = new HashSet<>();
queue.offer(start);
visited.add(start);

while (!queue.isEmpty()) {
    int v = queue.poll();
    for (int neighbor : getNeighbors(v)) {
        if (!visited.contains(neighbor)) {
            visited.add(neighbor);
            queue.offer(neighbor);
        }
    }
}
```

### DFS 템플릿
```java
void dfs(int v, Set<Integer> visited) {
    visited.add(v);
    for (int neighbor : getNeighbors(v)) {
        if (!visited.contains(neighbor)) {
            dfs(neighbor, visited);
        }
    }
}
```

---

## ✅ 체크리스트

- [ ] 인접 리스트 기반 그래프 구현
- [ ] 인접 행렬 기반 그래프 구현
- [ ] 방향 그래프 / 무방향 그래프
- [ ] BFS, DFS 순회
- [ ] 최단 경로 (BFS, 다익스트라)
- [ ] 위상 정렬
- [ ] 사이클 탐지
- [ ] 연결 요소 찾기
- [ ] MST (프림, 크루스칼)

---

## 📚 참고

- [Visualgo - Graph Traversal](https://visualgo.net/en/dfsbfs)
- [Visualgo - Single-Source Shortest Path](https://visualgo.net/en/sssp)
- 그래프 표현 선택 기준 (희소 vs 밀집)
