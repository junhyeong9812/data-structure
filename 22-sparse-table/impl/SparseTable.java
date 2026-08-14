package com.datastructure.sparsetable;

public abstract class SparseTable implements StaticRangeQuery {

    protected final int n;
    protected final int levels;
    protected final long[][] table;
    protected final int[] log;
    private final long[] values;

    protected SparseTable(long[] initial) {
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        this.n = initial.length;
        this.values = initial.clone();
        this.log = buildLogTable(n);
        this.levels = log[n] + 1;
        this.table = new long[levels][n];
        build();
    }

    protected abstract long combine(long a, long b);

    protected abstract long identity();

    static int[] buildLogTable(int n) {
        int[] table = new int[n + 1];
        for (int i = 2; i <= n; i++) {
            table[i] = table[i >> 1] + 1;
        }
        return table;
    }

    private void build() {
        System.arraycopy(values, 0, table[0], 0, n);
        for (int k = 1; k < levels; k++) {
            int half = 1 << (k - 1);
            for (int i = 0; i + (1 << k) <= n; i++) {
                table[k][i] = combine(table[k - 1][i], table[k - 1][i + half]);
            }
        }
    }

    @Override
    public long query(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return identity();
        }
        int k = log[to - from + 1];
        return combine(table[k][from], table[k][to - (1 << k) + 1]);
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

    public int levels() {
        return levels;
    }

    public int unitCount() {
        return levels * n;
    }

    long node(int level, int index) {
        return table[level][index];
    }

    int logOf(int length) {
        return log[length];
    }

    protected void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
