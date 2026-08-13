package com.datastructure.segment;

public class LazySegmentTree {

    private final int n;
    private final long[] tree;
    private final long[] lazy;

    public LazySegmentTree(long[] initial) {
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        this.n = initial.length;
        this.tree = new long[4 * n];
        this.lazy = new long[4 * n];
        build(1, 0, n - 1, initial);
    }

    private void build(int node, int lo, int hi, long[] src) {
        if (lo == hi) {
            tree[node] = src[lo];
            return;
        }
        int mid = (lo + hi) >>> 1;
        build(node * 2, lo, mid, src);
        build(node * 2 + 1, mid + 1, hi, src);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    /** 미뤄둔 갱신을 자식에게 내린다. */
    private void push(int node, int lo, int hi) {
        if (lazy[node] == 0) {
            return;
        }
        int mid = (lo + hi) >>> 1;
        apply(node * 2, lo, mid, lazy[node]);
        apply(node * 2 + 1, mid + 1, hi, lazy[node]);
        lazy[node] = 0;
    }

    /** 이 노드가 덮는 구간 전체에 delta 를 더한 효과를 즉시 반영한다. */
    private void apply(int node, int lo, int hi, long delta) {
        tree[node] += delta * (hi - lo + 1);
        lazy[node] += delta;
    }

    public void rangeAdd(int from, int to, long delta) {
        requireRange(from, to);
        if (from > to) {
            return;
        }
        rangeAdd(1, 0, n - 1, from, to, delta);
    }

    private void rangeAdd(int node, int lo, int hi, int from, int to, long delta) {
        if (to < lo || hi < from) {
            return;
        }
        if (from <= lo && hi <= to) {
            apply(node, lo, hi, delta);
            return;
        }
        push(node, lo, hi);
        int mid = (lo + hi) >>> 1;
        rangeAdd(node * 2, lo, mid, from, to, delta);
        rangeAdd(node * 2 + 1, mid + 1, hi, from, to, delta);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    public long rangeSum(int from, int to) {
        requireRange(from, to);
        if (from > to) {
            return 0L;
        }
        return rangeSum(1, 0, n - 1, from, to);
    }

    private long rangeSum(int node, int lo, int hi, int from, int to) {
        if (to < lo || hi < from) {
            return 0L;
        }
        if (from <= lo && hi <= to) {
            return tree[node];
        }
        push(node, lo, hi);
        int mid = (lo + hi) >>> 1;
        return rangeSum(node * 2, lo, mid, from, to)
                + rangeSum(node * 2 + 1, mid + 1, hi, from, to);
    }

    public long get(int index) {
        return rangeSum(index, index);
    }

    public int size() {
        return n;
    }

    long lazyAt(int node) {
        return lazy[node];
    }

    private void requireRange(int from, int to) {
        if (from < 0 || from >= n || to < 0 || to >= n) {
            throw new IndexOutOfBoundsException(
                    "구간 [" + from + ", " + to + "] 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
