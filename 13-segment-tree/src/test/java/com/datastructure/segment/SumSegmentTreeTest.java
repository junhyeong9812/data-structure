package com.datastructure.segment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("SumSegmentTree: 구간 합")
class SumSegmentTreeTest {

    private static long naiveSum(long[] a, int from, int to) {
        long s = 0;
        for (int i = from; i <= to; i++) {
            s += a[i];
        }
        return s;
    }

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("모든 구간이 맞다")
        void everyRange() {
            long[] a = {1, 2, 3, 4, 5, 6, 7, 8};
            SumSegmentTree t = new SumSegmentTree(a);
            for (int from = 0; from < a.length; from++) {
                for (int to = from; to < a.length; to++) {
                    assertEquals(naiveSum(a, from, to), t.query(from, to),
                            "구간 [" + from + ", " + to + "]");
                }
            }
            assertEquals(36, t.query(0, 7));
            assertEquals(1, t.query(0, 0));
            assertEquals(8, t.query(7, 7));
        }

        @Test
        @DisplayName("원소가 하나")
        void singleton() {
            SumSegmentTree t = new SumSegmentTree(new long[]{42});
            assertEquals(42, t.query(0, 0));
            assertEquals(1, t.size());
            t.update(0, 7);
            assertEquals(7, t.query(0, 0));
        }

        @Test
        @DisplayName("갱신이 위쪽까지 올라간다")
        void updatePropagates() {
            long[] a = {1, 2, 3, 4};
            SumSegmentTree t = new SumSegmentTree(a);
            assertEquals(10, t.query(0, 3));
            t.update(1, 20);
            assertEquals(28, t.query(0, 3), "잎만 고치고 위를 안 고치면 여기서 10 이 나온다");
            assertEquals(23, t.query(1, 2));
            assertEquals(20, t.query(1, 1));
            assertEquals(20, t.get(1));
        }

        @Test
        @DisplayName("from > to 면 항등원")
        void reversedRange() {
            SumSegmentTree t = new SumSegmentTree(new long[]{1, 2, 3});
            assertEquals(0, t.query(2, 0));
        }

        @Test
        @DisplayName("음수와 큰 수")
        void negativesAndLarge() {
            long[] a = {-5, Long.MAX_VALUE / 4, -3, 0};
            SumSegmentTree t = new SumSegmentTree(a);
            assertEquals(naiveSum(a, 0, 3), t.query(0, 3));
        }

        @Test
        @DisplayName("잘못된 인자")
        void badArgs() {
            assertThrows(IllegalArgumentException.class, () -> new SumSegmentTree(new long[0]));
            assertThrows(IllegalArgumentException.class, () -> new SumSegmentTree(null));
            SumSegmentTree t = new SumSegmentTree(new long[]{1, 2});
            assertThrows(IndexOutOfBoundsException.class, () -> t.query(0, 2));
            assertThrows(IndexOutOfBoundsException.class, () -> t.query(-1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> t.update(2, 5));
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("느린 구현과 계속 같다")
        void matchesNaive() {
            Random rnd = new Random(20260813L);
            for (int n : new int[]{1, 2, 3, 7, 8, 9, 64, 100}) {
                long[] a = new long[n];
                for (int i = 0; i < n; i++) {
                    a[i] = rnd.nextInt(2001) - 1000;
                }
                SumSegmentTree t = new SumSegmentTree(a);
                for (int step = 0; step < 500; step++) {
                    if (rnd.nextBoolean()) {
                        int idx = rnd.nextInt(n);
                        long v = rnd.nextInt(2001) - 1000;
                        a[idx] = v;
                        t.update(idx, v);
                    } else {
                        int from = rnd.nextInt(n);
                        int to = from + rnd.nextInt(n - from);
                        assertEquals(naiveSum(a, from, to), t.query(from, to),
                                "n=" + n + " step=" + step + " [" + from + ", " + to + "]");
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("구조")
    class Structure {

        @Test
        @DisplayName("배열이 4n 이다")
        void fourN() {
            // n 이 2의 거듭제곱이면 2n 이면 되지만, 아니면 한 층 더 깊어진다.
            // 정확히 계산하는 대신 넉넉히 잡는 것이 관행이다.
            assertEquals(4 * 5, new SumSegmentTree(new long[5]).treeSize());
            assertEquals(4 * 8, new SumSegmentTree(new long[8]).treeSize());
        }

        @Test
        @DisplayName("뿌리가 전체 합이다")
        void rootIsWhole() {
            SumSegmentTree t = new SumSegmentTree(new long[]{1, 2, 3, 4});
            assertEquals(10, t.node(1), "노드 1 이 뿌리다");
            assertEquals(3, t.node(2), "노드 2 는 왼쪽 절반 [0..1]");
            assertEquals(7, t.node(3), "노드 3 은 오른쪽 절반 [2..3]");
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(20)
        @DisplayName("조회와 갱신이 둘 다 로그다")
        void bothAreLogarithmic() {
            // 누적합이었다면 갱신 하나가 O(n) 이라 이 루프가 10^10 이 된다.
            int n = 200_000;
            long[] a = new long[n];
            for (int i = 0; i < n; i++) {
                a[i] = i % 100;
            }
            SumSegmentTree t = new SumSegmentTree(a);
            long acc = 0;
            for (int step = 0; step < 200_000; step++) {
                t.update(step % n, step % 50);
                acc += t.query(0, n - 1);
            }
            assertTrue(acc != 0);
        }
    }
}
