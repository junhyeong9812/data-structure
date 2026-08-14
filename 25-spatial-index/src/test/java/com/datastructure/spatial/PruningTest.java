package com.datastructure.spatial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 한계 측정. 가지치기가 실제로 일을 줄이는지, 그리고 언제 안 줄이는지를 숫자로 못 박는다.
 *
 * 시간을 재지 않는다. 기계와 JIT 에 따라 흔들리기 때문이다.
 * 대신 들여다본 노드 수를 센다. 결정적이고, 같은 수가 언제나 나온다.
 *
 * 전수 조사는 질의 한 번에 정확히 n 이다. 트리가 그보다 훨씬 적으면 가지치기가 먹은 것이고,
 * n 에 가까우면 안 먹은 것이다. 후자가 있다는 것이 이 문제의 절반이다.
 */
@DisplayName("가지치기의 효과와 한계")
class PruningTest {

    private static final int N = 4000;
    private static final int SPAN = 10_000;

    private static List<Point2D> spread() {
        return new TestSupport.Dice(55L).points(N, SPAN);
    }

    private static QuadTree quadOf(List<Point2D> points) {
        QuadTree quad = new QuadTree(new Rectangle(0, 0, 16_383, 16_383), 8);
        for (Point2D p : points) quad.insert(p);
        return quad;
    }

    private static KdTree kdOf(List<Point2D> points) {
        KdTree kd = new KdTree();
        for (Point2D p : points) kd.insert(p);
        return kd;
    }

    @Nested
    @DisplayName("측정 1: 가지치기는 실제로 줄인다")
    class ItWorks {

        @Test
        @DisplayName("범위 조회 200번")
        void rangeSearch() {
            List<Point2D> points = spread();
            NaiveSpatialIndex naive = new NaiveSpatialIndex();
            for (Point2D p : points) naive.insert(p);
            KdTree kd = kdOf(points);
            QuadTree quad = quadOf(points);
            int n = naive.size();

            naive.resetVisits();
            kd.resetVisits();
            quad.resetVisits();
            TestSupport.Dice dice = new TestSupport.Dice(31337L);
            for (int q = 0; q < 200; q++) {
                int x = dice.next(SPAN - 500);
                int y = dice.next(SPAN - 500);
                Rectangle area = new Rectangle(x, y, x + 500, y + 500);
                naive.rangeSearch(area);
                kd.rangeSearch(area);
                quad.rangeSearch(area);
            }

            assertEquals(200L * n, naive.visits(), "전수 조사는 질의마다 정확히 n 이다");
            assertTrue(kd.visits() * 20 < naive.visits(),
                    "KD-트리가 " + kd.visits() + " 번 봤다. 전수는 " + naive.visits() + " 번이다");
            assertTrue(quad.visits() * 20 < naive.visits(),
                    "쿼드트리가 " + quad.visits() + " 번 봤다. 전수는 " + naive.visits() + " 번이다");
        }

        @Test
        @DisplayName("최근접 200번")
        void nearest() {
            List<Point2D> points = spread();
            NaiveSpatialIndex naive = new NaiveSpatialIndex();
            for (Point2D p : points) naive.insert(p);
            KdTree kd = kdOf(points);
            QuadTree quad = quadOf(points);
            int n = naive.size();

            naive.resetVisits();
            kd.resetVisits();
            quad.resetVisits();
            TestSupport.Dice dice = new TestSupport.Dice(4711L);
            for (int q = 0; q < 200; q++) {
                Point2D target = dice.point(SPAN);
                naive.nearest(target);
                kd.nearest(target);
                quad.nearest(target);
            }

            assertEquals(200L * n, naive.visits());
            assertTrue(kd.visits() * 50 < naive.visits(),
                    "KD-트리 " + kd.visits() + " 대 전수 " + naive.visits());
            assertTrue(quad.visits() * 50 < naive.visits(),
                    "쿼드트리 " + quad.visits() + " 대 전수 " + naive.visits());
        }

        @Test
        @DisplayName("최근접 10개 200번")
        void nearestK() {
            // k 가 커지면 반경이 커지고, 반경이 커지면 가지치기가 덜 먹는다.
            // 그래도 전수보다는 한참 적다. 여기서 힙의 머리가 곧 가지치기 반경이다.
            List<Point2D> points = spread();
            NaiveSpatialIndex naive = new NaiveSpatialIndex();
            for (Point2D p : points) naive.insert(p);
            KdTree kd = kdOf(points);
            QuadTree quad = quadOf(points);

            naive.resetVisits();
            kd.resetVisits();
            quad.resetVisits();
            TestSupport.Dice dice = new TestSupport.Dice(4711L);
            for (int q = 0; q < 200; q++) {
                Point2D target = dice.point(SPAN);
                naive.nearestK(target, 10);
                kd.nearestK(target, 10);
                quad.nearestK(target, 10);
            }

            assertTrue(kd.visits() * 20 < naive.visits(),
                    "KD-트리 " + kd.visits() + " 대 전수 " + naive.visits());
            assertTrue(quad.visits() * 20 < naive.visits(),
                    "쿼드트리 " + quad.visits() + " 대 전수 " + naive.visits());
            assertTrue(kd.visits() > 0, "가지치기를 해도 0 번일 수는 없다");
        }
    }

    @Nested
    @DisplayName("측정 2: 안 먹는 배치가 있다 (고칠 수 없다)")
    class ItFails {

        /** 지름 200만짜리 원 위의 점. sqrt 는 명세가 정확한 반올림을 요구하므로 어디서 돌려도 같다. */
        private List<Point2D> circle(int radius, int perQuadrant) {
            LinkedHashSet<Point2D> ring = new LinkedHashSet<>();
            for (int i = 0; i < perQuadrant; i++) {
                int x = (int) ((long) radius * i / perQuadrant);
                int y = (int) Math.round(Math.sqrt((double) radius * radius - (double) x * x));
                ring.add(new Point2D(x, y));
                ring.add(new Point2D(-x, y));
                ring.add(new Point2D(x, -y));
                ring.add(new Point2D(-x, -y));
                ring.add(new Point2D(y, x));
                ring.add(new Point2D(-y, x));
                ring.add(new Point2D(y, -x));
                ring.add(new Point2D(-y, -x));
            }
            return new ArrayList<>(ring);
        }

        @Test
        @DisplayName("모든 점이 같은 거리에 있으면 한 개도 못 건너뛴다")
        void equidistantPointsDefeatPruning() {
            // 가지치기의 조건은 하나다. 지금까지의 최단 거리 > 분할선까지의 거리.
            // 원 한가운데서 물으면 모든 점이 반지름만큼 떨어져 있고, 모든 분할선은 그 안에 있다.
            // 그래서 어떤 서브트리도 버릴 수 없다. 구현을 아무리 잘해도 그렇다.
            List<Point2D> ring = circle(1_000_000, 256);
            KdTree kd = kdOf(ring);
            KdTree built = KdTree.build(ring);
            int n = kd.size();

            kd.resetVisits();
            built.resetVisits();
            kd.nearest(new Point2D(0, 0));
            built.nearest(new Point2D(0, 0));

            assertEquals(n, kd.visits(), "삽입으로 지은 트리가 " + n + "개를 전부 봤다");
            assertEquals(n, built.visits(), "중앙값으로 지은 균형 트리도 마찬가지다. 균형의 문제가 아니다");
        }

        @Test
        @DisplayName("질의점이 데이터에서 멀면 가지치기가 통째로 무너진다")
        void farAwayQueryDefeatsPruning() {
            // 점 4000개가 가로 8000 안에 한 줄로 있다. 그 줄 위에서 물으면 30 번이면 끝난다.
            // 같은 트리에 100만 떨어진 곳에서 물으면 최단 거리가 100만이라
            // 모든 분할선(가로 8000 안)이 그 반경 안에 들어온다. 하나도 못 버린다.
            int n = 2000;
            List<Point2D> line = new ArrayList<>();
            for (int i = 0; i < n; i++) line.add(new Point2D(2 * i, 0));
            java.util.Collections.shuffle(line, new java.util.Random(7));
            KdTree kd = kdOf(line);
            assertEquals(n, kd.size());

            kd.resetVisits();
            kd.nearest(new Point2D(1001, 0));
            long onTheLine = kd.visits();

            kd.resetVisits();
            kd.nearest(new Point2D(1001, 1_000_000));
            long farAway = kd.visits();

            assertTrue(onTheLine * 20 < n, "줄 위에서는 " + onTheLine + " 번이면 된다");
            assertEquals(n, farAway, "멀리서 물으면 " + n + "개를 전부 본다. 전수 조사와 똑같다");
        }

        @Test
        @DisplayName("한 직선 위에 있다는 것만으로는 안 무너진다")
        void aLineAloneIsNotTheWorstCase() {
            // 정직하게 적어둔다. "모든 점의 y 가 같으면 가지치기가 안 먹는다"는 설명을
            // 측정해보니 틀렸다. y 분할 층이 아무 일도 못 하는 것은 맞지만,
            // 그 층의 한쪽 자식이 비어 있어서 값이 싸다. x 분할은 멀쩡히 먹는다.
            //
            // 무너뜨리는 것은 직선이 아니라 "최단 거리가 데이터 폭보다 크다"는 조건이다.
            // 그것을 farAwayQueryDefeatsPruning 이 보여준다.
            int n = 2000;
            List<Point2D> line = new ArrayList<>();
            for (int i = 0; i < n; i++) line.add(new Point2D(2 * i, 17));
            java.util.Collections.shuffle(line, new java.util.Random(7));
            KdTree kd = kdOf(line);

            kd.resetVisits();
            TestSupport.Dice dice = new TestSupport.Dice(21L);
            for (int q = 0; q < 100; q++) kd.nearest(new Point2D(dice.next(4000), 17));
            assertTrue(kd.visits() * 10 < 100L * n,
                    "직선 위 질의 100번에 " + kd.visits() + " 번. 전수라면 " + (100L * n) + " 번이다");
        }
    }

    @Nested
    @DisplayName("측정 3: 깊이를 무엇이 정하는가")
    class Depth {

        private List<Point2D> evenlySpread() {
            return new TestSupport.Dice(31L).points(1000, 1024);
        }

        private List<Point2D> clustered() {
            TestSupport.Dice dice = new TestSupport.Dice(31L);
            List<Point2D> points = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                points.add(new Point2D(500 + dice.next(32), 500 + dice.next(32)));
            }
            return points;
        }

        @Test
        @DisplayName("쿼드트리는 점이 몰리면 깊어진다")
        void clusteringDeepensQuadTree() {
            QuadTree even = new QuadTree(new Rectangle(0, 0, 1023, 1023), 4);
            for (Point2D p : evenlySpread()) even.insert(p);
            QuadTree tight = new QuadTree(new Rectangle(0, 0, 1023, 1023), 4);
            for (Point2D p : clustered()) tight.insert(p);

            assertEquals(1000, even.size());
            assertEquals(635, tight.size(), "32x32 안에 1000번 찍으면 서로 다른 점은 635개다");
            assertEquals(7, even.depth(), "고르게 흩어지면 7층");
            assertEquals(10, tight.depth(), "점이 더 적은데 3층 더 깊다. 칸이 아니라 점이 몰렸기 때문이다");
        }

        @Test
        @DisplayName("쿼드트리의 깊이는 데이터가 아니라 좌표 범위가 정한다")
        void quadTreeDepthFollowsTheBounds() {
            // 점은 하나도 안 바꾸고 경계만 넓힌다. 그것만으로 깊이가 두 배가 된다.
            // 위쪽 열 층은 자식 하나만 있는 통과 노드다. 데이터와 아무 상관이 없다.
            List<Point2D> points = clustered();
            QuadTree small = new QuadTree(new Rectangle(0, 0, 1023, 1023), 4);
            QuadTree big = new QuadTree(new Rectangle(0, 0, 1_048_575, 1_048_575), 4);
            for (Point2D p : points) {
                small.insert(p);
                big.insert(p);
            }
            assertEquals(small.size(), big.size());
            assertEquals(10, small.depth(), "경계 1024 에서 10층");
            assertEquals(20, big.depth(), "같은 점인데 경계 2^20 에서는 20층");

            KdTree kd = kdOf(points);
            assertEquals(20, kd.height(), "KD-트리는 경계를 모른다. 데이터만 본다");
            assertEquals(11, KdTree.build(points).height(), "중앙값으로 지으면 11층");
        }
    }

    @Nested
    @DisplayName("측정 4: 삽입 순서가 KD-트리의 균형을 정한다")
    class InsertionOrder {

        @Test
        @DisplayName("정렬된 순서로 넣으면 한 줄이 된다")
        void sortedInsertDegenerates() {
            // 06번 이진 탐색 트리와 같은 문제다. 축을 번갈아 갈라도 막지 못한다.
            // (i, i) 는 x 로 봐도 y 로 봐도 오름차순이라 언제나 오른쪽으로만 간다.
            int n = 1000;
            List<Point2D> diagonal = new ArrayList<>();
            for (int i = 0; i < n; i++) diagonal.add(new Point2D(i, i));

            KdTree inserted = kdOf(diagonal);
            assertEquals(n, inserted.height(), "높이가 원소 수와 같다. 사실상 연결 리스트다");

            KdTree built = KdTree.build(diagonal);
            assertEquals(10, built.height(), "중앙값으로 일괄 구축하면 10층이다. log2(1000) 이 약 10이다");
        }

        @Test
        @DisplayName("한 줄이 된 트리는 최근접도 느리다")
        void degenerateTreeSearchesMore() {
            int n = 1000;
            List<Point2D> diagonal = new ArrayList<>();
            for (int i = 0; i < n; i++) diagonal.add(new Point2D(i, i));
            KdTree inserted = kdOf(diagonal);
            KdTree built = KdTree.build(diagonal);

            inserted.resetVisits();
            built.resetVisits();
            TestSupport.Dice dice = new TestSupport.Dice(12L);
            for (int q = 0; q < 100; q++) {
                Point2D target = dice.point(1000);
                inserted.nearest(target);
                built.nearest(target);
            }

            assertTrue(built.visits() * 2 < inserted.visits(),
                    "일괄 구축 " + built.visits() + " 대 정렬 삽입 " + inserted.visits());
            assertTrue(inserted.visits() < 100L * n,
                    "축퇴해도 전수 조사보다는 낫다. " + inserted.visits() + " 대 " + (100L * n));
        }

        @Test
        @DisplayName("흩어진 순서로 넣으면 균형이 잡힌다")
        void shuffledInsertIsFine() {
            // 축퇴는 KD-트리의 성질이 아니라 입력 순서의 성질이다.
            int n = 1000;
            List<Point2D> shuffled = new ArrayList<>();
            for (int i = 0; i < n; i++) shuffled.add(new Point2D((i * 617) % n, (i * 331) % n));
            KdTree kd = kdOf(shuffled);
            assertTrue(kd.height() < 40, "높이가 " + kd.height() + " 다. 정렬 삽입은 " + n + " 이었다");
        }
    }
}
