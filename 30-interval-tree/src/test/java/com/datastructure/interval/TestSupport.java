package com.datastructure.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 테스트가 공유하는 도구. 자료구조가 아니라 검증 장비다.
 *
 * 여기 있는 것은 둘뿐이다.
 *   1. 결정적 난수  - Random 을 쓰면 JDK 가 바뀔 때 기댓값이 흔들린다
 *   2. 구간 정렬    - findAll 의 순서는 계약이 아니므로 비교 직전에만 정렬한다
 */
final class TestSupport {

    private TestSupport() {
    }

    /** start, end 순 정렬. 순서를 계약으로 만들지 않으려고 비교 직전에만 쓴다. */
    static List<Interval> sorted(List<Interval> intervals) {
        List<Interval> copy = new ArrayList<>(intervals);
        Collections.sort(copy);
        return copy;
    }

    /** 결정적 난수 발생기. 같은 seed 면 언제 어디서 돌려도 같은 구간이 나온다. */
    static final class Dice {
        private long state;

        Dice(long seed) {
            this.state = seed;
        }

        long next(long bound) {
            state = state * 6364136223846793005L + 1442695040888963407L;
            return Math.floorMod(state >>> 33, bound);
        }

        /** [s, s + len) 을 만든다. len 은 1 이상이므로 빈 구간이 안 나온다. */
        Interval interval(long span, long maxLength) {
            long s = next(span);
            return new Interval(s, s + 1 + next(maxLength));
        }

        List<Interval> intervals(int count, long span, long maxLength) {
            List<Interval> out = new ArrayList<>();
            for (int i = 0; i < count; i++) out.add(interval(span, maxLength));
            return out;
        }
    }
}
