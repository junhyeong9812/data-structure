package com.datastructure.stack.pop;

import java.util.EmptyStackException;
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
        if (top == null) {
            throw new EmptyStackException();
        }
        E element = top.element;
        top = top.prev;
        return element;
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
        return top == null;
    }

    public int size() {
        if (top == null) {
            return 0;
        }
        Node<E> temp = top;
        int count = 0;
        while(temp != null) {
            temp = temp.prev;
            count++;
        }
        return count;
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
