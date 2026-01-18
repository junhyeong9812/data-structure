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
        for (int i = this.index; i > 0; i--) {
            elements[i] = elements[i - 1];
        }
        elements[index] = element;
        index++;
    }

    public void add(int index, Object element) {
        if (this.index + 1 > capacity) {
            capacity = (int)(capacity * 1.5);
            elements = Arrays.copyOf(elements, capacity);
        }
        for (int i = this.index; i > index ; i--) {
            elements[i] = elements[i - 1];
        }
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
