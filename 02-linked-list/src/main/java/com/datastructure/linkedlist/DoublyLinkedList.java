package com.datastructure.linkedlist;

/**
 * 양방향 연결 리스트.
 *
 * 노드가 앞뒤를 다 가리킨다. 그 참조 하나가 무엇을 바꾸는지 SinglyLinkedList 와 비교하며 보라.
 *   removeLast 가 O(1) 이 된다. tail 의 앞 노드를 바로 알 수 있기 때문이다.
 *   뒤쪽 인덱스 접근이 평균 절반으로 줄어든다. 양쪽에서 접근할 수 있기 때문이다.
 * 대신 노드마다 참조를 하나 더 들고 있어야 하고, 링크를 고칠 때 손볼 곳이 두 배다.
 *
 * 01번 동적 배열과 같은 일을 하는데 저장 방식이 정반대다.
 * 배열은 원소를 메모리에 연속으로 붙여 두고, 이건 원소마다 노드를 만들어 서로를 가리키게 한다.
 *
 * 그 차이가 복잡도를 뒤집는다.
 *   배열: 인덱스 접근 O(1), 중간 삽입/삭제 O(n) (밀거나 당겨야 하므로)
 *   연결: 인덱스 접근 O(n) (세면서 가야 하므로), 노드를 이미 알면 삽입/삭제 O(1)
 *
 * 그래서 이 자료구조의 함정은 배열과 반대다.
 * 배열에서는 remove 반복이 느렸는데, 여기서는 get(i) 반복이 느리다.
 *
 * 지켜야 할 계약
 *   - head.prev 는 항상 null, tail.next 는 항상 null.
 *   - 앞에서 세나 뒤에서 세나 원소 개수는 size 와 같고, 순서는 서로의 역순이다.
 *     (테스트가 이걸 직접 검사한다. prev 링크만 틀린 버그는 앞으로만 훑는 테스트로는 안 잡힌다.)
 *   - 떼어낸 노드의 prev/next 는 끊는다. 안 그러면 지운 노드가 리스트 전체를 붙잡는다.
 *
 * 채워져 있는 것과 비어 있는 것
 *   자명한 메서드는 미리 채워두었다. TODO 가 붙은 것이 이 문제의 본체다.
 *
 * 참고: 필드 이름 head, tail, size 와 Node 의 item, prev, next 는 테스트가 직접 들여다본다.
 *       바꾸면 테스트가 깨진다. README 의 "테스트가 내부를 본다" 참고.
 */
public class DoublyLinkedList<E> implements List<E> {

    static class Node<E> {
        E item;
        Node<E> prev;
        Node<E> next;

        Node(Node<E> prev, E item, Node<E> next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    // head/tail 과 unlink 는 private 이 아니라 패키지 공개다.
    // LinkedListProblems 가 노드를 직접 다뤄야 풀리는 문제들이 있기 때문이다.
    // (인덱스만으로 풀면 전부 O(n^2) 이 된다. 그게 이 문제집의 함정이다.)
    // 테스트도 이 필드들을 직접 보므로 이름은 계약의 일부다.
    Node<E> head;
    Node<E> tail;
    private int size;

    // ------------------------------------------------------------------
    // 채워져 있는 부분
    // ------------------------------------------------------------------

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public void addFirst(E element) {
        linkFirst(element);
    }

    public void addLast(E element) {
        linkLast(element);
    }

    /** 맨 뒤 추가. 배열과 달리 확장 복사가 없어 언제나 O(1) 이다. */
    @Override
    public void add(E element) {
        linkLast(element);
    }

    public E getFirst() {
        if (head == null) throw new java.util.NoSuchElementException("비어 있다");
        return head.item;
    }

    public E getLast() {
        if (tail == null) throw new java.util.NoSuchElementException("비어 있다");
        return tail.item;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("인덱스 " + index + ", 크기 " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("인덱스 " + index + ", 크기 " + size);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<E> n = head; n != null; n = n.next) {
            if (n != head) sb.append(", ");
            sb.append(n.item);
        }
        return sb.append(']').toString();
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 맨 앞에 노드를 붙인다.
     *
     * 생각할 것
     *   - 리스트가 비어 있을 때와 아닐 때 손봐야 할 링크가 다르다.
     *   - 비어 있었다면 이 노드가 head 이자 tail 이다.
     *
     * TODO(01): 구현하라.
     */
    private void linkFirst(E element) {
        throw new UnsupportedOperationException("TODO(01): linkFirst");
    }

    /**
     * 맨 뒤에 노드를 붙인다. linkFirst 의 대칭이다.
     *
     * TODO(02): 구현하라.
     */
    private void linkLast(E element) {
        throw new UnsupportedOperationException("TODO(02): linkLast");
    }

    /**
     * 지정한 노드 "앞"에 새 노드를 끼운다.
     *
     * 생각할 것
     *   - 고쳐야 할 링크가 몇 개인가? 순서를 잘못 잡으면 아직 필요한 참조를 먼저 잃는다.
     *   - succ 가 head 였다면 head 도 바뀐다.
     *
     * TODO(03): 구현하라.
     */
    private void linkBefore(E element, Node<E> succ) {
        throw new UnsupportedOperationException("TODO(03): linkBefore");
    }

    /**
     * 노드를 리스트에서 떼어내고 그 값을 반환한다.
     *
     * 생각할 것
     *   - 앞뒤 이웃을 서로 잇는다. 양 끝 노드였다면 head 나 tail 도 바뀐다.
     *   - 떼어낸 노드의 prev/next/item 을 그대로 두면 그 노드가 리스트 전체를 계속 붙잡는다.
     *
     * TODO(04): 구현하라.
     */
    E unlink(Node<E> node) {
        throw new UnsupportedOperationException("TODO(04): unlink");
    }

    /**
     * index 번째 노드를 찾는다.
     *
     * 생각할 것
     *   - 배열과 달리 계산으로 갈 수 없다. 세면서 가야 한다. 그래서 O(n) 이다.
     *   - 양방향이라는 성질을 쓰면 평균 이동 거리를 절반으로 줄일 수 있다.
     *     찾는 위치가 뒤쪽이면 어디서 출발하는 게 나은가?
     *
     * TODO(05): 구현하라.
     */
    private Node<E> node(int index) {
        throw new UnsupportedOperationException("TODO(05): node");
    }

    /**
     * index 위치에 끼워 넣는다. index == size 면 맨 뒤 추가와 같다.
     *
     * TODO(06): 구현하라.
     */
    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("TODO(06): add(index, element)");
    }

    /**
     * index 번째 값. 배열에서는 O(1) 이었지만 여기서는 O(n) 이다.
     *
     * TODO(07): 구현하라.
     */
    @Override
    public E get(int index) {
        throw new UnsupportedOperationException("TODO(07): get");
    }

    /**
     * index 번째 값을 바꾸고 이전 값을 반환한다.
     *
     * TODO(08): 구현하라.
     */
    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException("TODO(08): set");
    }

    /**
     * index 번째를 지우고 그 값을 반환한다.
     *
     * TODO(09): 구현하라.
     */
    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("TODO(09): remove(index)");
    }

    /**
     * 값이 같은 첫 원소를 지운다. 지웠으면 true.
     *
     * 생각할 것
     *   - 여기서는 인덱스를 거칠 필요가 없다. 훑다가 찾은 노드를 바로 떼면 된다.
     *   - null 도 담길 수 있다.
     *
     * TODO(10): 구현하라.
     */
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("TODO(10): remove(Object)");
    }

    /**
     * 맨 앞을 지우고 값을 반환한다. 비어 있으면 NoSuchElementException.
     * 배열에서는 O(n) 이었다. 여기서는?
     *
     * TODO(11): 구현하라.
     */
    public E removeFirst() {
        throw new UnsupportedOperationException("TODO(11): removeFirst");
    }

    /**
     * 맨 뒤를 지우고 값을 반환한다. 비어 있으면 NoSuchElementException.
     *
     * TODO(12): 구현하라.
     */
    public E removeLast() {
        throw new UnsupportedOperationException("TODO(12): removeLast");
    }

    /**
     * 값이 같은 첫 원소의 인덱스. 없으면 -1. null 도 찾을 수 있어야 한다.
     *
     * TODO(13): 구현하라.
     */
    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("TODO(13): indexOf");
    }

    /**
     * 모두 비운다.
     *
     * 생각할 것
     *   - head 와 tail 만 null 로 만들어도 리스트는 비어 보인다. 그걸로 충분한가?
     *     노드들끼리 서로를 가리키고 있으면 어떻게 되는가?
     *
     * TODO(14): 구현하라.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("TODO(14): clear");
    }

    /**
     * 앞에서부터 순서대로 담은 새 배열.
     *
     * TODO(15): 구현하라.
     */
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("TODO(15): toArray");
    }

    /**
     * 제자리 뒤집기.
     *
     * 생각할 것
     *   - 값을 옮기지 않는다. 각 노드의 prev 와 next 를 맞바꾸면 방향이 뒤집힌다.
     *   - 링크를 바꾸고 나면 다음 노드로 갈 길을 잃는다. 순서가 중요하다.
     *   - head 와 tail 은?
     *
     * TODO(16): 구현하라. O(n) 이고 추가 메모리는 O(1) 이어야 한다.
     */
    @Override
    public void reverse() {
        throw new UnsupportedOperationException("TODO(16): reverse");
    }

    /**
     * 앞에서부터 훑는 반복자. remove() 를 지원해야 한다.
     *
     * 생각할 것
     *   - next() 가 방금 돌려준 노드를 remove() 가 지워야 한다. 그 노드를 어떻게 기억하는가?
     *   - next() 를 부르기 전에 remove() 를 부르면? 연속으로 두 번 remove() 하면?
     *     둘 다 IllegalStateException 이다.
     *   - unlink 를 재사용할 수 있다.
     *
     * TODO(17): 구현하라.
     */
    @Override
    public java.util.Iterator<E> iterator() {
        throw new UnsupportedOperationException("TODO(17): iterator");
    }
}
