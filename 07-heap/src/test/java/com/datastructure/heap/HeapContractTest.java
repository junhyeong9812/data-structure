package com.datastructure.heap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Heap 계약 테스트. 두 구현이 물려받는다.
 *
 * 힙은 **부분 순서**만 지키므로 "전체가 정렬되어 있는가"를 물으면 안 된다.
 * 물어야 하는 것은 "꺼내는 순서가 정렬되어 있는가"다. 그 차이가 이 자료구조의 정의다.
 */
abstract class HeapContractTest {

    protected abstract <E> Heap<E> create(Comparator<? super E> comparator);

    protected Heap<Integer> minHeap() {
        return create(Comparator.naturalOrder());
    }

    protected Heap<Integer> maxHeap() {
        return create(Comparator.<Integer>naturalOrder().reversed());
    }

    /** 전부 꺼내서 나온 순서를 돌려준다. 힙의 계약은 이 순서에 있다. */
    protected static java.util.List<Integer> drain(Heap<Integer> heap) {
        java.util.List<Integer> out = new ArrayList<>();
        while (!heap.isEmpty()) out.add(heap.poll());
        return out;
    }

    @Nested
    @DisplayName("꺼내는 순서")
    class PollOrder {

        @Test
        @DisplayName("최소 힙은 작은 것부터 나온다")
        void minHeapPollsAscending() {
            Heap<Integer> heap = minHeap();
            for (int v : new int[]{5, 1, 9, 3, 7}) heap.insert(v);
            assertEquals(java.util.List.of(1, 3, 5, 7, 9), drain(heap));
        }

        @Test
        @DisplayName("최대 힙은 큰 것부터 나온다")
        void maxHeapPollsDescending() {
            Heap<Integer> heap = maxHeap();
            for (int v : new int[]{5, 1, 9, 3, 7}) heap.insert(v);
            assertEquals(java.util.List.of(9, 7, 5, 3, 1), drain(heap));
        }

        @Test
        @DisplayName("peek 는 꺼내지 않는다")
        void peekDoesNotRemove() {
            Heap<Integer> heap = minHeap();
            heap.insert(5);
            heap.insert(1);

            assertEquals(1, heap.peek());
            assertEquals(1, heap.peek());
            assertEquals(2, heap.size(), "peek 이 크기를 바꾸면 안 된다");
        }

        @Test
        @DisplayName("같은 값이 여러 개여도 개수가 맞는다")
        void handlesDuplicates() {
            Heap<Integer> heap = minHeap();
            for (int v : new int[]{3, 1, 3, 1, 2}) heap.insert(v);
            assertEquals(java.util.List.of(1, 1, 2, 3, 3), drain(heap));
        }

        @Test
        @DisplayName("넣기와 빼기를 섞어도 순서가 맞는다")
        void interleavedOperations() {
            Heap<Integer> heap = minHeap();
            heap.insert(5);
            heap.insert(3);
            assertEquals(3, heap.poll());
            heap.insert(1);
            heap.insert(4);
            assertEquals(1, heap.poll());
            assertEquals(4, heap.poll());
            assertEquals(5, heap.poll());
            assertTrue(heap.isEmpty());
        }

        @Test
        @DisplayName("이미 정렬된 입력도, 역순 입력도 정확하다")
        void handlesSortedAndReversedInput() {
            Heap<Integer> ascending = minHeap();
            for (int i = 1; i <= 10; i++) ascending.insert(i);
            assertEquals(java.util.List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), drain(ascending));

            Heap<Integer> descending = minHeap();
            for (int i = 10; i >= 1; i--) descending.insert(i);
            assertEquals(java.util.List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), drain(descending));
        }
    }

    @Nested
    @DisplayName("빈 힙과 초기화")
    class EmptyAndClear {

        @Test
        void emptyThrows() {
            Heap<Integer> heap = minHeap();
            assertThrows(NoSuchElementException.class, heap::peek);
            assertThrows(NoSuchElementException.class, heap::poll);
        }

        @Test
        @DisplayName("전부 꺼낸 뒤에도 예외다")
        void throwsAfterDrain() {
            Heap<Integer> heap = minHeap();
            heap.insert(1);
            heap.poll();
            assertThrows(NoSuchElementException.class, heap::poll);
            assertEquals(0, heap.size(), "실패한 poll 이 크기를 음수로 만들면 안 된다");
        }

        @Test
        void rejectsNull() {
            Heap<Integer> heap = minHeap();
            assertThrows(IllegalArgumentException.class, () -> heap.insert(null));
        }

        @Test
        void clearsAndReusable() {
            Heap<Integer> heap = minHeap();
            for (int v : new int[]{3, 1, 2}) heap.insert(v);

            heap.clear();
            assertEquals(0, heap.size());
            assertThrows(NoSuchElementException.class, heap::poll);

            heap.insert(9);
            assertEquals(9, heap.peek());
            assertEquals(1, heap.size());
        }
    }

    @Nested
    @DisplayName("대량 처리")
    class Bulk {

        @Test
        @DisplayName("흩어진 입력 2000개가 정렬되어 나온다")
        void survivesManyOperations() {
            Heap<Integer> heap = minHeap();
            final int n = 2_000;
            for (int i = 0; i < n; i++) heap.insert((i * 7919) % n);

            java.util.List<Integer> out = drain(heap);
            assertEquals(n, out.size());
            for (int i = 0; i < n; i++) {
                assertEquals(i, out.get(i), "인덱스 " + i);
            }
        }

        @Test
        @DisplayName("사용자 비교자를 그대로 따른다")
        void respectsCustomComparator() {
            // 절댓값이 작은 것부터. 힙의 방향은 비교자 하나가 정한다.
            Heap<Integer> heap = create(Comparator.comparingInt(Math::abs));
            for (int v : new int[]{-5, 2, -1, 4}) heap.insert(v);
            assertEquals(java.util.List.of(-1, 2, 4, -5), drain(heap));
        }
    }
}
