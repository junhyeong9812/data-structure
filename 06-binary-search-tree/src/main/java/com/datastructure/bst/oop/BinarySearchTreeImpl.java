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

    @Override
    public boolean search(T value) {
        return getValue(root, value) != null;
    }

    @Override
    public void delete(T value) {
        root = deleteNode(root, value);
    }

    @Override
    public boolean contains(T value) { return false; }
    @Override
    public T min() { return findMin(root).value; }
    @Override
    public T max() { return findMax(root).value; }

    @Override
    public int size() { return size; }
    @Override
    public boolean isEmpty() { return size==0; }
    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

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


    private TreeNode<T> getValue(TreeNode<T> node, T value) {
        if (node == null) return null;
        if (value == null) throw new IllegalArgumentException("null은 비교할 수 없습니다.");
        int cmp = value.compareTo(node.value);
        if (cmp == 0) { return node; }
        if (cmp > 0) { return getValue(node.right, value);}
        if (cmp < 0) { return getValue(node.left, value);}
        return null;
    }

    private TreeNode<T> deleteNode(TreeNode<T> node, T value) {
        if (node == null) throw new IllegalArgumentException();
        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = deleteNode(node.left, value);
        } else if (cmp > 0) {
            node.right = deleteNode(node.right, value);
        } else {
            if (node.left == null && node.right == null) {
                size--;
                return null;
            }

            if (node.left == null) {
                size--;
                return node.right;
            }
            if (node.right == null) {
                size--;
                return node.left;
            }

            TreeNode<T> successor = findMin(node.right);
            node.value = successor.value;
            node.right = deleteNode(node.right, successor.value);
        }
        return node;
    }

    private TreeNode<T> findMin(TreeNode<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private TreeNode<T> findMax(TreeNode<T> node) {
        while (node.right != null) {
            node = node.left;
        }
        return node;
    }
}