package com.datastructure.interval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 값 객체 Interval. 이 문제의 절반이 여기 있다.
 *
 * overlaps 한 줄이 틀리면 트리도 전수 조사도 같이 틀린다.
 * 둘이 같은 함수를 쓰므로 대조 테스트가 그 오류를 못 잡는다. 그래서 여기서 따로 못 박는다.
 */
@DisplayName("Interval - 반개구간 [start, end)")
class IntervalTest {

    /** 구간이 덮는 정수 점의 집합. 겹침의 정의를 코드가 아니라 뜻으로 다시 쓴 것이다. */
    private static Set<Long> pointsOf(Interval iv) {
        Set<Long> out = new HashSet<>();
        for (long p = iv.start; p < iv.end; p++) out.add(p);
        return out;
    }

    @Nested
    @DisplayName("만들기")
    class Construction {

        @Test
        @DisplayName("빈 구간과 뒤집힌 구간은 거절한다")
        void rejectsEmptyAndReversed() {
            assertThrows(IllegalArgumentException.class, () -> new Interval(5, 5),
                    "[5, 5) 는 아무것도 안 담는다. 유령 구간을 허용하면 대조가 흐려진다");
            assertThrows(IllegalArgumentException.class, () -> new Interval(9, 3));
        }

        @Test
        void keepsEndpointsAndLength() {
            Interval iv = Interval.of(9, 11);
            assertEquals(9, iv.start);
            assertEquals(11, iv.end);
            assertEquals(2, iv.length());
        }

        @Test
        @DisplayName("음수 좌표와 아주 큰 좌표도 된다")
        void handlesNegativeAndLarge() {
            assertEquals(10, Interval.of(-5, 5).length());
            Interval huge = Interval.of(-4_000_000_000L, 4_000_000_000L);
            assertEquals(8_000_000_000L, huge.length(), "int 로 잡았으면 여기서 넘친다");
        }

        @Test
        void equalityIsByEndpoints() {
            assertEquals(Interval.of(1, 2), Interval.of(1, 2));
            assertEquals(Interval.of(1, 2).hashCode(), Interval.of(1, 2).hashCode());
            assertNotEquals(Interval.of(1, 2), Interval.of(1, 3));
        }
    }

    @Nested
    @DisplayName("겹침 - 경계가 전부다")
    class Overlaps {

        @Test
        @DisplayName("맞닿은 구간은 안 겹친다. 이게 반개구간을 고른 이유다")
        void touchingDoesNotOverlap() {
            Interval nine = Interval.of(9, 11);
            assertFalse(nine.overlaps(Interval.of(11, 13)),
                    "9시 회의가 11시에 끝나고 11시 회의가 시작한다. 겹치지 않는다");
            assertFalse(nine.overlaps(Interval.of(5, 9)), "반대쪽도 같다");
            assertFalse(Interval.of(11, 13).overlaps(nine), "겹침은 대칭이다");
        }

        @Test
        @DisplayName("한 점만 겹쳐도 겹친다")
        void singlePointOverlapCounts() {
            assertTrue(Interval.of(9, 11).overlaps(Interval.of(10, 11)));
            assertTrue(Interval.of(9, 11).overlaps(Interval.of(10, 100)),
                    "10 하나만 공유해도 겹친다");
            assertTrue(Interval.of(0, 1).overlaps(Interval.of(0, 1)), "자기 자신과는 언제나 겹친다");
        }

        @Test
        @DisplayName("품고 있어도 겹친다")
        void containmentOverlaps() {
            assertTrue(Interval.of(0, 100).overlaps(Interval.of(40, 50)));
            assertTrue(Interval.of(40, 50).overlaps(Interval.of(0, 100)));
        }

        @Test
        @DisplayName("좌표 0..7 의 모든 구간 쌍을 전수로 대조한다")
        void exhaustiveAgainstPointSets() {
            // 겹침 조건 한 줄을 믿지 않고, "공유하는 점이 있나"로 다시 계산해 맞춘다.
            // 손으로 고른 예제는 부등호 하나를 뒤집어도 살아남는 것이 있다. 전수는 안 그렇다.
            List<Interval> all = new ArrayList<>();
            for (int s = 0; s <= 7; s++) {
                for (int e = s + 1; e <= 8; e++) all.add(Interval.of(s, e));
            }
            assertEquals(36, all.size());

            int overlapping = 0;
            for (Interval a : all) {
                for (Interval b : all) {
                    Set<Long> shared = new HashSet<>(pointsOf(a));
                    shared.retainAll(pointsOf(b));
                    boolean expected = !shared.isEmpty();
                    assertEquals(expected, a.overlaps(b), a + " overlaps " + b);
                    assertEquals(a.overlaps(b), b.overlaps(a), "대칭이어야 한다: " + a + " " + b);
                    if (expected) overlapping++;
                }
            }
            assertEquals(1296, all.size() * all.size(), "36 x 36 쌍을 전부 봤다");
            assertTrue(overlapping > 0 && overlapping < 1296, "겹치는 쌍 " + overlapping + "개");
        }

        @Test
        void rejectsNull() {
            assertThrows(IllegalArgumentException.class, () -> Interval.of(1, 2).overlaps(null));
        }
    }

    @Nested
    @DisplayName("점 포함과 정렬 순서")
    class ContainsAndOrder {

        @Test
        @DisplayName("end 는 안 들어간다")
        void containsIsHalfOpen() {
            Interval iv = Interval.of(9, 11);
            assertFalse(iv.contains(8));
            assertTrue(iv.contains(9), "start 는 들어간다");
            assertTrue(iv.contains(10));
            assertFalse(iv.contains(11), "end 는 안 들어간다. 그래서 [11,13) 과 안 겹친다");
            assertFalse(iv.contains(12));
        }

        @Test
        @DisplayName("길이가 곧 담긴 정수 점의 개수다")
        void lengthEqualsPointCount() {
            for (int e = 1; e <= 20; e++) {
                Interval iv = Interval.of(0, e);
                int count = 0;
                for (long p = -5; p < 30; p++) if (iv.contains(p)) count++;
                assertEquals(iv.length(), count, iv + " 가 담는 점의 개수");
            }
        }

        @Test
        @DisplayName("start 로 정렬하고 같으면 end 로")
        void ordersByStartThenEnd() {
            List<Interval> given = new ArrayList<>(List.of(
                    Interval.of(9, 20), Interval.of(9, 11), Interval.of(1, 100), Interval.of(9, 10)));
            java.util.Collections.sort(given);
            assertEquals(List.of(Interval.of(1, 100), Interval.of(9, 10),
                    Interval.of(9, 11), Interval.of(9, 20)), given);
            assertEquals(0, Interval.of(3, 4).compareTo(Interval.of(3, 4)));
        }

        @Test
        @DisplayName("큰 좌표를 빼서 비교하면 넘친다")
        void comparisonDoesNotOverflow() {
            // start 차이를 뺄셈으로 구하면 여기서 부호가 뒤집힌다. Long.compare 는 안 그렇다.
            Interval low = Interval.of(-9_000_000_000_000_000_000L, 0);
            Interval high = Interval.of(9_000_000_000_000_000_000L, 9_000_000_000_000_000_001L);
            assertTrue(low.compareTo(high) < 0, "작은 것이 앞이다");
            assertTrue(high.compareTo(low) > 0);
        }
    }
}
