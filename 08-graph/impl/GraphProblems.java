package com.datastructure.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;

/** [구현] 그래프 응용 문제. 전부 Graph 인터페이스만 안다. */
public final class GraphProblems {

    private GraphProblems() {
    }

    /**
     * 너비 우선. 가까운 것부터 층층이 넓혀 가므로 처음 닿았을 때가 곧 최단이다.
     * 깊이 우선으로 하면 먼 길로 먼저 닿을 수 있어 최단이 아니다.
     *
     * 방문 표시는 큐에 넣을 때 한다. 꺼낼 때 하면 같은 정점이 큐에 여러 번 들어간다.
     */
    public static int[] bfsDistances(Graph graph, int start) {
        int[] distance = new int[graph.vertexCount()];
        Arrays.fill(distance, -1);
        if (graph.vertexCount() == 0) return distance;

        Deque<Integer> queue = new ArrayDeque<>();
        distance[start] = 0;
        queue.addLast(start);

        while (!queue.isEmpty()) {
            int current = queue.removeFirst();
            for (int next : graph.neighbors(current)) {
                if (distance[next] == -1) {
                    distance[next] = distance[current] + 1;
                    queue.addLast(next);          // 넣을 때 표시했으므로 중복이 없다
                }
            }
        }
        return distance;
    }

    /**
     * 깊이 우선을 반복으로 구현한다.
     *
     * 재귀로 하면 호출 스택이 그 역할을 하는데, 10만 개가 한 줄로 이어진 그래프에서 넘친다.
     * 명시적 스택을 쓰면 그 문제가 없다.
     *
     * 이웃을 역순으로 쌓아야 재귀와 같은 순서로 방문한다. 스택은 나중에 넣은 것이 먼저 나오기 때문이다.
     */
    public static int[] dfsOrder(Graph graph, int start) {
        boolean[] visited = new boolean[graph.vertexCount()];
        List<Integer> order = new ArrayList<>();
        if (graph.vertexCount() == 0) return new int[0];

        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (visited[current]) continue;      // 스택에는 중복이 들어올 수 있다
            visited[current] = true;
            order.add(current);

            List<Integer> next = new ArrayList<>();
            for (int v : graph.neighbors(current)) next.add(v);
            for (int i = next.size() - 1; i >= 0; i--) {
                if (!visited[next.get(i)]) stack.push(next.get(i));
            }
        }
        return order.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 위상 정렬 (Kahn).
     *
     * 들어오는 간선이 없는 정점은 지금 처리해도 된다. 그걸 빼면 다른 정점의 진입 차수가 줄고,
     * 0 이 된 것이 다음 차례가 된다. 큐가 그 대기열이다.
     *
     * 다 돌았는데 정점이 남았다면 진입 차수가 0 이 되지 못한 것들이 있다는 뜻이고,
     * 그건 서로를 기다리는 순환이 있다는 말이다.
     */
    public static int[] topologicalSort(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("방향 그래프여야 순서를 정할 수 있다");
        }
        int n = graph.vertexCount();
        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (int v : graph.neighbors(u)) inDegree[v]++;
        }

        Deque<Integer> ready = new ArrayDeque<>();
        for (int v = 0; v < n; v++) {
            if (inDegree[v] == 0) ready.addLast(v);
        }

        int[] order = new int[n];
        int filled = 0;
        while (!ready.isEmpty()) {
            int current = ready.removeFirst();
            order[filled++] = current;
            for (int next : graph.neighbors(current)) {
                if (--inDegree[next] == 0) ready.addLast(next);
            }
        }

        if (filled != n) {
            throw new IllegalStateException("순환이 있어 순서를 정할 수 없다");
        }
        return order;
    }

    /**
     * 다익스트라.
     *
     * BFS 는 간선 수만 세므로 가중치가 있으면 틀린다.
     * "지금까지 알아낸 것 중 가장 가까운 정점"을 반복해서 꺼내야 하고, 그게 07번 힙이다.
     * (여기서는 java.util.PriorityQueue 를 쓴다. 모듈이 분리되어 있어서일 뿐 같은 물건이다.)
     *
     * 이미 확정된 정점이 큐에 남아 있을 수 있으므로 꺼낼 때 걸러낸다.
     * 음수 간선이 있으면 "한 번 확정하면 끝"이라는 전제가 깨져서 이 알고리즘이 성립하지 않는다.
     */
    public static long[] shortestPaths(Graph graph, int start) {
        int n = graph.vertexCount();
        long[] distance = new long[n];
        Arrays.fill(distance, -1);
        if (n == 0) return distance;

        long[] best = new long[n];
        Arrays.fill(best, Long.MAX_VALUE);
        best[start] = 0;

        PriorityQueue<long[]> queue = new PriorityQueue<>((a, b) -> Long.compare(a[1], b[1]));
        queue.add(new long[]{start, 0});

        while (!queue.isEmpty()) {
            long[] top = queue.poll();
            int current = (int) top[0];
            if (top[1] > best[current]) continue;      // 낡은 항목이다

            for (int next : graph.neighbors(current)) {
                long candidate = best[current] + graph.weight(current, next);
                if (candidate < best[next]) {
                    best[next] = candidate;
                    queue.add(new long[]{next, candidate});
                }
            }
        }

        for (int v = 0; v < n; v++) {
            if (best[v] != Long.MAX_VALUE) distance[v] = best[v];
        }
        return distance;
    }
}
