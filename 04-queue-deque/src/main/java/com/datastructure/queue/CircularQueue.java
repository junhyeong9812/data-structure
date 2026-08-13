package com.datastructure.queue;

import java.util.NoSuchElementException;

/**
 * 원형 배열로 만든 큐.
 *
 * ArrayQueue 를 먼저 만들고 그 테스트를 돌려본 뒤에 이걸 하라.
 * 무엇을 고치는 것인지 모르면 이 구현은 그냥 복잡하기만 하다.
 *
 * 표현 방식
 *   head 는 첫 원소의 인덱스, size 는 담긴 개수다.
 *   i 번째 원소의 실제 위치는 (head + i) % capacity 다.
 *
 *   tail 인덱스를 따로 두지 않는 이유가 있다. head == tail 이 "꽉 참"인지 "빔"인지
 *   구분되지 않기 때문이다. size 를 쓰면 그 모호함이 사라진다.
 *
 * 참고: 필드 이름 elements, head 는 테스트가 직접 들여다본다.
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

    /** i 번째 원소의 실제 배열 인덱스. 되감기를 여기 한 곳에 모아둔다. */
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

    // ------------------------------------------------------------------

    /**
     * 필요하면 배열을 키운다.
     *
     * 생각할 것
     *   - 원소가 배열 끝을 넘어 앞쪽으로 감겨 있을 수 있다.
     *     그 상태로 통째로 복사하면 순서가 뒤죽박죽이 된다. Arrays.copyOf 로는 안 된다.
     *   - 새 배열에서 head 를 어디에 두는 것이 편한가?
     *
     * TODO(06): 구현하라.
     */
    private void ensureCapacity(int minCapacity) {
        throw new UnsupportedOperationException("TODO(06): ensureCapacity");
    }

    /**
     * 뒤에 넣는다.
     *
     * 생각할 것
     *   - 넣을 자리는 몇 번째인가? 그것을 실제 인덱스로 바꾸려면?
     *   - 용량을 먼저 확보해야 한다. 확보하면 배열 길이가 바뀐다는 점에 주의하라.
     *
     * TODO(07): 구현하라.
     */
    @Override
    public void enqueue(E element) {
        throw new UnsupportedOperationException("TODO(07): enqueue");
    }

    /**
     * 앞에서 뺀다.
     *
     * 생각할 것
     *   - head 가 배열 끝에 있으면 다음은 어디인가?
     *   - 뺀 자리를 비우는 것을 잊지 마라.
     *
     * TODO(08): 구현하라.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E dequeue() {
        throw new UnsupportedOperationException("TODO(08): dequeue");
    }

    /** TODO(09): 구현하라. */
    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        throw new UnsupportedOperationException("TODO(09): peek");
    }

    /**
     * 전부 비운다. 용량은 유지한다.
     *
     * 생각할 것
     *   - 살아 있는 원소만 지우면 된다. 감겨 있는 경우를 잊지 마라.
     *
     * TODO(10): 구현하라.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("TODO(10): clear");
    }
}
