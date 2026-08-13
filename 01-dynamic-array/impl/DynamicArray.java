package com.datastructure.dynamicarray;

import java.util.Arrays;

/**
 * [구현] 동적 배열.
 *
 * 스켈레톤의 TODO 를 채운 버전이다. 막혔을 때만 보고, 보고 나면 다시 스켈레톤으로 돌아가 직접 쳐라.
 * 눈으로 읽은 것은 손이 기억하지 않는다.
 */
public class DynamicArray<E> {

    private static final int DEFAULT_CAPACITY = 4;

    private Object[] elements;
    private int size;

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("초기 용량은 음수일 수 없다: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int capacity() {
        return elements.length;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkIndex(index);
        return (E) elements[index];
    }

    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("인덱스 " + index + ", 크기 " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("인덱스 " + index + ", 크기 " + size);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[i]);
        }
        return sb.append(']').toString();
    }

    // ------------------------------------------------------------------

    /**
     * 2배씩 키운다.
     *
     * 왜 2배인가: 1씩 늘리면 add n 번에 총 복사량이 1+2+...+n = O(n^2) 이 된다.
     * 2배로 키우면 확장은 log n 번만 일어나고 총 복사량은 n + n/2 + n/4 + ... < 2n = O(n) 이다.
     * 그래서 add 한 번의 상환 비용이 O(1) 이 된다.
     *
     * 용량이 0일 때 2배를 해도 0이므로 그 경우만 따로 잡아준다.
     * minCapacity 가 2배보다 클 때 그쪽을 따르는 분기는 지금 공개 API 로는 도달하지 않는다
     * (ensureCapacity 가 size+1 로만 불리므로). addAll 같은 대량 삽입이 생기면 그때 쓰인다.
     * 부수 효과로 elements.length * 2 가 오버플로해 음수가 되어도 이 분기가 받아낸다.
     */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= elements.length) {
            return;
        }
        int newCapacity = (elements.length == 0) ? DEFAULT_CAPACITY : elements.length * 2;
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        elements = Arrays.copyOf(elements, newCapacity);
    }

    public void add(E element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
    }

    /**
     * 뒤로 밀 때 System.arraycopy 를 쓴다.
     * 직접 for 문으로 앞에서부터 옮기면 아직 안 읽은 값을 덮어쓴다(뒤에서부터 옮겨야 안전하다).
     * arraycopy 는 겹치는 구간을 알아서 처리해준다.
     */
    public void add(int index, E element) {
        checkPositionIndex(index);
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        checkIndex(index);
        E old = (E) elements[index];
        elements[index] = element;
        return old;
    }

    /**
     * 앞으로 당긴 뒤 마지막 칸을 null 로 지운다.
     *
     * 이 null 대입이 없으면 size 밖에 옛 참조가 남아 그 객체가 GC 되지 않는다.
     * 논리적으로는 지워졌는데 메모리에서는 안 지워지는, 찾기 어려운 누수다.
     */
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        checkIndex(index);
        E old = (E) elements[index];
        int moved = size - index - 1;
        if (moved > 0) {
            System.arraycopy(elements, index + 1, elements, index, moved);
        }
        elements[--size] = null;
        maybeShrink();
        return old;
    }

    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index < 0) {
            return false;
        }
        remove(index);
        return true;
    }

    /**
     * null 을 담을 수 있으므로 null 탐색 경로를 따로 둔다.
     * o.equals(...) 를 그냥 부르면 o 가 null 일 때 NPE 가 난다.
     */
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (elements[i] == null) return i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(elements[i])) return i;
            }
        }
        return -1;
    }

    /** size 만 0 으로 만들면 참조가 남는다. remove 와 같은 이유로 명시적으로 끊어준다. */
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }


    /**
     * 1/4 이하로 떨어지면 절반으로 줄인다.
     *
     * 왜 1/4 인가: 늘리는 문턱(꽉 참)과 줄이는 문턱 사이에 여유를 두기 위해서다.
     * 1/2 에서 줄이면 capacity 16, size 8 같은 경계에서 add/remove 를 번갈아 할 때
     * 매번 늘리고 줄이기를 반복한다. 연산 하나가 O(n) 이 되어 상환 O(1) 이 무너진다(thrashing).
     *
     * 1/4 로 두면 줄인 직후 size 는 새 capacity 의 절반이라, 다시 꽉 차려면 그만큼 더 넣어야 한다.
     * 그 여유가 왕복을 막는다.
     */
    private void maybeShrink() {
        int halved = elements.length / 2;
        if (size <= elements.length / 4 && halved >= DEFAULT_CAPACITY) {
            elements = Arrays.copyOf(elements, halved);
        }
    }

    /** 내부 배열을 그대로 주면 호출자가 우리 상태를 마음대로 바꿀 수 있다. 복사본을 준다. */
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }
}
