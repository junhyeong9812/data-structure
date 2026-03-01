package com.datastructure.practice0222.queue;

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
}
