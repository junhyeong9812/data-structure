package com.datastructure.btree;

import java.util.ArrayList;
import java.util.List;

public class BPlusTree<K extends Comparable<K>, V> implements SearchTree<K, V> {

    static final class Node<K, V> {
        final boolean leaf;
        final List<K> keys = new ArrayList<>();
        final List<V> values = new ArrayList<>();
        final List<Node<K, V>> children = new ArrayList<>();
        Node<K, V> next;

        Node(boolean leaf) {
            this.leaf = leaf;
        }
    }

    private static final class Split<K, V> {
        final K key;
        final Node<K, V> right;

        Split(K key, Node<K, V> right) {
            this.key = key;
            this.right = right;
        }
    }

    private final int order;
    Node<K, V> root = new Node<>(true);
    private int size;

    /** order 는 노드 하나가 가질 수 있는 자식의 최대 수다. 키는 order-1 개까지. */
    public BPlusTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("차수는 3 이상이어야 한다: " + order);
        }
        this.order = order;
    }

    private int maxKeys() {
        return order - 1;
    }

    private int minKeys() {
        return maxKeys() / 2;
    }

    /** keys 에서 key 이상인 첫 자리. */
    static <K extends Comparable<K>, V> int lowerBound(Node<K, V> node, K key) {
        int lo = 0;
        int hi = node.keys.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (node.keys.get(mid).compareTo(key) < 0) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    /** 내부 노드에서 key 를 찾아 내려갈 자식의 인덱스. */
    static <K extends Comparable<K>, V> int childIndex(Node<K, V> node, K key) {
        int lo = 0;
        int hi = node.keys.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (node.keys.get(mid).compareTo(key) <= 0) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    Node<K, V> findLeaf(K key) {
        Node<K, V> cur = root;
        while (!cur.leaf) {
            cur = cur.children.get(childIndex(cur, key));
        }
        return cur;
    }

    @Override
    public V get(K key) {
        requireKey(key);
        Node<K, V> leaf = findLeaf(key);
        int i = lowerBound(leaf, key);
        if (i < leaf.keys.size() && leaf.keys.get(i).compareTo(key) == 0) {
            return leaf.values.get(i);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        Object[] old = new Object[1];
        Split<K, V> split = insert(root, key, value, old);
        if (split != null) {
            Node<K, V> newRoot = new Node<>(false);
            newRoot.keys.add(split.key);
            newRoot.children.add(root);
            newRoot.children.add(split.right);
            root = newRoot;
        }
        @SuppressWarnings("unchecked")
        V result = (V) old[0];
        return result;
    }

    private Split<K, V> insert(Node<K, V> node, K key, V value, Object[] old) {
        if (node.leaf) {
            int i = lowerBound(node, key);
            if (i < node.keys.size() && node.keys.get(i).compareTo(key) == 0) {
                old[0] = node.values.get(i);
                node.values.set(i, value);
                return null;
            }
            node.keys.add(i, key);
            node.values.add(i, value);
            size++;
            return node.keys.size() > maxKeys() ? splitLeaf(node) : null;
        }
        int i = childIndex(node, key);
        Split<K, V> s = insert(node.children.get(i), key, value, old);
        if (s == null) {
            return null;
        }
        node.keys.add(i, s.key);
        node.children.add(i + 1, s.right);
        return node.keys.size() > maxKeys() ? splitInternal(node) : null;
    }

    private Split<K, V> splitLeaf(Node<K, V> node) {
        int mid = node.keys.size() / 2;
        Node<K, V> right = new Node<>(true);
        right.keys.addAll(node.keys.subList(mid, node.keys.size()));
        right.values.addAll(node.values.subList(mid, node.values.size()));
        node.keys.subList(mid, node.keys.size()).clear();
        node.values.subList(mid, node.values.size()).clear();
        right.next = node.next;
        node.next = right;
        return new Split<>(right.keys.get(0), right);
    }

    private Split<K, V> splitInternal(Node<K, V> node) {
        int mid = node.keys.size() / 2;
        K up = node.keys.get(mid);
        Node<K, V> right = new Node<>(false);
        right.keys.addAll(node.keys.subList(mid + 1, node.keys.size()));
        right.children.addAll(node.children.subList(mid + 1, node.children.size()));
        node.keys.subList(mid, node.keys.size()).clear();
        node.children.subList(mid + 1, node.children.size()).clear();
        return new Split<>(up, right);
    }

    @Override
    public V remove(K key) {
        requireKey(key);
        V old = get(key);
        if (old == null) {
            return null;
        }
        removeFrom(root, key);
        if (!root.leaf && root.children.size() == 1) {
            root = root.children.get(0);
        }
        size--;
        return old;
    }

    private void removeFrom(Node<K, V> node, K key) {
        if (node.leaf) {
            int i = lowerBound(node, key);
            if (i < node.keys.size() && node.keys.get(i).compareTo(key) == 0) {
                node.keys.remove(i);
                node.values.remove(i);
            }
            return;
        }
        int i = childIndex(node, key);
        Node<K, V> child = node.children.get(i);
        removeFrom(child, key);
        if (child.keys.size() < minKeys()) {
            fix(node, i);
        }
        // 구분키를 여기서 더 고칠 필요는 없다.
        // 구분키 s 는 "왼쪽은 s 미만, 오른쪽은 s 이상"만 지키면 되고,
        // 지우기로 오른쪽의 최솟값이 s 보다 커져도 그 성질은 그대로다.
        // (빌려오기와 병합에서는 고쳐야 한다. 키가 실제로 경계를 넘어가기 때문이다)
    }

    private void fix(Node<K, V> node, int i) {
        if (i > 0 && node.children.get(i - 1).keys.size() > minKeys()) {
            borrowFromLeft(node, i);
            return;
        }
        if (i < node.children.size() - 1 && node.children.get(i + 1).keys.size() > minKeys()) {
            borrowFromRight(node, i);
            return;
        }
        if (i < node.children.size() - 1) {
            merge(node, i);
        } else {
            merge(node, i - 1);
        }
    }

    private void borrowFromLeft(Node<K, V> node, int i) {
        Node<K, V> child = node.children.get(i);
        Node<K, V> left = node.children.get(i - 1);
        if (child.leaf) {
            child.keys.add(0, left.keys.remove(left.keys.size() - 1));
            child.values.add(0, left.values.remove(left.values.size() - 1));
            node.keys.set(i - 1, child.keys.get(0));
        } else {
            child.keys.add(0, node.keys.get(i - 1));
            child.children.add(0, left.children.remove(left.children.size() - 1));
            node.keys.set(i - 1, left.keys.remove(left.keys.size() - 1));
        }
    }

    private void borrowFromRight(Node<K, V> node, int i) {
        Node<K, V> child = node.children.get(i);
        Node<K, V> right = node.children.get(i + 1);
        if (child.leaf) {
            child.keys.add(right.keys.remove(0));
            child.values.add(right.values.remove(0));
            node.keys.set(i, right.keys.get(0));
        } else {
            child.keys.add(node.keys.get(i));
            child.children.add(right.children.remove(0));
            node.keys.set(i, right.keys.remove(0));
        }
    }

    private void merge(Node<K, V> node, int i) {
        Node<K, V> left = node.children.get(i);
        Node<K, V> right = node.children.get(i + 1);
        if (left.leaf) {
            left.keys.addAll(right.keys);
            left.values.addAll(right.values);
            left.next = right.next;
            node.keys.remove(i);
        } else {
            left.keys.add(node.keys.remove(i));
            left.keys.addAll(right.keys);
            left.children.addAll(right.children);
        }
        node.children.remove(i + 1);
    }

    /** 가장 왼쪽 잎. 여기서부터 next 를 따라가면 전체가 정렬 순서로 나온다. */
    Node<K, V> firstLeaf() {
        Node<K, V> cur = root;
        while (!cur.leaf) {
            cur = cur.children.get(0);
        }
        return cur;
    }

    @Override
    public List<K> keys() {
        List<K> out = new ArrayList<>(size);
        for (Node<K, V> leaf = firstLeaf(); leaf != null; leaf = leaf.next) {
            out.addAll(leaf.keys);
        }
        return out;
    }

    /** from 이상 to 이하를 정렬 순서로. 잎 사슬을 따라 걷기만 하면 된다. */
    public List<K> keysInRange(K from, K to) {
        requireKey(from);
        requireKey(to);
        List<K> out = new ArrayList<>();
        if (from.compareTo(to) > 0) {
            return out;
        }
        Node<K, V> leaf = findLeaf(from);
        int i = lowerBound(leaf, from);
        while (leaf != null) {
            while (i < leaf.keys.size()) {
                K k = leaf.keys.get(i);
                if (k.compareTo(to) > 0) {
                    return out;
                }
                out.add(k);
                i++;
            }
            leaf = leaf.next;
            i = 0;
        }
        return out;
    }

    @Override
    public K firstKey() {
        Node<K, V> leaf = firstLeaf();
        return leaf.keys.isEmpty() ? null : leaf.keys.get(0);
    }

    @Override
    public K lastKey() {
        Node<K, V> cur = root;
        while (!cur.leaf) {
            cur = cur.children.get(cur.children.size() - 1);
        }
        return cur.keys.isEmpty() ? null : cur.keys.get(cur.keys.size() - 1);
    }

    @Override
    public int height() {
        int h = 1;
        Node<K, V> cur = root;
        while (!cur.leaf) {
            cur = cur.children.get(0);
            h++;
        }
        return h;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        root = new Node<>(true);
        size = 0;
    }

    int order() {
        return order;
    }

    private static void requireKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
    }
}
