# oop/DisjointSet.java

제네릭 Disjoint Set. 임의 타입 원소 지원, HashMap 기반.

```java
package com.datastructure.unionfind.oop;

import java.util.*;

public class DisjointSet<T> {
    private static class Node<T> {
        T value;
        Node<T> parent;
        int rank;
        int size;

        Node(T value) {
            this.value = value;
            this.parent = this;
            this.rank = 0;
            this.size = 1;
        }
    }

    private final Map<T, Node<T>> nodes = new HashMap<>();
    private int setCount;

    public void makeSet(T x) {
        if (!nodes.containsKey(x)) {
            nodes.put(x, new Node<>(x));
            setCount++;
        }
    }

    public T find(T x) {
        Node<T> node = nodes.get(x);
        if (node == null) throw new NoSuchElementException(String.valueOf(x));

        Node<T> root = node;
        while (root.parent != root) root = root.parent;
        while (node.parent != root) {
            Node<T> next = node.parent;
            node.parent = root;
            node = next;
        }
        return root.value;
    }

    public boolean union(T x, T y) {
        Node<T> rx = nodes.get(find(x));
        Node<T> ry = nodes.get(find(y));
        if (rx == ry) return false;

        if (rx.rank < ry.rank) {
            rx.parent = ry;
            ry.size += rx.size;
        } else if (rx.rank > ry.rank) {
            ry.parent = rx;
            rx.size += ry.size;
        } else {
            ry.parent = rx;
            rx.size += ry.size;
            rx.rank++;
        }
        setCount--;
        return true;
    }

    public boolean connected(T x, T y) {
        return Objects.equals(find(x), find(y));
    }

    public int getSize(T x) {
        return nodes.get(find(x)).size;
    }

    public int getSetCount() {
        return setCount;
    }

    public boolean contains(T x) {
        return nodes.containsKey(x);
    }

    public Map<T, List<T>> getComponents() {
        Map<T, List<T>> map = new HashMap<>();
        for (T value : nodes.keySet()) {
            T root = find(value);
            map.computeIfAbsent(root, k -> new ArrayList<>()).add(value);
        }
        return map;
    }
}
```
