package com.datastructure.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

public class MyTestCase {

    // ===== 기본 그래프 구현체 (CRUD) =====

    @Nested @DisplayName("Graph (무방향, 인접 리스트) 테스트")
    class GraphTest {
        @Nested @DisplayName("addVertex 테스트")
        class AddVertexTest {}

        @Nested @DisplayName("addEdge 테스트")
        class AddEdgeTest {}

        @Nested @DisplayName("removeVertex 테스트")
        class RemoveVertexTest {}

        @Nested @DisplayName("removeEdge 테스트")
        class RemoveEdgeTest {}

        @Nested @DisplayName("hasVertex 테스트")
        class HasVertexTest {}

        @Nested @DisplayName("hasEdge 테스트")
        class HasEdgeTest {}

        @Nested @DisplayName("getNeighbors 테스트")
        class GetNeighborsTest {}

        @Nested @DisplayName("vertexCount / edgeCount 테스트")
        class CountTest {}
    }

    @Nested @DisplayName("DirectedGraph (방향, 인접 리스트) 테스트")
    class DirectedGraphTest {
        @Nested @DisplayName("addEdge 테스트")
        class AddEdgeTest {}

        @Nested @DisplayName("removeEdge 테스트")
        class RemoveEdgeTest {}

        @Nested @DisplayName("hasEdge 테스트")
        class HasEdgeTest {}

        @Nested @DisplayName("getNeighbors 테스트")
        class GetNeighborsTest {}
    }

    @Nested @DisplayName("WeightedGraph (가중치, 인접 리스트) 테스트")
    class WeightedGraphTest {
        @Nested @DisplayName("addEdge 가중치 간선 테스트")
        class AddEdgeTest {}
    }

    // ===== 인접 행렬 구현체 (CRUD) =====

    @Nested @DisplayName("AdjacencyMatrixGraph (무방향, 인접 행렬) 테스트")
    class AdjacencyMatrixGraphTest {
        @Nested @DisplayName("addVertex 테스트")
        class AddVertexTest {}

        @Nested @DisplayName("addEdge 테스트")
        class AddEdgeTest {}

        @Nested @DisplayName("removeVertex 테스트")
        class RemoveVertexTest {}

        @Nested @DisplayName("removeEdge 테스트")
        class RemoveEdgeTest {}

        @Nested @DisplayName("hasEdge 테스트")
        class HasEdgeTest {}
    }

    @Nested @DisplayName("DirectedAdjacencyMatrixGraph (방향, 인접 행렬) 테스트")
    class DirectedAdjacencyMatrixGraphTest {
        @Nested @DisplayName("addEdge 테스트")
        class AddEdgeTest {}

        @Nested @DisplayName("hasEdge 테스트")
        class HasEdgeTest {}
    }

    // ===== 응용 알고리즘 =====

    @Nested @DisplayName("GraphProblems 테스트")
    class GraphProblemsTest {
        @Nested @DisplayName("BFS 테스트")
        class BfsTest {}

        @Nested @DisplayName("DFS 테스트")
        class DfsTest {}

        @Nested @DisplayName("최단 경로 (비가중치) 테스트")
        class ShortestPathTest {}

        @Nested @DisplayName("다익스트라 테스트")
        class DijkstraTest {}

        @Nested @DisplayName("벨만-포드 테스트")
        class BellmanFordTest {}

        @Nested @DisplayName("플로이드-워셜 테스트")
        class FloydWarshallTest {}

        @Nested @DisplayName("사이클 탐지 테스트")
        class CycleDetectionTest {}

        @Nested @DisplayName("위상 정렬 테스트")
        class TopologicalSortTest {}

        @Nested @DisplayName("연결 요소 테스트")
        class ConnectedComponentsTest {}

        @Nested @DisplayName("이분 그래프 테스트")
        class BipartiteTest {}

        @Nested @DisplayName("프림 MST 테스트")
        class PrimTest {}

        @Nested @DisplayName("크루스칼 MST 테스트")
        class KruskalTest {}
    }
}