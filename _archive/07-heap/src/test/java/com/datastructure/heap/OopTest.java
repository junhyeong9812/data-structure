package com.datastructure.heap;

import com.datastructure.heap.oop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.*;

@DisplayName("힙 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("BinaryHeap 기본")
    class BinaryHeapBasicTest {
        
        private Heap<Integer> heap;

        @BeforeEach
        void setUp() {
            heap = new BinaryHeap<>();
        }

        @Test
        @DisplayName("01. 빈 힙 생성 시 isEmpty는 true다")
        void test01_emptyHeapIsEmpty() {
            // TODO: isEmpty() 구현 후 테스트
            // assertThat(heap.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("02. offer로 요소를 추가할 수 있다")
        void test02_offer() {
            // TODO: offer() 구현 후 테스트
            // heap.offer(5);
            // assertThat(heap.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("03. poll로 요소를 제거할 수 있다")
        void test03_poll() {
            // TODO: poll() 구현 후 테스트
            // heap.offer(5);
            // assertThat(heap.poll()).isEqualTo(5);
            // assertThat(heap.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("04. peek은 제거하지 않고 조회한다")
        void test04_peek() {
            // TODO: peek() 구현 후 테스트
            // heap.offer(5);
            // assertThat(heap.peek()).isEqualTo(5);
            // assertThat(heap.peek()).isEqualTo(5);
        }

        @Test
        @DisplayName("05. 기본은 최소 힙이다")
        void test05_defaultIsMinHeap() {
            // TODO: 최소 힙 테스트
            // heap.offer(3);
            // heap.offer(1);
            // heap.offer(4);
            // assertThat(heap.poll()).isEqualTo(1);
            // assertThat(heap.poll()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Comparator 사용")
    class ComparatorTest {

        @Test
        @DisplayName("06. Comparator로 최대 힙을 만들 수 있다")
        void test06_maxHeapWithComparator() {
            // TODO: Comparator 지원 테스트
            // Heap<Integer> maxHeap = new BinaryHeap<>(Comparator.reverseOrder());
            // maxHeap.offer(3);
            // maxHeap.offer(1);
            // maxHeap.offer(4);
            // assertThat(maxHeap.poll()).isEqualTo(4);
        }

        @Test
        @DisplayName("07. 사용자 정의 Comparator 사용")
        void test07_customComparator() {
            // TODO: 사용자 정의 Comparator 테스트
            // Comparator<String> byLength = Comparator.comparingInt(String::length);
            // Heap<String> heap = new BinaryHeap<>(byLength);
            // heap.offer("aaa");
            // heap.offer("a");
            // heap.offer("aa");
            // assertThat(heap.poll()).isEqualTo("a");
        }
    }

    @Nested
    @DisplayName("제네릭 타입")
    class GenericTypeTest {

        @Test
        @DisplayName("08. String 타입 힙")
        void test08_stringHeap() {
            // TODO: 제네릭 테스트
            // Heap<String> heap = new BinaryHeap<>();
            // heap.offer("banana");
            // heap.offer("apple");
            // heap.offer("cherry");
            // assertThat(heap.poll()).isEqualTo("apple");
        }

        @Test
        @DisplayName("09. 사용자 정의 Comparable 객체")
        void test09_customComparable() {
            // TODO: Comparable 테스트
            // record Task(int priority, String name) implements Comparable<Task> {
            //     public int compareTo(Task o) { return Integer.compare(priority, o.priority); }
            // }
            // Heap<Task> heap = new BinaryHeap<>();
            // heap.offer(new Task(3, "low"));
            // heap.offer(new Task(1, "high"));
            // assertThat(heap.poll().name()).isEqualTo("high");
        }
    }

    @Nested
    @DisplayName("PriorityQueue 인터페이스")
    class PriorityQueueTest {

        @Test
        @DisplayName("10. PriorityQueue로 사용")
        void test10_asPriorityQueue() {
            // TODO: PriorityQueue 인터페이스 테스트
            // PriorityQueue<Integer> pq = new BinaryHeap<>();
            // pq.offer(3);
            // pq.offer(1);
            // assertThat(pq.poll()).isEqualTo(1);
        }

        @Test
        @DisplayName("11. 빈 큐에서 poll은 null 반환")
        void test11_pollFromEmpty() {
            // TODO: null 반환 테스트
            // PriorityQueue<Integer> pq = new BinaryHeap<>();
            // assertThat(pq.poll()).isNull();
        }

        @Test
        @DisplayName("12. 빈 큐에서 peek은 null 반환")
        void test12_peekFromEmpty() {
            // TODO: null 반환 테스트
            // PriorityQueue<Integer> pq = new BinaryHeap<>();
            // assertThat(pq.peek()).isNull();
        }
    }

    @Nested
    @DisplayName("MedianFinder")
    class MedianFinderTest {

        @Test
        @DisplayName("13. 단일 요소의 중앙값")
        void test13_singleElement() {
            // TODO: MedianFinder 구현 후 테스트
            // MedianFinder finder = new MedianFinder();
            // finder.addNum(1);
            // assertThat(finder.findMedian()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("14. 짝수 개 요소의 중앙값")
        void test14_evenElements() {
            // TODO: 짝수 개 테스트
            // MedianFinder finder = new MedianFinder();
            // finder.addNum(1);
            // finder.addNum(2);
            // assertThat(finder.findMedian()).isEqualTo(1.5);
        }

        @Test
        @DisplayName("15. 홀수 개 요소의 중앙값")
        void test15_oddElements() {
            // TODO: 홀수 개 테스트
            // MedianFinder finder = new MedianFinder();
            // finder.addNum(1);
            // finder.addNum(2);
            // finder.addNum(3);
            // assertThat(finder.findMedian()).isEqualTo(2.0);
        }

        @Test
        @DisplayName("16. 순서 상관없이 중앙값 유지")
        void test16_anyOrder() {
            // TODO: 순서 무관 테스트
            // MedianFinder finder = new MedianFinder();
            // finder.addNum(3);
            // finder.addNum(1);
            // finder.addNum(2);
            // assertThat(finder.findMedian()).isEqualTo(2.0);
        }
    }

    @Nested
    @DisplayName("KthLargest")
    class KthLargestTest {

        @Test
        @DisplayName("17. K번째 큰 요소 추적")
        void test17_kthLargest() {
            // TODO: KthLargest 구현 후 테스트
            // KthLargest kth = new KthLargest(3, new int[]{4, 5, 8, 2});
            // assertThat(kth.add(3)).isEqualTo(4);
            // assertThat(kth.add(5)).isEqualTo(5);
            // assertThat(kth.add(10)).isEqualTo(5);
            // assertThat(kth.add(9)).isEqualTo(8);
            // assertThat(kth.add(4)).isEqualTo(8);
        }

        @Test
        @DisplayName("18. 빈 초기 배열")
        void test18_emptyInitial() {
            // TODO: 빈 초기 배열 테스트
            // KthLargest kth = new KthLargest(1, new int[]{});
            // assertThat(kth.add(1)).isEqualTo(1);
            // assertThat(kth.add(2)).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("기타 기능")
    class Miscellaneous {

        @Test
        @DisplayName("19. size 정확성")
        void test19_size() {
            // TODO: size() 테스트
            // Heap<Integer> heap = new BinaryHeap<>();
            // heap.offer(1);
            // heap.offer(2);
            // assertThat(heap.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("20. clear")
        void test20_clear() {
            // TODO: clear() 테스트
            // Heap<Integer> heap = new BinaryHeap<>();
            // heap.offer(1);
            // heap.offer(2);
            // heap.clear();
            // assertThat(heap.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("21. contains")
        void test21_contains() {
            // TODO: contains() 테스트
            // Heap<Integer> heap = new BinaryHeap<>();
            // heap.offer(1);
            // heap.offer(2);
            // assertThat(heap.contains(1)).isTrue();
            // assertThat(heap.contains(3)).isFalse();
        }

        @Test
        @DisplayName("22. remove 특정 요소")
        void test22_remove() {
            // TODO: remove() 테스트
            // Heap<Integer> heap = new BinaryHeap<>();
            // heap.offer(1);
            // heap.offer(2);
            // heap.offer(3);
            // heap.remove(2);
            // assertThat(heap.contains(2)).isFalse();
        }

        @Test
        @DisplayName("23. Iterator 지원")
        void test23_iterator() {
            // TODO: Iterator 테스트
            // Heap<Integer> heap = new BinaryHeap<>();
            // heap.offer(3);
            // heap.offer(1);
            // heap.offer(2);
            // List<Integer> list = new ArrayList<>();
            // for (Integer i : heap) {
            //     list.add(i);
            // }
            // assertThat(list).hasSize(3);
        }

        @Test
        @DisplayName("24. toArray")
        void test24_toArray() {
            // TODO: toArray() 테스트
            // Heap<Integer> heap = new BinaryHeap<>();
            // heap.offer(3);
            // heap.offer(1);
            // Integer[] arr = heap.toArray(new Integer[0]);
            // assertThat(arr).hasSize(2);
        }

        @Test
        @DisplayName("25. equals")
        void test25_equals() {
            // TODO: equals() 테스트
            // Heap<Integer> heap1 = new BinaryHeap<>();
            // Heap<Integer> heap2 = new BinaryHeap<>();
            // heap1.offer(1);
            // heap2.offer(1);
            // assertThat(heap1).isEqualTo(heap2);
        }

        @Test
        @DisplayName("26. hashCode")
        void test26_hashCode() {
            // TODO: hashCode() 테스트
            // Heap<Integer> heap1 = new BinaryHeap<>();
            // Heap<Integer> heap2 = new BinaryHeap<>();
            // heap1.offer(1);
            // heap2.offer(1);
            // assertThat(heap1.hashCode()).isEqualTo(heap2.hashCode());
        }

        @Test
        @DisplayName("27. toString")
        void test27_toString() {
            // TODO: toString() 테스트
            // Heap<Integer> heap = new BinaryHeap<>();
            // heap.offer(1);
            // assertThat(heap.toString()).contains("1");
        }

        @Test
        @DisplayName("28. stream 지원")
        void test28_stream() {
            // TODO: stream() 테스트
            // Heap<Integer> heap = new BinaryHeap<>();
            // heap.offer(1);
            // heap.offer(2);
            // heap.offer(3);
            // int sum = heap.stream().mapToInt(Integer::intValue).sum();
            // assertThat(sum).isEqualTo(6);
        }

        @Test
        @DisplayName("29. 대량 데이터")
        void test29_largeData() {
            // TODO: 대량 데이터 테스트
            // Heap<Integer> heap = new BinaryHeap<>();
            // for (int i = 0; i < 10000; i++) {
            //     heap.offer(i);
            // }
            // assertThat(heap.size()).isEqualTo(10000);
        }

        @Test
        @DisplayName("30. null 요소 거부")
        void test30_rejectNull() {
            // TODO: null 거부 테스트
            // Heap<Integer> heap = new BinaryHeap<>();
            // assertThatThrownBy(() -> heap.offer(null))
            //     .isInstanceOf(NullPointerException.class);
        }
    }
}
