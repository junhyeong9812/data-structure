package com.datastructure.queue;

import com.datastructure.queue.pop.ArrayQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class MyTestCase {

    @Nested
    @DisplayName("배열 기반 큐")
    class ArrayQueueTest {

        ArrayQueue<Integer> queue;

        @BeforeEach
        void setup() {
             queue = new ArrayQueue<>();
        }

        @Nested
        @DisplayName("배열 기반 큐를 생성 테스트")
        class CreateTest {

            @Test
            @DisplayName("배열 기반 큐를 생성한다.")
            void create_emtpy_queue() {
                assertThat(queue.isEmpty()).isTrue();
                assertThat(queue.size()).isZero();
            }

            @Test
            @DisplayName("null 요소를 저장할 수 있다.")
            void create_queue_allows_null() {
                assertThatCode(() -> queue.enqueue(null))
                        .doesNotThrowAnyException();
            }

            @Test
            @DisplayName("초기 용량을 지정하여 큐를 생성한다.")
            void create_queue_with_capacity() {
                ArrayQueue<Integer> customQueue = new ArrayQueue<>(20);
                assertThat(customQueue.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("enqueue 메서드 테스트")
        class EnqueueTest {

        }

        @Nested
        @DisplayName("offer 메서드 테스트")
        class OfferTest {

        }

        @Nested
        @DisplayName("dequeue 메서드 테스트")
        class DequeueTest {

        }

        @Nested
        @DisplayName("poll 메서드 테스트")
        class PollTest {

        }

        @Nested
        @DisplayName("peek 메서드 테스트")
        class PeekTest {

        }

        @Nested
        @DisplayName("front 메서드 테스트")
        class FrontTest {

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
    @DisplayName("연결 리스트 기반 큐")
    class LinkedListQueueTest {

        @Nested
        @DisplayName("연결 리스트 기반 큐 생성 테스트")
        class CreateTest {

        }

        @Nested
        @DisplayName("enqueue 메서드 테스트")
        class EnqueueTest {

        }

        @Nested
        @DisplayName("offer 메서드 테스트")
        class OfferTest {

        }

        @Nested
        @DisplayName("dequeue 메서드 테스트")
        class DequeueTest {

        }

        @Nested
        @DisplayName("poll 메서드 테스트")
        class PollTest {

        }

        @Nested
        @DisplayName("peek 메서드 테스트")
        class PeekTest {

        }

        @Nested
        @DisplayName("front 메서드 테스트")
        class FrontTest {

        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmtpyTest {

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
    @DisplayName("원형 큐")
    class CircularQueueTest {

        @Nested
        @DisplayName("원형 큐 생성 테스트")
        class CreateTest {

        }

        @Nested
        @DisplayName("enqueue 메서드 테스트")
        class EnqueueTest {

        }

        @Nested
        @DisplayName("offer 메서드 테스트")
        class OfferTest {

        }

        @Nested
        @DisplayName("dequeue 메서드 테스트")
        class DequeueTest {

        }

        @Nested
        @DisplayName("poll 메서드 테스트")
        class PollTest {

        }

        @Nested
        @DisplayName("peek 메서드 테스트")
        class PeekTest {

        }

        @Nested
        @DisplayName("front 메서드 테스트")
        class FrontTest {

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
    @DisplayName("배열 기반 덱")
    class ArrayDequeTest {

        @Nested
        @DisplayName("배열 기반 덱 생성 테스트")
        class CreateTest {

        }

        @Nested
        @DisplayName("addFirst 메서드 테스트")
        class AddFirstTest {}

        @Nested
        @DisplayName("addLast 메서드 테스트")
        class AddLastTest {}

        @Nested
        @DisplayName("removeFirst 메서드 테스트")
        class RemoveFirstTest {}

        @Nested
        @DisplayName("removeLast 메서드 테스트")
        class RemoveLastTest {}

        @Nested
        @DisplayName("peekFirst 메서드 테스트")
        class PeekFirstTest {}

        @Nested
        @DisplayName("peekLast 메서드 테스트")
        class PeekLastTest {}
    }

    @Nested
    @DisplayName("연결 리스트 기반 덱")
    class LinkedListDequeTest {

        @Nested
        @DisplayName("연결 리스트 기반 덱")
        class CreateTest {}

        @Nested
        @DisplayName("addFirst 메서드 테스트")
        class AddFirstTest {}

        @Nested
        @DisplayName("addLast 메서드 테스트")
        class AddLastTest {}

        @Nested
        @DisplayName("removeFirst 메서드 테스트")
        class removeFirstTest {}

        @Nested
        @DisplayName("removeLast 메서드 테스트")
        class removeLastTest {}

        @Nested
        @DisplayName("peekFirst 메서드 테스트")
        class peekFirstTest {}

        @Nested
        @DisplayName("peekLast 메서드 테스트")
        class peekLastTest {}
    }


    @Nested
    @DisplayName("추가 응용 테스트")
    class ApplicationTest {
        @Nested
        @DisplayName("슬라이딩 윈도우 최댓값")
        class SlidingWindowMaxTest {}

        @Nested
        @DisplayName("최근 요청 카운터")
        class RecentRequestCounterTest {

        }
    }

}
