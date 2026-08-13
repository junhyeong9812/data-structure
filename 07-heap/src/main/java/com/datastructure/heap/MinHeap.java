package com.datastructure.heap;

import java.util.Comparator;

/**
 * 가장 작은 것이 먼저 나오는 힙.
 *
 * BinaryHeap 에 비교자만 끼운 것이다. 힙 로직은 한 줄도 다시 쓰지 않는다.
 * **최소 힙과 최대 힙의 차이는 비교 방향 하나뿐**이라는 것이 요점이다.
 *
 * 이 클래스에는 TODO 가 없다. BinaryHeap 을 끝내면 자동으로 동작한다.
 */
public class MinHeap<E extends Comparable<E>> extends BinaryHeap<E> {

    public MinHeap() {
        super(Comparator.naturalOrder());
    }
}
