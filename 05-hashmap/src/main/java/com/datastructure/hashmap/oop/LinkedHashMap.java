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

        public void setValue(V value) {this.value = value;}
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
    public V put(K key, V value) {
        rehash();
        int index = getIndex(key);
        Entry<K, V> entry = buckets[index];

        while (entry != null) {
            if (entry.getKey() == key || (key != null && key.equals(entry.getKey()))) {
                V oldValue = entry.getValue();
                entry.setValue(value);
                return oldValue;
            }
            entry = entry.next;
        }

        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.setNext(buckets[index]);
        buckets[index] = newEntry;
        size++;
        return null; }

    @Override
    public V get(K key) {
        int index = getIndex(key);
        Entry<K, V> entry = buckets[index];

        while (entry != null) {
            if (entry.getKey() == key || (key != null && key.equals(entry.getKey()))) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int index = getIndex(key);
        Entry<K, V> entry = buckets[index];
        Entry<K, V> beforeEntry = null;

        while (entry != null) {
            if (entry.getKey() == key || (key != null && key.equals(entry.getKey()))) {
                if (beforeEntry == null) {
                    buckets[index] = entry.next;
                } else {
                    beforeEntry.setNext(entry.next);
                }
                size--;
                return entry.getValue();
            }
            beforeEntry = entry;
            entry = entry.next;
        }
        return null; }

    @Override
    public boolean containsKey(K key) {
        int index = getIndex(key);
        Entry<K, V> entry = buckets[index];

        while (entry != null) {
            if (entry.getKey() == key || (key != null && key.equals(entry.getKey()))) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

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

    private int getIndex(K key) {
        if (key != null) {
            int hash = key.hashCode();
            int index = hash & (capacity - 1);

            return index;
        }
        return 0;
    }

    private void rehash() {
        if ((float) size / capacity >= 0.75f) {
            growCapacity();
        }
    }

    @SuppressWarnings("unchecked")
    private void growCapacity() {
        int oldCapacity = capacity;
        Entry<K, V>[] oldBuckets = buckets;

        capacity = capacity * 2;
        buckets = new Entry[capacity];

        for (int i = 0; i < oldCapacity; i++) {
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