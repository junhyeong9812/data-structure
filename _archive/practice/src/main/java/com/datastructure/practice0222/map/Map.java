package com.datastructure.practice0222.map;

import java.util.Collection;
import java.util.Set;

public interface Map<K, V> extends Iterable<Map.Entry<K, V>> {

    void put(K Key, V value);

    V get(K key);

    V remove(K key);

    boolean containsKey(K key);

    boolean containsValue(V value);

    int size();

    boolean isEmpty();

    void clear();

    Set<K> keySet();

    Collection<V> values();

    Set<Entry<K, V>> entrySet();

    V getOrDefault(K key, V defaultValue);

    interface Entry<K, V> {
        K getKey();
        V getValue();
        V setValue(V value);
    }
}
