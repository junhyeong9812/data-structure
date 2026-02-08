package com.datastructure.stack.oop;

import java.util.*;

public class LinkedStackImpl<E> implements Stack<E> {

    private Node<E> top;

    private class Node<E> {
        E element;
        Node<E> prev;

        public Node(E element) {
            this.element = element;
        }
    }
    public void push(E element) {
        Node<E> addStack = new Node<>(element);
        addStack.prev = top;
        top = addStack;
    }

    public E pop() {
        if (top == null) {
            throw new EmptyStackException();
        }
        E popElement = top.element;
        top = top.prev;
        return popElement;
    }

    public E peek() {
        if (top == null) {
            throw new EmptyStackException();
        }
        return top.element;
    }

    public E top() {
        if (top == null) {
            throw new EmptyStackException();
        }
        return top.element;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size(){
        int count = 0;
        Node<E> checkNode = top;
        while(checkNode != null) {
            checkNode = checkNode.prev;
            count++;
        }
        return count;
    }

    public void clear() {
        top = null;
    }

    public int search(E element) {
        int count = 0;
        int size = size();
        Node<E> checkNode = top;
        while(checkNode != null) {
            if (Objects.equals(checkNode.element,element)) {
                return size - count;
            }
            checkNode = checkNode.prev;
            count++;
        }
        return -1;
    }

//    public E[] toArray(){
//        int size = size();
//        E[] result = (E[])new Object[size];
//        Node<E>  checkNode = top;
//        while (checkNode != null) {
//            result[size-1] = checkNode.element;;
//            checkNode = checkNode.prev;
//            size--;
//        }
//        return result;
//    }
    public Object[] toArray(){
        int size = size();
        Object[] result = new Object[size];
        Node<E>  checkNode = top;
        for (int i = size; i > 0; i--) {
            result[i - 1] = checkNode.element;
            checkNode = checkNode.prev;
        }
        return result;
    }
}
