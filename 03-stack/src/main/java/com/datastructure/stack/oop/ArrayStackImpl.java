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
        growStack();
        stackData[top] = element;
        top++;
    }

    public E pop() {
        top--;
        E popData = stackData[top];
        shrinkStack();
        return popData;
    }

    public E peek() {
        return stackData[top - 1];
    }

    public E top() {
        return stackData[top - 1];
    }

    public boolean isEmpty() {
        return top == 0;
    }

    public int size(){
        return top;
    }

    public void clear() {
        capacity = 10;
        top = 0;
        stackData = (E[])(new Object[capacity]);
    }

    public int search(E element) {

    }

    public E[] toArray() {
        return Arrays.copyOf(stackData,top);
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
