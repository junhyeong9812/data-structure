package com.datastructure.lrucache;

import com.datastructure.lrucache.pop.LRUCache;
import com.datastructure.lrucache.pop.LRUCacheProblems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("LRU 캐시 - POP 구현 테스트")
class PopTest {

    private LRUCache cache;

    @BeforeEach
    void setUp() {
        cache = new LRUCache(3);
    }

    @Nested
    @DisplayName("기본 연산")
    class BasicOperations {

        @Test
        @DisplayName("01. put으로 값을 저장할 수 있다")
        void test01_put() {
            // TODO: put() 구현 후 테스트
            // cache.put(1, 100);
            // assertThat(cache.get(1)).isEqualTo(100);
        }

        @Test
        @DisplayName("02. get으로 값을 조회할 수 있다")
        void test02_get() {
            // TODO: get() 구현 후 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // assertThat(cache.get(1)).isEqualTo(100);
            // assertThat(cache.get(2)).isEqualTo(200);
        }

        @Test
        @DisplayName("03. 존재하지 않는 키는 -1 반환")
        void test03_getNonExistent() {
            // TODO: 존재하지 않는 키 테스트
            // assertThat(cache.get(999)).isEqualTo(-1);
        }

        @Test
        @DisplayName("04. 기존 키에 put하면 값이 업데이트된다")
        void test04_updateValue() {
            // TODO: 값 업데이트 테스트
            // cache.put(1, 100);
            // cache.put(1, 111);
            // assertThat(cache.get(1)).isEqualTo(111);
        }

        @Test
        @DisplayName("05. size가 정확히 반환된다")
        void test05_size() {
            // TODO: size() 구현 후 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // assertThat(cache.size()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("LRU 제거")
    class LRUEviction {

        @Test
        @DisplayName("06. 용량 초과 시 LRU 항목 제거")
        void test06_evictLRU() {
            // TODO: LRU 제거 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // cache.put(3, 300);
            // cache.put(4, 400);  // 1 제거
            // assertThat(cache.get(1)).isEqualTo(-1);
            // assertThat(cache.get(2)).isEqualTo(200);
        }

        @Test
        @DisplayName("07. get으로 접근하면 최근 사용으로 표시")
        void test07_getUpdatesOrder() {
            // TODO: get이 순서 갱신하는지 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // cache.put(3, 300);
            // cache.get(1);       // 1을 최근으로
            // cache.put(4, 400);  // 2 제거 (가장 오래됨)
            // assertThat(cache.get(1)).isEqualTo(100);
            // assertThat(cache.get(2)).isEqualTo(-1);
        }

        @Test
        @DisplayName("08. put으로 업데이트하면 최근 사용으로 표시")
        void test08_putUpdatesOrder() {
            // TODO: put 업데이트가 순서 갱신하는지 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // cache.put(3, 300);
            // cache.put(1, 111);  // 1을 최근으로
            // cache.put(4, 400);  // 2 제거
            // assertThat(cache.get(1)).isEqualTo(111);
            // assertThat(cache.get(2)).isEqualTo(-1);
        }

        @Test
        @DisplayName("09. 연속 제거 테스트")
        void test09_multipleEvictions() {
            // TODO: 연속 제거 테스트
            // LRUCache small = new LRUCache(2);
            // small.put(1, 100);
            // small.put(2, 200);
            // small.put(3, 300);  // 1 제거
            // small.put(4, 400);  // 2 제거
            // assertThat(small.get(1)).isEqualTo(-1);
            // assertThat(small.get(2)).isEqualTo(-1);
            // assertThat(small.get(3)).isEqualTo(300);
            // assertThat(small.get(4)).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("엣지 케이스")
    class EdgeCases {

        @Test
        @DisplayName("10. 용량 1인 캐시")
        void test10_capacityOne() {
            // TODO: 용량 1 테스트
            // LRUCache tiny = new LRUCache(1);
            // tiny.put(1, 100);
            // tiny.put(2, 200);  // 1 제거
            // assertThat(tiny.get(1)).isEqualTo(-1);
            // assertThat(tiny.get(2)).isEqualTo(200);
        }

        @Test
        @DisplayName("11. 같은 키 반복 삽입")
        void test11_repeatedPut() {
            // TODO: 같은 키 반복 테스트
            // cache.put(1, 100);
            // cache.put(1, 200);
            // cache.put(1, 300);
            // assertThat(cache.size()).isEqualTo(1);
            // assertThat(cache.get(1)).isEqualTo(300);
        }

        @Test
        @DisplayName("12. 비어있는 캐시에서 get")
        void test12_getFromEmpty() {
            // TODO: 빈 캐시 테스트
            // LRUCache empty = new LRUCache(3);
            // assertThat(empty.get(1)).isEqualTo(-1);
        }
    }

    @Nested
    @DisplayName("추가 기능")
    class AdditionalFeatures {

        @Test
        @DisplayName("13. remove로 항목 제거")
        void test13_remove() {
            // TODO: remove() 구현 후 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // cache.remove(1);
            // assertThat(cache.get(1)).isEqualTo(-1);
            // assertThat(cache.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("14. clear로 전체 삭제")
        void test14_clear() {
            // TODO: clear() 구현 후 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // cache.clear();
            // assertThat(cache.size()).isEqualTo(0);
            // assertThat(cache.get(1)).isEqualTo(-1);
        }

        @Test
        @DisplayName("15. containsKey 테스트")
        void test15_containsKey() {
            // TODO: containsKey() 구현 후 테스트
            // cache.put(1, 100);
            // assertThat(cache.containsKey(1)).isTrue();
            // assertThat(cache.containsKey(2)).isFalse();
        }

        @Test
        @DisplayName("16. peek은 순서 변경 없이 조회")
        void test16_peek() {
            // TODO: peek() 구현 후 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // cache.put(3, 300);
            // cache.peek(1);      // 순서 변경 없음
            // cache.put(4, 400);  // 1 제거 (peek은 순서 변경 안 함)
            // assertThat(cache.get(1)).isEqualTo(-1);
        }
    }

    @Nested
    @DisplayName("순서 조회")
    class OrderQueries {

        @Test
        @DisplayName("17. getOldest - 가장 오래된 항목")
        void test17_getOldest() {
            // TODO: getOldest() 구현 후 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // cache.put(3, 300);
            // assertThat(cache.getOldestKey()).isEqualTo(1);
        }

        @Test
        @DisplayName("18. getNewest - 가장 최근 항목")
        void test18_getNewest() {
            // TODO: getNewest() 구현 후 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // cache.put(3, 300);
            // assertThat(cache.getNewestKey()).isEqualTo(3);
        }

        @Test
        @DisplayName("19. keys - 최근 순서로 키 반환")
        void test19_keys() {
            // TODO: keys() 구현 후 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // cache.put(3, 300);
            // cache.get(1);  // 1을 최근으로
            // assertThat(cache.keys()).containsExactly(1, 3, 2);
        }

        @Test
        @DisplayName("20. values - 최근 순서로 값 반환")
        void test20_values() {
            // TODO: values() 구현 후 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // cache.put(3, 300);
            // cache.get(1);  // 1을 최근으로
            // assertThat(cache.values()).containsExactly(100, 300, 200);
        }
    }

    @Nested
    @DisplayName("LeetCode 스타일")
    class LeetCodeStyle {

        @Test
        @DisplayName("21. LeetCode 예제 1")
        void test21_leetcodeExample1() {
            // TODO: LeetCode 예제 테스트
            // LRUCache cache = new LRUCache(2);
            // cache.put(1, 1);
            // cache.put(2, 2);
            // assertThat(cache.get(1)).isEqualTo(1);
            // cache.put(3, 3);    // 2 제거
            // assertThat(cache.get(2)).isEqualTo(-1);
            // cache.put(4, 4);    // 1 제거
            // assertThat(cache.get(1)).isEqualTo(-1);
            // assertThat(cache.get(3)).isEqualTo(3);
            // assertThat(cache.get(4)).isEqualTo(4);
        }

        @Test
        @DisplayName("22. LeetCode 예제 2")
        void test22_leetcodeExample2() {
            // TODO: 또 다른 예제 테스트
            // LRUCache cache = new LRUCache(2);
            // cache.put(2, 1);
            // cache.put(1, 1);
            // cache.put(2, 3);    // 2 업데이트 + 최근으로
            // cache.put(4, 1);    // 1 제거
            // assertThat(cache.get(1)).isEqualTo(-1);
            // assertThat(cache.get(2)).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("23. capacity 조회")
        void test23_capacity() {
            // TODO: capacity() 구현 후 테스트
            // assertThat(cache.capacity()).isEqualTo(3);
        }

        @Test
        @DisplayName("24. isEmpty 테스트")
        void test24_isEmpty() {
            // TODO: isEmpty() 구현 후 테스트
            // assertThat(cache.isEmpty()).isTrue();
            // cache.put(1, 100);
            // assertThat(cache.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("25. 대량 데이터")
        void test25_largeData() {
            // TODO: 대량 데이터 테스트
            // LRUCache large = new LRUCache(1000);
            // for (int i = 0; i < 10000; i++) {
            //     large.put(i, i * 10);
            // }
            // assertThat(large.size()).isEqualTo(1000);
            // assertThat(large.get(9999)).isEqualTo(99990);
            // assertThat(large.get(0)).isEqualTo(-1);
        }

        @Test
        @DisplayName("26. 연속 get")
        void test26_consecutiveGets() {
            // TODO: 연속 get 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // cache.put(3, 300);
            // for (int i = 0; i < 100; i++) {
            //     cache.get(1);  // 1을 계속 최근으로
            // }
            // cache.put(4, 400);  // 2 제거
            // assertThat(cache.get(1)).isEqualTo(100);
            // assertThat(cache.get(2)).isEqualTo(-1);
        }

        @Test
        @DisplayName("27. 음수 값")
        void test27_negativeValues() {
            // TODO: 음수 값 테스트
            // cache.put(1, -100);
            // assertThat(cache.get(1)).isEqualTo(-100);
        }

        @Test
        @DisplayName("28. 0 값")
        void test28_zeroValue() {
            // TODO: 0 값 테스트
            // cache.put(1, 0);
            // assertThat(cache.get(1)).isEqualTo(0);
        }

        @Test
        @DisplayName("29. toString")
        void test29_toString() {
            // TODO: toString() 구현 후 테스트
            // cache.put(1, 100);
            // cache.put(2, 200);
            // assertThat(cache.toString()).contains("1", "2");
        }

        @Test
        @DisplayName("30. 히트율 계산")
        void test30_hitRate() {
            // TODO: 히트율 테스트 (선택)
            // cache.put(1, 100);
            // cache.get(1);  // hit
            // cache.get(2);  // miss
            // cache.get(1);  // hit
            // assertThat(cache.getHitRate()).isCloseTo(0.67, within(0.01));
        }
    }
}
