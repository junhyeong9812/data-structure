package com.datastructure.heap;

import java.util.NoSuchElementException;

/**
 * 스트림의 중앙값을 계속 알려주는 장치. 이 문제집에서 힙의 진가가 가장 잘 드러나는 문제다.
 *
 * 값이 하나씩 흘러 들어오고, 그때마다 지금까지의 중앙값을 답해야 한다.
 * 개수가 짝수면 가운데 두 값의 평균이다.
 *
 *   1 -> 1.0
 *   1, 2 -> 1.5
 *   1, 2, 3 -> 2.0
 *
 * 순진한 방법
 *   매번 정렬하면 한 번에 O(n log n), 전체 O(n^2 log n) 이다.
 *   정렬을 유지하며 끼워 넣어도 삽입이 O(n) 이라 전체 O(n^2) 이다.
 *
 * 핵심 발상
 *   중앙값을 알려면 전체 순서를 알 필요가 없다.
 *   작은 절반과 큰 절반으로 나눠 두고, 각 절반의 "경계에 있는 값"만 빨리 알면 된다.
 *   작은 절반에서는 가장 큰 값, 큰 절반에서는 가장 작은 값이 필요하다.
 *
 *   그게 정확히 최대 힙 하나와 최소 힙 하나다.
 *
 * 유지해야 할 것 두 가지
 *   1. 크기 균형 - 두 힙의 크기 차이가 1을 넘지 않는다
 *   2. 순서 - 작은 절반의 어떤 값도 큰 절반의 어떤 값보다 크지 않다
 *
 * 이 둘을 지키면 중앙값은 두 힙의 꼭대기만 보면 나온다.
 */
public class MedianFinder {

    /** 작은 절반. 여기서 필요한 것은 "가장 큰 값"이다. */
    private final Heap<Integer> lower;
    /** 큰 절반. 여기서 필요한 것은 "가장 작은 값"이다. */
    private final Heap<Integer> upper;
    private int count;

    /**
     * @param lower 작은 절반을 담을 빈 힙
     * @param upper 큰 절반을 담을 빈 힙
     *
     * 어느 쪽이 최대 힙이고 어느 쪽이 최소 힙이어야 하는지는 호출자가 정한다.
     * 테스트가 그것까지 검사한다.
     */
    public MedianFinder(Heap<Integer> lower, Heap<Integer> upper) {
        if (!lower.isEmpty() || !upper.isEmpty()) {
            throw new IllegalArgumentException("비어 있는 힙 두 개를 넘겨야 한다");
        }
        this.lower = lower;
        this.upper = upper;
        this.count = 0;
    }

    /**
     * 값을 하나 받는다.
     *
     * 생각할 것
     *   - 새 값이 어느 쪽에 속하는지 어떻게 판단하는가? 각 힙의 꼭대기와 비교하면 된다.
     *   - 넣고 나면 크기 균형이 깨질 수 있다. 어떻게 되돌리는가?
     *   - 순서 조건이 깨지지 않게 하려면 넣는 순서를 어떻게 잡아야 하는가?
     *     (한쪽에 넣었다가 꼭대기를 옮기는 방법이 흔하다.)
     *
     * TODO(11): 구현하라.
     */
    public void add(int value) {
        throw new UnsupportedOperationException("TODO(11): add");
    }

    /**
     * 지금까지의 중앙값. 아직 하나도 안 들어왔으면 NoSuchElementException.
     *
     * 생각할 것
     *   - 개수가 홀수면 어느 힙의 꼭대기인가? 짝수면?
     *   - 두 정수의 평균을 낼 때 정수 나눗셈으로 하면 어떻게 되는가?
     *
     * TODO(12): 구현하라.
     */
    public double median() {
        throw new UnsupportedOperationException("TODO(12): median");
    }

    public int count() {
        return count;
    }
}
