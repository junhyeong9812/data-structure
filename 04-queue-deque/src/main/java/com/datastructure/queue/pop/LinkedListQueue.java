package com.datastructure.queue.pop;

public class LinkedListQueue<E> {
    private Node<E> front;
    private Node<E> rear;
    private int size;

    public LinkedListQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    private static class Node<E> {
        private E element;
        private Node<E> next;

        public Node(E element) {
            this.element = element;
        }

        public void setNext(Node<E> next) {
            this.next = next;
        }
    }
}
