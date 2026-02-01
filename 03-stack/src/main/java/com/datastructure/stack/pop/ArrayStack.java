package com.datastructure.stack.pop;

import java.util.Arrays;
import java.util.List;

public class ArrayStack<E> {
    private int capacity = 10;
    private E[] data;
    private int top;

    @SuppressWarnings("unChecked")
    public ArrayStack() {
        this.data = (E[])new Object[capacity];
        top = 0;
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
        return false;
    }

    public int size() {
        return 0;
    }

    public void clear() {

    }

    public int search(E element) {
        return 0;
    }

    public List<E> toArray() {
        return null;
    }
}
