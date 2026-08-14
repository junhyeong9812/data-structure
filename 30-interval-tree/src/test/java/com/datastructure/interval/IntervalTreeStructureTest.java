package com.datastructure.interval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 트리 내부를 직접 본다. 계약 테스트만으로는 안 잡히는 것이 여기 있다.
 *
 * 계약 테스트는 "답이 맞는가"만 본다. 그건 전수 조사로도 통과한다.
 * 여기서는 시작점 순서와 maxEnd 가 실제로 지켜지는지 노드를 재귀로 검사한다.
 *
 * maxEnd 가 낡으면 예외가 안 난다. 답이 조용히 빠진다.
 * 그 조용한 실패를 가장 빨리 잡는 것이 이 불변식 검사다.
 */
@DisplayName("IntervalTree 의 내부 구조")
class IntervalTreeStructureTest {

    /** 부분트리에 실제로 있는 end 의 최댓값. 노드가 들고 있는 maxEnd 와 따로 계산한다. */
    private static long actualMaxEnd(IntervalTree.Node node) {
        if (node == null) return Long.MIN_VALUE;
        return Math.max(node.interval.end,
                Math.max(actualMaxEnd(node.left), actualMaxEnd(node.right)));
    }

    private static void assertMaxEndSound(IntervalTree.Node node) {
        if (node == null) return;
        assertEquals(actualMaxEnd(node), node.maxEnd,
                "노드 " + node.interval + " 의 maxEnd 가 부분트리의 실제 최댓값과 다르다");
        assertMaxEndSound(node.left);
        assertMaxEndSound(node.right);
    }

    /** 시작점 순서. 06번의 탐색 성질 그대로다. */
    private static void assertOrdered(IntervalTree.Node node, Interval lower, Interval upper) {
        if (node == null) return;
        if (lower != null) {
            assertTrue(node.interval.compareTo(lower) > 0,
                    node.interval + " 가 조상 " + lower + " 보다 뒤여야 한다");
        }
        if (upper != null) {
            assertTrue(node.interval.compareTo(upper) < 0,
                    node.interval + " 가 조상 " + upper + " 보다 앞이어야 한다");
        }
        assertOrdered(node.left, lower, node.interval);
        assertOrdered(node.right, node.interval, upper);
    }

    private static int countNodes(IntervalTree.Node node) {
        return node == null ? 0 : 1 + countNodes(node.left) + countNodes(node.right);
    }

    private static void assertSound(IntervalTree tree) {
        assertOrdered(tree.root, null, null);
        assertMaxEndSound(tree.root);
        assertEquals(tree.size(), countNodes(tree.root), "노드 수가 size 와 다르다");
    }

    private static IntervalTree treeOf(long... startEndPairs) {
        IntervalTree tree = new IntervalTree();
        for (int i = 0; i < startEndPairs.length; i += 2) {
            tree.insert(Interval.of(startEndPairs[i], startEndPairs[i + 1]));
        }
        return tree;
    }

    @Nested
    @DisplayName("maxEnd 불변식")
    class MaxEndInvariant {

        @Test
        @DisplayName("잎의 maxEnd 는 자기 end 다")
        void leafHoldsOwnEnd() {
            IntervalTree tree = treeOf(9, 11);
            assertEquals(11, tree.root.maxEnd);
            assertNull(tree.root.left);
            assertNull(tree.root.right);
        }

        @Test
        @DisplayName("뿌리의 maxEnd 는 전체의 최대 end 다")
        void rootHoldsGlobalMax() {
            IntervalTree tree = treeOf(50, 60, 10, 999, 70, 80, 20, 25);
            assertEquals(999, tree.root.maxEnd, "왼쪽 깊은 곳의 999 가 뿌리까지 올라와야 한다");
            assertSound(tree);
        }

        @Test
        @DisplayName("삽입할 때마다 모든 노드의 maxEnd 가 실제 최댓값과 같다")
        void everyInsertKeepsInvariant() {
            // 조상 갱신을 빼도 예외는 안 난다. 여기서만 걸린다.
            IntervalTree tree = new IntervalTree();
            TestSupport.Dice dice = new TestSupport.Dice(20260814L);
            for (int i = 0; i < 400; i++) {
                tree.insert(dice.interval(5000, 900));
                assertSound(tree);
            }
            assertTrue(tree.size() > 350, "중복이 너무 많다: " + tree.size());
        }

        @Test
        @DisplayName("지울 때마다 모든 노드의 maxEnd 가 실제 최댓값과 같다")
        void everyRemoveKeepsInvariant() {
            IntervalTree tree = new IntervalTree();
            TestSupport.Dice dice = new TestSupport.Dice(4242L);
            for (int i = 0; i < 300; i++) tree.insert(dice.interval(2000, 700));
            List<Interval> all = new ArrayList<>(tree.toList());
            assertSound(tree);

            for (int i = 0; i < all.size(); i += 2) {
                assertTrue(tree.remove(all.get(i)));
                assertSound(tree);
            }
            for (int i = 1; i < all.size(); i += 2) {
                assertTrue(tree.remove(all.get(i)));
                assertSound(tree);
            }
            assertNull(tree.root, "전부 지우면 뿌리가 없다");
        }

        @Test
        @DisplayName("가장 늦게 끝나던 구간을 지우면 조상의 maxEnd 가 줄어야 한다")
        void removalShrinksAncestorMaxEnd() {
            IntervalTree tree = treeOf(0, 5, 10, 100, 20, 30);
            assertEquals(100, tree.root.maxEnd);
            assertTrue(tree.remove(Interval.of(10, 100)));
            assertEquals(30, tree.root.maxEnd,
                    "100 이 사라졌는데 뿌리가 아직 100 을 들고 있으면 답은 맞고 방문 수만 는다");
            assertSound(tree);
        }

        @Test
        @DisplayName("낡은 maxEnd 는 예외가 아니라 누락으로 나타난다")
        void staleMaxEndLosesAnswersSilently() {
            // 넣는 순서를 일부러 잡았다. [0,100) 이 두 단계 아래에 들어간다.
            // 삽입에서 조상 갱신을 빼면 [10,20) 의 maxEnd 가 20 에 머문다.
            // 그러면 질의 [70,80) 이 왼쪽으로 안 내려가 [0,100) 을 못 찾는다. 예외는 안 난다.
            IntervalTree tree = treeOf(50, 60, 10, 20, 0, 100);
            assertEquals(100, tree.root.left.maxEnd,
                    "[0,100) 의 끝점이 부모 [10,20) 까지 올라와야 한다");

            Interval q = Interval.of(70, 80);
            assertEquals(List.of(Interval.of(0, 100)), tree.findAll(q));
            assertEquals(Interval.of(0, 100), tree.findAny(q));
            assertTrue(tree.anyOverlaps(q));
        }
    }

    @Nested
    @DisplayName("시작점 순서")
    class Ordering {

        @Test
        @DisplayName("먼저 넣은 것이 뿌리다. 06번과 같다")
        void firstInsertBecomesRoot() {
            IntervalTree tree = treeOf(50, 60, 10, 20, 70, 80);
            assertEquals(Interval.of(50, 60), tree.root.interval);
            assertEquals(Interval.of(10, 20), tree.root.left.interval);
            assertEquals(Interval.of(70, 80), tree.root.right.interval);
        }

        @Test
        @DisplayName("start 가 같으면 end 로 갈린다")
        void tiesBreakOnEnd() {
            IntervalTree tree = treeOf(10, 50, 10, 20, 10, 90);
            assertEquals(Interval.of(10, 20), tree.root.left.interval);
            assertEquals(Interval.of(10, 90), tree.root.right.interval);
            assertSound(tree);
        }

        @Test
        @DisplayName("자식이 둘인 노드를 지워도 순서와 maxEnd 가 지켜진다")
        void removeWithTwoChildren() {
            IntervalTree tree = treeOf(50, 60, 30, 40, 70, 80, 20, 25, 40, 45, 60, 65, 90, 95);
            assertTrue(tree.remove(Interval.of(30, 40)));
            assertSound(tree);
            assertTrue(tree.remove(Interval.of(50, 60)), "뿌리를 지운다");
            assertSound(tree);
            assertEquals(5, tree.size());
        }

        @Test
        @DisplayName("clear 는 뿌리만 끊으면 된다")
        void clearDetachesRoot() {
            IntervalTree tree = treeOf(1, 2, 3, 4, 5, 6);
            tree.clear();
            assertNull(tree.root);
            assertEquals(0, tree.size);
        }
    }

    @Nested
    @DisplayName("findAny 는 한 갈래로만 내려간다")
    class SinglePathDescent {

        @Test
        @DisplayName("방문 수가 높이를 넘지 않는다")
        void visitsNeverExceedHeight() {
            IntervalTree tree = new IntervalTree();
            TestSupport.Dice dice = new TestSupport.Dice(31L);
            for (int i = 0; i < 2000; i++) tree.insert(dice.interval(100000, 300));
            int height = tree.height();

            TestSupport.Dice queries = new TestSupport.Dice(77L);
            for (int i = 0; i < 500; i++) {
                Interval q = queries.interval(100000, 500);
                Interval got = tree.findAny(q);
                assertTrue(tree.visitedNodes() <= height,
                        "findAny 가 " + tree.visitedNodes() + " 번 봤다. 높이는 " + height + " 다");
                if (got != null) {
                    assertTrue(got.overlaps(q), got + " 가 " + q + " 와 안 겹친다");
                }
            }
        }

        @Test
        @DisplayName("겹치는 것이 없으면 방문해도 null 을 준다")
        void emptyAnswerStillWalksOnePath() {
            IntervalTree tree = treeOf(0, 10, 20, 30, 40, 50);
            assertNull(tree.findAny(Interval.of(10, 20)));
            assertTrue(tree.visitedNodes() >= 1, "적어도 뿌리는 본다");
            assertTrue(tree.visitedNodes() <= tree.height());
        }
    }

    @Nested
    @DisplayName("방문 수는 마지막 질의의 값이다")
    class VisitCounter {

        @Test
        void resetsOnEveryQuery() {
            IntervalTree tree = treeOf(0, 10, 20, 30, 40, 50, 60, 70);
            assertEquals(0, tree.visitedNodes(), "질의 전이면 0 이다");

            tree.findAll(Interval.of(0, 1000));
            long wide = tree.visitedNodes();
            assertEquals(4, wide, "전부 겹치는 질의는 노드를 전부 본다");

            tree.findAll(Interval.of(-100, -99));
            assertEquals(1, tree.visitedNodes(),
                    "질의가 전부보다 앞이면 뿌리에서 오른쪽이 잘린다. 누적이면 " + (wide + 1) + " 이 나온다");

            long afterQuery = tree.visitedNodes();
            tree.insert(Interval.of(80, 90));
            tree.toList();
            assertEquals(afterQuery, tree.visitedNodes(), "질의가 아닌 연산은 이 값을 안 건드린다");
        }

        @Test
        @DisplayName("전수 조사는 findAll 에서 언제나 정확히 n 이다")
        void naiveAlwaysScansEverything() {
            NaiveIntervalStore naive = new NaiveIntervalStore();
            for (int i = 0; i < 50; i++) naive.insert(Interval.of(i * 10, i * 10 + 5));
            naive.findAll(Interval.of(0, 1));
            assertEquals(50, naive.visitedNodes());
            naive.findAll(Interval.of(100000, 100001));
            assertEquals(50, naive.visitedNodes(), "답이 없어도 전부 본다. 그게 전수 조사다");
        }
    }
}
