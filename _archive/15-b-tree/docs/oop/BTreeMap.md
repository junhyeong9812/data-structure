# oop/BTreeMap.java

제네릭 Key-Value B-Tree. `Comparable<K>` 키. 단순화를 위해 insert/search/traverse 중심으로 구현(삭제는 pop의 BTree 알고리즘과 동일 구조).

```java
package com.datastructure.btree.oop;

import java.util.*;

public class BTreeMap<K extends Comparable<K>, V> {
    private class Node {
        int n;
        final Object[] keys;
        final Object[] values;
        final Object[] children;
        boolean leaf;

        Node(boolean leaf) {
            this.keys = new Object[2 * t - 1];
            this.values = new Object[2 * t - 1];
            this.children = new Object[2 * t];
            this.leaf = leaf;
        }
    }

    private final int t;
    private Node root;
    private int size;

    public BTreeMap(int t) {
        if (t < 2) throw new IllegalArgumentException("t >= 2");
        this.t = t;
        this.root = new Node(true);
    }

    @SuppressWarnings("unchecked")
    private K key(Node n, int i) {
        return (K) n.keys[i];
    }

    @SuppressWarnings("unchecked")
    private V value(Node n, int i) {
        return (V) n.values[i];
    }

    @SuppressWarnings("unchecked")
    private Node child(Node n, int i) {
        return (Node) n.children[i];
    }

    public V get(K key) {
        Objects.requireNonNull(key);
        return get(root, key);
    }

    private V get(Node x, K key) {
        int i = 0;
        while (i < x.n && key.compareTo(key(x, i)) > 0) i++;
        if (i < x.n && key.compareTo(key(x, i)) == 0) return value(x, i);
        if (x.leaf) return null;
        return get(child(x, i), key);
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V put(K key, V val) {
        Objects.requireNonNull(key);
        V existing = get(key);
        if (existing != null) {
            updateValue(root, key, val);
            return existing;
        }

        Node r = root;
        if (r.n == 2 * t - 1) {
            Node s = new Node(false);
            s.children[0] = r;
            splitChild(s, 0);
            root = s;
            insertNonFull(s, key, val);
        } else {
            insertNonFull(r, key, val);
        }
        size++;
        return null;
    }

    private boolean updateValue(Node x, K key, V val) {
        int i = 0;
        while (i < x.n && key.compareTo(key(x, i)) > 0) i++;
        if (i < x.n && key.compareTo(key(x, i)) == 0) {
            x.values[i] = val;
            return true;
        }
        if (x.leaf) return false;
        return updateValue(child(x, i), key, val);
    }

    private void insertNonFull(Node x, K key, V val) {
        int i = x.n - 1;
        if (x.leaf) {
            while (i >= 0 && key.compareTo(key(x, i)) < 0) {
                x.keys[i + 1] = x.keys[i];
                x.values[i + 1] = x.values[i];
                i--;
            }
            x.keys[i + 1] = key;
            x.values[i + 1] = val;
            x.n++;
        } else {
            while (i >= 0 && key.compareTo(key(x, i)) < 0) i--;
            i++;
            if (child(x, i).n == 2 * t - 1) {
                splitChild(x, i);
                if (key.compareTo(key(x, i)) > 0) i++;
            }
            insertNonFull(child(x, i), key, val);
        }
    }

    private void splitChild(Node x, int i) {
        Node y = child(x, i);
        Node z = new Node(y.leaf);
        z.n = t - 1;
        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = y.keys[j + t];
            z.values[j] = y.values[j + t];
        }
        if (!y.leaf) {
            for (int j = 0; j < t; j++) z.children[j] = y.children[j + t];
        }
        y.n = t - 1;
        for (int j = x.n; j >= i + 1; j--) x.children[j + 1] = x.children[j];
        x.children[i + 1] = z;
        for (int j = x.n - 1; j >= i; j--) {
            x.keys[j + 1] = x.keys[j];
            x.values[j + 1] = x.values[j];
        }
        x.keys[i] = y.keys[t - 1];
        x.values[i] = y.values[t - 1];
        x.n++;
    }

    public List<Map.Entry<K, V>> entries() {
        List<Map.Entry<K, V>> result = new ArrayList<>();
        traverse(root, result);
        return result;
    }

    private void traverse(Node x, List<Map.Entry<K, V>> result) {
        int i;
        for (i = 0; i < x.n; i++) {
            if (!x.leaf) traverse(child(x, i), result);
            result.add(Map.entry(key(x, i), value(x, i)));
        }
        if (!x.leaf) traverse(child(x, i), result);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
```
