package com.datastructure.sparsetable;

/**
 * 구간에 대한 질문에 답하는 자료구조. 값은 바꿀 수 없다.
 *
 * 13번 RangeQuery 와 비교해보라. update 가 없다.
 * 빠진 것이 아니라 일부러 버린 것이고, 그 대가로 조회가 O(1) 이 된다.
 *
 * | | 조회 | 갱신 | 전처리 | 메모리 |
 * |---|---|---|---|---|
 * | 세그먼트 트리(13번) | O(log n) | O(log n) | O(n) | 4n |
 * | 펜윅 트리(17번) | O(log n) | O(log n) | O(n) | n + 1 |
 * | 희소 테이블 | O(1) | 없다 | O(n log n) | n log n |
 *
 * 셋 중 무엇을 고를지는 조회와 갱신의 비율이 정한다.
 * 갱신이 한 번도 없고 조회가 백만 번이면 log n 배가 그대로 이득이다.
 * 갱신이 한 번이라도 있으면 전체를 다시 지어야 하므로 O(n log n) 을 문다.
 *
 * 그런데 아무 연산이나 되지 않는다. 그것이 이 문제의 중심이다.
 * 13번은 결합법칙(모노이드)을 요구했고, 17번은 역연산을 요구했다.
 * 여기서는 멱등성을 요구한다. f(x, x) = x.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface StaticRangeQuery {

    /** 원소 개수. */
    int size();

    /** from 번째부터 to 번째까지(양끝 포함)를 하나로 접은 값. from > to 면 항등원. */
    long query(int from, int to);

    /** index 번째의 값. 만든 뒤로 바뀌지 않는다. */
    long get(int index);
}
