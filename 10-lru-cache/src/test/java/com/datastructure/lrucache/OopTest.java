package com.datastructure.lrucache;

import com.datastructure.lrucache.oop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("LRU 캐시 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("LRUCacheImpl")
    class LRUCacheImplTest {

        private Cache<Integer, String> cache;

        @BeforeEach
        void setUp() {
            cache = new LRUCacheImpl<>(3);
        }

        @Test
        @DisplayName("01. put/get 기본 동작")
        void test01_putGet() {
            // TODO: put(), get() 구현 후 테스트
            // cache.put(1, "one");
            // assertThat(cache.get(1)).isEqualTo("one");
        }

        @Test
        @DisplayName("02. 존재하지 않는 키는 null 반환")
        void test02_getNonExistent() {
            // TODO: null 반환 테스트
            // assertThat(cache.get(999)).isNull();
        }

        @Test
        @DisplayName("03. LRU 제거")
        void test03_eviction() {
            // TODO: LRU 제거 테스트
            // cache.put(1, "one");
            // cache.put(2, "two");
            // cache.put(3, "three");
            // cache.put(4, "four");  // 1 제거
            // assertThat(cache.get(1)).isNull();
        }

        @Test
        @DisplayName("04. get이 순서 갱신")
        void test04_getUpdatesOrder() {
            // TODO: get 순서 갱신 테스트
            // cache.put(1, "one");
            // cache.put(2, "two");
            // cache.put(3, "three");
            // cache.get(1);           // 1을 최근으로
            // cache.put(4, "four");   // 2 제거
            // assertThat(cache.get(1)).isEqualTo("one");
            // assertThat(cache.get(2)).isNull();
        }

        @Test
        @DisplayName("05. put 업데이트가 순서 갱신")
        void test05_putUpdatesOrder() {
            // TODO: put 순서 갱신 테스트
            // cache.put(1, "one");
            // cache.put(2, "two");
            // cache.put(3, "three");
            // cache.put(1, "ONE");    // 1을 최근으로 + 값 변경
            // cache.put(4, "four");   // 2 제거
            // assertThat(cache.get(1)).isEqualTo("ONE");
            // assertThat(cache.get(2)).isNull();
        }
    }

    @Nested
    @DisplayName("제네릭 타입")
    class GenericTypeTest {

        @Test
        @DisplayName("06. String 키/값")
        void test06_stringKeyValue() {
            // TODO: String 타입 테스트
            // Cache<String, String> cache = new LRUCacheImpl<>(2);
            // cache.put("key1", "value1");
            // cache.put("key2", "value2");
            // assertThat(cache.get("key1")).isEqualTo("value1");
        }

        @Test
        @DisplayName("07. 사용자 정의 객체")
        void test07_customObject() {
            // TODO: 사용자 정의 객체 테스트
            // record User(String name) {}
            // Cache<Integer, User> cache = new LRUCacheImpl<>(2);
            // cache.put(1, new User("Alice"));
            // assertThat(cache.get(1).name()).isEqualTo("Alice");
        }

        @Test
        @DisplayName("08. null 값 저장")
        void test08_nullValue() {
            // TODO: null 값 테스트 (구현에 따라)
            // cache.put(1, null);
            // assertThat(cache.containsKey(1)).isTrue();
            // assertThat(cache.get(1)).isNull();
        }
    }

    @Nested
    @DisplayName("LinkedHashMapLRU")
    class LinkedHashMapLRUTest {

        private Cache<Integer, String> cache;

        @BeforeEach
        void setUp() {
            cache = new LinkedHashMapLRU<>(3);
        }

        @Test
        @DisplayName("09. LinkedHashMap 기반 LRU")
        void test09_linkedHashMapLRU() {
            // TODO: LinkedHashMap 기반 구현 테스트
            // cache.put(1, "one");
            // cache.put(2, "two");
            // cache.put(3, "three");
            // cache.put(4, "four");  // 1 제거
            // assertThat(cache.get(1)).isNull();
        }

        @Test
        @DisplayName("10. accessOrder 동작")
        void test10_accessOrder() {
            // TODO: accessOrder 테스트
            // cache.put(1, "one");
            // cache.put(2, "two");
            // cache.put(3, "three");
            // cache.get(1);           // 1을 최근으로
            // cache.put(4, "four");   // 2 제거
            // assertThat(cache.get(1)).isEqualTo("one");
            // assertThat(cache.get(2)).isNull();
        }
    }

    @Nested
    @DisplayName("ThreadSafeLRUCache")
    class ThreadSafeLRUCacheTest {

        @Test
        @DisplayName("11. 스레드 안전 기본 동작")
        void test11_threadSafeBasic() {
            // TODO: ThreadSafe 버전 테스트
            // Cache<Integer, String> cache = new ThreadSafeLRUCache<>(3);
            // cache.put(1, "one");
            // assertThat(cache.get(1)).isEqualTo("one");
        }

        @Test
        @DisplayName("12. 멀티스레드 환경")
        void test12_multiThread() throws InterruptedException {
            // TODO: 멀티스레드 테스트
            // Cache<Integer, Integer> cache = new ThreadSafeLRUCache<>(100);
            // ExecutorService executor = Executors.newFixedThreadPool(4);
            // for (int i = 0; i < 1000; i++) {
            //     final int key = i;
            //     executor.submit(() -> cache.put(key, key));
            // }
            // executor.shutdown();
            // executor.awaitTermination(1, TimeUnit.SECONDS);
            // assertThat(cache.size()).isLessThanOrEqualTo(100);
        }
    }

    @Nested
    @DisplayName("Cache 인터페이스 공통")
    class CacheInterfaceCommonTest {

        @Test
        @DisplayName("13. 다형성")
        void test13_polymorphism() {
            // TODO: 다형성 테스트
            // Cache<Integer, String>[] caches = new Cache[] {
            //     new LRUCacheImpl<>(3),
            //     new LinkedHashMapLRU<>(3)
            // };
            // for (Cache<Integer, String> cache : caches) {
            //     cache.put(1, "test");
            //     assertThat(cache.get(1)).isEqualTo("test");
            // }
        }

        @Test
        @DisplayName("14. remove")
        void test14_remove() {
            // TODO: remove() 테스트
            // Cache<Integer, String> cache = new LRUCacheImpl<>(3);
            // cache.put(1, "one");
            // cache.remove(1);
            // assertThat(cache.get(1)).isNull();
        }

        @Test
        @DisplayName("15. clear")
        void test15_clear() {
            // TODO: clear() 테스트
            // Cache<Integer, String> cache = new LRUCacheImpl<>(3);
            // cache.put(1, "one");
            // cache.clear();
            // assertThat(cache.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("16. containsKey")
        void test16_containsKey() {
            // TODO: containsKey() 테스트
            // Cache<Integer, String> cache = new LRUCacheImpl<>(3);
            // cache.put(1, "one");
            // assertThat(cache.containsKey(1)).isTrue();
            // assertThat(cache.containsKey(2)).isFalse();
        }
    }

    @Nested
    @DisplayName("확장 기능")
    class ExtendedFeaturesTest {

        @Test
        @DisplayName("17. peek")
        void test17_peek() {
            // TODO: peek() 테스트
            // LRUCacheImpl<Integer, String> cache = new LRUCacheImpl<>(3);
            // cache.put(1, "one");
            // cache.put(2, "two");
            // cache.put(3, "three");
            // cache.peek(1);          // 순서 변경 없음
            // cache.put(4, "four");   // 1 제거
            // assertThat(cache.get(1)).isNull();
        }

        @Test
        @DisplayName("18. getOldest/getNewest")
        void test18_oldestNewest() {
            // TODO: getOldest(), getNewest() 테스트
            // LRUCacheImpl<Integer, String> cache = new LRUCacheImpl<>(3);
            // cache.put(1, "one");
            // cache.put(2, "two");
            // cache.put(3, "three");
            // assertThat(cache.getOldest()).isEqualTo(Map.entry(1, "one"));
            // assertThat(cache.getNewest()).isEqualTo(Map.entry(3, "three"));
        }

        @Test
        @DisplayName("19. keys/values")
        void test19_keysValues() {
            // TODO: keys(), values() 테스트
            // LRUCacheImpl<Integer, String> cache = new LRUCacheImpl<>(3);
            // cache.put(1, "one");
            // cache.put(2, "two");
            // cache.get(1);  // 1을 최근으로
            // assertThat(cache.keys()).containsExactly(1, 2);
        }

        @Test
        @DisplayName("20. Iterator")
        void test20_iterator() {
            // TODO: Iterator 테스트
            // LRUCacheImpl<Integer, String> cache = new LRUCacheImpl<>(3);
            // cache.put(1, "one");
            // cache.put(2, "two");
            // int count = 0;
            // for (var entry : cache) {
            //     count++;
            // }
            // assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("21. 용량 1")
        void test21_capacityOne() {
            // TODO: 용량 1 테스트
            // Cache<Integer, String> cache = new LRUCacheImpl<>(1);
            // cache.put(1, "one");
            // cache.put(2, "two");
            // assertThat(cache.get(1)).isNull();
            // assertThat(cache.get(2)).isEqualTo("two");
        }

        @Test
        @DisplayName("22. 용량 초과 연속")
        void test22_continuousEviction() {
            // TODO: 연속 제거 테스트
            // Cache<Integer, String> cache = new LRUCacheImpl<>(2);
            // for (int i = 0; i < 100; i++) {
            //     cache.put(i, "val" + i);
            // }
            // assertThat(cache.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("23. equals")
        void test23_equals() {
            // TODO: equals() 테스트
            // Cache<Integer, String> c1 = new LRUCacheImpl<>(3);
            // Cache<Integer, String> c2 = new LRUCacheImpl<>(3);
            // c1.put(1, "one");
            // c2.put(1, "one");
            // assertThat(c1).isEqualTo(c2);
        }

        @Test
        @DisplayName("24. hashCode")
        void test24_hashCode() {
            // TODO: hashCode() 테스트
            // Cache<Integer, String> c1 = new LRUCacheImpl<>(3);
            // Cache<Integer, String> c2 = new LRUCacheImpl<>(3);
            // c1.put(1, "one");
            // c2.put(1, "one");
            // assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        }

        @Test
        @DisplayName("25. toString")
        void test25_toString() {
            // TODO: toString() 테스트
            // Cache<Integer, String> cache = new LRUCacheImpl<>(3);
            // cache.put(1, "one");
            // assertThat(cache.toString()).contains("1", "one");
        }

        @Test
        @DisplayName("26. 불법 용량")
        void test26_illegalCapacity() {
            // TODO: 불법 용량 테스트
            // assertThatThrownBy(() -> new LRUCacheImpl<>(0))
            //     .isInstanceOf(IllegalArgumentException.class);
            // assertThatThrownBy(() -> new LRUCacheImpl<>(-1))
            //     .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("27. null 키 거부")
        void test27_rejectNullKey() {
            // TODO: null 키 테스트
            // Cache<Integer, String> cache = new LRUCacheImpl<>(3);
            // assertThatThrownBy(() -> cache.put(null, "value"))
            //     .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("28. stream 지원")
        void test28_stream() {
            // TODO: stream() 테스트
            // LRUCacheImpl<Integer, String> cache = new LRUCacheImpl<>(3);
            // cache.put(1, "one");
            // cache.put(2, "two");
            // long count = cache.stream().count();
            // assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("29. computeIfAbsent")
        void test29_computeIfAbsent() {
            // TODO: computeIfAbsent() 테스트 (선택)
            // Cache<Integer, String> cache = new LRUCacheImpl<>(3);
            // String value = cache.computeIfAbsent(1, k -> "computed");
            // assertThat(value).isEqualTo("computed");
        }

        @Test
        @DisplayName("30. putIfAbsent")
        void test30_putIfAbsent() {
            // TODO: putIfAbsent() 테스트 (선택)
            // Cache<Integer, String> cache = new LRUCacheImpl<>(3);
            // cache.put(1, "one");
            // cache.putIfAbsent(1, "ONE");  // 변경 안 됨
            // assertThat(cache.get(1)).isEqualTo("one");
        }
    }
}
