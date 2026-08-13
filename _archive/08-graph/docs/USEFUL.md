# 그래프 구현에 유용한 Java API

## 📦 컬렉션 자료구조

### Map (인접 리스트용)
```java
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

// 인접 리스트 생성
Map<Integer, List<Integer>> adjList = new HashMap<>();

// 정점 추가
adjList.put(v, new ArrayList<>());
adjList.putIfAbsent(v, new ArrayList<>());

// 간선 추가
adjList.computeIfAbsent(u, k -> new ArrayList<>()).add(v);

// 인접 정점 조회
List<Integer> neighbors = adjList.getOrDefault(v, Collections.emptyList());

// 모든 정점 순회
for (int v : adjList.keySet()) { ... }
for (Map.Entry<Integer, List<Integer>> entry : adjList.entrySet()) { ... }
adjList.forEach((v, neighbors) -> { ... });
```

### List (인접 정점 저장)
```java
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

// 배열 기반 인접 리스트
@SuppressWarnings("unchecked")
List<Integer>[] adj = new ArrayList[n];
for (int i = 0; i < n; i++) {
    adj[i] = new ArrayList<>();
}

// 간선 추가
adj[u].add(v);

// 인접 정점 순회
for (int neighbor : adj[v]) { ... }
```

### Set (방문 체크)
```java
import java.util.Set;
import java.util.HashSet;

Set<Integer> visited = new HashSet<>();

visited.add(v);           // 추가
visited.contains(v);      // 존재 확인
visited.remove(v);        // 제거
visited.size();           // 크기

// boolean[] 대신 사용 (정점이 0~n-1이 아닐 때)
boolean[] visited = new boolean[n];  // 정점이 0~n-1일 때
```

---

## 🔄 순회용 자료구조

### Queue (BFS)
```java
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayDeque;

Queue<Integer> queue = new LinkedList<>();
Queue<Integer> queue = new ArrayDeque<>();  // 더 효율적

queue.offer(v);    // 삽입 (실패 시 false)
queue.add(v);      // 삽입 (실패 시 예외)
queue.poll();      // 제거 및 반환 (비어있으면 null)
queue.remove();    // 제거 및 반환 (비어있으면 예외)
queue.peek();      // 조회 (비어있으면 null)
queue.isEmpty();   // 비어있는지

// BFS 레벨별 처리
while (!queue.isEmpty()) {
    int levelSize = queue.size();
    for (int i = 0; i < levelSize; i++) {
        int v = queue.poll();
        // 처리...
    }
}
```

### Deque/Stack (DFS)
```java
import java.util.Deque;
import java.util.ArrayDeque;

Deque<Integer> stack = new ArrayDeque<>();

stack.push(v);     // 스택 맨 위에 추가
stack.pop();       // 스택 맨 위에서 제거 및 반환
stack.peek();      // 스택 맨 위 조회
stack.isEmpty();   // 비어있는지

// 양방향 사용
stack.addFirst(v);  // 앞에 추가
stack.addLast(v);   // 뒤에 추가
stack.removeFirst(); // 앞에서 제거
stack.removeLast();  // 뒤에서 제거
```

### PriorityQueue (다익스트라)
```java
import java.util.PriorityQueue;

// [정점, 거리] 배열 사용
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);

// 또는 record 사용 (Java 14+)
record Node(int vertex, int distance) implements Comparable<Node> {
    public int compareTo(Node other) {
        return Integer.compare(distance, other.distance);
    }
}
PriorityQueue<Node> pq = new PriorityQueue<>();

pq.offer(new int[]{v, dist});
int[] current = pq.poll();
```

---

## 🔢 배열 관련

### 2D 배열 (인접 행렬)
```java
// 인접 행렬 생성
int[][] matrix = new int[n][n];

// 간선 추가
matrix[u][v] = 1;           // 비가중치
matrix[u][v] = weight;      // 가중치
matrix[u][v] = matrix[v][u] = 1;  // 무방향

// 인접 여부 확인
boolean hasEdge = matrix[u][v] != 0;

// 초기화
Arrays.fill(matrix[i], Integer.MAX_VALUE);

// 2D 배열 복사
int[][] copy = new int[n][];
for (int i = 0; i < n; i++) {
    copy[i] = matrix[i].clone();
}
```

### Arrays 유틸리티
```java
import java.util.Arrays;

// 초기화
Arrays.fill(arr, -1);
Arrays.fill(distances, Integer.MAX_VALUE);

// 2D 배열 초기화
for (int[] row : matrix) {
    Arrays.fill(row, INF);
}

// 배열 정렬 (크루스칼용)
int[][] edges = new int[m][3];  // [u, v, weight]
Arrays.sort(edges, (a, b) -> a[2] - b[2]);

// 출력
System.out.println(Arrays.toString(arr));
System.out.println(Arrays.deepToString(matrix));
```

---

## 🧮 수학/상수

### 무한대 표현
```java
// 정수 무한대
int INF = Integer.MAX_VALUE;
int INF = (int) 1e9;  // 오버플로우 방지용

// 거리 갱신 시 오버플로우 주의
if (dist != INF && dist + weight < distances[neighbor]) {
    distances[neighbor] = dist + weight;
}

// Long 타입
long INF = Long.MAX_VALUE;
```

### Math 클래스
```java
Math.min(a, b);
Math.max(a, b);
Math.abs(a);

// 거리 비교
int newDist = Math.min(distances[v], distances[u] + weight);
```

---

## 📊 그래프 입력 처리

### 간선 리스트 입력
```java
// 입력: n개 정점, m개 간선
// 각 줄: u v (또는 u v weight)
Scanner sc = new Scanner(System.in);
int n = sc.nextInt();
int m = sc.nextInt();

List<List<Integer>> adj = new ArrayList<>();
for (int i = 0; i < n; i++) {
    adj.add(new ArrayList<>());
}

for (int i = 0; i < m; i++) {
    int u = sc.nextInt();
    int v = sc.nextInt();
    adj.get(u).add(v);
    adj.get(v).add(u);  // 무방향
}
```

### 그리드를 그래프로 변환
```java
// 4방향 이동
int[] dx = {0, 0, 1, -1};
int[] dy = {1, -1, 0, 0};

// 8방향 이동
int[] dx = {0, 0, 1, -1, 1, 1, -1, -1};
int[] dy = {1, -1, 0, 0, 1, -1, 1, -1};

// BFS on 그리드
for (int d = 0; d < 4; d++) {
    int nx = x + dx[d];
    int ny = y + dy[d];
    
    if (nx >= 0 && nx < rows && ny >= 0 && ny < cols) {
        if (!visited[nx][ny] && grid[nx][ny] != '#') {
            visited[nx][ny] = true;
            queue.offer(new int[]{nx, ny});
        }
    }
}

// 좌표를 단일 인덱스로 변환
int index = x * cols + y;
int x = index / cols;
int y = index % cols;
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void bfsShouldVisitAllConnectedVertices() {
    Graph graph = new Graph();
    graph.addEdge(0, 1);
    graph.addEdge(0, 2);
    graph.addEdge(1, 3);
    
    List<Integer> bfs = graph.bfs(0);
    
    assertThat(bfs).hasSize(4);
    assertThat(bfs).containsExactlyInAnyOrder(0, 1, 2, 3);
    assertThat(bfs.get(0)).isEqualTo(0);  // 시작점이 첫 번째
}

@Test
void dijkstraShouldFindShortestPath() {
    WeightedGraph graph = new WeightedGraph();
    graph.addEdge(0, 1, 4);
    graph.addEdge(0, 2, 1);
    graph.addEdge(2, 1, 2);
    
    Map<Integer, Integer> distances = graph.dijkstra(0);
    
    assertThat(distances.get(0)).isEqualTo(0);
    assertThat(distances.get(1)).isEqualTo(3);  // 0→2→1
    assertThat(distances.get(2)).isEqualTo(1);
}

@Test
void shouldDetectCycle() {
    DirectedGraph graph = new DirectedGraph();
    graph.addEdge(0, 1);
    graph.addEdge(1, 2);
    graph.addEdge(2, 0);
    
    assertThat(graph.hasCycle()).isTrue();
}
```

---

## 📚 Java 21 관련

### Record로 간선/노드 표현
```java
// 간선
public record Edge(int from, int to, int weight) 
    implements Comparable<Edge> {
    @Override
    public int compareTo(Edge other) {
        return Integer.compare(weight, other.weight);
    }
}

// 노드 (다익스트라용)
public record Node(int vertex, int distance) 
    implements Comparable<Node> {
    @Override
    public int compareTo(Node other) {
        return Integer.compare(distance, other.distance);
    }
}

// 좌표 (그리드 BFS용)
public record Point(int x, int y) {}
```

### Pattern Matching
```java
// 정점 타입에 따른 처리
public void processVertex(Object v) {
    switch (v) {
        case Integer i -> processIntVertex(i);
        case String s -> processStringVertex(s);
        default -> throw new IllegalArgumentException();
    }
}
```

### Stream으로 그래프 처리
```java
// 모든 간선 수집
List<Edge> allEdges = adjList.entrySet().stream()
    .flatMap(e -> e.getValue().stream()
        .map(neighbor -> new Edge(e.getKey(), neighbor, 1)))
    .toList();

// 연결 요소 크기
long largestComponent = components.stream()
    .mapToInt(List::size)
    .max()
    .orElse(0);
```

---

## ⚡ 성능 팁

### 1. ArrayDeque vs LinkedList
```java
// ArrayDeque가 더 빠름 (캐시 지역성)
Queue<Integer> queue = new ArrayDeque<>();  // 권장
Queue<Integer> queue = new LinkedList<>();  // 느림
```

### 2. 방문 배열 vs Set
```java
// 정점이 0~n-1이면 배열이 빠름
boolean[] visited = new boolean[n];
visited[v] = true;

// 정점이 불연속이거나 타입이 다르면 Set
Set<Integer> visited = new HashSet<>();
visited.add(v);
```

### 3. 다익스트라 최적화
```java
// 이미 처리된 정점 스킵
int[] current = pq.poll();
int v = current[0];
int dist = current[1];

if (dist > distances[v]) continue;  // 이미 더 짧은 경로로 처리됨
```

### 4. 간선 리스트 정렬 (크루스칼)
```java
// Comparator 대신 Comparable 사용
record Edge(int u, int v, int w) implements Comparable<Edge> {
    public int compareTo(Edge o) { return w - o.w; }
}
Collections.sort(edges);  // Comparator 불필요
```
