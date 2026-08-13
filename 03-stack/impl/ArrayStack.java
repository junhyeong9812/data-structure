package com.datastructure.stack;

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * [구현] 배열 스택.
 *
 * 참고: 이 폴더에 Stack.java 가 없다. 인터페이스는 src/main 에서 온다.
 * 계약은 하나여야 하므로 복제하지 않는다.
 */
public class ArrayStack<E> implements Stack<E> {

    private static final int DEFAULT_CAPACITY = 4;

    Object[] elements;
    int top;

    public ArrayStack() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayStack(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("초기 용량은 음수일 수 없다: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.top = 0;
    }

    @Override
    public int size() {
        return top;
    }

    @Override
    public boolean isEmpty() {
        return top == 0;
    }

    public int capacity() {
        return elements.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < top; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[i]);
        }
        return sb.append("] <- top").toString();
    }

    // ------------------------------------------------------------------

    /** 01번과 같은 전략. 배수로 키워야 push 의 상환 비용이 O(1) 이 된다. */
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

    @Override
    public void push(E element) {
        ensureCapacity(top + 1);
        elements[top++] = element;
    }

    /**
     * 꺼낸 자리를 null 로 지운다.
     * 이게 없으면 top 밖에 옛 참조가 남아 그 객체가 GC 되지 않는다. 01번과 같은 문제다.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E pop() {
        if (top == 0) throw new EmptyStackException();
        E value = (E) elements[--top];
        elements[top] = null;
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        if (top == 0) throw new EmptyStackException();
        return (E) elements[top - 1];
    }

    @Override
    public void clear() {
        for (int i = 0; i < top; i++) {
            elements[i] = null;
        }
        top = 0;
    }
}
