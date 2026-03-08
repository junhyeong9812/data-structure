package com.datastructure.queue.pop;

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
        this.size = 0;
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
        shrinkArrayDeque();
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
        shrinkArrayDeque();
        return result;
    }

    public E peekFirst() {
        if (size == 0) return null;
        return elements[front];
    }

    public E peekLast() {
        if (size == 0) return null;
        return elements[(rear - 1 + capacity) % capacity];
    }

    public int size() { return size; }

    public boolean isEmpty() { return size==0; }

    public void clear() {
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

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
        if (size <= capacity/4 && capacity > 10) {
            int newCapacity = Math.max(capacity/2, 10);
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
}
