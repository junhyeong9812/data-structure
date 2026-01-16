package com.datastructure.heap;

import com.datastructure.heap.pop.MaxHeap;
import com.datastructure.heap.pop.MinHeap;
import com.datastructure.heap.pop.HeapSort;
import com.datastructure.heap.pop.HeapProblems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("힙 - POP 구현 테스트")
class PopTest {

    @Nested
    @DisplayName("최대 힙")
    class MaxHeapTest {
        
        private MaxHeap heap;

        @BeforeEach
        void setUp() {
            heap = new MaxHeap();
        }

        @Test
        @DisplayName("01. 빈 힙 생성 시 isEmpty는 true다")
        void test01_emptyHeapIsEmpty() {
            // TODO: isEmpty() 구현 후 테스트
            // assertThat(heap.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("02. insert 후 isEmpty는 false다")
        void test02_insertMakesNonEmpty() {
            // TODO: insert() 구현 후 테스트
            // heap.insert(5);
            // assertThat(heap.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("03. peek은 최댓값을 반환한다")
        void test03_peekReturnsMax() {
            // TODO: peek() 구현 후 테스트
            // heap.insert(3);
            // heap.insert(5);
            // heap.insert(1);
            // assertThat(heap.peek()).isEqualTo(5);
        }

        @Test
        @DisplayName("04. extractMax는 최댓값을 제거하고 반환한다")
        void test04_extractMaxRemovesMax() {
            // TODO: extractMax() 구현 후 테스트
            // heap.insert(3);
            // heap.insert(5);
            // heap.insert(1);
            // assertThat(heap.extractMax()).isEqualTo(5);
            // assertThat(heap.extractMax()).isEqualTo(3);
            // assertThat(heap.extractMax()).isEqualTo(1);
        }

        @Test
        @DisplayName("05. size가 정확히 반환된다")
        void test05_sizeIsCorrect() {
            // TODO: size() 구현 후 테스트
            // heap.insert(1);
            // heap.insert(2);
            // assertThat(heap.size()).isEqualTo(2);
            // heap.extractMax();
            // assertThat(heap.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("06. 빈 힙에서 extractMax 시 예외가 발생한다")
        void test06_extractMaxFromEmpty() {
            // TODO: 예외 처리 테스트
            // assertThatThrownBy(() -> heap.extractMax())
            //     .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("07. 빈 힙에서 peek 시 예외가 발생한다")
        void test07_peekFromEmpty() {
            // TODO: 예외 처리 테스트
            // assertThatThrownBy(() -> heap.peek())
            //     .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("08. 힙 속성이 유지된다")
        void test08_heapPropertyMaintained() {
            // TODO: 힙 속성 검증 테스트
            // heap.insert(3);
            // heap.insert(1);
            // heap.insert(4);
            // heap.insert(1);
            // heap.insert(5);
            // heap.insert(9);
            // int prev = Integer.MAX_VALUE;
            // while (!heap.isEmpty()) {
            //     int curr = heap.extractMax();
            //     assertThat(curr).isLessThanOrEqualTo(prev);
            //     prev = curr;
            // }
        }
    }

    @Nested
    @DisplayName("최소 힙")
    class MinHeapTest {
        
        private MinHeap heap;

        @BeforeEach
        void setUp() {
            heap = new MinHeap();
        }

        @Test
        @DisplayName("09. extractMin은 최솟값을 반환한다")
        void test09_extractMinReturnsMin() {
            // TODO: MinHeap 구현 후 테스트
            // heap.insert(3);
            // heap.insert(1);
            // heap.insert(4);
            // assertThat(heap.extractMin()).isEqualTo(1);
            // assertThat(heap.extractMin()).isEqualTo(3);
        }

        @Test
        @DisplayName("10. peek은 최솟값을 반환한다")
        void test10_peekReturnsMin() {
            // TODO: peek() 구현 후 테스트
            // heap.insert(5);
            // heap.insert(2);
            // heap.insert(8);
            // assertThat(heap.peek()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Heapify")
    class HeapifyTest {

        @Test
        @DisplayName("11. 배열로부터 최대 힙을 생성할 수 있다")
        void test11_heapifyMaxHeap() {
            // TODO: heapify() 구현 후 테스트
            // int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
            // MaxHeap heap = MaxHeap.heapify(arr);
            // assertThat(heap.extractMax()).isEqualTo(9);
            // assertThat(heap.extractMax()).isEqualTo(6);
        }

        @Test
        @DisplayName("12. heapify는 O(n) 시간에 동작한다")
        void test12_heapifyIsLinear() {
            // TODO: 대량 데이터 테스트
            // int[] arr = new int[10000];
            // for (int i = 0; i < arr.length; i++) {
            //     arr[i] = i;
            // }
            // MaxHeap heap = MaxHeap.heapify(arr);
            // assertThat(heap.size()).isEqualTo(10000);
        }
    }

    @Nested
    @DisplayName("힙 정렬")
    class HeapSortTest {

        @Test
        @DisplayName("13. 힙 정렬로 배열을 정렬할 수 있다")
        void test13_heapSort() {
            // TODO: HeapSort.sort() 구현 후 테스트
            // int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
            // HeapSort.sort(arr);
            // assertThat(arr).isSorted();
        }

        @Test
        @DisplayName("14. 이미 정렬된 배열도 정렬된다")
        void test14_sortedArray() {
            // TODO: 정렬된 배열 테스트
            // int[] arr = {1, 2, 3, 4, 5};
            // HeapSort.sort(arr);
            // assertThat(arr).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("15. 역순 배열도 정렬된다")
        void test15_reverseSortedArray() {
            // TODO: 역순 배열 테스트
            // int[] arr = {5, 4, 3, 2, 1};
            // HeapSort.sort(arr);
            // assertThat(arr).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("16. 중복 요소가 있어도 정렬된다")
        void test16_duplicates() {
            // TODO: 중복 요소 테스트
            // int[] arr = {3, 1, 3, 1, 3};
            // HeapSort.sort(arr);
            // assertThat(arr).containsExactly(1, 1, 3, 3, 3);
        }
    }

    @Nested
    @DisplayName("힙 응용 문제")
    class HeapProblemsTest {

        @Test
        @DisplayName("17. Top-K 요소를 찾을 수 있다")
        void test17_topK() {
            // TODO: topK() 구현 후 테스트
            // int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
            // int[] topK = HeapProblems.topK(arr, 3);
            // assertThat(topK).containsExactlyInAnyOrder(9, 6, 5);
        }

        @Test
        @DisplayName("18. K번째 큰 요소를 찾을 수 있다")
        void test18_kthLargest() {
            // TODO: kthLargest() 구현 후 테스트
            // int[] arr = {3, 2, 1, 5, 6, 4};
            // assertThat(HeapProblems.kthLargest(arr, 2)).isEqualTo(5);
        }

        @Test
        @DisplayName("19. 스트림에서 중앙값을 유지할 수 있다")
        void test19_medianStream() {
            // TODO: MedianFinder 구현 후 테스트
            // MedianFinder finder = new MedianFinder();
            // finder.addNum(1);
            // assertThat(finder.findMedian()).isEqualTo(1.0);
            // finder.addNum(2);
            // assertThat(finder.findMedian()).isEqualTo(1.5);
            // finder.addNum(3);
            // assertThat(finder.findMedian()).isEqualTo(2.0);
        }

        @Test
        @DisplayName("20. K개의 정렬된 리스트를 병합할 수 있다")
        void test20_mergeKSortedLists() {
            // TODO: mergeKSortedLists() 구현 후 테스트
            // int[][] lists = {{1, 4, 5}, {1, 3, 4}, {2, 6}};
            // int[] merged = HeapProblems.mergeKSortedLists(lists);
            // assertThat(merged).containsExactly(1, 1, 2, 3, 4, 4, 5, 6);
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("21. clear로 모든 요소를 삭제할 수 있다")
        void test21_clear() {
            // TODO: clear() 구현 후 테스트
            // MaxHeap heap = new MaxHeap();
            // heap.insert(1);
            // heap.insert(2);
            // heap.clear();
            // assertThat(heap.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("22. 대량 데이터 삽입/추출")
        void test22_largeData() {
            // TODO: 대량 데이터 테스트
            // MaxHeap heap = new MaxHeap();
            // for (int i = 0; i < 10000; i++) {
            //     heap.insert(i);
            // }
            // for (int i = 9999; i >= 0; i--) {
            //     assertThat(heap.extractMax()).isEqualTo(i);
            // }
        }

        @Test
        @DisplayName("23. 음수 값도 처리된다")
        void test23_negativeValues() {
            // TODO: 음수 테스트
            // MaxHeap heap = new MaxHeap();
            // heap.insert(-5);
            // heap.insert(-1);
            // heap.insert(-10);
            // assertThat(heap.extractMax()).isEqualTo(-1);
        }

        @Test
        @DisplayName("24. toArray로 배열 변환")
        void test24_toArray() {
            // TODO: toArray() 구현 후 테스트
            // MaxHeap heap = new MaxHeap();
            // heap.insert(3);
            // heap.insert(1);
            // heap.insert(4);
            // int[] arr = heap.toArray();
            // assertThat(arr).hasSize(3);
        }

        @Test
        @DisplayName("25. toString으로 힙 내용 확인")
        void test25_toString() {
            // TODO: toString() 구현 후 테스트
            // MaxHeap heap = new MaxHeap();
            // heap.insert(3);
            // heap.insert(1);
            // heap.insert(4);
            // assertThat(heap.toString()).contains("4");
        }

        @Test
        @DisplayName("26. increaseKey로 값을 증가시킬 수 있다")
        void test26_increaseKey() {
            // TODO: increaseKey() 구현 후 테스트
            // MaxHeap heap = new MaxHeap();
            // heap.insert(3);
            // heap.insert(5);
            // heap.insert(1);
            // heap.increaseKey(2, 10);  // 1 → 10
            // assertThat(heap.peek()).isEqualTo(10);
        }

        @Test
        @DisplayName("27. decreaseKey로 값을 감소시킬 수 있다")
        void test27_decreaseKey() {
            // TODO: decreaseKey() 구현 후 테스트
            // MinHeap heap = new MinHeap();
            // heap.insert(3);
            // heap.insert(5);
            // heap.insert(1);
            // heap.decreaseKey(1, 0);  // 5 → 0
            // assertThat(heap.peek()).isEqualTo(0);
        }

        @Test
        @DisplayName("28. delete로 특정 위치 요소 삭제")
        void test28_delete() {
            // TODO: delete() 구현 후 테스트
            // MaxHeap heap = new MaxHeap();
            // heap.insert(3);
            // heap.insert(5);
            // heap.insert(1);
            // heap.delete(1);  // 인덱스 1의 요소 삭제
            // assertThat(heap.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("29. 두 힙을 병합할 수 있다")
        void test29_merge() {
            // TODO: merge() 구현 후 테스트
            // MaxHeap heap1 = new MaxHeap();
            // heap1.insert(1);
            // heap1.insert(3);
            // MaxHeap heap2 = new MaxHeap();
            // heap2.insert(2);
            // heap2.insert(4);
            // heap1.merge(heap2);
            // assertThat(heap1.size()).isEqualTo(4);
            // assertThat(heap1.extractMax()).isEqualTo(4);
        }

        @Test
        @DisplayName("30. contains로 요소 존재 확인")
        void test30_contains() {
            // TODO: contains() 구현 후 테스트
            // MaxHeap heap = new MaxHeap();
            // heap.insert(3);
            // heap.insert(5);
            // assertThat(heap.contains(3)).isTrue();
            // assertThat(heap.contains(10)).isFalse();
        }
    }
}
