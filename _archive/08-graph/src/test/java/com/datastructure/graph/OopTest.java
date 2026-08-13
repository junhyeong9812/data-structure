package com.datastructure.graph;

import com.datastructure.graph.oop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("그래프 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("인접 리스트 그래프")
    class AdjacencyListGraphTest {

        private GraphInterface<Integer> graph;

        @BeforeEach
        void setUp() {
            graph = new AdjacencyListGraph<>();
        }

        @Test
        @DisplayName("01. 정점 추가")
        void test01_addVertex() {
            // TODO: addVertex() 구현 후 테스트
            // graph.addVertex(1);
            // assertThat(graph.containsVertex(1)).isTrue();
        }

        @Test
        @DisplayName("02. 간선 추가")
        void test02_addEdge() {
            // TODO: addEdge() 구현 후 테스트
            // graph.addEdge(1, 2);
            // assertThat(graph.containsEdge(1, 2)).isTrue();
        }

        @Test
        @DisplayName("03. BFS 순회")
        void test03_bfs() {
            // TODO: bfs() 구현 후 테스트
            // graph.addEdge(0, 1);
            // graph.addEdge(0, 2);
            // assertThat(graph.bfs(0)).containsExactlyInAnyOrder(0, 1, 2);
        }

        @Test
        @DisplayName("04. DFS 순회")
        void test04_dfs() {
            // TODO: dfs() 구현 후 테스트
            // graph.addEdge(0, 1);
            // graph.addEdge(0, 2);
            // assertThat(graph.dfs(0)).containsExactlyInAnyOrder(0, 1, 2);
        }

        @Test
        @DisplayName("05. 인접 정점 조회")
        void test05_neighbors() {
            // TODO: neighbors() 구현 후 테스트
            // graph.addEdge(0, 1);
            // graph.addEdge(0, 2);
            // assertThat(graph.neighbors(0)).containsExactlyInAnyOrder(1, 2);
        }
    }

    @Nested
    @DisplayName("인접 행렬 그래프")
    class AdjacencyMatrixGraphTest {

        private AdjacencyMatrixGraph graph;

        @BeforeEach
        void setUp() {
            graph = new AdjacencyMatrixGraph(5);
        }

        @Test
        @DisplayName("06. 간선 추가 (행렬)")
        void test06_addEdgeMatrix() {
            // TODO: addEdge() 구현 후 테스트
            // graph.addEdge(0, 1);
            // assertThat(graph.containsEdge(0, 1)).isTrue();
        }

        @Test
        @DisplayName("07. 간선 확인 O(1)")
        void test07_containsEdge() {
            // TODO: containsEdge() 구현 후 테스트
            // graph.addEdge(0, 1);
            // graph.addEdge(1, 2);
            // assertThat(graph.containsEdge(0, 2)).isFalse();
        }

        @Test
        @DisplayName("08. 인접 행렬 반환")
        void test08_getMatrix() {
            // TODO: getMatrix() 구현 후 테스트
            // graph.addEdge(0, 1);
            // int[][] matrix = graph.getMatrix();
            // assertThat(matrix[0][1]).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("제네릭 그래프")
    class GenericGraphTest {

        @Test
        @DisplayName("09. String 정점 그래프")
        void test09_stringGraph() {
            // TODO: 제네릭 테스트
            // GraphInterface<String> graph = new AdjacencyListGraph<>();
            // graph.addEdge("A", "B");
            // graph.addEdge("B", "C");
            // assertThat(graph.bfs("A")).containsExactlyInAnyOrder("A", "B", "C");
        }

        @Test
        @DisplayName("10. 사용자 정의 객체 정점")
        void test10_customObjectGraph() {
            // TODO: 사용자 정의 객체 테스트
            // record City(String name) {}
            // GraphInterface<City> graph = new AdjacencyListGraph<>();
            // City seoul = new City("Seoul");
            // City busan = new City("Busan");
            // graph.addEdge(seoul, busan);
            // assertThat(graph.neighbors(seoul)).contains(busan);
        }
    }

    @Nested
    @DisplayName("가중치 그래프")
    class WeightedGraphImplTest {

        private WeightedGraphImpl<Integer> graph;

        @BeforeEach
        void setUp() {
            graph = new WeightedGraphImpl<>();
        }

        @Test
        @DisplayName("11. 가중치 간선 추가")
        void test11_addWeightedEdge() {
            // TODO: 가중치 간선 테스트
            // graph.addEdge(0, 1, 5.0);
            // assertThat(graph.getWeight(0, 1)).isEqualTo(5.0);
        }

        @Test
        @DisplayName("12. 다익스트라")
        void test12_dijkstra() {
            // TODO: dijkstra() 구현 후 테스트
            // graph.addEdge(0, 1, 4.0);
            // graph.addEdge(0, 2, 1.0);
            // graph.addEdge(2, 1, 2.0);
            // Map<Integer, Double> dist = graph.dijkstra(0);
            // assertThat(dist.get(1)).isEqualTo(3.0);
        }

        @Test
        @DisplayName("13. 최단 경로 반환")
        void test13_shortestPath() {
            // TODO: shortestPath() 구현 후 테스트
            // List<Integer> path = graph.shortestPath(0, 1);
            // assertThat(path).containsExactly(0, 2, 1);
        }
    }

    @Nested
    @DisplayName("그래프 알고리즘")
    class GraphAlgorithmsTest {

        @Test
        @DisplayName("14. 사이클 탐지")
        void test14_hasCycle() {
            // TODO: hasCycle() 구현 후 테스트
            // DirectedGraph graph = new DirectedGraphImpl<>();
            // graph.addEdge(0, 1);
            // graph.addEdge(1, 2);
            // graph.addEdge(2, 0);
            // assertThat(graph.hasCycle()).isTrue();
        }

        @Test
        @DisplayName("15. 위상 정렬")
        void test15_topologicalSort() {
            // TODO: topologicalSort() 구현 후 테스트
            // List<Integer> sorted = graph.topologicalSort();
            // // 순서 검증
        }

        @Test
        @DisplayName("16. 연결 요소")
        void test16_connectedComponents() {
            // TODO: connectedComponents() 구현 후 테스트
            // List<Set<Integer>> components = graph.connectedComponents();
            // assertThat(components).hasSize(expectedCount);
        }

        @Test
        @DisplayName("17. 이분 그래프 판별")
        void test17_isBipartite() {
            // TODO: isBipartite() 구현 후 테스트
            // assertThat(graph.isBipartite()).isTrue();
        }
    }

    @Nested
    @DisplayName("그래프 유틸리티")
    class GraphUtilitiesTest {

        @Test
        @DisplayName("18. 그래프 복사")
        void test18_copy() {
            // TODO: copy() 구현 후 테스트
            // GraphInterface<Integer> copy = graph.copy();
            // assertThat(copy).isEqualTo(graph);
        }

        @Test
        @DisplayName("19. 역방향 그래프")
        void test19_reverse() {
            // TODO: reverse() 구현 후 테스트
            // DirectedGraph reversed = graph.reverse();
            // assertThat(reversed.containsEdge(2, 1)).isTrue();
        }

        @Test
        @DisplayName("20. 부분 그래프 추출")
        void test20_subgraph() {
            // TODO: subgraph() 구현 후 테스트
            // Set<Integer> vertices = Set.of(0, 1, 2);
            // GraphInterface<Integer> sub = graph.subgraph(vertices);
        }
    }

    @Nested
    @DisplayName("Iterator / Stream")
    class IteratorStreamTest {

        @Test
        @DisplayName("21. 정점 Iterator")
        void test21_vertexIterator() {
            // TODO: Iterator 구현 후 테스트
            // for (Integer v : graph.vertices()) {
            //     // ...
            // }
        }

        @Test
        @DisplayName("22. 간선 Iterator")
        void test22_edgeIterator() {
            // TODO: 간선 Iterator 테스트
            // for (Edge<Integer> edge : graph.edges()) {
            //     // ...
            // }
        }

        @Test
        @DisplayName("23. Stream 지원")
        void test23_stream() {
            // TODO: stream() 테스트
            // long count = graph.vertexStream().count();
            // assertThat(count).isEqualTo(expectedCount);
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("24. equals")
        void test24_equals() {
            // TODO: equals() 구현 후 테스트
            // GraphInterface<Integer> g1 = new AdjacencyListGraph<>();
            // GraphInterface<Integer> g2 = new AdjacencyListGraph<>();
            // g1.addEdge(0, 1);
            // g2.addEdge(0, 1);
            // assertThat(g1).isEqualTo(g2);
        }

        @Test
        @DisplayName("25. hashCode")
        void test25_hashCode() {
            // TODO: hashCode() 구현 후 테스트
            // assertThat(g1.hashCode()).isEqualTo(g2.hashCode());
        }

        @Test
        @DisplayName("26. toString")
        void test26_toString() {
            // TODO: toString() 구현 후 테스트
            // assertThat(graph.toString()).contains("0", "1");
        }

        @Test
        @DisplayName("27. 차수(degree) 계산")
        void test27_degree() {
            // TODO: degree() 구현 후 테스트
            // graph.addEdge(0, 1);
            // graph.addEdge(0, 2);
            // assertThat(graph.degree(0)).isEqualTo(2);
        }

        @Test
        @DisplayName("28. 빈 그래프")
        void test28_emptyGraph() {
            // TODO: 빈 그래프 테스트
            // GraphInterface<Integer> graph = new AdjacencyListGraph<>();
            // assertThat(graph.isEmpty()).isTrue();
            // assertThat(graph.vertexCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("29. 대량 데이터")
        void test29_largeGraph() {
            // TODO: 대량 데이터 테스트
            // for (int i = 0; i < 10000; i++) {
            //     graph.addEdge(i, i + 1);
            // }
            // assertThat(graph.vertexCount()).isEqualTo(10001);
        }

        @Test
        @DisplayName("30. clear")
        void test30_clear() {
            // TODO: clear() 테스트
            // graph.addEdge(0, 1);
            // graph.clear();
            // assertThat(graph.isEmpty()).isTrue();
        }
    }
}
