package com.datastructure.dynamicarray.pop;

import java.util.Arrays;

public class DynamicArray {

    private int capacity = 10;
    private Object[] elements = new Object[capacity];
    private int index = 0;

    public DynamicArray() {

    }

    public DynamicArray(int capacity) {
        this.capacity = capacity;
        this.elements = new Object[capacity];
    }

    public void add(Object element) {
        if (this.index + 1 > capacity) {
            capacity = (int)(capacity * 1.5);
            elements = Arrays.copyOf(elements, capacity);
        }
        elements[index] = element;
        index++;
    }

    public void add(int index, Object element) {
        if (index < 0 || index > this.index) {
            throw new IndexOutOfBoundsException();
        }
        if (this.index + 1 > capacity) {
            capacity = (int)(capacity * 1.5);
            elements = Arrays.copyOf(elements, capacity);
        }
        System.arraycopy(elements, index, elements, index+1, this.index - index);
        elements[index] = element;
        this.index++;
    }

    public Object get(int index) {
        return elements[index];
    }

    public void set(int index, Object element) {
        if (elements[index] != null) {
        }
        elements[index] = element;
    }

    public void remove() {

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
}
