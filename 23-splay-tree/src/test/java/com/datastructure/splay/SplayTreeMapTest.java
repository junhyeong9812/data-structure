package com.datastructure.splay;

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

@DisplayName("SplayTreeMap: 계약")
class SplayTreeMapTest {

    private SortedTree<Integer, String> create() {
        return new SplayTreeMap<>();
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
        @DisplayName("floor 와 ceiling 은 뿌리가 답이 아닐 때도 맞다")
        void navigationAfterSplay() {
            // splay 는 key 를 못 찾으면 **가장 가까운 노드**를 뿌리로 올린다.
            // 그 노드가 key 보다 클 수도 있고 작을 수도 있으므로 양쪽을 다 봐야 한다.
            SortedTree<Integer, String> t = of(10, 20, 30, 40, 50);
            assertEquals(20, t.floorKey(25));
            assertEquals(30, t.ceilingKey(25));
            assertEquals(30, t.floorKey(35));
            assertEquals(40, t.ceilingKey(35));
            assertEquals(50, t.floorKey(1000));
            assertNull(t.ceilingKey(1000));
            assertNull(t.floorKey(-1000));
            assertEquals(10, t.ceilingKey(-1000));
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
        @DisplayName("뿌리에 왼쪽이 없는 경우")
        void removeSmallest() {
            // remove 는 뿌리로 올린 뒤 왼쪽 부분트리에서 최댓값을 다시 splay 해 붙인다.
            // 왼쪽이 아예 없으면 붙일 곳이 없다. 그 경우를 따로 다뤄야 한다.
            SortedTree<Integer, String> t = of(1, 2, 3);
            assertEquals("v1", t.remove(1));
            assertEquals(List.of(2, 3), t.keys());
            assertEquals(2, t.firstKey());
            assertEquals("v2", t.remove(2));
            assertEquals("v3", t.remove(3));
            assertTrue(t.isEmpty());
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
        @DisplayName("TreeMap 과 2만 스텝을 대조한다")
        void matchesTreeMap() {
            Random rnd = new Random(20260814L);
            SortedTree<Integer, String> t = create();
            TreeMap<Integer, String> ref = new TreeMap<>();

            for (int step = 0; step < 20_000; step++) {
                int key = rnd.nextInt(500);
                int op = rnd.nextInt(12);
                if (op < 5) {
                    String v = "v" + step;
                    assertEquals(ref.put(key, v), t.put(key, v), "put step " + step);
                } else if (op < 8) {
                    assertEquals(ref.remove(key), t.remove(key), "remove step " + step);
                } else if (op < 10) {
                    assertEquals(ref.get(key), t.get(key), "get step " + step);
                } else if (op < 11) {
                    assertEquals(ref.floorKey(key), t.floorKey(key), "floorKey step " + step);
                } else {
                    assertEquals(ref.ceilingKey(key), t.ceilingKey(key), "ceilingKey step " + step);
                }
                assertEquals(ref.size(), t.size(), "크기가 갈렸다 (step " + step + ")");
            }
            assertEquals(new ArrayList<>(ref.keySet()), t.keys());
            assertEquals(ref.firstKey(), t.firstKey());
            assertEquals(ref.lastKey(), t.lastKey());
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
        @DisplayName("20만 개")
        void twoHundredThousand() {
            // 정렬 순서로 넣으면 한 줄이 되고, 그 상태에서 깊은 키를 조회하면
            // 재귀 splay 가 스택을 20만 층 쌓는다. 그래서 넣는 순서를 섞는다.
            // 이것이 이 구현의 진짜 한계다. README 에 적어두었다.
            List<Integer> keys = new ArrayList<>();
            for (int i = 0; i < 200_000; i++) {
                keys.add(i);
            }
            Collections.shuffle(keys, new Random(4242L));
            SortedTree<Integer, String> t = create();
            for (int k : keys) {
                t.put(k, "v");
            }
            assertEquals(200_000, t.size());
            for (int k : keys) {
                assertEquals("v", t.get(k));
            }
        }
    }
}
