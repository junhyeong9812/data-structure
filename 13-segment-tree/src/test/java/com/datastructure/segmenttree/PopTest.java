package com.datastructure.segmenttree;

import com.datastructure.segmenttree.pop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("구간 트리 - POP 구현 테스트")
class PopTest {

    @Nested
    @DisplayName("구간 합 SegmentTree")
    class SumSegmentTreeTest {

        private SegmentTree tree;

        @BeforeEach
        void setUp() {
            int[] arr = {1, 3, 5, 7, 9, 11};
            tree = new SegmentTree(arr);
        }

        @Test
        @DisplayName("01. 전체 구간 합")
        void test01_fullRangeSum() {
            // TODO: query() 구현 후 테스트
            // assertThat(tree.query(0, 5)).isEqualTo(36);
        }

        @Test
        @DisplayName("02. 부분 구간 합")
        void test02_partialRangeSum() {
            // TODO: 부분 구간 테스트
            // assertThat(tree.query(1, 3)).isEqualTo(15);  // 3+5+7
            // assertThat(tree.query(2, 4)).isEqualTo(21);  // 5+7+9
        }

        @Test
        @DisplayName("03. 단일 원소 쿼리")
        void test03_singleElementQuery() {
            // TODO: 단일 원소 테스트
            // assertThat(tree.query(2, 2)).isEqualTo(5);
        }

        @Test
        @DisplayName("04. 점 업데이트")
        void test04_pointUpdate() {
            // TODO: update() 구현 후 테스트
            // tree.update(1, 10);  // arr[1] = 10
            // assertThat(tree.query(1, 3)).isEqualTo(22);  // 10+5+7
        }

        @Test
        @DisplayName("05. 여러 번 업데이트")
        void test05_multipleUpdates() {
            // TODO: 여러 번 업데이트 테스트
            // tree.update(0, 10);
            // tree.update(5, 20);
            // assertThat(tree.query(0, 5)).isEqualTo(54);
        }
    }

    @Nested
    @DisplayName("구간 최소값 MinSegmentTree")
    class MinSegmentTreeTest {

        private MinSegmentTree tree;

        @BeforeEach
        void setUp() {
            int[] arr = {5, 2, 8, 1, 9, 3};
            tree = new MinSegmentTree(arr);
        }

        @Test
        @DisplayName("06. 전체 구간 최소값")
        void test06_fullRangeMin() {
            // TODO: query() 구현 후 테스트
            // assertThat(tree.query(0, 5)).isEqualTo(1);
        }

        @Test
        @DisplayName("07. 부분 구간 최소값")
        void test07_partialRangeMin() {
            // TODO: 부분 구간 테스트
            // assertThat(tree.query(0, 2)).isEqualTo(2);  // min(5,2,8)
            // assertThat(tree.query(3, 5)).isEqualTo(1);  // min(1,9,3)
        }

        @Test
        @DisplayName("08. 업데이트 후 최소값")
        void test08_updateMin() {
            // TODO: 업데이트 후 테스트
            // tree.update(3, 10);  // 1 → 10
            // assertThat(tree.query(0, 5)).isEqualTo(2);  // 새 최소값
        }
    }

    @Nested
    @DisplayName("지연 전파 LazySegmentTree")
    class LazySegmentTreeTest {

        private LazySegmentTree tree;

        @BeforeEach
        void setUp() {
            int[] arr = {1, 2, 3, 4, 5};
            tree = new LazySegmentTree(arr);
        }

        @Test
        @DisplayName("09. 구간 업데이트 - 전체에 더하기")
        void test09_rangeUpdateAdd() {
            // TODO: updateRange() 구현 후 테스트
            // tree.updateRange(0, 4, 10);  // 전체에 10 더하기
            // assertThat(tree.query(0, 4)).isEqualTo(65);  // 11+12+13+14+15
        }

        @Test
        @DisplayName("10. 구간 업데이트 - 부분 더하기")
        void test10_partialRangeUpdate() {
            // TODO: 부분 구간 업데이트 테스트
            // tree.updateRange(1, 3, 10);  // [1,3]에 10 더하기
            // assertThat(tree.query(0, 4)).isEqualTo(45);  // 1+12+13+14+5
        }

        @Test
        @DisplayName("11. 여러 구간 업데이트")
        void test11_multipleRangeUpdates() {
            // TODO: 여러 구간 업데이트 테스트
            // tree.updateRange(0, 2, 5);
            // tree.updateRange(2, 4, 3);
            // assertThat(tree.query(0, 4)).isEqualTo(33);
        }

        @Test
        @DisplayName("12. 업데이트와 쿼리 혼합")
        void test12_mixedOperations() {
            // TODO: 혼합 연산 테스트
            // tree.updateRange(0, 2, 10);
            // assertThat(tree.query(1, 3)).isEqualTo(29);  // 12+13+4
            // tree.updateRange(2, 4, 5);
            // assertThat(tree.query(0, 4)).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("엣지 케이스")
    class EdgeCases {

        @Test
        @DisplayName("13. 크기 1 배열")
        void test13_singleElement() {
            // TODO: 크기 1 테스트
            // int[] arr = {42};
            // SegmentTree tree = new SegmentTree(arr);
            // assertThat(tree.query(0, 0)).isEqualTo(42);
        }

        @Test
        @DisplayName("14. 크기 2 배열")
        void test14_twoElements() {
            // TODO: 크기 2 테스트
            // int[] arr = {10, 20};
            // SegmentTree tree = new SegmentTree(arr);
            // assertThat(tree.query(0, 1)).isEqualTo(30);
        }

        @Test
        @DisplayName("15. 2의 거듭제곱 크기")
        void test15_powerOfTwo() {
            // TODO: 2^n 크기 테스트
            // int[] arr = {1, 2, 3, 4, 5, 6, 7, 8};
            // SegmentTree tree = new SegmentTree(arr);
            // assertThat(tree.query(0, 7)).isEqualTo(36);
        }

        @Test
        @DisplayName("16. 음수 값")
        void test16_negativeValues() {
            // TODO: 음수 값 테스트
            // int[] arr = {-1, -3, -5, -7};
            // SegmentTree tree = new SegmentTree(arr);
            // assertThat(tree.query(0, 3)).isEqualTo(-16);
        }

        @Test
        @DisplayName("17. 큰 값")
        void test17_largeValues() {
            // TODO: 큰 값 테스트 (오버플로우 주의)
            // int[] arr = {Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2};
            // SegmentTree tree = new SegmentTree(arr);
        }
    }

    @Nested
    @DisplayName("구간 최대값")
    class MaxSegmentTreeTest {

        @Test
        @DisplayName("18. 구간 최대값 쿼리")
        void test18_rangeMax() {
            // TODO: MaxSegmentTree 또는 옵션 구현 후 테스트
            // int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
            // MaxSegmentTree tree = new MaxSegmentTree(arr);
            // assertThat(tree.query(0, 7)).isEqualTo(9);
            // assertThat(tree.query(2, 5)).isEqualTo(9);
        }
    }

    @Nested
    @DisplayName("구간 GCD")
    class GcdSegmentTreeTest {

        @Test
        @DisplayName("19. 구간 GCD 쿼리")
        void test19_rangeGcd() {
            // TODO: GCD 세그먼트 트리 테스트
            // int[] arr = {12, 18, 24, 6};
            // GcdSegmentTree tree = new GcdSegmentTree(arr);
            // assertThat(tree.query(0, 3)).isEqualTo(6);
            // assertThat(tree.query(0, 2)).isEqualTo(6);  // gcd(12,18,24)
        }
    }

    @Nested
    @DisplayName("대용량 테스트")
    class LargeScaleTest {

        @Test
        @DisplayName("20. 10000개 원소")
        void test20_tenThousand() {
            // TODO: 대용량 테스트
            // int[] arr = new int[10000];
            // Arrays.fill(arr, 1);
            // SegmentTree tree = new SegmentTree(arr);
            // assertThat(tree.query(0, 9999)).isEqualTo(10000);
        }

        @Test
        @DisplayName("21. 많은 쿼리")
        void test21_manyQueries() {
            // TODO: 많은 쿼리 테스트
            // int[] arr = new int[1000];
            // for (int i = 0; i < 1000; i++) arr[i] = i;
            // SegmentTree tree = new SegmentTree(arr);
            // for (int i = 0; i < 10000; i++) {
            //     tree.query(0, 999);
            // }
        }

        @Test
        @DisplayName("22. 많은 업데이트")
        void test22_manyUpdates() {
            // TODO: 많은 업데이트 테스트
            // int[] arr = new int[1000];
            // SegmentTree tree = new SegmentTree(arr);
            // for (int i = 0; i < 10000; i++) {
            //     tree.update(i % 1000, i);
            // }
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("23. add 연산 (기존 값에 더하기)")
        void test23_addOperation() {
            // TODO: add() 구현 후 테스트
            // int[] arr = {1, 2, 3, 4, 5};
            // SegmentTree tree = new SegmentTree(arr);
            // tree.add(2, 10);  // arr[2] += 10
            // assertThat(tree.query(0, 4)).isEqualTo(25);
        }

        @Test
        @DisplayName("24. 구간 곱")
        void test24_rangeProduct() {
            // TODO: 구간 곱 테스트 (선택)
            // int[] arr = {2, 3, 4, 5};
            // ProductSegmentTree tree = new ProductSegmentTree(arr);
            // assertThat(tree.query(0, 3)).isEqualTo(120);
        }

        @Test
        @DisplayName("25. 동적 배열 확장")
        void test25_dynamicResize() {
            // TODO: 동적 크기 조정 테스트 (선택)
        }

        @Test
        @DisplayName("26. 빈 배열")
        void test26_emptyArray() {
            // TODO: 빈 배열 테스트
            // int[] arr = {};
            // assertThatThrownBy(() -> new SegmentTree(arr))
            //     .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("27. null 배열")
        void test27_nullArray() {
            // TODO: null 테스트
            // assertThatThrownBy(() -> new SegmentTree(null))
            //     .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("28. 인덱스 범위 초과")
        void test28_indexOutOfBounds() {
            // TODO: 범위 초과 테스트
            // int[] arr = {1, 2, 3};
            // SegmentTree tree = new SegmentTree(arr);
            // assertThatThrownBy(() -> tree.query(0, 5))
            //     .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("29. 잘못된 범위 (left > right)")
        void test29_invalidRange() {
            // TODO: 잘못된 범위 테스트
            // int[] arr = {1, 2, 3};
            // SegmentTree tree = new SegmentTree(arr);
            // 구현에 따라 0 반환 또는 예외
        }

        @Test
        @DisplayName("30. toString")
        void test30_toString() {
            // TODO: toString() 구현 후 테스트
            // int[] arr = {1, 2, 3};
            // SegmentTree tree = new SegmentTree(arr);
            // assertThat(tree.toString()).isNotEmpty();
        }
    }
}
