package com.datastructure.queue.pop;

import java.util.NoSuchElementException;

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

    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E removed = front.element;
        if (size == 1) {
            front = null;
            rear = null;
        } else {
            front = front.next;
            front.prev = null;
        }
        size--;
        return removed; }

    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E removed = rear.element;
        if (size == 1) {
            front = null;
            rear = null;
        } else {
            rear = rear.prev;
            rear.next = null;
        }
        size--;
        return removed;
    }

    public E peekFirst() {
        if (size == 0) return null;
        return front.element;
    }

    public E peekLast() {
        if (size == 0) return null;
        return rear.element;
    }

    public int size() { return 0; }

    public boolean isEmpty() { return true; }

    public void clear() {}
}