package com.datastructure.bloom;

import java.util.ArrayList;
import java.util.List;

public class ScalableBloomFilter<T> implements ProbabilisticSet<T> {

    static final double TIGHTENING = 0.5;
    static final int GROWTH = 2;

    private final List<BloomFilter<T>> filters = new ArrayList<>();
    private final int initialCapacity;
    private final double initialFpr;

    private int nextCapacity;
    private double nextFpr;
    private long inserted;

    public ScalableBloomFilter(int initialCapacity, double falsePositiveRate) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("초기 용량은 1 이상이어야 한다: " + initialCapacity);
        }
        if (!(falsePositiveRate > 0.0 && falsePositiveRate < 1.0)) {
            throw new IllegalArgumentException("오탐률은 0 과 1 사이여야 한다: " + falsePositiveRate);
        }
        this.initialCapacity = initialCapacity;
        this.initialFpr = falsePositiveRate;
        this.nextCapacity = initialCapacity;
        this.nextFpr = falsePositiveRate;
    }

    private void grow() {
        filters.add(new BloomFilter<>(nextCapacity, nextFpr));
        nextFpr = nextFpr * TIGHTENING;
        nextCapacity = nextCapacity * GROWTH;
    }

    @Override
    public void add(T item) {
        BloomFilter<T> current = filters.isEmpty() ? null : filters.get(filters.size() - 1);
        if (current == null || current.insertedCount() >= current.capacity()) {
            grow();
            current = filters.get(filters.size() - 1);
        }
        current.add(item);
        inserted++;
    }

    @Override
    public boolean mightContain(T item) {
        for (BloomFilter<T> f : filters) {
            if (f.mightContain(item)) {
                return true;
            }
        }
        return false;
    }

    public int filterCount() {
        return filters.size();
    }

    @Override
    public long insertedCount() {
        return inserted;
    }

    @Override
    public long bitSize() {
        long total = 0;
        for (BloomFilter<T> f : filters) {
            total += f.bitSize();
        }
        return total;
    }

    @Override
    public double expectedFalsePositiveRate() {
        double allMiss = 1.0;
        for (BloomFilter<T> f : filters) {
            allMiss *= (1 - f.expectedFalsePositiveRate());
        }
        return 1 - allMiss;
    }

    @Override
    public void clear() {
        filters.clear();
        nextCapacity = initialCapacity;
        nextFpr = initialFpr;
        inserted = 0;
    }
}
