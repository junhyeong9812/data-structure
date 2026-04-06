package com.datastructure.bst.oop;

import java.util.*;

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
    public boolean contains(T value) { return search(value); }
    @Override
    public T min() {
        if (root == null) throw new IllegalStateException("빈 트리에는 사용할 수 없습니다.");
        return findMin(root).value;
    }
    @Override
    public T max() {
        if (root == null) throw new IllegalStateException("빈 트리에는 사용할 수 없습니다.");
        return findMax(root).value;
    }

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
    public int height() { return getHeight(root); }

    private int getHeight(TreeNode<T> node) {
        if (node == null) return 0;
        if (node.left != null && node.right != null) {
            return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
        }
        if (node.left != null) {
            return getHeight(node.left) + 1;
        }
        if (node.right != null) {
            return getHeight(node.right) + 1;
        }
        return 1;
    }

    @Override
    public List<T> inorder() {
        List<T> result = new ArrayList<>();
        getInOrder(root, result);
        return result; }

    private void getInOrder(TreeNode<T> node, List<T> result) {
        if (node == null) return;
        if (node.left != null) {
            getInOrder(node.left, result);
        }
        result.add(node.value);
        if (node.right != null) {
            getInOrder(node.right, result);
        }
    }

    @Override
    public List<T> preorder() {
        List<T> result = new ArrayList<>();
        getPreOrder(root, result);
        return result;
    }

    private void getPreOrder(TreeNode<T> node, List<T> result) {
        if (node == null) return;
        result.add(node.value);
        if (node.left != null) {
            getInOrder(node.left, result);
        }
        if (node.right != null) {
            getInOrder(node.right, result);
        }
    }
    @Override
    public List<T> postorder() {
        List<T> result = new ArrayList<>();
        getPostOrder(root, result);
        return result; }

    private void getPostOrder(TreeNode<T> node, List<T> result) {
        if (node == null) return;
        if (node.left != null) {
            getPostOrder(node.left, result);
        }
        if (node.right != null) {
            getPostOrder(node.right, result);
        }
        result.add(node.value);
    }
    @Override
    public List<T> levelorder() {
        Deque<TreeNode<T>> nodes = new LinkedList<TreeNode<T>>();
        List<T> result = new ArrayList<>();
        if (root == null) return result;
        nodes.add(root);
        while (!nodes.isEmpty()) {
            TreeNode<T> node = nodes.pop();
            result.add(node.value);
            if (node.left != null) {
                nodes.add(node.left);
            }
            if (node.right != null) {
                nodes.add(node.right);
            }
        }
        return result;
    }
    @Override
    public T floor(T value) {
        return findFloor(root, value);
    }

    private T findFloor(TreeNode<T> node, T value) {
        if (value == null) return null;
        if (node == null) return null;
        int cmp = value.compareTo(node.value);
        if (cmp == 0) return node.value;
        if (cmp < 0) return findFloor(node.left, value);
        T right = findFloor(node.right, value);
        if (right != null) return right;
        return node.value;
    }

    @Override
    public T ceiling(T value) { return findCeiling(root, value); }

    private T findCeiling(TreeNode<T> node, T value) {
        if (node == null) return null;
        int cmp = value.compareTo(node.value);
        if (cmp == 0) return node.value;
        if (cmp > 0) return findCeiling(node.right, value);
        T left = findCeiling(node.left, value);
        if (left != null) return left;
        return node.value;
    }

    @Override
    public int rank(T value) { return calculateRank(root, value); }

    private int calculateRank(TreeNode<T> node, T value) {
        if (node == null) return 0;
        int cmp = value.compareTo(node.value);
        if (cmp > 0) {
            return calculateRank(node.left, value) + calculateRank(node.right, value) + 1;
        }
        if (cmp == 0) {
            return calculateRank(node.left, value);
        }
        if (cmp < 0) {
            return calculateRank(node.left, value);
        }
        return 0;
    }

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
            node = node.right;
        }
        return node;
    }
}