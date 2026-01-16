# 그래프 풀이 해설

## 📌 핵심 아이디어

그래프는 **정점(Vertex)**과 **간선(Edge)**으로 구성된 자료구조로,
다양한 관계와 연결을 모델링합니다.

---

## 🔑 핵심 개념

### 1. 그래프 표현
```
그래프:
  0 --- 1
  |     |
  2 --- 3

인접 리스트:
  0: [1, 2]
  1: [0, 3]
  2: [0, 3]
  3: [1, 2]

인접 행렬:
     0  1  2  3
  0 [0, 1, 1, 0]
  1 [1, 0, 0, 1]
  2 [1, 0, 0, 1]
  3 [0, 1, 1, 0]
```

### 2. BFS (너비 우선 탐색)
```
시작: 0

레벨 0: [0]
레벨 1: [1, 2]
레벨 2: [3]

방문 순서: 0 → 1 → 2 → 3

특징: 최단 경로 보장 (비가중치)
```

### 3. DFS (깊이 우선 탐색)
```
시작: 0

스택: [0]
방문: 0 → 1 → 3 → 2

특징: 백트래킹, 사이클 탐지에 유용
```

### 4. 다익스트라 알고리즘
```
시작: A

     4
  A ---→ B
  |       ↘
1 ↓   2    1
  |       ↙
  C ---→ D
     3

거리 갱신:
  A: 0
  C: 1 (A→C)
  B: 3 (A→C→B가 아니라 직접 A→B=4보다 A→C→B=1+2=3이 짧음)
  D: 4 (A→C→D)
```

---

## 📝 POP 구현 해설
```java
public class Graph {
    private Map<Integer, List<Integer>> adjList;
    private boolean directed;
    
    public Graph(boolean directed) {
        this.adjList = new HashMap<>();
        this.directed = directed;
    }
    
    public void addVertex(int v) {
        adjList.putIfAbsent(v, new ArrayList<>());
    }
    
    public void addEdge(int u, int v) {
        addVertex(u);
        addVertex(v);
        adjList.get(u).add(v);
        if (!directed) {
            adjList.get(v).add(u);
        }
    }
    
    public List<Integer> getNeighbors(int v) {
        return adjList.getOrDefault(v, Collections.emptyList());
    }
    
    // BFS
    public List<Integer> bfs(int start) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        
        queue.offer(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            int v = queue.poll();
            result.add(v);
            
            for (int neighbor : getNeighbors(v)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        
        return result;
    }
    
    // DFS (재귀)
    public List<Integer> dfs(int start) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        dfsHelper(start, visited, result);
        return result;
    }
    
    private void dfsHelper(int v, Set<Integer> visited, List<Integer> result) {
        visited.add(v);
        result.add(v);
        
        for (int neighbor : getNeighbors(v)) {
            if (!visited.contains(neighbor)) {
                dfsHelper(neighbor, visited, result);
            }
        }
    }
    
    // DFS (반복)
    public List<Integer> dfsIterative(int start) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();
        
        stack.push(start);
        
        while (!stack.isEmpty()) {
            int v = stack.pop();
            
            if (!visited.contains(v)) {
                visited.add(v);
                result.add(v);
                
                // 역순으로 추가 (순서 유지)
                List<Integer> neighbors = getNeighbors(v);
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    if (!visited.contains(neighbors.get(i))) {
                        stack.push(neighbors.get(i));
                    }
                }
            }
        }
        
        return result;
    }
    
    // 최단 경로 (비가중치 - BFS)
    public List<Integer> shortestPath(int start, int end) {
        if (start == end) return List.of(start);
        
        Map<Integer, Integer> parent = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        
        queue.offer(start);
        parent.put(start, null);
        
        while (!queue.isEmpty()) {
            int v = queue.poll();
            
            for (int neighbor : getNeighbors(v)) {
                if (!parent.containsKey(neighbor)) {
                    parent.put(neighbor, v);
                    
                    if (neighbor == end) {
                        return reconstructPath(parent, start, end);
                    }
                    
                    queue.offer(neighbor);
                }
            }
        }
        
        return Collections.emptyList();  // 경로 없음
    }
    
    private List<Integer> reconstructPath(Map<Integer, Integer> parent, int start, int end) {
        List<Integer> path = new ArrayList<>();
        Integer current = end;
        
        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }
        
        Collections.reverse(path);
        return path;
    }
    
    // 사이클 탐지 (방향 그래프)
    public boolean hasCycle() {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> recursionStack = new HashSet<>();
        
        for (int v : adjList.keySet()) {
            if (hasCycleDFS(v, visited, recursionStack)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean hasCycleDFS(int v, Set<Integer> visited, Set<Integer> recStack) {
        if (recStack.contains(v)) return true;  // 사이클!
        if (visited.contains(v)) return false;
        
        visited.add(v);
        recStack.add(v);
        
        for (int neighbor : getNeighbors(v)) {
            if (hasCycleDFS(neighbor, visited, recStack)) {
                return true;
            }
        }
        
        recStack.remove(v);
        return false;
    }
    
    // 위상 정렬 (Kahn's Algorithm)
    public List<Integer> topologicalSort() {
        if (!directed) throw new IllegalStateException("Only for directed graphs");
        
        Map<Integer, Integer> inDegree = new HashMap<>();
        for (int v : adjList.keySet()) {
            inDegree.put(v, 0);
        }
        
        for (int v : adjList.keySet()) {
            for (int neighbor : adjList.get(v)) {
                inDegree.merge(neighbor, 1, Integer::sum);
            }
        }
        
        Queue<Integer> queue = new LinkedList<>();
        for (int v : inDegree.keySet()) {
            if (inDegree.get(v) == 0) {
                queue.offer(v);
            }
        }
        
        List<Integer> result = new ArrayList<>();
        
        while (!queue.isEmpty()) {
            int v = queue.poll();
            result.add(v);
            
            for (int neighbor : getNeighbors(v)) {
                inDegree.merge(neighbor, -1, Integer::sum);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        
        if (result.size() != adjList.size()) {
            throw new IllegalStateException("Graph has a cycle");
        }
        
        return result;
    }
}
```

---

## 📝 가중치 그래프 & 다익스트라
```java
public class WeightedGraph {
    private Map<Integer, List<int[]>> adjList;  // [neighbor, weight]
    
    public WeightedGraph() {
        this.adjList = new HashMap<>();
    }
    
    public void addEdge(int u, int v, int weight) {
        adjList.computeIfAbsent(u, k -> new ArrayList<>()).add(new int[]{v, weight});
        adjList.computeIfAbsent(v, k -> new ArrayList<>()).add(new int[]{u, weight});
    }
    
    // 다익스트라 알고리즘
    public Map<Integer, Integer> dijkstra(int start) {
        Map<Integer, Integer> distances = new HashMap<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        
        for (int v : adjList.keySet()) {
            distances.put(v, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.offer(new int[]{start, 0});
        
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int v = current[0];
            int dist = current[1];
            
            if (dist > distances.get(v)) continue;  // 이미 처리됨
            
            for (int[] edge : adjList.getOrDefault(v, Collections.emptyList())) {
                int neighbor = edge[0];
                int weight = edge[1];
                int newDist = dist + weight;
                
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    pq.offer(new int[]{neighbor, newDist});
                }
            }
        }
        
        return distances;
    }
    
    // 다익스트라 + 경로 추적
    public List<Integer> dijkstraPath(int start, int end) {
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        
        for (int v : adjList.keySet()) {
            distances.put(v, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.offer(new int[]{start, 0});
        
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int v = current[0];
            int dist = current[1];
            
            if (v == end) break;
            if (dist > distances.get(v)) continue;
            
            for (int[] edge : adjList.getOrDefault(v, Collections.emptyList())) {
                int neighbor = edge[0];
                int weight = edge[1];
                int newDist = dist + weight;
                
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    parent.put(neighbor, v);
                    pq.offer(new int[]{neighbor, newDist});
                }
            }
        }
        
        // 경로 복원
        List<Integer> path = new ArrayList<>();
        Integer current = end;
        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }
        Collections.reverse(path);
        return path.get(0) == start ? path : Collections.emptyList();
    }
}
```

---

## 📝 MST (최소 신장 트리)
```java
// 크루스칼 알고리즘 (Union-Find 사용)
public List<int[]> kruskal() {
    List<int[]> edges = new ArrayList<>();  // [u, v, weight]
    // 모든 간선 수집...
    
    // 가중치 순 정렬
    edges.sort((a, b) -> a[2] - b[2]);
    
    UnionFind uf = new UnionFind(vertexCount);
    List<int[]> mst = new ArrayList<>();
    
    for (int[] edge : edges) {
        int u = edge[0], v = edge[1], weight = edge[2];
        
        if (uf.find(u) != uf.find(v)) {
            uf.union(u, v);
            mst.add(edge);
        }
    }
    
    return mst;
}
```

---

## ⏱️ 복잡도 분석

| 알고리즘 | 시간복잡도 | 공간복잡도 |
|---------|-----------|-----------|
| BFS/DFS | O(V + E) | O(V) |
| 다익스트라 (힙) | O((V+E) log V) | O(V) |
| 벨만-포드 | O(V * E) | O(V) |
| 플로이드-워셜 | O(V³) | O(V²) |
| 위상 정렬 | O(V + E) | O(V) |
| 프림 (힙) | O((V+E) log V) | O(V) |
| 크루스칼 | O(E log E) | O(V) |

### 그래프 표현 비교

|  | 인접 리스트 | 인접 행렬 |
|--|-----------|----------|
| 공간 | O(V + E) | O(V²) |
| 간선 확인 | O(degree) | O(1) |
| 모든 인접 정점 | O(degree) | O(V) |
| 적합한 경우 | 희소 그래프 | 밀집 그래프 |

---

## ❌ 흔한 실수

### 1. 방문 체크 시점
```java
// 잘못됨: poll 후 체크 (중복 처리)
while (!queue.isEmpty()) {
    int v = queue.poll();
    if (visited.contains(v)) continue;  // 이미 큐에 여러 번 들어감
    visited.add(v);
}

// 올바름: 큐에 넣을 때 체크
if (!visited.contains(neighbor)) {
    visited.add(neighbor);  // 여기서!
    queue.offer(neighbor);
}
```

### 2. 방향 그래프에서 무방향 처리
```java
// 무방향 그래프: 양쪽 추가
adjList.get(u).add(v);
adjList.get(v).add(u);

// 방향 그래프: 한쪽만
adjList.get(u).add(v);
```

### 3. 연결되지 않은 그래프 처리
```java
// 모든 정점에서 시작해야 전체 탐색
for (int v : adjList.keySet()) {
    if (!visited.contains(v)) {
        dfs(v, visited);
    }
}
```

---

## 🔗 관련 문제

- LeetCode 200: Number of Islands
- LeetCode 207: Course Schedule (위상 정렬)
- LeetCode 133: Clone Graph
- LeetCode 743: Network Delay Time (다익스트라)
- LeetCode 785: Is Graph Bipartite?
- LeetCode 210: Course Schedule II
- LeetCode 994: Rotting Oranges (BFS)
