package com.datastructure.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/** AdjacencyListGraph 가 계약을 지키는지 + 인접 리스트 고유 성질. */
class AdjacencyListGraphTest extends GraphContractTest {

    @Override
    protected Graph create(int vertexCount, boolean directed) {
        return new AdjacencyListGraph(vertexCount, directed);
    }

    @Test
    @DisplayName("없는 간선에는 자리를 쓰지 않는다")
    void usesNoSpaceForAbsentEdges() {
        AdjacencyListGraph g = new AdjacencyListGraph(1_000, true);
        g.addEdge(0, 1);

        int stored = 0;
        for (int v = 0; v < g.vertexCount(); v++) stored += g.adjacency[v].size();

        assertEquals(1, stored,
            "정점이 1000개인데 저장된 항목은 " + stored + "개다. 인접 행렬이라면 100만 칸이다");
    }

    @Test
    @DisplayName("무방향 간선은 양쪽 목록에 저장된다")
    void undirectedStoresBothSides() {
        AdjacencyListGraph g = new AdjacencyListGraph(3, false);
        g.addEdge(0, 1);

        assertEquals(1, g.adjacency[0].size());
        assertEquals(1, g.adjacency[1].size());
        assertEquals(1, g.edgeCount(), "저장은 두 번이지만 간선은 하나다");
    }

    @Test
    @DisplayName("같은 간선을 다시 이어도 목록이 늘지 않는다")
    void reAddDoesNotGrowList() {
        AdjacencyListGraph g = new AdjacencyListGraph(3, true);
        g.addEdge(0, 1, 5);
        g.addEdge(0, 1, 9);

        assertEquals(1, g.adjacency[0].size(), "찾아서 덮어써야 한다. 그냥 넣으면 중복된다");
        assertEquals(9, g.adjacency[0].get(0).weight);
    }

    @Test
    @DisplayName("20만 정점 희소 그래프를 5초 안에 만들고 훑는다")
    void handlesLargeSparseGraph() {
        // 인접 행렬로는 이 크기를 아예 만들 수 없다. 20만^2 = 400억 칸이다.
        // 현실의 그래프(도로망, 소셜, 의존성)는 대개 이렇게 희소하다.
        final int n = 200_000;
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            AdjacencyListGraph g = new AdjacencyListGraph(n, false);
            for (int i = 0; i + 1 < n; i++) g.addEdge(i, i + 1);

            assertEquals(n - 1, g.edgeCount());
            int total = 0;
            for (int v = 0; v < n; v++) {
                for (int ignored : g.neighbors(v)) total++;
            }
            assertEquals(2 * (n - 1), total, "각 간선이 양쪽에서 한 번씩 보인다");
        });
    }
}
