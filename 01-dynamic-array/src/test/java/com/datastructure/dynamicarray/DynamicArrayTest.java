package com.datastructure.dynamicarray;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;

import static com.datastructure.dynamicarray.TestSupport.raw;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 이 테스트는 DynamicArray 의 계약이다.
 * 각 테스트가 무엇을 확정하는지 읽으면서 치면 구현할 때 지켜야 할 것이 정리된다.
 */
class DynamicArrayTest {

    @Nested
    @DisplayName("용량 확장")
    class Capacity {

        @Test
        @DisplayName("꽉 차면 용량이 두 배로 늘어난다")
        void growsByDoubling() {
            DynamicArray<Integer> array = new DynamicArray<>(2);
            assertEquals(2, array.capacity());

            array.add(1);
            array.add(2);
            assertEquals(2, array.capacity(), "아직 꽉 찼을 뿐 넘치지는 않았다");

            array.add(3);
            assertEquals(4, array.capacity(), "넘치는 순간 두 배가 된다");
        }

        @Test
        @DisplayName("초기 용량이 0이어도 확장된다")
        void growsFromZeroCapacity() {
            DynamicArray<Integer> array = new DynamicArray<>(0);
            array.add(1);
            assertTrue(array.capacity() >= 1, "0 * 2 = 0 이므로 별도 처리가 필요하다");
            assertEquals(1, array.get(0));
        }

        @Test
        @DisplayName("확장은 계속 두 배씩 일어난다")
        void keepsDoubling() {
            // 첫 확장만 크게 하고 이후 1씩 늘리는 구현도 growsByDoubling 은 통과한다.
            // 상환 O(1) 이 성립하려면 계속 배수여야 한다.
            DynamicArray<Integer> array = new DynamicArray<>(2);
            for (int i = 0; i < 2; i++) array.add(i);
            assertEquals(2, array.capacity());

            array.add(99);
            assertEquals(4, array.capacity());
            for (int i = 0; i < 2; i++) array.add(i);   // size 5 -> 8 로
            assertEquals(8, array.capacity());
            for (int i = 0; i < 4; i++) array.add(i);   // size 9 -> 16 으로
            assertEquals(16, array.capacity());
        }

        @Test
        @DisplayName("1/4 이하로 떨어지면 절반으로 줄어든다")
        void shrinksAtQuarter() {
            DynamicArray<Integer> array = new DynamicArray<>(4);
            for (int i = 0; i < 16; i++) array.add(i);
            assertEquals(16, array.capacity());

            while (array.size() > 5) array.remove(array.size() - 1);
            assertEquals(16, array.capacity(), "아직 1/4(=4)보다 크다");

            array.remove(array.size() - 1);           // size 4 == 16/4
            assertEquals(8, array.capacity(), "1/4 에 닿으면 절반으로");
        }

        @Test
        @DisplayName("줄여도 최소 용량 아래로는 안 간다")
        void doesNotShrinkBelowMinimum() {
            DynamicArray<Integer> array = new DynamicArray<>(4);
            array.add(1);
            array.add(2);
            array.remove(1);
            array.remove(0);

            assertEquals(0, array.size());
            assertTrue(array.capacity() >= 4, "매번 절반으로 줄이면 0 까지 내려간다");
        }

        @Test
        @DisplayName("절반이 아니라 1/4 에서 줄이는 이유 (thrashing 방지)")
        void doesNotThrashAtBoundary() {
            // capacity 16, size 8 에서 add/remove 를 번갈아 한다.
            // 1/2 에서 줄이는 구현이라면 remove 마다 8 로 줄고 add 마다 16 으로 늘어난다.
            // 연산 하나가 매번 O(n) 전체 복사가 되어 상환 O(1) 이 무너진다.
            DynamicArray<Integer> array = new DynamicArray<>(4);
            for (int i = 0; i < 16; i++) array.add(i);
            while (array.size() > 8) array.remove(array.size() - 1);
            assertEquals(16, array.capacity(), "size 가 절반일 때 줄이면 안 된다");

            for (int i = 0; i < 50; i++) {
                array.add(99);
                array.remove(array.size() - 1);
                assertEquals(16, array.capacity(), "경계에서 용량이 오르내리면 thrashing 이다");
            }
        }

        @Test
        @DisplayName("줄인 뒤에도 원소가 온전하다")
        void keepsElementsAfterShrink() {
            DynamicArray<Integer> array = new DynamicArray<>(4);
            for (int i = 0; i < 16; i++) array.add(i);
            while (array.size() > 4) array.remove(array.size() - 1);

            assertEquals(8, array.capacity());
            assertEquals(4, array.size());
            for (int i = 0; i < 4; i++) assertEquals(i, array.get(i));
        }

        @Test
        @DisplayName("size 와 capacity 는 다르다")
        void sizeIsNotCapacity() {
            DynamicArray<Integer> array = new DynamicArray<>(10);
            array.add(1);
            assertEquals(1, array.size(), "담긴 개수");
            assertEquals(10, array.capacity(), "내부 배열의 길이");
        }
    }

    @Nested
    @DisplayName("추가")
    class Add {

        @Test
        @DisplayName("맨 뒤에 순서대로 쌓인다")
        void addsInOrder() {
            DynamicArray<String> array = new DynamicArray<>();
            array.add("a");
            array.add("b");
            assertEquals(2, array.size());
            assertEquals("a", array.get(0));
            assertEquals("b", array.get(1));
        }

        @Test
        @DisplayName("중간에 끼우면 뒤가 밀린다")
        void insertsAtIndex() {
            DynamicArray<Integer> array = new DynamicArray<>();
            array.add(1);
            array.add(3);
            array.add(1, 2);

            assertEquals(3, array.size());
            assertEquals(1, array.get(0));
            assertEquals(2, array.get(1));
            assertEquals(3, array.get(2));
        }

        @Test
        @DisplayName("꽉 찬 상태에서 중간에 끼워도 넘치지 않는다")
        void insertsIntoFullArray() {
            // 중간 삽입도 용량을 확보해야 한다. 빠뜨리면 여기서 ArrayIndexOutOfBounds 가 난다.
            DynamicArray<Integer> array = new DynamicArray<>(2);
            array.add(1);
            array.add(2);
            array.add(0, 0);

            assertEquals(3, array.size());
            assertEquals(0, array.get(0));
            assertEquals(1, array.get(1));
            assertEquals(2, array.get(2));
        }

        @Test
        @DisplayName("size 위치에 삽입하면 맨 뒤 추가와 같다")
        void insertAtSizeIsAppend() {
            DynamicArray<Integer> array = new DynamicArray<>();
            array.add(1);
            array.add(1, 2);
            assertEquals(2, array.get(1));
        }

        @Test
        @DisplayName("범위 밖 삽입은 거부한다")
        void rejectsOutOfRangeInsert() {
            DynamicArray<Integer> array = new DynamicArray<>();
            array.add(1);
            assertThrows(IndexOutOfBoundsException.class, () -> array.add(2, 99));
            assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 99));
        }

        @Test
        @DisplayName("null 도 담을 수 있다")
        void allowsNull() {
            DynamicArray<String> array = new DynamicArray<>();
            array.add(null);
            assertEquals(1, array.size());
            assertNull(array.get(0));
        }
    }

    @Nested
    @DisplayName("조회와 변경")
    class GetAndSet {

        @Test
        @DisplayName("set 은 이전 값을 반환한다")
        void setReturnsOldValue() {
            DynamicArray<String> array = new DynamicArray<>();
            array.add("old");
            assertEquals("old", array.set(0, "new"));
            assertEquals("new", array.get(0));
            assertEquals(1, array.size(), "set 은 크기를 바꾸지 않는다");
        }

        @Test
        @DisplayName("범위 밖 변경은 거부하고 상태를 바꾸지 않는다")
        void rejectsOutOfRangeSet() {
            // index == size 는 capacity 안이면 조용히 size 밖 칸을 바꿔버린다.
            // get 과 달리 눈에 안 보이므로 사후 상태까지 확인한다.
            DynamicArray<String> array = new DynamicArray<>(4);
            array.add("a");

            assertThrows(IndexOutOfBoundsException.class, () -> array.set(1, "z"));
            assertThrows(IndexOutOfBoundsException.class, () -> array.set(-1, "z"));
            assertEquals(1, array.size());
            assertEquals("a", array.get(0));
            assertNull(raw(array)[1], "size 밖 칸이 변경되면 안 된다");
        }

        @Test
        @DisplayName("범위 밖 조회는 거부한다")
        void rejectsOutOfRangeGet() {
            DynamicArray<Integer> array = new DynamicArray<>();
            array.add(1);
            assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
            assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        }
    }
}
