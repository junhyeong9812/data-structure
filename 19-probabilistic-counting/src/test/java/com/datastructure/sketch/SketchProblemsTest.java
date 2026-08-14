package com.datastructure.sketch;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("SketchProblems")
class SketchProblemsTest {

    private static List<int[]> hh(int[] stream, int k) {
        return SketchProblems.heavyHitters(stream, k);
    }

    private static void assertPairs(List<int[]> actual, int... expected) {
        List<int[]> want = new ArrayList<>();
        for (int i = 0; i < expected.length; i += 2) {
            want.add(new int[]{expected[i], expected[i + 1]});
        }
        assertEquals(want.size(), actual.size(),
                "개수가 다르다: " + fmt(actual));
        for (int i = 0; i < want.size(); i++) {
            assertArrayEquals(want.get(i), actual.get(i),
                    i + "번째가 다르다. 실제: " + fmt(actual));
        }
    }

    private static String fmt(List<int[]> l) {
        StringBuilder sb = new StringBuilder();
        for (int[] e : l) {
            sb.append("{").append(e[0]).append(",").append(e[1]).append("} ");
        }
        return sb.toString();
    }

    @Nested
    @DisplayName("문제 1: heavy hitters")
    class HeavyHitters {

        // 실제 빈도: 7->5, 5->4, 1->3, 9->2, 3->1, 2->1
        private final int[] stream = {5, 5, 5, 5, 1, 1, 1, 9, 9, 3, 7, 7, 7, 7, 7, 2};

        @Test
        @DisplayName("빈도 내림차순으로 k 개")
        void topK() {
            assertPairs(hh(stream, 1), 7, 5);
            assertPairs(hh(stream, 3), 7, 5, 5, 4, 1, 3);
            assertPairs(hh(stream, 4), 7, 5, 5, 4, 1, 3, 9, 2);
        }

        @Test
        @DisplayName("동점이면 값이 작은 것 먼저")
        void tieBreak() {
            // 빈도 1 인 것이 2 와 3 둘이다. k=5 면 2 가 뽑혀야 한다.
            assertPairs(hh(stream, 5), 7, 5, 5, 4, 1, 3, 9, 2, 2, 1);
            int[] ties = {1, 1, 2, 2, 3, 3, 4};
            assertPairs(hh(ties, 2), 1, 2, 2, 2);
            assertPairs(hh(ties, 3), 1, 2, 2, 2, 3, 2);
        }

        @Test
        @DisplayName("k 가 종류 수보다 크면 있는 것만 준다")
        void kTooLarge() {
            assertPairs(hh(stream, 10), 7, 5, 5, 4, 1, 3, 9, 2, 2, 1, 3, 1);
            assertEquals(6, hh(stream, 100).size());
        }

        @Test
        @DisplayName("빈 스트림, 잘못된 k")
        void edges() {
            assertEquals(0, hh(new int[0], 3).size());
            assertThrows(IllegalArgumentException.class, () -> hh(stream, 0));
            assertThrows(IllegalArgumentException.class, () -> hh(stream, -1));
            assertThrows(IllegalArgumentException.class, () -> hh(null, 3));
        }

        @Test
        @DisplayName("극단값도 다룬다")
        void extremeValues() {
            int[] s = {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, 0};
            assertPairs(hh(s, 3), Integer.MIN_VALUE, 2, 0, 1, Integer.MAX_VALUE, 1);
        }

        @Test
        @Timeout(30)
        @DisplayName("30만 개 스트림에서 진짜 상위 5개를 찾는다")
        void findsRealHeavyHitters() {
            // 꼬리에 10만 종류가 섞여 있어도 상위 5개는 흔들리지 않는다.
            // **그런데 반환한 빈도는 정확값이 아니라 추정치다.** 실제보다 크거나 같다.
            Random rnd = new Random(11L);
            int[] heavy = {7, 3, 9, 1, 5};
            int[] freq = {50_000, 40_000, 30_000, 20_000, 10_000};
            int[] s = new int[300_000];
            int idx = 0;
            for (int i = 0; i < heavy.length; i++) {
                for (int j = 0; j < freq[i]; j++) {
                    s[idx++] = heavy[i];
                }
            }
            while (idx < s.length) {
                s[idx++] = 10_000 + rnd.nextInt(100_000);
            }
            List<int[]> out = hh(s, 5);
            assertEquals(5, out.size());
            for (int i = 0; i < 5; i++) {
                assertEquals(heavy[i], out.get(i)[0], i + "위가 틀렸다: " + fmt(out));
                assertTrue(out.get(i)[1] >= freq[i], "추정 빈도는 실제 이상이어야 한다");
                assertTrue(out.get(i)[1] < freq[i] * 1.01,
                        "1% 안에는 들어야 한다: " + out.get(i)[1] + " 대 " + freq[i]);
            }
        }

        @Test
        @DisplayName("한계: 반환한 빈도가 정확값이 아니다")
        void reportedCountsAreEstimates() {
            // 스케치는 원소를 담지 않으므로 **스스로 후보를 나열하지 못한다.**
            // 그래서 스트림을 한 번 더 훑어야 하고, 그때 나오는 빈도는 늘 추정치다.
            Random rnd = new Random(3L);
            int[] s = new int[400_000];
            for (int i = 0; i < s.length; i++) {
                s[i] = i < 40_000 ? 1 : 100 + rnd.nextInt(300_000);
            }
            List<int[]> out = hh(s, 1);
            assertEquals(1, out.get(0)[0]);
            assertTrue(out.get(0)[1] >= 40_000, "과소평가는 없다");
            assertTrue(out.get(0)[1] > 40_000, "실측: 정확값보다 크게 나온다 - " + out.get(0)[1]);
        }
    }

    @Nested
    @DisplayName("문제 2: 샤드별로 세고 병합하기")
    class DistinctAcrossShards {

        @Test
        @DisplayName("작은 예")
        void small() {
            int[][] shards = {{1, 2, 3, 4}, {3, 4, 5}, {}, {5, 6, 7, 8, 9, 10}};
            assertEquals(10, SketchProblems.distinctAcrossShards(shards, 12));
            assertEquals(0, SketchProblems.distinctAcrossShards(new int[0][], 12));
            assertEquals(0, SketchProblems.distinctAcrossShards(new int[][]{{}, {}}, 12));
        }

        @Test
        @DisplayName("샤드별로 세고 합친 것이 통째로 센 것과 **완전히 같다**")
        void sameAsSinglePass() {
            // 이 등식이 성립하기 때문에 서버 100대가 각자 세도 된다.
            // **비슷한 것이 아니라 같다.** merge 가 레지스터별 max 라서 그렇다.
            int[][] shards = new int[8][];
            for (int s = 0; s < 8; s++) {
                shards[s] = new int[25_000];
                for (int i = 0; i < 25_000; i++) {
                    shards[s][i] = s * 20_000 + i;      // 샤드끼리 겹친다
                }
            }
            HyperLogLog whole = new HyperLogLog(14);
            for (int[] shard : shards) {
                for (int x : shard) {
                    whole.add(x);
                }
            }
            assertEquals(whole.estimate(), SketchProblems.distinctAcrossShards(shards, 14));
        }

        @Test
        @DisplayName("겹치는 원소를 두 번 세지 않는다")
        void overlapCountedOnce() {
            int[][] shards = new int[8][];
            Set<Integer> truth = new HashSet<>();
            for (int s = 0; s < 8; s++) {
                shards[s] = new int[25_000];
                for (int i = 0; i < 25_000; i++) {
                    shards[s][i] = s * 20_000 + i;
                    truth.add(shards[s][i]);
                }
            }
            assertEquals(165_000, truth.size());
            long est = SketchProblems.distinctAcrossShards(shards, 14);
            // 단순 합이면 20만이 나온다. 실측 추정 166620 (오차 0.98%)
            assertTrue(Math.abs(est - 165_000) < 165_000 * 0.05, "추정 " + est);
            assertTrue(est < 190_000, "겹치는 것을 두 번 셌다: " + est);
        }

        @Test
        @DisplayName("옮기는 바이트가 원본의 6분의 1도 안 된다")
        void networkCost() {
            // 요점이 이것이다. 샤드 8개가 원본 20만 개(80만 바이트)를 보내는 대신
            // 레지스터 16384바이트씩만 보낸다.
            int shardCount = 8;
            int perShard = 25_000;
            long rawBytes = (long) shardCount * perShard * Integer.BYTES;
            long sketchBytes = (long) shardCount * new HyperLogLog(14).memoryBytes();
            assertEquals(800_000, rawBytes);
            assertEquals(131_072, sketchBytes);
            assertTrue(sketchBytes * 6 < rawBytes, "스케치 " + sketchBytes + " 대 원본 " + rawBytes);
        }

        @Test
        @DisplayName("샤드 수가 늘어도 옮기는 양은 샤드당 고정이다")
        void costPerShardIsFixed() {
            // 정확히 세려면 샤드마다 원소 전부를 보내야 한다. 여기서는 항상 m 바이트다.
            for (int p : new int[]{10, 12, 14}) {
                int[][] shards = new int[3][];
                for (int s = 0; s < 3; s++) {
                    shards[s] = new int[100_000];
                    for (int i = 0; i < 100_000; i++) {
                        shards[s][i] = s * 100_000 + i;
                    }
                }
                long est = SketchProblems.distinctAcrossShards(shards, p);
                double rel = Math.abs(est - 300_000) / 300_000.0;
                double theory = 1.04 / Math.sqrt(1 << p);
                assertTrue(rel < theory * 4,
                        "p=" + p + " 상대오차 " + rel + " (이론 " + theory + ")");
            }
        }

        @Test
        @DisplayName("잘못된 인자")
        void edges() {
            assertThrows(IllegalArgumentException.class,
                    () -> SketchProblems.distinctAcrossShards(null, 12));
            assertThrows(IllegalArgumentException.class,
                    () -> SketchProblems.distinctAcrossShards(new int[][]{{1}, null}, 12));
            assertThrows(IllegalArgumentException.class,
                    () -> SketchProblems.distinctAcrossShards(new int[][]{{1}}, 3));
            assertThrows(IllegalArgumentException.class,
                    () -> SketchProblems.distinctAcrossShards(new int[][]{{1}}, 17));
        }
    }
}
