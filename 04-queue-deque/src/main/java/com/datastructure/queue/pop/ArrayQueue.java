package com.datastructure.queue.pop;

import java.util.Arrays;

public class ArrayQueue<E> {
    private int capacity;
    private E[] elements;
    private int size;

    public ArrayQueue() {
        this.capacity = 10;
        this.elements = (E[])(new Object[capacity]);
        this.size = 0;
    }

    public ArrayQueue(int capacity) {
        this.capacity = capacity;
        this.elements = (E[])(new Object[capacity]);
        this.size = 0;
    }

    public void enqueue(E element) {}

    public boolean offer(E element) {return false;}

    public E dequeue() {return null;}

    public E poll() {return null;}

    public E peek() {return null;}

    public E front() {return null;}

    public boolean isEmpty() {return true;}

    public int size() {return 0;}

    public void clear() {}

    private void growArrayQueue() {
        if (capacity == size) {
            capacity = (int)(capacity * 1.5);
            elements = Arrays.copyOf(elements, capacity);
        }
    }

    private void shrinkArrayQueue() {
        if (size <= capacity/4 && capacity > 10) {
            capacity = Math.max(capacity/2, 10);
            elements = Arrays.copyOf(elements, capacity);
        }
    }
}
