package com.datastructure.heap;

/** [구현] 힙 응용 문제. */
public final class HeapProblems {

    private HeapProblems() {
    }

    /**
     * 전부 넣었다가 하나씩 꺼내면 정렬된 순서로 나온다.
     * 힙의 계약이 "가장 앞선 것이 먼저 나온다"이므로, 반복해서 꺼내면 그게 곧 정렬이다.
     *
     * 넣기 n 번과 빼기 n 번이 각각 O(log n) 이므로 전체 O(n log n) 이다.
     * 비교 기반 정렬의 하한이 그것이니 최적이다.
     */
    public static void heapSort(int[] values, Heap<Integer> heap) {
        for (int v : values) {
            heap.insert(v);
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = heap.poll();
        }
    }
}
