package com.datastructure.skiplist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("SkipList: 구조 자체")
class SkipListTest {

    private SkipList<Integer, String> list() {
        return new SkipList<>(20260813L);
    }

    /**
     * 층 구조가 건전한가.
     *
     * 두 가지를 본다.
     *   1. 각 층이 정렬돼 있다
     *   2. 위층에 있는 노드는 **아래층에도 전부 있다** (부분 수열이다)
     *
     * 2번이 핵심이다. 이걸 안 보면 위층 링크를 엉뚱하게 이어도 대부분의 테스트를 통과한다.
     * 조회가 결국 레벨 0 으로 내려와 확인하기 때문이다.
     */
    private void assertSound(SkipList<Integer, String> sl) {
        List<Integer> level0 = new ArrayList<>();
        for (SkipList.Node<Integer, String> n = sl.head.forward[0]; n != null; n = n.forward[0]) {
            level0.add(n.key);
        }
        for (int i = 1; i < level0.size(); i++) {
            assertTrue(level0.get(i - 1) < level0.get(i),
                    "레벨 0 이 정렬돼 있지 않다: " + level0.get(i - 1) + " -> " + level0.get(i));
        }
        assertEquals(sl.size(), level0.size(), "size 와 레벨 0 의 길이가 다르다");

        for (int lv = 1; lv < sl.currentLevel(); lv++) {
            List<Integer> layer = new ArrayList<>();
            for (SkipList.Node<Integer, String> n = sl.head.forward[lv]; n != null; n = n.forward[lv]) {
                layer.add(n.key);
            }
            for (int i = 1; i < layer.size(); i++) {
                assertTrue(layer.get(i - 1) < layer.get(i), "레벨 " + lv + " 이 정렬돼 있지 않다");
            }
            int pos = 0;
            for (Integer key : layer) {
                int found = level0.indexOf(key);
                assertTrue(found >= pos,
                        "레벨 " + lv + " 의 " + key + " 가 레벨 0 에 없거나 순서가 어긋난다");
                pos = found + 1;
            }
        }

        for (int lv = sl.currentLevel(); lv < SkipList.MAX_LEVEL; lv++) {
            assertNull(sl.head.forward[lv],
                    "currentLevel 위쪽(" + lv + ")에 노드가 남아 있다");
        }
    }

    @Nested
    @DisplayName("층 구조")
    class Layers {

        @Test
        @DisplayName("연산마다 층이 건전하다")
        void staysSound() {
            SkipList<Integer, String> sl = list();
            assertSound(sl);
            for (int i = 0; i < 200; i++) {
                sl.put(i * 3 % 200, "v");
                assertSound(sl);
            }
            for (int i = 0; i < 200; i += 2) {
                sl.remove(i);
                assertSound(sl);
            }
            sl.clear();
            assertSound(sl);
            assertEquals(1, sl.currentLevel(), "비우면 레벨이 1 로 돌아와야 한다");
        }

        @Test
        @DisplayName("다 지우면 레벨이 1 로 내려온다")
        void levelShrinks() {
            SkipList<Integer, String> sl = list();
            for (int i = 0; i < 1000; i++) {
                sl.put(i, "v");
            }
            assertTrue(sl.currentLevel() > 5, "1000개면 층이 여럿 생긴다: " + sl.currentLevel());
            for (int i = 0; i < 1000; i++) {
                sl.remove(i);
            }
            assertEquals(1, sl.currentLevel(), "빈 층을 안 내리면 조회가 헛돈다");
            assertEquals(0, sl.size());
        }

        @Test
        @DisplayName("레벨은 1 아래로 안 내려간다")
        void levelNeverBelowOne() {
            SkipList<Integer, String> sl = list();
            sl.put(1, "a");
            sl.remove(1);
            assertEquals(1, sl.currentLevel());
            assertNull(sl.get(1));
            sl.put(2, "b");
            assertEquals("b", sl.get(2));
        }
    }

    @Nested
    @DisplayName("동전 던지기")
    class CoinFlips {

        @Test
        @DisplayName("레벨 분포가 절반씩 준다")
        void geometricDistribution() {
            SkipList<Integer, String> sl = new SkipList<>(42L);
            int[] counts = new int[SkipList.MAX_LEVEL + 1];
            for (int i = 0; i < 100_000; i++) {
                counts[sl.randomLevel()]++;
            }
            assertTrue(counts[1] > 48_000 && counts[1] < 52_000,
                    "레벨 1 이 " + counts[1] + "개. 절반(5만) 근처여야 한다");
            assertTrue(counts[2] > 23_000 && counts[2] < 27_000,
                    "레벨 2 가 " + counts[2] + "개. 4분의 1(2.5만) 근처여야 한다");
            assertTrue(counts[3] > 11_000 && counts[3] < 14_000, "레벨 3 이 " + counts[3] + "개");
            assertEquals(0, counts[0], "레벨 0 짜리 노드는 없다. 최소 1 이다");
        }

        @Test
        @DisplayName("MAX_LEVEL 을 넘지 않는다")
        void respectsMaxLevel() {
            SkipList<Integer, String> sl = new SkipList<>(7L);
            for (int i = 0; i < 200_000; i++) {
                int lv = sl.randomLevel();
                assertTrue(lv >= 1 && lv <= SkipList.MAX_LEVEL, "레벨 " + lv);
            }
        }

        @Test
        @DisplayName("seed 가 같으면 결과가 같다")
        void seedIsDeterministic() {
            // 무작위를 쓰는 자료구조는 seed 를 주입받아야 테스트할 수 있다.
            SkipList<Integer, String> a = new SkipList<>(99L);
            SkipList<Integer, String> b = new SkipList<>(99L);
            for (int i = 0; i < 500; i++) {
                a.put(i, "v");
                b.put(i, "v");
            }
            assertEquals(a.currentLevel(), b.currentLevel());
            assertEquals(a.keys(), b.keys());
        }
    }

    @Nested
    @DisplayName("06번 BST 와의 대비")
    class VersusBst {

        @Test
        @DisplayName("입력 순서가 구조에 영향을 주지 않는다")
        void inputOrderDoesNotMatter() {
            // 06번 BST 는 정렬 입력에서 높이가 n 이 됐다.
            // 여기서는 **입력이 무엇이든 층 분포가 같다.** 무작위성이 자료가 아니라 구조에 있다.
            int n = 20_000;

            SkipList<Integer, String> sorted = new SkipList<>(1L);
            for (int i = 0; i < n; i++) {
                sorted.put(i, "v");
            }

            SkipList<Integer, String> reversed = new SkipList<>(1L);
            for (int i = n - 1; i >= 0; i--) {
                reversed.put(i, "v");
            }

            SkipList<Integer, String> shuffled = new SkipList<>(1L);
            List<Integer> keys = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                keys.add(i);
            }
            java.util.Collections.shuffle(keys, new Random(3L));
            for (int k : keys) {
                shuffled.put(k, "v");
            }

            int expected = (int) (Math.log(n) / Math.log(2));   // 약 14
            for (SkipList<Integer, String> sl : List.of(sorted, reversed, shuffled)) {
                assertTrue(sl.currentLevel() <= expected + 6,
                        "층이 " + sl.currentLevel() + " 개다. log2(" + n + ") = " + expected
                                + " 근처여야 한다");
                assertTrue(sl.currentLevel() >= expected - 4, "층이 " + sl.currentLevel() + "개뿐이다");
            }
        }

        @Test
        @Timeout(20)
        @DisplayName("정렬 입력 10만 개에서 조회가 빠르다")
        void sortedInsertStaysFast() {
            SkipList<Integer, String> sl = new SkipList<>(5L);
            for (int i = 0; i < 100_000; i++) {
                sl.put(i, "v");
            }
            // BST 였다면 조회 하나가 10만 걸음이라 이 루프가 100억 걸음이 된다.
            for (int round = 0; round < 20; round++) {
                for (int i = 0; i < 100_000; i += 7) {
                    assertEquals("v", sl.get(i));
                }
            }
        }
    }

    @Nested
    @DisplayName("한계: 확률에 기대므로 보장이 아니다")
    class ProbabilisticLimit {

        @Test
        @Timeout(10)
        @DisplayName("나쁜 동전 운이면 그냥 연결 리스트가 된다")
        void unluckyCoinsDegenerate() {
            // 늘 뒷면만 나오는 동전을 주면 모든 노드가 레벨 1 이 된다.
            // 그러면 이건 **정렬된 연결 리스트**이고 조회가 O(n) 이다.
            //
            // 실제로 일어날 확률은 2^-n 이라 무시해도 되지만,
            // **최악을 막아주는 것이 아니라 확률을 낮춰줄 뿐**이라는 것은 알아야 한다.
            // 16번 레드블랙 트리는 이것을 보장으로 바꾼다. 대신 회전 코드를 짊어진다.
            Random alwaysTails = new Random() {
                @Override
                public double nextDouble() {
                    return 1.0;      // P(0.5) 보다 크므로 늘 멈춘다
                }
            };
            SkipList<Integer, String> sl = new SkipList<>(alwaysTails);
            for (int i = 0; i < 1000; i++) {
                sl.put(i, "v");
            }
            assertEquals(1, sl.currentLevel(), "층이 하나뿐이다. 연결 리스트다");
            assertEquals(1000, sl.size());
            assertEquals("v", sl.get(999), "답은 여전히 맞다. 느릴 뿐이다");
            assertEquals(999, sl.lastKey());
        }

        @Test
        @Timeout(10)
        @DisplayName("늘 앞면이면 모든 노드가 MAX_LEVEL 이 된다")
        void alwaysHeadsHitsCeiling() {
            Random alwaysHeads = new Random() {
                @Override
                public double nextDouble() {
                    return 0.0;
                }
            };
            SkipList<Integer, String> sl = new SkipList<>(alwaysHeads);
            for (int i = 0; i < 100; i++) {
                sl.put(i, "v");
            }
            assertEquals(SkipList.MAX_LEVEL, sl.currentLevel(),
                    "상한이 없으면 배열이 무한히 커진다");
            assertEquals(100, sl.size());
            assertEquals(List.of(0, 1, 2), sl.keysInRange(0, 2));
        }
    }
}
