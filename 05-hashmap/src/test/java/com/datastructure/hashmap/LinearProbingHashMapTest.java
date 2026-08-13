package com.datastructure.hashmap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LinearProbingHashMap 이 계약을 지키는지 + **tombstone 검증**.
 *
 * 이 파일의 핵심은 deleteKeepsProbeChain 이다.
 * 지운 자리를 그냥 비우면 그 자리를 지나 뒤쪽에 들어간 키를 영영 못 찾게 된다.
 * 계약 테스트만으로는 잘 안 잡히는 버그라 여기서 구조를 직접 본다.
 */
class LinearProbingHashMapTest extends MapContractTest {

    @Override
    protected <K, V> Map<K, V> create() {
        return new LinearProbingHashMap<>();
    }

    @Test
    @DisplayName("충돌하면 옆칸으로 밀려 들어간다")
    void probesToNextSlot() {
        LinearProbingHashMap<Integer, String> map = new LinearProbingHashMap<>();
        map.put(1, "a");
        map.put(9, "b");        // 같은 버킷이라 옆칸으로

        int home = map.bucketOf(1, map.capacity());
        assertEquals(LinearProbingHashMap.OCCUPIED, map.states[home]);
        assertEquals(LinearProbingHashMap.OCCUPIED, map.states[map.nextProbe(home, map.capacity())]);
        assertEquals("a", map.get(1));
        assertEquals("b", map.get(9));
    }

    @Test
    @DisplayName("지운 자리를 비우면 뒤쪽 키를 못 찾는다 (tombstone 이 필요한 이유)")
    void deleteKeepsProbeChain() {
        LinearProbingHashMap<Integer, String> map = new LinearProbingHashMap<>();
        map.put(1, "a");        // home
        map.put(9, "b");        // home + 1
        map.put(17, "c");       // home + 2

        assertEquals("a", map.remove(1));

        // 여기가 핵심이다. 1 을 지운 자리가 EMPTY 가 되면 9 를 찾을 때 거기서 멈춰버린다.
        assertEquals("b", map.get(9), "지운 자리에서 탐사가 끊겼다. tombstone 을 남겨야 한다");
        assertEquals("c", map.get(17));
        assertTrue(map.containsKey(9));
    }

    @Test
    @DisplayName("지운 자리는 EMPTY 가 아니라 TOMBSTONE 이다")
    void removeLeavesTombstone() {
        LinearProbingHashMap<Integer, String> map = new LinearProbingHashMap<>();
        map.put(1, "a");
        int home = map.bucketOf(1, map.capacity());

        map.remove(1);

        assertEquals(LinearProbingHashMap.TOMBSTONE, map.states[home],
            "EMPTY 로 만들면 탐사 사슬이 끊긴다");
        assertEquals(0, map.size());
        assertNull(map.keys().iterator().hasNext() ? map.keys().iterator().next() : null,
            "tombstone 은 키 목록에 나오면 안 된다");
    }

    @Test
    @DisplayName("tombstone 자리를 재사용한다")
    void reusesTombstoneSlot() {
        LinearProbingHashMap<Integer, String> map = new LinearProbingHashMap<>();
        map.put(1, "a");
        int home = map.bucketOf(1, map.capacity());
        map.remove(1);

        map.put(1, "z");

        assertEquals(LinearProbingHashMap.OCCUPIED, map.states[home]);
        assertEquals("z", map.get(1));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("tombstone 을 재사용해도 뒤쪽의 같은 키를 중복 생성하지 않는다")
    void doesNotDuplicateWhenReusingTombstone() {
        // 1(home), 9(home+1) 을 넣고 1 을 지우면 home 이 tombstone 이다.
        // 이때 9 를 다시 put 하면, tombstone 자리를 그냥 쓰면 9 가 두 군데 생긴다.
        LinearProbingHashMap<Integer, String> map = new LinearProbingHashMap<>();
        map.put(1, "a");
        map.put(9, "b");
        map.remove(1);

        assertEquals("b", map.put(9, "B"), "이전 값을 반환해야 한다");
        assertEquals(1, map.size(), "같은 키가 두 개 생기면 안 된다");
        assertEquals("B", map.get(9));
    }

    @Test
    @DisplayName("넣고 지우기를 반복해도 느려지거나 깨지지 않는다")
    void survivesChurn() {
        // tombstone 만 쌓이면 탐사가 길어진다. 그래서 리사이즈 판단에 used 를 쓴다.
        LinearProbingHashMap<Integer, String> map = new LinearProbingHashMap<>();
        for (int round = 0; round < 2_000; round++) {
            map.put(round, "v" + round);
            map.remove(round);
        }
        assertEquals(0, map.size());

        map.put(42, "x");
        assertEquals("x", map.get(42), "tombstone 이 쌓인 뒤에도 정상이어야 한다");
    }

    @Test
    @DisplayName("clear 후 상태와 값 참조가 남지 않는다")
    void clearResetsSlots() {
        LinearProbingHashMap<Integer, String> map = new LinearProbingHashMap<>();
        for (int i = 0; i < 3; i++) map.put(i, "v" + i);

        map.clear();

        for (int i = 0; i < map.capacity(); i++) {
            assertEquals(LinearProbingHashMap.EMPTY, map.states[i], "칸 " + i);
            assertNull(map.keys[i], "키 참조가 남아 있다: " + i);
            assertNull(map.values[i], "값 참조가 남아 있다: " + i);
        }
    }

    @Test
    @DisplayName("한계: 연속된 키는 한 덩어리로 뭉친다 (1차 군집화)")
    void sequentialKeysCluster() {
        // 이건 버그가 아니라 이 방식의 성질이다. 올바른 구현에서도 그대로 나온다.
        //
        // 정수의 hashCode 는 값 그대로다. 버킷 수가 2의 거듭제곱이면
        // 연속된 키 0..m 은 슬롯 0..m 에 빈틈없이 들어찬다.
        // 그러면 그 구간 안으로 떨어지는 "없는 키" 조회가 덩어리 끝까지 걸어가야 끝난다.
        //
        // 체이닝은 같은 상황에서 사슬이 짧아 아무 문제가 없다. 여기서만 생기는 약점이다.
        // 실무 구현이 해시를 한 번 섞는(bit mixing) 이유가 이것이다.
        LinearProbingHashMap<Integer, String> map = new LinearProbingHashMap<>();
        for (int i = 0; i < 1_000; i++) map.put(i, "v" + i);

        int capacity = map.capacity();
        int home = map.bucketOf(0, capacity);
        int run = 0;
        int i = home;
        while (map.states[i] == LinearProbingHashMap.OCCUPIED && run <= capacity) {
            run++;
            i = map.nextProbe(i, capacity);
        }

        assertTrue(run >= 1_000,
            "연속 키 1000개가 끊김 없이 " + run + "칸에 몰려 있다. "
                + "이 구간으로 떨어지는 조회는 덩어리 끝까지 탐사해야 한다.");
    }
}
