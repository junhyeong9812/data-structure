package com.datastructure.stack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ArrayStack 이 Stack 계약을 지키는지.
 *
 * 계약 테스트는 물려받고, 여기에는 **배열 구현에만 있는 성질**만 적는다.
 * 이게 이 구조의 요점이다. 계약이 늘어나면 StackContractTest 한 곳만 고치면 양쪽에 적용된다.
 */
class ArrayStackTest extends StackContractTest {

    @Override
    protected <E> Stack<E> create() {
        return new ArrayStack<>();
    }

    @Test
    @DisplayName("꽉 차면 용량이 두 배로 늘어난다")
    void growsByDoubling() {
        ArrayStack<Integer> stack = new ArrayStack<>(2);
        assertEquals(2, stack.capacity());

        stack.push(1);
        stack.push(2);
        assertEquals(2, stack.capacity(), "아직 꽉 찼을 뿐이다");

        stack.push(3);
        assertEquals(4, stack.capacity(), "넘치는 순간 두 배가 된다");
    }

    @Test
    @DisplayName("확장은 계속 두 배씩 일어난다")
    void keepsDoubling() {
        ArrayStack<Integer> stack = new ArrayStack<>(2);
        for (int i = 0; i < 3; i++) stack.push(i);
        assertEquals(4, stack.capacity());
        for (int i = 0; i < 2; i++) stack.push(i);
        assertEquals(8, stack.capacity());
        for (int i = 0; i < 4; i++) stack.push(i);
        assertEquals(16, stack.capacity());
    }

    @Test
    @DisplayName("초기 용량이 0이어도 확장된다")
    void growsFromZeroCapacity() {
        ArrayStack<Integer> stack = new ArrayStack<>(0);
        stack.push(1);
        assertTrue(stack.capacity() >= 1);
        assertEquals(1, stack.peek());
    }

    @Test
    @DisplayName("pop 한 자리의 참조가 남지 않는다")
    void clearsPoppedSlot() {
        // top 밖은 논리적으로 없는 값이다. 참조가 남으면 그 객체는 GC 되지 않는다.
        ArrayStack<String> stack = new ArrayStack<>(4);
        stack.push("a");
        stack.push("b");
        stack.pop();

        assertNull(stack.elements[1], "꺼낸 자리를 비우지 않으면 누수다");
    }

    @Test
    @DisplayName("clear 후 참조가 남지 않고 용량은 유지된다")
    void clearDetachesButKeepsCapacity() {
        ArrayStack<String> stack = new ArrayStack<>(8);
        stack.push("a");
        stack.push("b");
        int before = stack.capacity();

        stack.clear();

        assertEquals(before, stack.capacity());
        for (int i = 0; i < stack.capacity(); i++) {
            assertNull(stack.elements[i], "인덱스 " + i + " 에 참조가 남아 있다");
        }
    }
}
