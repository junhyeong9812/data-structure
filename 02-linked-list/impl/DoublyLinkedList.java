package com.datastructure.linkedlist;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * [구현] 양방향 연결 리스트.
 *
 * 스켈레톤의 TODO 를 채운 버전이다. 막혔을 때만 보고, 보고 나면 다시 스켈레톤으로 돌아가 직접 쳐라.
 */
public class DoublyLinkedList<E> implements List<E> {

    static class Node<E> {
        E item;
        Node<E> prev;
        Node<E> next;

        Node(Node<E> prev, E item, Node<E> next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    Node<E> head;
    Node<E> tail;
    private int size;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public void addFirst(E element) {
        linkFirst(element);
    }

    public void addLast(E element) {
        linkLast(element);
    }

    @Override
    public void add(E element) {
        linkLast(element);
    }

    public E getFirst() {
        if (head == null) throw new NoSuchElementException("비어 있다");
        return head.item;
    }

    public E getLast() {
        if (tail == null) throw new NoSuchElementException("비어 있다");
        return tail.item;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
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

    /**
     * 새 노드가 기존 head 를 가리키고, 기존 head 가 새 노드를 되가리킨다.
     * 비어 있었다면 되가리킬 상대가 없으므로 tail 도 이 노드가 된다.
     */
    private void linkFirst(E element) {
        Node<E> oldHead = head;
        Node<E> newNode = new Node<>(null, element, oldHead);
        head = newNode;
        if (oldHead == null) {
            tail = newNode;
        } else {
            oldHead.prev = newNode;
        }
        size++;
    }

    /** linkFirst 의 완전한 대칭. prev/next, head/tail 을 맞바꾸면 그대로다. */
    private void linkLast(E element) {
        Node<E> oldTail = tail;
        Node<E> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size++;
    }

    /**
     * succ 앞에 끼운다.
     *
     * 순서가 중요하다. succ.prev 를 먼저 덮어쓰면 원래 앞 노드를 잃는다.
     * 그래서 pred 를 먼저 붙잡아 둔다.
     */
    private void linkBefore(E element, Node<E> succ) {
        Node<E> pred = succ.prev;
        Node<E> newNode = new Node<>(pred, element, succ);
        succ.prev = newNode;
        if (pred == null) {
            head = newNode;          // succ 가 head 였다
        } else {
            pred.next = newNode;
        }
        size++;
    }

    /**
     * 이웃끼리 직접 잇고 이 노드를 사슬에서 뺀다.
     *
     * 마지막의 prev/next/item 정리가 중요하다.
     * 떼어낸 노드가 이웃을 계속 가리키면, 그 노드 하나만 어딘가에 남아도
     * 리스트 전체가 GC 되지 않는다. 실제로 자주 나는 누수다.
     */
    E unlink(Node<E> node) {
        E item = node.item;
        Node<E> pred = node.prev;
        Node<E> succ = node.next;

        if (pred == null) {
            head = succ;             // 맨 앞이었다
        } else {
            pred.next = succ;
            node.prev = null;
        }

        if (succ == null) {
            tail = pred;             // 맨 뒤였다
        } else {
            succ.prev = pred;
            node.next = null;
        }

        node.item = null;
        size--;
        return item;
    }

    /**
     * 배열처럼 계산으로 갈 수 없으니 세면서 간다.
     *
     * 양방향이라는 성질을 쓰면 뒤쪽 인덱스는 tail 에서 거슬러 올라가는 게 빠르다.
     * 최악은 여전히 O(n) 이지만 평균 이동 거리가 절반이 된다.
     */
    private Node<E> node(int index) {
        if (index < (size >> 1)) {
            Node<E> n = head;
            for (int i = 0; i < index; i++) n = n.next;
            return n;
        }
        Node<E> n = tail;
        for (int i = size - 1; i > index; i--) n = n.prev;
        return n;
    }

    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);
        if (index == size) {
            linkLast(element);
        } else {
            linkBefore(element, node(index));
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
        return unlink(node(index));
    }

    /** 인덱스를 거치지 않는다. 훑다가 찾은 노드를 바로 뗀다. */
    @Override
    public boolean remove(Object o) {
        for (Node<E> n = head; n != null; n = n.next) {
            if (o == null ? n.item == null : o.equals(n.item)) {
                unlink(n);
                return true;
            }
        }
        return false;
    }

    /** 배열에서는 뒤를 전부 당겨야 해서 O(n) 이었다. 여기서는 링크 하나만 고치면 되므로 O(1) 이다. */
    public E removeFirst() {
        if (head == null) throw new NoSuchElementException("비어 있다");
        return unlink(head);
    }

    public E removeLast() {
        if (tail == null) throw new NoSuchElementException("비어 있다");
        return unlink(tail);
    }

    @Override
    public int indexOf(Object o) {
        int i = 0;
        for (Node<E> n = head; n != null; n = n.next, i++) {
            if (o == null ? n.item == null : o.equals(n.item)) return i;
        }
        return -1;
    }

    /**
     * head/tail 만 null 로 만들면 리스트는 비어 보이지만 노드들이 서로를 계속 가리킨다.
     * 그중 하나라도 외부에서 참조되고 있으면 전부 살아남는다. 그래서 사슬을 끊는다.
     */
    @Override
    public void clear() {
        Node<E> n = head;
        while (n != null) {
            Node<E> next = n.next;
            n.item = null;
            n.prev = null;
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
        for (Node<E> n = head; n != null; n = n.next) {
            result[i++] = n.item;
        }
        return result;
    }

    /**
     * 각 노드의 prev 와 next 를 맞바꾸면 방향이 뒤집힌다. 값은 하나도 안 옮긴다.
     * 링크를 바꾸는 순간 다음으로 갈 길을 잃으므로 미리 잡아둔다.
     */
    @Override
    public void reverse() {
        Node<E> node = head;
        while (node != null) {
            Node<E> next = node.next;     // 먼저 잡는다
            node.next = node.prev;
            node.prev = next;
            node = next;
        }
        Node<E> oldHead = head;
        head = tail;
        tail = oldHead;
    }

    /**
     * 양방향이라 lastReturned.prev 로 앞 노드를 바로 알 수 있다.
     * 단일 연결에서는 그 앞 노드를 따로 들고 있어야 한다. 그 차이가 여기 드러난다.
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> next = head;
            private Node<E> lastReturned = null;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public E next() {
                if (next == null) throw new NoSuchElementException();
                lastReturned = next;
                next = next.next;
                return lastReturned.item;
            }

            @Override
            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException("next() 를 먼저 부르거나, 이미 지웠다");
                }
                unlink(lastReturned);      // head/tail 갱신과 링크 끊기를 그대로 재사용한다
                lastReturned = null;
            }
        };
    }
}
