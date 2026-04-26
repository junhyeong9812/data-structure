package com.datastructure.heap.pop;

import java.util.Arrays;

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

    public void insert(E value) {
        array[size] = value;
        for (int i = 0; i < array.length; i++) {
            int cmp = value.compareTo(array[i]);
            if (cmp == 0) {
                System.arraycopy(array, i, array, i+1,array.length);
                array[i] = value;
                break;
            }
            if (cmp > 0) {
                System.arraycopy(array, i, array, i+1,array.length);
                array[i] = value;
                break;
            }
        }
    }
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

    private void growCapacity() {
        int capacity = this.array.length;
        if (this.size >= capacity) {
            array = Arrays.copyOf(array, (int)(capacity * 1.5));
        }
    }

    private void collapseCapacity() {
        int capacity = this.array.length;
        if (this.size <= capacity/4 && capacity> 10) {
            array = Arrays.copyOf(array,Math.max(capacity/2, 10));
        }
    }
}