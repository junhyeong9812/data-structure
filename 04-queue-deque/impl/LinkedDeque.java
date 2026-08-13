package com.datastructure.queue;

import java.util.NoSuchElementException;

/** [구현] 양방향 노드 데크. 02번 연결 리스트에서 양 끝만 쓰는 형태다. */
public class LinkedDeque<E> implements Deque<E> {

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

    Node<E> first;
    Node<E> last;
    private int size;

    @Override public int size() { return size; }
    @Override public boolean isEmpty() { return size == 0; }

    @Override public void enqueue(E element) { addLast(element); }
    @Override public E dequeue() { return removeFirst(); }
    @Override public E peek() { return peekFirst(); }

    @Override
    public E peekFirst() {
        if (first == null) throw new NoSuchElementException("비어 있다");
        return first.item;
    }

    @Override
    public E peekLast() {
        if (last == null) throw new NoSuchElementException("비어 있다");
        return last.item;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<E> n = first; n != null; n = n.next) {
            if (n != first) sb.append(", ");
            sb.append(n.item);
        }
        return sb.append(']').toString();
    }

    void bumpSize(int delta) { size += delta; }

    // ------------------------------------------------------------------

    @Override
    public void addFirst(E element) {
        Node<E> oldFirst = first;
        Node<E> node = new Node<>(null, element, oldFirst);
        first = node;
        if (oldFirst == null) {
            last = node;              // 비어 있었다면 이 노드가 양 끝이다
        } else {
            oldFirst.prev = node;
        }
        bumpSize(1);
    }

    @Override
    public void addLast(E element) {
        Node<E> oldLast = last;
        Node<E> node = new Node<>(oldLast, element, null);
        last = node;
        if (oldLast == null) {
            first = node;
        } else {
            oldLast.next = node;
        }
        bumpSize(1);
    }

    /**
     * 떼어낸 노드의 링크를 끊는다.
     * 안 끊으면 그 노드 하나가 데크 전체를 GC 대상에서 제외시킨다.
     */
    @Override
    public E removeFirst() {
        if (first == null) throw new NoSuchElementException("비어 있다");
        Node<E> removed = first;
        E value = removed.item;
        first = removed.next;
        if (first == null) {
            last = null;              // 마지막 하나를 뺐다
        } else {
            first.prev = null;
        }
        removed.item = null;
        removed.next = null;
        bumpSize(-1);
        return value;
    }

    @Override
    public E removeLast() {
        if (last == null) throw new NoSuchElementException("비어 있다");
        Node<E> removed = last;
        E value = removed.item;
        last = removed.prev;
        if (last == null) {
            first = null;
        } else {
            last.next = null;
        }
        removed.item = null;
        removed.prev = null;
        bumpSize(-1);
        return value;
    }

    @Override
    public void clear() {
        Node<E> n = first;
        while (n != null) {
            Node<E> next = n.next;
            n.item = null;
            n.prev = null;
            n.next = null;
            n = next;
        }
        first = null;
        last = null;
        size = 0;
    }
}
