package com.datastructure.queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CircularQueue 가 Queue 계약을 지키는지 + ArrayQueue 의 한계를 실제로 고쳤는지.
 *
 * ArrayQueueTest.wastesSpace 와 여기 reusesSpace 를 나란히 놓고 보라.
 * 같은 시나리오인데 결과가 다르다. 그게 되감기의 값어치다.
 */
class CircularQueueTest extends QueueContractTest {

    @Override
    protected <E> Queue<E> createQueue() {
        return new CircularQueue<>();
    }

    @Test
    @DisplayName("되감으므로 공간을 재사용한다")
    void reusesSpace() {
        CircularQueue<Integer> q = new CircularQueue<>(4);

        // ArrayQueueTest.wastesSpace 와 완전히 같은 시나리오다.
        for (int i = 0; i < 1_000; i++) {
            q.enqueue(i);
            q.dequeue();
        }

        assertEquals(0, q.size());
        assertEquals(4, q.capacity(),
            "되감으면 앞자리를 다시 쓸 수 있으므로 용량이 커질 이유가 없다");
    }

    @Test
    @DisplayName("배열 끝을 넘어가면 앞으로 감긴다")
    void wrapsAround() {
        CircularQueue<Integer> q = new CircularQueue<>(4);
        for (int i = 0; i < 4; i++) q.enqueue(i);   // [0,1,2,3], head=0
        q.dequeue();
        q.dequeue();                                 // head=2, size=2
        q.enqueue(4);
        q.enqueue(5);                                // 4,5 는 배열 앞쪽 0,1 자리에 들어간다

        assertEquals(4, q.capacity(), "감아 썼으므로 확장이 일어나면 안 된다");
        assertEquals(2, q.dequeue());
        assertEquals(3, q.dequeue());
        assertEquals(4, q.dequeue());
        assertEquals(5, q.dequeue());
    }

    @Test
    @DisplayName("감긴 상태에서 확장해도 순서가 유지된다")
    void growsWhileWrapped() {
        CircularQueue<Integer> q = new CircularQueue<>(4);
        for (int i = 0; i < 4; i++) q.enqueue(i);
        q.dequeue();
        q.dequeue();
        q.enqueue(4);
        q.enqueue(5);      // 여기까지 [2,3,4,5] 이고 배열에는 감겨 있다
        q.enqueue(6);      // 확장이 일어난다

        // 통째로 복사하면 순서가 뒤죽박죽이 된다. 풀어서 옮겨야 한다.
        assertEquals(5, q.size());
        for (int expected = 2; expected <= 6; expected++) {
            assertEquals(expected, q.dequeue(), "확장 시 감긴 것을 풀지 않았다");
        }
    }

    @Test
    @DisplayName("감긴 상태에서 clear 해도 참조가 남지 않는다")
    void clearsWrappedSlots() {
        CircularQueue<String> q = new CircularQueue<>(4);
        for (int i = 0; i < 4; i++) q.enqueue("v" + i);
        q.dequeue();
        q.dequeue();
        q.enqueue("x");
        q.enqueue("y");    // 감긴 상태

        q.clear();

        for (int i = 0; i < q.capacity(); i++) {
            assertNull(q.elements[i], "인덱스 " + i + " 에 참조가 남아 있다");
        }
    }
}
