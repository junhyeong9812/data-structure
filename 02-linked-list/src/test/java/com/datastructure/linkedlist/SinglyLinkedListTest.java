package com.datastructure.linkedlist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** SinglyLinkedList 가 계약을 지키는지 + 단일 연결 고유 성질. */
class SinglyLinkedListTest extends ListIterationContractTest {

    @Override
    protected <E> List<E> create() {
        return new SinglyLinkedList<>();
    }

    /** 앞에서 센 개수가 size 와 같고 tail 이 마지막 노드인지. */
    private static <E> void assertSound(SinglyLinkedList<E> list) {
        if (list.size() == 0) {
            assertNull(list.head, "비었으면 head 는 null");
            assertNull(list.tail, "비었으면 tail 은 null");
            return;
        }
        assertNull(list.tail.next, "tail.next 는 null 이어야 한다");

        int count = 0;
        SinglyLinkedList.Node<E> last = null;
        for (SinglyLinkedList.Node<E> n = list.head; n != null; n = n.next) {
            last = n;
            if (++count > list.size() + 1) break;
        }
        assertEquals(list.size(), count, "앞에서 센 개수가 size 와 다르다");
        assertSame(list.tail, last, "tail 이 마지막 노드를 가리키지 않는다");
    }

    @Test
    @DisplayName("양 끝 연산 후에도 구조가 성하다")
    void staysSound() {
        SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
        list.addLast(2);
        assertSound(list);
        list.addFirst(1);
        assertSound(list);
        list.addLast(3);
        assertSound(list);
        list.removeFirst();
        assertSound(list);
        list.removeLast();
        assertSound(list);
    }

    @Test
    @DisplayName("removeLast 후 tail 이 새 마지막 노드를 가리킨다")
    void removeLastUpdatesTail() {
        // tail 만 알아서는 그 앞 노드를 알 수 없다. head 부터 다시 세야 한다. 그래서 O(n) 이다.
        SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
        for (int i = 0; i < 3; i++) list.addLast(i);

        assertEquals(2, list.removeLast());
        assertEquals(1, list.getLast());
        assertSound(list);

        list.addLast(9);                    // tail 이 틀렸으면 여기서 드러난다
        assertArrayEquals(new Object[]{0, 1, 9}, list.toArray());
    }

    @Test
    @DisplayName("원소가 하나일 때 removeLast")
    void removeLastOnSingleElement() {
        SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
        list.addLast(1);
        assertEquals(1, list.removeLast());
        assertTrue(list.isEmpty());
        assertSound(list);
    }

    @Test
    @DisplayName("떼어낸 노드는 리스트를 붙잡지 않는다")
    void removedNodeIsDetached() {
        SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
        for (int i = 0; i < 3; i++) list.addLast(i);
        SinglyLinkedList.Node<Integer> first = list.head;

        list.removeFirst();

        assertNull(first.next, "떼어낸 노드의 next 를 끊어야 한다");
        assertSound(list);
    }

    @Test
    @DisplayName("clear 는 노드 사슬을 끊는다")
    void clearDetachesChain() {
        SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
        for (int i = 0; i < 3; i++) list.addLast(i);
        SinglyLinkedList.Node<Integer> head = list.head;

        list.clear();

        assertNull(list.head);
        assertNull(head.next, "head/tail 만 null 로 만들면 노드끼리 계속 붙잡는다");
        assertSound(list);
    }

    @Test
    @DisplayName("뒤집으면 tail 도 바뀐다")
    void reverseUpdatesTail() {
        SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
        for (int i = 1; i <= 3; i++) list.addLast(i);

        list.reverse();

        assertEquals(3, list.getFirst());
        assertEquals(1, list.getLast());
        assertSound(list);
    }

    @Test
    @DisplayName("비었을 때 양 끝 접근은 예외다")
    void emptyEndsThrow() {
        SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
        assertThrows(java.util.NoSuchElementException.class, list::getFirst);
        assertThrows(java.util.NoSuchElementException.class, list::getLast);
        assertThrows(java.util.NoSuchElementException.class, list::removeFirst);
        assertThrows(java.util.NoSuchElementException.class, list::removeLast);
    }
}
