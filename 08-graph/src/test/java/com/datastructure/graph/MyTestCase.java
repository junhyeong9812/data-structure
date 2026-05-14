package com.datastructure.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

public class MyTestCase {

    @Nested @DisplayName("인접 리스트 그래프 (무방향)")
    class AdjacencyListGraphTest {

    }

    @Nested @DisplayName("인접 리스트 그래프 (방향)")
    class DirectedAdjacencyListGraphTest {}

    @Nested @DisplayName("인접 행렬 그래프 (무방향)")
    class AdjacencyMatrixGraphTest {}

    @Nested @DisplayName("인접 행렬 그래프 (방향)")
    class DirectedAdjacencyMatrixGraphTest {}

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
