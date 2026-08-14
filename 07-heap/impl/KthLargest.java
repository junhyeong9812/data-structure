package com.datastructure.heap;

/**
 * [구현] 스트림에서 k 번째로 큰 값.
 *
 * 핵심은 최소 힙을 쓴다는 것이다. 처음에는 뒤집혀 보인다.
 *
 * k 개만 들고 있으면 그중 가장 작은 것이 곧 k 번째로 큰 값이다.
 * 그리고 새 값이 들어와 k+1 개가 되면 가장 작은 것을 버려야 한다.
 * "가장 작은 것을 O(1) 로 보고 O(log n) 으로 버린다"가 정확히 최소 힙이다.
 *
 * 최대 힙을 쓰면 가장 큰 것만 빨리 알 수 있어 버릴 것을 못 찾는다.
 */
public class KthLargest {

    private final int k;
    private final Heap<Integer> heap;

    public KthLargest(int k, Heap<Integer> heap) {
        if (k < 1) {
            throw new IllegalArgumentException("k 는 1 이상이어야 한다: " + k);
        }
        if (!heap.isEmpty()) {
            throw new IllegalArgumentException("비어 있는 힙을 넘겨야 한다");
        }
        this.k = k;
        this.heap = heap;
    }

    public int add(int value) {
        heap.insert(value);
        if (heap.size() > k) {
            heap.poll();          // 가장 작은 것을 버린다. k 개만 남는다
        }
        return heap.peek();       // 남은 것 중 가장 작은 것이 곧 k 번째로 큰 값
    }

    public int size() {
        return heap.size();
    }
}
