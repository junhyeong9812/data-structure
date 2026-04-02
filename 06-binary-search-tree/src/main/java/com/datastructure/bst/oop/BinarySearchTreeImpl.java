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

        public T getValue() {return value;}

        public TreeNode<T> getLeft() {return left;}

        public TreeNode<T> getRight() {return right;}
    }

    private TreeNode<T> root;
    private int size = 0;

    @Override
    public void insert(T value) {
        if (value == null) throw new IllegalArgumentException("null은 넣을 수 없습니다.");
        if (root == null) {
            root = new TreeNode<T>(value);
            size++;
        }
        else setNode(root, value);
    }

    private void setNode(TreeNode<T> node, T value) {
        int cmp = value.compareTo(node.getValue());
        if (cmp < 0) {
            if(node.getLeft() == null) {
                node.setLeft(new TreeNode(value));
                size++;
                return;
            }
            setNode(node.left, value);
        }
        if (cmp > 0) {
            if(node.getRight() == null) {
                node.setRight(new TreeNode(value));
                size++;
                return;
            }
            setNode(node.right, value);
        }
    }

    @Override
    public boolean search(T value) {
        return getValue(root, value);
    }

    private boolean getValue(TreeNode<T> node, T value) {
        if (node == null) return false;
        if (value == null) throw new IllegalArgumentException("null은 비교할 수 없습니다.");
        int cmp = value.compareTo(node.value);
        if (cmp == 0) { return true; }
        if (cmp > 0) { return getValue(node.right, value);}
        if (cmp < 0) { return getValue(node.left, value);}
        return false;
    }

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