package com.datastructure.searchindex;

/**
 * 단순 빈도. 이 문서에서 몇 번 나왔는지가 곧 점수다.
 *
 * 기준선이다. 이 클래스가 세 줄이라는 것이 요점이고, 그래서 TODO 를 두지 않았다.
 * TfIdfScorer 의 TODO 를 채우기 전에 이것을 먼저 읽어라. 인자 셋 중 둘을 안 쓴다.
 *
 * 안 쓰는 그 둘이 없으면 무슨 일이 생기는지가 ScoringTest 의 주제다.
 * 흔한 말을 많이 가진 문서가 이긴다. 검색 결과 첫 줄에 "그리고" 가 100번 나온 문서가 온다.
 */
public class TermFrequencyScorer implements Scorer {

    @Override
    public double score(int termFrequency, int documentFrequency, int documentCount) {
        if (termFrequency <= 0) {
            return 0.0;
        }
        return termFrequency;
    }

    @Override
    public String toString() {
        return "단순 빈도";
    }
}
