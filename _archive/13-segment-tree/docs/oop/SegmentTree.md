# oop/SegmentTree.java

제네릭 세그먼트 트리. `BinaryOperator<T>`로 합/최소/최대/GCD 등 임의 결합 연산 지원.

```java
package com.datastructure.segmenttree.oop;

import java.util.Objects;
import java.util.function.BinaryOperator;

public class SegmentTree<T> {
    private final Object[] tree;
    private final int n;
    private final BinaryOperator<T> combiner;
    private final T identity;

    public SegmentTree(T[] arr, BinaryOperator<T> combiner, T identity) {
        this.combiner = Objects.requireNonNull(combiner);
        this.identity = identity;
        this.n = arr.length;
        this.tree = new Object[4 * Math.max(1, n)];
        if (n > 0) build(arr, 1, 0, n - 1);
    }

    public static SegmentTree<Long> sumTree(long[] arr) {
        Long[] boxed = new Long[arr.length];
        for (int i = 0; i < arr.length; i++) boxed[i] = arr[i];
        return new SegmentTree<>(boxed, Long::sum, 0L);
    }

    public static SegmentTree<Integer> minTree(int[] arr) {
        Integer[] boxed = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) boxed[i] = arr[i];
        return new SegmentTree<>(boxed, Math::min, Integer.MAX_VALUE);
    }

    public static SegmentTree<Integer> maxTree(int[] arr) {
        Integer[] boxed = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) boxed[i] = arr[i];
        return new SegmentTree<>(boxed, Math::max, Integer.MIN_VALUE);
    }

    @SuppressWarnings("unchecked")
    private T at(int i) {
        return (T) tree[i];
    }

    private void set(int i, T v) {
        tree[i] = v;
    }

    private void build(T[] arr, int node, int start, int end) {
        if (start == end) {
            set(node, arr[start]);
            return;
        }
        int mid = (start + end) >>> 1;
        build(arr, 2 * node, start, mid);
        build(arr, 2 * node + 1, mid + 1, end);
        set(node, combiner.apply(at(2 * node), at(2 * node + 1)));
    }

    public T query(int l, int r) {
        if (l < 0 || r >= n || l > r) throw new IndexOutOfBoundsException();
        return query(1, 0, n - 1, l, r);
    }

    private T query(int node, int start, int end, int l, int r) {
        if (r < start || l > end) return identity;
        if (l <= start && end <= r) return at(node);
        int mid = (start + end) >>> 1;
        return combiner.apply(
                query(2 * node, start, mid, l, r),
                query(2 * node + 1, mid + 1, end, l, r));
    }

    public void update(int idx, T val) {
        if (idx < 0 || idx >= n) throw new IndexOutOfBoundsException();
        update(1, 0, n - 1, idx, val);
    }

    private void update(int node, int start, int end, int idx, T val) {
        if (start == end) {
            set(node, val);
            return;
        }
        int mid = (start + end) >>> 1;
        if (idx <= mid) update(2 * node, start, mid, idx, val);
        else update(2 * node + 1, mid + 1, end, idx, val);
        set(node, combiner.apply(at(2 * node), at(2 * node + 1)));
    }

    public int size() {
        return n;
    }
}
```
