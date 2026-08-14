package com.datastructure.queue;

/**
 * 양쪽 끝에서 넣고 뺄 수 있는 자료구조(double ended queue).
 *
 * Queue 를 확장한다. 데크는 큐가 할 수 있는 일을 전부 할 수 있기 때문이다.
 * (enqueue = addLast, dequeue = removeFirst, peek = peekFirst 로 대응한다.)
 *
 * 이 상속에는 설계 의도가 있다.
 * 어떤 코드가 "줄 세우기"만 필요하면 파라미터 타입을 Queue 로 받아라.
 * Deque 로 받으면 호출자가 앞으로도 넣을 수 있게 되고, 그건 의도한 계약이 아니다.
 * 인터페이스는 능력을 제한해서 의도를 드러낸다.
 *
 *   ArrayDeque   원형 배열. CircularQueue 를 양쪽 끝으로 확장한 것
 *   LinkedDeque  양방향 노드. 02번 연결 리스트에서 양 끝만 쓰는 형태
 */
public interface Deque<E> extends Queue<E> {

    void addFirst(E element);

    void addLast(E element);

    /** @throws java.util.NoSuchElementException 비어 있을 때 */
    E removeFirst();

    /** @throws java.util.NoSuchElementException 비어 있을 때 */
    E removeLast();

    /** @throws java.util.NoSuchElementException 비어 있을 때 */
    E peekFirst();

    /** @throws java.util.NoSuchElementException 비어 있을 때 */
    E peekLast();
}
