package com.datastructure.stack.oop;

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

    public int search(E element) {
        return 0;
    }

    public E[] toArray(){
        return null;
    }
}
