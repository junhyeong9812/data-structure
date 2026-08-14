package com.datastructure.searchindex;

import static com.datastructure.searchindex.TestSupport.indexAll;
import static com.datastructure.searchindex.TestSupport.word;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 색인이 무엇을 사고 무엇을 파는지 숫자로 적는다.
 *
 * CrossCheckTest 는 두 엔진의 답이 같다는 것만 본다. 답이 같으면 색인은 순전히 손해다.
 * 무엇을 얻었는지는 여기서만 드러난다. 방문 수, 비교 횟수, 그리고 그 대가인 색인 크기다.
 *
 * 기댓값을 전부 못 박는다. 구현이 조금이라도 다르게 훑으면 답이 아니라 이 수가 먼저 어긋난다.
 */
@DisplayName("색인의 값과 대가 측정")
class MeasurementTest {

    private static final int DOCS = 2_000;
    private static final int VOCABULARY = 200;

    private static final List<String> CORPUS =
            new TestSupport.Dice(31_337L).documents(DOCS, VOCABULARY, 8, 40);

    private static InvertedIndexEngine indexed() {
        InvertedIndexEngine engine = new InvertedIndexEngine();
        indexAll(engine, CORPUS);
        return engine;
    }

    private static LinearScanEngine naive() {
        LinearScanEngine engine = new LinearScanEngine();
        indexAll(engine, CORPUS);
        return engine;
    }

    @Nested
    @DisplayName("측정 1: 질의 하나에 몇 문서를 보는가")
    class VisitedDocuments {

        @Test
        @DisplayName("전수 조사는 늘 문서 수만큼 본다. 질의가 무엇이든 상관없다")
        void naiveAlwaysVisitsEveryDocument() {
            LinearScanEngine engine = naive();

            engine.search(word(0), 10);
            long common = engine.visitedDocs();
            engine.search(word(199), 10);
            long rare = engine.visitedDocs();
            engine.search(word(198) + " " + word(199), 10);
            long both = engine.visitedDocs();

            System.out.printf("  전수 조사 (문서 %,d개)%n", DOCS);
            System.out.printf("    제일 흔한 낱말   %,7d%n", common);
            System.out.printf("    제일 드문 낱말   %,7d%n", rare);
            System.out.printf("    드문 낱말 둘     %,7d%n", both);

            assertEquals(DOCS, common, "흔한 낱말");
            assertEquals(DOCS, rare, "드문 낱말");
            assertEquals(DOCS, both, "드문 낱말 둘");
        }

        @Test
        @DisplayName("역색인은 드문 낱말일수록 적게 본다")
        void invertedIndexVisitsOnlyPostings() {
            InvertedIndexEngine engine = indexed();

            engine.search(word(0), 10);
            long common = engine.visitedDocs();
            engine.search(word(199), 10);
            long rare = engine.visitedDocs();
            engine.search(word(198) + " " + word(199), 10);
            long both = engine.visitedDocs();

            System.out.printf("  역색인%n");
            System.out.printf("    제일 흔한 낱말   %,7d   (df %,d)%n",
                    common, engine.postings(word(0)).size());
            System.out.printf("    제일 드문 낱말   %,7d   (df %,d)%n",
                    rare, engine.postings(word(199)).size());
            System.out.printf("    드문 낱말 둘     %,7d%n", both);

            assertEquals(346, common, "흔한 낱말");
            assertEquals(2, rare, "드문 낱말");
            assertEquals(8, both, "드문 낱말 둘");

            // 2 대 2,000 이다. 색인이 파는 것이 이것이다.
            assertTrue(rare * 100 < DOCS, "드문 낱말은 전수 조사의 100분의 1도 안 본다: " + rare);
        }

        @Test
        @DisplayName("한계: 모든 문서에 있는 항을 물으면 역색인도 전부 본다")
        void commonTermIsNoBetterThanScanning() {
            List<String> everywhere = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                everywhere.add("alpha beta " + word(i % 20));
            }
            InvertedIndexEngine engine = new InvertedIndexEngine();
            indexAll(engine, everywhere);

            engine.search("alpha", 10);
            long visited = engine.visitedDocs();

            System.out.printf("  모든 문서에 있는 항을 물으면 %,d개 문서에 방문 %,d%n",
                    everywhere.size(), visited);

            assertEquals(500, engine.postings("alpha").size(), "df 가 문서 수와 같다");
            assertEquals(500, visited, "포스팅 리스트를 통째로 훑는다");
            // 25번의 "모든 점이 같은 거리", 30번의 "결과가 크면 진다" 와 같은 자리다.
            // 색인은 후보를 줄여야 이기는데, 줄일 것이 없으면 줄일 수 없다.
        }
    }

    @Nested
    @DisplayName("측정 2: 병합 순서가 비교 횟수를 정한다")
    class MergeOrderCost {

        @Test
        @DisplayName("짧은 리스트부터 병합하면 비교가 줄어든다. 답은 그대로다")
        void shortestFirstDoesLessWork() {
            String query = word(0) + " " + word(1) + " " + word(199);

            InvertedIndexEngine shortest = new InvertedIndexEngine(
                    new StandardAnalyzer(), new TfIdfScorer(), MergeOrder.SHORTEST_FIRST);
            InvertedIndexEngine given = new InvertedIndexEngine(
                    new StandardAnalyzer(), new TfIdfScorer(), MergeOrder.QUERY_ORDER);
            indexAll(shortest, CORPUS);
            indexAll(given, CORPUS);

            List<SearchResult> a = shortest.search(query, 10);
            List<SearchResult> b = given.search(query, 10);

            System.out.printf("  질의: 흔한 말 둘 + 드문 말 하나%n");
            System.out.printf("    짧은 것부터   비교 %,7d   방문 %,7d%n",
                    shortest.comparisons(), shortest.visitedDocs());
            System.out.printf("    준 순서대로   비교 %,7d   방문 %,7d%n",
                    given.comparisons(), given.visitedDocs());

            assertEquals(TestSupport.docIds(a), TestSupport.docIds(b), "답은 같아야 한다");
            assertEquals(231, shortest.comparisons(), "짧은 것부터");
            assertEquals(683, given.comparisons(), "준 순서대로");
            assertEquals(233, shortest.visitedDocs(), "짧은 것부터 방문");
            assertEquals(1_082, given.visitedDocs(), "준 순서대로 방문");

            // 답이 같으므로 CrossCheckTest 로는 이 선택을 절대 잡을 수 없다. 이 수만 잡는다.
            assertTrue(given.comparisons() > 2 * shortest.comparisons(),
                    given.comparisons() + " 대 " + shortest.comparisons());
        }
    }

    @Nested
    @DisplayName("측정 3: 색인의 크기가 대가다")
    class IndexSize {

        @Test
        @DisplayName("포스팅 수와 위치 수를 센다. 위치가 색인의 대부분이다")
        void positionsCostMoreThanPostings() {
            InvertedIndexEngine engine = indexed();

            long terms = engine.termCount();
            long postings = engine.postingCount();
            long positions = engine.positionCount();

            System.out.printf("  문서 %,d개, 항 %,d종%n", DOCS, terms);
            System.out.printf("    포스팅 (항, 문서) 쌍   %,9d%n", postings);
            System.out.printf("    위치 정수              %,9d%n", positions);
            System.out.printf("    위치 대 포스팅          %.2f 배%n", positions / (double) postings);

            assertEquals(200, terms, "낱말 200종. 불용어 the, and 는 안 들어간다");
            assertEquals(37_348, postings, "포스팅 수");
            assertEquals(40_158, positions, "위치 정수 개수");
        }

        @Test
        @DisplayName("위치 정수의 개수는 원문의 항 개수와 정확히 같다")
        void everyTokenBecomesOnePosition() {
            InvertedIndexEngine engine = indexed();

            long tokens = 0;
            Analyzer analyzer = new StandardAnalyzer();
            for (String text : CORPUS) {
                tokens += analyzer.analyze(text).size();
            }

            System.out.printf("  원문의 항 %,d개, 색인 안의 위치 %,d개%n",
                    tokens, engine.positionCount());

            // 이 등식이 깨지면 색인이 무엇을 흘렸거나 무엇을 두 번 담은 것이다.
            // 답이 맞는 동안에도 깨질 수 있다. 그래서 따로 센다.
            assertEquals(tokens, engine.positionCount(), "원문의 항 개수와 같아야 한다");
        }

        @Test
        @DisplayName("전수 조사는 색인이 0 이다. 대신 질의마다 전부 다시 분석한다")
        void naiveStoresNothing() {
            LinearScanEngine engine = naive();

            long before = System.nanoTime();
            engine.termCount();
            long naiveNanos = System.nanoTime() - before;

            InvertedIndexEngine fast = indexed();
            before = System.nanoTime();
            fast.termCount();
            long indexedNanos = System.nanoTime() - before;

            System.out.printf("  termCount 한 번: 전수 조사 %,d ns, 역색인 %,d ns%n",
                    naiveNanos, indexedNanos);

            // 값이 같다는 것만 못 박는다. 시간은 기계마다 다르므로 재기만 하고 판정하지 않는다.
            assertEquals(engine.termCount(), fast.termCount(), "항 개수는 같다");
        }
    }

    @Nested
    @DisplayName("측정 4: 구문 검색은 좁힌 뒤에 본다")
    class PhraseCost {

        @Test
        @DisplayName("교집합으로 먼저 좁힌다. 안 좁히면 전수 조사와 같아진다")
        void phraseNarrowsBeforeCheckingPositions() {
            InvertedIndexEngine engine = indexed();
            LinearScanEngine scan = naive();

            // 0번 문서에 실제로 있는 구문을 쓴다. 결과가 0 건이면 아무것도 안 재는 측정이 된다.
            List<String> first = new StandardAnalyzer().analyze(CORPUS.get(0));
            String phrase = first.get(0) + " " + first.get(1);

            List<Integer> a = engine.searchPhrase(phrase);
            List<Integer> b = scan.searchPhrase(phrase);

            System.out.printf("  구문 \"%s\"%n", phrase);
            System.out.printf("    역색인    방문 %,7d   결과 %d%n", engine.visitedDocs(), a.size());
            System.out.printf("    전수 조사  방문 %,7d   결과 %d%n", scan.visitedDocs(), b.size());

            assertEquals(b, a, "답은 같아야 한다");
            assertTrue(a.contains(0), "0번 문서에서 뽑은 구문이다: " + phrase);
            assertEquals(DOCS, scan.visitedDocs(), "전수 조사는 늘 전부 본다");
            assertTrue(engine.visitedDocs() < DOCS / 2,
                    "역색인은 교집합으로 먼저 좁힌다: " + engine.visitedDocs());
        }
    }
}
