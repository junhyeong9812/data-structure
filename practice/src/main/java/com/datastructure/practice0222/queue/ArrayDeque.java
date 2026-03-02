package com.datastructure.practice0222.queue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

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

    @Override
    public E removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("덱이 비어있습니다.");
        E value = elements[head];
        elements[head] = null;
        head = inc(head);
        size--;
        shrinkIfNeeded();
        return value;
    }

    @Override
    public E removeLast() {
        if (isEmpty()) throw new NoSuchElementException("덱이 비어있습니다.");
        tail = dec(tail);
        E value = elements[tail];
        elements[tail] = null;
        size--;
        shrinkIfNeeded();
        return value;
    }

    @Override
    public E peekFirst() {
        if(isEmpty()) throw new NoSuchElementException("덱이 비어있습니다.");
        return elements[head];
    }

    @Override
    public E peekLast() {
        if (isEmpty()) throw new NoSuchElementException("덱이 비어있습니다.");
        return elements[dec(tail)];
    }

    // Queue 인터페이스 매핑 (FIFO)

    @Override
    public void enqueue(E element) {
        addLast(element);
    }

    @Override
    public E dequeue() {
        return removeFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    // 공통
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(E element) {
        for (int i = 0; i < size; i++ ) {
            if (Objects.equals(elements[(head + i) % elements.length], element)) return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        elements = (E[]) new Object[DEFAULT_CAPACITY];
        head = 0;
        tail = 0;
        size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                return elements[(head + cursor++) % elements.length];
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[(head + i) % elements.length]);
        }
        return sb.append("]").toString();
    }

    // 내부 유틸리티
    // 인덱스를 한 칸 앞으로 (순환)
    private int inc(int idx) {
        return (idx + 1) % elements.length;
    }
    // 인덱스를 한 칸 뒤로 (순환)
    private int dec(int idx) {
        return (idx - 1 + elements.length) % elements.length;
    }

    private void shrinkIfNeeded() {
        if (size > 0 && size <= elements.length / 4 && elements.length > DEFAULT_CAPACITY) {
            resize((Math.max(elements.length / 2, DEFAULT_CAPACITY)));
        }
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
