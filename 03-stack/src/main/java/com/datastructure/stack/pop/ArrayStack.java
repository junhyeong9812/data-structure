package com.datastructure.stack.pop;

import java.util.Arrays;
import java.util.List;

public class ArrayStack<E> {
    private int capacity = 10;
    private E[] data;
    private int top;

    @SuppressWarnings("unChecked")
    public ArrayStack() {
        this.data = (E[]) new Object[capacity];
        top = 0;
    }

    public void push(E element) {
        extendCapacity();
        data[top++] = element;
    }

    public E pop() {
        E popData = data[--top];
        data[top] = null;
        shrinkCapacity();
        return popData;
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

    public Object[] toArray() {
        return null;
    }

    private void extendCapacity() {
        if (top == capacity) {
            capacity = (int)(capacity * 1.5);
            data = Arrays.copyOf(data, capacity);
        }
    }
    private void shrinkCapacity() {
        if (top <= capacity/4 && capacity > 10) {
            capacity = Math.max(capacity/2, 10);
            data = Arrays.copyOf(data, capacity);
        }
    }
}
