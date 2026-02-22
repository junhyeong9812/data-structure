package com.datastructure.practice0222.linkedlist;

import java.util.NoSuchElementException;
import java.util.Objects;

public class DoublyLinkedListImpl<E> implements LinkedList<E> {

    private Node<E> head;
    private Node<E> tail;
    private int size;

    private static class Node<E> {
        E element;
        Node<E> prev;
        Node<E> next;

        Node(E element) {this.element = element;}

        Node(E element, Node<E> prev, Node<E> next) {
            this.element = element;
            this.prev = prev;
            this.next = next;
        }
    }

    @Override
    public void addFirst(E element){
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

    @Override
    public void addLast(E element){
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

    @Override
    public void add(int index, E element){
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
        for (int i =0; i < index - 1; i++) {
            beforeNode = beforeNode.next;
        }
        Node<E> newNode = new Node<>(element);
        newNode.next = beforeNode.next;
        newNode.prev = beforeNode;
        Node<E> afterNode= beforeNode.next;
        beforeNode.next = newNode;
        afterNode.prev = newNode;
        size++;
    }

    @Override
    public E removeFirst(){
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E removed = head.element;
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

    @Override
    public E removeLast(){
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E removed = tail.element;
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

    @Override
    public E remove(int index){
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
        E removed = targetNode.element;
        Node<E> beforeNode = targetNode.prev;
        Node<E> afterNode = targetNode.next;
        beforeNode.next = afterNode;
        afterNode.prev = beforeNode;
        size--;
        return removed;
    }

    @Override
    public E get(int index){
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
    public E set(int index, E element){
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> targetNode = head;
        for (int i = 0; i < index; i++) {
            targetNode = targetNode.next;
        }
        E beforeElement = targetNode.element;
        targetNode.element = element;
        return beforeElement;
    }

    @Override
    public int size(){return size;}

    @Override
    public boolean isEmpty(){return size == 0;}

    @Override
    public boolean contains(E element){
        Node<E> node = head;
        for (int i = 0; i < size; i++) {
            if (Objects.equals(element, node.element)) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    @Override
    public int indexOf(E element){
        Node<E> node = head;
        for(int i = 0; i < size; i++) {
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
