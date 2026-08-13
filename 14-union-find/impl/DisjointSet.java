package com.datastructure.unionfind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DisjointSet<T> {

    private final Map<T, Integer> ids = new LinkedHashMap<>();
    private final List<T> items = new ArrayList<>();
    private final MapUnionFind uf = new MapUnionFind();

    public boolean add(T item) {
        requireItem(item);
        if (ids.containsKey(item)) {
            return false;
        }
        int id = items.size();
        ids.put(item, id);
        items.add(item);
        uf.add(id);
        return true;
    }

    public boolean contains(T item) {
        return ids.containsKey(item);
    }

    public T find(T item) {
        requireItem(item);
        add(item);
        return items.get(uf.find(ids.get(item)));
    }

    public boolean union(T a, T b) {
        requireItem(a);
        requireItem(b);
        add(a);
        add(b);
        return uf.union(ids.get(a), ids.get(b));
    }

    public boolean connected(T a, T b) {
        requireItem(a);
        requireItem(b);
        if (!ids.containsKey(a) || !ids.containsKey(b)) {
            return false;
        }
        return uf.connected(ids.get(a), ids.get(b));
    }

    public int componentCount() {
        return uf.componentCount();
    }

    public int size() {
        return items.size();
    }

    public int sizeOf(T item) {
        requireItem(item);
        add(item);
        return uf.sizeOf(ids.get(item));
    }

    /** 묶음마다 원소 목록. 대표를 키로 한다. */
    public Map<T, List<T>> groups() {
        Map<T, List<T>> out = new LinkedHashMap<>();
        for (T item : items) {
            out.computeIfAbsent(find(item), k -> new ArrayList<>()).add(item);
        }
        return out;
    }

    private static void requireItem(Object item) {
        if (item == null) {
            throw new IllegalArgumentException("원소는 null 일 수 없다");
        }
    }
}
