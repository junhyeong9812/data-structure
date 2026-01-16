package com.datastructure.hashmap;

import com.datastructure.hashmap.oop.Map;
import com.datastructure.hashmap.oop.HashMap;
import com.datastructure.hashmap.oop.LinkedHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("해시맵 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("HashMap 기본")
    class HashMapBasicTest {
        
        private Map<String, Integer> map;

        @BeforeEach
        void setUp() {
            map = new HashMap<>();
        }

        @Test
        @DisplayName("01. 빈 맵 생성 시 size는 0이다")
        void test01_emptyMapSizeIsZero() {
            // TODO: size() 구현 후 테스트
            // assertThat(map.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("02. put으로 키-값을 저장할 수 있다")
        void test02_putAndGet() {
            // TODO: put(), get() 구현 후 테스트
            // map.put("key", 100);
            // assertThat(map.get("key")).isEqualTo(100);
        }

        @Test
        @DisplayName("03. 같은 키로 put하면 값이 덮어쓰기된다")
        void test03_putOverwrites() {
            // TODO: 값 덮어쓰기 테스트
            // map.put("key", 1);
            // map.put("key", 2);
            // assertThat(map.get("key")).isEqualTo(2);
        }

        @Test
        @DisplayName("04. remove로 키-값을 삭제할 수 있다")
        void test04_remove() {
            // TODO: remove() 구현 후 테스트
            // map.put("key", 100);
            // map.remove("key");
            // assertThat(map.containsKey("key")).isFalse();
        }

        @Test
        @DisplayName("05. containsKey로 키 존재 여부 확인")
        void test05_containsKey() {
            // TODO: containsKey() 구현 후 테스트
            // map.put("key", 100);
            // assertThat(map.containsKey("key")).isTrue();
            // assertThat(map.containsKey("other")).isFalse();
        }
    }

    @Nested
    @DisplayName("제네릭 타입")
    class GenericTypeTest {

        @Test
        @DisplayName("06. Integer 키와 String 값")
        void test06_integerKeyStringValue() {
            // TODO: 제네릭 테스트
            // Map<Integer, String> map = new HashMap<>();
            // map.put(1, "one");
            // map.put(2, "two");
            // assertThat(map.get(1)).isEqualTo("one");
        }

        @Test
        @DisplayName("07. 사용자 정의 객체를 키로 사용")
        void test07_customObjectKey() {
            // TODO: 사용자 정의 키 테스트
            // record Point(int x, int y) {}
            // Map<Point, String> map = new HashMap<>();
            // map.put(new Point(1, 2), "A");
            // assertThat(map.get(new Point(1, 2))).isEqualTo("A");
        }

        @Test
        @DisplayName("08. null 키 지원")
        void test08_nullKey() {
            // TODO: null 키 테스트
            // map.put(null, 100);
            // assertThat(map.get(null)).isEqualTo(100);
        }

        @Test
        @DisplayName("09. null 값 지원")
        void test09_nullValue() {
            // TODO: null 값 테스트
            // map.put("key", null);
            // assertThat(map.containsKey("key")).isTrue();
            // assertThat(map.get("key")).isNull();
        }
    }

    @Nested
    @DisplayName("컬렉션 뷰")
    class CollectionViewTest {

        @Test
        @DisplayName("10. keySet으로 모든 키 조회")
        void test10_keySet() {
            // TODO: keySet() 구현 후 테스트
            // map.put("a", 1);
            // map.put("b", 2);
            // assertThat(map.keySet()).containsExactlyInAnyOrder("a", "b");
        }

        @Test
        @DisplayName("11. values로 모든 값 조회")
        void test11_values() {
            // TODO: values() 구현 후 테스트
            // map.put("a", 1);
            // map.put("b", 2);
            // assertThat(map.values()).containsExactlyInAnyOrder(1, 2);
        }

        @Test
        @DisplayName("12. entrySet으로 모든 엔트리 조회")
        void test12_entrySet() {
            // TODO: entrySet() 구현 후 테스트
            // map.put("a", 1);
            // for (Map.Entry<String, Integer> entry : map.entrySet()) {
            //     assertThat(entry.getKey()).isEqualTo("a");
            //     assertThat(entry.getValue()).isEqualTo(1);
            // }
        }
    }

    @Nested
    @DisplayName("Java 8+ 메서드")
    class Java8MethodsTest {

        @Test
        @DisplayName("13. getOrDefault 동작")
        void test13_getOrDefault() {
            // TODO: getOrDefault() 구현 후 테스트
            // map.put("a", 1);
            // assertThat(map.getOrDefault("a", -1)).isEqualTo(1);
            // assertThat(map.getOrDefault("b", -1)).isEqualTo(-1);
        }

        @Test
        @DisplayName("14. putIfAbsent 동작")
        void test14_putIfAbsent() {
            // TODO: putIfAbsent() 구현 후 테스트
            // map.put("a", 1);
            // map.putIfAbsent("a", 100);
            // map.putIfAbsent("b", 2);
            // assertThat(map.get("a")).isEqualTo(1);
            // assertThat(map.get("b")).isEqualTo(2);
        }

        @Test
        @DisplayName("15. computeIfAbsent 동작")
        void test15_computeIfAbsent() {
            // TODO: computeIfAbsent() 구현 후 테스트
            // map.computeIfAbsent("a", k -> k.length());
            // assertThat(map.get("a")).isEqualTo(1);
        }

        @Test
        @DisplayName("16. merge로 값 병합")
        void test16_merge() {
            // TODO: merge() 구현 후 테스트
            // map.put("a", 1);
            // map.merge("a", 10, Integer::sum);
            // assertThat(map.get("a")).isEqualTo(11);
        }

        @Test
        @DisplayName("17. forEach로 순회")
        void test17_forEach() {
            // TODO: forEach() 구현 후 테스트
            // map.put("a", 1);
            // map.put("b", 2);
            // List<String> keys = new ArrayList<>();
            // map.forEach((k, v) -> keys.add(k));
            // assertThat(keys).containsExactlyInAnyOrder("a", "b");
        }
    }

    @Nested
    @DisplayName("LinkedHashMap")
    class LinkedHashMapTest {

        @Test
        @DisplayName("18. 삽입 순서 유지")
        void test18_insertionOrder() {
            // TODO: LinkedHashMap 구현 후 테스트
            // Map<String, Integer> map = new LinkedHashMap<>();
            // map.put("c", 3);
            // map.put("a", 1);
            // map.put("b", 2);
            // assertThat(map.keySet()).containsExactly("c", "a", "b");
        }

        @Test
        @DisplayName("19. 접근 순서 모드 (LRU용)")
        void test19_accessOrder() {
            // TODO: 접근 순서 모드 구현 후 테스트
            // LinkedHashMap<String, Integer> map = new LinkedHashMap<>(16, 0.75f, true);
            // map.put("a", 1);
            // map.put("b", 2);
            // map.put("c", 3);
            // map.get("a");  // a를 맨 뒤로 이동
            // assertThat(map.keySet()).containsExactly("b", "c", "a");
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("20. clear로 모든 요소 삭제")
        void test20_clear() {
            // TODO: clear() 구현 후 테스트
            // map.put("a", 1);
            // map.put("b", 2);
            // map.clear();
            // assertThat(map.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("21. 리해싱 후 값 유지")
        void test21_rehashPreservesValues() {
            // TODO: 리해싱 테스트
            // for (int i = 0; i < 100; i++) {
            //     map.put("key" + i, i);
            // }
            // for (int i = 0; i < 100; i++) {
            //     assertThat(map.get("key" + i)).isEqualTo(i);
            // }
        }

        @Test
        @DisplayName("22. containsValue 동작")
        void test22_containsValue() {
            // TODO: containsValue() 구현 후 테스트
            // map.put("a", 100);
            // assertThat(map.containsValue(100)).isTrue();
            // assertThat(map.containsValue(999)).isFalse();
        }

        @Test
        @DisplayName("23. equals로 맵 비교")
        void test23_equals() {
            // TODO: equals() 구현 후 테스트
            // Map<String, Integer> map1 = new HashMap<>();
            // Map<String, Integer> map2 = new HashMap<>();
            // map1.put("a", 1);
            // map2.put("a", 1);
            // assertThat(map1).isEqualTo(map2);
        }

        @Test
        @DisplayName("24. hashCode 일관성")
        void test24_hashCode() {
            // TODO: hashCode() 구현 후 테스트
            // Map<String, Integer> map1 = new HashMap<>();
            // Map<String, Integer> map2 = new HashMap<>();
            // map1.put("a", 1);
            // map2.put("a", 1);
            // assertThat(map1.hashCode()).isEqualTo(map2.hashCode());
        }

        @Test
        @DisplayName("25. toString 형식")
        void test25_toString() {
            // TODO: toString() 구현 후 테스트
            // map.put("a", 1);
            // assertThat(map.toString()).contains("a=1");
        }

        @Test
        @DisplayName("26. 대량 데이터 처리")
        void test26_largeData() {
            // TODO: 대량 데이터 테스트
            // for (int i = 0; i < 100000; i++) {
            //     map.put("key" + i, i);
            // }
            // assertThat(map.size()).isEqualTo(100000);
        }

        @Test
        @DisplayName("27. putAll로 다른 맵 병합")
        void test27_putAll() {
            // TODO: putAll() 구현 후 테스트
            // Map<String, Integer> other = new HashMap<>();
            // other.put("a", 1);
            // other.put("b", 2);
            // map.putAll(other);
            // assertThat(map.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("28. replaceAll로 모든 값 변환")
        void test28_replaceAll() {
            // TODO: replaceAll() 구현 후 테스트
            // map.put("a", 1);
            // map.put("b", 2);
            // map.replaceAll((k, v) -> v * 10);
            // assertThat(map.get("a")).isEqualTo(10);
        }

        @Test
        @DisplayName("29. replace로 특정 값 교체")
        void test29_replace() {
            // TODO: replace() 구현 후 테스트
            // map.put("a", 1);
            // map.replace("a", 100);
            // assertThat(map.get("a")).isEqualTo(100);
        }

        @Test
        @DisplayName("30. Iterator remove 동작")
        void test30_iteratorRemove() {
            // TODO: Iterator.remove() 구현 후 테스트
            // map.put("a", 1);
            // map.put("b", 2);
            // Iterator<Map.Entry<String, Integer>> iter = map.entrySet().iterator();
            // while (iter.hasNext()) {
            //     if (iter.next().getKey().equals("a")) {
            //         iter.remove();
            //     }
            // }
            // assertThat(map.containsKey("a")).isFalse();
        }
    }
}
