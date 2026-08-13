package com.datastructure.skiplist;

import java.util.List;

public class SkipListSet<K extends Comparable<K>> implements OrderedSet<K> {

    private static final Object PRESENT = new Object();

    private final SkipListMap<K, Object> map;

    public SkipListSet() {
        this.map = new SkipListMap<>();
    }

    public SkipListSet(long seed) {
        this.map = new SkipListMap<>(seed);
    }

    @Override
    public boolean add(K key) {
        return map.put(key, PRESENT) == null;
    }

    @Override
    public boolean contains(K key) {
        return map.containsKey(key);
    }

    @Override
    public boolean remove(K key) {
        return map.remove(key) != null;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public K first() {
        return map.firstKey();
    }

    @Override
    public K last() {
        return map.lastKey();
    }

    @Override
    public K floor(K key) {
        return map.floorKey(key);
    }

    @Override
    public K ceiling(K key) {
        return map.ceilingKey(key);
    }

    @Override
    public List<K> toList() {
        return map.keys();
    }

    @Override
    public List<K> range(K from, K to) {
        return map.keysInRange(from, to);
    }
}
