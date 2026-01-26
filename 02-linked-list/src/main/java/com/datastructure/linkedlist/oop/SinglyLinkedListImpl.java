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
