package com.datastructure.heap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class HeapProblemsTest {

    @Nested
    @DisplayName("문제 1. 힙 정렬")
    class HeapSort {

        private int[] sorted(int... values) {
            int[] copy = values.clone();
            HeapProblems.heapSort(copy, new BinaryHeap<>(Comparator.naturalOrder()));
            return copy;
        }

        @Test
        void sortsAscending() {
            assertArrayEquals(new int[]{1, 2, 3, 5, 9}, sorted(5, 3, 9, 1, 2));
            assertArrayEquals(new int[]{1, 2, 3}, sorted(1, 2, 3), "이미 정렬된 것도");
            assertArrayEquals(new int[]{1, 2, 3}, sorted(3, 2, 1), "역순도");
        }

        @Test
        void handlesEdges() {
            assertArrayEquals(new int[]{}, sorted());
            assertArrayEquals(new int[]{7}, sorted(7));
            assertArrayEquals(new int[]{2, 2, 2}, sorted(2, 2, 2), "같은 값만");
            assertArrayEquals(new int[]{-5, -1, 0, 3}, sorted(3, -1, -5, 0), "음수도");
        }

        @Test
        @DisplayName("원본 배열을 직접 고친다")
        void sortsInPlace() {
            int[] values = {3, 1, 2};
            HeapProblems.heapSort(values, new BinaryHeap<>(Comparator.naturalOrder()));
            assertArrayEquals(new int[]{1, 2, 3}, values);
        }
    }

    @Nested
    @DisplayName("문제 2. k 번째로 큰 값")
    class KthLargestTest {

        private KthLargest of(int k) {
            // 어떤 힙을 넘겨야 하는지가 이 문제의 핵심이다
            return new KthLargest(k, new MinHeap<Integer>());
        }

        @Test
        void tracksKthLargest() {
            KthLargest counter = of(3);
            assertEquals(4, counter.add(4), "아직 3개가 안 모였으면 가장 작은 값");
            assertEquals(4, counter.add(5));
            assertEquals(4, counter.add(8), "4, 5, 8 중 3번째로 큰 것은 4");
            assertEquals(5, counter.add(9), "4, 5, 8, 9 중 3번째는 5");
            assertEquals(5, counter.add(4), "4, 4, 5, 8, 9 중 3번째는 5");
        }

        @Test
        @DisplayName("k 개만 들고 있는다")
        void keepsOnlyK() {
            KthLargest counter = of(2);
            for (int i = 1; i <= 100; i++) counter.add(i);
            assertEquals(2, counter.size(), "전부 들고 있으면 메모리가 낭비된다");
            assertEquals(99, counter.add(0), "99, 100 중 2번째는 99");
        }

        @Test
        void rejectsBadArguments() {
            assertThrows(IllegalArgumentException.class, () -> new KthLargest(0, new MinHeap<Integer>()));
            MinHeap<Integer> dirty = new MinHeap<>();
            dirty.insert(1);
            assertThrows(IllegalArgumentException.class, () -> new KthLargest(1, dirty));
        }

        @Test
        @DisplayName("20만 건을 5초 안에")
        void mustBeLogarithmic() {
            KthLargest counter = of(10);
            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                for (int i = 0; i < 200_000; i++) counter.add(i);
                assertEquals(10, counter.size());
                assertEquals(199_990, counter.add(0), "10번째로 큰 값");
            });
        }
    }

    @Nested
    @DisplayName("문제 3. 중앙값 찾기")
    class MedianFinderTest {

        /** 작은 절반은 최대 힙, 큰 절반은 최소 힙이어야 한다. 그것도 이 문제의 답이다. */
        private MedianFinder create() {
            return new MedianFinder(new MaxHeap<Integer>(), new MinHeap<Integer>());
        }

        @Test
        void tracksMedian() {
            MedianFinder finder = create();
            finder.add(1);
            assertEquals(1.0, finder.median(), 1e-9);
            finder.add(2);
            assertEquals(1.5, finder.median(), 1e-9, "짝수면 가운데 두 값의 평균");
            finder.add(3);
            assertEquals(2.0, finder.median(), 1e-9);
        }

        @Test
        @DisplayName("순서에 상관없이 같은 답이 나온다")
        void orderIndependent() {
            MedianFinder ascending = create();
            for (int v : new int[]{1, 2, 3, 4, 5}) ascending.add(v);

            MedianFinder shuffled = create();
            for (int v : new int[]{5, 1, 4, 2, 3}) shuffled.add(v);

            assertEquals(3.0, ascending.median(), 1e-9);
            assertEquals(3.0, shuffled.median(), 1e-9);
        }

        @Test
        @DisplayName("정수 나눗셈으로 평균을 내면 틀린다")
        void averageIsNotIntegerDivision() {
            MedianFinder finder = create();
            finder.add(1);
            finder.add(2);
            assertEquals(1.5, finder.median(), 1e-9, "1 이 아니라 1.5 다");
        }

        @Test
        void emptyThrows() {
            assertThrows(NoSuchElementException.class, () -> create().median());
        }

        @Test
        void rejectsNonEmptyHeaps() {
            MaxHeap<Integer> dirty = new MaxHeap<>();
            dirty.insert(1);
            assertThrows(IllegalArgumentException.class,
                () -> new MedianFinder(dirty, new MinHeap<Integer>()));
        }

        @Test
        @DisplayName("10만 건을 5초 안에 (매번 정렬하면 통과 못 한다)")
        void mustBeLogarithmic() {
            MedianFinder finder = create();
            final int n = 100_000;
            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                for (int i = 0; i < n; i++) finder.add((i * 7919) % n);
                assertEquals((n - 1) / 2.0, finder.median(), 1e-9);
            }, "매번 정렬하거나 정렬 삽입하면 O(n^2) 이라 여기서 막힌다.");
        }
    }
}
