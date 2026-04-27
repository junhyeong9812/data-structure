package com.datastructure.heap.pop;

import java.util.Arrays;
import java.util.NoSuchElementException;

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
        if (value == null) throw new IllegalArgumentException("null 넣지 마이소");
        growCapacity();
        array[size] = value;
        siftUp(size);
        size++;
    }

    private void siftUp(int index) {
        while(index > 0) {
            int parent = (index - 1) / 2;
            if (array[index].compareTo(array[parent]) > 0) {
                E tmp = array[index];
                array[index] = array[parent];
                array[parent] = tmp;
                index = parent;
            } else {
                break;
            }
        }
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

    public E extractMax() {
        if (size == 0) throw new NoSuchElementException("삭제할 값이 없습니다.");
        E max = array[0];
        array[0] = array[size - 1];
        array[size - 1] = null;
        size--;
        siftDown(0);
        collapseCapacity();
        return max;
    }

    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int largest = index;

            if (left < size && array[left].compareTo(array[largest]) > 0) {
                largest = left;
            }
            if (right < size && array[right].compareTo(array[largest]) > 0) {
                largest = right;
            }
            if (largest == index) break;
            E temp = array[index];
            array[index] = array[largest];
            array[largest] = temp;
            index = largest;
        }
    }


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