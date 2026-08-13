```text
package com.datastructure.heap.oop;

public class MedianFinder {

    private final BinaryHeap<Integer> lower;  // maxHeap: 작은 쪽 절반
    private final BinaryHeap<Integer> upper;  // minHeap: 큰 쪽 절반

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

        // 균형 유지
        if (lower.size() > upper.size() + 1) {
            upper.insert(lower.extract());
        } else if (upper.size() > lower.size()) {
            lower.insert(upper.extract());
        }
    }

    public double findMedian() {
        if (lower.isEmpty()) throw new IllegalStateException("데이터가 없습니다.");
        if (lower.size() > upper.size()) {
            return lower.peek();
        }
        return (lower.peek() + upper.peek()) / 2.0;
    }
}

package com.datastructure.heap.oop;

public class KthLargest {

    private final int k;
    private final BinaryHeap<Integer> heap;  // minHeap으로 크기 k 유지

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
```