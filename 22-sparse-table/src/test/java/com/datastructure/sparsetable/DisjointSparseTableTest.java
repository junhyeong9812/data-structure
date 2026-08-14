package com.datastructure.sparsetable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("DisjointSparseTable: 겹치지 않게 덮으면 합도 된다")
class DisjointSparseTableTest {

    private static long naiveSum(long[] a, int from, int to) {
        long s = 0;
        for (int i = from; i <= to; i++) {
            s += a[i];
        }
        return s;
    }

    private static long[] deterministic(int n, long seed) {
        long[] a = new long[n];
        long x = seed;
        for (int i = 0; i < n; i++) {
            x = x * 6364136223846793005L + 1442695040888963407L;
            a[i] = Math.floorMod(x >>> 33, 200) - 100;
        }
        return a;
    }

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("손으로 확인한 값")
        void knownValues() {
            long[] a = {3, 1, 4, 1, 5, 9, 2, 6};
            DisjointSparseTable t = new DisjointSparseTable(a);
            assertEquals(31, t.query(0, 7));
            assertEquals(19, t.query(2, 5));
            assertEquals(1, t.query(3, 3));
            assertEquals(4, t.query(0, 1));
            assertEquals(8, t.size());
        }

        @Test
        @DisplayName("원소가 하나")
        void singleton() {
            DisjointSparseTable t = new DisjointSparseTable(new long[]{42});
            assertEquals(42, t.query(0, 0));
            assertEquals(42, t.get(0));
            assertEquals(0, t.levels(), "n=1 이면 층이 아예 없다. 조회는 값 그대로다");
            assertEquals(0, t.unitCount());
        }

        @Test
        @DisplayName("from > to 면 항등원")
        void reversedRange() {
            DisjointSparseTable t = new DisjointSparseTable(new long[]{1, 2, 3});
            assertEquals(0, t.query(2, 0));
        }

        @Test
        @DisplayName("잘못된 인자")
        void badArgs() {
            assertThrows(IllegalArgumentException.class,
                    () -> new DisjointSparseTable(new long[0]));
            assertThrows(IllegalArgumentException.class,
                    () -> new DisjointSparseTable(null));
            assertThrows(IllegalArgumentException.class,
                    () -> new DisjointSparseTable(new long[]{1}, 0L, null));
            DisjointSparseTable t = new DisjointSparseTable(new long[]{1, 2});
            assertThrows(IndexOutOfBoundsException.class, () -> t.query(0, 2));
            assertThrows(IndexOutOfBoundsException.class, () -> t.get(-1));
        }
    }

    @Nested
    @DisplayName("l == r 이 이 구조의 함정이다")
    class SameIndex {

        @Test
        @DisplayName("한 칸짜리 구간은 따로 처리해야 한다")
        void singleIndexRange() {
            // 조회는 **l 과 r 의 최상위 다른 비트**로 층을 고른다. l == r 이면 다른 비트가 없다.
            // l ^ r == 0 이고 31 - numberOfLeadingZeros(0) = -1 이다.
            // 따로 안 빼면 배열 인덱스 -1 로 터진다. **여기가 제일 틀리기 쉽다.**
            long[] a = {3, 1, 4, 1, 5, 9, 2, 6};
            DisjointSparseTable t = new DisjointSparseTable(a);
            for (int i = 0; i < a.length; i++) {
                assertEquals(a[i], t.query(i, i), "[" + i + ", " + i + "]");
            }
        }

        @Test
        @DisplayName("모든 크기에서 한 칸 구간이 맞다")
        void singleIndexAtEverySize() {
            for (int n = 1; n <= 40; n++) {
                long[] a = deterministic(n, 5L + n);
                DisjointSparseTable t = new DisjointSparseTable(a);
                for (int i = 0; i < n; i++) {
                    assertEquals(a[i], t.query(i, i), "n=" + n + " i=" + i);
                }
            }
        }
    }

    @Nested
    @DisplayName("전수 대조")
    class Exhaustive {

        @Test
        @DisplayName("n=1..64 의 모든 구간 합이 맞다")
        void everySumOfEverySize() {
            for (int n = 1; n <= 64; n++) {
                long[] a = deterministic(n, 3000L + n);
                DisjointSparseTable t = new DisjointSparseTable(a);
                for (int from = 0; from < n; from++) {
                    for (int to = from; to < n; to++) {
                        assertEquals(naiveSum(a, from, to), t.query(from, to),
                                "n=" + n + " [" + from + ", " + to + "]");
                    }
                }
            }
        }

        @Test
        @DisplayName("2의 거듭제곱이 아닌 크기에서 패딩이 새지 않는다")
        void paddingIsIdentity() {
            // 안쪽에서 배열을 2의 거듭제곱으로 늘려 쓴다. 늘린 칸은 항등원으로 채운다.
            //
            // **정직하게 적어둔다.** 그 채우기를 통째로 지워도(0 으로 남겨도)
            // 이 테스트를 포함해 90개가 전부 통과한다. 조회는 인덱스를 검사하므로
            // 누적 구간이 절대 n 을 넘지 않고, **패딩 칸을 읽는 경로 자체가 없기 때문이다.**
            // 그래도 항등원으로 채운다. 읽히지 않는 칸에 의미 없는 값을 두면
            // 나중에 경계를 한 칸이라도 넓힐 때 조용히 틀린다.
            // (11번 h2==0, 16번 뿌리 색, 18번 nextSetBit 범위 검사와 같은 자리다)
            for (int n : new int[]{3, 5, 6, 7, 9, 17, 33}) {
                long[] a = new long[n];
                for (int i = 0; i < n; i++) {
                    a[i] = 100 + i;
                }
                DisjointSparseTable min =
                        new DisjointSparseTable(a, Long.MAX_VALUE, Math::min);
                for (int from = 0; from < n; from++) {
                    for (int to = from; to < n; to++) {
                        assertEquals(100 + from, min.query(from, to), "n=" + n);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("아무 결합 연산이나 된다")
    class AnyMonoid {

        @Test
        @DisplayName("곱")
        void product() {
            long[] a = {3, 1, 4, 1, 5, 9, 2, 6};
            DisjointSparseTable t = new DisjointSparseTable(a, 1L, (x, y) -> x * y);
            assertEquals(6480, t.query(0, 7));
            assertEquals(180, t.query(2, 5));
            assertEquals(1, t.query(3, 3));
        }

        @Test
        @DisplayName("최소도 된다. 멱등성은 필요조건이 아니었다")
        void minAlsoWorks() {
            long[] a = {3, 1, 4, 1, 5, 9, 2, 6};
            DisjointSparseTable t = new DisjointSparseTable(a, Long.MAX_VALUE, Math::min);
            assertEquals(1, t.query(0, 7));
            assertEquals(1, t.query(2, 5));
            assertEquals(2, t.query(6, 7));
        }

        @Test
        @DisplayName("교환법칙이 없어도 된다. 결합법칙만 있으면 된다")
        void nonCommutative() {
            // f(x, y) = "y 가 0 이 아니면 y, 아니면 x" = 구간의 **마지막 0 아닌 값**.
            // 결합법칙이 있고 항등원은 0 인데 **교환법칙은 없다.** f(1,2)=2, f(2,1)=1.
            //
            // 이 테스트가 있는 이유: 누적 방향이나 combine 의 인자 순서를 뒤집는 실수는
            // 합, 최소, 곱 같은 교환법칙 있는 연산에서는 **우연히 맞아서** 안 잡힌다.
            long[] a = {5, 0, 7, 0, 0, 9, 0, 3};
            DisjointSparseTable t =
                    new DisjointSparseTable(a, 0L, (x, y) -> y != 0 ? y : x);
            for (int from = 0; from < a.length; from++) {
                for (int to = from; to < a.length; to++) {
                    long expected = 0;
                    for (int i = from; i <= to; i++) {
                        if (a[i] != 0) {
                            expected = a[i];
                        }
                    }
                    assertEquals(expected, t.query(from, to), "[" + from + ", " + to + "]");
                }
            }
            assertEquals(3, t.query(0, 7));
            assertEquals(9, t.query(1, 6));
            assertEquals(0, t.query(3, 4), "구간에 0 뿐이면 0");
        }

        @Test
        @DisplayName("최소는 두 구조가 같은 답을 낸다")
        void agreesWithSparseTable() {
            // 멱등한 연산은 **양쪽 다** 할 수 있다. 그때는 메모리가 작은 쪽을 고르면 된다.
            for (int n : new int[]{1, 2, 3, 17, 64, 100}) {
                long[] a = deterministic(n, 909L + n);
                MinSparseTable sparse = new MinSparseTable(a);
                DisjointSparseTable disjoint =
                        new DisjointSparseTable(a, Long.MAX_VALUE, Math::min);
                for (int from = 0; from < n; from++) {
                    for (int to = from; to < n; to++) {
                        assertEquals(sparse.query(from, to), disjoint.query(from, to),
                                "n=" + n + " [" + from + ", " + to + "]");
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("비용")
    class Cost {

        @Test
        @DisplayName("조회는 combine 한 번이다")
        void queryIsOneCombine() {
            long[] a = deterministic(1000, 77L);
            int[] calls = new int[1];
            DisjointSparseTable t = new DisjointSparseTable(a, 0L, (x, y) -> {
                calls[0]++;
                return x + y;
            });
            calls[0] = 0;                        // 전처리에서 부른 것은 빼고 센다
            t.query(0, 999);
            assertEquals(1, calls[0], "1000칸 구간도 combine 한 번");
            t.query(500, 500);
            assertEquals(1, calls[0], "l == r 은 값을 그대로 돌려주므로 combine 조차 없다");
            for (int i = 0; i < 100; i++) {
                t.query(i, 999 - i);
            }
            assertEquals(101, calls[0], "질의 100번이면 정확히 100번 더 늘어난다");
        }

        @Test
        @DisplayName("메모리는 레벨 수 곱하기 패딩한 너비")
        void memory() {
            // 희소 테이블과 같은 n log n 급이다. 다만 2의 거듭제곱으로 올림한 폭을 쓴다.
            int[][] expected = {{1, 0, 0}, {2, 1, 2}, {3, 2, 8}, {4, 2, 8}, {5, 3, 24},
                    {8, 3, 24}, {16, 4, 64}, {100, 7, 896}, {1000, 10, 10_240}};
            for (int[] row : expected) {
                DisjointSparseTable t = new DisjointSparseTable(new long[row[0]]);
                assertEquals(row[1], t.levels(), "n=" + row[0] + " 의 레벨 수");
                assertEquals(row[2], t.unitCount(), "n=" + row[0] + " 의 칸 수");
            }
        }

        @Test
        @Timeout(20)
        @DisplayName("20만 원소, 20만 질의")
        void largeScale() {
            int n = 200_000;
            long[] a = new long[n];
            for (int i = 0; i < n; i++) {
                a[i] = i % 1000;
            }
            DisjointSparseTable t = new DisjointSparseTable(a);
            long acc = 0;
            for (int i = 0; i < 200_000; i++) {
                int l = (i * 37) % n;
                int r = Math.min(n - 1, l + (i * 53) % 5000);
                acc += t.query(l, r);
            }
            // 파이썬 누적합으로 검산한 값이다.
            assertEquals(247_925_690_250L, acc, "합이 흔들리면 어딘가 어긋난 것이다");
        }
    }
}
