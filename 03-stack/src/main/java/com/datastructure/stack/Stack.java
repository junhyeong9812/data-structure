package com.datastructure.stack;

/**
 * 후입선출(LIFO) 스택.
 *
 * 마지막에 넣은 것이 먼저 나온다. 접시를 쌓았다가 위에서부터 꺼내는 것과 같다.
 *
 * 이 인터페이스에는 TODO 가 없다. **계약은 주어지는 것**이고 채우는 것은 구현체다.
 * 같은 계약을 두 가지 방식으로 구현해 본다.
 *
 *   ArrayStack   배열에 쌓는다. 01번 동적 배열의 성질을 그대로 물려받는다.
 *   LinkedStack  노드를 잇는다. 02번 연결 리스트의 성질을 그대로 물려받는다.
 *
 * 둘은 겉으로 완전히 같게 동작해야 한다. 그래서 계약 테스트도 하나만 쓰고 양쪽에 물린다.
 * 다른 것은 "언제 느려지는가"와 "메모리를 어떻게 쓰는가" 뿐이다.
 */
public interface Stack<E> {

    /** 맨 위에 쌓는다. null 도 허용한다. */
    void push(E element);

    /**
     * 맨 위를 꺼내 반환한다. 스택에서 제거된다.
     *
     * @throws java.util.EmptyStackException 비어 있을 때
     */
    E pop();

    /**
     * 맨 위를 보기만 한다. 제거하지 않는다.
     *
     * @throws java.util.EmptyStackException 비어 있을 때
     */
    E peek();

    int size();

    boolean isEmpty();

    /** 전부 비운다. 담고 있던 원소 참조를 남기지 않는다. */
    void clear();
}
