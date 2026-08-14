package com.datastructure.sparsetable;

/**
 * 구간 최소를 O(1) 에 답한다. 뼈대는 SparseTable 이고 두 메서드만 다르다.
 *
 * min 은 멱등이다. min(x, x) = x. 그래서 두 창이 겹쳐도 답이 안 변한다.
 * 희소 테이블의 대표적인 쓰임이 바로 이것이라 이 자료구조를 아예
 * "RMQ(Range Minimum Query) 구조"라고 부르기도 한다.
 */
public class MinSparseTable extends SparseTable {

    public MinSparseTable(long[] initial) {
        super(initial);
    }

    @Override
    protected long combine(long a, long b) {
        // TODO 1: 둘 중 작은 것.
        throw new UnsupportedOperationException("TODO 1: combine");
    }

    @Override
    protected long identity() {
        // TODO 2: 어떤 값과 min 을 해도 그 값이 나오는 수.
        //
        // 13번 MinSegmentTree 와 답은 같다. 다만 **쓰이는 자리가 다르다.**
        // 세그먼트 트리에서는 범위 밖 노드를 메우느라 조회마다 쓰였다.
        // 여기서는 조회가 항상 두 창을 정확히 덮으므로 **빈 구간(from > to)에서만** 쓰인다.
        // 그래서 0 을 넣어도 대부분의 테스트가 통과한다. 그래도 0 은 답이 아니다.
        throw new UnsupportedOperationException("TODO 2: identity");
    }
}
