package com.datastructure.skiplist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** OrderedMap 계약. 06번 SortedMapContractTest 와 같은 내용이다. */
abstract class OrderedMapContractTest {

    protected abstract OrderedMap<Integer, String> create();

    protected OrderedMap<Integer, String> of(int... keys) {
        OrderedMap<Integer, String> m = create();
        for (int k : keys) {
            m.put(k, "v" + k);
        }
        return m;
    }

    @Nested
    @DisplayName("빈 맵")
    class Empty {

        @Test
        @DisplayName("아무것도 없다")
        void nothing() {
            OrderedMap<Integer, String> m = create();
            assertEquals(0, m.size());
            assertTrue(m.isEmpty());
            assertNull(m.get(1));
            assertFalse(m.containsKey(1));
            assertNull(m.remove(1));
            assertNull(m.firstKey());
            assertNull(m.lastKey());
            assertNull(m.floorKey(1));
            assertNull(m.ceilingKey(1));
            assertEquals(List.of(), m.keys());
            assertEquals(List.of(), m.keysInRange(0, 100));
        }
    }

    @Nested
    @DisplayName("기본 동작")
    class Basics {

        @Test
        @DisplayName("넣고 꺼낸다")
        void putGet() {
            OrderedMap<Integer, String> m = of(5, 3, 8);
            assertEquals("v5", m.get(5));
            assertEquals("v3", m.get(3));
            assertEquals("v8", m.get(8));
            assertNull(m.get(4));
            assertEquals(3, m.size());
        }

        @Test
        @DisplayName("같은 키에 다시 넣으면 옛 값을 준다")
        void putReturnsOld() {
            OrderedMap<Integer, String> m = create();
            assertNull(m.put(1, "a"));
            assertEquals("a", m.put(1, "b"));
            assertEquals("b", m.get(1));
            assertEquals(1, m.size(), "크기는 안 는다");
        }

        @Test
        @DisplayName("지우면 옛 값을 준다")
        void removeReturnsOld() {
            OrderedMap<Integer, String> m = of(1, 2, 3);
            assertEquals("v2", m.remove(2));
            assertNull(m.remove(2));
            assertNull(m.get(2));
            assertEquals(2, m.size());
            assertEquals(List.of(1, 3), m.keys());
        }

        @Test
        @DisplayName("clear")
        void clear() {
            OrderedMap<Integer, String> m = of(1, 2, 3);
            m.clear();
            assertEquals(0, m.size());
            assertTrue(m.isEmpty());
            assertEquals(List.of(), m.keys());
            assertNull(m.firstKey());
            m.put(9, "z");
            assertEquals("z", m.get(9), "비운 뒤에도 쓸 수 있다");
            assertEquals(List.of(9), m.keys());
        }

        @Test
        @DisplayName("null 은 거부한다")
        void rejectsNull() {
            OrderedMap<Integer, String> m = create();
            assertThrows(IllegalArgumentException.class, () -> m.put(null, "a"));
            assertThrows(IllegalArgumentException.class, () -> m.put(1, null));
            assertThrows(IllegalArgumentException.class, () -> m.get(null));
        }
    }

    @Nested
    @DisplayName("정렬 순서")
    class Ordering {

        @Test
        @DisplayName("넣은 순서와 무관하게 정렬돼 나온다")
        void alwaysSorted() {
            assertEquals(List.of(1, 3, 5, 7, 9), of(5, 1, 9, 3, 7).keys());
            assertEquals(List.of(1, 3, 5, 7, 9), of(9, 7, 5, 3, 1).keys());
            assertEquals(List.of(1, 3, 5, 7, 9), of(1, 3, 5, 7, 9).keys());
        }

        @Test
        @DisplayName("firstKey 와 lastKey")
        void firstAndLast() {
            OrderedMap<Integer, String> m = of(5, 1, 9, 3);
            assertEquals(1, m.firstKey());
            assertEquals(9, m.lastKey());
            m.remove(1);
            assertEquals(3, m.firstKey());
            m.remove(9);
            assertEquals(5, m.lastKey());
        }

        @Test
        @DisplayName("원소가 하나면 first 와 last 가 같다")
        void singleton() {
            OrderedMap<Integer, String> m = of(42);
            assertEquals(42, m.firstKey());
            assertEquals(42, m.lastKey());
        }
    }

    @Nested
    @DisplayName("floor 와 ceiling")
    class FloorCeiling {

        private OrderedMap<Integer, String> sample() {
            return of(10, 20, 30, 40);
        }

        @Test
        @DisplayName("정확히 있는 키")
        void exact() {
            OrderedMap<Integer, String> m = sample();
            assertEquals(20, m.floorKey(20));
            assertEquals(20, m.ceilingKey(20));
        }

        @Test
        @DisplayName("사이에 있는 키")
        void between() {
            OrderedMap<Integer, String> m = sample();
            assertEquals(20, m.floorKey(25));
            assertEquals(30, m.ceilingKey(25));
        }

        @Test
        @DisplayName("범위 밖")
        void outside() {
            OrderedMap<Integer, String> m = sample();
            assertNull(m.floorKey(5), "5 이하인 것이 없다");
            assertEquals(10, m.ceilingKey(5));
            assertEquals(40, m.floorKey(100));
            assertNull(m.ceilingKey(100), "100 이상인 것이 없다");
        }

        @Test
        @DisplayName("경계값")
        void boundaries() {
            OrderedMap<Integer, String> m = sample();
            assertEquals(10, m.floorKey(10));
            assertNull(m.floorKey(9));
            assertEquals(40, m.ceilingKey(40));
            assertNull(m.ceilingKey(41));
        }
    }

    @Nested
    @DisplayName("범위 조회")
    class Range {

        private OrderedMap<Integer, String> sample() {
            return of(1, 3, 5, 7, 9, 11);
        }

        @Test
        @DisplayName("양끝을 포함한다")
        void inclusive() {
            assertEquals(List.of(3, 5, 7), sample().keysInRange(3, 7));
        }

        @Test
        @DisplayName("없는 경계값도 된다")
        void betweenBoundaries() {
            assertEquals(List.of(5, 7, 9), sample().keysInRange(4, 10));
        }

        @Test
        @DisplayName("전체와 빈 범위")
        void wholeAndEmpty() {
            assertEquals(List.of(1, 3, 5, 7, 9, 11), sample().keysInRange(-100, 100));
            assertEquals(List.of(), sample().keysInRange(100, 200));
            assertEquals(List.of(), sample().keysInRange(7, 3), "from > to 면 빈 리스트다");
            assertEquals(List.of(5), sample().keysInRange(5, 5));
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class RandomCrossCheck {

        @Test
        @DisplayName("TreeMap 과 계속 같은 답을 낸다")
        void matchesTreeMap() {
            Random rnd = new Random(20260813L);
            OrderedMap<Integer, String> m = create();
            TreeMap<Integer, String> ref = new TreeMap<>();

            for (int step = 0; step < 8000; step++) {
                int key = rnd.nextInt(300);
                int op = rnd.nextInt(10);
                if (op < 6) {
                    String v = "v" + step;
                    assertEquals(ref.put(key, v), m.put(key, v), "put step " + step);
                } else if (op < 8) {
                    assertEquals(ref.remove(key), m.remove(key), "remove step " + step);
                } else {
                    assertEquals(ref.get(key), m.get(key), "get step " + step);
                }
                assertEquals(ref.size(), m.size(), "크기가 갈렸다 (step " + step + ")");
            }

            assertEquals(new ArrayList<>(ref.keySet()), m.keys());
            assertEquals(ref.firstKey(), m.firstKey());
            assertEquals(ref.lastKey(), m.lastKey());
            for (int k = -5; k < 305; k += 7) {
                assertEquals(ref.floorKey(k), m.floorKey(k), "floorKey(" + k + ")");
                assertEquals(ref.ceilingKey(k), m.ceilingKey(k), "ceilingKey(" + k + ")");
            }
            for (int from = 0; from < 300; from += 41) {
                int to = from + 30;
                assertEquals(new ArrayList<>(ref.subMap(from, true, to, true).keySet()),
                        m.keysInRange(from, to), "범위 [" + from + ", " + to + "]");
            }
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(20)
        @DisplayName("정렬된 순서로 10만 개를 넣어도 안 무너진다")
        void sortedInsertIsFine() {
            // **06번 이진 탐색 트리는 여기서 죽는다.** 높이가 10만이 되기 때문이다.
            OrderedMap<Integer, String> m = create();
            for (int i = 0; i < 100_000; i++) {
                m.put(i, "v");
            }
            for (int i = 0; i < 100_000; i++) {
                assertEquals("v", m.get(i));
            }
            assertEquals(0, m.firstKey());
            assertEquals(99_999, m.lastKey());
        }

        @Test
        @Timeout(20)
        @DisplayName("범위 조회가 전체를 훑지 않는다")
        void rangeDoesNotScanAll() {
            OrderedMap<Integer, String> m = create();
            for (int i = 0; i < 200_000; i++) {
                m.put(i, "v");
            }
            for (int q = 0; q < 20_000; q++) {
                assertEquals(11, m.keysInRange(150_000, 150_010).size());
            }
        }
    }
}
