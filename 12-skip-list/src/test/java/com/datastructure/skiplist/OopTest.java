package com.datastructure.skiplist;

import com.datastructure.skiplist.oop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DisplayName("스킵 리스트 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("SkipListSet")
    class SkipListSetTest {

        private OrderedSet<Integer> set;

        @BeforeEach
        void setUp() {
            set = new SkipListSet<>();
        }

        @Test
        @DisplayName("01. add/contains")
        void test01_addContains() {
            // TODO: add(), contains() 구현 후 테스트
            // set.add(5);
            // assertThat(set.contains(5)).isTrue();
            // assertThat(set.contains(10)).isFalse();
        }

        @Test
        @DisplayName("02. 정렬 순서 유지")
        void test02_sortedOrder() {
            // TODO: iterator 구현 후 테스트
            // set.add(5);
            // set.add(2);
            // set.add(8);
            // List<Integer> list = new ArrayList<>();
            // set.forEach(list::add);
            // assertThat(list).containsExactly(2, 5, 8);
        }

        @Test
        @DisplayName("03. remove")
        void test03_remove() {
            // TODO: remove() 구현 후 테스트
            // set.add(5);
            // set.add(10);
            // set.remove(5);
            // assertThat(set.contains(5)).isFalse();
        }

        @Test
        @DisplayName("04. size/isEmpty")
        void test04_sizeIsEmpty() {
            // TODO: size(), isEmpty() 테스트
            // assertThat(set.isEmpty()).isTrue();
            // set.add(5);
            // assertThat(set.isEmpty()).isFalse();
            // assertThat(set.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("05. floor/ceiling")
        void test05_floorCeiling() {
            // TODO: floor(), ceiling() 테스트
            // set.add(1);
            // set.add(5);
            // set.add(10);
            // assertThat(set.floor(6)).isEqualTo(5);
            // assertThat(set.ceiling(6)).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("SkipListMapImpl")
    class SkipListMapImplTest {

        private OrderedMap<String, Integer> map;

        @BeforeEach
        void setUp() {
            map = new SkipListMapImpl<>();
        }

        @Test
        @DisplayName("06. put/get")
        void test06_putGet() {
            // TODO: put(), get() 구현 후 테스트
            // map.put("apple", 1);
            // assertThat(map.get("apple")).isEqualTo(1);
        }

        @Test
        @DisplayName("07. 키 정렬 순서")
        void test07_keySortedOrder() {
            // TODO: keys() 또는 iterator 테스트
            // map.put("banana", 2);
            // map.put("apple", 1);
            // map.put("cherry", 3);
            // assertThat(map.keys()).containsExactly("apple", "banana", "cherry");
        }

        @Test
        @DisplayName("08. containsKey")
        void test08_containsKey() {
            // TODO: containsKey() 테스트
            // map.put("apple", 1);
            // assertThat(map.containsKey("apple")).isTrue();
            // assertThat(map.containsKey("banana")).isFalse();
        }

        @Test
        @DisplayName("09. remove")
        void test09_remove() {
            // TODO: remove() 테스트
            // map.put("apple", 1);
            // map.remove("apple");
            // assertThat(map.containsKey("apple")).isFalse();
        }

        @Test
        @DisplayName("10. getOrDefault")
        void test10_getOrDefault() {
            // TODO: getOrDefault() 테스트
            // map.put("apple", 1);
            // assertThat(map.getOrDefault("apple", 0)).isEqualTo(1);
            // assertThat(map.getOrDefault("banana", 0)).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("제네릭 타입")
    class GenericTypeTest {

        @Test
        @DisplayName("11. Double 타입")
        void test11_doubleType() {
            // TODO: Double 타입 테스트
            // OrderedSet<Double> set = new SkipListSet<>();
            // set.add(3.14);
            // set.add(2.71);
            // assertThat(set.first()).isEqualTo(2.71);
        }

        @Test
        @DisplayName("12. 사용자 정의 Comparable")
        void test12_customComparable() {
            // TODO: 사용자 정의 타입 테스트
            // record Score(int value) implements Comparable<Score> {
            //     public int compareTo(Score o) {
            //         return Integer.compare(value, o.value);
            //     }
            // }
            // OrderedSet<Score> set = new SkipListSet<>();
            // set.add(new Score(90));
            // set.add(new Score(80));
            // assertThat(set.first().value()).isEqualTo(80);
        }
    }

    @Nested
    @DisplayName("Iterable 지원")
    class IterableTest {

        @Test
        @DisplayName("13. for-each 루프")
        void test13_forEach() {
            // TODO: Iterable 구현 테스트
            // OrderedSet<Integer> set = new SkipListSet<>();
            // set.add(3);
            // set.add(1);
            // set.add(2);
            // List<Integer> list = new ArrayList<>();
            // for (int x : set) {
            //     list.add(x);
            // }
            // assertThat(list).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("14. stream")
        void test14_stream() {
            // TODO: stream() 테스트
            // OrderedSet<Integer> set = new SkipListSet<>();
            // set.add(1);
            // set.add(2);
            // set.add(3);
            // int sum = set.stream().mapToInt(x -> x).sum();
            // assertThat(sum).isEqualTo(6);
        }
    }

    @Nested
    @DisplayName("범위 연산")
    class RangeOperations {

        @Test
        @DisplayName("15. range")
        void test15_range() {
            // TODO: range() 테스트
            // OrderedSet<Integer> set = new SkipListSet<>();
            // for (int i = 1; i <= 10; i++) set.add(i);
            // assertThat(set.range(3, 7)).containsExactly(3, 4, 5, 6, 7);
        }

        @Test
        @DisplayName("16. headSet")
        void test16_headSet() {
            // TODO: headSet() 테스트 (to 미만)
            // assertThat(set.headSet(5)).containsExactly(1, 2, 3, 4);
        }

        @Test
        @DisplayName("17. tailSet")
        void test17_tailSet() {
            // TODO: tailSet() 테스트 (from 이상)
            // assertThat(set.tailSet(5)).containsExactly(5, 6, 7, 8, 9, 10);
        }

        @Test
        @DisplayName("18. subSet")
        void test18_subSet() {
            // TODO: subSet() 테스트
            // assertThat(set.subSet(3, 7)).containsExactly(3, 4, 5, 6);
        }
    }

    @Nested
    @DisplayName("인터페이스 공통")
    class InterfaceCommonTest {

        @Test
        @DisplayName("19. 다형성")
        void test19_polymorphism() {
            // TODO: 다형성 테스트
            // OrderedSet<Integer>[] sets = new OrderedSet[] {
            //     new SkipListSet<>()
            // };
            // for (OrderedSet<Integer> s : sets) {
            //     s.add(5);
            //     assertThat(s.contains(5)).isTrue();
            // }
        }

        @Test
        @DisplayName("20. clear")
        void test20_clear() {
            // TODO: clear() 테스트
            // OrderedSet<Integer> set = new SkipListSet<>();
            // set.add(1);
            // set.clear();
            // assertThat(set.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("21. first/last")
        void test21_firstLast() {
            // TODO: first(), last() 테스트
            // OrderedSet<Integer> set = new SkipListSet<>();
            // set.add(5);
            // set.add(2);
            // set.add(8);
            // assertThat(set.first()).isEqualTo(2);
            // assertThat(set.last()).isEqualTo(8);
        }

        @Test
        @DisplayName("22. pollFirst/pollLast")
        void test22_pollFirstLast() {
            // TODO: pollFirst(), pollLast() 테스트
            // OrderedSet<Integer> set = new SkipListSet<>();
            // set.add(1);
            // set.add(2);
            // set.add(3);
            // assertThat(set.pollFirst()).isEqualTo(1);
            // assertThat(set.pollLast()).isEqualTo(3);
            // assertThat(set.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("23. equals")
        void test23_equals() {
            // TODO: equals() 테스트
            // OrderedSet<Integer> s1 = new SkipListSet<>();
            // OrderedSet<Integer> s2 = new SkipListSet<>();
            // s1.add(1);
            // s2.add(1);
            // assertThat(s1).isEqualTo(s2);
        }

        @Test
        @DisplayName("24. hashCode")
        void test24_hashCode() {
            // TODO: hashCode() 테스트
            // assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
        }

        @Test
        @DisplayName("25. toString")
        void test25_toString() {
            // TODO: toString() 테스트
            // set.add(1);
            // assertThat(set.toString()).contains("1");
        }

        @Test
        @DisplayName("26. Comparator 지원")
        void test26_comparator() {
            // TODO: Comparator 버전 테스트
            // OrderedSet<String> set = new SkipListSet<>(
            //     Comparator.comparingInt(String::length)
            // );
            // set.add("aa");
            // set.add("b");
            // set.add("ccc");
            // assertThat(set.first()).isEqualTo("b");
        }

        @Test
        @DisplayName("27. descendingIterator")
        void test27_descendingIterator() {
            // TODO: 역순 iterator 테스트
            // set.add(1);
            // set.add(2);
            // set.add(3);
            // List<Integer> desc = new ArrayList<>();
            // set.descendingIterator().forEachRemaining(desc::add);
            // assertThat(desc).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("28. Map entries")
        void test28_mapEntries() {
            // TODO: Map entries 테스트
            // map.put("a", 1);
            // map.put("b", 2);
            // assertThat(map.entrySet()).hasSize(2);
        }

        @Test
        @DisplayName("29. Map values")
        void test29_mapValues() {
            // TODO: Map values 테스트
            // map.put("a", 1);
            // map.put("b", 2);
            // assertThat(map.values()).containsExactlyInAnyOrder(1, 2);
        }

        @Test
        @DisplayName("30. 대량 데이터 정렬 확인")
        void test30_largeDataSorted() {
            // TODO: 대량 데이터 정렬 테스트
            // OrderedSet<Integer> set = new SkipListSet<>();
            // Random rand = new Random(42);
            // for (int i = 0; i < 1000; i++) {
            //     set.add(rand.nextInt(10000));
            // }
            // Integer prev = null;
            // for (int x : set) {
            //     if (prev != null) {
            //         assertThat(x).isGreaterThanOrEqualTo(prev);
            //     }
            //     prev = x;
            // }
        }
    }
}
