package com.datastructure.sketch;

import java.util.HashSet;
import java.util.Set;

public class ExactCardinality implements CardinalityEstimator {

    static final long BYTES_PER_ELEMENT = 48;

    private final Set<Integer> seen = new HashSet<>();

    @Override
    public void add(int item) {
        seen.add(item);
    }

    @Override
    public long estimate() {
        return seen.size();
    }

    @Override
    public long memoryBytes() {
        return (long) seen.size() * BYTES_PER_ELEMENT;
    }
}
