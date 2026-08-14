package com.datastructure.heap;

/**
 * 스트림에서 k 번째로 큰 값을 계속 알려주는 장치.
 *
 * 값이 하나씩 흘러 들어오고, 그때마다 "지금까지 중 k 번째로 큰 값"을 답해야 한다.
 *
 *   k=3, 흘러 들어오는 값 4, 5, 8, 2 -> 답 4, 4, 4, 4
 *   (4 하나만 있을 때는 아직 3개가 안 되므로 그때의 정의는 아래 계약 참고)
 *
 * 왜 힙인가
 *   매번 전부 정렬하면 O(n log n) 이 반복된다.
 *   그런데 우리는 k 번째만 알면 되므로 k 개만 들고 있으면 된다.
 *   그 k 개 중 가장 작은 것이 곧 k 번째로 큰 값이다.
 *
 * 어떤 힙을 써야 하는가가 이 문제의 핵심이다.
 * "가장 큰 값"을 찾는데 최소 힙을 쓰는 것이 처음에는 뒤집혀 보인다.
 */
public class KthLargest {

    private final int k;
    private final Heap<Integer> heap;

    /**
     * @param k    몇 번째로 큰 값을 추적할지
     * @param heap 비어 있는 작업용 힙. 어떤 힙을 넘겨야 하는지는 호출자가 정한다.
     */
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

    /**
     * 값을 하나 받고, 지금까지 중 k 번째로 큰 값을 반환한다.
     * 아직 k 개가 안 모였으면 지금까지 중 가장 작은 값을 반환한다.
     *
     * 생각할 것
     *   - 힙에 몇 개를 들고 있어야 하는가? 그보다 많아지면 무엇을 버려야 하는가?
     *   - 버릴 것은 "가장 작은 것"이다. 그럼 어떤 힙이어야 그게 O(log n) 인가?
     *   - 힙의 맨 위가 곧 답이 되도록 유지할 수 있는가?
     *
     * TODO(10): 구현하라.
     */
    public int add(int value) {
        throw new UnsupportedOperationException("TODO(10): add");
    }

    public int size() {
        return heap.size();
    }
}
