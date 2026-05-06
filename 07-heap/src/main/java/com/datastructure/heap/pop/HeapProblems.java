package com.datastructure.heap.pop;

import java.util.ArrayList;
import java.util.List;

public class HeapProblems {

    // Top-K: 배열에서 가장 큰 k개 요소 반환
    public static List<Integer> topK(int[] array, int k) {
        if (k <= 0) return new ArrayList<>();
        if (k >= array.length) {
            List<Integer> result = new ArrayList<>();
            for (int v : array) result.add(v);
            return result;
        }

        // MinHeap으로 크기 K를 유지하면서 큰 값만 남김
        MinHeap<Integer> heap = new MinHeap<>(k);
        for (int v : array) {
            if (heap.size() < k) {
                heap.insert(v);
            } else if (v > heap.peek()) {
                heap.extractMin();
                heap.insert(v);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            result.add(heap.extractMin());
        }
        return result;
    }

    // K번째 큰 요소: 배열에서 k번째로 큰 값 반환
    public static int kthLargest(int[] array, int k) {
        if ( k <= 0 || k > array.length) throw new IndexOutOfBoundsException();

        // MinHeap으로 크기 K를 유지
        MinHeap<Integer> heap = new MinHeap<>();
        for (int v : array) {
            if (heap.size() < k) {
                heap.insert(v);
            } else if ( v > heap.peek()) {
                heap.extractMin();
                heap.insert(v);
            }
        }
        return heap.peek();
    }

    // 중앙값 스트림: 숫자가 하나씩 들어올 때마다 현재까지의 중앙값 반환
    public static double[] medianStream(int[] stream) { return null; }

    // 다익스트라: 가중치 그래프에서 시작점으로부터 각 노드까지 최단거리
    public static int[] dijkstra(int[][] graph, int start) { return null; }
}
