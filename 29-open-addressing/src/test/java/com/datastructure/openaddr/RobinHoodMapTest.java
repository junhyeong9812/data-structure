package com.datastructure.openaddr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 로빈후드 해싱. 수열은 선형 탐사와 같고 자리 다툼의 규칙만 다르다.
 *
 * 여기서 볼 것은 셋이다.
 *   뺏기가 실제로 일어나는가 (배치가 선형 탐사와 달라지는가)
 *   불변식이 유지되는가 (탐사 거리가 앞에서 뒤로 1 이상 뛰지 않는다)
 *   tombstone 없이 지울 수 있는가 (backward shift)
 */
class RobinHoodMapTest extends ProbeMapContractTest {

    @Override
    protected <K, V> ProbeMap<K, V> create() {
        return new RobinHoodMap<>();
    }

    @Override
    protected <K, V> ProbeMap<K, V> create(int capacity, double maxLoad) {
        return new RobinHoodMap<>(capacity, maxLoad);
    }

    /**
     * 불변식 검사. 어떤 칸이 차 있고 그 앞칸도 차 있으면
     * 이 칸의 탐사 거리는 앞칸의 거리 + 1 이하다.
     *
     * 뺏기를 빼먹으면 이게 깨진다. 늦게 온 놈이 더 멀리 밀려나 있게 되기 때문이다.
     */
    private static void assertInvariant(RobinHoodMap<?, ?> map) {
        int capacity = map.capacity();
        for (int i = 0; i < capacity; i++) {
            if (map.keys[i] == null) continue;
            int previous = (i - 1) & (capacity - 1);
            if (map.keys[previous] == null) continue;
            assertTrue(map.distanceOf(i) <= map.distanceOf(previous) + 1,
                    "칸 " + i + " 의 거리 " + map.distanceOf(i)
                            + " 가 앞칸 " + previous + " 의 거리 " + map.distanceOf(previous) + " 보다 2 이상 크다");
        }
    }

    @Test
    @DisplayName("더 멀리 걸어온 키가 자리를 뺏는다")
    void richerKeyGivesUpItsSlot() {
        // 0, 16, 32 는 홈이 전부 0 이라 0, 1, 2번 칸에 거리 0, 1, 2 로 앉는다.
        // 그다음 3(홈 3, 거리 0)을 넣고 2(홈 2)를 넣는다.
        // 2 는 3번 칸에서 거리 1 인데 그 칸의 주인 3 은 거리 0 이다. 그래서 뺏는다.
        RobinHoodMap<Integer, String> map = new RobinHoodMap<>(16, 0.9);
        for (int key : new int[] {0, 16, 32, 3, 2}) map.put(key, "v" + key);

        assertEquals(2, map.keys[3], "덜 걸은 3 이 자리를 내주어야 한다");
        assertEquals(3, map.keys[4], "뺏긴 3 은 들려서 다음 칸으로 간다");
        assertEquals(1, map.distanceOf(3));
        assertEquals(1, map.distanceOf(4));
        assertInvariant(map);

        // 선형 탐사는 같은 입력에 3 을 3번 칸에 두고 2 를 4번 칸으로 밀어낸다.
        LinearProbeMap<Integer, String> linear = new LinearProbeMap<>(16, 0.9);
        for (int key : new int[] {0, 16, 32, 3, 2}) linear.put(key, "v" + key);
        assertEquals(3, linear.keys[3], "선형 탐사에서는 먼저 온 놈이 자리를 지킨다");
        assertEquals(2, linear.keys[4]);
    }

    @Test
    @DisplayName("없는 키 조회는 빈칸까지 안 가고 멈춘다")
    void missStopsEarly() {
        // 걸어온 거리가 지금 칸 주인의 거리보다 커지는 순간, 그 뒤에 이 키가 있을 수 없다.
        // 있었다면 그 칸을 뺏었어야 하기 때문이다. 선형 탐사에는 이 근거가 없어 빈칸까지 간다.
        RobinHoodMap<Integer, String> robin = new RobinHoodMap<>(16, 0.9);
        LinearProbeMap<Integer, String> linear = new LinearProbeMap<>(16, 0.9);
        for (int key : new int[] {0, 16, 32, 3, 2}) {
            robin.put(key, "v");
            linear.put(key, "v");
        }

        assertNull(robin.get(48));       // 홈 0 인 없는 키
        int robinProbes = robin.lastProbeCount();
        assertNull(linear.get(48));
        int linearProbes = linear.lastProbeCount();

        assertEquals(4, robinProbes);
        assertEquals(6, linearProbes, "선형 탐사는 덩어리 끝의 빈칸을 볼 때까지 간다");
    }

    @Test
    @DisplayName("지운 자리를 뒤에서 당겨온다 (tombstone 이 없다)")
    void removeShiftsBackward() {
        RobinHoodMap<Integer, String> map = new RobinHoodMap<>(16, 0.9);
        for (int key : new int[] {0, 16, 32, 48}) map.put(key, "v" + key);
        assertEquals(3, map.distanceOf(3));

        assertEquals("v0", map.remove(0));

        assertEquals(16, map.keys[0], "뒤엣것을 당겨와야 한다");
        assertEquals(32, map.keys[1]);
        assertEquals(48, map.keys[2]);
        assertNull(map.keys[3], "당겨온 뒤 마지막 자리는 진짜로 비운다. tombstone 이 아니다");
        assertEquals(3, map.size());
        for (int key : new int[] {16, 32, 48}) assertNotNull(map.get(key), "키 " + key);

        map.get(64);
        assertEquals(4, map.lastProbeCount(), "빈칸이 진짜 비어 있으니 거기서 멈춘다");
        assertInvariant(map);
    }

    @Test
    @DisplayName("홈에 앉은 키는 당겨오지 않는다")
    void shiftStopsAtAHomeSlot() {
        // 거리 0 인 키를 당기면 자기 홈보다 앞으로 가버려서 영영 못 찾는다.
        RobinHoodMap<Integer, String> map = new RobinHoodMap<>(16, 0.9);
        map.put(0, "a");
        map.put(16, "b");     // 홈 0, 거리 1 -> 1번 칸
        map.put(2, "c");      // 홈 2, 거리 0 -> 2번 칸

        map.remove(0);

        assertEquals(16, map.keys[0]);
        assertNull(map.keys[1]);
        assertEquals(2, map.keys[2], "홈에 앉은 2 는 그대로 있어야 한다");
        assertEquals("c", map.get(2));
        assertEquals(0, map.distanceOf(2));
    }

    @Test
    @DisplayName("넣고 지우기를 섞어도 불변식이 유지된다")
    void invariantSurvivesRandomChurn() {
        RobinHoodMap<Integer, String> map = new RobinHoodMap<>(256, 0.85);
        Random random = new Random(4242);
        java.util.Map<Integer, String> mirror = new java.util.HashMap<>();

        for (int step = 0; step < 20_000; step++) {
            int key = random.nextInt(400);
            if (random.nextInt(100) < 55) {
                map.put(key, "v" + step);
                mirror.put(key, "v" + step);
            } else {
                assertEquals(mirror.remove(key), map.remove(key), "스텝 " + step);
            }
            assertEquals(mirror.size(), map.size(), "스텝 " + step);
            if (step % 200 == 0) assertInvariant(map);
        }
        assertInvariant(map);
        for (java.util.Map.Entry<Integer, String> e : mirror.entrySet()) {
            assertEquals(e.getValue(), map.get(e.getKey()), "키 " + e.getKey());
        }

        int occupied = 0;
        for (Object key : map.keys) if (key != null) occupied++;
        assertEquals(map.size(), occupied, "지운 자리가 남아 있으면 안 된다. tombstone 이 없다는 뜻이다");
    }
}
