package com.datastructure.hashmap.pop;

import java.util.Collection;
import java.util.Set;

public class ChainingHashMap<K, V> {

    public static class Entry<K, V> {
        K key;
        V value;

        Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }

        public K getKey() {return key;}

        public V getValue() {return value;}

        public Entry<K, V> getNext() {return next;}

        public void setNext(Entry<K, V> entry) {this.next = entry;}
    }

    private Entry<K, V>[] buckets;
    private int capacity;
    private int size;
    private static final float LOAD_FACTOR = 0.75f;

    public ChainingHashMap() {
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
