package com.datastructure.suffix;

import java.util.Arrays;

/**
 * 접미사를 문자열로 비교해서 정렬한다. 기준선이다.
 *
 * 만드는 데 다섯 줄이면 되고 답도 맞다. 그런데 못 쓴다.
 * 정렬은 O(n log n) 번 비교하는데 비교 하나가 O(n) 이라 전체가 O(n^2 log n) 이다.
 *
 * 왜 그런지 보라. 접미사 "aaaa...a" 와 "aaa...a" 를 비교하면 짧은 쪽이 끝날 때까지 전부 같다.
 * 즉 비교 한 번이 문자열 하나를 통째로 읽는다.
 *
 * 여기에 접미사를 substring 으로 만들어 담기까지 하면 메모리도 O(n^2) 가 된다.
 * (이 구현은 원문 인덱스로 비교해 그것만은 피한다)
 *
 * 먼저 이걸 만들어보고 무엇이 문제인지 숫자로 본 다음 SuffixArray 로 간다.
 * charComparisons() 가 그 숫자다.
 */
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

        // 정렬 자체는 자바에 맡긴다. 이 구현의 비용은 정렬이 아니라 **비교**에 있다.
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        Arrays.sort(order, this::compareSuffixes);
        for (int i = 0; i < n; i++) {
            sa[i] = order[i];
        }
    }

    /**
     * 위치 i 에서 시작하는 접미사와 위치 j 에서 시작하는 접미사를 사전순으로 비교한다.
     *
     * 음수면 i 쪽이 앞, 양수면 j 쪽이 앞, 0 이면 같다.
     * (접미사는 길이가 전부 달라서 0 이 나올 경우는 i == j 뿐이다)
     */
    int compareSuffixes(int i, int j) {
        // TODO 1: 두 접미사를 앞에서부터 한 글자씩 비교한다.
        //
        //   1. 양쪽 다 남아 있는 동안 글자를 하나씩 본다. **볼 때마다 charComparisons 를 1 늘린다.**
        //      이 숫자가 이 구현이 존재하는 이유다. 늘리는 걸 빠뜨리면 한계 측정이 의미를 잃는다.
        //   2. 다르면 거기서 끝이다. 작은 쪽이 앞이다.
        //   3. 한쪽이 먼저 끝나면 **짧은 쪽이 앞이다.** "a" 가 "ab" 보다 앞이다.
        //      남은 길이는 각각 n - i 와 n - j 다.
        //
        // substring 을 만들어 compareTo 를 부르면 답은 같지만 **비교마다 문자열을 새로 만든다.**
        // 그러면 메모리까지 O(n^2) 가 된다. 원문 인덱스로 훑어라.
        throw new UnsupportedOperationException("TODO 1: compareSuffixes");
    }

    public int[] toArray() {
        return sa.clone();
    }

    public int size() {
        return sa.length;
    }

    /** 만드는 동안 실제로 훑어본 글자 수. 한계 측정용이다. */
    public long charComparisons() {
        return charComparisons;
    }

    public static int[] of(String text) {
        return new NaiveSuffixArray(text).toArray();
    }
}
