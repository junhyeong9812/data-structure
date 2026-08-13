package com.datastructure.bloom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** ProbabilisticSet 계약. 세 구현이 똑같이 지켜야 하는 것만 여기 있다. */
abstract class ProbabilisticSetContractTest {

    protected abstract ProbabilisticSet<Integer> create(int expectedInsertions, double fpr);

    @Nested
    @DisplayName("생성")
    class Construction {

        @Test
        @DisplayName("잘못된 인자를 거부한다")
        void rejectsBadArgs() {
            assertThrows(IllegalArgumentException.class, () -> create(0, 0.01));
            assertThrows(IllegalArgumentException.class, () -> create(-1, 0.01));
            assertThrows(IllegalArgumentException.class, () -> create(100, 0.0),
                    "오탐률 0 은 불가능하다. 그러려면 원소를 담아야 한다");
            assertThrows(IllegalArgumentException.class, () -> create(100, 1.0));
            assertThrows(IllegalArgumentException.class, () -> create(100, -0.1));
        }

        @Test
        @DisplayName("처음엔 무엇도 없다")
        void startsEmpty() {
            ProbabilisticSet<Integer> f = create(100, 0.01);
            assertEquals(0, f.insertedCount());
            for (int i = 0; i < 100; i++) {
                assertFalse(f.mightContain(i), "빈 필터가 " + i + " 를 있다고 한다");
            }
        }
    }

    @Nested
    @DisplayName("계약의 핵심: 누락이 없다")
    class NoFalseNegatives {

        @Test
        @DisplayName("넣은 것은 반드시 통과한다")
        void everythingInsertedPasses() {
            ProbabilisticSet<Integer> f = create(1000, 0.01);
            for (int i = 0; i < 1000; i++) {
                f.add(i);
            }
            for (int i = 0; i < 1000; i++) {
                assertTrue(f.mightContain(i), i + " 를 넣었는데 없다고 한다. 누락은 절대 안 된다");
            }
        }

        @Test
        @DisplayName("용량을 훨씬 넘겨도 누락은 없다")
        void overloadedStillNoFalseNegatives() {
            // 오탐률은 치솟지만 **누락은 여전히 0 이어야 한다.**
            ProbabilisticSet<Integer> f = create(100, 0.01);
            for (int i = 0; i < 5000; i++) {
                f.add(i);
            }
            for (int i = 0; i < 5000; i++) {
                assertTrue(f.mightContain(i), "과부하 상태에서도 누락은 안 된다: " + i);
            }
        }

        @Test
        @DisplayName("같은 원소를 여러 번 넣어도 된다")
        void repeatedInsert() {
            ProbabilisticSet<Integer> f = create(100, 0.01);
            for (int i = 0; i < 50; i++) {
                f.add(42);
            }
            assertTrue(f.mightContain(42));
            assertEquals(50, f.insertedCount(), "insertedCount 는 add 호출 횟수다");
        }

        @Test
        @DisplayName("음수와 큰 수도 된다")
        void negativeAndLargeKeys() {
            ProbabilisticSet<Integer> f = create(100, 0.01);
            int[] keys = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};
            for (int k : keys) {
                f.add(k);
            }
            for (int k : keys) {
                assertTrue(f.mightContain(k), k + " 에서 걸렸다. 인덱스가 음수로 갔을 수 있다");
            }
        }
    }

    @Nested
    @DisplayName("오탐")
    class FalsePositives {

        @Test
        @DisplayName("안 넣은 것은 대부분 걸러진다")
        void mostAbsentAreRejected() {
            ProbabilisticSet<Integer> f = create(10_000, 0.01);
            for (int i = 0; i < 10_000; i++) {
                f.add(i);
            }
            int falsePositives = 0;
            for (int i = 1_000_000; i < 1_100_000; i++) {
                if (f.mightContain(i)) {
                    falsePositives++;
                }
            }
            double rate = falsePositives / 100_000.0;
            assertTrue(rate < 0.05,
                    "실측 오탐률 " + rate + " - 목표 1% 대비 너무 높다. 해시가 잘 안 퍼지고 있다");
        }
    }

    @Nested
    @DisplayName("clear")
    class Clear {

        @Test
        @DisplayName("전부 잊는다")
        void forgetsEverything() {
            ProbabilisticSet<Integer> f = create(1000, 0.01);
            for (int i = 0; i < 1000; i++) {
                f.add(i);
            }
            f.clear();
            assertEquals(0, f.insertedCount());
            int survivors = 0;
            for (int i = 0; i < 1000; i++) {
                if (f.mightContain(i)) {
                    survivors++;
                }
            }
            assertEquals(0, survivors, survivors + "개가 살아남았다. 비우지 못했다");
            f.add(7);
            assertTrue(f.mightContain(7), "비운 뒤에도 쓸 수 있어야 한다");
        }
    }

    @Nested
    @DisplayName("메모리")
    class Memory {

        @Test
        @DisplayName("크기가 줄지 않고, 넣은 뒤에는 0 이 아니다")
        void sizeIsMonotonic() {
            // ScalableBloomFilter 는 첫 add 전에 아무것도 안 만든다(0 이 정상이다).
            // "원소 수와 무관하게 고정"은 그쪽에서 성립하지 않으므로 각 구현 테스트에서 본다.
            ProbabilisticSet<Integer> f = create(1000, 0.01);
            long prev = f.bitSize();
            assertTrue(prev >= 0);
            for (int i = 0; i < 1000; i++) {
                f.add(i);
                long now = f.bitSize();
                assertTrue(now >= prev, "크기가 줄었다: " + prev + " -> " + now);
                prev = now;
            }
            assertTrue(prev > 0, "원소를 넣었는데 비트를 하나도 안 쓴다");
        }

        @Test
        @DisplayName("예상 오탐률은 넣을수록 오른다")
        void expectedRateGrows() {
            ProbabilisticSet<Integer> f = create(1000, 0.01);
            double prev = f.expectedFalsePositiveRate();
            assertTrue(prev < 1e-9, "빈 필터의 예상 오탐률은 0 이어야 한다: " + prev);
            for (int round = 0; round < 5; round++) {
                for (int i = 0; i < 300; i++) {
                    f.add(round * 300 + i);
                }
                double now = f.expectedFalsePositiveRate();
                assertTrue(now > prev, "넣었는데 예상 오탐률이 안 올랐다");
                assertTrue(now <= 1.0 && now >= 0.0, "확률이 범위를 벗어났다: " + now);
                prev = now;
            }
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(30)
        @DisplayName("100만 개")
        void millionElements() {
            ProbabilisticSet<Integer> f = create(1_000_000, 0.01);
            for (int i = 0; i < 1_000_000; i++) {
                f.add(i);
            }
            Random rnd = new Random(5L);
            Set<Integer> sample = new HashSet<>();
            while (sample.size() < 10_000) {
                sample.add(rnd.nextInt(1_000_000));
            }
            for (int k : sample) {
                assertTrue(f.mightContain(k), "누락: " + k);
            }
        }
    }
}
