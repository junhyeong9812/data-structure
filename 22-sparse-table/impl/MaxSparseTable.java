package com.datastructure.sparsetable;

public class MaxSparseTable extends SparseTable {

    public MaxSparseTable(long[] initial) {
        super(initial);
    }

    @Override
    protected long combine(long a, long b) {
        return Math.max(a, b);
    }

    @Override
    protected long identity() {
        return Long.MIN_VALUE;
    }
}
