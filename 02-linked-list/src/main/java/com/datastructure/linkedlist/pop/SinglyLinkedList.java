package com.datastructure.linkedlist.pop;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

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
        Node<E> newNode = new Node<>(element, beforeNode.next);
        beforeNode.next = newNode;
        size++;
    }

    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E removed = head.data;
        head = head.next;
        size--;
        if (size == 0) {
            tail = null;
        }
        return removed;
    }

    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E removed = tail.data;
        if (size ==1) {
            head = null;
            tail = null;
        } else {
            Node<E> beforeNode = head;
            for (int i = 0; i < size - 2; i++) {
                beforeNode = beforeNode.next;
            }
            beforeNode.next = null;
            tail = beforeNode;
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

        Node<E> beforeNode = head;
        for (int i = 0; i < index - 1; i++) {
            beforeNode = beforeNode.next;
        }
        E removedData = beforeNode.next.data;
        beforeNode.next = beforeNode.next.next;

        size--;
        return removedData;
    }

    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> beforeNode = head;
        for (int i = 0; i < index; i++) {
            beforeNode = beforeNode.next;
        }
        return beforeNode.data;
    }

    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> beforeNode = head;
        for (int i = 0; i < index; i++) {
            beforeNode = beforeNode.next;
        }
        E oldData = beforeNode.data;
        beforeNode.data = element;
        return oldData;
    }

    public int size() {
        return  size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(E element) {
        Node<E> node = head;
        for (int index = 0; index < size; index++) {
            if(Objects.equals(node.data, element)) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    public int indexOf(E element) {
        Node<E> node = head;
        for (int index = 0; index < size; index++) {
            if(Objects.equals(node.data, element)) {
                return index;
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


    }
}
