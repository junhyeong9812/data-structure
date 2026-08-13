package com.datastructure.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LRUCache<K, V> implements Cache<K, V> {

    static final class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;
    }

    private final int capacity;
    private final Map<K, Node<K, V>> index = new HashMap<>();

    final Node<K, V> head = new Node<>();
    final Node<K, V> tail = new Node<>();

    private long hits;
    private long misses;
    private long evictions;

    public LRUCache(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("용량은 1 이상이어야 한다: " + capacity);
        }
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    private void unlink(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = null;
        node.next = null;
    }

    private void linkLast(Node<K, V> node) {
        node.prev = tail.prev;
        node.next = tail;
        tail.prev.next = node;
        tail.prev = node;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = index.get(key);
        if (node == null) {
            misses++;
            return null;
        }
        hits++;
        unlink(node);
        linkLast(node);
        return node.value;
    }

    @Override
    public void put(K key, V value) {
        requirePair(key, value);
        Node<K, V> existing = index.get(key);
        if (existing != null) {
            existing.value = value;
            unlink(existing);
            linkLast(existing);
            return;
        }
        if (index.size() == capacity) {
            Node<K, V> oldest = head.next;
            index.remove(oldest.key);
            unlink(oldest);
            evictions++;
        }
        Node<K, V> node = new Node<>();
        node.key = key;
        node.value = value;
        linkLast(node);
        index.put(key, node);
    }

    @Override
    public V remove(K key) {
        Node<K, V> node = index.remove(key);
        if (node == null) {
            return null;
        }
        V old = node.value;
        unlink(node);
        return old;
    }

    @Override
    public List<K> keysInOrder() {
        List<K> out = new ArrayList<>(index.size());
        for (Node<K, V> cur = head.next; cur != tail; cur = cur.next) {
            out.add(cur.key);
        }
        return out;
    }

    @Override
    public boolean containsKey(K key) {
        return index.containsKey(key);
    }

    @Override
    public int size() {
        return index.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public boolean isEmpty() {
        return index.isEmpty();
    }

    @Override
    public void clear() {
        index.clear();
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public long hits() {
        return hits;
    }

    @Override
    public long misses() {
        return misses;
    }

    @Override
    public long evictions() {
        return evictions;
    }

    private static void requirePair(Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다 - get 의 null 이 '없다'를 뜻하기 때문이다");
        }
    }
}
