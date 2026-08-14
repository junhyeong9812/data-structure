package com.datastructure.interval;

import static com.datastructure.interval.TestSupport.sorted;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * IntervalStore 계약 테스트. 전수 조사와 트리가 같은 벌을 물려받는다.
 *
 * 구현이 둘이므로 abstract 로 둔다. 하위 클래스는 create 하나만 준다.
 * 하위에서 같은 이름의 @Nested 를 만들면 여기 있는 것이 가려져 조용히 안 돌아간다.
 * 그래서 구조 검사는 IntervalTreeStructureTest 라는 별도 클래스에 둔다.
 */
abstract class IntervalStoreContractTest {

    protected abstract IntervalStore create();

    protected IntervalStore of(long... startEndPairs) {
        IntervalStore store = create();
        for (int i = 0; i < startEndPairs.length; i += 2) {
            store.insert(Interval.of(startEndPairs[i], startEndPairs[i + 1]));
        }
        return store;
    }

    /** 회의실 예약 셋. README 와 같은 예제다. */
    protected IntervalStore bookings() {
        return of(9, 11, 10, 12, 14, 15);
    }

    @Nested
    @DisplayName("담기와 지우기")
    class InsertAndRemove {

        @Test
        void insertsAndCounts() {
            IntervalStore store = create();
            assertTrue(store.isEmpty());
            assertTrue(store.insert(Interval.of(9, 11)));
            assertTrue(store.insert(Interval.of(10, 12)));
            assertEquals(2, store.size());
            assertFalse(store.isEmpty());
        }

        @Test
        @DisplayName("같은 구간을 두 번 넣으면 두 번째는 false")
        void rejectsDuplicates() {
            IntervalStore store = create();
            assertTrue(store.insert(Interval.of(9, 11)));
            assertFalse(store.insert(Interval.of(9, 11)));
            assertEquals(1, store.size(), "크기가 늘면 안 된다");
        }

        @Test
        @DisplayName("start 는 같고 end 가 다르면 다른 구간이다")
        void sameStartDifferentEnd() {
            IntervalStore store = of(9, 11, 9, 20, 9, 10);
            assertEquals(3, store.size());
            assertEquals(List.of(Interval.of(9, 10), Interval.of(9, 11), Interval.of(9, 20)),
                    store.toList(), "end 로 정렬된다");
        }

        @Test
        @DisplayName("지우기는 겹치는 것이 아니라 같은 것을 지운다")
        void removesExactMatch() {
            IntervalStore store = bookings();
            assertFalse(store.remove(Interval.of(10, 11)), "겹치기만 하는 구간은 못 지운다");
            assertEquals(3, store.size());
            assertTrue(store.remove(Interval.of(10, 12)));
            assertEquals(2, store.size());
            assertFalse(store.remove(Interval.of(10, 12)), "이미 지웠다");
        }

        @Test
        @DisplayName("전부 지워도 온전하고 다시 쓸 수 있다")
        void survivesFullDrain() {
            IntervalStore store = of(50, 60, 30, 40, 70, 80, 20, 25, 45, 55, 65, 75, 85, 90);
            List<Interval> all = new ArrayList<>(store.toList());
            for (Interval iv : all) {
                assertTrue(store.remove(iv), iv + " 를 못 지웠다");
            }
            assertTrue(store.isEmpty());
            assertTrue(store.toList().isEmpty());
            assertNull(store.findAny(Interval.of(0, 1000)));

            store.insert(Interval.of(1, 2));
            assertEquals(List.of(Interval.of(1, 2)), store.toList());
        }

        @Test
        void clearsAndReusable() {
            IntervalStore store = bookings();
            store.clear();
            assertEquals(0, store.size());
            assertTrue(store.toList().isEmpty());
            assertFalse(store.anyOverlaps(Interval.of(0, 100)));

            store.insert(Interval.of(3, 4));
            assertEquals(1, store.size());
        }

        @Test
        void rejectsNull() {
            IntervalStore store = create();
            assertThrows(IllegalArgumentException.class, () -> store.insert(null));
            assertThrows(IllegalArgumentException.class, () -> store.remove(null));
            assertThrows(IllegalArgumentException.class, () -> store.findAny(null));
            assertThrows(IllegalArgumentException.class, () -> store.findAll(null));
            assertThrows(IllegalArgumentException.class, () -> store.anyOverlaps(null));
        }
    }

    @Nested
    @DisplayName("겹침 질의")
    class OverlapQueries {

        @Test
        @DisplayName("회의실 예약에 10:30~11:30 을 묻는다")
        void findsOverlapping() {
            // 자정부터의 분 단위 좌표. 9시가 540 이다. 시각을 정수로 잡는 흔한 방법이다.
            IntervalStore store = of(540, 660, 600, 720, 840, 900);   // [9,11) [10,12) [14,15)
            List<Interval> got = sorted(store.findAll(Interval.of(630, 690)));
            assertEquals(List.of(Interval.of(540, 660), Interval.of(600, 720)), got,
                    "[14,15) 는 안 겹친다");
        }

        @Test
        @DisplayName("맞닿기만 하는 구간은 안 나온다")
        void touchingIsNotFound() {
            IntervalStore store = bookings();
            assertEquals(List.of(), store.findAll(Interval.of(12, 14)),
                    "[12,14) 는 [10,12) 와도 [14,15) 와도 안 겹친다");
            assertFalse(store.anyOverlaps(Interval.of(12, 14)));
            assertNull(store.findAny(Interval.of(12, 14)));
        }

        @Test
        @DisplayName("새 예약을 넣어도 되는지 묻는 것이 anyOverlaps 다")
        void anyOverlapsAnswersBookability() {
            IntervalStore store = bookings();
            assertTrue(store.anyOverlaps(Interval.of(10, 11)), "[9,11) 과 [10,12) 둘 다와 겹친다");
            assertFalse(store.anyOverlaps(Interval.of(12, 14)), "빈 시간이다");
            assertFalse(store.anyOverlaps(Interval.of(15, 16)), "[14,15) 가 끝난 뒤다");
            assertTrue(store.anyOverlaps(Interval.of(0, 100)), "전부와 겹친다");
        }

        @Test
        @DisplayName("findAny 는 겹치는 것 하나를 준다. 어느 것이든 맞다")
        void findAnyReturnsSomeOverlap() {
            IntervalStore store = bookings();
            Interval q = Interval.of(10, 11);
            Interval got = store.findAny(q);
            assertNotNull(got);
            assertTrue(got.overlaps(q), got + " 가 " + q + " 와 안 겹친다");
            assertTrue(store.toList().contains(got), "없는 구간을 만들어냈다: " + got);

            assertNull(store.findAny(Interval.of(100, 200)));
        }

        @Test
        @DisplayName("전부 겹치는 질의와 아무것도 안 겹치는 질의")
        void extremes() {
            IntervalStore store = bookings();
            assertEquals(sorted(store.toList()), sorted(store.findAll(Interval.of(0, 1000))));
            assertEquals(List.of(), store.findAll(Interval.of(1000, 1001)));
            assertEquals(List.of(), store.findAll(Interval.of(-100, -99)));
        }

        @Test
        @DisplayName("빈 저장소는 무엇을 물어도 빈 답")
        void emptyStoreAnswersEmpty() {
            IntervalStore store = create();
            assertNull(store.findAny(Interval.of(1, 2)));
            assertEquals(List.of(), store.findAll(Interval.of(1, 2)));
            assertFalse(store.anyOverlaps(Interval.of(1, 2)));
        }

        @Test
        @DisplayName("품고 있는 구간, 품긴 구간, 걸친 구간을 다 찾는다")
        void allContainmentShapes() {
            IntervalStore store = of(0, 100, 40, 45, 30, 50, 60, 120, -10, 45);
            List<Interval> got = sorted(store.findAll(Interval.of(41, 44)));
            assertEquals(List.of(Interval.of(-10, 45), Interval.of(0, 100),
                    Interval.of(30, 50), Interval.of(40, 45)), got,
                    "[60,120) 만 안 겹친다");
        }

        @Test
        @DisplayName("겹치는 것이 하나도 없는 틈")
        void gapBetweenIntervals() {
            IntervalStore store = of(0, 10, 20, 30, 40, 50);
            assertEquals(List.of(), store.findAll(Interval.of(10, 20)), "정확히 틈에 들어간다");
            assertEquals(List.of(Interval.of(0, 10)), store.findAll(Interval.of(9, 20)));
            assertEquals(List.of(Interval.of(20, 30)), store.findAll(Interval.of(10, 21)));
        }
    }

    @Nested
    @DisplayName("정렬된 전체 목록")
    class SortedListing {

        @Test
        void listsInOrder() {
            IntervalStore store = of(50, 60, 10, 20, 30, 40, 5, 100);
            assertEquals(List.of(Interval.of(5, 100), Interval.of(10, 20),
                    Interval.of(30, 40), Interval.of(50, 60)), store.toList());
        }

        @Test
        @DisplayName("지운 뒤에도 정렬이 유지된다")
        void staysSortedAfterRemoval() {
            IntervalStore store = create();
            for (int i = 0; i < 200; i++) {
                store.insert(Interval.of((i * 37) % 200, (i * 37) % 200 + 3));
            }
            assertEquals(200, store.size());
            for (int i = 0; i < 200; i += 2) {
                assertTrue(store.remove(Interval.of(i, i + 3)), "i=" + i);
            }
            List<Interval> listed = store.toList();
            assertEquals(100, listed.size());
            for (int i = 1; i < listed.size(); i++) {
                assertTrue(listed.get(i - 1).compareTo(listed.get(i)) < 0,
                        "정렬이 깨졌다: " + listed.get(i - 1) + " " + listed.get(i));
            }
        }
    }
}
