package com.datastructure.queue;

import java.util.NoSuchElementException;

/** [구현] 원형 배열 데크. CircularQueue 를 양쪽 끝으로 확장한 것이다. */
public class ArrayDeque<E> implements Deque<E> {

    private static final int DEFAULT_CAPACITY = 4;

    Object[] elements;
    int head;
    private int size;

    public ArrayDeque() { this(DEFAULT_CAPACITY); }

    public ArrayDeque(int initialCapacity) {
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

    private int indexOf(int i) { return (head + i) % elements.length; }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= elements.length) return;
        int newCapacity = elements.length * 2;
        if (newCapacity < minCapacity) newCapacity = minCapacity;
        Object[] moved = new Object[newCapacity];
        for (int i = 0; i < size; i++) moved[i] = elements[indexOf(i)];
        elements = moved;
        head = 0;
    }

    @Override public void enqueue(E element) { addLast(element); }
    @Override public E dequeue() { return removeFirst(); }
    @Override public E peek() { return peekFirst(); }

    @Override
    @SuppressWarnings("unchecked")
    public E peekFirst() {
        if (size == 0) throw new NoSuchElementException("비어 있다");
        return (E) elements[head];
    }

    @Override
    @SuppressWarnings("unchecked")
    public E peekLast() {
        if (size == 0) throw new NoSuchElementException("비어 있다");
        return (E) elements[indexOf(size - 1)];
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

    // ------------------------------------------------------------------

    /**
     * head 를 한 칸 "뒤로" 감는다.
     *
     * head 가 0 이면 한 칸 앞은 배열의 마지막 칸이다.
     * (head - 1) 만 하면 -1 이 되므로 배열 길이를 더한 뒤 나머지를 취한다.
     * ensureCapacity 가 head 를 0 으로 바꿀 수 있으므로 반드시 그 뒤에 계산한다.
     */
    @Override
    public void addFirst(E element) {
        ensureCapacity(size + 1);
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = element;
        size++;
    }

    @Override
    public void addLast(E element) {
        ensureCapacity(size + 1);
        elements[indexOf(size)] = element;
        size++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E removeFirst() {
        if (size == 0) throw new NoSuchElementException("비어 있다");
        E value = (E) elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;
        return value;
    }

    /** 뒤에서 뺄 때는 head 가 움직이지 않는다. 마지막 원소의 자리만 비우면 된다. */
    @Override
    @SuppressWarnings("unchecked")
    public E removeLast() {
        if (size == 0) throw new NoSuchElementException("비어 있다");
        int last = indexOf(size - 1);
        E value = (E) elements[last];
        elements[last] = null;
        size--;
        return value;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) elements[indexOf(i)] = null;
        head = 0;
        size = 0;
    }
}
