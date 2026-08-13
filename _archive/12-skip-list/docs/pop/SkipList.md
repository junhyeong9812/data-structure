# pop/SkipList.java

int 키 전용 스킵 리스트. add/contains/remove + floor/ceiling/range/rank/select.

```java
package com.datastructure.skiplist.pop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkipList {
    private static final int MAX_LEVEL = 16;
    private static final double P = 0.5;

    private static class Node {
        int key;
        Node[] forward;

        Node(int key, int level) {
            this.key = key;
            this.forward = new Node[level + 1];
        }
    }

    private final Node head;
    private int level; // 현재 최대 레벨
    private int size;
    private final Random random;

    public SkipList() {
        this.head = new Node(Integer.MIN_VALUE, MAX_LEVEL);
        this.level = 0;
        this.size = 0;
        this.random = new Random();
    }

    private int randomLevel() {
        int lvl = 0;
        while (random.nextDouble() < P && lvl < MAX_LEVEL) lvl++;
        return lvl;
    }

    public boolean add(int key) {
        Node[] update = new Node[MAX_LEVEL + 1];
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].key < key) {
                cur = cur.forward[i];
            }
            update[i] = cur;
        }
        Node next = cur.forward[0];
        if (next != null && next.key == key) return false; // 중복

        int lvl = randomLevel();
        if (lvl > level) {
            for (int i = level + 1; i <= lvl; i++) update[i] = head;
            level = lvl;
        }
        Node newNode = new Node(key, lvl);
        for (int i = 0; i <= lvl; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
        size++;
        return true;
    }

    public boolean contains(int key) {
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].key < key) {
                cur = cur.forward[i];
            }
        }
        Node next = cur.forward[0];
        return next != null && next.key == key;
    }

    public boolean remove(int key) {
        Node[] update = new Node[MAX_LEVEL + 1];
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].key < key) {
                cur = cur.forward[i];
            }
            update[i] = cur;
        }
        Node target = cur.forward[0];
        if (target == null || target.key != key) return false;

        for (int i = 0; i <= level; i++) {
            if (update[i].forward[i] != target) break;
            update[i].forward[i] = target.forward[i];
        }
        while (level > 0 && head.forward[level] == null) level--;
        size--;
        return true;
    }

    public Integer floor(int key) {
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].key <= key) {
                cur = cur.forward[i];
            }
        }
        return cur == head ? null : cur.key;
    }

    public Integer ceiling(int key) {
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].key < key) {
                cur = cur.forward[i];
            }
        }
        Node next = cur.forward[0];
        return next == null ? null : next.key;
    }

    public List<Integer> range(int from, int to) {
        List<Integer> result = new ArrayList<>();
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null && cur.forward[i].key < from) {
                cur = cur.forward[i];
            }
        }
        cur = cur.forward[0];
        while (cur != null && cur.key <= to) {
            result.add(cur.key);
            cur = cur.forward[0];
        }
        return result;
    }

    public Integer getMin() {
        Node first = head.forward[0];
        return first == null ? null : first.key;
    }

    public Integer getMax() {
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.forward[i] != null) cur = cur.forward[i];
        }
        return cur == head ? null : cur.key;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getLevel() {
        return level;
    }

    public void clear() {
        for (int i = 0; i <= MAX_LEVEL; i++) head.forward[i] = null;
        level = 0;
        size = 0;
    }
}
```
