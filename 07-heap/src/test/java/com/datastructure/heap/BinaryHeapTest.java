package com.datastructure.heap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BinaryHeap 이 계약을 지키는지 + **힙 성질을 배열에서 직접 검사**.
 *
 * 계약 테스트는 "꺼내는 순서"만 본다. 그건 안에서 정렬해두기만 해도 통과한다.
 * 여기서는 배열이 실제로 힙 모양인지 본다. 그게 O(log n) 의 근거이기 때문이다.
 */
class BinaryHeapTest extends HeapContractTest {

    @Override
    protected <E> Heap<E> create(Comparator<? super E> comparator) {
        return new BinaryHeap<>(comparator);
    }

    /** 모든 부모가 자식보다 앞서는지. 이것이 힙의 불변식이다. */
    private static <E> void assertHeapProperty(BinaryHeap<E> heap) {
        for (int i = 1; i < heap.size; i++) {
            int parent = BinaryHeap.parentOf(i);
            assertTrue(heap.compare(parent, i) <= 0,
                "부모(" + parent + ")=" + heap.at(parent) + " 가 자식(" + i + ")=" + heap.at(i)
                    + " 보다 뒤처진다. 힙 성질이 깨졌다");
        }
    }

    private static BinaryHeap<Integer> minHeapOf(int... values) {
        BinaryHeap<Integer> heap = new BinaryHeap<>(Comparator.naturalOrder());
        for (int v : values) heap.insert(v);
        return heap;
    }

    @Test
    @DisplayName("인덱스 계산이 트리 구조와 맞는다")
    void indexArithmetic() {
        assertEquals(0, BinaryHeap.parentOf(1));
        assertEquals(0, BinaryHeap.parentOf(2));
        assertEquals(1, BinaryHeap.parentOf(3));
        assertEquals(1, BinaryHeap.parentOf(4));
        assertEquals(1, BinaryHeap.leftOf(0));
        assertEquals(2, BinaryHeap.rightOf(0));
        assertEquals(3, BinaryHeap.leftOf(1));
    }

    @Test
    @DisplayName("넣은 뒤 힙 성질이 지켜진다")
    void keepsHeapPropertyOnInsert() {
        BinaryHeap<Integer> heap = minHeapOf(5, 3, 8, 1, 9, 2);
        assertHeapProperty(heap);
        assertEquals(1, heap.at(0), "맨 위가 가장 앞선 것이다");
    }

    @Test
    @DisplayName("꺼낸 뒤에도 힙 성질이 지켜진다")
    void keepsHeapPropertyOnPoll() {
        BinaryHeap<Integer> heap = minHeapOf(5, 3, 8, 1, 9, 2, 7, 4, 6);
        for (int i = 0; i < 5; i++) {
            heap.poll();
            assertHeapProperty(heap);
        }
    }

    @Test
    @DisplayName("전체가 정렬되지는 않는다 - 부분 순서만 지킨다")
    void isNotFullySorted() {
        // 이게 힙과 정렬된 배열의 차이다. 덜 지키니까 넣기가 싸다.
        BinaryHeap<Integer> heap = minHeapOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        assertHeapProperty(heap);

        boolean fullySorted = true;
        for (int i = 1; i < heap.size; i++) {
            if (heap.compare(i - 1, i) > 0) fullySorted = false;
        }
        // 오름차순 입력이면 우연히 정렬되어 보일 수 있으므로, 역순 입력으로 확인한다
        BinaryHeap<Integer> reversed = new BinaryHeap<>(Comparator.naturalOrder());
        for (int i = 10; i >= 1; i--) reversed.insert(i);
        assertHeapProperty(reversed);

        boolean reversedSorted = true;
        for (int i = 1; i < reversed.size; i++) {
            if (reversed.compare(i - 1, i) > 0) reversedSorted = false;
        }
        assertFalse(fullySorted && reversedSorted,
            "힙은 전체 정렬을 보장하지 않는다. 보장하면 그건 힙보다 비싼 구조다");
    }

    @Test
    @DisplayName("꺼낸 자리의 참조가 남지 않는다")
    void clearsPolledSlot() {
        BinaryHeap<String> heap = new BinaryHeap<>(Comparator.naturalOrder());
        heap.insert("a");
        heap.insert("b");
        heap.poll();

        assertNull(heap.elements[heap.size], "꺼낸 자리를 비우지 않으면 누수다");
    }

    @Test
    @DisplayName("용량이 부족하면 늘어난다")
    void growsWhenFull() {
        BinaryHeap<Integer> heap = new BinaryHeap<>(Comparator.naturalOrder());
        int before = heap.capacity();
        for (int i = 0; i < before + 1; i++) heap.insert(i);
        assertTrue(heap.capacity() > before);
        assertEquals(before + 1, heap.size());
    }

    @Test
    @DisplayName("20만 개를 넣고 빼기를 5초 안에 (O(n^2) 은 통과 못 한다)")
    void mustBeLogarithmic() {
        // SortedListHeap 은 넣기가 O(n) 이라 여기서 막힌다. 그래서 이 테스트는 여기에만 있다.
        final int n = 200_000;
        BinaryHeap<Integer> heap = new BinaryHeap<>(Comparator.naturalOrder());

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            for (int i = 0; i < n; i++) heap.insert((i * 7919) % n);
            assertEquals(n, heap.size());
            for (int i = 0; i < n; i++) {
                assertEquals(i, heap.poll(), "인덱스 " + i);
            }
        }, "넣기가 O(n) 이면 전체가 O(n^2) 이라 여기서 막힌다.");
    }
}
