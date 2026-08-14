package com.datastructure.openaddr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 이차 탐사. 일차 군집화는 막지만 두 가지를 새로 짊어진다.
 *
 * 하나는 이차 군집화다. 홈이 같으면 경로가 같아서 같은 홈끼리는 여전히 줄을 선다.
 * 다른 하나는 방문 한계다. i*i 로만 건너뛰면 용량이 2의 거듭제곱일 때 일부 칸에 영영 못 간다.
 */
class QuadraticProbeMapTest extends ProbeMapContractTest {

    @Override
    protected <K, V> ProbeMap<K, V> create() {
        return new QuadraticProbeMap<>();
    }

    @Override
    protected <K, V> ProbeMap<K, V> create(int capacity, double maxLoad) {
        return new QuadraticProbeMap<>(capacity, maxLoad);
    }

    /** 홈 0 에서 시작한 수열이 도달하는 서로 다른 칸의 수. */
    private static int reachable(QuadraticProbeMap<?, ?> map) {
        Set<Integer> slots = new HashSet<>();
        for (int i = 0; i < map.capacity(); i++) {
            slots.add(map.probe(0, i));
        }
        return slots.size();
    }

    @Test
    @DisplayName("한계: i*i 는 칸의 6분의 1 만 방문한다 (2의 거듭제곱 용량)")
    void naiveSquareReachesOnlyPartOfTheTable() {
        // "이차 탐사는 절반만 본다"는 말을 흔히 하는데, 그건 용량이 소수일 때 이야기다.
        // 용량이 2의 거듭제곱이면 훨씬 나쁘다. 실제로 세보면 6분의 1 언저리다.
        assertEquals(12, reachable(new QuadraticProbeMap<>(64, 1.0, false)));
        assertEquals(44, reachable(new QuadraticProbeMap<>(256, 1.0, false)));
        assertEquals(684, reachable(new QuadraticProbeMap<>(4096, 1.0, false)),
                "4096칸 중 684칸. 절반이 아니라 6분의 1 이다");

        // 삼각수는 전부 본다. i*i 대신 (i*i + i)/2 로 건너뛰는 이유가 이것이다.
        assertEquals(64, reachable(new QuadraticProbeMap<>(64, 1.0, true)));
        assertEquals(4096, reachable(new QuadraticProbeMap<>(4096, 1.0, true)));
    }

    @Test
    @DisplayName("한계: 빈칸이 52개 남았는데 넣기가 실패한다")
    void naiveSquareFailsWhileSlotsRemain() {
        // 홈이 전부 0 인 키만 넣는다. 그 홈에서 갈 수 있는 칸이 12개뿐이라 13번째에서 막힌다.
        // 부하율은 12/64 = 0.19 다. 이게 "부하율을 0.5 아래로 유지하라"로도 안 막히는 이유다.
        QuadraticProbeMap<Integer, String> naive = new QuadraticProbeMap<>(64, 1.0, false);
        for (int i = 0; i < 12; i++) naive.put(i * 64, "v");

        assertEquals(12, naive.size());
        assertEquals(64, naive.capacity(), "리사이즈가 일어나면 이 측정이 무의미해진다");
        IllegalStateException boom =
                assertThrows(IllegalStateException.class, () -> naive.put(12 * 64, "v"));
        assertTrue(boom.getMessage().contains("빈칸"), boom.getMessage());
        assertEquals(12, naive.size(), "실패한 삽입이 크기를 바꾸면 안 된다");

        // 같은 키들을 삼각수 수열은 전부 받는다. 64칸을 하나도 남기지 않는다.
        QuadraticProbeMap<Integer, String> triangular = new QuadraticProbeMap<>(64, 1.0, true);
        for (int i = 0; i < 64; i++) triangular.put(i * 64, "v");
        assertEquals(64, triangular.size());
        assertEquals(1.0, triangular.loadFactor(), 1e-9);
        for (int i = 0; i < 64; i++) assertEquals("v", triangular.get(i * 64), "키 " + (i * 64));
    }

    @Test
    @DisplayName("i 가 아무리 커도 수열은 정의대로다")
    void offsetStaysOnTheSequence() {
        // i 가 46341 을 넘으면 i*i 가 int 를 넘친다. 그래도 답은 같다.
        // 마지막에 2의 거듭제곱으로 & 하기 때문에 2^32 나머지가 그대로 통과하기 때문이다.
        // 이 테스트는 그 사실이 아니라 수열의 정의 자체를 못 박는다.
        // (long 을 int 로 바꾼 변종은 152개가 전부 통과했다. 결함이 아니라 동치 변환이다)
        QuadraticProbeMap<Integer, String> map = new QuadraticProbeMap<>(1024, 0.9);
        int mask = map.capacity() - 1;
        for (int i : new int[] {46_340, 46_341, 50_000, 100_000}) {
            long offset = ((long) i * i + i) / 2;
            assertEquals((int) ((0 + offset) & mask), map.probe(0, i), "i=" + i);
        }

        QuadraticProbeMap<Integer, String> naive = new QuadraticProbeMap<>(1024, 0.9, false);
        for (int i : new int[] {46_341, 100_000}) {
            assertEquals((int) (((long) i * i) & mask), naive.probe(0, i), "i=" + i);
        }
    }

    @Test
    @DisplayName("건너뛰므로 옆칸에 덩어리를 만들지 않는다")
    void doesNotFillNeighbouringSlots() {
        QuadraticProbeMap<Integer, String> map = new QuadraticProbeMap<>(64, 0.9);
        map.put(1, "a");
        map.put(65, "b");     // 홈이 같다. +1
        map.put(129, "c");    // 홈이 같다. +3

        assertEquals(1, map.keys[1]);
        assertEquals(65, map.keys[2]);
        assertEquals(129, map.keys[4], "세 번째는 +3 이다. 선형이면 3번 칸이었다");
        assertEquals(ProbeSequenceMap.EMPTY, map.states[3], "사이 칸이 비어 있어야 덩어리가 안 붙는다");
    }

    @Test
    @DisplayName("한계: 홈이 같으면 경로도 같다 (이차 군집화)")
    void sameHomeSharesTheWholePath() {
        // 이차 탐사가 푼 것은 홈이 다른 키들끼리 서로 방해하던 문제다.
        // 홈이 같은 키들은 여전히 한 줄로 선다. 경로가 홈 하나로만 정해지기 때문이다.
        QuadraticProbeMap<Integer, String> map = new QuadraticProbeMap<>(64, 0.9);
        int m = 8;
        for (int i = 0; i < m; i++) map.put(i * 64, "v");     // 홈이 전부 0

        for (int i = 0; i < m; i++) {
            map.get(i * 64);
            assertEquals(i + 1, map.lastProbeCount(), i + "번째로 넣은 키");
        }
        assertEquals(m, map.maxProbeCount(), "k 개가 같은 홈이면 마지막 놈은 k 칸을 걷는다");
    }
}
