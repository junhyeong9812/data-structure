package com.datastructure.persistent;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * [구현] 앞에만 붙이는 불변 목록.
 *
 * 참고: 이 폴더에 PersistentList.java 가 없다. 인터페이스는 src/main 에서 온다.
 *
 * 노드가 곧 목록이다. 목록 객체와 노드 객체를 따로 두지 않는다.
 * 그래서 tail() 이 새 껍데기를 만들지 않고 이미 있는 목록을 그대로 돌려줄 수 있다.
 * 그것이 O(1) prepend 의 정체다.
 */
public final class ConsList<E> implements PersistentList<E> {

    private static final ConsList<Object> EMPTY = new ConsList<>();

    private final E head;
    private final ConsList<E> tail;
    private final int size;

    private ConsList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    private ConsList(E head, ConsList<E> tail) {
        this.head = head;
        this.tail = tail;
        this.size = tail.size + 1;
    }

    /** 빈 목록. 언제나 같은 객체다. */
    @SuppressWarnings("unchecked")
    public static <E> ConsList<E> empty() {
        return (ConsList<E>) EMPTY;
    }

    /** 준 순서 그대로의 목록. 뒤에서부터 앞에 붙인다. */
    @SafeVarargs
    public static <E> ConsList<E> of(E... elements) {
        ConsList<E> list = empty();
        for (int i = elements.length - 1; i >= 0; i--) {
            list = list.prepend(elements[i]);
        }
        return list;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public E head() {
        if (size == 0) {
            throw new NoSuchElementException("빈 목록에는 머리가 없다");
        }
        return head;
    }

    @Override
    public ConsList<E> tail() {
        if (size == 0) {
            throw new NoSuchElementException("빈 목록에는 꼬리가 없다");
        }
        return tail;
    }

    @Override
    public ConsList<E> prepend(E element) {
        return new ConsList<>(element, this);
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("인덱스 " + index + ", 크기 " + size);
        }
        ConsList<E> cur = this;
        for (int i = 0; i < index; i++) {
            cur = cur.tail;
        }
        return cur.head;
    }

    /**
     * 앞에서부터 읽어 앞에 붙이면 순서가 뒤집힌다.
     * prepend 를 n 번 부르므로 노드 n 개가 새로 생기고 옛 목록과 아무것도 공유하지 못한다.
     */
    @Override
    public ConsList<E> reverse() {
        ConsList<E> out = empty();
        for (ConsList<E> cur = this; cur.size > 0; cur = cur.tail) {
            out = out.prepend(cur.head);
        }
        return out;
    }

    @Override
    public List<E> toList() {
        List<E> out = new ArrayList<>(size);
        for (ConsList<E> cur = this; cur.size > 0; cur = cur.tail) {
            out.add(cur.head);
        }
        return out;
    }

    /**
     * 값이 같으면 같다고 본다.
     *
     * 그래서 assertEquals 로는 구조 공유를 검증할 수 없다.
     * 통째로 복사한 목록도 값은 같으므로 통과한다. assertSame 이라야 갈린다.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConsList<?> other) || size != other.size) {
            return false;
        }
        ConsList<?> a = this;
        ConsList<?> b = other;
        while (a.size > 0) {
            if (!Objects.equals(a.head, b.head)) {
                return false;
            }
            a = a.tail;
            b = b.tail;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (ConsList<E> cur = this; cur.size > 0; cur = cur.tail) {
            h = 31 * h + Objects.hashCode(cur.head);
        }
        return h;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (ConsList<E> cur = this; cur.size > 0; cur = cur.tail) {
            if (cur != this) {
                sb.append(", ");
            }
            sb.append(cur.head);
        }
        return sb.append(']').toString();
    }
}
