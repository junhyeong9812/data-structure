package com.datastructure.queue.oop;

import java.util.NoSuchElementException;

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
        growArrayQueue();
        elements[rear] = element;
        rear++;
        size++;
        return true;
    }

    @Override
    public E dequeue() {
        if (size == 0) {
            throw new NoSuchElementException("큐의 데이터가 비어있습니다.");
        }
        E removed = elements[front];
        elements[front] = null;
        front++;
        size--;
        shrinkArrayQueue();
        return removed;
    }

    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }
        E removed = elements[front];
        elements[front] = null;
        front++;
        size--;
        shrinkArrayQueue();
        return removed;
    }

    @Override
    public E peek() {
        if (size == 0) return null;
        return elements[front];
    }

    @Override
    public E front() {
        if (size == 0) return null;
        return elements[front];
    }

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

    private void shrinkArrayQueue() {
        if (size <= capacity/4 && capacity > 10) {
            int newCapacity = Math.max(capacity/2, 10);
            E[] newElements = (E[])(new Object[newCapacity]);

            System.arraycopy(elements, front, newElements, 0, size);

            elements = newElements;
            capacity = newCapacity;
            front = 0;
            rear = size;
        }
    }
}
