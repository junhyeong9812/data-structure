package com.datastructure.heap.pop;

import java.util.Arrays;
import java.util.NoSuchElementException;

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

    public boolean offer(E value) {
        if (value == null) return false;
        addElement(value);
        return true;
    }

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

    public E extractMin() {
        if (size == 0) throw new NoSuchElementException("삭제할 값이 없는데용?");
        return removeRoot();
    }
    public E poll() { return null; }

    private E removeRoot() {
        E min = array[0];
        array[0] = array[size - 1];
        array[size - 1] = null;
        size--;
        siftDown(0);
        collapseCapacity();
        return min;
    }

    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2* index + 2;
            int smallest = index;

            if (left < size && array[left].compareTo(array[smallest]) < 0) {
                smallest = left;
            }
            if (right < size && array[right].compareTo(array[smallest]) < 0) {
                smallest = right;
            }
            if (smallest == index) break;
            E temp = array[index];
            array[index] = array[smallest];
            array[smallest] = temp;
            index = smallest;
        }
    }

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

    private void collapseCapacity() {
        int capacity = this.array.length;
        if (this.size <= capacity / 4 && capacity > 10) {
            array = Arrays.copyOf(array, Math.max(capacity / 2, 10));
        }
    }
}