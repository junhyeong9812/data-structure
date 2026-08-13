# pop/BTree.java

int 키 B-Tree. 차수 t. insert/search/delete/traverse. CLRS 스타일 구현.

```java
package com.datastructure.btree.pop;

import java.util.ArrayList;
import java.util.List;

public class BTree {
    static class Node {
        int n;
        final int[] keys;
        final Node[] children;
        boolean leaf;

        Node(int t, boolean leaf) {
            this.keys = new int[2 * t - 1];
            this.children = new Node[2 * t];
            this.leaf = leaf;
            this.n = 0;
        }
    }

    private final int t;       // minimum degree
    private Node root;
    private int size;

    public BTree(int t) {
        if (t < 2) throw new IllegalArgumentException("t >= 2");
        this.t = t;
        this.root = new Node(t, true);
        this.size = 0;
    }

    public boolean search(int key) {
        return search(root, key) != null;
    }

    private Node search(Node x, int key) {
        int i = 0;
        while (i < x.n && key > x.keys[i]) i++;
        if (i < x.n && key == x.keys[i]) return x;
        if (x.leaf) return null;
        return search(x.children[i], key);
    }

    public void insert(int key) {
        if (search(key)) return; // 중복 무시
        Node r = root;
        if (r.n == 2 * t - 1) {
            Node s = new Node(t, false);
            s.children[0] = r;
            splitChild(s, 0);
            root = s;
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
        size++;
    }

    private void insertNonFull(Node x, int key) {
        int i = x.n - 1;
        if (x.leaf) {
            while (i >= 0 && key < x.keys[i]) {
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            x.keys[i + 1] = key;
            x.n++;
        } else {
            while (i >= 0 && key < x.keys[i]) i--;
            i++;
            if (x.children[i].n == 2 * t - 1) {
                splitChild(x, i);
                if (key > x.keys[i]) i++;
            }
            insertNonFull(x.children[i], key);
        }
    }

    private void splitChild(Node x, int i) {
        Node y = x.children[i];
        Node z = new Node(t, y.leaf);
        z.n = t - 1;
        for (int j = 0; j < t - 1; j++) z.keys[j] = y.keys[j + t];
        if (!y.leaf) {
            for (int j = 0; j < t; j++) z.children[j] = y.children[j + t];
        }
        y.n = t - 1;
        for (int j = x.n; j >= i + 1; j--) x.children[j + 1] = x.children[j];
        x.children[i + 1] = z;
        for (int j = x.n - 1; j >= i; j--) x.keys[j + 1] = x.keys[j];
        x.keys[i] = y.keys[t - 1];
        x.n++;
    }

    public boolean delete(int key) {
        if (!search(key)) return false;
        delete(root, key);
        if (root.n == 0 && !root.leaf) root = root.children[0];
        size--;
        return true;
    }

    private void delete(Node x, int key) {
        int idx = 0;
        while (idx < x.n && x.keys[idx] < key) idx++;

        if (idx < x.n && x.keys[idx] == key) {
            if (x.leaf) removeFromLeaf(x, idx);
            else removeFromInternal(x, idx);
        } else {
            if (x.leaf) return;
            boolean isLast = (idx == x.n);
            if (x.children[idx].n < t) fillChild(x, idx);
            if (isLast && idx > x.n) delete(x.children[idx - 1], key);
            else delete(x.children[idx], key);
        }
    }

    private void removeFromLeaf(Node x, int idx) {
        for (int i = idx + 1; i < x.n; i++) x.keys[i - 1] = x.keys[i];
        x.n--;
    }

    private void removeFromInternal(Node x, int idx) {
        int key = x.keys[idx];
        if (x.children[idx].n >= t) {
            int pred = getPredecessor(x, idx);
            x.keys[idx] = pred;
            delete(x.children[idx], pred);
        } else if (x.children[idx + 1].n >= t) {
            int succ = getSuccessor(x, idx);
            x.keys[idx] = succ;
            delete(x.children[idx + 1], succ);
        } else {
            mergeChildren(x, idx);
            delete(x.children[idx], key);
        }
    }

    private int getPredecessor(Node x, int idx) {
        Node cur = x.children[idx];
        while (!cur.leaf) cur = cur.children[cur.n];
        return cur.keys[cur.n - 1];
    }

    private int getSuccessor(Node x, int idx) {
        Node cur = x.children[idx + 1];
        while (!cur.leaf) cur = cur.children[0];
        return cur.keys[0];
    }

    private void fillChild(Node x, int idx) {
        if (idx != 0 && x.children[idx - 1].n >= t) borrowFromPrev(x, idx);
        else if (idx != x.n && x.children[idx + 1].n >= t) borrowFromNext(x, idx);
        else {
            if (idx != x.n) mergeChildren(x, idx);
            else mergeChildren(x, idx - 1);
        }
    }

    private void borrowFromPrev(Node x, int idx) {
        Node child = x.children[idx];
        Node sibling = x.children[idx - 1];
        for (int i = child.n - 1; i >= 0; i--) child.keys[i + 1] = child.keys[i];
        if (!child.leaf) {
            for (int i = child.n; i >= 0; i--) child.children[i + 1] = child.children[i];
        }
        child.keys[0] = x.keys[idx - 1];
        if (!child.leaf) child.children[0] = sibling.children[sibling.n];
        x.keys[idx - 1] = sibling.keys[sibling.n - 1];
        child.n++;
        sibling.n--;
    }

    private void borrowFromNext(Node x, int idx) {
        Node child = x.children[idx];
        Node sibling = x.children[idx + 1];
        child.keys[child.n] = x.keys[idx];
        if (!child.leaf) child.children[child.n + 1] = sibling.children[0];
        x.keys[idx] = sibling.keys[0];
        for (int i = 1; i < sibling.n; i++) sibling.keys[i - 1] = sibling.keys[i];
        if (!sibling.leaf) {
            for (int i = 1; i <= sibling.n; i++) sibling.children[i - 1] = sibling.children[i];
        }
        child.n++;
        sibling.n--;
    }

    private void mergeChildren(Node x, int idx) {
        Node child = x.children[idx];
        Node sibling = x.children[idx + 1];
        child.keys[t - 1] = x.keys[idx];
        for (int i = 0; i < sibling.n; i++) child.keys[i + t] = sibling.keys[i];
        if (!child.leaf) {
            for (int i = 0; i <= sibling.n; i++) child.children[i + t] = sibling.children[i];
        }
        for (int i = idx + 1; i < x.n; i++) x.keys[i - 1] = x.keys[i];
        for (int i = idx + 2; i <= x.n; i++) x.children[i - 1] = x.children[i];
        child.n += sibling.n + 1;
        x.n--;
    }

    public List<Integer> traverse() {
        List<Integer> result = new ArrayList<>();
        if (root != null) traverse(root, result);
        return result;
    }

    private void traverse(Node x, List<Integer> result) {
        int i;
        for (i = 0; i < x.n; i++) {
            if (!x.leaf) traverse(x.children[i], result);
            result.add(x.keys[i]);
        }
        if (!x.leaf) traverse(x.children[i], result);
    }

    public int getMin() {
        if (size == 0) throw new java.util.NoSuchElementException();
        Node cur = root;
        while (!cur.leaf) cur = cur.children[0];
        return cur.keys[0];
    }

    public int getMax() {
        if (size == 0) throw new java.util.NoSuchElementException();
        Node cur = root;
        while (!cur.leaf) cur = cur.children[cur.n];
        return cur.keys[cur.n - 1];
    }

    public int getHeight() {
        int h = 0;
        Node cur = root;
        while (!cur.leaf) {
            cur = cur.children[0];
            h++;
        }
        return h;
    }

    public int size() {
        return size;
    }
}
```
