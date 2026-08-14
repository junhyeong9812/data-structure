package com.datastructure.suffix;

/**
 * 접미사 배열 + LCP 로 푸는 문제 셋.
 *
 * 셋 다 접미사 배열 없이도 풀린다. 다만 길이 10만에서는 그 방법들이 안 돌아간다.
 */
public final class SuffixArrayProblems {

    /**
     * 두 문자열을 이을 때 끼우는 구분자.
     *
     * 입력에 나올 리 없는 제어 문자를 쓴다. 문자 코드가 작아야 하는 것은 아니지만,
     * 유일해야 하는 것은 절대 조건이다. 왜인지는 문제 3에서 본다.
     */
    static final char SEPARATOR = (char) 1;

    private SuffixArrayProblems() {
    }

    /**
     * 문제 1: 문자열 s 의 서로 다른 부분 문자열 개수. (빈 문자열은 세지 않는다)
     *
     * 09번 문제 3번과 같은 문제다. 거기서는 모든 접미사를 트라이에 밀어 넣고
     * 만들어진 노드를 셌다. 답은 맞지만 노드가 최대 n(n+1)/2 개라
     * 길이 10만이면 50억 개가 필요해 아예 못 만든다.
     *
     * 여기서는 뺄셈 한 번이다.
     *
     * <pre>
     *   전체 부분 문자열 개수(중복 포함) = n(n+1)/2
     *   중복해서 센 것                  = sum(lcp)
     * </pre>
     *
     * 왜 sum(lcp) 인지 보라. 접미사 하나가 부분 문자열 (길이)개를 만든다.
     * 사전순으로 이웃한 접미사끼리는 앞 lcp 글자로 시작하는 것들이 겹친다.
     * 정렬해 두면 겹치는 상대가 바로 앞 하나뿐이라 한 번씩만 빼면 된다.
     */
    public static long countDistinctSubstrings(String s) {
        // TODO 1: n(n+1)/2 - sum(lcp).
        //
        // s 가 null 이거나 비었으면 0.
        //
        // **long 으로 계산하라.** n 이 10만이면 n(n+1)/2 = 5,000,050,000 이라 int 를 넘는다.
        // n 을 int 로 두고 n * (n + 1) / 2 를 쓰면 조용히 음수가 나온다.
        // (09번은 int 를 반환했다. 그때는 길이 1000 이 한계라 문제가 안 됐다)
        throw new UnsupportedOperationException("TODO 1: countDistinctSubstrings");
    }

    /**
     * 문제 2: 두 번 이상 나오는 가장 긴 부분 문자열. 없으면 빈 문자열.
     *
     * 겹쳐도 두 번으로 센다. "aaa" 에서 "aa" 는 위치 0 과 1 에 있다.
     * 답이 여럿이면 사전순으로 앞선 것을 준다.
     */
    public static String longestRepeatedSubstring(String s) {
        // TODO 2: LCP 배열의 최댓값이 곧 답의 길이다.
        //
        // 두 번 나오는 부분 문자열은 **접미사 두 개의 공통 접두사**다.
        // 그리고 접미사 배열이 정렬돼 있으니 가장 많이 겹치는 두 접미사는 반드시 이웃이다.
        // 그래서 이웃만 보는 lcp 로 충분하다.
        //
        //   길이 = lcp.max(), 시작 위치 = sa[lcp.argMax()]
        //
        // 최댓값이 0 이면 반복이 없다. 빈 문자열이다.
        // s 가 null 이거나 2글자 미만이어도 빈 문자열이다.
        //
        // **동률 처리가 argMax 에 이미 들어 있다.** 앞선 순위를 고르면 접미사 배열이 사전순이라
        // 자연히 사전순으로 앞선 답이 나온다. 뒤에서부터 찾으면 사전순 마지막이 나온다.
        throw new UnsupportedOperationException("TODO 2: longestRepeatedSubstring");
    }

    /**
     * 문제 3: a 와 b 에 둘 다 들어 있는 가장 긴 부분 문자열. 없으면 빈 문자열.
     *
     * 답이 여럿이면 사전순으로 앞선 것을 준다.
     * a 나 b 가 null 이면 거부하고, 둘 중 하나가 비었으면 빈 문자열이다.
     */
    public static String longestCommonSubstring(String a, String b) {
        // TODO 3: a + SEPARATOR + b 로 이어붙이고 접미사 배열과 LCP 를 만든다.
        //
        // 그 다음 이웃한 접미사 쌍 중에서 **서로 다른 쪽에서 온 것**만 보고 lcp 의 최댓값을 잡는다.
        // 시작 위치가 a.length() 보다 작으면 a 쪽, 크면 b 쪽이다.
        //
        // **구분자가 왜 필요한가.** 그냥 a + b 로 이으면 경계가 사라진다.
        // a = "a", b = "aa" 를 이으면 "aaa" 가 되고, 경계를 넘는 접미사 쌍이
        // "aa" 를 공통이라고 보고한다. **a 에는 "aa" 가 없다.** 답이 조용히 틀린다.
        //
        // 구분자를 끼우면 공통 접두사가 거기서 반드시 끊긴다.
        // a 쪽 접미사는 언젠가 구분자를 만나고 b 쪽 접미사에는 구분자가 없기 때문이다.
        // 그래서 **입력에 구분자가 들어 있으면 거부해야 한다.** 유일하지 않으면 보장이 깨진다.
        //
        // 구분자 자체에서 시작하는 접미사(위치 a.length())는 a 도 b 도 아니다.
        // 그런데 그 접미사의 lcp 는 늘 0 이다. 구분자로 시작하는 접미사가 그것 하나뿐이라
        // **따로 걸러내지 않아도 최댓값이 될 수 없다.** (걸러도 되고, 안 걸러도 답은 같다)
        throw new UnsupportedOperationException("TODO 3: longestCommonSubstring");
    }
}
