package com.datastructure.practice0222.stack;

import java.util.EmptyStackException;
import java.util.Objects;

public class LinkedStackImpl<E> implements Stack<E> {

    private Node<E> top;

    private static class Node<E> {
        E element;
        Node<E> prev;
        public Node(E element) {this.element = element;}
    }

    @Override
    public void push(E element) {
        Node<E> addStack = new Node<>(element);
        addStack.prev = top;
        top = addStack;
    }

    @Override
    public E pop() {
        if (top == null) {
            throw new EmptyStackException();
        }
        E popElement = top.element;
        top = top.prev;
        return popElement;
    }

    @Override
    public E peek() {
        if (top == null) {
            throw new EmptyStackException();
        }
        return top.element;
    }

    @Override
    public E top() {
        if (top == null) {
            throw new EmptyStackException();
        }
        return top.element;
    }

    @Override
    public boolean isEmpty() { return top == null; }

    @Override
    public int size() {
        int count = 0;
        Node<E> checkNode = top;
        while(checkNode != null) {
            checkNode = checkNode.prev;
            count++;
        }
        return count;
    }

    @Override
    public void clear() { top = null; }

    @Override
    public int search(E element) {
        int count = 0;
        Node<E> checkNode = top;
        while(checkNode != null) {
            if (Objects.equals(checkNode.element, element)) {
                return count + 1;
            }
            checkNode = checkNode.prev;
            count++;
        }
        return -1;
    }

    @Override
    public Object[] toArray() {
        int size = size();
        Object[] result = new Object[size];
        Node<E> checkNode = top;
        for (int i = size; i > 0; i--) {
            result[i - 1] = checkNode.element;
            checkNode = checkNode.prev;
        }
        return result;
    }
}
