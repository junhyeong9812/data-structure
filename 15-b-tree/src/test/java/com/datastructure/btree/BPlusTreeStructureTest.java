package com.datastructure.btree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("BPlusTree 고유 성질")
class BPlusTreeStructureTest {

    private BPlusTree<Integer, String> tree(int order, int n) {
        BPlusTree<Integer, String> t = new BPlusTree<>(order);
        for (int i = 0; i < n; i++) {
            t.put(i, "v" + i);
        }
        return t;
    }

    @Nested
    @DisplayName("값이 잎에만 있다")
    class ValuesOnlyInLeaves {

        @Test
        @DisplayName("내부 노드는 길잡이 키만 들고 있다")
        void internalNodesHaveNoValues() {
            BPlusTree<Integer, String> t = tree(4, 200);
            assertTrue(t.height() > 1);
            assertNoValues(t.root);
        }

        private void assertNoValues(BPlusTree.Node<Integer, String> node) {
            if (node.leaf) {
                assertEquals(node.keys.size(), node.values.size(), "잎은 키마다 값이 있어야 한다");
                return;
            }
            assertTrue(node.values.isEmpty(),
                    "내부 노드에 값이 " + node.values.size() + "개 있다. 잎에만 있어야 한다");
            assertEquals(node.keys.size() + 1, node.children.size(),
                    "내부 노드는 키가 k 개면 자식이 k+1 개다");
            for (BPlusTree.Node<Integer, String> c : node.children) {
                assertNoValues(c);
            }
        }

        @Test
        @DisplayName("구분키는 잎에도 있다")
        void separatorsAreDuplicated() {
            // B-트리는 키가 한 군데에만 있다. B+트리는 **구분키가 잎에 또 있다.**
            // 그 중복이 대가이고, 대신 잎만 훑으면 전부가 나온다는 것을 얻는다.
            BPlusTree<Integer, String> t = tree(4, 50);
            List<Integer> fromLeaves = t.keys();
            assertEquals(50, fromLeaves.size(), "잎만 훑어도 전부 나온다");
            for (int i = 0; i < 50; i++) {
                assertEquals(i, fromLeaves.get(i));
            }
        }
    }

    @Nested
    @DisplayName("잎이 사슬로 이어져 있다")
    class LeafChain {

        @Test
        @DisplayName("첫 잎에서 next 만 따라가면 전부 나온다")
        void chainCoversEverything() {
            BPlusTree<Integer, String> t = tree(4, 500);
            List<Integer> walked = new ArrayList<>();
            int leafCount = 0;
            for (BPlusTree.Node<Integer, String> leaf = t.firstLeaf(); leaf != null; leaf = leaf.next) {
                walked.addAll(leaf.keys);
                leafCount++;
            }
            assertEquals(500, walked.size());
            assertTrue(leafCount > 1, "잎이 " + leafCount + "개다");
            for (int i = 0; i < 500; i++) {
                assertEquals(i, walked.get(i), "사슬이 정렬 순서를 지켜야 한다");
            }
        }

        @Test
        @DisplayName("지운 뒤에도 사슬이 안 끊긴다")
        void chainSurvivesDeletion() {
            // 잎을 병합할 때 next 를 안 이어주면 여기서 뒷부분이 통째로 사라진다.
            BPlusTree<Integer, String> t = tree(4, 500);
            for (int i = 0; i < 500; i += 2) {
                t.remove(i);
            }
            List<Integer> walked = new ArrayList<>();
            for (BPlusTree.Node<Integer, String> leaf = t.firstLeaf(); leaf != null; leaf = leaf.next) {
                walked.addAll(leaf.keys);
            }
            assertEquals(250, walked.size(), "사슬로 걸은 것과 크기가 같아야 한다");
            assertEquals(t.size(), walked.size());
            assertEquals(t.keys(), walked);
        }
    }

    @Nested
    @DisplayName("범위 조회가 이 구조의 존재 이유다")
    class RangeScan {

        @Test
        @DisplayName("시작 잎을 찾아 사슬을 걷는다")
        void scansRange() {
            BPlusTree<Integer, String> t = tree(4, 1000);
            List<Integer> expected = new ArrayList<>();
            for (int i = 300; i <= 350; i++) {
                expected.add(i);
            }
            assertEquals(expected, t.keysInRange(300, 350));
        }

        @Test
        @DisplayName("경계와 빈 범위")
        void boundaries() {
            BPlusTree<Integer, String> t = new BPlusTree<>(4);
            for (int k : new int[]{10, 20, 30, 40, 50}) {
                t.put(k, "v");
            }
            assertEquals(List.of(20, 30, 40), t.keysInRange(20, 40));
            assertEquals(List.of(20, 30), t.keysInRange(15, 35), "없는 경계값도 된다");
            assertEquals(List.of(10, 20, 30, 40, 50), t.keysInRange(-100, 100));
            assertEquals(List.of(), t.keysInRange(60, 70));
            assertEquals(List.of(), t.keysInRange(40, 20), "from > to");
            assertEquals(List.of(30), t.keysInRange(30, 30));
        }

        @Test
        @DisplayName("지운 뒤에도 맞다")
        void afterDeletion() {
            BPlusTree<Integer, String> t = tree(5, 2000);
            Random rnd = new Random(101L);
            TreeMap<Integer, String> ref = new TreeMap<>();
            for (int i = 0; i < 2000; i++) {
                ref.put(i, "v" + i);
            }
            for (int step = 0; step < 1000; step++) {
                int k = rnd.nextInt(2000);
                ref.remove(k);
                t.remove(k);
            }
            for (int trial = 0; trial < 200; trial++) {
                int from = rnd.nextInt(2000);
                int to = from + rnd.nextInt(300);
                assertEquals(new ArrayList<>(ref.subMap(from, true, to, true).keySet()),
                        t.keysInRange(from, to), "범위 [" + from + ", " + to + "]");
            }
        }

        @Test
        @Timeout(20)
        @DisplayName("전체를 안 훑는다")
        void doesNotScanWholeTree() {
            // 잎 사슬이 없으면 트리를 다시 내려가며 순회해야 한다.
            // 여기서는 시작 잎만 찾고 **디스크에서는 순차 읽기**가 된다.
            BPlusTree<Integer, String> t = new BPlusTree<>(64);
            for (int i = 0; i < 300_000; i++) {
                t.put(i, "v");
            }
            for (int q = 0; q < 20_000; q++) {
                assertEquals(11, t.keysInRange(150_000, 150_010).size());
            }
        }
    }

    @Nested
    @DisplayName("낮은 높이")
    class Fanout {

        @Test
        @DisplayName("차수 100 이면 100만 개가 네 층 안이다")
        void wideIsShallow() {
            BPlusTree<Integer, String> t = new BPlusTree<>(100);
            for (int i = 0; i < 1_000_000; i++) {
                t.put(i, "v");
            }
            assertTrue(t.height() <= 4,
                    "100만 개가 " + t.height() + "층. 디스크 읽기 " + t.height() + "번이다");
            assertEquals(1_000_000, t.size());
            assertNull(t.get(-1));
            assertEquals("v", t.get(999_999));
        }
    }
}
