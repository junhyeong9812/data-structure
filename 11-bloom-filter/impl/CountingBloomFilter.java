package com.datastructure.bloom;

public class CountingBloomFilter<T> implements ProbabilisticSet<T> {

    static final int MAX_COUNT = 255;

    private final int bits;
    private final int hashCount;
    private final byte[] counters;
    private long inserted;
    private long saturations;

    public CountingBloomFilter(int expectedInsertions, double falsePositiveRate) {
        if (expectedInsertions < 1) {
            throw new IllegalArgumentException("예상 원소 수는 1 이상이어야 한다: " + expectedInsertions);
        }
        if (!(falsePositiveRate > 0.0 && falsePositiveRate < 1.0)) {
            throw new IllegalArgumentException("오탐률은 0 과 1 사이여야 한다: " + falsePositiveRate);
        }
        this.bits = BloomFilter.optimalBits(expectedInsertions, falsePositiveRate);
        this.hashCount = BloomFilter.optimalHashCount(bits, expectedInsertions);
        this.counters = new byte[bits];
    }

    private int[] indexes(T item) {
        long h = BloomFilter.mix64(item == null ? 0 : item.hashCode());
        int h1 = (int) h;
        int h2 = (int) (h >>> 32);
        if (h2 == 0) {
            h2 = 1;
        }
        int[] out = new int[hashCount];
        for (int i = 0; i < hashCount; i++) {
            out[i] = Math.floorMod(h1 + i * h2, bits);
        }
        return out;
    }

    @Override
    public void add(T item) {
        for (int idx : indexes(item)) {
            int c = counters[idx] & 0xFF;
            if (c == MAX_COUNT) {
                saturations++;
            } else {
                counters[idx] = (byte) (c + 1);
            }
        }
        inserted++;
    }

    public boolean remove(T item) {
        if (!mightContain(item)) {
            return false;
        }
        for (int idx : indexes(item)) {
            int c = counters[idx] & 0xFF;
            if (c != MAX_COUNT && c > 0) {
                counters[idx] = (byte) (c - 1);
            }
        }
        inserted--;
        return true;
    }

    @Override
    public boolean mightContain(T item) {
        for (int idx : indexes(item)) {
            if ((counters[idx] & 0xFF) == 0) {
                return false;
            }
        }
        return true;
    }

    int count(int index) {
        return counters[index] & 0xFF;
    }

    long saturations() {
        return saturations;
    }

    @Override
    public long insertedCount() {
        return inserted;
    }

    @Override
    public long bitSize() {
        return (long) bits * 8;
    }

    /** 계수기 하나가 몇 비트인지. 비트 배열 대비 몇 배를 쓰는지가 여기서 나온다. */
    public int bitsPerSlot() {
        return 8;
    }

    @Override
    public double expectedFalsePositiveRate() {
        return Math.pow(1 - Math.exp(-(double) hashCount * inserted / bits), hashCount);
    }

    @Override
    public void clear() {
        java.util.Arrays.fill(counters, (byte) 0);
        inserted = 0;
        saturations = 0;
    }
}
