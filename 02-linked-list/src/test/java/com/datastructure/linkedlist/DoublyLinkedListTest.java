package com.datastructure.linkedlist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/** DoublyLinkedList 가 계약을 지키는지 + 양방향 고유 성질. */
class DoublyLinkedListTest extends ListIterationContractTest {

    @Override
    protected <E> List<E> create() {
        return new DoublyLinkedList<>();
    }

    /**
     * 구조 건전성 검사. 이 문제집에서 가장 중요한 단언이다.
     *
     * 앞으로 훑은 결과와 뒤로 훑은 결과가 서로의 역순이 아니면 링크가 깨진 것이다.
     * 이 검사가 없으면 prev 를 아예 안 잇는 구현도 계약 테스트를 대부분 통과한다.
     */
    private static <E> void assertSound(DoublyLinkedList<E> list) {
        int size = list.size();
        if (size == 0) {
            assertNull(list.head, "비었으면 head 는 null");
            assertNull(list.tail, "비었으면 tail 은 null");
            return;
        }
        assertNull(list.head.prev, "head.prev 는 null 이어야 한다");
        assertNull(list.tail.next, "tail.next 는 null 이어야 한다");

        java.util.List<E> forward = new ArrayList<>();
        for (DoublyLinkedList.Node<E> n = list.head; n != null; n = n.next) {
            forward.add(n.item);
            if (forward.size() > size + 1) break;
        }
        java.util.List<E> backward = new ArrayList<>();
        for (DoublyLinkedList.Node<E> n = list.tail; n != null; n = n.prev) {
            backward.add(n.item);
            if (backward.size() > size + 1) break;
        }

        assertEquals(size, forward.size(), "앞에서 센 개수가 size 와 다르다");
        assertEquals(size, backward.size(), "뒤에서 센 개수가 size 와 다르다 (prev 링크를 확인하라)");
        Collections.reverse(backward);
        assertEquals(forward, backward, "앞으로 훑은 것과 뒤로 훑은 것이 서로의 역순이 아니다");
    }

    @Test
    @DisplayName("모든 연산 후에 앞뒤 링크가 성하다")
    void staysSound() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
        list.add(2);
        assertSound(list);
        list.add(0, 1);
        assertSound(list);
        list.add(3);
        assertSound(list);
        list.remove(1);
        assertSound(list);
        list.removeFirst();
        assertSound(list);
        list.removeLast();
        assertSound(list);
    }

    @Test
    @DisplayName("뒤집은 뒤에도 양방향으로 성하다")
    void soundAfterReverse() {
        // prev 를 안 고치고 next 만 뒤집으면 앞에서는 맞고 뒤에서는 깨진다.
        DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
        for (int i = 1; i <= 5; i++) list.add(i);

        list.reverse();

        assertSound(list);
        assertEquals(5, list.getFirst());
        assertEquals(1, list.getLast());
    }

    @Test
    @DisplayName("순회 중 삭제 후에도 양방향으로 성하다")
    void soundAfterIteratorRemove() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
        for (int i = 0; i < 6; i++) list.add(i);

        java.util.Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            if (it.next() % 2 == 0) it.remove();
        }

        assertSound(list);
        assertArrayEquals(new Object[]{1, 3, 5}, list.toArray());
    }

    @Test
    @DisplayName("떼어낸 노드는 리스트를 붙잡지 않는다")
    void removedNodeIsDetached() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
        for (int i = 0; i < 3; i++) list.add(i);
        DoublyLinkedList.Node<Integer> middle = list.head.next;

        list.remove(1);

        assertNull(middle.prev, "떼어낸 노드의 prev 를 끊어야 한다");
        assertNull(middle.next, "떼어낸 노드의 next 를 끊어야 한다");
        assertSound(list);
    }

    @Test
    @DisplayName("clear 는 노드 사슬을 끊는다")
    void clearDetachesChain() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
        for (int i = 0; i < 3; i++) list.add(i);
        DoublyLinkedList.Node<Integer> head = list.head;

        list.clear();

        assertNull(head.next, "head/tail 만 null 로 만들면 노드끼리 계속 붙잡는다");
        assertSound(list);
    }

    @Test
    @DisplayName("removeLast 가 O(1) 이다 - tail 의 앞 노드를 바로 안다")
    void removeLastIsConstant() {
        // 단일 연결은 여기서 head 부터 다시 세야 한다. 이중은 tail.prev 로 바로 간다.
        DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
        for (int i = 0; i < 3; i++) list.add(i);

        assertEquals(2, list.removeLast());
        assertSame(list.tail, list.head.next, "새 tail 이 두 번째 노드여야 한다");
        assertSound(list);
    }
}
