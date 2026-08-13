package com.datastructure.skiplist;

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

@DisplayName("SkipListSet: 맵을 집합으로 쓰기")
class SkipListSetTest {

    private OrderedSet<Integer> set(int... keys) {
        OrderedSet<Integer> s = new SkipListSet<>(20260813L);
        for (int k : keys) {
            s.add(k);
        }
        return s;
    }

    @Nested
    @DisplayName("add 와 remove 가 변화 여부를 알려준다")
    class ReturnValues {

        @Test
        @DisplayName("새로 들어가면 true, 이미 있으면 false")
        void addTellsWhetherNew() {
            OrderedSet<Integer> s = new SkipListSet<>(1L);
            assertTrue(s.add(5), "처음이면 true");
            assertFalse(s.add(5), "두 번째는 false");
            assertEquals(1, s.size(), "크기는 안 는다");
        }

        @Test
        @DisplayName("지웠으면 true, 없었으면 false")
        void removeTellsWhetherPresent() {
            OrderedSet<Integer> s = set(1, 2, 3);
            assertTrue(s.remove(2));
            assertFalse(s.remove(2));
            assertEquals(2, s.size());
        }

        @Test
        @DisplayName("별도 조회 없이 put 의 반환값으로 안다")
        void noExtraLookup() {
            // containsKey 로 먼저 확인하면 같은 길을 두 번 걷는다.
            // 계약을 "옛 값을 준다"로 설계해두면 이 재사용이 공짜다.
            OrderedSet<Integer> s = new SkipListSet<>(1L);
            for (int i = 0; i < 100; i++) {
                assertTrue(s.add(i));
            }
            for (int i = 0; i < 100; i++) {
                assertFalse(s.add(i));
            }
            assertEquals(100, s.size());
        }
    }

    @Nested
    @DisplayName("맵의 기능이 그대로 온다")
    class InheritedBehaviour {

        @Test
        @DisplayName("정렬, first/last, floor/ceiling, range")
        void everything() {
            OrderedSet<Integer> s = set(30, 10, 40, 20);
            assertEquals(List.of(10, 20, 30, 40), s.toList());
            assertEquals(10, s.first());
            assertEquals(40, s.last());
            assertEquals(20, s.floor(25));
            assertEquals(30, s.ceiling(25));
            assertEquals(List.of(20, 30), s.range(20, 30));
            assertNull(s.floor(5));
            assertNull(s.ceiling(50));
        }

        @Test
        @DisplayName("빈 집합")
        void empty() {
            OrderedSet<Integer> s = new SkipListSet<>(1L);
            assertTrue(s.isEmpty());
            assertEquals(0, s.size());
            assertFalse(s.contains(1));
            assertNull(s.first());
            assertNull(s.last());
            assertEquals(List.of(), s.toList());
        }

        @Test
        @DisplayName("clear")
        void clear() {
            OrderedSet<Integer> s = set(1, 2, 3);
            s.clear();
            assertEquals(0, s.size());
            assertEquals(List.of(), s.toList());
            assertTrue(s.add(1), "비운 뒤엔 다시 새 원소다");
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("TreeSet 과 계속 같다")
        void matchesTreeSet() {
            Random rnd = new Random(4242L);
            OrderedSet<Integer> s = new SkipListSet<>(77L);
            TreeSet<Integer> ref = new TreeSet<>();
            for (int step = 0; step < 6000; step++) {
                int key = rnd.nextInt(200);
                if (rnd.nextInt(3) == 0) {
                    assertEquals(ref.remove(key), s.remove(key), "remove step " + step);
                } else {
                    assertEquals(ref.add(key), s.add(key), "add step " + step);
                }
                assertEquals(ref.size(), s.size(), "step " + step);
            }
            assertEquals(new ArrayList<>(ref), s.toList());
            for (int k = -5; k < 205; k += 3) {
                assertEquals(ref.floor(k), s.floor(k), "floor(" + k + ")");
                assertEquals(ref.ceiling(k), s.ceiling(k), "ceiling(" + k + ")");
            }
        }
    }
}
