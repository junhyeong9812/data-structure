package com.datastructure.practice0222.queue;

import java.util.NoSuchElementException;

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
}
