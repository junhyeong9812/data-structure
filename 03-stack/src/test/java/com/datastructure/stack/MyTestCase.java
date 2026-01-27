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
