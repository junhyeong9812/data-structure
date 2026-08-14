package com.datastructure.searchindex;

import java.util.List;

/**
 * 본문을 토큰 목록으로 자르는 계약.
 *
 * 검색의 첫 단계다. 여기서 무엇을 한 토큰으로 볼지 정하고 나면
 * 그 뒤의 모든 것(색인, 질의, 구문 검색)이 그 결정 위에 얹힌다.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
@FunctionalInterface
public interface Tokenizer {

    /**
     * 본문을 토큰으로 자른다. 나온 순서 그대로 담는다.
     *
     * 빈 문자열은 빈 목록이다. text 가 null 이면 IllegalArgumentException.
     */
    List<String> tokenize(String text);
}
