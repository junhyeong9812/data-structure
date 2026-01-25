package com.datastructure.linkedlist.pop;

import java.util.NoSuchElementException;

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
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E removed = head.data;
        if (size == 1) {
            head = null;
            tail = null;
        } else {
            head.next.prev = null;
            head = head.next;
        }
        size--;
        return removed;
    }

    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E removed = tail.data;
        if (size == 1) {
            head = null;
            tail = null;
        } else {
            tail.prev.next = null;
            tail = tail.prev;
        }
        size--;
        return removed;
    }

    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            return removeFirst();
        }
        if (index == size - 1) {
            return removeLast();
        }
        Node<E> targetNode = head;
        for (int i = 0; i < index; i++) {
            targetNode = targetNode.next;
        }
        targetNode.prev.next = targetNode.next;
        targetNode.next.prev = targetNode.prev;
        E removed = targetNode.data;
        size--;
        return removed;
    }

    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> targetNode = head;
        for(int i = 0; i < index; i++) {
            targetNode = targetNode.next;
        }
        return targetNode.data;
    }

    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> targetNode = head;
        for (int i = 0; i < index; i++) {
            targetNode = targetNode.next;
        }
        E removed = targetNode.data;
        targetNode.data = element;
        return removed;
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
