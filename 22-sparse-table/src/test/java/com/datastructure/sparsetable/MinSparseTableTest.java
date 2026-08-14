package com.datastructure.sparsetable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MinSparseTable: 구간 최소를 O(1) 로")
class MinSparseTableTest extends SparseTableContractTest {

    @Override
    protected SparseTable create(long[] values) {
        return new MinSparseTable(values);
    }

    @Override
    protected long naive(long[] values, int from, int to) {
        long m = Long.MAX_VALUE;
        for (int i = from; i <= to; i++) {
            m = Math.min(m, values[i]);
        }
        return m;
    }

    @Override
    protected long identityValue() {
        return Long.MAX_VALUE;
    }

    @Nested
    @DisplayName("최소만의 것")
    class MinOnly {

        @Test
        @DisplayName("음수가 섞여도 맞다")
        void negatives() {
            long[] a = {-5, 3, -100, 7, 0, -1};
            MinSparseTable t = new MinSparseTable(a);
            for (int from = 0; from < a.length; from++) {
                for (int to = from; to < a.length; to++) {
                    assertEquals(naive(a, from, to), t.query(from, to),
                            "구간 [" + from + ", " + to + "]");
                }
            }
            assertEquals(-100, t.query(0, 5));
            assertEquals(3, t.query(1, 1));
            assertEquals(-1, t.query(3, 5));
        }

        @Test
        @DisplayName("항등원이 0 이면 양수만 있어도 망가진다")
        void zeroIdentityBreaksEvenWithPositives() {
            // 13번 MinSegmentTree 에서 본 것과 같은 함정이다.
            // 다만 여기서는 항등원이 **빈 구간에서만** 쓰인다. 조회는 항상 두 창을 덮으므로
            // 범위 밖을 항등원으로 메우는 일이 없다. 그래서 티가 덜 나고 더 위험하다.
            long[] a = {5, 7, 9, 11, 13};
            MinSparseTable t = new MinSparseTable(a);
            assertEquals(Long.MAX_VALUE, t.query(3, 1), "빈 구간의 최소는 무한대다. 0 이 아니다");
            assertEquals(7, t.query(1, 3));
        }

        @Test
        @DisplayName("Long 의 양 끝 값")
        void extremes() {
            long[] a = {Long.MAX_VALUE, Long.MIN_VALUE, 0};
            MinSparseTable t = new MinSparseTable(a);
            assertEquals(Long.MIN_VALUE, t.query(0, 2));
            assertEquals(Long.MAX_VALUE, t.query(0, 0), "MAX_VALUE 는 항등원과 같지만 값이기도 하다");
            assertEquals(0, t.query(2, 2));
        }
    }
}
