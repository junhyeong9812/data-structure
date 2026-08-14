package com.datastructure.interval;

import java.util.List;

/**
 * 구간을 담고 "무엇이 겹치나"를 묻는 자료구조.
 *
 * <h2>13번, 25번과 무엇이 다른가</h2>
 *
 * 13번 세그먼트 트리는 고정된 배열의 값을 담고 인덱스 구간의 집계를 물었다.
 * 배열이 먼저 있고 구간은 질의였다. 25번 KD-트리는 점을 담고 사각형 안의 점을 물었다.
 *
 * 여기서는 구간 자체가 자료다. 그리고 겹침을 묻는다.
 *
 * | | 무엇을 담나 | 무엇을 묻나 |
 * |---|---|---|
 * | 13번 세그먼트 트리 | 배열의 값 | 인덱스 구간의 집계 |
 * | 25번 KD-트리 | 점 | 사각형 안의 점, 가까운 점 |
 * | 30번 인터벌 트리 | 구간 | 겹치는 구간 |
 *
 * <h2>계약</h2>
 *
 * 구간은 반개구간 [start, end) 다. Interval 의 설명을 먼저 읽어라.
 * 중복은 담지 않는다. 같은 구간을 두 번 넣으면 두 번째는 false 다.
 * 그래서 두 구현이 전부 집합이고, 서로 답을 대조할 수 있다.
 *
 * 좌표는 long 이다. 부동소수점을 쓰면 같은 구간인지가 흔들려서
 * 두 구현의 답을 비교하는 이 문제집의 주력 검증이 성립하지 않는다.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface IntervalStore {

    /**
     * 구간을 넣는다. 실제로 늘어났으면 true.
     *
     * 이미 같은 구간이 있으면 false. iv 가 null 이면 IllegalArgumentException.
     */
    boolean insert(Interval iv);

    /**
     * 그 구간을 지운다. 실제로 줄었으면 true.
     *
     * 겹치는 것이 아니라 같은 것을 지운다. iv 가 null 이면 IllegalArgumentException.
     */
    boolean remove(Interval iv);

    /** 담긴 구간의 개수. */
    int size();

    /** 전부 비운다. */
    void clear();

    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * query 와 겹치는 구간 하나. 없으면 null.
     *
     * 겹치는 것이 여럿이면 그중 무엇이 나오든 맞는 답이다.
     * 그래서 테스트는 어느 구간인지가 아니라 겹치는지를 본다.
     *
     * query 가 null 이면 IllegalArgumentException.
     */
    Interval findAny(Interval query);

    /**
     * query 와 겹치는 구간 전부.
     *
     * 순서는 정하지 않는다. 구현마다 다르게 나오는 것이 정상이고,
     * 그것을 계약으로 만들면 트리가 방문 순서를 못 바꾼다. 테스트는 정렬해서 비교한다.
     *
     * query 가 null 이면 IllegalArgumentException.
     */
    List<Interval> findAll(Interval query);

    /** query 와 겹치는 것이 하나라도 있나. query 가 null 이면 IllegalArgumentException. */
    boolean anyOverlaps(Interval query);

    /**
     * 담긴 구간 전부를 start 오름차순으로. start 가 같으면 end 오름차순.
     *
     * findAll 과 달리 이쪽은 순서가 계약이다. 구조가 성한지 밖에서 볼 수 있는 창이다.
     */
    List<Interval> toList();
}
