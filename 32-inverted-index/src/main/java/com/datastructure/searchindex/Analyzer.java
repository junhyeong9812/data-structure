package com.datastructure.searchindex;

import java.util.List;

/**
 * 본문을 색인에 넣을 항(term) 목록으로 바꾸는 계약.
 *
 * 토큰화보다 한 겹 위다. 자르고, 다듬고, 버린다.
 * 이 문제집의 StandardAnalyzer 는 자르기 -> 소문자화 -> 불용어 제거까지만 한다.
 *
 * 결과 목록의 인덱스가 곧 그 항의 위치다. 0 부터 센다.
 * 불용어를 버리면 그만큼 뒤가 당겨진다. 실무 엔진은 버린 자리에 간격을 남겨 두는데
 * (position increment gap) 여기서는 그 장치를 넣지 않는다. 규칙이 하나 늘면 이 박스의 주제가 흐려진다.
 *
 * 이 인터페이스에는 TODO 가 없다.
 */
@FunctionalInterface
public interface Analyzer {

    /**
     * 본문을 항 목록으로. 순서를 지킨다.
     *
     * text 가 null 이면 IllegalArgumentException.
     */
    List<String> analyze(String text);
}
