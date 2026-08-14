package com.datastructure.suffix;

public final class SuffixArrayProblems {

    static final char SEPARATOR = (char) 1;

    private SuffixArrayProblems() {
    }

    public static long countDistinctSubstrings(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        long n = s.length();
        LcpArray lcp = new LcpArray(new SuffixArray(s));
        return n * (n + 1) / 2 - lcp.sum();
    }

    public static String longestRepeatedSubstring(String s) {
        if (s == null || s.length() < 2) {
            return "";
        }
        SuffixArray sa = new SuffixArray(s);
        LcpArray lcp = new LcpArray(sa);
        int best = lcp.max();
        if (best == 0) {
            return "";
        }
        int start = sa.toArray()[lcp.argMax()];
        return s.substring(start, start + best);
    }

    public static String longestCommonSubstring(String a, String b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("a 와 b 가 있어야 한다");
        }
        if (a.indexOf(SEPARATOR) >= 0 || b.indexOf(SEPARATOR) >= 0) {
            throw new IllegalArgumentException("구분자로 쓰는 \\u0001 이 입력에 들어 있다");
        }
        if (a.isEmpty() || b.isEmpty()) {
            return "";
        }

        String joined = a + SEPARATOR + b;
        int cut = a.length();
        int[] sa = new SuffixArray(joined).toArray();
        int[] lcp = LcpArray.build(joined, sa);

        int best = 0;
        int at = -1;
        for (int i = 1; i < sa.length; i++) {
            int x = sa[i - 1];
            int y = sa[i];
            if (x == cut || y == cut) {
                continue;
            }
            boolean crosses = (x < cut) != (y < cut);
            if (crosses && lcp[i] > best) {
                best = lcp[i];
                at = i;
            }
        }
        if (best == 0) {
            return "";
        }
        return joined.substring(sa[at], sa[at] + best);
    }
}
