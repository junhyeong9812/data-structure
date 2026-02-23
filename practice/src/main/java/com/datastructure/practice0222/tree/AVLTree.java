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
}
