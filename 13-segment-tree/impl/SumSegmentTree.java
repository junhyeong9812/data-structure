package com.datastructure.segment;

public class SumSegmentTree extends SegmentTree {

    public SumSegmentTree(long[] initial) {
        super(initial);
    }

    @Override
    protected long combine(long a, long b) {
        return a + b;
    }

    @Override
    protected long identity() {
        return 0L;
    }
}
