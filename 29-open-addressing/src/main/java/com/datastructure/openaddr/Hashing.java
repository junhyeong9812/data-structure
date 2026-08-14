package com.datastructure.openaddr;

/**
 * 다섯 구현이 공유하는 해시 계산. TODO 가 없다.
 *
 * 홈 버킷을 정하는 hash 는 05번과 같이 부호 비트만 지운다. 일부러 섞지 않는다.
 * 섞어버리면 연속된 정수 키가 흩어져서 이 박스가 재현하려는 군집화가 사라진다.
 * 실무 구현이 해시를 섞는 이유가 바로 그것이지만, 여기서는 먼저 병을 보여야 한다.
 *
 * mix 는 두 번째 해시가 필요한 곳(이중 해싱의 보폭, 쿠쿠의 두 번째 자리)에서만 쓴다.
 * 첫 번째 해시와 상관이 낮아야 의미가 있다.
 */
final class Hashing {

    private Hashing() {
    }

    /** 홈 버킷 계산에 쓰는 해시. 음수를 없애되 값의 하위 비트는 그대로 둔다. */
    static int hash(Object key) {
        return key.hashCode() & 0x7fffffff;
    }

    /** 두 번째 해시. 상위 비트를 하위로 끌어내려 섞는다(splitmix 계열). */
    static int mix(int h) {
        h ^= (h >>> 16);
        h *= 0x9E3779B1;
        h ^= (h >>> 15);
        return h;
    }

    /** n 이상인 2의 거듭제곱. 최소 4. */
    static int tableSizeFor(int n) {
        int capacity = 4;
        while (capacity < n) {
            capacity <<= 1;
        }
        return capacity;
    }
}
