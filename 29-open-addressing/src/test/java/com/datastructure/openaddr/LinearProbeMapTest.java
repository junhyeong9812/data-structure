package com.datastructure.openaddr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 선형 탐사. 기준선이고, 군집화가 여기서 나온다.
 *
 * 계약은 부모가 본다. 여기서는 05번에서 다룬 tombstone 과, 이 박스의 출발점인 덩어리를 본다.
 */
class LinearProbeMapTest extends ProbeMapContractTest {

    @Override
    protected <K, V> ProbeMap<K, V> create() {
        return new LinearProbeMap<>();
    }

    @Override
    protected <K, V> ProbeMap<K, V> create(int capacity, double maxLoad) {
        return new LinearProbeMap<>(capacity, maxLoad);
    }

    @Test
    @DisplayName("충돌하면 바로 옆칸으로 간다")
    void probesToNextSlot() {
        LinearProbeMap<Integer, String> map = new LinearProbeMap<>(64, 0.9);
        map.put(1, "a");
        map.put(65, "b");

        assertEquals(1, map.keys[1]);
        assertEquals(65, map.keys[2], "옆칸이 아니면 선형 탐사가 아니다");
        assertEquals("b", map.values[2]);
        assertEquals(ProbeSequenceMap.OCCUPIED, map.states[2]);

        map.get(65);
        assertEquals(2, map.lastProbeCount(), "한 칸 밀렸으니 두 번 본다");
    }

    @Test
    @DisplayName("지운 자리는 EMPTY 가 아니라 TOMBSTONE 이다")
    void removeLeavesTombstone() {
        LinearProbeMap<Integer, String> map = new LinearProbeMap<>(64, 0.9);
        map.put(1, "a");
        map.remove(1);

        assertEquals(ProbeSequenceMap.TOMBSTONE, map.states[1], "EMPTY 로 만들면 탐사 사슬이 끊긴다");
        assertEquals(1, map.tombstones());
        assertNull(map.keys[1], "키 참조는 놓아야 한다");
        assertEquals(0, map.size());
    }

    @Test
    @DisplayName("tombstone 자리를 재사용하되 중복을 만들지 않는다")
    void reusesTombstoneWithoutDuplicating() {
        // 1(홈), 65(홈+1) 을 넣고 1 을 지우면 홈이 tombstone 이다.
        // 이때 65 를 다시 put 하면서 그 자리를 그냥 쓰면 65 가 두 군데 생긴다.
        LinearProbeMap<Integer, String> map = new LinearProbeMap<>(64, 0.9);
        map.put(1, "a");
        map.put(65, "b");
        map.remove(1);

        assertEquals("b", map.put(65, "B"), "이전 값을 반환해야 한다");
        assertEquals(1, map.size(), "같은 키가 두 개 생기면 안 된다");
        assertEquals("B", map.get(65));

        map.put(1, "z");
        assertEquals(ProbeSequenceMap.OCCUPIED, map.states[1], "빈 tombstone 은 재사용해야 한다");
        assertEquals(0, map.tombstones());
        assertEquals(2, map.size());
    }

    @Test
    @DisplayName("리사이즈가 tombstone 청소를 겸한다")
    void resizeCleansTombstones() {
        // 넣고 지우기만 반복하면 실제 원소는 적은데 자리는 계속 막힌다.
        // 그래서 리사이즈 판단은 size 가 아니라 used 로 한다. 05번에서 다룬 그대로다.
        LinearProbeMap<Integer, String> map = new LinearProbeMap<>();
        for (int i = 0; i < 500; i++) {
            map.put(i, "v");
            map.remove(i);
        }
        assertEquals(0, map.size());
        assertTrue(map.capacity() <= 16,
                "원소가 0인데 배열이 " + map.capacity() + " 까지 커졌다. 같은 크기로 다시 배치해야 한다");
        assertTrue(map.tombstones() < map.capacity(), "청소가 한 번도 안 됐다");
    }

    @Test
    @DisplayName("한계: 연속된 정수 키는 한 덩어리가 되고 없는 키 조회가 그 끝까지 걷는다")
    void sequentialKeysCluster() {
        // 05번에서 시간으로 봤던 것(체이닝 120ms 대 선형탐사 75초)을 여기서는 걸음 수로 본다.
        // 정수의 hashCode 는 값 그대로다. 0..n-1 은 슬롯 0..n-1 에 빈틈없이 들어찬다.
        // 있는 키를 찾는 것은 한 번이면 된다. 문제는 그 구간으로 떨어지는 없는 키다.
        int n = 1000;
        LinearProbeMap<Integer, String> map = new LinearProbeMap<>(4096, 1.0);
        for (int i = 0; i < n; i++) map.put(i, "v");

        for (int i = 0; i < n; i++) {
            map.get(i);
            assertEquals(1, map.lastProbeCount(), "있는 키는 홈에 그대로 있다: " + i);
        }

        // 4096 + i 는 없는 키인데 홈은 i 다. 덩어리 한가운데로 떨어진다.
        map.get(4096);
        assertEquals(n + 1, map.lastProbeCount(),
                "덩어리 맨 앞에 떨어진 없는 키가 끝의 빈칸까지 " + n + "칸을 걸어야 한다");
        map.get(4096 + n / 2);
        assertEquals(n / 2 + 1, map.lastProbeCount(), "한가운데 떨어져도 절반은 걷는다");

        assertEquals(1, map.maxProbeCount(), "있는 키만 보면 아무 문제가 없어 보인다");
    }

    @Test
    @DisplayName("한계: 덩어리는 서로 붙어서 자란다 (일차 군집화)")
    void clustersMerge() {
        // 두 덩어리 사이의 빈칸 하나가 채워지면 두 덩어리가 하나가 된다.
        // 길이가 더해지는 것이 아니라 이후의 탐사 비용이 통째로 합쳐진다.
        LinearProbeMap<Integer, String> map = new LinearProbeMap<>(64, 0.9);
        for (int i = 0; i < 5; i++) map.put(i, "v");          // 0..4
        for (int i = 6; i < 11; i++) map.put(i, "v");         // 6..10

        map.get(64);                                          // 홈 0, 5 에서 멈춘다
        int before = map.lastProbeCount();
        map.put(5, "bridge");                                 // 다리를 놓는다
        map.get(64);
        int after = map.lastProbeCount();

        assertEquals(6, before);
        assertEquals(12, after, "빈칸 하나가 메워지자 탐사가 두 덩어리를 통째로 걷는다");
        assertNotEquals(before, after);
    }
}
