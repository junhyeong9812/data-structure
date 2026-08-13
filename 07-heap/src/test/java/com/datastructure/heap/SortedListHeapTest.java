package com.datastructure.heap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SortedListHeap 이 계약을 지키는지 + **이 구현의 한계**.
 *
 * insertCostGrowsQuadratically 는 버그를 잡는 테스트가 아니다. 올바른 구현에서도 통과한다.
 * "이 방법은 넣기가 비싸다"를 숫자로 못 박아 BinaryHeap 을 만들 이유를 만든다.
 */
class SortedListHeapTest extends HeapContractTest {

    @Override
    protected <E> Heap<E> create(Comparator<? super E> comparator) {
        return new SortedListHeap<>(comparator);
    }

    @Test
    @DisplayName("한계: 넣기 비용이 원소 수에 비례한다")
    void insertCostGrowsQuadratically() {
        SortedListHeap<Integer> heap = new SortedListHeap<>(Comparator.naturalOrder());
        final int n = 1_000;

        // 오름차순으로 넣으면 매번 배열 맨 앞에 들어가므로 뒤를 전부 밀어야 한다.
        // (이 구현은 "뒤로 갈수록 앞선다"로 유지하므로 작은 값이 앞쪽이다.)
        for (int i = 1; i <= n; i++) heap.insert(i);

        long expected = (long) n * (n - 1) / 2;
        assertTrue(heap.moves >= expected * 9 / 10,
            "원소를 " + heap.moves + "번 밀었다. n 이 " + n + " 인데 약 " + expected + "번이다.\n"
                + "넣기 한 번이 O(n) 이라 n 번 넣으면 O(n^2) 이 된다. BinaryHeap 이 이걸 고친다.");
    }

    @Test
    @DisplayName("역순으로 넣으면 밀 일이 없다 (최선의 경우)")
    void reverseOrderCostsNothing() {
        // 같은 구현인데 입력 순서에 따라 비용이 극단적으로 갈린다. 그것도 이 방법의 성질이다.
        SortedListHeap<Integer> heap = new SortedListHeap<>(Comparator.naturalOrder());
        for (int i = 1_000; i >= 1; i--) heap.insert(i);

        assertEquals(0, heap.moves, "이미 제자리라 밀 필요가 없다");
        assertEquals(1, heap.peek());
    }

    @Test
    @DisplayName("꺼내기는 시프트가 없다")
    void pollDoesNotShift() {
        SortedListHeap<Integer> heap = new SortedListHeap<>(Comparator.naturalOrder());
        for (int i = 1_000; i >= 1; i--) heap.insert(i);
        long before = heap.moves;

        for (int i = 0; i < 500; i++) heap.poll();

        assertEquals(before, heap.moves, "가장 앞선 것을 배열 끝에 두면 꺼낼 때 밀 일이 없다");
    }

    @Test
    @DisplayName("꺼낸 자리의 참조가 남지 않는다")
    void clearsPolledSlot() {
        SortedListHeap<String> heap = new SortedListHeap<>(Comparator.naturalOrder());
        heap.insert("a");
        heap.insert("b");
        heap.poll();

        assertNull(heap.elements[heap.size], "꺼낸 자리를 비우지 않으면 누수다");
    }
}
