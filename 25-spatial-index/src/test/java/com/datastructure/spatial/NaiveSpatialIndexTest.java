package com.datastructure.spatial;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 전수 조사 기준선. 이 구현이 이 문제집의 정답 판정 기준이다.
 *
 * 느린 것은 흠이 아니다. 흠은 틀리는 것이다.
 * 두 트리는 이것과 답이 같아야 하고, 이것보다 훨씬 적게 훑어야 한다.
 */
@DisplayName("NaiveSpatialIndex (전수 조사)")
class NaiveSpatialIndexTest extends SpatialIndexContractTest {

    @Override
    protected SpatialIndex create() {
        return new NaiveSpatialIndex();
    }

    @Test
    @DisplayName("질의 한 번에 정확히 n 개를 훑는다")
    void everyQueryScansEverything() {
        // 이 수가 비교 기준이다. 트리가 몇 배를 아끼는지는 전부 이 n 에 대한 비율이다.
        NaiveSpatialIndex index = new NaiveSpatialIndex();
        for (Point2D p : new TestSupport.Dice(7L).points(500, 1000)) index.insert(p);
        int n = index.size();

        index.resetVisits();
        index.rangeSearch(new Rectangle(0, 0, 10, 10));
        assertEquals(n, index.visits(), "범위 조회 한 번이 n");

        index.resetVisits();
        index.nearest(new Point2D(500, 500));
        assertEquals(n, index.visits(), "최근접 한 번도 n");

        index.resetVisits();
        index.nearestK(new Point2D(500, 500), 10);
        assertEquals(n, index.visits(), "k 개를 찾아도 n. 사각형이 작든 크든 언제나 n 이다");

        index.resetVisits();
        for (int i = 0; i < 100; i++) index.nearest(new Point2D(i, i));
        assertEquals(100L * n, index.visits(), "질의 100 번이면 100n");
    }
}
