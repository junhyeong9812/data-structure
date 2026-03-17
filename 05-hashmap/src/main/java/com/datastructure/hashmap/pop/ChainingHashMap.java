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

    @SuppressWarnings("unchecked")
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

    private int getIndex(K key) {
        if (key != null) {
            int hash = key.hashCode();
            int index = hash & (capacity - 1);

            //int Math.abs(hash) % capacity;
            return index;
        }
        return 0;
    }

    private void rehash() {
        if ( (float) size / capacity >= 0.75f ) {
            growCapacity();
        }
    }

    @SuppressWarnings("unchecked")
    private void growCapacity() {
        int oldCapacity = capacity;
        Entry<K, V>[] oldBuckets = buckets;

        capacity = capacity * 2;
        buckets = new Entry[capacity];

        for(int i = 0; i < oldCapacity; i++) {
            Entry<K, V> entry = oldBuckets[i];
            while(entry != null) {
                Entry<K, V> next = entry.next;
                int newIndex = getIndex(entry.getKey());
                entry.next = buckets[newIndex];
                buckets[newIndex] = entry;
                entry = next;
            }
        }
    }
}
