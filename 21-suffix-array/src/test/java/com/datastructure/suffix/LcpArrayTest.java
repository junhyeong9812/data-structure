package com.datastructure.suffix;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LcpArray: Kasai 알고리즘")
class LcpArrayTest {

    private static int[] lcpOf(String s) {
        return LcpArray.build(s, new SuffixArray(s).toArray());
    }

    @Nested
    @DisplayName("값")
    class Values {

        @Test
        @DisplayName("banana 의 LCP 는 [0, 1, 3, 0, 0, 2] 다")
        void banana() {
            // 접미사 순서는 a, ana, anana, banana, na, nana 다.
            //   a 와 ana 는 앞 1글자가 같다
            //   ana 와 anana 는 앞 3글자가 같다
            //   anana 와 banana 는 0
            //   banana 와 na 는 0
            //   na 와 nana 는 2
            // **lcp[0] 은 늘 0 이다.** 앞에 비교할 이웃이 없다.
            assertArrayEquals(new int[]{0, 1, 3, 0, 0, 2}, lcpOf("banana"));
        }

        @Test
        @DisplayName("mississippi 와 abracadabra")
        void others() {
            assertArrayEquals(new int[]{0, 1, 1, 4, 0, 0, 1, 0, 2, 1, 3}, lcpOf("mississippi"));
            assertArrayEquals(new int[]{0, 1, 4, 1, 1, 0, 3, 0, 0, 0, 2}, lcpOf("abracadabra"));
        }

        @Test
        @DisplayName("같은 글자만 있으면 1씩 는다")
        void allSame() {
            assertArrayEquals(new int[]{0, 1, 2, 3}, lcpOf("aaaa"));
        }

        @Test
        @DisplayName("전부 다른 글자면 전부 0 이다")
        void allDistinct() {
            assertArrayEquals(new int[]{0, 0, 0}, lcpOf("abc"));
        }

        @Test
        @DisplayName("작은 예 몇 개")
        void smallCases() {
            assertArrayEquals(new int[]{0}, lcpOf("a"));
            assertArrayEquals(new int[]{0, 2, 0, 1}, lcpOf("abab"));
            assertArrayEquals(new int[]{0, 1, 0}, lcpOf("aab"));
            assertArrayEquals(new int[]{0, 1, 3, 0, 2}, lcpOf("ababa"));
            assertArrayEquals(new int[]{0, 1, 0, 2, 0}, lcpOf("cacao"));
            assertArrayEquals(new int[0], lcpOf(""));
        }

        @Test
        @DisplayName("SuffixArray 를 그대로 받는 생성자")
        void fromSuffixArray() {
            LcpArray lcp = new LcpArray(new SuffixArray("banana"));
            assertEquals(6, lcp.size());
            assertEquals(3, lcp.get(2));
            assertEquals(6L, lcp.sum());
            assertEquals(3, lcp.max());
            assertEquals(2, lcp.argMax());
        }

        @Test
        @DisplayName("역배열은 위치에서 순위를 준다")
        void inverse() {
            // sa[i] = 순위 i 의 시작 위치. rank[p] = 위치 p 의 순위. 서로 뒤집은 것이다.
            int[] sa = {5, 3, 1, 0, 4, 2};
            assertArrayEquals(new int[]{3, 2, 5, 1, 4, 0}, LcpArray.inverse(sa));
            int[] rank = LcpArray.inverse(sa);
            for (int i = 0; i < sa.length; i++) {
                assertEquals(i, rank[sa[i]], "뒤집으면 제자리로 와야 한다");
            }
        }

        @Test
        @DisplayName("max 와 argMax 는 동률이면 앞선 순위를 준다")
        void maxTies() {
            // aaaa 는 lcp 가 [0,1,2,3] 이라 최댓값이 하나다.
            LcpArray a = new LcpArray(new SuffixArray("aaaa"));
            assertEquals(3, a.max());
            assertEquals(3, a.argMax());
            // abab 는 [0,2,0,1] 이다.
            LcpArray b = new LcpArray(new SuffixArray("abab"));
            assertEquals(2, b.max());
            assertEquals(1, b.argMax());
            // 전부 0 이면 max 0, argMax 0.
            LcpArray c = new LcpArray(new SuffixArray("abc"));
            assertEquals(0, c.max());
            assertEquals(0, c.argMax());
            // 빈 배열은 argMax 가 -1 이다.
            assertEquals(-1, new LcpArray(new SuffixArray("")).argMax());
        }
    }

    @Nested
    @DisplayName("직접 세기와의 대조")
    class AgainstNaive {

        @Test
        @DisplayName("무작위 300개에서 직접 센 LCP 와 같다")
        void matchesNaiveOnRandomInput() {
            for (int trial = 0; trial < 300; trial++) {
                String s = SuffixFixtures.pseudoRandom(trial % 41, 2 + trial % 5, trial * 29L + 11);
                int[] sa = new SuffixArray(s).toArray();
                assertArrayEquals(SuffixFixtures.naiveLcp(s, sa), LcpArray.build(s, sa),
                        "입력 " + s);
            }
        }

        @Test
        @DisplayName("나이브 접미사 배열 위에서도 같은 답이다")
        void worksOnNaiveSuffixArray() {
            for (int trial = 0; trial < 50; trial++) {
                String s = SuffixFixtures.pseudoRandom(trial, 3, trial * 3L + 2);
                int[] sa = NaiveSuffixArray.of(s);
                assertArrayEquals(SuffixFixtures.naiveLcp(s, sa), LcpArray.build(s, sa));
            }
        }

        @Test
        @DisplayName("lcp 는 이웃한 두 접미사의 실제 공통 접두사 길이다")
        void isActuallyCommonPrefixLength() {
            String s = SuffixFixtures.pseudoRandom(300, 3, 99);
            int[] sa = new SuffixArray(s).toArray();
            int[] lcp = LcpArray.build(s, sa);
            for (int i = 1; i < sa.length; i++) {
                String a = s.substring(sa[i - 1]);
                String b = s.substring(sa[i]);
                int h = lcp[i];
                assertTrue(h <= Math.min(a.length(), b.length()));
                assertEquals(a.substring(0, h), b.substring(0, h), "앞 h 글자가 같아야 한다");
                if (h < Math.min(a.length(), b.length())) {
                    assertTrue(a.charAt(h) != b.charAt(h), "h+1 번째는 달라야 한다");
                }
            }
        }
    }

    @Nested
    @DisplayName("한계 측정: Kasai 가 O(n) 인 이유")
    class Amortized {

        @Test
        @DisplayName("같은 글자 2000개에서 직접 세기는 200만, Kasai 는 4000 걸음이다")
        void kasaiIsLinear() {
            // **원문 순서로 가면 h 가 한 번에 1 이상 줄지 않는다.**
            // 그래서 h 는 전체에서 최대 n 번 늘고 n 번 준다. 합쳐서 O(n) 이다.
            //
            // 직접 세기는 이웃마다 처음부터 다시 센다. "aaa...a" 면 이웃 하나가 최대 n 글자다.
            int n = 2000;
            String s = "a".repeat(n);
            int[] sa = new SuffixArray(s).toArray();

            LcpArray kasai = new LcpArray(s, sa);
            // 정답 구현은 1,999 번이다. h 가 최대 n 번 늘고 n 번 줄기 때문에 3n 이 상한이다.
            assertTrue(kasai.charComparisons() <= 3L * n,
                    "Kasai 가 훑은 글자: " + kasai.charComparisons());

            // 직접 세기의 비용은 lcp 값의 합이다. 여기서는 n(n-1)/2 = 1,999,000 이다.
            assertEquals(1_999_000L, kasai.sum());
            assertTrue(kasai.sum() > kasai.charComparisons() * 100,
                    "결과값의 합(" + kasai.sum() + ")보다 훑은 글자 수("
                            + kasai.charComparisons() + ")가 훨씬 적다");
        }

        @Test
        @DisplayName("길이 10만도 훑는 글자가 30만을 안 넘는다")
        void linearOnLargeInput() {
            int n = 100_000;
            String s = SuffixFixtures.pseudoRandom(n, 4, 1);
            int[] sa = new SuffixArray(s).toArray();
            LcpArray lcp = new LcpArray(s, sa);
            // 정답 구현은 199,985 번이다. 길이에 정확히 비례한다.
            assertTrue(lcp.charComparisons() <= 3L * n,
                    "훑은 글자: " + lcp.charComparisons() + " (n = " + n + ")");
            assertEquals(750_316L, lcp.sum());
            assertEquals(17, lcp.max(), "가장 긴 반복은 17글자다");
        }
    }

    @Nested
    @DisplayName("거부")
    class Rejects {

        @Test
        @DisplayName("길이가 안 맞으면 거부한다")
        void lengthMismatch() {
            assertThrows(IllegalArgumentException.class,
                    () -> new LcpArray("banana", new int[]{0, 1, 2}));
        }

        @Test
        @DisplayName("null 은 거부한다")
        void nulls() {
            assertThrows(IllegalArgumentException.class, () -> new LcpArray(null, new int[0]));
            assertThrows(IllegalArgumentException.class, () -> new LcpArray("a", null));
            assertThrows(IllegalArgumentException.class, () -> new LcpArray((SuffixArray) null));
        }

        @Test
        @DisplayName("범위 밖 순위는 거부한다")
        void outOfRange() {
            LcpArray lcp = new LcpArray(new SuffixArray("banana"));
            assertThrows(IndexOutOfBoundsException.class, () -> lcp.get(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> lcp.get(6));
        }

        @Test
        @DisplayName("toArray 는 복사본이다")
        void toArrayIsCopy() {
            LcpArray lcp = new LcpArray(new SuffixArray("banana"));
            int[] first = lcp.toArray();
            first[2] = 99;
            assertEquals(3, lcp.toArray()[2]);
        }
    }
}
