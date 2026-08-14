package com.datastructure.depresolve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 두 알고리즘이 무엇을 같게 하고 무엇을 다르게 하는지 숫자로 적는다.
 *
 * 계약 테스트는 둘 다 통과한다. 답이 같아서가 아니라 답의 성질이 같아서다.
 * 실제 답은 다르고, 그 사실 자체를 여기서 못 박는다.
 */
@DisplayName("두 알고리즘의 차이 측정")
class MeasurementTest {

    /** 노드 n 개를 한 줄로 이은 사슬. */
    private static DependencyGraph chain(int n) {
        DependencyGraph g = new DependencyGraph();
        for (int i = 0; i + 1 < n; i++) {
            g.dependsOn(name(i + 1), name(i));
        }
        return g;
    }

    /** 서로 상관없는 노드 n 개. */
    private static DependencyGraph independent(int n) {
        DependencyGraph g = new DependencyGraph();
        for (int i = 0; i < n; i++) {
            g.add(name(i));
        }
        return g;
    }

    /** 완전 이진 트리 모양. 자식 둘이 부모에 기댄다. */
    private static DependencyGraph binaryTree(int n) {
        DependencyGraph g = new DependencyGraph();
        g.add(name(0));
        for (int i = 1; i < n; i++) {
            g.dependsOn(name(i), name((i - 1) / 2));
        }
        return g;
    }

    private static String name(int i) {
        return String.format("n%03d", i);
    }

    @Nested
    @DisplayName("측정 1: 답이 실제로 다르다")
    class DifferentAnswers {

        @Test
        @DisplayName("같은 그래프에 두 답이 나오고 둘 다 맞다")
        void bothAreCorrectAndDifferent() {
            DependencyGraph g = new DependencyGraph();
            g.dependsOn("z", "root");
            g.dependsOn("b", "root");
            g.dependsOn("m", "root");
            g.dependsOn("leaf", "z");

            List<String> kahn = new KahnResolver(g).resolve();
            List<String> dfs = new DfsResolver(g).resolve();

            System.out.printf("  칸  %s%n", kahn);
            System.out.printf("  DFS %s%n", dfs);

            assertEquals(List.of("root", "b", "m", "z", "leaf"), kahn);
            assertEquals(List.of("root", "z", "m", "leaf", "b"), dfs);
            assertNotEquals(kahn, dfs, "답이 같으면 이 박스의 전제가 무너진다");

            // 둘 다 맞다. 그래서 답을 비교하는 대신 성질을 검사한다.
            for (List<String> order : List.of(kahn, dfs)) {
                assertTrue(order.indexOf("root") < order.indexOf("z"));
                assertTrue(order.indexOf("z") < order.indexOf("leaf"));
                assertEquals(5, order.size());
            }
        }
    }

    @Nested
    @DisplayName("측정 2: 순환이 났을 때 말해줄 수 있는 것")
    class DiagnosticPower {

        @Test
        @DisplayName("칸은 있다는 것만, DFS 는 어디인지까지")
        void onlyDfsLocatesTheCycle() {
            DependencyGraph g = new DependencyGraph();
            // 순환 셋에 무관한 노드 100개를 붙여둔다. 건초더미 속의 바늘이다.
            for (int i = 0; i < 100; i++) {
                g.dependsOn("noise" + (i + 1), "noise" + i);
            }
            g.dependsOn("a", "c");
            g.dependsOn("b", "a");
            g.dependsOn("c", "b");

            CycleException byKahn =
                    assertThrows(CycleException.class, () -> new KahnResolver(g).resolve());
            CycleException byDfs =
                    assertThrows(CycleException.class, () -> new DfsResolver(g).resolve());

            System.out.printf("  노드 %,d개 중 순환은 셋이다%n", g.size());
            System.out.printf("    칸   %s%n", byKahn.getMessage());
            System.out.printf("    DFS  %s%n", byDfs.getMessage());

            assertEquals(0, byKahn.path().size(), "칸은 경로를 모른다");
            assertEquals(4, byDfs.path().size(), "DFS 는 셋에 처음을 한 번 더");
            assertTrue(byDfs.path().containsAll(List.of("a", "b", "c")));
            for (String node : byDfs.path()) {
                assertTrue(!node.startsWith("noise"), "잡음이 섞이면 안 된다: " + byDfs.path());
            }

            // 그래프가 아무리 커도 DFS 가 주는 경로는 순환의 크기만큼이다.
            assertEquals(104, g.size());
        }
    }

    @Nested
    @DisplayName("측정 3: 층 수가 코어를 늘려도 못 줄이는 시간이다")
    class CriticalPath {

        @Test
        @DisplayName("같은 100개인데 층이 100, 7, 1 이다")
        void shapeDecidesParallelism() {
            for (Resolver r : List.of(new KahnResolver(chain(100)), new DfsResolver(chain(100)))) {
                assertEquals(100, r.layers().size(), "사슬은 한 줄이라 못 줄인다");
            }

            int treeLayers = new KahnResolver(binaryTree(100)).layers().size();
            int flatLayers = new KahnResolver(independent(100)).layers().size();

            System.out.printf("  노드 100개, 모양만 다르다%n");
            System.out.printf("    사슬        층 %3d   (코어 100개여도 100 단위 시간)%n", 100);
            System.out.printf("    이진 트리    층 %3d%n", treeLayers);
            System.out.printf("    전부 독립    층 %3d   (코어 100개면 1 단위 시간)%n", flatLayers);

            assertEquals(7, treeLayers);
            assertEquals(1, flatLayers);

            // 두 구현이 같은 층을 낸다. 층은 답이 여럿인 값이 아니기 때문이다.
            assertEquals(new KahnResolver(binaryTree(100)).layers(),
                    new DfsResolver(binaryTree(100)).layers());
        }

        @Test
        @DisplayName("한계 - 사슬 하나가 전체를 붙든다")
        void oneLongChainHoldsEverything() {
            DependencyGraph g = independent(200);
            int before = new KahnResolver(g).layers().size();

            // 독립 노드 200개에 길이 30 짜리 사슬 하나를 더 붙인다.
            for (int i = 0; i < 30; i++) {
                g.dependsOn("chain" + (i + 1), "chain" + i);
            }
            List<List<String>> after = new KahnResolver(g).layers();

            System.out.printf("  독립 200개 -> 층 %d%n", before);
            System.out.printf("  거기에 길이 31 짜리 사슬 하나를 붙이면 -> 층 %d%n", after.size());
            System.out.printf("    0층에 %d개가 몰리고 나머지 30개 층은 하나씩이다%n", after.get(0).size());

            assertEquals(1, before);
            assertEquals(31, after.size());
            assertEquals(201, after.get(0).size(), "독립 200개 + 사슬의 머리");
            for (int i = 1; i < after.size(); i++) {
                assertEquals(1, after.get(i).size(), i + "층");
            }
            // 노드의 87퍼센트가 0층인데 전체는 31 단위 시간이 걸린다.
            // 평균이 아니라 가장 긴 사슬이 시간을 정한다. 25번 30번에서 본 것과 같은 자리다.
        }
    }

    @Nested
    @DisplayName("측정 4: 훑는 양은 둘 다 노드 더하기 간선이다")
    class BothAreLinear {

        @Test
        @DisplayName("칸은 간선마다 한 번, DFS 는 노드마다 한 번")
        void neitherRevisits() {
            DependencyGraph g = binaryTree(100);
            KahnResolver kahn = new KahnResolver(g);
            DfsResolver dfs = new DfsResolver(g);
            kahn.resolve();
            dfs.resolve();

            System.out.printf("  노드 %d, 간선 %d%n", g.size(), g.edgeCount());
            System.out.printf("    칸   진입 차수 내림 %d%n", kahn.relaxations());
            System.out.printf("    DFS  내려감 %d%n", dfs.visits());

            assertEquals(99, kahn.relaxations(), "간선 수와 같다");
            assertEquals(100, dfs.visits(), "노드 수와 같다");
            // 구조가 다른 것이 아니라 세는 대상이 다르다. 둘 다 O(V+E) 다.
        }
    }
}
