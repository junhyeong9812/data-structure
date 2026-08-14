package com.datastructure.openaddr;

/**
 * [구현] 이차 탐사. 홈에서 점점 멀리 건너뛴다.
 *
 * 선형 탐사는 옆칸으로 가서 덩어리를 키웠다. 여기서는 건너뛰므로 덩어리가 안 붙는다.
 * 다만 같은 홈으로 떨어진 키끼리는 여전히 같은 경로를 걷는다(이차 군집화).
 * 그건 이중 해싱이 푼다.
 *
 * 이 구현에는 수열이 둘이다. triangular 가 그 스위치다.
 *
 *   naive       홈 + i*i          (+1, +4, +9, +16 ...)
 *   triangular  홈 + (i*i + i)/2  (+1, +3, +6, +10 ...)
 *
 * 용량이 2의 거듭제곱일 때 둘의 성질이 다르다.
 *   naive 는 칸의 일부만 방문한다. 방문 가능한 칸이 다 차면 빈칸이 남아 있어도 못 넣는다.
 *   triangular 는 모든 칸을 정확히 한 번씩 방문한다. 그래서 부하율이 얼마든 자리를 찾는다.
 *
 * 흔히 "이차 탐사는 절반만 본다"고 하는데, 그건 용량이 소수일 때 i*i 의 성질이다.
 * 2의 거듭제곱에서는 훨씬 나쁘다. QuadraticProbeMapTest 가 실제 수를 센다.
 */
public class QuadraticProbeMap<K, V> extends ProbeSequenceMap<K, V> {

    /** false 면 i*i 로만 건너뛴다. 한계 측정용이다. */
    final boolean triangular;

    public QuadraticProbeMap() {
        this(DEFAULT_CAPACITY, DEFAULT_MAX_LOAD, true);
    }

    public QuadraticProbeMap(int capacity, double maxLoad) {
        this(capacity, maxLoad, true);
    }

    public QuadraticProbeMap(int capacity, double maxLoad, boolean triangular) {
        super(capacity, maxLoad);
        this.triangular = triangular;
    }

    @Override
    int probe(int hash, int i) {
        // i 가 46341 을 넘으면 i*i 가 int 를 넘긴다. 그런데 재보면 결과가 같다.
        // 마지막에 2의 거듭제곱으로 & 하기 때문에 넘침(2^32 나머지)이 그대로 통과한다.
        // long 으로 두는 것은 "여기서 넘친다"를 읽는 사람에게 알리기 위해서다.
        long offset = triangular ? ((long) i * i + i) / 2 : (long) i * i;
        return (int) ((hash + offset) & mask);
    }
}
