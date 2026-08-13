package com.datastructure.trie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("TrieProblems")
class TrieProblemsTest {

    @Nested
    @DisplayName("문제 1: 가장 긴 공통 접두사")
    class LongestCommonPrefix {

        @Test
        @DisplayName("갈라지는 지점까지")
        void branchPoint() {
            assertEquals("fl", TrieProblems.longestCommonPrefix(
                    new String[]{"flower", "flow", "flight"}));
            assertEquals("inters", TrieProblems.longestCommonPrefix(
                    new String[]{"interspecies", "interstellar", "interstate"}));
        }

        @Test
        @DisplayName("공통이 없으면 빈 문자열")
        void noCommon() {
            assertEquals("", TrieProblems.longestCommonPrefix(
                    new String[]{"dog", "racecar", "car"}));
        }

        @Test
        @DisplayName("한 단어가 다른 단어의 접두사면 거기서 멈춘다")
        void oneIsPrefixOfOther() {
            // 자식이 하나뿐이어도 "a" 에서 단어가 끝나므로 더 갈 수 없다.
            assertEquals("a", TrieProblems.longestCommonPrefix(new String[]{"a", "ab"}));
            assertEquals("a", TrieProblems.longestCommonPrefix(new String[]{"ab", "a"}));
            assertEquals("app", TrieProblems.longestCommonPrefix(
                    new String[]{"apple", "app", "application"}));
        }

        @Test
        @DisplayName("단어가 하나면 그 단어다")
        void single() {
            assertEquals("apple", TrieProblems.longestCommonPrefix(new String[]{"apple"}));
        }

        @Test
        @DisplayName("같은 단어만 여럿이면 그 단어다")
        void allSame() {
            assertEquals("ab", TrieProblems.longestCommonPrefix(new String[]{"ab", "ab", "ab"}));
        }

        @Test
        @DisplayName("빈 입력과 빈 단어")
        void empties() {
            assertEquals("", TrieProblems.longestCommonPrefix(new String[]{}));
            assertEquals("", TrieProblems.longestCommonPrefix(null));
            assertEquals("", TrieProblems.longestCommonPrefix(new String[]{""}));
            assertEquals("", TrieProblems.longestCommonPrefix(new String[]{"", "abc"}));
        }

        @Test
        @Timeout(15)
        @DisplayName("10만 단어")
        void manyWords() {
            String[] words = new String[100_000];
            for (int i = 0; i < words.length; i++) {
                words[i] = "commonprefix" + i;
            }
            assertTrue(TrieProblems.longestCommonPrefix(words).startsWith("commonprefix"));
        }
    }

    @Nested
    @DisplayName("문제 2: 자동완성 상위 k개")
    class Autocomplete {

        private MapTrie sample() {
            MapTrie t = new MapTrie();
            for (String w : List.of("car", "card", "care", "careful", "cars", "cat", "dog")) {
                t.insert(w);
            }
            return t;
        }

        @Test
        @DisplayName("사전순 앞에서 k 개")
        void topK() {
            assertEquals(List.of("car", "card", "care"),
                    TrieProblems.autocomplete(sample(), "car", 3));
            assertEquals(List.of("car"), TrieProblems.autocomplete(sample(), "car", 1));
        }

        @Test
        @DisplayName("k 가 남은 개수보다 크면 있는 만큼")
        void fewerThanK() {
            assertEquals(List.of("car", "card", "care", "careful", "cars"),
                    TrieProblems.autocomplete(sample(), "car", 100));
            assertEquals(List.of("dog"), TrieProblems.autocomplete(sample(), "dog", 5));
        }

        @Test
        @DisplayName("k 가 0 이하면 빈 리스트")
        void nonPositiveK() {
            assertEquals(List.of(), TrieProblems.autocomplete(sample(), "car", 0));
            assertEquals(List.of(), TrieProblems.autocomplete(sample(), "car", -1));
        }

        @Test
        @DisplayName("없는 접두사면 빈 리스트")
        void missingPrefix() {
            assertEquals(List.of(), TrieProblems.autocomplete(sample(), "z", 5));
            assertEquals(List.of(), TrieProblems.autocomplete(sample(), "carz", 5));
        }

        @Test
        @DisplayName("빈 접두사는 전체에서 앞 k 개")
        void emptyPrefix() {
            assertEquals(List.of("car", "card"), TrieProblems.autocomplete(sample(), "", 2));
        }

        @Test
        @DisplayName("접두사 자신이 첫 결과가 된다")
        void prefixItselfComesFirst() {
            assertEquals(List.of("car"), TrieProblems.autocomplete(sample(), "car", 1));
        }

        @Test
        @DisplayName("keysWithPrefix 의 앞부분과 같다")
        void agreesWithKeysWithPrefix() {
            MapTrie t = sample();
            for (String p : List.of("", "c", "ca", "car", "dog", "z")) {
                for (int k = 1; k <= 8; k++) {
                    List<String> all = t.keysWithPrefix(p);
                    List<String> expected = all.subList(0, Math.min(k, all.size()));
                    assertEquals(expected, TrieProblems.autocomplete(t, p, k),
                            "접두사 '" + p + "' k=" + k);
                }
            }
        }

        @Test
        @Timeout(20)
        @DisplayName("10만 개가 걸린 접두사에서 10개만 뽑는다")
        void stopsEarly() {
            // 전부 모으고 자르면 질의 하나가 10만 걸음이다. 그것을 10만 번 한다.
            MapTrie t = new MapTrie();
            for (int i = 0; i < 100_000; i++) {
                t.insert("a" + encode(i));
            }
            List<String> first = TrieProblems.autocomplete(t, "a", 10);
            assertEquals(10, first.size());
            for (int q = 0; q < 100_000; q++) {
                assertEquals(first, TrieProblems.autocomplete(t, "a", 10));
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

    @Nested
    @DisplayName("문제 3: 서로 다른 부분 문자열 개수")
    class DistinctSubstrings {

        @Test
        @DisplayName("겹치는 것이 없으면 n(n+1)/2")
        void allDistinct() {
            assertEquals(6, TrieProblems.countDistinctSubstrings("abc"));
            assertEquals(1, TrieProblems.countDistinctSubstrings("a"));
        }

        @Test
        @DisplayName("같은 것은 한 번만 센다")
        void deduplicates() {
            assertEquals(3, TrieProblems.countDistinctSubstrings("aaa"),
                    "a, aa, aaa 뿐이다. 6 이 나오면 중복을 세고 있다");
            assertEquals(7, TrieProblems.countDistinctSubstrings("abab"));
            assertEquals(15, TrieProblems.countDistinctSubstrings("banana"));
            assertEquals(53, TrieProblems.countDistinctSubstrings("mississippi"));
        }

        @Test
        @DisplayName("빈 입력은 0")
        void empty() {
            assertEquals(0, TrieProblems.countDistinctSubstrings(""));
            assertEquals(0, TrieProblems.countDistinctSubstrings(null));
        }

        @Test
        @Timeout(20)
        @DisplayName("길이 1000 이면 노드가 최대 50만 개다")
        void quadraticGrowth() {
            // 주기적인 문자열을 쓰면 안 된다. "abcabcabc..." 는 부분 문자열이 몇 개 안 나온다.
            java.util.Random rnd = new java.util.Random(2026L);
            StringBuilder sb = new StringBuilder(1000);
            for (int i = 0; i < 1000; i++) {
                sb.append((char) ('a' + rnd.nextInt(26)));
            }
            int n = TrieProblems.countDistinctSubstrings(sb.toString());
            assertTrue(n > 450_000 && n <= 1000 * 1001 / 2,
                    "부분 문자열 " + n + "개 - 길이의 제곱에 비례해서 늘어난다");
        }

        @Test
        @DisplayName("한 글자만 반복하면 길이만큼이다")
        void singleCharRepeated() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 500; i++) {
                sb.append('x');
            }
            assertEquals(500, TrieProblems.countDistinctSubstrings(sb.toString()));
        }
    }
}
