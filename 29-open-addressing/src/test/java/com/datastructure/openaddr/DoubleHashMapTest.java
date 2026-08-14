package com.datastructure.openaddr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 이중 해싱. 보폭을 두 번째 해시가 정한다.
 *
 * 여기서 볼 것은 하나다. 보폭이 홀수여야 모든 칸을 본다.
 * 그 한 비트가 없으면 이차 탐사와 같은 병(못 가는 칸)이 생긴다.
 */
class DoubleHashMapTest extends ProbeMapContractTest {

    @Override
    protected <K, V> ProbeMap<K, V> create() {
        return new DoubleHashMap<>();
    }

    @Override
    protected <K, V> ProbeMap<K, V> create(int capacity, double maxLoad) {
        return new DoubleHashMap<>(capacity, maxLoad);
    }

    @Test
    @DisplayName("보폭은 언제나 홀수이고 0 이 아니다")
    void stepIsAlwaysOdd() {
        DoubleHashMap<Integer, String> map = new DoubleHashMap<>(1024, 0.9);
        for (int key = -5000; key < 5000; key++) {
            int step = map.stepFor(Hashing.hash(key));
            assertEquals(1, step & 1, "키 " + key + " 의 보폭이 짝수다: " + step);
            assertNotEquals(0, step & map.mask, "키 " + key + " 의 보폭이 용량의 배수라 제자리를 돈다");
        }
    }

    @Test
    @DisplayName("홀수 보폭은 모든 칸을 정확히 한 번씩 본다")
    void oddStepVisitsEverySlot() {
        DoubleHashMap<Integer, String> map = new DoubleHashMap<>(256, 0.9);
        for (int key = 0; key < 200; key++) {
            int hash = Hashing.hash(key);
            Set<Integer> slots = new HashSet<>();
            for (int i = 0; i < map.capacity(); i++) slots.add(map.probe(hash, i));
            assertEquals(map.capacity(), slots.size(), "키 " + key + " 의 수열이 일부 칸을 빠뜨린다");
        }
    }

    @Test
    @DisplayName("보폭이 짝수면 그 약수만큼 칸이 사라진다")
    void evenStepWouldMissSlots() {
        // 보폭 s, 용량 m 일 때 i*s mod m 은 m/gcd(s, m) 개의 칸만 돈다.
        // m 이 2의 거듭제곱이면 약수는 2 뿐이라, 보폭을 홀수로 만드는 것만으로 gcd 가 1 이 된다.
        // 마지막 or 1 이 하는 일이 이것이다. 한 비트가 "모든 칸을 본다"를 보장한다.
        int capacity = 256;
        for (int step : new int[] {2, 4, 8, 64}) {
            Set<Integer> slots = new HashSet<>();
            for (int i = 0; i < capacity; i++) slots.add((7 + i * step) & (capacity - 1));
            assertEquals(capacity / step, slots.size(), "보폭 " + step);
        }
        Set<Integer> zeroStep = new HashSet<>();
        for (int i = 0; i < capacity; i++) zeroStep.add((7 + i * 0) & (capacity - 1));
        assertEquals(1, zeroStep.size(), "보폭이 0 이면 제자리만 무한히 본다");
    }

    @Test
    @DisplayName("홈이 같아도 두 번째 칸부터 갈라진다 (이차 군집화가 없다)")
    void sameHomeDivergesImmediately() {
        // 이차 탐사는 홈이 같으면 경로가 통째로 같았다. 여기서는 보폭이 키마다 다르다.
        DoubleHashMap<Integer, String> map = new DoubleHashMap<>(64, 0.9);
        int home = map.probe(Hashing.hash(1), 0);
        assertEquals(home, map.probe(Hashing.hash(65), 0), "홈은 같아야 한다");
        assertNotEquals(map.probe(Hashing.hash(1), 1), map.probe(Hashing.hash(65), 1),
                "두 번째 칸이 같으면 이차 탐사와 다를 것이 없다");

        map.put(1, "a");
        map.put(65, "b");
        map.put(129, "c");
        assertEquals("a", map.get(1));
        map.get(65);
        assertEquals(2, map.lastProbeCount(), "충돌은 한 번 났으니 두 칸이면 찾는다");
    }

    @Test
    @DisplayName("가득 찬 테이블에도 마지막 한 칸까지 넣는다")
    void fillsEverySlot() {
        // 이차 탐사(naive)는 여기서 실패했다. 홀수 보폭은 모든 칸을 돌기 때문에 실패하지 않는다.
        DoubleHashMap<Integer, String> map = new DoubleHashMap<>(64, 1.0);
        for (int i = 0; i < 64; i++) map.put(i * 64, "v");    // 홈이 전부 0

        assertEquals(64, map.size());
        assertEquals(1.0, map.loadFactor(), 1e-9);
        assertEquals(64, map.capacity());
        for (int i = 0; i < 64; i++) assertEquals("v", map.get(i * 64), "키 " + (i * 64));
    }
}
