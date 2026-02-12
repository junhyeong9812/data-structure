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
            @DisplayName("초기 용량을 지정하여 큐를 생성한다.")
            void create_queue_with_capacity() {
                ArrayQueue<Integer> customQueue = new ArrayQueue<>(20);
                assertThat(customQueue.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("enqueue 메서드 테스트")
        class EnqueueTest {

            @Test
            @DisplayName("빈 배열 기반 큐에 요소를 추가할 수 있다.")
            void enqueue_to_empty_queue() {

                queue.enqueue(10);

                assertThat(queue.peek()).isEqualTo(10);
                assertThat(queue.size()).isOne();
            }

            @Test
            @DisplayName("요소가 있는 배열 기반 큐에 요소를 추가할 수 있다.")
            void enqueue_to_non_empty_queue() {
                queue.enqueue(1);
                queue.enqueue(2);
                queue.enqueue(3);

                assertThat(queue.peek()).isEqualTo(1);
                assertThat(queue.size()).isEqualTo(3);
            }

            @Test
            @DisplayName("null 요소를 저장할 수 있다.")
            void enqueue_null_element() {
                assertThatCode(() -> queue.enqueue(null))
                        .doesNotThrowAnyException();
            }

            @Test
            @DisplayName("여러 요소를 순차적으로 추가할 수 있다.")
            void enqueue_multiple_elements() {
                for (int i = 0; i < 5; i++) {
                    queue.enqueue(i);
                }
                assertThat(queue.size()).isEqualTo(5);
                assertThat(queue.peek()).isEqualTo(0);
            }

            @Test
            @DisplayName("용량 초과 시 자동으로 확장된다.")
            void enqueue_expands_capacity_when_full() {
                for (int i = 0; i < 10; i++) {
                    queue.enqueue(i);
                }
                queue.enqueue(11);

                assertThat(queue.size()).isEqualTo(11);
                assertThat(queue.peek()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("offer 메서드 테스트")
        class OfferTest {

            @Test
            @DisplayName("빈 배열 기반 큐에 요소를 추가할 수 있다.")
            void offer_to_empty_queue() {
                queue.offer(1);

                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.peek()).isEqualTo(1);
            }

            @Test
            @DisplayName("요소를 추가하면 True를 반환한다.")
            void offer_returns_true_on_success() {
                boolean result = queue.offer(1);

                assertThat(result).isTrue();
            }

            @Test
            @DisplayName("요소가 있는 배열 기반 큐에 요소를 추가할 수 있다.")
            void offer_to_non_empty_queue() {
                queue.offer(0);
                queue.offer(1);

                assertThat(queue.size()).isEqualTo(2);
                assertThat(queue.peek()).isEqualTo(0);
            }

            @Test
            @DisplayName("배열 기반 큐에 null을 추가할 수 있다.")
            void offer_null_element() {
                queue.offer(null);

                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.peek()).isNull();
            }

            @Test
            @DisplayName("여러 요소를 순차적으로 추가할 수 있다.")
            void offer_multiple_elements() {
                for (int i = 0; i < 10; i++) {
                    queue.offer(i);
                }
                assertThat(queue.dequeue()).isEqualTo(0);
                assertThat(queue.dequeue()).isEqualTo(1);
                assertThat(queue.dequeue()).isEqualTo(2);
                assertThat(queue.dequeue()).isEqualTo(3);
            }

            @Test
            @DisplayName("용량 초과 시 자동으로 확장된다.")
            void offer_expands_capacity_when_full() {
                for (int i = 0; i < 10; i++) {
                    queue.offer(i);
                }
                boolean result = queue.offer(1);

                assertThat(result).isTrue();
                assertThat(queue.size()).isEqualTo(11);
            }
        }

        @Nested
        @DisplayName("dequeue 메서드 테스트")
        class DequeueTest {

            @Test
            @DisplayName("빈 배열 기반 큐에 dequeue 시 예외가 발생한다.")
            void dequeue_throws_exception_when_empty() {
                assertThatThrownBy(() -> queue.dequeue())
                        .isInstanceOf(Exception.class);
                //큐의 익셉션이 어떤건지 확인할 필요가 있다.
            }

            @Test
            @DisplayName("요소가 존재하는 배열 기반 큐에 요소를 제거한다.")
            void dequeue_removes_element_from_non_empty_queue() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                int first = queue.dequeue();
                assertThat(first).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("null 요소를 제거할 수 있다.")
            void dequeue_removes_null_element() {
                queue.enqueue(null);

                assertThat(queue.dequeue()).isNull();
                assertThat(queue.isEmpty()).isTrue();
                assertThat(queue.size()).isZero();
            }

            @Test
            @DisplayName("여러 요소를 순차적으로 제거할 수 있다.")
            void dequeue_multiple_elements_in_fifo_order() {
                for(int i = 0; i < 10; i++) {
                    queue.enqueue(i);
                }

                assertThat(queue.dequeue()).isEqualTo(0);
                assertThat(queue.dequeue()).isEqualTo(1);
                assertThat(queue.dequeue()).isEqualTo(2);
                assertThat(queue.size()).isEqualTo(7);
            }

            @Test
            @DisplayName("요소를 제거 후 사이즈가 감소한다.")
            void dequeue_decreases_size() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                queue.dequeue();

                assertThat(queue.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("용량 축소 임계값 이하로 감소 시 정상 동작한다.")
            void dequeue_shrinks_capacity_when_below_threshold() {
                for (int i = 0; i < 15; i++) {
                    queue.enqueue(i);
                }
                for (int i = 0; i < 11; i++) {
                    queue.dequeue();
                }

                assertThat(queue.size()).isEqualTo(4);
                assertThat(queue.peek()).isEqualTo(12);
            }
        }

        @Nested
        @DisplayName("poll 메서드 테스트")
        class PollTest {

            @Test
            @DisplayName("빈 배열 기반 큐에 poll 시 null을 반환한다.")
            void poll_returns_null_when_empty() {
                assertThat(queue.isEmpty()).isTrue();
                assertThat(queue.size()).isZero();
                assertThat(queue.poll()).isNull();
            }

            @Test
            @DisplayName("요소가 존재하는 배열 기반 큐에 요소를 제거한다.")
            void poll_removes_element_from_non_empty_queue() {
                queue.enqueue(0);

                int result = queue.poll();
                assertThat(result).isZero();
                assertThat(queue.isEmpty()).isTrue();
                assertThat(queue.size()).isZero();
            }

            @Test
            @DisplayName("null 요소를 제거할 수 있다.")
            void poll_removes_null_element() {
                queue.enqueue(null);
                assertThat(queue.size()).isEqualTo(1);

                Object result = queue.poll();
                assertThat(result).isNull();
                assertThat(queue.size()).isZero();
            }

            @Test
            @DisplayName("여러 요소를 순차적으로 제거할 수 있다.")
            void poll_multiple_elements_in_fifo_order() {
                for (int i = 0; i < 3; i++) {
                    queue.enqueue(i);
                }

                assertThat(queue.poll()).isEqualTo(0);
                assertThat(queue.poll()).isEqualTo(1);
                assertThat(queue.poll()).isEqualTo(2);
                assertThat(queue.poll()).isNull();
                assertThat(queue.size()).isZero();
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소를 제거 후 사이즈가 감소한다.")
            void poll_decreases_size() {
                for (int i = 0; i < 5; i++) {
                    queue.enqueue(i);
                }
                assertThat(queue.size()).isEqualTo(5);

                int result = queue.poll();

                assertThat(queue.size()).isEqualTo(4);
                assertThat(result).isEqualTo(0);
            }

            @Test
            @DisplayName("용량 축소 임계값 이하로 감소 시 정상 동작한다.")
            void poll_shrinks_capacity_when_below_threshold() {
                for (int i = 0; i < 15; i++) {
                    queue.enqueue(i);
                }
                for (int i = 0; i < 11; i++) {
                    queue.poll();
                }

                assertThat(queue.size()).isEqualTo(4);
                assertThat(queue.peek()).isEqualTo(12);
            }
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
