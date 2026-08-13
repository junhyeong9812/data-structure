package com.datastructure.queue;

/**
 * 선입선출(FIFO) 큐.
 *
 * 먼저 넣은 것이 먼저 나온다. 줄 서는 것과 같다.
 * 03번 스택(LIFO)과 정확히 반대다. 그 차이가 배열 구현을 훨씬 까다롭게 만든다.
 *
 * 스택은 한쪽 끝만 건드리므로 배열과 잘 맞았다.
 * 큐는 넣는 쪽과 빼는 쪽이 반대라, 배열로 만들면 문제가 생긴다. 그게 이 문제의 본체다.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 *
 * 구현이 셋이다. 순서대로 만들어라.
 *   ArrayQueue     가장 먼저 떠오르는 방식. 동작은 맞는데 문제가 있다. 그 문제를 눈으로 확인한다
 *   CircularQueue  그 문제를 고친 것
 *   (LinkedDeque)  Deque 가 Queue 를 확장하므로 연결 기반 큐는 그쪽에서 나온다
 */
public interface Queue<E> {

    /** 뒤에 넣는다. null 도 허용한다. */
    void enqueue(E element);

    /**
     * 앞에서 뺀다.
     *
     * @throws java.util.NoSuchElementException 비어 있을 때
     */
    E dequeue();

    /**
     * 앞을 보기만 한다.
     *
     * @throws java.util.NoSuchElementException 비어 있을 때
     */
    E peek();

    int size();

    boolean isEmpty();

    /** 전부 비운다. 담고 있던 원소 참조를 남기지 않는다. */
    void clear();
}
