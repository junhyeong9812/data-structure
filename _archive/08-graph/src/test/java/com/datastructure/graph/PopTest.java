package com.datastructure.graph;

import com.datastructure.graph.pop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("그래프 - POP 구현 테스트")
class PopTest {

    @Nested
    @DisplayName("무방향 그래프 기본")
    class UndirectedGraphBasicTest {
        
        private Graph graph;

        @BeforeEach
        void setUp() {
            graph = new Graph();
        }

        @Test
        @DisplayName("01. 정점을 추가할 수 있다")
        void test01_addVertex() {
            // TODO: addVertex() 구현 후 테스트
            // graph.addVertex(1);
            // assertThat(graph.hasVertex(1)).isTrue();
        }

        @Test
        @DisplayName("02. 간선을 추가할 수 있다")
        void test02_addEdge() {
            // TODO: addEdge() 구현 후 테스트
            // graph.addEdge(1, 2);
            // assertThat(graph.hasEdge(1, 2)).isTrue();
            // assertThat(graph.hasEdge(2, 1)).isTrue();  // 무방향
        }

        @Test
        @DisplayName("03. 인접 정점을 조회할 수 있다")
        void test03_getNeighbors() {
            // TODO: getNeighbors() 구현 후 테스트
            // graph.addEdge(1, 2);
            // graph.addEdge(1, 3);
            // assertThat(graph.getNeighbors(1)).containsExactlyInAnyOrder(2, 3);
        }

        @Test
        @DisplayName("04. 정점 개수를 확인할 수 있다")
        void test04_vertexCount() {
            // TODO: vertexCount() 구현 후 테스트
            // graph.addVertex(1);
            // graph.addVertex(2);
            // graph.addVertex(3);
            // assertThat(graph.vertexCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("05. 간선 개수를 확인할 수 있다")
        void test05_edgeCount() {
            // TODO: edgeCount() 구현 후 테스트
            // graph.addEdge(1, 2);
            // graph.addEdge(2, 3);
            // assertThat(graph.edgeCount()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("BFS/DFS 순회")
    class TraversalTest {

        private Graph graph;

        @BeforeEach
        void setUp() {
            graph = new Graph();
            // 0 - 1 - 3
            // |   |
            // 2 - 4
        }

        @Test
        @DisplayName("06. BFS로 그래프를 순회할 수 있다")
        void test06_bfs() {
            // TODO: bfs() 구현 후 테스트
            // graph.addEdge(0, 1);
            // graph.addEdge(0, 2);
            // graph.addEdge(1, 3);
            // graph.addEdge(1, 4);
            // graph.addEdge(2, 4);
            // List<Integer> result = graph.bfs(0);
            // assertThat(result).hasSize(5);
            // assertThat(result.get(0)).isEqualTo(0);
        }

        @Test
        @DisplayName("07. DFS로 그래프를 순회할 수 있다")
        void test07_dfs() {
            // TODO: dfs() 구현 후 테스트
            // graph.addEdge(0, 1);
            // graph.addEdge(0, 2);
            // graph.addEdge(1, 3);
            // List<Integer> result = graph.dfs(0);
            // assertThat(result).hasSize(4);
            // assertThat(result.get(0)).isEqualTo(0);
        }

        @Test
        @DisplayName("08. DFS 반복 버전")
        void test08_dfsIterative() {
            // TODO: dfsIterative() 구현 후 테스트
            // graph.addEdge(0, 1);
            // graph.addEdge(0, 2);
            // List<Integer> result = graph.dfsIterative(0);
            // assertThat(result).containsExactlyInAnyOrder(0, 1, 2);
        }

        @Test
        @DisplayName("09. 연결되지 않은 그래프에서 부분 순회")
        void test09_disconnectedGraph() {
            // TODO: 연결되지 않은 그래프 테스트
            // graph.addEdge(0, 1);
            // graph.addEdge(2, 3);
            // List<Integer> result = graph.bfs(0);
            // assertThat(result).containsExactlyInAnyOrder(0, 1);
        }
    }

    @Nested
    @DisplayName("최단 경로")
    class ShortestPathTest {

        @Test
        @DisplayName("10. BFS로 비가중치 최단 경로 찾기")
        void test10_shortestPathBfs() {
            // TODO: shortestPath() 구현 후 테스트
            // Graph graph = new Graph();
            // graph.addEdge(0, 1);
            // graph.addEdge(1, 2);
            // graph.addEdge(0, 3);
            // graph.addEdge(3, 2);
            // List<Integer> path = graph.shortestPath(0, 2);
            // assertThat(path).hasSize(3);
        }

        @Test
        @DisplayName("11. 경로가 없으면 빈 리스트 반환")
        void test11_noPath() {
            // TODO: 경로 없음 테스트
            // Graph graph = new Graph();
            // graph.addVertex(0);
            // graph.addVertex(1);
            // List<Integer> path = graph.shortestPath(0, 1);
            // assertThat(path).isEmpty();
        }
    }

    @Nested
    @DisplayName("방향 그래프")
    class DirectedGraphTest {

        private DirectedGraph graph;

        @BeforeEach
        void setUp() {
            graph = new DirectedGraph();
        }

        @Test
        @DisplayName("12. 방향 간선은 단방향이다")
        void test12_directedEdge() {
            // TODO: DirectedGraph 구현 후 테스트
            // graph.addEdge(1, 2);
            // assertThat(graph.hasEdge(1, 2)).isTrue();
            // assertThat(graph.hasEdge(2, 1)).isFalse();
        }

        @Test
        @DisplayName("13. 사이클을 탐지할 수 있다")
        void test13_hasCycle() {
            // TODO: hasCycle() 구현 후 테스트
            // graph.addEdge(0, 1);
            // graph.addEdge(1, 2);
            // graph.addEdge(2, 0);
            // assertThat(graph.hasCycle()).isTrue();
        }

        @Test
        @DisplayName("14. 사이클이 없으면 false 반환")
        void test14_noCycle() {
            // TODO: 사이클 없음 테스트
            // graph.addEdge(0, 1);
            // graph.addEdge(1, 2);
            // assertThat(graph.hasCycle()).isFalse();
        }

        @Test
        @DisplayName("15. 위상 정렬")
        void test15_topologicalSort() {
            // TODO: topologicalSort() 구현 후 테스트
            // graph.addEdge(5, 0);
            // graph.addEdge(5, 2);
            // graph.addEdge(4, 0);
            // graph.addEdge(4, 1);
            // graph.addEdge(2, 3);
            // graph.addEdge(3, 1);
            // List<Integer> sorted = graph.topologicalSort();
            // assertThat(sorted.indexOf(5)).isLessThan(sorted.indexOf(0));
        }
    }

    @Nested
    @DisplayName("가중치 그래프")
    class WeightedGraphTest {

        private WeightedGraph graph;

        @BeforeEach
        void setUp() {
            graph = new WeightedGraph();
        }

        @Test
        @DisplayName("16. 가중치 간선 추가")
        void test16_addWeightedEdge() {
            // TODO: 가중치 간선 테스트
            // graph.addEdge(0, 1, 5);
            // assertThat(graph.getWeight(0, 1)).isEqualTo(5);
        }

        @Test
        @DisplayName("17. 다익스트라 최단 거리")
        void test17_dijkstra() {
            // TODO: dijkstra() 구현 후 테스트
            // graph.addEdge(0, 1, 4);
            // graph.addEdge(0, 2, 1);
            // graph.addEdge(2, 1, 2);
            // Map<Integer, Integer> distances = graph.dijkstra(0);
            // assertThat(distances.get(1)).isEqualTo(3);  // 0→2→1
        }

        @Test
        @DisplayName("18. 다익스트라 경로 추적")
        void test18_dijkstraPath() {
            // TODO: dijkstraPath() 구현 후 테스트
            // graph.addEdge(0, 1, 4);
            // graph.addEdge(0, 2, 1);
            // graph.addEdge(2, 1, 2);
            // List<Integer> path = graph.dijkstraPath(0, 1);
            // assertThat(path).containsExactly(0, 2, 1);
        }

        @Test
        @DisplayName("19. 벨만-포드 (음의 가중치)")
        void test19_bellmanFord() {
            // TODO: bellmanFord() 구현 후 테스트
            // graph.addDirectedEdge(0, 1, 4);
            // graph.addDirectedEdge(1, 2, -2);
            // graph.addDirectedEdge(0, 2, 3);
            // Map<Integer, Integer> distances = graph.bellmanFord(0);
            // assertThat(distances.get(2)).isEqualTo(2);  // 0→1→2
        }
    }

    @Nested
    @DisplayName("그래프 문제")
    class GraphProblemsTest {

        @Test
        @DisplayName("20. 연결 요소 개수")
        void test20_connectedComponents() {
            // TODO: GraphProblems.connectedComponents() 구현 후 테스트
            // Graph graph = new Graph();
            // graph.addEdge(0, 1);
            // graph.addEdge(2, 3);
            // graph.addVertex(4);
            // int count = GraphProblems.connectedComponents(graph);
            // assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("21. 이분 그래프 판별")
        void test21_isBipartite() {
            // TODO: isBipartite() 구현 후 테스트
            // Graph graph = new Graph();
            // graph.addEdge(0, 1);
            // graph.addEdge(1, 2);
            // graph.addEdge(2, 3);
            // graph.addEdge(3, 0);
            // assertThat(GraphProblems.isBipartite(graph)).isTrue();
        }

        @Test
        @DisplayName("22. 이분 그래프가 아닌 경우")
        void test22_notBipartite() {
            // TODO: 이분 그래프 아님 테스트
            // Graph graph = new Graph();
            // graph.addEdge(0, 1);
            // graph.addEdge(1, 2);
            // graph.addEdge(2, 0);  // 삼각형
            // assertThat(GraphProblems.isBipartite(graph)).isFalse();
        }

        @Test
        @DisplayName("23. 섬의 개수 (그리드)")
        void test23_numberOfIslands() {
            // TODO: numberOfIslands() 구현 후 테스트
            // char[][] grid = {
            //     {'1', '1', '0', '0'},
            //     {'1', '0', '0', '1'},
            //     {'0', '0', '1', '1'}
            // };
            // assertThat(GraphProblems.numberOfIslands(grid)).isEqualTo(3);
        }

        @Test
        @DisplayName("24. 썩은 오렌지 (BFS)")
        void test24_rottingOranges() {
            // TODO: rottingOranges() 구현 후 테스트
            // int[][] grid = {
            //     {2, 1, 1},
            //     {1, 1, 0},
            //     {0, 1, 1}
            // };
            // assertThat(GraphProblems.rottingOranges(grid)).isEqualTo(4);
        }

        @Test
        @DisplayName("25. 복제 그래프")
        void test25_cloneGraph() {
            // TODO: cloneGraph() 구현 후 테스트
            // 그래프 복제 테스트
        }
    }

    @Nested
    @DisplayName("MST")
    class MSTTest {

        @Test
        @DisplayName("26. 프림 MST")
        void test26_prim() {
            // TODO: prim() 구현 후 테스트
            // WeightedGraph graph = new WeightedGraph();
            // graph.addEdge(0, 1, 4);
            // graph.addEdge(0, 2, 3);
            // graph.addEdge(1, 2, 1);
            // graph.addEdge(1, 3, 2);
            // graph.addEdge(2, 3, 4);
            // int mstWeight = graph.primMST();
            // assertThat(mstWeight).isEqualTo(6);
        }

        @Test
        @DisplayName("27. 크루스칼 MST")
        void test27_kruskal() {
            // TODO: kruskal() 구현 후 테스트
            // WeightedGraph graph = new WeightedGraph();
            // // 간선 추가...
            // List<int[]> mst = graph.kruskalMST();
            // assertThat(mst).hasSize(n - 1);
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("28. 정점/간선 삭제")
        void test28_removeVertexEdge() {
            // TODO: remove 기능 테스트
            // Graph graph = new Graph();
            // graph.addEdge(0, 1);
            // graph.addEdge(1, 2);
            // graph.removeEdge(0, 1);
            // assertThat(graph.hasEdge(0, 1)).isFalse();
        }

        @Test
        @DisplayName("29. 플로이드-워셜")
        void test29_floydWarshall() {
            // TODO: floydWarshall() 구현 후 테스트
            // int[][] dist = GraphProblems.floydWarshall(graph);
            // assertThat(dist[0][2]).isEqualTo(expectedDistance);
        }

        @Test
        @DisplayName("30. 강한 연결 요소 (SCC)")
        void test30_stronglyConnectedComponents() {
            // TODO: SCC 구현 후 테스트
            // DirectedGraph graph = new DirectedGraph();
            // // ...
            // int sccCount = GraphProblems.countSCC(graph);
        }
    }
}
