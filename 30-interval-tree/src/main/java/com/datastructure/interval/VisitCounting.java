package com.datastructure.interval;

/**
 * 마지막 질의가 들여다본 노드 수를 세는 계약. 한계 측정용이다.
 *
 * 이 문제의 주장은 "maxEnd 한 필드가 가지치기를 만든다" 하나다.
 * 시간을 재면 기계와 JIT 에 따라 흔들려서 주장이 흐려진다. 그래서 걸음 수를 센다.
 *
 * 세는 단위는 "들여다본 것" 하나다.
 *   - NaiveIntervalStore 는 리스트 원소 하나를 볼 때마다 하나다. findAll 은 언제나 정확히 n 이다.
 *   - IntervalTree 는 재귀가 들어간 노드 하나다. 가지치기로 안 내려간 부분트리는 통째로 안 센다.
 *
 * 누적이 아니라 마지막 질의 하나의 값이다. findAny, findAll, anyOverlaps 가 시작할 때 0 으로 되돌린다.
 * insert, remove, toList 는 이 값을 건드리지 않는다.
 *
 * 이 인터페이스에는 TODO 가 없다.
 */
public interface VisitCounting {

    /** 마지막 질의가 들여다본 노드 수. 질의 전이면 0. */
    long visitedNodes();
}
