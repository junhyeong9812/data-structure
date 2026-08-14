package com.datastructure.bitset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("WordBitSet: long 에 눌러 담기")
class WordBitSetTest extends BitVectorContractTest {

    @Override
    protected BitVector create(int size) {
        return new WordBitSet(size);
    }

    @Nested
    @DisplayName("워드 계산")
    class WordMath {

        @Test
        @DisplayName("64비트마다 워드 하나")
        void wordCount() {
            assertEquals(1, WordBitSet.wordCountFor(1));
            assertEquals(1, WordBitSet.wordCountFor(64));
            assertEquals(2, WordBitSet.wordCountFor(65));
            assertEquals(2, WordBitSet.wordCountFor(128));
            assertEquals(3, WordBitSet.wordCountFor(129));
            assertEquals(15_625, WordBitSet.wordCountFor(1_000_000));
        }

        @Test
        @DisplayName("비트 자리에서 워드 번호와 마스크")
        void indexAndMask() {
            assertEquals(0, WordBitSet.wordIndex(0));
            assertEquals(0, WordBitSet.wordIndex(63));
            assertEquals(1, WordBitSet.wordIndex(64));
            assertEquals(1, WordBitSet.wordIndex(127));
            assertEquals(15_624, WordBitSet.wordIndex(999_999));

            assertEquals(1L, WordBitSet.mask(0));
            assertEquals(2L, WordBitSet.mask(1));
            assertEquals(Long.MIN_VALUE, WordBitSet.mask(63), "63번은 부호 비트다");
            assertEquals(1L, WordBitSet.mask(64), "64번은 다음 워드의 0번 자리다");
        }

        @Test
        @DisplayName("실제로 눌러 담긴다")
        void bitsArePacked() {
            WordBitSet v = new WordBitSet(128);
            v.set(0);
            v.set(1);
            v.set(63);
            assertEquals(0x8000000000000003L, v.word(0), "0, 1, 63 번 비트가 한 워드에 있다");
            assertEquals(0L, v.word(1));
            v.set(64);
            assertEquals(1L, v.word(1));
        }
    }

    @Nested
    @DisplayName("한계 1: boolean[] 대비 8분의 1")
    class MemoryAgainstNaive {

        @Test
        @DisplayName("100만 비트에서 1MB 가 125KB 가 된다")
        void eightTimesSmaller() {
            BitVector naive = new BooleanArrayBitSet(1_000_000);
            BitVector packed = new WordBitSet(1_000_000);
            assertEquals(1_000_000, naive.memoryBytes());
            assertEquals(125_000, packed.memoryBytes());
            assertEquals(8, naive.memoryBytes() / packed.memoryBytes());
        }

        @Test
        @DisplayName("집합 연산이 도는 단위 수가 64분의 1이다")
        void sixtyFourTimesFewerSteps() {
            // and/or/xor 는 정확히 unitCount 만큼 돈다.
            // **이게 비트맵 인덱스가 존재하는 이유다.** 메모리보다 이쪽이 크다.
            assertEquals(1_000_000, new BooleanArrayBitSet(1_000_000).unitCount());
            assertEquals(15_625, new WordBitSet(1_000_000).unitCount());
            assertEquals(64, 1_000_000 / 15_625);
        }

        @Test
        @Timeout(25)
        @DisplayName("실제로 빠르다")
        void actuallyFaster() {
            // 나이브 구현이면 10^9 걸음이라 이 안에 못 끝난다.
            int n = 1_000_000;
            WordBitSet a = new WordBitSet(n);
            WordBitSet b = new WordBitSet(n);
            for (int i = 0; i < n; i += 3) {
                a.set(i);
            }
            for (int i = 0; i < n; i += 5) {
                b.set(i);
            }
            for (int round = 0; round < 1000; round++) {
                WordBitSet c = new WordBitSet(n);
                c.or(a);
                c.and(b);
                assertTrue(c.cardinality() > 0);
            }
        }
    }

    @Nested
    @DisplayName("한계 2: 밀도가 낮으면 낭비다")
    class WastefulWhenSparse {

        @Test
        @DisplayName("10개만 켜도 15,625 워드를 잡는다")
        void allocatesRegardlessOfDensity() {
            // 고칠 수 없는 성질이다. 크기를 미리 잡는 구조라 그렇다.
            // SparseBitSet 이 이 문제를 다른 방식으로 푼다.
            WordBitSet v = new WordBitSet(1_000_000);
            for (int i = 0; i < 10; i++) {
                v.set(i * 100_000);
            }
            assertEquals(10, v.cardinality());
            assertEquals(15_625, v.unitCount(), "켜진 것이 10개인데 워드는 15,625개다");
            assertEquals(125_000, v.memoryBytes());

            SparseBitSet sparse = new SparseBitSet(1_000_000);
            for (int i = 0; i < 10; i++) {
                sparse.set(i * 100_000);
            }
            assertEquals(10, sparse.unitCount(), "실제로 쓰는 워드만 갖는다");
            assertTrue(sparse.memoryBytes() < v.memoryBytes() / 200,
                    "희소할 때는 " + sparse.memoryBytes() + " 대 " + v.memoryBytes());
        }
    }
}
