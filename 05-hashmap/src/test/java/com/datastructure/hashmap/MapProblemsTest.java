package com.datastructure.hashmap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/** 응용 문제는 세 구현 모두로 돌린다. Map 인터페이스만 쓰므로 결과가 같아야 한다. */
class MapProblemsTest {

    static Stream<Arguments> implementations() {
        return Stream.of(
            Arguments.of("Chaining", (Supplier<Map<Object, Object>>) ChainingHashMap::new),
            Arguments.of("LinearProbing", (Supplier<Map<Object, Object>>) LinearProbingHashMap::new),
            Arguments.of("LinkedHash", (Supplier<Map<Object, Object>>) LinkedHashMap::new)
        );
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> make(Supplier<Map<Object, Object>> f) {
        return (Map<K, V>) f.get();
    }

    @Nested
    @DisplayName("문제 1. 빈도 세기")
    class CountFrequencies {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.hashmap.MapProblemsTest#implementations")
        void counts(String n, Supplier<Map<Object, Object>> f) {
            Map<Integer, Integer> counts = make(f);
            MapProblems.countFrequencies(new int[]{1, 2, 2, 3, 3, 3}, counts);

            assertEquals(1, counts.get(1));
            assertEquals(2, counts.get(2));
            assertEquals(3, counts.get(3));
            assertEquals(3, counts.size());
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.hashmap.MapProblemsTest#implementations")
        void handlesEmptyAndNegatives(String n, Supplier<Map<Object, Object>> f) {
            Map<Integer, Integer> empty = make(f);
            MapProblems.countFrequencies(new int[]{}, empty);
            assertEquals(0, empty.size());

            Map<Integer, Integer> neg = make(f);
            MapProblems.countFrequencies(new int[]{-1, -1, 0}, neg);
            assertEquals(2, neg.get(-1), "음수 해시도 정상 처리되어야 한다");
            assertEquals(1, neg.get(0));
        }
    }

    @Nested
    @DisplayName("문제 2. 두 수의 합")
    class TwoSum {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.hashmap.MapProblemsTest#implementations")
        void findsPair(String n, Supplier<Map<Object, Object>> f) {
            assertArrayEquals(new int[]{0, 1},
                MapProblems.twoSum(new int[]{2, 7, 11, 15}, 9, make(f)));
            assertArrayEquals(new int[]{1, 2},
                MapProblems.twoSum(new int[]{3, 2, 4}, 6, make(f)));
            assertArrayEquals(new int[]{0, 1},
                MapProblems.twoSum(new int[]{3, 3}, 6, make(f)), "같은 값이 두 번 나오는 경우");
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.hashmap.MapProblemsTest#implementations")
        void returnsEmptyWhenNone(String n, Supplier<Map<Object, Object>> f) {
            assertArrayEquals(new int[]{}, MapProblems.twoSum(new int[]{1, 2}, 99, make(f)));
            assertArrayEquals(new int[]{}, MapProblems.twoSum(new int[]{}, 0, make(f)));
            assertArrayEquals(new int[]{}, MapProblems.twoSum(new int[]{5}, 10, make(f)),
                "같은 원소를 두 번 쓰면 안 된다");
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.hashmap.MapProblemsTest#implementations")
        @DisplayName("20만 건에서 5초 안에 (O(n^2) 은 통과 못 한다)")
        void mustBeLinear(String name, Supplier<Map<Object, Object>> f) {
            final int n = 200_000;
            int[] values = new int[n];
            // 연속 정수를 쓰면 안 된다. 선형 탐사에서 키가 통째로 뭉쳐(1차 군집화)
            // 자료구조 자체가 느려지고, 그러면 이 테스트가 알고리즘이 아니라 구현을 재게 된다.
            // 그 성질은 LinearProbingHashMapTest 에서 따로 본다.
            for (int i = 0; i < n; i++) values[i] = i * 4001;
            // 답이 맨 끝에 있어야 O(n^2) 구현이 끝까지 다 훑는다
            int target = values[n - 2] + values[n - 1];

            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                assertArrayEquals(new int[]{n - 2, n - 1},
                    MapProblems.twoSum(values, target, make(f)));
            }, "모든 쌍을 보면 O(n^2) 이라 여기서 막힌다. '짝을 이미 봤는지'를 물어라.");
        }
    }

    @Nested
    @DisplayName("문제 3. 처음으로 한 번만 나온 문자")
    class FirstUniqueChar {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.hashmap.MapProblemsTest#implementations")
        void findsIndex(String n, Supplier<Map<Object, Object>> f) {
            assertEquals(0, MapProblems.firstUniqueChar("leetcode", make(f)));
            assertEquals(2, MapProblems.firstUniqueChar("loveleetcode", make(f)));
            assertEquals(1, MapProblems.firstUniqueChar("abac", make(f)));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.hashmap.MapProblemsTest#implementations")
        void returnsMinusOneWhenNone(String n, Supplier<Map<Object, Object>> f) {
            assertEquals(-1, MapProblems.firstUniqueChar("aabb", make(f)));
            assertEquals(-1, MapProblems.firstUniqueChar("", make(f)));
            assertEquals(0, MapProblems.firstUniqueChar("z", make(f)));
        }
    }
}
