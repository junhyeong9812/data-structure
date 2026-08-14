package com.datastructure.interval;

/**
 * 반개구간 [start, end). 값 객체다. 만든 뒤로 바뀌지 않는다.
 *
 * 반개구간으로 정한 이유가 이 박스의 절반이다.
 * 회의 [9,11) 이 끝나는 순간 회의 [11,13) 이 시작해도 두 회의는 겹치지 않는다.
 * 닫힌 구간 [9,11] 과 [11,13] 으로 잡으면 11 시 한 점을 공유해서 "겹친다"가 나온다.
 * 그러면 붙여 잡은 회의를 예약할 수 없다.
 *
 * 좌표는 long 이다. 부동소수점을 쓰면 같은 구간인지가 흔들려서 대조 검증이 성립하지 않는다.
 */
public final class Interval implements Comparable<Interval> {

    public final long start;
    public final long end;

    public Interval(long start, long end) {
        if (start >= end) {
            // 빈 구간을 허용하면 [5,5) 가 무엇과도 안 겹치는 유령이 되어 대조가 흐려진다.
            throw new IllegalArgumentException("start 는 end 보다 작아야 한다: [" + start + ", " + end + ")");
        }
        this.start = start;
        this.end = end;
    }

    /** 짧게 쓰려고 열어둔다. new Interval(a, b) 과 같다. */
    public static Interval of(long start, long end) {
        return new Interval(start, end);
    }

    /** 길이. 반개구간이라 end - start 가 그대로 길이다. */
    public long length() {
        return end - start;
    }

    /**
     * 겹치는가. 이 한 줄이 이 박스의 절반이다.
     *
     * 생각할 것
     *   - 겹치는 경우를 나열하면 네 가지가 넘는다. 안 겹치는 경우를 세면 둘뿐이다.
     *     통째로 왼쪽이거나 통째로 오른쪽이다. 그 둘을 부정하면 조건이 나온다.
     *   - 부등호를 반개구간에 맞춰야 한다. [9,11) 과 [11,13) 은 안 겹친다.
     *
     * TODO 1: 구현하라. other 가 null 이면 IllegalArgumentException.
     */
    public boolean overlaps(Interval other) {
        // 부등호 하나를 <= 로 써도 컴파일되고 예외도 안 나고 손으로 고른 예제는 대부분 맞는다.
        // 트리와 전수 조사가 이 함수를 같이 쓰므로 대조 테스트도 그 실수를 못 잡는다.
        // 그래서 IntervalTest 가 좌표 0..7 의 구간 쌍 1296 개를 전부 본다.
        throw new UnsupportedOperationException("TODO 1: overlaps");
    }

    /**
     * 점이 이 구간 안에 있나.
     *
     * TODO 2: 구현하라. 두 부등호 중 한쪽에만 등호가 붙는다. 어느 쪽인가.
     */
    public boolean contains(long point) {
        throw new UnsupportedOperationException("TODO 2: contains");
    }

    /** start 오름차순, 같으면 end 오름차순. 트리가 이 순서로 선다. */
    @Override
    public int compareTo(Interval other) {
        // 차를 구해 int 로 줄이는 흔한 방법은 여기서 넘친다. 좌표가 long 이기 때문이다.
        int cmp = Long.compare(start, other.start);
        return cmp != 0 ? cmp : Long.compare(end, other.end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interval other)) return false;
        return start == other.start && end == other.end;
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(start) + Long.hashCode(end);
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + ")";
    }
}
