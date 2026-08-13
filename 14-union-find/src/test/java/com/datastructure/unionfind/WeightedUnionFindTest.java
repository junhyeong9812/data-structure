package com.datastructure.unionfind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("WeightedUnionFind: 차이까지 관리하는")
class WeightedUnionFindTest {

    @Nested
    @DisplayName("차이를 유도한다")
    class Deduction {

        @Test
        @DisplayName("직접 말한 적 없는 차이를 안다")
        void transitiveDifference() {
            WeightedUnionFind uf = new WeightedUnionFind(5);
            assertTrue(uf.union(0, 1, 3));    // value(1) - value(0) = 3
            assertTrue(uf.union(1, 2, 5));    // value(2) - value(1) = 5
            assertEquals(8, uf.diff(0, 2), "3 + 5 = 8 이다. 말한 적 없는데 안다");
            assertEquals(-8, uf.diff(2, 0), "반대 방향은 부호가 뒤집힌다");
            assertEquals(0, uf.diff(0, 0));
        }

        @Test
        @DisplayName("긴 사슬에서도 맞다")
        void longChain() {
            WeightedUnionFind uf = new WeightedUnionFind(100);
            for (int i = 1; i < 100; i++) {
                assertTrue(uf.union(i - 1, i, 2));
            }
            assertEquals(198, uf.diff(0, 99), "2 씩 99번");
            assertEquals(20, uf.diff(30, 40));
            assertEquals(1, uf.componentCount());
        }

        @Test
        @DisplayName("음수 차이도 된다")
        void negativeWeights() {
            WeightedUnionFind uf = new WeightedUnionFind(4);
            uf.union(0, 1, -7);
            uf.union(1, 2, 3);
            assertEquals(-4, uf.diff(0, 2));
            assertEquals(4, uf.diff(2, 0));
        }

        @Test
        @DisplayName("연결 안 된 것은 못 답한다")
        void unconnectedThrows() {
            WeightedUnionFind uf = new WeightedUnionFind(4);
            uf.union(0, 1, 3);
            assertFalse(uf.connected(0, 2));
            assertThrows(IllegalStateException.class, () -> uf.diff(0, 2));
        }
    }

    @Nested
    @DisplayName("모순을 잡아낸다")
    class Contradiction {

        @Test
        @DisplayName("아는 것과 어긋나면 false")
        void detectsInconsistency() {
            WeightedUnionFind uf = new WeightedUnionFind(5);
            uf.union(0, 1, 3);
            uf.union(1, 2, 5);
            assertFalse(uf.union(0, 2, 2), "8 이어야 하는데 2 라고 했다");
            assertTrue(uf.union(0, 2, 8), "맞는 값이면 true. 아무것도 안 바뀐다");
            assertEquals(8, uf.diff(0, 2), "거부한 뒤에도 상태가 멀쩡해야 한다");
        }

        @Test
        @DisplayName("모순된 선언은 상태를 안 바꾼다")
        void rejectedUnionIsClean() {
            WeightedUnionFind uf = new WeightedUnionFind(5);
            uf.union(0, 1, 10);
            int before = uf.componentCount();
            assertFalse(uf.union(1, 0, 99));
            assertEquals(before, uf.componentCount());
            assertEquals(10, uf.diff(0, 1));
        }
    }

    @Nested
    @DisplayName("경로 압축이 weight 를 망치지 않는다")
    class CompressionKeepsWeights {

        @Test
        @DisplayName("find 를 여러 번 해도 답이 같다")
        void repeatedFindIsStable() {
            // 재귀 호출 **전에** weight 를 더하면 여기서 값이 어긋난다.
            WeightedUnionFind uf = new WeightedUnionFind(50);
            for (int i = 1; i < 50; i++) {
                uf.union(i - 1, i, i);
            }
            long expected = uf.diff(0, 49);
            for (int round = 0; round < 100; round++) {
                for (int i = 0; i < 50; i++) {
                    uf.find(i);
                }
                assertEquals(expected, uf.diff(0, 49),
                        "압축을 반복했더니 답이 " + uf.diff(0, 49) + " 로 변했다");
            }
            assertEquals(49 * 50 / 2, expected, "1+2+...+49");
        }

        @Test
        @DisplayName("순서를 섞어 합쳐도 맞다")
        void arbitraryMergeOrder() {
            // 크기로 붙이기 때문에 어느 쪽이 밑으로 갈지 모른다.
            // 부호를 한 쪽만 처리하면 여기서 걸린다.
            WeightedUnionFind uf = new WeightedUnionFind(8);
            uf.union(0, 1, 1);
            uf.union(2, 3, 1);
            uf.union(4, 5, 1);
            uf.union(6, 7, 1);
            uf.union(1, 2, 10);      // 두 2짜리 나무를 합친다
            uf.union(5, 6, 10);
            uf.union(3, 4, 100);     // 두 4짜리 나무를 합친다

            assertEquals(1, uf.diff(0, 1));
            assertEquals(11, uf.diff(0, 2));
            assertEquals(12, uf.diff(0, 3));
            assertEquals(112, uf.diff(0, 4));
            assertEquals(113, uf.diff(0, 5));
            assertEquals(123, uf.diff(0, 6));
            assertEquals(124, uf.diff(0, 7));
            assertEquals(1, uf.componentCount());
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("실제 값을 정해두고 맞춰본다")
        void matchesGroundTruth() {
            Random rnd = new Random(8888L);
            int n = 200;
            long[] truth = new long[n];
            for (int i = 0; i < n; i++) {
                truth[i] = rnd.nextInt(2001) - 1000;
            }
            WeightedUnionFind uf = new WeightedUnionFind(n);

            for (int step = 0; step < 4000; step++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                if (rnd.nextBoolean()) {
                    assertTrue(uf.union(a, b, truth[b] - truth[a]),
                            "진짜 값에서 온 선언은 절대 모순이 아니다 (step " + step + ")");
                } else if (uf.connected(a, b)) {
                    assertEquals(truth[b] - truth[a], uf.diff(a, b), "step " + step);
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j += 17) {
                    if (uf.connected(i, j)) {
                        assertEquals(truth[j] - truth[i], uf.diff(i, j), i + " -> " + j);
                    }
                }
            }
        }

        @Test
        @DisplayName("틀린 값은 반드시 거부한다")
        void rejectsWrongValues() {
            Random rnd = new Random(99L);
            int n = 100;
            long[] truth = new long[n];
            for (int i = 0; i < n; i++) {
                truth[i] = rnd.nextInt(100);
            }
            WeightedUnionFind uf = new WeightedUnionFind(n);
            for (int i = 1; i < n; i++) {
                uf.union(i - 1, i, truth[i] - truth[i - 1]);
            }
            for (int step = 0; step < 500; step++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                long wrong = truth[b] - truth[a] + 1 + rnd.nextInt(10);
                assertFalse(uf.union(a, b, wrong), "step " + step);
            }
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(20)
        @DisplayName("10만 개 사슬")
        void largeChain() {
            int n = 100_000;
            WeightedUnionFind uf = new WeightedUnionFind(n);
            for (int i = 1; i < n; i++) {
                uf.union(i - 1, i, 1);
            }
            for (int round = 0; round < 50; round++) {
                assertEquals(n - 1, uf.diff(0, n - 1));
            }
        }
    }
}
