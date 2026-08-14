package com.datastructure.stack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 응용 문제는 두 구현 모두로 돌린다.
 *
 * 문제 코드가 Stack 인터페이스만 알고 있으니 구현을 바꿔 끼워도 결과가 같아야 한다.
 * 같지 않다면 둘 중 하나가 계약을 어긴 것이다. 이 테스트가 그걸 잡는다.
 */
class StackProblemsTest {

    /** 두 구현의 생성자를 나란히 놓는다. 새 구현이 생기면 여기에 한 줄 추가한다. */
    static Stream<org.junit.jupiter.params.provider.Arguments> implementations() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of("ArrayStack",
                (Supplier<Stack<Object>>) ArrayStack::new),
            org.junit.jupiter.params.provider.Arguments.of("LinkedStack",
                (Supplier<Stack<Object>>) LinkedStack::new)
        );
    }

    @SuppressWarnings("unchecked")
    private static <E> Stack<E> make(Supplier<Stack<Object>> factory) {
        return (Stack<E>) factory.get();
    }

    @Nested
    @DisplayName("문제 1. 괄호 짝 맞추기")
    class IsBalanced {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        void acceptsBalanced(String name, Supplier<Stack<Object>> f) {
            assertTrue(StackProblems.isBalanced("()", make(f)));
            assertTrue(StackProblems.isBalanced("([]{})", make(f)));
            assertTrue(StackProblems.isBalanced("a(b[c]d)e", make(f)), "괄호 외 문자는 무시한다");
            assertTrue(StackProblems.isBalanced("", make(f)), "빈 문자열은 균형이다");
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        void rejectsUnbalanced(String name, Supplier<Stack<Object>> f) {
            assertFalse(StackProblems.isBalanced("([)]", make(f)), "짝은 맞지만 순서가 어긋난다");
            assertFalse(StackProblems.isBalanced("((", make(f)), "안 닫혔다");
            assertFalse(StackProblems.isBalanced(")(", make(f)), "닫는 게 먼저 왔다");
            assertFalse(StackProblems.isBalanced("(]", make(f)), "종류가 다르다");
        }
    }

    @Nested
    @DisplayName("문제 2. 후위 표기식 계산")
    class EvaluatePostfix {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        void evaluates(String name, Supplier<Stack<Object>> f) {
            assertEquals(7, StackProblems.evaluatePostfix("3 4 +", make(f)));
            assertEquals(14, StackProblems.evaluatePostfix("3 4 + 2 *", make(f)));
            assertEquals(14, StackProblems.evaluatePostfix("5 1 2 + 4 * + 3 -", make(f)));
            assertEquals(42, StackProblems.evaluatePostfix("42", make(f)));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        @DisplayName("피연산자 순서가 중요하다")
        void operandOrderMatters(String name, Supplier<Stack<Object>> f) {
            assertEquals(-1, StackProblems.evaluatePostfix("3 4 -", make(f)), "3 - 4 이지 4 - 3 이 아니다");
            assertEquals(2, StackProblems.evaluatePostfix("8 4 /", make(f)), "8 / 4 이지 4 / 8 이 아니다");
            assertEquals(-6, StackProblems.evaluatePostfix("2 8 -", make(f)));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        void rejectsMalformed(String name, Supplier<Stack<Object>> f) {
            assertThrows(IllegalArgumentException.class,
                () -> StackProblems.evaluatePostfix("3 +", make(f)), "피연산자가 모자란다");
            assertThrows(IllegalArgumentException.class,
                () -> StackProblems.evaluatePostfix("3 4", make(f)), "연산이 끝나고 값이 둘 남았다");
        }
    }

    @Nested
    @DisplayName("문제 3. 오른쪽의 첫 번째 더 큰 값")
    class NextGreater {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        void findsNextGreater(String name, Supplier<Stack<Object>> f) {
            assertArrayEquals(new int[]{3, 3, -1},
                StackProblems.nextGreater(new int[]{2, 1, 3}, make(f)));
            assertArrayEquals(new int[]{-1, -1, -1},
                StackProblems.nextGreater(new int[]{5, 4, 3}, make(f)));
            assertArrayEquals(new int[]{3, 4, 4, -1},
                StackProblems.nextGreater(new int[]{1, 3, 2, 4}, make(f)));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        void handlesEdges(String name, Supplier<Stack<Object>> f) {
            assertArrayEquals(new int[]{}, StackProblems.nextGreater(new int[]{}, make(f)));
            assertArrayEquals(new int[]{-1}, StackProblems.nextGreater(new int[]{7}, make(f)));
            assertArrayEquals(new int[]{-1, -1, -1},
                StackProblems.nextGreater(new int[]{5, 5, 5}, make(f)), "같은 값은 '더 큰' 값이 아니다");
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        @DisplayName("20만 건에서 5초 안에 끝나야 한다 (O(n^2) 은 통과 못 한다)")
        void mustBeLinear(String name, Supplier<Stack<Object>> f) {
            final int n = 200_000;
            int[] values = new int[n];
            for (int i = 0; i < n; i++) values[i] = n - i;   // 내림차순: 모든 답이 -1, 스택이 최대로 쌓인다

            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                int[] result = StackProblems.nextGreater(values, make(f));
                assertEquals(n, result.length);
                for (int i = 0; i < n; i++) {
                    if (result[i] != -1) fail("인덱스 " + i + " 의 답이 -1 이 아니다: " + result[i]);
                }
            }, "각 원소마다 오른쪽을 훑으면 O(n^2) 이라 여기서 막힌다.");
        }
    }

    @Nested
    @DisplayName("문제 4. 스택 정렬")
    class SortAscending {

        private static int[] drain(Stack<Integer> stack) {
            int[] out = new int[stack.size()];
            for (int i = out.length - 1; i >= 0; i--) out[i] = stack.pop();
            return out;   // 바닥부터 top 순서
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        void sortsAscending(String name, Supplier<Stack<Object>> f) {
            Stack<Integer> s = make(f);
            for (int v : new int[]{3, 1, 2}) s.push(v);

            StackProblems.sortAscending(s, make(f));

            assertArrayEquals(new int[]{1, 2, 3}, drain(s), "top 에 가장 큰 값이 와야 한다");
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        void handlesDuplicatesAndEdges(String name, Supplier<Stack<Object>> f) {
            Stack<Integer> dup = make(f);
            for (int v : new int[]{2, 1, 2, 1}) dup.push(v);
            StackProblems.sortAscending(dup, make(f));
            assertArrayEquals(new int[]{1, 1, 2, 2}, drain(dup));

            Stack<Integer> empty = make(f);
            assertDoesNotThrow(() -> StackProblems.sortAscending(empty, make(f)));
            assertEquals(0, empty.size());

            Stack<Integer> one = make(f);
            one.push(9);
            StackProblems.sortAscending(one, make(f));
            assertArrayEquals(new int[]{9}, drain(one));
        }
    }

    @Nested
    @DisplayName("문제 5. 중위 -> 후위 변환")
    class InfixToPostfix {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        void respectsPrecedence(String name, Supplier<Stack<Object>> f) {
            assertEquals("3 4 2 * +", StackProblems.infixToPostfix("3 + 4 * 2", make(f)));
            assertEquals("3 4 * 2 +", StackProblems.infixToPostfix("3 * 4 + 2", make(f)));
            assertEquals("3 4 +", StackProblems.infixToPostfix("3 + 4", make(f)));
            assertEquals("42", StackProblems.infixToPostfix("42", make(f)));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        void respectsParentheses(String name, Supplier<Stack<Object>> f) {
            assertEquals("3 4 + 2 *", StackProblems.infixToPostfix("( 3 + 4 ) * 2", make(f)));
            assertEquals("3 4 5 + *", StackProblems.infixToPostfix("3 * ( 4 + 5 )", make(f)));
            assertEquals("3 4 +", StackProblems.infixToPostfix("( ( 3 + 4 ) )", make(f)));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        @DisplayName("같은 우선순위는 왼쪽부터 묶인다")
        void leftAssociative(String name, Supplier<Stack<Object>> f) {
            // "높은" 만 보고 꺼내면 "3 4 5 - -" 가 되어 3-(4-5) 로 계산된다. 답이 달라진다.
            assertEquals("3 4 - 5 -", StackProblems.infixToPostfix("3 - 4 - 5", make(f)));
            assertEquals("8 4 / 2 /", StackProblems.infixToPostfix("8 / 4 / 2", make(f)));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        @DisplayName("괄호가 안 맞으면 거부한다")
        void rejectsUnbalancedParentheses(String name, Supplier<Stack<Object>> f) {
            assertThrows(IllegalArgumentException.class,
                () -> StackProblems.infixToPostfix("( 3 + 4", make(f)), "닫는 괄호가 없다");
            assertThrows(IllegalArgumentException.class,
                () -> StackProblems.infixToPostfix("3 + 4 )", make(f)), "여는 괄호가 없다");
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.datastructure.stack.StackProblemsTest#implementations")
        @DisplayName("변환한 것을 문제 2로 계산하면 값이 맞는다")
        void chainsWithEvaluate(String name, Supplier<Stack<Object>> f) {
            // 두 문제가 이어진다. 이게 후위 표기가 존재하는 이유다.
            assertEquals(11, StackProblems.evaluatePostfix(
                StackProblems.infixToPostfix("3 + 4 * 2", make(f)), make(f)));
            assertEquals(14, StackProblems.evaluatePostfix(
                StackProblems.infixToPostfix("( 3 + 4 ) * 2", make(f)), make(f)));
            assertEquals(-6, StackProblems.evaluatePostfix(
                StackProblems.infixToPostfix("3 - 4 - 5", make(f)), make(f)));
        }
    }
}
