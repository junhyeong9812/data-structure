package com.datastructure.stack;

import java.util.EmptyStackException;

/**
 * [구현] 연결 스택.
 *
 * 02번 linkFirst 와 같은 일을 한다. 단방향이라 고칠 링크가 하나뿐이다.
 * 확장 복사가 없어 push 가 "가끔 느려지는" 순간이 없다.
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

    @Override
    public void push(E element) {
        top = new Node<>(element, top);
        size++;
    }

    /**
     * 떼어낸 노드의 next 를 끊는다.
     * 안 끊으면 그 노드 하나가 아래 스택 전체를 GC 대상에서 제외시킨다.
     */
    @Override
    public E pop() {
        if (top == null) throw new EmptyStackException();
        Node<E> popped = top;
        E value = popped.item;
        top = popped.next;
        popped.item = null;
        popped.next = null;
        size--;
        return value;
    }

    @Override
    public E peek() {
        if (top == null) throw new EmptyStackException();
        return top.item;
    }

    @Override
    public void clear() {
        Node<E> n = top;
        while (n != null) {
            Node<E> next = n.next;
            n.item = null;
            n.next = null;
            n = next;
        }
        top = null;
        size = 0;
    }
}
