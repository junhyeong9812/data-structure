package com.datastructure.bitset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("BitSetProblems")
class BitSetProblemsTest {

    @Nested
    @DisplayName("문제 1: 에라토스테네스의 체")
    class Sieve {

        @Test
        @DisplayName("30 이하의 소수")
        void upToThirty() {
            BitVector p = BitSetProblems.sieve(30);
            assertEquals(List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29), p.toList());
            assertFalse(p.get(0), "0 은 소수가 아니다");
            assertFalse(p.get(1), "1 도 아니다");
            assertTrue(p.get(2));
        }

        @Test
        @DisplayName("개수")
        void counts() {
            assertEquals(25, BitSetProblems.sieve(100).cardinality());
            assertEquals(168, BitSetProblems.sieve(1000).cardinality());
            assertEquals(1229, BitSetProblems.sieve(10_000).cardinality());
        }

        @Test
        @DisplayName("경계")
        void boundaries() {
            assertEquals(List.of(2), BitSetProblems.sieve(2).toList());
            assertEquals(List.of(2, 3), BitSetProblems.sieve(3).toList());
            assertEquals(List.of(2, 3), BitSetProblems.sieve(4).toList(), "4 는 소수가 아니다");
            assertThrows(IllegalArgumentException.class, () -> BitSetProblems.sieve(1));
        }

        @Test
        @Timeout(20)
        @DisplayName("100만까지")
        void toMillion() {
            // 여기가 비트셋을 쓰는 이유다. boolean[] 이면 1MB, 비트셋이면 125KB.
            BitVector p = BitSetProblems.sieve(1_000_000);
            assertEquals(78_498, p.cardinality());
            assertTrue(p.get(999_983), "999983 은 100만 이하 최대 소수다");
            assertFalse(p.get(999_984));
        }

        @Test
        @DisplayName("p*p 부터 지우는 이유")
        void startsAtSquare() {
            // 2p, 3p ... 는 더 작은 소수가 이미 지웠다. p*p 부터가 낭비 없는 시작점이다.
            // 그리고 p*p 가 int 를 넘칠 수 있어 long 으로 비교해야 한다.
            BitVector p = BitSetProblems.sieve(50);
            assertEquals(List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47),
                    p.toList());
        }
    }

    @Nested
    @DisplayName("문제 2: 자카드 유사도")
    class Jaccard {

        @Test
        @DisplayName("교집합 나누기 합집합")
        void basic() {
            BitVector a = new WordBitSet(10);
            BitVector b = new WordBitSet(10);
            for (int i : new int[]{0, 1, 2, 3}) {
                a.set(i);
            }
            for (int i : new int[]{2, 3, 4}) {
                b.set(i);
            }
            assertEquals(2.0 / 5, BitSetProblems.jaccard(a, b), 1e-9);
        }

        @Test
        @DisplayName("같으면 1, 안 겹치면 0")
        void extremes() {
            BitVector a = new WordBitSet(10);
            BitVector b = new WordBitSet(10);
            a.set(1);
            a.set(2);
            b.set(1);
            b.set(2);
            assertEquals(1.0, BitSetProblems.jaccard(a, b), 1e-9);

            BitVector c = new WordBitSet(10);
            c.set(5);
            assertEquals(0.0, BitSetProblems.jaccard(a, c), 1e-9);
        }

        @Test
        @DisplayName("둘 다 비면 1 이다")
        void bothEmpty() {
            // 0/0 을 어떻게 정할 것인가. "같다"로 보는 것이 관례다.
            // **정하지 않으면 NaN 이 나와 비교가 조용히 전부 false 가 된다.**
            BitVector a = new WordBitSet(10);
            BitVector b = new WordBitSet(10);
            double j = BitSetProblems.jaccard(a, b);
            assertEquals(1.0, j, 1e-9);
            assertFalse(Double.isNaN(j), "NaN 이 나오면 비교가 전부 false 가 된다");
        }

        @Test
        @DisplayName("구현이 달라도 답이 같다")
        void acrossImplementations() {
            BitVector packed = new WordBitSet(100);
            BitVector sparse = new SparseBitSet(100);
            for (int i = 0; i < 50; i++) {
                packed.set(i);
            }
            for (int i = 25; i < 75; i++) {
                sparse.set(i);
            }
            assertEquals(25.0 / 75, BitSetProblems.jaccard(packed, sparse), 1e-9);
        }

        @Test
        @DisplayName("크기가 다르면 거부한다")
        void sizeMismatch() {
            assertThrows(IllegalArgumentException.class,
                    () -> BitSetProblems.jaccard(new WordBitSet(10), new WordBitSet(20)));
            assertThrows(IllegalArgumentException.class,
                    () -> BitSetProblems.jaccard(null, new WordBitSet(10)));
        }
    }

    @Nested
    @DisplayName("문제 3: 부분집합 열거")
    class Subsets {

        @Test
        @DisplayName("s = (s-1) & mask 로 전부 돈다")
        void enumerates() {
            // 1011 의 부분집합은 1011, 1010, 1001, 1000, 0011, 0010, 0001, 0000 이다.
            // **켜진 비트만 골라 도는 것**이지 0..mask 를 전부 도는 것이 아니다.
            assertEquals(List.of(11, 10, 9, 8, 3, 2, 1, 0),
                    BitSetProblems.enumerateSubsets(0b1011));
        }

        @Test
        @DisplayName("개수는 2^(켜진 비트 수)")
        void countIsPowerOfTwo() {
            for (int mask : new int[]{0b1, 0b11, 0b111, 0b1010, 0b10101}) {
                assertEquals(1 << Integer.bitCount(mask),
                        BitSetProblems.enumerateSubsets(mask).size(),
                        "마스크 " + Integer.toBinaryString(mask));
            }
        }

        @Test
        @DisplayName("0 은 자기 자신 하나뿐")
        void zeroMask() {
            assertEquals(List.of(0), BitSetProblems.enumerateSubsets(0));
        }

        @Test
        @DisplayName("전부 켜진 마스크")
        void fullMask() {
            assertEquals(List.of(7, 6, 5, 4, 3, 2, 1, 0), BitSetProblems.enumerateSubsets(7));
        }

        @Test
        @DisplayName("내림차순이고 중복이 없다")
        void descendingAndDistinct() {
            List<Integer> out = BitSetProblems.enumerateSubsets(0b110110);
            assertEquals(1 << 4, out.size());
            for (int i = 1; i < out.size(); i++) {
                assertTrue(out.get(i - 1) > out.get(i), "내림차순이 아니다");
            }
            for (int s : out) {
                assertEquals(s, s & 0b110110, s + " 는 마스크의 부분집합이 아니다");
            }
        }

        @Test
        @DisplayName("음수는 거부한다")
        void negativeRejected() {
            assertThrows(IllegalArgumentException.class,
                    () -> BitSetProblems.enumerateSubsets(-1));
        }
    }
}
