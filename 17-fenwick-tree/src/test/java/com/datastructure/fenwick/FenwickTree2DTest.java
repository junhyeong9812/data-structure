package com.datastructure.fenwick;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("FenwickTree2D: 직사각형 합")
class FenwickTree2DTest {

    private static long naive(long[][] a, int r1, int c1, int r2, int c2) {
        long s = 0;
        for (int r = r1; r <= r2; r++) {
            for (int c = c1; c <= c2; c++) {
                s += a[r][c];
            }
        }
        return s;
    }

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("한 칸씩 넣고 꺼낸다")
        void singleCells() {
            FenwickTree2D t = new FenwickTree2D(3, 4);
            assertEquals(3, t.rows());
            assertEquals(4, t.cols());
            t.add(1, 2, 5);
            assertEquals(5, t.get(1, 2));
            assertEquals(0, t.get(0, 0));
            assertEquals(5, t.rangeSum(0, 0, 2, 3));
        }

        @Test
        @DisplayName("모든 직사각형이 맞다")
        void everyRectangle() {
            int rows = 4;
            int cols = 5;
            long[][] a = new long[rows][cols];
            FenwickTree2D t = new FenwickTree2D(rows, cols);
            Random rnd = new Random(11L);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    a[r][c] = rnd.nextInt(21) - 10;
                    t.add(r, c, a[r][c]);
                }
            }
            for (int r1 = 0; r1 < rows; r1++) {
                for (int c1 = 0; c1 < cols; c1++) {
                    for (int r2 = r1; r2 < rows; r2++) {
                        for (int c2 = c1; c2 < cols; c2++) {
                            assertEquals(naive(a, r1, c1, r2, c2), t.rangeSum(r1, c1, r2, c2),
                                    "(" + r1 + "," + c1 + ")~(" + r2 + "," + c2 + ")");
                        }
                    }
                }
            }
        }

        @Test
        @DisplayName("뒤집힌 범위는 0")
        void reversed() {
            FenwickTree2D t = new FenwickTree2D(3, 3);
            t.add(1, 1, 7);
            assertEquals(0, t.rangeSum(2, 0, 0, 2));
            assertEquals(0, t.rangeSum(0, 2, 2, 0));
        }

        @Test
        @DisplayName("set")
        void setWorks() {
            FenwickTree2D t = new FenwickTree2D(3, 3);
            t.set(1, 1, 10);
            assertEquals(10, t.get(1, 1));
            t.set(1, 1, 3);
            assertEquals(3, t.get(1, 1));
            assertEquals(3, t.rangeSum(0, 0, 2, 2));
        }

        @Test
        @DisplayName("잘못된 인자")
        void badArgs() {
            assertThrows(IllegalArgumentException.class, () -> new FenwickTree2D(0, 3));
            assertThrows(IllegalArgumentException.class, () -> new FenwickTree2D(3, 0));
            FenwickTree2D t = new FenwickTree2D(2, 2);
            assertThrows(IndexOutOfBoundsException.class, () -> t.add(2, 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> t.get(0, 2));
        }
    }

    @Nested
    @DisplayName("포함-배제")
    class InclusionExclusion {

        @Test
        @DisplayName("네 조각으로 직사각형을 오려낸다")
        void fourPieces() {
            // rangeSum = P(r2,c2) - P(r1-1,c2) - P(r2,c1-1) + P(r1-1,c1-1)
            //
            // 왼쪽 위 귀퉁이를 두 번 빼게 되므로 한 번 되돌려준다.
            // 마지막 항의 부호를 빠뜨리는 것이 여기서 제일 흔한 실수다.
            FenwickTree2D t = new FenwickTree2D(3, 3);
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    t.add(r, c, r * 3 + c + 1);      // 1..9
                }
            }
            assertEquals(45, t.prefixSum(2, 2), "1+2+...+9");
            assertEquals(12, t.prefixSum(1, 1), "1+2+4+5");
            assertEquals(28, t.rangeSum(1, 1, 2, 2), "5+6+8+9");
            assertEquals(5, t.rangeSum(1, 1, 1, 1));
            assertEquals(0, t.prefixSum(-1, 2), "음수 경계는 0");
            assertEquals(0, t.prefixSum(2, -1));
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("느린 구현과 계속 같다")
        void matchesNaive() {
            Random rnd = new Random(4321L);
            for (int[] dim : new int[][]{{1, 1}, {1, 7}, {7, 1}, {8, 8}, {9, 5}}) {
                int rows = dim[0];
                int cols = dim[1];
                long[][] a = new long[rows][cols];
                FenwickTree2D t = new FenwickTree2D(rows, cols);
                for (int step = 0; step < 400; step++) {
                    if (rnd.nextBoolean()) {
                        int r = rnd.nextInt(rows);
                        int c = rnd.nextInt(cols);
                        long d = rnd.nextInt(41) - 20;
                        a[r][c] += d;
                        t.add(r, c, d);
                    } else {
                        int r1 = rnd.nextInt(rows);
                        int c1 = rnd.nextInt(cols);
                        int r2 = r1 + rnd.nextInt(rows - r1);
                        int c2 = c1 + rnd.nextInt(cols - c1);
                        assertEquals(naive(a, r1, c1, r2, c2), t.rangeSum(r1, c1, r2, c2),
                                rows + "x" + cols + " step=" + step);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(20)
        @DisplayName("1000 x 1000")
        void largeGrid() {
            int n = 1000;
            FenwickTree2D t = new FenwickTree2D(n, n);
            for (int i = 0; i < 200_000; i++) {
                t.add(i % n, (i * 7) % n, 1);
            }
            assertEquals(200_000, t.rangeSum(0, 0, n - 1, n - 1));
            for (int q = 0; q < 100_000; q++) {
                t.rangeSum(100, 100, 900, 900);
            }
        }
    }
}
