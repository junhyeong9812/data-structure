package com.datastructure.queue.oop;

public class ArrayDequeImpl<E> implements Deque<E> {

    private int capacity;
    private E[] elements;
    private int front;
    private int rear;
    private int size;

    @SuppressWarnings("unchecked")
    public ArrayDequeImpl() {
        this.capacity = 10;
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public ArrayDequeImpl(int selectCapacity) {
        this.capacity = selectCapacity;
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

    @Override
    public void enqueue(E element)  {
        growArrayDeque();
    }

    @Override
    public void addFirst(E element) {}

    @Override
    public void addLast(E element) {}

    @Override
    public boolean offer(E element) {return true;}

    @Override
    public E dequeue()  {return null;}

    @Override
    public E removeFirst() {return null;}

    @Override
    public E removeLast() {return null;}

    @Override
    public E poll() {return null;}

    @Override
    public E peek() {return null;}

    @Override
    public E peekFirst() {return null;}

    @Override
    public E peekLast() {return null;}

    @Override
    public E front() {return null;}

    @Override
    public boolean isEmpty() {return true;}

    @Override
    public int size() {return 0;}

    @Override
    public void clear() {}

    @SuppressWarnings("unchecked")
    private void growArrayDeque() {
        int newCapacity = (int)(capacity * 1.5);
        E[] newElements = (E[])(new Object[newCapacity]);

//        if (front + size <= capacity) {
//            System.arraycopy(elements, front, newElements, 0, size);
//        } else {
//            int firstPart = capacity - front;
//            System.arraycopy(elements, front, newElements, 0, firstPart);
//            System.arraycopy(elements, 0, newElements, firstPart, size - firstPart);
//        }

        for (int i = 0; i < size; i++) {
            newElements[i] = elements[(front + i)%capacity];
        }

        capacity = newCapacity;
        elements = newElements;
        front = 0;
        rear = size;
    }
}
