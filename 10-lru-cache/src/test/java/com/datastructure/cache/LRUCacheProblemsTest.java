package com.datastructure.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("LRUCacheProblems")
class LRUCacheProblemsTest {

    private static final double EPS = 1e-9;

    @Nested
    @DisplayName("문제 1: LRU 적중률")
    class HitRatio {

        @Test
        @DisplayName("고전 예제")
        void classic() {
            assertEquals(2.0 / 6, LRUCacheProblems.hitRatio(2, new int[]{1, 2, 1, 3, 1, 2}), EPS);
            assertEquals(2.0 / 12,
                    LRUCacheProblems.hitRatio(3, new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5}), EPS);
        }

        @Test
        @DisplayName("같은 키만 반복하면 거의 다 적중")
        void allSame() {
            assertEquals(3.0 / 4, LRUCacheProblems.hitRatio(2, new int[]{1, 1, 1, 1}), EPS);
            assertEquals(3.0 / 5, LRUCacheProblems.hitRatio(1, new int[]{1, 1, 1, 2, 2}), EPS);
        }

        @Test
        @DisplayName("빈 입력은 0")
        void empty() {
            assertEquals(0.0, LRUCacheProblems.hitRatio(3, new int[]{}), EPS);
            assertEquals(0.0, LRUCacheProblems.hitRatio(3, null), EPS);
        }

        @Test
        @DisplayName("정수 나눗셈을 쓰면 여기서 걸린다")
        void notIntegerDivision() {
            double r = LRUCacheProblems.hitRatio(2, new int[]{1, 2, 1, 3, 1, 2});
            assertTrue(r > 0.0 && r < 1.0, "적중률이 " + r + " 다. 0 이나 1 이면 정수로 나눈 것이다");
        }

        @Test
        @DisplayName("한계: 순차 스캔에서는 적중률이 0 이다")
        void sequentialScanDefeatsLru() {
            // 용량 3 짜리 캐시에 0,1,2,3 을 돌아가며 찍으면 **한 번도 안 맞는다.**
            // 다음에 쓸 것을 정확히 골라 버리기 때문이다.
            // 버그가 아니라 LRU 라는 가정(최근 쓴 것을 또 쓴다)이 반대로 틀린 경우다.
            int[] scan = new int[400];
            for (int i = 0; i < scan.length; i++) {
                scan[i] = i % 4;
            }
            assertEquals(0.0, LRUCacheProblems.hitRatio(3, scan), EPS,
                    "LRU 는 여기서 완전히 진다");
            assertTrue(LRUCacheProblems.optimalHitRatio(3, scan) > 0.6,
                    "최적은 같은 입력에서 60% 를 넘는다. 정책의 문제이지 용량의 문제가 아니다");
        }

        @Test
        @Timeout(20)
        @DisplayName("100만 접근")
        void largeTrace() {
            Random rnd = new Random(7L);
            int[] acc = new int[1_000_000];
            for (int i = 0; i < acc.length; i++) {
                acc[i] = rnd.nextInt(5000);
            }
            double r = LRUCacheProblems.hitRatio(1000, acc);
            assertTrue(r > 0.1 && r < 0.4, "적중률 " + r + " 이 상식적인 범위를 벗어났다");
        }
    }

    @Nested
    @DisplayName("문제 2: 미래를 안다면 (Belady)")
    class Optimal {

        @Test
        @DisplayName("고전 예제")
        void classic() {
            assertEquals(5.0 / 12,
                    LRUCacheProblems.optimalHitRatio(3, new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5}), EPS);
            assertEquals(2.0 / 6,
                    LRUCacheProblems.optimalHitRatio(2, new int[]{1, 2, 3, 1, 2, 3}), EPS);
        }

        @Test
        @DisplayName("LRU 가 0 인 곳에서도 답을 낸다")
        void beatsLruOnScan() {
            int[] scan = new int[]{1, 2, 3, 1, 2, 3};
            assertEquals(0.0, LRUCacheProblems.hitRatio(2, scan), EPS);
            assertEquals(2.0 / 6, LRUCacheProblems.optimalHitRatio(2, scan), EPS);
        }

        @Test
        @DisplayName("최적은 LRU 보다 절대 나쁘지 않다")
        void neverWorseThanLru() {
            Random rnd = new Random(99L);
            for (int trial = 0; trial < 200; trial++) {
                int cap = 1 + rnd.nextInt(5);
                int[] acc = new int[20 + rnd.nextInt(60)];
                for (int i = 0; i < acc.length; i++) {
                    acc[i] = rnd.nextInt(8);
                }
                double lru = LRUCacheProblems.hitRatio(cap, acc);
                double opt = LRUCacheProblems.optimalHitRatio(cap, acc);
                assertTrue(opt >= lru - EPS,
                        "최적 " + opt + " 이 LRU " + lru + " 보다 나쁘다. cap=" + cap);
            }
        }

        @Test
        @DisplayName("같은 키만 있으면 둘이 같다")
        void degenerate() {
            assertEquals(3.0 / 4, LRUCacheProblems.optimalHitRatio(2, new int[]{1, 1, 1, 1}), EPS);
            assertEquals(0.0, LRUCacheProblems.optimalHitRatio(3, new int[]{}), EPS);
            assertEquals(0.0, LRUCacheProblems.optimalHitRatio(3, null), EPS);
        }

        @Test
        @DisplayName("용량이 서로 다른 키 수 이상이면 첫 접근만 빗나간다")
        void capacityLargeEnough() {
            int[] acc = new int[]{1, 2, 3, 1, 2, 3, 1, 2, 3};
            assertEquals(6.0 / 9, LRUCacheProblems.optimalHitRatio(3, acc), EPS);
            assertEquals(6.0 / 9, LRUCacheProblems.hitRatio(3, acc), EPS, "이 경우엔 LRU 도 최적이다");
        }
    }

    @Nested
    @DisplayName("문제 3: 최근 N개 중복 제거")
    class Deduplicate {

        @Test
        @DisplayName("창 안의 중복만 지운다")
        void windowed() {
            assertEquals(List.of(1, 2, 3, 4, 2),
                    LRUCacheProblems.deduplicateStream(3, new int[]{1, 2, 3, 1, 4, 1, 2}));
        }

        @Test
        @DisplayName("창이 좁으면 놓친다")
        void narrowWindowMisses() {
            // 용량 1 이면 바로 앞의 것만 기억한다.
            assertEquals(List.of(1, 2, 1, 2),
                    LRUCacheProblems.deduplicateStream(1, new int[]{1, 2, 1, 2}));
            // 창이 넉넉하면 전부 잡는다.
            assertEquals(List.of(1, 2),
                    LRUCacheProblems.deduplicateStream(4, new int[]{1, 2, 1, 2}));
        }

        @Test
        @DisplayName("창이 딱 맞아도 순환하면 못 잡는다")
        void exactlyAtCapacity() {
            // 용량 2, 서로 다른 키 3개 순환. 다시 볼 때마다 이미 밀려나 있다.
            assertEquals(List.of(1, 2, 3, 1, 2, 3),
                    LRUCacheProblems.deduplicateStream(2, new int[]{1, 2, 3, 1, 2, 3}));
        }

        @Test
        @DisplayName("연속 중복")
        void consecutive() {
            assertEquals(List.of(1), LRUCacheProblems.deduplicateStream(2, new int[]{1, 1, 1}));
        }

        @Test
        @DisplayName("빈 입력")
        void empty() {
            assertEquals(List.of(), LRUCacheProblems.deduplicateStream(3, new int[]{}));
            assertEquals(List.of(), LRUCacheProblems.deduplicateStream(3, null));
        }

        @Test
        @Timeout(20)
        @DisplayName("100만 건")
        void large() {
            Random rnd = new Random(11L);
            int[] stream = new int[1_000_000];
            for (int i = 0; i < stream.length; i++) {
                stream[i] = rnd.nextInt(2000);
            }
            List<Integer> out = LRUCacheProblems.deduplicateStream(1000, stream);
            assertTrue(out.size() < stream.length, "중복이 하나도 안 걸렸다");
            assertTrue(out.size() > 1000, "너무 많이 걸렀다: " + out.size());
        }
    }
}
