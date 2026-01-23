package com.datastructure.linkedlist.pop;

import java.util.ArrayList;
import java.util.List;

public class SinglyLinkedList<E> {

    private Node<E> head = null;
    private Node<E> tail = null;
    private int size = 0;

    private static class Node<E> {
        E data;
        Node<E> next;

        Node(E data) {
            this.data = data;
        }

        Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }


    public void addFirst(E element) {
        Node<E> newNode = new Node<>(element, head);
        head = newNode;
        if (size == 0) {
            tail = newNode;
        }
        size++;
    }

    public void addLast(E element) {

    }

    public void add(int index, E element) {

    }

    public E removeFirst() {
        return null;
    }

    public E removeLast() {
        return null;
    }

    public E remove(int index) {
        return null;
    }

    public E get(int index) {
        return null;
    }

    public E set(int index, E element) {
        return null;
    }

    public int size() {
        return  0;
    }

    public boolean isEmpty() {
        return true;
    }

    public boolean contains(E element) {
        return true;
    }

    public int indexOf(E element) {
        return -1;
    }

    public void clear() {}

    public void reverse() {}
}
