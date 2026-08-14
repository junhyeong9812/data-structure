package com.datastructure.sparsetable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 내부 구조와 한계를 숫자로 못 박는다.
 *
 * 계약만 보면 "조회가 맞는다"까지밖에 못 본다.
 * 메모리를 얼마나 쓰는지, 전처리에 몇 걸음이 드는지는 여기서만 드러난다.
 */
@DisplayName("SparseTable 구조와 한계")
class SparseTableStructureTest {

    /**
     * combine 을 몇 번 부르는지 센다. 시간을 재지 않고 걸음 수를 센다.
     *
     * 주의: 카운터에 `= 0` 을 붙이면 안 된다. 필드 초기화가 super() 뒤에 돌기 때문에
     * 생성자 안에서 센 값을 도로 지운다. (자바 초기화 순서의 고전적인 함정이다)
     */
    static final class CountingSparseTable extends SparseTable {

        private int combineCalls;

        CountingSparseTable(long[] initial) {
            super(initial);
        }

        @Override
        protected long combine(long a, long b) {
            combineCalls++;
            return Math.min(a, b);
        }

        @Override
        protected long identity() {
            return Long.MAX_VALUE;
        }

        int combineCalls() {
            return combineCalls;
        }

        void resetCalls() {
            combineCalls = 0;
        }
    }

    @Nested
    @DisplayName("로그 테이블")
    class LogTable {

        @Test
        @DisplayName("floor(log2(len)) 을 미리 채워둔다")
        void matchesBitTrick() {
            int n = 5000;
            int[] log = SparseTable.buildLogTable(n);
            assertEquals(n + 1, log.length, "길이 n 까지 물어볼 수 있어야 한다");
            assertEquals(0, log[1], "log2(1) = 0");
            for (int len = 1; len <= n; len++) {
                assertEquals(31 - Integer.numberOfLeadingZeros(len), log[len], "len=" + len);
            }
        }

        @Test
        @DisplayName("2의 거듭제곱 경계")
        void powersOfTwo() {
            int[] log = SparseTable.buildLogTable(1024);
            assertEquals(0, log[1]);
            assertEquals(1, log[2]);
            assertEquals(1, log[3], "3 은 아직 2^1 이다");
            assertEquals(2, log[4]);
            assertEquals(2, log[7]);
            assertEquals(3, log[8]);
            assertEquals(9, log[1023]);
            assertEquals(10, log[1024]);
        }

        @Test
        @DisplayName("Math.log 를 안 쓰는 이유는 오차가 아니라 속도다")
        void mathLogIsNotWrongHereJustSlow() {
            // **정직하게 적어둔다.** 흔히 "Math.log 는 부동소수점 오차로 틀린 층을 준다"고 하는데,
            // 이 JDK 에서 1..200000 을 전수 대조하면 **한 번도 안 틀린다.**
            // 명세가 보장하는 것은 1 ulp 이내라서 다른 구현에서 틀릴 여지가 있을 뿐이다.
            //
            // 안 쓰는 진짜 이유는 **비용**이다. 조회 한 번마다 log 를 두 번 부르면
            // O(1) 이라는 말이 무색해진다. 같은 기계에서 1천만 번 재보면 16배 차이가 났다.
            int[] log = SparseTable.buildLogTable(200_000);
            for (int len = 1; len <= 200_000; len++) {
                assertEquals(log[len], (int) (Math.log(len) / Math.log(2)),
                        "len=" + len + " 에서 드디어 어긋난다면 이 테스트를 고치지 말고 기록하라");
            }
        }
    }

    /** 13번 세그먼트 트리가 잡는 배열 크기. 비교 기준으로만 쓴다. */
    static int segmentTreeCells(int n) {
        return 4 * n;
    }

    @Nested
    @DisplayName("한계 1: 메모리는 세그먼트 트리보다 크다")
    class Memory {

        @Test
        @DisplayName("레벨 수는 floor(log2 n) + 1")
        void levelCount() {
            int[][] expected = {{1, 1}, {2, 2}, {3, 2}, {4, 3}, {5, 3}, {8, 4}, {16, 5}, {17, 5},
                    {100, 7}, {1000, 10}};
            for (int[] pair : expected) {
                assertEquals(pair[1], new MinSparseTable(new long[pair[0]]).levels(),
                        "n=" + pair[0]);
            }
        }

        @Test
        @DisplayName("칸 수가 4n 을 넘는 지점이 있다")
        void crossesFourN() {
            // **공짜가 아니다.** n 이 커질수록 세그먼트 트리보다 더 쓴다.
            //   n=8   : 32 대 32   같다
            //   n=16  : 80 대 64   여기서 역전된다
            //   n=1000: 10000 대 4000
            assertEquals(32, new MinSparseTable(new long[8]).unitCount());
            assertEquals(segmentTreeCells(8), new MinSparseTable(new long[8]).unitCount(),
                    "n=8 에서는 같다");

            assertEquals(80, new MinSparseTable(new long[16]).unitCount());
            assertTrue(new MinSparseTable(new long[16]).unitCount() > segmentTreeCells(16),
                    "n=16 부터 희소 테이블이 더 쓴다");

            assertEquals(700, new MinSparseTable(new long[100]).unitCount());
            assertEquals(10_000, new MinSparseTable(new long[1000]).unitCount());
            assertEquals(2.5, 10_000 / (double) segmentTreeCells(1000), 1e-9,
                    "n=1000 이면 13번 세그먼트 트리의 2.5배다");
        }

        @Test
        @DisplayName("0층은 원본 그대로다")
        void levelZeroIsTheInput() {
            long[] a = {5, 2, 8, 1, 9};
            MinSparseTable t = new MinSparseTable(a);
            for (int i = 0; i < a.length; i++) {
                assertEquals(a[i], t.node(0, i), "table[0][" + i + "]");
            }
            assertEquals(2, t.node(1, 0), "table[1][0] = min(5, 2)");
            assertEquals(2, t.node(1, 1), "table[1][1] = min(2, 8)");
            assertEquals(1, t.node(2, 0), "table[2][0] = min(5, 2, 8, 1)");
            assertEquals(1, t.node(2, 1), "table[2][1] = min(2, 8, 1, 9)");
        }
    }

    @Nested
    @DisplayName("한계 2: 갱신이 없다")
    class NoUpdate {

        @Test
        @DisplayName("전처리는 O(n log n) 걸음이다")
        void preprocessingSteps() {
            // 13번 세그먼트 트리의 build 는 n-1 번이면 끝난다. 여기는 층마다 n 번씩이다.
            int[][] expected = {{1, 0}, {2, 1}, {4, 4}, {8, 13}, {16, 38}, {100, 480}};
            for (int[] pair : expected) {
                CountingSparseTable t = new CountingSparseTable(new long[pair[0]]);
                assertEquals(pair[1], t.combineCalls(), "n=" + pair[0] + " 의 전처리 combine 호출 수");
            }
        }

        @Test
        @DisplayName("값 하나를 바꾸려면 전부 다시 지어야 한다")
        void oneChangeRebuildsEverything() {
            // **갱신이 불가능한 이유가 이것이다.**
            // a[7] 이 바뀌면 그 값을 포함하는 칸이 층마다 있다. 층 k 에는 최대 2^k 개다.
            // 다 합치면 결국 테이블 전체를 다시 계산하는 것과 같은 규모가 된다.
            //
            // 13번 세그먼트 트리는 잎에서 뿌리까지 한 줄, 즉 log2(16) = 4 번이면 끝났다.
            long[] a = new long[16];
            CountingSparseTable t = new CountingSparseTable(a);
            assertEquals(38, t.combineCalls(), "처음 짓는 데 38 번");

            t.resetCalls();
            a[7] = -1;
            CountingSparseTable rebuilt = new CountingSparseTable(a);
            assertEquals(38, rebuilt.combineCalls(), "한 칸 바꿔도 또 38 번. 세그먼트 트리는 4 번이다");
            assertEquals(-1, rebuilt.query(0, 15));
        }

        @Test
        @DisplayName("조회는 언제나 combine 한 번이다")
        void queryIsAlwaysOneStep() {
            // **이것이 O(1) 의 정확한 뜻이다.** 구간이 얼마나 넓든 combine 을 딱 한 번 부른다.
            CountingSparseTable t = new CountingSparseTable(new long[1000]);
            t.resetCalls();
            t.query(0, 999);
            assertEquals(1, t.combineCalls(), "1000칸 구간도 한 번");
            t.resetCalls();
            t.query(500, 500);
            assertEquals(1, t.combineCalls(), "한 칸 구간도 한 번");
            t.resetCalls();
            for (int i = 0; i < 100; i++) {
                t.query(i, 999 - i);
            }
            assertEquals(100, t.combineCalls(), "질의 100번이면 정확히 100번");
        }
    }
}
