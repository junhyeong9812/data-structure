package com.datastructure.persistent;

import java.util.ArrayList;
import java.util.List;

/**
 * [구현] 경로 복사로 만드는 불변 이진 탐색 트리.
 *
 * 참고: 이 폴더에 PersistentMap.java 가 없다. 인터페이스는 src/main 에서 온다.
 *
 * 06번과 탐색은 완전히 같다. 다른 것은 내려간 길을 되짚어 올라오며
 * 노드를 새로 만든다는 것뿐이다. 안 지나간 가지는 옛 트리의 노드를 그대로 가리킨다.
 */
public final class PersistentTreeMap<K extends Comparable<K>, V> implements PersistentMap<K, V> {

    static final class Node<K, V> {
        final K key;
        final V value;
        final Node<K, V> left;
        final Node<K, V> right;
        final int size;

        Node(K key, V value, Node<K, V> left, Node<K, V> right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
            this.size = 1 + sizeOf(left) + sizeOf(right);
        }
    }

    // 빈 맵은 상태가 없으므로 하나만 있으면 된다. 타입 인자는 형식이고 내용물이 없다.
    private static final PersistentTreeMap<?, ?> EMPTY = new PersistentTreeMap<Integer, Object>(null, 0);

    final Node<K, V> root;
    private final int nodesCreated;

    private PersistentTreeMap(Node<K, V> root, int nodesCreated) {
        this.root = root;
        this.nodesCreated = nodesCreated;
    }

    @SuppressWarnings("unchecked")
    public static <K extends Comparable<K>, V> PersistentTreeMap<K, V> empty() {
        return (PersistentTreeMap<K, V>) EMPTY;
    }

    private static int sizeOf(Node<?, ?> node) {
        return node == null ? 0 : node.size;
    }

    private static void requireKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
    }

    public int nodesCreatedByLastPut() {
        return nodesCreated;
    }

    @Override
    public int size() {
        return sizeOf(root);
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public V get(K key) {
        requireKey(key);
        Node<K, V> cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp < 0) {
                cur = cur.left;
            } else if (cmp > 0) {
                cur = cur.right;
            } else {
                return cur.value;
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public PersistentTreeMap<K, V> put(K key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        int[] created = new int[1];
        return new PersistentTreeMap<>(put(root, key, value, created), created[0]);
    }

    /**
     * 지나간 길목만 새로 만든다. 안 지나간 가지는 인자로 받은 참조를 그대로 넘긴다.
     *
     * new Node 를 부르는 자리가 셋인데 전부 created 를 하나 올린다.
     * 그 합이 이 put 이 쓴 메모리다.
     */
    private Node<K, V> put(Node<K, V> node, K key, V value, int[] created) {
        created[0]++;
        if (node == null) {
            return new Node<>(key, value, null, null);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return new Node<>(node.key, node.value, put(node.left, key, value, created), node.right);
        }
        if (cmp > 0) {
            return new Node<>(node.key, node.value, node.left, put(node.right, key, value, created));
        }
        return new Node<>(key, value, node.left, node.right);
    }

    @Override
    public PersistentTreeMap<K, V> remove(K key) {
        requireKey(key);
        int[] created = new int[1];
        Node<K, V> newRoot = remove(root, key, created);
        if (newRoot == root) {
            return this;      // 없는 키였다. 새 버전을 만들 이유가 없다
        }
        return new PersistentTreeMap<>(newRoot, created[0]);
    }

    /**
     * 06번의 삭제와 같은 세 경우인데, 값을 옮겨 심는 대신 새 노드를 만든다.
     *
     * 하위 호출이 같은 참조를 돌려주면 그 아래에서 아무것도 안 바뀐 것이므로
     * 이쪽도 새로 만들지 않고 자신을 그대로 돌려준다. 그러면 없는 키를 지울 때
     * 뿌리까지 참조가 그대로 올라와 맵 전체를 공유하게 된다.
     */
    private Node<K, V> remove(Node<K, V> node, K key, int[] created) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            Node<K, V> newLeft = remove(node.left, key, created);
            if (newLeft == node.left) {
                return node;
            }
            created[0]++;
            return new Node<>(node.key, node.value, newLeft, node.right);
        }
        if (cmp > 0) {
            Node<K, V> newRight = remove(node.right, key, created);
            if (newRight == node.right) {
                return node;
            }
            created[0]++;
            return new Node<>(node.key, node.value, node.left, newRight);
        }
        if (node.left == null) {
            return node.right;
        }
        if (node.right == null) {
            return node.left;
        }
        Node<K, V> successor = node.right;
        while (successor.left != null) {
            successor = successor.left;
        }
        Node<K, V> newRight = removeMin(node.right, created);
        created[0]++;
        return new Node<>(successor.key, successor.value, node.left, newRight);
    }

    /** 가장 작은 노드를 뺀 부분트리. 내려간 길목만 새로 만든다. */
    private Node<K, V> removeMin(Node<K, V> node, int[] created) {
        if (node.left == null) {
            return node.right;
        }
        created[0]++;
        return new Node<>(node.key, node.value, removeMin(node.left, created), node.right);
    }

    @Override
    public List<K> keys() {
        List<K> out = new ArrayList<>(size());
        inorder(root, out);
        return out;
    }

    private void inorder(Node<K, V> node, List<K> out) {
        if (node == null) {
            return;
        }
        inorder(node.left, out);
        out.add(node.key);
        inorder(node.right, out);
    }

    /** 가장 긴 뿌리-잎 경로의 길이. 비었으면 0. 균형을 안 잡으므로 이 값이 커질 수 있다. */
    public int height() {
        return height(root);
    }

    private int height(Node<K, V> node) {
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (K key : keys()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(key).append('=').append(get(key));
            first = false;
        }
        return sb.append('}').toString();
    }
}
