package com.datastructure.heap;

import com.datastructure.heap.pop.HeapProblems;
import com.datastructure.heap.pop.HeapSort;
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
        class HeapifyTest {
            @Test @DisplayName("빈 배열이 들어오면 빈 힙을 반환한다.")
            void heapify_empty_array_returns_empty_heap() {
                Integer[] array = {};
                MinHeap<Integer> result = MinHeap.heapify(array);

                assertThat(result.isEmpty()).isTrue();
                assertThat(result.size()).isZero();
            }

            @Test @DisplayName("요소가 하나인 배열을 힙으로 변환한다.")
            void heapify_single_element() {
                Integer[] array = {3};
                MinHeap<Integer> result = MinHeap.heapify(array);

                assertThat(result.size()).isEqualTo(1);
                assertThat(result.peek()).isEqualTo(3);
            }

            @Test @DisplayName("여러 요소의 배열을 힙으로 변환한다.")
            void heapify_multiple_elements() {
                Integer[] array = {3, 1, 5, 2, 4};
                MinHeap<Integer> result = MinHeap.heapify(array);

                assertThat(result.size()).isEqualTo(5);
                assertThat(result.peek()).isEqualTo(1);
            }

            @Test @DisplayName("변환된 힙은 최솟값이 루트에 위치한다.")
            void heapify_maintains_min_heap_property() {
                Integer[] array = {3, 1, 5, 2, 4};
                MinHeap<Integer> result = MinHeap.heapify(array);

                assertThat(result.peek()).isEqualTo(1);
            }

            @Test @DisplayName("중복 값이 있는 배열도 그대로 힙으로 변환한다.")
            void heapify_with_duplicate_values() {
                Integer[] array = {3, 3, 5, 2, 5};
                MinHeap<Integer> result = MinHeap.heapify(array);

                assertThat(result.size()).isEqualTo(5);
                assertThat(result.peek()).isEqualTo(2);
            }

            @Test @DisplayName("null 배열이 들어오면 예외가 발생한다.")
            void heapify_null_array_throws_exception() {
                assertThatThrownBy(() -> MinHeap.heapify(null))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("null 요소가 포함된 배열이 들어오면 예외가 발생한다.")
            void heapify_array_with_null_element_throws_exception() {
                Integer[] array = {null, 1, 5, 2, 4};
                assertThatThrownBy(() -> MinHeap.heapify(array))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested @DisplayName("decreaseKey 메서드 테스트")
        class DecreaseKeyTest {
            @Test @DisplayName("빈 힙에서 호출하면 예외가 발생한다.")
            void decreaseKey_empty_heap_throws_exception() {
                assertThatThrownBy(() -> heap.decreaseKey(1,10))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test @DisplayName("index가 음수이면 예외가 발생한다.")
            void decreaseKey_negative_index_throws_exception() {
                heap.insert(1);

                assertThatThrownBy(() -> heap.decreaseKey(-1, 10))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test @DisplayName("index가 size 이상이면 예외가 발생한다.")
            void decreaseKey_index_out_of_bounds_throws_exception() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);

                assertThatThrownBy(() -> heap.decreaseKey(3, 10))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test @DisplayName("newValue가 null이면 예외가 발생한다.")
            void decreaseKey_null_value_throws_exception() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);

                assertThatThrownBy(() -> heap.decreaseKey(1, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("기존 값보다 큰 값을 넣으면 예외가 발생한다.")
            void decreaseKey_bigger_value_throws_exception() {
                heap.insert(5);

                assertThatThrownBy(() -> heap.decreaseKey(0, 10))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test @DisplayName("기존 값과 같은 값을 넣으면 변화가 없다.")
            void decreaseKey_same_value_no_change() {
                heap.insert(1);
                heap.insert(2);
                heap.insert(3);
                assertThat(heap.peek()).isEqualTo(1);

                heap.decreaseKey(2, 3);

                assertThat(heap.peek()).isEqualTo(1);
            }

            @Test @DisplayName("값 감소 후 힙 속성이 유지된다.")
            void decreaseKey_maintains_min_heap_property() {
                heap.insert(3);
                heap.insert(5);
                heap.insert(7);
                heap.insert(9);
                heap.insert(8);
                assertThat(heap.peek()).isEqualTo(3);

                heap.decreaseKey(3, 1);

                assertThat(heap.peek()).isEqualTo(1);
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

            @Test @DisplayName("루트 요소를 삭제하면 다음 최솟값이 루트가 된다.")
            void delete_root_element() {
                heap.insert(3);
                heap.insert(5);
                heap.insert(1);
                assertThat(heap.peek()).isEqualTo(1);

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
                assertThat(heap.peek()).isEqualTo(1);

                heap.delete(3);

                assertThat(heap.peek()).isEqualTo(1);
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
            MinHeap<Integer> heap2;

            @BeforeEach
            void setUp() { heap2 = new MinHeap<Integer>();}

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
                assertThat(heap.peek()).isEqualTo(1);
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
                assertThat(heap.peek()).isEqualTo(1);
            }

            @Test @DisplayName("병합 후 힙 속성이 유지된다.")
            void merge_maintains_min_heap_property() {
                heap.insert(2);
                heap.insert(4);

                heap2.insert(1);
                heap2.insert(3);
                assertThat(heap.peek()).isEqualTo(2);

                heap = heap.merge(heap2);

                assertThat(heap.peek()).isEqualTo(1);
            }

            @Test @DisplayName("null 힙과 병합하면 예외가 발생한다.")
            void merge_null_throws_exception() {
                heap.insert(1);

                assertThatThrownBy(() -> heap.merge(null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    // ── 응용 ──
    @Nested @DisplayName("HeapSort 테스트")
    class HeapSortTest {

        @Test @DisplayName("빈 배열 정렬")
        void sort_empty_array() {
            int[] array = {};
            int[] result = HeapSort.sortAscending(array);

            assertThat(result).isEmpty();
        }

        @Test @DisplayName("요소가 하나인 배열 정렬")
        void sort_single_element() {
            int[] array = {3};
            int[] result = HeapSort.sortAscending(array);

            assertThat(result).containsExactly(3);
        }

        @Test @DisplayName("여러 요소 오름차순 정렬")
        void sort_multiple_elements_ascending() {
            int[] array = {3,1,2,6,5,4};
            int[] result = HeapSort.sortAscending(array);

            assertThat(result).containsExactly(1, 2, 3, 4, 5, 6);
        }

        @Test @DisplayName("이미 정렬된 배열")
        void sort_already_sorted_array() {
            int[] array = {1, 2, 3, 4, 5, 6};
            int[] result = HeapSort.sortAscending(array);

            assertThat(result).containsExactly(1, 2, 3, 4, 5, 6);
        }

        @Test @DisplayName("역순 정렬")
        void sort_descending_order() {
            int[] array = {3,1,2,6,5,4};
            int[] result = HeapSort.sortDescending(array);

            assertThat(result).containsExactly(6, 5, 4, 3, 2, 1);
        }

        @Test @DisplayName("중복값이 있는 정렬")
        void sort_with_duplicate_values() {
            int[] array = {4,1,2,6,4,4};
            int[] result = HeapSort.sortAscending(array);

            assertThat(result).containsExactly(1, 2, 4, 4, 4, 6);
        }
    }

    @Nested @DisplayName("Top-K 테스트")
    class TopKTest {
        @Test @DisplayName("k가 0이면 빈 결과")
        void topK_zero_returns_empty() {
            int[] array = {0,1};
            List<Integer> result= HeapProblems.topK(array, 0);
            assertThat(result.isEmpty()).isTrue();
        }

        @Test @DisplayName("k가 배열 크기보다 크면 예외 또는 전체 반환")
        void topK_k_larger_than_size_returns_all() {
            int[] array = {1,2,3};
            List<Integer> result = HeapProblems.topK(array, 5);
            assertThat(result).containsExactlyInAnyOrder(1,2,3);
        }

        @Test @DisplayName("Top-3 추출 시 상위 3개 반환")
        void topK_returns_top_three() {
            int[] array = {1, 2, 3, 4, 5, 6};
            List<Integer> result = HeapProblems.topK(array, 3);
            assertThat(result).containsExactlyInAnyOrder(6,5,4);
        }

        @Test @DisplayName("중복 값이 있는 경우")
        void topK_with_duplicate_values() {
            int[] array = {1, 2, 3, 5, 5, 6};
            List<Integer> result = HeapProblems.topK(array, 3);
            assertThat(result).containsExactlyInAnyOrder(6,5,5);
        }
    }

    @Nested @DisplayName("중앙값 스트림 테스트")
    class MedianStreamTest {
        @Test @DisplayName("요소 하나 → 그 값이 중앙값")
        void medianStream_single_element() {
            int[] stream = {1};
            double[] result = HeapProblems.medianStream(stream);
            assertThat(result).containsExactly(1.0);
        }

        @Test @DisplayName("요소 두 개 → 매 시점의 중앙값")
        void medianStream_two_elements() {
            int[] stream = {1, 2};
            double[] result = HeapProblems.medianStream(stream);
            assertThat(result).containsExactly(1.0, 1.5);
        }

        @Test @DisplayName("홀수 개 → 매 시점의 중앙값")
        void medianStream_odd_elements() {
            int[] stream = {1, 2, 3};
            double[] result = HeapProblems.medianStream(stream);
            assertThat(result).containsExactly(1.0, 1.5, 2.0);
        }

        @Test @DisplayName("짝수 개 → 매 시점의 중앙값")
        void medianStream_even_elements() {
            int[] stream = {1, 2, 3, 4};
            double[] result = HeapProblems.medianStream(stream);
            assertThat(result).containsExactly(1.0, 1.5, 2.0, 2.5);
        }
    }

    @Nested @DisplayName("K번째 큰 요소 테스트")
    class KthLargestTest {
        @Test @DisplayName("k가 1이면 최댓값")
        void kthLargest_k_is_one_returns_max() {
            int[] array = {1,2,3,4};
            int result = HeapProblems.kthLargest(array, 1);
            assertThat(result).isEqualTo(4);
        }

        @Test @DisplayName("k가 배열 크기이면 최솟값")
        void kthLargest_k_is_size_returns_min() {
            int[] array = {1,2,3,4};
            int result = HeapProblems.kthLargest(array, 4);
            assertThat(result).isEqualTo(1);
        }

        @Test @DisplayName("k가 범위 밖이면 예외")
        void kthLargest_k_out_of_range_throws_exception() {
            int[] array = {1,2,3,4};
            assertThatThrownBy(() -> HeapProblems.kthLargest(array, 5))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test @DisplayName("중복 값이 있는 경우")
        void kthLargest_with_duplicate_values() {
            int[] array = {1,2,3,3,5};
            int result = HeapProblems.kthLargest(array, 4);
            assertThat(result).isEqualTo(3);
        }
    }

    @Nested @DisplayName("다익스트라 최단경로 테스트")
    class DijkstraTest {
        @Test @DisplayName("시작점에서 자기 자신까지 거리는 0")
        void dijkstra_start_to_self_is_zero() {
            // 노드 3개, 연결 없음에도 자기 자신은 0
            int[][] graph = {
                    {0, 1, -1},
                    {-1, 0, -1},
                    {-1, -1, 0}
            };

            int[] result = HeapProblems.dijkstra(graph, 0);

            assertThat(result[0]).isEqualTo(0);
        }

        @Test @DisplayName("직접 연결된 노드까지 최단거리")
        void dijkstra_direct_connection() {
            //  0 --5--> 1 --3--> 2
            int[][] graph = {
                    {0, 5, -1},
                    {-1, 0, 3},
                    {-1, -1, 0}
            };

            int[] result = HeapProblems.dijkstra(graph, 0);

            assertThat(result[0]).isEqualTo(0);
            assertThat(result[1]).isEqualTo(5);
            assertThat(result[2]).isEqualTo(8);
        }

        @Test @DisplayName("경유하는 게 더 짧은 경우")
        void dijkstra_shorter_via_detour() {
            //  0 --10--> 2
            //  0 --1-->  1 --2--> 2
            //  직접: 10, 경유: 1+2=3
            int[][] graph = {
                    {0,  1, 10},
                    {-1, 0,  2},
                    {-1, -1, 0}
            };

            int[] result = HeapProblems.dijkstra(graph, 0);

            assertThat(result[0]).isEqualTo(0);
            assertThat(result[1]).isEqualTo(1);
            assertThat(result[2]).isEqualTo(3);
        }

        @Test @DisplayName("도달 불가능한 노드")
        void dijkstra_unreachable_node() {
            //  0 --1--> 1, 노드 2는 연결 없음
            int[][] graph = {
                    {0, 1, -1},
                    {-1, 0, -1},
                    {-1, -1, 0}
            };

            int[] result = HeapProblems.dijkstra(graph, 0);

            assertThat(result[0]).isEqualTo(0);
            assertThat(result[1]).isEqualTo(1);
            assertThat(result[2]).isEqualTo(-1);
        }
    }
}