package com.datastructure.queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** ArrayDeque 가 Deque(및 Queue) 계약을 지키는지 + 원형 배열 고유 성질. */
class ArrayDequeTest extends DequeContractTest {

    @Override
    protected <E> Deque<E> createDeque() {
        return new ArrayDeque<>();
    }

    @Test
    @DisplayName("앞에 넣으면 head 가 배열 뒤쪽으로 감긴다")
    void addFirstWrapsHeadBackwards() {
        ArrayDeque<Integer> d = new ArrayDeque<>(4);
        d.addLast(1);          // head=0
        d.addFirst(0);         // head 가 0 에서 한 칸 앞 -> 배열 끝(3)으로 감긴다

        assertEquals(3, d.head, "head 가 음수가 되면 안 된다. 배열 끝으로 감아야 한다");
        assertEquals(0, d.peekFirst());
        assertEquals(1, d.peekLast());
    }

    @Test
    @DisplayName("양쪽으로 넣고 빼도 공간을 재사용한다")
    void reusesSpaceBothEnds() {
        ArrayDeque<Integer> d = new ArrayDeque<>(4);
        for (int i = 0; i < 1_000; i++) {
            d.addFirst(i);
            d.removeLast();
        }
        assertEquals(0, d.size());
        assertEquals(4, d.capacity(), "되감으면 확장할 이유가 없다");
    }

    @Test
    @DisplayName("감긴 상태에서 확장해도 순서가 유지된다")
    void growsWhileWrapped() {
        ArrayDeque<Integer> d = new ArrayDeque<>(4);
        d.addLast(2);
        d.addLast(3);
        d.addFirst(1);     // head 가 감긴다
        d.addFirst(0);
        // [0,1,2,3] 이고 배열에는 감겨 있다
        d.addLast(4);      // 확장

        assertEquals(5, d.size());
        for (int expected = 0; expected <= 4; expected++) {
            assertEquals(expected, d.removeFirst(), "확장 시 감긴 것을 풀지 않았다");
        }
    }

    @Test
    @DisplayName("뺀 자리의 참조가 남지 않는다")
    void clearsRemovedSlots() {
        ArrayDeque<String> d = new ArrayDeque<>(4);
        d.addLast("a");
        d.addLast("b");
        d.removeLast();
        d.removeFirst();

        for (int i = 0; i < d.capacity(); i++) {
            assertNull(d.elements[i], "인덱스 " + i + " 에 참조가 남아 있다");
        }
    }
}
