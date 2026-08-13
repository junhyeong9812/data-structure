package com.datastructure.btree;

import java.util.ArrayList;
import java.util.List;

public class BTree<K extends Comparable<K>, V> implements SearchTree<K, V> {

    static final class Node<K, V> {
        final List<K> keys = new ArrayList<>();
        final List<V> values = new ArrayList<>();
        final List<Node<K, V>> children = new ArrayList<>();

        boolean leaf() {
            return children.isEmpty();
        }
    }

    private final int minDegree;
    Node<K, V> root = new Node<>();
    private int size;

    /** minDegree(t) 가 2 면 2-3-4 트리다. 노드는 키를 t-1 개 이상 2t-1 개 이하로 갖는다. */
    public BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("최소 차수는 2 이상이어야 한다: " + minDegree);
        }
        this.minDegree = minDegree;
    }

    private int maxKeys() {
        return 2 * minDegree - 1;
    }

    /** node.keys 에서 key 가 들어갈 자리. 같은 키가 있으면 그 자리. */
    int lowerBound(Node<K, V> node, K key) {
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

    @Override
    public V get(K key) {
        requireKey(key);
        Node<K, V> cur = root;
        while (true) {
            int i = lowerBound(cur, key);
            if (i < cur.keys.size() && cur.keys.get(i).compareTo(key) == 0) {
                return cur.values.get(i);
            }
            if (cur.leaf()) {
                return null;
            }
            cur = cur.children.get(i);
        }
    }

    @Override
    public V put(K key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        if (root.keys.size() == maxKeys()) {
            Node<K, V> newRoot = new Node<>();
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        return insertNonFull(root, key, value);
    }

    /** parent.children[i] 가 꽉 찼을 때 반으로 쪼개고 가운데 키를 부모로 올린다. */
    private void splitChild(Node<K, V> parent, int i) {
        Node<K, V> full = parent.children.get(i);
        int t = minDegree;
        Node<K, V> right = new Node<>();

        K midKey = full.keys.get(t - 1);
        V midValue = full.values.get(t - 1);

        for (int j = t; j < full.keys.size(); j++) {
            right.keys.add(full.keys.get(j));
            right.values.add(full.values.get(j));
        }
        if (!full.leaf()) {
            for (int j = t; j < full.children.size(); j++) {
                right.children.add(full.children.get(j));
            }
            full.children.subList(t, full.children.size()).clear();
        }
        full.keys.subList(t - 1, full.keys.size()).clear();
        full.values.subList(t - 1, full.values.size()).clear();

        parent.keys.add(i, midKey);
        parent.values.add(i, midValue);
        parent.children.add(i + 1, right);
    }

    private V insertNonFull(Node<K, V> node, K key, V value) {
        int i = lowerBound(node, key);
        if (i < node.keys.size() && node.keys.get(i).compareTo(key) == 0) {
            V old = node.values.get(i);
            node.values.set(i, value);
            return old;
        }
        if (node.leaf()) {
            node.keys.add(i, key);
            node.values.add(i, value);
            size++;
            return null;
        }
        if (node.children.get(i).keys.size() == maxKeys()) {
            splitChild(node, i);
            int cmp = node.keys.get(i).compareTo(key);
            if (cmp == 0) {
                V old = node.values.get(i);
                node.values.set(i, value);
                return old;
            }
            if (cmp < 0) {
                i++;
            }
        }
        return insertNonFull(node.children.get(i), key, value);
    }

    @Override
    public V remove(K key) {
        requireKey(key);
        if (get(key) == null) {
            return null;
        }
        V old = get(key);
        removeFrom(root, key);
        if (root.keys.isEmpty() && !root.leaf()) {
            root = root.children.get(0);
        }
        size--;
        return old;
    }

    private void removeFrom(Node<K, V> node, K key) {
        int i = lowerBound(node, key);
        boolean here = i < node.keys.size() && node.keys.get(i).compareTo(key) == 0;

        if (here && node.leaf()) {
            node.keys.remove(i);
            node.values.remove(i);
            return;
        }
        if (here) {
            Node<K, V> left = node.children.get(i);
            Node<K, V> right = node.children.get(i + 1);
            if (left.keys.size() >= minDegree) {
                Node<K, V> pred = left;
                while (!pred.leaf()) {
                    pred = pred.children.get(pred.children.size() - 1);
                }
                K pk = pred.keys.get(pred.keys.size() - 1);
                V pv = pred.values.get(pred.values.size() - 1);
                node.keys.set(i, pk);
                node.values.set(i, pv);
                removeFrom(left, pk);
            } else if (right.keys.size() >= minDegree) {
                Node<K, V> succ = right;
                while (!succ.leaf()) {
                    succ = succ.children.get(0);
                }
                K sk = succ.keys.get(0);
                V sv = succ.values.get(0);
                node.keys.set(i, sk);
                node.values.set(i, sv);
                removeFrom(right, sk);
            } else {
                mergeChildren(node, i);
                removeFrom(left, key);
            }
            return;
        }
        if (node.leaf()) {
            return;
        }
        int childIndex = i;
        if (node.children.get(childIndex).keys.size() < minDegree) {
            childIndex = fill(node, childIndex);
        }
        removeFrom(node.children.get(childIndex), key);
    }

    /** children[i] 의 키가 모자라면 형제에게 빌리거나 병합한다. 내려갈 자식 인덱스를 준다. */
    private int fill(Node<K, V> node, int i) {
        if (i > 0 && node.children.get(i - 1).keys.size() >= minDegree) {
            borrowFromLeft(node, i);
            return i;
        }
        if (i < node.children.size() - 1 && node.children.get(i + 1).keys.size() >= minDegree) {
            borrowFromRight(node, i);
            return i;
        }
        if (i < node.children.size() - 1) {
            mergeChildren(node, i);
            return i;
        }
        mergeChildren(node, i - 1);
        return i - 1;
    }

    private void borrowFromLeft(Node<K, V> node, int i) {
        Node<K, V> child = node.children.get(i);
        Node<K, V> left = node.children.get(i - 1);

        child.keys.add(0, node.keys.get(i - 1));
        child.values.add(0, node.values.get(i - 1));
        node.keys.set(i - 1, left.keys.remove(left.keys.size() - 1));
        node.values.set(i - 1, left.values.remove(left.values.size() - 1));
        if (!left.leaf()) {
            child.children.add(0, left.children.remove(left.children.size() - 1));
        }
    }

    private void borrowFromRight(Node<K, V> node, int i) {
        Node<K, V> child = node.children.get(i);
        Node<K, V> right = node.children.get(i + 1);

        child.keys.add(node.keys.get(i));
        child.values.add(node.values.get(i));
        node.keys.set(i, right.keys.remove(0));
        node.values.set(i, right.values.remove(0));
        if (!right.leaf()) {
            child.children.add(right.children.remove(0));
        }
    }

    /** children[i] 와 children[i+1] 을 keys[i] 를 가운데 끼워 하나로 합친다. */
    private void mergeChildren(Node<K, V> node, int i) {
        Node<K, V> left = node.children.get(i);
        Node<K, V> right = node.children.get(i + 1);

        left.keys.add(node.keys.remove(i));
        left.values.add(node.values.remove(i));
        left.keys.addAll(right.keys);
        left.values.addAll(right.values);
        left.children.addAll(right.children);
        node.children.remove(i + 1);
    }

    @Override
    public List<K> keys() {
        List<K> out = new ArrayList<>(size);
        collect(root, out);
        return out;
    }

    private void collect(Node<K, V> node, List<K> out) {
        if (node.leaf()) {
            out.addAll(node.keys);
            return;
        }
        for (int i = 0; i < node.keys.size(); i++) {
            collect(node.children.get(i), out);
            out.add(node.keys.get(i));
        }
        collect(node.children.get(node.children.size() - 1), out);
    }

    @Override
    public K firstKey() {
        if (size == 0) {
            return null;
        }
        Node<K, V> cur = root;
        while (!cur.leaf()) {
            cur = cur.children.get(0);
        }
        return cur.keys.get(0);
    }

    @Override
    public K lastKey() {
        if (size == 0) {
            return null;
        }
        Node<K, V> cur = root;
        while (!cur.leaf()) {
            cur = cur.children.get(cur.children.size() - 1);
        }
        return cur.keys.get(cur.keys.size() - 1);
    }

    @Override
    public int height() {
        int h = 1;
        Node<K, V> cur = root;
        while (!cur.leaf()) {
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
        root = new Node<>();
        size = 0;
    }

    int minDegree() {
        return minDegree;
    }

    private static void requireKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
    }
}
