package com.datastructure.queue.pop;

import java.util.Arrays;

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

    public boolean offer(E element) {return false;}

    public E dequeue() {return null;}

    public E poll() {return null;}

    public E peek() {return null;}

    public E front() {return null;}

    public boolean isEmpty() {return true;}

    public int size() {return 0;}

    public void clear() {}

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
