package com.datastructure.linkedlist;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * [구현] 단일 연결 리스트.
 *
 * tail 이 있어 addLast 는 O(1) 인데 removeLast 는 O(n) 이다.
 * 지울 노드의 **앞** 노드를 알 방법이 없어 head 부터 다시 세야 하기 때문이다.
 */
public class SinglyLinkedList<E> implements List<E> {

    static class Node<E> {
        E item;
        Node<E> next;

        Node(E item, Node<E> next) {
            this.item = item;
            this.next = next;
        }
    }

    Node<E> head;
    Node<E> tail;
    private int size;

    @Override public int size() { return size; }
    @Override public boolean isEmpty() { return size == 0; }
    @Override public boolean contains(Object o) { return indexOf(o) >= 0; }

    public E getFirst() {
        if (head == null) throw new NoSuchElementException("비어 있다");
        return head.item;
    }

    public E getLast() {
        if (tail == null) throw new NoSuchElementException("비어 있다");
        return tail.item;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("인덱스 " + index + ", 크기 " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("인덱스 " + index + ", 크기 " + size);
        }
    }

    void bumpSize(int delta) { size += delta; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<E> n = head; n != null; n = n.next) {
            if (n != head) sb.append(", ");
            sb.append(n.item);
        }
        return sb.append(']').toString();
    }

    // ------------------------------------------------------------------

    public void addFirst(E element) {
        head = new Node<>(element, head);
        if (tail == null) tail = head;
        bumpSize(1);
    }

    /** tail 이 있으므로 앞에서부터 갈 필요가 없다. 그래서 O(1) 이다. */
    public void addLast(E element) {
        Node<E> node = new Node<>(element, null);
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        bumpSize(1);
    }

    @Override public void add(E element) { addLast(element); }

    /** 단방향이라 언제나 head 부터다. 이중처럼 뒤에서 거슬러 갈 수 없다. */
    private Node<E> node(int index) {
        Node<E> n = head;
        for (int i = 0; i < index; i++) n = n.next;
        return n;
    }

    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);
        if (index == 0) {
            addFirst(element);
        } else if (index == size) {
            addLast(element);
        } else {
            Node<E> pred = node(index - 1);        // 끼우려면 앞 노드가 필요하다
            pred.next = new Node<>(element, pred.next);
            bumpSize(1);
        }
    }

    @Override
    public E get(int index) {
        checkIndex(index);
        return node(index).item;
    }

    @Override
    public E set(int index, E element) {
        checkIndex(index);
        Node<E> n = node(index);
        E old = n.item;
        n.item = element;
        return old;
    }

    @Override
    public E remove(int index) {
        checkIndex(index);
        if (index == 0) return removeFirst();
        if (index == size - 1) return removeLast();

        Node<E> pred = node(index - 1);
        Node<E> target = pred.next;
        E value = target.item;
        pred.next = target.next;
        target.next = null;
        target.item = null;
        bumpSize(-1);
        return value;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index < 0) return false;
        remove(index);
        return true;
    }

    public E removeFirst() {
        if (head == null) throw new NoSuchElementException("비어 있다");
        Node<E> removed = head;
        E value = removed.item;
        head = removed.next;
        if (head == null) tail = null;
        removed.next = null;
        removed.item = null;
        bumpSize(-1);
        return value;
    }

    /**
     * tail 을 알아도 그 앞 노드를 모른다. 그래서 head 부터 다시 세야 한다.
     * 이 한 가지가 단일 연결 리스트의 대표적 약점이다.
     */
    public E removeLast() {
        if (tail == null) throw new NoSuchElementException("비어 있다");
        if (head == tail) {
            E value = head.item;
            head.item = null;
            head = null;
            tail = null;
            bumpSize(-1);
            return value;
        }
        Node<E> pred = head;
        while (pred.next != tail) {       // <- 여기가 O(n)
            pred = pred.next;
        }
        E value = tail.item;
        tail.item = null;
        pred.next = null;
        tail = pred;
        bumpSize(-1);
        return value;
    }

    @Override
    public int indexOf(Object o) {
        int i = 0;
        for (Node<E> n = head; n != null; n = n.next, i++) {
            if (o == null ? n.item == null : o.equals(n.item)) return i;
        }
        return -1;
    }

    @Override
    public void clear() {
        Node<E> n = head;
        while (n != null) {
            Node<E> next = n.next;
            n.item = null;
            n.next = null;
            n = next;
        }
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> n = head; n != null; n = n.next) result[i++] = n.item;
        return result;
    }

    /**
     * 포인터 세 개를 굴린다.
     *
     * cur.next 를 prev 로 돌리는 순간 원래 다음 노드로 갈 길을 잃으므로 미리 잡아둔다.
     * 다 돌면 prev 가 새 head 이고, 원래 head 가 새 tail 이다.
     */
    @Override
    public void reverse() {
        Node<E> oldHead = head;
        Node<E> prev = null;
        Node<E> cur = head;
        while (cur != null) {
            Node<E> next = cur.next;      // 먼저 잡는다
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        head = prev;
        tail = oldHead;
    }

    /**
     * 단방향이라 "방금 돌려준 노드의 앞"을 따로 들고 있어야 지울 수 있다.
     * 이중 연결에서는 lastReturned.prev 로 바로 알 수 있어 이 변수가 필요 없다.
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> next = head;
            private Node<E> prev = null;              // next 의 앞 노드
            private Node<E> lastReturned = null;
            private Node<E> beforeLastReturned = null;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public E next() {
                if (next == null) throw new NoSuchElementException();
                beforeLastReturned = prev;
                lastReturned = next;
                prev = next;
                next = next.next;
                return lastReturned.item;
            }

            @Override
            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException("next() 를 먼저 부르거나, 이미 지웠다");
                }
                if (beforeLastReturned == null) {
                    head = lastReturned.next;
                } else {
                    beforeLastReturned.next = lastReturned.next;
                }
                if (lastReturned == tail) {
                    tail = beforeLastReturned;
                }
                lastReturned.next = null;
                lastReturned.item = null;
                bumpSize(-1);
                prev = beforeLastReturned;            // 한 칸 지웠으니 앞이 당겨진다
                lastReturned = null;
            }
        };
    }
}
