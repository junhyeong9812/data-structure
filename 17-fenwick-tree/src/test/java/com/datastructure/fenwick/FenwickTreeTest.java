package com.datastructure.fenwick;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("FenwickTree")
class FenwickTreeTest {

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
        @DisplayName("처음엔 전부 0 이다")
        void startsAtZero() {
            FenwickTree t = new FenwickTree(8);
            assertEquals(8, t.size());
            for (int i = 0; i < 8; i++) {
                assertEquals(0, t.get(i));
                assertEquals(0, t.prefixSum(i));
            }
            assertEquals(0, t.rangeSum(0, 7));
        }

        @Test
        @DisplayName("더하고 누적합을 본다")
        void addAndPrefix() {
            FenwickTree t = new FenwickTree(5);
            t.add(0, 1);
            t.add(1, 2);
            t.add(2, 3);
            t.add(3, 4);
            t.add(4, 5);
            assertEquals(1, t.prefixSum(0));
            assertEquals(3, t.prefixSum(1));
            assertEquals(6, t.prefixSum(2));
            assertEquals(15, t.prefixSum(4));
            assertEquals(0, t.prefixSum(-1), "빈 접두는 0 이다");
        }

        @Test
        @DisplayName("모든 구간이 맞다")
        void everyRange() {
            long[] a = {3, -1, 4, 1, -5, 9, 2, 6};
            FenwickTree t = new FenwickTree(a);
            for (int from = 0; from < a.length; from++) {
                for (int to = from; to < a.length; to++) {
                    assertEquals(naiveSum(a, from, to), t.rangeSum(from, to),
                            "구간 [" + from + ", " + to + "]");
                }
            }
        }

        @Test
        @DisplayName("set 은 차이만큼 더한다")
        void setWorks() {
            FenwickTree t = new FenwickTree(new long[]{1, 2, 3, 4});
            assertEquals(10, t.rangeSum(0, 3));
            t.set(1, 20);
            assertEquals(20, t.get(1));
            assertEquals(28, t.rangeSum(0, 3));
            t.set(1, 0);
            assertEquals(8, t.rangeSum(0, 3));
        }

        @Test
        @DisplayName("뒤집힌 범위는 0")
        void reversedRange() {
            FenwickTree t = new FenwickTree(new long[]{1, 2, 3});
            assertEquals(0, t.rangeSum(2, 0));
        }

        @Test
        @DisplayName("원소가 하나")
        void singleton() {
            FenwickTree t = new FenwickTree(new long[]{42});
            assertEquals(42, t.get(0));
            assertEquals(42, t.prefixSum(0));
            assertEquals(42, t.rangeSum(0, 0));
        }

        @Test
        @DisplayName("잘못된 인자")
        void badArgs() {
            assertThrows(IllegalArgumentException.class, () -> new FenwickTree(0));
            assertThrows(IllegalArgumentException.class, () -> new FenwickTree(new long[0]));
            FenwickTree t = new FenwickTree(3);
            assertThrows(IndexOutOfBoundsException.class, () -> t.add(3, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> t.prefixSum(3));
            assertThrows(IndexOutOfBoundsException.class, () -> t.rangeSum(0, 5));
        }
    }

    @Nested
    @DisplayName("i & -i")
    class LowestOneBit {

        @Test
        @DisplayName("각 칸이 담당하는 구간 길이가 최하위 1비트다")
        void nodeCoversLowestOneBit() {
            // tree[i] 는 i 에서 왼쪽으로 (i & -i) 개를 덮는다.
            //   tree[1] -> [1..1]     1 & -1 = 1
            //   tree[2] -> [1..2]     2 & -2 = 2
            //   tree[4] -> [1..4]     4 & -4 = 4
            //   tree[6] -> [5..6]     6 & -6 = 2
            FenwickTree t = new FenwickTree(new long[]{1, 2, 3, 4, 5, 6, 7, 8});
            assertEquals(1, t.node(1), "[1..1] = 1");
            assertEquals(3, t.node(2), "[1..2] = 1+2");
            assertEquals(3, t.node(3), "[3..3] = 3");
            assertEquals(10, t.node(4), "[1..4] = 1+2+3+4");
            assertEquals(11, t.node(6), "[5..6] = 5+6");
            assertEquals(36, t.node(8), "[1..8] = 전체");
        }

        @Test
        @DisplayName("O(n) 으로 만든 것과 하나씩 더한 것이 같다")
        void buildMatchesIncremental() {
            Random rnd = new Random(7L);
            for (int n : new int[]{1, 2, 3, 7, 8, 9, 64, 100}) {
                long[] a = new long[n];
                for (int i = 0; i < n; i++) {
                    a[i] = rnd.nextInt(200) - 100;
                }
                FenwickTree built = new FenwickTree(a);
                FenwickTree incremental = new FenwickTree(n);
                for (int i = 0; i < n; i++) {
                    incremental.add(i, a[i]);
                }
                for (int i = 1; i <= n; i++) {
                    assertEquals(incremental.node(i), built.node(i), "n=" + n + " 칸 " + i);
                }
            }
        }
    }

    @Nested
    @DisplayName("메모리")
    class Memory {

        @Test
        @DisplayName("칸이 n+1 개뿐이다")
        void nPlusOne() {
            // 13번 세그먼트 트리는 같은 일에 4n 칸을 썼다.
            for (int n : new int[]{1, 5, 100, 100_000}) {
                assertEquals(n + 1, new FenwickTree(n).treeSize(),
                        "세그먼트 트리라면 " + (4 * n) + " 칸이다");
            }
        }
    }

    @Nested
    @DisplayName("누적합으로 위치 찾기")
    class FindByPrefix {

        @Test
        @DisplayName("누적합이 target 이상이 되는 첫 자리")
        void findsIndex() {
            FenwickTree t = new FenwickTree(new long[]{1, 2, 3});
            assertEquals(0, t.findPrefixIndex(1));
            assertEquals(1, t.findPrefixIndex(2));
            assertEquals(1, t.findPrefixIndex(3));
            assertEquals(2, t.findPrefixIndex(4));
            assertEquals(2, t.findPrefixIndex(6));
            assertEquals(-1, t.findPrefixIndex(7), "전체 합보다 크면 없다");
        }

        @Test
        @DisplayName("빈도표에서 k 번째 원소 찾기")
        void kthElement() {
            // 이게 실무 쓰임새다. 값 v 가 몇 개 있는지를 셈해두고
            // "정렬했을 때 k 번째는 무엇인가"를 O(log n) 에 답한다.
            // 훑어서 세면 O(n) 이다.
            FenwickTree counts = new FenwickTree(10);
            counts.add(2, 3);      // 값 2 가 3개
            counts.add(5, 2);      // 값 5 가 2개
            counts.add(9, 1);      // 값 9 가 1개

            assertEquals(2, counts.findPrefixIndex(1), "1번째는 2");
            assertEquals(2, counts.findPrefixIndex(3), "3번째는 2");
            assertEquals(5, counts.findPrefixIndex(4), "4번째는 5");
            assertEquals(5, counts.findPrefixIndex(5), "5번째는 5");
            assertEquals(9, counts.findPrefixIndex(6), "6번째는 9");
            assertEquals(-1, counts.findPrefixIndex(7));
        }

        @Test
        @DisplayName("느린 방법과 대조")
        void matchesNaive() {
            Random rnd = new Random(1234L);
            int n = 200;
            long[] a = new long[n];
            FenwickTree t = new FenwickTree(n);
            for (int i = 0; i < n; i++) {
                a[i] = rnd.nextInt(5);
                t.add(i, a[i]);
            }
            long total = naiveSum(a, 0, n - 1);
            for (long target = 1; target <= total + 2; target++) {
                int expected = -1;
                long acc = 0;
                for (int i = 0; i < n; i++) {
                    acc += a[i];
                    if (acc >= target) {
                        expected = i;
                        break;
                    }
                }
                assertEquals(expected, t.findPrefixIndex(target), "target=" + target);
            }
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("느린 구현과 계속 같다")
        void matchesNaive() {
            Random rnd = new Random(20260813L);
            for (int n : new int[]{1, 2, 3, 7, 8, 9, 33, 128, 200}) {
                long[] a = new long[n];
                FenwickTree t = new FenwickTree(n);
                for (int step = 0; step < 800; step++) {
                    if (rnd.nextBoolean()) {
                        int idx = rnd.nextInt(n);
                        long d = rnd.nextInt(201) - 100;
                        a[idx] += d;
                        t.add(idx, d);
                    } else {
                        int from = rnd.nextInt(n);
                        int to = from + rnd.nextInt(n - from);
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
        @DisplayName("100만 개")
        void million() {
            int n = 1_000_000;
            FenwickTree t = new FenwickTree(n);
            for (int i = 0; i < n; i++) {
                t.add(i, 1);
            }
            assertEquals(n, t.rangeSum(0, n - 1));
            long acc = 0;
            for (int q = 0; q < 1_000_000; q++) {
                acc += t.prefixSum(q % n);
            }
            assertTrue(acc > 0);
        }
    }
}
