package com.datastructure.bloom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("ScalableBloomFilter: 차면 잇는")
class ScalableBloomFilterTest extends ProbabilisticSetContractTest {

    @Override
    protected ProbabilisticSet<Integer> create(int expectedInsertions, double fpr) {
        return new ScalableBloomFilter<>(expectedInsertions, fpr);
    }

    private ScalableBloomFilter<Integer> scalable(int n, double p) {
        return new ScalableBloomFilter<>(n, p);
    }

    @Nested
    @DisplayName("자라기")
    class Growth {

        @Test
        @DisplayName("차기 전에는 필터가 하나다")
        void singleFilterUntilFull() {
            ScalableBloomFilter<Integer> f = scalable(100, 0.01);
            assertEquals(0, f.filterCount(), "아직 아무것도 안 넣었다");
            for (int i = 0; i < 100; i++) {
                f.add(i);
            }
            assertEquals(1, f.filterCount(), "딱 용량까지는 하나로 버틴다");
        }

        @Test
        @DisplayName("차면 하나 더 붙는다")
        void addsFilterWhenFull() {
            ScalableBloomFilter<Integer> f = scalable(100, 0.01);
            for (int i = 0; i < 101; i++) {
                f.add(i);
            }
            assertEquals(2, f.filterCount());
        }

        @Test
        @DisplayName("용량이 두 배씩 커져서 필터 수는 로그로 는다")
        void capacityDoubles() {
            ScalableBloomFilter<Integer> f = scalable(100, 0.01);
            // 100 + 200 + 400 + 800 + 1600 = 3100 까지 5개로 버틴다
            for (int i = 0; i < 3100; i++) {
                f.add(i);
            }
            assertEquals(5, f.filterCount());
            for (int i = 0; i < 100; i++) {
                f.add(10_000 + i);
            }
            assertEquals(6, f.filterCount());
        }
    }

    @Nested
    @DisplayName("오탐률이 유계다")
    class BoundedFalsePositives {

        @Test
        @DisplayName("필터가 늘어도 예상 오탐률이 2p 를 안 넘는다")
        void staysUnderTwiceTarget() {
            ScalableBloomFilter<Integer> f = scalable(100, 0.01);
            for (int i = 0; i < 50_000; i++) {
                f.add(i);
            }
            assertTrue(f.filterCount() >= 8, "필터가 " + f.filterCount() + "개 생겼다");
            double rate = f.expectedFalsePositiveRate();
            assertTrue(rate < 0.021,
                    "예상 오탐률 " + rate + " 가 2p=0.02 를 넘었다. 오탐률을 안 조인 것이다");
        }

        @Test
        @DisplayName("확률은 더하는 것이 아니라 곱으로 구한다")
        void notASum() {
            // 필터 둘의 예상 오탐률이 각각 약 0.498, 0.249 다.
            // 더하면 0.748, "둘 다 빗나갈 확률의 여집합"으로 구하면 0.623 이다.
            // 확률을 더하면 1 을 넘을 수도 있다. 애초에 틀린 계산이다.
            ScalableBloomFilter<Integer> f = scalable(100, 0.5);
            for (int i = 0; i < 300; i++) {
                f.add(i);
            }
            assertEquals(2, f.filterCount());
            double r = f.expectedFalsePositiveRate();
            assertTrue(r > 0.60 && r < 0.65,
                    "예상 오탐률 " + r + " - 0.623 근처여야 한다. 0.75 근처면 더한 것이다");
        }

        @Test
        @DisplayName("어떤 경우에도 1 을 넘지 않는다")
        void neverExceedsOne() {
            ScalableBloomFilter<Integer> f = scalable(10, 0.5);
            for (int i = 0; i < 20_000; i++) {
                f.add(i);
                double r = f.expectedFalsePositiveRate();
                assertTrue(r >= 0.0 && r <= 1.0, "확률이 " + r + " 다");
            }
        }

        @Test
        @Timeout(30)
        @DisplayName("실측 오탐률도 유계 안에 있다")
        void measuredStaysBounded() {
            ScalableBloomFilter<Integer> f = scalable(1000, 0.01);
            for (int i = 0; i < 100_000; i++) {
                f.add(i);
            }
            for (int i = 0; i < 100_000; i++) {
                assertTrue(f.mightContain(i), "누락: " + i);
            }
            int fp = 0;
            for (int i = 10_000_000; i < 10_100_000; i++) {
                if (f.mightContain(i)) {
                    fp++;
                }
            }
            double measured = fp / 100_000.0;
            assertTrue(measured < 0.03,
                    "실측 오탐률 " + measured + " - 100배 과부하인데도 3% 안이어야 한다");
        }

        @Test
        @DisplayName("같은 과부하에서 기본형은 무너진다")
        void plainFilterCollapses() {
            // 대비가 이 클래스의 존재 이유다.
            BloomFilter<Integer> plain = new BloomFilter<>(1000, 0.01);
            for (int i = 0; i < 100_000; i++) {
                plain.add(i);
            }
            int fp = 0;
            for (int i = 10_000_000; i < 10_010_000; i++) {
                if (plain.mightContain(i)) {
                    fp++;
                }
            }
            assertTrue(fp / 10_000.0 > 0.9,
                    "100배를 넣으면 거의 전부를 있다고 한다. 필터 구실을 못 한다");
        }
    }

    @Nested
    @DisplayName("대가")
    class Cost {

        @Test
        @DisplayName("조회가 필터 수만큼 든다")
        void lookupCostsGrow() {
            ScalableBloomFilter<Integer> f = scalable(100, 0.01);
            for (int i = 0; i < 100_000; i++) {
                f.add(i);
            }
            // 100 * 2^k 의 합이 10만을 넘는 지점 - 10개 정도
            assertTrue(f.filterCount() >= 9 && f.filterCount() <= 12,
                    "필터 " + f.filterCount() + "개. 원소 수의 로그로 는다");
        }

        @Test
        @DisplayName("clear 하면 필터가 전부 사라진다")
        void clearDropsAllFilters() {
            ScalableBloomFilter<Integer> f = scalable(100, 0.01);
            for (int i = 0; i < 1000; i++) {
                f.add(i);
            }
            assertTrue(f.filterCount() > 1);
            f.clear();
            assertEquals(0, f.filterCount());
            assertEquals(0, f.bitSize());
        }
    }
}
