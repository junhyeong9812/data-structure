package com.datastructure.trie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("WordDictionary: 와일드카드 검색")
class WordDictionaryTest {

    private WordDictionary dict(String... words) {
        WordDictionary d = new WordDictionary();
        for (String w : words) {
            d.addWord(w);
        }
        return d;
    }

    @Nested
    @DisplayName("점이 없으면 그냥 트라이다")
    class NoWildcard {

        @Test
        @DisplayName("정확히 일치")
        void exact() {
            WordDictionary d = dict("bad", "dad", "mad");
            assertTrue(d.search("bad"));
            assertTrue(d.search("dad"));
            assertFalse(d.search("pad"));
            assertEquals(3, d.size());
        }

        @Test
        @DisplayName("접두사는 단어가 아니다")
        void prefixIsNotWord() {
            WordDictionary d = dict("apple");
            assertFalse(d.search("app"), "end 대신 자식 유무를 보면 여기서 true 가 나온다");
            assertFalse(d.search("appl"));
            assertTrue(d.search("apple"));
        }

        @Test
        @DisplayName("빈 사전은 무엇도 못 찾는다")
        void empty() {
            WordDictionary d = new WordDictionary();
            assertFalse(d.search("a"));
            assertFalse(d.search("."));
            assertFalse(d.search(""));
        }

        @Test
        @DisplayName("빈 문자열도 단어다")
        void emptyWord() {
            WordDictionary d = dict("");
            assertTrue(d.search(""));
            assertFalse(d.search("."));
        }
    }

    @Nested
    @DisplayName("점 하나는 글자 하나다")
    class SingleDot {

        @Test
        @DisplayName("자리마다")
        void eachPosition() {
            WordDictionary d = dict("bad", "dad", "mad");
            assertTrue(d.search(".ad"));
            assertTrue(d.search("b.d"));
            assertTrue(d.search("ba."));
        }

        @Test
        @DisplayName("길이가 다르면 안 맞는다")
        void lengthMatters() {
            WordDictionary d = dict("bad");
            assertFalse(d.search(".."), "두 글자짜리는 없다");
            assertFalse(d.search("...."), "네 글자짜리는 없다");
            assertTrue(d.search("..."));
        }

        @Test
        @DisplayName("맞는 갈래가 하나도 없으면 false")
        void noBranchMatches() {
            WordDictionary d = dict("bad", "dad");
            assertFalse(d.search(".ax"));
            assertFalse(d.search("x.d"));
        }
    }

    @Nested
    @DisplayName("점이 여럿")
    class ManyDots {

        @Test
        @DisplayName("전부 점이면 그 길이의 단어가 있느냐다")
        void allDots() {
            WordDictionary d = dict("a", "bb", "ccc");
            assertTrue(d.search("."));
            assertTrue(d.search(".."));
            assertTrue(d.search("..."));
            assertFalse(d.search("...."));
        }

        @Test
        @DisplayName("되돌아오기가 실제로 필요한 경우")
        void needsBacktracking() {
            // .b 로 시작하는 갈래가 둘이다. ab 쪽으로 먼저 내려가면 막히고,
            // 되돌아와 cb 쪽을 시도해야 답을 찾는다.
            WordDictionary d = dict("abx", "cby");
            assertTrue(d.search(".by"), "첫 갈래에서 막혔다고 false 를 반환하면 여기서 틀린다");
            assertTrue(d.search(".bx"));
            assertFalse(d.search(".bz"));
        }

        @Test
        @DisplayName("깊은 곳에서 갈라져도 찾는다")
        void deepBranch() {
            WordDictionary d = dict("aaaaaa", "aaaaab", "aaaaac");
            assertTrue(d.search("aaaaa."));
            assertTrue(d.search("....."  + "c"));
            assertFalse(d.search("aaaaa" + "d"));
            assertFalse(d.search("......." ));
        }

        @Test
        @DisplayName("긴 단어와 짧은 단어가 섞여 있어도")
        void mixedLengths() {
            WordDictionary d = dict("at", "and", "an", "add");
            assertTrue(d.search("a"  + "."));
            assertTrue(d.search("a"  + ".."));
            assertTrue(d.search(".n" + "d"));
            assertFalse(d.search("a" + "..."));
        }
    }

    @Nested
    @DisplayName("null 패턴")
    class NullPattern {

        @Test
        @DisplayName("예외")
        void throwsOnNull() {
            WordDictionary d = new WordDictionary();
            assertThrows(IllegalArgumentException.class, () -> d.search(null));
            assertThrows(IllegalArgumentException.class, () -> d.addWord(null));
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(15)
        @DisplayName("접두사가 고정되면 점이 있어도 싸다")
        void fixedPrefixIsCheap() {
            WordDictionary d = new WordDictionary();
            for (int i = 0; i < 100_000; i++) {
                d.addWord(encode(i));
            }
            for (int q = 0; q < 100_000; q++) {
                assertTrue(d.search("aaaaa."), "aaaaaa 는 0 을 인코딩한 것이다");
            }
        }

        private static String encode(int n) {
            char[] buf = new char[6];
            for (int i = 5; i >= 0; i--) {
                buf[i] = (char) ('a' + (n % 26));
                n /= 26;
            }
            return new String(buf);
        }
    }
}
