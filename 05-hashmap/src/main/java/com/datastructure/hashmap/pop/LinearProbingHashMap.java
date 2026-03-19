package com.datastructure.hashmap.pop;

import java.util.Collection;
import java.util.Set;

public class LinearProbingHashMap<K, V> {

    public static class Entry<K, V> {
        K key;
        V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public V getValue() {return value;}

        public K getKey() {return key;}

        public void setValue(V value) {this.value = value;}
    }

    private Entry<K, V>[] buckets;
    private int capacity;
    private int size;
    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public LinearProbingHashMap() {
        this.capacity = 16;
        this.buckets = new Entry[capacity];
        this.size = 0;
    }

    public V put(K key, V value) {return null;}
    public V get(K key) {return null;}
    public V remove(K key) {return null;}
    public boolean containsKey(K key) {return false;}
    public boolean containsValue(V value) {return false;}
    public int size() {return 0;}
    public boolean isEmpty() {return true;}
    public void clear() {}
    public Set<K> keySet() {return null;}
    public Collection<V> values() {return null;}
    public Set<Entry<K, V>> entrySet() {return null;}
}
