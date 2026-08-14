package com.datastructure.openaddr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * 쿠쿠 해싱. 다른 넷과 종류가 다르다.
 *
 * 넷은 "찾을 때까지 걷는다"였다. 여기서는 자리가 두 개뿐이라 두 칸만 보면 끝난다.
 * 그 대신 삽입이 남의 자리를 뺏고, 뺏긴 놈이 또 뺏고, 고리가 생기면 전부 다시 넣는다.
 */
class CuckooHashMapTest extends ProbeMapContractTest {

    @Override
    protected <K, V> ProbeMap<K, V> create() {
        return new CuckooHashMap<>();
    }

    @Override
    protected <K, V> ProbeMap<K, V> create(int capacity, double maxLoad) {
        return new CuckooHashMap<>(capacity, maxLoad);
    }

    /** hashCode 가 같고 equals 는 다른 키. 쿠쿠의 고칠 수 없는 한계를 보는 데 쓴다. */
    private record SameHash(int id) {
        @Override
        public int hashCode() {
            return 42;
        }
    }

    @Test
    @DisplayName("모든 키는 자기 두 자리 중 하나에 있다")
    void everyKeyLivesInOneOfItsTwoSlots() {
        CuckooHashMap<Integer, String> map = new CuckooHashMap<>(256, 0.45);
        for (int i = 0; i < 100; i++) map.put(i * 7, "v" + i);

        for (int i = 0; i < 100; i++) {
            Integer key = i * 7;
            int hash = Hashing.hash(key);
            boolean here = key.equals(map.keys[map.slot1(hash)]) || key.equals(map.keys[map.slot2(hash)]);
            assertTrue(here, "키 " + key + " 가 두 자리 중 어디에도 없다");
        }
    }

    @Test
    @DisplayName("조회는 부하율과 상관없이 두 칸 이하다")
    @Timeout(60)
    void lookupIsAlwaysAtMostTwoProbes() {
        // 이것이 쿠쿠가 파는 것 전부다. 선형 탐사는 부하율 0.9 에서 없는 키 하나에 수백 칸을 봤다.
        CuckooHashMap<Integer, String> map = new CuckooHashMap<>(64, 0.49);
        Random random = new Random(11);
        for (int i = 0; i < 3_000; i++) {
            map.put(random.nextInt(100_000), "v");
        }
        for (int i = 0; i < 3_000; i++) {
            map.get(random.nextInt(100_000));
            assertTrue(map.lastProbeCount() <= 2, "조회가 " + map.lastProbeCount() + " 칸을 봤다");
        }
        for (Integer key : map.keys()) {
            map.get(key);
            assertTrue(map.lastProbeCount() <= 2, "있는 키 " + key);
        }
        assertTrue(map.maxProbeCount() <= 2, "담긴 키 전부에 대해 최악이 2 여야 한다");
    }

    @Test
    @DisplayName("자리가 차 있으면 주인을 쫓아낸다")
    void insertKicksTheResident() {
        CuckooHashMap<Integer, String> map = new CuckooHashMap<>(64, 0.9);
        int hash = Hashing.hash(0);
        int a = map.slot1(hash);

        map.put(0, "first");
        assertEquals(0, map.keys[a]);
        assertEquals(0, map.kickCount(), "빈자리에 넣었으면 뺏은 것이 없다");

        map.put(32, "second");            // 32 & 31 == 0 이라 1번 자리가 같다
        assertEquals(32, map.keys[a], "새 항목이 1번 테이블 자리를 차지한다");
        assertEquals(1, map.kickCount());
        assertEquals("first", map.get(0), "쫓겨난 놈은 2번 테이블에 있어야 한다");
        assertEquals(2, map.lastProbeCount(), "2번 테이블에 있으면 두 칸을 본다");
        assertEquals(map.slot2(hash), findSlot(map, 0));
    }

    private static int findSlot(CuckooHashMap<Integer, String> map, int key) {
        for (int i = 0; i < map.capacity(); i++) {
            if (map.keys[i] != null && map.keys[i].equals(key)) return i;
        }
        return -1;
    }

    @Test
    @DisplayName("지운 자리는 그냥 비운다 (tombstone 이 없다)")
    void removeJustClearsTheSlot() {
        CuckooHashMap<Integer, String> map = new CuckooHashMap<>(64, 0.45);
        map.put(0, "a");
        map.put(32, "b");                 // 0 을 2번 테이블로 쫓아낸다
        int slotOfZero = findSlot(map, 0);

        assertEquals("a", map.remove(0));
        assertNull(map.keys[slotOfZero], "표시를 남길 이유가 없다. 사슬이 없기 때문이다");
        assertEquals("b", map.get(32), "지운 자리가 남의 조회를 막지 않는다");
        map.get(0);
        assertEquals(2, map.lastProbeCount(), "없는 키도 두 칸이면 끝난다");
    }

    @Test
    @DisplayName("고리에 빠지면 키우고 전부 다시 넣는다")
    @Timeout(60)
    void cycleTriggersRehash() {
        // 자리가 두 개뿐이라 부하율이 0.5 에 가까워지면 뺏기가 돌고 돈다.
        // 부하율 상한을 풀어놓고 넣으면 재해싱이 실제로 일어난다.
        CuckooHashMap<Integer, String> map = new CuckooHashMap<>(1024, 0.99);
        Random random = new Random(99);
        for (int i = 0; i < 600; i++) map.put(random.nextInt(1 << 24), "v");

        assertTrue(map.cycleRehashCount() > 0,
                "부하율 0.5 를 넘겨 넣었는데 고리가 한 번도 안 났다");
        assertTrue(map.kickCount() > 600, "뺏기가 " + map.kickCount() + " 번밖에 안 일어났다");
        assertTrue(map.capacity() > 1024, "고리가 났으면 테이블이 커져 있어야 한다");
        for (Integer key : map.keys()) {
            assertEquals("v", map.get(key), "재해싱이 키를 잃어버렸다: " + key);
        }
    }

    @Test
    @DisplayName("한계: hashCode 가 같은 키 셋은 담을 수 없다")
    @Timeout(60)
    void threeKeysWithTheSameHashCannotFit() {
        // 자리가 둘뿐이므로 세 번째는 갈 곳이 없다. 용량을 키워도 세 키의 두 자리가 그대로 겹친다.
        // 체이닝은 이런 키를 그냥 사슬에 매단다. 상수 조회를 얻고 이걸 내준 것이다.
        CuckooHashMap<SameHash, String> map = new CuckooHashMap<>(64, 0.45);
        map.put(new SameHash(1), "a");
        map.put(new SameHash(2), "b");
        assertEquals(2, map.size(), "둘까지는 두 자리에 나눠 들어간다");

        IllegalStateException boom =
                assertThrows(IllegalStateException.class, () -> map.put(new SameHash(3), "c"));
        assertTrue(boom.getMessage().contains("재해싱"), boom.getMessage());
        assertEquals("a", map.get(new SameHash(1)), "실패해도 있던 것은 멀쩡해야 한다");
        assertEquals("b", map.get(new SameHash(2)));
    }

    @Test
    @DisplayName("재해싱 중에도 항목을 잃어버리지 않는다")
    @Timeout(60)
    void rehashKeepsEveryEntry() {
        // 고리에 빠진 순간 들고 있던 항목이 테이블 밖에 있다. 그걸 버리면 크기만 맞고 키가 사라진다.
        CuckooHashMap<Integer, Integer> map = new CuckooHashMap<>(64, 0.9);
        for (int i = 0; i < 2_000; i++) map.put(i, i);

        assertEquals(2_000, map.size());
        for (int i = 0; i < 2_000; i++) assertEquals(i, map.get(i), "키 " + i);
        assertTrue(map.rehashCount() > 0);
        int occupied = 0;
        for (Object key : map.keys) if (key != null) occupied++;
        assertEquals(map.size(), occupied);
    }
}
