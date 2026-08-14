package com.datastructure.openaddr;

/**
 * [구현] 선형 탐사. 기준선이다.
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
        return (hash + i) & mask;
    }
}
