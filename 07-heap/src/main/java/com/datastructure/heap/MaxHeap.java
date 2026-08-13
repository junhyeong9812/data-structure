package com.datastructure.heap;

import java.util.Comparator;

/**
 * 가장 큰 것이 먼저 나오는 힙.
 *
 * MinHeap 과 비교자만 반대다. 그 외에는 완전히 같다.
 *
 * 이 클래스에는 TODO 가 없다.
 */
public class MaxHeap<E extends Comparable<E>> extends BinaryHeap<E> {

    public MaxHeap() {
        super(Comparator.<E>naturalOrder().reversed());
    }
}
