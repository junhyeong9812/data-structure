package com.datastructure.queue;

import com.datastructure.queue.oop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("큐/덱 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("배열 기반 큐")
    class ArrayQueueTest {
        
        private Queue<Integer> queue;

        @BeforeEach
        void setUp() {
            queue = new ArrayQueueImpl<>();
        }

        @Test
        @DisplayName("01. 빈 큐 생성 시 isEmpty는 true다")
        void test01_emptyQueueIsEmpty() {
            // TODO: isEmpty() 구현 후 테스트
            // assertThat(queue.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("02. offer 후 isEmpty는 false다")
        void test02_offerMakesNonEmpty() {
            // TODO: offer() 구현 후 테스트
            // queue.offer(1);
            // assertThat(queue.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("03. FIFO 순서로 poll된다")
        void test03_fifoOrder() {
            // TODO: FIFO 순서 테스트
            // queue.offer(1);
            // queue.offer(2);
            // queue.offer(3);
            // assertThat(queue.poll()).isEqualTo(1);
            // assertThat(queue.poll()).isEqualTo(2);
            // assertThat(queue.poll()).isEqualTo(3);
        }

        @Test
        @DisplayName("04. peek은 요소를 제거하지 않는다")
        void test04_peekDoesNotRemove() {
            // TODO: peek() 구현 후 테스트
            // queue.offer(1);
            // assertThat(queue.peek()).isEqualTo(1);
            // assertThat(queue.peek()).isEqualTo(1);
        }

        @Test
        @DisplayName("05. poll은 빈 큐에서 null을 반환한다")
        void test05_pollFromEmptyReturnsNull() {
            // TODO: poll() 구현 후 테스트
            // assertThat(queue.poll()).isNull();
        }

        @Test
        @DisplayName("06. remove는 빈 큐에서 예외를 발생시킨다")
        void test06_removeFromEmptyThrows() {
            // TODO: remove() 구현 후 테스트
            // assertThatThrownBy(() -> queue.remove())
            //     .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("연결 리스트 기반 큐")
    class LinkedQueueTest {
        
        private Queue<Integer> queue;

        @BeforeEach
        void setUp() {
            queue = new LinkedQueueImpl<>();
        }

        @Test
        @DisplayName("07. 기본 offer/poll 동작")
        void test07_basicOfferPoll() {
            // TODO: LinkedQueueImpl 구현 후 테스트
            // queue.offer(1);
            // queue.offer(2);
            // assertThat(queue.poll()).isEqualTo(1);
        }

        @Test
        @DisplayName("08. null 요소를 저장할 수 있다")
        void test08_nullElement() {
            // TODO: null 처리 테스트
            // queue.offer(null);
            // assertThat(queue.peek()).isNull();
            // assertThat(queue.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("배열 기반 덱")
    class ArrayDequeTest {
        
        private Deque<Integer> deque;

        @BeforeEach
        void setUp() {
            deque = new ArrayDequeImpl<>();
        }

        @Test
        @DisplayName("09. addFirst로 앞에 추가")
        void test09_addFirst() {
            // TODO: addFirst() 구현 후 테스트
            // deque.addFirst(1);
            // deque.addFirst(2);
            // assertThat(deque.peekFirst()).isEqualTo(2);
        }

        @Test
        @DisplayName("10. addLast로 뒤에 추가")
        void test10_addLast() {
            // TODO: addLast() 구현 후 테스트
            // deque.addLast(1);
            // deque.addLast(2);
            // assertThat(deque.peekLast()).isEqualTo(2);
        }

        @Test
        @DisplayName("11. removeFirst로 앞에서 제거")
        void test11_removeFirst() {
            // TODO: removeFirst() 구현 후 테스트
            // deque.addLast(1);
            // deque.addLast(2);
            // assertThat(deque.removeFirst()).isEqualTo(1);
        }

        @Test
        @DisplayName("12. removeLast로 뒤에서 제거")
        void test12_removeLast() {
            // TODO: removeLast() 구현 후 테스트
            // deque.addLast(1);
            // deque.addLast(2);
            // assertThat(deque.removeLast()).isEqualTo(2);
        }

        @Test
        @DisplayName("13. pollFirst는 빈 덱에서 null 반환")
        void test13_pollFirstEmpty() {
            // TODO: pollFirst() 구현 후 테스트
            // assertThat(deque.pollFirst()).isNull();
        }

        @Test
        @DisplayName("14. pollLast는 빈 덱에서 null 반환")
        void test14_pollLastEmpty() {
            // TODO: pollLast() 구현 후 테스트
            // assertThat(deque.pollLast()).isNull();
        }
    }

    @Nested
    @DisplayName("연결 리스트 기반 덱")
    class LinkedDequeTest {
        
        private Deque<Integer> deque;

        @BeforeEach
        void setUp() {
            deque = new LinkedDequeImpl<>();
        }

        @Test
        @DisplayName("15. 기본 양방향 연산")
        void test15_basicBidirectional() {
            // TODO: LinkedDequeImpl 구현 후 테스트
            // deque.addFirst(2);
            // deque.addFirst(1);
            // deque.addLast(3);
            // assertThat(deque.removeFirst()).isEqualTo(1);
            // assertThat(deque.removeLast()).isEqualTo(3);
        }

        @Test
        @DisplayName("16. Iterator로 순회")
        void test16_iterator() {
            // TODO: Iterator 구현 후 테스트
            // deque.addLast(1);
            // deque.addLast(2);
            // deque.addLast(3);
            // int sum = 0;
            // for (Integer i : deque) {
            //     sum += i;
            // }
            // assertThat(sum).isEqualTo(6);
        }

        @Test
        @DisplayName("17. descendingIterator로 역순 순회")
        void test17_descendingIterator() {
            // TODO: descendingIterator 구현 후 테스트
            // deque.addLast(1);
            // deque.addLast(2);
            // deque.addLast(3);
            // Iterator<Integer> iter = deque.descendingIterator();
            // assertThat(iter.next()).isEqualTo(3);
            // assertThat(iter.next()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("제네릭 타입")
    class GenericTypeTest {

        @Test
        @DisplayName("18. String 타입 큐")
        void test18_stringQueue() {
            // TODO: 제네릭 테스트
            // Queue<String> queue = new ArrayQueueImpl<>();
            // queue.offer("Hello");
            // queue.offer("World");
            // assertThat(queue.poll()).isEqualTo("Hello");
        }

        @Test
        @DisplayName("19. 사용자 정의 객체 덱")
        void test19_customObjectDeque() {
            // TODO: 사용자 정의 객체 테스트
            // record Person(String name) {}
            // Deque<Person> deque = new LinkedDequeImpl<>();
            // deque.addLast(new Person("Alice"));
            // assertThat(deque.peekFirst().name()).isEqualTo("Alice");
        }
    }

    @Nested
    @DisplayName("슬라이딩 윈도우 최댓값")
    class SlidingWindowMaxTest {

        @Test
        @DisplayName("20. 기본 케이스")
        void test20_basicCase() {
            // TODO: SlidingWindowMax 구현 후 테스트
            // int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
            // int[] result = SlidingWindowMax.maxSlidingWindow(nums, 3);
            // assertThat(result).containsExactly(3, 3, 5, 5, 6, 7);
        }

        @Test
        @DisplayName("21. 단일 요소")
        void test21_singleElement() {
            // TODO: 단일 요소 테스트
            // int[] nums = {1};
            // int[] result = SlidingWindowMax.maxSlidingWindow(nums, 1);
            // assertThat(result).containsExactly(1);
        }

        @Test
        @DisplayName("22. 모든 요소 동일")
        void test22_allSame() {
            // TODO: 동일 요소 테스트
            // int[] nums = {5, 5, 5, 5};
            // int[] result = SlidingWindowMax.maxSlidingWindow(nums, 2);
            // assertThat(result).containsExactly(5, 5, 5);
        }

        @Test
        @DisplayName("23. 음수 포함")
        void test23_negativeNumbers() {
            // TODO: 음수 테스트
            // int[] nums = {-7, -8, 7, 5, 7, 1, 6, 0};
            // int[] result = SlidingWindowMax.maxSlidingWindow(nums, 4);
            // assertThat(result).containsExactly(7, 7, 7, 7, 7);
        }
    }

    @Nested
    @DisplayName("기타 기능")
    class Miscellaneous {

        @Test
        @DisplayName("24. clear로 모든 요소 삭제")
        void test24_clear() {
            // TODO: clear() 구현 후 테스트
            // Queue<Integer> queue = new ArrayQueueImpl<>();
            // queue.offer(1);
            // queue.offer(2);
            // queue.clear();
            // assertThat(queue.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("25. size 정확성")
        void test25_size() {
            // TODO: size() 테스트
            // Deque<Integer> deque = new ArrayDequeImpl<>();
            // deque.addFirst(1);
            // deque.addLast(2);
            // assertThat(deque.size()).isEqualTo(2);
            // deque.removeFirst();
            // assertThat(deque.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("26. contains 확인")
        void test26_contains() {
            // TODO: contains() 구현 후 테스트
            // Queue<Integer> queue = new ArrayQueueImpl<>();
            // queue.offer(1);
            // queue.offer(2);
            // assertThat(queue.contains(1)).isTrue();
            // assertThat(queue.contains(3)).isFalse();
        }

        @Test
        @DisplayName("27. toArray 변환")
        void test27_toArray() {
            // TODO: toArray() 구현 후 테스트
            // Deque<Integer> deque = new LinkedDequeImpl<>();
            // deque.addLast(1);
            // deque.addLast(2);
            // deque.addLast(3);
            // Object[] arr = deque.toArray();
            // assertThat(arr).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("28. equals 비교")
        void test28_equals() {
            // TODO: equals() 구현 후 테스트
            // Queue<Integer> q1 = new ArrayQueueImpl<>();
            // Queue<Integer> q2 = new ArrayQueueImpl<>();
            // q1.offer(1); q1.offer(2);
            // q2.offer(1); q2.offer(2);
            // assertThat(q1).isEqualTo(q2);
        }

        @Test
        @DisplayName("29. hashCode 일관성")
        void test29_hashCode() {
            // TODO: hashCode() 구현 후 테스트
            // Queue<Integer> q1 = new ArrayQueueImpl<>();
            // Queue<Integer> q2 = new ArrayQueueImpl<>();
            // q1.offer(1);
            // q2.offer(1);
            // assertThat(q1.hashCode()).isEqualTo(q2.hashCode());
        }

        @Test
        @DisplayName("30. toString 형식")
        void test30_toString() {
            // TODO: toString() 구현 후 테스트
            // Queue<Integer> queue = new ArrayQueueImpl<>();
            // queue.offer(1);
            // queue.offer(2);
            // assertThat(queue.toString()).contains("1", "2");
        }
    }
}
