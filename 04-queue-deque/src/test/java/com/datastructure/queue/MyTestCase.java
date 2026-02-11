package com.datastructure.queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MyTestCase {

    @Nested
    @DisplayName("배열 기반 큐")
    class ArrayQueueTest {

        @Nested
        @DisplayName("배열 기반 큐를 생성할 수 있다")
        class Creation {

            @Test
            @DisplayName("배열 기반 큐를 생성한다.")
            void test1() {

            }
        }

    }

    @Nested
    @DisplayName("연결 리스트 기반 큐")
    class LinkedListQueueTest {

    }

    @Nested
    @DisplayName("원형 큐")
    class CircularQueueTest {

    }

    @Nested
    @DisplayName("배열 기반 덱")
    class ArrayDequeTest {

    }

    @Nested
    @DisplayName("연결 리스트 기반 덱")
    class LinkedListDequeTest {

    }


    @Nested
    @DisplayName("추가 응용 테스트")
    class ApplicationTest {

    }

}
