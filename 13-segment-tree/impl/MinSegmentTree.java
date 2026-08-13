package com.datastructure.segment;

public class MinSegmentTree extends SegmentTree {

    public MinSegmentTree(long[] initial) {
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
