package com.datastructure.practice0222.array;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class DynamicArrayImpl<E> implements DynamicArray<E> {

    private int capacity = 10;
    private E[] elements;
    private int index = 0;

    @SuppressWarnings("unchecked")
    public DynamicArrayImpl() {this.elements = (E[]) new Object[capacity];}

    @SuppressWarnings("unchecked")
    public DynamicArrayImpl(int capacity) {
        this.capacity = capacity;
        this.elements = (E[]) new Object[capacity];
    }

    @Override
    public void add(E element) {
        growElements();
        elements[index] = element;
        index++;
    }

    @Override
    public void add(int index, E element) {
        int ableIndex = this.index + 1;
        checkIndex(index, ableIndex);
        growElements();
        System.arraycopy(elements, index, elements, index + 1, this.index - index);
        elements[index] = element;
        this.index++;
    }

    @Override
    public E get(int index){
        checkIndex(index, this.index);
        return elements[index];
    }

    @Override
    public E set(int index, E element) {
        checkIndex(index, this.index);
        E old = elements[index];
        elements[index] = element;
        return old;
    }

    @Override
    public E remove(int index) {
        checkIndex(index, this.index);
        E old = elements[index];
        System.arraycopy(elements, index + 1, elements, index, this.index - index - 1);
        this.index--;
        elements[this.index] = null;
        shrinkElements();
        return old;
    }

    @Override
    public int size() {
        return index;
    }

    @Override
    public boolean isEmpty() {
        return index == 0;
    }

    @Override
    public boolean contains(E element) {
        for (int index = 0; index < this.index; index++) {
            if(Objects.equals(elements[index],element)) return true;
        }
        return false;
    }

    @Override
    public int indexOf(E element) {
        for (int index = 0; index < this.index; index++) {
            if(Objects.equals(elements[index], element)) return index;
        }
        return -1;
    }

    @Override
    public void clear() {
        Arrays.fill(elements, 0, index, null);
        index = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = 0;
            @Override
            public boolean hasNext() {return cursor < index;}

            @Override
            public E next() {return elements[cursor++];}
        };
    }
    private void checkIndex(int index, int bound) {
        if (index < 0 || index >= bound) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void growElements() {
        if (capacity < index + 1) {
            int growCapacity = (int)(capacity * 1.5);
            capacity = growCapacity;
            elements = Arrays.copyOf(elements, growCapacity);
        }
    }

    private void shrinkElements() {
        if (this.index <= capacity/4 && capacity > 10) {
            capacity = Math.max(capacity/2, 10);
            elements = Arrays.copyOf(elements, capacity);
        }
    }
}
