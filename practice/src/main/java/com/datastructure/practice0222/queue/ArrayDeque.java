package com.datastructure.practice0222.queue;

/**
 * 원형 배열 기반 덱(Circular Array Deque)
 *
 * head는 뒤로, tail은 앞으로 이동하며 배열을 순환한다.
 * 모든 연산 분할 상환 0(1). 용량 초과 시 2배 확장.
 * java의 ArrayDeque와 동일한 방식
 */
public class ArrayDeque<E> implements Deque<E> {

    private static final int DEFAULT_CAPACITY = 10;

    private E[] elements;
    private int head;
    private int tail;
    private int size;

    @SuppressWarnings("unchecked")
    public ArrayDeque() {
        this.elements = (E[]) new Object[DEFAULT_CAPACITY];
    }

    @SuppressWarnings("unchecked")
    public ArrayDeque(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("용량은 1 이상이여아 합니다.");
        this.elements = (E[]) new Object[capacity];
    }

    // Deque 연산

    @Override
    public void addFirst(E element) {
        if (size == elements.length) resize(elements.length * 2);
        head =dec(head);
        elements[head] = element;
        size++;
    }

    @Override
    public void addLast(E element) {
        if (size == elements.length) resize(elements.length * 2);
    }

    // 인덱스를 한 칸 뒤로 (순환)
    private int dec(int idx) {
        return (idx - 1 + elements.length) % elements.length;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        E[] newElements = (E[]) new Object[newCapacity];
        for (int i = 0; i <size; i++) {
            newElements[i] = elements[(head + i) % elements.length];
        }
        elements = newElements;
        head = 0;
        tail = size;
    }
}
