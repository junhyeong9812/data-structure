package com.datastructure.dynamicarray;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;

import static com.datastructure.dynamicarray.TestSupport.raw;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DynamicArray 계약 중 삭제, 탐색, 변환 부분.
 * DynamicArrayTest 와 한 파일이면 250줄을 넘어 따라치기 어려워지므로 나눴다.
 */
class DynamicArrayRemovalTest {

    @Nested
    @DisplayName("삭제")
    class Remove {

        @Test
        @DisplayName("인덱스로 지우면 뒤가 당겨진다")
        void removesByIndex() {
            DynamicArray<Integer> array = new DynamicArray<>();
            array.add(1);
            array.add(2);
            array.add(3);

            assertEquals(2, array.remove(1));
            assertEquals(2, array.size());
            assertEquals(1, array.get(0));
            assertEquals(3, array.get(1));
        }

        @Test
        @DisplayName("값으로 지우면 첫 번째 것만 지운다")
        void removesFirstMatchingValue() {
            DynamicArray<String> array = new DynamicArray<>();
            array.add("a");
            array.add("b");
            array.add("a");

            assertTrue(array.remove("a"));
            assertEquals(2, array.size());
            assertEquals("b", array.get(0));
            assertEquals("a", array.get(1));
        }

        @Test
        @DisplayName("없는 값을 지우려 하면 false")
        void returnsFalseWhenAbsent() {
            DynamicArray<String> array = new DynamicArray<>();
            array.add("a");
            assertFalse(array.remove("zzz"));
            assertEquals(1, array.size());
        }

        @Test
        @DisplayName("범위 밖 삭제는 거부하고 상태를 바꾸지 않는다")
        void rejectsOutOfRangeRemove() {
            // index == size 는 배열 범위 안이라 검사를 빠뜨리면 예외조차 안 나고
            // 마지막 원소가 조용히 사라진다. 예외 타입만 보면 안 되고 사후 상태까지 봐야 한다.
            DynamicArray<String> array = new DynamicArray<>();
            array.add("x");

            assertThrows(IndexOutOfBoundsException.class, () -> array.remove(1));
            assertThrows(IndexOutOfBoundsException.class, () -> array.remove(-1));
            assertEquals(1, array.size(), "실패한 remove 가 크기를 건드리면 안 된다");
            assertEquals("x", array.get(0));
        }

        @Test
        @DisplayName("지운 뒤 마지막 칸의 참조가 남지 않는다")
        void clearsTrailingReference() {
            DynamicArray<String> array = new DynamicArray<>(4);
            array.add("a");
            array.add("b");
            array.remove(1);

            // size 밖에 옛 참조가 남아 있으면 그 객체는 GC 되지 않는다 (메모리 누수).
            // size 만 줄이면 이 단언에서 걸린다.
            assertNull(raw(array)[1], "size 밖 칸을 끊지 않으면 누수다");
        }
    }

    @Nested
    @DisplayName("탐색과 초기화")
    class SearchAndClear {

        @Test
        @DisplayName("indexOf 는 첫 번째 위치를 준다")
        void findsFirstIndex() {
            DynamicArray<String> array = new DynamicArray<>();
            array.add("a");
            array.add("b");
            array.add("a");

            assertEquals(0, array.indexOf("a"));
            assertEquals(1, array.indexOf("b"));
            assertEquals(-1, array.indexOf("zzz"));
        }

        @Test
        @DisplayName("null 을 찾을 때 예외가 나지 않는다")
        void findsNullSafely() {
            DynamicArray<String> array = new DynamicArray<>();
            array.add("a");
            array.add(null);

            assertEquals(1, array.indexOf(null));
            assertTrue(array.contains(null));
        }

        @Test
        @DisplayName("remove(int) 와 remove(Object) 는 다른 메서드다")
        void removeOverloadDistinguishesIndexFromValue() {
            // ArrayList 의 대표적인 함정이다.
            // DynamicArray<Integer> 에서 remove(1) 은 "인덱스 1", remove(Integer.valueOf(1)) 은 "값 1".
            DynamicArray<Integer> array = new DynamicArray<>();
            array.add(10);
            array.add(20);

            assertEquals(20, array.remove(1), "int 는 인덱스로 해석된다");

            array.add(20);
            assertTrue(array.remove(Integer.valueOf(20)), "래핑하면 값으로 해석된다");
            assertEquals(1, array.size());
            assertEquals(10, array.get(0));
        }

        @Test
        @DisplayName("clear 후에는 비어 있고, 참조도 남지 않고, 용량은 유지된다")
        void clearsButKeepsCapacity() {
            DynamicArray<Integer> array = new DynamicArray<>(8);
            array.add(1);
            array.add(2);
            int capacityBefore = array.capacity();

            array.clear();

            assertEquals(0, array.size());
            assertTrue(array.isEmpty());
            assertEquals(capacityBefore, array.capacity());
            assertTrue(Arrays.stream(raw(array)).allMatch(Objects::isNull),
                "size 만 0 으로 만들면 참조가 그대로 남는다");
        }
    }

    @Nested
    @DisplayName("toArray")
    class ToArray {

        @Test
        @DisplayName("size 만큼만 담아서 준다")
        void copiesOnlyElements() {
            DynamicArray<Integer> array = new DynamicArray<>(100);
            array.add(1);
            array.add(2);

            Object[] result = array.toArray();
            assertEquals(2, result.length, "capacity 가 아니라 size 만큼이다");
            assertArrayEquals(new Object[]{1, 2}, result);
        }

        @Test
        @DisplayName("반환한 배열을 고쳐도 원본은 안 변한다")
        void returnsDefensiveCopy() {
            DynamicArray<Integer> array = new DynamicArray<>();
            array.add(1);

            Object[] result = array.toArray();
            result[0] = 999;

            assertEquals(1, array.get(0), "내부 배열을 그대로 주면 안 된다");
        }
    }
}
