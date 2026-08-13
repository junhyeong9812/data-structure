package com.datastructure.practice0222.stack;

public interface Stack<E> {

    public void push(E element);

    public E pop();

    public E peek();

    public E top();

    public boolean isEmpty();

    public int size();

    public void clear();

    public int search(E element);

    public Object[] toArray();
}
