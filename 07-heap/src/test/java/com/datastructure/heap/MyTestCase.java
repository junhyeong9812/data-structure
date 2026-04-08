package com.datastructure.heap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MyTestCase {

    // ── 기본 연산 ──
    @Nested @DisplayName("insert 메서드 테스트")
    class InsertTest {}

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

    // ── 추가 연산 ──
    @Nested @DisplayName("heapify 메서드 테스트")
    class HeapifyTest {}

    @Nested @DisplayName("increaseKey 메서드 테스트")
    class IncreaseKeyTest {}

    @Nested @DisplayName("decreaseKey 메서드 테스트")
    class DecreaseKeyTest {}

    @Nested @DisplayName("delete 메서드 테스트")
    class DeleteTest {}

    @Nested @DisplayName("merge 메서드 테스트")
    class MergeTest {}

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
