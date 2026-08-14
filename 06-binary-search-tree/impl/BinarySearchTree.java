package com.datastructure.bst;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * [구현] 이진 탐색 트리.
 *
 * 참고: 이 폴더에 SortedMap.java 가 없다. 인터페이스는 src/main 에서 온다.
 *
 * 노드마다 왼쪽에는 자기보다 작은 것, 오른쪽에는 큰 것만 온다.
 * 그 규칙 하나로 "어디를 볼지"가 매번 절반으로 줄어든다. 그게 O(log n) 의 정체다.
 *
 * 단, 트리가 균형 잡혀 있을 때만 그렇다.
 * 정렬된 데이터를 순서대로 넣으면 모든 노드가 오른쪽으로만 이어져 그냥 연결 리스트가 된다.
 * 그러면 O(log n) 이 O(n) 이 된다. 이 구조의 가장 큰 약점이고, 16번 레드블랙 트리가 그걸 고친다.
 *
 * 이 문제에서는 그 약점을 고치지 않고 눈으로 확인한다. 무엇이 문제인지 모르면
 * 레드블랙 트리의 복잡한 회전이 그냥 복잡하기만 하다.
 *
 * 참고: 필드 이름 root, size 와 Node 의 key, value, left, right 는 테스트가 직접 들여다본다.
 */
public class BinarySearchTree<K extends Comparable<K>, V> implements SortedMap<K, V> {

    static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    Node<K, V> root;
    int size;

    // ------------------------------------------------------------------
    // 채워져 있는 부분
    // ------------------------------------------------------------------

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(K key) {
        return findNode(key) != null;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    @Override
    public void clear() {
        root = null;      // 트리는 부모만 끊으면 아래가 통째로 GC 대상이 된다
        size = 0;
    }

    /**
     * 트리의 높이. 비었으면 0, 노드 하나면 1.
     *
     * 균형이 깨졌는지 눈으로 보려고 열어둔다.
     * n 개를 넣었을 때 균형 잡힌 트리는 높이가 log2(n) 근처, 치우친 트리는 n 이다.
     */
    public int height() {
        return heightOf(root);
    }

    private int heightOf(Node<K, V> node) {
        if (node == null) return 0;
        return 1 + Math.max(heightOf(node.left), heightOf(node.right));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (K key : keys()) {
            if (!first) sb.append(", ");
            sb.append(key).append('=').append(get(key));
            first = false;
        }
        return sb.append('}').toString();
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 키에 해당하는 노드를 찾는다. 없으면 null.
     *
     * 생각할 것
     *   - 비교 결과가 음수면 어디로, 양수면 어디로 가야 하는가?
     *   - 재귀와 반복 중 어느 쪽이든 된다. 반복이 스택을 안 쓴다.
     *
     */
    Node<K, V> findNode(K key) {
        if (key == null) {
            return null;
        }
        Node<K, V> cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp < 0) cur = cur.left;
            else if (cmp > 0) cur = cur.right;
            else return cur;
        }
        return null;
    }

    /**
     * 키에 값을 넣고 이전 값을 반환한다.
     *
     * 생각할 것
     *   - 이미 있는 키면 값만 바꾼다. 새 노드를 달면 같은 키가 두 개가 된다.
     *   - 새 노드는 항상 잎(leaf)으로 붙는다. 붙일 자리를 찾으려면 그 부모를 알아야 한다.
     *
     */
    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("null 키는 담을 수 없다");
        }
        if (root == null) {
            root = new Node<>(key, value);
            size++;
            return null;
        }
        Node<K, V> parent = null;
        Node<K, V> cur = root;
        int cmp = 0;
        while (cur != null) {
            parent = cur;
            cmp = key.compareTo(cur.key);
            if (cmp < 0) cur = cur.left;
            else if (cmp > 0) cur = cur.right;
            else {
                V old = cur.value;
                cur.value = value;      // 새 노드를 달면 같은 키가 두 개가 된다
                return old;
            }
        }
        Node<K, V> node = new Node<>(key, value);
        if (cmp < 0) parent.left = node; else parent.right = node;
        size++;
        return null;
    }

    /**
     * 키를 지우고 그 값을 반환한다. 없었으면 null.
     *
     * 이 문제에서 가장 어려운 부분이다. 세 가지 경우가 있다.
     *   1. 자식이 없다  -> 그냥 떼면 된다
     *   2. 자식이 하나  -> 그 자식을 자기 자리에 올린다
     *   3. 자식이 둘    -> 누구를 올려야 순서가 유지되는가?
     *
     * 생각할 것
     *   - 3번에서 아무 자식이나 올리면 "왼쪽은 작고 오른쪽은 크다"가 깨진다.
     *     지울 노드를 대신할 수 있는 값은 딱 둘뿐이다. 그게 무엇인가?
     *   - 그 값을 가져오고 나면 원래 있던 자리는 어떻게 되는가? 그 노드는 자식이 몇 개인가?
     *
     */
    @Override
    public V remove(K key) {
        if (key == null) {
            return null;
        }
        Node<K, V> parent = null;
        Node<K, V> cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp < 0) { parent = cur; cur = cur.left; }
            else if (cmp > 0) { parent = cur; cur = cur.right; }
            else break;
        }
        if (cur == null) {
            return null;
        }
        V old = cur.value;

        // 자식이 둘이면 아무 자식이나 올릴 수 없다. 순서가 깨진다.
        // 대신할 수 있는 값은 딱 둘이다. 왼쪽에서 가장 큰 것(선행자) 또는 오른쪽에서 가장 작은 것(후속자).
        // 여기서는 후속자를 쓴다. 후속자는 정의상 왼쪽 자식이 없으므로,
        // 값을 옮기고 나면 "자식이 하나 이하인 노드를 지우는" 쉬운 문제로 바뀐다.
        if (cur.left != null && cur.right != null) {
            Node<K, V> successorParent = cur;
            Node<K, V> successor = cur.right;
            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }
            cur.key = successor.key;
            cur.value = successor.value;
            parent = successorParent;
            cur = successor;            // 이제 지울 대상은 후속자다
        }

        Node<K, V> child = (cur.left != null) ? cur.left : cur.right;
        if (parent == null) root = child;
        else if (parent.left == cur) parent.left = child;
        else parent.right = child;

        cur.left = null;
        cur.right = null;
        size--;
        return old;
    }

    /**
     * 가장 작은 키. 비었으면 NoSuchElementException.
     *
     */
    @Override
    public K firstKey() {
        if (root == null) {
            throw new NoSuchElementException("비어 있다");
        }
        Node<K, V> cur = root;
        while (cur.left != null) cur = cur.left;   // 왼쪽 끝이 가장 작다
        return cur.key;
    }

    /** TODO(05): 구현하라. firstKey 의 대칭이다. */
    @Override
    public K lastKey() {
        if (root == null) {
            throw new NoSuchElementException("비어 있다");
        }
        Node<K, V> cur = root;
        while (cur.right != null) cur = cur.right;
        return cur.key;
    }

    /**
     * key 이하인 키 중 가장 큰 것. 없으면 null.
     *
     * 생각할 것
     *   - 지금 노드가 key 보다 크면 답은 반드시 왼쪽에 있다.
     *   - 지금 노드가 key 이하면 이 노드가 답일 수도 있다. 그런데 오른쪽에 더 나은 답이 있을 수도 있다.
     *     그럼 어떻게 해야 하는가?
     *
     */
    @Override
    public K floorKey(K key) {
        if (key == null) {
            return null;
        }
        K best = null;
        Node<K, V> cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) return cur.key;
            if (cmp < 0) {
                cur = cur.left;          // 이 노드는 너무 크다. 답은 왼쪽에만 있다
            } else {
                best = cur.key;          // 후보로 기억하되, 오른쪽에 더 가까운 것이 있을 수 있다
                cur = cur.right;
            }
        }
        return best;
    }

    /** TODO(07): 구현하라. floorKey 의 대칭이다. */
    @Override
    public K ceilingKey(K key) {
        if (key == null) {
            return null;
        }
        K best = null;
        Node<K, V> cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) return cur.key;
            if (cmp > 0) {
                cur = cur.right;
            } else {
                best = cur.key;
                cur = cur.left;
            }
        }
        return best;
    }

    /**
     * 오름차순 전체 키.
     *
     * 생각할 것
     *   - 왼쪽을 다 보고, 자기를 보고, 오른쪽을 보면 정렬된 순서가 나온다. 왜 그런가?
     *   - 이 순회를 중위 순회(in-order)라고 부른다.
     *
     */
    @Override
    public Iterable<K> keys() {
        java.util.List<K> out = new ArrayList<>(size);
        inOrder(root, out);
        return out;
    }

    /**
     * 왼쪽 -> 자기 -> 오른쪽 순서로 본다.
     *
     * 왼쪽에는 자기보다 작은 것만, 오른쪽에는 큰 것만 있으므로
     * 이 순서로 모으면 정렬된 결과가 나온다. 그게 탐색 성질의 직접적인 귀결이다.
     */
    private void inOrder(Node<K, V> node, java.util.List<K> out) {
        if (node == null) return;
        inOrder(node.left, out);
        out.add(node.key);
        inOrder(node.right, out);
    }

    /**
     * from 이상 to 이하인 키를 오름차순으로. 범위가 뒤집혀 있으면 빈 결과.
     *
     * 생각할 것
     *   - 전부 훑고 걸러내면 O(n) 이다. 트리 구조를 쓰면 볼 필요 없는 가지를 통째로 건너뛸 수 있다.
     *   - 지금 노드가 from 보다 작으면 그 왼쪽 가지는 전부 from 보다 작다. 볼 이유가 있는가?
     *
     */
    @Override
    public Iterable<K> keysInRange(K from, K to) {
        java.util.List<K> out = new ArrayList<>();
        if (from == null || to == null || from.compareTo(to) > 0) {
            return out;
        }
        collectRange(root, from, to, out);
        return out;
    }

    /**
     * 볼 필요 없는 가지를 통째로 건너뛴다.
     *
     * 지금 노드가 from 이하면 그 왼쪽은 전부 from 미만이므로 볼 이유가 없다.
     * 이 가지치기가 있어야 O(log n + k) 가 된다. 없으면 그냥 전체 순회라 O(n) 이다.
     */
    private void collectRange(Node<K, V> node, K from, K to, java.util.List<K> out) {
        if (node == null) return;
        int cmpFrom = node.key.compareTo(from);
        int cmpTo = node.key.compareTo(to);

        if (cmpFrom > 0) collectRange(node.left, from, to, out);
        if (cmpFrom >= 0 && cmpTo <= 0) out.add(node.key);
        if (cmpTo < 0) collectRange(node.right, from, to, out);
    }
}
