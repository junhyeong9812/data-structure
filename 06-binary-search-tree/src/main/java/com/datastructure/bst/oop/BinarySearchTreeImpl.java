package com.datastructure.bst.oop;

import java.util.Iterator;
import java.util.List;

public class BinarySearchTreeImpl<T extends Comparable<T>> implements BST<T> {

    private class TreeNode<T> {
        private T value;
        private TreeNode<T> left;
        private TreeNode<T> right;

        public TreeNode(T value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }

        public void setLeft(TreeNode<T> left) {
            this.left = left;
        }

        public void setRight(TreeNode<T> right) {
            this.right = right;
        }

        public TreeNode<T> getLeft() {return left;}

        public TreeNode<T> getRight() {return right;}
    }

    private TreeNode<T> root;
    private int size = 0;

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