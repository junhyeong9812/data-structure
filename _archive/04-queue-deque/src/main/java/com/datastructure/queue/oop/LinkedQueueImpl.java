package com.datastructure.queue.oop;

import java.util.NoSuchElementException;

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
    public void enqueue(E element) {
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
    public boolean offer(E element) {
        Node<E> newNode = new Node<>(element);
        if ( size == 0 ) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
        return true;
    }

    @Override
    public E dequeue() {
        if (size == 0) {
            throw new NoSuchElementException("빈 큐에서는 요소를 제거할 수 없습니다.");
        }
        E removed = front.element;
        front = front.next;
        size--;
        return removed;
    }

    @Override
    public E poll() {
        if (size == 0) {
            throw new NoSuchElementException("빈 큐에서는 요소를 제거할 수 없습니다.");
        }
        E removed = front.element;
        front = front.next;
        size--;
        return removed;
    }

    @Override
    public E peek() {
        if (size == 0) {
            return null;
        }
        return front.element;
    }

    @Override
    public E front() {
        if (size == 0) {
            return null;
        }
        return front.element;
    }

    @Override
    public boolean isEmpty() {return size==0;}

    @Override
    public int size() {return size;}

    @Override
    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }
}
