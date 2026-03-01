package com.datastructure.practice0222.queue;

import java.util.NoSuchElementException;

/**
 * 연결 리스트 기반 큐 (inked Queue)
 *
 * head에서 dequeue, tail에서 enqueue.
 * 모든 연산 0(1), 용량 제한 없음.
 */
public class LinkedQueue<E> implements Queue<E> {

    private static class Node<E> {
        E value;
        Node<E> next;

        Node(E value) {this.value = value; }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    @Override
    public void enqueue(E element) {
        Node<E> node = new Node<>(element);
        if (tail != null) {
            tail.next = node;
        } else {
            head = node;
        }
        tail = node;
        size++;
    }

    @Override
    public E dequeue() {
        if (isEmpty()) throw new NoSuchElementException("큐가 비어있습니다.");
        E value = head.value;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return value;
    }


}
