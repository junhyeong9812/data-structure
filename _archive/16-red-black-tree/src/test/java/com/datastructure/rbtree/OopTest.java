package com.datastructure.rbtree;

import com.datastructure.rbtree.oop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Red-Black 트리 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("RedBlackTreeMap")
    class RedBlackTreeMapTest {

        private SortedTree<Integer, String> tree;

        @BeforeEach
        void setUp() {
            tree = new RedBlackTreeMap<>();
        }

        @Test
        @DisplayName("01. put/get")
        void test01_putGet() {
            // TODO: put(), get() 테스트
            // tree.put(10, "ten");
            // assertThat(tree.get(10)).isEqualTo(Optional.of("ten"));
        }

        @Test
        @DisplayName("02. containsKey")
        void test02_containsKey() {
            // TODO: containsKey() 테스트
        }

        @Test
        @DisplayName("03. remove")
        void test03_remove() {
            // TODO: remove() 테스트
        }

        @Test
        @DisplayName("04. 값 업데이트")
        void test04_updateValue() {
            // TODO: 값 업데이트 테스트
        }

        @Test
        @DisplayName("05. size/isEmpty")
        void test05_sizeIsEmpty() {
            // TODO: size(), isEmpty() 테스트
        }
    }

    @Nested
    @DisplayName("RedBlackTreeSet")
    class RedBlackTreeSetTest {

        private RedBlackTreeSet<Integer> set;

        @BeforeEach
        void setUp() {
            set = new RedBlackTreeSet<>();
        }

        @Test
        @DisplayName("06. add/contains")
        void test06_addContains() {
            // TODO: add(), contains() 테스트
        }

        @Test
        @DisplayName("07. remove")
        void test07_remove() {
            // TODO: remove() 테스트
        }

        @Test
        @DisplayName("08. 정렬 순서")
        void test08_sortedOrder() {
            // TODO: 정렬 순서 테스트
        }
    }

    @Nested
    @DisplayName("SortedTree 인터페이스")
    class SortedTreeInterfaceTest {

        @Test
        @DisplayName("09. firstKey/lastKey")
        void test09_firstLastKey() {
            // TODO: 최소/최대 키 테스트
        }

        @Test
        @DisplayName("10. floorKey/ceilingKey")
        void test10_floorCeiling() {
            // TODO: floor/ceiling 테스트
        }

        @Test
        @DisplayName("11. lowerKey/higherKey")
        void test11_lowerHigher() {
            // TODO: lower/higher 테스트
        }

        @Test
        @DisplayName("12. subMap")
        void test12_subMap() {
            // TODO: 범위 뷰 테스트
        }
    }

    @Nested
    @DisplayName("Iterable 지원")
    class IterableTest {

        @Test
        @DisplayName("13. for-each")
        void test13_forEach() {
            // TODO: Iterable 구현 테스트
        }

        @Test
        @DisplayName("14. stream")
        void test14_stream() {
            // TODO: Stream API 테스트
        }
    }

    @Nested
    @DisplayName("제네릭 타입")
    class GenericTypeTest {

        @Test
        @DisplayName("15. String 키")
        void test15_stringKey() {
            // TODO: String 키 테스트
        }

        @Test
        @DisplayName("16. 사용자 정의 Comparable")
        void test16_customComparable() {
            // TODO: 사용자 정의 타입 테스트
        }

        @Test
        @DisplayName("17. Comparator 지원")
        void test17_comparator() {
            // TODO: Comparator 지원 테스트
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("18. equals")
        void test18_equals() {
            // TODO: equals() 테스트
        }

        @Test
        @DisplayName("19. hashCode")
        void test19_hashCode() {
            // TODO: hashCode() 테스트
        }

        @Test
        @DisplayName("20. toString")
        void test20_toString() {
            // TODO: toString() 테스트
        }

        @Test
        @DisplayName("21. 복사 생성자")
        void test21_copyConstructor() {
            // TODO: 복사 생성자 테스트
        }

        @Test
        @DisplayName("22. clear")
        void test22_clear() {
            // TODO: clear() 테스트
        }

        @Test
        @DisplayName("23. entrySet")
        void test23_entrySet() {
            // TODO: Map의 entrySet 테스트
        }

        @Test
        @DisplayName("24. keySet")
        void test24_keySet() {
            // TODO: Map의 keySet 테스트
        }

        @Test
        @DisplayName("25. values")
        void test25_values() {
            // TODO: Map의 values 테스트
        }

        @Test
        @DisplayName("26. 대용량 테스트")
        void test26_largeScale() {
            // TODO: 대용량 OOP 테스트
        }

        @Test
        @DisplayName("27. Red-Black 속성 검증")
        void test27_properties() {
            // TODO: 속성 검증 테스트
        }

        @Test
        @DisplayName("28. 예외 처리")
        void test28_exceptionHandling() {
            // TODO: 예외 테스트
        }

        @Test
        @DisplayName("29. null 처리")
        void test29_nullHandling() {
            // TODO: null 처리 테스트
        }

        @Test
        @DisplayName("30. descendingMap")
        void test30_descendingMap() {
            // TODO: 역순 뷰 테스트
        }
    }
}
