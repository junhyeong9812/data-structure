package com.datastructure.segmenttree;

import com.datastructure.segmenttree.oop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("구간 트리 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("SumSegmentTree")
    class SumSegmentTreeTest {

        private RangeQuery<Long> tree;

        @BeforeEach
        void setUp() {
            int[] arr = {1, 3, 5, 7, 9, 11};
            tree = new SumSegmentTree(arr);
        }

        @Test
        @DisplayName("01. query 인터페이스")
        void test01_queryInterface() {
            // TODO: query() 구현 후 테스트
            // assertThat(tree.query(0, 5)).isEqualTo(36L);
        }

        @Test
        @DisplayName("02. update 인터페이스")
        void test02_updateInterface() {
            // TODO: update() 구현 후 테스트
            // tree.update(1, 10);
            // assertThat(tree.query(0, 5)).isEqualTo(43L);
        }

        @Test
        @DisplayName("03. long 타입 반환")
        void test03_longType() {
            // TODO: long 타입 테스트
            // Long result = tree.query(0, 5);
            // assertThat(result).isInstanceOf(Long.class);
        }
    }

    @Nested
    @DisplayName("MinMaxSegmentTree")
    class MinMaxSegmentTreeTest {

        private MinMaxSegmentTree tree;

        @BeforeEach
        void setUp() {
            int[] arr = {5, 2, 8, 1, 9, 3};
            tree = new MinMaxSegmentTree(arr);
        }

        @Test
        @DisplayName("04. queryMin")
        void test04_queryMin() {
            // TODO: queryMin() 구현 후 테스트
            // assertThat(tree.queryMin(0, 5)).isEqualTo(1);
        }

        @Test
        @DisplayName("05. queryMax")
        void test05_queryMax() {
            // TODO: queryMax() 구현 후 테스트
            // assertThat(tree.queryMax(0, 5)).isEqualTo(9);
        }

        @Test
        @DisplayName("06. 업데이트 후 Min/Max")
        void test06_updateMinMax() {
            // TODO: 업데이트 후 테스트
            // tree.update(3, 10);
            // assertThat(tree.queryMin(0, 5)).isEqualTo(2);
            // assertThat(tree.queryMax(0, 5)).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("LazySegmentTreeImpl")
    class LazySegmentTreeImplTest {

        private RangeQuery<Long> tree;

        @BeforeEach
        void setUp() {
            int[] arr = {1, 2, 3, 4, 5};
            tree = new LazySegmentTreeImpl(arr);
        }

        @Test
        @DisplayName("07. 구간 업데이트")
        void test07_rangeUpdate() {
            // TODO: updateRange() 구현 후 테스트
            // ((LazySegmentTreeImpl) tree).updateRange(1, 3, 10);
            // assertThat(tree.query(0, 4)).isEqualTo(45L);
        }

        @Test
        @DisplayName("08. 지연 전파 후 쿼리")
        void test08_lazyQuery() {
            // TODO: 지연 전파 테스트
            // ((LazySegmentTreeImpl) tree).updateRange(0, 4, 5);
            // assertThat(tree.query(2, 3)).isEqualTo(17L);  // 8+9
        }
    }

    @Nested
    @DisplayName("GenericSegmentTree")
    class GenericSegmentTreeTest {

        @Test
        @DisplayName("09. 합 연산")
        void test09_sumOperation() {
            // TODO: GenericSegmentTree + 합 연산 테스트
            // Integer[] arr = {1, 2, 3, 4, 5};
            // GenericSegmentTree<Integer> tree = new GenericSegmentTree<>(
            //     arr, Integer::sum, 0);
            // assertThat(tree.query(0, 4)).isEqualTo(15);
        }

        @Test
        @DisplayName("10. 최소 연산")
        void test10_minOperation() {
            // TODO: GenericSegmentTree + 최소 연산 테스트
            // Integer[] arr = {5, 2, 8, 1, 9};
            // GenericSegmentTree<Integer> tree = new GenericSegmentTree<>(
            //     arr, Math::min, Integer.MAX_VALUE);
            // assertThat(tree.query(0, 4)).isEqualTo(1);
        }

        @Test
        @DisplayName("11. 최대 연산")
        void test11_maxOperation() {
            // TODO: GenericSegmentTree + 최대 연산 테스트
            // Integer[] arr = {5, 2, 8, 1, 9};
            // GenericSegmentTree<Integer> tree = new GenericSegmentTree<>(
            //     arr, Math::max, Integer.MIN_VALUE);
            // assertThat(tree.query(0, 4)).isEqualTo(9);
        }

        @Test
        @DisplayName("12. 사용자 정의 연산")
        void test12_customOperation() {
            // TODO: 사용자 정의 연산 테스트
            // GCD, XOR 등
        }
    }

    @Nested
    @DisplayName("RangeQuery 인터페이스")
    class RangeQueryInterfaceTest {

        @Test
        @DisplayName("13. 다형성")
        void test13_polymorphism() {
            // TODO: 다형성 테스트
            // int[] arr = {1, 2, 3, 4, 5};
            // RangeQuery<Long>[] trees = new RangeQuery[] {
            //     new SumSegmentTree(arr),
            //     new LazySegmentTreeImpl(arr)
            // };
            // for (RangeQuery<Long> tree : trees) {
            //     assertThat(tree.query(0, 4)).isEqualTo(15L);
            // }
        }

        @Test
        @DisplayName("14. build 메서드")
        void test14_build() {
            // TODO: build() 테스트
            // RangeQuery<Long> tree = new SumSegmentTree();
            // tree.build(new int[]{1, 2, 3});
            // assertThat(tree.query(0, 2)).isEqualTo(6L);
        }
    }

    @Nested
    @DisplayName("Builder 패턴")
    class BuilderPatternTest {

        @Test
        @DisplayName("15. Builder로 생성")
        void test15_builder() {
            // TODO: Builder 패턴 테스트 (선택)
            // SegmentTree tree = SegmentTree.builder()
            //     .data(new int[]{1, 2, 3})
            //     .operation(Operation.SUM)
            //     .build();
        }
    }

    @Nested
    @DisplayName("불변성")
    class ImmutabilityTest {

        @Test
        @DisplayName("16. 원본 배열 수정 영향 없음")
        void test16_originalArrayUnaffected() {
            // TODO: 불변성 테스트
            // int[] arr = {1, 2, 3};
            // RangeQuery<Long> tree = new SumSegmentTree(arr);
            // arr[0] = 100;  // 원본 수정
            // assertThat(tree.query(0, 2)).isEqualTo(6L);  // 영향 없음
        }
    }

    @Nested
    @DisplayName("통계 정보")
    class StatisticsTest {

        @Test
        @DisplayName("17. size")
        void test17_size() {
            // TODO: size() 테스트
            // int[] arr = {1, 2, 3, 4, 5};
            // SumSegmentTree tree = new SumSegmentTree(arr);
            // assertThat(tree.size()).isEqualTo(5);
        }

        @Test
        @DisplayName("18. treeSize")
        void test18_treeSize() {
            // TODO: 내부 트리 크기 테스트
            // assertThat(tree.treeSize()).isGreaterThan(5);
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("19. equals")
        void test19_equals() {
            // TODO: equals() 테스트
            // SumSegmentTree t1 = new SumSegmentTree(new int[]{1, 2, 3});
            // SumSegmentTree t2 = new SumSegmentTree(new int[]{1, 2, 3});
            // assertThat(t1).isEqualTo(t2);
        }

        @Test
        @DisplayName("20. hashCode")
        void test20_hashCode() {
            // TODO: hashCode() 테스트
            // assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        }

        @Test
        @DisplayName("21. toString")
        void test21_toString() {
            // TODO: toString() 테스트
            // assertThat(tree.toString()).contains("Segment");
        }

        @Test
        @DisplayName("22. getArray")
        void test22_getArray() {
            // TODO: 원본 배열 복사본 반환 테스트
            // int[] original = {1, 2, 3};
            // SumSegmentTree tree = new SumSegmentTree(original);
            // int[] copy = tree.getArray();
            // copy[0] = 100;
            // assertThat(tree.query(0, 0)).isEqualTo(1L);  // 영향 없음
        }

        @Test
        @DisplayName("23. Iterator")
        void test23_iterator() {
            // TODO: 원소 순회 테스트 (선택)
            // for (long val : tree) { ... }
        }

        @Test
        @DisplayName("24. Stream")
        void test24_stream() {
            // TODO: stream() 테스트 (선택)
            // long sum = tree.stream().mapToLong(x -> x).sum();
        }

        @Test
        @DisplayName("25. 복사 생성자")
        void test25_copyConstructor() {
            // TODO: 복사 생성자 테스트
            // SumSegmentTree original = new SumSegmentTree(new int[]{1, 2, 3});
            // SumSegmentTree copy = new SumSegmentTree(original);
            // copy.update(0, 100);
            // assertThat(original.query(0, 0)).isEqualTo(1L);
        }

        @Test
        @DisplayName("26. toArray")
        void test26_toArray() {
            // TODO: toArray() 테스트
            // int[] arr = tree.toArray();
            // assertThat(arr).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("27. 대용량 테스트")
        void test27_largeScale() {
            // TODO: 대용량 테스트
            // int[] arr = new int[100000];
            // Arrays.fill(arr, 1);
            // RangeQuery<Long> tree = new SumSegmentTree(arr);
            // assertThat(tree.query(0, 99999)).isEqualTo(100000L);
        }

        @Test
        @DisplayName("28. 연속 연산")
        void test28_consecutiveOperations() {
            // TODO: 연속 연산 테스트
            // int[] arr = {1, 2, 3, 4, 5};
            // SumSegmentTree tree = new SumSegmentTree(arr);
            // for (int i = 0; i < 1000; i++) {
            //     tree.update(i % 5, i);
            //     tree.query(0, 4);
            // }
        }

        @Test
        @DisplayName("29. 예외 처리")
        void test29_exceptionHandling() {
            // TODO: 예외 테스트
            // int[] arr = {1, 2, 3};
            // SumSegmentTree tree = new SumSegmentTree(arr);
            // assertThatThrownBy(() -> tree.query(-1, 2))
            //     .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("30. 문서화")
        void test30_documentation() {
            // TODO: Javadoc 확인 (수동)
        }
    }
}
