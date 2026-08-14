package com.datastructure.searchindex;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

/**
 * 테스트가 공유하는 도구. 자료구조가 아니라 검증 장비다.
 *
 * 여기 있는 것은 셋뿐이다.
 *   1. 결정적 난수    Random 을 쓰면 JDK 가 바뀔 때 기댓값이 흔들린다
 *   2. 말뭉치 생성    낱말 빈도를 일부러 한쪽으로 쏠리게 만든다. 흔한 말이 없으면 IDF 를 시험할 수 없다
 *   3. 결과 꺼내기    SearchResult 목록에서 문서 번호만 뽑는다. 점수는 델타로 따로 본다
 */
final class TestSupport {

    private TestSupport() {
    }

    /** 낱말 i 의 표기. t000 이 제일 흔하고 번호가 클수록 드물다. */
    static String word(int i) {
        return String.format(Locale.ROOT, "t%03d", i);
    }

    static List<Integer> docIds(List<SearchResult> results) {
        List<Integer> out = new ArrayList<>();
        for (SearchResult r : results) {
            out.add(r.docId());
        }
        return out;
    }

    static void indexAll(SearchEngine engine, List<String> documents) {
        for (int docId = 0; docId < documents.size(); docId++) {
            engine.index(docId, documents.get(docId));
        }
    }

    /** 결정적 난수 발생기. 같은 seed 면 언제 어디서 돌려도 같은 말뭉치가 나온다. */
    static final class Dice {
        private long state;

        Dice(long seed) {
            this.state = seed;
        }

        long next(long bound) {
            state = state * 6364136223846793005L + 1442695040888963407L;
            return Math.floorMod(state >>> 33, bound);
        }

        int nextInt(int bound) {
            return (int) next(bound);
        }

        /**
         * 낱말 하나를 뽑는다. 두 번 뽑아 작은 쪽을 쓴다.
         *
         * 그래서 t000 이 t199 보다 훨씬 자주 나온다. 실제 글이 그렇게 생겼고,
         * 무엇보다 빈도가 고르면 IDF 와 단순 빈도의 차이가 안 드러난다.
         */
        int skewedWord(int vocabulary) {
            return Math.min(nextInt(vocabulary), nextInt(vocabulary));
        }

        /** 문서 하나. 길이는 minLength 이상 maxLength 이하이고 불용어가 섞인다. */
        String document(int vocabulary, int minLength, int maxLength) {
            int length = minLength + nextInt(maxLength - minLength + 1);
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    text.append(' ');
                }
                if (next(6) == 0) {
                    text.append(next(2) == 0 ? "the" : "and");
                } else {
                    text.append(word(skewedWord(vocabulary)));
                }
            }
            return text.toString();
        }

        List<String> documents(int count, int vocabulary, int minLength, int maxLength) {
            List<String> out = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                out.add(document(vocabulary, minLength, maxLength));
            }
            return out;
        }

        /** 질의 하나. 서로 다른 낱말 termCount 개를 공백으로 잇는다. */
        String query(int vocabulary, int termCount) {
            LinkedHashSet<String> terms = new LinkedHashSet<>();
            int guard = 0;
            while (terms.size() < termCount && guard++ < 100) {
                terms.add(word(skewedWord(vocabulary)));
            }
            return String.join(" ", terms);
        }
    }
}
