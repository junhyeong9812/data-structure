package com.datastructure.interval;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * 응용 둘. 여기서는 트리를 안 쓴다.
 *
 * 트리를 만들어놓고 트리가 필요 없는 문제를 나란히 두는 것이 이 자리의 요점이다.
 * 두 문제 다 정렬 한 번에 훑기로 끝난다. 질의가 한 번뿐이면 정렬이 이긴다.
 * 트리가 이기는 것은 질의가 반복될 때다.
 */
@DisplayName("구간 응용 - 트리가 필요 없는 문제들")
class IntervalProblemsTest {

    private static List<Interval> of(long... startEndPairs) {
        List<Interval> out = new ArrayList<>();
        for (int i = 0; i < startEndPairs.length; i += 2) {
            out.add(Interval.of(startEndPairs[i], startEndPairs[i + 1]));
        }
        return out;
    }

    /** 구간 목록이 덮는 정수 점의 집합. merge 의 뜻을 코드가 아니라 정의로 다시 쓴 것이다. */
    private static Set<Long> pointsOf(List<Interval> intervals) {
        Set<Long> out = new HashSet<>();
        for (Interval iv : intervals) {
            for (long p = iv.start; p < iv.end; p++) out.add(p);
        }
        return out;
    }

    @Nested
    @DisplayName("merge - 겹치거나 맞닿은 것을 합친다")
    class Merge {

        @Test
        @DisplayName("회의실 예약 셋")
        void bookings() {
            assertEquals(of(9, 12, 14, 15), IntervalProblems.merge(of(9, 11, 10, 12, 14, 15)));
        }

        @Test
        @DisplayName("맞닿은 구간은 합친다. overlaps 와 조건이 다르다")
        void touchingIntervalsMerge() {
            // [9,11) 과 [11,13) 은 안 겹친다. 그런데 둘이 덮는 점의 집합은 [9,13) 과 정확히 같다.
            // 반개구간이라 11 이 딱 한 번만 들어가서 빈틈이 없다. 그래서 여기서는 등호가 들어간다.
            assertEquals(of(9, 13), IntervalProblems.merge(of(9, 11, 11, 13)));
            assertEquals(of(9, 13), IntervalProblems.merge(of(11, 13, 9, 11)), "입력 순서와 무관하다");
        }

        @Test
        @DisplayName("품긴 구간은 사라진다")
        void containedIntervalsVanish() {
            assertEquals(of(1, 4), IntervalProblems.merge(of(1, 4, 2, 3)));
            assertEquals(of(1, 10, 11, 12), IntervalProblems.merge(of(1, 10, 2, 3, 4, 5, 11, 12)),
                    "긴 것 안에 짧은 것이 들어가면 end 가 줄면 안 된다");
        }

        @Test
        @DisplayName("연쇄로 이어지면 통째로 하나가 된다")
        void chainsCollapse() {
            assertEquals(of(1, 7), IntervalProblems.merge(of(1, 3, 3, 5, 5, 7, 2, 6)));
        }

        @Test
        void trivialInputs() {
            assertEquals(List.of(), IntervalProblems.merge(List.of()));
            assertEquals(of(5, 6), IntervalProblems.merge(of(5, 6)));
            assertThrows(IllegalArgumentException.class, () -> IntervalProblems.merge(null));
        }

        @Test
        @DisplayName("결과는 정렬되고, 서로 겹치지도 맞닿지도 않는다")
        void resultIsDisjointAndSorted() {
            TestSupport.Dice dice = new TestSupport.Dice(9090L);
            List<Interval> given = dice.intervals(60, 10_000, 50);
            List<Interval> merged = IntervalProblems.merge(given);

            for (int i = 1; i < merged.size(); i++) {
                assertTrue(merged.get(i - 1).end < merged.get(i).start,
                        "결과끼리 붙어 있다: " + merged.get(i - 1) + " " + merged.get(i));
            }
            assertEquals(pointsOf(given), pointsOf(merged), "덮는 점의 집합이 달라졌다");
            assertEquals(48, merged.size(), "60개가 48개로 줄어든다");
        }

        @Test
        @DisplayName("빽빽하면 거의 하나로 합쳐진다")
        void denseInputCollapses() {
            TestSupport.Dice dice = new TestSupport.Dice(1234L);
            List<Interval> given = dice.intervals(80, 300, 30);
            List<Interval> merged = IntervalProblems.merge(given);
            assertEquals(2, merged.size());
            assertEquals(pointsOf(given), pointsOf(merged));
        }
    }

    @Nested
    @DisplayName("maxConcurrent - 회의실이 몇 개 필요한가")
    class MaxConcurrent {

        @Test
        @DisplayName("회의실 예약 셋은 두 개가 필요하다")
        void bookings() {
            assertEquals(2, IntervalProblems.maxConcurrent(of(9, 11, 10, 12, 14, 15)));
        }

        @Test
        @DisplayName("붙여 잡은 회의는 방 하나면 된다. 이 함정이 이 문제의 전부다")
        void touchingMeetingsNeedOneRoom() {
            // 끝 이벤트를 시작보다 먼저 처리해야 한다. 좌표가 같을 때 시작을 먼저 처리하면
            // 11시에 끝나는 회의와 11시에 시작하는 회의가 잠깐 같이 열려 있는 것으로 세어진다.
            assertEquals(1, IntervalProblems.maxConcurrent(of(0, 10, 10, 20, 20, 30, 30, 40)),
                    "이어달리기 회의 넷. 등호를 넣으면 2 가 나온다");
            assertEquals(1, IntervalProblems.maxConcurrent(of(9, 11, 11, 13)));
        }

        @Test
        @DisplayName("전부 겹치면 개수만큼 필요하다")
        void fullyOverlappingNeedsAll() {
            assertEquals(3, IntervalProblems.maxConcurrent(of(1, 5, 2, 6, 3, 7)));
            assertEquals(4, IntervalProblems.maxConcurrent(of(1, 10, 2, 10, 3, 10, 4, 10)));
            assertEquals(3, IntervalProblems.maxConcurrent(of(0, 100, 0, 101, 1, 2, 50, 60)),
                    "[1,2) 와 [50,60) 은 서로 안 겹친다. 셋이 최대다");
        }

        @Test
        void trivialInputs() {
            assertEquals(0, IntervalProblems.maxConcurrent(List.of()));
            assertEquals(1, IntervalProblems.maxConcurrent(of(5, 6)));
            assertEquals(1, IntervalProblems.maxConcurrent(of(0, 10, 20, 30, 40, 50)),
                    "떨어져 있으면 하나면 된다");
            assertThrows(IllegalArgumentException.class, () -> IntervalProblems.maxConcurrent(null));
        }

        @Test
        @DisplayName("스위핑과 점 세기가 같은 답을 준다")
        void sweepMatchesPointCounting() {
            // 답이 나는 자리는 언제나 어떤 구간의 시작점이다. 덮는 개수는 거기서만 는다.
            TestSupport.Dice dice = new TestSupport.Dice(1234L);
            List<Interval> given = dice.intervals(80, 300, 30);

            int byCounting = 0;
            for (Interval a : given) {
                int at = 0;
                for (Interval b : given) if (b.contains(a.start)) at++;
                byCounting = Math.max(byCounting, at);
            }
            assertEquals(byCounting, IntervalProblems.maxConcurrent(given));
            assertEquals(8, byCounting, "이 데이터셋은 회의실 8개가 필요하다");
        }
    }

    @Nested
    @DisplayName("트리는 언제 이기는가")
    class WhenTheTreeWins {

        @Test
        @DisplayName("한 번 묻는 질문에는 정렬이 이긴다")
        void oneShotQuestionsBelongToSorting() {
            // 같은 답을 트리로도 낼 수 있다. 어떤 구간의 시작점을 덮는 구간이 몇 개인지 물으면 된다.
            // 답은 같고, 걸음 수만 는다. 게다가 트리는 짓는 데 n 번을 먼저 써야 한다.
            TestSupport.Dice dice = new TestSupport.Dice(2718L);
            List<Interval> given = dice.intervals(1_000, 20_000, 400);

            IntervalTree tree = new IntervalTree();
            for (Interval iv : given) tree.insert(iv);

            long visits = 0;
            int best = 0;
            for (Interval iv : given) {
                best = Math.max(best, tree.findAll(new Interval(iv.start, iv.start + 1)).size());
                visits += tree.visitedNodes();
            }

            assertEquals(IntervalProblems.maxConcurrent(given), best, "답은 같다");
            assertTrue(visits > given.size(),
                    "트리로 풀면 노드를 " + visits + "번 본다. 구간은 " + given.size() + "개뿐이다");
        }

        @Test
        @DisplayName("반복해서 묻는 질문에는 트리가 이긴다")
        void repeatedQuestionsBelongToTheTree() {
            TestSupport.Dice dice = new TestSupport.Dice(2718L);
            List<Interval> given = dice.intervals(1_000, 20_000, 400);

            NaiveIntervalStore naive = new NaiveIntervalStore();
            IntervalTree tree = new IntervalTree();
            for (Interval iv : given) {
                naive.insert(iv);
                tree.insert(iv);
            }

            long naiveTotal = 0;
            long treeTotal = 0;
            TestSupport.Dice queries = new TestSupport.Dice(161803L);
            for (int q = 0; q < 500; q++) {
                Interval query = queries.interval(20_000, 50);
                naive.findAll(query);
                naiveTotal += naive.visitedNodes();
                tree.findAll(query);
                treeTotal += tree.visitedNodes();
            }
            assertEquals(500L * naive.size(), naiveTotal);
            assertTrue(treeTotal * 20 < naiveTotal,
                    "질의 500번에 트리는 " + treeTotal + " 번, 전수는 " + naiveTotal + " 번 본다");
        }
    }
}
