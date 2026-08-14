package com.datastructure.searchindex;

import java.util.ArrayList;
import java.util.List;

/**
 * 글자와 숫자가 아닌 것을 만나면 자른다.
 *
 * 공백, 마침표, 쉼표, 괄호, 따옴표가 전부 경계다.
 * Character.isLetterOrDigit 이 한글도 글자로 보므로 한국어도 그대로 잘린다.
 *
 * 한국어를 이렇게 자르면 "고양이가" 와 "고양이" 가 다른 항이 된다.
 * 그것을 고치려면 형태소 분석기가 필요하고, 그 규칙은 이 박스의 주제가 아니다.
 * 영어의 어간 추출(stemming)도 같은 이유로 넣지 않았다.
 */
public class SimpleTokenizer implements Tokenizer {

    @Override
    public List<String> tokenize(String text) {
        if (text == null) {
            throw new IllegalArgumentException("본문이 null 이다");
        }
        // TODO 1: 글자와 숫자를 모으다가 그 밖의 글자를 만나면 한 토큰으로 끊는다.
        //
        // Character.isLetterOrDigit 하나로 판정한다. 빈 토큰은 내보내지 않는다.
        //
        // 본문의 **마지막 글자가 경계가 아니면** 그 토큰은 경계를 못 만나고 끝난다.
        // 이 경우를 흘려보내면 예외도 안 나고 마지막 항 하나가 조용히 사라진다.
        // 그리고 그 항의 위치까지 같이 사라지므로 구문 검색이 문서 끝에서만 틀린다.
        throw new UnsupportedOperationException("TODO 1: tokenize");
    }

    @Override
    public String toString() {
        return "공백과 문장부호로 자르기";
    }
}
