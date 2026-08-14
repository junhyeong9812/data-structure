package com.datastructure.queue;

import java.util.NoSuchElementException;

/**
 * 원형 배열로 만든 데크.
 *
 * CircularQueue 를 양쪽 끝으로 확장한 것이다. 그 문제를 먼저 풀고 오라.
 * 되감기 계산(indexOf)과 확장(ensureCapacity)은 거기서 이미 푼 것이므로 여기서는 채워두었다.
 * 같은 것을 두 번 풀게 하지 않는다.
 *
 * 새로 나오는 것은 "앞쪽으로도 넣고 뺀다"뿐이다. head 가 뒤로 감기는 경우가 생긴다.
 *
 * 참고: 필드 이름 elements, head 는 테스트가 직접 들여다본다.
 */
public class ArrayDeque<E> implements Deque<E> {

    private static final int DEFAULT_CAPACITY = 4;

    Object[] elements;
    int head;
    private int size;

    public ArrayDeque() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayDeque(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("초기 용량은 1 이상이어야 한다: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.head = 0;
        this.size = 0;
    }

    // ------------------------------------------------------------------
    // 채워져 있는 부분 (CircularQueue 에서 이미 푼 것들)
    // ------------------------------------------------------------------

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

    /** i 번째 원소의 실제 배열 인덱스. */
    private int indexOf(int i) {
        return (head + i) % elements.length;
    }

    /** 감긴 것을 풀어서 새 배열에 순서대로 옮긴다. head 는 0 으로 되돌린다. */
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

    /** Queue 로 쓸 때의 이름들. 데크의 특정 끝에 그대로 대응한다. */
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
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 앞에 넣는다.
     *
     * 생각할 것
     *   - head 가 0 일 때 한 칸 "앞"은 배열의 어디인가? 음수가 되지 않게 하려면?
     *   - 용량을 먼저 확보해야 한다. 확보하면 배열 길이와 head 가 둘 다 바뀐다.
     *
     * TODO(11): 구현하라.
     */
    @Override
    public void addFirst(E element) {
        throw new UnsupportedOperationException("TODO(11): addFirst");
    }

    /**
     * 뒤에 넣는다. CircularQueue.enqueue 와 같은 일이다.
     *
     * TODO(12): 구현하라.
     */
    @Override
    public void addLast(E element) {
        throw new UnsupportedOperationException("TODO(12): addLast");
    }

    /**
     * 앞을 뺀다. 비었으면 NoSuchElementException.
     *
     * TODO(13): 구현하라. 뺀 자리를 비우는 것을 잊지 마라.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E removeFirst() {
        throw new UnsupportedOperationException("TODO(13): removeFirst");
    }

    /**
     * 뒤를 뺀다. 비었으면 NoSuchElementException.
     *
     * TODO(14): 구현하라. head 는 움직이지 않는다는 점에 주의하라.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E removeLast() {
        throw new UnsupportedOperationException("TODO(14): removeLast");
    }

    /**
     * 전부 비운다. 용량은 유지한다. 감겨 있는 경우를 잊지 마라.
     *
     * TODO(15): 구현하라.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("TODO(15): clear");
    }
}
