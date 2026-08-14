package com.datastructure.searchindex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * [구현] 항 하나가 문서 하나에 어떻게 들어 있는지.
 *
 * 문서 번호, 빈도, 위치 목록 셋이다. 빈도는 따로 안 들고 위치 개수로 센다.
 * 두 값을 다 들면 둘이 어긋날 자리가 생기고, 어긋나면 조용히 점수가 틀린다.
 *
 * 위치는 분석기가 준 항 목록에서의 인덱스다. 0 부터 세고 오름차순이다.
 * 위치를 안 담으면 구문 검색을 아예 못 한다. 대신 색인이 커진다.
 * 원문의 토큰 하나마다 정수 하나가 색인에 들어간다. MeasurementTest 가 그 개수를 못 박는다.
 *
 * 이 클래스에는 TODO 가 없다. 담는 그릇이고, addPosition 의 오름차순 검사는
 * 학습자가 채울 코드가 기대는 계약이라 미리 세워둔다.
 */
public final class Posting {

    private final int docId;
    private final List<Integer> positions = new ArrayList<>();

    public Posting(int docId) {
        if (docId < 0) {
            throw new IllegalArgumentException("문서 번호는 0 이상이다: " + docId);
        }
        this.docId = docId;
    }

    public int docId() {
        return docId;
    }

    /** 이 문서에서 이 항이 나온 횟수. TF-IDF 의 tf 다. */
    public int frequency() {
        return positions.size();
    }

    /** 오름차순 위치 목록. 밖에서 못 고친다. */
    public List<Integer> positions() {
        return Collections.unmodifiableList(positions);
    }

    /**
     * 위치를 하나 덧붙인다.
     *
     * 본문을 앞에서 뒤로 훑으면 자연히 오름차순이므로 정렬할 일이 없다.
     * 오름차순이 아니면 던진다. 이 목록이 오름차순이라는 것을 구문 검색이 그대로 믿기 때문이다.
     */
    public void addPosition(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("위치는 0 이상이다: " + position);
        }
        if (!positions.isEmpty() && position <= positions.get(positions.size() - 1)) {
            throw new IllegalArgumentException(
                    "위치는 오름차순이어야 한다. 마지막이 " + positions.get(positions.size() - 1)
                            + " 인데 " + position + " 이 왔다");
        }
        positions.add(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Posting other)) {
            return false;
        }
        return docId == other.docId && positions.equals(other.positions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docId, positions);
    }

    @Override
    public String toString() {
        return "문서 " + docId + " 빈도 " + frequency() + " 위치 " + positions;
    }
}
