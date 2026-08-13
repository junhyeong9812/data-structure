package com.datastructure.hashmap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** ChainingHashMap 이 계약을 지키는지 + 체이닝 고유 성질. */
class ChainingHashMapTest extends MapContractTest {

    @Override
    protected <K, V> Map<K, V> create() {
        return new ChainingHashMap<>();
    }

    private static int chainLength(ChainingHashMap<Integer, String> map, int bucket) {
        int n = 0;
        for (ChainingHashMap.Node<Integer, String> x = map.buckets[bucket]; x != null; x = x.next) n++;
        return n;
    }

    @Test
    @DisplayName("같은 버킷에 오면 사슬로 매달린다")
    void chainsCollisions() {
        ChainingHashMap<Integer, String> map = new ChainingHashMap<>();
        map.put(1, "a");
        map.put(9, "b");        // 1 과 같은 버킷 (8 로 나눈 나머지가 같다)

        int bucket = map.bucketOf(1, map.capacity());
        assertEquals(2, chainLength(map, bucket), "두 개가 한 자리에 매달려야 한다");
    }

    @Test
    @DisplayName("같은 키를 다시 넣으면 사슬이 길어지지 않는다")
    void replaceDoesNotGrowChain() {
        ChainingHashMap<Integer, String> map = new ChainingHashMap<>();
        map.put(1, "a");
        map.put(1, "b");

        assertEquals(1, chainLength(map, map.bucketOf(1, map.capacity())),
            "값만 바꿔야 하는데 새로 매달면 같은 키가 두 개가 된다");
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("부하율을 넘으면 버킷이 두 배가 된다")
    void resizesWhenLoaded() {
        ChainingHashMap<Integer, String> map = new ChainingHashMap<>();
        int before = map.capacity();

        // DEFAULT_CAPACITY 8, LOAD_FACTOR 0.75 -> 6개까지는 그대로, 7번째에서 늘어난다
        for (int i = 0; i < 6; i++) map.put(i, "v" + i);
        assertEquals(before, map.capacity());

        map.put(6, "v6");
        assertEquals(before * 2, map.capacity(), "부하율을 넘으면 두 배가 된다");
    }

    @Test
    @DisplayName("리사이즈 뒤에는 버킷 번호가 다시 계산되어 있다")
    void rehashesOnResize() {
        // 버킷 수가 바뀌면 같은 키도 다른 자리로 간다. 통째로 옮기면 찾을 수 없게 된다.
        ChainingHashMap<Integer, String> map = new ChainingHashMap<>();
        for (int i = 0; i < 20; i++) map.put(i, "v" + i);

        for (int i = 0; i < 20; i++) {
            int bucket = map.bucketOf(i, map.capacity());
            boolean found = false;
            for (ChainingHashMap.Node<Integer, String> n = map.buckets[bucket]; n != null; n = n.next) {
                if (n.key.equals(i)) found = true;
            }
            assertTrue(found, "키 " + i + " 가 계산된 버킷에 없다. 재해시를 안 했다");
        }
    }

    @Test
    @DisplayName("사슬 가운데를 지워도 앞뒤가 이어진다")
    void removesFromMiddleOfChain() {
        ChainingHashMap<Integer, String> map = new ChainingHashMap<>();
        map.put(1, "a");
        map.put(9, "b");
        map.put(17, "c");

        assertNotNull(map.remove(9));

        assertEquals("a", map.get(1));
        assertEquals("c", map.get(17), "사슬이 끊기면 여기서 드러난다");
        assertEquals(2, chainLength(map, map.bucketOf(1, map.capacity())));
    }

    @Test
    @DisplayName("clear 후 버킷에 참조가 남지 않는다")
    void clearDetachesBuckets() {
        ChainingHashMap<Integer, String> map = new ChainingHashMap<>();
        for (int i = 0; i < 5; i++) map.put(i, "v" + i);
        int capacity = map.capacity();

        map.clear();

        assertEquals(capacity, map.capacity(), "버킷 배열 길이는 유지한다");
        for (int i = 0; i < capacity; i++) {
            assertNull(map.buckets[i], "버킷 " + i + " 에 참조가 남아 있다");
        }
    }
}
