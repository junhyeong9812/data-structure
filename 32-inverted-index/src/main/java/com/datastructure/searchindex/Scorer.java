package com.datastructure.searchindex;

/**
 * 문서 하나와 질의어 하나가 만났을 때 몇 점인지를 정하는 계약.
 *
 * 검색 결과의 점수는 질의어마다 이 함수를 부르고 그 값을 더한 것이다.
 * 더하는 쪽은 엔진이 하고, 여기서는 항 하나의 몫만 정한다.
 *
 * 인자가 셋인 이유가 이 박스의 절반이다.
 *   termFrequency  이 문서에서 이 항이 몇 번 나왔나. 많이 나오면 그 문서 이야기일 것이다
 *   documentFrequency  이 항을 가진 문서가 몇 개인가. 많으면 흔한 말이라 변별력이 없다
 *   documentCount  색인에 든 전체 문서 수
 *
 * 앞의 하나만 보면 "의", "그리고" 가 이긴다. 뒤의 둘이 그것을 고친다.
 *
 * 이 인터페이스에는 TODO 가 없다.
 */
@FunctionalInterface
public interface Scorer {

    /**
     * 항 하나의 점수.
     *
     * 셋 중 하나라도 0 이하면 0 을 준다. 없는 항에 점수를 주지 않기 위한 하한이다.
     */
    double score(int termFrequency, int documentFrequency, int documentCount);
}
