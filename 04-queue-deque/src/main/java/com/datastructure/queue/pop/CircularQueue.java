package com.datastructure.queue.pop;

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
