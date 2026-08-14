package com.datastructure.searchindex;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * [구현] 자르기 -> 소문자화 -> 불용어 제거.
 *
 * 세 단계의 순서가 계약이다. 소문자로 바꾼 뒤에 불용어를 본다.
 * 뒤집으면 "The" 가 안 걸러지고 "the" 만 걸러진다. 같은 말이 두 항이 된다.
 *
 * toLowerCase 에 Locale.ROOT 를 준다. 기본 로케일을 쓰면 터키어 환경에서
 * I 가 점 없는 i 로 바뀌어 색인과 질의가 어긋난다. 기계마다 답이 달라지는 종류의 버그다.
 */
public class StandardAnalyzer implements Analyzer {

    /** 기본 불용어. 전부 소문자로 적어 둔다. 비교 직전에 소문자로 바꾸기 때문이다. */
    public static final Set<String> DEFAULT_STOPWORDS =
            Set.of("a", "an", "and", "or", "the", "of", "is", "to", "in", "it",
                    "그리고", "그러나", "또는", "및", "등");

    private final Tokenizer tokenizer;
    private final Set<String> stopwords;

    public StandardAnalyzer() {
        this(new SimpleTokenizer(), DEFAULT_STOPWORDS);
    }

    public StandardAnalyzer(Set<String> stopwords) {
        this(new SimpleTokenizer(), stopwords);
    }

    public StandardAnalyzer(Tokenizer tokenizer, Set<String> stopwords) {
        if (tokenizer == null || stopwords == null) {
            throw new IllegalArgumentException("토크나이저와 불용어 집합은 null 일 수 없다");
        }
        this.tokenizer = tokenizer;
        this.stopwords = Set.copyOf(stopwords);
    }

    @Override
    public List<String> analyze(String text) {
        List<String> out = new ArrayList<>();
        for (String token : tokenizer.tokenize(text)) {
            String lower = token.toLowerCase(Locale.ROOT);
            if (!stopwords.contains(lower)) {
                out.add(lower);
            }
        }
        return out;
    }

    /** 불용어 집합. 테스트가 들여다본다. */
    public Set<String> stopwords() {
        return stopwords;
    }

    @Override
    public String toString() {
        return "표준 분석기(불용어 " + stopwords.size() + "개)";
    }
}
