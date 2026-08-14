package com.datastructure.segment;

/**
 * 구간에 대한 질문에 답하는 자료구조. 값도 바꿀 수 있다.
 *
 * "3번부터 7번까지의 합" 하나만 필요하면 세그먼트 트리는 과하다.
 * 누적합(prefix sum)을 미리 만들어두면 조회가 O(1) 이다.
 *
 *   prefix[i] = a[0] + ... + a[i-1]
 *   sum(l, r) = prefix[r+1] - prefix[l]
 *
 * 문제는 값이 바뀔 때다. a[2] 를 고치면 prefix[3] 부터 끝까지 전부 다시 계산해야 한다. O(n) 이다.
 *
 * | | 조회 | 갱신 | 언제 쓰나 |
 * |---|---|---|---|
 * | 매번 훑기 | O(n) | O(1) | 갱신이 압도적으로 많을 때 |
 * | 누적합 | O(1) | O(n) | 값이 안 바뀔 때 |
 * | 세그먼트 트리 | O(log n) | O(log n) | 둘 다 자주 일어날 때 |
 * | 펜윅 트리(17번) | O(log n) | O(log n) | 합만 필요할 때. 코드가 훨씬 짧다 |
 *
 * 어느 쪽이 나은지는 조회와 갱신의 비율이 정한다. 세그먼트 트리는 양쪽을 다 걸고 넘어지는 답이다.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface RangeQuery {

    /** 원소 개수. */
    int size();

    /** index 번째 값을 value 로 바꾼다. */
    void update(int index, long value);

    /** from 번째부터 to 번째까지(양끝 포함)를 하나로 접은 값. from > to 면 항등원. */
    long query(int from, int to);

    /** index 번째의 현재 값. */
    long get(int index);
}
