package com.datastructure.btree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** SearchTree 계약. B-트리와 B+트리가 똑같이 지켜야 하는 것만 여기 있다. */
abstract class SearchTreeContractTest {

    /** 차수를 받아 만든다. 작은 차수일수록 분할과 병합이 자주 일어나 버그가 잘 드러난다. */
    protected abstract SearchTree<Integer, String> create(int degree);

    /** 이 구현이 다룰 수 있는 가장 작은 차수. */
    protected abstract int smallestDegree();

    protected SearchTree<Integer, String> of(int... keys) {
        SearchTree<Integer, String> t = create(smallestDegree());
        for (int k : keys) {
            t.put(k, "v" + k);
        }
        return t;
    }

    @Nested
    @DisplayName("빈 트리")
    class Empty {

        @Test
        @DisplayName("아무것도 없다")
        void nothing() {
            SearchTree<Integer, String> t = create(smallestDegree());
            assertEquals(0, t.size());
            assertTrue(t.isEmpty());
            assertNull(t.get(1));
            assertFalse(t.containsKey(1));
            assertNull(t.remove(1));
            assertNull(t.firstKey());
            assertNull(t.lastKey());
            assertEquals(List.of(), t.keys());
            assertEquals(1, t.height(), "빈 트리도 잎 하나짜리다");
        }
    }

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("넣고 꺼낸다")
        void putGet() {
            SearchTree<Integer, String> t = of(5, 3, 8, 1, 9);
            assertEquals("v5", t.get(5));
            assertEquals("v1", t.get(1));
            assertNull(t.get(4));
            assertEquals(5, t.size());
            assertTrue(t.containsKey(9));
        }

        @Test
        @DisplayName("같은 키에 다시 넣으면 옛 값을 준다")
        void putReturnsOld() {
            SearchTree<Integer, String> t = create(smallestDegree());
            assertNull(t.put(1, "a"));
            assertEquals("a", t.put(1, "b"));
            assertEquals("b", t.get(1));
            assertEquals(1, t.size());
        }

        @Test
        @DisplayName("정렬 순서로 나온다")
        void sorted() {
            assertEquals(List.of(1, 3, 5, 8, 9), of(5, 3, 8, 1, 9).keys());
            assertEquals(List.of(1, 3, 5, 8, 9), of(9, 8, 5, 3, 1).keys());
        }

        @Test
        @DisplayName("first 와 last")
        void firstAndLast() {
            SearchTree<Integer, String> t = of(5, 3, 8, 1, 9);
            assertEquals(1, t.firstKey());
            assertEquals(9, t.lastKey());
        }

        @Test
        @DisplayName("clear")
        void clear() {
            SearchTree<Integer, String> t = of(1, 2, 3, 4, 5, 6, 7, 8);
            t.clear();
            assertEquals(0, t.size());
            assertEquals(List.of(), t.keys());
            assertEquals(1, t.height());
            t.put(9, "z");
            assertEquals("z", t.get(9));
            assertEquals(List.of(9), t.keys());
        }

        @Test
        @DisplayName("null 은 거부한다")
        void rejectsNull() {
            SearchTree<Integer, String> t = create(smallestDegree());
            assertThrows(IllegalArgumentException.class, () -> t.put(null, "a"));
            assertThrows(IllegalArgumentException.class, () -> t.put(1, null));
            assertThrows(IllegalArgumentException.class, () -> t.get(null));
        }
    }

    @Nested
    @DisplayName("자라기")
    class Growth {

        @Test
        @DisplayName("차수를 넘으면 층이 는다")
        void heightGrows() {
            SearchTree<Integer, String> t = create(smallestDegree());
            assertEquals(1, t.height());
            for (int i = 0; i < 1000; i++) {
                t.put(i, "v");
            }
            assertTrue(t.height() > 1, "1000개면 여러 층이어야 한다");
            assertEquals(1000, t.size());
        }

        @Test
        @DisplayName("정렬 입력에서도 균형이 유지된다")
        void sortedInsertStaysBalanced() {
            // **06번 BST 는 여기서 높이가 n 이 됐다.**
            // B-트리는 위로 자라기 때문에 무슨 순서로 넣어도 모든 잎이 같은 깊이다.
            SearchTree<Integer, String> ascending = create(smallestDegree());
            SearchTree<Integer, String> descending = create(smallestDegree());
            for (int i = 0; i < 10_000; i++) {
                ascending.put(i, "v");
                descending.put(-i, "v");
            }
            // 분할이 좌우 대칭이 아니라 한 층까지는 차이가 날 수 있다.
            // 요점은 **n 이 아니라 log n 근처에 머문다**는 것이다.
            assertTrue(Math.abs(ascending.height() - descending.height()) <= 1,
                    "오름차순 " + ascending.height() + "층, 내림차순 " + descending.height() + "층");
            assertTrue(ascending.height() < 30,
                    "BST 였다면 10000 층이다. 실제 " + ascending.height());
            assertTrue(descending.height() < 30, "높이 " + descending.height());
        }

        @Test
        @DisplayName("차수가 클수록 낮아진다")
        void higherDegreeIsShallower() {
            SearchTree<Integer, String> small = create(smallestDegree());
            SearchTree<Integer, String> big = create(64);
            for (int i = 0; i < 20_000; i++) {
                small.put(i, "v");
                big.put(i, "v");
            }
            assertTrue(big.height() < small.height(),
                    "차수 64: " + big.height() + "층, 차수 " + smallestDegree()
                            + ": " + small.height() + "층");
            assertTrue(big.height() <= 4, "2만 개가 " + big.height() + "층이다");
        }
    }

    @Nested
    @DisplayName("지우기")
    class Removal {

        @Test
        @DisplayName("잎에서 지운다")
        void removeFromLeaf() {
            SearchTree<Integer, String> t = of(1, 2, 3);
            assertEquals("v2", t.remove(2));
            assertNull(t.get(2));
            assertEquals(2, t.size());
            assertEquals(List.of(1, 3), t.keys());
            assertNull(t.remove(2), "두 번째는 null");
        }

        @Test
        @DisplayName("내부 노드의 키를 지운다")
        void removeFromInternal() {
            SearchTree<Integer, String> t = create(smallestDegree());
            for (int i = 0; i < 100; i++) {
                t.put(i, "v" + i);
            }
            List<Integer> before = t.keys();
            assertEquals(100, before.size());
            for (int i = 0; i < 100; i += 2) {
                assertEquals("v" + i, t.remove(i), "키 " + i);
            }
            assertEquals(50, t.size());
            List<Integer> expected = new ArrayList<>();
            for (int i = 1; i < 100; i += 2) {
                expected.add(i);
            }
            assertEquals(expected, t.keys());
        }

        @Test
        @DisplayName("전부 지우면 뿌리가 잎으로 돌아온다")
        void shrinksBackToLeaf() {
            SearchTree<Integer, String> t = create(smallestDegree());
            for (int i = 0; i < 500; i++) {
                t.put(i, "v");
            }
            assertTrue(t.height() > 1);
            for (int i = 0; i < 500; i++) {
                assertEquals("v", t.remove(i), "키 " + i);
                assertEquals(500 - i - 1, t.size());
            }
            assertEquals(1, t.height(), "다 지우면 다시 한 층이어야 한다");
            assertTrue(t.isEmpty());
            assertEquals(List.of(), t.keys());
        }

        @Test
        @DisplayName("역순으로 지워도 된다")
        void removeDescending() {
            SearchTree<Integer, String> t = create(smallestDegree());
            for (int i = 0; i < 300; i++) {
                t.put(i, "v");
            }
            for (int i = 299; i >= 0; i--) {
                assertEquals("v", t.remove(i), "키 " + i);
                assertEquals(i, t.size());
                if (i > 0) {
                    assertEquals(0, t.firstKey());
                    assertEquals(i - 1, t.lastKey());
                }
            }
            assertEquals(1, t.height());
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("TreeMap 과 계속 같은 답을 낸다")
        void matchesTreeMap() {
            for (int degree : new int[]{smallestDegree(), 3, 4, 8}) {
                Random rnd = new Random(20260813L + degree);
                SearchTree<Integer, String> t = create(degree);
                TreeMap<Integer, String> ref = new TreeMap<>();

                for (int step = 0; step < 12_000; step++) {
                    int key = rnd.nextInt(400);
                    int op = rnd.nextInt(10);
                    if (op < 5) {
                        String v = "v" + step;
                        assertEquals(ref.put(key, v), t.put(key, v),
                                "차수 " + degree + " put step " + step);
                    } else if (op < 8) {
                        assertEquals(ref.remove(key), t.remove(key),
                                "차수 " + degree + " remove step " + step);
                    } else {
                        assertEquals(ref.get(key), t.get(key),
                                "차수 " + degree + " get step " + step);
                    }
                    assertEquals(ref.size(), t.size(),
                            "차수 " + degree + " 크기가 갈렸다 (step " + step + ")");
                }
                assertEquals(new ArrayList<>(ref.keySet()), t.keys(), "차수 " + degree);
                assertEquals(ref.isEmpty() ? null : ref.firstKey(), t.firstKey());
                assertEquals(ref.isEmpty() ? null : ref.lastKey(), t.lastKey());
            }
        }

        @Test
        @DisplayName("무작위 순서로 넣고 무작위 순서로 지운다")
        void shuffledInsertAndDelete() {
            Random rnd = new Random(4242L);
            int n = 3000;
            List<Integer> keys = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                keys.add(i);
            }
            Collections.shuffle(keys, rnd);

            SearchTree<Integer, String> t = create(smallestDegree());
            for (int k : keys) {
                t.put(k, "v" + k);
            }
            assertEquals(n, t.size());

            Collections.shuffle(keys, rnd);
            TreeMap<Integer, String> ref = new TreeMap<>();
            for (int i = 0; i < n; i++) {
                ref.put(i, "v" + i);
            }
            for (int k : keys) {
                assertEquals(ref.remove(k), t.remove(k), "키 " + k);
                assertEquals(ref.size(), t.size());
                assertEquals(new ArrayList<>(ref.keySet()), t.keys(), "키 " + k + " 를 지운 뒤");
            }
            assertTrue(t.isEmpty());
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(25)
        @DisplayName("50만 개")
        void halfMillion() {
            SearchTree<Integer, String> t = create(64);
            for (int i = 0; i < 500_000; i++) {
                t.put(i, "v");
            }
            assertEquals(500_000, t.size());
            assertTrue(t.height() <= 5, "차수 64 면 50만 개가 " + t.height() + "층이다");
            for (int i = 0; i < 500_000; i += 3) {
                assertEquals("v", t.get(i));
            }
        }
    }
}
