package com.datastructure.spatial;

import static com.datastructure.spatial.TestSupport.distances;
import static com.datastructure.spatial.TestSupport.sorted;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 세 구현이 똑같이 지켜야 하는 계약. 전수 조사와 두 트리의 답이 같아야 한다.
 *
 * 기댓값은 손으로 쓴 것이 아니라 파이썬 참조 구현으로 검산한 것이다.
 * 고정 데이터셋은 KD-트리 문헌에 흔히 나오는 여섯 점이다.
 *
 * 동점 주의. nearest 는 같은 거리의 점이 여럿이면 답이 여럿이다.
 * 그래서 여기서는 어느 점이 나왔는지가 아니라 그 점까지의 거리를 본다.
 */
abstract class SpatialIndexContractTest {

    /** 구현체를 만든다. QuadTree 는 경계가 필요하므로 하위 클래스가 정한다. */
    protected abstract SpatialIndex create();

    static final Point2D[] FIXED = {
            new Point2D(2, 3), new Point2D(5, 4), new Point2D(9, 6),
            new Point2D(4, 7), new Point2D(8, 1), new Point2D(7, 2)
    };

    protected SpatialIndex fixed() {
        SpatialIndex index = create();
        for (Point2D p : FIXED) index.insert(p);
        return index;
    }

    /** 0..4 격자 25점. 경계 포함 여부를 보는 데 쓴다. */
    protected SpatialIndex grid() {
        SpatialIndex index = create();
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 4; y++) index.insert(new Point2D(x, y));
        }
        return index;
    }

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("빈 인덱스")
        void empty() {
            SpatialIndex index = create();
            assertEquals(0, index.size());
            assertTrue(index.isEmpty());
            assertFalse(index.contains(new Point2D(1, 1)));
            assertNull(index.nearest(new Point2D(1, 1)), "빈 인덱스의 최근접은 null 이다");
            assertTrue(index.nearestK(new Point2D(1, 1), 5).isEmpty());
            assertTrue(index.rangeSearch(new Rectangle(-10, -10, 10, 10)).isEmpty());
        }

        @Test
        @DisplayName("넣으면 들어 있다")
        void insertAndContains() {
            SpatialIndex index = create();
            assertTrue(index.insert(new Point2D(3, 7)));
            assertEquals(1, index.size());
            assertTrue(index.contains(new Point2D(3, 7)), "같은 좌표면 같은 점이다");
            assertFalse(index.contains(new Point2D(7, 3)), "x 와 y 를 바꾸면 다른 점이다");
            assertFalse(index.contains(new Point2D(3, 8)));
        }

        @Test
        @DisplayName("같은 점을 두 번 넣으면 두 번째는 false")
        void duplicatesAreRejected() {
            // 이 문제집의 결정이다. 중복을 허용하면 세 구현의 답을 대조할 수 없다.
            SpatialIndex index = create();
            assertTrue(index.insert(new Point2D(5, 5)));
            assertFalse(index.insert(new Point2D(5, 5)), "이미 있는 점은 안 늘어난다");
            assertEquals(1, index.size());
            assertEquals(1, index.rangeSearch(new Rectangle(0, 0, 10, 10)).size(),
                    "범위 조회에도 한 번만 나와야 한다");
        }

        @Test
        @DisplayName("clear 하면 처음으로 돌아간다")
        void clearResets() {
            SpatialIndex index = fixed();
            assertEquals(6, index.size());
            index.clear();
            assertEquals(0, index.size());
            assertFalse(index.contains(new Point2D(2, 3)));
            assertNull(index.nearest(new Point2D(2, 3)));
            assertTrue(index.insert(new Point2D(2, 3)), "지운 뒤에는 다시 넣을 수 있다");
        }

        @Test
        @DisplayName("들어간 점은 전부 찾을 수 있다")
        void everyInsertedPointIsFound() {
            SpatialIndex index = grid();
            assertEquals(25, index.size());
            for (int x = 0; x <= 4; x++) {
                for (int y = 0; y <= 4; y++) {
                    assertTrue(index.contains(new Point2D(x, y)), "(" + x + ", " + y + ")");
                }
            }
            assertFalse(index.contains(new Point2D(5, 0)));
        }
    }

    @Nested
    @DisplayName("범위 조회")
    class RangeSearch {

        @Test
        @DisplayName("고정 데이터셋")
        void fixedDataset() {
            SpatialIndex index = fixed();
            assertEquals(
                    sorted(List.of(new Point2D(5, 4), new Point2D(7, 2), new Point2D(8, 1), new Point2D(9, 6))),
                    sorted(index.rangeSearch(new Rectangle(4, 1, 9, 6))));
            assertEquals(6, index.rangeSearch(new Rectangle(0, 0, 10, 10)).size(), "전부 덮는 사각형");
            assertEquals(List.of(), index.rangeSearch(new Rectangle(0, 0, 1, 1)), "아무것도 없는 구석");
            assertEquals(List.of(new Point2D(2, 3)), index.rangeSearch(new Rectangle(2, 3, 2, 3)),
                    "점 하나짜리 사각형");
            assertEquals(List.of(new Point2D(5, 4)), index.rangeSearch(new Rectangle(5, 4, 8, 7)));
        }

        @Test
        @DisplayName("경계는 포함이다")
        void bordersAreInclusive() {
            // 여기가 부등호 하나로 갈린다. < 와 <= 를 바꾸면 테두리가 통째로 사라진다.
            SpatialIndex index = grid();
            assertEquals(9, index.rangeSearch(new Rectangle(1, 1, 3, 3)).size());
            assertEquals(List.of(new Point2D(0, 0)), index.rangeSearch(new Rectangle(0, 0, 0, 0)),
                    "왼쪽 아래 끝 점 하나");
            assertEquals(List.of(new Point2D(4, 4)), index.rangeSearch(new Rectangle(4, 4, 4, 4)),
                    "오른쪽 위 끝 점 하나");
            assertEquals(5, index.rangeSearch(new Rectangle(2, 0, 2, 4)).size(), "폭 1 짜리 세로 띠");
            assertEquals(25, index.rangeSearch(new Rectangle(0, 0, 4, 4)).size(), "격자 전체");
        }

        @Test
        @DisplayName("범위 밖은 빈 목록")
        void outsideIsEmpty() {
            SpatialIndex index = grid();
            assertTrue(index.rangeSearch(new Rectangle(5, 5, 9, 9)).isEmpty());
            assertTrue(index.rangeSearch(new Rectangle(-9, -9, -1, -1)).isEmpty());
            assertTrue(index.rangeSearch(new Rectangle(-9, 0, -1, 4)).isEmpty(), "왼쪽으로 벗어난 띠");
        }

        @Test
        @DisplayName("결과에 중복이 없다")
        void noDuplicatesInResult() {
            // 쿼드트리에서 네 칸이 겹치게 쪼개지면 한 점이 두 번 나온다.
            SpatialIndex index = grid();
            List<Point2D> all = index.rangeSearch(new Rectangle(0, 0, 4, 4));
            assertEquals(25, all.size());
            assertEquals(25, all.stream().distinct().count(), "같은 점이 두 번 나오면 칸이 겹친 것이다");
        }
    }

    @Nested
    @DisplayName("최근접")
    class Nearest {

        @Test
        @DisplayName("고정 데이터셋")
        void fixedDataset() {
            SpatialIndex index = fixed();
            assertEquals(2L, new Point2D(6, 5).squaredDistanceTo(index.nearest(new Point2D(6, 5))));
            assertEquals(13L, new Point2D(0, 0).squaredDistanceTo(index.nearest(new Point2D(0, 0))));
            assertEquals(17L, new Point2D(10, 10).squaredDistanceTo(index.nearest(new Point2D(10, 10))));
        }

        @Test
        @DisplayName("자기 자신이 들어 있으면 거리 0")
        void exactHit() {
            SpatialIndex index = fixed();
            for (Point2D p : FIXED) {
                assertEquals(p, index.nearest(p), "자기가 들어 있는데 다른 점을 골랐다");
            }
        }

        @Test
        @DisplayName("점이 하나뿐이면 언제나 그 점")
        void singlePoint() {
            SpatialIndex index = create();
            index.insert(new Point2D(3, 3));
            assertEquals(new Point2D(3, 3), index.nearest(new Point2D(-100, 500)));
        }

        @Test
        @DisplayName("동점이면 답이 여럿이다")
        void tiesHaveManyAnswers() {
            // 이 계약을 안 정해두면 구현을 바꿀 때마다 테스트가 깨진다.
            // 우리가 못 박는 것은 거리이지 어느 점인지가 아니다.
            SpatialIndex index = create();
            index.insert(new Point2D(0, 0));
            index.insert(new Point2D(2, 0));
            index.insert(new Point2D(1, 1));
            Point2D target = new Point2D(1, 0);
            Point2D found = index.nearest(target);
            assertNotNull(found);
            assertEquals(1L, target.squaredDistanceTo(found), "셋 다 제곱거리가 1 이다");
            assertTrue(index.contains(found), "인덱스에 없는 점을 만들어내면 안 된다");
        }
    }

    @Nested
    @DisplayName("최근접 k 개")
    class NearestK {

        @Test
        @DisplayName("고정 데이터셋의 거리 수열")
        void fixedDataset() {
            SpatialIndex index = fixed();
            assertEquals(List.of(2L, 8L, 10L),
                    distances(new Point2D(6, 5), index.nearestK(new Point2D(6, 5), 3)));
            assertEquals(List.of(13L, 41L),
                    distances(new Point2D(0, 0), index.nearestK(new Point2D(0, 0), 2)));
        }

        @Test
        @DisplayName("k 가 크면 전부 준다")
        void kBiggerThanSize() {
            SpatialIndex index = fixed();
            List<Point2D> all = index.nearestK(new Point2D(6, 5), 10);
            assertEquals(6, all.size(), "6 개밖에 없으면 6 개다");
            assertEquals(List.of(2L, 8L, 10L, 10L, 20L, 20L), distances(new Point2D(6, 5), all),
                    "10 과 20 이 각각 동점이다");
        }

        @Test
        @DisplayName("가까운 순서로 나온다")
        void sortedByDistance() {
            SpatialIndex index = grid();
            Point2D target = new Point2D(1, 1);
            List<Long> ds = distances(target, index.nearestK(target, 25));
            for (int i = 1; i < ds.size(); i++) {
                assertTrue(ds.get(i - 1) <= ds.get(i),
                        i + "번째가 " + (i - 1) + "번째보다 가깝다. 정렬이 안 됐다");
            }
            assertEquals(0L, ds.get(0), "자기 자신이 제일 가깝다");
            assertEquals(25, ds.size());
        }

        @Test
        @DisplayName("k=1 은 nearest 와 같은 거리다")
        void kOneMatchesNearest() {
            SpatialIndex index = fixed();
            for (int x = -2; x <= 12; x++) {
                for (int y = -2; y <= 12; y++) {
                    Point2D t = new Point2D(x, y);
                    assertEquals(t.squaredDistanceTo(index.nearest(t)),
                            t.squaredDistanceTo(index.nearestK(t, 1).get(0)), "target " + t);
                }
            }
        }

        @Test
        @DisplayName("k 가 0 이면 빈 목록, 음수면 예외")
        void kZeroAndNegative() {
            SpatialIndex index = fixed();
            assertTrue(index.nearestK(new Point2D(0, 0), 0).isEmpty());
            assertThrows(IllegalArgumentException.class, () -> index.nearestK(new Point2D(0, 0), -1));
        }

        @Test
        @DisplayName("결과는 전부 인덱스 안의 서로 다른 점이다")
        void resultsAreRealAndDistinct() {
            SpatialIndex index = grid();
            List<Point2D> got = index.nearestK(new Point2D(2, 2), 9);
            assertEquals(9, got.size());
            assertEquals(9, got.stream().distinct().count(), "같은 점을 여러 번 담으면 안 된다");
            for (Point2D p : got) assertTrue(index.contains(p), p + " 는 인덱스에 없다");
        }
    }

    @Nested
    @DisplayName("잘못된 인자")
    class BadArgs {

        @Test
        @DisplayName("null 은 전부 IllegalArgumentException")
        void nullsRejected() {
            SpatialIndex index = fixed();
            assertThrows(IllegalArgumentException.class, () -> index.insert(null));
            assertThrows(IllegalArgumentException.class, () -> index.contains(null));
            assertThrows(IllegalArgumentException.class, () -> index.rangeSearch(null));
            assertThrows(IllegalArgumentException.class, () -> index.nearest(null));
            assertThrows(IllegalArgumentException.class, () -> index.nearestK(null, 3));
        }

        @Test
        @DisplayName("뒤집힌 사각형은 만들 수 없다")
        void invertedRectangle() {
            assertThrows(IllegalArgumentException.class, () -> new Rectangle(5, 0, 1, 9));
            assertThrows(IllegalArgumentException.class, () -> new Rectangle(0, 5, 9, 1));
        }
    }
}
