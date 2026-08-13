package com.datastructure.heap;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * 정렬된 배열로 만든 힙 - 가장 먼저 떠오르는 방법.
 *
 * 넣을 때마다 제자리에 끼워 넣어 항상 정렬 상태를 유지한다.
 * 그러면 가장 앞선 것은 늘 맨 끝(또는 맨 앞)에 있으니 peek 도 poll 도 O(1) 이다.
 *
 * **이 구현은 계약을 전부 지킨다. 동작은 맞다.**
 * 문제는 넣을 때다. 끼워 넣으려면 뒤를 밀어야 하고, 그게 O(n) 이다. 01번에서 본 시프트 비용이다.
 *
 * 힙이 정말 필요한 상황은 넣기와 빼기가 뒤섞여 반복되는 경우다.
 * 그런 곳에서 넣기가 O(n) 이면 전체가 O(n^2) 이 된다.
 * 그 문제를 BinaryHeap 에서 고친다. 여기서는 먼저 문제를 만나는 것이 목적이다.
 *
 * 참고: 필드 이름 elements, size, moves 는 테스트가 직접 들여다본다.
 */
public class SortedListHeap<E> implements Heap<E> {

    private static final int DEFAULT_CAPACITY = 8;

    private final Comparator<? super E> comparator;
    Object[] elements;
    int size;

    /**
     * 원소를 밀어낸 횟수. 넣기 비용이 얼마나 드는지 눈으로 보려고 센다.
     * 실제 자료구조에는 이런 계수기가 없다. 여기는 학습용이다.
     */
    long moves;

    public SortedListHeap(Comparator<? super E> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("비교 기준이 있어야 한다");
        }
        this.comparator = comparator;
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.moves = 0;
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

    /**
     * 배열은 "뒤로 갈수록 앞선다"로 유지한다.
     * 그래야 가장 앞선 것을 꺼낼 때 맨 끝을 떼면 되어 시프트가 없다.
     */
    @SuppressWarnings("unchecked")
    private E at(int index) {
        return (E) elements[index];
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

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 정렬을 유지하며 끼워 넣는다.
     *
     * 생각할 것
     *   - 뒤로 갈수록 앞선 것이 오도록 유지한다. 새 원소는 어디에 들어가야 하는가?
     *   - 자리를 만들려면 뒤에 있는 것들을 밀어야 한다. 몇 칸이나 밀리는가?
     *     민 횟수만큼 moves 를 늘려라. 그게 이 구현의 비용이다.
     *
     * TODO(01): 구현하라. element 가 null 이면 IllegalArgumentException.
     */
    @Override
    public void insert(E element) {
        throw new UnsupportedOperationException("TODO(01): insert");
    }

    /** TODO(02): 구현하라. 비었으면 NoSuchElementException. */
    @Override
    public E peek() {
        throw new UnsupportedOperationException("TODO(02): peek");
    }

    /**
     * 가장 앞선 것을 꺼낸다.
     *
     * 생각할 것
     *   - 배열을 "뒤로 갈수록 앞선다"로 유지했다면 여기서는 시프트가 필요한가?
     *   - 꺼낸 자리의 참조를 남기지 마라.
     *
     * TODO(03): 구현하라.
     */
    @Override
    public E poll() {
        throw new UnsupportedOperationException("TODO(03): poll");
    }
}
