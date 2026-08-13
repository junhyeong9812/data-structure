package com.datastructure.segment;

/**
 * 구간 합. 결합 함수와 항등원만 정하면 끝난다.
 *
 * 뼈대 코드를 한 줄도 다시 안 쓴다. 07번 MinHeap/MaxHeap 과 같은 구조다.
 */
public class SumSegmentTree extends SegmentTree {

    public SumSegmentTree(long[] initial) {
        super(initial);
    }

    @Override
    protected long combine(long a, long b) {
        // TODO 1: 합이다.
        throw new UnsupportedOperationException("TODO 1: combine");
    }

    @Override
    protected long identity() {
        // TODO 2: 어떤 값과 더해도 그 값이 그대로 나오는 수.
        throw new UnsupportedOperationException("TODO 2: identity");
    }
}
