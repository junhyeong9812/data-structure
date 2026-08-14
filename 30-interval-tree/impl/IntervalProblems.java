package com.datastructure.interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * [구현] 구간 응용 둘. 여기서는 인터벌 트리를 쓰지 않는다.
 *
 * 트리를 만들어놓고 트리가 필요 없는 문제를 나란히 두는 것이 이 자리의 요점이다.
 * 두 문제 다 정렬 한 번에 훑기로 O(n log n) 에 끝난다.
 * 트리를 지어도 짓는 데만 O(n log n) 이 들고 질의는 그 뒤부터다.
 *
 * 트리가 이기는 것은 질의가 반복될 때다. 질의가 한 번이면 정렬이 이긴다.
 * 자료구조를 고르는 기준은 자료가 아니라 질의의 횟수다.
 */
public final class IntervalProblems {

    private IntervalProblems() {
    }

    /**
     * 겹치거나 맞닿은 구간을 합친다. 결과는 start 오름차순이고 서로 안 겹친다.
     *
     * 여기서 합치는 조건은 overlaps 와 다르다.
     * [9,11) 과 [11,13) 은 안 겹치지만 둘을 합한 점의 집합은 [9,13) 과 정확히 같다.
     * 반개구간이라 11 이 딱 한 번만 들어가서 빈틈이 없기 때문이다.
     * 그래서 합치기의 조건은 next.start 가 cur.end 이하인가이고, 등호가 들어간다.
     */
    public static List<Interval> merge(List<Interval> intervals) {
        if (intervals == null) {
            throw new IllegalArgumentException("구간 목록이 null 이다");
        }
        List<Interval> out = new ArrayList<>();
        if (intervals.isEmpty()) {
            return out;
        }
        List<Interval> sorted = new ArrayList<>(intervals);
        Collections.sort(sorted);       // start 오름차순. 이게 없으면 한 번 훑기가 성립하지 않는다

        long curStart = sorted.get(0).start;
        long curEnd = sorted.get(0).end;
        for (int i = 1; i < sorted.size(); i++) {
            Interval next = sorted.get(i);
            if (next.start <= curEnd) {
                curEnd = Math.max(curEnd, next.end);     // 통째로 들어간 구간이 있으므로 max 다
            } else {
                out.add(new Interval(curStart, curEnd));
                curStart = next.start;
                curEnd = next.end;
            }
        }
        out.add(new Interval(curStart, curEnd));         // 마지막 하나가 남는다
        return out;
    }

    /**
     * 동시에 겹치는 구간의 최대 개수. 회의실이 몇 개 필요한가.
     *
     * 시작 이벤트와 끝 이벤트를 각각 정렬해놓고 이른 것부터 처리한다.
     * 좌표가 같으면 끝을 먼저 처리해야 한다. 반개구간이므로 [9,11) 이 끝나는 11 시에
     * [11,13) 이 시작해도 그 순간 회의실은 하나면 된다.
     * starts[i] 가 ends[j] 보다 작을 때만 시작을 처리하는 것이 그 규칙이다.
     * 등호를 넣으면 붙여 잡은 회의가 겹친다고 나온다.
     */
    public static int maxConcurrent(List<Interval> intervals) {
        if (intervals == null) {
            throw new IllegalArgumentException("구간 목록이 null 이다");
        }
        int n = intervals.size();
        if (n == 0) {
            return 0;
        }
        long[] starts = new long[n];
        long[] ends = new long[n];
        for (int i = 0; i < n; i++) {
            Interval iv = intervals.get(i);
            if (iv == null) {
                throw new IllegalArgumentException("구간이 null 이다");
            }
            starts[i] = iv.start;
            ends[i] = iv.end;
        }
        Arrays.sort(starts);
        Arrays.sort(ends);

        int best = 0;
        int open = 0;
        int i = 0;
        int j = 0;
        while (i < n) {
            if (starts[i] < ends[j]) {
                open++;
                best = Math.max(best, open);
                i++;
            } else {
                open--;
                j++;
            }
        }
        return best;
    }
}
