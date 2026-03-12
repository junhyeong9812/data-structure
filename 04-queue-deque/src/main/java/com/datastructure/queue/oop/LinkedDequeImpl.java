package com.datastructure.queue.oop;

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
    public void enqueue(E element)  {}

    @Override
    public void addFirst(E element) {}

    @Override
    public void addLast(E element) {}

    @Override
    public boolean offer(E element) {return true;}

    @Override
    public E dequeue()  {return null;}

    @Override
    public E removeFirst() {return null;}

    @Override
    public E removeLast() {return null;}

    @Override
    public E poll() {return null;}

    @Override
    public E peek() {return null;}

    @Override
    public E peekFirst() {return null;}

    @Override
    public E peekLast() {return null;}

    @Override
    public E front() {return null;}

    @Override
    public boolean isEmpty() {return true;}

    @Override
    public int size() {return 0;}

    @Override
    public void clear() {}
}
