package com.datastructure.sparsetable;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("SparseTableProblems")
class SparseTableProblemsTest {

    @Nested
    @DisplayName("문제 1: 슬라이딩 윈도우 최소")
    class SlidingWindowMin {

        @Test
        @DisplayName("크기 3 창")
        void windowOfThree() {
            // 파이썬 참조 구현으로 검산한 값이다.
            int[] a = {1, 3, -1, -3, 5, 3, 6, 7};
            assertArrayEquals(new int[]{-1, -3, -3, -3, 3, 3},
                    SparseTableProblems.slidingWindowMin(a, 3));
        }

        @Test
        @DisplayName("창이 1 이면 원본, 창이 n 이면 전체 최소")
        void degenerateWindows() {
            int[] a = {1, 3, -1, -3, 5, 3, 6, 7};
            assertArrayEquals(a, SparseTableProblems.slidingWindowMin(a, 1));
            assertArrayEquals(new int[]{-3}, SparseTableProblems.slidingWindowMin(a, 8));
        }

        @Test
        @DisplayName("결과 길이는 n - k + 1")
        void resultLength() {
            int[] a = new int[20];
            for (int i = 0; i < a.length; i++) {
                a[i] = (i * 7) % 13;
            }
            for (int k = 1; k <= a.length; k++) {
                assertEquals(a.length - k + 1,
                        SparseTableProblems.slidingWindowMin(a, k).length, "k=" + k);
            }
        }

        @Test
        @DisplayName("느린 구현과 모든 창 크기에서 같다")
        void matchesNaive() {
            for (int n = 1; n <= 40; n++) {
                int[] a = new int[n];
                long x = 12345L + n;
                for (int i = 0; i < n; i++) {
                    x = x * 6364136223846793005L + 1442695040888963407L;
                    a[i] = (int) Math.floorMod(x >>> 33, 200) - 100;
                }
                for (int k = 1; k <= n; k++) {
                    int[] got = SparseTableProblems.slidingWindowMin(a, k);
                    for (int i = 0; i < got.length; i++) {
                        int expected = Integer.MAX_VALUE;
                        for (int j = i; j < i + k; j++) {
                            expected = Math.min(expected, a[j]);
                        }
                        assertEquals(expected, got[i], "n=" + n + " k=" + k + " i=" + i);
                    }
                }
            }
        }

        @Test
        @DisplayName("04번 덱 풀이와 무엇이 다른가")
        void versusDeque() {
            // **04번은 같은 문제를 덱으로 O(n) 에 푼다.** 창이 한 방향으로만 움직이고
            // 창 크기가 하나로 고정일 때는 그쪽이 더 싸다. 전처리도 없다.
            //
            // 희소 테이블은 전처리 O(n log n) 을 먼저 문다. 대신 **창 크기가 몇이든**
            // 어떤 구간이든 그 뒤로는 O(1) 이다. 덱은 k 가 바뀌면 처음부터 다시 훑어야 한다.
            //
            // 아래가 그 차이다. 테이블 하나로 k = 1..n 을 전부 답한다.
            int n = 64;
            long[] a = new long[n];
            for (int i = 0; i < n; i++) {
                a[i] = (i * 37) % 91;
            }
            MinSparseTable table = new MinSparseTable(a);
            int answered = 0;
            for (int k = 1; k <= n; k++) {
                for (int i = 0; i + k <= n; i++) {
                    long expected = Long.MAX_VALUE;
                    for (int j = i; j < i + k; j++) {
                        expected = Math.min(expected, a[j]);
                    }
                    assertEquals(expected, table.query(i, i + k - 1), "k=" + k + " i=" + i);
                    answered++;
                }
            }
            assertEquals(2080, answered, "테이블 한 번 지어놓고 2080개 창을 전부 답했다");
        }

        @Test
        @DisplayName("잘못된 인자")
        void badArgs() {
            int[] a = {1, 2, 3};
            assertThrows(IllegalArgumentException.class,
                    () -> SparseTableProblems.slidingWindowMin(a, 0));
            assertThrows(IllegalArgumentException.class,
                    () -> SparseTableProblems.slidingWindowMin(a, 4));
            assertThrows(IllegalArgumentException.class,
                    () -> SparseTableProblems.slidingWindowMin(new int[0], 1));
            assertThrows(IllegalArgumentException.class,
                    () -> SparseTableProblems.slidingWindowMin(null, 1));
        }
    }

    @Nested
    @DisplayName("문제 2: 구간 gcd 질의")
    class RangeGcd {

        @Test
        @DisplayName("손으로 확인한 값")
        void knownValues() {
            int[] a = {12, 18, 24, 9, 27, 6, 15, 45};
            int[][] q = {{0, 7}, {0, 0}, {1, 2}, {3, 5}, {6, 7}, {2, 6}};
            assertArrayEquals(new long[]{3, 12, 6, 3, 15, 3},
                    SparseTableProblems.rangeGcdQueries(a, q));
        }

        @Test
        @DisplayName("질의가 없으면 빈 결과")
        void noQueries() {
            assertEquals(0, SparseTableProblems.rangeGcdQueries(new int[]{4, 6}, new int[0][]).length);
        }

        @Test
        @DisplayName("잘못된 인자")
        void badArgs() {
            int[] a = {4, 6};
            assertThrows(IllegalArgumentException.class,
                    () -> SparseTableProblems.rangeGcdQueries(a, new int[][]{{0, 2}}));
            assertThrows(IllegalArgumentException.class,
                    () -> SparseTableProblems.rangeGcdQueries(a, new int[][]{{1, 0}}));
            assertThrows(IllegalArgumentException.class,
                    () -> SparseTableProblems.rangeGcdQueries(a, new int[][]{{-1, 1}}));
            assertThrows(IllegalArgumentException.class,
                    () -> SparseTableProblems.rangeGcdQueries(a, new int[][]{{0}}));
            assertThrows(IllegalArgumentException.class,
                    () -> SparseTableProblems.rangeGcdQueries(a, new int[][]{null}));
            assertThrows(IllegalArgumentException.class,
                    () -> SparseTableProblems.rangeGcdQueries(a, null));
            assertThrows(IllegalArgumentException.class,
                    () -> SparseTableProblems.rangeGcdQueries(new int[0], new int[0][]));
        }

        @Test
        @DisplayName("느린 구현과 같다")
        void matchesNaive() {
            for (int n = 1; n <= 30; n++) {
                int[] a = new int[n];
                for (int i = 0; i < n; i++) {
                    a[i] = ((i * 41) % 97) + 1;
                }
                int count = 0;
                int[][] q = new int[n * (n + 1) / 2][];
                for (int l = 0; l < n; l++) {
                    for (int r = l; r < n; r++) {
                        q[count++] = new int[]{l, r};
                    }
                }
                long[] got = SparseTableProblems.rangeGcdQueries(a, q);
                for (int i = 0; i < q.length; i++) {
                    long g = 0;
                    for (int j = q[i][0]; j <= q[i][1]; j++) {
                        g = gcd(g, a[j]);
                    }
                    assertEquals(g, got[i], "n=" + n + " [" + q[i][0] + ", " + q[i][1] + "]");
                }
            }
        }

        @Test
        @Timeout(20)
        @DisplayName("20만 원소, 20만 질의")
        void largeScale() {
            int n = 200_000;
            int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = (int) ((i * 2654435761L) % 1000003L) + 1;
            }
            int[][] q = new int[200_000][];
            for (int i = 0; i < q.length; i++) {
                int l = (i * 37) % n;
                int r = Math.min(n - 1, l + (i * 53) % 5000);
                q[i] = new int[]{l, r};
            }
            long[] got = SparseTableProblems.rangeGcdQueries(a, q);
            long acc = 0;
            for (long v : got) {
                acc += v;
            }
            // 파이썬 참조 구현으로 검산한 값이다.
            assertEquals(30_410_799L, acc);
        }
    }

    @Nested
    @DisplayName("한계 측정: 세그먼트 트리와의 걸음 수 차이")
    class StepCount {

        /** 조회 때 combine 을 몇 번 부르는지 센다. 13번 SegmentTree 와 같은 구조다. */
        static final class CountingGcdSegmentTree {

            private final int n;
            private final long[] tree;
            private int combineCalls;

            CountingGcdSegmentTree(long[] values) {
                this.n = values.length;
                this.tree = new long[4 * n];
                build(values, 1, 0, n - 1);
            }

            private void build(long[] values, int node, int lo, int hi) {
                if (lo == hi) {
                    tree[node] = values[lo];
                    return;
                }
                int mid = (lo + hi) >>> 1;
                build(values, node * 2, lo, mid);
                build(values, node * 2 + 1, mid + 1, hi);
                tree[node] = gcd(tree[node * 2], tree[node * 2 + 1]);
            }

            long query(int from, int to) {
                return query(1, 0, n - 1, from, to);
            }

            private long query(int node, int lo, int hi, int from, int to) {
                if (to < lo || hi < from) {
                    return 0;
                }
                if (from <= lo && hi <= to) {
                    return tree[node];
                }
                int mid = (lo + hi) >>> 1;
                long a = query(node * 2, lo, mid, from, to);
                long b = query(node * 2 + 1, mid + 1, hi, from, to);
                combineCalls++;
                return gcd(a, b);
            }

            int combineCalls() {
                return combineCalls;
            }

            void resetCalls() {
                combineCalls = 0;
            }
        }

        /** 카운터에 `= 0` 을 붙이면 안 된다. 필드 초기화가 super() 뒤에 돌아 값을 지운다. */
        static final class CountingGcdSparseTable extends SparseTable {

            private int combineCalls;

            CountingGcdSparseTable(long[] initial) {
                super(initial);
            }

            @Override
            protected long combine(long a, long b) {
                combineCalls++;
                return gcd(a, b);
            }

            @Override
            protected long identity() {
                return 0L;
            }

            int combineCalls() {
                return combineCalls;
            }

            void resetCalls() {
                combineCalls = 0;
            }
        }

        @Test
        @DisplayName("질의 2000번에 세그먼트 트리는 37798걸음, 희소 테이블은 2000걸음")
        void sparseTableIsFlat() {
            // **O(q log n) 과 O(q) 의 차이가 여기서 눈에 보인다.**
            // 시간을 재지 않는다. 걸음 수를 센다(계약 4장).
            int n = 4096;
            int q = 2000;
            long[] a = new long[n];
            for (int i = 0; i < n; i++) {
                a[i] = (i * 2654435761L) % 1000003L + 1;
            }
            CountingGcdSegmentTree seg = new CountingGcdSegmentTree(a);
            CountingGcdSparseTable sparse = new CountingGcdSparseTable(a);
            seg.resetCalls();
            sparse.resetCalls();

            for (int i = 0; i < q; i++) {
                int l = (i * 37) % n;
                int r = Math.min(n - 1, l + (i * 53) % 1000);
                assertEquals(seg.query(l, r), sparse.query(l, r), "i=" + i + " 답이 달라지면 안 된다");
            }

            // 파이썬 참조 구현으로 검산한 값이다.
            assertEquals(37_798, seg.combineCalls(), "세그먼트 트리는 질의마다 log n 개 노드를 합친다");
            assertEquals(q, sparse.combineCalls(), "희소 테이블은 질의마다 정확히 한 번이다");
            assertTrue(seg.combineCalls() > 15 * sparse.combineCalls(),
                    "n=4096 에서 이미 18배가 넘는다. n 이 커지면 더 벌어진다");
        }

        @Test
        @DisplayName("대신 전처리에서 그만큼을 먼저 낸다")
        void preprocessingIsTheBill()  {
            // **공짜가 아니다.** 조회를 log n 배 싸게 하는 값을 전처리와 메모리로 낸다.
            // 질의가 적으면 세그먼트 트리가 이긴다. 그 손익분기가 이 자료구조의 선택 기준이다.
            int n = 4096;
            long[] a = new long[n];
            for (int i = 0; i < n; i++) {
                a[i] = (i * 2654435761L) % 1000003L + 1;
            }
            CountingGcdSparseTable sparse = new CountingGcdSparseTable(a);
            assertEquals(40_974, sparse.combineCalls(), "전처리에만 40974 걸음이 든다");
            assertEquals(13, sparse.levels());
            assertEquals(53_248, sparse.unitCount(), "칸도 13층 x 4096 = 53248개다");
        }
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
}
