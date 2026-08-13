package com.datastructure.graph;

import java.util.ArrayList;

/**
 * [구현] 인접 행렬 그래프.
 *
 * V x V 표를 만들어 놓고 (u, v) 칸에 가중치를 적는다. 간선이 없으면 NO_EDGE 를 적어둔다.
 *
 * `hasEdge(u, v)` 가 배열 한 번 읽기라 O(1) 이다. 인접 리스트는 목록을 훑어야 해서 O(deg(u)) 다.
 *
 * 대신 대가가 크다.
 *   - **간선이 하나도 없어도 V^2 칸을 쓴다.** 정점 10만 개면 100억 칸이다. 못 만든다.
 *   - `neighbors(v)` 가 이웃이 하나도 없어도 V 칸을 다 훑는다.
 *     그래서 BFS/DFS 가 O(V + E) 가 아니라 O(V^2) 이 된다.
 *
 * 이 성질은 버그가 아니라 이 표현 방식의 정의다. AdjacencyMatrixGraphTest 가 숫자로 보여준다.
 *
 * 참고: 필드 이름 matrix, edges 는 테스트가 직접 들여다본다.
 */
public class AdjacencyMatrixGraph implements Graph {

    private final int vertexCount;
    private final boolean directed;
    int[][] matrix;
    int edges;

    public AdjacencyMatrixGraph(int vertexCount, boolean directed) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("정점 수는 음수일 수 없다: " + vertexCount);
        }
        this.vertexCount = vertexCount;
        this.directed = directed;
        this.matrix = new int[vertexCount][vertexCount];
        for (int[] row : matrix) java.util.Arrays.fill(row, NO_EDGE);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int u = 0; u < vertexCount; u++) {
            for (int v = 0; v < vertexCount; v++) {
                sb.append(matrix[u][v] == NO_EDGE ? "." : String.valueOf(matrix[u][v])).append(' ');
            }
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
     *   - 인접 리스트와 달리 "이미 있는지" 찾는 데 비용이 안 든다. 왜인가?
     *   - 그래도 edgeCount 를 정확히 세려면 확인이 필요하다.
     *   - 무방향이면 표의 어느 칸들을 채워야 하는가?
     *
     */
    @Override
    public void addEdge(int from, int to, int weight) {
        checkVertex(from);
        checkVertex(to);
        if (weight < 0) {
            throw new IllegalArgumentException("음수 가중치는 담을 수 없다: " + weight);
        }
        // 인접 리스트와 달리 "이미 있는지" 찾는 데 비용이 없다. 칸을 바로 보면 된다.
        if (matrix[from][to] == NO_EDGE) {
            edges++;
        }
        matrix[from][to] = weight;
        if (!directed) {
            matrix[to][from] = weight;    // 표의 두 칸을 채워도 간선은 하나다
        }
    }

    /** TODO(06): 구현하라. 이게 O(1) 인 것이 이 표현의 유일한 장점이다. */
    @Override
    public boolean hasEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        return matrix[from][to] != NO_EDGE;    // 배열 한 번 읽기. O(1)
    }

    /** TODO(07): 구현하라. */
    @Override
    public int weight(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        return matrix[from][to];
    }

    /**
     * from 의 이웃들.
     *
     * 생각할 것
     *   - 이웃이 하나도 없어도 몇 칸을 봐야 하는가? 그게 이 표현의 대가다.
     *
     */
    @Override
    public Iterable<Integer> neighbors(int from) {
        checkVertex(from);
        // 이웃이 하나도 없어도 V 칸을 다 봐야 한다. 그게 이 표현의 대가다.
        java.util.List<Integer> result = new ArrayList<>();
        for (int v = 0; v < vertexCount; v++) {
            if (matrix[from][v] != NO_EDGE) result.add(v);
        }
        return result;
    }
}
