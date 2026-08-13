package com.datastructure.linkedlist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 응용 문제는 List 인터페이스만 알고 있으므로 두 구현에서 같게 동작해야 한다.
 */
class ListProblemsTest {

    static Stream<Arguments> implementations() {
        return Stream.of(
            Arguments.of("SinglyLinkedList", (Supplier<List<Object>>) SinglyLinkedList::new),
            Arguments.of("DoublyLinkedList", (Supplier<List<Object>>) DoublyLinkedList::new)
        );
    }

    @SuppressWarnings("unchecked")
    private static <E> List<E> make(Supplier<List<Object>> f) {
        return (List<E>) f.get();
    }

    private static List<Integer> of(Supplier<List<Object>> f, int... values) {
        List<Integer> list = make(f);
        for (int v : values) list.add(v);
        return list;
    }

    @Nested
    @DisplayName("문제 1. 조건부 일괄 삭제")
    class RemoveAllIf {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.linkedlist.ListProblemsTest#implementations")
        void removesMatching(String n, Supplier<List<Object>> f) {
            List<Integer> list = of(f, 1, 2, 3, 4, 5, 6);
            assertEquals(3, ListProblems.removeAllIf(list, v -> v % 2 == 0));
            assertArrayEquals(new Object[]{1, 3, 5}, list.toArray());
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.linkedlist.ListProblemsTest#implementations")
        @DisplayName("맨 앞과 맨 뒤가 지워져도 이후 추가가 정상이다")
        void removesEnds(String n, Supplier<List<Object>> f) {
            List<Integer> list = of(f, 2, 1, 3, 4);
            assertEquals(2, ListProblems.removeAllIf(list, v -> v % 2 == 0));
            assertArrayEquals(new Object[]{1, 3}, list.toArray());

            list.add(9);      // tail 이 제대로 갱신됐는지
            assertArrayEquals(new Object[]{1, 3, 9}, list.toArray());
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.linkedlist.ListProblemsTest#implementations")
        void removesAllAndNone(String n, Supplier<List<Object>> f) {
            List<Integer> all = of(f, 2, 4, 6);
            assertEquals(3, ListProblems.removeAllIf(all, v -> v % 2 == 0));
            assertEquals(0, all.size());

            List<Integer> none = of(f, 1, 3, 5);
            assertEquals(0, ListProblems.removeAllIf(none, v -> v % 2 == 0));
            assertArrayEquals(new Object[]{1, 3, 5}, none.toArray());
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.linkedlist.ListProblemsTest#implementations")
        @DisplayName("10만 건에서 5초 안에 (인덱스로 훑으면 통과 못 한다)")
        void mustBeLinear(String name, Supplier<List<Object>> f) {
            final int n = 100_000;
            List<Integer> list = make(f);
            for (int i = 0; i < n; i++) list.add(i);   // add 는 O(1)

            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                int removed = ListProblems.removeAllIf(list, v -> v % 2 == 0);
                assertEquals(n / 2, removed);
                assertEquals(n / 2, list.size());

                long expected = (long) (n / 2) * (n / 2);   // 1+3+...+(n-1)
                long actual = 0;
                for (int v : list) actual += v;
                assertEquals(expected, actual, "남은 원소가 홀수 전부가 아니다");
            }, "get(i) 나 remove(i) 로 훑으면 탐색이 매번 O(n) 이라 여기서 막힌다.");
        }
    }

    @Nested
    @DisplayName("문제 2. 가운데 값")
    class FindMiddle {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.linkedlist.ListProblemsTest#implementations")
        void oddAndEven(String n, Supplier<List<Object>> f) {
            assertEquals(2, ListProblems.findMiddle(of(f, 1, 2, 3)));
            assertEquals(3, ListProblems.findMiddle(of(f, 1, 2, 3, 4, 5)));
            assertEquals(3, ListProblems.findMiddle(of(f, 1, 2, 3, 4)), "짝수면 뒤쪽");
            assertEquals(2, ListProblems.findMiddle(of(f, 1, 2)));
            assertEquals(9, ListProblems.findMiddle(of(f, 9)));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.linkedlist.ListProblemsTest#implementations")
        void emptyThrows(String n, Supplier<List<Object>> f) {
            assertThrows(java.util.NoSuchElementException.class,
                () -> ListProblems.findMiddle(of(f)));
        }
    }

    @Nested
    @DisplayName("문제 3. 정렬된 두 리스트 병합")
    class MergeSorted {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.linkedlist.ListProblemsTest#implementations")
        void merges(String n, Supplier<List<Object>> f) {
            List<Integer> result = make(f);
            ListProblems.mergeSorted(of(f, 1, 3, 5), of(f, 2, 3, 6), result);
            assertArrayEquals(new Object[]{1, 2, 3, 3, 5, 6}, result.toArray());
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.linkedlist.ListProblemsTest#implementations")
        void handlesEmptySide(String n, Supplier<List<Object>> f) {
            List<Integer> r1 = make(f);
            ListProblems.mergeSorted(of(f), of(f, 1, 2), r1);
            assertArrayEquals(new Object[]{1, 2}, r1.toArray());

            List<Integer> r2 = make(f);
            ListProblems.mergeSorted(of(f, 1, 2), of(f), r2);
            assertArrayEquals(new Object[]{1, 2}, r2.toArray());

            List<Integer> r3 = make(f);
            ListProblems.mergeSorted(of(f), of(f), r3);
            assertEquals(0, r3.size());
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.linkedlist.ListProblemsTest#implementations")
        @DisplayName("원본은 변하지 않는다")
        void doesNotMutateInputs(String n, Supplier<List<Object>> f) {
            List<Integer> a = of(f, 1, 3);
            List<Integer> b = of(f, 2);
            ListProblems.mergeSorted(a, b, make(f));

            assertArrayEquals(new Object[]{1, 3}, a.toArray());
            assertArrayEquals(new Object[]{2}, b.toArray());
        }
    }
}
