package com.datastructure.searchindex;

import java.util.Locale;

/**
 * 검색 결과 한 줄. 문서 번호와 점수.
 *
 * 정렬 규칙이 계약이다. 점수 내림차순, 동점이면 문서 번호 오름차순.
 * 동점 규칙이 없으면 같은 색인에 같은 질의를 넣어도 순서가 달라질 수 있고,
 * 그러면 두 구현의 답을 비교하는 이 박스의 주력 검증이 성립하지 않는다.
 *
 * 점수는 double 이다. 비교는 Double.compare 로 한다.
 * 빼서 int 로 자르면 0.4 점 차이가 0 이 되어 동점으로 보인다.
 */
public final class SearchResult implements Comparable<SearchResult> {

    private final int docId;
    private final double score;

    public SearchResult(int docId, double score) {
        if (docId < 0) {
            throw new IllegalArgumentException("문서 번호는 0 이상이다: " + docId);
        }
        this.docId = docId;
        this.score = score;
    }

    public int docId() {
        return docId;
    }

    public double score() {
        return score;
    }

    @Override
    public int compareTo(SearchResult other) {
        // TODO 4: 위 javadoc 의 정렬 규칙. 두 키의 **방향이 서로 반대**라는 데서 실수가 난다.
        //
        // 동점 규칙을 빼면 대부분의 테스트가 그대로 통과한다. 점수가 겹치는 질의에서만,
        // 그것도 정렬이 안정 정렬이라 입력 순서가 우연히 맞는 동안에는 드러나지 않는다.
        throw new UnsupportedOperationException("TODO 4: compareTo");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchResult other)) {
            return false;
        }
        return docId == other.docId && Double.compare(score, other.score) == 0;
    }

    @Override
    public int hashCode() {
        return docId * 31 + Double.hashCode(score);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "문서 %d 점수 %.6f", docId, score);
    }
}
