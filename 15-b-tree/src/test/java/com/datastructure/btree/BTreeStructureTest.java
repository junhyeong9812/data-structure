package com.datastructure.btree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("BTree 고유 성질")
class BTreeStructureTest {

    private BTree<Integer, String> tree(int t, int n) {
        BTree<Integer, String> b = new BTree<>(t);
        for (int i = 0; i < n; i++) {
            b.put(i, "v" + i);
        }
        return b;
    }

    /**
     * B-트리 불변식.
     *
     *   1. 뿌리가 아닌 노드는 키를 t-1 개 이상 2t-1 개 이하로 갖는다
     *   2. 키가 k 개면 자식이 0 개(잎) 또는 k+1 개다
     *   3. 노드 안의 키가 정렬돼 있다
     *   4. 모든 잎이 같은 깊이다
     */
    private void assertInvariants(BTree<Integer, String> b) {
        List<Integer> depths = new ArrayList<>();
        check(b, b.root, true, depths, 1);
        for (int d : depths) {
            assertEquals(depths.get(0), d, "잎마다 깊이가 다르다. 균형이 깨졌다");
        }
    }

    private void check(BTree<Integer, String> b, BTree.Node<Integer, String> node,
            boolean isRoot, List<Integer> depths, int depth) {
        int t = b.minDegree();
        assertTrue(node.keys.size() <= 2 * t - 1,
                "키가 " + node.keys.size() + "개다. 최대는 " + (2 * t - 1));
        if (!isRoot) {
            assertTrue(node.keys.size() >= t - 1,
                    "키가 " + node.keys.size() + "개뿐이다. 최소는 " + (t - 1));
        }
        assertEquals(node.keys.size(), node.values.size(), "키와 값의 개수가 다르다");
        for (int i = 1; i < node.keys.size(); i++) {
            assertTrue(node.keys.get(i - 1) < node.keys.get(i), "노드 안이 정렬돼 있지 않다");
        }
        if (node.leaf()) {
            depths.add(depth);
            return;
        }
        assertEquals(node.keys.size() + 1, node.children.size(),
                "키가 " + node.keys.size() + "개면 자식이 " + (node.keys.size() + 1) + "개여야 한다");
        for (BTree.Node<Integer, String> c : node.children) {
            check(b, c, false, depths, depth + 1);
        }
    }

    @Nested
    @DisplayName("불변식")
    class Invariants {

        @Test
        @DisplayName("넣는 내내 지켜진다")
        void heldDuringInsert() {
            BTree<Integer, String> b = new BTree<>(2);
            for (int i = 0; i < 300; i++) {
                b.put(i * 7 % 300, "v");
                assertInvariants(b);
            }
        }

        @Test
        @DisplayName("지우는 내내 지켜진다")
        void heldDuringDelete() {
            BTree<Integer, String> b = tree(3, 400);
            Random rnd = new Random(1234L);
            List<Integer> keys = new ArrayList<>();
            for (int i = 0; i < 400; i++) {
                keys.add(i);
            }
            java.util.Collections.shuffle(keys, rnd);
            for (int k : keys) {
                b.remove(k);
                assertInvariants(b);
            }
            assertTrue(b.isEmpty());
        }
    }

    @Nested
    @DisplayName("값이 모든 노드에 있다")
    class ValuesEverywhere {

        @Test
        @DisplayName("내부 노드에서도 값을 바로 꺼낸다")
        void internalNodesCarryValues() {
            // B+트리와 갈리는 지점이다.
            // B-트리는 운이 좋으면 뿌리에서 바로 답이 나온다. 대신 범위 조회가 불편하다.
            BTree<Integer, String> b = tree(2, 100);
            assertTrue(b.height() > 1);
            List<Integer> rootKeys = new ArrayList<>(b.root.keys);
            assertTrue(!rootKeys.isEmpty());
            for (int k : rootKeys) {
                assertEquals("v" + k, b.get(k), "뿌리의 키는 한 번에 나와야 한다");
            }
            assertEquals(rootKeys.size(), b.root.values.size(),
                    "내부 노드도 키마다 값을 들고 있다");
        }

        @Test
        @DisplayName("키는 트리 전체에서 한 번만 나온다")
        void noDuplication() {
            // B+트리는 구분키가 잎에 또 나온다. B-트리는 안 그렇다.
            BTree<Integer, String> b = tree(2, 200);
            List<Integer> all = new ArrayList<>();
            gather(b.root, all);
            assertEquals(200, all.size(), "중복이 있으면 200 보다 크다");
            assertEquals(b.keys(), all.stream().sorted().toList());
        }

        private void gather(BTree.Node<Integer, String> node, List<Integer> out) {
            out.addAll(node.keys);
            for (BTree.Node<Integer, String> c : node.children) {
                gather(c, out);
            }
        }
    }

    @Nested
    @DisplayName("인자 검사")
    class Args {

        @Test
        @DisplayName("차수는 2 이상")
        void degreeAtLeastTwo() {
            assertThrows(IllegalArgumentException.class, () -> new BTree<>(1));
            assertThrows(IllegalArgumentException.class, () -> new BTree<>(0));
            assertThrows(IllegalArgumentException.class, () -> new BPlusTree<>(2));
        }
    }
}
