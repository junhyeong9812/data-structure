package com.datastructure.practice0222.tree;

import java.util.List;

public interface Tree<E extends Comparable<E>> extends Iterable<E> {
    /**
     * Comparable<E>를 해야되는 이유는
     * CompareTo() 메서드를 통해 하위 엘리멘트가 추가될 때 비교 가능하도록 하기 위함이야.
     * 트리 구조를 유지하기 위해서는 비교하여 크고 작음을 왼쪽/ 오른쪽으로 배치 해야되기 때문에 필요하다.
     * */
    public void add (E elemente);

    public boolean contains(E element);

    public E remove(E element);

    public E min();

    public E max();

    public int size();

    public boolean isEmpty();

    public int height();

    public void clear();

    // 순회 방식
    public List<E> inOrder();
    public List<E> preOrder();
    public List<E> postOrder();
    public List<E> levelOrder();
}
