package com.datastructure.queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Deque 계약 테스트.
 *
 * QueueContractTest 를 물려받으므로 큐 계약도 자동으로 검사된다.
 * 인터페이스가 Deque extends Queue 이니 테스트도 같은 모양이다.
 */
abstract class DequeContractTest extends QueueContractTest {

    protected abstract <E> Deque<E> createDeque();

    @Override
    protected <E> Queue<E> createQueue() {
        return createDeque();     // 데크는 큐로도 쓸 수 있다. 그게 계약이다
    }

    @Nested
    @DisplayName("양쪽 끝")
    class BothEnds {

        @Test
        @DisplayName("앞에 넣으면 순서가 뒤집힌다")
        void addFirstReverses() {
            Deque<Integer> d = createDeque();
            d.addFirst(1);
            d.addFirst(2);
            d.addFirst(3);

            assertEquals(3, d.removeFirst());
            assertEquals(2, d.removeFirst());
            assertEquals(1, d.removeFirst());
        }

        @Test
        @DisplayName("뒤에서 빼면 마지막 것이 나온다")
        void removeLastTakesTail() {
            Deque<Integer> d = createDeque();
            d.addLast(1);
            d.addLast(2);
            d.addLast(3);

            assertEquals(3, d.removeLast());
            assertEquals(1, d.removeFirst());
            assertEquals(2, d.removeLast());
            assertTrue(d.isEmpty());
        }

        @Test
        @DisplayName("양쪽을 섞어 써도 순서가 맞는다")
        void mixedEnds() {
            Deque<Integer> d = createDeque();
            d.addLast(2);
            d.addFirst(1);
            d.addLast(3);
            d.addFirst(0);
            // [0, 1, 2, 3]

            assertEquals(0, d.peekFirst());
            assertEquals(3, d.peekLast());
            assertEquals(0, d.removeFirst());
            assertEquals(3, d.removeLast());
            assertEquals(1, d.removeFirst());
            assertEquals(2, d.removeLast());
            assertTrue(d.isEmpty());
        }

        @Test
        @DisplayName("원소가 하나일 때 양 끝이 같다")
        void singleElementIsBothEnds() {
            Deque<String> d = createDeque();
            d.addFirst("only");

            assertEquals("only", d.peekFirst());
            assertEquals("only", d.peekLast());
            assertEquals("only", d.removeLast());
            assertTrue(d.isEmpty());
        }

        @Test
        @DisplayName("peek 는 양쪽 다 빼지 않는다")
        void peeksDoNotRemove() {
            Deque<Integer> d = createDeque();
            d.addLast(1);
            d.addLast(2);

            assertEquals(1, d.peekFirst());
            assertEquals(2, d.peekLast());
            assertEquals(2, d.size());
        }

        @Test
        @DisplayName("비었을 때 양쪽 끝 연산은 예외다")
        void emptyEndsThrow() {
            Deque<Integer> d = createDeque();
            assertThrows(NoSuchElementException.class, d::removeFirst);
            assertThrows(NoSuchElementException.class, d::removeLast);
            assertThrows(NoSuchElementException.class, d::peekFirst);
            assertThrows(NoSuchElementException.class, d::peekLast);
        }
    }

    @Nested
    @DisplayName("Queue 로도 쓸 수 있다")
    class UsableAsQueue {

        @Test
        @DisplayName("enqueue 는 addLast, dequeue 는 removeFirst 와 같다")
        void queueMethodsMapToEnds() {
            Deque<Integer> d = createDeque();
            d.enqueue(1);
            d.addLast(2);

            assertEquals(1, d.peekFirst());
            assertEquals(1, d.dequeue());
            assertEquals(2, d.removeFirst());
        }
    }
}
