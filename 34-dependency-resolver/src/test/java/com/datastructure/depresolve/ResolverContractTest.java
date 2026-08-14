package com.datastructure.depresolve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 두 구현이 똑같이 지켜야 하는 것.
 *
 * 여기 있는 검사는 대부분 "답이 무엇인가" 가 아니라 "답이 성질을 만족하는가" 다.
 * 위상 정렬은 정답이 여럿이라 값을 못 박을 수가 없기 때문이다.
 *
 * 값을 못 박는 검사는 구현별 테스트로 내려간다. 칸 알고리즘만 사전순 최소를 보장한다.
 */
abstract class ResolverContractTest {

    protected abstract Resolver resolverFor(DependencyGraph graph);

    // ---------------------------------------------------------------- 검증 도구

    /**
     * 이 목록이 정말 위상 정렬인가.
     *
     * 답을 비교하는 대신 이것을 묻는다. 셋을 본다.
     *   1. 모든 노드가 정확히 한 번씩 있다
     *   2. 앞에 와야 하는 것이 실제로 앞에 있다
     *   3. 없는 이름이 섞이지 않았다
     *
     * 2번만 보면 노드를 통째로 빠뜨린 구현이 통과한다. 빠진 것은 순서를 어기지 않기 때문이다.
     */
    protected static void assertIsTopologicalOrder(DependencyGraph graph, List<String> order) {
        List<String> names = graph.names();
        assertEquals(names.size(), order.size(), "개수가 다르다: " + order);
        assertEquals(new HashSet<>(names), new HashSet<>(order), "이름 집합이 다르다: " + order);
        assertEquals(order.size(), new HashSet<>(order).size(), "중복이 있다: " + order);

        java.util.Map<String, Integer> position = new java.util.HashMap<>();
        for (int i = 0; i < order.size(); i++) {
            position.put(order.get(i), i);
        }
        for (String name : names) {
            for (String next : graph.after(name)) {
                assertTrue(position.get(name) < position.get(next),
                        name + " 이 " + next + " 보다 앞에 와야 하는데 " + order);
            }
        }
    }

    /** 층 나누기가 성질을 만족하는가. */
    protected static void assertLayersAreValid(DependencyGraph graph, List<List<String>> layers) {
        java.util.Map<String, Integer> layerOf = new java.util.HashMap<>();
        int total = 0;
        for (int i = 0; i < layers.size(); i++) {
            for (String name : layers.get(i)) {
                layerOf.put(name, i);
                total++;
            }
            assertTrue(!layers.get(i).isEmpty(), i + "층이 비었다");
        }
        assertEquals(graph.size(), total, "노드가 빠졌거나 겹쳤다");

        for (String name : graph.names()) {
            for (String next : graph.after(name)) {
                assertTrue(layerOf.get(name) < layerOf.get(next),
                        name + "(" + layerOf.get(name) + "층) 이 "
                                + next + "(" + layerOf.get(next) + "층) 보다 앞 층이어야 한다");
            }
        }
    }

    protected static DependencyGraph graphOf(String... pairs) {
        DependencyGraph g = new DependencyGraph();
        for (String pair : pairs) {
            String[] parts = pair.split("<-");
            if (parts.length == 1) {
                g.add(parts[0].trim());
            } else {
                // "b <- a" 는 a 가 b 에 기댄다는 뜻이다. b 가 먼저 온다.
                g.dependsOn(parts[1].trim(), parts[0].trim());
            }
        }
        return g;
    }

    // ---------------------------------------------------------------- 계약

    @Nested
    @DisplayName("계약: 순서")
    class Ordering {

        @Test
        @DisplayName("기댄 것이 먼저 온다")
        void dependencyComesFirst() {
            DependencyGraph g = graphOf("lib <- app");
            List<String> order = resolverFor(g).resolve();
            assertEquals(List.of("lib", "app"), order);
        }

        @Test
        @DisplayName("사슬")
        void chain() {
            DependencyGraph g = graphOf("a <- b", "b <- c", "c <- d");
            assertEquals(List.of("a", "b", "c", "d"), resolverFor(g).resolve());
        }

        @Test
        @DisplayName("다이아몬드. 가운데 둘의 순서는 정해지지 않는다")
        void diamond() {
            DependencyGraph g = graphOf("a <- b", "a <- c", "b <- d", "c <- d");
            List<String> order = resolverFor(g).resolve();
            assertIsTopologicalOrder(g, order);
            assertEquals("a", order.get(0));
            assertEquals("d", order.get(3));
        }

        @Test
        @DisplayName("서로 상관없는 것들")
        void independentNodes() {
            DependencyGraph g = graphOf("a", "b", "c");
            List<String> order = resolverFor(g).resolve();
            assertIsTopologicalOrder(g, order);
            assertEquals(3, order.size());
        }

        @Test
        @DisplayName("빈 그래프는 빈 목록")
        void emptyGraph() {
            assertEquals(List.of(), resolverFor(new DependencyGraph()).resolve());
        }

        @Test
        @DisplayName("노드 하나")
        void singleNode() {
            assertEquals(List.of("only"), resolverFor(graphOf("only")).resolve());
        }

        @Test
        @DisplayName("같은 의존을 두 번 걸어도 답이 같다")
        void duplicateEdgesDoNotMatter() {
            DependencyGraph g = new DependencyGraph();
            g.dependsOn("app", "lib");
            g.dependsOn("app", "lib");
            g.dependsOn("app", "lib");
            assertEquals(List.of("lib", "app"), resolverFor(g).resolve());
        }

        @Test
        @DisplayName("섬이 여럿이어도 전부 나온다")
        void disconnectedComponents() {
            DependencyGraph g = graphOf("a <- b", "x <- y", "lone");
            List<String> order = resolverFor(g).resolve();
            assertIsTopologicalOrder(g, order);
        }
    }

    @Nested
    @DisplayName("계약: 순환")
    class Cycles {

        @Test
        @DisplayName("서로 기대면 던진다")
        void twoWayCycleThrows() {
            DependencyGraph g = graphOf("a <- b", "b <- a");
            assertThrows(CycleException.class, () -> resolverFor(g).resolve());
        }

        @Test
        @DisplayName("자기 자신에게 기대는 것도 순환이다")
        void selfLoopIsACycle() {
            DependencyGraph g = new DependencyGraph();
            g.dependsOn("a", "a");
            assertThrows(CycleException.class, () -> resolverFor(g).resolve());
        }

        @Test
        @DisplayName("긴 고리")
        void longCycleThrows() {
            DependencyGraph g = graphOf("a <- b", "b <- c", "c <- d", "d <- a");
            assertThrows(CycleException.class, () -> resolverFor(g).resolve());
        }

        @Test
        @DisplayName("순환이 한 구석에만 있어도 전체가 실패한다. 부분 결과는 안 준다")
        void partialResultIsNotAcceptable() {
            DependencyGraph g = graphOf("ok1 <- ok2", "x <- y", "y <- x");
            assertThrows(CycleException.class, () -> resolverFor(g).resolve());
            assertThrows(CycleException.class, () -> resolverFor(g).layers());
        }

        @Test
        @DisplayName("다이아몬드는 순환이 아니다. 같은 노드를 두 번 만나는 것뿐이다")
        void diamondIsNotACycle() {
            DependencyGraph g = graphOf("a <- b", "a <- c", "b <- d", "c <- d");
            assertIsTopologicalOrder(g, resolverFor(g).resolve());
            assertEquals(List.of(), resolverFor(g).cycle());
        }

        @Test
        @DisplayName("긴 다이아몬드도 순환이 아니다")
        void wideDiamondIsNotACycle() {
            DependencyGraph g = new DependencyGraph();
            for (int i = 0; i < 20; i++) {
                g.dependsOn("mid" + i, "top");
                g.dependsOn("bottom", "mid" + i);
            }
            assertIsTopologicalOrder(g, resolverFor(g).resolve());
            assertEquals(List.of(), resolverFor(g).cycle());
        }

        @Test
        @DisplayName("순환이 없으면 cycle 이 빈 목록이다")
        void noCycleMeansEmpty() {
            assertEquals(List.of(), resolverFor(graphOf("a <- b")).cycle());
        }
    }

    @Nested
    @DisplayName("계약: 층")
    class Layers {

        @Test
        @DisplayName("아무것에도 안 기대는 것들이 0층이다")
        void independentGoFirst() {
            DependencyGraph g = graphOf("a <- c", "b <- c");
            List<List<String>> layers = resolverFor(g).layers();
            assertEquals(List.of(List.of("a", "b"), List.of("c")), layers);
        }

        @Test
        @DisplayName("사슬은 층이 노드 수만큼이다. 병렬로 못 줄인다")
        void chainCannotBeParallelised() {
            DependencyGraph g = graphOf("a <- b", "b <- c", "c <- d");
            List<List<String>> layers = resolverFor(g).layers();
            assertEquals(4, layers.size());
            for (List<String> layer : layers) {
                assertEquals(1, layer.size());
            }
        }

        @Test
        @DisplayName("서로 상관없으면 층이 하나다")
        void independentIsOneLayer() {
            DependencyGraph g = graphOf("a", "b", "c", "d");
            assertEquals(List.of(List.of("a", "b", "c", "d")), resolverFor(g).layers());
        }

        @Test
        @DisplayName("층은 가장 늦게 가능한 때가 아니라 가장 이른 때다")
        void layersAreAsEarlyAsPossible() {
            // c 는 a 에만 기댄다. b 를 기다릴 이유가 없으므로 1층이다.
            DependencyGraph g = graphOf("a <- b", "b <- d", "a <- c");
            List<List<String>> layers = resolverFor(g).layers();
            assertEquals(List.of(List.of("a"), List.of("b", "c"), List.of("d")), layers);
        }

        @Test
        @DisplayName("빈 그래프는 층이 없다")
        void emptyHasNoLayers() {
            assertEquals(List.of(), resolverFor(new DependencyGraph()).layers());
        }

        @Test
        @DisplayName("층 순서대로 펼치면 그것도 위상 정렬이다")
        void flattenedLayersAreATopologicalOrder() {
            DependencyGraph g = graphOf("a <- b", "a <- c", "b <- d", "c <- d", "d <- e", "a <- f");
            List<List<String>> layers = resolverFor(g).layers();
            assertLayersAreValid(g, layers);

            List<String> flat = new ArrayList<>();
            for (List<String> layer : layers) {
                flat.addAll(layer);
            }
            assertIsTopologicalOrder(g, flat);
        }
    }

    @Nested
    @DisplayName("계약: 무작위 그래프에서도 성질이 유지된다")
    class RandomGraphs {

        /** 결정적 난수. 사이클이 안 생기게 번호가 작은 쪽에서 큰 쪽으로만 잇는다. */
        @Test
        @DisplayName("무작위 DAG 60개에서 성질을 검사한다")
        void propertiesHoldOnRandomDags() {
            long state = 34_000L;
            for (int trial = 0; trial < 60; trial++) {
                DependencyGraph g = new DependencyGraph();
                int n = 6 + trial % 12;
                for (int i = 0; i < n; i++) {
                    g.add("n" + i);
                }
                for (int i = 0; i < n; i++) {
                    for (int j = i + 1; j < n; j++) {
                        state = state * 6364136223846793005L + 1442695040888963407L;
                        if (Math.floorMod(state >>> 33, 4) == 0) {
                            g.dependsOn("n" + j, "n" + i);      // i 가 먼저 온다
                        }
                    }
                }
                Resolver r = resolverFor(g);
                assertIsTopologicalOrder(g, r.resolve());
                assertLayersAreValid(g, r.layers());
                assertEquals(List.of(), r.cycle(), "DAG 인데 순환이라고 한다");
            }
        }

        @Test
        @DisplayName("간선 하나를 되돌리면 전부 순환으로 잡힌다")
        void reversingOneEdgeCreatesACycle() {
            long state = 34_001L;
            int caught = 0;
            for (int trial = 0; trial < 40; trial++) {
                DependencyGraph g = new DependencyGraph();
                int n = 5 + trial % 8;
                for (int i = 0; i + 1 < n; i++) {
                    g.dependsOn("n" + (i + 1), "n" + i);
                }
                state = state * 6364136223846793005L + 1442695040888963407L;
                int back = (int) Math.floorMod(state >>> 33, n - 1) + 1;
                g.dependsOn("n0", "n" + back);      // 뒤에서 앞으로 되돌린다

                assertThrows(CycleException.class, () -> resolverFor(g).resolve());
                caught++;
            }
            assertEquals(40, caught);
        }
    }

    @Nested
    @DisplayName("계약: 그래프의 방향")
    class GraphDirection {

        @Test
        @DisplayName("기댄 쪽과 기대어지는 쪽이 안 뒤집혔다")
        void arrowsPointForward() {
            DependencyGraph g = new DependencyGraph();
            g.dependsOn("app", "lib");

            assertEquals(List.of("app"), g.after("lib"), "lib 뒤에 app 이 온다");
            assertEquals(List.of(), g.after("app"));
            assertEquals(0, g.inDegreeOf("lib"));
            assertEquals(1, g.inDegreeOf("app"));
            assertEquals(List.of("lib"), g.dependenciesOf("app"));
        }

        @Test
        @DisplayName("방향이 뒤집혀도 순환 탐지는 멀쩡하다. 그래서 순환 테스트로는 못 잡는다")
        void cycleDetectionCannotCatchAFlippedGraph() {
            DependencyGraph g = graphOf("a <- b", "b <- a");
            assertThrows(CycleException.class, () -> resolverFor(g).resolve());
            // 방향을 뒤집어도 여전히 순환이다. 순서를 실제로 확인해야만 방향을 검증할 수 있다.
            DependencyGraph flipped = graphOf("b <- a", "a <- b");
            assertThrows(CycleException.class, () -> resolverFor(flipped).resolve());
        }

        @Test
        @DisplayName("같은 의존을 두 번 걸어도 진입 차수는 한 번만 오른다")
        void duplicateEdgeCountsOnce() {
            DependencyGraph g = new DependencyGraph();
            g.dependsOn("app", "lib");
            g.dependsOn("app", "lib");
            assertEquals(1, g.inDegreeOf("app"));
            assertEquals(1, g.edgeCount());
        }

        @Test
        @DisplayName("빈 이름은 던진다")
        void blankNamesThrow() {
            DependencyGraph g = new DependencyGraph();
            assertThrows(IllegalArgumentException.class, () -> g.add(null));
            assertThrows(IllegalArgumentException.class, () -> g.add("  "));
            assertThrows(IllegalArgumentException.class, () -> g.dependsOn("a", null));
        }

        @Test
        @DisplayName("이름 목록은 오름차순이다")
        void namesAreSorted() {
            DependencyGraph g = graphOf("zebra", "apple", "mango");
            assertEquals(List.of("apple", "mango", "zebra"), g.names());
        }
    }
}
