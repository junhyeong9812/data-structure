package com.datastructure.heap;

import java.util.NoSuchElementException;

/**
 * [구현] 스트림의 중앙값.
 *
 * 중앙값을 알려면 전체 순서가 필요 없다. 경계에 있는 값 하나(또는 둘)만 알면 된다.
 * 그래서 작은 절반은 최대 힙, 큰 절반은 최소 힙으로 두고 두 꼭대기만 본다.
 */
public class MedianFinder {

    private final Heap<Integer> lower;   // 최대 힙: 작은 절반 중 가장 큰 것이 꼭대기
    private final Heap<Integer> upper;   // 최소 힙: 큰 절반 중 가장 작은 것이 꼭대기
    private int count;

    public MedianFinder(Heap<Integer> lower, Heap<Integer> upper) {
        if (!lower.isEmpty() || !upper.isEmpty()) {
            throw new IllegalArgumentException("비어 있는 힙 두 개를 넘겨야 한다");
        }
        this.lower = lower;
        this.upper = upper;
        this.count = 0;
    }

    /**
     * 항상 lower 에 먼저 넣고 그 꼭대기를 upper 로 넘긴다.
     *
     * 이렇게 하면 "새 값이 어느 쪽에 속하나"를 따로 판단할 필요가 없다.
     * lower 에 넣으면 힙이 알아서 가장 큰 것을 꼭대기로 올리고, 그것을 넘기면 순서 조건이 저절로 지켜진다.
     *
     * 그 다음 크기 균형만 맞춘다. lower 가 upper 보다 하나 더 많거나 같게 유지한다.
     */
    public void add(int value) {
        lower.insert(value);
        upper.insert(lower.poll());          // 순서 조건을 여기서 보장한다

        if (upper.size() > lower.size()) {   // 균형 조건
            lower.insert(upper.poll());
        }
        count++;
    }

    public double median() {
        if (count == 0) {
            throw new NoSuchElementException("아직 값이 없다");
        }
        if (lower.size() > upper.size()) {
            return lower.peek();             // 홀수면 더 많은 쪽의 꼭대기
        }
        // 짝수면 두 꼭대기의 평균. 정수 나눗셈으로 하면 1.5 가 1 이 된다.
        return (lower.peek() + upper.peek()) / 2.0;
    }

    public int count() {
        return count;
    }
}
