package com.datastructure.queue.pop;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class ArrayDeque<E> {

    private int capacity;
    private E[] elements;
    private int front;
    private int rear;
    private int size;

    public ArrayDeque() {
        this.capacity = 10;
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

    public ArrayDeque(int capacity) {
        this.capacity = capacity;
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
    }

    public void addFirst(E element) {
        if (size >= capacity) {
            growArrayDeque();
        }
        front = (front - 1 + capacity) % capacity;
        elements[front] = element;
        size++;
    }

    public void addLast(E element) {
        if (size >= capacity) {
            growArrayDeque();
        }
        elements[rear] = element;
        rear = (rear + 1) % capacity;
        size++;
    }

    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E result = elements[front];
        elements[front] = null;
        front = (front + 1) % capacity;
        size--;
        return result;
    }

    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        rear = (rear - 1 + capacity) % capacity;
        E result = elements[rear];
        elements[rear] = null;
        size--;
        return result;
    }

    public E peekFirst() { return null; }

    public E peekLast() { return null; }

    public int size() { return 0; }

    public boolean isEmpty() { return true; }

    public void clear() {}

    private void growArrayDeque() {
        if (size >= capacity) {
            int newCapacity = (int)(capacity * 1.5);
            E[] newElements = (E[])(new Object[newCapacity]);

            for (int i = 0; i < size; i++) {
                newElements[i] = elements[(front + i) % capacity];
            }

            elements = newElements;
            capacity = newCapacity;
            front = 0;
            rear = size;
        }
    }

    private void shrinkArrayDeque() {
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
