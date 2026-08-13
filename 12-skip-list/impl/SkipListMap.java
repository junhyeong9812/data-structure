package com.datastructure.skiplist;

import java.util.List;

public class SkipListMap<K extends Comparable<K>, V> implements OrderedMap<K, V> {

    private final SkipList<K, V> list;

    public SkipListMap() {
        this.list = new SkipList<>();
    }

    public SkipListMap(long seed) {
        this.list = new SkipList<>(seed);
    }

    SkipList<K, V> list() {
        return list;
    }

    @Override
    public V put(K key, V value) {
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다 - get 의 null 이 '없다'를 뜻하기 때문이다");
        }
        return list.put(key, value);
    }

    @Override
    public V get(K key) {
        return list.get(key);
    }

    @Override
    public boolean containsKey(K key) {
        return list.containsKey(key);
    }

    @Override
    public V remove(K key) {
        return list.remove(key);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public K firstKey() {
        return list.firstKey();
    }

    @Override
    public K lastKey() {
        return list.lastKey();
    }

    @Override
    public K floorKey(K key) {
        return list.floorKey(key);
    }

    @Override
    public K ceilingKey(K key) {
        return list.ceilingKey(key);
    }

    @Override
    public List<K> keys() {
        return list.keys();
    }

    @Override
    public List<K> keysInRange(K from, K to) {
        return list.keysInRange(from, to);
    }
}
