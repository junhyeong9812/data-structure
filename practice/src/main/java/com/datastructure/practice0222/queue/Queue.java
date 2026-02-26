package com.datastructure.practice0222.queue;

public interface Queue<E> extends Iterable<E> {

    void enqueue(E element);

    E dequeue();

    E peek();

    int size();

    boolean isEmpty();

    boolean contains(E element);

    void clear();
}
