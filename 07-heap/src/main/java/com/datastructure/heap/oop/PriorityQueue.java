package com.datastructure.heap.oop;

public interface PriorityQueue<E> {
    boolean offer(E value);
    E poll();
    E peak();
    int size();
    boolean isEmpty();
}
