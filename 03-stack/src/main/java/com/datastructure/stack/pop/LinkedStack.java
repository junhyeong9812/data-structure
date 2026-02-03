package com.datastructure.stack.pop;

import java.util.List;

public class LinkedStack<E> {

    private Node<E> top;

    public static class Node<E> {
        E element;
        Node<E> prev;

        public Node(E element) {
            this.element = element;
        }
    }

    public void push(E element) {
        Node<E> newNode = new Node<>(element);
        newNode.prev = top;
        top = newNode;
    }

    public E pop() {
        E element = top.element;
        top = top.prev;
        return element;
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
}
