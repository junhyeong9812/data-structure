package com.datastructure.segment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

public class GenericSegmentTree<T> {

    private final int n;
    private final Object[] tree;
    private final Object[] values;
    private final T identity;
    private final BinaryOperator<T> combine;

    public GenericSegmentTree(List<T> initial, T identity, BinaryOperator<T> combine) {
        if (initial == null || initial.isEmpty()) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        if (identity == null || combine == null) {
            throw new IllegalArgumentException("항등원과 결합 함수가 필요하다");
        }
        this.n = initial.size();
        this.identity = identity;
        this.combine = combine;
        this.values = initial.toArray();
        this.tree = new Object[4 * n];
        build(1, 0, n - 1);
    }

    @SuppressWarnings("unchecked")
    private T at(Object[] arr, int i) {
        return (T) arr[i];
    }

    private void build(int node, int lo, int hi) {
        if (lo == hi) {
            tree[node] = values[lo];
            return;
        }
        int mid = (lo + hi) >>> 1;
        build(node * 2, lo, mid);
        build(node * 2 + 1, mid + 1, hi);
        tree[node] = combine.apply(at(tree, node * 2), at(tree, node * 2 + 1));
    }

    public void update(int index, T value) {
        requireIndex(index);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        values[index] = value;
        update(1, 0, n - 1, index, value);
    }

    private void update(int node, int lo, int hi, int index, T value) {
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
        tree[node] = combine.apply(at(tree, node * 2), at(tree, node * 2 + 1));
    }

    public T query(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return identity;
        }
        return query(1, 0, n - 1, from, to);
    }

    private T query(int node, int lo, int hi, int from, int to) {
        if (to < lo || hi < from) {
            return identity;
        }
        if (from <= lo && hi <= to) {
            return at(tree, node);
        }
        int mid = (lo + hi) >>> 1;
        return combine.apply(query(node * 2, lo, mid, from, to),
                query(node * 2 + 1, mid + 1, hi, from, to));
    }

    public T get(int index) {
        requireIndex(index);
        return at(values, index);
    }

    public int size() {
        return n;
    }

    public List<T> toList() {
        List<T> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            out.add(at(values, i));
        }
        return out;
    }

    private void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
