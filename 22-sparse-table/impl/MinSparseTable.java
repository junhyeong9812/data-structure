package com.datastructure.sparsetable;

public class MinSparseTable extends SparseTable {

    public MinSparseTable(long[] initial) {
        super(initial);
    }

    @Override
    protected long combine(long a, long b) {
        return Math.min(a, b);
    }

    @Override
    protected long identity() {
        return Long.MAX_VALUE;
    }
}
