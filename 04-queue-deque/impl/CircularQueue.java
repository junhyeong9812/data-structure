package com.datastructure.queue;

import java.util.NoSuchElementException;

/**
 * [구현] 원형 배열 큐.
 *
 * ArrayQueue 와 다른 점은 딱 하나, 배열 끝에 닿으면 처음으로 되감는다는 것이다.
 * 그 한 가지가 "원소 0개인데 배열 2048칸"을 "원소 0개면 배열 4칸"으로 바꾼다.
 */
public class CircularQueue<E> implements Queue<E> {

    private static final int DEFAULT_CAPACITY = 4;

    Object[] elements;
    int head;
    private int size;

    public CircularQueue() {
        this(DEFAULT_CAPACITY);
    }

    public CircularQueue(int initialCapacity) {
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

    int indexOf(int i) {
        return (head + i) % elements.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[indexOf(i)]);
        }
        return sb.append(']').toString();
    }

    /**
     * 감긴 것을 풀어서 옮긴다.
     *
     * Arrays.copyOf 로 통째로 복사하면 안 된다. 원소가 배열 끝을 넘어 앞쪽에 있을 수 있는데,
     * 그대로 복사하면 논리적 순서가 깨진다. 그래서 indexOf 로 순서대로 읽어 옮긴다.
     * 옮기고 나면 감김이 풀렸으므로 head 는 0 이다.
     */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= elements.length) {
            return;
        }
        int newCapacity = elements.length * 2;
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        Object[] moved = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            moved[i] = elements[indexOf(i)];
        }
        elements = moved;
        head = 0;
    }

    @Override
    public void enqueue(E element) {
        ensureCapacity(size + 1);          // 배열 길이가 바뀔 수 있으므로 indexOf 보다 먼저 부른다
        elements[indexOf(size)] = element;
        size++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E dequeue() {
        if (size == 0) throw new NoSuchElementException("비어 있다");
        E value = (E) elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;   // 끝에 닿으면 처음으로
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
            elements[indexOf(i)] = null;   // 감겨 있을 수 있으므로 논리 순서로 지운다
        }
        head = 0;
        size = 0;
    }
}
