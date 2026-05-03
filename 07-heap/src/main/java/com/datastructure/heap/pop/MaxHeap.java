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
        return removeRoot();
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


    public E poll() {
        if (size == 0) return null;
        return removeRoot();
    }

    private E removeRoot() {
        E max = array[0];
        array[0] = array[size - 1];
        array[size - 1] = null;
        size--;
        siftDown(0);
        collapseCapacity();
        return max;
    }

    public E getMax() {
        if (size == 0) throw new NoSuchElementException("힙이 비어있습니다.");
        return array[0];
    }

    public E peek() {
        if (size == 0) return null;
        return array[0];
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    public void clear() {
        this.array = (E[]) new Comparable[10];
        this.size = 0;
    }

    public static <E extends Comparable<E>> MaxHeap<E> heapify(E[] array) {
        if (array == null) throw new IllegalArgumentException();
        for (E e : array) {
            if (e == null) throw new IllegalArgumentException();
        }
        MaxHeap<E> heap = new MaxHeap<>(Math.max(array.length, 1));
        System.arraycopy(array, 0, heap.array, 0, array.length);
        heap.size = array.length;

        for (int i = (heap.size - 2) / 2; i >= 0; i--) {
            heap.siftDown(i);
        }
        return heap;
    }

    public void increaseKey(int index, E newValue) {
        if (size == 0) throw new NoSuchElementException("힙이 비어있습니다.");
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        if (newValue == null) throw new IllegalArgumentException();
        if (newValue.compareTo(array[index]) < 0) throw new IllegalArgumentException("기존 값보다 작습니다.");
        array[index] = newValue;
        siftUp(index);
    }

    public E delete(int index) {
        if (size == 0) throw new NoSuchElementException("힙이 비어있습니다.");
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        E removed = array[index];
        array[index] = array[size - 1];
        array[size - 1] = null;
        size--;
        if (index < size) {
            siftDown(index);
            siftUp(index);
        }
        collapseCapacity();
        return removed;
    }

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