package com.datastructure.unionfind;

import java.util.HashMap;
import java.util.Map;

public class MapUnionFind implements UnionFind {

    private final Map<Integer, Integer> parent = new HashMap<>();
    private final Map<Integer, Integer> treeSize = new HashMap<>();
    private int components;

    public MapUnionFind() {
    }

    /** 없으면 혼자짜리 묶음으로 만든다. 넣었으면 true. */
    public boolean add(int x) {
        if (parent.containsKey(x)) {
            return false;
        }
        parent.put(x, x);
        treeSize.put(x, 1);
        components++;
        return true;
    }

    public boolean contains(int x) {
        return parent.containsKey(x);
    }

    @Override
    public int find(int x) {
        add(x);
        int root = x;
        while (parent.get(root) != root) {
            root = parent.get(root);
        }
        int cur = x;
        while (parent.get(cur) != root) {
            int next = parent.get(cur);
            parent.put(cur, root);
            cur = next;
        }
        return root;
    }

    @Override
    public boolean union(int x, int y) {
        int rx = find(x);
        int ry = find(y);
        if (rx == ry) {
            return false;
        }
        if (treeSize.get(rx) < treeSize.get(ry)) {
            int tmp = rx;
            rx = ry;
            ry = tmp;
        }
        parent.put(ry, rx);
        treeSize.put(rx, treeSize.get(rx) + treeSize.get(ry));
        components--;
        return true;
    }

    @Override
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    @Override
    public int componentCount() {
        return components;
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public int sizeOf(int x) {
        return treeSize.get(find(x));
    }
}
