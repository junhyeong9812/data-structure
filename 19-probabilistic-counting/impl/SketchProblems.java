package com.datastructure.sketch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public final class SketchProblems {

    static final double HEAVY_HITTER_EPSILON = 0.001;
    static final double HEAVY_HITTER_DELTA = 0.01;

    static final Comparator<int[]> WORST_FIRST = (a, b) -> {
        if (a[1] != b[1]) {
            return Integer.compare(a[1], b[1]);
        }
        return Integer.compare(b[0], a[0]);
    };

    private SketchProblems() {
    }

    public static List<int[]> heavyHitters(int[] stream, int k) {
        if (stream == null) {
            throw new IllegalArgumentException("스트림이 없다");
        }
        if (k < 1) {
            throw new IllegalArgumentException("k 는 1 이상이어야 한다: " + k);
        }
        CountMinSketch sketch = new CountMinSketch(HEAVY_HITTER_EPSILON, HEAVY_HITTER_DELTA);
        for (int x : stream) {
            sketch.add(x);
        }

        PriorityQueue<int[]> heap = new PriorityQueue<>(WORST_FIRST);
        Set<Integer> inHeap = new HashSet<>();
        for (int x : stream) {
            if (inHeap.contains(x)) {
                continue;
            }
            int[] candidate = {x, (int) sketch.estimateCount(x)};
            if (heap.size() < k) {
                heap.offer(candidate);
                inHeap.add(x);
            } else if (WORST_FIRST.compare(candidate, heap.peek()) > 0) {
                inHeap.remove(heap.poll()[0]);
                heap.offer(candidate);
                inHeap.add(x);
            }
        }

        List<int[]> out = new ArrayList<>(heap);
        out.sort(WORST_FIRST.reversed());
        return out;
    }

    public static long distinctAcrossShards(int[][] shards, int p) {
        if (shards == null) {
            throw new IllegalArgumentException("샤드가 없다");
        }
        HyperLogLog merged = new HyperLogLog(p);
        for (int[] shard : shards) {
            if (shard == null) {
                throw new IllegalArgumentException("빈 샤드는 null 이 아니라 길이 0 이어야 한다");
            }
            HyperLogLog local = new HyperLogLog(p);
            for (int x : shard) {
                local.add(x);
            }
            merged.merge(local);
        }
        return merged.estimate();
    }
}
