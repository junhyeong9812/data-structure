package com.datastructure.sketch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** FrequencyEstimator 계약. 정확한 구현과 스케치가 똑같이 지켜야 하는 것만 여기 있다. */
abstract class FrequencyEstimatorContractTest {

    protected abstract FrequencyEstimator create();

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("빈 것은 전부 0 이다")
        void startsEmpty() {
            FrequencyEstimator f = create();
            assertEquals(0, f.totalCount());
            for (int i = -50; i < 50; i++) {
                assertEquals(0, f.estimateCount(i), "빈 계수기가 " + i + " 를 셌다");
            }
        }

        @Test
        @DisplayName("한 번 넣으면 1 이상이다")
        void singleAdd() {
            FrequencyEstimator f = create();
            f.add(42);
            assertTrue(f.estimateCount(42) >= 1, "넣었는데 " + f.estimateCount(42) + " 다");
            assertEquals(1, f.totalCount());
        }

        @Test
        @DisplayName("count 를 주면 그만큼 더한다")
        void bulkAdd() {
            FrequencyEstimator f = create();
            f.add(7, 100);
            f.add(7, 50);
            assertTrue(f.estimateCount(7) >= 150, "150 이상이어야 한다: " + f.estimateCount(7));
            assertEquals(150, f.totalCount(), "totalCount 는 더한 개수의 합이다");
        }

        @Test
        @DisplayName("음수 count 는 거부한다")
        void negativeCountRejected() {
            // 빼기를 허용하면 "절대 과소평가하지 않는다"가 즉시 깨진다.
            // 11번 CountingBloomFilter 가 삭제를 얻고 "누락 없음"을 잃은 것과 같은 자리다.
            FrequencyEstimator f = create();
            assertThrows(IllegalArgumentException.class, () -> f.add(1, -1));
            assertThrows(IllegalArgumentException.class, () -> f.add(1, Long.MIN_VALUE));
        }

        @Test
        @DisplayName("count 0 은 아무 일도 하지 않는다")
        void zeroCountIsNoop() {
            FrequencyEstimator f = create();
            f.add(3, 0);
            assertEquals(0, f.totalCount());
            assertEquals(0, f.estimateCount(3));
        }

        @Test
        @DisplayName("음수 키와 극단값도 된다")
        void extremeKeys() {
            FrequencyEstimator f = create();
            int[] keys = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};
            for (int k : keys) {
                f.add(k, 5);
            }
            for (int k : keys) {
                assertTrue(f.estimateCount(k) >= 5,
                        k + " 에서 걸렸다. 인덱스가 음수로 갔을 수 있다");
            }
            assertEquals(25, f.totalCount());
        }
    }

    @Nested
    @DisplayName("계약의 핵심: 절대 과소평가하지 않는다")
    class NeverUnderestimates {

        @Test
        @DisplayName("같은 것을 1000번 넣으면 1000 이상이다")
        void repeated() {
            FrequencyEstimator f = create();
            for (int i = 0; i < 1000; i++) {
                f.add(99);
            }
            assertTrue(f.estimateCount(99) >= 1000, "실제 1000, 추정 " + f.estimateCount(99));
        }

        @Test
        @DisplayName("무작위 스트림을 HashMap 과 대조한다")
        void randomStreamAgainstHashMap() {
            // 이 테스트가 이 문제집에서 가장 많은 것을 잡는다.
            // 정확한 답을 아는 참조 구현(HashMap)과 수만 스텝을 맞춰본다.
            FrequencyEstimator f = create();
            Map<Integer, Long> truth = new HashMap<>();
            Random rnd = new Random(20260813L);
            for (int step = 0; step < 50_000; step++) {
                int key = rnd.nextInt(3000) - 1500;
                long count = 1 + rnd.nextInt(3);
                f.add(key, count);
                truth.merge(key, count, Long::sum);
            }
            long total = 0;
            for (Map.Entry<Integer, Long> e : truth.entrySet()) {
                long estimate = f.estimateCount(e.getKey());
                assertTrue(estimate >= e.getValue(),
                        "키 " + e.getKey() + ": 실제 " + e.getValue() + " 인데 " + estimate
                                + " 로 추정했다. **과소평가는 계약 위반이다**");
                total += e.getValue();
            }
            assertEquals(total, f.totalCount());
        }

        @Test
        @DisplayName("안 넣은 것을 넣었다고 할 수는 있어도 그 반대는 없다")
        void oneDirectionalError() {
            // 11번 블룸 필터의 비대칭이 여기서 이 모양으로 다시 나온다.
            //   블룸: false 면 확실히 없다
            //   스케치: 추정치가 c 면 실제는 c 이하다
            FrequencyEstimator f = create();
            for (int i = 0; i < 500; i++) {
                f.add(i);
            }
            for (int i = 0; i < 500; i++) {
                assertTrue(f.estimateCount(i) >= 1, i + " 를 넣었는데 0 이라고 한다");
            }
        }
    }

    @Nested
    @DisplayName("메모리")
    class MemoryReport {

        @Test
        @DisplayName("줄지 않고, 0 이상이다")
        void monotonic() {
            FrequencyEstimator f = create();
            long prev = f.memoryBytes();
            assertTrue(prev >= 0);
            for (int i = 0; i < 2000; i++) {
                f.add(i);
                long now = f.memoryBytes();
                assertTrue(now >= prev, "메모리가 줄었다: " + prev + " -> " + now);
                prev = now;
            }
            assertTrue(prev > 0, "원소를 넣었는데 저장 공간이 0 이다");
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(30)
        @DisplayName("100만 번 더하기")
        void millionAdds() {
            FrequencyEstimator f = create();
            for (int i = 0; i < 1_000_000; i++) {
                f.add(i % 100_000);
            }
            assertEquals(1_000_000, f.totalCount());
            assertTrue(f.estimateCount(0) >= 10);
        }
    }
}
