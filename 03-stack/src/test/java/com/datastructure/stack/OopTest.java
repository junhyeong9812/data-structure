package com.datastructure.stack;

import com.datastructure.stack.oop.Stack;
import com.datastructure.stack.oop.ArrayStackImpl;
import com.datastructure.stack.oop.LinkedStackImpl;
import com.datastructure.stack.oop.StackCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("스택 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("배열 기반 스택")
    class ArrayStackTest {
        
        private Stack<Integer> stack;

        @BeforeEach
        void setUp() {
            stack = new ArrayStackImpl<>();
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
        @DisplayName("제네릭 타입")
        class GenericType {

            @Test
            @DisplayName("09. String 타입을 저장할 수 있다")
            void test09_stringType() {
                // TODO: 제네릭 테스트
                // Stack<String> strStack = new ArrayStackImpl<>();
                // strStack.push("Hello");
                // strStack.push("World");
                // assertThat(strStack.pop()).isEqualTo("World");
            }

            @Test
            @DisplayName("10. null을 저장할 수 있다")
            void test10_nullElement() {
                // TODO: null 처리 테스트
                // stack.push(null);
                // assertThat(stack.peek()).isNull();
            }

            @Test
            @DisplayName("11. 사용자 정의 객체를 저장할 수 있다")
            void test11_customObject() {
                // TODO: 사용자 정의 객체 테스트
                // record Person(String name) {}
                // Stack<Person> personStack = new ArrayStackImpl<>();
                // personStack.push(new Person("Alice"));
                // assertThat(personStack.pop().name()).isEqualTo("Alice");
            }
        }
    }

    @Nested
    @DisplayName("연결 리스트 기반 스택")
    class LinkedStackTest {
        
        private Stack<Integer> stack;

        @BeforeEach
        void setUp() {
            stack = new LinkedStackImpl<>();
        }

        @Test
        @DisplayName("12. 기본 push/pop 동작")
        void test12_basicPushPop() {
            // TODO: LinkedStackImpl 구현 후 테스트
            // stack.push(1);
            // stack.push(2);
            // assertThat(stack.pop()).isEqualTo(2);
            // assertThat(stack.pop()).isEqualTo(1);
        }

        @Test
        @DisplayName("13. 대량 데이터 처리")
        void test13_largeData() {
            // TODO: 대량 데이터 테스트
            // for (int i = 0; i < 10000; i++) {
            //     stack.push(i);
            // }
            // assertThat(stack.size()).isEqualTo(10000);
        }

        @Test
        @DisplayName("14. Iterator로 순회할 수 있다")
        void test14_iterator() {
            // TODO: Iterable 구현 후 테스트
            // stack.push(1);
            // stack.push(2);
            // stack.push(3);
            // int sum = 0;
            // for (Integer i : stack) {
            //     sum += i;
            // }
            // assertThat(sum).isEqualTo(6);
        }
    }

    @Nested
    @DisplayName("스택 계산기")
    class StackCalculatorTest {

        @Nested
        @DisplayName("괄호 매칭")
        class ParenthesesMatching {

            @Test
            @DisplayName("15. 올바른 괄호 - 단순")
            void test15_validSimple() {
                // TODO: isValidParentheses() 구현 후 테스트
                // assertThat(StackCalculator.isValidParentheses("()")).isTrue();
            }

            @Test
            @DisplayName("16. 올바른 괄호 - 복합")
            void test16_validComplex() {
                // TODO: isValidParentheses() 구현 후 테스트
                // assertThat(StackCalculator.isValidParentheses("([{}])")).isTrue();
            }

            @Test
            @DisplayName("17. 올바르지 않은 괄호")
            void test17_invalid() {
                // TODO: isValidParentheses() 구현 후 테스트
                // assertThat(StackCalculator.isValidParentheses("(]")).isFalse();
                // assertThat(StackCalculator.isValidParentheses("([)]")).isFalse();
            }
        }

        @Nested
        @DisplayName("후위 표기법 계산")
        class PostfixEvaluation {

            @Test
            @DisplayName("18. 기본 사칙연산")
            void test18_basicOperations() {
                // TODO: evaluatePostfix() 구현 후 테스트
                // assertThat(StackCalculator.evaluatePostfix("3 4 +")).isEqualTo(7);
                // assertThat(StackCalculator.evaluatePostfix("10 3 -")).isEqualTo(7);
                // assertThat(StackCalculator.evaluatePostfix("3 4 *")).isEqualTo(12);
                // assertThat(StackCalculator.evaluatePostfix("12 4 /")).isEqualTo(3);
            }

            @Test
            @DisplayName("19. 복합 수식")
            void test19_complexExpression() {
                // TODO: evaluatePostfix() 구현 후 테스트
                // assertThat(StackCalculator.evaluatePostfix("3 4 + 2 *")).isEqualTo(14);
            }

            @Test
            @DisplayName("20. 제네릭 타입 지원 (Double)")
            void test20_doubleType() {
                // TODO: 제네릭 evaluatePostfix() 구현 후 테스트
                // assertThat(StackCalculator.evaluatePostfixDouble("3.5 2.5 +")).isEqualTo(6.0);
            }
        }

        @Nested
        @DisplayName("중위 → 후위 변환")
        class InfixToPostfix {

            @Test
            @DisplayName("21. 단순 수식")
            void test21_simple() {
                // TODO: infixToPostfix() 구현 후 테스트
                // assertThat(StackCalculator.infixToPostfix("3 + 4")).isEqualTo("3 4 +");
            }

            @Test
            @DisplayName("22. 연산자 우선순위")
            void test22_precedence() {
                // TODO: infixToPostfix() 구현 후 테스트
                // assertThat(StackCalculator.infixToPostfix("3 + 4 * 2")).isEqualTo("3 4 2 * +");
            }

            @Test
            @DisplayName("23. 괄호 처리")
            void test23_parentheses() {
                // TODO: infixToPostfix() 구현 후 테스트
                // assertThat(StackCalculator.infixToPostfix("( 3 + 4 ) * 2")).isEqualTo("3 4 + 2 *");
            }

            @Test
            @DisplayName("24. 복합 수식")
            void test24_complex() {
                // TODO: infixToPostfix() 구현 후 테스트
                // assertThat(StackCalculator.infixToPostfix("( 1 + 2 ) * ( 3 + 4 )")).isEqualTo("1 2 + 3 4 + *");
            }
        }
    }

    @Nested
    @DisplayName("기타 기능")
    class Miscellaneous {

        @Test
        @DisplayName("25. clear 후 스택이 비어있다")
        void test25_clear() {
            // TODO: clear() 구현 후 테스트
            // Stack<Integer> stack = new ArrayStackImpl<>();
            // stack.push(1);
            // stack.push(2);
            // stack.clear();
            // assertThat(stack.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("26. search로 요소 위치 찾기")
        void test26_search() {
            // TODO: search() 구현 후 테스트
            // Stack<Integer> stack = new ArrayStackImpl<>();
            // stack.push(1);
            // stack.push(2);
            // stack.push(3);
            // assertThat(stack.search(3)).isEqualTo(1);
            // assertThat(stack.search(1)).isEqualTo(3);
        }

        @Test
        @DisplayName("27. forEach로 순회")
        void test27_forEach() {
            // TODO: forEach 구현 후 테스트
            // Stack<Integer> stack = new ArrayStackImpl<>();
            // stack.push(1);
            // stack.push(2);
            // stack.push(3);
            // List<Integer> result = new ArrayList<>();
            // stack.forEach(result::add);
            // assertThat(result).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("28. equals로 스택 비교")
        void test28_equals() {
            // TODO: equals() 구현 후 테스트
            // Stack<Integer> stack1 = new ArrayStackImpl<>();
            // Stack<Integer> stack2 = new ArrayStackImpl<>();
            // stack1.push(1);
            // stack1.push(2);
            // stack2.push(1);
            // stack2.push(2);
            // assertThat(stack1).isEqualTo(stack2);
        }

        @Test
        @DisplayName("29. hashCode 일관성")
        void test29_hashCode() {
            // TODO: hashCode() 구현 후 테스트
            // Stack<Integer> stack1 = new ArrayStackImpl<>();
            // Stack<Integer> stack2 = new ArrayStackImpl<>();
            // stack1.push(1);
            // stack2.push(1);
            // assertThat(stack1.hashCode()).isEqualTo(stack2.hashCode());
        }

        @Test
        @DisplayName("30. toString 형식")
        void test30_toString() {
            // TODO: toString() 구현 후 테스트
            // Stack<Integer> stack = new ArrayStackImpl<>();
            // stack.push(1);
            // stack.push(2);
            // stack.push(3);
            // assertThat(stack.toString()).contains("1", "2", "3");
        }
    }
}
