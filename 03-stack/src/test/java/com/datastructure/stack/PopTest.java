package com.datastructure.stack;

import com.datastructure.stack.pop.ArrayStack;
import com.datastructure.stack.pop.LinkedStack;
import com.datastructure.stack.pop.StackProblems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("스택 - POP 구현 테스트")
class PopTest {

    @Nested
    @DisplayName("배열 기반 스택")
    class ArrayStackTest {
        
        private ArrayStack stack;

        @BeforeEach
        void setUp() {
            stack = new ArrayStack();
        }

        @Nested
        @DisplayName("기본 연산")
        class BasicOperations {

            @Test
            @DisplayName("01. 빈 스택 생성 시 isEmpty는 true다")
            void test01_emptyStackIsEmpty() {
                // TODO: isEmpty() 메서드 구현 후 테스트
                // assertThat(stack.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("02. push 후 isEmpty는 false다")
            void test02_pushMakesNonEmpty() {
                // TODO: push() 메서드 구현 후 테스트
                // stack.push(1);
                // assertThat(stack.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("03. push한 요소를 pop으로 꺼낼 수 있다")
            void test03_pushAndPop() {
                // TODO: push(), pop() 구현 후 테스트
                // stack.push(42);
                // assertThat(stack.pop()).isEqualTo(42);
            }

            @Test
            @DisplayName("04. LIFO 순서로 pop된다")
            void test04_lifoOrder() {
                // TODO: LIFO 순서 테스트
                // stack.push(1);
                // stack.push(2);
                // stack.push(3);
                // assertThat(stack.pop()).isEqualTo(3);
                // assertThat(stack.pop()).isEqualTo(2);
                // assertThat(stack.pop()).isEqualTo(1);
            }

            @Test
            @DisplayName("05. peek은 요소를 제거하지 않는다")
            void test05_peekDoesNotRemove() {
                // TODO: peek() 구현 후 테스트
                // stack.push(1);
                // assertThat(stack.peek()).isEqualTo(1);
                // assertThat(stack.peek()).isEqualTo(1);
                // assertThat(stack.size()).isEqualTo(1);
            }

            @Test
            @DisplayName("06. size가 정확히 반환된다")
            void test06_sizeIsCorrect() {
                // TODO: size() 구현 후 테스트
                // assertThat(stack.size()).isEqualTo(0);
                // stack.push(1);
                // stack.push(2);
                // assertThat(stack.size()).isEqualTo(2);
            }
        }

        @Nested
        @DisplayName("예외 처리")
        class ExceptionHandling {

            @Test
            @DisplayName("07. 빈 스택에서 pop 시 예외가 발생한다")
            void test07_popFromEmptyStack() {
                // TODO: EmptyStackException 처리 후 테스트
                // assertThatThrownBy(() -> stack.pop())
                //     .isInstanceOf(EmptyStackException.class);
            }

            @Test
            @DisplayName("08. 빈 스택에서 peek 시 예외가 발생한다")
            void test08_peekFromEmptyStack() {
                // TODO: EmptyStackException 처리 후 테스트
                // assertThatThrownBy(() -> stack.peek())
                //     .isInstanceOf(EmptyStackException.class);
            }
        }

        @Nested
        @DisplayName("동적 확장")
        class DynamicGrowth {

            @Test
            @DisplayName("09. 초기 용량을 초과해도 정상 동작한다")
            void test09_growBeyondInitialCapacity() {
                // TODO: 자동 확장 구현 후 테스트
                // for (int i = 0; i < 100; i++) {
                //     stack.push(i);
                // }
                // assertThat(stack.size()).isEqualTo(100);
                // assertThat(stack.pop()).isEqualTo(99);
            }

            @Test
            @DisplayName("10. clear 후 스택이 비어있다")
            void test10_clearEmptiesStack() {
                // TODO: clear() 구현 후 테스트
                // stack.push(1);
                // stack.push(2);
                // stack.clear();
                // assertThat(stack.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("검색")
        class Search {

            @Test
            @DisplayName("11. search로 요소 위치를 찾을 수 있다")
            void test11_searchFindsElement() {
                // TODO: search() 구현 후 테스트 (top=1 기준)
                // stack.push(1);
                // stack.push(2);
                // stack.push(3);
                // assertThat(stack.search(3)).isEqualTo(1);  // top
                // assertThat(stack.search(2)).isEqualTo(2);
                // assertThat(stack.search(1)).isEqualTo(3);
            }

            @Test
            @DisplayName("12. 존재하지 않는 요소의 search는 -1이다")
            void test12_searchNotFound() {
                // TODO: search() 구현 후 테스트
                // stack.push(1);
                // assertThat(stack.search(999)).isEqualTo(-1);
            }
        }
    }

    @Nested
    @DisplayName("연결 리스트 기반 스택")
    class LinkedStackTest {
        
        private LinkedStack stack;

        @BeforeEach
        void setUp() {
            stack = new LinkedStack();
        }

        @Test
        @DisplayName("13. 기본 push/pop 동작")
        void test13_basicPushPop() {
            // TODO: LinkedStack 구현 후 테스트
            // stack.push(1);
            // stack.push(2);
            // assertThat(stack.pop()).isEqualTo(2);
            // assertThat(stack.pop()).isEqualTo(1);
        }

        @Test
        @DisplayName("14. 크기 제한 없이 동작한다")
        void test14_noSizeLimit() {
            // TODO: 대량 데이터 테스트
            // for (int i = 0; i < 10000; i++) {
            //     stack.push(i);
            // }
            // assertThat(stack.size()).isEqualTo(10000);
        }
    }

    @Nested
    @DisplayName("스택 응용 문제")
    class StackProblemsTest {

        @Nested
        @DisplayName("괄호 매칭")
        class ParenthesesMatching {

            @Test
            @DisplayName("15. 올바른 괄호 - 단순 케이스")
            void test15_validSimple() {
                // TODO: isValidParentheses() 구현 후 테스트
                // assertThat(StackProblems.isValidParentheses("()")).isTrue();
                // assertThat(StackProblems.isValidParentheses("[]")).isTrue();
                // assertThat(StackProblems.isValidParentheses("{}")).isTrue();
            }

            @Test
            @DisplayName("16. 올바른 괄호 - 복합 케이스")
            void test16_validComplex() {
                // TODO: isValidParentheses() 구현 후 테스트
                // assertThat(StackProblems.isValidParentheses("(){}[]")).isTrue();
                // assertThat(StackProblems.isValidParentheses("([{}])")).isTrue();
                // assertThat(StackProblems.isValidParentheses("{[()]}")).isTrue();
            }

            @Test
            @DisplayName("17. 올바르지 않은 괄호 - 짝 불일치")
            void test17_invalidMismatch() {
                // TODO: isValidParentheses() 구현 후 테스트
                // assertThat(StackProblems.isValidParentheses("(]")).isFalse();
                // assertThat(StackProblems.isValidParentheses("([)]")).isFalse();
            }

            @Test
            @DisplayName("18. 올바르지 않은 괄호 - 닫는 괄호 초과")
            void test18_invalidExtraClosing() {
                // TODO: isValidParentheses() 구현 후 테스트
                // assertThat(StackProblems.isValidParentheses("())")).isFalse();
                // assertThat(StackProblems.isValidParentheses(")")).isFalse();
            }

            @Test
            @DisplayName("19. 올바르지 않은 괄호 - 여는 괄호 초과")
            void test19_invalidExtraOpening() {
                // TODO: isValidParentheses() 구현 후 테스트
                // assertThat(StackProblems.isValidParentheses("(()")).isFalse();
                // assertThat(StackProblems.isValidParentheses("(")).isFalse();
            }

            @Test
            @DisplayName("20. 빈 문자열은 유효하다")
            void test20_emptyStringIsValid() {
                // TODO: isValidParentheses() 구현 후 테스트
                // assertThat(StackProblems.isValidParentheses("")).isTrue();
            }
        }

        @Nested
        @DisplayName("후위 표기법 계산")
        class PostfixEvaluation {

            @Test
            @DisplayName("21. 단순 덧셈")
            void test21_simpleAddition() {
                // TODO: evaluatePostfix() 구현 후 테스트
                // assertThat(StackProblems.evaluatePostfix("3 4 +")).isEqualTo(7);
            }

            @Test
            @DisplayName("22. 단순 뺄셈 (순서 주의)")
            void test22_simpleSubtraction() {
                // TODO: evaluatePostfix() 구현 후 테스트
                // assertThat(StackProblems.evaluatePostfix("5 3 -")).isEqualTo(2);
            }

            @Test
            @DisplayName("23. 복합 수식")
            void test23_complexExpression() {
                // TODO: evaluatePostfix() 구현 후 테스트
                // assertThat(StackProblems.evaluatePostfix("3 4 + 2 *")).isEqualTo(14);
                // assertThat(StackProblems.evaluatePostfix("5 1 2 + 4 * + 3 -")).isEqualTo(14);
            }

            @Test
            @DisplayName("24. 나눗셈")
            void test24_division() {
                // TODO: evaluatePostfix() 구현 후 테스트
                // assertThat(StackProblems.evaluatePostfix("10 2 /")).isEqualTo(5);
            }
        }

        @Nested
        @DisplayName("중위 → 후위 변환")
        class InfixToPostfix {

            @Test
            @DisplayName("25. 단순 수식 변환")
            void test25_simpleConversion() {
                // TODO: infixToPostfix() 구현 후 테스트
                // assertThat(StackProblems.infixToPostfix("3 + 4")).isEqualTo("3 4 +");
            }

            @Test
            @DisplayName("26. 연산자 우선순위 적용")
            void test26_operatorPrecedence() {
                // TODO: infixToPostfix() 구현 후 테스트
                // assertThat(StackProblems.infixToPostfix("3 + 4 * 2")).isEqualTo("3 4 2 * +");
            }

            @Test
            @DisplayName("27. 괄호 처리")
            void test27_parentheses() {
                // TODO: infixToPostfix() 구현 후 테스트
                // assertThat(StackProblems.infixToPostfix("( 3 + 4 ) * 2")).isEqualTo("3 4 + 2 *");
            }

            @Test
            @DisplayName("28. 동일 우선순위 (왼쪽 결합)")
            void test28_samePrecedence() {
                // TODO: infixToPostfix() 구현 후 테스트
                // assertThat(StackProblems.infixToPostfix("3 - 4 + 5")).isEqualTo("3 4 - 5 +");
            }
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("29. toArray로 배열 변환")
        void test29_toArray() {
            // TODO: toArray() 구현 후 테스트
            // ArrayStack stack = new ArrayStack();
            // stack.push(1);
            // stack.push(2);
            // stack.push(3);
            // int[] arr = stack.toArray();
            // assertThat(arr).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("30. toString으로 스택 내용 확인")
        void test30_toString() {
            // TODO: toString() 구현 후 테스트
            // ArrayStack stack = new ArrayStack();
            // stack.push(1);
            // stack.push(2);
            // stack.push(3);
            // assertThat(stack.toString()).isEqualTo("[1, 2, 3]");
        }
    }
}
