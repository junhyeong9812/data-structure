package com.datastructure.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LinkedHashMapLRU<K, V> implements Cache<K, V> {

    private final int capacity;
    private final LinkedHashMap<K, V> map;

    private long hits;
    private long misses;
    private long evictions;

    public LinkedHashMapLRU(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("용량은 1 이상이어야 한다: " + capacity);
        }
        this.capacity = capacity;
        this.map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LinkedHashMapLRU.this.capacity;
            }
        };
    }

    @Override
    public V get(K key) {
        V v = map.get(key);
        if (v == null) {
            misses++;
        } else {
            hits++;
        }
        return v;
    }

    @Override
    public void put(K key, V value) {
        requirePair(key, value);
        boolean isNew = !map.containsKey(key);
        int before = map.size();
        map.put(key, value);
        if (isNew && before == capacity) {
            evictions++;
        }
    }

    @Override
    public V remove(K key) {
        return map.remove(key);
    }

    @Override
    public List<K> keysInOrder() {
        return new ArrayList<>(map.keySet());
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
