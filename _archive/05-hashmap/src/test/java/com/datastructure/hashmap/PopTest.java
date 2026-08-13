package com.datastructure.hashmap;

import com.datastructure.hashmap.pop.ChainingHashMap;
import com.datastructure.hashmap.pop.LinearProbingHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("해시맵 - POP 구현 테스트")
class PopTest {

    @Nested
    @DisplayName("체이닝 해시맵")
    class ChainingHashMapTest {
        
        private ChainingHashMap map;

        @BeforeEach
        void setUp() {
            map = new ChainingHashMap();
        }

        @Nested
        @DisplayName("기본 연산")
        class BasicOperations {

            @Test
            @DisplayName("01. 빈 맵 생성 시 size는 0이다")
            void test01_emptyMapSizeIsZero() {
                // TODO: size() 구현 후 테스트
                // assertThat(map.size()).isEqualTo(0);
            }

            @Test
            @DisplayName("02. 빈 맵은 isEmpty가 true다")
            void test02_emptyMapIsEmpty() {
                // TODO: isEmpty() 구현 후 테스트
                // assertThat(map.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("03. put으로 키-값을 저장할 수 있다")
            void test03_putAndGet() {
                // TODO: put(), get() 구현 후 테스트
                // map.put("key", 100);
                // assertThat(map.get("key")).isEqualTo(100);
            }

            @Test
            @DisplayName("04. 존재하지 않는 키는 null을 반환한다")
            void test04_getNonExistentKey() {
                // TODO: get() 구현 후 테스트
                // assertThat(map.get("nonexistent")).isNull();
            }

            @Test
            @DisplayName("05. 같은 키로 put하면 값이 덮어쓰기된다")
            void test05_putOverwrites() {
                // TODO: 값 덮어쓰기 테스트
                // map.put("key", 1);
                // map.put("key", 2);
                // assertThat(map.get("key")).isEqualTo(2);
                // assertThat(map.size()).isEqualTo(1);
            }

            @Test
            @DisplayName("06. put은 이전 값을 반환한다")
            void test06_putReturnsOldValue() {
                // TODO: put() 반환값 테스트
                // map.put("key", 1);
                // Integer old = map.put("key", 2);
                // assertThat(old).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("삭제")
        class Remove {

            @Test
            @DisplayName("07. remove로 키-값을 삭제할 수 있다")
            void test07_remove() {
                // TODO: remove() 구현 후 테스트
                // map.put("key", 100);
                // map.remove("key");
                // assertThat(map.containsKey("key")).isFalse();
            }

            @Test
            @DisplayName("08. remove는 삭제된 값을 반환한다")
            void test08_removeReturnsValue() {
                // TODO: remove() 반환값 테스트
                // map.put("key", 100);
                // Integer removed = map.remove("key");
                // assertThat(removed).isEqualTo(100);
            }

            @Test
            @DisplayName("09. 존재하지 않는 키 remove는 null을 반환한다")
            void test09_removeNonExistent() {
                // TODO: 존재하지 않는 키 삭제 테스트
                // assertThat(map.remove("nonexistent")).isNull();
            }
        }

        @Nested
        @DisplayName("검색")
        class Search {

            @Test
            @DisplayName("10. containsKey로 키 존재 여부를 확인할 수 있다")
            void test10_containsKey() {
                // TODO: containsKey() 구현 후 테스트
                // map.put("key", 100);
                // assertThat(map.containsKey("key")).isTrue();
                // assertThat(map.containsKey("other")).isFalse();
            }

            @Test
            @DisplayName("11. containsValue로 값 존재 여부를 확인할 수 있다")
            void test11_containsValue() {
                // TODO: containsValue() 구현 후 테스트
                // map.put("key", 100);
                // assertThat(map.containsValue(100)).isTrue();
                // assertThat(map.containsValue(999)).isFalse();
            }
        }

        @Nested
        @DisplayName("null 처리")
        class NullHandling {

            @Test
            @DisplayName("12. null 키를 저장할 수 있다")
            void test12_nullKey() {
                // TODO: null 키 처리 구현 후 테스트
                // map.put(null, 100);
                // assertThat(map.get(null)).isEqualTo(100);
            }

            @Test
            @DisplayName("13. null 값을 저장할 수 있다")
            void test13_nullValue() {
                // TODO: null 값 처리 구현 후 테스트
                // map.put("key", null);
                // assertThat(map.get("key")).isNull();
                // assertThat(map.containsKey("key")).isTrue();
            }
        }

        @Nested
        @DisplayName("리해싱")
        class Rehashing {

            @Test
            @DisplayName("14. 많은 요소 추가 시 자동으로 리해싱된다")
            void test14_autoRehash() {
                // TODO: 리해싱 구현 후 테스트
                // for (int i = 0; i < 100; i++) {
                //     map.put("key" + i, i);
                // }
                // assertThat(map.size()).isEqualTo(100);
                // for (int i = 0; i < 100; i++) {
                //     assertThat(map.get("key" + i)).isEqualTo(i);
                // }
            }

            @Test
            @DisplayName("15. 리해싱 후에도 모든 값이 유지된다")
            void test15_valuesPreservedAfterRehash() {
                // TODO: 리해싱 후 값 보존 테스트
                // for (int i = 0; i < 50; i++) {
                //     map.put("key" + i, i * 10);
                // }
                // for (int i = 0; i < 50; i++) {
                //     assertThat(map.get("key" + i)).isEqualTo(i * 10);
                // }
            }
        }
    }

    @Nested
    @DisplayName("선형 탐사 해시맵")
    class LinearProbingHashMapTest {
        
        private LinearProbingHashMap map;

        @BeforeEach
        void setUp() {
            map = new LinearProbingHashMap();
        }

        @Test
        @DisplayName("16. 기본 put/get 동작")
        void test16_basicPutGet() {
            // TODO: LinearProbingHashMap 구현 후 테스트
            // map.put("a", 1);
            // map.put("b", 2);
            // assertThat(map.get("a")).isEqualTo(1);
            // assertThat(map.get("b")).isEqualTo(2);
        }

        @Test
        @DisplayName("17. 충돌 시 선형 탐사로 해결")
        void test17_linearProbing() {
            // TODO: 충돌 해결 테스트
            // 같은 해시값을 가지는 키들 테스트
        }

        @Test
        @DisplayName("18. 삭제 후 검색이 정상 동작")
        void test18_removeAndSearch() {
            // TODO: 삭제 후 클러스터 재배치 테스트
            // map.put("a", 1);
            // map.put("b", 2);  // a와 충돌 가정
            // map.put("c", 3);  // b와 충돌 가정
            // map.remove("b");
            // assertThat(map.get("c")).isEqualTo(3);  // 여전히 찾을 수 있어야 함
        }

        @Test
        @DisplayName("19. 로드 팩터 초과 시 리해싱")
        void test19_rehashOnHighLoad() {
            // TODO: 리해싱 테스트
            // for (int i = 0; i < 100; i++) {
            //     map.put("key" + i, i);
            // }
            // assertThat(map.size()).isEqualTo(100);
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("20. clear로 모든 요소 삭제")
        void test20_clear() {
            // TODO: clear() 구현 후 테스트
            // ChainingHashMap map = new ChainingHashMap();
            // map.put("a", 1);
            // map.put("b", 2);
            // map.clear();
            // assertThat(map.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("21. keySet으로 모든 키 조회")
        void test21_keySet() {
            // TODO: keySet() 구현 후 테스트
            // ChainingHashMap map = new ChainingHashMap();
            // map.put("a", 1);
            // map.put("b", 2);
            // Set<String> keys = map.keySet();
            // assertThat(keys).containsExactlyInAnyOrder("a", "b");
        }

        @Test
        @DisplayName("22. values로 모든 값 조회")
        void test22_values() {
            // TODO: values() 구현 후 테스트
            // ChainingHashMap map = new ChainingHashMap();
            // map.put("a", 1);
            // map.put("b", 2);
            // Collection<Integer> values = map.values();
            // assertThat(values).containsExactlyInAnyOrder(1, 2);
        }

        @Test
        @DisplayName("23. 동일한 해시값을 가진 키들 처리")
        void test23_sameHashKeys() {
            // TODO: 해시 충돌 테스트
            // "Aa"와 "BB"는 같은 해시값을 가짐
            // ChainingHashMap map = new ChainingHashMap();
            // map.put("Aa", 1);
            // map.put("BB", 2);
            // assertThat(map.get("Aa")).isEqualTo(1);
            // assertThat(map.get("BB")).isEqualTo(2);
        }

        @Test
        @DisplayName("24. getOrDefault 동작")
        void test24_getOrDefault() {
            // TODO: getOrDefault() 구현 후 테스트
            // ChainingHashMap map = new ChainingHashMap();
            // map.put("a", 1);
            // assertThat(map.getOrDefault("a", -1)).isEqualTo(1);
            // assertThat(map.getOrDefault("b", -1)).isEqualTo(-1);
        }

        @Test
        @DisplayName("25. putIfAbsent 동작")
        void test25_putIfAbsent() {
            // TODO: putIfAbsent() 구현 후 테스트
            // ChainingHashMap map = new ChainingHashMap();
            // map.put("a", 1);
            // map.putIfAbsent("a", 100);  // 무시됨
            // map.putIfAbsent("b", 2);    // 추가됨
            // assertThat(map.get("a")).isEqualTo(1);
            // assertThat(map.get("b")).isEqualTo(2);
        }

        @Test
        @DisplayName("26. 대량 데이터 성능 테스트")
        void test26_performanceTest() {
            // TODO: 성능 테스트
            // ChainingHashMap map = new ChainingHashMap();
            // int n = 100000;
            // for (int i = 0; i < n; i++) {
            //     map.put("key" + i, i);
            // }
            // assertThat(map.size()).isEqualTo(n);
        }

        @Test
        @DisplayName("27. toString으로 내용 확인")
        void test27_toString() {
            // TODO: toString() 구현 후 테스트
            // ChainingHashMap map = new ChainingHashMap();
            // map.put("a", 1);
            // map.put("b", 2);
            // assertThat(map.toString()).contains("a=1", "b=2");
        }

        @Test
        @DisplayName("28. entrySet으로 모든 엔트리 조회")
        void test28_entrySet() {
            // TODO: entrySet() 구현 후 테스트
            // ChainingHashMap map = new ChainingHashMap();
            // map.put("a", 1);
            // map.put("b", 2);
            // Set<Entry> entries = map.entrySet();
            // assertThat(entries).hasSize(2);
        }

        @Test
        @DisplayName("29. 반복자(Iterator)로 순회")
        void test29_iterator() {
            // TODO: Iterator 구현 후 테스트
            // ChainingHashMap map = new ChainingHashMap();
            // map.put("a", 1);
            // map.put("b", 2);
            // int sum = 0;
            // for (Entry e : map) {
            //     sum += e.getValue();
            // }
            // assertThat(sum).isEqualTo(3);
        }

        @Test
        @DisplayName("30. equals와 hashCode 정확성")
        void test30_equalsAndHashCode() {
            // TODO: equals/hashCode 테스트
            // ChainingHashMap map1 = new ChainingHashMap();
            // ChainingHashMap map2 = new ChainingHashMap();
            // map1.put("a", 1);
            // map2.put("a", 1);
            // assertThat(map1).isEqualTo(map2);
            // assertThat(map1.hashCode()).isEqualTo(map2.hashCode());
        }
    }
}
