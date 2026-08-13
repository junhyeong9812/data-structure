package com.datastructure.practice0222.linkedlist;

public interface LinkedList<E> {

    public void addFirst(E element);

    public void addLast(E element);

    public void add(int index, E element);

    public E removeFirst();

    public E removeLast();

    public E remove(int index);

    public E get(int index);

    public E set(int index, E element);

    public int size();

    public boolean isEmpty();

    public boolean contains(E element);

    public int indexOf(E element);

    public void clear();

    public void reverse();
}
