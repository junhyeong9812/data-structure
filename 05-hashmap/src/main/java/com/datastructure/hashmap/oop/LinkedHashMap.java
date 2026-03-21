package com.datastructure.hashmap.oop;

import java.util.Collection;
import java.util.Set;

public class LinkedHashMap<K, V> implements Map<K, V> {

    public static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }

        public K getKey() { return key; }

        public V getValue() { return value; }

        public Entry<K , V> getNext() {return next;}

        public void setNext(Entry<K, V> entry) {this.next = entry;}
    }

    private Entry<K, V>[] buckets;
    private int capacity;
    private int size;
    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public LinkedHashMap() {
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

    public Set<Entry<K, V>> entrySet() { return null; }
}