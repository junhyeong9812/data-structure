package com.datastructure.linkedlist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * List 계약 테스트 - 기본 연산.
 *
 * **두 구현이 물려받는다.** 계약을 한 곳에만 적어야 둘이 어긋나지 않는다.
 * 여기는 공개 API 만 쓴다. 내부 구조 검사는 구현별 테스트가 한다.
 */
abstract class ListContractTest {

    protected abstract <E> List<E> create();

    protected static <E> List<E> filled(List<E> list, java.util.Collection<E> values) {
        values.forEach(list::add);
        return list;
    }

    @Nested
    @DisplayName("추가")
    class Add {

        @Test
        @DisplayName("맨 뒤에 순서대로 쌓인다")
        void addsInOrder() {
            List<String> list = create();
            list.add("a");
            list.add("b");
            assertEquals(2, list.size());
            assertEquals("a", list.get(0));
            assertEquals("b", list.get(1));
        }

        @Test
        @DisplayName("중간에 끼우면 뒤가 밀린다")
        void insertsAtIndex() {
            List<Integer> list = create();
            list.add(1);
            list.add(3);
            list.add(1, 2);

            assertArrayEquals(new Object[]{1, 2, 3}, list.toArray());
        }

        @Test
        @DisplayName("0 위치와 size 위치에도 끼울 수 있다")
        void insertsAtBothEnds() {
            List<Integer> list = create();
            list.add(2);
            list.add(0, 1);
            list.add(2, 3);

            assertArrayEquals(new Object[]{1, 2, 3}, list.toArray());
        }

        @Test
        @DisplayName("범위 밖 삽입은 거부하고 상태를 바꾸지 않는다")
        void rejectsOutOfRangeInsert() {
            List<Integer> list = create();
            list.add(1);
            assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, 99));
            assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 99));
            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("null 도 담을 수 있다")
        void allowsNull() {
            List<String> list = create();
            list.add(null);
            assertEquals(1, list.size());
            assertNull(list.get(0));
        }
    }

    @Nested
    @DisplayName("조회와 변경")
    class GetAndSet {

        @Test
        @DisplayName("모든 인덱스를 정확히 찾는다")
        void findsEveryIndex() {
            List<Integer> list = create();
            for (int i = 0; i < 10; i++) list.add(i);
            for (int i = 0; i < 10; i++) assertEquals(i, list.get(i), "인덱스 " + i);
        }

        @Test
        @DisplayName("set 은 이전 값을 반환하고 크기를 바꾸지 않는다")
        void setReturnsOldValue() {
            List<String> list = create();
            list.add("old");
            assertEquals("old", list.set(0, "new"));
            assertEquals("new", list.get(0));
            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("범위 밖 조회와 변경은 거부한다")
        void rejectsOutOfRange() {
            List<Integer> list = create();
            list.add(1);
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> list.set(1, 9));
            assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, 9));
        }
    }

    @Nested
    @DisplayName("삭제")
    class Remove {

        @Test
        @DisplayName("가운데를 지우면 앞뒤가 이어진다")
        void removesMiddle() {
            List<Integer> list = create();
            for (int v : new int[]{1, 2, 3}) list.add(v);

            assertEquals(2, list.remove(1));
            assertArrayEquals(new Object[]{1, 3}, list.toArray());
        }

        @Test
        @DisplayName("맨 앞과 맨 뒤도 지울 수 있다")
        void removesBothEnds() {
            List<Integer> list = create();
            for (int v : new int[]{1, 2, 3}) list.add(v);

            assertEquals(1, list.remove(0));
            assertEquals(3, list.remove(list.size() - 1));
            assertArrayEquals(new Object[]{2}, list.toArray());
        }

        @Test
        @DisplayName("마지막 하나를 지워도 온전하다")
        void removesOnlyElement() {
            List<Integer> list = create();
            list.add(42);
            assertEquals(42, list.remove(0));
            assertTrue(list.isEmpty());

            list.add(9);              // 다시 쓸 수 있어야 한다
            assertEquals(9, list.get(0));
        }

        @Test
        @DisplayName("값으로 지우면 첫 번째 것만 지운다")
        void removesFirstMatchingValue() {
            List<Integer> list = create();
            for (int v : new int[]{1, 2, 1}) list.add(v);

            assertTrue(list.remove(Integer.valueOf(1)));
            assertArrayEquals(new Object[]{2, 1}, list.toArray());
            assertFalse(list.remove(Integer.valueOf(99)));
        }

        @Test
        @DisplayName("범위 밖 삭제는 거부하고 상태를 바꾸지 않는다")
        void rejectsOutOfRangeRemove() {
            List<Integer> list = create();
            list.add(1);
            assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
            assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
            assertEquals(1, list.size(), "실패한 remove 가 크기를 건드리면 안 된다");
        }

        @Test
        @DisplayName("전부 지워도 온전하다")
        void survivesFullDrain() {
            List<Integer> list = create();
            for (int i = 0; i < 5; i++) list.add(i);
            while (!list.isEmpty()) list.remove(0);
            assertEquals(0, list.size());
        }
    }

    @Nested
    @DisplayName("탐색과 초기화")
    class SearchAndClear {

        @Test
        void findsFirstIndex() {
            List<String> list = create();
            for (String v : new String[]{"a", "b", "a"}) list.add(v);

            assertEquals(0, list.indexOf("a"));
            assertEquals(1, list.indexOf("b"));
            assertEquals(-1, list.indexOf("zzz"));
            assertTrue(list.contains("b"));
        }

        @Test
        @DisplayName("null 을 찾을 때 예외가 나지 않는다")
        void findsNullSafely() {
            List<String> list = create();
            list.add("a");
            list.add(null);

            assertEquals(1, list.indexOf(null));
            assertTrue(list.contains(null));
        }

        @Test
        @DisplayName("clear 후에는 비어 있고 다시 쓸 수 있다")
        void clearsAndReusable() {
            List<Integer> list = create();
            for (int v : new int[]{1, 2, 3}) list.add(v);

            list.clear();
            assertEquals(0, list.size());
            assertEquals(0, list.toArray().length);

            list.add(9);
            assertArrayEquals(new Object[]{9}, list.toArray());
        }

        @Test
        void toArrayInOrder() {
            List<Integer> list = create();
            for (int v : new int[]{1, 2, 3}) list.add(v);
            assertArrayEquals(new Object[]{1, 2, 3}, list.toArray());
            assertEquals(0, create().toArray().length);
        }
    }
}
