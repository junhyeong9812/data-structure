package com.datastructure.skiplist;

import com.datastructure.skiplist.pop.SkipList;
import com.datastructure.skiplist.pop.SkipListMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DisplayName("스킵 리스트 - POP 구현 테스트")
class PopTest {

    @Nested
    @DisplayName("기본 SkipList")
    class BasicSkipListTest {

        private SkipList<Integer> list;

        @BeforeEach
        void setUp() {
            list = new SkipList<>();
        }

        @Test
        @DisplayName("01. 빈 스킵 리스트 생성")
        void test01_emptySkipList() {
            // TODO: isEmpty(), size() 구현 후 테스트
            // assertThat(list.isEmpty()).isTrue();
            // assertThat(list.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("02. 원소 추가 후 검색")
        void test02_addAndSearch() {
            // TODO: add(), search() 구현 후 테스트
            // list.add(5);
            // assertThat(list.search(5)).isTrue();
        }

        @Test
        @DisplayName("03. 존재하지 않는 원소 검색")
        void test03_searchNonExistent() {
            // TODO: 존재하지 않는 원소 테스트
            // list.add(5);
            // assertThat(list.search(10)).isFalse();
        }

        @Test
        @DisplayName("04. 여러 원소 추가")
        void test04_addMultiple() {
            // TODO: 여러 원소 테스트
            // list.add(3);
            // list.add(6);
            // list.add(7);
            // list.add(9);
            // assertThat(list.size()).isEqualTo(4);
            // assertThat(list.search(6)).isTrue();
            // assertThat(list.search(7)).isTrue();
        }

        @Test
        @DisplayName("05. 원소 삭제")
        void test05_remove() {
            // TODO: remove() 구현 후 테스트
            // list.add(5);
            // list.add(10);
            // assertThat(list.remove(5)).isTrue();
            // assertThat(list.search(5)).isFalse();
            // assertThat(list.search(10)).isTrue();
        }
    }

    @Nested
    @DisplayName("정렬 순서")
    class SortedOrderTest {

        private SkipList<Integer> list;

        @BeforeEach
        void setUp() {
            list = new SkipList<>();
        }

        @Test
        @DisplayName("06. 정렬된 순서 유지")
        void test06_maintainsSortedOrder() {
            // TODO: Iterator 구현 후 테스트
            // list.add(5);
            // list.add(2);
            // list.add(8);
            // list.add(1);
            // List<Integer> elements = new ArrayList<>();
            // for (int x : list) {
            //     elements.add(x);
            // }
            // assertThat(elements).containsExactly(1, 2, 5, 8);
        }

        @Test
        @DisplayName("07. getMin - 최소 원소")
        void test07_getMin() {
            // TODO: getMin() 구현 후 테스트
            // list.add(5);
            // list.add(2);
            // list.add(8);
            // assertThat(list.getMin()).isEqualTo(2);
        }

        @Test
        @DisplayName("08. getMax - 최대 원소")
        void test08_getMax() {
            // TODO: getMax() 구현 후 테스트
            // list.add(5);
            // list.add(2);
            // list.add(8);
            // assertThat(list.getMax()).isEqualTo(8);
        }
    }

    @Nested
    @DisplayName("범위 연산")
    class RangeOperationsTest {

        private SkipList<Integer> list;

        @BeforeEach
        void setUp() {
            list = new SkipList<>();
            // 1, 3, 5, 7, 9, 11 추가 (테스트 활성화 시)
        }

        @Test
        @DisplayName("09. floor - key 이하의 최대")
        void test09_floor() {
            // TODO: floor() 구현 후 테스트
            // for (int i = 1; i <= 11; i += 2) list.add(i);
            // assertThat(list.floor(6)).isEqualTo(5);
            // assertThat(list.floor(5)).isEqualTo(5);
            // assertThat(list.floor(0)).isNull();
        }

        @Test
        @DisplayName("10. ceiling - key 이상의 최소")
        void test10_ceiling() {
            // TODO: ceiling() 구현 후 테스트
            // for (int i = 1; i <= 11; i += 2) list.add(i);
            // assertThat(list.ceiling(6)).isEqualTo(7);
            // assertThat(list.ceiling(7)).isEqualTo(7);
            // assertThat(list.ceiling(12)).isNull();
        }

        @Test
        @DisplayName("11. range - 범위 쿼리")
        void test11_range() {
            // TODO: range() 구현 후 테스트
            // for (int i = 1; i <= 11; i += 2) list.add(i);
            // assertThat(list.range(4, 8)).containsExactly(5, 7);
        }

        @Test
        @DisplayName("12. 빈 범위 쿼리")
        void test12_emptyRange() {
            // TODO: 빈 범위 테스트
            // for (int i = 1; i <= 11; i += 2) list.add(i);
            // assertThat(list.range(12, 20)).isEmpty();
        }
    }

    @Nested
    @DisplayName("레벨 테스트")
    class LevelTest {

        @Test
        @DisplayName("13. 레벨 정보 확인")
        void test13_levelInfo() {
            // TODO: getLevel() 구현 후 테스트
            // SkipList<Integer> list = new SkipList<>();
            // for (int i = 0; i < 100; i++) {
            //     list.add(i);
            // }
            // assertThat(list.getLevel()).isGreaterThan(0);
        }

        @Test
        @DisplayName("14. 대량 삽입 시 레벨 증가")
        void test14_levelGrowth() {
            // TODO: 대량 삽입 레벨 테스트
            // SkipList<Integer> list = new SkipList<>();
            // for (int i = 0; i < 10000; i++) {
            //     list.add(i);
            // }
            // 평균적으로 log2(10000) ≈ 13 레벨
            // assertThat(list.getLevel()).isBetween(5, 20);
        }
    }

    @Nested
    @DisplayName("SkipListMap (Key-Value)")
    class SkipListMapTest {

        private SkipListMap<String, Integer> map;

        @BeforeEach
        void setUp() {
            map = new SkipListMap<>();
        }

        @Test
        @DisplayName("15. put/get 기본")
        void test15_putGet() {
            // TODO: put(), get() 구현 후 테스트
            // map.put("apple", 1);
            // map.put("banana", 2);
            // assertThat(map.get("apple")).isEqualTo(1);
            // assertThat(map.get("banana")).isEqualTo(2);
        }

        @Test
        @DisplayName("16. 값 업데이트")
        void test16_updateValue() {
            // TODO: 값 업데이트 테스트
            // map.put("apple", 1);
            // map.put("apple", 10);
            // assertThat(map.get("apple")).isEqualTo(10);
        }

        @Test
        @DisplayName("17. containsKey")
        void test17_containsKey() {
            // TODO: containsKey() 구현 후 테스트
            // map.put("apple", 1);
            // assertThat(map.containsKey("apple")).isTrue();
            // assertThat(map.containsKey("banana")).isFalse();
        }

        @Test
        @DisplayName("18. remove")
        void test18_remove() {
            // TODO: remove() 구현 후 테스트
            // map.put("apple", 1);
            // Integer removed = map.remove("apple");
            // assertThat(removed).isEqualTo(1);
            // assertThat(map.containsKey("apple")).isFalse();
        }
    }

    @Nested
    @DisplayName("엣지 케이스")
    class EdgeCases {

        @Test
        @DisplayName("19. 중복 추가 무시")
        void test19_duplicateAdd() {
            // TODO: 중복 추가 테스트
            // SkipList<Integer> list = new SkipList<>();
            // list.add(5);
            // list.add(5);
            // assertThat(list.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("20. 존재하지 않는 원소 삭제")
        void test20_removeNonExistent() {
            // TODO: 존재하지 않는 원소 삭제 테스트
            // SkipList<Integer> list = new SkipList<>();
            // list.add(5);
            // assertThat(list.remove(10)).isFalse();
        }

        @Test
        @DisplayName("21. 빈 리스트에서 삭제")
        void test21_removeFromEmpty() {
            // TODO: 빈 리스트 삭제 테스트
            // SkipList<Integer> list = new SkipList<>();
            // assertThat(list.remove(5)).isFalse();
        }

        @Test
        @DisplayName("22. 빈 리스트의 min/max")
        void test22_minMaxEmpty() {
            // TODO: 빈 리스트 min/max 테스트
            // SkipList<Integer> list = new SkipList<>();
            // assertThat(list.getMin()).isNull();
            // assertThat(list.getMax()).isNull();
        }

        @Test
        @DisplayName("23. 단일 원소")
        void test23_singleElement() {
            // TODO: 단일 원소 테스트
            // SkipList<Integer> list = new SkipList<>();
            // list.add(42);
            // assertThat(list.getMin()).isEqualTo(42);
            // assertThat(list.getMax()).isEqualTo(42);
        }
    }

    @Nested
    @DisplayName("대용량 테스트")
    class LargeScaleTest {

        @Test
        @DisplayName("24. 10000개 원소")
        void test24_tenThousand() {
            // TODO: 대용량 테스트
            // SkipList<Integer> list = new SkipList<>();
            // for (int i = 0; i < 10000; i++) {
            //     list.add(i);
            // }
            // assertThat(list.size()).isEqualTo(10000);
            // assertThat(list.search(5000)).isTrue();
        }

        @Test
        @DisplayName("25. 역순 삽입")
        void test25_reverseOrder() {
            // TODO: 역순 삽입 테스트
            // SkipList<Integer> list = new SkipList<>();
            // for (int i = 1000; i >= 1; i--) {
            //     list.add(i);
            // }
            // assertThat(list.getMin()).isEqualTo(1);
            // assertThat(list.getMax()).isEqualTo(1000);
        }

        @Test
        @DisplayName("26. 랜덤 순서 삽입")
        void test26_randomOrder() {
            // TODO: 랜덤 순서 테스트
            // SkipList<Integer> list = new SkipList<>();
            // Random random = new Random(42);
            // Set<Integer> expected = new HashSet<>();
            // for (int i = 0; i < 1000; i++) {
            //     int val = random.nextInt(10000);
            //     list.add(val);
            //     expected.add(val);
            // }
            // assertThat(list.size()).isEqualTo(expected.size());
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("27. clear")
        void test27_clear() {
            // TODO: clear() 구현 후 테스트
            // SkipList<Integer> list = new SkipList<>();
            // list.add(1);
            // list.add(2);
            // list.clear();
            // assertThat(list.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("28. String 타입")
        void test28_stringType() {
            // TODO: String 타입 테스트
            // SkipList<String> list = new SkipList<>();
            // list.add("banana");
            // list.add("apple");
            // list.add("cherry");
            // assertThat(list.getMin()).isEqualTo("apple");
        }

        @Test
        @DisplayName("29. contains (search alias)")
        void test29_contains() {
            // TODO: contains() 구현 후 테스트
            // SkipList<Integer> list = new SkipList<>();
            // list.add(5);
            // assertThat(list.contains(5)).isTrue();
        }

        @Test
        @DisplayName("30. toString")
        void test30_toString() {
            // TODO: toString() 구현 후 테스트
            // SkipList<Integer> list = new SkipList<>();
            // list.add(1);
            // list.add(2);
            // assertThat(list.toString()).contains("1", "2");
        }
    }
}
