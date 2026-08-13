package com.datastructure.linkedlist;

import java.util.Iterator;

/**
 * 순서가 있고 인덱스로 접근할 수 있는 목록.
 *
 * 같은 계약을 두 방식으로 구현한다.
 *
 *   SinglyLinkedList  노드가 다음만 가리킨다
 *   DoublyLinkedList  노드가 앞뒤를 다 가리킨다
 *
 * 겉으로는 완전히 같게 동작해야 한다. 다른 것은 **어떤 연산이 비싼가**뿐이다.
 * 그 차이가 이 문제의 본체다.
 *
 * | 연산 | 단일 | 이중 |
 * |------|------|------|
 * | addFirst, removeFirst | O(1) | O(1) |
 * | addLast | O(1) (tail 이 있으므로) | O(1) |
 * | **removeLast** | **O(n)** | O(1) |
 * | 뒤쪽 인덱스 접근 | O(n) | O(n) 이지만 평균 절반 |
 * | 노드당 메모리 | 참조 1개 | 참조 2개 |
 *
 * tail 을 들고 있는데도 removeLast 가 O(n) 인 이유를 설명할 수 있어야 한다.
 * 그게 단일 연결 리스트의 핵심이다.
 *
 * Iterable 을 확장하는 이유
 *   `get(i)` 를 반복하면 매번 앞에서부터 세므로 전체가 O(n^2) 이 된다.
 *   순회를 O(n) 으로 하는 유일한 방법이 Iterator 다.
 *   **여기서 Iterator 는 문법 설탕이 아니라 복잡도를 바꾸는 장치다.**
 *   01번 배열에서는 get(i) 가 이미 O(1) 이라 Iterator 가 복잡도를 바꾸지 않는다. 그래서 거기엔 없다.
 */
public interface List<E> extends Iterable<E> {

    int size();

    boolean isEmpty();

    /** 맨 뒤에 추가한다. null 도 허용한다. */
    void add(E element);

    /** index 위치에 끼워 넣는다. index == size 면 맨 뒤 추가와 같다. */
    void add(int index, E element);

    E get(int index);

    /** 바꾸고 이전 값을 반환한다. */
    E set(int index, E element);

    /** 지우고 그 값을 반환한다. */
    E remove(int index);

    /** 값이 같은 첫 원소를 지운다. 지웠으면 true. */
    boolean remove(Object o);

    /** 값이 같은 첫 원소의 인덱스. 없으면 -1. null 도 찾을 수 있어야 한다. */
    int indexOf(Object o);

    boolean contains(Object o);

    void clear();

    Object[] toArray();

    /**
     * 이 리스트를 제자리에서 뒤집는다. 새 리스트를 만들지 않는다.
     *
     * 두 구현이 가장 다른 지점이다.
     *   단일: 포인터 세 개(이전, 현재, 다음)를 굴리며 방향을 바꾼다
     *   이중: 각 노드의 prev 와 next 를 맞바꾸고 양 끝을 교환한다
     */
    void reverse();

    /**
     * 앞에서부터 훑는 반복자.
     *
     * `remove()` 를 지원해야 한다. 순회하면서 지우는 것이
     * 이 자료구조에서 조건부 삭제를 O(n) 으로 하는 유일한 방법이기 때문이다.
     */
    @Override
    Iterator<E> iterator();
}
