# pop/WeightedGraph.java

가중치 그래프. 다익스트라, 벨만-포드, 플로이드-워셜, 프림 MST, 크루스칼 MST.

```java
package com.datastructure.graph.pop;

import java.util.*;

public class WeightedGraph {
    public static class Edge {
        public final int u, v;
        public final int weight;

        public Edge(int u, int v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }

    private final Map<Integer, List<int[]>> adjList; // [neighbor, weight]
    private final List<Edge> edges;
    private final boolean directed;

    public WeightedGraph() {
        this(false);
    }

    public WeightedGraph(boolean directed) {
        this.adjList = new HashMap<>();
        this.edges = new ArrayList<>();
        this.directed = directed;
    }

    public void addVertex(int v) {
        adjList.putIfAbsent(v, new ArrayList<>());
    }

    public void addEdge(int u, int v, int weight) {
        addVertex(u);
        addVertex(v);
        adjList.get(u).add(new int[]{v, weight});
        if (!directed) {
            adjList.get(v).add(new int[]{u, weight});
        }
        edges.add(new Edge(u, v, weight));
    }

    public Set<Integer> vertices() {
        return adjList.keySet();
    }

    // 다익스트라: O((V+E) log V), 음의 가중치 X
    public Map<Integer, Integer> dijkstra(int start) {
        Map<Integer, Integer> dist = new HashMap<>();
        for (int v : adjList.keySet()) dist.put(v, Integer.MAX_VALUE);
        dist.put(start, 0);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{start, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int v = cur[0], d = cur[1];
            if (d > dist.get(v)) continue;

            for (int[] edge : adjList.getOrDefault(v, Collections.emptyList())) {
                int nb = edge[0], w = edge[1];
                int nd = d + w;
                if (nd < dist.get(nb)) {
                    dist.put(nb, nd);
                    pq.offer(new int[]{nb, nd});
                }
            }
        }
        return dist;
    }

    // 다익스트라 + 경로 복원
    public List<Integer> dijkstraPath(int start, int end) {
        Map<Integer, Integer> dist = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        for (int v : adjList.keySet()) dist.put(v, Integer.MAX_VALUE);
        dist.put(start, 0);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{start, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int v = cur[0], d = cur[1];
            if (d > dist.get(v)) continue;

            for (int[] edge : adjList.getOrDefault(v, Collections.emptyList())) {
                int nb = edge[0], w = edge[1];
                int nd = d + w;
                if (nd < dist.get(nb)) {
                    dist.put(nb, nd);
                    parent.put(nb, v);
                    pq.offer(new int[]{nb, nd});
                }
            }
        }

        if (!dist.containsKey(end) || dist.get(end) == Integer.MAX_VALUE) {
            return Collections.emptyList();
        }

        LinkedList<Integer> path = new LinkedList<>();
        Integer cur = end;
        while (cur != null) {
            path.addFirst(cur);
            cur = parent.get(cur);
        }
        return path;
    }

    // 벨만-포드: O(V*E), 음의 가중치 OK, 음의 사이클 탐지
    public Map<Integer, Integer> bellmanFord(int start) {
        Map<Integer, Integer> dist = new HashMap<>();
        for (int v : adjList.keySet()) dist.put(v, Integer.MAX_VALUE);
        dist.put(start, 0);

        int V = adjList.size();
        for (int i = 0; i < V - 1; i++) {
            for (Edge e : edges) {
                if (dist.get(e.u) == Integer.MAX_VALUE) continue;
                if (dist.get(e.u) + e.weight < dist.get(e.v)) {
                    dist.put(e.v, dist.get(e.u) + e.weight);
                }
                if (!directed) {
                    if (dist.get(e.v) == Integer.MAX_VALUE) continue;
                    if (dist.get(e.v) + e.weight < dist.get(e.u)) {
                        dist.put(e.u, dist.get(e.v) + e.weight);
                    }
                }
            }
        }

        // 음의 사이클 검사
        for (Edge e : edges) {
            if (dist.get(e.u) != Integer.MAX_VALUE
                    && dist.get(e.u) + e.weight < dist.get(e.v)) {
                throw new IllegalStateException("Negative cycle detected");
            }
        }
        return dist;
    }

    // 플로이드-워셜: 모든 쌍 최단 경로 O(V^3)
    public Map<Integer, Map<Integer, Integer>> floydWarshall() {
        List<Integer> verts = new ArrayList<>(adjList.keySet());
        Map<Integer, Map<Integer, Integer>> dist = new HashMap<>();

        for (int u : verts) {
            dist.put(u, new HashMap<>());
            for (int v : verts) {
                dist.get(u).put(v, u == v ? 0 : Integer.MAX_VALUE);
            }
        }

        for (int u : verts) {
            for (int[] edge : adjList.get(u)) {
                int v = edge[0], w = edge[1];
                dist.get(u).put(v, Math.min(dist.get(u).get(v), w));
            }
        }

        for (int k : verts) {
            for (int i : verts) {
                for (int j : verts) {
                    int dik = dist.get(i).get(k);
                    int dkj = dist.get(k).get(j);
                    if (dik == Integer.MAX_VALUE || dkj == Integer.MAX_VALUE) continue;
                    if (dik + dkj < dist.get(i).get(j)) {
                        dist.get(i).put(j, dik + dkj);
                    }
                }
            }
        }
        return dist;
    }

    // 프림 MST: O((V+E) log V)
    public List<Edge> prim() {
        List<Edge> mst = new ArrayList<>();
        if (adjList.isEmpty()) return mst;

        Set<Integer> inMST = new HashSet<>();
        int start = adjList.keySet().iterator().next();
        inMST.add(start);

        // [from, to, weight]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        for (int[] edge : adjList.get(start)) {
            pq.offer(new int[]{start, edge[0], edge[1]});
        }

        while (!pq.isEmpty() && inMST.size() < adjList.size()) {
            int[] e = pq.poll();
            int from = e[0], to = e[1], w = e[2];
            if (inMST.contains(to)) continue;

            inMST.add(to);
            mst.add(new Edge(from, to, w));

            for (int[] next : adjList.get(to)) {
                if (!inMST.contains(next[0])) {
                    pq.offer(new int[]{to, next[0], next[1]});
                }
            }
        }
        return mst;
    }

    // 크루스칼 MST: O(E log E) (Union-Find)
    public List<Edge> kruskal() {
        List<Edge> sorted = new ArrayList<>(edges);
        sorted.sort(Comparator.comparingInt(e -> e.weight));

        Map<Integer, Integer> parent = new HashMap<>();
        for (int v : adjList.keySet()) parent.put(v, v);

        List<Edge> mst = new ArrayList<>();
        for (Edge e : sorted) {
            int ru = find(parent, e.u);
            int rv = find(parent, e.v);
            if (ru != rv) {
                parent.put(ru, rv);
                mst.add(e);
                if (mst.size() == adjList.size() - 1) break;
            }
        }
        return mst;
    }

    private int find(Map<Integer, Integer> parent, int x) {
        while (parent.get(x) != x) {
            parent.put(x, parent.get(parent.get(x))); // 경로 압축
            x = parent.get(x);
        }
        return x;
    }
}
```
