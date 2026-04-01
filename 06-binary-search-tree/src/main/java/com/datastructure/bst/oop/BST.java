package com.datastructure.bst.oop;

import java.util.List;

public interface BST<T extends Comparable<T>> extends Iterable<T> {

    void insert(T value);
    boolean search(T value);
    void delete(T value);
    boolean contains(T value);
    T min();
    T max();
    int size();
    boolean isEmpty();
    void clear();
    int height();
    List<T> inorder();
    List<T> preorder();
    List<T> postorder();
    List<T> levelorder();
    T floor(T value);
    T ceiling(T value);
    int rank(T value);
    T select(int k);
    T predecessor(T value);
    T successor(T value);
}