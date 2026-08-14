package com.datastructure.openaddr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ProbeMap 계약 테스트. 다섯 구현이 전부 물려받는다.
 *
 * 공개 API 만 쓰고 순서를 가정하지 않는다. 탐사 수열이 다르면 키 순서도 다르기 때문이다.
 * 여기서 통과한다고 좋은 구현인 것은 아니다. 좋고 나쁨은 ProbeCountTest 가 숫자로 가른다.
 */
abstract class ProbeMapContractTest {

    protected abstract <K, V> ProbeMap<K, V> create();

    /** 용량과 최대 부하율을 지정해서 만든다. 리사이즈를 막고 재는 데 쓴다. */
    protected abstract <K, V> ProbeMap<K, V> create(int capacity, double maxLoad);

    protected static <K> Set<K> keySet(ProbeMap<K, ?> map) {
        Set<K> set = new HashSet<>();
        map.keys().forEach(set::add);
        return set;
    }

    @Nested
    @DisplayName("넣기와 찾기")
    class PutAndGet {

        @Test
        void storesAndRetrieves() {
            ProbeMap<String, Integer> map = create();
            assertNull(map.put("a", 1), "처음 넣는 키는 이전 값이 없다");
            assertNull(map.put("b", 2));

            assertEquals(1, map.get("a"));
            assertEquals(2, map.get("b"));
            assertEquals(2, map.size());
        }

        @Test
        @DisplayName("같은 키에 다시 넣으면 값이 바뀌고 이전 값이 나온다")
        void replacesExistingKey() {
            ProbeMap<String, Integer> map = create();
            map.put("a", 1);

            assertEquals(1, map.put("a", 99), "이전 값을 반환해야 한다");
            assertEquals(99, map.get("a"));
            assertEquals(1, map.size(), "크기가 늘면 안 된다");
        }

        @Test
        void missingKeyReturnsNull() {
            ProbeMap<String, Integer> map = create();
            map.put("a", 1);
            assertNull(map.get("zzz"));
            assertFalse(map.containsKey("zzz"));
            assertTrue(map.containsKey("a"));
        }

        @Test
        @DisplayName("값으로 null 을 담을 수 있고 containsKey 로 구분된다")
        void allowsNullValue() {
            ProbeMap<String, Integer> map = create();
            map.put("a", null);

            assertNull(map.get("a"));
            assertTrue(map.containsKey("a"), "값이 null 인 것과 키가 없는 것은 다르다");
            assertEquals(1, map.size());
        }

        @Test
        @DisplayName("null 키는 거부한다")
        void rejectsNullKey() {
            ProbeMap<String, Integer> map = create();
            assertThrows(IllegalArgumentException.class, () -> map.put(null, 1));
            assertNull(map.get(null), "조회는 예외 대신 null 이다");
            assertFalse(map.containsKey(null));
            assertNull(map.remove(null));
        }
    }

    @Nested
    @DisplayName("지우기")
    class Remove {

        @Test
        void removesAndReturnsValue() {
            ProbeMap<String, Integer> map = create();
            map.put("a", 1);
            map.put("b", 2);

            assertEquals(1, map.remove("a"));
            assertNull(map.get("a"));
            assertFalse(map.containsKey("a"));
            assertEquals(1, map.size());
            assertEquals(2, map.get("b"), "다른 키는 그대로여야 한다");
        }

        @Test
        void removingMissingKeyIsNull() {
            ProbeMap<String, Integer> map = create();
            assertNull(map.remove("zzz"));
            assertEquals(0, map.size());
        }

        @Test
        @DisplayName("지운 키를 다시 넣을 수 있다")
        void reinsertAfterRemove() {
            ProbeMap<String, Integer> map = create();
            map.put("a", 1);
            map.remove("a");
            assertNull(map.put("a", 2), "다시 넣으면 새 키다");
            assertEquals(2, map.get("a"));
            assertEquals(1, map.size());
        }

        @Test
        @DisplayName("탐사 사슬 한가운데를 지워도 뒤쪽 키를 찾는다")
        void deleteKeepsProbeChain() {
            // 05번의 그 함정이다. 같은 홈으로 가는 셋을 넣고 첫째를 지운다.
            // 선형/이차/이중은 tombstone 으로, 로빈후드는 당겨오기로, 쿠쿠는 사슬 자체가 없어서 푼다.
            ProbeMap<Integer, String> map = create(64, 0.9);
            map.put(1, "a");
            map.put(65, "b");
            map.put(129, "c");

            assertEquals("a", map.remove(1));
            assertEquals("b", map.get(65), "지운 자리에서 탐사가 끊겼다");
            assertEquals("c", map.get(129));
            assertEquals(2, map.size());

            assertEquals("b", map.remove(65));
            assertEquals("c", map.get(129), "두 개를 지워도 마지막은 찾아야 한다");
            assertEquals(1, map.size());
        }

        @Test
        @DisplayName("넣고 지우기를 많이 반복해도 깨지지 않는다")
        void survivesChurn() {
            ProbeMap<Integer, String> map = create();
            for (int round = 0; round < 3_000; round++) {
                map.put(round, "v" + round);
                assertEquals("v" + round, map.remove(round), "라운드 " + round);
            }
            assertEquals(0, map.size());
            assertTrue(map.isEmpty());

            map.put(42, "x");
            assertEquals("x", map.get(42), "지운 자리가 쌓인 뒤에도 정상이어야 한다");
        }
    }

    @Nested
    @DisplayName("충돌과 리사이즈")
    class CollisionAndResize {

        @Test
        @DisplayName("같은 홈으로 가는 키들이 서로를 덮지 않는다")
        void handlesCollisions() {
            ProbeMap<Integer, String> map = create();
            map.put(1, "a");
            map.put(9, "b");
            map.put(17, "c");

            assertEquals("a", map.get(1));
            assertEquals("b", map.get(9));
            assertEquals("c", map.get(17));
            assertEquals(3, map.size());
        }

        @Test
        @DisplayName("해시가 음수인 키도 담긴다")
        void handlesNegativeHash() {
            ProbeMap<Integer, String> map = create();
            map.put(Integer.MIN_VALUE, "min");
            map.put(-1, "neg");
            map.put(0, "zero");

            assertEquals("min", map.get(Integer.MIN_VALUE));
            assertEquals("neg", map.get(-1));
            assertEquals("zero", map.get(0));
            assertEquals(3, map.size());
            assertEquals("min", map.remove(Integer.MIN_VALUE));
        }

        @Test
        @DisplayName("많이 넣어도 전부 찾을 수 있다")
        void survivesManyInserts() {
            ProbeMap<Integer, Integer> map = create();
            final int n = 2_000;
            for (int i = 0; i < n; i++) map.put(i, i * 10);

            assertEquals(n, map.size());
            for (int i = 0; i < n; i++) {
                assertEquals(i * 10, map.get(i), "키 " + i);
            }
            assertEquals(n, keySet(map).size(), "키가 중복되거나 사라지면 안 된다");
        }

        @Test
        @DisplayName("리사이즈 뒤에도 지우기가 정상이다")
        void removeAfterResize() {
            ProbeMap<Integer, Integer> map = create();
            for (int i = 0; i < 200; i++) map.put(i, i);
            for (int i = 0; i < 200; i += 2) assertEquals(i, map.remove(i));

            assertEquals(100, map.size());
            for (int i = 1; i < 200; i += 2) assertEquals(i, map.get(i), "홀수 키 " + i);
            for (int i = 0; i < 200; i += 2) assertNull(map.get(i), "짝수 키 " + i);
        }

        @Test
        @DisplayName("문자열 키도 담긴다")
        void handlesStringKeys() {
            ProbeMap<String, Integer> map = create();
            for (int i = 0; i < 500; i++) map.put("key" + i, i);
            assertEquals(500, map.size());
            for (int i = 0; i < 500; i++) assertEquals(i, map.get("key" + i), "key" + i);
            assertNull(map.get("key500"));
        }
    }

    @Nested
    @DisplayName("키 목록과 초기화")
    class KeysAndClear {

        @Test
        @DisplayName("담긴 키를 빠짐없이 준다")
        void listsAllKeys() {
            ProbeMap<String, Integer> map = create();
            map.put("a", 1);
            map.put("b", 2);
            map.put("c", 3);

            assertEquals(Set.of("a", "b", "c"), keySet(map));

            List<String> all = new ArrayList<>();
            map.keys().forEach(all::add);
            assertEquals(3, all.size(), "같은 키가 두 번 나오면 안 된다");
        }

        @Test
        @DisplayName("지운 키는 목록에 없다")
        void removedKeysAreGone() {
            ProbeMap<Integer, Integer> map = create();
            for (int i = 0; i < 50; i++) map.put(i, i);
            for (int i = 0; i < 50; i += 2) map.remove(i);

            Set<Integer> keys = keySet(map);
            assertEquals(25, keys.size());
            for (int i = 1; i < 50; i += 2) assertTrue(keys.contains(i), "홀수 키 " + i);
        }

        @Test
        void clearsAndReusable() {
            ProbeMap<String, Integer> map = create();
            map.put("a", 1);
            map.put("b", 2);

            map.clear();
            assertEquals(0, map.size());
            assertTrue(map.isEmpty());
            assertNull(map.get("a"));
            assertTrue(keySet(map).isEmpty());

            map.put("c", 3);
            assertEquals(3, map.get("c"));
            assertEquals(1, map.size());
        }
    }

    @Nested
    @DisplayName("측정 계약")
    class Measurement {

        @Test
        @DisplayName("용량은 2의 거듭제곱이고 부하율은 size/capacity 다")
        void capacityAndLoadFactor() {
            ProbeMap<Integer, Integer> map = create(64, 0.9);
            assertEquals(64, map.capacity());
            assertEquals(0.0, map.loadFactor());

            for (int i = 0; i < 32; i++) map.put(i, i);
            assertEquals(64, map.capacity(), "이만큼으로는 리사이즈가 일어나면 안 된다");
            assertEquals(0.5, map.loadFactor(), 1e-9);
            assertEquals(0, map.capacity() & (map.capacity() - 1), "용량이 2의 거듭제곱이 아니다");
        }

        @Test
        @DisplayName("홈 버킷에 바로 있으면 탐사 한 번이다")
        void probeCountStartsAtOne() {
            ProbeMap<Integer, Integer> map = create(64, 0.9);
            map.put(7, 7);

            assertEquals(7, map.get(7));
            assertEquals(1, map.lastProbeCount(), "충돌이 없으면 홈 한 칸만 본다");
            assertEquals(1, map.maxProbeCount());
        }

        @Test
        @DisplayName("탐사 횟수는 조회마다 다시 센다")
        void probeCountResetsPerOperation() {
            ProbeMap<Integer, Integer> map = create(64, 0.9);
            for (int i = 0; i < 8; i++) map.put(i * 64, i);   // 전부 홈이 0 이다

            map.get(0);
            int first = map.lastProbeCount();
            map.get(0);
            assertEquals(first, map.lastProbeCount(), "누적되면 안 된다");
            assertTrue(first >= 1);
        }

        @Test
        @DisplayName("maxProbeCount 는 담긴 키 중 가장 먼 놈의 탐사 수다")
        void maxProbeCountCoversEveryKey() {
            ProbeMap<Integer, Integer> map = create(64, 0.9);
            for (int i = 0; i < 8; i++) map.put(i * 64, i);

            int worst = 0;
            for (Integer key : map.keys()) {
                map.get(key);
                worst = Math.max(worst, map.lastProbeCount());
            }
            assertEquals(worst, map.maxProbeCount());
            assertTrue(worst >= 2, "여덟이 같은 홈으로 갔으니 누군가는 두 칸 이상 걸었다");
        }
    }
}
