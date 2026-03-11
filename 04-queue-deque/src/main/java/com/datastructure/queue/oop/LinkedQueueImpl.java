package com.datastructure.queue.oop;

public class LinkedQueueImpl<E> implements Queue<E> {

    private Node<E> front;
    private Node<E> rear;
    private int size;

    public LinkedQueueImpl() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    private static class Node<E> {
        E element;
        Node<E> next;

        public Node(E element) {this.element = element;}

        public void setNext(Node<E> next) {this.next = next;}
    }

    @Override
    public void enqueue(E element) {}

    @Override
    public boolean offer(E element) {
        return true;
    }
    @Override
    public E dequeue() {
        return null;
    }

    @Override
    public E poll() {return null;}

    @Override
    public E peek() {return null;}

    @Override
    public E front() {return null;}

    @Override
    public boolean isEmpty() {return true;}

    @Override
    public int size() {return 0;}

    @Override
    public void clear() {}
}
