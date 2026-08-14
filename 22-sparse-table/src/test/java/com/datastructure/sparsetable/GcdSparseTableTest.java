package com.datastructure.sparsetable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("GcdSparseTable: 멱등이면서 최소도 최대도 아닌 것")
class GcdSparseTableTest extends SparseTableContractTest {

    @Override
    protected SparseTable create(long[] values) {
        return new GcdSparseTable(values);
    }

    @Override
    protected long naive(long[] values, int from, int to) {
        long g = 0;
        for (int i = from; i <= to; i++) {
            g = gcd(g, values[i]);
        }
        return g;
    }

    private static long gcd(long a, long b) {
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
    protected long identityValue() {
        return 0L;
    }

    @Nested
    @DisplayName("gcd 만의 것")
    class GcdOnly {

        @Test
        @DisplayName("손으로 확인한 값")
        void knownValues() {
            long[] a = {12, 18, 24, 9, 27, 6};
            GcdSparseTable t = new GcdSparseTable(a);
            assertEquals(3, t.query(0, 5));
            assertEquals(3, t.query(1, 3));
            assertEquals(3, t.query(2, 4));
            assertEquals(9, t.query(3, 3));
            assertEquals(6, t.query(0, 1), "gcd(12, 18) = 6");
        }

        @Test
        @DisplayName("여기서는 항등원이 0 이 맞다")
        void zeroIsTheRightIdentity() {
            // 최소 트리에서는 0 이 답을 망쳤다. **gcd 에서는 0 이 정확히 항등원이다.**
            // gcd(0, x) = x 이기 때문이다. "0 은 항상 위험하다"가 아니라
            // **그 연산의 항등원이 무엇이냐**가 기준이다.
            GcdSparseTable t = new GcdSparseTable(new long[]{12, 18});
            assertEquals(0, t.query(1, 0), "빈 구간의 gcd 는 0 이다");
            assertEquals(12, t.combine(12, 0));
            assertEquals(12, t.combine(0, 12));
        }

        @Test
        @DisplayName("서로소면 1 이다")
        void coprime() {
            GcdSparseTable t = new GcdSparseTable(new long[]{7, 11, 13, 14});
            assertEquals(1, t.query(0, 2));
            assertEquals(14, t.query(3, 3), "한 칸 구간은 그 값 자체다");
            assertEquals(1, t.query(1, 3));
        }

        @Test
        @DisplayName("음수는 절댓값으로 본다")
        void negatives() {
            long[] a = {-12, 18, -24, 9};
            GcdSparseTable t = new GcdSparseTable(a);
            for (int from = 0; from < a.length; from++) {
                for (int to = from; to < a.length; to++) {
                    assertEquals(naive(a, from, to), t.query(from, to),
                            "구간 [" + from + ", " + to + "]");
                }
            }
            assertEquals(3, t.query(0, 3));
        }

        @Test
        @DisplayName("gcd 는 멱등이지만 역연산이 없다")
        void idempotentButNotInvertible() {
            // **17번 펜윅 트리가 요구한 조건과 여기가 요구하는 조건은 다르다.**
            //   펜윅: 역연산이 있어야 한다 -> gcd 는 안 된다
            //   희소 테이블: 멱등이어야 한다 -> gcd 는 된다
            // gcd(a..r) 과 gcd(a..l-1) 을 알아도 gcd(l..r) 을 복원할 수 없다.
            // 12 와 18 의 gcd 는 6 이지만, 6 과 12 만으로 18 을 되찾을 방법이 없다.
            GcdSparseTable t = new GcdSparseTable(new long[]{12, 18, 24});
            assertEquals(6, t.combine(12, 18));
            assertEquals(6, t.combine(6, 6), "멱등이다");
            assertEquals(6, t.query(0, 2), "gcd(12, 18, 24) = 6");
        }
    }
}
