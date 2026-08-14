package com.datastructure.conshash;

/**
 * 이름 하나를 원 위의 자리로 옮기는 함수. TODO 가 없다.
 *
 * 원의 크기는 0 이상 2^32 미만이다. 그 안의 값을 주는 것이 이 인터페이스의 계약이다.
 *
 * 이걸 갈아끼울 수 있게 만든 이유는 하나다. 해시가 나쁘면 원도 나쁘다는 것을 재기 위해서다.
 * Hashing.MIXED 와 Hashing.WEAK 를 같은 링에 번갈아 넣어 보면 분포가 어떻게 무너지는지 보인다.
 * 12번 스킵 리스트에서 Random 을 주입받게 만든 것과 같은 자리다.
 */
@FunctionalInterface
public interface RingHash {

    /** 0 이상 2^32 미만. */
    long position(String name);
}
