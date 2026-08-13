package com.datastructure.unionfind;

public class WeightedUnionFind {

    private final int[] parent;
    private final int[] treeSize;
    private final long[] weight;
    private int components;

    public WeightedUnionFind(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다: " + n);
        }
        this.parent = new int[n];
        this.treeSize = new int[n];
        this.weight = new long[n];
        this.components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            treeSize[i] = 1;
        }
    }

    public int find(int x) {
        requireIndex(x);
        if (parent[x] == x) {
            return x;
        }
        int p = parent[x];
        int root = find(p);
        weight[x] += weight[p];
        parent[x] = root;
        return root;
    }

    /** value(y) - value(x) = w 라고 선언한다. 이미 아는 것과 모순이면 false. */
    public boolean union(int x, int y, long w) {
        int rx = find(x);
        int ry = find(y);
        if (rx == ry) {
            return weight[y] - weight[x] == w;
        }
        long d = w + weight[x] - weight[y];
        if (treeSize[rx] >= treeSize[ry]) {
            parent[ry] = rx;
            weight[ry] = d;
            treeSize[rx] += treeSize[ry];
        } else {
            parent[rx] = ry;
            weight[rx] = -d;
            treeSize[ry] += treeSize[rx];
        }
        components--;
        return true;
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    /** value(y) - value(x). 연결돼 있지 않으면 예외. */
    public long diff(int x, int y) {
        if (!connected(x, y)) {
            throw new IllegalStateException(x + " 와 " + y + " 는 아직 연결되지 않았다");
        }
        return weight[y] - weight[x];
    }

    public int componentCount() {
        return components;
    }

    public int size() {
        return parent.length;
    }

    long weightOf(int x) {
        return weight[x];
    }

    private void requireIndex(int x) {
        if (x < 0 || x >= parent.length) {
            throw new IndexOutOfBoundsException("원소 " + x + " 가 범위를 벗어났다 (크기 " + parent.length + ")");
        }
    }
}
