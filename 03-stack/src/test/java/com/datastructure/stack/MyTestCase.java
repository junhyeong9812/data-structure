package com.datastructure.stack;

import com.datastructure.stack.pop.ArrayStack;
import com.datastructure.stack.pop.LinkedStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            }

            @Test
            @DisplayName("데이터가 있는 스택에 요소를 추가한다")
            void push_nonEmptyStack_success() {
                // 추가 size가 2인지 확인한다.
                // 추가 후 top으로 값이 같은 지 확인한다.
            }

            @Test
            @DisplayName("null인 요소를 추가할 수 있다")
            void push_nullElement_success() {
                // 추가 size를 확인한다.
                // 추가 후 top으로 값을 확인한다.
            }
        }

        @Nested
        @DisplayName("pop 메서드 테스트")
        class PopTest {
            @Test
            @DisplayName("빈 스택에 요소를 제거한다")
            void pop_emptyStack_throwsException() {
                //익셉션이 정상적으로 일어나는 확인
            }

            @Test
            @DisplayName("데이터가 있는 스택의 요소를 제거한다")
            void pop_nonEmptyStack_success() {
                // 사이즈 감소 확인
                // 반환값이 마지막 요소가 맞는 지 확인
            }

            @Test
            @DisplayName("null인 요소를 제거한다")
            void pop_nullElement_success() {
                // 사이즈 감소 확인
                // 반환된 값이 null인지 확
            }
        }

        @Nested
        @DisplayName("peek 메서드 테스트")
        class PeekTest {

            @Test
            @DisplayName("빈 스택의 요소를 조회하면 예외가 발생한다")
            void peek_emptyStack_throwsException() {
                // 빈요소를 조회 시 익셉션이 나오는 지 확인한다.
            }

            @Test
            @DisplayName("데이터가 있는 스택의 요소를 조회한다")
            void peek_nonEmptyStack_success() {
                // 조회한 데이터가 일치하는 지확인한다.
                // 사이즈를 확인한다.
            }

            @Test
            @DisplayName("null인 요소를 조회한다")
            void peek_nullElement_success() {
                // 조회한 데이터가 null인 지확인한다.
                // 사이즈를 확인한다.
            }
        }

        @Nested
        @DisplayName("top 메서드 테스트")
        class TopTest {
            @Test
            @DisplayName("빈 스택의 요소를 조회하면 예외가 발생한다")
            void top_emptyStack_throwsException() {
                // 빈 스택에 사용시 예외가 발생한다.
            }

            @Test
            @DisplayName("데이터가 있는 요소에 데이터를 조회한다")
            void top_nonEmptyStack_success() {
                //데이터가 마지막으로 넣은 데이터와 일치하는 지 확인
                // 데이터 사이즈가 맞는 지 확인한다.
            }

            @Test
            @DisplayName("null 요소를 조회한다")
            void top_nullElement_success() {
                // 데이터가 null인 지 조회한다.
                // 데이터 사이즈를 확인한다.
            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {

            @Test
            @DisplayName("빈 스택을 조회한다")
            void isEmpty_emptyStack_returnsTrue() {
                // 빈 스택일 떄 true
            }

            @Test
            @DisplayName("데이터가 존재하는 스택을 조회한다.")
            void isEmpty_nonEmptyStack_returnFalse() {
                //데이터가 존재할 때 false
            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {

            @Test
            @DisplayName("빈 스택의 사이즈가 0인지 확인한다")
            void size_emptyStack_returnZero() {
                // 사이즈가 0인지 확인한다.
            }

            @Test
            @DisplayName("데이터가 있는 스택의 사이즈를 확인한다")
            void size_nonEmptyStack_returnCount() {
                // 사이즈가 일치하는 지 확인한다
            }
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {

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

        }

        @Nested
        @DisplayName("push 메서드 테스트")
        class PushTest {

        }

        @Nested
        @DisplayName("pop 메서드 테스트")
        class PopTest {

        }

        @Nested
        @DisplayName("peek 메서드 테스트")
        class peekTest {

        }

        @Nested
        @DisplayName("top 메서드 테스트")
        class TopTest {

        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {

        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {

        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {

        }
    }

    @Nested
    @DisplayName("추가 기능 테스트")
    class AdditionalTest {
        @Nested
        @DisplayName("search 메서드 테스트")
        class SearchTest {

        }

        @Nested
        @DisplayName("toArray 메서드 테스트")
        class ToArrayTest {

        }
    }

    @Nested
    @DisplayName("추가 응용 테스트")
    class ApplicationTest {
        @Nested
        @DisplayName("괄호 매칭 테스트")
        class ParenthesesTest {

        }

        @Nested
        @DisplayName("후위 표기법 계산 테스트")
        class PostfixEvaluationTest {

        }

        @Nested
        @DisplayName("중위->후위 변환 테스트")
        class InfixToPostfixTest {

        }
    }

}
