package com.datastructure.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 응용 문제는 두 구현 모두로 돌린다. 결과는 같고 비용만 다르다.
 *
 * 큰 입력을 쓰는 성능 테스트만 인접 리스트 전용이다. 행렬로는 그 크기를 만들 수 없다.
 */
class GraphProblemsTest {

    static Stream<Arguments> implementations() {
        return Stream.of(
            Arguments.of("AdjacencyList",
                (BiFunction<Integer, Boolean, Graph>) AdjacencyListGraph::new),
            Arguments.of("AdjacencyMatrix",
                (BiFunction<Integer, Boolean, Graph>) AdjacencyMatrixGraph::new)
        );
    }

    private static Graph build(BiFunction<Integer, Boolean, Graph> f, int n, boolean directed, int[][] edges) {
        Graph g = f.apply(n, directed);
        for (int[] e : edges) {
            if (e.length == 2) g.addEdge(e[0], e[1]); else g.addEdge(e[0], e[1], e[2]);
        }
        return g;
    }

    @Nested
    @DisplayName("문제 1. 최단 거리 (간선 수)")
    class BfsDistances {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        void findsDistances(String n, BiFunction<Integer, Boolean, Graph> f) {
            Graph g = build(f, 4, false, new int[][]{{0, 1}, {1, 2}, {0, 3}});
            assertArrayEquals(new int[]{0, 1, 2, 1}, GraphProblems.bfsDistances(g, 0));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        @DisplayName("못 가는 정점은 -1")
        void unreachableIsMinusOne(String n, BiFunction<Integer, Boolean, Graph> f) {
            Graph g = build(f, 4, false, new int[][]{{0, 1}});
            assertArrayEquals(new int[]{0, 1, -1, -1}, GraphProblems.bfsDistances(g, 0));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        @DisplayName("가중치를 무시하고 간선 수만 센다")
        void ignoresWeights(String n, BiFunction<Integer, Boolean, Graph> f) {
            // 0 -> 1 은 가중치 100 이지만 간선 하나다
            Graph g = build(f, 3, true, new int[][]{{0, 1, 100}, {1, 2, 1}});
            assertArrayEquals(new int[]{0, 1, 2}, GraphProblems.bfsDistances(g, 0));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        @DisplayName("긴 경로를 먼저 탐색해도 최단이 나온다")
        void shortestEvenWhenLongPathExploredFirst(String n, BiFunction<Integer, Boolean, Graph> f) {
            // 깊이 우선으로 풀면 여기서 틀린다.
            // 0 의 이웃은 [1, 2] 순서인데, 스택은 나중에 넣은 2 를 먼저 꺼낸다.
            // 2 쪽은 5 까지 네 칸이고 1 쪽은 두 칸이다. 긴 쪽을 먼저 밟으면 5 의 거리가 4 로 굳는다.
            Graph g = build(f, 6, true,
                new int[][]{{0, 1}, {0, 2}, {1, 5}, {2, 3}, {3, 4}, {4, 5}});
            int[] d = GraphProblems.bfsDistances(g, 0);
            assertEquals(2, d[5], "5 까지는 0->1->5 로 두 칸이다. 깊이 우선이면 4 가 나온다");
            assertArrayEquals(new int[]{0, 1, 1, 2, 3, 2}, d);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        @DisplayName("여러 경로가 있으면 짧은 쪽")
        void takesShorterPath(String n, BiFunction<Integer, Boolean, Graph> f) {
            // 0->3 직행과 0->1->2->3 우회가 둘 다 있다
            Graph g = build(f, 4, true, new int[][]{{0, 1}, {1, 2}, {2, 3}, {0, 3}});
            assertArrayEquals(new int[]{0, 1, 2, 1}, GraphProblems.bfsDistances(g, 0));
        }
    }

    @Nested
    @DisplayName("문제 2. 깊이 우선 순서")
    class DfsOrder {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        @DisplayName("갈 수 있는 정점을 빠짐없이 한 번씩 방문한다")
        void visitsAllReachableOnce(String n, BiFunction<Integer, Boolean, Graph> f) {
            Graph g = build(f, 5, false, new int[][]{{0, 1}, {1, 2}, {2, 3}});
            int[] order = GraphProblems.dfsOrder(g, 0);

            assertEquals(0, order[0], "시작점이 먼저다");
            assertEquals(4, order.length, "정점 4 는 이어져 있지 않다");
            Set<Integer> seen = new HashSet<>();
            for (int v : order) assertTrue(seen.add(v), "정점 " + v + " 를 두 번 방문했다");
            assertEquals(Set.of(0, 1, 2, 3), seen);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        @DisplayName("순환이 있어도 무한 루프에 빠지지 않는다")
        void handlesCycles(String n, BiFunction<Integer, Boolean, Graph> f) {
            Graph g = build(f, 3, true, new int[][]{{0, 1}, {1, 2}, {2, 0}});
            assertEquals(3, GraphProblems.dfsOrder(g, 0).length);
        }

        @Test
        @DisplayName("깊은 그래프에서 스택이 넘치지 않는다")
        void handlesDeepGraph() {
            // 재귀로 구현하면 여기서 StackOverflowError 가 난다. 반복으로 짜야 한다.
            final int n = 100_000;
            AdjacencyListGraph g = new AdjacencyListGraph(n, true);
            for (int i = 0; i + 1 < n; i++) g.addEdge(i, i + 1);

            assertEquals(n, GraphProblems.dfsOrder(g, 0).length);
        }
    }

    @Nested
    @DisplayName("문제 3. 위상 정렬")
    class TopologicalSort {

        /** 모든 간선이 앞에서 뒤로 가는지. 답이 여러 개일 수 있으므로 순서 자체는 비교하지 않는다. */
        private void assertValidOrder(Graph g, int[] order) {
            assertEquals(g.vertexCount(), order.length, "정점을 빠짐없이 담아야 한다");
            int[] position = new int[g.vertexCount()];
            java.util.Arrays.fill(position, -1);
            for (int i = 0; i < order.length; i++) {
                assertEquals(-1, position[order[i]], "정점 " + order[i] + " 가 두 번 나온다");
                position[order[i]] = i;
            }
            for (int u = 0; u < g.vertexCount(); u++) {
                for (int v : g.neighbors(u)) {
                    assertTrue(position[u] < position[v],
                        "간선 " + u + "->" + v + " 인데 순서가 " + position[u] + " > " + position[v] + " 다");
                }
            }
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        void sortsDag(String n, BiFunction<Integer, Boolean, Graph> f) {
            Graph g = build(f, 4, true, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}});
            assertValidOrder(g, GraphProblems.topologicalSort(g));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        @DisplayName("간선이 없으면 아무 순서나 유효하다")
        void handlesIsolatedVertices(String n, BiFunction<Integer, Boolean, Graph> f) {
            Graph g = build(f, 3, true, new int[][]{});
            assertValidOrder(g, GraphProblems.topologicalSort(g));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        @DisplayName("순환이 있으면 거부한다")
        void rejectsCycle(String n, BiFunction<Integer, Boolean, Graph> f) {
            Graph g = build(f, 3, true, new int[][]{{0, 1}, {1, 2}, {2, 0}});
            assertThrows(IllegalStateException.class, () -> GraphProblems.topologicalSort(g));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        @DisplayName("무방향 그래프는 거부한다")
        void rejectsUndirected(String n, BiFunction<Integer, Boolean, Graph> f) {
            Graph g = build(f, 3, false, new int[][]{{0, 1}});
            assertThrows(IllegalArgumentException.class, () -> GraphProblems.topologicalSort(g));
        }
    }

    @Nested
    @DisplayName("문제 4. 가중치 최단 거리")
    class ShortestPaths {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        @DisplayName("간선 수가 많아도 가중치 합이 작으면 그쪽이 최단")
        void picksCheaperPath(String n, BiFunction<Integer, Boolean, Graph> f) {
            // 0->3 직행은 100, 0->1->2->3 우회는 3. BFS 로는 직행이 최단이라 틀린다.
            Graph g = build(f, 4, true,
                new int[][]{{0, 3, 100}, {0, 1, 1}, {1, 2, 1}, {2, 3, 1}});
            assertArrayEquals(new long[]{0, 1, 2, 3}, GraphProblems.shortestPaths(g, 0));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        void unreachableIsMinusOne(String n, BiFunction<Integer, Boolean, Graph> f) {
            Graph g = build(f, 3, true, new int[][]{{0, 1, 5}});
            assertArrayEquals(new long[]{0, 5, -1}, GraphProblems.shortestPaths(g, 0));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.graph.GraphProblemsTest#implementations")
        @DisplayName("무방향 그래프에서도 동작한다")
        void worksOnUndirected(String n, BiFunction<Integer, Boolean, Graph> f) {
            Graph g = build(f, 3, false, new int[][]{{0, 1, 2}, {1, 2, 3}});
            assertArrayEquals(new long[]{0, 2, 5}, GraphProblems.shortestPaths(g, 0));
        }

        @Test
        @DisplayName("10만 정점을 5초 안에")
        void handlesLargeGraph() {
            final int n = 100_000;
            AdjacencyListGraph g = new AdjacencyListGraph(n, true);
            for (int i = 0; i + 1 < n; i++) g.addEdge(i, i + 1, 2);

            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                long[] distances = GraphProblems.shortestPaths(g, 0);
                assertEquals(0, distances[0]);
                assertEquals(2L * (n - 1), distances[n - 1]);
            });
        }
    }
}
