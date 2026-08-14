package com.datastructure.openaddr;

/**
 * 개방 주소법 해시맵의 계약. 05번 Map 에 측정 세 가지를 얹었다.
 *
 * 05번에서 개방 주소법의 한계를 쟀다. 연속된 정수 키를 넣었더니 슬롯이 한 덩어리로 들어차고,
 * 그 구간으로 떨어지는 없는 키 조회가 덩어리 끝까지 걸어갔다. 체이닝 120ms 대 선형 탐사 75초였다.
 *
 * 원인은 일차 군집화(primary clustering)다. 충돌한 것이 옆칸으로 가면 그 옆칸도 막히고,
 * 막힌 구간이 서로 붙어서 자란다. 덩어리가 덩어리를 키운다.
 *
 * 이 문제집의 다섯 구현은 그 덩어리를 각각 다른 방식으로 판다.
 *   LinearProbeMap      기준선. 05번 것을 다시 만든다. 군집화가 여기서 나온다
 *   QuadraticProbeMap   +1, +4, +9 로 건너뛴다. 일차 군집화는 막지만 같은 홈끼리는 여전히 같은 경로다
 *   DoubleHashMap       두 번째 해시로 보폭을 정한다. 키마다 경로가 다르다
 *   RobinHoodMap        가난한 놈에게 자리를 준다. 탐사 거리의 분산을 줄인다
 *   CuckooHashMap       자리가 두 개뿐이다. 조회가 최악 O(1). 대신 삽입이 비싸다
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface ProbeMap<K, V> {

    /**
     * 키에 값을 넣는다. 이미 있던 키면 값을 바꾸고 이전 값을 반환한다. 없었으면 null.
     *
     * null 키는 허용하지 않는다(IllegalArgumentException). null 값은 허용한다.
     */
    V put(K key, V value);

    /** 없으면 null. */
    V get(Object key);

    boolean containsKey(Object key);

    /** 지우고 그 값을 반환한다. 없었으면 null. */
    V remove(Object key);

    int size();

    boolean isEmpty();

    void clear();

    /** 담긴 키 전부. 순서는 구현이 정한다. */
    Iterable<K> keys();

    // ------------------------------------------------------------------
    // 측정. 이 박스의 값어치가 여기 있다.
    // ------------------------------------------------------------------

    /** 총 칸 수. 쿠쿠 해싱은 두 테이블의 합이다. */
    int capacity();

    /**
     * 마지막 put/get/containsKey/remove 가 들여다본 칸 수.
     *
     * 홈 버킷을 보는 것이 1 이다. 비어 있는 칸도 들여다보긴 했으므로 센다.
     * (없는 키 조회는 빈칸을 봐야 없다는 것을 알 수 있다. 그 한 번이 이 방식의 비용이다.)
     *
     * 리사이즈와 재해싱 내부의 재배치는 세지 않는다. 그건 그 연산의 비용이지 탐사 경로가 아니다.
     * 시간을 재지 않고 이것을 세는 이유는 기계와 JIT 에 흔들리지 않기 때문이다.
     */
    int lastProbeCount();

    /**
     * 지금 담긴 키를 전부 조회했을 때 드는 탐사 수의 최댓값.
     *
     * 계약이 직접 센다. 구현이 자기 방식으로 세면 다섯을 나란히 놓을 수 없기 때문이다.
     * 로빈후드가 낮추는 것이 평균이 아니라 이 값이다.
     *
     * 주의: 안에서 get 을 부르므로 lastProbeCount 가 덮인다. 둘을 같이 재려면 순서를 지켜라.
     */
    default int maxProbeCount() {
        int max = 0;
        for (K key : keys()) {
            get(key);
            max = Math.max(max, lastProbeCount());
        }
        return max;
    }

    /** size / capacity. tombstone 은 안 센다. */
    default double loadFactor() {
        return (double) size() / capacity();
    }
}
