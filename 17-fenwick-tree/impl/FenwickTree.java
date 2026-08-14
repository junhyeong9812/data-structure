package com.datastructure.fenwick;

public class FenwickTree implements PrefixSumTree {

    private final int n;
    private final long[] tree;

    public FenwickTree(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("크기는 1 이상이어야 한다: " + size);
        }
        this.n = size;
        this.tree = new long[size + 1];
    }

    public FenwickTree(long[] initial) {
        this(initial == null || initial.length == 0 ? 1 : initial.length);
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        buildFrom(initial);
    }

    private void buildFrom(long[] values) {
        for (int i = 1; i <= n; i++) {
            tree[i] += values[i - 1];
            int parent = i + (i & -i);
            if (parent <= n) {
                tree[parent] += tree[i];
            }
        }
    }

    @Override
    public void add(int index, long delta) {
        requireIndex(index);
        for (int x = index + 1; x <= n; x += x & -x) {
            tree[x] += delta;
        }
    }

    @Override
    public long prefixSum(int index) {
        if (index < 0) {
            return 0L;
        }
        requireIndex(index);
        long sum = 0;
        for (int x = index + 1; x > 0; x -= x & -x) {
            sum += tree[x];
        }
        return sum;
    }

    @Override
    public long rangeSum(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return 0L;
        }
        return prefixSum(to) - prefixSum(from - 1);
    }

    @Override
    public long get(int index) {
        return rangeSum(index, index);
    }

    @Override
    public void set(int index, long value) {
        add(index, value - get(index));
    }

    /**
     * 누적합이 target 이상이 되는 가장 작은 인덱스. 없으면 -1.
     * 모든 값이 0 이상일 때만 뜻이 있다.
     */
    public int findPrefixIndex(long target) {
        int pos = 0;
        long remaining = target;
        for (int pw = Integer.highestOneBit(n); pw > 0; pw >>= 1) {
            if (pos + pw <= n && tree[pos + pw] < remaining) {
                pos += pw;
                remaining -= tree[pos];
            }
        }
        return pos >= n ? -1 : pos;
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

    private void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
