package com.datastructure.heap;

import com.datastructure.heap.pop.MaxHeap;
import com.datastructure.heap.pop.MinHeap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
        class OfferTest {}

        @Nested @DisplayName("extractMax 메서드 테스트")
        class ExtractMaxTest {}

        @Nested @DisplayName("poll 메서드 테스트")
        class PollTest {}

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