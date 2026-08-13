package com.datastructure.stack;

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * 배열로 만든 스택.
 *
 * 01번 동적 배열의 성질을 그대로 물려받는다.
 *   - 맨 위가 배열의 끝이다. 그래서 push/pop 이 시프트 없이 O(1) 이다.
 *     (스택이 배열과 잘 맞는 이유다. 맨 앞을 건드릴 일이 없다.)
 *   - 꽉 차면 더 큰 배열로 옮겨야 한다. 그 순간만 O(n), 상환하면 O(1).
 *   - 남는 용량만큼 메모리를 미리 쓴다.
 *
 * 참고: 필드 이름 elements, top 은 테스트가 직접 들여다본다.
 */
public class ArrayStack<E> implements Stack<E> {

    private static final int DEFAULT_CAPACITY = 4;

    Object[] elements;
    /** 다음에 쓸 자리. 담긴 개수와 같다. */
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

    /** 내부 배열 길이. 확장 전략을 테스트로 확인하려고 열어둔다. */
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

    /**
     * 필요하면 내부 배열을 키운다.
     *
     * 생각할 것
     *   - 01번에서 이미 푼 문제다. 왜 배수로 키우는가?
     *   - 용량이 0 인 경우를 잊지 마라.
     *
     * TODO(01): 구현하라.
     */
    private void ensureCapacity(int minCapacity) {
        throw new UnsupportedOperationException("TODO(01): ensureCapacity");
    }

    /**
     * 맨 위에 쌓는다.
     *
     * TODO(02): 구현하라.
     */
    @Override
    public void push(E element) {
        throw new UnsupportedOperationException("TODO(02): push");
    }

    /**
     * 맨 위를 꺼낸다. 비었으면 EmptyStackException.
     *
     * 생각할 것
     *   - 꺼낸 자리에 옛 참조가 남으면 그 객체는 GC 되지 않는다. 01번과 같은 문제다.
     *
     * TODO(03): 구현하라.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E pop() {
        throw new UnsupportedOperationException("TODO(03): pop");
    }

    /**
     * 맨 위를 보기만 한다. 비었으면 EmptyStackException.
     *
     * TODO(04): 구현하라.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        throw new UnsupportedOperationException("TODO(04): peek");
    }

    /**
     * 전부 비운다. 용량은 유지한다.
     *
     * TODO(05): 구현하라.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("TODO(05): clear");
    }
}
