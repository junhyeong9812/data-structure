package com.datastructure.sparsetable;

import java.util.Arrays;
import java.util.function.LongBinaryOperator;

public class DisjointSparseTable implements StaticRangeQuery {

    private final int n;
    private final int width;
    private final int levels;
    private final long[][] table;
    private final long[] values;
    private final long identity;
    private final LongBinaryOperator combine;

    public DisjointSparseTable(long[] initial) {
        this(initial, 0L, Long::sum);
    }

    public DisjointSparseTable(long[] initial, long identity, LongBinaryOperator combine) {
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        if (combine == null) {
            throw new IllegalArgumentException("결합 함수가 필요하다");
        }
        this.n = initial.length;
        this.values = initial.clone();
        this.identity = identity;
        this.combine = combine;

        int w = 1;
        int lv = 0;
        while (w < n) {
            w <<= 1;
            lv++;
        }
        this.width = w;
        this.levels = lv;
        this.table = new long[levels][width];
        build();
    }

    private void build() {
        long[] padded = new long[width];
        Arrays.fill(padded, identity);
        System.arraycopy(values, 0, padded, 0, n);

        for (int level = 0; level < levels; level++) {
            int range = 1 << level;
            long[] row = table[level];
            for (int center = range; center < width; center += range << 1) {
                row[center - 1] = padded[center - 1];
                for (int i = center - 2; i >= center - range; i--) {
                    row[i] = combine.applyAsLong(padded[i], row[i + 1]);
                }
                row[center] = padded[center];
                for (int i = center + 1; i < center + range; i++) {
                    row[i] = combine.applyAsLong(row[i - 1], padded[i]);
                }
            }
        }
    }

    @Override
    public long query(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return identity;
        }
        if (from == to) {
            return values[from];
        }
        int level = 31 - Integer.numberOfLeadingZeros(from ^ to);
        return combine.applyAsLong(table[level][from], table[level][to]);
    }

    @Override
    public long get(int index) {
        requireIndex(index);
        return values[index];
    }

    @Override
    public int size() {
        return n;
    }

    public int levels() {
        return levels;
    }

    public int unitCount() {
        return levels * width;
    }

    long node(int level, int index) {
        return table[level][index];
    }

    private void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
