package com.datastructure.interval;

/**
 * [구현] 반개구간 [start, end). 값 객체다. 만든 뒤로 바뀌지 않는다.
 *
 * 참고: 이 폴더에 IntervalStore.java 와 VisitCounting.java 가 없다. 인터페이스는 src/main 에서 온다.
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
     * 두 구간이 안 겹치는 경우는 둘뿐이다. 내가 통째로 왼쪽이거나, 통째로 오른쪽이다.
     * 그 둘을 부정하면 겹침 조건이 나온다.
     */
    public boolean overlaps(Interval other) {
        if (other == null) {
            throw new IllegalArgumentException("구간이 null 이다");
        }
        return this.start < other.end && other.start <= this.end;
    }

    /** 점이 이 구간 안에 있나. end 는 안 들어간다. */
    public boolean contains(long point) {
        return start <= point && point < end;
    }

    /** start 오름차순, 같으면 end 오름차순. 트리가 이 순서로 선다. */
    @Override
    public int compareTo(Interval other) {
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
