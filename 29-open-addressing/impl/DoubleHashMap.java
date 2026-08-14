package com.datastructure.openaddr;

/**
 * [구현] 이중 해싱. 보폭을 두 번째 해시가 정한다.
 *
 * 선형과 이차는 홈이 같으면 경로가 같았다. 그래서 같은 홈으로 떨어진 키끼리 뭉쳤다.
 * 여기서는 보폭이 키마다 다르므로 홈이 같아도 두 번째 칸부터 갈라진다.
 * 군집화가 두 단계 모두 사라진다.
 *
 * 대신 보폭에 조건이 붙는다.
 *   보폭이 0 이면 제자리를 무한히 본다.
 *   보폭이 용량과 서로소가 아니면 일부 칸에 영영 못 간다.
 *     보폭 s, 용량 m 일 때 i*s mod m 이 도는 칸은 gcd(s, m) 로 나눈 m/gcd 개뿐이다.
 *     m 이 2의 거듭제곱이면 약수가 2 뿐이므로 s 를 홀수로 만들기만 하면 gcd 가 1 이 된다.
 *     그래서 마지막에 or 1 을 한다. 이 한 비트가 "모든 칸을 본다"를 보장한다.
 *
 * 캐시 지역성은 다섯 중 제일 나쁘다. 다음 칸이 배열의 아무 데나 있기 때문이다.
 * 탐사 횟수가 적어도 실제 시간은 그만큼 안 줄어드는 이유다.
 */
public class DoubleHashMap<K, V> extends ProbeSequenceMap<K, V> {

    public DoubleHashMap() {
        super();
    }

    public DoubleHashMap(int capacity, double maxLoad) {
        super(capacity, maxLoad);
    }

    /** 이 키의 보폭. 반드시 홀수다. */
    int stepFor(int hash) {
        return Hashing.mix(hash) | 1;
    }

    @Override
    int probe(int hash, int i) {
        return (hash + i * stepFor(hash)) & mask;
    }
}
