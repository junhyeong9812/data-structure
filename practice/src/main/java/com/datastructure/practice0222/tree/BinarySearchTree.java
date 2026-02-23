package com.datastructure.practice0222.tree;

import java.util.Objects;

/**
 * 이진 탐색 트리 (Binary Search Tree)
 * 시간 복잡도 :
 *      - 평균: 삽입 0(log n), 탐색 0(log n), 삭제 0(log n)
 *      - 최악 (편향): 삽입 O(n), 탐색 O(n), 삭제 O(n)
 * */
public class BinarySearchTree<E extends Comparable<E>> extends AbstractTree<E> {

    @Override
    public void add(E element) {
        Objects.requireNonNull(element);
        root = insert(root,  element);
    }

    @Override
    public E remove(E element) {
        Objects.requireNonNull(element);
        Node<E> target = findNode(root, element);
        E removed = target.value;
        root = delete(root, element);
        return removed;
    }

    // -- 재귀 삽입 --

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
        }
        //중복 무시
        return node;
    }

    // -- 재귀 삭제 --
    private Node<E> delete(Node<E> node, E element) {
        if (node == null) return null;

        int cmp = element.compareTo(node.value);
        if (cmp < 0) {
            node.left = delete(node.left, element);
        } else if (cmp > 0) {
            node.right = delete(node.right, element);
        } else {
            // 삭제 대상 노드 발견
            size--;
            // 자식이 0개 또는 1개
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            // 자식이 2개: 후속자(오른쪽 서브트리 최솟값)로 대체
            Node<E> successor = findMin(node.right);
            node.value = successor.value;
            size++; // delete에서 다시 감소하므로 보정
            node.right = delete(node.right, successor.value);
        }
        return node;
    }
}
