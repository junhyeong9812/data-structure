package com.datastructure.queue.pop;

public class LinkedListDeque<E> {

    private Node<E> front;
    private Node<E> rear;
    private int size;

    public static class Node<E> {
        private E element;
        private Node<E> prev;
        private Node<E> next;

        public Node(E element) { this.element = element; }

        public void setPrev(Node<E> prev) { this.prev = prev; }

        public void setNext(Node<E> next) { this.next = next; }
    }

    public void addFirst(E element) {
        Node<E> newElement = new Node<>(element);
        if (size == 0) {
            front = newElement;
            rear = newElement;
        } else {
            newElement.setNext(front);
            front.setPrev(newElement);
            front = newElement;
        }
        size++;
    }

    public void addLast(E element) {
        Node<E> newElement = new Node<>(element);
        if (size == 0) {
            front = newElement;
            rear = newElement;
        } else {
            newElement.setPrev(rear);
            rear.setNext(newElement);
            rear = newElement;
        }
        size++;
    }

    public E removeFirst() { return null; }

    public E removeLast() { return null; }

    public E peekFirst() { return null; }

    public E peekLast() { return null; }

    public int size() { return 0; }

    public boolean isEmpty() { return true; }

    public void clear() {}
}