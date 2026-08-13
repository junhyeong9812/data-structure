package com.datastructure.queue;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * [구현] 배열 큐 - 되감기 없는 나이브 버전.
 *
 * 계약은 전부 지킨다. 다만 head 왼쪽 자리를 다시 쓰지 못해 공간을 버린다.
 * 그 성질을 ArrayQueueTest.wastesSpace 가 숫자로 보여준다. 고치는 것은 CircularQueue 다.
 *
 * 참고: 이 폴더에 Queue.java 가 없다. 인터페이스는 src/main 에서 온다.
 */
public class ArrayQueue<E> implements Queue<E> {

    private static final int DEFAULT_CAPACITY = 4;

    Object[] elements;
    int head;
    private int size;

    public ArrayQueue() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayQueue(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("초기 용량은 1 이상이어야 한다: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.head = 0;
        this.size = 0;
    }

    @Override public int size() { return size; }
    @Override public boolean isEmpty() { return size == 0; }
    public int capacity() { return elements.length; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[head + i]);
        }
        return sb.append(']').toString();
    }

    /**
     * 되감지 않으므로 필요한 자리는 "head 부터 끝까지"다.
     * head 가 오른쪽으로 갈수록 앞쪽 빈칸은 영영 못 쓴다. 그래서 배열이 계속 커진다.
     */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= elements.length) {
            return;
        }
        int newCapacity = elements.length * 2;
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        elements = Arrays.copyOf(elements, newCapacity);
    }

    @Override
    public void enqueue(E element) {
        ensureCapacity(head + size + 1);
        elements[head + size] = element;
        size++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E dequeue() {
        if (size == 0) throw new NoSuchElementException("비어 있다");
        E value = (E) elements[head];
        elements[head] = null;       // 뺀 자리를 비운다. 안 그러면 GC 가 안 된다
        head++;
        size--;
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        if (size == 0) throw new NoSuchElementException("비어 있다");
        return (E) elements[head];
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[head + i] = null;
        }
        head = 0;
        size = 0;
    }
}
