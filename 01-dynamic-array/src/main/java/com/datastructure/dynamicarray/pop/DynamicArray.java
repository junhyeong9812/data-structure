package com.datastructure.dynamicarray.pop;

import java.util.Arrays;

public class DynamicArray<T> {

    private int capacity = 10;
    private T[] elements;
    private int index = 0;

    @SuppressWarnings("unchecked")
    public DynamicArray() {
        this.elements = (T[]) new Object[capacity];
    }

    @SuppressWarnings("unchecked")
    public DynamicArray(int capacity) {
        this.capacity = capacity;
        this.elements = (T[]) new Object[capacity];
    }

    public void add(T element) {
        if (this.index + 1 > capacity) {
            capacity = (int)(capacity * 1.5);
            elements = Arrays.copyOf(elements, capacity);
        }
        elements[index] = element;
        index++;
    }

    public void add(int index, T element) {
        checkIndex(index, this.index + 1);
        if (this.index + 1 > capacity) {
            capacity = (int)(capacity * 1.5);
            elements = Arrays.copyOf(elements, capacity);
        }
        System.arraycopy(elements, index, elements, index+1, this.index - index);
        elements[index] = element;
        this.index++;
    }

    public T get(int index) {
        checkIndex(index, this.index);
        return elements[index];
    }

    public T set(int index, T element) {
        checkIndex(index, this.index);
        T old = elements[index];
        elements[index] = element;
        return old;
    }

    public T remove(int index) {
        checkIndex(index, this.index);
        T old= elements[index];
        System.arraycopy(elements, index + 1, elements, index, this.index - index - 1);
        this.index--;
        return old;
    }

    public int size() {
        return index;
    }

    public boolean isEmpty() {
        return index == 0;
    }

    public void contains() {

    }

    public void clear() {

    }

    private void extendCapacity() {

    }

    private void collapseCapacity() {

    }

    private void checkIndex(int index, int bound) {
        if (index < 0 || index >= bound) {
            throw new IndexOutOfBoundsException();
        }
    }
}
