package com.datastructure.conshash;

/**
 * 네 구현이 공유하는 해시. TODO 가 없다.
 *
 * 05번과 29번에서 계속 나온 이야기가 여기서 한 번 더 나온다. 해시가 나쁘면 그 위에 얹은 것도 나쁘다.
 * 여기서는 그 이야기가 원 위의 자리로 나타난다.
 */
public final class Hashing {

    /** 원의 크기. 자리는 0 이상 이 값 미만이다. */
    public static final long RING_SIZE = 1L << 32;

    private Hashing() {
    }

    /**
     * 섞는 해시. 이름을 FNV-1a 로 64비트로 만든 뒤 splitmix64 로 흩고 상위 32비트를 쓴다.
     *
     * 상위 비트를 쓰는 이유는 곱셈 해시가 상위 비트에서 더 잘 섞이기 때문이다.
     */
    public static final RingHash MIXED = name -> mix64(fnv64(name)) >>> 32;

    /**
     * 안 섞는 해시. 자바 String.hashCode 를 부호만 지워서 그대로 쓴다.
     *
     * node-0 과 node-1 의 hashCode 는 정확히 1 만큼 다르다. 즉 원 위에서 한 칸 옆이다.
     * 노드 열 대가 2^32 짜리 원의 한 점에 뭉쳐 있게 되고, 그러면 가상 노드를 아무리 늘려도 소용없다.
     * BalanceTest 가 그것을 잰다.
     */
    public static final RingHash WEAK = name -> name.hashCode() & 0xffffffffL;

    /** FNV-1a 64비트. 점프 해시가 키를 long 으로 만들 때도 쓴다. */
    public static long fnv64(String s) {
        long h = 0xcbf29ce484222325L;
        for (int i = 0; i < s.length(); i++) {
            h ^= s.charAt(i) & 0xff;
            h *= 0x100000001b3L;
        }
        return h;
    }

    /** splitmix64 의 마무리 단계. 비트를 골고루 흩는다. */
    public static long mix64(long z) {
        z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
        return z ^ (z >>> 31);
    }

    /**
     * 모듈로 샤딩이 쓰는 음수 없는 해시.
     *
     * 원 방식과 같은 자리 계산에서 뽑는다. 두 방식의 이동량을 나란히 놓으려면
     * 키를 흩는 방식이 같아야 하고, 그래야 차이가 배정 규칙에서만 나온다.
     */
    public static int bucketHash(String key) {
        return (int) (MIXED.position(key) & 0x7fffffffL);
    }
}
