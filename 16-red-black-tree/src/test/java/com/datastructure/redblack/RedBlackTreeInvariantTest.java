package com.datastructure.redblack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("레드블랙 불변식")
class RedBlackTreeInvariantTest {

    /**
     * 좌편향 레드블랙 트리의 다섯 가지 불변식.
     *
     *   1. 뿌리는 검다
     *   2. 빨간 링크는 왼쪽에만 (좌편향의 정의)
     *   3. 빨간 노드가 연달아 나오지 않는다
     *   4. 모든 경로의 검은 링크 수가 같다 (균형의 정체)
     *   5. 탐색 성질이 지켜진다
     *
     * 4번이 균형의 정체다. 검은 높이가 같으니 가장 짧은 경로(전부 검정)와
     * 가장 긴 경로(검빨검빨...)의 길이 차이가 두 배를 못 넘는다.
     */
    private void assertInvariants(RedBlackTree<Integer, String> t) {
        assertTrue(!RedBlackTree.isRed(t.root), "뿌리가 빨갛다");
        checkNode(t.root);
        assertTrue(blackHeightOrMinusOne(t.root) >= 0, "경로마다 검은 링크 수가 다르다");
        List<Integer> keys = t.keys();
        for (int i = 1; i < keys.size(); i++) {
            assertTrue(keys.get(i - 1) < keys.get(i), "중위 순회가 정렬돼 있지 않다");
        }
        assertEquals(t.size(), keys.size(), "size 와 실제 노드 수가 다르다");
    }

    private void checkNode(RedBlackTree.Node<Integer, String> h) {
        if (h == null) {
            return;
        }
        assertTrue(!RedBlackTree.isRed(h.right),
                "오른쪽으로 기운 빨간 링크가 있다 (키 " + h.key + "). 좌편향이 아니다");
        if (RedBlackTree.isRed(h)) {
            assertTrue(!RedBlackTree.isRed(h.left),
                    "빨간 노드가 연달아 있다 (키 " + h.key + ")");
        }
        checkNode(h.left);
        checkNode(h.right);
    }

    /** 검은 높이. 좌우가 다르면 -1. */
    private int blackHeightOrMinusOne(RedBlackTree.Node<Integer, String> h) {
        if (h == null) {
            return 0;
        }
        int l = blackHeightOrMinusOne(h.left);
        int r = blackHeightOrMinusOne(h.right);
        if (l < 0 || r < 0 || l != r) {
            return -1;
        }
        return l + (RedBlackTree.isRed(h) ? 0 : 1);
    }

    @Nested
    @DisplayName("넣는 내내 지켜진다")
    class DuringInsert {

        @Test
        @DisplayName("오름차순")
        void ascending() {
            RedBlackTree<Integer, String> t = new RedBlackTree<>();
            for (int i = 0; i < 300; i++) {
                t.put(i, "v");
                assertInvariants(t);
            }
        }

        @Test
        @DisplayName("내림차순")
        void descending() {
            RedBlackTree<Integer, String> t = new RedBlackTree<>();
            for (int i = 300; i > 0; i--) {
                t.put(i, "v");
                assertInvariants(t);
            }
        }

        @Test
        @DisplayName("무작위")
        void shuffled() {
            Random rnd = new Random(77L);
            RedBlackTree<Integer, String> t = new RedBlackTree<>();
            for (int i = 0; i < 500; i++) {
                t.put(rnd.nextInt(1000), "v");
                assertInvariants(t);
            }
        }
    }

    @Nested
    @DisplayName("지우는 내내 지켜진다")
    class DuringDelete {

        @Test
        @DisplayName("무작위 순서로 하나씩")
        void shuffledDelete() {
            Random rnd = new Random(1234L);
            RedBlackTree<Integer, String> t = new RedBlackTree<>();
            List<Integer> keys = new ArrayList<>();
            for (int i = 0; i < 400; i++) {
                t.put(i, "v" + i);
                keys.add(i);
            }
            Collections.shuffle(keys, rnd);
            for (int k : keys) {
                assertEquals("v" + k, t.remove(k), "키 " + k);
                assertInvariants(t);
            }
            assertTrue(t.isEmpty());
        }

        @Test
        @DisplayName("앞에서부터 지우기")
        void deleteFromFront() {
            RedBlackTree<Integer, String> t = new RedBlackTree<>();
            for (int i = 0; i < 300; i++) {
                t.put(i, "v");
            }
            for (int i = 0; i < 300; i++) {
                t.remove(i);
                assertInvariants(t);
            }
        }

        @Test
        @DisplayName("넣기와 지우기를 섞어서")
        void mixed() {
            Random rnd = new Random(31337L);
            RedBlackTree<Integer, String> t = new RedBlackTree<>();
            for (int step = 0; step < 3000; step++) {
                int k = rnd.nextInt(120);
                if (rnd.nextBoolean()) {
                    t.put(k, "v" + k);
                } else {
                    t.remove(k);
                }
                assertInvariants(t);
            }
        }

        @Test
        @org.junit.jupiter.api.Timeout(60)
        @DisplayName("작은 트리를 쥐어짜기")
        void tinyTreesUnderPressure() {
            // 트리가 작을수록 뿌리 근처의 특수한 경우가 자주 나온다.
            // 크기 1~6 짜리 트리에서 모든 삭제 순서를 훑는다.
            for (int n = 1; n <= 6; n++) {
                int[] order = new int[n];
                permute(order, 0, n, new boolean[n]);
            }
        }

        private void permute(int[] order, int depth, int n, boolean[] used) {
            if (depth == n) {
                RedBlackTree<Integer, String> t = new RedBlackTree<>();
                for (int i = 0; i < n; i++) {
                    t.put(i, "v" + i);
                }
                assertInvariants(t);
                for (int i = 0; i < n; i++) {
                    assertEquals("v" + order[i], t.remove(order[i]),
                            "n=" + n + " 순서 " + java.util.Arrays.toString(order));
                    assertInvariants(t);
                }
                assertTrue(t.isEmpty());
                return;
            }
            for (int i = 0; i < n; i++) {
                if (!used[i]) {
                    used[i] = true;
                    order[depth] = i;
                    permute(order, depth + 1, n, used);
                    used[i] = false;
                }
            }
        }
    }

    @Nested
    @DisplayName("2-3 트리와의 대응")
    class TwoThreeCorrespondence {

        @Test
        @DisplayName("검은 높이가 2-3 트리의 높이다")
        void blackHeightIsTwoThreeHeight() {
            // 빨간 링크로 이어진 두 노드가 2-3 트리의 키 2개짜리 노드다.
            // 그래서 **검은 링크만 세면** 그게 2-3 트리의 층수다.
            RedBlackTree<Integer, String> t = new RedBlackTree<>();
            for (int i = 0; i < 100_000; i++) {
                t.put(i, "v");
            }
            int bh = t.blackHeight();
            double log2 = Math.log(100_000) / Math.log(2);
            double log3 = Math.log(100_000) / Math.log(3);
            assertTrue(bh >= log3 - 1 && bh <= log2 + 1,
                    "검은 높이 " + bh + " 가 log3(n)=" + (int) log3
                            + " 과 log2(n)=" + (int) log2 + " 사이여야 한다");
            assertTrue(t.height() <= 2 * bh,
                    "전체 높이가 검은 높이의 두 배를 넘었다: " + t.height() + " 대 " + bh);
        }

        @Test
        @DisplayName("빨간 노드는 전체의 절반을 못 넘는다")
        void redNodesAreMinority() {
            RedBlackTree<Integer, String> t = new RedBlackTree<>();
            Random rnd = new Random(5L);
            for (int i = 0; i < 20_000; i++) {
                t.put(rnd.nextInt(100_000), "v");
            }
            int[] counts = new int[2];
            count(t.root, counts);
            assertEquals(t.size(), counts[0] + counts[1]);
            assertTrue(counts[1] <= counts[0],
                    "빨강 " + counts[1] + " 개, 검정 " + counts[0] + " 개");
        }

        private void count(RedBlackTree.Node<Integer, String> h, int[] counts) {
            if (h == null) {
                return;
            }
            counts[RedBlackTree.isRed(h) ? 1 : 0]++;
            count(h.left, counts);
            count(h.right, counts);
        }
    }
}
