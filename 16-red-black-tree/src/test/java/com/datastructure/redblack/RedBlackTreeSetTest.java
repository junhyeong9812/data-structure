package com.datastructure.redblack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RedBlackTreeSet")
class RedBlackTreeSetTest {

    @Nested
    @DisplayName("맵을 집합으로")
    class AsSet {

        @Test
        @DisplayName("add 와 remove 가 변화 여부를 알려준다")
        void returnValues() {
            RedBlackTreeSet<Integer> s = new RedBlackTreeSet<>();
            assertTrue(s.add(5));
            assertFalse(s.add(5));
            assertEquals(1, s.size());
            assertTrue(s.remove(5));
            assertFalse(s.remove(5));
            assertTrue(s.isEmpty());
        }

        @Test
        @DisplayName("정렬과 탐색이 그대로 온다")
        void inherited() {
            RedBlackTreeSet<Integer> s = new RedBlackTreeSet<>();
            for (int k : new int[]{30, 10, 40, 20}) {
                s.add(k);
            }
            assertEquals(List.of(10, 20, 30, 40), s.toList());
            assertEquals(10, s.first());
            assertEquals(40, s.last());
            assertEquals(20, s.floor(25));
            assertEquals(30, s.ceiling(25));
            assertNull(s.floor(5));
            assertNull(s.ceiling(50));
            s.clear();
            assertEquals(List.of(), s.toList());
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("TreeSet 과 계속 같다")
        void matchesTreeSet() {
            Random rnd = new Random(2468L);
            RedBlackTreeSet<Integer> s = new RedBlackTreeSet<>();
            TreeSet<Integer> ref = new TreeSet<>();
            for (int step = 0; step < 8000; step++) {
                int key = rnd.nextInt(300);
                if (rnd.nextInt(3) == 0) {
                    assertEquals(ref.remove(key), s.remove(key), "remove step " + step);
                } else {
                    assertEquals(ref.add(key), s.add(key), "add step " + step);
                }
                assertEquals(ref.size(), s.size(), "step " + step);
            }
            assertEquals(new ArrayList<>(ref), s.toList());
        }
    }
}
