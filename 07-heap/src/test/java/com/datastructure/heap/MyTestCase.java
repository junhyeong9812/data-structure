package com.datastructure.heap;

import com.datastructure.heap.pop.MaxHeap;
import com.datastructure.heap.pop.MinHeap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MyTestCase {

    @Nested @DisplayName("MaxHeap 테스트")
    class MaxHeapTest {
        MaxHeap<Integer> heap;
        @BeforeEach
        void setup() {heap = new MaxHeap<>();}

        @Nested @DisplayName("creation 테스트")
        class CreationTest {
            @Test @DisplayName("빈 Heap을 생성할 수 있다.")
            void create_empty_heap() {
                assertThat(heap.isEmpty()).isTrue();
                assertThat(heap.size()).isZero();
            }

            @Test @DisplayName("capacity를 지정할 수 있다.")
            void create_heap_with_capacity() {
                heap = new MaxHeap<>(10);
                assertThat(heap.isEmpty()).isTrue();
                assertThat(heap.size()).isZero();
            }
        }

        @Nested @DisplayName("insert 메서드 테스트")
        class InsertTest {

            @Test @DisplayName("빈 heap에 요소를 추가할 수 있다.")
            void insert_into_empty_heap() {
                heap.insert(1);

                assertThat(heap.peek()).isEqualTo(1);
                assertThat(heap.isEmpty()).isFalse();
                assertThat(heap.size()).isOne();
            }

            @Test @DisplayName("요소가 있는 heap에 요소를 추가할 수있다.")
            void insert_into_non_empty_heap() {
                heap.insert(1);
                heap.insert(5);
                heap.insert(3);

                assertThat(heap.peek()).isEqualTo(5);
                assertThat(heap.isEmpty()).isFalse();
                assertThat(heap.size()).isEqualTo(3);
            }

            @Test @DisplayName("삽입 시 최댓값이 루트에 위치한다.")
            void insert_maintains_max_heap_property() {
                heap.insert(3);
                heap.insert(1);
                heap.insert(5);
                heap.insert(2);
                heap.insert(4);

                assertThat(heap.peek()).isEqualTo(5);
            }

            @Test @DisplayName("중복 값을 추가할 수 있다.")
            void insert_duplicate_values() {
                heap.insert(3);
                heap.insert(3);

                assertThat(heap.size()).isEqualTo(2);
                assertThat(heap.peek()).isEqualTo(3);
            }

            @Test @DisplayName("null 요소를 추가하면 예외가 발생한다.")
            void insert_null_throws_exception() {
                assertThatThrownBy(() -> heap.insert(null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested @DisplayName("offer 메서드 테스트")
        class OfferTest {
            @Test @DisplayName("빈 heap에 요소를 추가할 수 있다.")
            void offer_into_empty_heap() {
                heap.offer(1);

                assertThat(heap.peek()).isEqualTo(1);
                assertThat(heap.isEmpty()).isFalse();
                assertThat(heap.size()).isOne();
            }

            @Test @DisplayName("요소가 있는 heap에 요소를 추가할 수있다.")
            void offer_into_non_empty_heap() {
                heap.offer(1);
                heap.offer(5);
                heap.offer(3);

                assertThat(heap.peek()).isEqualTo(5);
                assertThat(heap.isEmpty()).isFalse();
                assertThat(heap.size()).isEqualTo(3);
            }

            @Test @DisplayName("삽입 시 최댓값이 루트에 위치한다.")
            void offer_maintains_max_heap_property() {
                heap.offer(3);
                heap.offer(1);
                heap.offer(5);
                heap.offer(2);
                heap.offer(4);

                assertThat(heap.peek()).isEqualTo(5);
            }

            @Test @DisplayName("중복 값을 추가할 수 있다.")
            void offer_duplicate_values() {
                heap.offer(3);
                heap.offer(3);

                assertThat(heap.size()).isEqualTo(2);
                assertThat(heap.peek()).isEqualTo(3);
            }

            @Test @DisplayName("null 요소를 추가하면 예외가 발생한다.")
            void offer_null_throws_exception() {
                assertThatThrownBy(() -> heap.offer(null))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("성공 시 true를 반환한다.")
            void offer_returns_true_on_success() {
                boolean result = heap.offer(1);

                assertThat(result).isTrue();
            }
        }

        @Nested @DisplayName("extractMax 메서드 테스트")
        class ExtractMaxTest {
            @Test @DisplayName("빈 MaxHeap에 extractMax 시 예외가 발생한다.")
            void extractMax_empty_heap_throws_exception() {
                assertThatThrownBy(() -> heap.extractMax())
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test @DisplayName("요소가 하나인 경우 최댓값을 제거 후 반환한다.")
            void extractMax_single_element() {
                heap.insert(1);

                int result = heap.extractMax();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isZero();
                assertThat(heap.isEmpty()).isTrue();
            }

            @Test @DisplayName("요소가 여러 개인 경우 최댓값을 제거 후 반환한다.")
            void extractMax_multiple_elements() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result = heap.extractMax();

                assertThat(result).isEqualTo(4);
                assertThat(heap.size()).isEqualTo(3);
                assertThat(heap.peek()).isEqualTo(3);
            }

            @Test @DisplayName("연속 extractMax 시 내림차순으로 반환된다.")
            void extractMax_returns_in_descending_order() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result1 = heap.extractMax();
                int result2 = heap.extractMax();
                int result3 = heap.extractMax();

                assertThat(result1).isEqualTo(4);
                assertThat(result2).isEqualTo(3);
                assertThat(result3).isEqualTo(2);
            }
        }

        @Nested @DisplayName("poll 메서드 테스트")
        class PollTest {
            @Test @DisplayName("빈 MaxHeap에 poll 시 null을 반환한다.")
            void poll_empty_heap_returns_null() {
                Integer result = heap.poll();

                assertThat(result).isNull();
            }

            @Test @DisplayName("요소가 하나인 경우 최댓값을 제거 후 반환한다.")
            void poll_single_element() {
                heap.insert(1);

                int result = heap.poll();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isZero();
                assertThat(heap.isEmpty()).isTrue();
            }

            @Test @DisplayName("요소가 여러 개인 경우 최댓값을 제거 후 반환한다.")
            void poll_multiple_elements() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result = heap.poll();

                assertThat(result).isEqualTo(4);
                assertThat(heap.size()).isEqualTo(3);
                assertThat(heap.peek()).isEqualTo(3);
            }

            @Test @DisplayName("연속 poll 시 내림차순으로 반환된다.")
            void poll_returns_in_descending_order() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result1 = heap.poll();
                int result2 = heap.poll();
                int result3 = heap.poll();

                assertThat(result1).isEqualTo(4);
                assertThat(result2).isEqualTo(3);
                assertThat(result3).isEqualTo(2);
            }
        }

        @Nested @DisplayName("peek 메서드 테스트")
        class PeekTest {
            @Test @DisplayName("빈 요소에서는 null을 반환한다.")
            void peek_empty_heap_returns_null() {
                Integer result = heap.peek();

                assertThat(result).isNull();
            }

            @Test @DisplayName("요소가 하나인 경우 해당 값을 반환한다.")
            void peek_single_element() {
                heap.insert(1);

                int result = heap.peek();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isEqualTo(1);
                assertThat(heap.isEmpty()).isFalse();
            }

            @Test @DisplayName("요소가 여러개인 경우 최댓값을 반환한다.")
            void peek_multiple_elements_returns_max() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result = heap.peek();

                assertThat(result).isEqualTo(4);
                assertThat(heap.size()).isEqualTo(4);
            }

            @Test @DisplayName("여러번 조회해도 같은 값을 반환한다.")
            void peek_multiple_times_returns_same_value() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result1 = heap.peek();
                int result2 = heap.peek();
                int result3 = heap.peek();

                assertThat(result1).isEqualTo(4);
                assertThat(result2).isEqualTo(4);
                assertThat(result3).isEqualTo(4);
                assertThat(heap.size()).isEqualTo(4);
            }
        }

        @Nested @DisplayName("getMax 메서드 테스트")
        class GetMaxTest {
            @Test @DisplayName("빈 요소에서는 예외가 발생한다.")
            void getMax_empty_heap_throws_exception() {
                assertThatThrownBy(() -> heap.getMax())
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test @DisplayName("요소가 하나인 경우 해당 값을 반환한다.")
            void getMax_single_element() {
                heap.insert(1);

                int result = heap.getMax();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isEqualTo(1);
                assertThat(heap.isEmpty()).isFalse();
            }

            @Test @DisplayName("요소가 여러개인 경우 최댓값을 반환한다.")
            void getMax_multiple_elements_returns_max() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result = heap.getMax();

                assertThat(result).isEqualTo(4);
                assertThat(heap.size()).isEqualTo(4);
            }

            @Test @DisplayName("여러번 조회해도 같은 값을 반환한다.")
            void getMax_multiple_times_returns_same_value() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result1 = heap.getMax();
                int result2 = heap.getMax();
                int result3 = heap.getMax();

                assertThat(result1).isEqualTo(4);
                assertThat(result2).isEqualTo(4);
                assertThat(result3).isEqualTo(4);
                assertThat(heap.size()).isEqualTo(4);
            }
        }

        @Nested @DisplayName("size 메서드 테스트")
        class SizeTest {
            @Test @DisplayName("빈 요소의 size는 0이다.")
            void size_empty_heap_returns_zero() {
                assertThat(heap.size()).isZero();
            }

            @Test @DisplayName("요소가 존재하는 경우 size는 요소의 갯수이다.")
            void size_returns_number_of_elements() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                assertThat(heap.size()).isEqualTo(4);
            }

            @Test @DisplayName("insert 후 size가 증가한다.")
            void size_increases_after_insert() {
                heap.insert(1);
                heap.insert(2);
                assertThat(heap.size()).isEqualTo(2);

                heap.insert(3);

                assertThat(heap.size()).isEqualTo(3);
            }

            @Test @DisplayName("extractMax 후 size가 감소한다.")
            void size_decreases_after_extractMax() {
                heap.insert(1);
                heap.insert(2);
                assertThat(heap.size()).isEqualTo(2);

                heap.extractMax();

                assertThat(heap.size()).isEqualTo(1);
            }
        }

        @Nested @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {
            @Test @DisplayName("요소가 존재하지 않으면 True를 반환한다.")
            void isEmpty_returns_true_when_empty() {
                assertThat(heap.isEmpty()).isTrue();
            }

            @Test @DisplayName("요소가 존재하면 False를 반환한다.")
            void isEmpty_returns_false_when_not_empty() {
                heap.insert(1);

                assertThat(heap.isEmpty()).isFalse();
            }
        }

        @Nested @DisplayName("clear 메서드 테스트")
        class ClearTest {
            @Test @DisplayName("요소가 존재하지 않아도 clear가 가능하다.")
            void clear_empty_heap_remains_empty() {
                heap.clear();

                assertThat(heap.size()).isZero();
                assertThat(heap.isEmpty()).isTrue();
            }

            @Test @DisplayName("요소가 존재하면 빈 heap으로 만든다.")
            void clear_removes_all_elements() {
                heap.insert(1);
                heap.insert(2);

                heap.clear();

                assertThat(heap.size()).isZero();
                assertThat(heap.isEmpty()).isTrue();
            }

            @Test @DisplayName("clear 후 다시 요소를 추가할 수 있다.")
            void clear_allows_insert_after_clear() {
                heap.insert(1);
                heap.clear();

                heap.insert(2);

                assertThat(heap.size()).isEqualTo(1);
                assertThat(heap.peek()).isEqualTo(2);
            }
        }

        @Nested @DisplayName("heapify 메서드 테스트")
        class HeapifyTest {

            @Test @DisplayName("빈 배열이 들어오면 빈 힙을 반환한다.")
            void heapify_empty_array_returns_empty_heap() {
                Integer[] array = {};
                MaxHeap<Integer> result = MaxHeap.heapify(array);

                assertThat(result.isEmpty()).isTrue();
                assertThat(result.size()).isZero();
            }

            @Test @DisplayName("요소가 하나인 배열을 힙으로 변환한다.")
            void heapify_single_element() {
                Integer[] array = {3};
                MaxHeap<Integer> result = MaxHeap.heapify(array);

                assertThat(result.size()).isEqualTo(1);
                assertThat(result.peek()).isEqualTo(3);
            }

            @Test @DisplayName("여러 요소의 배열을 힙으로 변환한다.")
            void heapify_multiple_elements() {
                Integer[] array = {3, 1, 5, 2, 4};
                MaxHeap<Integer> result = MaxHeap.heapify(array);

                assertThat(result.size()).isEqualTo(5);
                assertThat(result.peek()).isEqualTo(5);
            }

            @Test @DisplayName("변환된 힙은 최댓값이 루트에 위치한다.")
            void heapify_maintains_max_heap_property() {
                Integer[] array = {3, 1, 5, 2, 4};
                MaxHeap<Integer> result = MaxHeap.heapify(array);

                assertThat(result.peek()).isEqualTo(5);
            }

            @Test @DisplayName("중복 값이 있는 배열도 그대로 힙으로 변환한다.")
            void heapify_with_duplicate_values() {
                Integer[] array = {3, 3, 5, 2, 5};
                MaxHeap<Integer> result = MaxHeap.heapify(array);

                assertThat(result.size()).isEqualTo(5);
                assertThat(result.peek()).isEqualTo(5);
            }

            @Test @DisplayName("null 배열이 들어오면 예외가 발생한다.")
            void heapify_null_array_throws_exception() {
                assertThatThrownBy(() -> MaxHeap.heapify(null))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("null 요소가 포함된 배열이 들어오면 예외가 발생한다.")
            void heapify_array_with_null_element_throws_exception() {
                Integer[] array = {null, 1, 5, 2, 4};
                assertThatThrownBy(() -> MaxHeap.heapify(array))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested @DisplayName("increaseKey 메서드 테스트")
        class IncreaseKeyTest {

            @Test @DisplayName("빈 힙에서 호출하면 예외가 발생한다.")
            void increaseKey_empty_heap_throws_exception() {
                assertThatThrownBy(() -> heap.increaseKey(1,10))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test @DisplayName("index가 음수이면 예외가 발생한다.")
            void increaseKey_negative_index_throws_exception() {
                heap.insert(1);

                assertThatThrownBy(() -> heap.increaseKey(-1, 10))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test @DisplayName("index가 size 이상이면 예외가 발생한다.")
            void increaseKey_index_out_of_bounds_throws_exception() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);

                assertThatThrownBy(() -> heap.increaseKey(3, 10))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test @DisplayName("newValue가 null이면 예외가 발생한다.")
            void increaseKey_null_value_throws_exception() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);

                assertThatThrownBy(() -> heap.increaseKey(1, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("기존 값보다 작은 값을 넣으면 예외가 발생한다.")
            void increaseKey_smaller_value_throws_exception() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);

                assertThatThrownBy(() -> heap.increaseKey(2, 1))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("기존 값과 같은 값을 넣으면 변화가 없다.")
            void increaseKey_same_value_no_change() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                assertThat(heap.peek()).isEqualTo(3);

                heap.increaseKey(2, 3);

                assertThat(heap.peek()).isEqualTo(3);
            }

            @Test @DisplayName("값 증가 후 힙 속성이 유지된다.")
            void increaseKey_maintains_max_heap_property() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                assertThat(heap.peek()).isEqualTo(3);

                heap.increaseKey(3, 10);

                assertThat(heap.peek()).isEqualTo(10);
            }
        }

        @Nested @DisplayName("delete 메서드 테스트")
        class DeleteTest {
            @Test @DisplayName("빈 힙에서 호출하면 예외가 발생한다.")
            void delete_empty_heap_throws_exception() {
                assertThatThrownBy(() -> heap.delete(1))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test @DisplayName("index가 음수이면 예외가 발생한다.")
            void delete_negative_index_throws_exception() {
                heap.insert(3);

                assertThatThrownBy(() -> heap.delete(-1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test @DisplayName("index가 size 이상이면 예외가 발생한다.")
            void delete_index_out_of_bounds_throws_exception() {
                heap.insert(3);

                assertThatThrownBy(() -> heap.delete(1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test @DisplayName("루트 요소를 삭제하면 다음 최댓값이 루트가 된다.")
            void delete_root_element() {
                heap.insert(3);
                heap.insert(5);
                heap.insert(1);
                assertThat(heap.peek()).isEqualTo(5);

                heap.delete(0);

                assertThat(heap.peek()).isEqualTo(3);
            }

            @Test @DisplayName("중간 요소를 삭제해도 힙 속성이 유지된다.")
            void delete_middle_element_maintains_heap_property() {
                heap.insert(3);
                heap.insert(5);
                heap.insert(1);
                heap.insert(2);
                heap.insert(4);
                assertThat(heap.peek()).isEqualTo(5);

                heap.delete(3);

                assertThat(heap.peek()).isEqualTo(5);
            }

            @Test @DisplayName("마지막 요소를 삭제하면 size가 감소한다.")
            void delete_last_element() {
                heap.insert(3);
                heap.insert(5);
                heap.insert(1);
                heap.insert(2);
                heap.insert(4);
                assertThat(heap.size()).isEqualTo(5);

                heap.delete(4);

                assertThat(heap.size()).isEqualTo(4);
            }

            @Test @DisplayName("삭제된 요소의 값을 반환한다.")
            void delete_returns_removed_value() {
                heap.insert(10);

                int result = heap.delete(0);

                assertThat(result).isEqualTo(10);
                assertThat(heap.isEmpty()).isTrue();
            }
        }

        @Nested @DisplayName("merge 메서드 테스트")
        class MergeTest {
            MaxHeap<Integer> heap2;

            @BeforeEach
            void setUp() {
                heap2 = new MaxHeap<Integer>();
            }

            @Test @DisplayName("빈 힙끼리 병합하면 빈 힙이다.")
            void merge_empty_heaps() {
                heap = heap.merge(heap2);

                assertThat(heap.size()).isZero();
                assertThat(heap.isEmpty()).isTrue();
            }

            @Test @DisplayName("빈 힙과 요소가 있는 힙을 병합하면 요소가 있는 힙이다.")
            void merge_empty_with_non_empty() {
                heap.insert(1);
                heap.insert(2);

                heap = heap.merge(heap2);

                assertThat(heap.size()).isEqualTo(2);
                assertThat(heap.peek()).isEqualTo(2);
            }

            @Test @DisplayName("요소가 있는 두 힙을 병합하면 모든 요소가 포함된다.")
            void merge_two_non_empty_heaps() {
                heap.insert(1);
                heap.insert(3);
                heap.insert(5);

                heap2.insert(2);
                heap2.insert(4);
                heap2.insert(6);

                heap = heap.merge(heap2);

                assertThat(heap.size()).isEqualTo(6);
                assertThat(heap.peek()).isEqualTo(6);
            }

            @Test @DisplayName("병합 후 힙 속성이 유지된다.")
            void merge_maintains_max_heap_property() {
                heap.insert(1);
                heap.insert(3);

                heap2.insert(2);
                heap2.insert(4);
                assertThat(heap.peek()).isEqualTo(3);

                heap = heap.merge(heap2);

                assertThat(heap.peek()).isEqualTo(4);
            }

            @Test @DisplayName("null 힙과 병합하면 예외가 발생한다.")
            void merge_null_throws_exception() {
                heap.insert(1);

                assertThatThrownBy(() -> heap.merge(null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested @DisplayName("MinHeap 테스트")
    class MinHeapTest {

        MinHeap<Integer> heap;
        @BeforeEach
        void setup() {heap = new MinHeap<>();}

        @Nested @DisplayName("creation 테스트")
        class CreationTest {
            @Test @DisplayName("빈 Heap을 생성할 수 있다.")
            void create_empty_heap() {
                assertThat(heap.isEmpty()).isTrue();
                assertThat(heap.size()).isZero();
            }

            @Test @DisplayName("capacity를 지정할 수 있다.")
            void create_heap_with_capacity() {
                heap = new MinHeap<>(10);
                assertThat(heap.isEmpty()).isTrue();
                assertThat(heap.size()).isZero();
            }
        }

        @Nested @DisplayName("insert 메서드 테스트")
        class InsertTest {

            @Test @DisplayName("빈 heap에 요소를 추가할 수 있다.")
            void insert_into_empty_heap() {
                heap.insert(1);

                assertThat(heap.peek()).isEqualTo(1);
                assertThat(heap.isEmpty()).isFalse();
                assertThat(heap.size()).isOne();
            }

            @Test @DisplayName("요소가 있는 heap에 요소를 추가할 수 있다.")
            void insert_into_non_empty_heap() {
                heap.insert(1);
                heap.insert(5);
                heap.insert(3);

                assertThat(heap.peek()).isEqualTo(1);
                assertThat(heap.isEmpty()).isFalse();
                assertThat(heap.size()).isEqualTo(3);
            }

            @Test @DisplayName("삽입 시 최솟값이 루트에 위치한다.")
            void insert_maintains_min_heap_property() {
                heap.insert(3);
                heap.insert(1);
                heap.insert(5);
                heap.insert(2);
                heap.insert(4);

                assertThat(heap.peek()).isEqualTo(1);
            }

            @Test @DisplayName("중복 값을 추가할 수 있다.")
            void insert_duplicate_values() {
                heap.insert(3);
                heap.insert(3);

                assertThat(heap.size()).isEqualTo(2);
                assertThat(heap.peek()).isEqualTo(3);
            }

            @Test @DisplayName("null 요소를 추가하면 예외가 발생한다.")
            void insert_null_throws_exception() {
                assertThatThrownBy(() -> heap.insert(null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested @DisplayName("offer 메서드 테스트")
        class OfferTest {
            @Test @DisplayName("빈 heap에 요소를 추가할 수 있다.")
            void offer_into_empty_heap() {
                heap.offer(1);

                assertThat(heap.peek()).isEqualTo(1);
                assertThat(heap.isEmpty()).isFalse();
                assertThat(heap.size()).isOne();
            }

            @Test @DisplayName("요소가 있는 heap에 요소를 추가할 수있다.")
            void offer_into_non_empty_heap() {
                heap.offer(1);
                heap.offer(5);
                heap.offer(3);

                assertThat(heap.peek()).isEqualTo(1);
                assertThat(heap.isEmpty()).isFalse();
                assertThat(heap.size()).isEqualTo(3);
            }

            @Test @DisplayName("삽입 시 최솟값이 루트에 위치한다.")
            void offer_maintains_min_heap_property() {
                heap.offer(3);
                heap.offer(1);
                heap.offer(5);
                heap.offer(2);
                heap.offer(4);

                assertThat(heap.peek()).isEqualTo(1);
            }

            @Test @DisplayName("중복 값을 추가할 수 있다.")
            void offer_duplicate_values() {
                heap.offer(3);
                heap.offer(3);

                assertThat(heap.size()).isEqualTo(2);
                assertThat(heap.peek()).isEqualTo(3);
            }

            @Test @DisplayName("null 요소를 추가하면 예외가 발생한다.")
            void offer_null_throws_exception() {
                assertThatThrownBy(() -> heap.offer(null))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("성공 시 true를 반환한다.")
            void offer_returns_true_on_success() {
                boolean result = heap.offer(1);
                assertThat(result).isTrue();
            }
        }

        @Nested @DisplayName("extractMin 메서드 테스트")
        class ExtractMinTest {
            @Test @DisplayName("빈 MinHeap에 extractMin 시 예외가 발생한다.")
            void extractMin_empty_heap_throws_exception() {
                assertThatThrownBy(() -> heap.extractMin())
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test @DisplayName("요소가 하나인 경우 최솟값을 제거 후 반환한다.")
            void extractMin_single_element() {
                heap.insert(1);

                int result = heap.extractMin();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isZero();
                assertThat(heap.isEmpty()).isTrue();
            }

            @Test @DisplayName("요소가 여러 개인 경우 최솟값을 제거 후 반환한다.")
            void extractMin_multiple_elements() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result = heap.extractMin();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isEqualTo(3);
                assertThat(heap.peek()).isEqualTo(2);
            }

            @Test @DisplayName("연속 extractMin 시 내림차순으로 반환된다.")
            void extractMin_returns_in_descending_order() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result1 = heap.extractMin();
                int result2 = heap.extractMin();
                int result3 = heap.extractMin();

                assertThat(result1).isEqualTo(1);
                assertThat(result2).isEqualTo(2);
                assertThat(result3).isEqualTo(3);
            }
        }

        @Nested @DisplayName("poll 메서드 테스트")
        class PollTest {
            @Test @DisplayName("빈 MinHeap에 poll 시 null을 반환한다.")
            void poll_empty_heap_returns_null() {
                Integer result = heap.poll();

                assertThat(result).isNull();
            }

            @Test @DisplayName("요소가 하나인 경우 최솟값을 제거 후 반환한다.")
            void poll_single_element() {
                heap.insert(1);

                int result = heap.poll();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isZero();
                assertThat(heap.isEmpty()).isTrue();
            }

            @Test @DisplayName("요소가 여러 개인 경우 최솟값을 제거 후 반환한다.")
            void poll_multiple_elements() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result = heap.poll();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isEqualTo(3);
                assertThat(heap.peek()).isEqualTo(2);
            }

            @Test @DisplayName("연속 poll 시 오름차순으로 반환된다.")
            void poll_returns_in_ascending_order() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result1 = heap.poll();
                int result2 = heap.poll();
                int result3 = heap.poll();

                assertThat(result1).isEqualTo(1);
                assertThat(result2).isEqualTo(2);
                assertThat(result3).isEqualTo(3);
            }
        }

        @Nested @DisplayName("peek 메서드 테스트")
        class PeekTest {
            @Test @DisplayName("빈 요소에서는 null을 반환한다.")
            void peek_empty_heap_returns_null() {
                Integer result = heap.peek();

                assertThat(result).isNull();
            }

            @Test @DisplayName("요소가 하나인 경우 해당 값을 반환한다.")
            void peek_single_element() {
                heap.insert(1);

                int result = heap.peek();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isEqualTo(1);
                assertThat(heap.isEmpty()).isFalse();
            }

            @Test @DisplayName("요소가 여러개인 경우 최솟값을 반환한다.")
            void peek_multiple_elements_returns_min() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result = heap.peek();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isEqualTo(4);
            }

            @Test @DisplayName("여러번 조회해도 같은 값을 반환한다.")
            void peek_multiple_times_returns_same_value() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result1 = heap.peek();
                int result2 = heap.peek();
                int result3 = heap.peek();

                assertThat(result1).isEqualTo(1);
                assertThat(result2).isEqualTo(1);
                assertThat(result3).isEqualTo(1);
                assertThat(heap.size()).isEqualTo(4);
            }
        }

        @Nested @DisplayName("getMin 메서드 테스트")
        class GetMinTest {
            @Test @DisplayName("빈 요소에서는 예외가 발생한다.")
            void getMin_empty_heap_throws_exception() {
                assertThatThrownBy(() -> heap.getMin())
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test @DisplayName("요소가 하나인 경우 해당 값을 반환한다.")
            void getMin_single_element() {
                heap.insert(1);

                int result = heap.getMin();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isEqualTo(1);
                assertThat(heap.isEmpty()).isFalse();
            }

            @Test @DisplayName("요소가 여러개인 경우 최솟값을 반환한다.")
            void getMin_multiple_elements_returns_min() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result = heap.getMin();

                assertThat(result).isEqualTo(1);
                assertThat(heap.size()).isEqualTo(4);
            }

            @Test @DisplayName("여러번 조회해도 같은 값을 반환한다.")
            void getMin_multiple_times_returns_same_value() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                int result1 = heap.getMin();
                int result2 = heap.getMin();
                int result3 = heap.getMin();

                assertThat(result1).isEqualTo(1);
                assertThat(result2).isEqualTo(2);
                assertThat(result3).isEqualTo(3);
                assertThat(heap.size()).isEqualTo(4);
            }
        }

        @Nested @DisplayName("size 메서드 테스트")
        class SizeTest {
            @Test @DisplayName("빈 요소의 size는 0이다.")
            void size_empty_heap_returns_zero() {
                assertThat(heap.size()).isZero();
            }

            @Test @DisplayName("요소가 존재하는 경우 size는 요소의 갯수이다.")
            void size_returns_number_of_elements() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                heap.insert(4);

                assertThat(heap.size()).isEqualTo(4);
            }

            @Test @DisplayName("insert 후 size가 증가한다.")
            void size_increases_after_insert() {
                heap.insert(1);
                heap.insert(2);
                assertThat(heap.size()).isEqualTo(2);

                heap.insert(3);

                assertThat(heap.size()).isEqualTo(3);
            }

            @Test @DisplayName("extractMin 후 size가 감소한다.")
            void size_decreases_after_extractMin() {
                heap.insert(1);
                heap.insert(2);
                assertThat(heap.size()).isEqualTo(2);

                heap.extractMin();

                assertThat(heap.size()).isEqualTo(1);
            }
        }

        @Nested @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {
            @Test @DisplayName("요소가 존재하지 않으면 True를 반환한다.")
            void isEmpty_returns_true_when_empty() {
                assertThat(heap.isEmpty()).isTrue();
            }

            @Test @DisplayName("요소가 존재하면 False를 반환한다.")
            void isEmpty_returns_false_when_not_empty() {
                heap.insert(1);

                assertThat(heap.isEmpty()).isFalse();
            }
        }

        @Nested @DisplayName("clear 메서드 테스트")
        class ClearTest {
            @Test @DisplayName("요소가 존재하지 않아도 clear가 가능하다.")
            void clear_empty_heap_remains_empty() {
                heap.clear();

                assertThat(heap.size()).isZero();
                assertThat(heap.isEmpty()).isTrue();
            }

            @Test @DisplayName("요소가 존재하면 빈 heap으로 만든다.")
            void clear_removes_all_elements() {
                heap.insert(1);
                heap.insert(2);

                heap.clear();

                assertThat(heap.size()).isZero();
                assertThat(heap.isEmpty()).isTrue();
            }

            @Test @DisplayName("clear 후 다시 요소를 추가할 수 있다.")
            void clear_allows_insert_after_clear() {
                heap.insert(1);
                heap.clear();

                heap.insert(2);

                assertThat(heap.size()).isEqualTo(1);
                assertThat(heap.peek()).isEqualTo(2);
            }
        }

        @Nested @DisplayName("heapify 메서드 테스트")
        class HeapifyTest {}

        @Nested @DisplayName("decreaseKey 메서드 테스트")
        class DecreaseKeyTest {}

        @Nested @DisplayName("delete 메서드 테스트")
        class DeleteTest {}

        @Nested @DisplayName("merge 메서드 테스트")
        class MergeTest {}
    }

    // ── 응용 ──
    @Nested @DisplayName("HeapSort 테스트")
    class HeapSortTest {}

    @Nested @DisplayName("Top-K 테스트")
    class TopKTest {}

    @Nested @DisplayName("중앙값 스트림 테스트")
    class MedianStreamTest {}

    @Nested @DisplayName("K번째 큰 요소 테스트")
    class KthLargestTest {}

    @Nested @DisplayName("다익스트라 최단경로 테스트")
    class DijkstraTest {}
}