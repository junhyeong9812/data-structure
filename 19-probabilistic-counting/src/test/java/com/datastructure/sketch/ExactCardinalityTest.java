package com.datastructure.sketch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ExactCardinality: HashSet 기준선")
class ExactCardinalityTest extends CardinalityEstimatorContractTest {

    @Override
    protected CardinalityEstimator create() {
        return new ExactCardinality();
    }

    @Override
    protected double tolerance() {
        return 0.0;
    }

    @Nested
    @DisplayName("한계 측정: 본 것을 전부 들고 있어야 한다")
    class MemoryGrowsWithCardinality {

        @Test
        @DisplayName("종류가 10배면 메모리도 10배다")
        void linearInCardinality() {
            long[] bytes = new long[3];
            int[] distinct = {1000, 10_000, 100_000};
            for (int t = 0; t < 3; t++) {
                ExactCardinality c = new ExactCardinality();
                for (int i = 0; i < distinct[t]; i++) {
                    c.add(i);
                }
                assertEquals(distinct[t], c.estimate());
                bytes[t] = c.memoryBytes();
            }
            assertEquals(bytes[0] * 10, bytes[1]);
            assertEquals(bytes[1] * 10, bytes[2]);
        }

        @Test
        @DisplayName("100만 종류면 HyperLogLog 보다 2000배 넘게 쓴다")
        void versusHyperLogLog() {
            ExactCardinality exact = new ExactCardinality();
            HyperLogLog hll = new HyperLogLog(14);
            for (int i = 0; i < 1_000_000; i++) {
                exact.add(i);
                hll.add(i);
            }
            // 실측: 정확 48,000,000 바이트 대 HLL 16,384 바이트 = 2929배
            assertTrue(exact.memoryBytes() / hll.memoryBytes() > 2000,
                    "정확 " + exact.memoryBytes() + " 대 HLL " + hll.memoryBytes());
            assertEquals(16_384, hll.memoryBytes(), "레지스터 하나에 1바이트씩 16384개다");
        }

        @Test
        @DisplayName("**원소를 다시 볼 일이 없는데도** 들고 있어야 한다")
        void keepsEverythingForever() {
            // 이것이 고칠 수 없는 성질이다. 답은 숫자 하나인데 그 숫자를 얻으려고
            // 100만 개를 붙들고 있는다. HyperLogLog 는 이 축을 잘라낸다.
            ExactCardinality c = new ExactCardinality();
            for (int i = 0; i < 100_000; i++) {
                c.add(i);
            }
            assertEquals(100_000L * ExactCardinality.BYTES_PER_ELEMENT, c.memoryBytes());
            assertEquals(100_000, c.estimate());
        }
    }
}
