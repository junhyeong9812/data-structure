package com.datastructure.heap.oop;

public interface Heap<E> {
    void insert(E value);
    E extract();
    E peek();
    int size();
    boolean isEmpty();
    void clear();
}
