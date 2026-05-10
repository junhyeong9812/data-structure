package com.datastructure.heap.oop;


import java.util.Arrays;
import java.util.Comparator;

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

    private void growCapacity() {
        if (size >= array.length) {
            array = Arrays.copyOf(array, (int)(array.length * 1.5));
        }
    }
}
