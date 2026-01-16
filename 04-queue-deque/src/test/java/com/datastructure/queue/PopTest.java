package com.datastructure.queue;

import com.datastructure.queue.pop.ArrayQueue;
import com.datastructure.queue.pop.CircularQueue;
import com.datastructure.queue.pop.ArrayDeque;
import com.datastructure.queue.pop.QueueProblems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("큐/덱 - POP 구현 테스트")
class PopTest {

    @Nested
    @DisplayName("배열 기반 큐")
    class ArrayQueueTest {
        
        private ArrayQueue queue;

        @BeforeEach
        void setUp() {
            queue = new ArrayQueue();
        }

        @Test
        @DisplayName("01. 빈 큐 생성 시 isEmpty는 true다")
        void test01_emptyQueueIsEmpty() {
            // TODO: isEmpty() 구현 후 테스트
            // assertThat(queue.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("02. enqueue 후 isEmpty는 false다")
        void test02_enqueueMakesNonEmpty() {
            // TODO: enqueue() 구현 후 테스트
            // queue.enqueue(1);
            // assertThat(queue.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("03. FIFO 순서로 dequeue된다")
        void test03_fifoOrder() {
            // TODO: FIFO 순서 테스트
            // queue.enqueue(1);
            // queue.enqueue(2);
            // queue.enqueue(3);
            // assertThat(queue.dequeue()).isEqualTo(1);
            // assertThat(queue.dequeue()).isEqualTo(2);
            // assertThat(queue.dequeue()).isEqualTo(3);
        }

        @Test
        @DisplayName("04. peek은 요소를 제거하지 않는다")
        void test04_peekDoesNotRemove() {
            // TODO: peek() 구현 후 테스트
            // queue.enqueue(1);
            // assertThat(queue.peek()).isEqualTo(1);
            // assertThat(queue.peek()).isEqualTo(1);
            // assertThat(queue.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("05. 빈 큐에서 dequeue 시 예외가 발생한다")
        void test05_dequeueFromEmpty() {
            // TODO: 예외 처리 구현 후 테스트
            // assertThatThrownBy(() -> queue.dequeue())
            //     .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("원형 큐")
    class CircularQueueTest {
        
        private CircularQueue queue;

        @BeforeEach
        void setUp() {
            queue = new CircularQueue(5);
        }

        @Test
        @DisplayName("06. 원형 큐가 가득 차면 enqueue가 실패한다")
        void test06_fullQueueEnqueueFails() {
            // TODO: 원형 큐 구현 후 테스트
            // for (int i = 0; i < 5; i++) {
            //     assertThat(queue.enqueue(i)).isTrue();
            // }
            // assertThat(queue.enqueue(5)).isFalse();
            // assertThat(queue.isFull()).isTrue();
        }

        @Test
        @DisplayName("07. dequeue 후 공간이 재사용된다")
        void test07_spaceReused() {
            // TODO: 원형 동작 테스트
            // queue.enqueue(1);
            // queue.enqueue(2);
            // queue.enqueue(3);
            // queue.dequeue();  // 1 제거
            // queue.dequeue();  // 2 제거
            // queue.enqueue(4);
            // queue.enqueue(5);
            // queue.enqueue(6);  // 원형으로 앞 공간 재사용
            // assertThat(queue.dequeue()).isEqualTo(3);
        }

        @Test
        @DisplayName("08. front와 rear가 올바르게 순환한다")
        void test08_circularIndexes() {
            // TODO: 인덱스 순환 테스트
            // for (int i = 0; i < 5; i++) queue.enqueue(i);
            // for (int i = 0; i < 5; i++) queue.dequeue();
            // for (int i = 10; i < 15; i++) queue.enqueue(i);
            // assertThat(queue.peek()).isEqualTo(10);
        }

        @Test
        @DisplayName("09. 빈 원형 큐에서 dequeue 시 예외가 발생한다")
        void test09_emptyCircularDequeue() {
            // TODO: 예외 처리 테스트
            // assertThatThrownBy(() -> queue.dequeue())
            //     .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("10. size가 정확히 반환된다")
        void test10_sizeCorrect() {
            // TODO: size() 테스트
            // queue.enqueue(1);
            // queue.enqueue(2);
            // assertThat(queue.size()).isEqualTo(2);
            // queue.dequeue();
            // assertThat(queue.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("배열 기반 덱")
    class ArrayDequeTest {
        
        private ArrayDeque deque;

        @BeforeEach
        void setUp() {
            deque = new ArrayDeque(10);
        }

        @Test
        @DisplayName("11. addFirst로 앞에 추가할 수 있다")
        void test11_addFirst() {
            // TODO: addFirst() 구현 후 테스트
            // deque.addFirst(1);
            // deque.addFirst(2);
            // assertThat(deque.peekFirst()).isEqualTo(2);
        }

        @Test
        @DisplayName("12. addLast로 뒤에 추가할 수 있다")
        void test12_addLast() {
            // TODO: addLast() 구현 후 테스트
            // deque.addLast(1);
            // deque.addLast(2);
            // assertThat(deque.peekLast()).isEqualTo(2);
        }

        @Test
        @DisplayName("13. removeFirst로 앞에서 제거할 수 있다")
        void test13_removeFirst() {
            // TODO: removeFirst() 구현 후 테스트
            // deque.addLast(1);
            // deque.addLast(2);
            // assertThat(deque.removeFirst()).isEqualTo(1);
        }

        @Test
        @DisplayName("14. removeLast로 뒤에서 제거할 수 있다")
        void test14_removeLast() {
            // TODO: removeLast() 구현 후 테스트
            // deque.addLast(1);
            // deque.addLast(2);
            // assertThat(deque.removeLast()).isEqualTo(2);
        }

        @Test
        @DisplayName("15. 덱을 스택처럼 사용할 수 있다")
        void test15_useAsStack() {
            // TODO: push/pop 테스트
            // deque.addFirst(1);
            // deque.addFirst(2);
            // deque.addFirst(3);
            // assertThat(deque.removeFirst()).isEqualTo(3);  // LIFO
        }

        @Test
        @DisplayName("16. 덱을 큐처럼 사용할 수 있다")
        void test16_useAsQueue() {
            // TODO: offer/poll 테스트
            // deque.addLast(1);
            // deque.addLast(2);
            // deque.addLast(3);
            // assertThat(deque.removeFirst()).isEqualTo(1);  // FIFO
        }

        @Test
        @DisplayName("17. 빈 덱에서 removeFirst 시 예외가 발생한다")
        void test17_removeFirstFromEmpty() {
            // TODO: 예외 처리 테스트
            // assertThatThrownBy(() -> deque.removeFirst())
            //     .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("18. 빈 덱에서 removeLast 시 예외가 발생한다")
        void test18_removeLastFromEmpty() {
            // TODO: 예외 처리 테스트
            // assertThatThrownBy(() -> deque.removeLast())
            //     .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("큐/덱 응용 문제")
    class QueueProblemsTest {

        @Test
        @DisplayName("19. 슬라이딩 윈도우 최댓값 - 기본")
        void test19_slidingWindowBasic() {
            // TODO: maxSlidingWindow() 구현 후 테스트
            // int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
            // int[] result = QueueProblems.maxSlidingWindow(nums, 3);
            // assertThat(result).containsExactly(3, 3, 5, 5, 6, 7);
        }

        @Test
        @DisplayName("20. 슬라이딩 윈도우 최댓값 - 단조 감소")
        void test20_slidingWindowDecreasing() {
            // TODO: 단조 감소 배열 테스트
            // int[] nums = {9, 8, 7, 6, 5};
            // int[] result = QueueProblems.maxSlidingWindow(nums, 2);
            // assertThat(result).containsExactly(9, 8, 7, 6);
        }

        @Test
        @DisplayName("21. 슬라이딩 윈도우 최댓값 - 단조 증가")
        void test21_slidingWindowIncreasing() {
            // TODO: 단조 증가 배열 테스트
            // int[] nums = {1, 2, 3, 4, 5};
            // int[] result = QueueProblems.maxSlidingWindow(nums, 2);
            // assertThat(result).containsExactly(2, 3, 4, 5);
        }

        @Test
        @DisplayName("22. 슬라이딩 윈도우 최댓값 - 윈도우 크기 1")
        void test22_slidingWindowSizeOne() {
            // TODO: k=1 테스트
            // int[] nums = {1, 3, 2};
            // int[] result = QueueProblems.maxSlidingWindow(nums, 1);
            // assertThat(result).containsExactly(1, 3, 2);
        }

        @Test
        @DisplayName("23. 슬라이딩 윈도우 최댓값 - 전체 윈도우")
        void test23_slidingWindowFullSize() {
            // TODO: k=n 테스트
            // int[] nums = {1, 3, 2};
            // int[] result = QueueProblems.maxSlidingWindow(nums, 3);
            // assertThat(result).containsExactly(3);
        }

        @Test
        @DisplayName("24. 최근 요청 카운터 - 기본")
        void test24_recentCounterBasic() {
            // TODO: RecentCounter 구현 후 테스트
            // RecentCounter counter = new RecentCounter();
            // assertThat(counter.ping(1)).isEqualTo(1);
            // assertThat(counter.ping(100)).isEqualTo(2);
            // assertThat(counter.ping(3001)).isEqualTo(3);
            // assertThat(counter.ping(3002)).isEqualTo(3);  // 1은 제외됨
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("25. clear로 모든 요소 삭제")
        void test25_clear() {
            // TODO: clear() 구현 후 테스트
            // ArrayQueue queue = new ArrayQueue();
            // queue.enqueue(1);
            // queue.enqueue(2);
            // queue.clear();
            // assertThat(queue.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("26. toArray로 배열 변환")
        void test26_toArray() {
            // TODO: toArray() 구현 후 테스트
            // ArrayQueue queue = new ArrayQueue();
            // queue.enqueue(1);
            // queue.enqueue(2);
            // queue.enqueue(3);
            // assertThat(queue.toArray()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("27. 원형 덱 구현")
        void test27_circularDeque() {
            // TODO: CircularDeque 구현 후 테스트
            // CircularDeque deque = new CircularDeque(3);
            // assertThat(deque.insertLast(1)).isTrue();
            // assertThat(deque.insertLast(2)).isTrue();
            // assertThat(deque.insertFront(3)).isTrue();
            // assertThat(deque.insertFront(4)).isFalse();  // Full
        }

        @Test
        @DisplayName("28. poll은 빈 큐에서 null을 반환한다")
        void test28_pollReturnsNull() {
            // TODO: poll() 구현 후 테스트
            // ArrayQueue queue = new ArrayQueue();
            // assertThat(queue.poll()).isNull();
        }

        @Test
        @DisplayName("29. offer는 항상 true를 반환한다 (동적 크기)")
        void test29_offerAlwaysSucceeds() {
            // TODO: offer() 구현 후 테스트
            // ArrayQueue queue = new ArrayQueue();
            // for (int i = 0; i < 1000; i++) {
            //     assertThat(queue.offer(i)).isTrue();
            // }
        }

        @Test
        @DisplayName("30. toString으로 큐 내용 확인")
        void test30_toString() {
            // TODO: toString() 구현 후 테스트
            // ArrayQueue queue = new ArrayQueue();
            // queue.enqueue(1);
            // queue.enqueue(2);
            // queue.enqueue(3);
            // assertThat(queue.toString()).isEqualTo("[1, 2, 3]");
        }
    }
}
