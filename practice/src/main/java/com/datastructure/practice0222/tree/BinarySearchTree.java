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

}
