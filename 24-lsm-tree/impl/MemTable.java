package com.datastructure.lsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class MemTable<K extends Comparable<K>, V> {

    private final TreeMap<K, Object> entries = new TreeMap<>();

    public void put(K key, Object value) {
        entries.put(key, value);
    }

    public Object get(K key) {
        return entries.get(key);
    }

    public boolean containsKey(K key) {
        return entries.containsKey(key);
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void clear() {
        entries.clear();
    }

    public List<Map.Entry<K, Object>> entriesInOrder() {
        List<Map.Entry<K, Object>> out = new ArrayList<>(entries.size());
        for (Map.Entry<K, Object> e : entries.entrySet()) {
            out.add(SSTable.cell(e.getKey(), e.getValue()));
        }
        return out;
    }
}
