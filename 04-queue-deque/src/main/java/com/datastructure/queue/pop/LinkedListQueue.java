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

    public void enqueue(E element) {
        Node<E> newNode = new Node<>(element);
        if (this.front == null) {
            this.front = newNode;
        } else {
            this.rear.setNext(newNode);
        }
        this.rear = newNode;
        size++;
    }

    public boolean offer(E element) {
        enqueue(element);
        return true;
    }
}
