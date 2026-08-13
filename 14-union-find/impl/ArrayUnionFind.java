package com.datastructure.unionfind;

public class ArrayUnionFind implements UnionFind {

    private final int[] parent;
    private final int[] treeSize;
    private final boolean unionBySize;
    private final boolean pathCompression;
    private int components;

    public ArrayUnionFind(int n) {
        this(n, true, true);
    }

    ArrayUnionFind(int n, boolean unionBySize, boolean pathCompression) {
        if (n < 1) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다: " + n);
        }
        this.parent = new int[n];
        this.treeSize = new int[n];
        this.unionBySize = unionBySize;
        this.pathCompression = pathCompression;
        this.components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            treeSize[i] = 1;
        }
    }

    @Override
    public int find(int x) {
        requireIndex(x);
        int root = x;
        while (parent[root] != root) {
            root = parent[root];
        }
        if (pathCompression) {
            int cur = x;
            while (parent[cur] != root) {
                int next = parent[cur];
                parent[cur] = root;
                cur = next;
            }
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
        if (unionBySize && treeSize[rx] < treeSize[ry]) {
            int tmp = rx;
            rx = ry;
            ry = tmp;
        }
        parent[ry] = rx;
        treeSize[rx] += treeSize[ry];
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
        return parent.length;
    }

    @Override
    public int sizeOf(int x) {
        return treeSize[find(x)];
    }

    int parentOf(int x) {
        requireIndex(x);
        return parent[x];
    }

    /** 뿌리까지 몇 걸음인가. 최적화의 효과를 재려고 둔 것이다. */
    int depthOf(int x) {
        requireIndex(x);
        int d = 0;
        int cur = x;
        while (parent[cur] != cur) {
            cur = parent[cur];
            d++;
        }
        return d;
    }

    private void requireIndex(int x) {
        if (x < 0 || x >= parent.length) {
            throw new IndexOutOfBoundsException("원소 " + x + " 가 범위를 벗어났다 (크기 " + parent.length + ")");
        }
    }
}
