package com.datastructure.bst;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SortedMap 계약 테스트.
 *
 * 지금은 구현이 하나뿐이지만 abstract 로 둔다.
 * 12번 스킵 리스트, 15번 B-트리, 16번 레드블랙 트리가 같은 계약을 구현하므로
 * 그때 이 파일을 그대로 물려받으면 된다.
 */
abstract class SortedMapContractTest {

    protected abstract <K extends Comparable<K>, V> SortedMap<K, V> create();

    protected static java.util.List<Integer> list(Iterable<Integer> it) {
        java.util.List<Integer> out = new ArrayList<>();
        it.forEach(out::add);
        return out;
    }

    protected SortedMap<Integer, String> of(int... keys) {
        SortedMap<Integer, String> map = create();
        for (int k : keys) map.put(k, "v" + k);
        return map;
    }

    @Nested
    @DisplayName("넣기와 찾기")
    class PutAndGet {

        @Test
        void storesAndRetrieves() {
            SortedMap<Integer, String> map = of(5, 3, 8);
            assertEquals("v5", map.get(5));
            assertEquals("v3", map.get(3));
            assertEquals("v8", map.get(8));
            assertEquals(3, map.size());
            assertNull(map.get(99));
            assertFalse(map.containsKey(99));
        }

        @Test
        @DisplayName("같은 키에 다시 넣으면 값이 바뀌고 이전 값이 나온다")
        void replacesExisting() {
            SortedMap<Integer, String> map = of(5);
            assertEquals("v5", map.put(5, "new"));
            assertEquals("new", map.get(5));
            assertEquals(1, map.size(), "크기가 늘면 안 된다");
        }

        @Test
        void rejectsNullKey() {
            SortedMap<Integer, String> map = create();
            assertThrows(IllegalArgumentException.class, () -> map.put(null, "x"));
        }

        @Test
        @DisplayName("값으로 null 을 담을 수 있다")
        void allowsNullValue() {
            SortedMap<Integer, String> map = create();
            map.put(1, null);
            assertNull(map.get(1));
            assertTrue(map.containsKey(1));
        }
    }

    @Nested
    @DisplayName("지우기 - 세 가지 경우")
    class Remove {

        @Test
        @DisplayName("자식이 없는 노드")
        void removesLeaf() {
            SortedMap<Integer, String> map = of(5, 3, 8);
            assertEquals("v3", map.remove(3));
            assertNull(map.get(3));
            assertEquals(java.util.List.of(5, 8), list(map.keys()));
        }

        @Test
        @DisplayName("자식이 하나인 노드")
        void removesNodeWithOneChild() {
            SortedMap<Integer, String> map = of(5, 3, 8, 2);   // 3 의 왼쪽에만 2 가 있다
            assertEquals("v3", map.remove(3));
            assertEquals(java.util.List.of(2, 5, 8), list(map.keys()), "자식이 자기 자리로 올라와야 한다");
            assertEquals("v2", map.get(2));
        }

        @Test
        @DisplayName("자식이 둘인 노드 - 순서가 유지되어야 한다")
        void removesNodeWithTwoChildren() {
            SortedMap<Integer, String> map = of(5, 3, 8, 2, 4, 7, 9);
            assertEquals("v3", map.remove(3), "3 은 자식이 둘(2, 4)이다");

            assertEquals(java.util.List.of(2, 4, 5, 7, 8, 9), list(map.keys()),
                "아무 자식이나 올리면 정렬이 깨진다");
            for (int k : new int[]{2, 4, 5, 7, 8, 9}) {
                assertEquals("v" + k, map.get(k), "키 " + k);
            }
        }

        @Test
        @DisplayName("뿌리를 지워도 된다")
        void removesRoot() {
            SortedMap<Integer, String> map = of(5, 3, 8, 2, 4, 7, 9);
            assertEquals("v5", map.remove(5));
            assertEquals(java.util.List.of(2, 3, 4, 7, 8, 9), list(map.keys()));
            assertEquals(6, map.size());
        }

        @Test
        void removingMissingKeyIsNull() {
            SortedMap<Integer, String> map = of(5);
            assertNull(map.remove(99));
            assertEquals(1, map.size());
        }

        @Test
        @DisplayName("전부 지워도 온전하고 다시 쓸 수 있다")
        void survivesFullDrain() {
            SortedMap<Integer, String> map = of(5, 3, 8, 2, 4, 7, 9);
            for (int k : new int[]{5, 3, 8, 2, 4, 7, 9}) {
                assertNotNull(map.remove(k), "키 " + k);
            }
            assertTrue(map.isEmpty());

            map.put(1, "again");
            assertEquals("again", map.get(1));
            assertEquals(java.util.List.of(1), list(map.keys()));
        }
    }

    @Nested
    @DisplayName("순서 질의 - 해시맵이 못 하는 것들")
    class OrderQueries {

        @Test
        void firstAndLast() {
            SortedMap<Integer, String> map = of(5, 3, 8, 2, 9);
            assertEquals(2, map.firstKey());
            assertEquals(9, map.lastKey());
        }

        @Test
        void emptyEndsThrow() {
            SortedMap<Integer, String> map = create();
            assertThrows(NoSuchElementException.class, map::firstKey);
            assertThrows(NoSuchElementException.class, map::lastKey);
        }

        @Test
        @DisplayName("floorKey 는 이하 중 가장 큰 것")
        void floor() {
            SortedMap<Integer, String> map = of(1, 5, 9);
            assertEquals(5, map.floorKey(6));
            assertEquals(5, map.floorKey(5), "자기 자신도 후보다");
            assertEquals(9, map.floorKey(100));
            assertNull(map.floorKey(0), "이하인 것이 없다");
        }

        @Test
        @DisplayName("ceilingKey 는 이상 중 가장 작은 것")
        void ceiling() {
            SortedMap<Integer, String> map = of(1, 5, 9);
            assertEquals(5, map.ceilingKey(2));
            assertEquals(5, map.ceilingKey(5), "자기 자신도 후보다");
            assertEquals(1, map.ceilingKey(-100));
            assertNull(map.ceilingKey(10), "이상인 것이 없다");
        }

        @Test
        @DisplayName("정렬된 순서로 순회한다")
        void iteratesSorted() {
            SortedMap<Integer, String> map = of(5, 3, 8, 2, 4, 7, 9);
            assertEquals(java.util.List.of(2, 3, 4, 5, 7, 8, 9), list(map.keys()),
                "넣은 순서가 아니라 정렬 순서여야 한다");
            assertTrue(list(of().keys()).isEmpty());
        }

        @Test
        void rangeQuery() {
            SortedMap<Integer, String> map = of(1, 3, 5, 7, 9);
            assertEquals(java.util.List.of(3, 5, 7), list(map.keysInRange(3, 7)));
            assertEquals(java.util.List.of(3, 5), list(map.keysInRange(2, 6)), "경계에 없는 값도 된다");
            assertEquals(java.util.List.of(1, 3, 5, 7, 9), list(map.keysInRange(-100, 100)));
            assertTrue(list(map.keysInRange(4, 4)).isEmpty(), "범위에 아무것도 없다");
            assertTrue(list(map.keysInRange(7, 3)).isEmpty(), "뒤집힌 범위는 빈 결과");
        }
    }

    @Nested
    @DisplayName("초기화와 대량 처리")
    class ClearAndBulk {

        @Test
        void clearsAndReusable() {
            SortedMap<Integer, String> map = of(5, 3, 8);
            map.clear();
            assertEquals(0, map.size());
            assertTrue(list(map.keys()).isEmpty());
            assertThrows(NoSuchElementException.class, map::firstKey);

            map.put(1, "x");
            assertEquals("x", map.get(1));
        }

        @Test
        @DisplayName("많이 넣고 섞어 지워도 정렬이 유지된다")
        void survivesManyOperations() {
            SortedMap<Integer, String> map = create();
            final int n = 500;
            for (int i = 0; i < n; i++) map.put((i * 37) % n, "v" + i);   // 흩어진 순서로
            assertEquals(n, map.size());

            for (int i = 0; i < n; i += 2) map.remove(i);
            assertEquals(n / 2, map.size());

            java.util.List<Integer> keys = list(map.keys());
            assertEquals(n / 2, keys.size());
            for (int i = 1; i < keys.size(); i++) {
                assertTrue(keys.get(i - 1) < keys.get(i), "정렬이 깨졌다: " + keys.get(i - 1) + " >= " + keys.get(i));
            }
        }
    }
}
