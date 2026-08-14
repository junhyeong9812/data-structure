package com.datastructure.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Graph 계약 테스트. 두 구현이 물려받는다.
 *
 * 이웃의 순서는 계약이 아니다. 구현마다 다르므로 집합으로만 비교한다.
 * 순서를 가정하는 순간 한쪽 구현에 묶인 테스트가 된다.
 */
abstract class GraphContractTest {

    protected abstract Graph create(int vertexCount, boolean directed);

    protected static Set<Integer> neighborSet(Graph g, int v) {
        Set<Integer> out = new HashSet<>();
        g.neighbors(v).forEach(out::add);
        return out;
    }

    protected static java.util.List<Integer> neighborList(Graph g, int v) {
        java.util.List<Integer> out = new ArrayList<>();
        g.neighbors(v).forEach(out::add);
        return out;
    }

    @Nested
    @DisplayName("무방향 그래프")
    class Undirected {

        @Test
        @DisplayName("간선은 양쪽에서 보인다")
        void edgeIsSymmetric() {
            Graph g = create(4, false);
            g.addEdge(0, 1);

            assertTrue(g.hasEdge(0, 1));
            assertTrue(g.hasEdge(1, 0), "무방향이면 반대로도 보여야 한다");
            assertEquals(Set.of(1), neighborSet(g, 0));
            assertEquals(Set.of(0), neighborSet(g, 1));
        }

        @Test
        @DisplayName("간선 개수는 한 번만 센다")
        void countsEdgeOnce() {
            Graph g = create(4, false);
            g.addEdge(0, 1);
            g.addEdge(1, 2);
            assertEquals(2, g.edgeCount(), "u-v 를 양쪽에 저장해도 간선은 하나다");
        }

        @Test
        @DisplayName("같은 간선을 다시 이으면 가중치만 바뀐다")
        void reAddReplacesWeight() {
            Graph g = create(3, false);
            g.addEdge(0, 1, 5);
            g.addEdge(0, 1, 9);

            assertEquals(1, g.edgeCount(), "간선이 두 개가 되면 안 된다");
            assertEquals(9, g.weight(0, 1));
            assertEquals(9, g.weight(1, 0));
            assertEquals(java.util.List.of(1), neighborList(g, 0), "이웃 목록에도 하나만 있어야 한다");
        }
    }

    @Nested
    @DisplayName("방향 그래프")
    class Directed {

        @Test
        @DisplayName("간선은 한 방향으로만 보인다")
        void edgeIsOneWay() {
            Graph g = create(4, true);
            g.addEdge(0, 1);

            assertTrue(g.hasEdge(0, 1));
            assertFalse(g.hasEdge(1, 0), "방향 그래프에서 반대로는 안 보인다");
            assertEquals(Set.of(1), neighborSet(g, 0));
            assertTrue(neighborSet(g, 1).isEmpty());
        }

        @Test
        @DisplayName("양방향은 간선 두 개다")
        void bothDirectionsAreTwoEdges() {
            Graph g = create(3, true);
            g.addEdge(0, 1);
            g.addEdge(1, 0);
            assertEquals(2, g.edgeCount());
        }
    }

    @Nested
    @DisplayName("가중치")
    class Weights {

        @Test
        void storesAndReads() {
            Graph g = create(3, true);
            g.addEdge(0, 1, 7);
            assertEquals(7, g.weight(0, 1));
            assertEquals(1, g.weight(0, 2) == Graph.NO_EDGE ? 1 : 0, "없는 간선은 NO_EDGE");
        }

        @Test
        @DisplayName("가중치를 안 주면 1 이다")
        void defaultWeightIsOne() {
            Graph g = create(3, true);
            g.addEdge(0, 1);
            assertEquals(1, g.weight(0, 1));
        }

        @Test
        @DisplayName("음수 가중치는 거부한다")
        void rejectsNegativeWeight() {
            Graph g = create(3, true);
            assertThrows(IllegalArgumentException.class, () -> g.addEdge(0, 1, -1));
            assertEquals(0, g.edgeCount(), "실패한 추가가 개수를 늘리면 안 된다");
        }
    }

    @Nested
    @DisplayName("경계")
    class Bounds {

        @Test
        void rejectsBadVertices() {
            Graph g = create(3, true);
            assertThrows(IndexOutOfBoundsException.class, () -> g.addEdge(0, 3));
            assertThrows(IndexOutOfBoundsException.class, () -> g.addEdge(-1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> g.hasEdge(0, 3));
            assertThrows(IndexOutOfBoundsException.class, () -> g.neighbors(3));
        }

        @Test
        @DisplayName("자기 자신으로 가는 간선")
        void selfLoop() {
            Graph g = create(3, true);
            g.addEdge(1, 1);
            assertTrue(g.hasEdge(1, 1));
            assertEquals(1, g.edgeCount());
            assertEquals(Set.of(1), neighborSet(g, 1));
        }

        @Test
        @DisplayName("정점이 없거나 간선이 없어도 된다")
        void emptyGraph() {
            Graph empty = create(0, false);
            assertEquals(0, empty.vertexCount());
            assertEquals(0, empty.edgeCount());

            Graph isolated = create(3, false);
            assertEquals(0, isolated.edgeCount());
            for (int v = 0; v < 3; v++) assertTrue(neighborSet(isolated, v).isEmpty());
        }

        @Test
        @DisplayName("이웃 목록을 고쳐도 그래프는 안 변한다")
        void neighborsIsDefensive() {
            Graph g = create(3, true);
            g.addEdge(0, 1);
            java.util.List<Integer> copy = neighborList(g, 0);
            copy.clear();

            assertEquals(Set.of(1), neighborSet(g, 0), "내부 목록을 그대로 주면 안 된다");
        }
    }
}
