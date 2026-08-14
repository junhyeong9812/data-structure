package com.datastructure.stack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Stack 계약 테스트. 한 번만 쓰고 두 구현이 물려받는다.
 *
 * 구현마다 계약 테스트를 복사하면 둘이 조용히 어긋난다.
 * 한쪽에만 케이스를 추가하고 다른 쪽은 잊는 일이 반드시 생긴다.
 *
 * 그래서 여기에 계약을 한 번만 적고, 구현체 테스트는 create() 만 채운다.
 * 실패했을 때 어느 구현인지는 클래스 이름으로 바로 드러난다.
 */
abstract class StackContractTest {

    /** 검사할 빈 스택을 만든다. 이것이 서브클래스가 채우는 유일한 것이다. */
    protected abstract <E> Stack<E> create();

    @Nested
    @DisplayName("기본 동작")
    class Basics {

        @Test
        @DisplayName("나중에 넣은 것이 먼저 나온다")
        void lastInFirstOut() {
            Stack<String> stack = create();
            stack.push("a");
            stack.push("b");
            stack.push("c");

            assertEquals("c", stack.pop());
            assertEquals("b", stack.pop());
            assertEquals("a", stack.pop());
            assertTrue(stack.isEmpty());
        }

        @Test
        @DisplayName("peek 는 꺼내지 않는다")
        void peekDoesNotRemove() {
            Stack<String> stack = create();
            stack.push("a");

            assertEquals("a", stack.peek());
            assertEquals("a", stack.peek());
            assertEquals(1, stack.size(), "peek 이 크기를 바꾸면 안 된다");
            assertEquals("a", stack.pop());
        }

        @Test
        @DisplayName("크기를 정확히 센다")
        void tracksSize() {
            Stack<Integer> stack = create();
            assertEquals(0, stack.size());
            assertTrue(stack.isEmpty());

            for (int i = 0; i < 5; i++) {
                stack.push(i);
                assertEquals(i + 1, stack.size());
            }
            for (int i = 5; i > 0; i--) {
                assertEquals(i, stack.size());
                stack.pop();
            }
            assertTrue(stack.isEmpty());
        }

        @Test
        @DisplayName("null 도 담을 수 있다")
        void allowsNull() {
            Stack<String> stack = create();
            stack.push(null);

            assertEquals(1, stack.size());
            assertNull(stack.peek());
            assertNull(stack.pop());
            assertTrue(stack.isEmpty(), "null 을 담았다고 비었다고 보면 안 된다");
        }
    }

    @Nested
    @DisplayName("빈 스택")
    class WhenEmpty {

        @Test
        @DisplayName("pop 과 peek 은 예외다")
        void popAndPeekThrow() {
            Stack<Integer> stack = create();
            assertThrows(EmptyStackException.class, stack::pop);
            assertThrows(EmptyStackException.class, stack::peek);
        }

        @Test
        @DisplayName("전부 꺼낸 뒤에도 예외다")
        void throwsAfterDrain() {
            Stack<Integer> stack = create();
            stack.push(1);
            stack.pop();

            assertThrows(EmptyStackException.class, stack::pop);
            assertEquals(0, stack.size(), "실패한 pop 이 크기를 음수로 만들면 안 된다");
        }
    }

    @Nested
    @DisplayName("초기화와 재사용")
    class ClearAndReuse {

        @Test
        @DisplayName("clear 후에는 비어 있다")
        void clearsAll() {
            Stack<Integer> stack = create();
            for (int i = 0; i < 3; i++) stack.push(i);

            stack.clear();

            assertEquals(0, stack.size());
            assertTrue(stack.isEmpty());
            assertThrows(EmptyStackException.class, stack::pop);
        }

        @Test
        @DisplayName("clear 후에도 다시 쓸 수 있다")
        void reusableAfterClear() {
            Stack<Integer> stack = create();
            stack.push(1);
            stack.clear();
            stack.push(9);

            assertEquals(1, stack.size());
            assertEquals(9, stack.peek());
        }

        @Test
        @DisplayName("많이 넣고 빼도 순서가 유지된다")
        void survivesManyOperations() {
            Stack<Integer> stack = create();
            final int n = 1_000;
            for (int i = 0; i < n; i++) stack.push(i);
            assertEquals(n, stack.size());

            for (int i = n - 1; i >= 0; i--) {
                assertEquals(i, stack.pop(), "인덱스 " + i);
            }
            assertTrue(stack.isEmpty());
        }
    }
}
