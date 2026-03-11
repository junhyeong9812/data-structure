package com.datastructure.queue.oop;

public interface Queue<E> {

    void enqueue(E element);

    boolean offer(E element);

    E dequeue();

    E poll();

    E peek();

    E front();

    boolean isEmpty();

    int size();

    void clear();
}
