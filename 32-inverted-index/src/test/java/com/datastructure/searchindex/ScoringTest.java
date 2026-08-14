package com.datastructure.searchindex;

import static com.datastructure.searchindex.TestSupport.docIds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 순위. 이 박스의 나머지 절반이다.
 *
 * 찾는 것과 줄 세우는 것은 다른 문제다. 역색인은 앞의 것만 푼다.
 * 뒤의 것을 단순 빈도로 풀면 흔한 말을 많이 가진 문서가 이긴다.
 *
 * 기댓값은 파이썬 참조 구현으로 검산한 값이다. 손으로 쓰지 않았다.
 * 부동소수점이므로 값 비교에는 델타를 둔다. 순위는 순서로만 단언한다.
 */
@DisplayName("점수와 순위")
class ScoringTest {

    private static final double DELTA = 1e-9;

    /** 흔한 말 하나와 드문 말 하나. 불용어 제거를 끄고 본다. */
    private static SearchEngine refunds(Scorer scorer) {
        SearchEngine engine = new InvertedIndexEngine(new StandardAnalyzer(Set.of()), scorer);
        engine.index(0, "회사 회사 회사 회사 회사 환불");
        engine.index(1, "회사 환불 환불 환불");
        engine.index(2, "회사 배송");
        engine.index(3, "회사 포장");
        engine.index(4, "회사 문의");
        return engine;
    }

    @Nested
    @DisplayName("Scorer 의 값")
    class ScorerValues {

        @Test
        @DisplayName("TF-IDF 는 tf 곱하기 log(N 나누기 df) 다")
        void tfIdfFormula() {
            Scorer scorer = new TfIdfScorer();
            assertEquals(4.828313737302301, scorer.score(3, 2, 10), DELTA);
            assertEquals(23.02585092994046, scorer.score(5, 1, 100), DELTA);
            assertEquals(1.3862943611198906, scorer.score(2, 4, 8), DELTA);
            assertEquals(4.394449154672439, scorer.score(4, 3, 9), DELTA);
        }

        @Test
        @DisplayName("df 가 N 이면 0 이다. log(1) 이기 때문이다")
        void everywhereMeansZero() {
            Scorer scorer = new TfIdfScorer();
            assertEquals(0.0, scorer.score(1, 10, 10), DELTA);
            assertEquals(0.0, scorer.score(999, 10, 10), DELTA,
                    "몇 번 나오든 0 이다. 모든 문서에 있으면 문서를 못 가른다");
            assertEquals(0.0, scorer.score(1, 1, 1), DELTA,
                    "문서가 하나뿐인 색인에서는 모든 항이 0 점이다");
        }

        @Test
        @DisplayName("나눗셈을 정수로 하면 조용히 0 이 된다")
        void divisionMustBeFloatingPoint() {
            // 100 나누기 60 을 int 로 하면 1 이고 log(1) 은 0 이다.
            // 흔한 항의 점수가 통째로 사라지는데 예외는 안 난다.
            assertEquals(0.5108256237659907, new TfIdfScorer().score(1, 60, 100), DELTA);
            assertTrue(new TfIdfScorer().score(1, 60, 100) > 0.0,
                    "df 가 N 보다 작으면 점수가 0 보다 커야 한다");
        }

        @Test
        @DisplayName("0 이하 인자는 0 점")
        void guards() {
            Scorer tfIdf = new TfIdfScorer();
            assertEquals(0.0, tfIdf.score(0, 2, 10), DELTA);
            assertEquals(0.0, tfIdf.score(3, 0, 10), DELTA);
            assertEquals(0.0, tfIdf.score(3, 2, 0), DELTA);
            assertEquals(0.0, new TermFrequencyScorer().score(0, 2, 10), DELTA);
        }

        @Test
        @DisplayName("단순 빈도는 뒤의 두 인자를 안 본다")
        void termFrequencyIgnoresTheRest() {
            Scorer scorer = new TermFrequencyScorer();
            assertEquals(3.0, scorer.score(3, 2, 10), DELTA);
            assertEquals(3.0, scorer.score(3, 10, 10), DELTA,
                    "모든 문서에 있는 말도 똑같이 3 점이다. 그게 문제다");
        }
    }

    @Nested
    @DisplayName("SearchResult 의 정렬 규칙")
    class ResultOrdering {

        @Test
        @DisplayName("점수 내림차순, 동점이면 문서 번호 오름차순")
        void sortsByScoreThenDocId() {
            List<SearchResult> results = new ArrayList<>(List.of(
                    new SearchResult(5, 1.0),
                    new SearchResult(2, 3.0),
                    new SearchResult(9, 1.0),
                    new SearchResult(1, 3.0)));
            Collections.sort(results);
            // 3.0 끼리는 1 이 먼저다. 문서 번호 오름차순이기 때문이다.
            // (기댓값을 손으로 [2, 1, 5, 9] 라고 썼다가 impl 이 잡았다)
            assertEquals(List.of(1, 2, 5, 9), docIds(results));
        }

        @Test
        @DisplayName("아주 작은 점수 차이도 순서를 가른다")
        void tinyDifferencesStillOrder() {
            // 빼서 int 로 자르면 0.4 가 0 이 되어 동점으로 보인다.
            List<SearchResult> results = new ArrayList<>(List.of(
                    new SearchResult(1, 1.0),
                    new SearchResult(2, 1.4)));
            Collections.sort(results);
            assertEquals(List.of(2, 1), docIds(results),
                    "점수 차이가 1 보다 작아도 2번이 위다");
        }

        @Test
        @DisplayName("같은 문서 번호라도 점수가 다르면 다른 결과다")
        void equality() {
            assertEquals(new SearchResult(1, 2.0), new SearchResult(1, 2.0));
            assertEquals(new SearchResult(1, 2.0).hashCode(), new SearchResult(1, 2.0).hashCode());
            assertNotEquals(new SearchResult(1, 2.0), new SearchResult(1, 2.5));
        }
    }

    @Nested
    @DisplayName("단순 빈도 대 TF-IDF")
    class RankingChanges {

        @Test
        @DisplayName("순위가 뒤집힌다")
        void tfIdfFlipsTheOrder() {
            List<SearchResult> byFrequency = refunds(new TermFrequencyScorer()).search("회사 환불", 10);
            List<SearchResult> byTfIdf = refunds(new TfIdfScorer()).search("회사 환불", 10);

            assertEquals(List.of(0, 1), docIds(byFrequency),
                    "단순 빈도는 회사를 다섯 번 가진 0번을 위로 올린다");
            assertEquals(6.0, byFrequency.get(0).score(), DELTA);
            assertEquals(4.0, byFrequency.get(1).score(), DELTA);

            assertEquals(List.of(1, 0), docIds(byTfIdf),
                    "TF-IDF 는 다섯 문서에 다 있는 회사를 0 점으로 만든다");
            assertEquals(2.7488721956224653, byTfIdf.get(0).score(), DELTA);
            assertEquals(0.9162907318741551, byTfIdf.get(1).score(), DELTA);

            assertNotEquals(docIds(byFrequency), docIds(byTfIdf),
                    "채점기만 갈아끼웠는데 첫 줄이 바뀐다");
        }

        @Test
        @DisplayName("흔한 항의 몫이 정확히 0 이다")
        void commonTermContributesNothing() {
            SearchEngine engine = refunds(new TfIdfScorer());
            List<SearchResult> both = engine.search("회사 환불", 10);
            List<SearchResult> rareOnly = engine.search("환불", 10);
            assertEquals(docIds(both), docIds(rareOnly), "회사를 빼도 순위가 같다");
            for (int i = 0; i < both.size(); i++) {
                assertEquals(rareOnly.get(i).score(), both.get(i).score(), DELTA,
                        "회사가 더한 값이 0 이라 점수까지 같다");
            }
        }

        @Test
        @DisplayName("불용어를 안 지우면 기능어가 첫 줄에 온다")
        void stopwordsMatterForTermFrequency() {
            SearchEngine keepsAll = new InvertedIndexEngine(
                    new StandardAnalyzer(Set.of()), new TermFrequencyScorer());
            keepsAll.index(0, "그리고 그리고 그리고 그리고 그리고 환불");
            keepsAll.index(1, "그리고 환불 환불 환불");
            assertEquals(List.of(0, 1), docIds(keepsAll.search("그리고 환불", 10)),
                    "그리고를 다섯 번 쓴 문서가 이긴다");

            SearchEngine drops = new InvertedIndexEngine(
                    new StandardAnalyzer(Set.of("그리고")), new TermFrequencyScorer());
            drops.index(0, "그리고 그리고 그리고 그리고 그리고 환불");
            drops.index(1, "그리고 환불 환불 환불");
            assertEquals(List.of(1, 0), docIds(drops.search("그리고 환불", 10)),
                    "불용어를 지우면 질의도 환불 하나가 되고 순위가 뒤집힌다");
            assertEquals(1, drops.termCount(), "색인에 남은 항은 환불 하나뿐이다");
        }
    }
}
