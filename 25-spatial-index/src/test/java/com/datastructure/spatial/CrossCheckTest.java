package com.datastructure.spatial;

import static com.datastructure.spatial.TestSupport.distances;
import static com.datastructure.spatial.TestSupport.sorted;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * 무작위 대조. 이 문제집에서 버그를 가장 많이 잡는 테스트다.
 *
 * 두 트리의 답이 전수 조사와 다르면 트리가 틀린 것이다.
 * 가지치기는 "볼 필요 없는 곳을 안 본다"인데, 조건을 하나만 틀리게 써도
 * "봐야 하는 곳을 안 본다"가 된다. 그러면 예외도 안 나고 조용히 몇 개가 빠진다.
 * 그 조용한 누락을 잡는 유일한 방법이 느린 구현과의 대조다.
 *
 * 동점 주의. 최근접은 답이 여럿일 수 있으므로 점이 아니라 제곱거리 수열을 비교한다.
 */
@DisplayName("전수 조사와의 무작위 대조")
class CrossCheckTest {

    /** 한 데이터셋에 대해 네 구현(전수, KD 삽입, KD 일괄구축, 쿼드)을 전부 대조한다. */
    private void crossCheck(String label, List<Point2D> points, Rectangle bounds,
                            int capacity, int queryRange, int queryOrigin, long seed) {
        NaiveSpatialIndex naive = new NaiveSpatialIndex();
        KdTree kd = new KdTree();
        QuadTree quad = new QuadTree(bounds, capacity);

        for (Point2D p : points) {
            boolean a = naive.insert(p);
            boolean b = kd.insert(p);
            boolean c = quad.insert(p);
            assertEquals(a, b, label + ": " + p + " 의 삽입 결과가 KD 와 다르다");
            assertEquals(a, c, label + ": " + p + " 의 삽입 결과가 쿼드와 다르다");
        }
        KdTree built = KdTree.build(points);
        int n = naive.size();
        assertEquals(n, kd.size(), label + ": 크기");
        assertEquals(n, quad.size(), label + ": 크기");
        assertEquals(n, built.size(), label + ": 일괄 구축 크기");

        for (Point2D p : points) {
            assertTrue(kd.contains(p), label + ": KD 가 " + p + " 를 못 찾는다");
            assertTrue(quad.contains(p), label + ": 쿼드가 " + p + " 를 못 찾는다");
            assertTrue(built.contains(p), label + ": 일괄 구축 트리가 " + p + " 를 못 찾는다");
        }

        List<SpatialIndex> trees = List.of(kd, quad, built);
        List<String> names = List.of("KD", "쿼드", "일괄구축");
        TestSupport.Dice dice = new TestSupport.Dice(seed);

        for (int q = 0; q < 200; q++) {
            int x1 = queryOrigin + dice.next(queryRange);
            int x2 = queryOrigin + dice.next(queryRange);
            int y1 = queryOrigin + dice.next(queryRange);
            int y2 = queryOrigin + dice.next(queryRange);
            Rectangle area = new Rectangle(Math.min(x1, x2), Math.min(y1, y2),
                    Math.max(x1, x2), Math.max(y1, y2));
            List<Point2D> expected = sorted(naive.rangeSearch(area));
            for (int i = 0; i < trees.size(); i++) {
                List<Point2D> got = trees.get(i).rangeSearch(area);
                assertEquals(expected.size(), got.size(),
                        label + ": " + names.get(i) + " 의 " + area + " 결과 개수");
                assertEquals(expected, sorted(got), label + ": " + names.get(i) + " 의 " + area);
            }
        }

        for (int q = 0; q < 200; q++) {
            Point2D target = new Point2D(queryOrigin + dice.next(queryRange),
                    queryOrigin + dice.next(queryRange));
            long expected = target.squaredDistanceTo(naive.nearest(target));
            for (int i = 0; i < trees.size(); i++) {
                Point2D got = trees.get(i).nearest(target);
                assertEquals(expected, target.squaredDistanceTo(got),
                        label + ": " + names.get(i) + " 의 " + target + " 최근접 거리");
                assertTrue(naive.contains(got), label + ": 없는 점을 만들어냈다 " + got);
            }
        }

        for (int q = 0; q < 100; q++) {
            Point2D target = new Point2D(queryOrigin + dice.next(queryRange),
                    queryOrigin + dice.next(queryRange));
            int k = 1 + dice.next(12);
            List<Long> expected = distances(target, naive.nearestK(target, k));
            for (int i = 0; i < trees.size(); i++) {
                List<Point2D> got = trees.get(i).nearestK(target, k);
                assertEquals(expected, distances(target, got),
                        label + ": " + names.get(i) + " 의 " + target + " 최근접 " + k + "개 거리 수열");
                assertEquals(got.size(), new ArrayList<>(new java.util.HashSet<>(got)).size(),
                        label + ": " + names.get(i) + " 가 같은 점을 두 번 담았다");
            }
        }
    }

    @Test
    @DisplayName("흩어진 점 3000개")
    @Timeout(60)
    void spreadOut() {
        List<Point2D> points = new TestSupport.Dice(12345L).points(3000, 1000);
        crossCheck("흩어진 3000개", points, new Rectangle(0, 0, 1023, 1023), 4, 1200, -100, 999L);
    }

    @Test
    @DisplayName("좁은 곳에 몰린 2000개 (같은 좌표가 잔뜩 나온다)")
    @Timeout(60)
    void clustered() {
        // 여기가 진짜 시험대다. 좌표 범위가 40 이라 같은 x, 같은 y 가 수없이 겹친다.
        // 분할 기준값과 같은 좌표를 어느 쪽에 두는지가 어긋나면 여기서 무너진다.
        List<Point2D> points = new TestSupport.Dice(777L).points(2000, 40);
        crossCheck("몰린 2000개", points, new Rectangle(0, 0, 63, 63), 2, 60, -10, 4242L);
    }

    @Test
    @DisplayName("한 줄로 늘어선 점")
    @Timeout(60)
    void onALine() {
        // 모든 y 가 같다. KD-트리의 y 축 분할이 아무 일도 못 한다.
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < 500; i++) points.add(new Point2D(i * 2, 17));
        crossCheck("가로줄 500개", points, new Rectangle(0, 0, 1023, 1023), 4, 1100, -50, 31L);
    }

    @Test
    @DisplayName("정렬된 순서로 넣은 점 (KD-트리가 한 줄이 된다)")
    @Timeout(60)
    void sortedInsertion() {
        // 트리가 축퇴해도 답은 맞아야 한다. 느려질 뿐이다.
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < 400; i++) points.add(new Point2D(i, i));
        crossCheck("대각선 400개", points, new Rectangle(0, 0, 511, 511), 4, 500, 0, 88L);
    }

    @Test
    @DisplayName("경계 밖은 쿼드트리만 거절한다")
    void quadTreeRejectsOutsideBounds() {
        QuadTree quad = new QuadTree(new Rectangle(0, 0, 15, 15), 2);
        KdTree kd = new KdTree();
        assertTrue(quad.insert(new Point2D(15, 15)), "경계 위는 안쪽이다");
        assertFalse(quad.insert(new Point2D(16, 0)), "경계 밖은 false");
        assertFalse(quad.insert(new Point2D(-1, 3)), "음수 쪽 경계 밖도 false");
        assertEquals(1, quad.size(), "거절된 점은 크기에 안 들어간다");
        assertFalse(quad.contains(new Point2D(16, 0)));

        assertTrue(kd.insert(new Point2D(16, 0)), "KD-트리에는 경계가 없다");
        assertTrue(kd.insert(new Point2D(-1_000_000, 5)));
        assertEquals(2, kd.size());
    }

    @Test
    @DisplayName("일괄 구축은 중복을 걸러낸다")
    void buildDropsDuplicates() {
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            points.add(new Point2D(i % 10, i % 7));
        }
        KdTree built = KdTree.build(points);
        NaiveSpatialIndex naive = new NaiveSpatialIndex();
        for (Point2D p : points) naive.insert(p);
        assertEquals(naive.size(), built.size(), "서로 다른 점의 개수가 같아야 한다");
        assertEquals(sorted(naive.rangeSearch(new Rectangle(-1, -1, 20, 20))),
                sorted(built.rangeSearch(new Rectangle(-1, -1, 20, 20))));
    }
}
