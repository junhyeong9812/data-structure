package com.datastructure.hashmap.pop;

import java.util.*;

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

    public V put(K key, V value) {
        rehash();
        int index = getIndex(key);

        while (buckets[index] != null) {
            if (buckets[index].getKey() == key
                    || (key != null && key.equals(buckets[index].getKey()))) {
                V oldValue = buckets[index].getValue();
                buckets[index].setValue(value);
                return oldValue;
            }
            index = (index + 1) % capacity;
        }

        buckets[index] = new Entry<>(key, value);
        size++;
        return null;
    }

    public V get(K key) {
        int index = getIndex(key);
        while (buckets[index] != null) {
            if (buckets[index].getKey() == key
            || (key != null && key.equals(buckets[index].getKey()))) {
                return buckets[index].getValue();
            }
            index = (index + 1) % capacity;
        }
        return null;
    }

    public V remove(K key) {
        int index = getIndex(key);
        while(buckets[index] != null) {
            if (buckets[index].getKey() == key
                    || (key != null && key.equals(buckets[index].getKey()))) {
                V removed = buckets[index].getValue();
                buckets[index] = null;
                size--;
                rehashCluster(index);
                return removed;
            }
            index = (index + 1) % capacity;
        }
        return null;
    }

    public boolean containsKey(K key) {
        int index = getIndex(key);
        while(buckets[index] != null) {
            if (buckets[index].getKey() == key
                    || (key != null && key.equals(buckets[index].getKey()))) {
                return true;
            }
            index = (index + 1) % capacity;
        }
        return false;
    }

    public boolean containsValue(V value) {
        for (int i = 0; i < capacity; i++) {
            if (buckets[i] != null && (buckets[i].getValue() == value
                    || (value != null && value.equals(buckets[i].getValue())))) {
                return true;
            }
        }
        return false;
    }

    public int size() {return size;}

    public boolean isEmpty() {return size==0;}

    public void clear() {
        this.capacity = 16;
        this.buckets = new Entry[capacity];
        this.size = 0;
    }
    public Set<K> keySet() {
        Set<K> result = new HashSet<>();
        for(int i = 0; i < capacity; i++) {
            if (buckets[i] != null) {
                result.add(buckets[i].getKey());
            }
        }
        return result;
    }

    public Collection<V> values() {
        List<V> result = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            if (buckets[i] != null) {
                result.add(buckets[i].getValue());
            }
        }
        return result;
    }

    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> result = new HashSet<>();
        for (int i = 0; i < capacity; i++) {
            if (buckets[i] != null) {
                Entry<K, V> entry = buckets[i];
                result.add(entry);
            }
        }
        return result;
    }

    private int getIndex(K key) {
        if (key != null){
            int hash = key.hashCode();
            int index = hash & (capacity - 1);

            return index;
        }
        return 0;
    }

    private void rehash() {
        if ((float) size / capacity >= LOAD_FACTOR) {
            growCapacity();
        }
    }

    private void growCapacity() {
        int oldCapacity = capacity;
        Entry<K, V>[]  oldBuckets = buckets;

        capacity = capacity * 2;
        buckets = new Entry[capacity];

        for (int i = 0; i < oldCapacity; i++) {
            Entry<K, V> entry = oldBuckets[i];
            if (entry != null) {
                put(entry.getKey(), entry.value);
            }
        }
    }

    private void rehashCluster(int startIndex) {
        int index = (startIndex + 1) % capacity;
        while (buckets[index] != null) {
            Entry<K, V> entry = buckets[index];
            buckets[index] = null;
            size--;
            put(entry.getKey(), entry.getValue());
            index = (index + 1) % capacity;
        }
    }
}
