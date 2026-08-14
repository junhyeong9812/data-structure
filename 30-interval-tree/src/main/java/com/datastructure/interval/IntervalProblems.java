package com.datastructure.interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 구간 응용 둘. 여기서는 인터벌 트리를 쓰지 않는다.
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
     * 생각할 것
     *   - 정렬하고 나면 지금 들고 있는 하나와 다음 하나만 보면 된다. 왜 그것으로 충분한가.
     *   - 합치는 조건이 overlaps 와 다르다. [9,11) 과 [11,13) 은 안 겹치는데,
     *     둘을 합한 점의 집합은 [9,13) 과 정확히 같다. 반개구간이라 빈틈이 없기 때문이다.
     *   - 다음 구간이 앞 구간 안에 통째로 들어가 있으면 end 가 줄면 안 된다.
     *   - 마지막 하나는 반복문 안에서 안 나온다.
     *
     * TODO 9: 구현하라. intervals 가 null 이면 IllegalArgumentException, 비어 있으면 빈 목록.
     */
    public static List<Interval> merge(List<Interval> intervals) {
        // 트리가 필요 없는 문제다. 질의가 한 번뿐이면 정렬이 이긴다.
        throw new UnsupportedOperationException("TODO 9: merge");
    }

    /**
     * 동시에 겹치는 구간의 최대 개수. 회의실이 몇 개 필요한가.
     *
     * 시작 이벤트와 끝 이벤트를 각각 정렬해놓고 이른 것부터 처리하는 스위핑이다.
     *
     * 생각할 것
     *   - 지금 열려 있는 개수를 세면서 그 최댓값을 기억한다.
     *   - 좌표가 같을 때 무엇을 먼저 처리해야 하는가. 반개구간이므로 [9,11) 이 끝나는 11 시에
     *     [11,13) 이 시작해도 그 순간 방은 하나면 된다. 이 함정이 이 문제의 전부다.
     *   - 시작을 다 처리하면 끝난다. 남은 끝 이벤트는 최댓값을 못 바꾼다.
     *
     * TODO 10: 구현하라. 빈 목록이면 0, null 이면 IllegalArgumentException.
     */
    public static int maxConcurrent(List<Interval> intervals) {
        // 부등호에 등호를 넣으면 붙여 잡은 회의 넷이 방 두 개를 쓴다고 나온다.
        // 답이 1 이어야 할 자리에서 2 가 나오고, 겹치는 데이터에서는 그 차이가 잘 안 보인다.
        throw new UnsupportedOperationException("TODO 10: maxConcurrent");
    }
}
