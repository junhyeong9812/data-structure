package com.datastructure.searchindex;

import static com.datastructure.searchindex.TestSupport.docIds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 색인과 질의에 다른 분석기를 쓰면 무슨 일이 생기나.
 *
 * 예외가 안 난다. 0건이 나온다. 그래서 실무에서 제일 흔한 검색 버그다.
 * 색인은 잘 만들어져 있고 문서도 거기 있는데 아무것도 안 나온다.
 * 로그에도 안 남는다. 질의는 성공했기 때문이다.
 *
 * 이 파일은 그 조용한 0건을 테스트로 못 박는다.
 * 두 엔진에 같은 일이 생긴다는 것도 같이 본다. 역색인만의 문제가 아니다.
 */
@DisplayName("분석기 불일치")
class AnalyzerMismatchTest {

    /** 자르기만 하고 소문자화도 불용어 제거도 안 하는 분석기. */
    private static final Analyzer RAW = text -> new SimpleTokenizer().tokenize(text);

    private static final Analyzer STANDARD = new StandardAnalyzer(Set.of("the"));

    @Nested
    @DisplayName("소문자화가 한쪽에만 있을 때")
    class LowercaseMismatch {

        @Test
        @DisplayName("색인만 소문자화하면 대문자 질의가 0건이다")
        void indexLowercasedQueryNot() {
            SearchEngine engine = new InvertedIndexEngine(STANDARD, RAW, new TfIdfScorer());
            engine.index(0, "Black CAT");
            engine.index(1, "black cat");

            assertEquals(List.of(), engine.search("CAT", 10),
                    "색인에는 cat 으로 들어갔는데 질의는 CAT 그대로 찾는다");
            assertEquals(List.of(0, 1), docIds(engine.search("cat", 10)),
                    "소문자로 물으면 나온다. 색인은 멀쩡하다");
        }

        @Test
        @DisplayName("질의만 소문자화하면 대문자로 쓰인 문서를 못 찾는다")
        void queryLowercasedIndexNot() {
            SearchEngine engine = new InvertedIndexEngine(RAW, STANDARD, new TfIdfScorer());
            engine.index(0, "Black CAT");
            engine.index(1, "black cat");

            assertEquals(List.of(1), docIds(engine.search("cat", 10)),
                    "0번 문서는 CAT 으로 색인돼 있어서 영영 안 나온다");
            assertTrue(engine.termCount() > 2,
                    "같은 말이 대소문자별로 다른 항이 된다. 색인이 쓸데없이 커진다");
        }

        @Test
        @DisplayName("같은 분석기를 쓰면 전부 나온다")
        void matchedAnalyzersWork() {
            SearchEngine engine = new InvertedIndexEngine(STANDARD, STANDARD, new TfIdfScorer());
            engine.index(0, "Black CAT");
            engine.index(1, "black cat");
            assertEquals(List.of(0, 1), docIds(engine.search("CAT", 10)));
            assertEquals(List.of(0, 1), docIds(engine.search("cat", 10)));
            assertEquals(2, engine.termCount(), "black 과 cat 둘뿐이다");
        }
    }

    @Nested
    @DisplayName("불용어가 한쪽에만 있을 때")
    class StopwordMismatch {

        @Test
        @DisplayName("색인에서만 지우면 그 말이 든 질의가 통째로 0건이다")
        void indexDropsStopwordsQueryDoesNot() {
            SearchEngine engine = new InvertedIndexEngine(STANDARD, RAW, new TfIdfScorer());
            engine.index(0, "the black cat");

            assertEquals(List.of(0), docIds(engine.search("black", 10)));
            assertEquals(List.of(), engine.search("the black", 10),
                    "the 가 색인에 없으니 AND 가 공집합이다. "
                            + "질의에 흔한 말 하나만 섞여도 답이 통째로 사라진다");
        }

        @Test
        @DisplayName("구문 검색도 같이 무너진다")
        void phraseBreaksToo() {
            SearchEngine engine = new InvertedIndexEngine(STANDARD, RAW, new TfIdfScorer());
            engine.index(0, "the black cat");
            assertEquals(List.of(), engine.searchPhrase("the black cat"),
                    "위치까지 맞춰 담아뒀는데 항 이름이 안 맞아서 못 쓴다");
            assertEquals(List.of(0), engine.searchPhrase("black cat"));
        }
    }

    @Nested
    @DisplayName("두 엔진에 똑같이 일어난다")
    class BothEnginesFail {

        @Test
        @DisplayName("전수 조사도 조용히 0건을 준다")
        void linearScanFailsTheSameWay() {
            SearchEngine naive = new LinearScanEngine(STANDARD, RAW, new TfIdfScorer());
            SearchEngine indexed = new InvertedIndexEngine(STANDARD, RAW, new TfIdfScorer());
            naive.index(0, "Black CAT");
            indexed.index(0, "Black CAT");

            assertEquals(List.of(), naive.search("CAT", 10));
            assertEquals(List.of(), indexed.search("CAT", 10));
            assertEquals(docIds(naive.search("cat", 10)), docIds(indexed.search("cat", 10)),
                    "색인 구조의 문제가 아니라 분석기 계약의 문제다");
        }
    }
}
