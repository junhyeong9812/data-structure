package com.datastructure.suffix;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("SuffixArray: 배가법")
class SuffixArrayTest extends SuffixArrayContractTest {

    @Override
    protected int[] build(String text) {
        return new SuffixArray(text).toArray();
    }

    @Nested
    @DisplayName("배가법의 속")
    class Doubling {

        @Test
        @DisplayName("1글자 순위는 문자 코드다")
        void initialRanks() {
            assertArrayEquals(new int[]{'b', 'a', 'n', 'a', 'n', 'a'},
                    SuffixArray.initialRanks("banana"));
            assertArrayEquals(new int[0], SuffixArray.initialRanks(""));
        }

        @Test
        @DisplayName("쌍 비교는 (rank[i], rank[i+k]) 순이다")
        void comparePair() {
            int[] rank = {1, 0, 1, 0};
            // 앞 순위가 다르면 그것으로 끝난다.
            assertTrue(SuffixArray.comparePair(rank, 1, 0, 1) < 0);
            assertTrue(SuffixArray.comparePair(rank, 0, 1, 1) > 0);
            // 앞이 같으면 k 칸 뒤를 본다. 0 과 2 는 둘 다 1 이지만 뒤가 rank[1]=0, rank[3]=0 이라 같다.
            assertEquals(0, SuffixArray.comparePair(rank, 0, 2, 1));
            // **범위를 벗어나면 -1 이다.** 문자열이 먼저 끝나는 쪽이 사전순으로 앞선다.
            // 위치 3 은 뒤가 없어 -1, 위치 1 은 뒤가 rank[2]=1 이라 3 이 앞선다.
            assertTrue(SuffixArray.comparePair(rank, 3, 1, 1) < 0);
        }

        @Test
        @DisplayName("같은 순위끼리 묶어 새 순위를 매긴다")
        void reRank() {
            // banana 를 (1글자 순위, 1칸 뒤 순위) 쌍으로 정렬하면 5, 1, 3, 0, 2, 4 다.
            // 즉 2글자까지 본 순서다: a / an / an / ba / na / na.
            //
            // 새 순위는 **앞 것과 쌍이 같으면 같은 번호**, 크면 1 증가다.
            // an 인 1 과 3 이 같은 1 번, na 인 2 와 4 가 같은 3 번을 받아야 한다.
            // **여기서 증가시켜 버리면 다음 라운드의 비교가 통째로 틀어진다.**
            String s = "banana";
            int[] rank = SuffixArray.initialRanks(s);
            int[] sa = {5, 1, 3, 0, 2, 4};
            int[] next = SuffixArray.reRank(sa, rank, 1);
            // 위치별 새 순위: a(5)=0, an(1)=1, an(3)=1, ba(0)=2, na(2)=3, na(4)=3
            assertArrayEquals(new int[]{2, 1, 3, 1, 3, 0}, next);
        }

        @Test
        @DisplayName("순위가 전부 달라지면 멈춘다")
        void stopsWhenAllRanksDistinct() {
            // 글자가 전부 다르면 1글자 순위만으로 결판난다. 정렬 한 번이면 끝이다.
            assertEquals(1, new SuffixArray("abc").sortRounds());
            assertEquals(1, new SuffixArray("a").sortRounds());
            // banana 는 a 가 셋이라 한 번 더 간다.
            assertEquals(2, new SuffixArray("banana").sortRounds());
        }

        @Test
        @DisplayName("정렬 횟수가 log n 에 묶인다")
        void roundsAreLogarithmic() {
            // 같은 글자만 있는 최악 입력에서도 라운드는 ceil(log2 n) 이다.
            assertEquals(11, new SuffixArray("a".repeat(2000)).sortRounds());
            assertEquals(10, new SuffixArray("a".repeat(1000)).sortRounds());
            // 글자가 섞이면 훨씬 빨리 끝난다. 순위가 그만큼 빨리 갈라지기 때문이다.
            assertEquals(4, new SuffixArray(SuffixFixtures.pseudoRandom(1000, 4, 1)).sortRounds());
        }
    }

    @Nested
    @DisplayName("나이브와의 대조")
    class AgainstNaive {

        @Test
        @DisplayName("무작위 400개에서 나이브 정렬과 결과가 같다")
        void sameAsNaiveOnRandomInput() {
            for (int trial = 0; trial < 400; trial++) {
                String s = SuffixFixtures.pseudoRandom(trial % 51, 2 + trial % 4, trial * 7L + 3);
                assertArrayEquals(new NaiveSuffixArray(s).toArray(), new SuffixArray(s).toArray(),
                        "입력 " + s);
            }
        }

        @Test
        @DisplayName("한계 측정: 나이브는 비교마다 글자를 다시 훑는다")
        void naiveRescansCharacters() {
            // **이게 배가법이 존재하는 이유다.**
            // 나이브는 비교 한 번에 문자열 전체를 훑을 수 있다. ab 가 반복되면 실제로 그렇게 된다.
            // 배가법은 정수 쌍 비교라 비교 한 번이 상수다. 대신 정렬을 log n 번 한다.
            String s = "ab".repeat(1000);
            NaiveSuffixArray naive = new NaiveSuffixArray(s);
            SuffixArray fast = new SuffixArray(s);

            assertArrayEquals(naive.toArray(), fast.toArray());
            assertTrue(naive.charComparisons() > 1_000_000,
                    "나이브가 훑은 글자 수: " + naive.charComparisons());
            assertEquals(11, fast.sortRounds(), "배가법은 정렬 11번으로 끝난다");
        }
    }

    @Nested
    @DisplayName("검색: 이진 탐색 두 번")
    class Find {

        @Test
        @DisplayName("banana 에서 찾기")
        void inBanana() {
            SuffixArray sa = new SuffixArray("banana");
            assertEquals(List.of(1, 3, 5), sa.find("a"));
            assertEquals(List.of(1, 3), sa.find("an"));
            assertEquals(List.of(1, 3), sa.find("ana"));
            assertEquals(List.of(2, 4), sa.find("na"));
            assertEquals(List.of(0), sa.find("ban"));
            assertEquals(List.of(0), sa.find("banana"));
            assertEquals(List.of(), sa.find("x"));
            assertEquals(List.of(), sa.find("nab"));
            assertEquals(List.of(), sa.find("bananas"), "본문보다 긴 패턴");
        }

        @Test
        @DisplayName("mississippi 에서 찾기")
        void inMississippi() {
            SuffixArray sa = new SuffixArray("mississippi");
            assertEquals(List.of(1, 4), sa.find("issi"));
            assertEquals(List.of(2, 5), sa.find("ss"));
            assertEquals(List.of(1, 4, 7, 10), sa.find("i"));
            assertEquals(List.of(0), sa.find("miss"));
            assertEquals(List.of(6), sa.find("sip"));
        }

        @Test
        @DisplayName("결과는 사전순이 아니라 위치 오름차순이다")
        void ascendingPositions() {
            // 접미사 배열 순서로 그냥 뱉으면 5, 3, 1 이 나온다. 정렬해서 주는 것이 계약이다.
            SuffixArray sa = new SuffixArray("banana");
            List<Integer> got = sa.find("a");
            for (int i = 1; i < got.size(); i++) {
                assertTrue(got.get(i - 1) < got.get(i), "오름차순이 아니다: " + got);
            }
        }

        @Test
        @DisplayName("contains 와 count 가 find 와 맞는다")
        void containsAndCount() {
            SuffixArray sa = new SuffixArray("mississippi");
            for (String p : new String[]{"i", "ss", "issi", "miss", "x", "ppi", "pp", "ippi"}) {
                assertEquals(!sa.find(p).isEmpty(), sa.contains(p), p);
                assertEquals(sa.find(p).size(), sa.count(p), p);
            }
        }

        @Test
        @DisplayName("무작위 200개에서 indexOf 훑기와 대조한다")
        void matchesIndexOfOnRandomInput() {
            for (int trial = 0; trial < 200; trial++) {
                String text = SuffixFixtures.pseudoRandom(30 + trial % 20, 2 + trial % 3,
                        trial * 13L + 5);
                SuffixArray sa = new SuffixArray(text);
                for (int len = 1; len <= 4; len++) {
                    String pattern = SuffixFixtures.pseudoRandom(len, 2 + trial % 3, trial * 5L + 9);
                    assertEquals(SuffixFixtures.occurrences(text, pattern), sa.find(pattern),
                            "본문 " + text + " 패턴 " + pattern);
                }
            }
        }

        @Test
        @DisplayName("빈 문자열에서는 아무것도 못 찾는다")
        void emptyText() {
            SuffixArray sa = new SuffixArray("");
            assertEquals(List.of(), sa.find("a"));
            assertFalse(sa.contains("a"));
        }

        @Test
        @DisplayName("null 과 빈 패턴은 거부한다")
        void badPattern() {
            SuffixArray sa = new SuffixArray("banana");
            assertThrows(IllegalArgumentException.class, () -> sa.find(null));
            assertThrows(IllegalArgumentException.class, () -> sa.contains(null));
            // 빈 패턴은 n+1 자리 전부에 있다. 답이 의미가 없어 계약에서 뺀다.
            assertThrows(IllegalArgumentException.class, () -> sa.find(""));
        }

        @Test
        @DisplayName("한계 측정: 검색이 log n 걸음이다. 트라이는 m 걸음이었다")
        void searchIsLogarithmic() {
            // **여기서 뭘 포기했는지가 보인다.**
            // 09번 트라이는 패턴 길이만큼만 내려가면 됐다(m 걸음, n 과 무관).
            // 접미사 배열은 이진 탐색이라 log n 번 찔러보고, 찌를 때마다 글자를 비교한다.
            String text = SuffixFixtures.pseudoRandom(100_000, 4, 1);
            SuffixArray sa = new SuffixArray(text);

            sa.find("abcd");
            int probes = sa.lastSearchProbes();
            // 이진 탐색 두 번이니 2 * ceil(log2(n+1)) = 34 가 상한이다.
            assertTrue(probes <= 34, "찔러본 횟수: " + probes);
            assertTrue(probes >= 30, "10만 개를 좁히려면 그만큼은 든다: " + probes);
        }
    }

    @Nested
    @DisplayName("한계 1: 09번 접미사 트라이와의 대비")
    class AgainstSuffixTrie {

        @Test
        @DisplayName("길이 1000 에 트라이 노드 500,500 개, 접미사 배열 int 1,000 개")
        void nodeCountAgainstIntArray() {
            // 글자가 전부 다르면 부분 문자열이 전부 다르다. 트라이 노드가 정확히 n(n+1)/2 개다.
            int n = 1000;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                sb.append((char) (0x100 + i));
            }
            String s = sb.toString();

            long nodes = SuffixFixtures.suffixTrieNodes(s);
            assertEquals(500_500L, nodes, "트라이 노드 수");
            SuffixArray sa = new SuffixArray(s);
            assertEquals(1000, sa.size());
            assertEquals(4000L, sa.memoryBytes(), "int 배열 하나. 노드도 포인터도 없다");
            assertEquals(500, nodes / sa.size(), "노드가 위치 하나당 500개꼴이다");
        }

        @Test
        @DisplayName("무작위 길이 1000 에서도 노드가 49만 개다")
        void randomThousand() {
            String s = SuffixFixtures.pseudoRandom(1000, 4, 1);
            assertEquals(496_341L, SuffixFixtures.suffixTrieNodes(s));
            assertEquals(4000L, new SuffixArray(s).memoryBytes());
        }

        @Test
        @Timeout(60)
        @DisplayName("길이 10만: 트라이면 노드 50억 개, 접미사 배열이면 400KB")
        void hundredThousand() {
            // n(n+1)/2 = 5,000,050,000. 노드 하나가 40바이트라 쳐도 200GB 다.
            // **아예 못 만든다.** 여기서는 int 10만 개, 400KB 로 끝난다.
            int n = 100_000;
            assertEquals(5_000_050_000L, (long) n * (n + 1) / 2);

            String s = SuffixFixtures.pseudoRandom(n, 4, 1);
            SuffixArray sa = new SuffixArray(s);
            assertEquals(400_000L, sa.memoryBytes());
            assertEquals(5, sa.sortRounds(), "정렬 5번으로 끝났다");
            assertTrue(sa.contains(s.substring(50_000, 50_020)));
            assertEquals(List.of(50_000), sa.find(s.substring(50_000, 50_020)));
        }
    }

    @Nested
    @DisplayName("접미사 꺼내기")
    class SuffixAt {

        @Test
        @DisplayName("순위로 접미사를 꺼낸다")
        void byRank() {
            SuffixArray sa = new SuffixArray("banana");
            assertEquals("a", sa.suffixAt(0));
            assertEquals("ana", sa.suffixAt(1));
            assertEquals("anana", sa.suffixAt(2));
            assertEquals("banana", sa.suffixAt(3));
            assertEquals("na", sa.suffixAt(4));
            assertEquals("nana", sa.suffixAt(5));
        }

        @Test
        @DisplayName("범위 밖은 거부한다")
        void outOfRange() {
            SuffixArray sa = new SuffixArray("banana");
            assertThrows(IndexOutOfBoundsException.class, () -> sa.suffixAt(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> sa.suffixAt(6));
            assertThrows(IndexOutOfBoundsException.class, () -> new SuffixArray("").suffixAt(0));
        }

        @Test
        @DisplayName("toArray 는 복사본이다")
        void toArrayIsCopy() {
            SuffixArray sa = new SuffixArray("banana");
            int[] first = sa.toArray();
            first[0] = 99;
            assertEquals(5, sa.toArray()[0], "밖에서 고친 것이 안에 남으면 안 된다");
        }
    }
}
