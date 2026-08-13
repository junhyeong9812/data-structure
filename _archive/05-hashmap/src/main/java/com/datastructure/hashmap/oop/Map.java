package com.datastructure.hashmap.oop;

import java.util.Collection;
import java.util.Set;

public interface Map<K, V> {

    V put(K key, V value);
    V get(K key);
    V remove(K key);
    boolean containsKey(K key);
    boolean containsValue(V value);
    int size();
    boolean isEmpty();
    void clear();
    Set<K> keySet();
    Collection<V> values();
}
