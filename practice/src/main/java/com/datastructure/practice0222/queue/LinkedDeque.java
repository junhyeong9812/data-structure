package com.datastructure.practice0222.queue;

/***
 * 이중 연결 리스트 기반 덱 (Doubly Linked Deque)
 *
 * 양쪽 끝 삽입/삭제 모두 0(1), 용량 제한 없음.
 */
public class LinkedDeque<E> implements Deque<E> {

    private static class Node<E> {
        E value;
        Node<E> prev;
        Node<E> next;

        Node(E value) {
            this.value = value;
        }
    }

    private Node<E> haed;
    private Node<E> tail;
    private int size;
    
    // Deque 연산

}
