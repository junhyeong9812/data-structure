# pop/GraphMatrix.java

인접 행렬 기반 그래프. 정점이 0 ~ n-1 범위로 고정. 간선 확인 O(1).

```java
package com.datastructure.graph.pop;

import java.util.*;

public class GraphMatrix {
    private final int[][] matrix;
    private final int n;
    private final boolean directed;
    private int edgeCount;

    public GraphMatrix(int n) {
        this(n, false);
    }

    public GraphMatrix(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.matrix = new int[n][n];
        this.edgeCount = 0;
    }

    public void addEdge(int u, int v) {
        addEdge(u, v, 1);
    }

    public void addEdge(int u, int v, int weight) {
        validate(u);
        validate(v);
        if (matrix[u][v] == 0) edgeCount++;
        matrix[u][v] = weight;
        if (!directed) {
            matrix[v][u] = weight;
        }
    }

    public void removeEdge(int u, int v) {
        validate(u);
        validate(v);
        if (matrix[u][v] != 0) edgeCount--;
        matrix[u][v] = 0;
        if (!directed) {
            matrix[v][u] = 0;
        }
    }

    public boolean hasEdge(int u, int v) {
        validate(u);
        validate(v);
        return matrix[u][v] != 0;
    }

    public int getWeight(int u, int v) {
        validate(u);
        validate(v);
        return matrix[u][v];
    }

    public List<Integer> getNeighbors(int v) {
        validate(v);
        List<Integer> neighbors = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (matrix[v][i] != 0) neighbors.add(i);
        }
        return neighbors;
    }

    public int vertexCount() {
        return n;
    }

    public int edgeCount() {
        return edgeCount;
    }

    public List<Integer> bfs(int start) {
        validate(start);
        List<Integer> result = new ArrayList<>();
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new ArrayDeque<>();

        queue.offer(start);
        visited[start] = true;

        while (!queue.isEmpty()) {
            int v = queue.poll();
            result.add(v);
            for (int i = 0; i < n; i++) {
                if (matrix[v][i] != 0 && !visited[i]) {
                    visited[i] = true;
                    queue.offer(i);
                }
            }
        }
        return result;
    }

    public List<Integer> dfs(int start) {
        validate(start);
        List<Integer> result = new ArrayList<>();
        boolean[] visited = new boolean[n];
        dfsRec(start, visited, result);
        return result;
    }

    private void dfsRec(int v, boolean[] visited, List<Integer> result) {
        visited[v] = true;
        result.add(v);
        for (int i = 0; i < n; i++) {
            if (matrix[v][i] != 0 && !visited[i]) {
                dfsRec(i, visited, result);
            }
        }
    }

    private void validate(int v) {
        if (v < 0 || v >= n) {
            throw new IndexOutOfBoundsException("Vertex out of range: " + v);
        }
    }
}
```
