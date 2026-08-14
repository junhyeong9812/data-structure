package com.datastructure.bitset;

import java.util.ArrayList;
import java.util.List;

/**
 * 비트셋으로 푸는 문제 셋.
 */
public final class BitSetProblems {

    private BitSetProblems() {
    }

    /**
     * 문제 1: n 이하의 소수를 전부 찾는다. 반환한 비트셋의 i번 비트가 켜져 있으면 i 가 소수다.
     *
     * **비트셋의 고전적인 쓰임이다.** 100만까지면 boolean[] 은 1MB, 비트셋은 125KB 다.
     * 캐시에 들어가느냐 마느냐가 갈리는 크기다.
     */
    public static BitVector sieve(int n) {
        if (n < 2) {
            throw new IllegalArgumentException("n 은 2 이상이어야 한다: " + n);
        }
        // TODO 1: 2부터 n 까지 전부 켜두고 배수를 지운다.
        //
        //   0 과 1 은 소수가 아니므로 처음부터 켜지 않는다.
        //   p 가 아직 켜져 있으면 소수다. **p*p 부터** p 씩 늘려가며 끈다.
        //
        // **왜 2p 가 아니라 p*p 부터인가.** 2p, 3p, ... (p-1)p 는 더 작은 소수가 이미 지웠다.
        // 5의 배수를 지울 때 10, 15, 20 은 2와 3이 이미 지웠고 25부터가 새것이다.
        //
        // **왜 p*p <= n 까지만 도는가.** p 가 sqrt(n) 을 넘으면 p*p 가 n 을 넘어 지울 것이 없다.
        //
        // 함정: `p * p` 는 int 를 넘칠 수 있다. n 이 크면 (long) 으로 올려 비교하라.
        throw new UnsupportedOperationException("TODO 1: sieve");
    }

    public static double jaccard(BitVector a, BitVector b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("두 집합이 모두 필요하다");
        }
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("크기가 다르다: " + a.size() + " 대 " + b.size());
        }
        // TODO 2: 교집합 크기 나누기 합집합 크기.
        //
        // **둘 다 비었으면 0/0 이다.** 그대로 두면 NaN 이 나오고,
        // NaN 은 어떤 비교와도 false 라 "유사도 0.8 이상" 같은 필터가 조용히 전부 걸러낸다.
        // "빈 것끼리는 같다"로 보고 1.0 을 주는 것이 관례다. **정하지 않으면 안 된다.**
        //
        // 그리고 정수 나눗셈을 조심하라(07, 10번에서 이미 나왔다).
        throw new UnsupportedOperationException("TODO 2: jaccard");
    }

    /**
     * 문제 3: 마스크의 **모든 부분집합**을 내림차순으로 열거한다.
     *
     * 0부터 mask 까지 전부 돌며 `(i & mask) == i` 를 검사하면 2^32 번이다.
     * 아래 관용구는 **부분집합 개수만큼만** 돈다. 켜진 비트가 4개면 16번이다.
     *
     * 쓰이는 곳: 비트마스크 DP(외판원 문제, 집합 분할), 조합 최적화.
     */
    public static List<Integer> enumerateSubsets(int mask) {
        if (mask < 0) {
            throw new IllegalArgumentException("마스크는 0 이상이어야 한다: " + mask);
        }
        // TODO 3: `s = (s - 1) & mask` 를 mask 부터 시작해 0 이 될 때까지 돈다.
        //
        // **왜 되는가.** s-1 은 s 의 가장 낮은 1비트를 0으로 만들고 그 아래를 전부 1로 만든다.
        // 거기에 mask 를 and 하면 **마스크 안에서 바로 다음으로 작은 부분집합**이 나온다.
        //
        // 함정: 0 도 부분집합이므로 담아야 하는데, 0에서 (0-1)&mask 를 하면 다시 mask 가 되어
        // **무한 루프**가 된다. 담은 뒤에 0인지 보고 빠져나가야 한다.
        // (for 의 조건에 `s != 0` 을 넣으면 0을 못 담는다)
        throw new UnsupportedOperationException("TODO 3: enumerateSubsets");
    }
}
