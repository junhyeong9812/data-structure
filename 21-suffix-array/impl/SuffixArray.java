package com.datastructure.suffix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SuffixArray {

    private final String text;
    private final int[] sa;
    private int sortRounds;
    private int searchProbes;

    public SuffixArray(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text 가 null 이다");
        }
        this.text = text;
        int n = text.length();
        this.sa = new int[n];
        if (n == 0) {
            return;
        }

        for (int i = 0; i < n; i++) {
            sa[i] = i;
        }
        int[] rank = initialRanks(text);

        Integer[] order = new Integer[n];
        for (int k = 1; ; k <<= 1) {
            final int[] r = rank;
            final int step = k;
            for (int i = 0; i < n; i++) {
                order[i] = sa[i];
            }
            Arrays.sort(order, (a, b) -> comparePair(r, a, b, step));
            for (int i = 0; i < n; i++) {
                sa[i] = order[i];
            }
            sortRounds++;

            rank = reRank(sa, r, k);
            if (rank[sa[n - 1]] == n - 1) {
                break;
            }
        }
    }

    static int[] initialRanks(String text) {
        int n = text.length();
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[i] = text.charAt(i);
        }
        return rank;
    }

    static int comparePair(int[] rank, int i, int j, int k) {
        if (rank[i] != rank[j]) {
            return Integer.compare(rank[i], rank[j]);
        }
        int n = rank.length;
        int ri = i + k < n ? rank[i + k] : -1;
        int rj = j + k < n ? rank[j + k] : -1;
        return Integer.compare(ri, rj);
    }

    static int[] reRank(int[] sa, int[] rank, int k) {
        int n = sa.length;
        int[] next = new int[n];
        next[sa[0]] = 0;
        for (int i = 1; i < n; i++) {
            int grew = comparePair(rank, sa[i - 1], sa[i], k) < 0 ? 1 : 0;
            next[sa[i]] = next[sa[i - 1]] + grew;
        }
        return next;
    }

    public int size() {
        return sa.length;
    }

    public String text() {
        return text;
    }

    public int[] toArray() {
        return sa.clone();
    }

    public String suffixAt(int rank) {
        if (rank < 0 || rank >= sa.length) {
            throw new IndexOutOfBoundsException(
                    "순위 " + rank + " 가 범위를 벗어났다 (크기 " + sa.length + ")");
        }
        return text.substring(sa[rank]);
    }

    int comparePrefix(int start, String pattern) {
        int m = pattern.length();
        int n = text.length();
        for (int i = 0; i < m; i++) {
            if (start + i >= n) {
                return -1;
            }
            char c = text.charAt(start + i);
            char p = pattern.charAt(i);
            if (c != p) {
                return c < p ? -1 : 1;
            }
        }
        return 0;
    }

    int lowerBound(String pattern) {
        int lo = 0;
        int hi = sa.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            searchProbes++;
            if (comparePrefix(sa[mid], pattern) < 0) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    int upperBound(String pattern) {
        int lo = 0;
        int hi = sa.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            searchProbes++;
            if (comparePrefix(sa[mid], pattern) <= 0) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    public List<Integer> find(String pattern) {
        requirePattern(pattern);
        searchProbes = 0;
        int lo = lowerBound(pattern);
        int hi = upperBound(pattern);
        List<Integer> out = new ArrayList<>(hi - lo);
        for (int i = lo; i < hi; i++) {
            out.add(sa[i]);
        }
        Collections.sort(out);
        return out;
    }

    public boolean contains(String pattern) {
        requirePattern(pattern);
        searchProbes = 0;
        return upperBound(pattern) > lowerBound(pattern);
    }

    public int count(String pattern) {
        requirePattern(pattern);
        searchProbes = 0;
        return upperBound(pattern) - lowerBound(pattern);
    }

    public int sortRounds() {
        return sortRounds;
    }

    public int lastSearchProbes() {
        return searchProbes;
    }

    public long memoryBytes() {
        return 4L * sa.length;
    }

    private static void requirePattern(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern 이 null 이다");
        }
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("빈 패턴은 모든 자리에 있으므로 질문이 되지 않는다");
        }
    }
}
