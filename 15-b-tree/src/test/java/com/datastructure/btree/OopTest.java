package com.datastructure.btree;

import com.datastructure.btree.oop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DisplayName("B-트리 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("BTreeImpl")
    class BTreeImplTest {

        private SearchTree<Integer, String> tree;

        @BeforeEach
        void setUp() {
            tree = new BTreeImpl<>(2);
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
            // tree.put(10, "ten");
            // assertThat(tree.containsKey(10)).isTrue();
            // assertThat(tree.containsKey(20)).isFalse();
        }

        @Test
        @DisplayName("03. remove")
        void test03_remove() {
            // TODO: remove() 테스트
            // tree.put(10, "ten");
            // tree.remove(10);
            // assertThat(tree.containsKey(10)).isFalse();
        }

        @Test
        @DisplayName("04. 값 업데이트")
        void test04_updateValue() {
            // TODO: 값 업데이트 테스트
            // tree.put(10, "ten");
            // tree.put(10, "TEN");
            // assertThat(tree.get(10)).isEqualTo(Optional.of("TEN"));
        }

        @Test
        @DisplayName("05. size/isEmpty")
        void test05_sizeIsEmpty() {
            // TODO: size(), isEmpty() 테스트
            // assertThat(tree.isEmpty()).isTrue();
            // tree.put(10, "ten");
            // assertThat(tree.isEmpty()).isFalse();
            // assertThat(tree.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("제네릭 타입")
    class GenericTypeTest {

        @Test
        @DisplayName("06. String 키")
        void test06_stringKey() {
            // TODO: String 키 테스트
            // SearchTree<String, Integer> tree = new BTreeImpl<>(3);
            // tree.put("apple", 1);
            // tree.put("banana", 2);
            // assertThat(tree.get("apple")).isEqualTo(Optional.of(1));
        }

        @Test
        @DisplayName("07. 사용자 정의 Comparable")
        void test07_customComparable() {
            // TODO: 사용자 정의 타입 테스트
        }
    }

    @Nested
    @DisplayName("SearchTree 인터페이스")
    class SearchTreeInterfaceTest {

        @Test
        @DisplayName("08. 다형성")
        void test08_polymorphism() {
            // TODO: 다형성 테스트
            // SearchTree<Integer, String> tree = new BTreeImpl<>(2);
            // tree.put(1, "one");
        }

        @Test
        @DisplayName("09. keys()")
        void test09_keys() {
            // TODO: 모든 키 반환 테스트
            // Iterable<K> keys = tree.keys();
        }

        @Test
        @DisplayName("10. values()")
        void test10_values() {
            // TODO: 모든 값 반환 테스트
        }
    }

    @Nested
    @DisplayName("B+ 트리")
    class BPlusTreeTest {

        @Test
        @DisplayName("11. 범위 쿼리")
        void test11_rangeQuery() {
            // TODO: B+ 트리 범위 쿼리 테스트
            // BPlusTreeImpl<Integer, String> tree = new BPlusTreeImpl<>(3);
            // tree.put(1, "a"); tree.put(5, "e"); tree.put(10, "j");
            // assertThat(tree.range(3, 8)).containsExactly("e");
        }

        @Test
        @DisplayName("12. 리프 연결 확인")
        void test12_leafLinks() {
            // TODO: B+ 트리 리프 연결 테스트
        }
    }

    @Nested
    @DisplayName("Iterable 지원")
    class IterableTest {

        @Test
        @DisplayName("13. for-each 순회")
        void test13_forEach() {
            // TODO: Iterable 구현 테스트
            // for (Entry<Integer, String> entry : tree) { ... }
        }

        @Test
        @DisplayName("14. stream()")
        void test14_stream() {
            // TODO: Stream API 테스트
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("15. equals")
        void test15_equals() {
            // TODO: equals() 테스트
        }

        @Test
        @DisplayName("16. hashCode")
        void test16_hashCode() {
            // TODO: hashCode() 테스트
        }

        @Test
        @DisplayName("17. toString")
        void test17_toString() {
            // TODO: toString() 테스트
        }

        @Test
        @DisplayName("18. 복사 생성자")
        void test18_copyConstructor() {
            // TODO: 복사 생성자 테스트
        }

        @Test
        @DisplayName("19. clear")
        void test19_clear() {
            // TODO: clear() 테스트
        }

        @Test
        @DisplayName("20. minKey/maxKey")
        void test20_minMaxKey() {
            // TODO: 최소/최대 키 테스트
        }

        @Test
        @DisplayName("21. getOrDefault")
        void test21_getOrDefault() {
            // TODO: getOrDefault() 테스트
        }

        @Test
        @DisplayName("22. putIfAbsent")
        void test22_putIfAbsent() {
            // TODO: putIfAbsent() 테스트
        }

        @Test
        @DisplayName("23. computeIfAbsent")
        void test23_computeIfAbsent() {
            // TODO: computeIfAbsent() 테스트
        }

        @Test
        @DisplayName("24. floorKey/ceilingKey")
        void test24_floorCeiling() {
            // TODO: floor/ceiling 테스트
        }

        @Test
        @DisplayName("25. subTree")
        void test25_subTree() {
            // TODO: 범위 뷰 테스트
        }

        @Test
        @DisplayName("26. 대용량 테스트")
        void test26_largeScale() {
            // TODO: 대용량 OOP 테스트
        }

        @Test
        @DisplayName("27. null 값 처리")
        void test27_nullValue() {
            // TODO: null 값 테스트
        }

        @Test
        @DisplayName("28. 예외 처리")
        void test28_exceptionHandling() {
            // TODO: 예외 테스트
        }

        @Test
        @DisplayName("29. 스레드 안전성")
        void test29_threadSafety() {
            // TODO: 스레드 안전성 테스트 (선택)
        }

        @Test
        @DisplayName("30. 직렬화")
        void test30_serialization() {
            // TODO: 직렬화 테스트 (선택)
        }
    }
}
