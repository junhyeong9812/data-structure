package com.datastructure.queue.pop;

import java.util.NoSuchElementException;

public class CircularQueue<E> {
    private int capacity;
    private E[] elements;
    private int front;
    private int rear;
    private int size;

    @SuppressWarnings("unchecked")
    public CircularQueue() {
        this.capacity = 10;
        this.elements = (E[])(new Object[this.capacity]);
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public CircularQueue(int capacity) {
        this.capacity = capacity;
        this.elements = (E[])(new Object[this.capacity]);
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

    public void enqueue(E element) {
        if (size == capacity) {
            throw new IllegalStateException("Queue is full");
        }
        elements[rear] = element;
        rear = (rear + 1) % capacity;
        size++;
    }

    public boolean offer(E element) {
        if (size == capacity) {
            return false;
        }
        enqueue(element);
        return true;
    }

    public E dequeue() {
        if (size == 0) {
            throw new NoSuchElementException("Queue is Empty");
        }
        E result = elements[front];
        elements[front] = null;
        front = (front + 1) % capacity;
        size--;
        return result;
    }

    public E poll() {
        if (size == 0) {
            return null;
        }
        return dequeue();
    }

    public boolean isFull() {
        return size == capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        this.elements = (E[])(new Object[this.capacity]);
        front = 0;
        rear = 0;
        size = 0;
    }
}
