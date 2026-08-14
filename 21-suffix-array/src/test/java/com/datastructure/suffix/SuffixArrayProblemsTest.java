package com.datastructure.suffix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("SuffixArrayProblems")
class SuffixArrayProblemsTest {

    @Nested
    @DisplayName("문제 1: 서로 다른 부분 문자열 수")
    class CountDistinct {

        @Test
        @DisplayName("작은 예")
        void smallCases() {
            assertEquals(6, SuffixArrayProblems.countDistinctSubstrings("abc"));
            assertEquals(3, SuffixArrayProblems.countDistinctSubstrings("aaa"));
            assertEquals(15, SuffixArrayProblems.countDistinctSubstrings("banana"));
            assertEquals(53, SuffixArrayProblems.countDistinctSubstrings("mississippi"));
            assertEquals(54, SuffixArrayProblems.countDistinctSubstrings("abracadabra"));
            assertEquals(7, SuffixArrayProblems.countDistinctSubstrings("abab"));
            assertEquals(9, SuffixArrayProblems.countDistinctSubstrings("ababa"));
            assertEquals(12, SuffixArrayProblems.countDistinctSubstrings("cacao"));
        }

        @Test
        @DisplayName("빈 입력과 null 은 0")
        void emptyIsZero() {
            assertEquals(0, SuffixArrayProblems.countDistinctSubstrings(""));
            assertEquals(0, SuffixArrayProblems.countDistinctSubstrings(null));
            assertEquals(1, SuffixArrayProblems.countDistinctSubstrings("a"));
        }

        @Test
        @DisplayName("09번 접미사 트라이와 같은 답을 낸다")
        void agreesWithSuffixTrie() {
            // **같은 문제, 완전히 다른 방법이다.**
            // 09번은 접미사를 트라이에 밀어 넣고 만들어진 노드를 셌다. 노드가 O(n^2) 다.
            // 여기서는 n(n+1)/2 에서 LCP 합을 뺀다. 배열 두 개, O(n) 공간이다.
            for (int trial = 0; trial < 200; trial++) {
                String s = SuffixFixtures.pseudoRandom(trial % 31, 2 + trial % 4, trial * 17L + 7);
                assertEquals(SuffixFixtures.suffixTrieNodes(s),
                        SuffixArrayProblems.countDistinctSubstrings(s),
                        "입력 " + s);
            }
        }

        @Test
        @DisplayName("부분 문자열을 집합에 다 넣은 것과도 같다")
        void agreesWithBruteForceSet() {
            // 파이썬으로 치면 len({s[i:j]}) 다. 확실하지만 O(n^2) 개를 담는다.
            for (int trial = 0; trial < 200; trial++) {
                String s = SuffixFixtures.pseudoRandom(trial % 26, 2 + trial % 3, trial * 41L + 13);
                assertEquals(SuffixFixtures.distinctByBruteForce(s),
                        SuffixArrayProblems.countDistinctSubstrings(s),
                        "입력 " + s);
            }
        }

        @Test
        @DisplayName("전부 다른 글자면 n(n+1)/2 다")
        void allDistinctCharacters() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 200; i++) {
                sb.append((char) (0x100 + i));
            }
            assertEquals(200L * 201 / 2, SuffixArrayProblems.countDistinctSubstrings(sb.toString()));
        }

        @Test
        @Timeout(60)
        @DisplayName("한계 돌파: 길이 10만. 09번은 여기서 못 돈다")
        void hundredThousand() {
            // 09번 트라이면 노드가 50억 개다. 아예 못 만든다.
            // 여기서는 int 배열 두 개로 끝난다. **답이 int 범위도 넘는다.**
            int n = 100_000;
            assertEquals(100_000L, SuffixArrayProblems.countDistinctSubstrings("a".repeat(n)));
            assertEquals(199_999L, SuffixArrayProblems.countDistinctSubstrings("ab".repeat(n / 2)));

            long distinct = SuffixArrayProblems.countDistinctSubstrings(
                    SuffixFixtures.pseudoRandom(n, 4, 1));
            assertEquals(4_999_299_684L, distinct);
            assertTrue(distinct > Integer.MAX_VALUE, "int 로는 담기지 않는다: " + distinct);
        }

        @Test
        @DisplayName("길이 1000 에서 트라이와 대조한다")
        void thousandAgainstTrie() {
            // 09번이 실질적으로 버틴 마지막 크기다. 노드 49만 개를 만든다.
            String s = SuffixFixtures.pseudoRandom(1000, 4, 1);
            assertEquals(496_341L, SuffixFixtures.suffixTrieNodes(s));
            assertEquals(496_341L, SuffixArrayProblems.countDistinctSubstrings(s));
        }
    }

    @Nested
    @DisplayName("문제 2: 가장 긴 반복 부분 문자열")
    class LongestRepeated {

        @Test
        @DisplayName("LCP 의 최댓값이 곧 답이다")
        void smallCases() {
            assertEquals("ana", SuffixArrayProblems.longestRepeatedSubstring("banana"));
            assertEquals("issi", SuffixArrayProblems.longestRepeatedSubstring("mississippi"));
            assertEquals("abra", SuffixArrayProblems.longestRepeatedSubstring("abracadabra"));
            assertEquals("aaa", SuffixArrayProblems.longestRepeatedSubstring("aaaa"));
            assertEquals("ab", SuffixArrayProblems.longestRepeatedSubstring("abab"));
            assertEquals("aba", SuffixArrayProblems.longestRepeatedSubstring("ababa"));
            assertEquals("ca", SuffixArrayProblems.longestRepeatedSubstring("cacao"));
        }

        @Test
        @DisplayName("반복이 없으면 빈 문자열")
        void noRepeat() {
            assertEquals("", SuffixArrayProblems.longestRepeatedSubstring("abc"));
            assertEquals("", SuffixArrayProblems.longestRepeatedSubstring("a"));
            assertEquals("", SuffixArrayProblems.longestRepeatedSubstring(""));
            assertEquals("", SuffixArrayProblems.longestRepeatedSubstring(null));
        }

        @Test
        @DisplayName("겹쳐도 반복으로 센다")
        void overlappingCounts() {
            // aaa 에서 aa 는 위치 0 과 1 에 있다. 겹치지만 두 번 나온 것이다.
            assertEquals("aa", SuffixArrayProblems.longestRepeatedSubstring("aaa"));
        }

        @Test
        @DisplayName("무작위 200개에서 전수 조사와 같다")
        void matchesBruteForce() {
            for (int trial = 0; trial < 200; trial++) {
                String s = SuffixFixtures.pseudoRandom(trial % 21, 2 + trial % 3, trial * 23L + 5);
                assertEquals(SuffixFixtures.longestRepeatedByBruteForce(s),
                        SuffixArrayProblems.longestRepeatedSubstring(s),
                        "입력 " + s);
            }
        }

        @Test
        @DisplayName("동률이면 사전순으로 앞선 것을 준다")
        void lexicographicTieBreak() {
            // abcabdbc 에서 길이 2 반복은 ab 와 bc 다. 접미사 배열이 사전순이라
            // 앞에서부터 최댓값을 잡으면 자연히 ab 가 나온다.
            assertEquals("ab", SuffixArrayProblems.longestRepeatedSubstring("abcabdbc"));
            assertEquals(SuffixFixtures.longestRepeatedByBruteForce("abcabdbc"),
                    SuffixArrayProblems.longestRepeatedSubstring("abcabdbc"));
        }

        @Test
        @Timeout(60)
        @DisplayName("길이 10만")
        void hundredThousand() {
            String s = SuffixFixtures.pseudoRandom(100_000, 4, 1);
            String lrs = SuffixArrayProblems.longestRepeatedSubstring(s);
            assertEquals("ccdaccdddcddaccac", lrs);
            assertEquals(17, lrs.length());
            assertEquals(2, SuffixFixtures.occurrences(s, lrs).size(), "정확히 두 번 나온다");
        }
    }

    @Nested
    @DisplayName("문제 3: 두 문자열의 최장 공통 부분 문자열")
    class LongestCommon {

        @Test
        @DisplayName("작은 예")
        void smallCases() {
            assertEquals("anana", SuffixArrayProblems.longestCommonSubstring("banana", "ananas"));
            assertEquals("abc", SuffixArrayProblems.longestCommonSubstring("abcdef", "zabcy"));
            assertEquals("miss",
                    SuffixArrayProblems.longestCommonSubstring("mississippi", "missouri"));
            assertEquals("aa", SuffixArrayProblems.longestCommonSubstring("aaa", "aa"));
        }

        @Test
        @DisplayName("공통이 없으면 빈 문자열")
        void noCommon() {
            assertEquals("", SuffixArrayProblems.longestCommonSubstring("abc", "xyz"));
            assertEquals("", SuffixArrayProblems.longestCommonSubstring("", "abc"));
            assertEquals("", SuffixArrayProblems.longestCommonSubstring("abc", ""));
        }

        @Test
        @DisplayName("구분자가 없으면 경계를 넘어 매칭된다")
        void separatorIsRequired() {
            // **여기가 이 문제의 함정이다.**
            // a = "a", b = "aa" 를 그냥 이어붙이면 "aaa" 가 된다.
            // 경계를 넘는 접미사 쌍이 "aa" 를 공통으로 보고하는데, a 에는 "aa" 가 없다.
            // 구분자를 끼우면 공통 접두사가 거기서 반드시 끊긴다.
            String got = SuffixArrayProblems.longestCommonSubstring("a", "aa");
            assertEquals("a", got);
            assertNotEquals("aa", got, "구분자를 빼면 여기서 aa 가 나온다");
        }

        @Test
        @DisplayName("답은 양쪽 모두의 부분 문자열이다")
        void resultIsSubstringOfBoth() {
            for (int trial = 0; trial < 200; trial++) {
                String a = SuffixFixtures.pseudoRandom(trial % 13, 2, trial * 11L + 1);
                String b = SuffixFixtures.pseudoRandom(trial % 17, 2, trial * 19L + 3);
                String got = SuffixArrayProblems.longestCommonSubstring(a, b);
                assertTrue(a.contains(got), "a=" + a + " 에 " + got + " 이 없다");
                assertTrue(b.contains(got), "b=" + b + " 에 " + got + " 이 없다");
            }
        }

        @Test
        @DisplayName("무작위 200개에서 전수 조사와 같다")
        void matchesBruteForce() {
            for (int trial = 0; trial < 200; trial++) {
                String a = SuffixFixtures.pseudoRandom(trial % 13, 2 + trial % 3, trial * 31L + 2);
                String b = SuffixFixtures.pseudoRandom(trial % 11, 2 + trial % 3, trial * 37L + 4);
                assertEquals(SuffixFixtures.longestCommonByBruteForce(a, b),
                        SuffixArrayProblems.longestCommonSubstring(a, b),
                        "a=" + a + " b=" + b);
            }
        }

        @Test
        @DisplayName("한쪽이 다른 쪽을 통째로 담고 있으면 그것이 답이다")
        void containment() {
            assertEquals("banana", SuffixArrayProblems.longestCommonSubstring("banana", "abanana"));
            assertEquals("banana", SuffixArrayProblems.longestCommonSubstring("bananax", "banana"));
        }

        @Test
        @DisplayName("null 과 구분자 문자는 거부한다")
        void rejects() {
            assertThrows(IllegalArgumentException.class,
                    () -> SuffixArrayProblems.longestCommonSubstring(null, "a"));
            assertThrows(IllegalArgumentException.class,
                    () -> SuffixArrayProblems.longestCommonSubstring("a", null));
            // 구분자로 쓰는 문자가 입력에 있으면 경계가 유일하지 않다. 조용히 틀리느니 거부한다.
            String withSeparator = "a" + (char) 1 + "b";
            assertThrows(IllegalArgumentException.class,
                    () -> SuffixArrayProblems.longestCommonSubstring(withSeparator, "ab"));
            assertThrows(IllegalArgumentException.class,
                    () -> SuffixArrayProblems.longestCommonSubstring("ab", withSeparator));
        }

        @Test
        @Timeout(60)
        @DisplayName("길이 5만 짜리 둘")
        void largeInput() {
            String a = SuffixFixtures.pseudoRandom(50_000, 4, 1);
            String b = SuffixFixtures.pseudoRandom(50_000, 4, 2);
            String got = SuffixArrayProblems.longestCommonSubstring(a, b);
            assertTrue(a.contains(got));
            assertTrue(b.contains(got));
            assertTrue(got.length() >= 12, "무작위 4글자 5만이면 12글자쯤은 겹친다: " + got.length());
        }
    }
}
