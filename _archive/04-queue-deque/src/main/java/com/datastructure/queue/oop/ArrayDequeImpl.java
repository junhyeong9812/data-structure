package com.datastructure.queue.oop;

import java.util.NoSuchElementException;

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
        if( size >= capacity) {
            growArrayDeque();
        }
        elements[rear] = element;
        rear = (rear + 1) % capacity;
        size++;
    }

    @Override
    public void addFirst(E element) {
        if( size >= capacity) {
            growArrayDeque();
        }
        front = (front - 1 + capacity) % capacity;
        elements[front] = element;
        size++;
    }

    @Override
    public void addLast(E element) {
        enqueue(element);
    }

    @Override
    public boolean offer(E element) {
        enqueue(element);
        return true;
    }

    @Override
    public E dequeue()  {
        if ( size == 0) {
            throw new NoSuchElementException("덱이 비었습니다.");
        }
        E removed = elements[front];
        elements[front] = null;
        front = (front + 1) % capacity;
        size--;
        shrinkArrayDeque();
        return removed;
    }

    @Override
    public E removeFirst() {return dequeue();}

    @Override
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException("덱이 비었습니다.");
        }
        rear = (rear - 1 + capacity) % capacity;
        E removed = elements[rear];
        elements[rear] = null;
        size--;
        shrinkArrayDeque();
        return removed;
    }

    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }
        E removed = elements[front];
        elements[front] = null;
        front = (front + 1) % capacity;
        size--;
        shrinkArrayDeque();
        return removed;
     }

    @Override
    public E peek() {
        if (size == 0) {
            return null;
        }
        return elements[front];}

    @Override
    public E peekFirst() {
        return peek();
    }

    @Override
    public E peekLast() {
        if (size == 0) {
            return null;
        }
        return elements[(rear - 1 + capacity) % capacity];
    }

    @Override
    public E front() {
        return peek();
    }

    @Override
    public boolean isEmpty() {return size == 0;}

    @Override
    public int size() {return size;}

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        this.elements = (E[])(new Object[capacity]);
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

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

    private void shrinkArrayDeque() {
        if (size < capacity/4 && capacity >10) {
            int newCapacity = capacity/2;
            E[] newElements = (E[])(new Object[newCapacity]);

            for (int i = 0; i < size; i++) {
                newElements[i] = elements[(front + i)% capacity];
            }
            capacity = newCapacity;
            elements = newElements;
            front = 0;
            rear = size;
        }
    }
}
