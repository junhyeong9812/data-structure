package com.datastructure.sparsetable;

/**
 * 구간 최대. MinSparseTable 과 두 줄만 다르다.
 *
 * 뼈대가 하나면 새 연산을 얹는 값이 두 줄이다. 13번에서 본 것과 같은 이야기다.
 */
public class MaxSparseTable extends SparseTable {

    public MaxSparseTable(long[] initial) {
        super(initial);
    }

    @Override
    protected long combine(long a, long b) {
        // TODO 1: 둘 중 큰 것.
        throw new UnsupportedOperationException("TODO 1: combine");
    }

    @Override
    protected long identity() {
        // TODO 2: 어떤 값과 max 를 해도 그 값이 나오는 수.
        //
        // 음수만 있는 배열에서 0 을 쓰면 최대가 0 이 된다. 최소 트리와 정확히 뒤집힌 함정이다.
        throw new UnsupportedOperationException("TODO 2: identity");
    }
}
