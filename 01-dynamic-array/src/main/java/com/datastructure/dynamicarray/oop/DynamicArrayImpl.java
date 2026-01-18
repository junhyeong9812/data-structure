package com.datastructure.dynamicarray.oop;

import java.util.Arrays;
import java.util.Objects;

public class DynamicArrayImpl<E> implements DynamicArray<E> {

    private int capacity = 10;
    private E[] elements;
    private int index = 0;

    @SuppressWarnings("unchecked")
    public DynamicArrayImpl() {
        this.elements = (E[]) new Object[capacity];
    }

    @SuppressWarnings("unchecked")
    public DynamicArrayImpl(int capacity) {
        this.capacity = capacity;
        this.elements = (E[]) new Object[capacity];
    }

    @Override
    public void add(E element){
        extendElements();
        elements[index] = element;
        index++;
    }

    @Override
    public void add(int index, E element){
        checkIndex(index, this.index + 1);
        extendElements();
        System.arraycopy(elements, index,elements, index + 1, this.index - index);
        elements[index] = element;
        this.index++;
    }

    @Override
    public E get(int index){
        checkIndex(index, this.index);
        return elements[index];
    }

    @Override
    public E set(int index, E element){
        checkIndex(index, this.index);
        E old = elements[index];
        elements[index] = element;
        return old;
    }

    @Override
    public E remove(int index){
        checkIndex(index, this.index);
        E old = elements[index];
        System.arraycopy(elements, index + 1, elements, index, this.index - index -1);
        this.index--;
        elements[this.index] = null;
        if (this.index - 1 <= capacity/4 && capacity > 10) {
            capacity = Math.max(capacity/2, 10);
            elements = Arrays.copyOf(elements, capacity);
        }

        return old;
    }

    @Override
    public int size(){
        return index;
    }

    @Override
    public boolean isEmpty(){
        return index == 0;
    }

    @Override
    public boolean contains(E element){
        return true;
    }

    @Override
    public int indexOf(E element){
        for (int index = 0; index < this.index; index++) {
            if(Objects.equals(elements[index],element)) return index;
        }
        return -1;
    }

    @Override
    public void clear() {
        Arrays.fill(elements, 0, index, null);
        index = 0;
    }

    private void checkIndex(int index, int bound) {
        if (index < 0 || index >= bound) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void extendElements() {
        if (capacity < index + 1) {
            int extendCapacity = (int)(capacity * 1.5);
            capacity = extendCapacity;
            elements = Arrays.copyOf(elements, extendCapacity);
        }
    }
}
