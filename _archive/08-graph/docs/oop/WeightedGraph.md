# oop/WeightedGraph.java

가중치 그래프 OOP 구현. 다익스트라, 벨만-포드, 프림, 크루스칼 인스턴스 메서드 제공.

```java
package com.datastructure.graph.oop;

import java.util.*;

public class WeightedGraph<V> {
    public static class Edge<V> {
        public final V u, v;
        public final int weight;

        public Edge(V u, V v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }

    private final Map<V, List<Edge<V>>> adj;
    private final List<Edge<V>> allEdges;
    private final boolean directed;

    public WeightedGraph() {
        this(false);
    }

    public WeightedGraph(boolean directed) {
        this.adj = new HashMap<>();
        this.allEdges = new ArrayList<>();
        this.directed = directed;
    }

    public void addVertex(V v) {
        adj.putIfAbsent(v, new ArrayList<>());
    }

    public void addEdge(V u, V v, int weight) {
        addVertex(u);
        addVertex(v);
        Edge<V> e = new Edge<>(u, v, weight);
        adj.get(u).add(e);
        if (!directed) {
            adj.get(v).add(new Edge<>(v, u, weight));
        }
        allEdges.add(e);
    }

    public Set<V> vertices() {
        return adj.keySet();
    }

    public List<Edge<V>> edges() {
        return Collections.unmodifiableList(allEdges);
    }

    public Map<V, Integer> dijkstra(V start) {
        Map<V, Integer> dist = new HashMap<>();
        for (V v : adj.keySet()) dist.put(v, Integer.MAX_VALUE);
        dist.put(start, 0);

        PriorityQueue<Map.Entry<V, Integer>> pq =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
        pq.offer(Map.entry(start, 0));

        while (!pq.isEmpty()) {
            Map.Entry<V, Integer> cur = pq.poll();
            V v = cur.getKey();
            int d = cur.getValue();
            if (d > dist.get(v)) continue;

            for (Edge<V> e : adj.getOrDefault(v, Collections.emptyList())) {
                int nd = d + e.weight;
                if (nd < dist.get(e.v)) {
                    dist.put(e.v, nd);
                    pq.offer(Map.entry(e.v, nd));
                }
            }
        }
        return dist;
    }

    public Map<V, Integer> bellmanFord(V start) {
        Map<V, Integer> dist = new HashMap<>();
        for (V v : adj.keySet()) dist.put(v, Integer.MAX_VALUE);
        dist.put(start, 0);

        int V = adj.size();
        for (int i = 0; i < V - 1; i++) {
            for (Edge<V> e : allEdges) {
                relax(dist, e.u, e.v, e.weight);
                if (!directed) relax(dist, e.v, e.u, e.weight);
            }
        }

        for (Edge<V> e : allEdges) {
            if (dist.get(e.u) != Integer.MAX_VALUE
                    && dist.get(e.u) + e.weight < dist.get(e.v)) {
                throw new IllegalStateException("Negative cycle detected");
            }
        }
        return dist;
    }

    private void relax(Map<V, Integer> dist, V u, V v, int w) {
        if (dist.get(u) == Integer.MAX_VALUE) return;
        if (dist.get(u) + w < dist.get(v)) {
            dist.put(v, dist.get(u) + w);
        }
    }

    public List<Edge<V>> prim() {
        List<Edge<V>> mst = new ArrayList<>();
        if (adj.isEmpty()) return mst;

        Set<V> inMST = new HashSet<>();
        V start = adj.keySet().iterator().next();
        inMST.add(start);

        PriorityQueue<Edge<V>> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        pq.addAll(adj.get(start));

        while (!pq.isEmpty() && inMST.size() < adj.size()) {
            Edge<V> e = pq.poll();
            if (inMST.contains(e.v)) continue;
            inMST.add(e.v);
            mst.add(e);
            for (Edge<V> next : adj.get(e.v)) {
                if (!inMST.contains(next.v)) pq.offer(next);
            }
        }
        return mst;
    }

    public List<Edge<V>> kruskal() {
        List<Edge<V>> sorted = new ArrayList<>(allEdges);
        sorted.sort(Comparator.comparingInt(e -> e.weight));

        Map<V, V> parent = new HashMap<>();
        for (V v : adj.keySet()) parent.put(v, v);

        List<Edge<V>> mst = new ArrayList<>();
        for (Edge<V> e : sorted) {
            V ru = find(parent, e.u);
            V rv = find(parent, e.v);
            if (!Objects.equals(ru, rv)) {
                parent.put(ru, rv);
                mst.add(e);
                if (mst.size() == adj.size() - 1) break;
            }
        }
        return mst;
    }

    private V find(Map<V, V> parent, V x) {
        while (!Objects.equals(parent.get(x), x)) {
            parent.put(x, parent.get(parent.get(x)));
            x = parent.get(x);
        }
        return x;
    }
}
```
