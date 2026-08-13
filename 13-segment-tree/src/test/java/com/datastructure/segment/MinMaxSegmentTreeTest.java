package com.datastructure.segment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MinMaxSegmentTree: 두 값을 같이 접기")
class MinMaxSegmentTreeTest {

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("모든 구간에서 최소와 최대가 맞다")
        void everyRange() {
            long[] a = {5, 2, 8, 1, 9, 3};
            MinMaxSegmentTree t = new MinMaxSegmentTree(a);
            Random unusedSeed = new Random(1L);
            for (int from = 0; from < a.length; from++) {
                for (int to = from; to < a.length; to++) {
                    long min = Long.MAX_VALUE;
                    long max = Long.MIN_VALUE;
                    for (int i = from; i <= to; i++) {
                        min = Math.min(min, a[i]);
                        max = Math.max(max, a[i]);
                    }
                    assertEquals(new MinMaxSegmentTree.MinMax(min, max), t.query(from, to),
                            "구간 [" + from + ", " + to + "]");
                }
            }
            assertEquals(1, t.query(0, 5).min());
            assertEquals(9, t.query(0, 5).max());
        }

        @Test
        @DisplayName("갱신이 양쪽에 반영된다")
        void updateAffectsBoth() {
            MinMaxSegmentTree t = new MinMaxSegmentTree(new long[]{5, 2, 8});
            assertEquals(new MinMaxSegmentTree.MinMax(2, 8), t.query(0, 2));
            t.update(1, 100);
            assertEquals(new MinMaxSegmentTree.MinMax(5, 100), t.query(0, 2));
            t.update(0, -50);
            assertEquals(new MinMaxSegmentTree.MinMax(-50, 100), t.query(0, 2));
        }
    }

    @Nested
    @DisplayName("항등원이 뒤집혀 있다")
    class InvertedIdentity {

        @Test
        @DisplayName("최소 자리에 최댓값, 최대 자리에 최솟값")
        void identityIsInverted() {
            // 어느 쪽과 merge 해도 영향을 안 주려면 이래야 한다.
            MinMaxSegmentTree.MinMax id = MinMaxSegmentTree.MinMax.IDENTITY;
            assertEquals(Long.MAX_VALUE, id.min());
            assertEquals(Long.MIN_VALUE, id.max());

            MinMaxSegmentTree t = new MinMaxSegmentTree(new long[]{7, 7, 7, 7});
            assertEquals(new MinMaxSegmentTree.MinMax(7, 7), t.query(1, 2),
                    "항등원이 (0,0) 이면 여기서 (0,7) 이 나온다");
        }
    }

    @Nested
    @DisplayName("하나로 두 트리를 대신한다")
    class OneInsteadOfTwo {

        @Test
        @DisplayName("MinSegmentTree 두 번 도는 것과 답이 같다")
        void agreesWithSeparateTrees() {
            Random rnd = new Random(31L);
            int n = 200;
            long[] a = new long[n];
            for (int i = 0; i < n; i++) {
                a[i] = rnd.nextInt(1000) - 500;
            }
            MinSegmentTree minTree = new MinSegmentTree(a);
            MinMaxSegmentTree both = new MinMaxSegmentTree(a);

            for (int step = 0; step < 500; step++) {
                int idx = rnd.nextInt(n);
                long v = rnd.nextInt(1000) - 500;
                minTree.update(idx, v);
                both.update(idx, v);

                int from = rnd.nextInt(n);
                int to = from + rnd.nextInt(n - from);
                assertEquals(minTree.query(from, to), both.query(from, to).min(),
                        "step " + step + " 구간 [" + from + ", " + to + "]");
            }
        }
    }
}
