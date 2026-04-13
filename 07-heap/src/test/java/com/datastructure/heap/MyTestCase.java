package com.datastructure.heap;

import com.datastructure.heap.pop.MaxHeap;
import com.datastructure.heap.pop.MinHeap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
                heap = new MaxHeap(10);
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
        class PeekTest {}

        @Nested @DisplayName("getMax 메서드 테스트")
        class GetMaxTest {}

        @Nested @DisplayName("size 메서드 테스트")
        class SizeTest {}

        @Nested @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {}

        @Nested @DisplayName("clear 메서드 테스트")
        class ClearTest {}

        @Nested @DisplayName("heapify 메서드 테스트")
        class HeapifyTest {}

        @Nested @DisplayName("increaseKey 메서드 테스트")
        class IncreaseKeyTest {}

        @Nested @DisplayName("delete 메서드 테스트")
        class DeleteTest {}

        @Nested @DisplayName("merge 메서드 테스트")
        class MergeTest {}
    }

    @Nested @DisplayName("MinHeap 테스트")
    class MinHeapTest {
        @Nested @DisplayName("insert 메서드 테스트")
        class InsertTest {}

        @Nested @DisplayName("offer 메서드 테스트")
        class OfferTest {}

        @Nested @DisplayName("extractMin 메서드 테스트")
        class ExtractMinTest {}

        @Nested @DisplayName("poll 메서드 테스트")
        class PollTest {}

        @Nested @DisplayName("peek 메서드 테스트")
        class PeekTest {}

        @Nested @DisplayName("getMin 메서드 테스트")
        class GetMinTest {}

        @Nested @DisplayName("size 메서드 테스트")
        class SizeTest {}

        @Nested @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {}

        @Nested @DisplayName("clear 메서드 테스트")
        class ClearTest {}

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