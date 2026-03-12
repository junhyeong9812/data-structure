package com.datastructure.queue.oop;

public class LinkedDequeImpl<E> implements Deque<E> {

    private Node<E> front;
    private Node<E> rear;
    private int size;

    public LinkedDequeImpl() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    private static class Node<E> {
        private E element;
        private Node<E> prev;
        private Node<E> next;

        public Node(E element) {
            this.element = element;
            this.prev = null;
            this.next = null;
        }
        public void setPrev(Node<E> prev) {this.prev = prev; }
        public void setNext(Node<E> next) {this.next = next; }
    }

}
