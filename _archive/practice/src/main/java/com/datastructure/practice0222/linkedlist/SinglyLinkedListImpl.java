package com.datastructure.practice0222.linkedlist;

import java.util.NoSuchElementException;
import java.util.Objects;

public class SinglyLinkedListImpl<E> implements LinkedList<E>{

    private Node<E> head;
    private Node<E> tail;
    private int size;

    private static class Node<E> {
        E element;
        Node<E> next;

        Node(E element) {this.element = element;}

        Node(E element, Node<E> next) {
            this.element = element;
            this.next = next;
        }
    }

    @Override
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

    @Override
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

    @Override
    public void add(int index, E element) {
        if(index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if(index == 0) {
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

    @Override
    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        E removed = head.element;
        head = head.next;
        if (size == 1) {
            tail = null;
        }
        size--;
        return removed;
    }

    @Override
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E removed = tail.element;
        if (size == 1) {
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

    @Override
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
        Node<E> targetNode = beforeNode.next;
        beforeNode.next = targetNode.next;
        size++;
        return targetNode.element;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> targetNode = head;
        for (int i = 0; i < index; i++) {
            targetNode = targetNode.next;
        }
        return targetNode.element;
    }

    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        E beforeElement = null;
        Node<E> targetNode = head;
        for (int i = 0; i < index; i++) {
            targetNode = targetNode.next;
        }
        beforeElement = targetNode.element;
        targetNode.element = element;
        return beforeElement;
    }

    @Override
    public int size() { return size;}

    @Override
    public boolean isEmpty(){return size == 0;}

    @Override
    public boolean contains(E element){
        Node<E> node = head;
        for (int i = 0; i < size; i++) {
            if (Objects.equals(element, node.element)) return true;
            node = node.next;
        }
        return false;
    }

    @Override
    public int indexOf(E element){
        Node<E> node = head;
        for (int i = 0; i < size; i++) {
            if (Objects.equals(element, node.element)) return i;
            node = node.next;
        }
        return -1;
    }

    @Override
    public void clear(){
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public void reverse(){
        Node<E> node = head;
        Node<E> next = null;
        Node<E> prev = null;
        tail = head;
        for (int i = 0; i < size; i++) {
            next = node.next;
            node.next = prev;
            prev = node;
            node = next;
        }
        head = prev;
    }
}
