package com.datastructure.heap;

/**
 * 힙으로 푸는 문제들.
 *
 * 힙이 빛나는 상황에는 공통점이 있다.
 * **전체를 정렬할 필요는 없고 "지금 가장 앞선 것"만 반복해서 알면 될 때**다.
 * 그 조건이 아니면 정렬이나 다른 구조가 낫다.
 */
public final class HeapProblems {

    private HeapProblems() {
    }

    /**
     * 문제 1. 힙 정렬
     *
     * 배열을 오름차순으로 정렬한다. 원본 배열을 직접 고친다.
     *
     * 생각할 것
     *   - 전부 힙에 넣었다가 하나씩 꺼내면 정렬된 순서로 나온다. 왜 그런가?
     *   - 복잡도는? 넣기 n 번과 빼기 n 번이 각각 O(log n) 이다.
     *   - (제자리 정렬로 만들 수도 있다. 배열 자체를 힙으로 보고 뒤에서부터 채우는 방법이다.
     *     여기서는 힙을 써서 푸는 것으로 충분하다.)
     *
     * TODO(09): 구현하라. heap 은 비어 있는 상태로 들어온다.
     */
    public static void heapSort(int[] values, Heap<Integer> heap) {
        throw new UnsupportedOperationException("TODO(09): heapSort");
    }
}
