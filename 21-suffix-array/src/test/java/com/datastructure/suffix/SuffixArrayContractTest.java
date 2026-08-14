package com.datastructure.suffix;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 접미사 배열이라면 무엇이든 지켜야 하는 계약.
 *
 * 구현이 둘이다. 나이브 정렬과 배가법. 결과는 글자 하나까지 같아야 한다.
 * 다른 것은 비용뿐이다.
 */
abstract class SuffixArrayContractTest {

    /** 이 계약을 검사할 구현. 접미사 시작 위치를 사전순으로 담은 배열을 준다. */
    protected abstract int[] build(String text);

    @Test
    @DisplayName("banana 는 [5, 3, 1, 0, 4, 2] 다")
    void banana() {
        // 사전순으로 a(5), ana(3), anana(1), banana(0), na(4), nana(2).
        // **접미사를 저장하지 않는다.** 배열에 남는 것은 시작 위치 6개뿐이다.
        assertArrayEquals(new int[]{5, 3, 1, 0, 4, 2}, build("banana"));
    }

    @Test
    @DisplayName("mississippi")
    void mississippi() {
        assertArrayEquals(new int[]{10, 7, 4, 1, 0, 9, 8, 6, 3, 5, 2}, build("mississippi"));
    }

    @Test
    @DisplayName("abracadabra")
    void abracadabra() {
        assertArrayEquals(new int[]{10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2}, build("abracadabra"));
    }

    @Test
    @DisplayName("같은 글자만 있으면 뒤에서부터다")
    void allSame() {
        // a 가 aa 보다 짧아서 앞선다. 짧은 쪽이 먼저다.
        assertArrayEquals(new int[]{3, 2, 1, 0}, build("aaaa"));
        assertArrayEquals(new int[]{0}, build("a"));
    }

    @Test
    @DisplayName("전부 다른 글자면 원래 순서 그대로다")
    void allDistinct() {
        assertArrayEquals(new int[]{0, 1, 2}, build("abc"));
        assertArrayEquals(new int[]{2, 1, 0}, build("cba"));
    }

    @Test
    @DisplayName("빈 문자열은 빈 배열")
    void empty() {
        assertArrayEquals(new int[0], build(""));
    }

    @Test
    @DisplayName("작은 예 몇 개")
    void smallCases() {
        assertArrayEquals(new int[]{2, 0, 3, 1}, build("abab"));
        assertArrayEquals(new int[]{0, 1, 2}, build("aab"));
        assertArrayEquals(new int[]{4, 2, 0, 3, 1}, build("ababa"));
        assertArrayEquals(new int[]{1, 3, 0, 2, 4}, build("cacao"));
    }

    @Test
    @DisplayName("0..n-1 의 순열이다")
    void isPermutation() {
        for (String s : new String[]{"banana", "mississippi", "aaaa", "abcdefg",
                SuffixFixtures.pseudoRandom(200, 3, 42)}) {
            int[] sa = build(s);
            assertEquals(s.length(), sa.length, s);
            Set<Integer> seen = new HashSet<>();
            for (int v : sa) {
                assertTrue(v >= 0 && v < s.length(), "범위 밖 " + v);
                assertTrue(seen.add(v), "같은 위치가 두 번 나왔다: " + v);
            }
        }
    }

    @Test
    @DisplayName("실제로 사전순이다")
    void isSorted() {
        for (String s : new String[]{"banana", "mississippi", "abracadabra",
                SuffixFixtures.pseudoRandom(200, 2, 7)}) {
            int[] sa = build(s);
            for (int i = 1; i < sa.length; i++) {
                String prev = s.substring(sa[i - 1]);
                String cur = s.substring(sa[i]);
                assertTrue(prev.compareTo(cur) < 0,
                        "순서가 틀렸다: " + prev + " 가 " + cur + " 앞에 있다");
            }
        }
    }

    @Test
    @DisplayName("무작위 300개를 접미사 정렬 결과와 대조한다")
    void matchesSortedSuffixesOnRandomInput() {
        // **이 문제의 생명이다.** 느리지만 확실한 기준(접미사를 통째로 만들어 정렬)과
        // 전부 대조한다. 길이 0 부터 40 까지, 문자 종류 2 부터 6 까지 섞는다.
        for (int trial = 0; trial < 300; trial++) {
            int n = trial % 41;
            int alphabet = 2 + trial % 5;
            String s = SuffixFixtures.pseudoRandom(n, alphabet, trial * 31L + 1);
            assertArrayEquals(SuffixFixtures.sortedSuffixIndexes(s), build(s),
                    "입력 " + s);
        }
    }

    @Test
    @DisplayName("한 글자만 있는 문자열도 길이가 다양하다")
    void singleLetterStrings() {
        for (int n = 1; n <= 30; n++) {
            String s = "a".repeat(n);
            int[] expected = new int[n];
            for (int i = 0; i < n; i++) {
                expected[i] = n - 1 - i;
            }
            assertArrayEquals(expected, build(s), "길이 " + n);
        }
    }

    @Test
    @DisplayName("아스키 밖 문자도 된다")
    void unicode() {
        // 문자 코드로 정렬하므로 한글도 그대로 된다.
        String s = "가나가나다";
        assertArrayEquals(SuffixFixtures.sortedSuffixIndexes(s), build(s));
    }

    @Test
    @DisplayName("null 은 거부한다")
    void nullRejected() {
        assertThrows(IllegalArgumentException.class, () -> build(null));
    }
}
