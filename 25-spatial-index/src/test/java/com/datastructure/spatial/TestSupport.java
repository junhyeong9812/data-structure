package com.datastructure.spatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 테스트가 공유하는 도구. 자료구조가 아니라 검증 장비다.
 *
 * 여기 있는 것은 셋뿐이다.
 *   1. 결정적 난수      - Random 을 쓰면 JDK 가 바뀔 때 기댓값이 흔들린다
 *   2. 점 목록 정렬     - rangeSearch 의 순서는 계약이 아니므로 비교 전에 정렬한다
 *   3. 제곱거리 수열    - nearest 는 동점이면 답이 여럿이다. 점이 아니라 거리를 비교한다
 */
final class TestSupport {

    private TestSupport() {
    }

    /** x, y 순 정렬. 순서를 계약으로 만들지 않으려고 비교 직전에만 쓴다. */
    static List<Point2D> sorted(List<Point2D> points) {
        List<Point2D> copy = new ArrayList<>(points);
        copy.sort(Comparator.comparingInt((Point2D p) -> p.x).thenComparingInt(p -> p.y));
        return copy;
    }

    /** target 에서의 제곱거리 수열. 동점이 있어도 이 수열은 유일하다. */
    static List<Long> distances(Point2D target, List<Point2D> points) {
        List<Long> out = new ArrayList<>();
        for (Point2D p : points) out.add(target.squaredDistanceTo(p));
        return out;
    }

    /** 결정적 난수 발생기. 같은 seed 면 언제 어디서 돌려도 같은 점이 나온다. */
    static final class Dice {
        private long state;

        Dice(long seed) {
            this.state = seed;
        }

        int next(int bound) {
            state = state * 6364136223846793005L + 1442695040888963407L;
            return (int) Math.floorMod(state >>> 33, bound);
        }

        Point2D point(int range) {
            return new Point2D(next(range), next(range));
        }

        List<Point2D> points(int count, int range) {
            List<Point2D> out = new ArrayList<>();
            for (int i = 0; i < count; i++) out.add(point(range));
            return out;
        }
    }
}
