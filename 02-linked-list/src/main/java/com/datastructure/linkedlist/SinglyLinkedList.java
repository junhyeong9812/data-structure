package com.datastructure.linkedlist;

import java.util.Iterator;

/**
 * 단일 연결 리스트.
 *
 * 노드가 다음만 가리킨다. 참조 하나를 뺐을 뿐인데 몇 가지가 크게 달라진다.
 *
 * tail 을 들고 있으므로 addLast 는 O(1) 이다. 맨 뒤에 붙이는 데는 앞 노드가 필요 없으니까.
 * 그런데 removeLast 는 O(n) 이다. tail 을 알아도 그것을 지우려면 그 앞 노드를 알아야 하는데,
 * 앞으로 갈 방법이 없어서 head 부터 다시 세야 한다.
 *
 * 이 비대칭이 단일 연결 리스트의 핵심이다.
 * "tail 이 있는데 왜 removeLast 가 O(n) 인가"를 설명할 수 있으면 이 자료구조를 이해한 것이다.
 *
 * 뒤쪽 인덱스 접근도 마찬가지다. 이중은 tail 에서 거슬러 올라가 평균 절반으로 줄일 수 있지만
 * 여기서는 언제나 head 부터다.
 *
 * 참고: 필드 이름 head, tail 과 Node 의 item, next 는 테스트가 직접 들여다본다.
 */
public class SinglyLinkedList<E> implements List<E> {

    static class Node<E> {
        E item;
        Node<E> next;

        Node(E item, Node<E> next) {
            this.item = item;
            this.next = next;
        }
    }

    Node<E> head;
    Node<E> tail;
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
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    public E getFirst() {
        if (head == null) throw new java.util.NoSuchElementException("비어 있다");
        return head.item;
    }

    public E getLast() {
        if (tail == null) throw new java.util.NoSuchElementException("비어 있다");
        return tail.item;
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

    /** size 를 구현 안에서만 조정하기 위한 통로. TODO 를 채울 때 쓴다. */
    void bumpSize(int delta) {
        size += delta;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<E> n = head; n != null; n = n.next) {
            if (n != head) sb.append(", ");
            sb.append(n.item);
        }
        return sb.append(']').toString();
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 맨 앞에 붙인다.
     *
     * TODO(01): 구현하라. 비어 있었다면 tail 도 이 노드다.
     */
    public void addFirst(E element) {
        throw new UnsupportedOperationException("TODO(01): addFirst");
    }

    /**
     * 맨 뒤에 붙인다.
     *
     * 생각할 것
     *   - tail 이 있으므로 앞에서부터 갈 필요가 없다. 왜 여기는 O(1) 인가?
     *
     * TODO(02): 구현하라.
     */
    public void addLast(E element) {
        throw new UnsupportedOperationException("TODO(02): addLast");
    }

    @Override
    public void add(E element) {
        addLast(element);
    }

    /**
     * index 번째 노드를 찾는다.
     *
     * 생각할 것
     *   - 이중 연결 리스트에서는 뒤쪽 인덱스를 tail 에서 거슬러 갔다. 여기서는 왜 못 하는가?
     *
     * TODO(03): 구현하라.
     */
    private Node<E> node(int index) {
        throw new UnsupportedOperationException("TODO(03): node");
    }

    /**
     * index 위치에 끼운다.
     *
     * 생각할 것
     *   - 끼우려면 그 앞 노드를 잡아야 한다. index 가 0 이면 앞 노드가 없다.
     *   - index == size 면 맨 뒤 추가와 같다.
     *
     * TODO(04): 구현하라.
     */
    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("TODO(04): add(index, element)");
    }

    /** TODO(05): 구현하라. */
    @Override
    public E get(int index) {
        throw new UnsupportedOperationException("TODO(05): get");
    }

    /** TODO(06): 구현하라. 이전 값을 반환한다. */
    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException("TODO(06): set");
    }

    /**
     * index 번째를 지운다.
     *
     * 생각할 것
     *   - 지우려면 그 앞 노드를 잡아야 한다. 맨 앞을 지울 때는 앞 노드가 없다.
     *   - 마지막을 지우면 tail 도 바뀐다. 그 새 tail 을 어떻게 아는가?
     *   - 떼어낸 노드의 next 를 끊어야 한다.
     *
     * TODO(07): 구현하라.
     */
    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("TODO(07): remove(index)");
    }

    /** TODO(08): 구현하라. null 도 찾을 수 있어야 한다. */
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("TODO(08): remove(Object)");
    }

    /**
     * 맨 앞을 지운다. 비었으면 NoSuchElementException.
     *
     * TODO(09): 구현하라.
     */
    public E removeFirst() {
        throw new UnsupportedOperationException("TODO(09): removeFirst");
    }

    /**
     * 맨 뒤를 지운다. 비었으면 NoSuchElementException.
     *
     * 생각할 것
     *   - tail 을 알고 있는데도 왜 O(n) 인가? 무엇이 없어서 그런가?
     *   - 원소가 하나뿐일 때는 어떻게 되는가?
     *
     * TODO(10): 구현하라.
     */
    public E removeLast() {
        throw new UnsupportedOperationException("TODO(10): removeLast");
    }

    /** TODO(11): 구현하라. null 도 찾을 수 있어야 한다. */
    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("TODO(11): indexOf");
    }

    /**
     * 모두 비운다.
     *
     * 생각할 것
     *   - head 와 tail 만 null 로 만들면 노드들이 서로를 계속 붙잡는다.
     *
     * TODO(12): 구현하라.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("TODO(12): clear");
    }

    /** TODO(13): 구현하라. */
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("TODO(13): toArray");
    }

    /**
     * 제자리 뒤집기.
     *
     * 생각할 것
     *   - 단방향이라 이중처럼 prev/next 를 맞바꿀 수 없다. 각 노드의 next 를 앞 노드로 돌려야 한다.
     *   - 포인터 세 개가 필요하다. 이전, 현재, 그리고 다음을 미리 잡아둘 것.
     *     next 를 바꾸는 순간 앞으로 갈 길을 잃기 때문이다.
     *   - head 와 tail 은 어떻게 되는가?
     *
     * TODO(14): 구현하라. 연결 리스트 문제의 고전이다.
     */
    @Override
    public void reverse() {
        throw new UnsupportedOperationException("TODO(14): reverse");
    }

    /**
     * 앞에서부터 훑는 반복자. remove() 를 지원해야 한다.
     *
     * 생각할 것
     *   - 단방향이라 "방금 돌려준 노드의 앞 노드"를 따로 들고 있어야 지울 수 있다.
     *   - next() 전에 remove() 하거나 연속으로 두 번 remove() 하면 IllegalStateException.
     *   - 지우고 나면 다음 next() 가 무엇을 돌려줘야 하는가?
     *   - tail 을 지우는 경우도 잊지 마라.
     *
     * TODO(15): 구현하라. 이게 이 문제집에서 Iterator 를 만드는 유일한 곳이다.
     */
    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("TODO(15): iterator");
    }
}
