package com.datastructure.heap.oop;

public interface PriorityQueue<E> {
    boolean offer(E value);
    E poll();
    E peek();
    int size();
    boolean isEmpty();
}
