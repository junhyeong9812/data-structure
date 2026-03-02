package com.datastructure.practice0222.queue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/***
 * 이중 연결 리스트 기반 덱 (Doubly Linked Deque)
 *
 * 양쪽 끝 삽입/삭제 모두 0(1), 용량 제한 없음.
 */
public class LinkedDeque<E> implements Deque<E> {

    private static class Node<E> {
        E value;
        Node<E> prev;
        Node<E> next;

        Node(E value) {
            this.value = value;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;
    
    // Deque 연산
    @Override
    public void addFirst(E element) {
        Node<E> node = new Node<>(element);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    @Override
    public void addLast(E element) {
        Node<E> node = new Node<>(element);
        if (tail == null) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    @Override
    public E removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("덱이 비어있습니다.");
        E value = head.value;
        head = head.next;
        if (head == null) {
            tail = null;
        } else {
            head.prev = null;
        }
        size--;
        return value;
    }

    @Override
    public E removeLast() {
        if (isEmpty()) throw new NoSuchElementException("덱이 비어있습니다.");
        E value = tail.value;
        tail = tail.prev;
        if (tail == null) {
            head = null;
        } else {
            tail.next = null;
        }
        size--;
        return value;
    }

    @Override
    public E peekFirst() {
        if (isEmpty()) throw new NoSuchElementException("덱이 비어있습니다.");
        return head.value;
    }

    @Override
    public E peekLast() {
        if (isEmpty()) throw new NoSuchElementException("덱이 비어있습니다.");
        return tail.value;
    }

    // Queue 인터페이스 매핑 (FIFO)

    @Override
    public void enqueue(E element) {
        addLast(element);
    }

    @Override
    public E dequeue() {
        return removeFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    // 공통

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(E element) {
        for ( Node<E> n = head; n != null; n = n.next) {
            if (Objects.equals(n.value, element)) return true;
        }
        return false;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> cursor = head;
            @Override
            public boolean hasNext() {
                return cursor != null;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                E value = cursor.value;;
                cursor = cursor.next;
                return value;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> n = head;
        while (n != null) {
            sb.append(n.value);
            if (n.next != null) sb.append(", ");
            n = n.next;
        }
        return sb.append("]").toString();
    }
}
