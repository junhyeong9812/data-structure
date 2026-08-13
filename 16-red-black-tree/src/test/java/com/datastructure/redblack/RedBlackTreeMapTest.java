package com.datastructure.redblack;

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

@DisplayName("RedBlackTreeMap: 계약")
class RedBlackTreeMapTest {

    private SortedTree<Integer, String> create() {
        return new RedBlackTreeMap<>();
    }

    private SortedTree<Integer, String> of(int... keys) {
        SortedTree<Integer, String> t = create();
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
            SortedTree<Integer, String> t = create();
            assertEquals(0, t.size());
            assertTrue(t.isEmpty());
            assertNull(t.get(1));
            assertFalse(t.containsKey(1));
            assertNull(t.remove(1));
            assertNull(t.firstKey());
            assertNull(t.lastKey());
            assertNull(t.floorKey(1));
            assertNull(t.ceilingKey(1));
            assertEquals(List.of(), t.keys());
            assertEquals(0, t.height());
        }
    }

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("넣고 꺼낸다")
        void putGet() {
            SortedTree<Integer, String> t = of(5, 3, 8, 1, 9);
            assertEquals("v5", t.get(5));
            assertNull(t.get(4));
            assertEquals(5, t.size());
            assertEquals(List.of(1, 3, 5, 8, 9), t.keys());
        }

        @Test
        @DisplayName("같은 키에 다시 넣으면 옛 값을 준다")
        void putReturnsOld() {
            SortedTree<Integer, String> t = create();
            assertNull(t.put(1, "a"));
            assertEquals("a", t.put(1, "b"));
            assertEquals("b", t.get(1));
            assertEquals(1, t.size());
        }

        @Test
        @DisplayName("first, last, floor, ceiling")
        void navigation() {
            SortedTree<Integer, String> t = of(10, 20, 30, 40);
            assertEquals(10, t.firstKey());
            assertEquals(40, t.lastKey());
            assertEquals(20, t.floorKey(20));
            assertEquals(20, t.floorKey(25));
            assertEquals(30, t.ceilingKey(25));
            assertNull(t.floorKey(5));
            assertNull(t.ceilingKey(45));
        }

        @Test
        @DisplayName("clear")
        void clear() {
            SortedTree<Integer, String> t = of(1, 2, 3, 4, 5);
            t.clear();
            assertEquals(0, t.size());
            assertEquals(0, t.height());
            t.put(9, "z");
            assertEquals(List.of(9), t.keys());
        }

        @Test
        @DisplayName("null 은 거부한다")
        void rejectsNull() {
            SortedTree<Integer, String> t = create();
            assertThrows(IllegalArgumentException.class, () -> t.put(null, "a"));
            assertThrows(IllegalArgumentException.class, () -> t.put(1, null));
            assertThrows(IllegalArgumentException.class, () -> t.get(null));
        }
    }

    @Nested
    @DisplayName("06번이 무너지던 자리")
    class WhereBstFails {

        @Test
        @DisplayName("정렬된 순서로 넣어도 높이가 log n 근처다")
        void sortedInsertStaysBalanced() {
            int n = 100_000;
            SortedTree<Integer, String> t = create();
            for (int i = 0; i < n; i++) {
                t.put(i, "v");
            }
            // 레드블랙 트리의 높이는 2*log2(n+1) 을 절대 안 넘는다. **확률이 아니라 보장이다.**
            int bound = (int) (2 * (Math.log(n + 1) / Math.log(2)));
            assertTrue(t.height() <= bound,
                    "높이 " + t.height() + " 가 상한 " + bound + " 을 넘었다");
            assertEquals(n, t.size());
            assertEquals(0, t.firstKey());
            assertEquals(n - 1, t.lastKey());
        }

        @Test
        @DisplayName("역순, 톱니 모양에서도 마찬가지다")
        void otherAdversarialOrders() {
            int n = 50_000;
            int bound = (int) (2 * (Math.log(n + 1) / Math.log(2)));

            SortedTree<Integer, String> desc = create();
            for (int i = n - 1; i >= 0; i--) {
                desc.put(i, "v");
            }
            assertTrue(desc.height() <= bound, "역순 높이 " + desc.height());

            SortedTree<Integer, String> zigzag = create();
            for (int i = 0; i < n / 2; i++) {
                zigzag.put(i, "v");
                zigzag.put(n - 1 - i, "v");
            }
            assertTrue(zigzag.height() <= bound, "톱니 높이 " + zigzag.height());
        }
    }

    @Nested
    @DisplayName("지우기")
    class Removal {

        @Test
        @DisplayName("하나씩")
        void one() {
            SortedTree<Integer, String> t = of(1, 2, 3);
            assertEquals("v2", t.remove(2));
            assertNull(t.remove(2));
            assertEquals(List.of(1, 3), t.keys());
        }

        @Test
        @DisplayName("전부 지우면 빈 트리로 돌아온다")
        void all() {
            SortedTree<Integer, String> t = create();
            for (int i = 0; i < 1000; i++) {
                t.put(i, "v");
            }
            for (int i = 0; i < 1000; i++) {
                assertEquals("v", t.remove(i), "키 " + i);
            }
            assertTrue(t.isEmpty());
            assertEquals(0, t.height());
            assertEquals(List.of(), t.keys());
        }

        @Test
        @DisplayName("역순으로 지워도 된다")
        void descending() {
            SortedTree<Integer, String> t = create();
            for (int i = 0; i < 1000; i++) {
                t.put(i, "v");
            }
            for (int i = 999; i >= 0; i--) {
                assertEquals("v", t.remove(i), "키 " + i);
                assertEquals(i, t.size());
            }
            assertEquals(0, t.height());
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("TreeMap 과 계속 같은 답을 낸다")
        void matchesTreeMap() {
            Random rnd = new Random(20260813L);
            SortedTree<Integer, String> t = create();
            TreeMap<Integer, String> ref = new TreeMap<>();

            for (int step = 0; step < 20_000; step++) {
                int key = rnd.nextInt(500);
                int op = rnd.nextInt(10);
                if (op < 5) {
                    String v = "v" + step;
                    assertEquals(ref.put(key, v), t.put(key, v), "put step " + step);
                } else if (op < 8) {
                    assertEquals(ref.remove(key), t.remove(key), "remove step " + step);
                } else {
                    assertEquals(ref.get(key), t.get(key), "get step " + step);
                }
                assertEquals(ref.size(), t.size(), "크기가 갈렸다 (step " + step + ")");
            }
            assertEquals(new ArrayList<>(ref.keySet()), t.keys());
            for (int k = -5; k < 505; k += 7) {
                assertEquals(ref.floorKey(k), t.floorKey(k), "floorKey(" + k + ")");
                assertEquals(ref.ceilingKey(k), t.ceilingKey(k), "ceilingKey(" + k + ")");
            }
        }

        @Test
        @DisplayName("무작위로 넣고 무작위로 지운다")
        void shuffled() {
            Random rnd = new Random(999L);
            int n = 5000;
            List<Integer> keys = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                keys.add(i);
            }
            Collections.shuffle(keys, rnd);
            SortedTree<Integer, String> t = create();
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
            SortedTree<Integer, String> t = create();
            for (int i = 0; i < 500_000; i++) {
                t.put(i, "v");
            }
            for (int i = 0; i < 500_000; i += 3) {
                assertEquals("v", t.get(i));
            }
            assertEquals(500_000, t.size());
        }
    }
}
