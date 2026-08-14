package com.datastructure.heap;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * 배열로 만든 이진 힙.
 *
 * SortedListHeap 을 먼저 만들고 그 테스트를 돌려본 뒤에 이걸 하라.
 * 무엇을 고치는 것인지 모르면 sift 연산이 그냥 복잡하기만 하다.
 *
 * 핵심 발상 두 가지
 *
 * 1. 전부 정렬할 필요가 없다.
 *    부모가 자식보다 앞서기만 하면 맨 위가 가장 앞선 것이다. 형제끼리는 아무 관계가 없어도 된다.
 *    덜 지키니까 넣기가 O(n) 에서 O(log n) 으로 줄어든다.
 *
 * 2. 완전 이진 트리는 배열로 표현할 수 있다.
 *    빈틈없이 채워진 트리라면 노드 위치를 계산으로 알 수 있다. 포인터가 필요 없다.
 *
 *      i 번 노드의 부모   (i - 1) / 2
 *      i 번 노드의 왼쪽    2i + 1
 *      i 번 노드의 오른쪽  2i + 2
 *
 *    02번 연결 리스트에서 노드마다 참조를 들고 있던 것과 대비된다.
 *    구조가 규칙적이면 그 규칙을 계산으로 대신할 수 있다.
 *
 * 참고: 필드 이름 elements, size 는 테스트가 직접 들여다본다.
 */
public class BinaryHeap<E> implements Heap<E> {

    private static final int DEFAULT_CAPACITY = 8;

    private final Comparator<? super E> comparator;
    Object[] elements;
    int size;

    public BinaryHeap(Comparator<? super E> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("비교 기준이 있어야 한다");
        }
        this.comparator = comparator;
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

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

    public int capacity() {
        return elements.length;
    }

    static int parentOf(int index) {
        return (index - 1) / 2;
    }

    static int leftOf(int index) {
        return 2 * index + 1;
    }

    static int rightOf(int index) {
        return 2 * index + 2;
    }

    @SuppressWarnings("unchecked")
    E at(int index) {
        return (E) elements[index];
    }

    /** a 가 b 보다 앞서면 음수. 힙의 방향은 이 비교자 하나가 정한다. */
    int compare(int a, int b) {
        return comparator.compare(at(a), at(b));
    }

    void swap(int a, int b) {
        Object tmp = elements[a];
        elements[a] = elements[b];
        elements[b] = tmp;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= elements.length) return;
        elements = java.util.Arrays.copyOf(elements, elements.length * 2);
    }

    @Override
    public void clear() {
        java.util.Arrays.fill(elements, 0, size, null);
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[i]);
        }
        return sb.append(']').toString();
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * index 의 원소를 위로 올린다. 부모보다 앞서면 자리를 바꾸며 계속 올라간다.
     *
     * 생각할 것
     *   - 어디서 멈춰야 하는가? 두 가지 조건이 있다.
     *   - 맨 위까지 올라가도 몇 번이면 되는가? 트리 높이가 곧 그 답이다.
     *
     * TODO(04): 구현하라.
     */
    void siftUp(int index) {
        throw new UnsupportedOperationException("TODO(04): siftUp");
    }

    /**
     * index 의 원소를 아래로 내린다. 자식보다 뒤처지면 자리를 바꾸며 계속 내려간다.
     *
     * 생각할 것
     *   - 자식이 둘이면 어느 쪽과 바꿔야 하는가? 아무 쪽이나 바꾸면 힙 성질이 깨진다.
     *   - 자식이 하나뿐이거나 없는 경우를 잊지 마라.
     *
     * TODO(05): 구현하라.
     */
    void siftDown(int index) {
        throw new UnsupportedOperationException("TODO(05): siftDown");
    }

    /**
     * 넣는다.
     *
     * 생각할 것
     *   - 완전 이진 트리를 유지하려면 새 원소는 어디에 놓아야 하는가?
     *   - 놓고 나면 힙 성질이 깨질 수 있다. 어느 방향으로 고쳐야 하는가?
     *
     * TODO(06): 구현하라. element 가 null 이면 IllegalArgumentException.
     */
    @Override
    public void insert(E element) {
        throw new UnsupportedOperationException("TODO(06): insert");
    }

    /** TODO(07): 구현하라. 비었으면 NoSuchElementException. */
    @Override
    public E peek() {
        throw new UnsupportedOperationException("TODO(07): peek");
    }

    /**
     * 가장 앞선 것을 꺼낸다.
     *
     * 생각할 것
     *   - 맨 위를 빼면 구멍이 생긴다. 완전 이진 트리를 유지하려면 무엇으로 메워야 하는가?
     *     (아무거나 올리면 트리 모양이 깨진다. 모양을 안 깨는 후보는 하나뿐이다.)
     *   - 메우고 나면 힙 성질이 깨질 수 있다. 어느 방향으로 고쳐야 하는가?
     *   - 빈 자리의 참조를 남기지 마라.
     *
     * TODO(08): 구현하라.
     */
    @Override
    public E poll() {
        throw new UnsupportedOperationException("TODO(08): poll");
    }
}
