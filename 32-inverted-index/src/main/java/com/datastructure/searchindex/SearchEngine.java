package com.datastructure.searchindex;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 문서를 담고 "이 말이 어느 문서에 있나"를 묻는 구조.
 *
 * <h2>09번과 무엇이 다른가</h2>
 *
 * 09번 트라이는 "이 단어가 있나"에 답했다. 사전 하나였다.
 * 검색 엔진이 답해야 하는 것은 "이 단어가 어느 문서에 있나"다. 사전이 아니라 색인이다.
 *
 * | | 무엇을 담나 | 무엇을 묻나 |
 * |---|---|---|
 * | 09번 트라이 | 단어 | 이 단어가 있나, 이 접두사로 시작하는 것 |
 * | 32번 역색인 | 단어 -&gt; 문서 목록 | 이 단어가 어느 문서에 있나, 어느 문서가 위인가 |
 *
 * <h2>계약</h2>
 *
 * 질의는 AND 다. 분석기를 통과한 질의어를 전부 가진 문서만 답이다.
 * 같은 질의어가 두 번 나오면 한 번으로 친다. "고양이 고양이" 와 "고양이" 는 같은 질의다.
 *
 * 점수는 질의어마다 Scorer 를 부른 값의 합이다.
 * 더하는 순서는 distinctTerms 가 준 순서로 고정한다. 순서를 바꾸면 부동소수점 합의
 * 마지막 비트가 달라져서 동점 판정이 흔들린다. 그러면 두 구현의 답을 비교할 수 없다.
 * 병합을 어느 순서로 하든 점수를 더하는 순서는 이 순서여야 한다.
 *
 * 이 인터페이스에는 TODO 가 없다.
 */
public interface SearchEngine {

    /**
     * 문서를 색인에 넣는다.
     *
     * docId 는 0 이상이고 한 번만 쓸 수 있다. 같은 번호를 두 번 넣으면 IllegalArgumentException.
     * 지우기와 고쳐 넣기는 다루지 않는다. 이 박스의 주제가 아니다.
     *
     * text 가 null 이거나 docId 가 음수면 IllegalArgumentException.
     */
    void index(int docId, String text);

    /** 색인에 든 문서 수. 분석 결과가 빈 문서도 한 개로 센다. TF-IDF 의 N 이 이 값이다. */
    int docCount();

    /** 색인에 든 서로 다른 항의 개수. 색인 분석기를 통과한 뒤의 항이다. */
    int termCount();

    /**
     * 질의어를 전부 가진 문서를 점수 내림차순으로 최대 k 개.
     *
     * 동점이면 문서 번호 오름차순이다. 그래서 답이 하나로 정해진다.
     * k 가 0 이거나 질의가 분석 후 비면 빈 목록. k 가 음수면 IllegalArgumentException.
     * query 가 null 이면 IllegalArgumentException.
     */
    List<SearchResult> search(String query, int k);

    /**
     * 분석된 항이 그 순서 그대로 붙어 나오는 문서를 문서 번호 오름차순으로.
     *
     * "검은 고양이" 는 검은 다음 칸이 고양이인 문서만 답이다.
     * AND 질의로는 이것을 구별할 수 없다. 두 항이 어디에 있는지를 안 담기 때문이다.
     *
     * phrase 가 분석 후 비면 빈 목록. null 이면 IllegalArgumentException.
     */
    List<Integer> searchPhrase(String phrase);

    /**
     * 분석 결과에서 중복을 걷어낸 질의어. 처음 나온 순서를 지킨다.
     *
     * 두 구현이 이 함수를 같이 쓴다. 점수를 더하는 순서가 여기서 정해지고,
     * 그래야 전수 조사와 역색인의 점수가 비트까지 같아진다.
     */
    static List<String> distinctTerms(List<String> analyzed) {
        return new ArrayList<>(new LinkedHashSet<>(analyzed));
    }
}
