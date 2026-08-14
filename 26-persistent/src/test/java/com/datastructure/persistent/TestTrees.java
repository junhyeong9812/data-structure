package com.datastructure.persistent;

import java.util.ArrayList;
import java.util.List;

/**
 * 테스트가 함께 쓰는 결정적 트리 만들기.
 *
 * 무작위로 섞어 넣으면 모양이 매번 달라져 노드 수를 단언할 수 없다.
 * 가운데부터 넣으면 언제나 같은 모양이 나오고, n = 2^k - 1 일 때 완전 균형이다.
 * 그래야 "put 한 번이 만드는 노드가 정확히 몇 개인가"를 못 박을 수 있다.
 */
final class TestTrees {

    private TestTrees() {
    }

    /** [lo, hi] 의 가운데를 먼저, 그 다음 좌우를 같은 방식으로. */
    static void balancedOrder(int lo, int hi, List<Integer> out) {
        if (lo > hi) {
            return;
        }
        int mid = (lo + hi) >>> 1;
        out.add(mid);
        balancedOrder(lo, mid - 1, out);
        balancedOrder(mid + 1, hi, out);
    }

    static List<Integer> balancedOrder(int n) {
        List<Integer> out = new ArrayList<>(n);
        balancedOrder(0, n - 1, out);
        return out;
    }

    /**
     * 짝수 키 0, 2, ..., 2(n-1) 을 담은 완전 균형 맵.
     * 홀수 자리를 비워두는 이유는 나중에 키 하나를 끼워 넣어 보기 위해서다.
     */
    static PersistentTreeMap<Integer, String> balanced(int n) {
        PersistentTreeMap<Integer, String> map = PersistentTreeMap.empty();
        for (int x : balancedOrder(n)) {
            map = map.put(2 * x, "v" + (2 * x));
        }
        return map;
    }
}
