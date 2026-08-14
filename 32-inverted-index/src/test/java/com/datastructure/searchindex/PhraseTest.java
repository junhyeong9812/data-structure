package com.datastructure.searchindex;

import static com.datastructure.searchindex.TestSupport.docIds;
import static com.datastructure.searchindex.TestSupport.indexAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * 구문 검색. 위치 목록을 담아둔 이유가 전부 여기에 있다.
 *
 * AND 질의와 구문 검색은 다르다. AND 는 "그 말들이 들어 있나"를 묻고
 * 구문 검색은 "그 말들이 그 순서로 붙어 있나"를 묻는다.
 * 포스팅에 문서 번호와 빈도만 담으면 앞의 질문까지만 답할 수 있다.
 *
 * 이 파일의 첫 테스트가 그 차이를 못 박는다. 위치를 안 쓰고 AND 결과를 그대로 주면
 * 답이 늘어나는 쪽으로만 틀리고 예외는 안 난다. 그래서 개수를 직접 견준다.
 */
@DisplayName("구문 검색과 위치")
class PhraseTest {

    private static SearchEngine cats(SearchEngine engine) {
        engine.index(0, "검은 고양이 가 담장 위 를 걷는다");
        engine.index(1, "고양이 가 검은 담장 위 에 앉았다");
        engine.index(2, "검은 개 가 담장 을 넘었다");
        return engine;
    }

    @Nested
    @DisplayName("AND 로는 못 하는 질의")
    class NotJustAnd {

        @Test
        @DisplayName("AND 는 답이 둘인데 구문은 하나다")
        void phraseIsStrictlyNarrower() {
            SearchEngine engine = cats(new InvertedIndexEngine());
            List<Integer> and = docIds(engine.search("검은 고양이", 10));
            List<Integer> phrase = engine.searchPhrase("검은 고양이");

            assertEquals(List.of(0, 1), and, "두 문서 다 검은과 고양이를 가지고 있다");
            assertEquals(List.of(0), phrase, "붙어 있는 것은 0번뿐이다");
            assertNotEquals(and, phrase,
                    "위치를 안 보고 AND 결과를 그대로 주면 이 줄에서 걸린다");
            assertTrue(and.containsAll(phrase), "구문 검색의 답은 늘 AND 답의 부분집합이다");
        }

        @Test
        @DisplayName("순서를 뒤집으면 답이 사라진다")
        void reversedPhraseFindsNothing() {
            SearchEngine engine = cats(new InvertedIndexEngine());
            assertEquals(List.of(0), engine.searchPhrase("검은 고양이"));
            assertEquals(List.of(), engine.searchPhrase("고양이 검은"),
                    "AND 로 보면 둘 다 답이다. 순서를 아는 것은 위치뿐이다");
        }

        @Test
        @DisplayName("세 낱말은 이어서 확인한다")
        void threeTermPhrase() {
            SearchEngine engine = cats(new InvertedIndexEngine());
            assertEquals(List.of(0), engine.searchPhrase("검은 고양이 가"));
            assertEquals(List.of(0, 1), engine.searchPhrase("담장 위"));
            assertEquals(List.of(), engine.searchPhrase("검은 고양이 담장"),
                    "앞의 둘만 보고 답하면 여기서 걸린다");
        }

        @Test
        @DisplayName("문서 끝에 걸친 구문")
        void phraseAtTheEnd() {
            SearchEngine engine = new InvertedIndexEngine();
            engine.index(0, "가 나 다");
            engine.index(1, "가 나");
            assertEquals(List.of(0, 1), engine.searchPhrase("가 나"));
            assertEquals(List.of(0), engine.searchPhrase("나 다"));
            assertEquals(List.of(), engine.searchPhrase("다 가"),
                    "문서 끝을 넘어가서 이어 붙이면 안 된다");
        }

        @Test
        @DisplayName("겹쳐 나오는 반복")
        void overlappingRepeats() {
            SearchEngine engine = new InvertedIndexEngine();
            engine.index(0, "가 가 가");
            engine.index(1, "가 나 가");
            assertEquals(List.of(0), engine.searchPhrase("가 가"));
            assertEquals(List.of(0), engine.searchPhrase("가 가 가"));
            assertEquals(List.of(), engine.searchPhrase("가 가 가 가"),
                    "세 번뿐인데 네 번 붙은 자리를 찾으면 안 된다");
        }
    }

    @Nested
    @DisplayName("위치가 색인에 실제로 들어 있나")
    class PositionsInTheIndex {

        @Test
        @DisplayName("포스팅의 위치가 분석 결과의 인덱스와 같다")
        void positionsMatchAnalyzedOrder() {
            InvertedIndexEngine engine = (InvertedIndexEngine) cats(new InvertedIndexEngine());
            assertEquals(List.of(0), engine.postings("검은").get(0).positions(), "0번 문서");
            assertEquals(List.of(2), engine.postings("검은").get(1).positions(), "1번 문서");
            assertEquals(List.of(0), engine.postings("검은").get(2).positions(), "2번 문서");
            assertEquals(List.of(1), engine.postings("고양이").get(0).positions());
            assertEquals(List.of(0), engine.postings("고양이").get(1).positions());
        }

        @Test
        @DisplayName("같은 낱말이 여러 번 나오면 위치가 여러 개다")
        void repeatedTermKeepsEveryPosition() {
            InvertedIndexEngine engine = new InvertedIndexEngine();
            engine.index(0, "가 나 가 다 가");
            Posting posting = engine.postings("가").get(0);
            assertEquals(List.of(0, 2, 4), posting.positions());
            assertEquals(3, posting.frequency(), "빈도는 위치 개수다");
        }
    }

    @Nested
    @DisplayName("전수 조사와의 무작위 대조")
    class AgainstLinearScan {

        @Test
        @DisplayName("말뭉치 1200개, 구문 400개")
        @Timeout(180)
        void randomPhrasesAgree() {
            // 전수 조사는 원문을 다시 분석해 토큰 열을 통째로 비교한다.
            // 역색인은 위치 산술로 답한다. 접근이 전혀 다르므로 대조가 뜻이 있다.
            TestSupport.Dice dice = new TestSupport.Dice(90210L);
            List<String> documents = dice.documents(1200, 40, 6, 20);

            SearchEngine naive = new LinearScanEngine();
            SearchEngine indexed = new InvertedIndexEngine();
            indexAll(naive, documents);
            indexAll(indexed, documents);

            List<String> phrases = new ArrayList<>();
            TestSupport.Dice picker = new TestSupport.Dice(31415L);
            for (int q = 0; q < 300; q++) {
                // 절반은 실제 문서에서 잘라낸 구문이라 반드시 맞는 답이 있다.
                List<String> tokens = new StandardAnalyzer()
                        .analyze(documents.get(picker.nextInt(documents.size())));
                int length = 2 + picker.nextInt(2);
                if (tokens.size() <= length) {
                    continue;
                }
                int start = picker.nextInt(tokens.size() - length);
                phrases.add(String.join(" ", tokens.subList(start, start + length)));
            }
            for (int q = 0; q < 100; q++) {
                phrases.add(picker.query(40, 2 + picker.nextInt(2)));
            }

            int nonEmpty = 0;
            for (String phrase : phrases) {
                List<Integer> expected = naive.searchPhrase(phrase);
                List<Integer> got = indexed.searchPhrase(phrase);
                assertEquals(expected, got, "구문 [" + phrase + "] 의 답이 다르다");
                if (!expected.isEmpty()) {
                    nonEmpty++;
                }
                // 구문 답은 언제나 AND 답의 부분집합이다. 위치를 무시하면 여기가 무너진다.
                assertTrue(docIds(indexed.search(phrase, documents.size())).containsAll(got),
                        "구문 [" + phrase + "] 의 답이 AND 답에 없다");
            }
            assertTrue(nonEmpty > 100,
                    "답이 있는 구문이 " + nonEmpty + "개뿐이면 대조가 시시하다");
        }

        @Test
        @DisplayName("구문이 AND 보다 확실히 좁다")
        @Timeout(120)
        void phraseNarrowsTheAndResult() {
            TestSupport.Dice dice = new TestSupport.Dice(2718L);
            List<String> documents = dice.documents(800, 25, 8, 24);
            SearchEngine indexed = new InvertedIndexEngine();
            indexAll(indexed, documents);

            long andTotal = 0;
            long phraseTotal = 0;
            TestSupport.Dice picker = new TestSupport.Dice(1618L);
            for (int q = 0; q < 200; q++) {
                String phrase = picker.query(25, 2);
                andTotal += indexed.search(phrase, documents.size()).size();
                phraseTotal += indexed.searchPhrase(phrase).size();
            }
            assertTrue(phraseTotal > 0, "구문 답이 하나도 없으면 측정이 시시하다");
            assertTrue(andTotal > phraseTotal * 5,
                    "AND 가 " + andTotal + "건, 구문이 " + phraseTotal + "건이다. "
                            + "위치를 안 쓰고 AND 결과를 그대로 주면 두 값이 같아진다");
        }
    }
}
