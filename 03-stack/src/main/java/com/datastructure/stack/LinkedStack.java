package com.datastructure.stack;

import java.util.EmptyStackException;

/**
 * 노드를 이어 만든 스택.
 *
 * 02번 연결 리스트의 성질을 그대로 물려받는다.
 *   - 맨 앞(head)이 스택의 top 이다. 맨 앞 삽입/삭제가 O(1) 이라 스택에 딱 맞는다.
 *   - 확장 복사가 없다. 그래서 push 가 "가끔 느려지는" 일이 없다. 언제나 O(1) 이다.
 *   - 대신 원소마다 노드 객체가 하나씩 더 생긴다. 메모리는 더 쓴다.
 *
 * 여기는 단방향이면 충분하다. 스택은 뒤로 갈 일이 없기 때문이다.
 * 02번에서 prev 를 관리하느라 들었던 수고가 여기서는 필요 없다.
 *
 * 참고: 필드 이름 top, size 와 Node 의 item, next 는 테스트가 직접 들여다본다.
 */
public class LinkedStack<E> implements Stack<E> {

    static class Node<E> {
        E item;
        Node<E> next;

        Node(E item, Node<E> next) {
            this.item = item;
            this.next = next;
        }
    }

    Node<E> top;
    private int size;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("top -> [");
        for (Node<E> n = top; n != null; n = n.next) {
            if (n != top) sb.append(", ");
            sb.append(n.item);
        }
        return sb.append(']').toString();
    }

    // ------------------------------------------------------------------

    /**
     * 맨 위에 쌓는다.
     *
     * 생각할 것
     *   - 02번의 linkFirst 와 같은 일이다. 단방향이라 고칠 링크가 하나뿐이다.
     *
     * TODO(06): 구현하라.
     */
    @Override
    public void push(E element) {
        throw new UnsupportedOperationException("TODO(06): push");
    }

    /**
     * 맨 위를 꺼낸다. 비었으면 EmptyStackException.
     *
     * 생각할 것
     *   - 떼어낸 노드가 다음 노드를 계속 가리키면 스택 전체가 GC 되지 않는다.
     *
     * TODO(07): 구현하라.
     */
    @Override
    public E pop() {
        throw new UnsupportedOperationException("TODO(07): pop");
    }

    /**
     * 맨 위를 보기만 한다. 비었으면 EmptyStackException.
     *
     * TODO(08): 구현하라.
     */
    @Override
    public E peek() {
        throw new UnsupportedOperationException("TODO(08): peek");
    }

    /**
     * 전부 비운다.
     *
     * TODO(09): 구현하라.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("TODO(09): clear");
    }
}
