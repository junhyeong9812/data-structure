package com.datastructure.queue;

import java.util.NoSuchElementException;

/**
 * 양방향 노드로 만든 데크.
 *
 * 02번 연결 리스트에서 양 끝만 쓰는 형태다. 인덱스 접근이 없으니 훨씬 단순하다.
 * 되감기도, 확장도, 용량도 없다. 원형 배열이 왜 그렇게 까다로웠는지가 대비로 드러난다.
 *
 * 대신 원소마다 노드가 하나씩 생기고 참조 두 개 값의 메모리를 더 쓴다.
 *
 * 참고: 필드 이름 first, last 와 Node 의 item, prev, next 는 테스트가 직접 들여다본다.
 */
public class LinkedDeque<E> implements Deque<E> {

    static class Node<E> {
        E item;
        Node<E> prev;
        Node<E> next;

        Node(Node<E> prev, E item, Node<E> next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    Node<E> first;
    Node<E> last;
    private int size;

    // ------------------------------------------------------------------
    // 채워져 있는 부분
    // ------------------------------------------------------------------

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

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
    public E peekFirst() {
        if (first == null) throw new NoSuchElementException("비어 있다");
        return first.item;
    }

    @Override
    public E peekLast() {
        if (last == null) throw new NoSuchElementException("비어 있다");
        return last.item;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<E> n = first; n != null; n = n.next) {
            if (n != first) sb.append(", ");
            sb.append(n.item);
        }
        return sb.append(']').toString();
    }

    /** size 를 구현체 안에서만 조정하기 위한 통로. TODO 를 채울 때 쓴다. */
    void bumpSize(int delta) {
        size += delta;
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 앞에 넣는다.
     *
     * 생각할 것
     *   - 비어 있었다면 이 노드가 first 이자 last 다.
     *
     * TODO(16): 구현하라. size 는 bumpSize(1) 로 늘린다.
     */
    @Override
    public void addFirst(E element) {
        throw new UnsupportedOperationException("TODO(16): addFirst");
    }

    /**
     * 뒤에 넣는다. addFirst 의 대칭이다.
     *
     * TODO(17): 구현하라.
     */
    @Override
    public void addLast(E element) {
        throw new UnsupportedOperationException("TODO(17): addLast");
    }

    /**
     * 앞을 뺀다. 비었으면 NoSuchElementException.
     *
     * 생각할 것
     *   - 마지막 하나를 뺐다면 last 도 정리해야 한다.
     *   - 떼어낸 노드의 링크를 끊지 않으면 데크 전체가 GC 되지 않는다.
     *
     * TODO(18): 구현하라.
     */
    @Override
    public E removeFirst() {
        throw new UnsupportedOperationException("TODO(18): removeFirst");
    }

    /**
     * 뒤를 뺀다. 비었으면 NoSuchElementException.
     *
     * TODO(19): 구현하라.
     */
    @Override
    public E removeLast() {
        throw new UnsupportedOperationException("TODO(19): removeLast");
    }

    /**
     * 전부 비운다. 노드 사슬도 끊는다.
     *
     * TODO(20): 구현하라.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("TODO(20): clear");
    }
}
