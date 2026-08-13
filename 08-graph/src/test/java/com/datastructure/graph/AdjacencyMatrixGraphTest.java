package com.datastructure.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AdjacencyMatrixGraph 가 계약을 지키는지 + **이 표현의 한계**.
 *
 * 아래 두 테스트는 버그를 잡는 것이 아니다. 올바른 구현에서도 통과한다.
 * "이 표현은 이런 대가를 치른다"를 숫자로 못 박는다.
 */
class AdjacencyMatrixGraphTest extends GraphContractTest {

    @Override
    protected Graph create(int vertexCount, boolean directed) {
        return new AdjacencyMatrixGraph(vertexCount, directed);
    }

    @Test
    @DisplayName("hasEdge 가 O(1) 이다 - 이 표현의 유일한 장점")
    void hasEdgeIsConstant() {
        // 인접 리스트는 목록을 훑어야 하지만 여기는 배열 한 번 읽기다.
        AdjacencyMatrixGraph g = new AdjacencyMatrixGraph(5, true);
        g.addEdge(0, 4, 7);

        assertEquals(7, g.matrix[0][4], "표에 직접 적혀 있다");
        assertEquals(Graph.NO_EDGE, g.matrix[0][1]);
        assertTrue(g.hasEdge(0, 4));
    }

    @Test
    @DisplayName("한계: 간선이 하나여도 V x V 칸을 쓴다")
    void allocatesSquareRegardlessOfEdges() {
        final int n = 1_000;
        AdjacencyMatrixGraph g = new AdjacencyMatrixGraph(n, true);
        g.addEdge(0, 1);

        long cells = (long) g.matrix.length * g.matrix[0].length;
        assertEquals((long) n * n, cells,
            "간선은 1개인데 칸은 " + cells + "개다. 인접 리스트라면 1개다.\n"
                + "정점이 10만 개면 100억 칸이라 아예 만들 수 없다.");
    }

    @Test
    @DisplayName("한계: 이웃이 없어도 V 칸을 훑는다")
    void neighborsScansEveryColumn() {
        // 이웃이 하나도 없는 정점의 neighbors 도 V 칸을 다 봐야 한다.
        // 그래서 이 표현 위에서 BFS/DFS 는 O(V + E) 가 아니라 O(V^2) 이 된다.
        final int n = 500;
        AdjacencyMatrixGraph g = new AdjacencyMatrixGraph(n, true);
        g.addEdge(0, 1);

        assertEquals(n, g.matrix[499].length,
            "정점 499 는 이웃이 없지만 neighbors 는 " + n + "칸을 훑어야 그걸 안다");

        int found = 0;
        for (int ignored : g.neighbors(499)) found++;
        assertEquals(0, found, "결과는 비어 있다. 비었다는 것을 아는 데 V 번이 든다");
    }

    @Test
    @DisplayName("무방향 간선은 표의 두 칸을 채운다")
    void undirectedFillsBothCells() {
        AdjacencyMatrixGraph g = new AdjacencyMatrixGraph(3, false);
        g.addEdge(0, 2, 4);

        assertEquals(4, g.matrix[0][2]);
        assertEquals(4, g.matrix[2][0]);
        assertEquals(1, g.edgeCount(), "두 칸을 채워도 간선은 하나다");
    }
}
