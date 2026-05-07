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
    public static double[] medianStream(int[] stream) {
        if (stream == null || stream.length == 0) return new double[0];

        double[] result = new double[stream.length];
        MaxHeap<Integer> lower = new MaxHeap<>();
        MinHeap<Integer> upper = new MinHeap<>();

        for (int i = 0; i < stream.length; i++) {
            int num = stream[i];

            // 데이터 삽입
            if (lower.isEmpty() || num <= lower.peek()) {
                lower.insert(num);
            } else {
                upper.insert(num);
            }

            // 균형 유지: lower가 같거나 1개 더 많게
            if (lower.size() > upper.size() + 1) {
                upper.insert(lower.extractMax());
            } else if (upper.size() > lower.size()) {
                lower.insert(upper.extractMin());
            }

            // 중앙값 계산
            if (lower.size() > upper.size()) {
                result[i] = lower.peek();
            } else {
                result[i] = (lower.peek() + upper.peek()) / 2.0;
            }
        }
        return result;
    }

    // 다익스트라: 가중치 그래프에서 시작점으로부터 각 노드까지 최단거리
    public static int[] dijkstra(int[][] graph, int start) { return null; }
}
