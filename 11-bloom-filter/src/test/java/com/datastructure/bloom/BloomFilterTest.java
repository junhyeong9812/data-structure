package com.datastructure.bloom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("BloomFilter: 비트 배열 하나")
class BloomFilterTest extends ProbabilisticSetContractTest {

    @Override
    protected ProbabilisticSet<Integer> create(int expectedInsertions, double fpr) {
        return new BloomFilter<>(expectedInsertions, fpr);
    }

    @Nested
    @DisplayName("크기 공식")
    class Sizing {

        @Test
        @DisplayName("m = -n ln(p) / (ln2)^2")
        void bits() {
            assertEquals(9586, BloomFilter.optimalBits(1000, 0.01));
            assertEquals(14378, BloomFilter.optimalBits(1000, 0.001));
            assertEquals(4793, BloomFilter.optimalBits(1000, 0.1));
            assertEquals(9_585_059, BloomFilter.optimalBits(1_000_000, 0.01));
        }

        @Test
        @DisplayName("올림한다. 내리면 목표 오탐률을 넘는다")
        void roundsUp() {
            assertEquals(10, BloomFilter.optimalBits(1, 0.01), "9.586 은 10 이어야 한다");
        }

        @Test
        @DisplayName("k = (m/n) ln2, 최소 1")
        void hashCount() {
            assertEquals(7, BloomFilter.optimalHashCount(9586, 1000));
            assertEquals(10, BloomFilter.optimalHashCount(14378, 1000));
            assertEquals(3, BloomFilter.optimalHashCount(4793, 1000));
            assertEquals(1, BloomFilter.optimalHashCount(145, 100), "1.45 -> 1");
            assertEquals(1, BloomFilter.optimalHashCount(1, 1000),
                    "정수로 나누면 0 이 나온다. 최소 1 이어야 한다");
        }

        @Test
        @DisplayName("원소를 담지 않으므로 크기가 고정이다")
        void fixedSize() {
            BloomFilter<Integer> f = new BloomFilter<>(1000, 0.01);
            long before = f.bitSize();
            for (int i = 0; i < 5000; i++) {
                f.add(i);
            }
            assertEquals(before, f.bitSize(), "용량의 5배를 넣어도 크기가 그대로다");
        }

        @Test
        @DisplayName("1% 오탐이면 원소당 약 9.6비트다")
        void bitsPerElement() {
            BloomFilter<Integer> f = new BloomFilter<>(1_000_000, 0.01);
            double perElement = (double) f.bitSize() / 1_000_000;
            assertTrue(perElement > 9.5 && perElement < 9.7, "원소당 " + perElement + "비트");
            assertEquals(7, f.hashCount());

            // 정확한 HashSet<Integer> 은 원소 하나에 최소 수십 바이트를 쓴다.
            // 여기서는 1.2MB 로 100만 개를 담는다.
            assertTrue(f.bitSize() / 8 < 1_300_000, "약 1.2MB 여야 한다");
        }
    }

    @Nested
    @DisplayName("이중 해싱")
    class DoubleHashing {

        @Test
        @DisplayName("같은 원소는 늘 같은 자리를 준다")
        void deterministic() {
            BloomFilter<Integer> f = new BloomFilter<>(1000, 0.01);
            assertArrayEquals(f.indexes(42), f.indexes(42));
            assertArrayEquals(f.indexes(-7), f.indexes(-7));
        }

        @Test
        @DisplayName("자리가 k 개이고 전부 범위 안이다")
        void countAndRange() {
            BloomFilter<Integer> f = new BloomFilter<>(1000, 0.01);
            for (int key : new int[]{0, 1, -1, 42, Integer.MIN_VALUE, Integer.MAX_VALUE}) {
                int[] idx = f.indexes(key);
                assertEquals(f.hashCount(), idx.length);
                for (int i : idx) {
                    assertTrue(i >= 0 && i < f.bitSize(),
                            "자리 " + i + " 가 범위 밖이다. floorMod 를 안 썼을 수 있다");
                }
            }
        }

        @Test
        @DisplayName("연속된 정수가 한곳에 몰리지 않는다")
        void spreadsSequentialKeys() {
            // Integer 의 hashCode 는 값 그대로다. 섞지 않고 쓰면 0..999 가
            // 비트 배열 앞쪽 1000칸에 그대로 줄을 선다.
            BloomFilter<Integer> f = new BloomFilter<>(1000, 0.01);
            int firstTenth = 0;
            int total = 0;
            for (int key = 0; key < 1000; key++) {
                for (int i : f.indexes(key)) {
                    total++;
                    if (i < f.bitSize() / 10) {
                        firstTenth++;
                    }
                }
            }
            double share = (double) firstTenth / total;
            assertTrue(share > 0.05 && share < 0.15,
                    "앞 10% 구간에 자리의 " + share + " 가 몰렸다. 해시를 안 섞은 것이다");
        }

        @Test
        @DisplayName("해시 하나가 같아도 자리 전체가 같지는 않다")
        void notAllSame() {
            BloomFilter<Integer> f = new BloomFilter<>(1000, 0.01);
            int[] idx = f.indexes(12345);
            boolean allEqual = true;
            for (int i = 1; i < idx.length; i++) {
                if (idx[i] != idx[0]) {
                    allEqual = false;
                    break;
                }
            }
            assertTrue(!allEqual, "자리 " + idx.length + "개가 전부 같다. h2 가 0 이었을 수 있다");
        }
    }

    @Nested
    @DisplayName("비트 조작")
    class Bits {

        @Test
        @DisplayName("add 가 그 자리들을 실제로 켠다")
        void addTurnsBitsOn() {
            BloomFilter<Integer> f = new BloomFilter<>(1000, 0.01);
            int[] idx = f.indexes(42);
            for (int i : idx) {
                assertTrue(!f.bit(i), "넣기 전인데 " + i + " 가 켜져 있다");
            }
            f.add(42);
            for (int i : idx) {
                assertTrue(f.bit(i), i + " 가 안 켜졌다");
            }
        }

        @Test
        @DisplayName("64비트 경계를 넘는 자리도 켜진다")
        void crossesWordBoundary() {
            // words[idx/64] 의 (idx%64) 비트다. >>> 6 과 & 63 을 잘못 쓰면
            // 63 -> 64 를 넘을 때 엉뚱한 워드를 건드린다.
            BloomFilter<Integer> f = new BloomFilter<>(1000, 0.5);
            for (int i = 0; i < f.bitSize(); i++) {
                assertTrue(!f.bit(i));
            }
            for (int key = 0; key < 500; key++) {
                f.add(key);
            }
            int on = 0;
            for (int i = 0; i < f.bitSize(); i++) {
                if (f.bit(i)) {
                    on++;
                }
            }
            assertTrue(on > 0 && on < f.bitSize(), "켜진 비트 " + on + " / " + f.bitSize());
        }
    }

    @Nested
    @DisplayName("예상 오탐률이 실측과 맞는가")
    class RateAccuracy {

        @Test
        @DisplayName("공식과 실측이 두 배 안에서 맞는다")
        void formulaMatchesReality() {
            BloomFilter<Integer> f = new BloomFilter<>(10_000, 0.01);
            for (int i = 0; i < 10_000; i++) {
                f.add(i);
            }
            double expected = f.expectedFalsePositiveRate();
            int hits = 0;
            for (int i = 5_000_000; i < 5_200_000; i++) {
                if (f.mightContain(i)) {
                    hits++;
                }
            }
            double measured = hits / 200_000.0;
            assertTrue(expected > 0.005 && expected < 0.02, "공식값 " + expected);
            assertTrue(measured < expected * 2 + 0.002,
                    "실측 " + measured + " 가 공식 " + expected + " 의 두 배를 넘는다");
        }

        @Test
        @DisplayName("한계: 용량을 넘기면 오탐률이 급격히 오른다")
        void overloadDegrades() {
            // 이걸 고칠 방법이 없다. 원소를 담지 않았으니 다시 배치할 수도 없다.
            // 같은 문제집의 ScalableBloomFilter 가 이 문제를 푼다.
            BloomFilter<Integer> f = new BloomFilter<>(1000, 0.01);
            for (int i = 0; i < 1000; i++) {
                f.add(i);
            }
            double atCapacity = f.expectedFalsePositiveRate();
            for (int i = 1000; i < 5000; i++) {
                f.add(i);
            }
            double overloaded = f.expectedFalsePositiveRate();

            assertTrue(atCapacity < 0.02, "설계 용량에서는 1% 근처다: " + atCapacity);
            assertTrue(overloaded > 0.5,
                    "5배 넣으면 오탐률이 " + overloaded + " 가 된다. 1% 짜리 필터가 아니게 된다");
        }
    }
}
