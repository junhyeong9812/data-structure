package com.datastructure.queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Queue 계약 테스트. **네 구현이 전부 물려받는다.**
 * (ArrayQueue, CircularQueue, 그리고 Deque 인 ArrayDeque, LinkedDeque)
 *
 * Deque 가 Queue 를 확장하므로 테스트도 같은 모양으로 상속한다.
 * 인터페이스 계층과 테스트 계층이 같은 모양이면 어디에 무엇을 적을지 헷갈리지 않는다.
 */
abstract class QueueContractTest {

    protected abstract <E> Queue<E> createQueue();

    @Nested
    @DisplayName("선입선출")
    class Fifo {

        @Test
        @DisplayName("먼저 넣은 것이 먼저 나온다")
        void firstInFirstOut() {
            Queue<String> q = createQueue();
            q.enqueue("a");
            q.enqueue("b");
            q.enqueue("c");

            assertEquals("a", q.dequeue());
            assertEquals("b", q.dequeue());
            assertEquals("c", q.dequeue());
            assertTrue(q.isEmpty());
        }

        @Test
        @DisplayName("peek 는 빼지 않는다")
        void peekDoesNotRemove() {
            Queue<String> q = createQueue();
            q.enqueue("a");
            q.enqueue("b");

            assertEquals("a", q.peek());
            assertEquals("a", q.peek());
            assertEquals(2, q.size(), "peek 이 크기를 바꾸면 안 된다");
        }

        @Test
        @DisplayName("넣고 빼기를 섞어도 순서가 유지된다")
        void interleavedOperations() {
            Queue<Integer> q = createQueue();
            q.enqueue(1);
            q.enqueue(2);
            assertEquals(1, q.dequeue());
            q.enqueue(3);
            assertEquals(2, q.dequeue());
            assertEquals(3, q.dequeue());
            assertTrue(q.isEmpty());
        }

        @Test
        @DisplayName("크기를 정확히 센다")
        void tracksSize() {
            Queue<Integer> q = createQueue();
            assertEquals(0, q.size());
            for (int i = 0; i < 5; i++) {
                q.enqueue(i);
                assertEquals(i + 1, q.size());
            }
            for (int i = 5; i > 0; i--) {
                assertEquals(i, q.size());
                q.dequeue();
            }
            assertTrue(q.isEmpty());
        }

        @Test
        @DisplayName("null 도 담을 수 있다")
        void allowsNull() {
            Queue<String> q = createQueue();
            q.enqueue(null);
            assertEquals(1, q.size());
            assertNull(q.peek());
            assertNull(q.dequeue());
            assertTrue(q.isEmpty());
        }
    }

    @Nested
    @DisplayName("빈 큐")
    class WhenEmpty {

        @Test
        void dequeueAndPeekThrow() {
            Queue<Integer> q = createQueue();
            assertThrows(NoSuchElementException.class, q::dequeue);
            assertThrows(NoSuchElementException.class, q::peek);
        }

        @Test
        @DisplayName("전부 뺀 뒤에도 예외다")
        void throwsAfterDrain() {
            Queue<Integer> q = createQueue();
            q.enqueue(1);
            q.dequeue();
            assertThrows(NoSuchElementException.class, q::dequeue);
            assertEquals(0, q.size(), "실패한 dequeue 가 크기를 음수로 만들면 안 된다");
        }
    }

    @Nested
    @DisplayName("초기화와 대량 처리")
    class ClearAndBulk {

        @Test
        void clearsAndReusable() {
            Queue<Integer> q = createQueue();
            for (int i = 0; i < 3; i++) q.enqueue(i);

            q.clear();
            assertEquals(0, q.size());
            assertThrows(NoSuchElementException.class, q::dequeue);

            q.enqueue(9);
            assertEquals(1, q.size());
            assertEquals(9, q.peek());
        }

        @Test
        @DisplayName("많이 넣고 빼도 순서가 유지된다")
        void survivesManyOperations() {
            Queue<Integer> q = createQueue();
            final int n = 1_000;
            for (int i = 0; i < n; i++) q.enqueue(i);
            for (int i = 0; i < n; i++) {
                assertEquals(i, q.dequeue(), "인덱스 " + i);
            }
            assertTrue(q.isEmpty());
        }
    }
}
