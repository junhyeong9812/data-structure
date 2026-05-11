package com.datastructure.heap.oop;

public class KthLargest {
    private final int k;
    private final BinaryHeap<Integer> heap;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        this.heap = BinaryHeap.minHeap();
        for (int num : nums) {
            add(num);
        }
    }

    public int add(int val) {
        if (heap.size() < k) {
            heap.insert(val);
        } else if (val > heap.peek()) {
            heap.extract();
            heap.insert(val);
        }
        return heap.peek();
    }
}
