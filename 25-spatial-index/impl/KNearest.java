package com.datastructure.spatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

final class KNearest {

    private final Point2D target;
    private final int k;
    private final PriorityQueue<Point2D> heap;

    KNearest(Point2D target, int k) {
        if (target == null) throw new IllegalArgumentException("target 이 null 이다");
        if (k < 1) throw new IllegalArgumentException("k 는 1 이상이어야 한다: " + k);
        this.target = target;
        this.k = k;
        this.heap = new PriorityQueue<>(
                (a, b) -> Long.compare(target.squaredDistanceTo(b), target.squaredDistanceTo(a)));
    }

    void offer(Point2D candidate) {
        if (heap.size() < k) {
            heap.add(candidate);
            return;
        }
        if (target.squaredDistanceTo(candidate) < target.squaredDistanceTo(heap.peek())) {
            heap.poll();
            heap.add(candidate);
        }
    }

    long radius() {
        return heap.size() < k ? Long.MAX_VALUE : target.squaredDistanceTo(heap.peek());
    }

    int size() {
        return heap.size();
    }

    List<Point2D> drain() {
        List<Point2D> out = new ArrayList<>(heap);
        out.sort(Comparator.comparingLong(target::squaredDistanceTo));
        return out;
    }
}
