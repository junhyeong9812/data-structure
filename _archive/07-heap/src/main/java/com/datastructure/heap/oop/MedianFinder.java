package com.datastructure.heap.oop;

public class MedianFinder {
    private final BinaryHeap<Integer> lower;
    private final BinaryHeap<Integer> upper;

    public MedianFinder() {
        this.lower = BinaryHeap.maxHeap();
        this.upper = BinaryHeap.minHeap();
    }

    public void addNum(int num) {
        if (lower.isEmpty() || num <= lower.peek()) {
            lower.insert(num);
        } else {
            upper.insert(num);
        }

        //균형 유지
        if (lower.size() > upper.size() + 1) {
            upper.insert(lower.extract());
        } else if (upper.size() > lower.size()) {
            lower.insert(upper.extract());
        }
    }

    public double findMedian() {
        if (lower.isEmpty()) throw new IllegalArgumentException("데이터가 없습니다.");
        if (lower.size() > upper.size()) {
            return lower.peek();
        }
        return (lower.peek() + upper.peek()) / 2.0;
    }
}
