package com.datastructure.openaddr;

/**
 * 선형 탐사. 기준선이다.
 *
 * 자리가 차 있으면 옆칸으로 한 칸씩 간다. 05번 LinearProbingHashMap 과 같은 수열이다.
 * 캐시 지역성은 다섯 중 제일 좋다. 다음에 볼 칸이 바로 옆이기 때문이다.
 *
 * 그리고 일차 군집화가 여기서 나온다. 충돌한 것이 옆칸을 막고, 막힌 구간끼리 붙어서 자란다.
 * 길이 L 인 덩어리 앞에 떨어진 없는 키 조회는 L+1 칸을 본다.
 * 다른 네 구현은 전부 이 수를 낮추려는 시도다.
 */
public class LinearProbeMap<K, V> extends ProbeSequenceMap<K, V> {

    public LinearProbeMap() {
        super();
    }

    public LinearProbeMap(int capacity, double maxLoad) {
        super(capacity, maxLoad);
    }

    @Override
    int probe(int hash, int i) {
        // TODO 3: i 번째 탐사가 볼 칸. 옆칸으로 한 칸씩 간다.
        //
        // 되감기를 잊지 마라. 배열 끝에 닿으면 처음으로 돌아와야 한다.
        // 용량이 늘 2의 거듭제곱이므로 나머지 연산 대신 & mask 로 감을 수 있다.
        // (04번 원형 배열과 같은 계산이다. mask 는 capacity - 1 이다)
        throw new UnsupportedOperationException("TODO 3: probe");
    }
}
