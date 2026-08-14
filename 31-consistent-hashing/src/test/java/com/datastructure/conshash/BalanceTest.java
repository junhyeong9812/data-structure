package com.datastructure.conshash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 한계 측정 2, 3, 4, 5. 균형에 관한 것 전부.
 *
 * 이동량이 원 방식을 쓰는 이유라면, 균형은 원 방식이 그냥은 못 하는 것이다.
 * 가상 노드가 그 값을 사는 방법이고, 값은 메모리로 치른다.
 */
@DisplayName("분포 측정")
class BalanceTest {

    private static final List<String> KEYS = RingMetrics.keys(100_000);

    private static ConsistentHashRing ringOf(int virtualNodes, RingHash hash) {
        ConsistentHashRing ring = new ConsistentHashRing(virtualNodes, hash);
        for (int i = 0; i < 10; i++) {
            ring.addNode("node-" + i);
        }
        return ring;
    }

    private static int min(Map<String, Integer> counts) {
        return counts.values().stream().mapToInt(Integer::intValue).min().orElseThrow();
    }

    private static int max(Map<String, Integer> counts) {
        return counts.values().stream().mapToInt(Integer::intValue).max().orElseThrow();
    }

    @Nested
    @DisplayName("측정 2: 가상 노드 수가 균형을 정한다")
    class VirtualNodeCount {

        @Test
        @DisplayName("1, 10, 100, 500 으로 늘려가며 최대 대 최소를 잰다")
        void imbalanceShrinksAsVirtualNodesGrow() {
            int[] virtualNodes = {1, 10, 100, 500, 1_000};
            int[] expectedMin = {873, 8_381, 8_136, 9_301, 9_278};
            int[] expectedMax = {15_835, 14_717, 12_044, 10_288, 10_513};
            double[] ratio = new double[virtualNodes.length];

            System.out.printf("  노드 10개, 키 %,d 개%n", KEYS.size());
            for (int i = 0; i < virtualNodes.length; i++) {
                ConsistentHashRing ring = ringOf(virtualNodes[i], Hashing.MIXED);
                Map<String, Integer> counts = ring.keyCounts(KEYS);
                ratio[i] = RingMetrics.imbalance(counts);
                System.out.printf("    가상 노드 %,5d  자리 %,6d  최소 %,6d  최대 %,6d  비 %.3f%n",
                        virtualNodes[i], ring.slotCount(), min(counts), max(counts), ratio[i]);

                assertEquals(10 * virtualNodes[i], ring.slotCount(), "자리 수");
                assertEquals(expectedMin[i], min(counts), "가상 노드 " + virtualNodes[i] + " 의 최소");
                assertEquals(expectedMax[i], max(counts), "가상 노드 " + virtualNodes[i] + " 의 최대");
            }

            assertTrue(ratio[0] > 18, "자리를 하나만 주면 최대가 최소의 " + ratio[0] + " 배다");
            assertTrue(ratio[1] < ratio[0] / 5, ratio[0] + " -> " + ratio[1]);
            assertTrue(ratio[2] < ratio[1], ratio[1] + " -> " + ratio[2]);
            assertTrue(ratio[3] < 1.15, "가상 노드 500 에서 " + ratio[3]);
        }

        @Test
        @DisplayName("정직하게: 더 늘린다고 계속 좋아지지는 않는다")
        void moreIsNotAlwaysBetter() {
            // 1000개가 500개보다 나쁘다. 자리를 무작위로 찍는 이상 이건 확률 과정이라
            // 단조롭게 좋아진다는 보장이 없다. 기대값이 좋아질 뿐이다.
            // 12번 스킵 리스트에서 "확률은 보장이 아니다"라고 했던 것과 같은 이야기다.
            double at500 = RingMetrics.imbalance(ringOf(500, Hashing.MIXED).keyCounts(KEYS));
            double at1000 = RingMetrics.imbalance(ringOf(1_000, Hashing.MIXED).keyCounts(KEYS));
            System.out.printf("  가상 노드 500 -> %.3f, 1000 -> %.3f (더 나쁘다)%n", at500, at1000);
            assertTrue(at1000 > at500, at500 + " 대 " + at1000);
        }

        @Test
        @DisplayName("균형의 값은 메모리로 치른다")
        void balanceCostsMemory() {
            // 자리 하나가 long 키와 노드 참조를 들고 TreeMap 노드로 산다.
            // 노드 10개짜리 클러스터에 10,000개를 들고 있는 셈이고, 노드 1000대면 100만개다.
            assertEquals(10, ringOf(1, Hashing.MIXED).slotCount());
            assertEquals(1_000, ringOf(100, Hashing.MIXED).slotCount());
            assertEquals(10_000, ringOf(1_000, Hashing.MIXED).slotCount());
            assertEquals(0, new JumpConsistentHash().slotCount());
        }
    }

    @Nested
    @DisplayName("측정 3: 가중치")
    class Weighting {

        @Test
        @DisplayName("자리를 두 배 주면 키도 두 배 온다")
        void doubleWeightTakesDoubleKeys() {
            WeightedConsistentHashRing ring = new WeightedConsistentHashRing(100);
            ring.addNode("small-a", 1);
            ring.addNode("small-b", 1);
            ring.addNode("big", 2);

            Map<String, Integer> counts = ring.keyCounts(KEYS);
            System.out.printf("  small-a %,d  small-b %,d  big %,d  (big 이 %.3f 배, 이상값 2.0)%n",
                    counts.get("small-a"), counts.get("small-b"), counts.get("big"),
                    (double) counts.get("big") / counts.get("small-a"));

            assertEquals(24_226, counts.get("small-a"));
            assertEquals(25_678, counts.get("small-b"));
            assertEquals(50_096, counts.get("big"));
            assertEquals(400, ring.slotCount());

            // 용량의 절반이면 키의 절반. 오차 1% 안이다.
            assertTrue(Math.abs(counts.get("big") / (double) KEYS.size() - 0.5) < 0.01,
                    "big 의 몫 " + counts.get("big") / (double) KEYS.size());
        }

        @Test
        @DisplayName("정직하게: 비율이 정확히 맞지는 않는다")
        void weightIsApproximate() {
            // small-a 24,226 대 small-b 25,678. 같은 가중치인데 6% 차이가 난다.
            // 가상 노드 100개로는 이 정도 오차가 남는다는 뜻이고, 자리를 늘리면 줄어든다.
            WeightedConsistentHashRing coarse = new WeightedConsistentHashRing(100);
            coarse.addNode("small-a", 1);
            coarse.addNode("small-b", 1);
            coarse.addNode("big", 2);
            Map<String, Integer> counts = coarse.keyCounts(KEYS);
            double skew = Math.abs(counts.get("small-a") - counts.get("small-b"))
                    / (double) counts.get("small-a");
            System.out.printf("  같은 가중치인 두 노드의 차이: %.1f%%%n", skew * 100);
            assertTrue(skew > 0.05, "오차가 " + skew + " 다. 없는 척하지 않는다");
        }
    }

    @Nested
    @DisplayName("측정 4: 해시가 나쁘면 원도 나쁘다")
    class WeakHashCollapse {

        @Test
        @DisplayName("node-0 부터 node-9 는 원 위에서 서로 한 칸 옆이다")
        void weakPositionsAreAdjacent() {
            long[] positions = new long[10];
            for (int i = 0; i < 10; i++) {
                positions[i] = Hashing.WEAK.position("node-" + i + "#0");
            }
            long span = positions[9] - positions[0];
            System.out.printf("  약한 해시: 자리 열 개가 %,d 폭 안에 있다 (원 전체는 %,d)%n",
                    span, Hashing.RING_SIZE);
            assertEquals(961, positions[1] - positions[0], "이름 한 글자 차이가 곧 자리 차이다");
            assertEquals(8_649, span);
            assertTrue(span < Hashing.RING_SIZE / 400_000, "원 전체의 50만 분의 1 안에 뭉쳐 있다");
        }

        @Test
        @DisplayName("가상 노드를 500개 줘도 살아나지 않는다")
        void virtualNodesCannotSaveABadHash() {
            // 가상 노드는 자리를 흩기 위한 것이 아니라 자리를 늘리기 위한 것이다.
            // 흩는 일은 해시가 한다. 해시가 안 흩으면 자리를 5000개 찍어도 다 같은 곳에 찍힌다.
            for (int virtualNodes : new int[] {1, 100, 500}) {
                ConsistentHashRing ring = ringOf(virtualNodes, Hashing.WEAK);
                Map<String, Integer> counts = ring.keyCounts(KEYS);
                long starved = counts.values().stream().filter(c -> c == 0).count();
                System.out.printf("    약한 해시 가상 노드 %,3d  자리 %,5d  최대 %,6d  0개인 노드 %d%n",
                        virtualNodes, ring.slotCount(), max(counts), starved);

                assertEquals(10 * virtualNodes, ring.slotCount());
                assertEquals(KEYS.size(), counts.get("node-0"), "node-0 이 전부 가져간다");
                assertEquals(9, starved, "나머지 아홉 대는 논다");
                assertEquals(Double.POSITIVE_INFINITY, RingMetrics.imbalance(counts));
            }
        }

        @Test
        @DisplayName("섞는 해시로 바꾸면 그대로 살아난다")
        void mixingFixesIt() {
            // 링 코드는 한 줄도 안 바뀐다. 바뀐 것은 이름을 자리로 옮기는 함수뿐이다.
            assertEquals(Double.POSITIVE_INFINITY,
                    RingMetrics.imbalance(ringOf(100, Hashing.WEAK).keyCounts(KEYS)));
            assertTrue(RingMetrics.imbalance(ringOf(100, Hashing.MIXED).keyCounts(KEYS)) < 1.5);
        }
    }

    @Nested
    @DisplayName("측정 5: 점프 해시는 자리 없이 더 고르다")
    class JumpBalance {

        @Test
        @DisplayName("자리 0개로 가상 노드 5000개짜리 원을 이긴다")
        void betterBalanceWithZeroMemory() {
            JumpConsistentHash jump = new JumpConsistentHash();
            for (int i = 0; i < 10; i++) {
                jump.addNode("node-" + i);
            }
            Map<String, Integer> jumpCounts = jump.keyCounts(KEYS);
            double jumpRatio = RingMetrics.imbalance(jumpCounts);
            double ringRatio = RingMetrics.imbalance(ringOf(500, Hashing.MIXED).keyCounts(KEYS));

            System.out.printf("  점프  최소 %,6d 최대 %,6d 비 %.4f  자리 %d개%n",
                    min(jumpCounts), max(jumpCounts), jumpRatio, jump.slotCount());
            System.out.printf("  원    비 %.4f  자리 %,d개%n", ringRatio, 5_000);

            assertEquals(9_827, min(jumpCounts));
            assertEquals(10_080, max(jumpCounts));
            assertTrue(jumpRatio < ringRatio, jumpRatio + " 대 " + ringRatio);
            assertEquals(0, jump.slotCount());
        }
    }
}
