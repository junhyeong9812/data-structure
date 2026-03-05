package com.datastructure.queue.pop;

public class ArrayDeque<E> {

    private int capacity;
    private E[] elements;
    private int front;
    private int rear;

    public ArrayDeque() {
        this.capacity = 10;
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
    }

    public ArrayDeque(int capacity) {
        this.capacity = capacity;
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
    }

    public void addFirst(E element) {

    }

    public void addLast(E element) {}

    public E removeFirst() { return null; }

    public E removeLast() { return null; }

    public E peekFirst() { return null; }

    public E peekLast() { return null; }

    public int size() { return 0; }

    public boolean isEmpty() { return true; }

    public void clear() {}

    private void growArrayDeque() {
        if (rear >= capacity) {
            int newCapacity = (int)(capacity * 1.5);
            E[] newElements = (E[])(new Object[newCapacity]);

            int size = rear - front;
            System.arraycopy(elements, front, newElements, 0, size);

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
