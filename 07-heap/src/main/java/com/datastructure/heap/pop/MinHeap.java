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

    public E poll() {
        if (size == 0) return null;
        return removeRoot();
    }

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

    public E getMin() {
        if (size == 0) throw new NoSuchElementException("힙이 비어있습니다.");
        return array[0];
    }

    public E peek() {
        if (size == 0) return null;
        return array[0];
    }

    public int size() { return size; }
    public boolean isEmpty() { return size==0; }
    public void clear() {
        this.array = (E[]) new Comparable[10];
        this.size = 0;
    }

    public static <E extends Comparable<E>> MinHeap<E> heapify(E[] array) {
        if (array == null) throw new IllegalArgumentException();
        for (E e: array) {
            if (e == null) throw new IllegalArgumentException();
        }
        MinHeap<E> heap = new MinHeap<>(Math.max(array.length, 1));
        System.arraycopy(array, 0, heap.array, 0, array.length);
        heap.size = array.length;

        for (int i = (heap.size - 2)/2; i >= 0; i--) {
            heap.siftDown(i);
        }
        return heap;
    }

    public void decreaseKey(int index, E newValue) {
        if (size == 0) throw new NoSuchElementException("힙이 비어있습니다.");
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        if (newValue == null) throw new IllegalArgumentException();
        if (newValue.compareTo(array[index]) > 0) throw new IllegalArgumentException("기존 값보다 큽니다.");
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

    public MinHeap<E> merge(MinHeap<E> other) {
        if (other == null) throw new IllegalArgumentException();
        E[] merged = (E[]) new Comparable[this.size + other.size];
        System.arraycopy(this.array, 0, merged, 0, this.size);
        System.arraycopy(other.array, 0, merged, this.size, other.size);
        return heapify(merged);
    }

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