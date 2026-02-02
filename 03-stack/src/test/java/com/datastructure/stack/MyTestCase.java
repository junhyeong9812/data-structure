package com.datastructure.stack;

import com.datastructure.stack.pop.ArrayStack;
import com.datastructure.stack.pop.LinkedStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

public class MyTestCase {

    @Nested
    @DisplayName("배열 기반 스택")
    class ArrayStackTest {
 
        ArrayStack stack ;
        @BeforeEach
        void setup() {
            stack = new ArrayStack();
        }

        @Nested
        @DisplayName("배열 기반 스택 생성 테스트")
        class Creation {

            @Test
            @DisplayName("배열 기반 스택이 생성된다.")
            void creation_success() {
                assertThat(stack).isNotNull();
                assertThat(stack.isEmpty()).isTrue();
                assertThat(stack.size()).isZero();
                // null이 아닌 지 확인한다
                // isEmpty인지 확인한다.
                // size가 0인지 확인한다.
            }
        }

        @Nested
        @DisplayName("push 메서드 테스트")
        class PushTest {
            @Test
            @DisplayName("빈 스택에 요소를 추가한다.")
            void push_emptyStack_success() {
                // 추가 후 size가 1인지 확인한다.
                // 추가 후 isEmpty가 false인지 확인한다.
                // top으로 값이 같은 지 확인한다.
                stack.push(1);

                assertThat(stack.size()).isEqualTo(1);
                assertThat(stack.isEmpty()).isFalse();
                assertThat(stack.top()).isEqualTo(1);
            }

            @Test
            @DisplayName("데이터가 있는 스택에 요소를 추가한다")
            void push_nonEmptyStack_success() {
                // 추가 size가 3인지 확인한다.
                // 추가 후 top으로 값이 같은 지 확인한다.
                stack.push(0);
                stack.push(1);
                stack.push(2);

                assertThat(stack.size()).isEqualTo(3);
                assertThat(stack.top()).isEqualTo(2);
            }

            @Test
            @DisplayName("null인 요소를 추가할 수 있다")
            void push_nullElement_success() {
                // 추가 size를 확인한다.
                // 추가 후 top으로 값을 확인한다.
                stack.push(null);

                assertThat(stack.size()).isEqualTo(1);
                assertThat(stack.top()).isNull();
            }
        }

        @Nested
        @DisplayName("pop 메서드 테스트")
        class PopTest {
            @Test
            @DisplayName("빈 스택에 요소를 제거한다")
            void pop_emptyStack_throwsException() {
                //익셉션이 정상적으로 일어나는 확인
                assertThatThrownBy(() -> stack.pop())
                        .isInstanceOf(EmptyStackException.class);
            }

            @Test
            @DisplayName("데이터가 있는 스택의 요소를 제거한다")
            void pop_nonEmptyStack_success() {
                // 사이즈 감소 확인
                // 반환값이 마지막 요소가 맞는 지 확인
                stack.push(0);
                stack.push(1);
                stack.push(2);
                Object popData = stack.pop();

                assertThat(stack.size()).isEqualTo(2);
                assertThat(popData).isEqualTo(2);
            }

            @Test
            @DisplayName("null인 요소를 제거한다")
            void pop_nullElement_success() {
                // 사이즈 감소 확인
                // 반환된 값이 null인지 확인
                stack.push(null);
                Object popData = stack.pop();
                assertThat(stack.size()).isZero();
                assertThat(popData).isNull();
            }
        }

        @Nested
        @DisplayName("peek 메서드 테스트")
        class PeekTest {

            @Test
            @DisplayName("빈 스택의 요소를 조회하면 예외가 발생한다")
            void peek_emptyStack_throwsException() {
                // 빈요소를 조회 시 익셉션이 나오는 지 확인한다.
                assertThatThrownBy(() -> stack.peek())
                        .isInstanceOf(EmptyStackException.class);
            }

            @Test
            @DisplayName("데이터가 있는 스택의 요소를 조회한다")
            void peek_nonEmptyStack_success() {
                // 조회한 데이터가 일치하는 지확인한다.
                // 사이즈를 확인한다.
                stack.push(1);
                Object peekData = stack.peek();
                assertThat(stack.size()).isOne();
                assertThat(peekData).isEqualTo(1);
            }

            @Test
            @DisplayName("null인 요소를 조회한다")
            void peek_nullElement_success() {
                // 조회한 데이터가 null인 지확인한다.
                // 사이즈를 확인한다.
                stack.push(null);
                Object peekNull = stack.peek();
                assertThat(stack.size()).isOne();
                assertThat(peekNull).isNull();
            }
        }

        @Nested
        @DisplayName("top 메서드 테스트")
        class TopTest {
            @Test
            @DisplayName("빈 스택의 요소를 조회하면 예외가 발생한다")
            void top_emptyStack_throwsException() {
                // 빈 스택에 사용시 예외가 발생한다.
                assertThatThrownBy(() -> stack.top())
                        .isInstanceOf(EmptyStackException.class);
            }

            @Test
            @DisplayName("데이터가 있는 요소에 데이터를 조회한다")
            void top_nonEmptyStack_success() {
                //데이터가 마지막으로 넣은 데이터와 일치하는 지 확인
                // 데이터 사이즈가 맞는 지 확인한다.
                stack.push(1);
                Object topData = stack.top();

                assertThat(stack.size()).isOne();
                assertThat(topData).isEqualTo(1);
            }

            @Test
            @DisplayName("null 요소를 조회한다")
            void top_nullElement_success() {
                // 데이터가 null인 지 조회한다.
                // 데이터 사이즈를 확인한다.
                stack.push(null);
                Object topData = stack.top();

                assertThat(stack.size()).isOne();
                assertThat(topData).isNull();
            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {

            @Test
            @DisplayName("빈 스택을 조회한다")
            void isEmpty_emptyStack_returnsTrue() {
                // 빈 스택일 떄 true
                assertThat(stack.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("데이터가 존재하는 스택을 조회한다.")
            void isEmpty_nonEmptyStack_returnFalse() {
                //데이터가 존재할 때 false
                stack.push(1);
                assertThat(stack.isEmpty()).isFalse();
            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {

            @Test
            @DisplayName("빈 스택의 사이즈가 0인지 확인한다")
            void size_emptyStack_returnZero() {
                // 사이즈가 0인지 확인한다.
                assertThat(stack.size()).isZero();
            }

            @Test
            @DisplayName("데이터가 있는 스택의 사이즈를 확인한다")
            void size_nonEmptyStack_returnCount() {
                // 사이즈가 일치하는 지 확인한다
                stack.push(1);
                stack.push(2);
                assertThat(stack.size()).isEqualTo(2);
            }
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {
            @Test
            @DisplayName("빈 요소일 때 동작하는 지 확인한다")
            void clear_emptyStack_success() {
                // 사이즈가 0인 지 확인한다.
                // isEmpty가 true인지 확인한다
                assertThatNoException().isThrownBy(() -> stack.clear());
                assertThat(stack.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("데이터가 있는 스택이 비워지는 지 확인한다")
            void clear_nonEmptyStack_success() {
                // clear 후 size가 0인 지 확인한다.
                // clear 후 isEmpty가 true인지 확인한다.
                stack.push(1);
                stack.push(2);

                stack.clear();
                assertThat(stack.isEmpty()).isTrue();
                assertThat(stack.size()).isZero();
            }
        }
    }

    @Nested
    @DisplayName("연결 리스트 기반 스택")
    class LinkedStackTest {

        LinkedStack stack ;
        @BeforeEach
        void setup() {
            stack = new LinkedStack();
        }

        @Nested
        @DisplayName("연결 리스트 기반 스택 생성 테스트")
        class Creation {

            @Test
            @DisplayName("연결 리스트 기반 스택이 생성된다.")
            void creation_success() {
                // null이 아닌 지 확인한다
                // isEmpty인지 확인한다.
                // size가 0인지 확인한다.
                assertThat(stack).isNotNull();
                assertThat(stack.isEmpty()).isTrue();
                assertThat(stack.size()).isZero();
            }
        }

        @Nested
        @DisplayName("push 메서드 테스트")
        class PushTest {
            @Test
            @DisplayName("빈 스택에 요소를 추가한다.")
            void push_emptyStack_success() {
                // 추가 후 size가 1인지 확인한다.
                // 추가 후 isEmpty가 false인지 확인한다.
                // top으로 값이 같은 지 확인한다.
                stack.push(1);
                assertThat(stack.size()).isEqualTo(1);
                assertThat(stack.isEmpty()).isFalse();
                assertThat(stack.top()).isEqualTo(1);
            }

            @Test
            @DisplayName("데이터가 있는 스택에 요소를 추가한다")
            void push_nonEmptyStack_success() {
                // 추가 size가 2인지 확인한다.
                // 추가 후 top으로 값이 같은 지 확인한다.
                stack.push(1);
                stack.push(2);

                assertThat(stack.size()).isEqualTo(2);
                assertThat(stack.top()).isEqualTo(2);
            }

            @Test
            @DisplayName("null인 요소를 추가할 수 있다")
            void push_nullElement_success() {
                // 추가 size를 확인한다.
                // 추가 후 top으로 값을 확인한다.
                stack.push(null);

                assertThat(stack.size()).isOne();
                assertThat(stack.top()).isNull();
            }
        }

        @Nested
        @DisplayName("pop 메서드 테스트")
        class PopTest {
            @Test
            @DisplayName("빈 스택에 요소를 제거한다")
            void pop_emptyStack_throwsException() {
                //익셉션이 정상적으로 일어나는 확인
                assertThatThrownBy(() -> stack.pop())
                        .isInstanceOf(EmptyStackException.class);
            }

            @Test
            @DisplayName("데이터가 있는 스택의 요소를 제거한다")
            void pop_nonEmptyStack_success() {
                // 사이즈 감소 확인
                // 반환값이 마지막 요소가 맞는 지 확인
                stack.push(1);
                stack.push(2);
                Object popData = stack.pop();
                assertThat(stack.size()).isOne();
                assertThat(popData).isEqualTo(2);
            }

            @Test
            @DisplayName("null인 요소를 제거한다")
            void pop_nullElement_success() {
                // 사이즈 감소 확인
                // 반환된 값이 null인지 확
                stack.push(null);

                Object popNull = stack.pop();
                assertThat(stack.size()).isZero();
                assertThat(popNull).isNull();
            }
        }

        @Nested
        @DisplayName("peek 메서드 테스트")
        class PeekTest {

            @Test
            @DisplayName("빈 스택의 요소를 조회하면 예외가 발생한다")
            void peek_emptyStack_throwsException() {
                // 빈요소를 조회 시 익셉션이 나오는 지 확인한다.
                assertThatThrownBy(() -> stack.peek())
                        .isInstanceOf(EmptyStackException.class);
            }

            @Test
            @DisplayName("데이터가 있는 스택의 요소를 조회한다")
            void peek_nonEmptyStack_success() {
                // 조회한 데이터가 일치하는 지확인한다.
                // 사이즈를 확인한다.
                stack.push(1);
                Object peekData = stack.peek();

                assertThat(stack.size()).isOne();
                assertThat(peekData).isEqualTo(1);
            }

            @Test
            @DisplayName("null인 요소를 조회한다")
            void peek_nullElement_success() {
                // 조회한 데이터가 null인 지확인한다.
                // 사이즈를 확인한다.
                stack.push(null);
                Object peekNull = stack.peek();
                assertThat(stack.size()).isOne();
                assertThat(peekNull).isNull();
            }
        }

        @Nested
        @DisplayName("top 메서드 테스트")
        class TopTest {
            @Test
            @DisplayName("빈 스택의 요소를 조회하면 예외가 발생한다")
            void top_emptyStack_throwsException() {
                // 빈 스택에 사용시 예외가 발생한다.
                assertThatThrownBy(() -> stack.top())
                        .isInstanceOf(EmptyStackException.class);
            }

            @Test
            @DisplayName("데이터가 있는 요소에 데이터를 조회한다")
            void top_nonEmptyStack_success() {
                //데이터가 마지막으로 넣은 데이터와 일치하는 지 확인
                // 데이터 사이즈가 맞는 지 확인한다.
                stack.push(1);
                stack.push(2);

                Object topData = stack.top();

                assertThat(stack.size()).isEqualTo(2);
                assertThat(topData).isEqualTo(2);
            }

            @Test
            @DisplayName("null 요소를 조회한다")
            void top_nullElement_success() {
                // 데이터가 null인 지 조회한다.
                // 데이터 사이즈를 확인한다.
                stack.push(null);

                Object topNull = stack.top();
                assertThat(stack.size()).isOne();
                assertThat(topNull).isNull();
            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {

            @Test
            @DisplayName("빈 스택을 조회한다")
            void isEmpty_emptyStack_returnsTrue() {
                // 빈 스택일 떄 true
                assertThat(stack.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("데이터가 존재하는 스택을 조회한다.")
            void isEmpty_nonEmptyStack_returnFalse() {
                //데이터가 존재할 때 false
                stack.push(1);

                assertThat(stack.isEmpty()).isFalse();
            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {

            @Test
            @DisplayName("빈 스택의 사이즈가 0인지 확인한다")
            void size_emptyStack_returnZero() {
                // 사이즈가 0인지 확인한다.
                assertThat(stack.size()).isZero();
            }

            @Test
            @DisplayName("데이터가 있는 스택의 사이즈를 확인한다")
            void size_nonEmptyStack_returnCount() {
                // 사이즈가 일치하는 지 확인한다
                stack.push(1);
                stack.push(2);
                stack.push(3);
                assertThat(stack.size()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {
            @Test
            @DisplayName("빈 요소일 때 동작하는 지 확인한다")
            void clear_emptyStack_success() {
                // 사이즈가 0인 지 확인한다.
                // isEmpty가 true인지 확인한다
                assertThatNoException().isThrownBy(() -> stack.clear());
                assertThat(stack.size()).isZero();
                assertThat(stack.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("데이터가 있는 스택이 비워지는 지 확인한다")
            void clear_nonEmptyStack_success() {
                // clear 후 size가 0인 지 확인한다.
                // clear 후 isEmpty가 true인지 확인한다.
                stack.push(1);
                stack.push(2);

                stack.clear();

                assertThat(stack.size()).isZero();
                assertThat(stack.isEmpty()).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("추가 기능 테스트")
    class AdditionalTest {
        @Nested
        @DisplayName("search 메서드 테스트")
        class SearchTest {
            @Test
            @DisplayName("빈 스택인 경우")
            void search_emptyStack_returnsMinusOne() {
                // -1 반환 확인

            }
            @Test
            @DisplayName("요소가 존재하는 경우")
            void search_existingElement_returnPosition() {

                // top=1 기준으로 위치 반환 확인
            }

            @Test
            @DisplayName("요소가 존재하지 않는 경우")
            void search_nonExistingElement_returnMinusOne() {
                // -1 반환 확인
            }
        }

        @Nested
        @DisplayName("toArray 메서드 테스트")
        class ToArrayTest {

            @Test
            @DisplayName("빈 스택인 경우")
            void toArray_emptyStack_returnsEmptyArray() {
                // 빈 배열이 반환된다.
            }

            @Test
            @DisplayName("요소가 존재하는 경우")
            void toArray_nonEmptyStack_returnsArray() {
//                배열 순서에 맞게 반환된다
            }
        }
    }

    @Nested
    @DisplayName("추가 응용 테스트")
    class ApplicationTest {
        @Nested
        @DisplayName("괄호 매칭 테스트")
        class ParenthesesTest {
            @Test
            @DisplayName("단순 괄호가 매칭된다 - ()")
            void parentheses_simple_returnsTrue() {

            }

            @Test
            @DisplayName("중괄호가 매칭된다 - {}")
            void parentheses_curly_returnsTrue() {

            }

            @Test
            @DisplayName("대괄호가 매칭된다 - []")
            void parentheses_square_returnsTrue() {

            }

            @Test
            @DisplayName("복합 괄호가 매칭된다 - ([{}])")
            void parentheses_nested_returnsTrue() {

            }

            @Test
            @DisplayName("여러 괄호가 매칭된다 - (){}[]")
            void parentheses_multiple_returnsTrue() {

            }

            @Test
            @DisplayName("짝이 맞지 않으면 false =(]")
            void parentheses_mismatch_returnsFalse() {

            }

            @Test
            @DisplayName("순서가 틀리면 false - ([)]")
            void parentheses_wrongOrder_returnsFalse() {

            }

            @Test
            @DisplayName("빈 문자열은 true")
            void parentheses_empty_returnsTrue() {

            }
        }

        @Nested
        @DisplayName("후위 표기법 계산 테스트")
        class PostfixEvaluationTest {

            @Test
            @DisplayName("덧셈 계산 - 3 4 +")
            void postfix_addition_success() {
                // 결과 7
            }

            @Test
            @DisplayName("뺄셈 계산 - 10 3 -")
            void postfix_substraction_success() {
                // 결과 7
            }

            @Test
            @DisplayName("나눗셈 계산 - 12 4 /")
            void postfix_division_success() {
                // 결과: 3
            }

            @Test
            @DisplayName("복합 수식 - 3 4 + 2 *")
            void postfix_complex_success() {
                //결과: 14
            }

            @Test
            @DisplayName("빈 수식이면 예외 발생")
            void postfix_empty_throwsException() {

            }

            @Test
            @DisplayName("피연산자 부족하면 예외 발 생 - 3 +")
            void postfix_insufficientOperands_throwsException() {

            }

            @Test
            @DisplayName("0으로 나누면 예외 발생")
            void postfix_divisionByZero_throwsException() {

            }
        }

        @Nested
        @DisplayName("중위->후위 변환 테스트")
        class InfixToPostfixTest {

            @Test
            @DisplayName("단순 수식 변환 - 3 + 4 -> 3 4 +")
            void infix_simple_success() {

            }

            @Test
            @DisplayName("연산자 우선 순위 적용 - 3 + 4 * 2 -> 3 4 2 * +")
            void infix_precedence_success() {

            }

            @Test
            @DisplayName("복합 수식 - ( 1 + 2) * ( 3 + 4) -> 1 2 + 3 4 + *")
            void infix_complex_success() {

            }

            @Test
            @DisplayName("빈 수식이면 빈 문자열 반환")
            void infix_empty_returnsEmpty() {

            }

            @Test
            @DisplayName("괄호가 맞지 않으면 예외 발생")
            void infix_mismatchedParentheses_throwsException() {

            }
        }
    }

}
