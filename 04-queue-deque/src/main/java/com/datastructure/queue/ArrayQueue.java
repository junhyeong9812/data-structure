package com.datastructure.queue;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * 배열로 만든 큐 - 가장 먼저 떠오르는 방식.
 *
 * head 를 앞에 두고, dequeue 하면 head 를 오른쪽으로 한 칸 옮긴다.
 * enqueue 는 뒤에 붙인다. 03번 스택에서 하던 것과 크게 다르지 않아 보인다.
 *
 * 이 구현은 계약을 전부 지킨다. 동작은 맞다.
 * 그런데 문제가 하나 있고, 그 문제는 테스트로 확인할 수 있다.
 * 무엇이 문제인지는 직접 만들고 테스트를 돌려서 눈으로 보라.
 * (ArrayQueueTest 에 그걸 드러내는 테스트가 있다.)
 *
 * 고치는 것은 다음 파일 CircularQueue 에서 한다. 여기서는 먼저 문제를 만나는 것이 목적이다.
 *
 * 참고: 필드 이름 elements, head 는 테스트가 직접 들여다본다.
 */
public class ArrayQueue<E> implements Queue<E> {

    private static final int DEFAULT_CAPACITY = 4;

    Object[] elements;
    /** 첫 원소의 인덱스. dequeue 할 때마다 오른쪽으로 간다. */
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

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public int capacity() {
        return elements.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[head + i]);
        }
        return sb.append(']').toString();
    }

    // ------------------------------------------------------------------

    /**
     * head + size 가 배열 끝을 넘지 않도록 확보한다.
     *
     * 되감기를 하지 않으므로 필요한 자리는 "head 부터 끝까지"다.
     * 즉 앞쪽 head 칸은 비어 있어도 쓸 수 없다.
     *
     * TODO(01): 구현하라. 01번, 03번과 같은 배수 확장이면 된다.
     */
    private void ensureCapacity(int minCapacity) {
        throw new UnsupportedOperationException("TODO(01): ensureCapacity");
    }

    /**
     * 뒤에 넣는다. 넣을 자리는 head + size 다.
     *
     * TODO(02): 구현하라.
     */
    @Override
    public void enqueue(E element) {
        throw new UnsupportedOperationException("TODO(02): enqueue");
    }

    /**
     * 앞에서 뺀다. head 를 오른쪽으로 옮긴다.
     *
     * 생각할 것
     *   - 뺀 자리를 그대로 두면 그 객체는 GC 되지 않는다. 01번, 03번과 같은 문제다.
     *
     * TODO(03): 구현하라.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E dequeue() {
        throw new UnsupportedOperationException("TODO(03): dequeue");
    }

    /** TODO(04): 구현하라. 비었으면 NoSuchElementException. */
    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        throw new UnsupportedOperationException("TODO(04): peek");
    }

    /**
     * 전부 비운다. head 도 처음으로 되돌린다.
     *
     * TODO(05): 구현하라.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("TODO(05): clear");
    }
}
