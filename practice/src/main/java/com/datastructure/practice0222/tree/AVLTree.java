package com.datastructure.practice0222.tree;

import java.util.Objects;

/**
 * AVL 트리 (자가 균형 이진 탐색 트리)
 *
 * 시간 복잡도:
 *      - 삽입 O(log n), 탐색 O(log n), 삭제 O(log n) (항상 보장)
 *
 * 균형 인수(Balance Factor) = 왼쪽 높이 - 오른쪽 높이
 * |BF| > 1이면 회전으로 재균형
 * */
public class AVLTree<E extends Comparable<E>> extends AbstractTree<E> {

    @Override
    public void add(E element) {
    }

    @Override
    public E remove(E element) {

    }

    @Override
    public int height() {

    }

    // -- 삽입 --
    private Node<E> insert(Node<E> node, E element) {
        if (node == null) {
            size++;
            return new Node<>(element);
        }
        int cmp = element.compareTo(node.value);
        if (cmp < 0) {
            node.left = insert(node.left, element);
        } else if (cmp > 0) {
            node.right = insert(node.right, element);
        } else {
            return node; // 중복 무시
        }

        return rebalance(node);
    }

    // -- AVL 회전 --
    private Node<E> rebalance(Node<E> node) {
        updateHeight(node);
        int balance = balanceFactor(node);

        // LL (왼쪽-왼쪽) -> 우회전
        if (balance > 1 && balanceFactor(node.left) >= 0) {
            return rotateRight(node);
        }

        // LR (왼쪽-오른쪽) -> 왼쪽 자식 좌회전 후 우회전
        if (balance > 1 && balanceFactor(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // RR (오른쪽-오른쪽) -> 좌회전
        if (balance < -1 && balanceFactor(node.right) <= 0) {
            return rotateLeft(node);
        }

        // RL (오른쪽-왼쪽) -> 오른쪽 자식 우회전 후 좌회전
        if (balance < -1 && balanceFactor(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    /**
     *     y             x
     *    / \           / \
     *   x   C  →     A   y
     *  / \               / \
     * A   B             B   C
     */
    private Node<E> rotateRight(Node<E> y) {
        Node<E> x = y.left;
        Node<E> b = x.right;

        x.right = y;
        y.left = b;

        updateHeight(y);
        updateHeight(x);
        return x;
    }

    /**
     *   x               y
     *  / \             / \
     * A   y    →     x   C
     *    / \        / \
     *   B   C      A   B
     */
    private Node<E> rotateLeft(Node<E> x) {
        Node<E> y = x.right;
        Node<E> b = y.left;

        y.left = x;
        x.right = b;

        updateHeight(x);
        updateHeight(y);
        return y;
    }
    // -- 유틸리티 --
    private int nodeHeight(Node<E> node) {return node == null ? 0 : node.height; }

    private void updateHeight(Node<E> node) {
        node.height = 1 + Math.max(nodeHeight(node.left), nodeHeight(node.right));
    }

    private int balanceFactor(Node<E> node) {
        return node == null ? 0 : nodeHeight(node.left) - nodeHeight(node.right);
    }
}
