package com.datastructure.spatial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 점과 사각형. 두 트리가 전부 이 두 클래스 위에 서 있다.
 *
 * 여기가 틀리면 트리는 조용히 틀린다. 그래서 계약보다 먼저 못 박는다.
 */
@DisplayName("Point2D 와 Rectangle")
class GeometryTest {

    @Nested
    @DisplayName("제곱거리")
    class SquaredDistance {

        @Test
        @DisplayName("기본")
        void basics() {
            assertEquals(0L, new Point2D(3, 4).squaredDistanceTo(new Point2D(3, 4)));
            assertEquals(25L, new Point2D(0, 0).squaredDistanceTo(new Point2D(3, 4)));
            assertEquals(25L, new Point2D(3, 4).squaredDistanceTo(new Point2D(0, 0)), "대칭이다");
            assertEquals(2L, new Point2D(6, 5).squaredDistanceTo(new Point2D(5, 4)));
            assertEquals(5.0, new Point2D(0, 0).distanceTo(new Point2D(3, 4)), 1e-12);
        }

        @Test
        @DisplayName("int 로 계산하면 넘친다")
        void needsLong() {
            // 좌표가 20억쯤 떨어지면 차이의 제곱이 4 * 10^18 이다.
            // int 최대는 21억이고 long 최대는 9.2 * 10^18 이라, **long 으로 올려야만** 담긴다.
            Point2D a = new Point2D(-1_000_000_000, 0);
            Point2D b = new Point2D(1_000_000_000, 0);
            assertEquals(4_000_000_000_000_000_000L, a.squaredDistanceTo(b));

            int dx = b.x - a.x;
            assertEquals(2_000_000_000, dx, "차이 자체는 int 에 들어간다");
            assertNotEquals(4_000_000_000_000_000_000L, (long) (dx * dx),
                    "int 로 곱한 뒤 long 으로 올리면 이미 늦었다");
        }

        @Test
        @DisplayName("제곱근을 씌우면 정보가 사라진다")
        void sqrtLosesInformation() {
            // 제곱근을 쓰지 않는 첫 번째 이유는 비용이지만, 두 번째 이유가 더 무섭다.
            // double 은 가수가 53비트라 큰 수에서 이웃한 값들이 **같은 double 로 뭉친다.**
            Point2D origin = new Point2D(0, 0);
            Point2D near = new Point2D(2_000_000_000, 0);
            Point2D far = new Point2D(2_000_000_000, 1);

            assertTrue(origin.squaredDistanceTo(near) < origin.squaredDistanceTo(far),
                    "제곱거리로는 분명히 다르다");
            assertEquals(origin.distanceTo(near), origin.distanceTo(far), 0.0,
                    "그런데 제곱근을 씌우면 완전히 같은 double 이 된다. 순서를 못 가린다");
        }

        @Test
        @DisplayName("제곱거리의 순서는 거리의 순서와 같다")
        void orderIsPreserved() {
            // 비교만 할 것이면 제곱근이 필요 없다는 근거다. 제곱은 음이 아닌 수에서 단조증가다.
            Point2D t = new Point2D(3, 7);
            TestSupport.Dice dice = new TestSupport.Dice(99L);
            for (int i = 0; i < 300; i++) {
                Point2D a = dice.point(50);
                Point2D b = dice.point(50);
                int bySquared = Long.compare(t.squaredDistanceTo(a), t.squaredDistanceTo(b));
                int byDistance = Double.compare(t.distanceTo(a), t.distanceTo(b));
                assertEquals(bySquared, byDistance, a + " 와 " + b + " 의 순서가 갈렸다");
            }
        }
    }

    @Nested
    @DisplayName("값 객체")
    class ValueObject {

        @Test
        @DisplayName("좌표가 같으면 같은 점이다")
        void equality() {
            assertEquals(new Point2D(3, 7), new Point2D(3, 7));
            assertEquals(new Point2D(3, 7).hashCode(), new Point2D(3, 7).hashCode());
            assertNotEquals(new Point2D(3, 7), new Point2D(7, 3), "x 와 y 를 바꾸면 다른 점이다");
            assertNotEquals(new Point2D(3, 7), "(3, 7)");
        }

        @Test
        @DisplayName("집합에 넣으면 중복이 사라진다")
        void worksInHashSet() {
            // 세 구현이 전부 "같은 점을 두 번 넣지 않는다"에 기대므로 equals 와 hashCode 가 계약이다.
            Set<Point2D> set = new HashSet<>();
            set.add(new Point2D(1, 2));
            set.add(new Point2D(1, 2));
            set.add(new Point2D(2, 1));
            assertEquals(2, set.size());
        }

        @Test
        @DisplayName("축으로 좌표를 꺼낸다")
        void coordinateByAxis() {
            // KD-트리가 깊이마다 축을 바꾸는데, 이게 없으면 if 문이 사방에 퍼진다.
            Point2D p = new Point2D(3, 7);
            assertEquals(3, p.coordinate(0));
            assertEquals(7, p.coordinate(1));
        }
    }

    @Nested
    @DisplayName("사각형")
    class Rectangles {

        @Test
        @DisplayName("경계를 포함한다")
        void contains() {
            Rectangle r = new Rectangle(2, 3, 6, 8);
            assertTrue(r.contains(new Point2D(2, 3)), "왼쪽 아래 모서리");
            assertTrue(r.contains(new Point2D(6, 8)), "오른쪽 위 모서리");
            assertTrue(r.contains(new Point2D(4, 3)), "아래 변");
            assertTrue(r.contains(new Point2D(4, 5)));
            assertFalse(r.contains(new Point2D(1, 5)));
            assertFalse(r.contains(new Point2D(7, 5)));
            assertFalse(r.contains(new Point2D(4, 2)));
            assertFalse(r.contains(new Point2D(4, 9)));
        }

        @Test
        @DisplayName("한 점을 스치기만 해도 겹친 것이다")
        void intersects() {
            // 여기서 부등호를 하나 잘못 쓰면 가지치기가 답을 통째로 지운다.
            // 안 겹치는 조건을 먼저 쓰고 부정하는 편이 훨씬 덜 틀린다.
            Rectangle r = new Rectangle(2, 3, 6, 8);
            assertTrue(r.intersects(r));
            assertTrue(r.intersects(new Rectangle(0, 0, 2, 3)), "모서리 한 점만 닿아도 겹친다");
            assertTrue(r.intersects(new Rectangle(6, 8, 20, 20)), "반대쪽 모서리");
            assertTrue(r.intersects(new Rectangle(0, 0, 100, 100)), "통째로 들어간다");
            assertTrue(r.intersects(new Rectangle(3, 4, 4, 5)), "안에 들어 있다");
            assertFalse(r.intersects(new Rectangle(0, 0, 1, 2)), "한 칸 모자라면 안 겹친다");
            assertFalse(r.intersects(new Rectangle(7, 3, 9, 8)), "오른쪽으로 한 칸 벗어났다");
            assertFalse(r.intersects(new Rectangle(2, 9, 6, 12)), "위로 한 칸 벗어났다");
        }

        @Test
        @DisplayName("점까지의 제곱거리")
        void squaredDistanceToPoint() {
            // 쿼드트리의 가지치기가 이 값 하나로 돌아간다.
            // 칸까지의 거리가 지금까지의 최단 거리보다 멀면 그 칸은 안 본다.
            Rectangle r = new Rectangle(0, 0, 10, 10);
            assertEquals(0L, r.squaredDistanceTo(new Point2D(5, 5)), "안에 있으면 0");
            assertEquals(0L, r.squaredDistanceTo(new Point2D(0, 0)), "모서리도 0");
            assertEquals(0L, r.squaredDistanceTo(new Point2D(10, 4)), "변 위도 0");
            assertEquals(4L, r.squaredDistanceTo(new Point2D(12, 5)), "오른쪽 변까지 2");
            assertEquals(9L, r.squaredDistanceTo(new Point2D(5, -3)), "아래 변까지 3");
            assertEquals(25L, r.squaredDistanceTo(new Point2D(13, 14)), "모서리까지 3, 4");
            assertEquals(2L, r.squaredDistanceTo(new Point2D(-1, -1)), "왼쪽 아래 대각선");
        }

        @Test
        @DisplayName("뒤집힌 사각형은 만들 수 없다")
        void inverted() {
            assertThrows(IllegalArgumentException.class, () -> new Rectangle(5, 0, 4, 9));
            assertThrows(IllegalArgumentException.class, () -> new Rectangle(0, 5, 9, 4));
            assertEquals(new Rectangle(3, 3, 3, 3), new Rectangle(3, 3, 3, 3), "한 점짜리는 된다");
        }

        @Test
        @DisplayName("네 칸으로 쪼개면 빈틈도 겹침도 없다")
        void subdivideIsAPartition() {
            // 겹치면 한 점이 두 칸에 들어가 rangeSearch 가 같은 점을 두 번 준다.
            // 빈틈이 있으면 그 자리의 점을 넣을 곳이 없다. 둘 다 조용히 틀린다.
            for (Rectangle box : new Rectangle[]{
                    new Rectangle(0, 0, 15, 15), new Rectangle(0, 0, 2, 2),
                    new Rectangle(-8, -8, 7, 7), new Rectangle(0, 0, 1, 1),
                    new Rectangle(3, 5, 10, 9)}) {
                Rectangle[] quads = box.subdivide();
                assertEquals(4, quads.length);
                int covered = 0;
                for (int x = box.minX; x <= box.maxX; x++) {
                    for (int y = box.minY; y <= box.maxY; y++) {
                        Point2D p = new Point2D(x, y);
                        int hits = 0;
                        for (Rectangle q : quads) if (q.contains(p)) hits++;
                        assertEquals(1, hits, box + " 안의 " + p + " 가 " + hits + "칸에 들어간다");
                        covered++;
                    }
                }
                assertTrue(covered > 0);
            }
        }

        @Test
        @DisplayName("칸이 1x1 이 되면 더 못 쪼갠다")
        void cannotSubdivideUnitCell() {
            // 이것이 쿼드트리 깊이의 상한이다. 정수 격자에서는 여기서 멈춘다.
            assertTrue(new Rectangle(0, 0, 1, 1).canSubdivide());
            assertFalse(new Rectangle(5, 5, 5, 5).canSubdivide());
            assertFalse(new Rectangle(0, 0, 0, 7).canSubdivide(), "폭이 1 이면 못 쪼갠다");
            assertThrows(IllegalStateException.class, () -> new Rectangle(5, 5, 5, 5).subdivide());
        }

        @Test
        @DisplayName("좌표가 20억 근처여도 중점이 안 넘친다")
        void subdivideNearTheIntLimit() {
            // (minX + maxX) / 2 로 쓰면 두 수를 더하는 순간 int 를 넘어 음수가 된다.
            // 그러면 min 이 max 보다 큰 사각형을 만들려다 터진다.
            // 15번 B-트리에서 본 것과 같은 함정이라 여기에도 못 박아둔다.
            Rectangle huge = new Rectangle(1_500_000_000, 1_500_000_000, 2_000_000_000, 2_000_000_000);
            Rectangle[] quads = huge.subdivide();
            assertEquals(new Rectangle(1_500_000_000, 1_500_000_000, 1_750_000_000, 1_750_000_000),
                    quads[0], "SW");
            assertEquals(new Rectangle(1_750_000_001, 1_750_000_001, 2_000_000_000, 2_000_000_000),
                    quads[3], "NE");
            for (Point2D p : new Point2D[]{
                    new Point2D(1_500_000_000, 1_500_000_000), new Point2D(2_000_000_000, 2_000_000_000),
                    new Point2D(1_750_000_000, 1_750_000_001), new Point2D(1_999_999_999, 1_500_000_000)}) {
                int hits = 0;
                for (Rectangle q : quads) if (q.contains(p)) hits++;
                assertEquals(1, hits, p + " 가 " + hits + "칸에 들어간다");
            }
        }

        @Test
        @DisplayName("2의 거듭제곱 경계는 정사각형으로 쪼개진다")
        void powerOfTwoStaysSquare() {
            Rectangle[] quads = new Rectangle(0, 0, 15, 15).subdivide();
            assertEquals(new Rectangle(0, 0, 7, 7), quads[0], "SW");
            assertEquals(new Rectangle(8, 0, 15, 7), quads[1], "SE");
            assertEquals(new Rectangle(0, 8, 7, 15), quads[2], "NW");
            assertEquals(new Rectangle(8, 8, 15, 15), quads[3], "NE");
        }
    }
}
