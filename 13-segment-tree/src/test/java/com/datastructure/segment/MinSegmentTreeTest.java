package com.datastructure.segment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MinSegmentTree: 구간 최소")
class MinSegmentTreeTest {

    private static long naiveMin(long[] a, int from, int to) {
        long m = Long.MAX_VALUE;
        for (int i = from; i <= to; i++) {
            m = Math.min(m, a[i]);
        }
        return m;
    }

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("모든 구간이 맞다")
        void everyRange() {
            long[] a = {5, 2, 8, 1, 9, 3};
            MinSegmentTree t = new MinSegmentTree(a);
            for (int from = 0; from < a.length; from++) {
                for (int to = from; to < a.length; to++) {
                    assertEquals(naiveMin(a, from, to), t.query(from, to),
                            "구간 [" + from + ", " + to + "]");
                }
            }
        }

        @Test
        @DisplayName("갱신이 최소를 바꾼다")
        void updateChangesMin() {
            MinSegmentTree t = new MinSegmentTree(new long[]{5, 2, 8, 1});
            assertEquals(1, t.query(0, 3));
            t.update(3, 100);
            assertEquals(2, t.query(0, 3));
            t.update(0, -7);
            assertEquals(-7, t.query(0, 3));
        }
    }

    @Nested
    @DisplayName("항등원이 0 이면 안 되는 이유")
    class IdentityMatters {

        @Test
        @DisplayName("양수만 있는 배열에서도 0 은 답을 망친다")
        void zeroBreaksEvenWithPositives() {
            // 범위 밖 구간이 0 을 반환하면 최소가 0 이 되어버린다.
            // 배열에 0 이 없는데 0 이 나오면 그게 신호다.
            long[] a = {5, 7, 9, 11, 13, 17, 19, 23};
            MinSegmentTree t = new MinSegmentTree(a);
            for (int from = 0; from < a.length; from++) {
                for (int to = from; to < a.length; to++) {
                    long got = t.query(from, to);
                    assertEquals(naiveMin(a, from, to), got, "구간 [" + from + ", " + to + "]");
                }
            }
            assertEquals(7, t.query(1, 3), "0 이 나오면 항등원이 틀린 것이다");
            assertEquals(17, t.query(5, 5));
        }

        @Test
        @DisplayName("from > to 는 MAX_VALUE 다")
        void emptyRangeIsMaxValue() {
            MinSegmentTree t = new MinSegmentTree(new long[]{5, 7});
            assertEquals(Long.MAX_VALUE, t.query(1, 0),
                    "빈 구간의 최소는 '무한대'다. 0 이 아니다");
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("느린 구현과 계속 같다")
        void matchesNaive() {
            Random rnd = new Random(777L);
            for (int n : new int[]{1, 3, 8, 33, 100}) {
                long[] a = new long[n];
                for (int i = 0; i < n; i++) {
                    a[i] = rnd.nextInt(2001) - 1000;
                }
                MinSegmentTree t = new MinSegmentTree(a);
                for (int step = 0; step < 400; step++) {
                    if (rnd.nextBoolean()) {
                        int idx = rnd.nextInt(n);
                        long v = rnd.nextInt(2001) - 1000;
                        a[idx] = v;
                        t.update(idx, v);
                    } else {
                        int from = rnd.nextInt(n);
                        int to = from + rnd.nextInt(n - from);
                        assertEquals(naiveMin(a, from, to), t.query(from, to),
                                "n=" + n + " step=" + step);
                    }
                }
            }
        }
    }
}
