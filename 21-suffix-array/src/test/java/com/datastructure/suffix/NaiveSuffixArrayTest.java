package com.datastructure.suffix;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("NaiveSuffixArray: 비교 정렬 기준선")
class NaiveSuffixArrayTest extends SuffixArrayContractTest {

    @Override
    protected int[] build(String text) {
        return NaiveSuffixArray.of(text);
    }

    @Nested
    @DisplayName("한계: 비교 한 번이 O(n) 이다")
    class ComparisonCost {

        @Test
        @DisplayName("같은 글자만 있으면 비교마다 끝까지 훑는다")
        void allSameLetters() {
            // 접미사 "aaa...a" 끼리는 짧은 쪽이 끝날 때까지 전부 같다.
            // **비교 하나가 문자열 하나를 통째로 읽는다.**
            NaiveSuffixArray naive = new NaiveSuffixArray("a".repeat(1000));
            assertTrue(naive.charComparisons() > 100_000,
                    "훑은 글자 수: " + naive.charComparisons());
        }

        @Test
        @DisplayName("글자가 다양하면 비교가 일찍 끝난다")
        void distinctLettersAreCheap() {
            // 첫 글자에서 갈리면 비교 하나가 글자 하나다. 나이브가 실제로 빠른 경우다.
            // **최악과 평균이 이렇게 다르다는 것도 정보다.**
            String varied = SuffixFixtures.pseudoRandom(1000, 26, 3);
            NaiveSuffixArray fast = new NaiveSuffixArray(varied);
            NaiveSuffixArray slow = new NaiveSuffixArray("a".repeat(1000));
            assertTrue(fast.charComparisons() * 10 < slow.charComparisons(),
                    "다양: " + fast.charComparisons() + ", 같은 글자: " + slow.charComparisons());
        }

        @Test
        @DisplayName("길이를 4배로 하면 훑는 글자가 10배 넘게 는다")
        void quadraticGrowth() {
            // n(n-1)/2 로 는다. 길이가 4배면 훑는 글자는 16배다.
            long small = new NaiveSuffixArray("a".repeat(500)).charComparisons();
            long large = new NaiveSuffixArray("a".repeat(2000)).charComparisons();
            assertTrue(large > small * 10,
                    "500 글자: " + small + ", 2000 글자: " + large);
        }

        @Test
        @DisplayName("배가법과 결과는 같다")
        void sameResultAsDoubling() {
            for (String s : new String[]{"banana", "mississippi", "a".repeat(200),
                    SuffixFixtures.pseudoRandom(500, 3, 17)}) {
                assertArrayEquals(new SuffixArray(s).toArray(), NaiveSuffixArray.of(s));
            }
        }

        @Test
        @DisplayName("빈 문자열은 비교가 없다")
        void emptyCostsNothing() {
            assertEquals(0, new NaiveSuffixArray("").charComparisons());
            assertEquals(0, new NaiveSuffixArray("a").charComparisons());
        }
    }
}
