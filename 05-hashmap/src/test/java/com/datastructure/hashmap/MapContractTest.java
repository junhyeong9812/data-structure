package com.datastructure.hashmap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Map 계약 테스트. **세 구현이 전부 물려받는다.**
 *
 * 여기는 공개 API 만 쓰고 **순서를 가정하지 않는다.**
 * 순서는 LinkedHashMap 만의 계약이므로 그쪽 테스트에서 본다.
 */
abstract class MapContractTest {

    protected abstract <K, V> Map<K, V> create();

    protected static <K> Set<K> keySet(Map<K, ?> map) {
        Set<K> set = new HashSet<>();
        map.keys().forEach(set::add);
        return set;
    }

    @Nested
    @DisplayName("넣기와 찾기")
    class PutAndGet {

        @Test
        void storesAndRetrieves() {
            Map<String, Integer> map = create();
            assertNull(map.put("a", 1), "처음 넣는 키는 이전 값이 없다");
            assertNull(map.put("b", 2));

            assertEquals(1, map.get("a"));
            assertEquals(2, map.get("b"));
            assertEquals(2, map.size());
        }

        @Test
        @DisplayName("같은 키에 다시 넣으면 값이 바뀌고 이전 값이 나온다")
        void replacesExistingKey() {
            Map<String, Integer> map = create();
            map.put("a", 1);

            assertEquals(1, map.put("a", 99), "이전 값을 반환해야 한다");
            assertEquals(99, map.get("a"));
            assertEquals(1, map.size(), "크기가 늘면 안 된다");
        }

        @Test
        void missingKeyReturnsNull() {
            Map<String, Integer> map = create();
            map.put("a", 1);
            assertNull(map.get("zzz"));
            assertFalse(map.containsKey("zzz"));
            assertTrue(map.containsKey("a"));
        }

        @Test
        @DisplayName("값으로 null 을 담을 수 있고 containsKey 로 구분된다")
        void allowsNullValue() {
            Map<String, Integer> map = create();
            map.put("a", null);

            assertNull(map.get("a"));
            assertTrue(map.containsKey("a"), "값이 null 인 것과 키가 없는 것은 다르다");
            assertEquals(1, map.size());
        }

        @Test
        @DisplayName("null 키는 거부한다")
        void rejectsNullKey() {
            Map<String, Integer> map = create();
            assertThrows(IllegalArgumentException.class, () -> map.put(null, 1));
            assertNull(map.get(null), "조회는 예외 대신 null 이다");
            assertFalse(map.containsKey(null));
        }
    }

    @Nested
    @DisplayName("지우기")
    class Remove {

        @Test
        void removesAndReturnsValue() {
            Map<String, Integer> map = create();
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
            Map<String, Integer> map = create();
            assertNull(map.remove("zzz"));
            assertEquals(0, map.size());
        }

        @Test
        @DisplayName("지운 키를 다시 넣을 수 있다")
        void reinsertAfterRemove() {
            Map<String, Integer> map = create();
            map.put("a", 1);
            map.remove("a");
            assertNull(map.put("a", 2), "다시 넣으면 새 키다");
            assertEquals(2, map.get("a"));
            assertEquals(1, map.size());
        }
    }

    @Nested
    @DisplayName("충돌과 리사이즈")
    class CollisionAndResize {

        @Test
        @DisplayName("같은 버킷으로 가는 키들이 서로를 덮지 않는다")
        void handlesCollisions() {
            // Integer 의 hashCode 는 값 그대로다. 초기 버킷 8 기준으로 셋 다 같은 자리로 간다.
            Map<Integer, String> map = create();
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
            // Integer 의 hashCode 는 값 그대로다. 즉 음수 해시가 실제로 나온다.
            // 음수를 그대로 % 하면 인덱스가 음수가 되어 여기서 터진다.
            Map<Integer, String> map = create();
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
            Map<Integer, Integer> map = create();
            final int n = 1_000;
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
            Map<Integer, Integer> map = create();
            for (int i = 0; i < 100; i++) map.put(i, i);
            for (int i = 0; i < 100; i += 2) assertEquals(i, map.remove(i));

            assertEquals(50, map.size());
            for (int i = 1; i < 100; i += 2) assertEquals(i, map.get(i), "홀수 키 " + i);
            for (int i = 0; i < 100; i += 2) assertNull(map.get(i), "짝수 키 " + i);
        }
    }

    @Nested
    @DisplayName("키 목록과 초기화")
    class KeysAndClear {

        @Test
        @DisplayName("담긴 키를 빠짐없이 준다")
        void listsAllKeys() {
            Map<String, Integer> map = create();
            map.put("a", 1);
            map.put("b", 2);
            map.put("c", 3);

            assertEquals(Set.of("a", "b", "c"), keySet(map));

            java.util.List<String> all = new ArrayList<>();
            map.keys().forEach(all::add);
            assertEquals(3, all.size(), "같은 키가 두 번 나오면 안 된다");
        }

        @Test
        void clearsAndReusable() {
            Map<String, Integer> map = create();
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
}
