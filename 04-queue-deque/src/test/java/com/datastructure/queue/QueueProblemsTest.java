package com.datastructure.queue;

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
 * 응용 문제는 구현을 바꿔 끼워도 결과가 같아야 한다.
 *
 * Deque 를 받는 문제는 데크 두 구현으로, Queue 만 받는 문제는 큐 네 구현 전부로 돌린다.
 * 파라미터 타입이 곧 그 문제가 요구하는 능력이다.
 */
class QueueProblemsTest {

    static Stream<Arguments> deques() {
        return Stream.of(
            Arguments.of("ArrayDeque", (Supplier<Deque<Object>>) ArrayDeque::new),
            Arguments.of("LinkedDeque", (Supplier<Deque<Object>>) LinkedDeque::new)
        );
    }

    static Stream<Arguments> queues() {
        return Stream.of(
            Arguments.of("ArrayQueue", (Supplier<Queue<Object>>) ArrayQueue::new),
            Arguments.of("CircularQueue", (Supplier<Queue<Object>>) CircularQueue::new),
            Arguments.of("ArrayDeque", (Supplier<Queue<Object>>) ArrayDeque::new),
            Arguments.of("LinkedDeque", (Supplier<Queue<Object>>) LinkedDeque::new)
        );
    }

    @SuppressWarnings("unchecked")
    private static <E> Deque<E> dq(Supplier<Deque<Object>> f) {
        return (Deque<E>) f.get();
    }

    @SuppressWarnings("unchecked")
    private static <E> Queue<E> q(Supplier<Queue<Object>> f) {
        return (Queue<E>) f.get();
    }

    @Nested
    @DisplayName("문제 1. 회문 판별")
    class IsPalindrome {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.queue.QueueProblemsTest#deques")
        void acceptsPalindromes(String n, Supplier<Deque<Object>> f) {
            assertTrue(QueueProblems.isPalindrome("aba", dq(f)));
            assertTrue(QueueProblems.isPalindrome("abba", dq(f)));
            assertTrue(QueueProblems.isPalindrome("A man, a plan, a canal: Panama", dq(f)));
            assertTrue(QueueProblems.isPalindrome("", dq(f)), "빈 문자열은 회문이다");
            assertTrue(QueueProblems.isPalindrome("x", dq(f)));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.queue.QueueProblemsTest#deques")
        void rejectsNonPalindromes(String n, Supplier<Deque<Object>> f) {
            assertFalse(QueueProblems.isPalindrome("abc", dq(f)));
            assertFalse(QueueProblems.isPalindrome("race a car", dq(f)));
        }
    }

    @Nested
    @DisplayName("문제 2. 슬라이딩 윈도우 최댓값")
    class SlidingWindowMax {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.queue.QueueProblemsTest#deques")
        void findsWindowMaxima(String n, Supplier<Deque<Object>> f) {
            assertArrayEquals(new int[]{3, 3, 5, 5, 6, 7},
                QueueProblems.slidingWindowMax(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3, dq(f)));
            assertArrayEquals(new int[]{1, 2, 3},
                QueueProblems.slidingWindowMax(new int[]{1, 2, 3}, 1, dq(f)), "k=1 이면 원본 그대로");
            assertArrayEquals(new int[]{3},
                QueueProblems.slidingWindowMax(new int[]{1, 3, 2}, 3, dq(f)), "k=n 이면 전체 최댓값 하나");
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.queue.QueueProblemsTest#deques")
        void handlesEdges(String n, Supplier<Deque<Object>> f) {
            assertArrayEquals(new int[]{}, QueueProblems.slidingWindowMax(new int[]{}, 3, dq(f)));
            assertArrayEquals(new int[]{5, 5, 5},
                QueueProblems.slidingWindowMax(new int[]{5, 5, 5, 5, 5}, 3, dq(f)), "같은 값만 있어도 된다");
            assertArrayEquals(new int[]{-1, -2, -2},
                QueueProblems.slidingWindowMax(new int[]{-1, -2, -3, -2}, 2, dq(f)), "음수만 있어도 된다");
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.queue.QueueProblemsTest#deques")
        @DisplayName("100만 x k=5만 을 5초 안에 (O(n*k) 는 통과 못 한다)")
        void mustBeLinear(String name, Supplier<Deque<Object>> f) {
            // 임계값 근거: 처음에 30만 x 1만(3e9 회)로 잡았더니 O(n*k) 구현이 1초대에 통과했다.
            // 내림차순 데이터라 안쪽 비교가 항상 거짓이고 분기 예측이 완벽해 JIT 가 너무 잘 돌린다.
            // 4.75e10 회로 올려야 확실히 걸린다. 올바른 구현은 여전히 수십 밀리초다.
            final int n = 1_000_000;
            final int k = 50_000;
            int[] values = new int[n];
            for (int i = 0; i < n; i++) values[i] = n - i;   // 내림차순: 창의 첫 값이 항상 최댓값

            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                int[] result = QueueProblems.slidingWindowMax(values, k, dq(f));
                assertEquals(n - k + 1, result.length);
                for (int i = 0; i < result.length; i++) {
                    if (result[i] != n - i) fail("인덱스 " + i + " 의 답이 " + (n - i) + " 이 아니다: " + result[i]);
                }
            }, "창마다 k 개를 훑으면 O(n*k) 라 여기서 막힌다.");
        }
    }

    @Nested
    @DisplayName("문제 3. 스트림에서 처음 한 번만 나온 문자")
    class FirstUniqueStream {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.queue.QueueProblemsTest#queues")
        void tracksFirstUnique(String n, Supplier<Queue<Object>> f) {
            assertEquals("aaabc#", QueueProblems.firstUniqueStream("abcabc", q(f)));
            assertEquals("a#b", QueueProblems.firstUniqueStream("aab", q(f)),
                "a 가 두 번 나오면 남는 게 없어 # 이고, 그 다음 b 는 유일하다");
            assertEquals("aabbb", QueueProblems.firstUniqueStream("abacc", q(f)),
                "abacc 에서 b 는 한 번만 나오므로 끝까지 답이다");
            assertEquals("", QueueProblems.firstUniqueStream("", q(f)));
            assertEquals("a", QueueProblems.firstUniqueStream("a", q(f)));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.queue.QueueProblemsTest#queues")
        void allRepeated(String n, Supplier<Queue<Object>> f) {
            assertEquals("aab#", QueueProblems.firstUniqueStream("abab", q(f)));
        }
    }

    @Nested
    @DisplayName("문제 4. k 칸 회전")
    class Rotate {

        private static int[] drain(Deque<Integer> d) {
            int[] out = new int[d.size()];
            for (int i = 0; i < out.length; i++) out[i] = d.removeFirst();
            return out;
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.queue.QueueProblemsTest#deques")
        void rotatesRight(String n, Supplier<Deque<Object>> f) {
            Deque<Integer> d = dq(f);
            for (int v : new int[]{1, 2, 3, 4, 5}) d.addLast(v);
            QueueProblems.rotate(d, 2);
            assertArrayEquals(new int[]{4, 5, 1, 2, 3}, drain(d));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.queue.QueueProblemsTest#deques")
        void handlesLargeAndNegativeK(String n, Supplier<Deque<Object>> f) {
            Deque<Integer> a = dq(f);
            for (int v : new int[]{1, 2, 3}) a.addLast(v);
            QueueProblems.rotate(a, 7);            // 7 % 3 == 1
            assertArrayEquals(new int[]{3, 1, 2}, drain(a));

            Deque<Integer> b = dq(f);
            for (int v : new int[]{1, 2, 3, 4, 5}) b.addLast(v);
            QueueProblems.rotate(b, -1);           // 왼쪽으로 한 칸
            assertArrayEquals(new int[]{2, 3, 4, 5, 1}, drain(b));

            Deque<Integer> c = dq(f);
            for (int v : new int[]{1, 2, 3}) c.addLast(v);
            QueueProblems.rotate(c, 3_000_000);    // k 를 그대로 반복하면 느리다
            assertArrayEquals(new int[]{1, 2, 3}, drain(c));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.queue.QueueProblemsTest#deques")
        void handlesZeroAndEmpty(String n, Supplier<Deque<Object>> f) {
            Deque<Integer> empty = dq(f);
            assertDoesNotThrow(() -> QueueProblems.rotate(empty, 3));

            Deque<Integer> one = dq(f);
            one.addLast(9);
            QueueProblems.rotate(one, 5);
            assertArrayEquals(new int[]{9}, drain(one));
        }
    }
}
