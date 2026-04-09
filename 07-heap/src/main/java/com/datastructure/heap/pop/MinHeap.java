package com.datastructure.heap.pop;

public class MinHeap<E extends Comparable<E>> {

    public MinHeap() {}
    public MinHeap(int initialCapacity) {}

    public void insert(E value) {}
    public boolean offer(E value) { return false; }

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
}