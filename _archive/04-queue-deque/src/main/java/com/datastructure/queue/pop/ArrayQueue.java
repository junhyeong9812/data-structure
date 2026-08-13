package com.datastructure.queue.pop;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class ArrayQueue<E> {
    private int capacity;
    private E[] elements;
    private int front;
    private int rear;

    public ArrayQueue() {
        this.capacity = 10;
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
    }

    public ArrayQueue(int capacity) {
        this.capacity = capacity;
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
    }

    public void enqueue(E element) {
        growArrayQueue();
        elements[rear] = element;
        rear++;
    }

    public boolean offer(E element) {
        enqueue(element);
        return true;
    }

    public E dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        E result = elements[front];
        elements[front] = null;
        front++;
        shrinkArrayQueue();
        return result;
    }

    public E poll() {
        if (isEmpty()) {
            return null;
        }
        E result = elements[front];
        elements[front] = null;
        front++;
        shrinkArrayQueue();
        return result;
    }

    public E peek() {
        return isEmpty() ? null : elements[front];
    }

    public E front() {
        return peek();
    }

    public boolean isEmpty() {return rear == front;}

    public int size() {return rear - front;}

    public void clear() {
        Arrays.fill(elements, front, rear, null);
        this.front = 0;
        this.rear = 0;
    }

    private void growArrayQueue() {
        if (rear >= capacity) {
            int newCapacity = (int)(capacity * 1.5);
            E[] newElements = (E[])(new Object[newCapacity]);

            // front부터 rear까지 새배열의 0부터 복사
            int size = rear - front;
            System.arraycopy(elements, front, newElements, 0, size);

            elements = newElements;
            capacity =  newCapacity;
            front = 0;
            rear = size;
        }
    }

    private void shrinkArrayQueue() {
        int size = rear - front;
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
