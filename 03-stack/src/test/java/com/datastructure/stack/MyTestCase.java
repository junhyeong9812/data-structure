package com.datastructure.stack;

import com.datastructure.stack.pop.ArrayStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

    }



}
