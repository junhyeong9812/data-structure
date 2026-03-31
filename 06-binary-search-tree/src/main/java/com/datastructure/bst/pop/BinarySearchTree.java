package com.datastructure.bst.pop;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class BinarySearchTree<T extends Comparable<T>> {

    private static class TreeNode<T> {
        T value;
        TreeNode<T> left;
        TreeNode<T> right;

        public TreeNode(T element) {
            this.value = element;
            this.left = null;
            this.right = null;
        }
    }
    private TreeNode<T> root;
    private int size = 0;

    public void insert(T value) {
        if (value == null) throw new IllegalArgumentException();
        root = insertNode(root, value);
    }

    public boolean search(T value) {
        return searchNode(root, value)!=null;
    }

    public void delete(T value) {
        root = deleteNode(root, value);
    }

    public boolean contains(T value) {
        return search(value);
    }

    public T min() {
        if (root == null) throw new IllegalStateException("빈 트리에는 사용할 수 없습니다.");
        return findMin(root).value;
    }

    public T max() {
        if (root == null) throw new IllegalStateException("빈 트리에는 사용할 수 없습니다.");
        return findMax(root).value;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public void clear() {
        root = null;
        size = 0;
    }

    public int height() {
        return getHeight(root);
    }

    public List<T> inorder() {
        List<T> result = new ArrayList<>();
        getInOrder(root, result);
        return result;
    }

    public List<T> preorder() {
        List<T> result = new ArrayList<>();
        getPreOrder(root, result);
        return result;
    }

    public List<T> postorder() {
        List<T> result = new ArrayList<>();
        getPostOrder(root, result);
        return null;
    }
    public List<T> levelorder() {
        Deque<TreeNode<T>> deque = new LinkedList<>();
        List<T> result = new ArrayList<>();
        if (root == null) return result;
        deque.add(root);
        while(!deque.isEmpty()) {
            TreeNode<T> node = deque.pop();
            result.add(node.value);
            if (node.left != null) {deque.add(node.left);}
            if (node.right != null) {deque.add(node.right);}

        }
        return result;
    }

    public T floor(T value) { return null; }
    public T ceiling(T value) { return null; }
    public int rank(T value) { return 0; }
    public T select(int k) { return null; }
    public T predecessor(T value) { return null; }
    public T successor(T value) { return null; }

    private TreeNode<T> insertNode(TreeNode<T> node, T value) {
        if (node == null) {
            size++;
            return new TreeNode<>(value);
        }
        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = insertNode(node.left, value);
        } else if (cmp > 0) {
            node.right = insertNode(node.right, value);
        }
        return node;
    }

    private TreeNode<T> searchNode(TreeNode<T> node, T value) {
        if (node == null) return null;
        int cmp = value.compareTo(node.value);
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return searchNode(node.left, value);
        } else {
            return searchNode(node.right, value);
        }
    }

    private TreeNode<T> deleteNode(TreeNode<T> node, T value) {
        if (node == null) throw new IllegalArgumentException();
        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = deleteNode(node.left, value);
        } else if (cmp > 0) {
            node.right = deleteNode(node.right, value);
        } else {
            // Case 1: 리프 노드
            if (node.left == null && node.right == null) {
                size--;
                return null;
            }

            // Case 2: 자식 하나
            if (node.left == null) {
                size--;
                return node.right;
            }
            if (node.right == null) {
                size--;
                return node.left;
            }

            // Case 3: 자식 둘 - 오른쪽 서브 트리의 최솟값으로 대체
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

    private int getHeight(TreeNode<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    private void getInOrder(TreeNode<T> node, List<T> inorders) {
        if (node == null) return;
        if (node.left != null) {
            getInOrder(node.left, inorders);
        }

        inorders.add(node.value);

        if (node.right != null) {
            getInOrder(node.right, inorders);
        }
    }

    private void getPreOrder(TreeNode<T> node, List<T> preorder) {
        if (node == null) return;
        preorder.add(node.value);
        if (node.left != null) {
            getPreOrder(node.left, preorder);
        }
        if (node.right != null) {
            getPreOrder(node.right, preorder);
        }
    }

    private void getPostOrder(TreeNode<T> node, List<T> postorder) {
        if (node == null) return;
        if (node.left != null) {
            getPostOrder(node.left, postorder);
        }
        if (node.right != null) {
            getPostOrder(node.right, postorder);
        }
        postorder.add(node.value);
    }
}