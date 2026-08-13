package com.datastructure.segment;

public class MinMaxSegmentTree {

    public record MinMax(long min, long max) {
        static final MinMax IDENTITY = new MinMax(Long.MAX_VALUE, Long.MIN_VALUE);

        static MinMax of(long v) {
            return new MinMax(v, v);
        }

        MinMax merge(MinMax other) {
            return new MinMax(Math.min(min, other.min), Math.max(max, other.max));
        }
    }

    private final int n;
    private final MinMax[] tree;
    private final long[] values;

    public MinMaxSegmentTree(long[] initial) {
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        this.n = initial.length;
        this.values = initial.clone();
        this.tree = new MinMax[4 * n];
        build(1, 0, n - 1);
    }

    private void build(int node, int lo, int hi) {
        if (lo == hi) {
            tree[node] = MinMax.of(values[lo]);
            return;
        }
        int mid = (lo + hi) >>> 1;
        build(node * 2, lo, mid);
        build(node * 2 + 1, mid + 1, hi);
        tree[node] = tree[node * 2].merge(tree[node * 2 + 1]);
    }

    public void update(int index, long value) {
        requireIndex(index);
        values[index] = value;
        update(1, 0, n - 1, index, value);
    }

    private void update(int node, int lo, int hi, int index, long value) {
        if (lo == hi) {
            tree[node] = MinMax.of(value);
            return;
        }
        int mid = (lo + hi) >>> 1;
        if (index <= mid) {
            update(node * 2, lo, mid, index, value);
        } else {
            update(node * 2 + 1, mid + 1, hi, index, value);
        }
        tree[node] = tree[node * 2].merge(tree[node * 2 + 1]);
    }

    public MinMax query(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return MinMax.IDENTITY;
        }
        return query(1, 0, n - 1, from, to);
    }

    private MinMax query(int node, int lo, int hi, int from, int to) {
        if (to < lo || hi < from) {
            return MinMax.IDENTITY;
        }
        if (from <= lo && hi <= to) {
            return tree[node];
        }
        int mid = (lo + hi) >>> 1;
        return query(node * 2, lo, mid, from, to)
                .merge(query(node * 2 + 1, mid + 1, hi, from, to));
    }

    public int size() {
        return n;
    }

    private void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
