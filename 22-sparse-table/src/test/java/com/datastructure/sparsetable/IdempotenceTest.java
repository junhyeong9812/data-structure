package com.datastructure.sparsetable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 한계 문서화 테스트. 일부러 틀린 결과를 단언한다.
 *
 * 합으로 희소 테이블을 만들면 컴파일도 되고 예외도 안 나고 그냥 조용히 틀린 답을 준다.
 * 얼마나 틀리는지를 숫자로 못 박아 둔다. 09번(트라이의 접두사 카운트),
 * 13번(평균은 결합법칙이 없다)에 같은 방식의 테스트가 있다.
 */
@DisplayName("멱등하지 않은 연산을 넣으면 어떻게 되는가")
class IdempotenceTest {

    /** 합은 멱등이 아니다. f(x, x) = 2x 다. 이 클래스는 틀린 답을 내라고 만든 것이다. */
    static final class BrokenSumSparseTable extends SparseTable {

        BrokenSumSparseTable(long[] initial) {
            super(initial);
        }

        @Override
        protected long combine(long a, long b) {
            return a + b;
        }

        @Override
        protected long identity() {
            return 0L;
        }
    }

    private static long realSum(long[] a, int from, int to) {
        long s = 0;
        for (int i = from; i <= to; i++) {
            s += a[i];
        }
        return s;
    }

    @Nested
    @DisplayName("합으로 만들면")
    class SumIsWrong {

        @Test
        @DisplayName("길이 1 구간부터 이미 두 배다")
        void evenSingleElementIsDoubled() {
            // 길이 1 이면 k = 0 이고 두 창이 **완전히 겹친다.**
            //   왼쪽 창 [i, i], 오른쪽 창 [i - 1 + 1, i] = [i, i]
            // combine(a[i], a[i]) 가 min 이면 a[i], 합이면 2*a[i] 다.
            BrokenSumSparseTable t = new BrokenSumSparseTable(new long[]{7});
            assertEquals(14, t.query(0, 0), "7 을 물었는데 14 가 나온다");
            assertNotEquals(7, t.query(0, 0));
        }

        @Test
        @DisplayName("[1, 2, 3] 의 모든 구간이 어떻게 틀리는가")
        void everyRangeOfThree() {
            // 파이썬 참조 구현으로 검산한 값이다. 손으로 쓴 것이 아니다.
            long[] a = {1, 2, 3};
            BrokenSumSparseTable t = new BrokenSumSparseTable(a);
            assertEquals(2, t.query(0, 0), "진짜 1");
            assertEquals(4, t.query(1, 1), "진짜 2");
            assertEquals(6, t.query(2, 2), "진짜 3");
            assertEquals(6, t.query(0, 1), "진짜 3. 길이 2 는 두 창이 통째로 겹쳐 정확히 두 배다");
            assertEquals(10, t.query(1, 2), "진짜 5");
            assertEquals(8, t.query(0, 2), "진짜 6. 가운데 2 만 두 번 세어 +2");
        }

        @Test
        @DisplayName("틀리는 양이 정확히 '겹친 구간의 합'이다")
        void errorIsExactlyTheOverlap() {
            // **틀림에도 규칙이 있다.** 두 창 [l, l+2^k-1] 과 [r-2^k+1, r] 이 겹치는 자리를
            // 두 번 센다. 그 합만큼 커진다. 이 식이 모든 구간에서 성립한다.
            int[] log = SparseTable.buildLogTable(32);
            for (int n = 1; n <= 32; n++) {
                long[] a = new long[n];
                for (int i = 0; i < n; i++) {
                    a[i] = i + 1;
                }
                BrokenSumSparseTable t = new BrokenSumSparseTable(a);
                for (int l = 0; l < n; l++) {
                    for (int r = l; r < n; r++) {
                        int k = log[r - l + 1];
                        long overlap = realSum(a, r - (1 << k) + 1, l + (1 << k) - 1);
                        assertEquals(realSum(a, l, r) + overlap, t.query(l, r),
                                "n=" + n + " [" + l + ", " + r + "]");
                    }
                }
            }
        }

        @Test
        @DisplayName("겹침은 절대 없어지지 않는다")
        void overlapAlwaysExists() {
            // 길이 len 을 덮는 두 창의 길이는 2^k 이고 k = floor(log2(len)) 이다.
            // 겹치는 칸 수는 2^(k+1) - len 인데, 2^k <= len < 2^(k+1) 이므로 **항상 1 이상이다.**
            // 그래서 "합은 어떤 구간에서는 맞겠지"가 성립하지 않는다. 전부 틀린다.
            int[] log = SparseTable.buildLogTable(4096);
            for (int len = 1; len <= 4096; len++) {
                int k = log[len];
                int overlap = (1 << (k + 1)) - len;
                assertTrue(overlap >= 1, "len=" + len + " 에서 겹침이 " + overlap);
                assertTrue(overlap <= (1 << k), "겹침이 창 하나보다 클 수는 없다");
            }
            assertEquals(1, (1 << (log[3] + 1)) - 3, "길이 3 은 한 칸만 겹친다");
            assertEquals(4, (1 << (log[4] + 1)) - 4, "길이 4 는 두 창이 통째로 겹친다");
        }
    }

    @Nested
    @DisplayName("멱등하면 겹쳐도 된다")
    class IdempotentIsFine {

        @Test
        @DisplayName("같은 겹침인데 최소는 맞는다")
        void minSurvivesTheSameOverlap() {
            // **겹치는 것 자체는 문제가 아니다.** 두 번 세는 것이 문제다.
            // min 은 몇 번을 접든 같은 값이라 겹쳐도 아무 일이 없다.
            long[] a = {1, 2, 3};
            MinSparseTable min = new MinSparseTable(a);
            assertEquals(1, min.query(0, 0));
            assertEquals(1, min.query(0, 2));
            assertEquals(2, min.query(1, 2));

            BrokenSumSparseTable sum = new BrokenSumSparseTable(a);
            assertNotEquals(realSum(a, 0, 2), sum.query(0, 2), "같은 뼈대인데 합만 틀린다");
        }

        @Test
        @DisplayName("멱등한 연산 목록")
        void whichOperationsQualify() {
            MinSparseTable t = new MinSparseTable(new long[]{1});
            long x = 12;
            assertEquals(x, Math.min(x, x), "min");
            assertEquals(x, Math.max(x, x), "max");
            assertEquals(x, x & x, "비트 AND");
            assertEquals(x, x | x, "비트 OR");
            assertEquals(x, new GcdSparseTable(new long[]{1}).combine(x, x), "gcd");
            assertNotEquals(x, x + x, "합은 아니다");
            assertNotEquals(x, x * x, "곱도 아니다");
            assertNotEquals(x, x ^ x, "XOR 은 f(x,x)=0 이라 더 심하다");
            assertEquals(x, t.combine(x, x));
        }
    }

    @Nested
    @DisplayName("그래도 합을 하고 싶으면")
    class DisjointFixesIt {

        @Test
        @DisplayName("Disjoint Sparse Table 은 같은 배열에서 맞는 답을 낸다")
        void disjointGetsItRight() {
            // **겹치지 않게 덮으면 멱등성이 필요 없다.** 조회는 여전히 combine 한 번, O(1) 이다.
            long[] a = {1, 2, 3};
            BrokenSumSparseTable broken = new BrokenSumSparseTable(a);
            DisjointSparseTable fixed = new DisjointSparseTable(a);
            for (int l = 0; l < a.length; l++) {
                for (int r = l; r < a.length; r++) {
                    assertEquals(realSum(a, l, r), fixed.query(l, r), "[" + l + ", " + r + "]");
                }
            }
            assertEquals(8, broken.query(0, 2));
            assertEquals(6, fixed.query(0, 2), "같은 질문에 이쪽은 맞는 답을 준다");
        }
    }
}
