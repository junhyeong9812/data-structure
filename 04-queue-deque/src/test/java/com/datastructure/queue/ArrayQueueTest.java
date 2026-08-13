package com.datastructure.queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ArrayQueue 가 Queue 계약을 지키는지 + **이 구현의 한계를 드러내는 테스트**.
 *
 * 아래 wastesSpace 는 버그를 잡는 테스트가 아니다. 올바른 구현에서도 통과한다.
 * "이 구현은 이런 성질을 가진다"를 눈으로 보여주려고 있는 테스트다.
 * 그 성질이 왜 문제인지 알아야 CircularQueue 를 만들 이유가 생긴다.
 */
class ArrayQueueTest extends QueueContractTest {

    @Override
    protected <E> Queue<E> createQueue() {
        return new ArrayQueue<>();
    }

    @Test
    @DisplayName("한계: 되감지 않으므로 공간을 버린다")
    void wastesSpace() {
        ArrayQueue<Integer> q = new ArrayQueue<>(4);

        // 담긴 개수는 늘 1개 이하인데, 넣고 빼기를 반복하면 용량이 계속 커진다.
        for (int i = 0; i < 1_000; i++) {
            q.enqueue(i);
            q.dequeue();
        }

        assertEquals(0, q.size(), "원소는 하나도 안 남았다");
        assertTrue(q.capacity() > 100,
            "그런데 용량은 " + q.capacity() + " 이다. head 왼쪽 자리를 다시 못 쓰기 때문이다.\n"
                + "원소 0개짜리 큐가 배열 1000칸을 붙잡고 있다. 이게 이 구현의 한계다.\n"
                + "CircularQueue 에서 이 문제를 고친다.");
    }

    @Test
    @DisplayName("꽉 차면 용량이 두 배로 늘어난다")
    void growsByDoubling() {
        ArrayQueue<Integer> q = new ArrayQueue<>(2);
        q.enqueue(1);
        q.enqueue(2);
        assertEquals(2, q.capacity());
        q.enqueue(3);
        assertEquals(4, q.capacity());
    }

    @Test
    @DisplayName("뺀 자리의 참조가 남지 않는다")
    void clearsDequeuedSlot() {
        ArrayQueue<String> q = new ArrayQueue<>(4);
        q.enqueue("a");
        q.enqueue("b");
        q.dequeue();

        assertNull(q.elements[0], "뺀 자리를 비우지 않으면 누수다");
    }
}
