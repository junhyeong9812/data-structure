package com.datastructure.spatial;

/**
 * 방문한 노드 수를 세는 계약. 한계 측정용이다.
 *
 * 이 문제의 주장은 "가지치기가 일을 줄인다" 하나다.
 * 시간을 재면 기계와 JIT 에 따라 흔들려서 주장이 흐려진다. 그래서 걸음 수를 센다.
 *
 * 세는 단위는 "재귀가 들어간 노드" 하나다.
 *   - NaiveSpatialIndex 는 질의 한 번에 정확히 n 이다. 전부 훑기 때문이다.
 *   - KdTree 는 가지치기로 안 내려간 서브트리를 통째로 안 센다.
 *   - QuadTree 는 들어가서 곧장 되돌아 나온 칸도 하나로 센다. 들어가긴 했기 때문이다.
 *     그래서 쿼드트리 쪽 수가 조금 후하게 나온다. 그런데도 전수 조사와 자릿수가 다르다.
 *
 * 그래서 두 수의 비가 곧 가지치기의 효과다. 이 인터페이스에는 TODO 가 없다.
 */
public interface VisitCounting {

    /** resetVisits 이후 들여다본 노드 수. */
    long visits();

    /** 0 으로 되돌린다. 측정 직전에 부른다. */
    void resetVisits();
}
