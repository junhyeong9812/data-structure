package com.datastructure.suffix;

/**
 * LCP 배열. 이웃한 두 접미사가 앞에서 몇 글자까지 같은가.
 *
 * banana 의 접미사 배열은 5, 3, 1, 0, 4, 2 이고 접미사는 a, ana, anana, banana, na, nana 다.
 *
 * <pre>
 *   순위  접미사    앞 순위와 겹치는 길이
 *    0    a         0   (앞이 없다)
 *    1    ana       1   a
 *    2    anana     3   ana
 *    3    banana    0
 *    4    na        0
 *    5    nana      2   na
 * </pre>
 *
 * 접미사 배열만으로는 "정렬됐다"까지다. 여기에 LCP 를 얹으면 문자열 문제 상당수가 뺄셈이 된다.
 *
 *   서로 다른 부분 문자열 수 = n(n+1)/2 - sum(lcp)
 *   가장 긴 반복 부분 문자열 = lcp 의 최댓값
 *
 * <h2>Kasai 알고리즘</h2>
 *
 * 이웃끼리 앞에서부터 세면 O(n^2) 다. Kasai 는 O(n) 이다. 요령은 훑는 순서다.
 *
 * 순위 순서가 아니라 원문 순서(0, 1, 2 ...)로 간다. 그러면 이런 성질이 성립한다.
 *
 *   위치 i 의 LCP 가 h 였다면, 위치 i+1 의 LCP 는 최소 h-1 이다.
 *
 * 앞 글자 하나를 떼도 나머지는 그대로 같기 때문이다. 그래서 h 를 0 으로 되돌리지 않고
 * 1만 빼고 이어서 센다. h 는 전체에서 최대 n 번 늘고 n 번 줄어 합이 O(n) 이다.
 * 이것이 상환 분석(amortized analysis)의 교과서 예다.
 */
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

    /**
     * 접미사 배열을 뒤집는다. rank[p] = 위치 p 에서 시작하는 접미사의 순위.
     *
     * sa 는 "순위 -> 위치"이고 이것은 "위치 -> 순위"다. Kasai 가 원문 순서로 가려면 이게 필요하다.
     */
    public static int[] inverse(int[] suffixArray) {
        // TODO 1: rank[sa[i]] = i.
        //
        // 한 줄이다. 그런데 **양변을 바꿔 쓰기 쉽다.** rank[i] = sa[i] 는 뒤집기가 아니다.
        // 뒤집은 것을 또 뒤집으면 제자리로 와야 한다. 그게 검사다.
        throw new UnsupportedOperationException("TODO 1: inverse");
    }

    private int[] kasai() {
        int n = sa.length;
        int[] out = new int[n];
        if (n == 0) {
            return out;
        }
        // TODO 2: Kasai 본체.
        //
        //   rank = inverse(sa) 를 구하고 h = 0 으로 시작한다.
        //   i 를 0 부터 n-1 까지 **원문 순서로** 돈다.
        //
        //     - rank[i] == 0 이면 앞 이웃이 없다. h = 0 으로 두고 넘어간다.
        //       사실 **이 자리에서 h 는 이미 0 이다.** (여기 오는 접미사가 사전순 최소라
        //       h >= 1 이면 자기보다 작은 접미사가 있다는 뜻이 되어 모순이다)
        //       변종으로 지워봤더니 90개가 다 통과했다. 그래도 남긴다. 읽는 사람이 그걸 모른다.
        //     - 아니면 앞 이웃 j = sa[rank[i] - 1] 을 잡고
        //       text[i+h] 와 text[j+h] 를 **h 부터** 비교해 나간다. (0 부터가 아니다)
        //       비교할 때마다 charComparisons 를 1 늘려라. 한계 측정이 이 값을 본다.
        //     - out[rank[i]] = h 로 적고, **h 가 0 보다 크면 1 줄인다.**
        //
        // 1 을 빼는 이유가 이 알고리즘의 전부다. 위치 i 에서 h 글자가 같았다면
        // 위치 i+1 은 **적어도 h-1 글자가 같다.** 앞 글자 하나만 떨어져 나갔기 때문이다.
        // 그 사실을 안 쓰고 매번 0 부터 세면 답은 같고 비용만 O(n^2) 가 된다.
        // (그러면 Amortized 테스트가 잡는다. 정답 구현은 길이 2000 에 1,999 걸음이다)
        //
        // 순서를 조심하라. **적고 나서 줄인다.** 줄이고 적으면 전부 1씩 작게 나온다.
        throw new UnsupportedOperationException("TODO 2: kasai");
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

    /** LCP 전체의 합. long 이다. 길이 10만이면 int 를 넘길 수 있다. */
    public long sum() {
        long total = 0;
        for (int v : lcp) {
            total += v;
        }
        return total;
    }

    /** 가장 긴 공통 접두사 길이. 곧 "가장 긴 반복 부분 문자열"의 길이다. */
    public int max() {
        int best = 0;
        for (int v : lcp) {
            if (v > best) {
                best = v;
            }
        }
        return best;
    }

    /** 최댓값이 있는 순위. 동률이면 앞선 순위. 비었으면 -1. */
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

    /** 만드는 동안 실제로 훑어본 글자 수. 한계 측정용이다. */
    public long charComparisons() {
        return charComparisons;
    }
}
