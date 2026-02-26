package com.datastructure.practice0222.queue;

/***
 * 원형 배열 기반 큐 (Circular Array Queue)
 *
 * head와 tail 포인터가 배열을 순회하여 이동한다.
 * 모든 연산 불할 상환 O(1), 용량 초과 시 2배 확장.
 */
public class ArrayQueue<E> implements Queue<E> {

    private static final int DEFAULT_CAPACITY = 10;

    private E[] elements;
    private int head;
    private int tail;
    private int size;

    @SuppressWarnings("unchecked")
    public ArrayQueue() {
        this.elements = (E[]) new Object[DEFAULT_CAPACITY];
    }

    @SuppressWarnings("unchecked")
    public ArrayQueue(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("용량은 1 이상이어야 합니다.");
        this.elements = (E[]) new Object[capacity];
    }

    @Override
    public void enqueue(E element) {
        if (size == elements.length) {
            resize(elements.length * 2);
        }
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
        size++;
    }

    // 내부 유틸리티 메서드
    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        E[] newElements = (E[]) new Object[newCapacity];
        for ( int i = 0; i < size; i++) {
            newElements[i] = elements[(head + i)% elements.length];
        }
        elements = newElements;
        head = 0;
        tail = size;
    }
}