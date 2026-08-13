package com.datastructure.bst;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BinarySearchTree 가 계약을 지키는지 + 트리 구조 자체를 본다.
 *
 * 계약 테스트는 "정렬된 결과가 나오는가"만 본다. 그건 정렬해서 내놓기만 해도 통과한다.
 * 여기서는 **왼쪽은 작고 오른쪽은 크다**가 실제로 지켜지는지 노드를 직접 검사한다.
 */
class BinarySearchTreeTest extends SortedMapContractTest {

    @Override
    protected <K extends Comparable<K>, V> SortedMap<K, V> create() {
        return new BinarySearchTree<>();
    }

    /** 탐색 성질이 지켜지는지 재귀로 확인한다. 이게 이 자료구조의 불변식이다. */
    private static <K extends Comparable<K>, V> void assertBstProperty(
        BinarySearchTree.Node<K, V> node, K lower, K upper) {
        if (node == null) return;
        if (lower != null) {
            assertTrue(node.key.compareTo(lower) > 0,
                "노드 " + node.key + " 가 조상 " + lower + " 보다 커야 하는데 아니다");
        }
        if (upper != null) {
            assertTrue(node.key.compareTo(upper) < 0,
                "노드 " + node.key + " 가 조상 " + upper + " 보다 작아야 하는데 아니다");
        }
        assertBstProperty(node.left, lower, node.key);
        assertBstProperty(node.right, node.key, upper);
    }

    private static <K extends Comparable<K>, V> int countNodes(BinarySearchTree.Node<K, V> node) {
        return node == null ? 0 : 1 + countNodes(node.left) + countNodes(node.right);
    }

    private static <K extends Comparable<K>, V> void assertSound(BinarySearchTree<K, V> tree) {
        assertBstProperty(tree.root, null, null);
        assertEquals(tree.size(), countNodes(tree.root), "노드 수가 size 와 다르다");
    }

    private BinarySearchTree<Integer, String> tree(int... keys) {
        BinarySearchTree<Integer, String> t = new BinarySearchTree<>();
        for (int k : keys) t.put(k, "v" + k);
        return t;
    }

    @Test
    @DisplayName("넣은 뒤 탐색 성질이 지켜진다")
    void keepsBstPropertyOnPut() {
        BinarySearchTree<Integer, String> t = tree(5, 3, 8, 2, 4, 7, 9);
        assertSound(t);
        assertEquals(5, t.root.key, "먼저 넣은 것이 뿌리다");
        assertEquals(3, t.root.left.key);
        assertEquals(8, t.root.right.key);
    }

    @Test
    @DisplayName("자식이 둘인 노드를 지워도 탐색 성질이 지켜진다")
    void keepsBstPropertyOnRemove() {
        // 계약 테스트는 정렬 결과만 본다. 여기서는 구조가 실제로 성한지 본다.
        BinarySearchTree<Integer, String> t = tree(50, 30, 70, 20, 40, 60, 80, 35, 45);
        t.remove(30);
        assertSound(t);
        t.remove(50);
        assertSound(t);
        t.remove(70);
        assertSound(t);
    }

    @Test
    @DisplayName("한쪽으로 치우친 트리에서도 정확하다")
    void correctWhenSkewed() {
        BinarySearchTree<Integer, String> t = tree(1, 2, 3, 4, 5);
        assertSound(t);
        assertNull(t.root.left, "정렬 순서로 넣으면 왼쪽이 비어 있다");
        assertEquals(5, t.height(), "다섯 개가 한 줄로 이어진다");
    }

    @Test
    @DisplayName("한계: 정렬된 순서로 넣으면 연결 리스트가 된다")
    void sortedInsertDegenerates() {
        // 이건 버그가 아니라 이 구조의 성질이다. 올바른 구현에서도 그대로 나온다.
        //
        // 균형 잡힌 트리라면 1000개의 높이가 10 근처여야 한다(log2(1000) = 약 10).
        // 그런데 정렬된 순서로 넣으면 오른쪽으로만 이어져 높이가 1000 이 된다.
        // 그러면 조회가 O(log n) 이 아니라 O(n) 이다. 트리라고 부를 이유가 없어진다.
        //
        // 16번 레드블랙 트리가 회전으로 이걸 고친다. 무엇을 고치는지 모르면 그 복잡함이 그냥 복잡하다.
        BinarySearchTree<Integer, String> sorted = new BinarySearchTree<>();
        for (int i = 0; i < 1_000; i++) sorted.put(i, "v" + i);

        assertEquals(1_000, sorted.height(),
            "정렬 입력에서는 높이가 원소 수와 같아진다. 사실상 연결 리스트다");

        // 흩어진 순서로 넣으면 훨씬 낮다
        BinarySearchTree<Integer, String> shuffled = new BinarySearchTree<>();
        for (int i = 0; i < 1_000; i++) shuffled.put((i * 617) % 1_000, "v" + i);

        assertTrue(shuffled.height() < 50,
            "흩어진 입력에서는 높이가 " + shuffled.height() + " 로 훨씬 낮다");
    }

    @Test
    @DisplayName("clear 는 뿌리만 끊으면 된다")
    void clearDetachesRoot() {
        BinarySearchTree<Integer, String> t = tree(5, 3, 8);
        t.clear();
        assertNull(t.root, "부모를 끊으면 아래가 통째로 GC 대상이 된다");
        assertEquals(0, t.size);
    }
}
