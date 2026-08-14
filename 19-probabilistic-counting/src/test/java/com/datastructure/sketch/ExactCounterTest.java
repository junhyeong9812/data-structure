package com.datastructure.sketch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ExactCounter: HashMap 기준선")
class ExactCounterTest extends FrequencyEstimatorContractTest {

    @Override
    protected FrequencyEstimator create() {
        return new ExactCounter();
    }

    @Nested
    @DisplayName("정확하다")
    class Exactness {

        @Test
        @DisplayName("추정이 아니라 답이다")
        void noError() {
            ExactCounter c = new ExactCounter();
            c.add(1, 10);
            c.add(2, 20);
            c.add(1, 5);
            assertEquals(15, c.estimateCount(1));
            assertEquals(20, c.estimateCount(2));
            assertEquals(0, c.estimateCount(3), "안 넣은 것은 0 이다. 스케치는 이걸 보장 못 한다");
            assertEquals(35, c.totalCount());
        }
    }

    @Nested
    @DisplayName("한계 측정: 메모리가 원소 종류 수에 비례한다")
    class MemoryGrowsWithDistinct {

        @Test
        @DisplayName("종류가 10배면 메모리도 10배다")
        void linearInDistinct() {
            // **이것이 고칠 수 없는 성질이다.** 스케치가 존재하는 이유이기도 하다.
            long[] bytes = new long[3];
            int[] distinct = {1000, 10_000, 100_000};
            for (int t = 0; t < 3; t++) {
                ExactCounter c = new ExactCounter();
                for (int i = 0; i < distinct[t]; i++) {
                    c.add(i);
                }
                assertEquals(distinct[t], c.distinctCount());
                bytes[t] = c.memoryBytes();
            }
            assertEquals(bytes[0] * 10, bytes[1]);
            assertEquals(bytes[1] * 10, bytes[2]);
        }

        @Test
        @DisplayName("같은 것만 반복하면 메모리는 안 는다")
        void repeatsAreFree() {
            // 비싼 것은 **개수**가 아니라 **종류**다. 스케치는 이 축을 잘라낸다.
            ExactCounter c = new ExactCounter();
            for (int i = 0; i < 100_000; i++) {
                c.add(7);
            }
            assertEquals(1, c.distinctCount());
            assertEquals(ExactCounter.BYTES_PER_ENTRY, c.memoryBytes());
            assertEquals(100_000, c.totalCount());
        }

        @Test
        @DisplayName("100만 종류면 CountMinSketch 보다 500배 넘게 쓴다")
        void versusSketch() {
            ExactCounter exact = new ExactCounter();
            CountMinSketch sketch = new CountMinSketch(0.001, 0.01);
            for (int i = 0; i < 1_000_000; i++) {
                exact.add(i);
                sketch.add(i);
            }
            // 실측: 정확 64,000,000 바이트 대 스케치 108,760 바이트 = 588배
            assertTrue(exact.memoryBytes() / sketch.memoryBytes() > 500,
                    "정확 " + exact.memoryBytes() + " 대 스케치 " + sketch.memoryBytes());
            assertEquals(108_760, sketch.memoryBytes(), "스케치 크기는 원소 수와 무관하게 고정이다");
        }
    }
}
