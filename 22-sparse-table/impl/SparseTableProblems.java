package com.datastructure.sparsetable;

public final class SparseTableProblems {

    private SparseTableProblems() {
    }

    public static int[] slidingWindowMin(int[] values, int k) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        if (k <= 0 || k > values.length) {
            throw new IllegalArgumentException("창 크기가 범위를 벗어났다: " + k + " (원소 " + values.length + ")");
        }
        long[] a = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            a[i] = values[i];
        }
        MinSparseTable t = new MinSparseTable(a);
        int[] out = new int[values.length - k + 1];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) t.query(i, i + k - 1);
        }
        return out;
    }

    public static long[] rangeGcdQueries(int[] values, int[][] queries) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        if (queries == null) {
            throw new IllegalArgumentException("질의 배열이 필요하다");
        }
        long[] a = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            a[i] = values[i];
        }
        GcdSparseTable t = new GcdSparseTable(a);
        long[] out = new long[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int[] q = queries[i];
            if (q == null || q.length != 2) {
                throw new IllegalArgumentException("질의는 {from, to} 두 칸이어야 한다: " + i + "번");
            }
            if (q[0] < 0 || q[1] >= values.length || q[0] > q[1]) {
                throw new IllegalArgumentException(
                        "질의 범위가 잘못됐다: [" + q[0] + ", " + q[1] + "] (원소 " + values.length + ")");
            }
            out[i] = t.query(q[0], q[1]);
        }
        return out;
    }
}
