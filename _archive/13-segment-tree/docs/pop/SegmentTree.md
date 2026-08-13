# pop/SegmentTree.java

구간 합 세그먼트 트리. build/query/update O(log n).

```java
package com.datastructure.segmenttree.pop;

public class SegmentTree {
    private final long[] tree;
    private final int n;

    public SegmentTree(int[] arr) {
        this.n = arr.length;
        this.tree = new long[4 * n];
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

    public long query(int l, int r) {
        validateRange(l, r);
        return query(1, 0, n - 1, l, r);
    }

    private long query(int node, int start, int end, int l, int r) {
        if (r < start || l > end) return 0L;
        if (l <= start && end <= r) return tree[node];
        int mid = (start + end) >>> 1;
        return query(2 * node, start, mid, l, r)
                + query(2 * node + 1, mid + 1, end, l, r);
    }

    public void update(int idx, int val) {
        if (idx < 0 || idx >= n) throw new IndexOutOfBoundsException();
        update(1, 0, n - 1, idx, val);
    }

    private void update(int node, int start, int end, int idx, int val) {
        if (start == end) {
            tree[node] = val;
            return;
        }
        int mid = (start + end) >>> 1;
        if (idx <= mid) update(2 * node, start, mid, idx, val);
        else update(2 * node + 1, mid + 1, end, idx, val);
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }

    public int size() {
        return n;
    }

    private void validateRange(int l, int r) {
        if (l < 0 || r >= n || l > r) {
            throw new IndexOutOfBoundsException("[" + l + ", " + r + "]");
        }
    }
}
```
