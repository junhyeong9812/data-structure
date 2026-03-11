package com.datastructure.queue.oop;

public interface Deque<E> extends Queue<E> {

    void addFirst(E element);

    void addLast(E element);

    E removeFirst();

    E removeLast();

    E peekFirst();

    E peekLast();
}
