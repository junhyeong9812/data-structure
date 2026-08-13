# pop/Graph.java

인접 리스트 기반 무방향/방향 그래프. BFS/DFS, 최단 경로(비가중치), 사이클 탐지, 위상 정렬, 연결 요소, 이분 그래프 판별 포함.

```java
package com.datastructure.graph.pop;

import java.util.*;

public class Graph {
    private final Map<Integer, List<Integer>> adjList;
    private final boolean directed;
    private int edgeCount;

    public Graph() {
        this(false);
    }

    public Graph(boolean directed) {
        this.adjList = new HashMap<>();
        this.directed = directed;
        this.edgeCount = 0;
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
        edgeCount++;
    }

    public void removeVertex(int v) {
        if (!adjList.containsKey(v)) return;
        for (int neighbor : adjList.get(v)) {
            adjList.get(neighbor).remove((Integer) v);
        }
        edgeCount -= adjList.get(v).size();
        adjList.remove(v);
        if (directed) {
            for (List<Integer> neighbors : adjList.values()) {
                neighbors.removeIf(n -> n == v);
            }
        }
    }

    public void removeEdge(int u, int v) {
        if (adjList.containsKey(u)) {
            if (adjList.get(u).remove((Integer) v)) {
                edgeCount--;
            }
        }
        if (!directed && adjList.containsKey(v)) {
            adjList.get(v).remove((Integer) u);
        }
    }

    public boolean hasVertex(int v) {
        return adjList.containsKey(v);
    }

    public boolean hasEdge(int u, int v) {
        return adjList.containsKey(u) && adjList.get(u).contains(v);
    }

    public List<Integer> getNeighbors(int v) {
        return adjList.getOrDefault(v, Collections.emptyList());
    }

    public int vertexCount() {
        return adjList.size();
    }

    public int edgeCount() {
        return edgeCount;
    }

    public Set<Integer> vertices() {
        return adjList.keySet();
    }

    // BFS (반복)
    public List<Integer> bfs(int start) {
        List<Integer> result = new ArrayList<>();
        if (!adjList.containsKey(start)) return result;

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();
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
        if (!adjList.containsKey(start)) return result;
        Set<Integer> visited = new HashSet<>();
        dfsRecursive(start, visited, result);
        return result;
    }

    private void dfsRecursive(int v, Set<Integer> visited, List<Integer> result) {
        visited.add(v);
        result.add(v);
        for (int neighbor : getNeighbors(v)) {
            if (!visited.contains(neighbor)) {
                dfsRecursive(neighbor, visited, result);
            }
        }
    }

    // DFS (반복, 스택)
    public List<Integer> dfsIterative(int start) {
        List<Integer> result = new ArrayList<>();
        if (!adjList.containsKey(start)) return result;

        Set<Integer> visited = new HashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            int v = stack.pop();
            if (visited.contains(v)) continue;
            visited.add(v);
            result.add(v);

            List<Integer> neighbors = getNeighbors(v);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                int n = neighbors.get(i);
                if (!visited.contains(n)) {
                    stack.push(n);
                }
            }
        }
        return result;
    }

    // 비가중치 최단 경로 (BFS)
    public List<Integer> shortestPath(int start, int end) {
        if (!adjList.containsKey(start) || !adjList.containsKey(end)) {
            return Collections.emptyList();
        }
        if (start == end) return List.of(start);

        Map<Integer, Integer> parent = new HashMap<>();
        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (int neighbor : getNeighbors(v)) {
                if (!parent.containsKey(neighbor)) {
                    parent.put(neighbor, v);
                    if (neighbor == end) {
                        return reconstructPath(parent, end);
                    }
                    queue.offer(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<Integer> reconstructPath(Map<Integer, Integer> parent, int end) {
        LinkedList<Integer> path = new LinkedList<>();
        Integer cur = end;
        while (cur != null) {
            path.addFirst(cur);
            cur = parent.get(cur);
        }
        return path;
    }

    // 사이클 탐지 (방향/무방향 모두)
    public boolean hasCycle() {
        if (directed) return hasCycleDirected();
        return hasCycleUndirected();
    }

    private boolean hasCycleDirected() {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> recStack = new HashSet<>();
        for (int v : adjList.keySet()) {
            if (!visited.contains(v) && cycleDFSDirected(v, visited, recStack)) {
                return true;
            }
        }
        return false;
    }

    private boolean cycleDFSDirected(int v, Set<Integer> visited, Set<Integer> recStack) {
        visited.add(v);
        recStack.add(v);
        for (int neighbor : getNeighbors(v)) {
            if (!visited.contains(neighbor)) {
                if (cycleDFSDirected(neighbor, visited, recStack)) return true;
            } else if (recStack.contains(neighbor)) {
                return true;
            }
        }
        recStack.remove(v);
        return false;
    }

    private boolean hasCycleUndirected() {
        Set<Integer> visited = new HashSet<>();
        for (int v : adjList.keySet()) {
            if (!visited.contains(v) && cycleDFSUndirected(v, -1, visited)) {
                return true;
            }
        }
        return false;
    }

    private boolean cycleDFSUndirected(int v, int parent, Set<Integer> visited) {
        visited.add(v);
        for (int neighbor : getNeighbors(v)) {
            if (!visited.contains(neighbor)) {
                if (cycleDFSUndirected(neighbor, v, visited)) return true;
            } else if (neighbor != parent) {
                return true;
            }
        }
        return false;
    }

    // 위상 정렬 (Kahn's algorithm)
    public List<Integer> topologicalSort() {
        if (!directed) {
            throw new IllegalStateException("Topological sort requires directed graph");
        }

        Map<Integer, Integer> inDegree = new HashMap<>();
        for (int v : adjList.keySet()) inDegree.put(v, 0);
        for (int v : adjList.keySet()) {
            for (int neighbor : adjList.get(v)) {
                inDegree.merge(neighbor, 1, Integer::sum);
            }
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (Map.Entry<Integer, Integer> e : inDegree.entrySet()) {
            if (e.getValue() == 0) queue.offer(e.getKey());
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int v = queue.poll();
            result.add(v);
            for (int neighbor : getNeighbors(v)) {
                inDegree.merge(neighbor, -1, Integer::sum);
                if (inDegree.get(neighbor) == 0) queue.offer(neighbor);
            }
        }

        if (result.size() != adjList.size()) {
            throw new IllegalStateException("Graph has a cycle");
        }
        return result;
    }

    // 연결 요소
    public List<List<Integer>> connectedComponents() {
        List<List<Integer>> components = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        for (int v : adjList.keySet()) {
            if (!visited.contains(v)) {
                List<Integer> component = new ArrayList<>();
                componentDFS(v, visited, component);
                components.add(component);
            }
        }
        return components;
    }

    private void componentDFS(int v, Set<Integer> visited, List<Integer> component) {
        visited.add(v);
        component.add(v);
        for (int neighbor : getNeighbors(v)) {
            if (!visited.contains(neighbor)) {
                componentDFS(neighbor, visited, component);
            }
        }
    }

    // 이분 그래프 판별
    public boolean isBipartite() {
        Map<Integer, Integer> color = new HashMap<>();
        for (int start : adjList.keySet()) {
            if (color.containsKey(start)) continue;

            Queue<Integer> queue = new ArrayDeque<>();
            queue.offer(start);
            color.put(start, 0);

            while (!queue.isEmpty()) {
                int v = queue.poll();
                for (int neighbor : getNeighbors(v)) {
                    if (!color.containsKey(neighbor)) {
                        color.put(neighbor, 1 - color.get(v));
                        queue.offer(neighbor);
                    } else if (color.get(neighbor).equals(color.get(v))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
```
