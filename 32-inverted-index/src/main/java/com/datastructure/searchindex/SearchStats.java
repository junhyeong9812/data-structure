package com.datastructure.searchindex;

/**
 * 마지막 질의가 얼마나 일했는지를 세는 계약. 한계 측정용이다.
 *
 * 이 박스의 주장은 "색인이 있으면 관련 문서만 본다" 하나다.
 * 시간을 재면 기계와 JIT 에 따라 흔들려서 주장이 흐려진다. 그래서 걸음 수를 센다.
 *
 * 세는 단위를 못 박아 둔다.
 *   visitedDocs   포스팅 리스트에서 원소를 하나 읽으면 하나.
 *                 전수 조사는 문서 하나를 열어보는 것이 하나이므로 질의마다 정확히 docCount 다.
 *   comparisons   교집합 병합에서 두 문서 번호를 견준 횟수. 전수 조사는 병합이 없으므로 늘 0 이다.
 *
 * 누적이 아니라 마지막 질의 하나의 값이다. search 와 searchPhrase 가 시작할 때 0 으로 되돌린다.
 * index, docCount, termCount 는 이 값을 건드리지 않는다.
 *
 * 이 인터페이스에는 TODO 가 없다.
 */
public interface SearchStats {

    /** 마지막 질의가 들여다본 문서(포스팅 원소) 수. 질의 전이면 0. */
    long visitedDocs();

    /** 마지막 질의의 병합에서 문서 번호를 견준 횟수. 질의 전이면 0. */
    long comparisons();
}
