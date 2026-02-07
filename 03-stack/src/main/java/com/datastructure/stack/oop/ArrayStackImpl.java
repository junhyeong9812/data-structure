package com.datastructure.stack.oop;

import java.util.Arrays;

public class ArrayStackImpl<E> implements Stack<E> {

    private int capacity;
    private E[] stackData;
    private int top;

    @SuppressWarnings("unchecked")
    public ArrayStackImpl() {
        this.capacity = 10;
        this.stackData = (E[]) new Object[capacity];
        this.top = 0;
    }

    public void push(E element) {

    }

    public E pop() {
        return null;
    }

    public E peek() {
        return null;
    }

    public E top() {
        return null;
    }

    public boolean isEmpty() {

        return true;
    }

    public int size(){
        return 0;
    }

    public void clear() {

    }

    private void growStack() {
        if (capacity == top) {
            capacity = (int)(capacity * 1.5);
            stackData = Arrays.copyOf(stackData, capacity);
        }
    }
    private void shrinkStack() {
        if (top <= capacity/4 && capacity > 10) {
            capacity = Math.max(capacity/2, 10);
            stackData = Arrays.copyOf(stackData, capacity);
        }
    }
}
