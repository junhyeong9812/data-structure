package com.datastructure.bst.oop;

import java.util.Iterator;
import java.util.List;

public class BinarySearchTreeImpl<T extends Comparable<T>> implements BST<T> {

    @Override
    public void insert(T value) {}
    @Override
    public boolean search(T value) { return false; }
    @Override
    public void delete(T value) {}
    @Override
    public boolean contains(T value) { return false; }
    @Override
    public T min() { return null; }
    @Override
    public T max() { return null; }
    @Override
    public int size() { return 0; }
    @Override
    public boolean isEmpty() { return true; }
    @Override
    public void clear() {}
    @Override
    public int height() { return 0; }
    @Override
    public List<T> inorder() { return null; }
    @Override
    public List<T> preorder() { return null; }
    @Override
    public List<T> postorder() { return null; }
    @Override
    public List<T> levelorder() { return null; }
    @Override
    public T floor(T value) { return null; }
    @Override
    public T ceiling(T value) { return null; }
    @Override
    public int rank(T value) { return 0; }
    @Override
    public T select(int k) { return null; }
    @Override
    public T predecessor(T value) { return null; }
    @Override
    public T successor(T value) { return null; }
    @Override
    public Iterator<T> iterator() { return null; }
}