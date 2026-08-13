package com.datastructure.bloom;

public class BloomFilter<T> implements ProbabilisticSet<T> {

    private final int bits;
    private final int hashCount;
    private final int capacity;
    private final long[] words;
    private long inserted;

    public BloomFilter(int expectedInsertions, double falsePositiveRate) {
        if (expectedInsertions < 1) {
            throw new IllegalArgumentException("예상 원소 수는 1 이상이어야 한다: " + expectedInsertions);
        }
        if (!(falsePositiveRate > 0.0 && falsePositiveRate < 1.0)) {
            throw new IllegalArgumentException("오탐률은 0 과 1 사이여야 한다: " + falsePositiveRate);
        }
        this.capacity = expectedInsertions;
        this.bits = optimalBits(expectedInsertions, falsePositiveRate);
        this.hashCount = optimalHashCount(bits, expectedInsertions);
        this.words = new long[(bits + 63) / 64];
    }

    static int optimalBits(int n, double p) {
        double m = -n * Math.log(p) / (Math.log(2) * Math.log(2));
        return (int) Math.max(1, Math.ceil(m));
    }

    static int optimalHashCount(int m, int n) {
        int k = (int) Math.round((double) m / n * Math.log(2));
        return Math.max(1, k);
    }

    static long mix64(long z) {
        z += 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    int[] indexes(T item) {
        long h = mix64(item == null ? 0 : item.hashCode());
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
            words[idx >>> 6] |= 1L << (idx & 63);
        }
        inserted++;
    }

    @Override
    public boolean mightContain(T item) {
        for (int idx : indexes(item)) {
            if ((words[idx >>> 6] & (1L << (idx & 63))) == 0) {
                return false;
            }
        }
        return true;
    }

    boolean bit(int index) {
        return (words[index >>> 6] & (1L << (index & 63))) != 0;
    }

    int hashCount() {
        return hashCount;
    }

    int capacity() {
        return capacity;
    }

    @Override
    public long insertedCount() {
        return inserted;
    }

    @Override
    public long bitSize() {
        return bits;
    }

    @Override
    public double expectedFalsePositiveRate() {
        return Math.pow(1 - Math.exp(-(double) hashCount * inserted / bits), hashCount);
    }

    @Override
    public void clear() {
        java.util.Arrays.fill(words, 0L);
        inserted = 0;
    }
}
