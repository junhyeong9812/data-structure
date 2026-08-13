# pop/LazySegmentTree.java

지연 전파(Lazy Propagation) 세그먼트 트리. 구간 update + 구간 sum query.

```java
package com.datastructure.segmenttree.pop;

public class LazySegmentTree {
    private final long[] tree;
    private final long[] lazy;
    private final int n;

    public LazySegmentTree(int[] arr) {
        this.n = arr.length;
        this.tree = new long[4 * Math.max(1, n)];
        this.lazy = new long[4 * Math.max(1, n)];
        if (n > 0) build(arr, 1, 0, n - 1);
    }

    private void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start];
            return;
        }
        int mid = (start + end) >>> 1;
        build(arr, 2 * node, start, mid);
        build(arr, 2 * node + 1, mid + 1, end);
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }

    private void push(int node, int start, int end) {
        if (lazy[node] == 0) return;
        int mid = (start + end) >>> 1;
        applyLazy(2 * node, start, mid, lazy[node]);
        applyLazy(2 * node + 1, mid + 1, end, lazy[node]);
        lazy[node] = 0;
    }

    private void applyLazy(int node, int start, int end, long val) {
        tree[node] += (long) (end - start + 1) * val;
        lazy[node] += val;
    }

    public void updateRange(int l, int r, int val) {
        if (l < 0 || r >= n || l > r) throw new IndexOutOfBoundsException();
        updateRange(1, 0, n - 1, l, r, val);
    }

    private void updateRange(int node, int start, int end, int l, int r, int val) {
        if (r < start || l > end) return;
        if (l <= start && end <= r) {
            applyLazy(node, start, end, val);
            return;
        }
        push(node, start, end);
        int mid = (start + end) >>> 1;
        updateRange(2 * node, start, mid, l, r, val);
        updateRange(2 * node + 1, mid + 1, end, l, r, val);
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }

    public long query(int l, int r) {
        if (l < 0 || r >= n || l > r) throw new IndexOutOfBoundsException();
        return query(1, 0, n - 1, l, r);
    }

    private long query(int node, int start, int end, int l, int r) {
        if (r < start || l > end) return 0L;
        if (l <= start && end <= r) return tree[node];
        push(node, start, end);
        int mid = (start + end) >>> 1;
        return query(2 * node, start, mid, l, r)
                + query(2 * node + 1, mid + 1, end, l, r);
    }
}
```
