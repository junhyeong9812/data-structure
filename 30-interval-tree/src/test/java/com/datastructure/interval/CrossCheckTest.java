package com.datastructure.interval;

import static com.datastructure.interval.TestSupport.sorted;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * 전수 조사와의 무작위 대조. 이 문제집에서 버그를 가장 많이 잡는 테스트다.
 *
 * 가지치기는 "볼 필요 없는 곳을 안 본다"인데, 부등호 하나만 뒤집어도
 * "봐야 하는 곳을 안 본다"가 된다. 그러면 예외도 안 나고 답이 조용히 몇 개 빠진다.
 * 그 조용한 누락을 잡는 유일한 방법이 느린 구현과의 대조다.
 *
 * findAll 의 순서는 계약이 아니므로 정렬해서 비교한다.
 * findAny 는 답이 여럿일 수 있으므로 어느 구간인지가 아니라 "겹치는가"와 "있는가"를 본다.
 */
@DisplayName("전수 조사와의 무작위 대조")
class CrossCheckTest {

    private void crossCheck(String label, List<Interval> intervals, long queryOrigin,
                            long querySpan, long queryLength, int queryCount, long querySeed) {
        NaiveIntervalStore naive = new NaiveIntervalStore();
        IntervalTree tree = new IntervalTree();

        for (Interval iv : intervals) {
            boolean a = naive.insert(iv);
            boolean b = tree.insert(iv);
            assertEquals(a, b, label + ": " + iv + " 의 삽입 결과가 다르다");
        }
        assertEquals(naive.size(), tree.size(), label + ": 크기");
        assertEquals(naive.toList(), tree.toList(), label + ": 정렬된 전체 목록");

        TestSupport.Dice dice = new TestSupport.Dice(querySeed);
        long found = 0;
        long results = 0;
        for (int q = 0; q < queryCount; q++) {
            long from = queryOrigin + dice.next(querySpan);
            Interval query = new Interval(from, from + 1 + dice.next(queryLength));

            List<Interval> expected = sorted(naive.findAll(query));
            List<Interval> got = sorted(tree.findAll(query));
            assertEquals(expected.size(), got.size(),
                    label + ": " + query + " 의 결과 개수. 빠진 것 또는 더한 것이 있다");
            assertEquals(expected, got, label + ": " + query);
            results += expected.size();

            Interval any = tree.findAny(query);
            if (expected.isEmpty()) {
                assertNull(any, label + ": " + query + " 는 겹치는 것이 없는데 " + any + " 를 줬다");
            } else {
                assertNotNull(any, label + ": " + query + " 에 답이 " + expected.size() + "개 있는데 null 이다");
                assertTrue(any.overlaps(query), label + ": " + any + " 는 " + query + " 와 안 겹친다");
                assertTrue(expected.contains(any), label + ": 없는 구간을 만들어냈다 " + any);
                found++;
            }
            assertEquals(!expected.isEmpty(), tree.anyOverlaps(query), label + ": anyOverlaps " + query);
        }
        assertTrue(found > queryCount / 10,
                label + ": 답이 있는 질의가 " + found + "개뿐이다. 데이터셋이 너무 성기다");
        assertTrue(results > 0, label + ": 결과가 하나도 없다");
    }

    @Test
    @DisplayName("흩어진 짧은 구간 3000개")
    @Timeout(120)
    void spreadOut() {
        List<Interval> intervals = new TestSupport.Dice(12345L).intervals(3000, 100_000, 500);
        crossCheck("흩어진 3000개", intervals, 0, 100_000, 800, 400, 999L);
    }

    @Test
    @DisplayName("좁은 곳에 몰린 2000개 (같은 시작점이 잔뜩 나온다)")
    @Timeout(120)
    void clustered() {
        // 여기가 진짜 시험대다. 좌표 범위가 200 이라 같은 start 와 같은 구간이 수없이 겹친다.
        // start 가 같을 때 end 로 가르는 규칙이 어긋나면 여기서 무너진다.
        List<Interval> intervals = new TestSupport.Dice(777L).intervals(2000, 200, 30);
        crossCheck("몰린 2000개", intervals, -10, 250, 20, 400, 4242L);
    }

    @Test
    @DisplayName("서로 길게 겹치는 1500개 (가지치기가 거의 못 자른다)")
    @Timeout(120)
    void longOverlapping() {
        List<Interval> intervals = new TestSupport.Dice(4242L).intervals(1500, 50_000, 20_000);
        crossCheck("긴 구간 1500개", intervals, 0, 50_000, 5_000, 300, 31L);
    }

    @Test
    @DisplayName("정렬된 순서로 넣은 구간 (트리가 한 줄이 된다)")
    @Timeout(120)
    void sortedInsertion() {
        // 트리가 축퇴해도 답은 맞아야 한다. 느려질 뿐이다. 06번과 같은 이야기다.
        List<Interval> intervals = new ArrayList<>();
        for (int i = 0; i < 800; i++) intervals.add(Interval.of(i * 5, i * 5 + 7));
        crossCheck("정렬 삽입 800개", intervals, -50, 4_100, 60, 300, 88L);
    }

    @Test
    @DisplayName("음수 좌표가 섞인 구간")
    @Timeout(120)
    void negativeCoordinates() {
        // maxEnd 의 빈 부분트리 값을 0 으로 두면 여기서 깨진다. 최댓값의 항등원은 0 이 아니다.
        List<Interval> intervals = new ArrayList<>();
        TestSupport.Dice dice = new TestSupport.Dice(20260814L);
        for (int i = 0; i < 800; i++) {
            long s = dice.next(20_000) - 19_000;
            intervals.add(Interval.of(s, s + 1 + dice.next(300)));
        }
        crossCheck("음수 800개", intervals, -19_500, 21_000, 400, 300, 5150L);
    }

    @Test
    @DisplayName("넣고 지우기를 섞어도 답이 같다")
    @Timeout(120)
    void interleavedRemoval() {
        // 삭제 뒤 maxEnd 를 안 고치면 답이 빠지거나(작아진 값을 못 올림)
        // 방문만 는다(커진 값을 못 내림). 앞쪽은 여기서 잡힌다.
        NaiveIntervalStore naive = new NaiveIntervalStore();
        IntervalTree tree = new IntervalTree();
        TestSupport.Dice dice = new TestSupport.Dice(606L);
        List<Interval> live = new ArrayList<>();

        for (int step = 0; step < 4000; step++) {
            if (live.isEmpty() || dice.next(3) != 0) {
                Interval iv = dice.interval(30_000, 900);
                boolean a = naive.insert(iv);
                boolean b = tree.insert(iv);
                assertEquals(a, b, "step " + step + " 삽입 결과가 다르다: " + iv);
                if (a) live.add(iv);
            } else {
                Interval victim = live.remove((int) dice.next(live.size()));
                assertEquals(naive.remove(victim), tree.remove(victim), "삭제 결과가 다르다: " + victim);
            }
            if (step % 200 == 0) {
                assertEquals(naive.size(), tree.size(), "step " + step + " 크기");
                assertEquals(naive.toList(), tree.toList(), "step " + step + " 전체 목록");
                Interval query = dice.interval(30_000, 1_500);
                assertEquals(sorted(naive.findAll(query)), sorted(tree.findAll(query)),
                        "step " + step + " 질의 " + query);
            }
        }
        assertTrue(tree.size() > 100, "남은 것이 " + tree.size() + "개뿐이다");
    }
}
