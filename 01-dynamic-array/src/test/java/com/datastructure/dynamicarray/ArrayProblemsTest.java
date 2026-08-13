package com.datastructure.dynamicarray;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ArrayProblemsTest {

    private static DynamicArray<Integer> of(int... values) {
        DynamicArray<Integer> array = new DynamicArray<>();
        for (int v : values) array.add(v);
        return array;
    }

    private static void assertContents(DynamicArray<Integer> array, int... expected) {
        assertEquals(expected.length, array.size(), "크기가 다르다: " + array);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], array.get(i), "인덱스 " + i + " 가 다르다: " + array);
        }
    }

    @Nested
    @DisplayName("문제 1. 정렬된 배열 중복 제거")
    class RemoveDuplicates {

        @Test
        void removesAdjacentDuplicates() {
            DynamicArray<Integer> array = of(1, 1, 2, 2, 2, 3);
            assertEquals(3, ArrayProblems.removeDuplicatesSorted(array));
            assertContents(array, 1, 2, 3);
        }

        @Test
        void keepsAlreadyUniqueArray() {
            DynamicArray<Integer> array = of(1, 2, 3);
            assertEquals(3, ArrayProblems.removeDuplicatesSorted(array));
            assertContents(array, 1, 2, 3);
        }

        @Test
        void handlesAllSame() {
            DynamicArray<Integer> array = of(7, 7, 7, 7);
            assertEquals(1, ArrayProblems.removeDuplicatesSorted(array));
            assertContents(array, 7);
        }

        @Test
        void handlesEmpty() {
            DynamicArray<Integer> array = of();
            assertEquals(0, ArrayProblems.removeDuplicatesSorted(array));
            assertEquals(0, array.size());
        }
    }

    @Nested
    @DisplayName("문제 2. k 칸 회전")
    class Rotate {

        @Test
        void rotatesRight() {
            DynamicArray<Integer> array = of(1, 2, 3, 4, 5);
            ArrayProblems.rotate(array, 2);
            assertContents(array, 4, 5, 1, 2, 3);
        }

        @Test
        void handlesKLargerThanSize() {
            DynamicArray<Integer> array = of(1, 2, 3);
            ArrayProblems.rotate(array, 7);   // 7 % 3 == 1
            assertContents(array, 3, 1, 2);
        }

        @Test
        void handlesNegativeK() {
            DynamicArray<Integer> array = of(1, 2, 3, 4, 5);
            ArrayProblems.rotate(array, -1);  // 왼쪽으로 한 칸
            assertContents(array, 2, 3, 4, 5, 1);
        }

        @Test
        void handlesZeroAndEmpty() {
            DynamicArray<Integer> array = of(1, 2, 3);
            ArrayProblems.rotate(array, 0);
            assertContents(array, 1, 2, 3);

            DynamicArray<Integer> empty = of();
            assertDoesNotThrow(() -> ArrayProblems.rotate(empty, 3));
        }
    }

    @Nested
    @DisplayName("문제 3. 정렬 유지 삽입")
    class InsertSorted {

        @Test
        void insertsInMiddle() {
            DynamicArray<Integer> array = of(1, 3, 5);
            assertEquals(2, ArrayProblems.insertSorted(array, 4));
            assertContents(array, 1, 3, 4, 5);
        }

        @Test
        void insertsAtEnds() {
            DynamicArray<Integer> array = of(2, 4);
            assertEquals(0, ArrayProblems.insertSorted(array, 1));
            assertContents(array, 1, 2, 4);
            assertEquals(3, ArrayProblems.insertSorted(array, 9));
            assertContents(array, 1, 2, 4, 9);
        }

        @Test
        @DisplayName("같은 값이 있으면 그 뒤에 넣는다")
        void insertsAfterEqualValues() {
            DynamicArray<Integer> array = of(1, 3, 3, 5);
            assertEquals(3, ArrayProblems.insertSorted(array, 3));
            assertContents(array, 1, 3, 3, 3, 5);
        }

        @Test
        void insertsIntoEmpty() {
            DynamicArray<Integer> array = of();
            assertEquals(0, ArrayProblems.insertSorted(array, 42));
            assertContents(array, 42);
        }
    }

    @Nested
    @DisplayName("문제 4. 조건부 일괄 삭제")
    class RemoveAllIf {

        @Test
        void removesMatching() {
            DynamicArray<Integer> array = of(1, 2, 3, 4, 5, 6);
            assertEquals(3, ArrayProblems.removeAllIf(array, v -> v % 2 == 0));
            assertContents(array, 1, 3, 5);
        }

        @Test
        void removesAll() {
            DynamicArray<Integer> array = of(2, 4, 6);
            assertEquals(3, ArrayProblems.removeAllIf(array, v -> v % 2 == 0));
            assertEquals(0, array.size());
        }

        @Test
        void removesNone() {
            DynamicArray<Integer> array = of(1, 3, 5);
            assertEquals(0, ArrayProblems.removeAllIf(array, v -> v % 2 == 0));
            assertContents(array, 1, 3, 5);
        }

        @Test
        @DisplayName("100만 건에서 5초 안에 끝나야 한다 (O(n^2) 구현은 통과 못 한다)")
        void mustBeLinear() {
            final int n = 1_000_000;
            // 용량을 미리 잡아 확장이 일어나지 않게 한다.
            // 확장 전략이 잘못된 구현에서 이 준비 구간이 O(n^2) 이 되면
            // removeAllIf 를 재보기도 전에 테스트가 멈춰버린다. 그건 용량 테스트가 잡을 일이다.
            DynamicArray<Integer> array = new DynamicArray<>(n);
            for (int i = 0; i < n; i++) array.add(i);

            // 배열을 채우는 시간은 측정에서 뺀다. removeAllIf 호출만 잰다.
            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                int removed = ArrayProblems.removeAllIf(array, v -> v % 2 == 0);
                assertEquals(n / 2, removed);
                assertEquals(n / 2, array.size());

                // 크기만 맞고 내용이 어긋나는 구현을 거른다. 남은 것은 홀수 전부여야 한다.
                long expectedSum = (long) (n / 2) * (n / 2);   // 1+3+...+(n-1)
                long actualSum = 0;
                for (int i = 0; i < array.size(); i++) actualSum += array.get(i);
                assertEquals(expectedSum, actualSum, "남은 원소가 홀수 전부가 아니다");
            }, "remove(i) 를 반복 호출하면 O(n^2) 이라 여기서 막힌다.");
        }
    }

    @Nested
    @DisplayName("문제 5. 정렬된 두 배열 병합")
    class Merge {

        @Test
        void mergesTwoSorted() {
            DynamicArray<Integer> merged = ArrayProblems.merge(of(1, 3, 5), of(2, 3, 6));
            assertContents(merged, 1, 2, 3, 3, 5, 6);
        }

        @Test
        void handlesEmptySide() {
            assertContents(ArrayProblems.merge(of(), of(1, 2)), 1, 2);
            assertContents(ArrayProblems.merge(of(1, 2), of()), 1, 2);
            assertEquals(0, ArrayProblems.merge(of(), of()).size());
        }

        @Test
        void handlesDifferentLengths() {
            assertContents(ArrayProblems.merge(of(1), of(2, 3, 4, 5)), 1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("원본은 변하지 않는다")
        void doesNotMutateInputs() {
            DynamicArray<Integer> a = of(1, 3);
            DynamicArray<Integer> b = of(2);
            ArrayProblems.merge(a, b);

            assertContents(a, 1, 3);
            assertContents(b, 2);
        }
    }
}
