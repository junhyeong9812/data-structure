# oop/LRUCache.java

제네릭 LRU 캐시. `Cache<K, V>` 구현. HashMap + DLL.

```java
package com.datastructure.lrucache.oop;

import java.util.*;

public class LRUCache<K, V> implements Cache<K, V> {
    private class Node {
        K key;
        V value;
        Node prev, next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node> map;
    private final Node head;
    private final Node tail;

    public LRUCache(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("capacity >= 1");
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(null, null);
        this.tail = new Node(null, null);
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public V get(K key) {
        Node node = map.get(key);
        if (node == null) return null;
        moveToHead(node);
        return node.value;
    }

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key);
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

    @Override
    public V remove(K key) {
        Node node = map.remove(key);
        if (node == null) return null;
        removeNode(node);
        return node.value;
    }

    @Override
    public V peek(K key) {
        Node node = map.get(key);
        return node == null ? null : node.value;
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public void clear() {
        map.clear();
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public List<K> keys() {
        List<K> result = new ArrayList<>();
        for (Node n = head.next; n != tail; n = n.next) {
            result.add(n.key);
        }
        return result;
    }

    @Override
    public List<V> values() {
        List<V> result = new ArrayList<>();
        for (Node n = head.next; n != tail; n = n.next) {
            result.add(n.value);
        }
        return result;
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
