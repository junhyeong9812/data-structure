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
        return data[top - 1];

    }

    public E top() {
        return data[top - 1];
    }

    public boolean isEmpty() {
        return top == 0;
    }

    public int size() {
        return top;
    }

    public void clear() {
        capacity = 10;
        data = (E[]) new Object[capacity];
        top = 0;
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
