package com.datastructure.searchindex;

/**
 * AND 질의에서 포스팅 리스트를 어느 순서로 병합할지.
 *
 * 답은 어느 쪽이든 같다. 다른 것은 비교 횟수뿐이다.
 * 그래서 이 선택은 무작위 대조로는 절대 안 잡히고, 비교 횟수를 세는 측정으로만 드러난다.
 * MeasurementTest 가 그 자 역할을 한다.
 *
 * 이 enum 에는 TODO 가 없다.
 */
public enum MergeOrder {

    /** 짧은 리스트부터. 중간 결과가 작게 시작해서 작게 유지된다. */
    SHORTEST_FIRST,

    /** 질의에 적힌 순서 그대로. 흔한 말을 먼저 쓰면 첫 병합부터 긴 리스트를 통째로 훑는다. */
    QUERY_ORDER
}
