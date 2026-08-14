package com.datastructure.searchindex;

import static com.datastructure.searchindex.TestSupport.docIds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * SearchEngine 계약 테스트. 전수 조사와 역색인이 같은 벌을 물려받는다.
 *
 * 구현이 둘이므로 abstract 로 둔다. 하위 클래스는 create 만 준다.
 * 하위에서 같은 이름의 @Nested 를 만들면 여기 있는 것이 가려져 조용히 안 돌아간다.
 * 그래서 구조 검사는 IndexStructureTest 라는 별도 클래스에 둔다.
 */
abstract class SearchEngineContractTest {

    /** 기본 구성. 표준 분석기와 TF-IDF. */
    protected abstract SearchEngine create();

    /** 분석기와 채점기를 갈아끼운 구성. 순위를 보는 테스트가 쓴다. */
    protected abstract SearchEngine create(Analyzer analyzer, Scorer scorer);

    /** 흔한 말 하나와 드문 말 하나. TF 와 TF-IDF 가 여기서 갈린다. */
    protected SearchEngine refunds(Scorer scorer) {
        SearchEngine engine = create(new StandardAnalyzer(Set.of()), scorer);
        engine.index(0, "회사 회사 회사 회사 회사 환불");
        engine.index(1, "회사 환불 환불 환불");
        engine.index(2, "회사 배송");
        engine.index(3, "회사 포장");
        engine.index(4, "회사 문의");
        return engine;
    }

    /** 같은 낱말을 순서만 바꿔 담은 문서들. 구문 검색이 여기서 갈린다. */
    protected SearchEngine cats() {
        SearchEngine engine = create();
        engine.index(0, "검은 고양이 가 담장 위 를 걷는다");
        engine.index(1, "고양이 가 검은 담장 위 에 앉았다");
        engine.index(2, "검은 개 가 담장 을 넘었다");
        return engine;
    }

    @Nested
    @DisplayName("색인에 담기")
    class Indexing {

        @Test
        @DisplayName("빈 엔진은 아무것도 못 찾는다")
        void emptyEngine() {
            SearchEngine engine = create();
            assertEquals(0, engine.docCount());
            assertEquals(0, engine.termCount());
            assertEquals(List.of(), engine.search("고양이", 10));
            assertEquals(List.of(), engine.searchPhrase("검은 고양이"));
        }

        @Test
        @DisplayName("문서 수와 서로 다른 항 개수")
        void countsDocumentsAndTerms() {
            SearchEngine engine = create(new StandardAnalyzer(Set.of()), new TfIdfScorer());
            engine.index(0, "회사 회사 환불");
            engine.index(1, "환불 배송");
            assertEquals(2, engine.docCount());
            assertEquals(3, engine.termCount(), "회사, 환불, 배송 셋이다. 중복은 한 번만 센다");
        }

        @Test
        @DisplayName("분석 결과가 빈 문서도 한 개로 센다. 그 수가 TF-IDF 의 N 이다")
        void emptyDocumentStillCounts() {
            SearchEngine engine = create();
            engine.index(0, "고양이");
            engine.index(1, "");
            engine.index(2, "the and of is");
            assertEquals(3, engine.docCount(), "불용어만 든 문서도 문서다");
            assertEquals(1, engine.termCount(), "항으로 남은 것은 고양이 하나뿐이다");
        }

        @Test
        @DisplayName("같은 문서 번호를 두 번 넣으면 던진다")
        void rejectsDuplicateDocId() {
            SearchEngine engine = create();
            engine.index(7, "고양이");
            assertThrows(IllegalArgumentException.class, () -> engine.index(7, "강아지"));
        }

        @Test
        @DisplayName("음수 문서 번호와 null 본문은 던진다")
        void rejectsBadArguments() {
            SearchEngine engine = create();
            assertThrows(IllegalArgumentException.class, () -> engine.index(-1, "고양이"));
            assertThrows(IllegalArgumentException.class, () -> engine.index(0, null));
        }

        @Test
        @DisplayName("문서 번호가 뒤죽박죽 들어와도 답은 같다")
        void docIdsMayArriveOutOfOrder() {
            SearchEngine shuffled = create();
            shuffled.index(9, "고양이 담장");
            shuffled.index(2, "고양이 지붕");
            shuffled.index(5, "고양이 담장");
            assertEquals(List.of(2, 5, 9), docIds(shuffled.search("고양이", 10)),
                    "점수가 같으면 문서 번호 오름차순이다. 담은 순서가 아니다");
            assertEquals(List.of(5, 9), docIds(shuffled.search("고양이 담장", 10)));
        }
    }

    @Nested
    @DisplayName("AND 질의")
    class AndQueries {

        @Test
        @DisplayName("질의어를 전부 가진 문서만 답이다")
        void requiresEveryTerm() {
            SearchEngine engine = cats();
            assertEquals(List.of(0, 1, 2), docIds(engine.search("검은", 10)));
            assertEquals(List.of(0, 1), docIds(engine.search("검은 고양이", 10)),
                    "2번 문서에는 고양이가 없다");
            assertEquals(List.of(0, 1), docIds(engine.search("고양이 검은", 10)),
                    "AND 는 질의어 순서를 안 본다");
        }

        @Test
        @DisplayName("색인에 없는 항이 하나라도 있으면 공집합")
        void missingTermMeansEmpty() {
            SearchEngine engine = cats();
            assertEquals(List.of(), engine.search("검은 코끼리", 10));
            assertEquals(List.of(), engine.search("코끼리", 10));
        }

        @Test
        @DisplayName("같은 질의어를 두 번 쓰면 한 번으로 친다")
        void duplicateQueryTermsCountOnce() {
            SearchEngine engine = refunds(new TermFrequencyScorer());
            List<SearchResult> once = engine.search("환불", 10);
            List<SearchResult> twice = engine.search("환불 환불", 10);
            assertEquals(once, twice,
                    "중복을 안 걷어내면 그 항의 몫이 두 배가 되어 순위가 달라진다");
        }

        @Test
        @DisplayName("분석 후 비는 질의는 빈 목록")
        void emptyQueryIsEmpty() {
            SearchEngine engine = cats();
            assertEquals(List.of(), engine.search("", 10));
            assertEquals(List.of(), engine.search("   ", 10));
            assertEquals(List.of(), engine.search("the and of", 10), "전부 불용어다");
        }

        @Test
        @DisplayName("대소문자를 안 가린다. 같은 분석기를 양쪽에 쓰기 때문이다")
        void caseInsensitive() {
            SearchEngine engine = create();
            engine.index(0, "Black CAT on the Roof");
            assertEquals(List.of(0), docIds(engine.search("cat", 10)));
            assertEquals(List.of(0), docIds(engine.search("CAT", 10)));
            assertEquals(List.of(0), docIds(engine.search("Black cat", 10)));
        }

        @Test
        @DisplayName("null 질의와 음수 k 는 던진다")
        void rejectsBadArguments() {
            SearchEngine engine = cats();
            assertThrows(IllegalArgumentException.class, () -> engine.search(null, 10));
            assertThrows(IllegalArgumentException.class, () -> engine.search("고양이", -1));
            assertThrows(IllegalArgumentException.class, () -> engine.searchPhrase(null));
        }
    }

    @Nested
    @DisplayName("순위와 자르기")
    class Ranking {

        @Test
        @DisplayName("점수 내림차순")
        void ordersByScoreDescending() {
            SearchEngine engine = refunds(new TermFrequencyScorer());
            List<SearchResult> results = engine.search("회사 환불", 10);
            assertEquals(List.of(0, 1), docIds(results));
            assertEquals(6.0, results.get(0).score(), 1e-9, "회사 5번 더하기 환불 1번");
            assertEquals(4.0, results.get(1).score(), 1e-9, "회사 1번 더하기 환불 3번");
        }

        @Test
        @DisplayName("동점이면 문서 번호 오름차순")
        void tieBreaksByDocId() {
            SearchEngine engine = cats();
            List<SearchResult> results = engine.search("검은 고양이", 10);
            assertEquals(2, results.size());
            assertEquals(results.get(0).score(), results.get(1).score(), 1e-12,
                    "두 문서의 tf 와 df 가 같아서 점수가 같다");
            assertEquals(List.of(0, 1), docIds(results),
                    "동점 규칙이 없으면 이 순서가 구현마다 달라진다");
        }

        @Test
        @DisplayName("k 가 결과보다 작으면 위에서 자른다")
        void topKCuts() {
            SearchEngine engine = refunds(new TermFrequencyScorer());
            assertEquals(List.of(0), docIds(engine.search("회사 환불", 1)));
            assertEquals(List.of(0, 1), docIds(engine.search("회사 환불", 2)));
            assertEquals(List.of(0, 1), docIds(engine.search("회사 환불", 99)),
                    "k 가 남아도 없는 답을 만들지 않는다");
        }

        @Test
        @DisplayName("k 가 0 이면 빈 목록")
        void kZeroIsEmpty() {
            assertEquals(List.of(), refunds(new TfIdfScorer()).search("회사", 0));
        }

        @Test
        @DisplayName("모든 문서에 있는 항은 0 점이다. df 가 N 이면 log(1) 이다")
        void termInEveryDocumentScoresZero() {
            SearchEngine engine = refunds(new TfIdfScorer());
            List<SearchResult> results = engine.search("회사", 10);
            assertEquals(5, results.size(), "다섯 문서 전부가 답이다");
            for (SearchResult r : results) {
                assertEquals(0.0, r.score(), 1e-12,
                        "모든 문서에 있는 말은 문서를 가르지 못한다");
            }
            assertEquals(List.of(0, 1, 2, 3, 4), docIds(results),
                    "전부 0 점이라 순위가 문서 번호 순으로 무너진다");
        }
    }

    @Nested
    @DisplayName("구문 검색")
    class PhraseQueries {

        @Test
        @DisplayName("붙어 있는 것만 답이다")
        void requiresAdjacency() {
            SearchEngine engine = cats();
            assertEquals(List.of(0, 1), docIds(engine.search("검은 고양이", 10)),
                    "AND 는 둘을 구별하지 못한다");
            assertEquals(List.of(0), engine.searchPhrase("검은 고양이"),
                    "1번은 검은 다음이 담장이라 아니다");
            assertEquals(List.of(0, 1), engine.searchPhrase("고양이 가"),
                    "0번은 1과 2 자리, 1번은 0과 1 자리다. 둘 다 붙어 있다");
            assertEquals(List.of(0, 2), engine.searchPhrase("가 담장"),
                    "1번은 가 다음이 검은이다");
            assertEquals(List.of(0, 1, 2), engine.searchPhrase("담장"),
                    "한 낱말짜리 구문은 그냥 AND 다");
        }

        @Test
        @DisplayName("순서를 뒤집으면 안 맞는다")
        void orderMatters() {
            SearchEngine engine = cats();
            assertEquals(List.of(0), engine.searchPhrase("검은 고양이"));
            assertEquals(List.of(), engine.searchPhrase("고양이 검은"),
                    "1번은 고양이와 검은 사이에 가 가 있다");
        }

        @Test
        @DisplayName("같은 낱말이 반복되는 구문")
        void repeatedTerms() {
            SearchEngine engine = create();
            engine.index(0, "라 라 라 노래");
            engine.index(1, "라 노래 라 노래");
            assertEquals(List.of(0), engine.searchPhrase("라 라"));
            assertEquals(List.of(0), engine.searchPhrase("라 라 라"));
            assertEquals(List.of(0, 1), engine.searchPhrase("라 노래"));
        }

        @Test
        @DisplayName("분석 후 비는 구문은 빈 목록")
        void emptyPhrase() {
            SearchEngine engine = cats();
            assertEquals(List.of(), engine.searchPhrase(""));
            assertEquals(List.of(), engine.searchPhrase("the and"));
        }

        @Test
        @DisplayName("불용어를 지우면 그 자리가 메워진다. 이 분석기의 대가다")
        void stopwordsCollapsePositions() {
            SearchEngine engine = create(new StandardAnalyzer(Set.of("the")), new TfIdfScorer());
            engine.index(0, "the black the cat");
            assertEquals(List.of(0), engine.searchPhrase("black cat"),
                    "원문에서는 안 붙어 있는데 붙은 것으로 보인다. "
                            + "불용어 자리에 간격을 안 남겼기 때문이고, 알고 고른 대가다");
            assertEquals(List.of(0), engine.searchPhrase("black the"),
                    "질의에서도 the 가 사라져서 black 한 낱말짜리 구문이 된다");
            assertTrue(engine.searchPhrase("the").isEmpty(),
                    "불용어만 남으면 물어볼 것이 없다");
        }
    }
}
