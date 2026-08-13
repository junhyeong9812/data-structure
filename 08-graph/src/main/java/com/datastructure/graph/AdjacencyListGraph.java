package com.datastructure.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * 인접 리스트 그래프.
 *
 * 정점마다 "내가 이어진 곳"의 목록을 들고 있는다. 02번 연결 리스트를 정점 수만큼 둔 셈이다.
 *
 * 없는 간선에는 아무 자리도 쓰지 않는다. 그래서 희소 그래프에서 압도적으로 유리하다.
 * 정점 100만 개에 간선 200만 개인 그래프를 행렬로 만들면 10^12 칸이 필요하다. 불가능하다.
 *
 * 대신 `hasEdge(u, v)` 를 물으면 u 의 이웃 목록을 훑어야 한다. O(deg(u)) 다.
 *
 * 참고: 필드 이름 adjacency, edges 는 테스트가 직접 들여다본다.
 */
public class AdjacencyListGraph implements Graph {

    /** 한 간선. to 와 weight 를 함께 들고 있는다. */
    static class Edge {
        final int to;
        int weight;

        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    private final int vertexCount;
    private final boolean directed;
    List<Edge>[] adjacency;
    int edges;

    @SuppressWarnings("unchecked")
    public AdjacencyListGraph(int vertexCount, boolean directed) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("정점 수는 음수일 수 없다: " + vertexCount);
        }
        this.vertexCount = vertexCount;
        this.directed = directed;
        this.adjacency = new List[vertexCount];
        for (int i = 0; i < vertexCount; i++) adjacency[i] = new ArrayList<>();
        this.edges = 0;
    }

    // ------------------------------------------------------------------
    // 채워져 있는 부분
    // ------------------------------------------------------------------

    @Override
    public int vertexCount() {
        return vertexCount;
    }

    @Override
    public int edgeCount() {
        return edges;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    @Override
    public void addEdge(int from, int to) {
        addEdge(from, to, 1);
    }

    void checkVertex(int v) {
        if (v < 0 || v >= vertexCount) {
            throw new IndexOutOfBoundsException("정점 " + v + ", 정점 수 " + vertexCount);
        }
    }

    /** from 의 이웃 목록에서 to 로 가는 간선을 찾는다. 없으면 null. */
    Edge findEdge(int from, int to) {
        for (Edge e : adjacency[from]) {
            if (e.to == to) return e;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int v = 0; v < vertexCount; v++) {
            sb.append(v).append(" -> ");
            for (Edge e : adjacency[v]) sb.append(e.to).append('(').append(e.weight).append(") ");
            sb.append('\n');
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 간선을 잇는다.
     *
     * 생각할 것
     *   - 이미 있는 간선이면 가중치만 덮어쓴다. 목록에 또 넣으면 같은 간선이 두 개가 된다.
     *   - 무방향이면 양쪽 목록에 다 넣어야 한다. 그런데 edgeCount 는 몇 개 늘어나는가?
     *   - 자기 자신으로 가는 간선(self loop)에서 무방향이면 어떻게 되는가?
     *
     * TODO(01): 구현하라.
     */
    @Override
    public void addEdge(int from, int to, int weight) {
        throw new UnsupportedOperationException("TODO(01): addEdge");
    }

    /** TODO(02): 구현하라. */
    @Override
    public boolean hasEdge(int from, int to) {
        throw new UnsupportedOperationException("TODO(02): hasEdge");
    }

    /** TODO(03): 구현하라. 간선이 없으면 NO_EDGE. */
    @Override
    public int weight(int from, int to) {
        throw new UnsupportedOperationException("TODO(03): weight");
    }

    /**
     * from 의 이웃들.
     *
     * 생각할 것
     *   - 내부 목록을 그대로 주면 호출자가 그래프를 바꿀 수 있다. 01번 toArray 와 같은 문제다.
     *
     * TODO(04): 구현하라.
     */
    @Override
    public Iterable<Integer> neighbors(int from) {
        throw new UnsupportedOperationException("TODO(04): neighbors");
    }
}
