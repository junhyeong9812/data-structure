package com.datastructure.linkedlist.oop;

public class SinglyLinkedListImpl<E> implements LinkedList<E> {

    private Node<E> head;
    private Node<E> tail;
    private int size;

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
        Node<E> newNode = new Node<>(element);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    public void addLast(E element) {
        Node<E> newNode = new Node<>(element);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public void add(int index, E element) {
        if(index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            addFirst(element);
            return;
        }
        if (index == size) {
            addLast(element);
            return;
        }
        Node<E> beforeNode = head;
        for (int i = 0; i < index - 1; i++) {
            beforeNode = beforeNode.next;
        }
        Node<E> newNode = new Node<>(element);
        newNode.next = beforeNode.next;
        beforeNode.next = newNode;
        size++;
    }

    public E removeFirst() {

    }

    public E removeLast() {

    }

    public E remove() {

    }

    public E get(int index) {

    }

    public E set(int index, E element) {

    }

    public int size() {

    }

    public boolean isEmpty() {

    }

    public boolean contains(E element) {

    }

    public int indexOf(E element) {

    }

    public void clear() {

    }

    public void reverse() {

    }
}
