package com.datastructure.searchindex;

import static com.datastructure.searchindex.TestSupport.docIds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * 전수 조사와의 무작위 대조. 이 문제집에서 버그를 가장 많이 잡는 테스트다.
 *
 * 교집합 병합은 부등호 하나만 뒤집어도 "봐야 하는 문서를 안 본다"가 된다.
 * 그러면 예외도 안 나고 결과가 조용히 몇 개 빠진다.
 * 포스팅 리스트의 정렬이 깨져도 마찬가지다. 답이 줄어들 뿐 아무도 안 알려준다.
 *
 * 문서 집합만 보지 않고 순위와 점수까지 본다.
 * 두 구현은 질의어를 같은 순서로 더하므로 점수가 비트까지 같아야 하지만,
 * 부동소수점을 그대로 견주는 것은 위험하므로 순서는 문서 번호 목록으로,
 * 값은 델타로 나눠서 본다.
 */
@DisplayName("전수 조사와의 무작위 대조")
class CrossCheckTest {

    private static final double DELTA = 1e-9;

    private void crossCheck(String label, List<String> documents, List<Integer> docIdOrder,
                            List<String> queries, Scorer scorer, MergeOrder mergeOrder) {
        Analyzer analyzer = new StandardAnalyzer(Set.of("the", "and"));
        SearchEngine naive = new LinearScanEngine(analyzer, scorer);
        InvertedIndexEngine indexed =
                new InvertedIndexEngine(analyzer, analyzer, scorer, mergeOrder);

        for (int i = 0; i < documents.size(); i++) {
            int docId = docIdOrder.get(i);
            naive.index(docId, documents.get(i));
            indexed.index(docId, documents.get(i));
        }
        assertEquals(naive.docCount(), indexed.docCount(), label + ": 문서 수");
        assertEquals(naive.termCount(), indexed.termCount(),
                label + ": 서로 다른 항 개수. 색인이 항을 흘리면 여기서 걸린다");

        long hits = 0;
        long answered = 0;
        for (String query : queries) {
            List<SearchResult> expected = naive.search(query, documents.size());
            List<SearchResult> got = indexed.search(query, documents.size());

            assertEquals(expected.size(), got.size(),
                    label + ": [" + query + "] 의 결과 개수. 빠진 것 또는 더한 것이 있다");
            assertEquals(docIds(expected), docIds(got),
                    label + ": [" + query + "] 의 순위");
            for (int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i).score(), got.get(i).score(), DELTA,
                        label + ": [" + query + "] 의 " + i + "번째 점수");
            }
            // 상위 k 자르기가 전체 목록의 앞부분과 같아야 한다.
            int k = Math.min(5, expected.size());
            assertEquals(docIds(expected).subList(0, k), docIds(indexed.search(query, k)),
                    label + ": [" + query + "] 의 상위 " + k);

            hits += expected.size();
            if (!expected.isEmpty()) {
                answered++;
            }
        }
        assertTrue(answered > queries.size() / 10,
                label + ": 답이 있는 질의가 " + answered + "개뿐이다. 말뭉치가 너무 성기다");
        assertTrue(hits > 0, label + ": 결과가 하나도 없다");
    }

    private static List<Integer> inOrder(int count) {
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            out.add(i);
        }
        return out;
    }

    private static List<String> queries(long seed, int count, int vocabulary) {
        TestSupport.Dice dice = new TestSupport.Dice(seed);
        List<String> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            out.add(dice.query(vocabulary, 1 + dice.nextInt(3)));
        }
        return out;
    }

    @Test
    @DisplayName("문서 2000개, 질의 250개")
    @Timeout(180)
    void wideCorpus() {
        List<String> documents = new TestSupport.Dice(12345L).documents(2000, 200, 10, 30);
        crossCheck("넓은 말뭉치", documents, inOrder(documents.size()),
                queries(999L, 250, 200), new TfIdfScorer(), MergeOrder.SHORTEST_FIRST);
    }

    @Test
    @DisplayName("낱말이 12개뿐이라 포스팅 리스트가 아주 길다")
    @Timeout(180)
    void tinyVocabulary() {
        // 거의 모든 문서가 거의 모든 낱말을 가진다. 병합이 길어지고 df 가 N 에 가까워진다.
        List<String> documents = new TestSupport.Dice(777L).documents(1500, 12, 8, 20);
        crossCheck("좁은 어휘", documents, inOrder(documents.size()),
                queries(4242L, 200, 12), new TfIdfScorer(), MergeOrder.SHORTEST_FIRST);
    }

    @Test
    @DisplayName("문서 번호가 뒤죽박죽 들어와도 답이 같다")
    @Timeout(180)
    void shuffledDocIds() {
        // 포스팅 리스트를 append 만 하면 정렬이 깨지고, 깨진 리스트로 병합하면
        // 답이 조용히 줄어든다. 문서를 번호 순으로 넣는 테스트로는 절대 안 잡힌다.
        List<String> documents = new TestSupport.Dice(20260814L).documents(1200, 60, 8, 24);
        List<Integer> order = inOrder(documents.size());
        TestSupport.Dice dice = new TestSupport.Dice(5150L);
        for (int i = order.size() - 1; i > 0; i--) {
            int j = dice.nextInt(i + 1);
            Collections.swap(order, i, j);
        }
        crossCheck("뒤섞인 문서 번호", documents, order,
                queries(88L, 200, 60), new TfIdfScorer(), MergeOrder.SHORTEST_FIRST);
    }

    @Test
    @DisplayName("단순 빈도 채점기로도 같다")
    @Timeout(180)
    void withTermFrequencyScorer() {
        // TF-IDF 는 흔한 항에서 0 점이 많이 나와 동점이 흔하다.
        // 단순 빈도는 점수가 흩어지므로 순위 비교가 훨씬 빡빡해진다.
        List<String> documents = new TestSupport.Dice(31337L).documents(1500, 80, 10, 30);
        crossCheck("단순 빈도", documents, inOrder(documents.size()),
                queries(606L, 200, 80), new TermFrequencyScorer(), MergeOrder.SHORTEST_FIRST);
    }

    @Test
    @DisplayName("병합 순서를 바꿔도 답이 같다. 달라지는 것은 비교 횟수뿐이다")
    @Timeout(180)
    void mergeOrderDoesNotChangeAnswers() {
        List<String> documents = new TestSupport.Dice(4711L).documents(1200, 50, 10, 28);
        crossCheck("질의 순서 병합", documents, inOrder(documents.size()),
                queries(1123L, 200, 50), new TfIdfScorer(), MergeOrder.QUERY_ORDER);
    }

    @Test
    @DisplayName("문서가 하나뿐이면 모든 점수가 0 이다")
    void singleDocumentCorpus() {
        SearchEngine naive = new LinearScanEngine();
        SearchEngine indexed = new InvertedIndexEngine();
        naive.index(0, "고양이 고양이 담장");
        indexed.index(0, "고양이 고양이 담장");
        List<SearchResult> expected = naive.search("고양이", 10);
        List<SearchResult> got = indexed.search("고양이", 10);
        assertEquals(docIds(expected), docIds(got));
        assertEquals(0.0, got.get(0).score(), DELTA,
                "df 가 N 이면 log(1) 이다. 문서 하나짜리 색인은 순위를 못 매긴다");
    }
}
