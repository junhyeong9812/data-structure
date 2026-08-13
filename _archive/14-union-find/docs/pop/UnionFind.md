# pop/UnionFind.java

배열 기반 Union-Find. 경로 압축 + Union by Rank.

```java
package com.datastructure.unionfind.pop;

public class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private final int[] size;
    private int setCount;

    public UnionFind(int n) {
        if (n < 0) throw new IllegalArgumentException();
        this.parent = new int[n];
        this.rank = new int[n];
        this.size = new int[n];
        this.setCount = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int x) {
        validate(x);
        // 경로 압축 (반복 + 두 패스)
        int root = x;
        while (parent[root] != root) root = parent[root];
        while (parent[x] != root) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }

    public boolean union(int x, int y) {
        int rx = find(x);
        int ry = find(y);
        if (rx == ry) return false;

        if (rank[rx] < rank[ry]) {
            parent[rx] = ry;
            size[ry] += size[rx];
        } else if (rank[rx] > rank[ry]) {
            parent[ry] = rx;
            size[rx] += size[ry];
        } else {
            parent[ry] = rx;
            size[rx] += size[ry];
            rank[rx]++;
        }
        setCount--;
        return true;
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    public int getSize(int x) {
        return size[find(x)];
    }

    public int getSetCount() {
        return setCount;
    }

    public int count() {
        return parent.length;
    }

    private void validate(int x) {
        if (x < 0 || x >= parent.length) {
            throw new IndexOutOfBoundsException(String.valueOf(x));
        }
    }
}
```
