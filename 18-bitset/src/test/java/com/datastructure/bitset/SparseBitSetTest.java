package com.datastructure.bitset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("SparseBitSet: 켜진 워드만 담는다")
class SparseBitSetTest extends BitVectorContractTest {

    @Override
    protected BitVector create(int size) {
        return new SparseBitSet(size);
    }

    @Nested
    @DisplayName("워드를 만들었다 지운다")
    class WordLifecycle {

        @Test
        @DisplayName("켜면 생기고 다 끄면 사라진다")
        void createsAndRemovesWords() {
            SparseBitSet v = new SparseBitSet(1000);
            assertEquals(0, v.unitCount());
            v.set(100);
            assertEquals(1, v.unitCount());
            v.set(101);
            assertEquals(1, v.unitCount(), "같은 워드라 안 늘어난다");
            v.set(500);
            assertEquals(2, v.unitCount());

            v.clear(100);
            assertEquals(2, v.unitCount(), "아직 101 이 남아 있다");
            v.clear(101);
            assertEquals(1, v.unitCount(), "빈 워드는 지워야 한다. 안 지우면 희소성이 사라진다");
            v.clearAll();
            assertEquals(0, v.unitCount());
        }
    }

    @Nested
    @DisplayName("한계: 빽빽하면 오히려 손해다")
    class WorseWhenDense {

        @Test
        @DisplayName("절반만 채워도 WordBitSet 보다 크다")
        void denseIsWorse() {
            // 맵 엔트리 하나가 워드 하나(8바이트)보다 훨씬 비싸다.
            // **1/6 보다 촘촘해지면 지는 거래다.** 공짜로 좋아지는 것은 없다.
            int n = 100_000;
            SparseBitSet sparse = new SparseBitSet(n);
            WordBitSet packed = new WordBitSet(n);
            for (int i = 0; i < n; i += 2) {
                sparse.set(i);
                packed.set(i);
            }
            assertEquals(sparse.cardinality(), packed.cardinality());
            assertTrue(sparse.memoryBytes() > packed.memoryBytes() * 5,
                    "희소 " + sparse.memoryBytes() + " 대 압축 " + packed.memoryBytes());
        }

        @Test
        @DisplayName("아주 드물면 크게 이긴다")
        void sparseWinsBig() {
            int n = 10_000_000;
            SparseBitSet sparse = new SparseBitSet(n);
            for (int i = 0; i < 5; i++) {
                sparse.set(i * 2_000_000);
            }
            WordBitSet packed = new WordBitSet(n);
            for (int i = 0; i < 5; i++) {
                packed.set(i * 2_000_000);
            }
            assertEquals(5, sparse.unitCount());
            assertEquals(156_250, packed.unitCount());
            assertTrue(sparse.memoryBytes() * 1000 < packed.memoryBytes(),
                    "희소 " + sparse.memoryBytes() + " 대 압축 " + packed.memoryBytes());
        }
    }
}
