package com.datastructure.sketch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HyperLogLog: 레지스터 m 개의 조화평균")
class HyperLogLogTest extends CardinalityEstimatorContractTest {

    @Override
    protected CardinalityEstimator create() {
        return new HyperLogLog(14);
    }

    @Override
    protected double tolerance() {
        // 이론 표준오차는 1.04/sqrt(16384) = 0.81%.
        // 실측 최악이 2.03% 였고 여유를 둬 5% 로 잡는다.
        return 0.05;
    }

    @Nested
    @DisplayName("구조")
    class Structure {

        @Test
        @DisplayName("레지스터가 m = 2^p 개다")
        void registerCount() {
            for (int p = 4; p <= 16; p++) {
                HyperLogLog h = new HyperLogLog(p);
                assertEquals(1 << p, h.registerCount());
                assertEquals(1 << p, h.memoryBytes(), "레지스터 하나에 1바이트다");
                assertEquals(p, h.precision());
            }
        }

        @Test
        @DisplayName("정밀도 범위를 검사한다")
        void precisionRange() {
            assertThrows(IllegalArgumentException.class, () -> new HyperLogLog(3));
            assertThrows(IllegalArgumentException.class, () -> new HyperLogLog(17));
            assertThrows(IllegalArgumentException.class, () -> new HyperLogLog(0));
            assertThrows(IllegalArgumentException.class, () -> new HyperLogLog(-1));
        }

        @Test
        @DisplayName("alpha 는 m 이 작을 때만 표를 쓴다")
        void alphaConstants() {
            assertEquals(0.673, HyperLogLog.alpha(16), 1e-12);
            assertEquals(0.697, HyperLogLog.alpha(32), 1e-12);
            assertEquals(0.709, HyperLogLog.alpha(64), 1e-12);
            assertEquals(0.7152704933, HyperLogLog.alpha(128), 1e-9);
            assertEquals(0.7212525005, HyperLogLog.alpha(16384), 1e-9);
            assertTrue(HyperLogLog.alpha(1 << 20) < 0.7213,
                    "m 이 커지면 0.7213 에 아래에서 다가간다");
        }

        @Test
        @DisplayName("랭크는 선행 0 개수 + 1 이고 상한이 64 - p + 1 이다")
        void rankBounds() {
            for (int p : new int[]{4, 10, 14}) {
                HyperLogLog h = new HyperLogLog(p);
                for (int i = 0; i < 200_000; i++) {
                    h.add(i);
                }
                int max = 0;
                int nonZero = 0;
                for (int i = 0; i < (1 << p); i++) {
                    max = Math.max(max, h.register(i));
                    if (h.register(i) > 0) {
                        nonZero++;
                    }
                }
                assertTrue(max >= 1, "p=" + p + " 에서 켜진 레지스터가 없다");
                assertTrue(max <= 64 - p + 1, "p=" + p + " 랭크 " + max + " 가 상한을 넘었다");
                assertEquals((1 << p) - h.zeroRegisters(), nonZero);
            }
        }

        @Test
        @DisplayName("레지스터는 max 만 유지한다. 내려가지 않는다")
        void registersOnlyGoUp() {
            HyperLogLog h = new HyperLogLog(10);
            int[] before = new int[1024];
            for (int i = 0; i < 5000; i++) {
                h.add(i);
            }
            for (int i = 0; i < 1024; i++) {
                before[i] = h.register(i);
            }
            for (int i = 0; i < 5000; i++) {
                h.add(i);          // 같은 것을 또 넣는다
            }
            for (int i = 0; i < 1024; i++) {
                assertEquals(before[i], h.register(i), "레지스터 " + i + " 가 바뀌었다");
            }
        }
    }

    @Nested
    @DisplayName("정확도")
    class Accuracy {

        @Test
        @DisplayName("표준 오차가 1.04 / sqrt(m) 근처다")
        void standardError() {
            // 실측: p=14 평균 0.53%, 최악 2.03% (이론 0.81%)
            //       p=10 평균 2.37%, 최악 6.17% (이론 3.25%)
            for (int p : new int[]{10, 14}) {
                double worst = 0;
                double sum = 0;
                int trials = 0;
                for (int n : new int[]{1000, 10_000, 100_000}) {
                    for (int t = 0; t < 8; t++) {
                        HyperLogLog h = new HyperLogLog(p);
                        int base = t * 3_000_000 + 101;
                        for (int i = 0; i < n; i++) {
                            h.add(base + i);
                        }
                        double rel = Math.abs(h.estimate() - n) / (double) n;
                        worst = Math.max(worst, rel);
                        sum += rel;
                        trials++;
                    }
                }
                double theory = 1.04 / Math.sqrt(1 << p);
                assertTrue(sum / trials < theory * 2,
                        "p=" + p + " 평균 상대오차 " + (sum / trials) + " 가 이론 " + theory + " 의 2배를 넘는다");
                assertTrue(worst < theory * 4,
                        "p=" + p + " 최악 " + worst + " 가 이론 " + theory + " 의 4배를 넘는다");
            }
        }

        @Test
        @DisplayName("정밀도를 올리면 오차가 준다")
        void moreRegistersMeansLessError() {
            long[] est = new long[3];
            int[] ps = {6, 10, 14};
            for (int t = 0; t < 3; t++) {
                HyperLogLog h = new HyperLogLog(ps[t]);
                for (int i = 0; i < 500_000; i++) {
                    h.add(i);
                }
                est[t] = h.estimate();
            }
            double e6 = Math.abs(est[0] - 500_000) / 500_000.0;
            double e14 = Math.abs(est[2] - 500_000) / 500_000.0;
            assertTrue(e14 < e6,
                    "p=6 오차 " + e6 + ", p=14 오차 " + e14 + " - 레지스터를 늘렸는데 안 좋아졌다");
        }
    }

    @Nested
    @DisplayName("한계 측정: 작은 카디널리티에서는 보정 없이 못 쓴다")
    class SmallRangeCorrection {

        @Test
        @DisplayName("보정을 끄면 10개를 11822개로 센다")
        void rawEstimateIsHopelesslyWrong() {
            // 조화평균 공식만 쓰면 **빈 레지스터가 대부분일 때 완전히 무너진다.**
            // 레지스터 16384개 중 10개만 켜져 있으면 2^0 이 16374번 더해져
            // 분모가 카디널리티와 무관해진다. 그래서 알파 x m 근처 값이 그대로 나온다.
            HyperLogLog h = new HyperLogLog(14);
            for (int i = 0; i < 10; i++) {
                h.add(i * 7 + 1);
            }
            assertEquals(11_822, h.rawEstimate(), "보정 없는 추정치");
            assertEquals(10, h.estimate(), "linear counting 보정을 하면 정확히 맞는다");
            assertTrue(h.rawEstimate() > h.estimate() * 1000,
                    "보정 없이는 1000배 넘게 틀린다");
        }

        @Test
        @DisplayName("빈 것도 보정이 없으면 11817 이다")
        void emptyWithoutCorrection() {
            HyperLogLog h = new HyperLogLog(14);
            assertEquals(11_817, h.rawEstimate(), "아무것도 안 넣었는데 1만이 넘는다");
            assertEquals(0, h.estimate());
            assertEquals(16_384, h.zeroRegisters());
        }

        @Test
        @DisplayName("linear counting 은 m x ln(m / 0인개수) 다")
        void linearCountingFormula() {
            // 공식이 아니라 발상이 중요하다. 상자 m 개에 공을 무작위로 던져
            // 빈 상자가 z 개 남았다면 던진 공의 개수를 역산할 수 있다.
            // **이 국면에서는 HyperLogLog 를 아예 안 쓰는 것이다.**
            HyperLogLog h = new HyperLogLog(14);
            for (int i = 0; i < 1000; i++) {
                h.add(i);
            }
            int zeros = h.zeroRegisters();
            long expected = Math.round(16_384 * Math.log(16_384.0 / zeros));
            assertEquals(expected, h.estimate());
            assertTrue(Math.abs(h.estimate() - 1000) < 50, "보정값 " + h.estimate());
        }

        @Test
        @DisplayName("보정은 2.5m 이하이고 빈 레지스터가 있을 때만 켠다")
        void correctionSwitchesOff() {
            // 5만 개를 p=14 에 넣으면 raw 가 2.5m = 40960 을 넘어 보정이 꺼진다.
            HyperLogLog big = new HyperLogLog(14);
            for (int i = 0; i < 50_000; i++) {
                big.add(i * 7 + 1);
            }
            assertTrue(big.rawEstimate() > 2.5 * 16_384, "raw=" + big.rawEstimate());
            assertEquals(big.rawEstimate(), big.estimate(), "이 국면에서는 보정을 쓰지 않는다");

            // 빈 레지스터가 하나도 없으면 ln(m/0) 이 무한대라 쓸 수 없다.
            HyperLogLog full = new HyperLogLog(10);
            for (int i = 0; i < 20_000; i++) {
                full.add(i);
            }
            assertEquals(0, full.zeroRegisters());
            assertEquals(full.rawEstimate(), full.estimate());
        }
    }

    @Nested
    @DisplayName("merge: HyperLogLog 의 킬러 기능")
    class Merge {

        @Test
        @DisplayName("따로 세고 합친 것이 한 번에 센 것과 **완전히 같다**")
        void mergeEqualsSinglePass() {
            // 근사치가 비슷한 정도가 아니라 **레지스터가 바이트 단위로 동일**하다.
            // max 가 결합법칙과 교환법칙을 지키기 때문이다. 이것이 분산 집계의 근거다.
            for (int p : new int[]{4, 8, 12, 14}) {
                HyperLogLog all = new HyperLogLog(p);
                HyperLogLog a = new HyperLogLog(p);
                HyperLogLog b = new HyperLogLog(p);
                for (int i = 0; i < 30_000; i++) {
                    all.add(i);
                    a.add(i);
                }
                for (int i = 20_000; i < 50_000; i++) {   // 겹치는 구간이 있다
                    all.add(i);
                    b.add(i);
                }
                a.merge(b);
                for (int i = 0; i < (1 << p); i++) {
                    assertEquals(all.register(i), a.register(i),
                            "p=" + p + " 레지스터 " + i + " 가 다르다");
                }
                assertEquals(all.estimate(), a.estimate());
            }
        }

        @Test
        @DisplayName("겹치는 원소를 두 번 세지 않는다")
        void unionNotSum() {
            // 카운터를 더하는 방식이면 5만이 아니라 6만이 나온다.
            HyperLogLog a = new HyperLogLog(14);
            HyperLogLog b = new HyperLogLog(14);
            for (int i = 0; i < 30_000; i++) {
                a.add(i);
            }
            for (int i = 20_000; i < 50_000; i++) {
                b.add(i);
            }
            a.merge(b);
            assertTrue(Math.abs(a.estimate() - 50_000) < 50_000 * 0.05,
                    "합집합은 5만이다. 추정 " + a.estimate());
        }

        @Test
        @DisplayName("멱등이고 순서에 안 흔들린다")
        void idempotentAndCommutative() {
            HyperLogLog a = new HyperLogLog(12);
            HyperLogLog b = new HyperLogLog(12);
            for (int i = 0; i < 10_000; i++) {
                a.add(i);
                b.add(i + 5000);
            }
            HyperLogLog ab = new HyperLogLog(12);
            ab.merge(a);
            ab.merge(b);
            HyperLogLog ba = new HyperLogLog(12);
            ba.merge(b);
            ba.merge(a);
            for (int i = 0; i < 4096; i++) {
                assertEquals(ab.register(i), ba.register(i), "순서가 답을 바꿨다");
            }
            long once = ab.estimate();
            ab.merge(a);
            ab.merge(b);
            assertEquals(once, ab.estimate(), "같은 것을 또 합쳤더니 값이 변했다");
        }

        @Test
        @DisplayName("자기 자신을 합쳐도 그대로다")
        void selfMerge() {
            HyperLogLog a = new HyperLogLog(10);
            for (int i = 0; i < 5000; i++) {
                a.add(i);
            }
            long before = a.estimate();
            a.merge(a);
            assertEquals(before, a.estimate());
        }

        @Test
        @DisplayName("정밀도가 다르면 합칠 수 없다")
        void precisionMismatch() {
            // 레지스터 번호가 상위 p 비트라 p 가 다르면 같은 원소가 다른 칸으로 간다.
            HyperLogLog a = new HyperLogLog(10);
            HyperLogLog b = new HyperLogLog(12);
            assertThrows(IllegalArgumentException.class, () -> a.merge(b));
            assertThrows(IllegalArgumentException.class, () -> a.merge(null));
        }
    }

    @Nested
    @DisplayName("한계 측정: 합집합은 되고 교집합은 안 된다")
    class IntersectionIsNotSupported {

        @Test
        @DisplayName("포함배제로 흉내내면 겹치는 부분이 작을수록 무너진다")
        void inclusionExclusionBlowsUp() {
            // |A n B| = |A| + |B| - |A u B| 는 산술적으로는 맞다.
            // **그런데 오차가 큰 수 셋의 뺄셈이라 작은 답을 구할 때 오차가 그대로 남는다.**
            // 10만짜리 두 집합의 0.8% 오차는 800 인데 답이 50 이면 무슨 소용인가.
            int p = 14;
            double relAtLarge = overlapError(p, 50_000);
            double relAtSmall = overlapError(p, 50);
            // 실측: 겹침 5만 -> 0.9%, 겹침 5000 -> 10%, 겹침 500 -> 97%, 겹침 50 -> 1540%
            assertTrue(relAtLarge < 0.05, "큰 교집합은 그럭저럭이다: " + relAtLarge);
            assertTrue(relAtSmall > 3.0,
                    "작은 교집합 상대오차가 " + relAtSmall + " 배다. **교집합은 쓰면 안 된다**");
        }

        private double overlapError(int p, int overlap) {
            HyperLogLog a = new HyperLogLog(p);
            HyperLogLog b = new HyperLogLog(p);
            for (int i = 0; i < 100_000; i++) {
                a.add(i);
            }
            for (int i = 100_000 - overlap; i < 200_000 - overlap; i++) {
                b.add(i);
            }
            HyperLogLog union = new HyperLogLog(p);
            union.merge(a);
            union.merge(b);
            long guess = a.estimate() + b.estimate() - union.estimate();
            return Math.abs(guess - overlap) / (double) overlap;
        }

        @Test
        @DisplayName("합집합은 원본 크기와 무관하게 O(m) 이다")
        void unionCostIsFixed() {
            // 원소를 하나도 안 옮긴다. 레지스터 16384바이트만 옮기면 끝이다.
            HyperLogLog a = new HyperLogLog(14);
            HyperLogLog b = new HyperLogLog(14);
            for (int i = 0; i < 1_000_000; i++) {
                a.add(i);
                b.add(i + 500_000);
            }
            assertEquals(16_384, a.memoryBytes());
            assertEquals(16_384, b.memoryBytes());
            a.merge(b);
            assertEquals(16_384, a.memoryBytes(), "합쳐도 안 커진다");
            assertTrue(Math.abs(a.estimate() - 1_500_000) < 1_500_000 * 0.05,
                    "합집합 150만, 추정 " + a.estimate());
        }
    }
}
