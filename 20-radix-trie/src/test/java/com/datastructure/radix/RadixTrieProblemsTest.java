package com.datastructure.radix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("RadixTrieProblems")
class RadixTrieProblemsTest {

    static RadixTrie<String> sample() {
        RadixTrie<String> t = new RadixTrie<>();
        for (String w : RadixTrieTest.ROMAN) {
            t.put(w, "v:" + w);
        }
        return t;
    }

    @Nested
    @DisplayName("문제 1: 가장 긴 공통 접두사")
    class LongestCommonPrefix {

        @Test
        @DisplayName("갈라지는 지점까지")
        void branchPoint() {
            assertEquals("fl", RadixTrieProblems.longestCommonPrefix(
                    new String[]{"flower", "flow", "flight"}));
            assertEquals("inters", RadixTrieProblems.longestCommonPrefix(
                    new String[]{"interspecies", "interstellar", "interstate"}));
            assertEquals("ab", RadixTrieProblems.longestCommonPrefix(
                    new String[]{"abc", "abc", "abd"}));
        }

        @Test
        @DisplayName("공통이 없으면 빈 문자열")
        void noCommon() {
            assertEquals("", RadixTrieProblems.longestCommonPrefix(
                    new String[]{"dog", "racecar", "car"}));
        }

        @Test
        @DisplayName("한 단어가 다른 단어의 접두사면 거기서 멈춘다")
        void oneIsPrefixOfOther() {
            assertEquals("a", RadixTrieProblems.longestCommonPrefix(new String[]{"a", "ab"}));
            assertEquals("a", RadixTrieProblems.longestCommonPrefix(new String[]{"ab", "a"}));
            assertEquals("app", RadixTrieProblems.longestCommonPrefix(
                    new String[]{"apple", "app", "application"}));
        }

        @Test
        @DisplayName("단어가 하나면 그 단어다")
        void single() {
            assertEquals("apple", RadixTrieProblems.longestCommonPrefix(new String[]{"apple"}));
            assertEquals("ab", RadixTrieProblems.longestCommonPrefix(
                    new String[]{"ab", "ab", "ab"}));
        }

        @Test
        @DisplayName("빈 입력과 빈 단어")
        void empties() {
            assertEquals("", RadixTrieProblems.longestCommonPrefix(new String[]{}));
            assertEquals("", RadixTrieProblems.longestCommonPrefix(null));
            assertEquals("", RadixTrieProblems.longestCommonPrefix(new String[]{""}));
            assertEquals("", RadixTrieProblems.longestCommonPrefix(new String[]{"", "abc"}));
        }

        @Test
        @DisplayName("답은 뿌리의 첫 간선 그 자체다")
        void answerIsTheFirstEdge() {
            // 09번에서는 뿌리부터 한 글자씩 내려가며 갈림길을 찾아야 했다.
            // 압축 트라이에서는 **그 사슬이 이미 간선 하나로 눌려 있다.**
            // 뿌리의 자식이 하나면 그 간선이 곧 답이고, 둘 이상이면 뿌리에서 갈라지므로 답은 "" 다.
            String[] words = {"interspecies", "interstellar", "interstate"};
            RadixTrie<String> t = new RadixTrie<>();
            for (String w : words) {
                t.put(w, w);
            }
            assertEquals(1, t.root.children.size());
            assertEquals("inters", t.root.children.values().iterator().next().edge);
            assertEquals(RadixTrieProblems.longestCommonPrefix(words),
                    t.root.children.values().iterator().next().edge);
        }

        @Test
        @DisplayName("무작위 단어들을 전수 비교와 대조한다")
        void matchesBruteForce() {
            java.util.Random rnd = new java.util.Random(88L);
            for (int trial = 0; trial < 300; trial++) {
                int n = 1 + rnd.nextInt(6);
                String[] words = new String[n];
                String base = "prefix";
                for (int i = 0; i < n; i++) {
                    StringBuilder sb = new StringBuilder(base.substring(0, rnd.nextInt(7)));
                    for (int j = rnd.nextInt(5); j > 0; j--) {
                        sb.append((char) ('a' + rnd.nextInt(3)));
                    }
                    words[i] = sb.toString();
                }
                String expected = words[0];
                for (String w : words) {
                    int i = 0;
                    while (i < expected.length() && i < w.length()
                            && expected.charAt(i) == w.charAt(i)) {
                        i++;
                    }
                    expected = expected.substring(0, i);
                }
                assertEquals(expected, RadixTrieProblems.longestCommonPrefix(words),
                        java.util.Arrays.toString(words));
            }
        }

        @Test
        @Timeout(15)
        @DisplayName("10만 단어")
        void manyWords() {
            String[] words = new String[100_000];
            for (int i = 0; i < words.length; i++) {
                words[i] = "commonprefix" + i;
            }
            assertTrue(RadixTrieProblems.longestCommonPrefix(words).startsWith("commonprefix"));
        }
    }

    @Nested
    @DisplayName("문제 2: 자동완성 상위 k개")
    class Autocomplete {

        @Test
        @DisplayName("사전순 앞에서 k 개")
        void topK() {
            assertEquals(List.of("romane"), RadixTrieProblems.autocomplete(sample(), "rom", 1));
            assertEquals(List.of("romane", "romanus", "romulus"),
                    RadixTrieProblems.autocomplete(sample(), "rom", 3));
            assertEquals(List.of("rubens", "ruber", "rubicon"),
                    RadixTrieProblems.autocomplete(sample(), "rub", 3));
            assertEquals(List.of("rubicon", "rubicundus"),
                    RadixTrieProblems.autocomplete(sample(), "rubic", 3));
        }

        @Test
        @DisplayName("k 가 남은 개수보다 크면 있는 만큼")
        void fewerThanK() {
            assertEquals(List.of("romane", "romanus", "romulus"),
                    RadixTrieProblems.autocomplete(sample(), "rom", 100));
            assertEquals(RadixTrieTest.ROMAN, RadixTrieProblems.autocomplete(sample(), "", 100));
        }

        @Test
        @DisplayName("k 가 0 이하면 빈 리스트")
        void nonPositiveK() {
            assertEquals(List.of(), RadixTrieProblems.autocomplete(sample(), "rom", 0));
            assertEquals(List.of(), RadixTrieProblems.autocomplete(sample(), "rom", -1));
        }

        @Test
        @DisplayName("없는 접두사면 빈 리스트")
        void missingPrefix() {
            assertEquals(List.of(), RadixTrieProblems.autocomplete(sample(), "z", 5));
            assertEquals(List.of(), RadixTrieProblems.autocomplete(sample(), "romanez", 5));
        }

        @Test
        @DisplayName("접두사가 간선 중간에서 끝나도 된다")
        void midEdgePrefix() {
            RadixTrie<String> t = new RadixTrie<>();
            t.put("romane", "v");
            assertEquals(List.of("romane"), RadixTrieProblems.autocomplete(t, "rom", 5));
            assertEquals(List.of(), RadixTrieProblems.autocomplete(t, "rome", 5));
        }

        @Test
        @DisplayName("keysWithPrefix 의 앞부분과 같다")
        void agreesWithKeysWithPrefix() {
            RadixTrie<String> t = sample();
            for (String p : List.of("", "r", "ro", "rom", "roman", "rub", "rubic", "z")) {
                for (int k = 1; k <= 8; k++) {
                    List<String> all = t.keysWithPrefix(p);
                    List<String> expected = all.subList(0, Math.min(k, all.size()));
                    assertEquals(expected, RadixTrieProblems.autocomplete(t, p, k),
                            "접두사 '" + p + "' k=" + k);
                }
            }
        }

        @Test
        @Timeout(20)
        @DisplayName("10만 개가 걸린 접두사에서 10개만 뽑는다")
        void stopsEarly() {
            // 전부 모으고 자르면 질의 하나가 10만 걸음이다. 그것을 10만 번 한다.
            RadixTrie<String> t = new RadixTrie<>();
            for (int i = 0; i < 100_000; i++) {
                t.put("a" + RadixTrieTest.encode(i), "v");
            }
            List<String> first = RadixTrieProblems.autocomplete(t, "a", 10);
            assertEquals(10, first.size());
            for (int q = 0; q < 100_000; q++) {
                assertEquals(first, RadixTrieProblems.autocomplete(t, "a", 10));
            }
        }
    }
}
