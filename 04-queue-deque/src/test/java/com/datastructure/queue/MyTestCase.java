package com.datastructure.queue;

import com.datastructure.queue.oop.Queue;
import com.datastructure.queue.pop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

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
                        .isInstanceOf(NoSuchElementException.class);
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
                assertThat(queue.peek()).isEqualTo(11);
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
                assertThat(queue.peek()).isEqualTo(11);
            }
        }

        @Nested
        @DisplayName("peek 메서드 테스트")
        class PeekTest {

            @Test
            @DisplayName("빈 큐에서 peek 시 null을 반환한다.")
            void peek_returns_null_when_empty() {
                assertThat(queue.isEmpty()).isTrue();
                assertThat(queue.peek()).isNull();
            }

            @Test
            @DisplayName("앞 요소를 조회할 수 있다.")
            void peek_returns_front_element() {
                queue.enqueue(0);
                queue.enqueue(1);

                assertThat(queue.peek()).isZero();
                assertThat(queue.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("앞 요소가 null인 경우 null을 반환한다.")
            void peek_returns_null_element() {
                queue.enqueue(null);
                queue.enqueue(1);

                assertThat(queue.peek()).isNull();
                assertThat(queue.isEmpty()).isFalse();
                assertThat(queue.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("peek 후 큐가 변경되지 않는다.")
            void peek_does_not_modify_queue() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.peek()).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(3);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("여러 번 조회해도 같은 요소를 반환한다.")
            void peek_multiple_times_returns_same_element() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.peek()).isEqualTo(0);
                assertThat(queue.peek()).isEqualTo(0);
                assertThat(queue.peek()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("front 메서드 테스트")
        class FrontTest {

            @Test
            @DisplayName("빈 큐에 front 시 null을 반환한다.")
            void front_returns_null_when_empty() {
                assertThat(queue.isEmpty()).isTrue();
                assertThat(queue.front()).isNull();
            }

            @Test
            @DisplayName("앞 요소를 조회할 수 있다.")
            void front_returns_front_element() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.front()).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(3);
            }

            @Test
            @DisplayName("앞 요소가 null인 경우 null을 반환한다.")
            void front_returns_null_element() {
                queue.enqueue(null);
                queue.enqueue(1);

                assertThat(queue.isEmpty()).isFalse();
                assertThat(queue.size()).isEqualTo(2);
                assertThat(queue.front()).isNull();
            }

            @Test
            @DisplayName("front 후 큐가 변경되지 않는다.")
            void front_does_not_modify_queue() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.front()).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(3);
            }

            @Test
            @DisplayName("여러번 조회해도 같은 요소를 반환한다.")
            void front_multiple_times_returns_same_element() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.front()).isEqualTo(0);
                assertThat(queue.front()).isEqualTo(0);
                assertThat(queue.front()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {

            @Test
            @DisplayName("빈 큐는 true를 반환한다.")
            void isEmpty_returns_true_when_empty() {
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 있는 큐는 false를 반환한다.")
            void isEmpty_returns_false_when_not_empty() {
                queue.enqueue(1);

                assertThat(queue.isEmpty()).isFalse();
            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {
            @Test
            @DisplayName("빈 큐의 사이즈는 0이다.")
            void size_returns_zero_when_empty() {
                assertThat(queue.size()).isZero();
            }

            @Test
            @DisplayName("요소가 있는 큐의 사이즈는 요소의 갯수이다.")
            void size_returns_number_of_elements() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.size()).isEqualTo(3);
            }

            @Test
            @DisplayName("enqueue 후 사이즈가 증가한다.")
            void size_increases_after_enqueue() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.size()).isEqualTo(3);

                queue.enqueue(3);

                assertThat(queue.size()).isEqualTo(4);
            }

            @Test
            @DisplayName("dequeue 후 사이즈가 감소한다.")
            void size_decreases_after_dequeue() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.size()).isEqualTo(3);

                queue.dequeue();

                assertThat(queue.size()).isEqualTo(2);
            }
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {

            @Test
            @DisplayName("빈 큐에 clear 시 변화가 없다.")
            void clear_empty_queue_remains_empty() {
                assertThatNoException().isThrownBy(() -> queue.clear());
            }

            @Test
            @DisplayName("요소가 있는 큐에 clear 시 빈 큐가 된다.")
            void clear_removes_all_elements() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                queue.clear();

                assertThat(queue.size()).isZero();
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("clear 후 isEmpty는 true를 반환한다.")
            void clear_makes_queue_empty() {
                queue.enqueue(1);

                queue.clear();

                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("clear 후 size는 0일 반환한다.")
            void clear_reset_size_to_zero() {
                queue.enqueue(1);

                queue.clear();

                assertThat(queue.size()).isZero();
            }

            @Test
            @DisplayName("clear 후 다시 요소를 추가할 수 있다.")
            void clear_allows_enqueue_after_clear() {
                queue.enqueue(1);
                queue.clear();

                queue.enqueue(2);

                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.peek()).isEqualTo(2);
            }
        }
    }

    @Nested
    @DisplayName("연결 리스트 기반 큐")
    class LinkedListQueueTest {

        LinkedListQueue<Integer> queue;

        @BeforeEach
        void setUp() {queue = new LinkedListQueue<>();}

        @Nested
        @DisplayName("연결 리스트 기반 큐 생성 테스트")
        class CreateTest {

            @Test
            @DisplayName("연결 리스트 기반 큐를 생성할 수 있다.")
            void create_linked_list_queue() {
                assertThat(queue.size()).isZero();
                assertThat(queue.isEmpty()).isTrue();
                assertThat(queue).isNotNull();
            }
        }

        @Nested
        @DisplayName("enqueue 메서드 테스트")
        class EnqueueTest {

            @Test
            @DisplayName("빈 큐에 값을 요소를 넣을 수 있다.")
            void enqueue_to_empty_queue() {
                queue.enqueue(0);

                assertThat(queue.size()).isOne();
                assertThat(queue.isEmpty()).isFalse();
                assertThat(queue.peek()).isEqualTo(0);
            }

            @Test
            @DisplayName("요소가 있는 큐에 요소를 넣을 수 있다.")
            void enqueue_to_non_empty_queue() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.size()).isEqualTo(3);
                assertThat(queue.peek()).isEqualTo(0);
            }

            @Test
            @DisplayName("큐에 null 요소를 넣을 수 있다.")
            void enqueue_null_element() {
                queue.enqueue(null);

                assertThat(queue.size()).isOne();
                assertThat(queue.isEmpty()).isFalse();
                assertThat(queue.peek()).isNull();
            }

            @Test
            @DisplayName("요소가 증가하면 카운트가 증가한다.")
            void enqueue_increases_size() {
                queue.enqueue(0);
                assertThat(queue.size()).isEqualTo(1);

                queue.enqueue(1);
                assertThat(queue.size()).isEqualTo(2);

                queue.enqueue(2);
                assertThat(queue.size()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("offer 메서드 테스트")
        class OfferTest {
            @Test
            @DisplayName("빈 큐에 값을 요소를 넣을 수 있다.")
            void offer_to_empty_queue() {
                boolean result = queue.offer(0);

                assertThat(result).isTrue();
                assertThat(queue.size()).isOne();
                assertThat(queue.isEmpty()).isFalse();
                assertThat(queue.peek()).isEqualTo(0);
            }

            @Test
            @DisplayName("요소가 있는 큐에 요소를 넣을 수 있다.")
            void offer_to_non_empty_queue() {
                boolean result0 = queue.offer(0);
                boolean result1 = queue.offer(1);
                boolean result2 = queue.offer(2);

                assertThat(result0).isTrue();
                assertThat(result1).isTrue();
                assertThat(result2).isTrue();
                assertThat(queue.size()).isEqualTo(3);
                assertThat(queue.peek()).isEqualTo(0);
            }

            @Test
            @DisplayName("큐에 null 요소를 넣을 수 있다.")
            void offer_null_element() {
                boolean result = queue.offer(null);

                assertThat(result).isTrue();
                assertThat(queue.size()).isOne();
                assertThat(queue.isEmpty()).isFalse();
                assertThat(queue.peek()).isNull();
            }

            @Test
            @DisplayName("요소가 증가하면 카운트가 증가한다.")
            void offer_increases_size() {
                queue.offer(0);
                assertThat(queue.size()).isEqualTo(1);

                queue.offer(1);
                assertThat(queue.size()).isEqualTo(2);

                queue.offer(2);
                assertThat(queue.size()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("dequeue 메서드 테스트")
        class DequeueTest {

            @Test
            @DisplayName("요소가 없는 큐에 dequeue 시 예외가 발생한다.")
            void dequeue_throws_exception_when_empty() {
                assertThatThrownBy(() -> queue.dequeue())
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("하나의 요소가 있는 큐에 dequeue 시 빈 큐가 된다.")
            void dequeue_single_element_makes_queues_empty() {
                queue.enqueue(0);

                int result = queue.dequeue();

                assertThat(result).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(0);
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("dequeue 후 size가 감소한다.")
            void dequeue_decreases_size() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);
                assertThat(queue.size()).isEqualTo(3);

                queue.dequeue();

                assertThat(queue.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("null 요소를 제거할 수 있다.")
            void dequeue_removes_null_element() {
                queue.enqueue(null);
                assertThat(queue.size()).isEqualTo(1);

                queue.dequeue();
                assertThat(queue.size()).isZero();
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("여러 요소를 순차적으로 제거할 수 있다.")
            void dequeue_multiple_elements_fifo_order() {
                for (int i = 0; i < 5; i++) {
                    queue.enqueue(i);
                }
                for (int i = 0; i < 5; i++) {
                    assertThat(queue.dequeue()).isEqualTo(i);
                }
            }
        }

        @Nested
        @DisplayName("poll 메서드 테스트")
        class PollTest {
            @Test
            @DisplayName("빈 큐에서 poll 시 null을 반환한다.")
            void poll_returns_null_when_empty() {
                assertThat(queue.poll()).isNull();
            }

            @Test
            @DisplayName("하나의 요소가 있는 큐에 poll 시 빈 큐가 된다.")
            void poll_single_element_makes_queues_empty() {
                queue.enqueue(0);

                int result = queue.poll();

                assertThat(result).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(0);
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("poll 후 size가 감소한다.")
            void poll_decreases_size() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);
                assertThat(queue.size()).isEqualTo(3);

                queue.poll();

                assertThat(queue.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("null 요소를 제거할 수 있다.")
            void poll_removes_null_element() {
                queue.enqueue(null);
                assertThat(queue.size()).isEqualTo(1);

                queue.poll();
                assertThat(queue.size()).isZero();
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("여러 요소를 순차적으로 제거할 수 있다.")
            void poll_multiple_elements_fifo_order() {
                for (int i = 0; i < 5; i++) {
                    queue.enqueue(i);
                }
                for (int i = 0; i < 5; i++) {
                    assertThat(queue.poll()).isEqualTo(i);
                }
            }
        }

        @Nested
        @DisplayName("peek 메서드 테스트")
        class PeekTest {

            @Test
            @DisplayName("빈 큐에서 peek 시 null을 반환한다.")
            void peek_returns_null_when_empty() {
                assertThat(queue.peek()).isNull();
            }

            @Test
            @DisplayName("단일 요소를 조회할 수 있다.")
            void peek_returns_single_element() {
                queue.enqueue(0);

                assertThat(queue.peek()).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("요소가 여러개일 때 맨 앞 요소를 조회할 수 있다.")
            void peek_returns_front_element() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.peek()).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(3);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("null인 요소를 조회할 수 있다.")
            void peek_return_null_element() {
                queue.enqueue(null);

                assertThat(queue.peek()).isNull();
                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("peek 후 큐가 변경되지 않는다.")
            void peek_does_not_modify_queue() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.peek()).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(3);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("여러 번 조회해도 같은 요소를 반환한다.")
            void peek_multiple_times_returns_same_element() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.peek()).isEqualTo(0);
                assertThat(queue.peek()).isEqualTo(0);
                assertThat(queue.peek()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("front 메서드 테스트")
        class FrontTest {
            @Test
            @DisplayName("빈 큐에서 front 시 null을 반환한다.")
            void front_returns_null_when_empty() {
                assertThat(queue.front()).isNull();
            }

            @Test
            @DisplayName("단일 요소를 조회할 수 있다.")
            void front_returns_single_element() {
                queue.enqueue(0);

                assertThat(queue.front()).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("요소가 여러개일 때 맨 앞 요소를 조회할 수 있다.")
            void front_returns_front_element() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.front()).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(3);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("null인 요소를 조회할 수 있다.")
            void front_return_null_element() {
                queue.enqueue(null);

                assertThat(queue.front()).isNull();
                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("front 후 큐가 변경되지 않는다.")
            void front_does_not_modify_queue() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.front()).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(3);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("여러 번 조회해도 같은 요소를 반환한다.")
            void front_multiple_times_returns_same_element() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.front()).isEqualTo(0);
                assertThat(queue.front()).isEqualTo(0);
                assertThat(queue.front()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {

            @Test
            @DisplayName("빈 큐는 true를 반환한다.")
            void isEmpty_returns_true_when_empty() {
                assertThat(queue.isEmpty()).isTrue();
            }
            @Test
            @DisplayName("요소가 있는 큐는 false를 반환한다.")
            void isEmpty_returns_false_when_not_empty() {
                queue.enqueue(1);
                assertThat(queue.isEmpty()).isFalse();
            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {

            @Test
            @DisplayName("빈 큐는 0이다.")
            void size_returns_zero_when_empty() {
                assertThat(queue.size()).isZero();
            }

            @Test
            @DisplayName("요소가 있는 큐는 요소의 갯수이다.")
            void size_returns_number_of_elements() {
                for (int i = 0; i < 5; i++) {
                    queue.enqueue(i);
                }

                assertThat(queue.size()).isEqualTo(5);
            }

            @Test
            @DisplayName("enqueue 후 size가 증가한다.")
            void size_increases_after_enqueue() {
                for (int i = 0; i < 5; i++) {
                    queue.enqueue(i);
                }
                assertThat(queue.size()).isEqualTo(5);

                queue.enqueue(6);

                assertThat(queue.size()).isEqualTo(6);
            }

            @Test
            @DisplayName("dequeue 후 size가 감소한다.")
            void size_decreases_after_dequeue() {
                for (int i = 0; i < 5; i++) {
                    queue.enqueue(i);
                }
                assertThat(queue.size()).isEqualTo(5);

                queue.dequeue();

                assertThat(queue.size()).isEqualTo(4);
            }

        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {

            @Test
            @DisplayName("빈 큐에 clear를 적용할 수 있다.")
            void clear_empty_queue_remains_empty() {
                assertThatNoException().isThrownBy(() -> queue.clear());
            }

            @Test
            @DisplayName("clear 후 빈 큐가 된다.")
            void clear_removes_all_elements() {
                queue.enqueue(0);

                queue.clear();

                assertThat(queue.size()).isEqualTo(0);
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("clear 후 size는 0을 반환한다.")
            void clear_resets_size_to_zero() {
                queue.enqueue(0);

                queue.clear();

                assertThat(queue.size()).isEqualTo(0);
            }

            @Test
            @DisplayName("clear 후 isEmpty는 true를 반환한다.")
            void clear_makes_queue_empty() {
                queue.enqueue(0);

                queue.clear();

                assertThat(queue.isEmpty()).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("원형 큐")
    class CircularQueueTest {

        CircularQueue<Integer> queue;

        @BeforeEach
        void setup() {
            queue  = new CircularQueue<>();
        }

        @Nested
        @DisplayName("원형 큐 생성 테스트")
        class CreateTest {

            @Test
            @DisplayName("생성된 원형 큐의 사이즈는 0이다.")
            void create_circular_queue_size_is_zero() {
                assertThat(queue.size()).isZero();
            }

            @Test
            @DisplayName("생성된 원형 큐의 isEmpty는 True이다.")
            void create_circular_queue_isEmpty_is_true() {
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("초기 용량을 지정하여 원형 큐를 생성한다.")
            void create_circular_queue_with_capacity() {
                CircularQueue<Integer> customQueue = new CircularQueue<>(20);
                assertThat(customQueue.isEmpty()).isTrue();
                assertThat(customQueue.size()).isZero();
            }
        }

        @Nested
        @DisplayName("enqueue 메서드 테스트")
        class EnqueueTest {

            @Test
            @DisplayName("빈 큐에 요소가 추가된다.")
            void enqueue_to_empty_queue() {
                queue.enqueue(1);

                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("요소가 있는 큐에 요소가 추가된다.")
            void enqueue_to_non_empty_queue() {
                queue.enqueue(1);
                queue.enqueue(2);
                queue.enqueue(3);

                queue.enqueue(4);

                assertThat(queue.size()).isEqualTo(4);
                assertThat(queue.peek()).isEqualTo(1);
            }

            @Test
            @DisplayName("null 요소가 추가된다.")
            void enqueue_null_element() {
                queue.enqueue(null);

                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.isEmpty()).isFalse();
                assertThat(queue.peek()).isNull();
            }

            @Test
            @DisplayName("요소가 가득 찬 큐에는 예외가 발생한다.")
            void enqueue_throws_exception_when_full() {
                queue = new CircularQueue<>(10);
                for (int i = 0; i < 10; i++) {
                    queue.enqueue(i);
                }

                assertThatThrownBy(() -> queue.enqueue(11))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            @DisplayName("dequeue 후 빈 자리에 요소를 추가할 수 있다.")
            void enqueue_after_dequeue_reuses_space() {
                for (int i = 0; i < 10; i++) {
                    queue.enqueue(i);
                }
                queue.dequeue();
                queue.enqueue(11);
                assertThat(queue.size()).isEqualTo(10);
                assertThat(queue.peek()).isEqualTo(1);
            }

            @Test
            @DisplayName("enqueue 후 size가 증가한다.")
            void enqueue_increase_size() {
                assertThat(queue.size()).isEqualTo(0);
                queue.enqueue(1);

                assertThat(queue.size()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("offer 메서드 테스트")
        class OfferTest {

            @Test
            @DisplayName("빈 큐에 요소를 추가하면 true를 반환한다.")
            void offer_to_empty_queue_returns_true() {
                boolean result = queue.offer(1);

                assertThat(result).isTrue();
                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("요소가 있는 큐에 요소를 추가하면 true를 반환한다.")
            void offer_to_non_empty_queue_returns_true() {
                queue.offer(1);
                queue.offer(2);
                queue.offer(3);

                boolean result = queue.offer(4);

                assertThat(result).isTrue();
                assertThat(queue.size()).isEqualTo(4);
            }

            @Test
            @DisplayName("null 요소를 추가하면 true를 반환한다.")
            void offer_null_element_returns_true() {
                boolean result = queue.offer(null);

                assertThat(result).isTrue();
                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.isEmpty()).isFalse();
                assertThat(queue.peek()).isNull();
            }

            @Test
            @DisplayName("가득찬 큐는 요소를 추가하면 false를 반환한다.")
            void offer_returns_false_when_full() {
                queue = new CircularQueue<>(10);
                for (int i = 0; i < 10; i++) {
                    queue.offer(i);
                }

                boolean result = queue.offer(11);

                assertThat(result).isFalse();
            }

            @Test
            @DisplayName("dequeue 후 빈 자리에 요소를 추가하면 true를 반환한다.")
            void offer_after_dequeue_reuses_space() {
                queue = new CircularQueue<>(10);
                for (int i = 0; i < 10; i++) {
                    queue.offer(i);
                }

                queue.dequeue();

                boolean result = queue.offer(11);

                assertThat(result).isTrue();
            }

            @Test
            @DisplayName("true를 반환하면 size가 증가한다.")
            void offer_increase_size_on_success() {
                assertThat(queue.size()).isZero();

                boolean result = queue.offer(1);

                assertThat(result).isTrue();
                assertThat(queue.size()).isOne();
            }
        }

        @Nested
        @DisplayName("dequeue 메서드 테스트")
        class DequeueTest {

            @Test
            @DisplayName("요소가 없는 큐에 dequeue 시 예외가 발생한다.")
            void dequeue_throws_exception_when_empty() {
                assertThatThrownBy(() -> queue.dequeue())
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("요소가 존재하는 큐에 dequeue 시 맨 앞 요소가 삭제된다.")
            void dequeue_removes_front_element() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                int removed = queue.dequeue();

                assertThat(removed).isEqualTo(0);
                assertThat(queue.size()).isEqualTo(2);
                assertThat(queue.peek()).isEqualTo(1);
            }

            @Test
            @DisplayName("null요소가 삭제된다.")
            void dequeue_removes_null_element() {
                queue.enqueue(null);

                Object removed = queue.dequeue();

                assertThat(removed).isNull();
                assertThat(queue.size()).isEqualTo(0);
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 삭제되면 사이즈가 줄어든다.")
            void dequeue_decrease_size() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.size()).isEqualTo(3);

                queue.dequeue();

                assertThat(queue.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("요소가 하나인 큐의 요소가 삭제되면 빈 큐가 된다.")
            void dequeue_single_element_makes_queue_empty() {
                queue.enqueue(1);

                int removed = queue.dequeue();

                assertThat(removed).isEqualTo(1);
                assertThat(queue.size()).isEqualTo(0);
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("여러 요소를 순차적으로 제거하면 FIFO 순서를 따른다.")
            void dequeue_multiple_elements_in_fifo_order() {
                for (int i = 0; i < 3; i++) {
                    queue.enqueue(i);
                }

                int removedFirst = queue.dequeue();
                int removedSecond = queue.dequeue();
                int removedThird = queue.dequeue();

                assertThat(removedFirst).isEqualTo(0);
                assertThat(removedSecond).isEqualTo(1);
                assertThat(removedThird).isEqualTo(2);
                assertThat(queue.size()).isEqualTo(0);
                assertThat(queue.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("poll 메서드 테스트")
        class PollTest {

            @Test
            @DisplayName("요소가 없는 큐에 poll 시 null을 반환한다.")
            void poll_returns_null_when_empty() {
                Integer result = queue.poll();

                assertThat(result).isNull();
                assertThat(queue.size()).isEqualTo(0);
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 존재하는 큐에 poll 시 맨 앞 요소가 삭제된다.")
            void poll_removes_front_element() {
                queue.enqueue(1);
                queue.enqueue(2);
                queue.enqueue(3);

                int removed = queue.poll();
                assertThat(removed).isEqualTo(1);
                assertThat(queue.size()).isEqualTo(2);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("null 요소가 삭제된다.")
            void poll_removes_null_element() {
                queue.enqueue(null);

                Integer removed = queue.poll();

                assertThat(removed).isNull();
                assertThat(queue.size()).isEqualTo(0);
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 삭제되면 사이즈가 줄어든다.")
            void poll_decreases_size() {
                queue.enqueue(0);
                queue.enqueue(1);
                queue.enqueue(2);

                assertThat(queue.size()).isEqualTo(3);

                queue.poll();

                assertThat(queue.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("요소가 하나인 큐의 요소가 삭제되면 빈 큐가 된다.")
            void poll_single_element_makes_queue_empty() {
                queue.enqueue(1);

                Integer removed = queue.poll();

                assertThat(removed).isEqualTo(1);
                assertThat(queue.size()).isEqualTo(0);
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("여러 요소를 순차적으로 제거하면 FIFO 순서를 따른다.")
            void poll_multiple_elements_in_fifo_order() {
                for (int i = 0; i < 3; i++) {
                    queue.enqueue(i);
                }

                int removedFirst = queue.poll();
                int removedSecond = queue.poll();
                int removedThird = queue.poll();

                assertThat(removedFirst).isEqualTo(0);
                assertThat(removedSecond).isEqualTo(1);
                assertThat(removedThird).isEqualTo(2);
                assertThat(queue.size()).isEqualTo(0);
                assertThat(queue.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("peek 메서드 테스트")
        class PeekTest {

            @Test
            @DisplayName("빈 큐에 peek 시 null을 반환한다.")
            void peek_returns_null_when_empty() {
                Integer result = queue.peek();

                assertThat(queue.size()).isZero();
                assertThat(queue.isEmpty()).isTrue();
                assertThat(result).isNull();
            }

            @Test
            @DisplayName("요소가 있는 큐는 첫 요소를 반환한다.")
            void peek_returns_front_element() {
                queue.enqueue(1);
                queue.enqueue(2);
                queue.enqueue(3);

                int peekResult = queue.peek();

                assertThat(peekResult).isEqualTo(1);
                assertThat(queue.size()).isEqualTo(3);
            }

            @Test
            @DisplayName("null인 요소를 반환할 수 있다.")
            void peek_returns_null_element() {
                queue.enqueue(null);

                Integer peekResult = queue.peek();

                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.isEmpty()).isFalse();
                assertThat(peekResult).isNull();
            }

            @Test
            @DisplayName("여러번 peek()을 해도 같은 데이터를 반환한다.")
            void peek_multiple_time_returns_same_element() {
                queue.enqueue(1);
                queue.enqueue(2);
                queue.enqueue(3);

                assertThat(queue.peek()).isEqualTo(1);
                assertThat(queue.peek()).isEqualTo(1);
                assertThat(queue.peek()).isEqualTo(1);
            }

            @Test
            @DisplayName("peek 후 큐가 변경되지 않는다.")
            void peek_does_not_modify_queue() {
                queue.enqueue(1);
                queue.enqueue(2);
                queue.enqueue(3);

                assertThat(queue.peek()).isEqualTo(1);
                assertThat(queue.size()).isEqualTo(3);
                assertThat(queue.isEmpty()).isFalse();
            }
        }

        @Nested
        @DisplayName("front 메서드 테스트")
        class FrontTest {

            @Test
            @DisplayName("빈 큐에 front 시 null을 반환한다.")
            void front_returns_null_when_empty() {
                Integer frontResult = queue.front();

                assertThat(frontResult).isNull();
                assertThat(queue.isEmpty()).isTrue();
                assertThat(queue.size()).isZero();
            }

            @Test
            @DisplayName("요소가 있는 큐는 첫 요소를 반환한다.")
            void front_returns_front_element() {
                queue.enqueue(1);
                queue.enqueue(2);
                queue.enqueue(3);

                int frontResult = queue.front();

                assertThat(frontResult).isEqualTo(1);
                assertThat(queue.size()).isEqualTo(3);
            }

            @Test
            @DisplayName("null인 요소를 반환할 수 있다.")
            void front_returns_null_element() {
                queue.enqueue(null);

                Integer frontResult = queue.front();

                assertThat(frontResult).isNull();
                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("여러번 front()을 해도 같은 데이터를 반환한다.")
            void front_multiple_times_returns_same_element() {
                queue.enqueue(1);
                queue.enqueue(2);
                queue.enqueue(3);

                assertThat(queue.front()).isEqualTo(1);
                assertThat(queue.front()).isEqualTo(1);
                assertThat(queue.front()).isEqualTo(1);
            }

            @Test
            @DisplayName("front 후 큐가 변경되지 않는다.")
            void front_does_not_modify_queue() {
                queue.enqueue(1);
                queue.enqueue(2);
                queue.enqueue(3);

                assertThat(queue.front()).isEqualTo(1);
                assertThat(queue.size()).isEqualTo(3);
                assertThat(queue.isEmpty()).isFalse();
            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {

            @Test
            @DisplayName("빈 큐는 True를 반환한다.")
            void isEmpty_returns_true_when_empty() {
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 있는 큐는 False를 반환한다.")
            void isEmpty_returns_false_when_not_empty() {
                queue.enqueue(1);

                assertThat(queue.isEmpty()).isFalse();
            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {
            @Test
            @DisplayName("빈 큐는 0을 반환한다.")
            void size_returns_zero_when_empty() {
                assertThat(queue.size()).isZero();
            }

            @Test
            @DisplayName("enqueue 후 size가 증가한다.")
            void size_increase_after_enqueue() {
                queue.enqueue(1);

                assertThat(queue.size()).isEqualTo(1);
            }

            @Test
            @DisplayName("dequeue 후 size가 감소한다.")
            void size_decreases_after_dequeue() {
                queue.enqueue(1);

                assertThat(queue.size()).isEqualTo(1);

                queue.dequeue();

                assertThat(queue.size()).isZero();
            }

            @Test
            @DisplayName("요소가 있는 큐는 요소의 갯수를 반환한다.")
            void size_returns_number_of_elements() {
                queue.enqueue(1);
                queue.enqueue(2);
                queue.enqueue(3);

                assertThat(queue.size()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {
            @Test
            @DisplayName("빈 큐에 clear 시 변화가 없다.")
            void clear_empty_queue_remains_empty() {
                queue.clear();

                assertThat(queue.size()).isZero();
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 있는 큐가 clear 시 빈 큐가 된다.")
            void clear_removes_all_elements() {

                queue.enqueue(1);

                queue.clear();

                assertThat(queue.size()).isZero();
                assertThat(queue.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("clear 후 다시 요소를 추가할 수 있다.")
            void clear_allows_enqueue_after_clear() {

                queue.enqueue(1);

                queue.clear();

                assertThat(queue.size()).isZero();
                assertThat(queue.isEmpty()).isTrue();

                queue.enqueue(1);

                assertThat(queue.size()).isEqualTo(1);
                assertThat(queue.isEmpty()).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("배열 기반 덱")
    class ArrayDequeTest {

        ArrayDeque<Integer> deque;

        @BeforeEach
        void setup() {
            deque = new ArrayDeque<>();
        }

        @Nested
        @DisplayName("배열 기반 덱 생성 테스트")
        class CreateTest {
            @Test
            @DisplayName("덱을 생성할 수 있다.")
            void create_empty_deque() {
                assertThat(deque.isEmpty()).isTrue();
                assertThat(deque.size()).isZero();
            }
        }

        @Nested
        @DisplayName("addFirst 메서드 테스트")
        class AddFirstTest {

            @Test
            @DisplayName("빈 덱에 요소를 추가할 수 있다.")
            void addFirst_to_empty_deque() {

                deque.addFirst(1);

                assertThat(deque.size()).isEqualTo(1);
                assertThat(deque.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("요소가 있는 덱의 앞에 요소를 추가할 수 있다.")
            void addFirst_to_non_empty_deque() {

                deque.addFirst(1);
                deque.addFirst(2);
                deque.addFirst(3);

                assertThat(deque.size()).isEqualTo(3);
                assertThat(deque.peekFirst()).isEqualTo(3);
                assertThat(deque.peekLast()).isEqualTo(1);
            }

            @Test
            @DisplayName("null인 요소를 추가할 수 있다.")
            void addFirst_null_element() {

                deque.addFirst(null);

                assertThat(deque.size()).isEqualTo(1);
                assertThat(deque.isEmpty()).isFalse();
                assertThat(deque.peekFirst()).isNull();
            }

            @Test
            @DisplayName("addFirst 후 size가 증가한다.")
            void addFirst_increase_size() {

                assertThat(deque.size()).isZero();
                deque.addFirst(1);

                assertThat(deque.size()).isEqualTo(1);
            }

            @Test
            @DisplayName("여러 요소를 앞에 추가하면 역순으로 쌓인다.")
            void addFirst_multiple_elements_in_reverse_order() {

                deque.addFirst(3);
                deque.addFirst(2);
                deque.addFirst(1);

                assertThat(deque.removeFirst()).isEqualTo(1);
                assertThat(deque.removeFirst()).isEqualTo(2);
                assertThat(deque.removeFirst()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("addLast 메서드 테스트")
        class AddLastTest {

            @Test
            @DisplayName("빈 덱에 요소를 추가할 수 있다.")
            void addLast_to_empty_deque() {
                deque.addLast(1);

                assertThat(deque.size()).isEqualTo(1);
                assertThat(deque.isEmpty()).isFalse();
                assertThat(deque.peekLast()).isEqualTo(1);
            }

            @Test
            @DisplayName("요소가 있는 덱의 뒤에 요소를 추가할 수 있다.")
            void addLast_to_non_empty_deque() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);

                assertThat(deque.size()).isEqualTo(3);
                assertThat(deque.peekLast()).isEqualTo(3);
            }

            @Test
            @DisplayName("null인 요소를 추가할 수 있다.")
            void addLast_null_element() {
                deque.addLast(null);

                assertThat(deque.peekLast()).isNull();
                assertThat(deque.size()).isEqualTo(1);
                assertThat(deque.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("addLast 후 size가 증가한다.")
            void addLast_increases_size() {
                deque.addLast(1);

                assertThat(deque.size()).isEqualTo(1);

                deque.addLast(2);

                assertThat(deque.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("여러 요소를 뒤에 추가하면 순차적으로 쌓인다.")
            void addLast_multiple_elements_in_order() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);

                assertThat(deque.removeLast()).isEqualTo(3);
                assertThat(deque.removeLast()).isEqualTo(2);
                assertThat(deque.removeLast()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("removeFirst 메서드 테스트")
        class RemoveFirstTest {

            @Test
            @DisplayName("빈 덱에 removeFirst 시 예외가 발생한다.")
            void removeFirst_throws_exception_when_empty() {
                assertThatThrownBy(() -> deque.removeFirst())
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("요소가 존재하는 덱의 첫 요소가 삭제된다.")
            void removeFirst_removes_front_element() {
                deque.addFirst(1);

                int result = deque.removeFirst();

                assertThat(result).isEqualTo(1);
                assertThat(deque.size()).isZero();
                assertThat(deque.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("null 요소를 삭제할 수 있다.")
            void removeFirst_removes_null_element() {
                deque.addFirst(null);

                Integer result = deque.removeFirst();

                assertThat(result).isNull();
                assertThat(deque.size()).isZero();
                assertThat(deque.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("removeFirst 시 size가 감소한다.")
            void removeFirst_decreases_size() {
                deque.addFirst(1);
                deque.addFirst(2);
                deque.addFirst(3);

                int result = deque.removeFirst();

                assertThat(result).isEqualTo(3);
                assertThat(deque.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("여러 요소를 삭제하면 앞부터 순차적으로 삭제된다.")
            void removeFirst_multiple_elements_in_order() {
                deque.addFirst(1);
                deque.addFirst(2);
                deque.addFirst(3);

                assertThat(deque.removeFirst()).isEqualTo(3);
                assertThat(deque.removeFirst()).isEqualTo(2);
                assertThat(deque.removeFirst()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("removeLast 메서드 테스트")
        class RemoveLastTest {

            @Test
            @DisplayName("빈 덱에 removeLast 시 예외가 발생한다.")
            void removeLast_throws_exception_when_empty() {
                assertThatThrownBy(() -> deque.removeLast())
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("요소가 있는 덱의 마지막 요소가 삭제된다.")
            void removeLast_removes_last_element() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);

                int result = deque.removeLast();

                assertThat(result).isEqualTo(3);
                assertThat(deque.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("null 요소가 삭제된다.")
            void removeLast_remove_null_element() {
                deque.addLast(null);

                Integer result = deque.removeLast();

                assertThat(result).isNull();
                assertThat(deque.isEmpty()).isTrue();
                assertThat(deque.size()).isZero();
            }

            @Test
            @DisplayName("removeLast 시 size가 감소한다.")
            void removeLast_decreases_size() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);
                assertThat(deque.size()).isEqualTo(3);

                deque.removeLast();

                assertThat(deque.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("여러 요소를 삭제하면 뒤부터 순차적으로 삭제된다.")
            void removeLast_multiple_elements_in_reverses_order() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);

                assertThat(deque.removeLast()).isEqualTo(3);
                assertThat(deque.removeLast()).isEqualTo(2);
                assertThat(deque.removeLast()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("peekFirst 메서드 테스트")
        class PeekFirstTest {

            @Test
            @DisplayName("빈 덱은 null을 반환한다.")
            void peekFirst_returns_null_when_empty() {
                Integer result = deque.peekFirst();

                assertThat(result).isNull();
                assertThat(deque.size()).isZero();
                assertThat(deque.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 존재하는 덱의 첫 요소가 반환된다.")
            void peekFirst_returns_front_element() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);

                int result = deque.peekFirst();

                assertThat(result).isEqualTo(1);
                assertThat(deque.size()).isEqualTo(3);
            }

            @Test
            @DisplayName("null인 요소가 반환된다.")
            void peekFirst_returns_null_element() {
                deque.addLast(null);

                Integer result = deque.peekFirst();

                assertThat(result).isNull();
                assertThat(deque.size()).isEqualTo(1);
                assertThat(deque.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("peekFirst 시 덱에 변화가 없다.")
            void peekFirst_does_not_modify_deque() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);
                assertThat(deque.size()).isEqualTo(3);

                int result = deque.peekFirst();

                assertThat(result).isEqualTo(1);
                assertThat(deque.size()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("peekLast 메서드 테스트")
        class PeekLastTest {
            @Test
            @DisplayName("빈 덱은 null을 반환한다.")
            void peekLast_returns_null_when_empty() {
                Integer result = deque.peekLast();

                assertThat(result).isNull();
                assertThat(deque.size()).isZero();
                assertThat(deque.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 존재하는 덱의 마지막 요소가 반환된다.")
            void peekLast_returns_last_element() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);

                int result = deque.peekLast();

                assertThat(result).isEqualTo(3);
                assertThat(deque.size()).isEqualTo(3);
            }

            @Test
            @DisplayName("null인 요소가 반환된다.")
            void peekLast_returns_null_element() {
                deque.addLast(null);

                Integer result = deque.peekLast();

                assertThat(result).isNull();
                assertThat(deque.size()).isEqualTo(1);
                assertThat(deque.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("peekLast 시 덱에 변화가 없다.")
            void peekLast_does_not_modify_deque() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);
                assertThat(deque.size()).isEqualTo(3);

                int result = deque.peekLast();

                assertThat(result).isEqualTo(3);
                assertThat(deque.size()).isEqualTo(3);
            }
        }
    }

    @Nested
    @DisplayName("연결 리스트 기반 덱")
    class LinkedListDequeTest {

        LinkedListDeque<Integer> deque;

        @BeforeEach
        void setup() {
            deque = new LinkedListDeque<>();
        }

        @Nested
        @DisplayName("연결 리스트 기반 덱")
        class CreateTest {

            @Test
            @DisplayName("생성 시 사이즈가 0이며 isEmpty는 true이다.")
            void create_deque_size_is_zero_and_isEmpty_is_true() {
                assertThat(deque.size()).isZero();
                assertThat(deque.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("addFirst 메서드 테스트")
        class AddFirstTest {

            @Test
            @DisplayName("빈 덱에 요소를 추가할 수 있다.")
            void addFirst_to_empty_deque() {
                deque.addFirst(1);

                assertThat(deque.size()).isOne();
                assertThat(deque.isEmpty()).isFalse();
                assertThat(deque.peekFirst()).isEqualTo(1);
            }

            @Test
            @DisplayName("요소가 있는 덱의 앞에 요소를 추가할 수 있다.")
            void addFirst_to_non_empty_deque() {
                deque.addFirst(1);
                deque.addFirst(2);
                deque.addFirst(3);

                assertThat(deque.size()).isEqualTo(3);
                assertThat(deque.peekFirst()).isEqualTo(3);
            }

            @Test
            @DisplayName("null요소를 추가할 수 있다.")
            void addFirst_null_element() {
                deque.addFirst(null);

                assertThat(deque.size()).isOne();
                assertThat(deque.isEmpty()).isFalse();
                assertThat(deque.peekFirst()).isNull();
            }

            @Test
            @DisplayName("데이터가 역순으로 등록된다.")
            void addFirst_multiple_elements_in_reverse_order() {
                for (int i = 0; i < 5; i++) {
                    deque.addFirst(i);
                }

                assertThat(deque.removeFirst()).isEqualTo(4);
                assertThat(deque.removeFirst()).isEqualTo(3);
                assertThat(deque.removeFirst()).isEqualTo(2);
                assertThat(deque.removeFirst()).isEqualTo(1);
                assertThat(deque.removeFirst()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("addLast 메서드 테스트")
        class AddLastTest {

            @Test
            @DisplayName("빈 덱에 요소를 추가할 수 있다.")
            void addLast_to_empty_deque() {
                deque.addLast(1);

                assertThat(deque.size()).isOne();
                assertThat(deque.isEmpty()).isFalse();
                assertThat(deque.peekLast()).isEqualTo(1);
            }

            @Test
            @DisplayName("요소가 있는 덱의 뒤에 요소를 추가할 수 있다.")
            void addLast_to_non_empty_deque() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);

                assertThat(deque.size()).isEqualTo(3);
                assertThat(deque.isEmpty()).isFalse();
                assertThat(deque.peekLast()).isEqualTo(3);
            }

            @Test
            @DisplayName("null 요소를 추가할 수 있다.")
            void addLast_null_element() {
                deque.addLast(null);

                assertThat(deque.size()).isOne();
                assertThat(deque.isEmpty()).isFalse();
                assertThat(deque.peekLast()).isNull();
            }

            @Test
            @DisplayName("데이터가 순차적으로 추가된다.")
            void addLast_multiple_elements_in_order() {
                for (int i = 0; i < 5; i++) {
                    deque.addLast(i);
                }

                assertThat(deque.removeLast()).isEqualTo(4);
                assertThat(deque.removeLast()).isEqualTo(3);
                assertThat(deque.removeLast()).isEqualTo(2);
                assertThat(deque.removeLast()).isEqualTo(1);
                assertThat(deque.removeLast()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("removeFirst 메서드 테스트")
        class RemoveFirstTest {

            @Test
            @DisplayName("빈 덱에 removeFirst 시 예외가 발생한다.")
            void removeFirst_throws_exception_when_empty() {
                assertThatThrownBy(() -> deque.removeFirst())
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("요소가 있는 덱에 removeFirst 시 첫번째 요소가 삭제된다.")
            void removeFirst_removes_front_element() {
                deque.addFirst(1);
                deque.addFirst(2);
                deque.addFirst(3);

                int removed = deque.removeFirst();

                assertThat(removed).isEqualTo(3);
                assertThat(deque.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("null 요소가 제거된다.")
            void removeFirst_removes_null_element() {
                deque.addFirst(null);

                Integer removed =deque.removeFirst();

                assertThat(removed).isNull();
                assertThat(deque.size()).isZero();
                assertThat(deque.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("removeFirst 시 사이즈가 감소한다.")
            void removeFirst_decreases_size() {
                for (int i = 0; i < 3; i++) {
                    deque.addFirst(i);
                }
                assertThat(deque.size()).isEqualTo(3);

                deque.removeFirst();

                assertThat(deque.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("데이터가 처음부터 순차적으로 삭제된다.")
            void removeFirst_multiple_elements_in_order() {
                for (int i = 0; i < 3; i++) {
                    deque.addFirst(i);
                }

                assertThat(deque.removeFirst()).isEqualTo(2);
                assertThat(deque.removeFirst()).isEqualTo(1);
                assertThat(deque.removeFirst()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("removeLast 메서드 테스트")
        class RemoveLastTest {

            @Test
            @DisplayName("빈 덱에 removeLast 시 예외가 발생한다.")
            void removeLast_throws_exception_when_empty() {
                assertThatThrownBy(() -> deque.removeLast())
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("요소가 있는 덱의 마지막 요소가 삭제된다.")
            void removeLast_removes_last_element() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);

                int removed = deque.removeLast();

                assertThat(removed).isEqualTo(3);
                assertThat(deque.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("null인 요소가 제거된다.")
            void removeLast_removes_null_element() {
                deque.addLast(null);

                Integer removed = deque.removeLast();

                assertThat(removed).isNull();
                assertThat(deque.size()).isZero();
                assertThat(deque.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("removeLast 시 사이즈가 감소한다.")
            void removeLast_decreases_size() {
                for (int i = 0; i < 3; i++) {
                    deque.addLast(i);
                }
                assertThat(deque.size()).isEqualTo(3);

                deque.removeLast();

                assertThat(deque.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("마지막 데이터부터 순차적으로 삭제된다.")
            void removeLast_multiple_elements_in_reverse_order() {
                for (int i = 0; i < 3; i++) {
                    deque.addLast(i);
                }

                assertThat(deque.removeLast()).isEqualTo(2);
                assertThat(deque.removeLast()).isEqualTo(1);
                assertThat(deque.removeLast()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("peekFirst 메서드 테스트")
        class PeekFirstTest {

            @Test
            @DisplayName("빈 덱은 null을 반환한다.")
            void peekFirst_returns_null_when_empty() {
                Integer target = deque.peekFirst();

                assertThat(target).isNull();
                assertThat(deque.size()).isZero();
                assertThat(deque.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 있는 덱의 첫번째 요소를 반환한다.")
            void peekFirst_returns_front_element() {
                deque.addFirst(1);
                deque.addFirst(2);
                deque.addFirst(3);

                int target = deque.peekFirst();

                assertThat(target).isEqualTo(3);
                assertThat(deque.size()).isEqualTo(3);
            }

            @Test
            @DisplayName("null인 요소를 반환한다.")
            void peekFirst_returns_null_element() {
                deque.addFirst(null);

                Integer target = deque.peekFirst();

                assertThat(target).isNull();
                assertThat(deque.size()).isEqualTo(1);
                assertThat(deque.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("여러번 peekFirst를 해도 덱은 변하지 않는다.")
            void peekFirst_does_not_modify_deque() {
                for (int i = 0; i < 5; i++) {
                    deque.addFirst(i);
                }

                assertThat(deque.peekFirst()).isEqualTo(4);
                assertThat(deque.peekFirst()).isEqualTo(4);
                assertThat(deque.peekFirst()).isEqualTo(4);
            }
        }

        @Nested
        @DisplayName("peekLast 메서드 테스트")
        class PeekLastTest {

            @Test
            @DisplayName("빈 덱은 null을 반환한다.")
            void peekLast_returns_null_when_empty() {
                Integer target = deque.peekLast();

                assertThat(target).isNull();
                assertThat(deque.size()).isZero();
                assertThat(deque.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 있는 덱은 마지막 요소를 반환한다.")
            void peekLast_returns_last_element() {
                deque.addLast(1);
                deque.addLast(2);
                deque.addLast(3);

                int target = deque.peekLast();

                assertThat(target).isEqualTo(3);
            }

            @Test
            @DisplayName("null 요소를 반환한다.")
            void peekLast_returns_null_elements() {
                deque.addLast(null);

                Integer target = deque.peekLast();

                assertThat(target).isNull();
                assertThat(deque.isEmpty()).isFalse();
                assertThat(deque.size()).isOne();
            }

            @Test
            @DisplayName("여러번 peekLast를 해도 덱은 변하지 않는다.")
            void peekLast_does_not_modify_deque() {
                for (int i = 0; i < 5; i++) {
                    deque.addLast(i);
                }

                assertThat(deque.peekLast()).isEqualTo(4);
                assertThat(deque.peekLast()).isEqualTo(4);
                assertThat(deque.peekLast()).isEqualTo(4);
            }
        }
    }


    @Nested
    @DisplayName("추가 응용 테스트")
    class ApplicationTest {
        @Nested
        @DisplayName("슬라이딩 윈도우 최댓값")
        class SlidingWindowMaxTest {

            QueueProblems queueProblems = new QueueProblems();

            @Test
            @DisplayName("배열과 최댓값이 들어오면 정상적으로 결과 배열이 반환된다.")
            void maxSlidingWindow_returns_correct_result() {
                int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
                int windowsLength = 3;
                int[] result = queueProblems.slidingWindow(nums, windowsLength);

                assertThat(result.length).isEqualTo(6);
                assertThat(result).containsExactly(3, 3, 5, 5, 6, 7);
            }

            @Test
            @DisplayName("빈 배열일 경우 예외를 발생시킨다.")
            void maxSlidingWindow_throws_exception_when_empty() {
                int[] nums = {};
                int windowsLength = 3;

                assertThatThrownBy(() -> queueProblems.slidingWindow(nums, windowsLength))
                        .isInstanceOf(Exception.class);
            }

            @Test
            @DisplayName("windowsLength가 배열의 길이와 같은 경우 최댓값 하나를 반환한다.")
            void maxSlidingWindow_k_equals_array_length() {
                int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
                int windowsLength = 8;
                int[] result = queueProblems.slidingWindow(nums, windowsLength);

                assertThat(result[0]).isEqualTo(7);
            }

            @Test
            @DisplayName("windowsLength가 1인 경우 원본 배열을 반환한다.")
            void maxSlidingWindow_k_is_one() {
                int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
                int windowsLength = 1;
                int[] result = queueProblems.slidingWindow(nums, windowsLength);

                assertThat(result).containsExactly(1, 3, -1, -3, 5, 3, 6, 7);
            }

            @Test
            @DisplayName("모든 요소가 같은 경우 동일한 값의 배열을 반환한다.")
            void maxSlidingWindow_all_elements_same() {
                int[] nums = {3, 3, 3, 3, 3, 3, 3, 3};
                int windowsLength = 3;
                int[] result = queueProblems.slidingWindow(nums, windowsLength);

                for (int i = 0; i < 6;i++) {
                    assertThat(result[i]).isEqualTo(3);
                }
            }

            @Test
            @DisplayName("windowsLength가 배열 길이보다 클 때 예외를 발생시킨다.")
            void maxSlidingWindow_k_exceeds_array_length() {
                int[] nums = {1, 3, -1};
                int windowsLength = 4;
                assertThatThrownBy(() -> queueProblems.slidingWindow(nums, windowsLength))
                        .isInstanceOf(Exception.class);
            }
        }

        @Nested
        @DisplayName("최근 요청 카운터")
        class RecentRequestCounterTest {
            @Test
            @DisplayName("")
            void test1() {}
        }
    }

}
