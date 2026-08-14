package com.datastructure.suffix;

public final class LcpArray {

    private final String text;
    private final int[] sa;
    private final int[] lcp;
    private long charComparisons;

    public LcpArray(SuffixArray suffixArray) {
        this(requireSa(suffixArray).text(), suffixArray.toArray());
    }

    public LcpArray(String text, int[] suffixArray) {
        if (text == null || suffixArray == null) {
            throw new IllegalArgumentException("text 와 suffixArray 가 있어야 한다");
        }
        if (text.length() != suffixArray.length) {
            throw new IllegalArgumentException(
                    "길이가 다르다: 문자열 " + text.length() + ", 접미사 배열 " + suffixArray.length);
        }
        this.text = text;
        this.sa = suffixArray.clone();
        this.lcp = kasai();
    }

    private static SuffixArray requireSa(SuffixArray suffixArray) {
        if (suffixArray == null) {
            throw new IllegalArgumentException("suffixArray 가 null 이다");
        }
        return suffixArray;
    }

    public static int[] inverse(int[] suffixArray) {
        int n = suffixArray.length;
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[suffixArray[i]] = i;
        }
        return rank;
    }

    private int[] kasai() {
        int n = sa.length;
        int[] out = new int[n];
        if (n == 0) {
            return out;
        }
        int[] rank = inverse(sa);
        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] == 0) {
                h = 0;
                continue;
            }
            int j = sa[rank[i] - 1];
            while (i + h < n && j + h < n) {
                charComparisons++;
                if (text.charAt(i + h) != text.charAt(j + h)) {
                    break;
                }
                h++;
            }
            out[rank[i]] = h;
            if (h > 0) {
                h--;
            }
        }
        return out;
    }

    public static int[] build(String text, int[] suffixArray) {
        return new LcpArray(text, suffixArray).toArray();
    }

    public int size() {
        return lcp.length;
    }

    public int get(int rank) {
        if (rank < 0 || rank >= lcp.length) {
            throw new IndexOutOfBoundsException(
                    "순위 " + rank + " 가 범위를 벗어났다 (크기 " + lcp.length + ")");
        }
        return lcp[rank];
    }

    public int[] toArray() {
        return lcp.clone();
    }

    public long sum() {
        long total = 0;
        for (int v : lcp) {
            total += v;
        }
        return total;
    }

    public int max() {
        int best = 0;
        for (int v : lcp) {
            if (v > best) {
                best = v;
            }
        }
        return best;
    }

    public int argMax() {
        if (lcp.length == 0) {
            return -1;
        }
        int at = 0;
        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] > lcp[at]) {
                at = i;
            }
        }
        return at;
    }

    public long charComparisons() {
        return charComparisons;
    }
}
