# pop/LRUCache.java

`int → int` LRU 캐시. HashMap + 이중 연결 리스트. O(1) get/put.

```java
package com.datastructure.lrucache.pop;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    private static class Node {
        int key, value;
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head; // dummy: head.next = 최근
    private final Node tail; // dummy: tail.prev = 가장 오래됨

    public LRUCache(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("capacity >= 1");
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            moveToHead(node);
            return;
        }
        Node fresh = new Node(key, value);
        map.put(key, fresh);
        addToHead(fresh);

        if (map.size() > capacity) {
            Node lru = tail.prev;
            removeNode(lru);
            map.remove(lru.key);
        }
    }

    public boolean remove(int key) {
        Node node = map.remove(key);
        if (node == null) return false;
        removeNode(node);
        return true;
    }

    public int peek(int key) {
        Node node = map.get(key);
        return node == null ? -1 : node.value;
    }

    public boolean containsKey(int key) {
        return map.containsKey(key);
    }

    public int size() {
        return map.size();
    }

    public int capacity() {
        return capacity;
    }

    public void clear() {
        map.clear();
        head.next = tail;
        tail.prev = head;
    }

    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }
}
```
