package com.datastructure.splay;

import java.util.List;

public class SplayTreeSet<K extends Comparable<K>> {

    private static final Object PRESENT = new Object();

    private final SplayTreeMap<K, Object> map = new SplayTreeMap<>();

    public boolean add(K key) {
        return map.put(key, PRESENT) == null;
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }

    public boolean remove(K key) {
        return map.remove(key) != null;
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void clear() {
        map.clear();
    }

    public List<K> toList() {
        return map.keys();
    }

    public K first() {
        return map.firstKey();
    }

    public K last() {
        return map.lastKey();
    }

    public K floor(K key) {
        return map.floorKey(key);
    }

    public K ceiling(K key) {
        return map.ceilingKey(key);
    }
}
