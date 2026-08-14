package com.datastructure.sparsetable;

/**
 * 구간 gcd. 멱등이면서 최소도 최대도 아닌 예라서 여기 있다.
 *
 * gcd(x, x) = x 다. 그래서 겹쳐도 된다.
 * 그런데 gcd 는 역연산이 없다. gcd(a..r) 과 gcd(a..l-1) 을 알아도 gcd(l..r) 을 못 구한다.
 * 그래서 17번 펜윅 트리로는 못 하고 여기서는 된다. 조건이 서로 다른 것이다.
 *
 * | 연산 | 결합법칙(13번) | 역연산(17번) | 멱등성(22번) |
 * |---|---|---|---|
 * | 합 | 예 | 예 | 아니오 |
 * | 최소/최대 | 예 | 아니오 | 예 |
 * | gcd | 예 | 아니오 | 예 |
 * | 비트 AND/OR | 예 | 아니오 | 예 |
 * | XOR | 예 | 예 | 아니오 (x^x = 0) |
 */
public class GcdSparseTable extends SparseTable {

    public GcdSparseTable(long[] initial) {
        super(initial);
    }

    @Override
    protected long combine(long a, long b) {
        // TODO 1: 유클리드 호제법. y 가 0 이 될 때까지 (x, y) 를 (y, x % y) 로 바꾼다.
        //
        // 음수를 절댓값으로 바꿔두고 시작하라. gcd 는 부호를 따지지 않는다.
        // (Math.abs(Long.MIN_VALUE) 는 음수 그대로다. 여기서는 다루지 않는 경계다)
        throw new UnsupportedOperationException("TODO 1: combine");
    }

    @Override
    protected long identity() {
        // TODO 2: 어떤 값과 gcd 를 해도 그 값이 나오는 수.
        //
        // **여기서는 0 이 맞다.** gcd(0, x) = x 이기 때문이다.
        // 최소 트리에서 0 이 답을 망친 것과 대비해보라. "0 은 위험하다"가 규칙이 아니라
        // **그 연산의 항등원이 무엇이냐**가 규칙이다.
        throw new UnsupportedOperationException("TODO 2: identity");
    }
}
