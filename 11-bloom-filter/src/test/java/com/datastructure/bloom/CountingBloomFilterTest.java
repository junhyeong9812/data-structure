package com.datastructure.bloom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CountingBloomFilter: 삭제가 되는")
class CountingBloomFilterTest extends ProbabilisticSetContractTest {

    @Override
    protected ProbabilisticSet<Integer> create(int expectedInsertions, double fpr) {
        return new CountingBloomFilter<>(expectedInsertions, fpr);
    }

    private CountingBloomFilter<Integer> counting(int n, double p) {
        return new CountingBloomFilter<>(n, p);
    }

    @Nested
    @DisplayName("삭제")
    class Removal {

        @Test
        @DisplayName("지우면 없어진다")
        void removeWorks() {
            CountingBloomFilter<Integer> f = counting(100, 0.01);
            f.add(42);
            assertTrue(f.mightContain(42));
            assertTrue(f.remove(42));
            assertFalse(f.mightContain(42));
            assertEquals(0, f.insertedCount());
        }

        @Test
        @DisplayName("없는 것은 못 지운다")
        void removeMissing() {
            CountingBloomFilter<Integer> f = counting(100, 0.01);
            f.add(1);
            assertFalse(f.remove(999));
            assertEquals(1, f.insertedCount());
        }

        @Test
        @DisplayName("자리를 공유하는 다른 원소는 살아남는다")
        void sharedSlotsSurvive() {
            // 기본형이 삭제를 못 하는 이유가 바로 이것이다.
            // 비트를 끄면 같은 자리를 쓰는 원소가 같이 사라진다.
            CountingBloomFilter<Integer> f = counting(1000, 0.01);
            for (int i = 0; i < 1000; i++) {
                f.add(i);
            }
            for (int i = 0; i < 500; i++) {
                assertTrue(f.remove(i), i + " 를 못 지웠다");
            }
            for (int i = 500; i < 1000; i++) {
                assertTrue(f.mightContain(i),
                        i + " 가 사라졌다. 남의 계수기를 0 까지 내렸다는 뜻이다");
            }
            assertEquals(500, f.insertedCount());
        }

        @Test
        @DisplayName("넣고 지우기를 반복해도 계수기가 안 샌다")
        void addRemoveCycles() {
            CountingBloomFilter<Integer> f = counting(100, 0.01);
            for (int round = 0; round < 1000; round++) {
                f.add(7);
                assertTrue(f.remove(7));
            }
            assertFalse(f.mightContain(7), "계수기가 0 으로 돌아오지 않았다");
            assertEquals(0, f.insertedCount());
        }

        @Test
        @DisplayName("같은 원소를 두 번 넣으면 두 번 지워야 한다")
        void countsAreRealCounts() {
            CountingBloomFilter<Integer> f = counting(100, 0.01);
            f.add(5);
            f.add(5);
            assertTrue(f.remove(5));
            assertTrue(f.mightContain(5), "두 번 넣었으니 한 번 지워도 남아 있어야 한다");
            assertTrue(f.remove(5));
            assertFalse(f.mightContain(5));
        }
    }

    @Nested
    @DisplayName("메모리 대가")
    class MemoryCost {

        @Test
        @DisplayName("비트 하나가 바이트 하나가 된다")
        void eightTimesLarger() {
            BloomFilter<Integer> plain = new BloomFilter<>(10_000, 0.01);
            CountingBloomFilter<Integer> counted = counting(10_000, 0.01);
            assertEquals(8, counted.bitsPerSlot());
            assertEquals(plain.bitSize() * 8, counted.bitSize(),
                    "같은 자리 수에 8배 메모리를 쓴다");
        }
    }

    @Nested
    @DisplayName("한계 1: 계수기 포화")
    class Saturation {

        @Test
        @DisplayName("255 를 넘으면 더 못 세고, 그 자리는 영원히 안 내려간다")
        void saturatedSlotsAreStuck() {
            CountingBloomFilter<Integer> f = counting(100, 0.01);
            for (int i = 0; i < 300; i++) {
                f.add(42);
            }
            assertTrue(f.saturations() > 0, "300번 넣었으면 255를 넘었어야 한다");

            for (int i = 0; i < 300; i++) {
                f.remove(42);
            }
            assertTrue(f.mightContain(42),
                    "포화된 자리는 안 내려간다. 300번 넣고 300번 지워도 남는다");

            // 4비트 계수기를 쓰는 실무 구현에서는 15만 넘어도 이렇게 된다.
            // 메모리를 절반 더 줄이는 대신 포화가 훨씬 쉽게 일어난다.
        }
    }

    @Nested
    @DisplayName("한계 2: 오탐을 지우면 누락이 생긴다")
    class RemovingFalsePositiveBreaksContract {

        @Test
        @DisplayName("넣은 적 없는 것을 지우면 넣은 것이 사라진다")
        void falseNegativeAppears() {
            // 이 자료구조가 삭제를 얻는 대신 치르는 값이다.
            // 블룸 필터의 유일한 보장("누락 없음")이 여기서 깨진다.
            //
            // 오탐이 났다고 늘 누락이 생기지는 않는다. 그 자리의 계수기가 2 이상이면
            // 하나 내려도 0 이 안 되기 때문이다. 그래서 **계수기가 1 인 자리를 밟는 오탐**이 필요하다.
            // 아래는 그런 경우를 실제로 찾아 보여준다.
            int ghost = -1;
            int lost = 0;

            for (int candidate = 1000; candidate < 50_000 && lost == 0; candidate++) {
                CountingBloomFilter<Integer> f = counting(200, 0.5);   // 자리 하나짜리 해시
                for (int i = 0; i < 100; i++) {
                    f.add(i);
                }
                if (!f.mightContain(candidate)) {
                    continue;
                }
                assertTrue(f.remove(candidate), "필터는 이걸 진짜라고 믿는다");
                int missing = 0;
                for (int i = 0; i < 100; i++) {
                    if (!f.mightContain(i)) {
                        missing++;
                    }
                }
                if (missing > 0) {
                    ghost = candidate;
                    lost = missing;
                }
            }

            assertTrue(lost > 0,
                    "넣은 적 없는 원소를 지워 누락이 생기는 경우를 못 찾았다");
            assertTrue(ghost >= 0);
        }
    }
}
