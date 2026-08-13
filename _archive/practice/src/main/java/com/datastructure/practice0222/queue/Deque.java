package com.datastructure.practice0222.queue;

/**
 * 덱 (Double-Ended Queue)
 * 양쪽 끝에서 삽입/샂게가 가능한 큐
 */
public interface Deque<E> extends Queue<E> {

    void addFirst(E element);

    void addLast(E element);

    E removeFirst();

    E removeLast();

    E peekFirst();

    E peekLast();

    //Queue 인터페이스의 기본 동작은 FIFO로 매핑
    // enqueue -> addLast, dequeue -> removeFirst, peek->peekFirst
}
