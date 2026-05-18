package com.datastructure.graph;

import com.datastructure.graph.pop.Graph;
import com.sun.jdi.request.DuplicateRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class MyTestCase {

    // ===== 기본 그래프 구현체 (CRUD) =====

    @Nested @DisplayName("Graph (무방향, 인접 리스트) 테스트")
    class GraphTest {

        private Graph graph;

        @BeforeEach
        void setup() {
            graph = new Graph();
        }

        @Nested @DisplayName("addVertex 테스트")
        class AddVertexTest {

            @Test @DisplayName("빈 Graph에 정점을 추가할 수 있다")
            void add_vertex_to_empty_graph() {
                assertThat(graph.isEmpty()).isTrue();

                graph.addVertex(1);

                assertThat(graph.isEmpty()).isFalse();
                assertThat(graph.vertexCount()).isEqualTo(1);
            }

            @Test @DisplayName("요소가 있는 Graph에 정점을 추가할 수 있다")
            void add_vertex_to_non_empty_graph() {
                graph.addVertex(1);
                assertThat(graph.vertexCount()).isEqualTo(1);

                graph.addVertex(2);
                assertThat(graph.vertexCount()).isEqualTo(2);
            }

            @Test @DisplayName("이미 존재하는 정점을 추가하면 예외가 발생한다")
            void add_duplicate_vertex_throws_exception() {
                graph.addVertex(1);
                assertThat(graph.vertexCount()).isEqualTo(1);

                assertThatThrownBy(() -> graph.addVertex(1))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("정점 추가 후 vertexCount가 증가한다")
            void add_vertex_increases_vertex_count() {
                graph.addVertex(1);
                assertThat(graph.vertexCount()).isEqualTo(1);

                graph.addVertex(2);
                assertThat(graph.vertexCount()).isEqualTo(2);

                graph.addVertex(3);
                assertThat(graph.vertexCount()).isEqualTo(3);

                graph.addVertex(4);
                assertThat(graph.vertexCount()).isEqualTo(4);
            }

            @Test @DisplayName("정점 추가 후 hasVertex가 true를 반환한다")
            void add_vertex_then_has_vertex_returns_true() {
                assertThat(graph.hasVertex(1)).isFalse();

                graph.addVertex(1);
                assertThat(graph.hasVertex(1)).isTrue();
            }
        }

        @Nested @DisplayName("addEdge 테스트")
        class AddEdgeTest {

            @Test @DisplayName("두 정점 사이에 간선을 추가 할 수 있다")
            void add_edge_between_two_vertices() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1, 2);

                assertThat(graph.hasEdge(1, 2)).isTrue();
            }

            @Test @DisplayName("무방향이므로 양쪽 다 연결 할 수 있다")
            void add_edge_connects_both_directions() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);
                assertThat(graph.hasEdge(1, 2)).isTrue();
                assertThat(graph.hasEdge(2, 1)).isTrue();
            }

            @Test @DisplayName("존재하지 않는 정점에 간선 추가 시 예외가 발생한다")
            void add_edge_to_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.addEdge(1, 2))
                        .isInstanceOf(IllegalArgumentException.class);

                graph.addVertex(1);
                assertThatThrownBy(() -> graph.addEdge(1, 2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("이미 존재하는 간선 중복 추가 시 예외가 발생한다")
            void add_duplicate_edge_throws_exception() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);
                assertThat(graph.hasEdge(1, 2)).isTrue();

                assertThatThrownBy(() -> graph.addEdge(1, 2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("자기 루프 시 예외가 발생한다")
            void add_self_loop_throws_exception() {
                graph.addVertex(1);

                assertThatThrownBy(() -> graph.addEdge(1, 1))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선 추가 후 edgeCount가 증가한다")
            void add_edge_increases_edge_count() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);

                graph.addEdge(1, 2);
                assertThat(graph.edgeCount()).isEqualTo(1);

                graph.addEdge(2, 3);
                assertThat(graph.edgeCount()).isEqualTo(2);

                graph.addEdge(1, 3);
                assertThat(graph.edgeCount()).isEqualTo(3);
            }
        }

        @Nested @DisplayName("removeVertex 테스트")
        class RemoveVertexTest {
            @Test @DisplayName("존재하지 않는 정점을 제거하면 예외가 발생한다")
            void remove_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.removeVertex(1))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("정점이 하나일 때 제거하면 빈 그래프가 된다")
            void remove_single_vertex_makes_empty_graph() {
                graph.addVertex(1);
                assertThat(graph.isEmpty()).isFalse();

                graph.removeVertex(1);

                assertThat(graph.isEmpty()).isTrue();
            }

            @Test @DisplayName("간선이 없는 정점을 제거할 수 있다")
            void remove_vertex_without_edges() {
                graph.addVertex(1);
                graph.addVertex(2);
                assertThat(graph.vertexCount()).isEqualTo(2);

                graph.removeVertex(2);

                assertThat(graph.vertexCount()).isEqualTo(1);
            }

            @Test @DisplayName("정점 제거 시 연결된 간선도 함께 제거된다")
            void remove_vertex_also_removes_connected_edges() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);
                assertThat(graph.vertexCount()).isEqualTo(2);
                assertThat(graph.edgeCount()).isEqualTo(1);

                graph.removeVertex(2);

                assertThat(graph.vertexCount()).isEqualTo(1);
                assertThat(graph.edgeCount()).isEqualTo(0);
            }

            @Test @DisplayName("정점 제거 후 vertexCount가 감소한다")
            void remove_vertex_decreases_vertex_count() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);
                assertThat(graph.vertexCount()).isEqualTo(3);

                graph.removeVertex(3);
                assertThat(graph.vertexCount()).isEqualTo(2);

                graph.removeVertex(2);
                assertThat(graph.vertexCount()).isEqualTo(1);

                graph.removeVertex(1);
                assertThat(graph.vertexCount()).isEqualTo(0);
            }

            @Test @DisplayName("정점 제거 후 edgeCount가 감소한다")
            void remove_vertex_decreases_edge_count() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);
                graph.addEdge(1, 2);
                graph.addEdge(2, 3);
                graph.addEdge(1, 3);
                assertThat(graph.edgeCount()).isEqualTo(3);

                graph.removeVertex(1);
                assertThat(graph.edgeCount()).isEqualTo(1);

                graph.removeVertex(2);
                assertThat(graph.edgeCount()).isEqualTo(0);
            }
        }

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