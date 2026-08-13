# oop/SkipListMap.java

제네릭 Key-Value 스킵 리스트. `put/get/remove/floorKey/ceilingKey` 등 정렬된 맵 기능.

```java
package com.datastructure.skiplist.oop;

import java.util.*;

public class SkipListMap<K extends Comparable<K>, V> {
    private static final int MAX_LEVEL = 16;
    private static final double P = 0.5;

    private class Node {
        K key;
        V value;
        Node[] forward;

        @SuppressWarnings("unchecked")
        Node(K key, V value, int level) {
            this.key = key;
            this.value = value;
            this.forward = (Node[]) java.lang.reflect.Array.newInstance(Node.class, level + 1);
        }
    }

    private final Node head;
    private int level;
    private int size;
    private final Random random = new Random();

    public SkipListMap() {
        this.head = new Node(null, null, MAX_LEVEL);
        this.level = 0;
        this.size = 0;
    }

    private int randomLevel() {
        int lvl = 0;
        while (random.nextDouble() < P && lvl < MAX_LEVEL) lvl++;
        return lvl;
    }

    private int cmp(K a, K b) {
        return a.compareTo(b);
    }

    public V put(K key, V value) {
        Objects.requireNonNull(key);
        @SuppressWarnings("unchecked")
        Node[] update = (Node[]) java.lang.reflect.Array.newInstance(Node.class, MAX_LEVEL + 1);
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cmp(cur.forward[i].key, key) < 0) {
                cur = cur.forward[i];
            }
            update[i] = cur;
        }
        Node next = cur.forward[0];
        if (next != null && cmp(next.key, key) == 0) {
            V old = next.value;
            next.value = value;
            return old;
        }

        int lvl = randomLevel();
        if (lvl > level) {
            for (int i = level + 1; i <= lvl; i++) update[i] = head;
            level = lvl;
        }
        Node node = new Node(key, value, lvl);
        for (int i = 0; i <= lvl; i++) {
            node.forward[i] = update[i].forward[i];
            update[i].forward[i] = node;
        }
        size++;
        return null;
    }

    public V get(K key) {
        Node node = findNode(key);
        return node == null ? null : node.value;
    }

    public boolean containsKey(K key) {
        return findNode(key) != null;
    }

    private Node findNode(K key) {
        if (key == null) return null;
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cmp(cur.forward[i].key, key) < 0) {
                cur = cur.forward[i];
            }
        }
        Node next = cur.forward[0];
        return (next != null && cmp(next.key, key) == 0) ? next : null;
    }

    public V remove(K key) {
        @SuppressWarnings("unchecked")
        Node[] update = (Node[]) java.lang.reflect.Array.newInstance(Node.class, MAX_LEVEL + 1);
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cmp(cur.forward[i].key, key) < 0) {
                cur = cur.forward[i];
            }
            update[i] = cur;
        }
        Node target = cur.forward[0];
        if (target == null || cmp(target.key, key) != 0) return null;

        for (int i = 0; i <= level; i++) {
            if (update[i].forward[i] != target) break;
            update[i].forward[i] = target.forward[i];
        }
        while (level > 0 && head.forward[level] == null) level--;
        size--;
        return target.value;
    }

    public K floorKey(K key) {
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cmp(cur.forward[i].key, key) <= 0) {
                cur = cur.forward[i];
            }
        }
        return cur == head ? null : cur.key;
    }

    public K ceilingKey(K key) {
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cmp(cur.forward[i].key, key) < 0) {
                cur = cur.forward[i];
            }
        }
        Node next = cur.forward[0];
        return next == null ? null : next.key;
    }

    public List<Map.Entry<K, V>> range(K from, K to) {
        List<Map.Entry<K, V>> result = new ArrayList<>();
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cmp(cur.forward[i].key, from) < 0) {
                cur = cur.forward[i];
            }
        }
        cur = cur.forward[0];
        while (cur != null && cmp(cur.key, to) <= 0) {
            result.add(Map.entry(cur.key, cur.value));
            cur = cur.forward[0];
        }
        return result;
    }

    public K firstKey() {
        Node first = head.forward[0];
        if (first == null) throw new NoSuchElementException();
        return first.key;
    }

    public K lastKey() {
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null) cur = cur.forward[i];
        }
        if (cur == head) throw new NoSuchElementException();
        return cur.key;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i <= MAX_LEVEL; i++) head.forward[i] = null;
        level = 0;
        size = 0;
    }
}
```
