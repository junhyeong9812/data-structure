package com.datastructure.heap.oop;


import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class BinaryHeap<E> implements Heap<E>, PriorityQueue<E> {
    private E[] array;
    private int size;
    private final Comparator<? super E> comparator;

    @SuppressWarnings("unchecked")
    public BinaryHeap(Comparator<? super E> comparator) {
        this.array = (E[]) new Object[10];
        this.size = 0;
        this.comparator = comparator;
    }

    public static <E extends Comparable<E>> BinaryHeap<E> maxHeap() {
        return new BinaryHeap<>(Comparator.reverseOrder());
    }

    public static <E extends Comparable<E>> BinaryHeap<E> minHeap() {
        return new BinaryHeap<>(Comparator.naturalOrder());
    }

    @Override
    public void insert(E value) {
        if (value == null) throw new IllegalArgumentException();
        growCapacity();
        array[size] = value;
        siftUp(size);
        size++;
    }

    @Override
    public boolean offer(E value) {
        if (value == null) return false;
        insert(value);
        return true;
    }

    @Override
    public E extract() {
        if (size == 0) throw new NoSuchElementException();
        return removeRoot();
    }

    @Override
    public E poll() {
        if (size == 0) return null;
        return removeRoot();
    }

    private E removeRoot() {
        E root = array[0];
        array[0] = array[size - 1];
        array[size - 1] =null;
        size--;
        if (size > 0) siftDown(0);
        collapseCapacity();
        return root;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (comparator.compare(array[index], array[parent]) < 0) {
                E tmp = array[index];
                array[index] = array[parent];
                array[parent] = tmp;
                index = parent;
            } else {
                break;
            }
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2* index + 2;
            int target = index;

            if (left < size && comparator.compare(array[left], array[target]) < 0) target = left;
            if (right < size && comparator.compare(array[right], array[target]) < 0) target = right;
            if (target == index) break;

            E tmp = array[index];
            array[index] = array[target];
            array[target] = tmp;
            index = target;
        }
    }

    private void growCapacity() {
        if (size >= array.length) {
            array = Arrays.copyOf(array, (int)(array.length * 1.5));
        }
    }

    private void collapseCapacity() {
        int capacity = array.length;
        if (size <= capacity / 4 && capacity > 10) {
            array = Arrays.copyOf(array, Math.max(capacity/2, 10));
        }
    }
}
