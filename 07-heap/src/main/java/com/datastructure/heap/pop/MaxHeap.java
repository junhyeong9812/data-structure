package com.datastructure.heap.pop;

public class MaxHeap<E extends Comparable<E>> {

    private E[] array;
    private int size;

    public MaxHeap() {
        this.array = (E[]) new Comparable[10];
        this.size = 0;
    }

    public MaxHeap(int initialCapacity) {
        this.array = (E[]) new Comparable[initialCapacity];
        this.size = 0;
    }

    public void insert(E value) {}
    public boolean offer(E value) { return false; }

    public E extractMax() { return null; }
    public E poll() { return null; }

    public E getMax() { return null; }
    public E peek() { return null; }

    public int size() { return 0; }
    public boolean isEmpty() { return false; }
    public void clear() {}

    public static <E extends Comparable<E>> MaxHeap<E> heapify(E[] array) { return null; }

    public void increaseKey(int index, E newValue) {}

    public E delete(int index) { return null; }

    public MaxHeap<E> merge(MaxHeap<E> other) { return null; }
}