package com.datastructure.queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** LinkedDeque 가 Deque(및 Queue) 계약을 지키는지 + 노드 구현 고유 성질. */
class LinkedDequeTest extends DequeContractTest {

    @Override
    protected <E> Deque<E> createDeque() {
        return new LinkedDeque<>();
    }

    /** 앞뒤 훑기가 서로의 역순인지. 02번에서 배운 검사다. */
    private static <E> void assertSound(LinkedDeque<E> d) {
        if (d.size() == 0) {
            assertNull(d.first, "비었으면 first 는 null");
            assertNull(d.last, "비었으면 last 는 null");
            return;
        }
        assertNull(d.first.prev, "first.prev 는 null 이어야 한다");
        assertNull(d.last.next, "last.next 는 null 이어야 한다");

        int forward = 0;
        for (LinkedDeque.Node<E> n = d.first; n != null; n = n.next) {
            if (++forward > d.size() + 1) break;
        }
        int backward = 0;
        for (LinkedDeque.Node<E> n = d.last; n != null; n = n.prev) {
            if (++backward > d.size() + 1) break;
        }
        assertEquals(d.size(), forward, "앞에서 센 개수가 size 와 다르다");
        assertEquals(d.size(), backward, "뒤에서 센 개수가 size 와 다르다 (prev 링크를 확인하라)");
    }

    @Test
    @DisplayName("양쪽으로 넣어도 링크가 성하다")
    void staysSoundOnBothEnds() {
        LinkedDeque<Integer> d = new LinkedDeque<>();
        d.addLast(2);
        assertSound(d);
        d.addFirst(1);
        assertSound(d);
        d.addLast(3);
        assertSound(d);
        d.removeFirst();
        assertSound(d);
        d.removeLast();
        assertSound(d);
    }

    @Test
    @DisplayName("첫 원소는 first 이자 last 다")
    void firstElementIsBothEnds() {
        LinkedDeque<String> d = new LinkedDeque<>();
        d.addFirst("only");
        assertSame(d.first, d.last, "원소가 하나면 같은 노드다");
        assertSound(d);
    }

    @Test
    @DisplayName("떼어낸 노드는 데크를 붙잡지 않는다")
    void removedNodeIsDetached() {
        LinkedDeque<Integer> d = new LinkedDeque<>();
        d.addLast(1);
        d.addLast(2);
        LinkedDeque.Node<Integer> firstNode = d.first;

        d.removeFirst();

        assertNull(firstNode.next, "떼어낸 노드의 next 를 끊어야 한다");
        assertSound(d);
    }

    @Test
    @DisplayName("clear 는 노드 사슬을 끊는다")
    void clearDetachesChain() {
        LinkedDeque<Integer> d = new LinkedDeque<>();
        d.addLast(1);
        d.addLast(2);
        LinkedDeque.Node<Integer> head = d.first;

        d.clear();

        assertNull(d.first);
        assertNull(head.next, "first/last 만 null 로 만들면 노드끼리 계속 붙잡는다");
        assertSound(d);
    }

    @Test
    @DisplayName("용량 개념이 없어 확장 비용이 없다")
    void noCapacityConcept() {
        LinkedDeque<Integer> d = new LinkedDeque<>();
        for (int i = 0; i < 100; i++) d.addLast(i);

        int nodes = 0;
        for (LinkedDeque.Node<Integer> n = d.first; n != null; n = n.next) nodes++;
        assertEquals(100, nodes, "노드 수가 곧 원소 수다");
        assertSound(d);
    }
}
