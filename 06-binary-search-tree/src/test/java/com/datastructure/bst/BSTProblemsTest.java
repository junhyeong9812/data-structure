package com.datastructure.bst;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class BSTProblemsTest {

    private static SortedMap<Integer, Integer> of(int... keys) {
        SortedMap<Integer, Integer> map = new BinarySearchTree<>();
        for (int k : keys) map.put(k, k * 10);
        return map;
    }

    @Nested
    @DisplayName("문제 1. 가장 가까운 키")
    class ClosestKey {

        @Test
        void findsClosest() {
            SortedMap<Integer, Integer> map = of(1, 5, 9);
            assertEquals(5, BSTProblems.closestKey(map, 6));
            assertEquals(5, BSTProblems.closestKey(map, 4));
            assertEquals(9, BSTProblems.closestKey(map, 100));
            assertEquals(1, BSTProblems.closestKey(map, -100));
            assertEquals(5, BSTProblems.closestKey(map, 5), "정확히 있으면 그것");
        }

        @Test
        @DisplayName("차이가 같으면 작은 쪽")
        void prefersSmallerOnTie() {
            assertEquals(5, BSTProblems.closestKey(of(1, 5, 9), 7), "5 와 9 모두 차이 2");
            assertEquals(1, BSTProblems.closestKey(of(1, 3), 2), "1 과 3 모두 차이 1");
        }

        @Test
        void emptyThrows() {
            assertThrows(NoSuchElementException.class,
                () -> BSTProblems.closestKey(new BinarySearchTree<Integer, Integer>(), 1));
        }

        @Test
        @DisplayName("10만 개에서 10만 번 물어도 5초 안에 (O(n) 은 통과 못 한다)")
        void mustBeLogarithmic() {
            SortedMap<Integer, Integer> map = new BinarySearchTree<>();
            final int n = 100_000;
            // 흩어진 순서로 넣어 트리가 균형 잡히게 한다. 치우친 트리는 이 구조의 한계지 알고리즘 문제가 아니다.
            // 곱수가 크면 int 를 넘쳐 키가 겹친다. 7919 는 100000 과 서로소라 전 범위를 한 번씩 훑는다.
            for (int i = 0; i < n; i++) map.put((i * 7919) % n, i);

            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                long sum = 0;
                for (int q = 0; q < n; q++) sum += BSTProblems.closestKey(map, q);
                assertEquals((long) n * (n - 1) / 2, sum, "각 질의의 답은 자기 자신이어야 한다");
            }, "전부 훑어서 최소 차이를 찾으면 O(n) 이라 여기서 막힌다. floor 와 ceiling 을 써라.");
        }
    }

    @Nested
    @DisplayName("문제 2. 구간 합")
    class RangeSum {

        @Test
        void sumsRange() {
            SortedMap<Integer, Integer> map = of(1, 3, 5, 7, 9);
            assertEquals(10 + 30 + 50, BSTProblems.rangeSum(map, 1, 5));
            assertEquals(30 + 50 + 70, BSTProblems.rangeSum(map, 2, 8), "경계에 없는 값도 된다");
            assertEquals(0, BSTProblems.rangeSum(map, 100, 200), "범위에 아무것도 없다");
            assertEquals(0, BSTProblems.rangeSum(map, 7, 3), "뒤집힌 범위");
        }

        @Test
        @DisplayName("전체 범위면 전부 더한다")
        void sumsAll() {
            assertEquals(10 + 30 + 50 + 70 + 90,
                BSTProblems.rangeSum(of(1, 3, 5, 7, 9), -100, 100));
        }
    }

    @Nested
    @DisplayName("문제 3. k 번째로 작은 키")
    class KthSmallest {

        @Test
        void findsKth() {
            SortedMap<Integer, Integer> map = of(5, 3, 8, 2, 9);
            assertEquals(2, BSTProblems.kthSmallest(map, 1));
            assertEquals(3, BSTProblems.kthSmallest(map, 2));
            assertEquals(9, BSTProblems.kthSmallest(map, 5));
        }

        @Test
        void rejectsOutOfRange() {
            SortedMap<Integer, Integer> map = of(1, 2, 3);
            assertThrows(IndexOutOfBoundsException.class, () -> BSTProblems.kthSmallest(map, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> BSTProblems.kthSmallest(map, 4));
            assertThrows(IndexOutOfBoundsException.class,
                () -> BSTProblems.kthSmallest(new BinarySearchTree<Integer, Integer>(), 1));
        }
    }
}
