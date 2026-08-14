package com.datastructure.suffix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 접미사 배열. 접미사를 저장하지 않고 시작 위치만 사전순으로 정렬해 둔다.
 *
 * banana 의 접미사는 여섯 개다.
 *
 * <pre>
 *   0 banana    사전순으로 정렬하면    5 a
 *   1 anana                          3 ana
 *   2 nana                           1 anana
 *   3 ana                            0 banana
 *   4 na                             4 na
 *   5 a                              2 nana
 * </pre>
 *
 * 남는 것은 오른쪽 열의 숫자 6개다. 문자열은 하나도 새로 만들지 않는다.
 * 이것이 O(n) 공간의 정체다. 09번 접미사 트라이는 같은 정보를 노드 n(n+1)/2 개로 들고 있었다.
 *
 * <h2>배가법 (prefix doubling)</h2>
 *
 * 나이브는 비교마다 글자를 끝까지 훑어 O(n^2 log n) 이었다.
 * 배가법은 이미 매긴 순위를 재활용한다.
 *
 * <pre>
 *   1글자 순위를 매긴다            a=0, b=1, n=2
 *   (내 순위, k칸 뒤 순위) 로 정렬  이러면 2k 글자까지 본 셈이 된다
 *   새 순위를 매긴다                같은 쌍이면 같은 순위
 *   k 를 2배로 한다                 1, 2, 4, 8 ... n 을 넘으면 끝
 * </pre>
 *
 * 정렬을 log n 번 하고 비교는 정수 두 개 비교라 상수다. O(n log^2 n) 이다.
 * (기수 정렬을 쓰면 O(n log n) 이 되지만 여기서는 Arrays.sort 로 충분하다)
 *
 * <h2>대가</h2>
 *
 * 트라이는 그림이 그려진다. 접미사 배열은 안 그려진다.
 * 검색도 트라이가 O(m) 인데 여기서는 O(m log n) 이다. 이진 탐색을 하기 때문이다.
 */
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

        // TODO 4: 배가법 본체.
        //
        //   1. sa 를 0, 1, 2 ... n-1 로 채운다. rank 는 initialRanks 로 시작한다.
        //   2. k 를 1 부터 2배씩 늘리며 반복한다.
        //      a. sa 를 comparePair(rank, a, b, k) 로 정렬한다.
        //         **int[] 는 Comparator 로 정렬할 수 없다.** Integer[] 에 옮겨 담아 정렬하고
        //         다시 sa 로 옮긴다. (이 박싱이 O(n log^2 n) 의 상수를 키우는 지점이다)
        //      b. sortRounds 를 1 늘린다. 한계 측정이 이 값을 본다.
        //      c. rank = reRank(sa, rank, k) 로 순위를 갱신한다.
        //      d. **끝났는지 본다. 순위가 전부 다르면 끝이다.**
        //         마지막 접미사의 순위가 n-1 이면 0..n-1 이 한 번씩 쓰였다는 뜻이다.
        //         이걸 k >= n 으로 대신해도 **답은 맞다.** 헛도는 라운드가 붙을 뿐이다.
        //         (변종으로 바꿔봤더니 90개 중 4개, 전부 라운드 수를 세는 테스트만 무너졌다)
        //
        // **람다가 잡는 변수는 사실상 final 이어야 한다.** rank 와 k 를 그대로 쓰면 컴파일이 안 된다.
        // 루프 안에서 지역 변수로 한 번 받아라.
        //
        // 순서를 조심하라. **정렬 -> 새 순위 -> 종료 검사**다.
        // 정렬하기 전 순위로 종료를 판단하면 한 라운드 일찍 멈춘다.
        throw new UnsupportedOperationException("TODO 4: 배가법 구축");
    }

    /** 1글자 순위. 문자 코드를 그대로 쓴다. */
    static int[] initialRanks(String text) {
        // TODO 1: rank[i] = i 번째 글자의 코드.
        //
        // 0, 1, 2 로 압축할 필요가 없다. **대소 관계만 맞으면 되기 때문이다.**
        // char 를 int 로 올리면 그게 곧 사전순 대소다.
        throw new UnsupportedOperationException("TODO 1: initialRanks");
    }

    /**
     * (rank[i], rank[i+k]) 와 (rank[j], rank[j+k]) 를 비교한다.
     *
     * 이것이 "앞 2k 글자를 비교한다"와 같은 뜻이다. rank 가 이미 k 글자를 요약하고 있기 때문이다.
     */
    static int comparePair(int[] rank, int i, int j, int k) {
        // TODO 2: 앞 순위부터 본다. 다르면 그것으로 끝, 같으면 k 칸 뒤 순위를 본다.
        //
        // **k 칸 뒤가 문자열 밖이면 -1 로 친다.** 이게 이 문제에서 제일 자주 틀리는 곳이다.
        // 0 으로 두면 문자열이 끝난 접미사가 rank 0 인 접미사와 같아진다.
        // -1 이어야 "먼저 끝난 쪽이 사전순으로 앞"이 된다. "a" 가 "ab" 보다 앞인 이유다.
        throw new UnsupportedOperationException("TODO 2: comparePair");
    }

    /**
     * 정렬된 sa 를 훑으며 같은 쌍끼리 같은 순위로 묶는다. 반환값은 위치별 새 순위다.
     *
     * 배가법에서 제일 틀리기 쉬운 곳이다.
     */
    static int[] reRank(int[] sa, int[] rank, int k) {
        // TODO 3: next[sa[0]] = 0 에서 시작해 앞에서부터 채운다.
        //
        //   next[sa[i]] = next[sa[i-1]] + (앞 것보다 크면 1, 같으면 0)
        //
        // **"같으면 0" 이 핵심이다.** 그냥 i 를 넣으면 같은 쌍이 다른 순위를 받고,
        // 다음 라운드의 비교가 통째로 틀어진다. banana 를 2글자까지 본 순서는
        // a / an / an / ba / na / na 이고 an 둘, na 둘은 **같은 순위여야 한다.**
        //
        // 크고 작음은 comparePair 로 판단하라. 여기서 직접 비교를 다시 쓰면 두 곳이 어긋난다.
        // (< 0 대신 != 0 을 써도 같다. 이미 정렬한 뒤라 앞 것이 뒤 것보다 클 수 없다.
        //  변종으로 바꿔봤더니 90개가 다 통과했다)
        throw new UnsupportedOperationException("TODO 3: reRank");
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

    /** 사전순 rank 번째 접미사. 테스트와 눈으로 보기용이다. */
    public String suffixAt(int rank) {
        if (rank < 0 || rank >= sa.length) {
            throw new IndexOutOfBoundsException(
                    "순위 " + rank + " 가 범위를 벗어났다 (크기 " + sa.length + ")");
        }
        return text.substring(sa[rank]);
    }

    /**
     * 위치 start 에서 시작하는 접미사의 앞부분을 pattern 과 비교한다.
     *
     * 접미사가 pattern 으로 시작하면 0 이다. 접미사 전체와 비교하는 것이 아니다.
     * 그래야 "pattern 으로 시작하는 접미사들"이 한 덩어리로 묶인다.
     */
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

    /** pattern 으로 시작하는 접미사가 처음 나오는 순위. 없으면 들어갈 자리. */
    int lowerBound(String pattern) {
        // TODO 5: 이진 탐색. comparePrefix 가 음수인 동안 오른쪽으로 간다.
        //
        //   lo = 0, hi = n 으로 시작해 lo < hi 인 동안
        //     mid 를 보고 comparePrefix(sa[mid], pattern) < 0 이면 lo = mid + 1, 아니면 hi = mid
        //
        // **찔러볼 때마다 searchProbes 를 1 늘려라.** 한계 측정이 이 값을 센다.
        // mid 는 (lo + hi) >>> 1 로 구한다. (lo + hi) / 2 는 큰 배열에서 넘칠 수 있다.
        throw new UnsupportedOperationException("TODO 5: lowerBound");
    }

    /** pattern 으로 시작하는 접미사가 끝나는 자리(마지막 다음). */
    int upperBound(String pattern) {
        // TODO 6: lowerBound 와 딱 한 글자 다르다.
        //
        // **<= 0 이면 오른쪽으로 간다.** 즉 "패턴으로 시작하는 것"도 넘어간다.
        // 그래야 [lowerBound, upperBound) 가 패턴으로 시작하는 접미사 전부가 된다.
        // 여기서 < 0 을 쓰면 lowerBound 와 같은 값이 나와 개수가 늘 0 이 된다.
        throw new UnsupportedOperationException("TODO 6: upperBound");
    }

    /** pattern 이 나타나는 모든 위치를 오름차순으로. */
    public List<Integer> find(String pattern) {
        requirePattern(pattern);
        searchProbes = 0;
        // TODO 7: 두 경계 사이의 sa 값을 모은다.
        //
        // **그대로 주면 안 된다.** sa 는 사전순이라 banana 에서 "a" 를 찾으면 5, 3, 1 순이다.
        // 계약은 위치 오름차순이다. 모은 뒤 정렬하라.
        //
        // 개수가 k 개면 O(m log n + k log k) 다. 트라이는 O(m + k) 였다.
        // **이진 탐색으로 바꾼 대가가 여기 있다.**
        throw new UnsupportedOperationException("TODO 7: find");
    }

    /**
     * pattern 이 한 번이라도 나오는가.
     *
     * find 를 부르면 위치를 다 모아야 한다. 있는지만 알면 될 때는 경계 두 개면 충분하다.
     */
    public boolean contains(String pattern) {
        requirePattern(pattern);
        searchProbes = 0;
        return upperBound(pattern) > lowerBound(pattern);
    }

    /** pattern 이 나오는 횟수. 위치를 모으지 않고 뺄셈 한 번으로 답한다. */
    public int count(String pattern) {
        requirePattern(pattern);
        searchProbes = 0;
        return upperBound(pattern) - lowerBound(pattern);
    }

    /** 배가 단계에서 정렬한 횟수. ceil(log2 n) 안쪽이어야 한다. 한계 측정용이다. */
    public int sortRounds() {
        return sortRounds;
    }

    /** 마지막 find/contains/count 가 이진 탐색으로 찔러본 횟수. 한계 측정용이다. */
    public int lastSearchProbes() {
        return searchProbes;
    }

    /** 접미사 배열이 쓰는 바이트. int 하나가 4바이트, 그게 전부다. */
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
