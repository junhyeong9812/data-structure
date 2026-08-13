package com.datastructure.segment;

public abstract class SegmentTree implements RangeQuery {

    protected final int n;
    protected final long[] tree;
    private final long[] values;

    protected SegmentTree(long[] initial) {
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        this.n = initial.length;
        this.values = initial.clone();
        this.tree = new long[4 * n];
        build(1, 0, n - 1);
    }

    protected abstract long combine(long a, long b);

    protected abstract long identity();

    private void build(int node, int lo, int hi) {
        if (lo == hi) {
            tree[node] = values[lo];
            return;
        }
        int mid = (lo + hi) >>> 1;
        build(node * 2, lo, mid);
        build(node * 2 + 1, mid + 1, hi);
        tree[node] = combine(tree[node * 2], tree[node * 2 + 1]);
    }

    @Override
    public void update(int index, long value) {
        requireIndex(index);
        values[index] = value;
        update(1, 0, n - 1, index, value);
    }

    private void update(int node, int lo, int hi, int index, long value) {
        if (lo == hi) {
            tree[node] = value;
            return;
        }
        int mid = (lo + hi) >>> 1;
        if (index <= mid) {
            update(node * 2, lo, mid, index, value);
        } else {
            update(node * 2 + 1, mid + 1, hi, index, value);
        }
        tree[node] = combine(tree[node * 2], tree[node * 2 + 1]);
    }

    @Override
    public long query(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return identity();
        }
        return query(1, 0, n - 1, from, to);
    }

    private long query(int node, int lo, int hi, int from, int to) {
        if (to < lo || hi < from) {
            return identity();
        }
        if (from <= lo && hi <= to) {
            return tree[node];
        }
        int mid = (lo + hi) >>> 1;
        return combine(query(node * 2, lo, mid, from, to),
                query(node * 2 + 1, mid + 1, hi, from, to));
    }

    @Override
    public long get(int index) {
        requireIndex(index);
        return values[index];
    }

    @Override
    public int size() {
        return n;
    }

    int treeSize() {
        return tree.length;
    }

    long node(int i) {
        return tree[i];
    }

    protected void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
