package com.datastructure.stack.oop;

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

    private void growStack() {}
    private void shrink() {}
}
