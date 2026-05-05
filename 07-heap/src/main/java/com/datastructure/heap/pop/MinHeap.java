package com.datastructure.heap.pop;

import java.util.Arrays;

public class MinHeap<E extends Comparable<E>> {

    private E[] array;
    private int size;

    public MinHeap() {
        this.array = (E[]) new Comparable[10];
        this.size = 0;
    }
    public MinHeap(int initialCapacity) {
        this.array = (E[]) new Comparable[initialCapacity];
        this.size = 0;
    }

    public void insert(E value) {
        if (value == null) throw new IllegalArgumentException("null 넣지 마이소");
        addElement(value);
    }

    public boolean offer(E value) { return false; }

    private void addElement(E value) {
        growCapacity();
        array[size] = value;
        siftUp(size);
        size++;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (array[index].compareTo(array[parent]) < 0) {
                E tmp = array[index];
                array[index] = array[parent];
                array[parent] = tmp;
                index = parent;
            } else {
                break;
            }
        }
    }

    public E extractMin() { return null; }
    public E poll() { return null; }

    public E getMin() { return null; }
    public E peek() { return null; }

    public int size() { return 0; }
    public boolean isEmpty() { return false; }
    public void clear() {}

    public static <E extends Comparable<E>> MinHeap<E> heapify(E[] array) { return null; }

    public void decreaseKey(int index, E newValue) {}

    public E delete(int index) { return null; }

    public MinHeap<E> merge(MinHeap<E> other) { return null; }

    private void growCapacity() {
        int capacity = this.array.length;
        if (this.size >= capacity) {
            array = Arrays.copyOf(array, (int)(capacity * 1.5));
        }
    }
}