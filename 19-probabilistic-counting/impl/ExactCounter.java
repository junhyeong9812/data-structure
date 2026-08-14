package com.datastructure.sketch;

import java.util.HashMap;
import java.util.Map;

public class ExactCounter implements FrequencyEstimator {

    static final long BYTES_PER_ENTRY = 64;

    private final Map<Integer, Long> counts = new HashMap<>();
    private long total;

    @Override
    public void add(int item) {
        add(item, 1);
    }

    @Override
    public void add(int item, long count) {
        if (count < 0) {
            throw new IllegalArgumentException("count 는 0 이상이어야 한다: " + count);
        }
        if (count == 0) {
            return;
        }
        counts.merge(item, count, Long::sum);
        total += count;
    }

    @Override
    public long estimateCount(int item) {
        return counts.getOrDefault(item, 0L);
    }

    @Override
    public long totalCount() {
        return total;
    }

    @Override
    public long memoryBytes() {
        return (long) counts.size() * BYTES_PER_ENTRY;
    }

    public int distinctCount() {
        return counts.size();
    }
}
