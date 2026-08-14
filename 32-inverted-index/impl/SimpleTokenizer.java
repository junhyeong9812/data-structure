package com.datastructure.searchindex;

import java.util.ArrayList;
import java.util.List;

/**
 * [구현] 글자와 숫자가 아닌 것을 만나면 자른다.
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
        List<String> out = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                buf.append(c);
            } else if (buf.length() > 0) {
                out.add(buf.toString());
                buf.setLength(0);
            }
        }
        // 마지막 토큰은 경계를 못 만나고 끝난다. 여기서 흘려보내지 않으면 조용히 사라진다.
        if (buf.length() > 0) {
            out.add(buf.toString());
        }
        return out;
    }

    @Override
    public String toString() {
        return "공백과 문장부호로 자르기";
    }
}
