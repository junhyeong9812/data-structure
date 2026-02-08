package com.datastructure.stack.oop;

import java.util.EmptyStackException;

public class LinkedStackImpl<E> implements Stack<E> {

    private Node<E> top;

    private class Node<E> {
        E element;
        Node<E> prev;

        public Node(E element) {
            this.element = element;
        }
    }
    public void push(E element) {
        Node<E> addStack = new Node<>(element);
        addStack.prev = top;
        top = addStack;
    }

    public E pop() {
        if (top == null) {
            throw new EmptyStackException();
        }
        E popElement = top.element;
        top = top.prev;
        return popElement;
    }

    public E peek() {
        if (top == null) {
            throw new EmptyStackException();
        }
        return top.element;
    }

    public E top() {
        if (top == null) {
            throw new EmptyStackException();
        }
        return top.element;
    }

    public boolean isEmpty() {

        return true;
    }

    public int size(){
        return 0;
    }

    public void clear() {

    }

    public int search(E element) {
        return 0;
    }

    public E[] toArray(){
        return null;
    }
}
