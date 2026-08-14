package com.datastructure.sketch;

import java.util.HashSet;
import java.util.Set;

/**
 * HashSet 하나로 만든 정확한 카디널리티. 기준선이다.
 *
 * 여기서도 어려울 것이 없다. 그리고 여기서도 문제는 하나뿐이다.
 *
 *   본 것을 전부 들고 있어야 한다.
 *
 * 답으로 원하는 것은 숫자 하나인데, 그 숫자를 얻으려고 1억 개를 붙들고 있는다.
 * 원소를 다시 꺼낼 일이 없는데도 그렇다. 중복인지 아닌지를 판정하려면 비교 대상이 있어야 하니까.
 *
 * HyperLogLog 는 이 축을 통째로 잘라낸다. 원소를 안 담고 해시의 모양만 기억한다.
 */
public class ExactCardinality implements CardinalityEstimator {

    /**
     * 원소 하나가 대략 몇 바이트인가.
     *
     * HashSet 은 값이 더미인 HashMap 이다. Node 32 + 박싱된 Integer 16 = 48.
     * 여기서도 자릿수를 보려는 것이지 정확한 값이 아니다.
     */
    static final long BYTES_PER_ELEMENT = 48;

    private final Set<Integer> seen = new HashSet<>();

    @Override
    public void add(int item) {
        // TODO 1: 집합에 넣는다.
        //
        // 한 줄이다. 그리고 이 한 줄이 전부 기억한다는 뜻이다.
        // 채우고 나서 memoryBytes 가 무엇에 비례하는지 다시 보라.
        // **개수가 아니라 종류에 비례한다.** 그것이 이 문제의 출발점이다.
        throw new UnsupportedOperationException("TODO 1: add");
    }

    @Override
    public long estimate() {
        return seen.size();
    }

    @Override
    public long memoryBytes() {
        return (long) seen.size() * BYTES_PER_ELEMENT;
    }
}
