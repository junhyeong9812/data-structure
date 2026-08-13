package com.datastructure.segment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("LazySegmentTree: 미뤄두기")
class LazySegmentTreeTest {

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
        @DisplayName("구간 전체에 더한다")
        void rangeAdd() {
            LazySegmentTree t = new LazySegmentTree(new long[]{1, 2, 3, 4, 5});
            assertEquals(15, t.rangeSum(0, 4));
            t.rangeAdd(1, 3, 10);
            assertEquals(45, t.rangeSum(0, 4), "3개에 10씩 = 30 이 늘어야 한다");
            assertEquals(1, t.get(0));
            assertEquals(12, t.get(1));
            assertEquals(13, t.get(2));
            assertEquals(14, t.get(3));
            assertEquals(5, t.get(4));
        }

        @Test
        @DisplayName("구간 길이를 곱해야 한다")
        void multipliesByLength() {
            // hi - lo + 1 의 +1 을 빠뜨리는 것이 여기서 제일 흔한 실수다.
            LazySegmentTree t = new LazySegmentTree(new long[]{0, 0, 0, 0});
            t.rangeAdd(0, 3, 5);
            assertEquals(20, t.rangeSum(0, 3), "4개 x 5 = 20 이다. 15 가 나오면 +1 을 빠뜨린 것이다");
            assertEquals(10, t.rangeSum(0, 1));
            assertEquals(5, t.rangeSum(2, 2));
        }

        @Test
        @DisplayName("겹치는 갱신이 누적된다")
        void overlappingUpdatesAccumulate() {
            LazySegmentTree t = new LazySegmentTree(new long[]{0, 0, 0, 0, 0});
            t.rangeAdd(0, 2, 1);
            t.rangeAdd(1, 3, 10);
            t.rangeAdd(2, 4, 100);
            assertEquals(1, t.get(0));
            assertEquals(11, t.get(1));
            assertEquals(111, t.get(2));
            assertEquals(110, t.get(3));
            assertEquals(100, t.get(4));
            assertEquals(333, t.rangeSum(0, 4));
        }

        @Test
        @DisplayName("한 원소짜리 구간")
        void singleElementRange() {
            LazySegmentTree t = new LazySegmentTree(new long[]{7});
            t.rangeAdd(0, 0, 3);
            assertEquals(10, t.rangeSum(0, 0));
        }

        @Test
        @DisplayName("잘못된 인자")
        void badArgs() {
            assertThrows(IllegalArgumentException.class, () -> new LazySegmentTree(new long[0]));
            LazySegmentTree t = new LazySegmentTree(new long[]{1, 2});
            assertThrows(IndexOutOfBoundsException.class, () -> t.rangeSum(0, 2));
            assertThrows(IndexOutOfBoundsException.class, () -> t.rangeAdd(-1, 1, 5));
        }
    }

    @Nested
    @DisplayName("쪽지가 제때 내려간다")
    class Propagation {

        @Test
        @DisplayName("갱신 직후 뿌리에 쪽지가 남아 있다")
        void lazyStaysAtRoot() {
            // 이게 미루기의 증거다. 전체 구간 갱신이면 뿌리 하나만 건드리고 끝난다.
            LazySegmentTree t = new LazySegmentTree(new long[]{0, 0, 0, 0});
            t.rangeAdd(0, 3, 5);
            assertEquals(5, t.lazyAt(1), "뿌리에 쪽지가 붙어야 한다");
            assertEquals(0, t.lazyAt(2), "자식에게는 아직 안 내려갔다");
        }

        @Test
        @DisplayName("아래를 보러 가면 그때 내려간다")
        void pushesOnDescent() {
            LazySegmentTree t = new LazySegmentTree(new long[]{0, 0, 0, 0});
            t.rangeAdd(0, 3, 5);
            assertEquals(10, t.rangeSum(0, 1), "여기서 push 가 일어난다");
            assertEquals(0, t.lazyAt(1), "뿌리 쪽지는 자식에게 넘어가 지워진다");
            assertEquals(20, t.rangeSum(0, 3), "그래도 전체 답은 그대로다");
        }

        @Test
        @DisplayName("두 번 더하지 않는다")
        void doesNotDoubleApply() {
            // push 에서 tree[node] 를 또 고치면 여기서 값이 두 배가 된다.
            LazySegmentTree t = new LazySegmentTree(new long[]{0, 0, 0, 0});
            t.rangeAdd(0, 3, 5);
            for (int i = 0; i < 10; i++) {
                assertEquals(20, t.rangeSum(0, 3), "반복 조회에서 값이 변하면 안 된다");
                assertEquals(5, t.get(i % 4));
            }
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("느린 구현과 계속 같다")
        void matchesNaive() {
            Random rnd = new Random(13579L);
            for (int n : new int[]{1, 2, 5, 16, 33, 100}) {
                long[] a = new long[n];
                LazySegmentTree t = new LazySegmentTree(a);
                for (int step = 0; step < 800; step++) {
                    int from = rnd.nextInt(n);
                    int to = from + rnd.nextInt(n - from);
                    if (rnd.nextBoolean()) {
                        long delta = rnd.nextInt(41) - 20;
                        for (int i = from; i <= to; i++) {
                            a[i] += delta;
                        }
                        t.rangeAdd(from, to, delta);
                    } else {
                        assertEquals(naiveSum(a, from, to), t.rangeSum(from, to),
                                "n=" + n + " step=" + step + " [" + from + ", " + to + "]");
                    }
                }
                for (int i = 0; i < n; i++) {
                    assertEquals(a[i], t.get(i), "n=" + n + " 원소 " + i);
                }
            }
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(20)
        @DisplayName("구간 갱신이 구간 길이에 안 끌린다")
        void rangeUpdateIsLogarithmic() {
            // 미루지 않고 잎마다 고치면 갱신 하나가 20만 번이라 이 루프가 2x10^10 이 된다.
            int n = 200_000;
            LazySegmentTree t = new LazySegmentTree(new long[n]);
            for (int step = 0; step < 100_000; step++) {
                t.rangeAdd(0, n - 1, 1);
            }
            assertEquals((long) n * 100_000, t.rangeSum(0, n - 1));
            assertTrue(t.get(12_345) == 100_000);
        }
    }
}
