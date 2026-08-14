package com.datastructure.sparsetable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MaxSparseTable: 뼈대는 같고 두 메서드만 다르다")
class MaxSparseTableTest extends SparseTableContractTest {

    @Override
    protected SparseTable create(long[] values) {
        return new MaxSparseTable(values);
    }

    @Override
    protected long naive(long[] values, int from, int to) {
        long m = Long.MIN_VALUE;
        for (int i = from; i <= to; i++) {
            m = Math.max(m, values[i]);
        }
        return m;
    }

    @Override
    protected long identityValue() {
        return Long.MIN_VALUE;
    }

    @Nested
    @DisplayName("최대만의 것")
    class MaxOnly {

        @Test
        @DisplayName("음수만 있으면 0 이 답이 될 수 없다")
        void allNegative() {
            long[] a = {-5, -3, -100, -7};
            MaxSparseTable t = new MaxSparseTable(a);
            assertEquals(-3, t.query(0, 3), "0 이 나오면 항등원이 새어 들어온 것이다");
            assertEquals(-3, t.query(0, 1));
            assertEquals(-7, t.query(3, 3));
        }

        @Test
        @DisplayName("최소와 최대가 서로 뒤집힌 관계다")
        void mirrorsMin() {
            long[] a = SparseTableContractTest.deterministic(50, 99L);
            long[] flipped = new long[a.length];
            for (int i = 0; i < a.length; i++) {
                flipped[i] = -a[i];
            }
            MaxSparseTable max = new MaxSparseTable(a);
            MinSparseTable min = new MinSparseTable(flipped);
            for (int from = 0; from < a.length; from++) {
                for (int to = from; to < a.length; to++) {
                    assertEquals(max.query(from, to), -min.query(from, to),
                            "구간 [" + from + ", " + to + "]");
                }
            }
        }
    }
}
