package com.datastructure.queue.oop;

public class ArrayQueueImpl<E> implements Queue<E> {

    private int capacity;
    private E[] elements;
    private int size;
    private int front;
    private int rear;

    public ArrayQueueImpl() {
        this.capacity = 10;
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
    }

    public ArrayQueueImpl(int capacity) {
        this.capacity = capacity;
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
    }

    @Override
    public void enqueue(E element) {
        growArrayQueue();
        elements[rear] = element;
        rear++;
        size++;
    }

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

    private void growArrayQueue() {
        if (rear >= capacity) {
            int newCapacity = (int)(capacity * 1.5);

            E[] newElements = (E[])(new Object[newCapacity]);

            System.arraycopy(elements, front, newElements, 0, size);

            elements = newElements;
            capacity = newCapacity;
            front = 0;
            rear = size;
        }
    }
}
