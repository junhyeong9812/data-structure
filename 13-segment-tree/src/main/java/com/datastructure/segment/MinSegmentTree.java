package com.datastructure.segment;

/**
 * 구간 최소. SumSegmentTree 와 **두 메서드만** 다르다.
 *
 * 항등원이 0 이 아니라는 점을 보라. 최소를 구하는데 0 을 섞으면 답이 0 이 된다.
 * "범위 밖은 없는 것처럼"을 만들려면 **그 연산의 항등원**이어야 한다.
 */
public class MinSegmentTree extends SegmentTree {

    public MinSegmentTree(long[] initial) {
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
        // 0 을 쓰면 음수만 있는 배열에서 틀린다... 가 아니라 **양수만 있어도 틀린다.**
        // 범위 밖 구간이 0 을 반환하면 최소가 0 이 되어버린다.
        throw new UnsupportedOperationException("TODO 2: identity");
    }
}
