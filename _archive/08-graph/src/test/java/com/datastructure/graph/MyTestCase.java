package com.datastructure.graph;

import com.datastructure.graph.pop.*;
import com.sun.jdi.request.DuplicateRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        class RemoveEdgeTest {
            @Test @DisplayName("존재하지 않는 간선을 제거하면 예외가 발생한다")
            void remove_non_existent_edge_throws_exception() {
                graph.addVertex(1);
                graph.addVertex(2);
                assertThatThrownBy(()-> graph.removeEdge(1, 2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("존재하지 않는 정점 간의 간선을 제거하면 예외가 발생한다")
            void remove_edge_with_non_existent_vertex_throws_exception() {
                assertThatThrownBy(()-> graph.removeEdge(1, 2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선 제거 후 양쪽 방향 모두 연결이 해제된다")
            void remove_edge_disconnects_both_directions() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);
                assertThat(graph.hasEdge(1,2)).isTrue();
                assertThat(graph.hasEdge(2,1)).isTrue();

                graph.removeEdge(1, 2);

                assertThat(graph.hasEdge(1,2)).isFalse();
                assertThat(graph.hasEdge(2,1)).isFalse();
            }

            @Test @DisplayName("간선 제거 후 정점은 유지된다")
            void remove_edge_keeps_vertices() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);
                assertThat(graph.hasVertex(1)).isTrue();
                assertThat(graph.hasVertex(2)).isTrue();

                graph.removeEdge(1, 2);

                assertThat(graph.hasVertex(1)).isTrue();
                assertThat(graph.hasVertex(2)).isTrue();
            }

            @Test @DisplayName("간선이 하나일 때 제거하면 edgeCount가 0이 된다")
            void remove_single_edge_makes_edge_count_zero() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);
                assertThat(graph.edgeCount()).isOne();

                graph.removeEdge(1, 2);

                assertThat(graph.edgeCount()).isZero();
            }

            @Test @DisplayName("간선 제거 후 edgeCount가 감소한다")
            void remove_edge_decreases_edge_count() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);
                graph.addEdge(1, 2);
                graph.addEdge(2, 3);
                graph.addEdge(1, 3);
                assertThat(graph.edgeCount()).isEqualTo(3);

                graph.removeEdge(1, 2);
                assertThat(graph.edgeCount()).isEqualTo(2);

                graph.removeEdge(2, 3);
                assertThat(graph.edgeCount()).isEqualTo(1);
            }
        }

        @Nested @DisplayName("hasVertex 테스트")
        class HasVertexTest {

            @Test @DisplayName("빈 graph에서는 false를 반환한다")
            void has_vertex_on_empty_graph_returns_false() {
                assertThat(graph.hasVertex(1)).isFalse();
            }

            @Test @DisplayName("추가하지 않은 정점 조회 시 false를 반환한다")
            void has_vertex_not_added_returns_false() {
                graph.addVertex(1);

                assertThat(graph.hasVertex(2)).isFalse();
            }

            @Test @DisplayName("추가한 정점 조회 시 true를 반환한다")
            void has_vertex_added_returns_true() {
                graph.addVertex(1);

                assertThat(graph.hasVertex(1)).isTrue();
            }

            @Test @DisplayName("제거한 정점 조회 시 false를 반환한다")
            void has_vertex_after_remove_returns_false() {
                graph.addVertex(1);
                assertThat(graph.hasVertex(1)).isTrue();

                graph.removeVertex(1);

                assertThat(graph.hasVertex(1)).isFalse();
            }
        }

        @Nested @DisplayName("hasEdge 테스트")
        class HasEdgeTest {

            @Test @DisplayName("추가하지 않은 간선 조회 시 false를 반환한다")
            void has_edge_not_added_returns_false() {
                graph.addVertex(1);
                graph.addVertex(2);

                assertThat(graph.hasEdge(1, 2)).isFalse();
            }

            @Test @DisplayName("추가한 간선 조회 시 true를 반환한다")
            void has_edge_added_returns_true() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);

                assertThat(graph.hasEdge(1, 2)).isTrue();
                assertThat(graph.hasEdge(2, 1)).isTrue();
            }

            @Test @DisplayName("제거한 간선 조회 시 false를 반환한다")
            void has_edge_after_remove_returns_false() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);
                assertThat(graph.hasEdge(1, 2)).isTrue();
                assertThat(graph.hasEdge(2, 1)).isTrue();

                graph.removeEdge(1, 2);
                assertThat(graph.hasEdge(1, 2)).isFalse();
                assertThat(graph.hasEdge(2, 1)).isFalse();
            }

            @Test @DisplayName("존재하지 않는 정점으로 조회 시 예외가 발생한다")
            void has_edge_with_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.hasEdge(1, 2))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested @DisplayName("getNeighbors 테스트")
        class GetNeighborsTest {
            @Test @DisplayName("간선이 없는 정점일 경우 빈 리스트를 반환한다")
            void get_neighbors_without_edges_returns_empty_list() {
                graph.addVertex(1);

                assertThat(graph.getNeighbors(1)).isEmpty();
            }

            @Test @DisplayName("존재하지 않는 정점 조회 시 예외가 발생한다")
            void get_neighbors_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.getNeighbors(1))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선이 존재하는 정점일 경우 이웃 정점 목록을 반환한다")
            void get_neighbors_returns_adjacent_vertices() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);
                assertThat(graph.getNeighbors(1)).containsExactlyInAnyOrder(2);

                graph.addVertex(3);
                graph.addEdge(1, 3);
                assertThat(graph.getNeighbors(1)).containsExactlyInAnyOrder(2,3);
            }
        }

        @Nested @DisplayName("vertexCount / edgeCount 테스트")
        class CountTest {
            @Test @DisplayName("빈 그래프에서 vertexCount와 edgeCount는 0이다")
            void empty_graph_counts_are_zero() {
                assertThat(graph.vertexCount()).isZero();
                assertThat(graph.edgeCount()).isZero();
            }

            @Test @DisplayName("정점 추가/삭제를 반복하면 vertexCount가 정확하다")
            void vertex_count_after_add_and_remove() {
                assertThat(graph.vertexCount()).isZero();

                graph.addVertex(1);
                assertThat(graph.vertexCount()).isOne();

                graph.addVertex(2);
                assertThat(graph.vertexCount()).isEqualTo(2);

                graph.removeVertex(2);
                assertThat(graph.vertexCount()).isEqualTo(1);
            }

            @Test @DisplayName("간선 추가/삭제를 반복하면 edgeCount가 정확하다")
            void edge_count_after_add_and_remove() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);
                assertThat(graph.edgeCount()).isZero();

                graph.addEdge(1,2);
                assertThat(graph.edgeCount()).isOne();

                graph.addEdge(2, 3);
                assertThat(graph.edgeCount()).isEqualTo(2);

                graph.removeEdge(2, 3);
                assertThat(graph.edgeCount()).isEqualTo(1);
            }

            @Test @DisplayName("정점 삭제 시 vertexCount와 edgeCount가 동시에 감소한다")
            void remove_vertex_decreases_both_counts() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);
                assertThat(graph.edgeCount()).isZero();

                graph.addEdge(1,2);
                assertThat(graph.edgeCount()).isOne();

                graph.addEdge(2, 3);
                assertThat(graph.edgeCount()).isEqualTo(2);
                assertThat(graph.vertexCount()).isEqualTo(3);

                graph.removeVertex(3);
                assertThat(graph.edgeCount()).isEqualTo(1);
                assertThat(graph.vertexCount()).isEqualTo(2);
            }
        }
    }

    @Nested @DisplayName("DirectedGraph (방향, 인접 리스트) 테스트")
    class DirectedGraphTest {

        private DirectedGraph graph;

        @BeforeEach
        void setup() {
            graph = new DirectedGraph();
        }

        @Nested @DisplayName("addEdge 테스트")
        class AddEdgeTest {
            @Test @DisplayName("존재하지 않는 정점에 간선 추가 시 예외가 발생한다")
            void add_edge_to_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.addEdge(1, 2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("두 정점 사이에 간선이 단방향으로 추가된다")
            void add_edge_connects_one_direction_only() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1,2);

                assertThat(graph.hasEdge(1, 2)).isTrue();
                assertThat(graph.hasEdge(2, 1)).isFalse();
            }

            @Test @DisplayName("같은 정점 쌍이라도 반대 방향 간선을 추가할 수 있다")
            void add_edge_reverse_direction_is_allowed() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1,2);
                graph.addEdge(2,1);

                assertThat(graph.hasEdge(1, 2)).isTrue();
                assertThat(graph.hasEdge(2, 1)).isTrue();
            }

            @Test @DisplayName("이미 존재하는 간선을 중복 추가하면 예외가 발생한다")
            void add_duplicate_edge_throws_exception() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1,2);

                assertThatThrownBy(() -> graph.addEdge(1, 2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("자기 루프 시 예외가 발생한다")
            void add_self_loop_throws_exception() {
                graph.addVertex(1);

                assertThatThrownBy(() -> graph.addEdge(1, 1))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested @DisplayName("removeEdge 테스트")
        class RemoveEdgeTest {
            @Test @DisplayName("존재하지 않는 정점의 간선을 제거하면 예외가 발생한다")
            void remove_edge_with_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.removeEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("존재하지 않는 간선을 제거하면 예외가 발생한다")
            void remove_non_existent_edge_throws_exception() {
                graph.addVertex(1);
                graph.addVertex(2);

                assertThatThrownBy(() -> graph.removeEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선 제거 시 해당 방향만 삭제되고 반대 방향은 유지된다")
            void remove_edge_only_removes_one_direction() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2);
                graph.addEdge(2,1);

                graph.removeEdge(1, 2);

                assertThat(graph.hasEdge(1,2)).isFalse();
                assertThat(graph.hasEdge(2,1)).isTrue();
            }
        }

        @Nested @DisplayName("hasEdge 테스트")
        class HasEdgeTest {
            @Test @DisplayName("존재하지 않는 간선의 경우 false를 반환한다")
            void has_edge_not_added_returns_false() {
                graph.addVertex(1);
                graph.addVertex(2);

                assertThat(graph.hasEdge(1,2)).isFalse();
            }

            @Test @DisplayName("간선이 존재할 경우 true를 반환한다")
            void has_edge_added_returns_true() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2);

                assertThat(graph.hasEdge(1,2)).isTrue();
            }

            @Test @DisplayName("반대 방향 간선은 false를 반환한다")
            void has_edge_reverse_direction_returns_false() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2);

                assertThat(graph.hasEdge(2,1)).isFalse();
            }

            @Test @DisplayName("존재하지 않는 정점일 경우 예외가 발생한다")
            void has_edge_with_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.hasEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested @DisplayName("getNeighbors 테스트")
        class GetNeighborsTest {
            @Test @DisplayName("존재하지 않는 정점 조회 시 예외가 발생한다")
            void get_neighbors_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.getNeighbors(1))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선이 없는 정점은 빈 리스트를 반환한다")
            void get_neighbors_without_edges_returns_empty_list() {
                graph.addVertex(1);
                assertThat(graph.getNeighbors(1)).isEmpty();
            }

            @Test @DisplayName("나가는 방향의 이웃만 반환한다")
            void get_neighbors_returns_only_outgoing_vertices() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);
                graph.addVertex(4);
                graph.addEdge(1,2);
                graph.addEdge(1,3);
                graph.addEdge(4,1);

                assertThat(graph.getNeighbors(1)).containsExactlyInAnyOrder(2,3);
                assertThat(graph.getNeighbors(1)).doesNotContain(4);
            }
        }

        @Nested @DisplayName("edgeCount 테스트")
        class EdgeCountTest {
            @Test @DisplayName("간선 하나 추가 시 edgeCount가 1이다")
            void edge_count_after_single_edge() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);

                assertThat(graph.edgeCount()).isEqualTo(1);
            }

            @Test @DisplayName("양방향 간선 추가 시 edgeCount가 2이다")
            void edge_count_after_bidirectional_edges() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);
                graph.addEdge(2, 1);

                assertThat(graph.edgeCount()).isEqualTo(2);
            }

            @Test @DisplayName("간선 제거 후 edgeCount가 감소한다")
            void edge_count_after_remove_edge() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2);
                graph.addEdge(2, 1);
                assertThat(graph.edgeCount()).isEqualTo(2);

                graph.removeEdge(2, 1);

                assertThat(graph.edgeCount()).isEqualTo(1);
            }
        }
    }

    @Nested @DisplayName("WeightedGraph (가중치, 인접 리스트) 테스트")
    class WeightedGraphTest {

        private WeightedGraph graph;

        @BeforeEach
        void setUp() {
            graph = new WeightedGraph();
        }

        @Nested @DisplayName("addEdge 가중치 간선 테스트")
        class AddEdgeTest {
            @Test @DisplayName("존재하지 않는 정점에 addEdge를 하면 예외가 발생한다")
            void add_edge_to_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.addEdge(1,2,1))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("존재하는 두 정점에 대해 가중치 간선을 생성할 수 있다.")
            void add_edge_with_weight() {
                graph.addVertex(1);
                graph.addVertex(2);
                assertThat(graph.edgeCount()).isEqualTo(0);

                graph.addEdge(1, 2, 3);

                assertThat(graph.getWeight(1, 2)).isEqualTo(3);
                assertThat(graph.edgeCount()).isEqualTo(1);
            }

            @Test @DisplayName("반대 방향의 간선을 생성할 수 있다")
            void add_edge_reverse_direction_is_allowed() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1, 2, 3);

                graph.addEdge(2, 1, 2);

                assertThat(graph.getWeight(2,1)).isEqualTo(2);
                assertThat(graph.edgeCount()).isEqualTo(2);
            }

            @Test @DisplayName("이미 존재하는 간선을 중복 추가하면 예외가 발생한다")
            void add_duplicate_edge_throws_exception() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2,3);

                assertThatThrownBy(() -> graph.addEdge(1,2,3))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("자기 루프 시 예외가 발생한다")
            void add_self_loop_throws_exception() {
                graph.addVertex(1);

                assertThatThrownBy(() -> graph.addEdge(1,1,1))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선 추가 후 가중치가 정확히 저장된다")
            void add_edge_stores_correct_weight() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);

                graph.addEdge(1, 2, 4);
                graph.addEdge(1, 3, 7);
                graph.addEdge(2, 3, 2);

                assertThat(graph.getWeight(1,2)).isEqualTo(4);
                assertThat(graph.getWeight(1,3)).isEqualTo(7);
                assertThat(graph.getWeight(2,3)).isEqualTo(2);
            }
        }

        @Nested @DisplayName("removeEdge 테스트")
        class RemoveEdgeTest {
            @Test @DisplayName("존재하지 않는 정점의 간선을 삭제 시 예외가 발생한다")
            void remove_edge_with_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.removeEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("존재하지 않는 간선 삭제 시 예외가 발생한다")
            void remove_non_existent_edge_throws_exception() {
                graph.addVertex(1);
                graph.addVertex(2);

                assertThatThrownBy(() -> graph.removeEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선이 삭제된다")
            void remove_edge_removes_edge() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2,3);

                graph.removeEdge(1,2);
                assertThat(graph.edgeCount()).isZero();
            }

            @Test @DisplayName("한 방향 간선 삭제 시 역방향 간선은 유지된다")
            void remove_edge_keeps_reverse_direction() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2,3);
                graph.addEdge(2,1,3);

                graph.removeEdge(1,2);
                assertThat(graph.hasEdge(2,1)).isTrue();
            }

            @Test @DisplayName("간선 삭제 후 edgeCount가 감소한다")
            void remove_edge_decreases_edge_count() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);
                graph.addEdge(1,2,3);
                graph.addEdge(1,3,4);
                graph.addEdge(2,3,5);
                assertThat(graph.edgeCount()).isEqualTo(3);

                graph.removeEdge(1,2);
                assertThat(graph.edgeCount()).isEqualTo(2);
            }
        }

        @Nested @DisplayName("getWeight 테스트")
        class GetWeightTest {
            @Test @DisplayName("존재하지 않는 정점일 경우 예외가 발생한다")
            void get_weight_with_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.getWeight(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("존재하지 않는 간선일 경우 예외가 발생한다")
            void get_weight_non_existent_edge_throws_exception() {
                graph.addVertex(1);
                graph.addVertex(2);

                assertThatThrownBy(() -> graph.getWeight(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("존재하는 간선일 경우 가중치를 반환한다")
            void get_weight_returns_correct_weight() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2,5);

                assertThat(graph.getWeight(1, 2)).isEqualTo(5);
            }
        }

        @Nested @DisplayName("edgeCount 테스트")
        class EdgeCountTest {
            @Test @DisplayName("정점이 존재하지 않으면 count는 0이다")
            void edge_count_without_vertices_is_zero() {
                assertThat(graph.edgeCount()).isZero();
            }

            @Test @DisplayName("간선이 존재하지 않으면 count는 0이다")
            void edge_count_without_edges_is_zero() {
                graph.addVertex(1);
                graph.addVertex(2);
                assertThat(graph.edgeCount()).isZero();
            }

            @Test @DisplayName("간선의 개수만큼 반환한다")
            void edge_count_returns_number_of_edges() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2,3);
                assertThat(graph.edgeCount()).isEqualTo(1);

                graph.addEdge(2,1,4);
                assertThat(graph.edgeCount()).isEqualTo(2);
            }
        }
    }

    // ===== 인접 행렬 구현체 (CRUD) =====

    @Nested @DisplayName("AdjacencyMatrixGraph (무방향, 인접 행렬) 테스트")
    class AdjacencyMatrixGraphTest {

        AdjacencyMatrixGraph graph;

        @BeforeEach
        void setup() {graph = new AdjacencyMatrixGraph(10);}

        @Nested @DisplayName("addVertex 테스트")
        class AddVertexTest {
            @Test @DisplayName("빈 그래프에 정점을 추가할 수 있다")
            void add_vertex_to_empty_graph() {
                assertThat(graph.hasVertex(1)).isFalse();

                graph.addVertex(1);

                assertThat(graph.hasVertex(1)).isTrue();
            }

            @Test @DisplayName("정점이 있는 그래프에 정점을 추가할 수 있다")
            void add_vertex_to_non_empty_graph() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);

                assertThat(graph.vertexCount()).isEqualTo(3);
            }

            @Test @DisplayName("이미 존재하는 정점을 추가하면 예외가 발생한다")
            void add_duplicate_vertex_throws_exception() {
                graph.addVertex(1);

                assertThatThrownBy(() -> graph.addVertex(1))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("정점 추가 후 vertexCount가 증가한다")
            void add_vertex_increases_vertex_count() {
                graph.addVertex(1);
                graph.addVertex(2);
                assertThat(graph.vertexCount()).isEqualTo(2);

                graph.addVertex(3);

                assertThat(graph.vertexCount()).isEqualTo(3);
            }

            @Test @DisplayName("용량을 초과하면 예외가 발생한다")
            void add_vertex_exceeds_capacity_throws_exception() {
                for (int i = 0; i < 10; i++) graph.addVertex(i);

                assertThatThrownBy(() -> graph.addVertex(11))
                .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested @DisplayName("addEdge 테스트")
        class AddEdgeTest {
            @Test @DisplayName("존재하지 않는 정점에 간선 추가 시 예외가 발생한다")
            void add_edge_to_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.addEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선이 생성된다")
            void add_edge_between_two_vertices() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1,2);

                assertThat(graph.edgeCount()).isEqualTo(1);
            }

            @Test @DisplayName("무방향이므로 양쪽 다 연결된다")
            void add_edge_connects_both_directions() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1,2);

                assertThat(graph.hasEdge(2,1)).isTrue();
            }

            @Test @DisplayName("이미 존재하는 간선인 경우 예외가 발생한다")
            void add_duplicate_edge_throws_exception() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1,2);

                assertThatThrownBy(() -> graph.addEdge(2,1))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("자기 루프 시 예외가 발생한다")
            void add_self_loop_throws_exception() {
                graph.addVertex(1);

                assertThatThrownBy(() -> graph.addEdge(1,1))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested @DisplayName("removeVertex 테스트")
        class RemoveVertexTest {
            @Test @DisplayName("존재하지 않는 정점 제거 시 예외가 발생한다")
            void remove_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.removeVertex(1))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("정점이 삭제된다")
            void remove_vertex_removes_vertex() {
                graph.addVertex(1);
                assertThat(graph.vertexCount()).isEqualTo(1);

                graph.removeVertex(1);

                assertThat(graph.vertexCount()).isEqualTo(0);
            }

            @Test @DisplayName("정점 삭제 시 연결된 간선도 삭제된다")
            void remove_vertex_also_removes_connected_edges() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2);
                assertThat(graph.edgeCount()).isEqualTo(1);

                graph.removeVertex(1);

                assertThat(graph.edgeCount()).isEqualTo(0);
            }

            @Test @DisplayName("정점 삭제 후 vertexCount가 감소한다")
            void remove_vertex_decreases_vertex_count() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);
                assertThat(graph.vertexCount()).isEqualTo(3);

                graph.removeVertex(1);

                assertThat(graph.vertexCount()).isEqualTo(2);

                graph.removeVertex(2);

                assertThat(graph.vertexCount()).isEqualTo(1);

                graph.removeVertex(3);

                assertThat(graph.vertexCount()).isEqualTo(0);
            }
        }

        @Nested @DisplayName("removeEdge 테스트")
        class RemoveEdgeTest {
            @Test @DisplayName("존재하지 않는 정점의 간선 제거 시 예외가 발생한다")
            void remove_edge_with_non_existent_vertex_throws_exception() {
                graph.addVertex(2);
                assertThatThrownBy(() -> graph.removeEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("존재하지 않는 간선 제거 시 예외가 발생한다")
            void remove_non_existent_edge_throws_exception() {
                graph.addVertex(1);
                graph.addVertex(2);
                assertThatThrownBy(() -> graph.removeEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선이 삭제된다")
            void remove_edge_removes_edge() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2);
                assertThat(graph.edgeCount()).isEqualTo(1);

                graph.removeEdge(1,2);

                assertThat(graph.edgeCount()).isZero();
            }

            @Test @DisplayName("간선 삭제 시 역방향 간선도 삭제된다")
            void remove_edge_also_removes_reverse_direction() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2);
                assertThat(graph.edgeCount()).isEqualTo(1);

                graph.removeEdge(2,1);

                assertThat(graph.edgeCount()).isZero();
            }
        }

        @Nested @DisplayName("hasEdge 테스트")
        class HasEdgeTest {
            @Test @DisplayName("존재하지 않는 정점으로 조회 시 예외가 발생한다")
            void has_edge_with_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.hasEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선이 존재하면 true를 반환한다")
            void has_edge_added_returns_true() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2);

                assertThat(graph.hasEdge(1,2)).isTrue();
            }

            @Test @DisplayName("간선이 존재하면 역방향도 true를 반환한다")
            void has_edge_reverse_direction_returns_true() {
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(1,2);

                assertThat(graph.hasEdge(2,1)).isTrue();
            }

            @Test @DisplayName("간선이 없으면 false를 반환한다")
            void has_edge_not_added_returns_false() {
                graph.addVertex(1);
                graph.addVertex(2);

                assertThat(graph.hasEdge(1,2)).isFalse();
            }
        }
    }

    @Nested @DisplayName("DirectedAdjacencyMatrixGraph (방향, 인접 행렬) 테스트")
    class DirectedAdjacencyMatrixGraphTest {

        DirectedAdjacencyMatrixGraph graph;

        @BeforeEach
        void setup() {
            graph = new DirectedAdjacencyMatrixGraph(10);
        }

        @Nested @DisplayName("addEdge 테스트")
        class AddEdgeTest {
            @Test @DisplayName("존재하지 않는 정점에 간선 생성 시 예외가 발생한다")
            void add_edge_to_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.addEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선이 단방향으로 생성된다")
            void add_edge_connects_one_direction_only() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1,2);

                assertThat(graph.hasEdge(1,2)).isTrue();
                assertThat(graph.hasEdge(2,1)).isFalse();
            }

            @Test @DisplayName("이미 존재하는 간선 중복 생성 시 예외가 발생한다")
            void add_duplicate_edge_throws_exception() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1,2);

                assertThatThrownBy(() -> graph.addEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("역방향 간선은 별도로 추가할 수 있다")
            void add_edge_reverse_direction_is_allowed() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1,2);
                graph.addEdge(2,1);

                assertThat(graph.hasEdge(1,2)).isTrue();
                assertThat(graph.hasEdge(2,1)).isTrue();
            }

            @Test @DisplayName("자기 루프 시 예외가 발생한다")
            void add_self_loop_throws_exception() {
                graph.addVertex(1);

                assertThatThrownBy(() -> graph.addEdge(1,1))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested @DisplayName("hasEdge 테스트")
        class HasEdgeTest {
            @Test @DisplayName("존재하지 않는 정점으로 조회 시 예외가 발생한다")
            void has_edge_with_non_existent_vertex_throws_exception() {
                assertThatThrownBy(() -> graph.hasEdge(1,2))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("간선이 존재하지 않으면 false를 반환한다")
            void has_edge_not_added_returns_false() {
                graph.addVertex(1);
                graph.addVertex(2);

                assertThat(graph.hasEdge(1,2)).isFalse();
            }

            @Test @DisplayName("간선이 존재하면 true를 반환한다")
            void has_edge_added_returns_true() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1,2);

                assertThat(graph.hasEdge(1,2)).isTrue();
            }

            @Test @DisplayName("반대 방향 간선은 false를 반환한다")
            void has_edge_reverse_direction_returns_false() {
                graph.addVertex(1);
                graph.addVertex(2);

                graph.addEdge(1,2);

                assertThat(graph.hasEdge(2,1)).isFalse();
            }
        }
    }

    // ===== 응용 알고리즘 =====

    @Nested @DisplayName("GraphProblems 테스트")
    class GraphProblemsTest {

        GraphProblems problems;

        @BeforeEach
        void setup() {
            problems = new GraphProblems();
        }

//        BFS(너비 우선 탐색): 시작 정점에서 가까운 정점부터 차례로 방문합니다. 같은 거리의 정점을
//        다 방문한 후 다음 거리로 넘어간다.
        @Nested @DisplayName("BFS 테스트")
        class BfsTest {

            Graph graph;
            List<Integer> bfs;

            @BeforeEach
            void setup() {
                graph = new Graph();
            }

            @Test @DisplayName("정점이 하나면 해당 정점만 반환한다")
            void bfs_single_vertex() {
                graph.addVertex(0);
                assertThat(problems.bfs(graph,0)).contains(0);
            }

            @Test @DisplayName("일렬로 연결된 그래프를 순서대로 방문한다")
            void bfs_linear_graph() {
                graph.addVertex(0);
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addEdge(0,1);
                graph.addEdge(1,2);
                assertThat(problems.bfs(graph,0)).containsExactly(0,1,2);
            }

            @Test @DisplayName("같은 거리의 정점을 먼저 방문한 후 다음 거리로 넘어간다")
            void bfs_visits_by_level() {
                //     0
                //    / \
                //   1   2     <- 거리 1
                //   |   |
                //   3   4     <- 거리 2
                graph.addVertex(0);
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);
                graph.addVertex(4);
                graph.addEdge(0, 1);
                graph.addEdge(0, 2);
                graph.addEdge(1, 3);
                graph.addEdge(2, 4);

                List<Integer> result = problems.bfs(graph, 0);
                // 0이 먼저, 그 다음 1,2(거리1), 그 다음 3,4(거리2)
                assertThat(result.indexOf(0)).isLessThan(result.indexOf(1));
                assertThat(result.indexOf(0)).isLessThan(result.indexOf(2));
                assertThat(result.indexOf(1)).isLessThan(result.indexOf(3));
                assertThat(result.indexOf(2)).isLessThan(result.indexOf(4));
            }

            @Test @DisplayName("이미 방문한 정점은 다시 방문하지 않는다")
            void bfs_does_not_revisit() {
                //   0 - 1
                //   |   |
                //   2 - 3   ← 사이클이 있어도 중복 방문 안 됨
                graph.addVertex(0);
                graph.addVertex(1);
                graph.addVertex(2);
                graph.addVertex(3);
                graph.addEdge(0, 1);
                graph.addEdge(1, 3);
                graph.addEdge(3, 2);
                graph.addEdge(2, 0);

                List<Integer> result = problems.bfs(graph, 0);
                assertThat(result).hasSize(4);
                assertThat(result).containsExactlyInAnyOrder(0, 1, 2, 3);
            }

            @Test @DisplayName("연결되지 않은 정점은 방문하지 않는다")
            void bfs_does_not_visit_disconnected() {
                //   0 - 1    3 - 4   ← 별도 그룹
                graph.addVertex(0);
                graph.addVertex(1);
                graph.addVertex(3);
                graph.addVertex(4);
                graph.addEdge(0, 1);
                graph.addEdge(3, 4);

                assertThat(problems.bfs(graph, 0)).containsExactlyInAnyOrder(0, 1);
                assertThat(problems.bfs(graph, 0)).doesNotContain(3, 4);
            }

            @Test @DisplayName("존재하지 않는 정점에서 시작하면 예외가 발생한다")
            void bfs_non_existent_start_throws_exception() {
                graph.addVertex(0);

                assertThatThrownBy(() -> problems.bfs(graph, 99))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

//        DFS(깊이 우선 탐색): 한 방향으로 끝까지 깊이 들어간 뒤, 막히면 돌아와서 다른 방향으로 탐색합니다
        @Nested @DisplayName("DFS 테스트")
        class DfsTest {}

//        최단 경로(비가중치):BFS로 시작점에서 도착점까지의 최단 경로를 찾습니다.
        @Nested @DisplayName("최단 경로 (비가중치) 테스트")
        class ShortestPathTest {}

//        가중치 그래프에서 시작점에서 모든 정점까지의 최단 거리를 구합니다.
//        음수 가중치는 불가, 우선순위 큐를 사용해서 가장 가까운 정점부터 처리한다
        @Nested @DisplayName("다익스트라 테스트")
        class DijkstraTest {}

//        다익스트라와 같은 목적인데 음수 가중치도 처리 가능하다.
//        모든 간선을 V-1번 반복 확인하는 방식이라 느리지만, 음수 사이클도 탐지할 수있다.
        @Nested @DisplayName("벨만-포드 테스트")
        class BellmanFordTest {}

//        모든 정점 쌍 간의 최단거리를 한꺼번에 구한다.
//        3중 반복문으로 K를 거쳐가는 게 더 빠른가?를 모두 확인한다
        @Nested @DisplayName("플로이드-워셜 테스트")
        class FloydWarshallTest {}

//        그래프에 순환이 있는지 확인한다.
//        방향 그래프에서는 DFS중 현재 탐색 경로에 있는 정점을 다시 만나면 사이클이다.
        @Nested @DisplayName("사이클 탐지 테스트")
        class CycleDetectionTest {}

//        방향 비순환 그래프(DAG)에서 선후관계를 지키는 순서를 정렬한다.
//        선수 과목처럼 A를 전저 해야 B를 할 수 있다를 정렬한다
        @Nested @DisplayName("위상 정렬 테스트")
        class TopologicalSortTest {}

//        서로 연결된 정점들의 그룹을 찾는다. 떨어져 있는 그래프가 몇 덩어리인 지 파악한다
        @Nested @DisplayName("연결 요소 테스트")
        class ConnectedComponentsTest {}

//        정점을 두 그룹으로 나눠서, 같은 그룹 안에서는 간선이 없도록 할 수 있는 지 판별한다.
        @Nested @DisplayName("이분 그래프 테스트")
        class BipartiteTest {}

        @Nested @DisplayName("프림 MST 테스트")
        class PrimTest {}

        @Nested @DisplayName("크루스칼 MST 테스트")
        class KruskalTest {}
    }
}