package com.datastructure.linkedlist.pop;

public class DoublyLinkedList<E> {

    private Node<E> head = null;
    private Node<E> tail = null;
    private int size = 0;

    public static class Node<E> {
        E data;
        Node<E> prev;
        Node<E> next;

        Node(E data) {
            this.data = data;
        }

        Node(E data, Node<E> prev, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    public void addFirst(E element) {
        Node<E> newNode = new Node<>(element);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            head.prev = newNode;
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
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    public void add(int index, E element) {
        if (index < 0 || index > size) {
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
        Node<E> newNode = new Node<>(element);
        Node<E> beforeNode = head;
        for (int i = 0; i <index - 1; i++) {
            beforeNode = beforeNode.next;
        }
        Node<E> nextNode = beforeNode.next;
        newNode.prev = beforeNode;
        newNode.next = nextNode;
        nextNode.prev = newNode;
        beforeNode.next = newNode;
        size++;
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
