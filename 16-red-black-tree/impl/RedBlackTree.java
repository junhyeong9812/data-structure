package com.datastructure.redblack;

import java.util.ArrayList;
import java.util.List;

public class RedBlackTree<K extends Comparable<K>, V> {

    static final boolean RED = true;
    static final boolean BLACK = false;

    static final class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;
        boolean color;

        Node(K key, V value, boolean color) {
            this.key = key;
            this.value = value;
            this.color = color;
        }
    }

    Node<K, V> root;
    private int size;

    static <K, V> boolean isRed(Node<K, V> node) {
        return node != null && node.color == RED;
    }

    private Node<K, V> rotateLeft(Node<K, V> h) {
        Node<K, V> x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    private Node<K, V> rotateRight(Node<K, V> h) {
        Node<K, V> x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    private void flipColors(Node<K, V> h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    private Node<K, V> balance(Node<K, V> h) {
        if (isRed(h.right) && !isRed(h.left)) {
            h = rotateLeft(h);
        }
        if (isRed(h.left) && isRed(h.left.left)) {
            h = rotateRight(h);
        }
        if (isRed(h.left) && isRed(h.right)) {
            flipColors(h);
        }
        return h;
    }

    public V put(K key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        Object[] old = new Object[1];
        root = put(root, key, value, old);
        root.color = BLACK;
        @SuppressWarnings("unchecked")
        V result = (V) old[0];
        return result;
    }

    private Node<K, V> put(Node<K, V> h, K key, V value, Object[] old) {
        if (h == null) {
            size++;
            return new Node<>(key, value, RED);
        }
        int cmp = key.compareTo(h.key);
        if (cmp < 0) {
            h.left = put(h.left, key, value, old);
        } else if (cmp > 0) {
            h.right = put(h.right, key, value, old);
        } else {
            old[0] = h.value;
            h.value = value;
        }
        return balance(h);
    }

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

    private Node<K, V> moveRedLeft(Node<K, V> h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }

    private Node<K, V> moveRedRight(Node<K, V> h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }

    private Node<K, V> deleteMin(Node<K, V> h) {
        if (h.left == null) {
            return null;
        }
        if (!isRed(h.left) && !isRed(h.left.left)) {
            h = moveRedLeft(h);
        }
        h.left = deleteMin(h.left);
        return balance(h);
    }

    public V remove(K key) {
        requireKey(key);
        V old = get(key);
        if (old == null) {
            return null;
        }
        // 표준 구현의 관례. 지워도 테스트가 전부 통과한다(README 참고).
        if (!isRed(root.left) && !isRed(root.right)) {
            root.color = RED;
        }
        root = delete(root, key);
        if (root != null) {
            root.color = BLACK;
        }
        size--;
        return old;
    }

    private Node<K, V> delete(Node<K, V> h, K key) {
        if (key.compareTo(h.key) < 0) {
            if (!isRed(h.left) && !isRed(h.left.left)) {
                h = moveRedLeft(h);
            }
            h.left = delete(h.left, key);
        } else {
            if (isRed(h.left)) {
                h = rotateRight(h);
            }
            if (key.compareTo(h.key) == 0 && h.right == null) {
                return null;
            }
            if (!isRed(h.right) && !isRed(h.right.left)) {
                h = moveRedRight(h);
            }
            if (key.compareTo(h.key) == 0) {
                Node<K, V> x = min(h.right);
                h.key = x.key;
                h.value = x.value;
                h.right = deleteMin(h.right);
            } else {
                h.right = delete(h.right, key);
            }
        }
        return balance(h);
    }

    private Node<K, V> min(Node<K, V> h) {
        while (h.left != null) {
            h = h.left;
        }
        return h;
    }

    public K floorKey(K key) {
        requireKey(key);
        Node<K, V> cur = root;
        K best = null;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) {
                return cur.key;
            }
            if (cmp < 0) {
                cur = cur.left;
            } else {
                best = cur.key;
                cur = cur.right;
            }
        }
        return best;
    }

    public K ceilingKey(K key) {
        requireKey(key);
        Node<K, V> cur = root;
        K best = null;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) {
                return cur.key;
            }
            if (cmp > 0) {
                cur = cur.right;
            } else {
                best = cur.key;
                cur = cur.left;
            }
        }
        return best;
    }

    public List<K> keys() {
        List<K> out = new ArrayList<>(size);
        inorder(root, out);
        return out;
    }

    private void inorder(Node<K, V> h, List<K> out) {
        if (h == null) {
            return;
        }
        inorder(h.left, out);
        out.add(h.key);
        inorder(h.right, out);
    }

    public K firstKey() {
        return root == null ? null : min(root).key;
    }

    public K lastKey() {
        if (root == null) {
            return null;
        }
        Node<K, V> cur = root;
        while (cur.right != null) {
            cur = cur.right;
        }
        return cur.key;
    }

    public int height() {
        return height(root);
    }

    private int height(Node<K, V> h) {
        return h == null ? 0 : 1 + Math.max(height(h.left), height(h.right));
    }

    /** 뿌리에서 잎까지 지나는 검은 링크의 수. 모든 경로에서 같아야 한다. */
    public int blackHeight() {
        int bh = 0;
        Node<K, V> cur = root;
        while (cur != null) {
            if (!isRed(cur)) {
                bh++;
            }
            cur = cur.left;
        }
        return bh;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    private static void requireKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
    }
}
