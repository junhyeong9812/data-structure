package com.datastructure.splay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("스플레이 트리의 구조")
class SplayTreeStructureTest {

    /**
     * 스플레이 트리가 지켜야 하는 것은 딱 하나다.
     *
     *   탐색 성질: 왼쪽 부분트리 전부 < 자기 < 오른쪽 부분트리 전부
     *
     * 16번 레드블랙 트리는 여기에 색 규칙 넷을 더 지켜야 했다. 여기는 없다.
     * 균형을 목표로 삼지 않으니 검사할 균형 조건도 없다.
     * 그래서 회전 순서를 잘못 짜도 이 검사는 통과한다. 그건 AmortizedCostTest 가 잡는다.
     */
    private void assertBst(SplayTree<Integer, String> t) {
        int counted = check(t.root, null, null);
        assertEquals(t.size(), counted, "size 와 실제 노드 수가 다르다");
        List<Integer> keys = t.keys();
        assertEquals(t.size(), keys.size(), "중위 순회가 노드를 빠뜨렸거나 더 셌다");
        for (int i = 1; i < keys.size(); i++) {
            assertTrue(keys.get(i - 1) < keys.get(i), "중위 순회가 정렬돼 있지 않다: " + keys);
        }
    }

    private int check(SplayTree.Node<Integer, String> h, Integer low, Integer high) {
        if (h == null) {
            return 0;
        }
        if (low != null) {
            assertTrue(h.key > low, "키 " + h.key + " 가 왼쪽 경계 " + low + " 이하다");
        }
        if (high != null) {
            assertTrue(h.key < high, "키 " + h.key + " 가 오른쪽 경계 " + high + " 이상이다");
        }
        return 1 + check(h.left, low, h.key) + check(h.right, h.key, high);
    }

    @Nested
    @DisplayName("노드에 여분 데이터가 없다")
    class NoExtraFields {

        @Test
        @DisplayName("필드는 key, value, left, right 넷뿐이다")
        void fourFields() {
            List<String> names = new ArrayList<>();
            for (Field f : SplayTree.Node.class.getDeclaredFields()) {
                if (!f.isSynthetic() && !Modifier.isStatic(f.getModifiers())) {
                    names.add(f.getName());
                }
            }
            Collections.sort(names);
            // 16번 RedBlackTree.Node 에는 여기에 color 가 하나 더 있다.
            // 12번 스킵 리스트에는 층 배열이, 15번 B-트리에는 키 배열과 자식 배열이 있었다.
            // 스플레이 트리는 균형 정보를 아예 저장하지 않는다. 그게 이 자료구조의 값이다.
            assertEquals(List.of("key", "left", "right", "value"), names,
                    "노드에 여분 필드가 생겼다: " + names);
        }
    }

    @Nested
    @DisplayName("조회가 구조를 바꾼다")
    class ReadsAreWrites {

        @Test
        @DisplayName("get 한 번이면 그 키가 뿌리다")
        void getSplaysToRoot() {
            SplayTree<Integer, String> t = new SplayTree<>();
            for (int k : new int[]{50, 30, 70, 20, 40, 60, 80, 10}) {
                t.put(k, "v" + k);
            }
            for (int k : new int[]{10, 80, 40, 50, 20}) {
                assertEquals("v" + k, t.get(k));
                assertEquals(0, t.depthOf(k), "get(" + k + ") 뒤에 " + k + " 가 뿌리가 아니다");
                assertBst(t);
            }
        }

        @Test
        @DisplayName("못 찾아도 splay 한다. 가장 가까운 노드가 뿌리로 온다")
        void missingKeyStillSplays() {
            SplayTree<Integer, String> t = new SplayTree<>();
            for (int k = 0; k < 100; k++) {
                t.put(k * 10, "v");
            }
            assertNull(t.get(455));
            // 455 는 없다. 450 이나 460 중 하나가 뿌리로 올라와 있어야 한다.
            int rootKey = t.root.key;
            assertTrue(rootKey == 450 || rootKey == 460,
                    "못 찾은 조회가 아무것도 안 했다. 뿌리 키 = " + rootKey);
            assertBst(t);

            assertNull(t.get(-1));
            assertEquals(0, t.depthOf(0), "범위 왼쪽 밖을 찾으면 최솟값이 뿌리로 와야 한다");
            assertNull(t.get(100_000));
            assertEquals(0, t.depthOf(990), "범위 오른쪽 밖을 찾으면 최댓값이 뿌리로 와야 한다");
        }

        @Test
        @DisplayName("put 은 새 키를 뿌리에 놓는다")
        void putPutsNewKeyAtRoot() {
            SplayTree<Integer, String> t = new SplayTree<>();
            Random rnd = new Random(11L);
            for (int i = 0; i < 300; i++) {
                int k = rnd.nextInt(1000);
                t.put(k, "v" + k);
                assertEquals(0, t.depthOf(k), "put(" + k + ") 뒤에 그 키가 뿌리가 아니다");
                assertBst(t);
            }
        }

        @Test
        @DisplayName("floorKey 도 쓰기다")
        void floorKeyMutates() {
            // 이 성질 때문에 SortedTree 를 읽기 잠금으로 감쌀 수 없다.
            // 10번 LRU 캐시의 get, 13번 lazy 세그먼트 트리의 rangeSum,
            // 14번 유니온파인드의 find 와 같은 종류다.
            SplayTree<Integer, String> t = new SplayTree<>();
            for (int k = 0; k < 200; k++) {
                t.put(k, "v");
            }
            t.get(199);
            int before = t.depthOf(3);
            assertNotEquals(0, before, "준비가 안 됐다. 3 이 이미 뿌리다");
            assertEquals(3, t.floorKey(3));
            assertEquals(0, t.depthOf(3), "floorKey 가 트리를 안 건드렸다");
            assertBst(t);
        }

        @Test
        @DisplayName("keys 와 depthOf 와 height 는 안 건드린다")
        void observersDoNotMutate() {
            SplayTree<Integer, String> t = new SplayTree<>();
            for (int k = 0; k < 50; k++) {
                t.put(k * 3, "v");
            }
            t.get(66);
            List<Integer> before = t.keys();
            int h = t.height();
            int d = t.depthOf(9);
            long rot = t.rotations();
            assertEquals(before, t.keys());
            assertEquals(h, t.height());
            assertEquals(d, t.depthOf(9));
            assertEquals(rot, t.rotations(), "관찰자가 회전을 일으켰다");
        }
    }

    @Nested
    @DisplayName("매 연산 뒤에 탐색 성질이 남아 있다")
    class InvariantHolds {

        @Test
        @DisplayName("오름차순으로 넣는 내내")
        void ascending() {
            SplayTree<Integer, String> t = new SplayTree<>();
            for (int i = 0; i < 300; i++) {
                t.put(i, "v");
                assertBst(t);
            }
        }

        @Test
        @DisplayName("내림차순으로 넣는 내내")
        void descending() {
            SplayTree<Integer, String> t = new SplayTree<>();
            for (int i = 300; i > 0; i--) {
                t.put(i, "v");
                assertBst(t);
            }
        }

        @Test
        @DisplayName("넣기, 지우기, 조회를 섞어서 3000번")
        void mixed() {
            Random rnd = new Random(31337L);
            SplayTree<Integer, String> t = new SplayTree<>();
            for (int step = 0; step < 3000; step++) {
                int k = rnd.nextInt(120);
                int op = rnd.nextInt(4);
                if (op == 0) {
                    t.put(k, "v" + k);
                } else if (op == 1) {
                    t.remove(k);
                } else if (op == 2) {
                    t.get(k);
                } else {
                    t.floorKey(k);
                    t.ceilingKey(k);
                }
                assertBst(t);
            }
        }

        @Test
        @DisplayName("작은 트리에서 모든 삭제 순서")
        void tinyTreesEveryDeletionOrder() {
            // 트리가 작을수록 remove 의 특수한 경우(왼쪽이 없다, 오른쪽이 없다)가 자주 나온다.
            for (int n = 1; n <= 6; n++) {
                permute(new int[n], 0, n, new boolean[n]);
            }
        }

        private void permute(int[] order, int depth, int n, boolean[] used) {
            if (depth == n) {
                SplayTree<Integer, String> t = new SplayTree<>();
                for (int i = 0; i < n; i++) {
                    t.put(i, "v" + i);
                }
                assertBst(t);
                for (int i = 0; i < n; i++) {
                    assertEquals("v" + order[i], t.remove(order[i]),
                            "n=" + n + " 순서 " + Arrays.toString(order));
                    assertBst(t);
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
}
