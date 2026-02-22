package com.datastructure.practice0222.stack;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Objects;

public class ArrayStackImpl<E> implements Stack<E> {

    private int capacity;
    private E[] stackElements;
    private int top;

    @SuppressWarnings("unchecked")
    public ArrayStackImpl() {
        this.capacity = 10;
        this.stackElements = (E[]) new Object[capacity];
        this.top = 0;
    }

    @Override
    public void push(E element) {
        growStack();
        stackElements[top] = element;
        top++;
    }

    @Override
    public E pop() {
        if (size() == 0) {
            throw new EmptyStackException();
        }
        top--;
        E popElement = stackElements[top];
        shrinkStack();
        return popElement;
    }

    @Override
    public E peek() {
        if (size() == 0) {
            throw new EmptyStackException();
        }
        return stackElements[top - 1];
    }

    @Override
    public E top() {
        if (size() == 0) {
            throw new EmptyStackException();
        }
        return stackElements[top - 1];
    }

    @Override
    public boolean isEmpty() { return top == 0; }

    @Override
    public int size() {return top;}

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        capacity = 10;
        top = 0;
        stackElements = (E[])(new Object[capacity]);
    }

    @Override
    public int search(E element) {
        for (int i = 0; i < top; i++) {
            if (Objects.equals(stackElements[i],element)) return i + 1;
        }
        return - 1;
    }

    @Override
    public Object[] toArray() { return Arrays.copyOf(stackElements, top); }

    private void growStack() {
        if (capacity == top) {
            capacity = (int)(capacity * 1.5);
            stackElements = Arrays.copyOf(stackElements, capacity);
        }
    }

    private void shrinkStack() {
        if (top <= capacity/4 && capacity > 10) {
            capacity = Math.max(capacity/2, 10);
            stackElements = Arrays.copyOf(stackElements, capacity);
        }
    }
}
