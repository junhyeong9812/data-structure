package com.datastructure.linkedlist.oop;

import java.util.NoSuchElementException;
import java.util.Objects;

public class DoublyLinkedListImpl<E> implements LinkedList<E> {

    private Node<E> head;
    private Node<E> tail;
    private int size;

    private static class Node<E> {
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
            newNode.next = head;
            head.prev = newNode;
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
            newNode.prev = tail;
            tail.next = newNode;
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
        Node<E> beforeNode = head;
        for (int i = 0; i < index - 1; i++) {
            beforeNode = beforeNode.next;
        }
        Node<E> newNode = new Node<>(element);
        newNode.next = beforeNode.next;
        newNode.prev = beforeNode;
        beforeNode.next = newNode;
        newNode.next.prev = newNode;
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
            head = head.next;
            head.prev = null;
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
            tail = tail.prev;
            tail.next = null;
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
        if (index == size -1 ) {
            return removeLast();
        }
        Node<E> targetNode = head;
        for (int i = 0; i < index; i++) {
            targetNode = targetNode.next;
        }
        E removed = targetNode.data;
        targetNode.prev.next = targetNode.next;
        targetNode.next.prev = targetNode.prev;
        size--;
        return removed;
    }

    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> targetNode = head;
        for (int i = 0; i < index; i++) {
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
        E beforeData = targetNode.data;
        targetNode.data = element;
        return beforeData;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(E element) {
        Node<E> node = head;
        for (int i = 0; i < size; i++) {
            if (Objects.equals(element, node.data)) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    public int indexOf(E element) {
        Node<E> node = head;
        for (int i = 0; i < size; i++) {
            if (Objects.equals(element, node.data)) {
                return i;
            }
            node = node.next;
        }
        return -1;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    public void reverse() {
        if (size == 0) return;
        Node<E> node = head;
        Node<E> prev = null;
        Node<E> next;
        Node<E> originalHead = head;
        head = tail;
        for (int i = 0; i < size; i++) {
            next = node.next;
            node.next = prev;
            prev = node;
            node.prev = next;
            node = next;
        }
        tail = originalHead;
    }
}
