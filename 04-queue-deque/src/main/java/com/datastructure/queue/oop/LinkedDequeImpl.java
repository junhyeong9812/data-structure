package com.datastructure.queue.oop;

import java.util.NoSuchElementException;

public class LinkedDequeImpl<E> implements Deque<E> {

    private Node<E> front;
    private Node<E> rear;
    private int size;

    public LinkedDequeImpl() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    private static class Node<E> {
        private E element;
        private Node<E> prev;
        private Node<E> next;

        public Node(E element) {
            this.element = element;
            this.prev = null;
            this.next = null;
        }
        public void setPrev(Node<E> prev) {this.prev = prev; }
        public void setNext(Node<E> next) {this.next = next; }
    }

    @Override
    public void enqueue(E element)  {
        Node<E> newNode = new Node<>(element);
        if (size == 0) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    @Override
    public void addFirst(E element) {
        Node<E> newNode = new Node<>(element);
        if (size == 0) {
            front = newNode;
            rear = newNode;
        } else {
            front.prev = newNode;
            newNode.next = front;
            front = newNode;
        }
        size++;
    }

    @Override
    public void addLast(E element) {
        enqueue(element);
    }

    @Override
    public boolean offer(E element) {
        enqueue(element);
        return true;
    }

    @Override
    public E dequeue()  {
        if (isEmpty()) {
            throw new NoSuchElementException("큐가 비였어요");
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
        return removed;
    }

    @Override
    public E removeFirst() {
        return dequeue();
    }

    @Override
    public E removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("큐가 비었어요!");
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

    @Override
    public E poll() {
        if (isEmpty()) {
            return null;
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
        return removed;
    }

    @Override
    public E peek() {
        if (size == 0) return null;
        return front.element;
    }

    @Override
    public E peekFirst() {return peek();}

    @Override
    public E peekLast() {
        if (size == 0) return null;
        return rear.element;
    }

    @Override
    public E front() {return null;}

    @Override
    public boolean isEmpty() {return true;}

    @Override
    public int size() {return 0;}

    @Override
    public void clear() {}
}
