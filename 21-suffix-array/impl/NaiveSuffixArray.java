package com.datastructure.suffix;

import java.util.Arrays;

public final class NaiveSuffixArray {

    private final String text;
    private final int[] sa;
    private long charComparisons;

    public NaiveSuffixArray(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text 가 null 이다");
        }
        this.text = text;
        int n = text.length();
        this.sa = new int[n];

        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        Arrays.sort(order, this::compareSuffixes);
        for (int i = 0; i < n; i++) {
            sa[i] = order[i];
        }
    }

    int compareSuffixes(int i, int j) {
        int n = text.length();
        int a = i;
        int b = j;
        while (a < n && b < n) {
            charComparisons++;
            char ca = text.charAt(a);
            char cb = text.charAt(b);
            if (ca != cb) {
                return ca < cb ? -1 : 1;
            }
            a++;
            b++;
        }
        return Integer.compare(n - i, n - j);
    }

    public int[] toArray() {
        return sa.clone();
    }

    public int size() {
        return sa.length;
    }

    public long charComparisons() {
        return charComparisons;
    }

    public static int[] of(String text) {
        return new NaiveSuffixArray(text).toArray();
    }
}
