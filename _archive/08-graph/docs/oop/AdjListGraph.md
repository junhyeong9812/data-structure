# oop/AdjListGraph.java

`Graph<V>` 인터페이스의 인접 리스트 기반 구현.

```java
package com.datastructure.graph.oop;

import java.util.*;

public class AdjListGraph<V> implements Graph<V> {
    private final Map<V, List<V>> adj;
    private final boolean directed;
    private int edgeCount;

    public AdjListGraph() {
        this(false);
    }

    public AdjListGraph(boolean directed) {
        this.adj = new HashMap<>();
        this.directed = directed;
        this.edgeCount = 0;
    }

    @Override
    public void addVertex(V v) {
        adj.putIfAbsent(v, new ArrayList<>());
    }

    @Override
    public void addEdge(V u, V v) {
        addVertex(u);
        addVertex(v);
        adj.get(u).add(v);
        if (!directed) adj.get(v).add(u);
        edgeCount++;
    }

    @Override
    public void removeVertex(V v) {
        if (!adj.containsKey(v)) return;
        for (V neighbor : adj.get(v)) {
            adj.get(neighbor).remove(v);
            edgeCount--;
        }
        adj.remove(v);
        if (directed) {
            for (List<V> neighbors : adj.values()) {
                while (neighbors.remove(v)) edgeCount--;
            }
        }
    }

    @Override
    public void removeEdge(V u, V v) {
        if (adj.containsKey(u) && adj.get(u).remove(v)) {
            edgeCount--;
        }
        if (!directed && adj.containsKey(v)) {
            adj.get(v).remove(u);
        }
    }

    @Override
    public boolean hasVertex(V v) {
        return adj.containsKey(v);
    }

    @Override
    public boolean hasEdge(V u, V v) {
        return adj.containsKey(u) && adj.get(u).contains(v);
    }

    @Override
    public Collection<V> getNeighbors(V v) {
        return adj.getOrDefault(v, Collections.emptyList());
    }

    @Override
    public Collection<V> vertices() {
        return adj.keySet();
    }

    @Override
    public int vertexCount() {
        return adj.size();
    }

    @Override
    public int edgeCount() {
        return edgeCount;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    @Override
    public List<V> bfs(V start) {
        List<V> result = new ArrayList<>();
        if (!adj.containsKey(start)) return result;

        Set<V> visited = new HashSet<>();
        Queue<V> queue = new ArrayDeque<>();
        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            V v = queue.poll();
            result.add(v);
            for (V n : getNeighbors(v)) {
                if (visited.add(n)) queue.offer(n);
            }
        }
        return result;
    }

    @Override
    public List<V> dfs(V start) {
        List<V> result = new ArrayList<>();
        if (!adj.containsKey(start)) return result;
        dfsRec(start, new HashSet<>(), result);
        return result;
    }

    private void dfsRec(V v, Set<V> visited, List<V> result) {
        visited.add(v);
        result.add(v);
        for (V n : getNeighbors(v)) {
            if (!visited.contains(n)) dfsRec(n, visited, result);
        }
    }

    @Override
    public List<V> shortestPath(V start, V end) {
        if (!adj.containsKey(start) || !adj.containsKey(end)) {
            return Collections.emptyList();
        }
        if (Objects.equals(start, end)) return List.of(start);

        Map<V, V> parent = new HashMap<>();
        Queue<V> queue = new ArrayDeque<>();
        queue.offer(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            V v = queue.poll();
            for (V n : getNeighbors(v)) {
                if (!parent.containsKey(n)) {
                    parent.put(n, v);
                    if (Objects.equals(n, end)) return reconstruct(parent, end);
                    queue.offer(n);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<V> reconstruct(Map<V, V> parent, V end) {
        LinkedList<V> path = new LinkedList<>();
        V cur = end;
        while (cur != null) {
            path.addFirst(cur);
            cur = parent.get(cur);
        }
        return path;
    }

    @Override
    public boolean hasCycle() {
        Set<V> visited = new HashSet<>();
        if (directed) {
            Set<V> rec = new HashSet<>();
            for (V v : adj.keySet()) {
                if (!visited.contains(v) && cycleDirected(v, visited, rec)) return true;
            }
        } else {
            for (V v : adj.keySet()) {
                if (!visited.contains(v) && cycleUndirected(v, null, visited)) return true;
            }
        }
        return false;
    }

    private boolean cycleDirected(V v, Set<V> visited, Set<V> rec) {
        visited.add(v);
        rec.add(v);
        for (V n : getNeighbors(v)) {
            if (!visited.contains(n)) {
                if (cycleDirected(n, visited, rec)) return true;
            } else if (rec.contains(n)) {
                return true;
            }
        }
        rec.remove(v);
        return false;
    }

    private boolean cycleUndirected(V v, V parent, Set<V> visited) {
        visited.add(v);
        for (V n : getNeighbors(v)) {
            if (!visited.contains(n)) {
                if (cycleUndirected(n, v, visited)) return true;
            } else if (!Objects.equals(n, parent)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<List<V>> connectedComponents() {
        List<List<V>> components = new ArrayList<>();
        Set<V> visited = new HashSet<>();
        for (V v : adj.keySet()) {
            if (!visited.contains(v)) {
                List<V> comp = new ArrayList<>();
                componentDFS(v, visited, comp);
                components.add(comp);
            }
        }
        return components;
    }

    private void componentDFS(V v, Set<V> visited, List<V> comp) {
        visited.add(v);
        comp.add(v);
        for (V n : getNeighbors(v)) {
            if (!visited.contains(n)) componentDFS(n, visited, comp);
        }
    }
}
```
