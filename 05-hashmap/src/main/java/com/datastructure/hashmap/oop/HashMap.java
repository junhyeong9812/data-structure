package com.datastructure.hashmap.oop;

import java.util.Collection;
import java.util.Set;

public class HashMap<K, V> implements Map<K, V> {

    public static class Entry<K , V> {
        K key;
        V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public V getValue() { return value; }
        public K getKey() { return key; }
        public void setValue(V value) {this.value = value;}
    }

    private Entry<K, V>[] buckets;
    private int capacity;
    private int size;
    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public HashMap() {
        this.capacity = 16;
        this.buckets = new Entry[capacity];
        this.size = 0;
    }

    @Override
    public V put(K key, V value) { return null; }

    @Override
    public V get(K key) { return null; }

    @Override
    public V remove(K key) { return null; }

    @Override
    public boolean containsKey(K key) { return false; }

    @Override
    public boolean containsValue(V value) { return false; }

    @Override
    public int size() { return 0; }

    @Override
    public boolean isEmpty() { return true; }

    @Override
    public void clear() {}

    @Override
    public Set<K> keySet() { return null; }

    @Override
    public Collection<V> values() { return null; }
}
