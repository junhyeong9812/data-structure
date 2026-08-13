package com.datastructure.practice0222.tree;

import java.util.*;

public abstract class AbstractTree<E extends Comparable<E>> implements Tree<E> {

    /**
     * protected로 하는 이유
     * private으로 하게 되면 하위 클래스에서 Node에 접근하지 못해 트리를 구현할 수 없다.
     * public으로 하면 외부에서 내부 구조가 노출되어 불필요
     * protected는 하위 클래스에서만 접근 가능하게 해서 하위 구현에서 사용 가능하되
     * 외부에서는 접근을 못하도록 protected를 사용
     */
    protected static class Node<E> {
        E value;
        Node<E> left;
        Node<E> right;
        int height; //AVL에서 사용

        Node(E value) {
            this.value = value;
            this.height = 1;
        }
    }

    protected Node<E> root;
    protected int size;

    @Override
    public int size() { return size; }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public boolean contains(E element) {
        Objects.requireNonNull(element);
        // requireNoneNull은 element가 null이면 NullPointException을 던지고
        // 아니면 그대로 통과시킨다.
        return findNode(root, element) != null;
    }

    @Override
    public E min() {
        if (isEmpty()) throw new NoSuchElementException("트리가 비어있습니다.");
        return findMin(root).value;
    }

    @Override
    public E max() {
        if (isEmpty()) throw new NoSuchElementException("트리가 비어있습니다.");
        return findMax(root).value;
    }

    @Override
    public int height() { return computeHeight(root); }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public List<E> inOrder() {
        List<E> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    @Override
    public List<E> preOrder() {
        List<E> result = new ArrayList<>();
        preOrder(root, result);
        return result;
    }

    @Override
    public List<E> postOrder() {
        List<E> result = new ArrayList<>();
        postOrder(root, result);
        return result;
    }

    @Override
    public List<E> levelOrder() {
        List<E> result = new ArrayList<>();
        if (root == null) return result;
        Queue<Node<E>> queue = new LinkedList<>();
        queue.offer(root);
        while(!queue.isEmpty()) {
            Node<E> node = queue.poll();
            result.add(node.value);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        return result;
    }

    @Override
    public Iterator<E> iterator() {
        // 기본적으로 중위 순회 (정렬 순서)
        return inOrder().iterator();
    }
    // --내부 유틸리티--

    protected Node<E> findNode(Node<E> node, E element) {
        if (node == null) return null;
        int cmp = element.compareTo(node.value);
        /**
         * 여기서 compareTo의 규약을 보면
         * 음수 : element가 node value보다작음
         * 0: 같음
         * 양수: element가 node.value보다 큼
         * 이때 integer같은 경우 실제로 뺄셈 결과가 나오기도 하지만, String이나 커스텀 객체는
         * 뺄셈이 아닌 다른 방향으로 비교한다.
         * 기본적으로 트리는 중복을 허용하지 않고 중복일 경우 내부 count나 배열을 둬서 관리한다.
         * */
        if (cmp < 0) return findNode(node.left, element);
        if (cmp > 0) return findNode(node.right, element);
        return node;
    }


    /**
     * BST에서 가장 왼쪽 노드가 곧 최솟값, 가장 오른쪽 노드가 곧 최댓값이기 때문에 아래와 같은
     * findMin/findMax라는 값을 사용한다.
     * BST의 규칙이 "왼쪽<부모<오른쪽"이니깐, 왼쪽으로 더 이상 못 갈때까지 내려가면
     * 그게 트리 전체에서 가장 작은 값이 되는거며, 그래서 가장 왼쪽 노르를 찾는다와 같은 의미이고
     * 똑같은 논리입니다. "왼쪽 < 부모 < 오른쪽"이니까 오른쪽으로 더 이상 못 갈 때까지 내려가면 그게 트리 전체에서 가장 큰 값이라 findMax
     * */
    protected Node<E> findMin(Node<E> node) {
        while(node.left != null) node = node.left;
        return node;
    }

    protected Node<E> findMax(Node<E> node) {
        while(node.right != null) node = node.right;
        return node;
    }

    protected int computeHeight(Node<E> node) {
        if (node == null) return 0;
        return 1 + Math.max(computeHeight(node.left), computeHeight(node.right));
    }

    private void inOrder(Node<E> node, List<E> result) {
        if (node == null) return;
        inOrder(node.left,result);
        result.add(node.value);
        inOrder(node.right, result);
    }

    private void preOrder(Node<E> node, List<E> result) {
        if (node == null) return;
        result.add(node.value);
        preOrder(node.left, result);
        preOrder(node.right, result);
    }

    private void postOrder(Node<E> node, List<E> result) {
        if (node == null) return;
        postOrder(node.left, result);
        postOrder(node.right, result);
        result.add(node.value);
    }
}
