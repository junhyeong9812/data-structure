package com.datastructure.sparsetable;

public class GcdSparseTable extends SparseTable {

    public GcdSparseTable(long[] initial) {
        super(initial);
    }

    @Override
    protected long combine(long a, long b) {
        long x = Math.abs(a);
        long y = Math.abs(b);
        while (y != 0) {
            long t = x % y;
            x = y;
            y = t;
        }
        return x;
    }

    @Override
    protected long identity() {
        return 0L;
    }
}
