package com.datastructure.splay;

import java.util.ArrayList;
import java.util.List;

public class SplayTree<K extends Comparable<K>, V> {

    static final class Node<K, V> {
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
    private int size;
    private long rotations;

    private Node<K, V> rotateRight(Node<K, V> h) {
        rotations++;
        Node<K, V> x = h.left;
        h.left = x.right;
        x.right = h;
        return x;
    }

    private Node<K, V> rotateLeft(Node<K, V> h) {
        rotations++;
        Node<K, V> x = h.right;
        h.right = x.left;
        x.left = h;
        return x;
    }

    private Node<K, V> splay(Node<K, V> h, K key) {
        if (h == null) {
            return null;
        }
        int cmp = key.compareTo(h.key);
        if (cmp < 0) {
            if (h.left == null) {
                return h;
            }
            int cmpLeft = key.compareTo(h.left.key);
            if (cmpLeft < 0) {
                // zig-zig. 할아버지를 먼저 돈다.
                h.left.left = splay(h.left.left, key);
                h = rotateRight(h);
            } else if (cmpLeft > 0) {
                // zig-zag. 부모를 먼저 돈다.
                h.left.right = splay(h.left.right, key);
                if (h.left.right != null) {
                    h.left = rotateLeft(h.left);
                }
            }
            if (h.left == null) {
                return h;
            }
            return rotateRight(h);
        } else if (cmp > 0) {
            if (h.right == null) {
                return h;
            }
            int cmpRight = key.compareTo(h.right.key);
            if (cmpRight > 0) {
                // zig-zig. 할아버지를 먼저 돈다.
                h.right.right = splay(h.right.right, key);
                h = rotateLeft(h);
            } else if (cmpRight < 0) {
                // zig-zag. 부모를 먼저 돈다.
                h.right.left = splay(h.right.left, key);
                if (h.right.left != null) {
                    h.right = rotateRight(h.right);
                }
            }
            if (h.right == null) {
                return h;
            }
            return rotateLeft(h);
        } else {
            return h;
        }
    }

    public V put(K key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        if (root == null) {
            root = new Node<>(key, value);
            size++;
            return null;
        }
        root = splay(root, key);
        int cmp = key.compareTo(root.key);
        if (cmp == 0) {
            V old = root.value;
            root.value = value;
            return old;
        }
        Node<K, V> fresh = new Node<>(key, value);
        if (cmp < 0) {
            fresh.left = root.left;
            fresh.right = root;
            root.left = null;
        } else {
            fresh.right = root.right;
            fresh.left = root;
            root.right = null;
        }
        root = fresh;
        size++;
        return null;
    }

    public V get(K key) {
        requireKey(key);
        if (root == null) {
            return null;
        }
        root = splay(root, key);
        return key.compareTo(root.key) == 0 ? root.value : null;
    }

    public V remove(K key) {
        requireKey(key);
        if (root == null) {
            return null;
        }
        root = splay(root, key);
        if (key.compareTo(root.key) != 0) {
            return null;
        }
        V old = root.value;
        Node<K, V> left = root.left;
        Node<K, V> right = root.right;
        if (left == null) {
            root = right;
        } else {
            left = splay(left, key);
            left.right = right;
            root = left;
        }
        size--;
        return old;
    }

    public K floorKey(K key) {
        requireKey(key);
        if (root == null) {
            return null;
        }
        root = splay(root, key);
        if (root.key.compareTo(key) <= 0) {
            return root.key;
        }
        Node<K, V> h = root.left;
        if (h == null) {
            return null;
        }
        while (h.right != null) {
            h = h.right;
        }
        return h.key;
    }

    public K ceilingKey(K key) {
        requireKey(key);
        if (root == null) {
            return null;
        }
        root = splay(root, key);
        if (root.key.compareTo(key) >= 0) {
            return root.key;
        }
        Node<K, V> h = root.right;
        if (h == null) {
            return null;
        }
        while (h.left != null) {
            h = h.left;
        }
        return h.key;
    }

    /** key 가 지금 몇 층에 있는지. 뿌리가 0, 없으면 -1. 이것만은 트리를 건드리지 않는다. */
    public int depthOf(K key) {
        requireKey(key);
        Node<K, V> cur = root;
        int depth = 0;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp < 0) {
                cur = cur.left;
            } else if (cmp > 0) {
                cur = cur.right;
            } else {
                return depth;
            }
            depth++;
        }
        return -1;
    }

    /** 태어난 뒤 지금까지의 누적 회전 수. 상환 비용을 재는 자다. */
    public long rotations() {
        return rotations;
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
        if (root == null) {
            return null;
        }
        Node<K, V> cur = root;
        while (cur.left != null) {
            cur = cur.left;
        }
        return cur.key;
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
