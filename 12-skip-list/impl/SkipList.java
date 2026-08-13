package com.datastructure.skiplist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkipList<K extends Comparable<K>, V> {

    static final int MAX_LEVEL = 32;
    static final double P = 0.5;

    static final class Node<K, V> {
        final K key;
        V value;
        final Node<K, V>[] forward;

        @SuppressWarnings("unchecked")
        Node(K key, V value, int level) {
            this.key = key;
            this.value = value;
            this.forward = new Node[level];
        }
    }

    final Node<K, V> head = new Node<>(null, null, MAX_LEVEL);
    private final Random random;
    int level = 1;
    private int size;

    public SkipList() {
        this(new Random());
    }

    public SkipList(long seed) {
        this(new Random(seed));
    }

    SkipList(Random random) {
        this.random = random;
    }

    int randomLevel() {
        int lvl = 1;
        while (lvl < MAX_LEVEL && random.nextDouble() < P) {
            lvl++;
        }
        return lvl;
    }

    /** key 보다 **작은** 마지막 노드를 레벨마다 찾아 update 에 담고, 레벨 0 의 그 노드를 준다. */
    @SuppressWarnings("unchecked")
    Node<K, V>[] findPredecessors(K key) {
        Node<K, V>[] update = new Node[MAX_LEVEL];
        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].key.compareTo(key) < 0) {
                cur = cur.forward[i];
            }
            update[i] = cur;
        }
        return update;
    }

    public V get(K key) {
        requireKey(key);
        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].key.compareTo(key) < 0) {
                cur = cur.forward[i];
            }
        }
        Node<K, V> next = cur.forward[0];
        return (next != null && next.key.compareTo(key) == 0) ? next.value : null;
    }

    public V put(K key, V value) {
        requireKey(key);
        Node<K, V>[] update = findPredecessors(key);
        Node<K, V> next = update[0].forward[0];
        if (next != null && next.key.compareTo(key) == 0) {
            V old = next.value;
            next.value = value;
            return old;
        }
        int lvl = randomLevel();
        if (lvl > level) {
            for (int i = level; i < lvl; i++) {
                update[i] = head;
            }
            level = lvl;
        }
        Node<K, V> node = new Node<>(key, value, lvl);
        for (int i = 0; i < lvl; i++) {
            node.forward[i] = update[i].forward[i];
            update[i].forward[i] = node;
        }
        size++;
        return null;
    }

    public V remove(K key) {
        requireKey(key);
        Node<K, V>[] update = findPredecessors(key);
        Node<K, V> target = update[0].forward[0];
        if (target == null || target.key.compareTo(key) != 0) {
            return null;
        }
        for (int i = 0; i < level; i++) {
            if (update[i].forward[i] != target) {
                break;
            }
            update[i].forward[i] = target.forward[i];
        }
        while (level > 1 && head.forward[level - 1] == null) {
            level--;
        }
        size--;
        return target.value;
    }

    public K floorKey(K key) {
        requireKey(key);
        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].key.compareTo(key) <= 0) {
                cur = cur.forward[i];
            }
        }
        return cur == head ? null : cur.key;
    }

    public K ceilingKey(K key) {
        requireKey(key);
        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].key.compareTo(key) < 0) {
                cur = cur.forward[i];
            }
        }
        Node<K, V> next = cur.forward[0];
        return next == null ? null : next.key;
    }

    public List<K> keys() {
        List<K> out = new ArrayList<>(size);
        for (Node<K, V> cur = head.forward[0]; cur != null; cur = cur.forward[0]) {
            out.add(cur.key);
        }
        return out;
    }

    public List<K> keysInRange(K from, K to) {
        requireKey(from);
        requireKey(to);
        List<K> out = new ArrayList<>();
        if (from.compareTo(to) > 0) {
            return out;
        }
        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].key.compareTo(from) < 0) {
                cur = cur.forward[i];
            }
        }
        for (Node<K, V> n = cur.forward[0]; n != null && n.key.compareTo(to) <= 0; n = n.forward[0]) {
            out.add(n.key);
        }
        return out;
    }

    public K firstKey() {
        Node<K, V> n = head.forward[0];
        return n == null ? null : n.key;
    }

    public K lastKey() {
        Node<K, V> cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.forward[i] != null) {
                cur = cur.forward[i];
            }
        }
        return cur == head ? null : cur.key;
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

    public int currentLevel() {
        return level;
    }

    public void clear() {
        for (int i = 0; i < MAX_LEVEL; i++) {
            head.forward[i] = null;
        }
        level = 1;
        size = 0;
    }

    private static void requireKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
    }
}
