package com.datastructure.searchindex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 색인에 들어가기 전의 세 부품. 토크나이저, 분석기, 포스팅.
 *
 * 이 셋이 틀리면 그 위의 모든 것이 조용히 틀린다.
 * 검색은 예외를 안 던지고 그냥 0건을 준다. 그래서 여기를 따로 못 박는다.
 */
@DisplayName("토큰화, 분석, 포스팅")
class AnalysisTest {

    @Nested
    @DisplayName("SimpleTokenizer")
    class Tokenizing {

        private final Tokenizer tokenizer = new SimpleTokenizer();

        @Test
        @DisplayName("공백과 문장부호로 자른다")
        void splitsOnPunctuation() {
            assertEquals(List.of("hello", "world"), tokenizer.tokenize("hello, world!"));
            assertEquals(List.of("a", "b", "c"), tokenizer.tokenize("a...b---c"));
            assertEquals(List.of("검은", "고양이"), tokenizer.tokenize("검은 고양이"));
        }

        @Test
        @DisplayName("마지막 토큰이 경계 없이 끝나도 살아남는다")
        void keepsTheLastToken() {
            // 흘려보내는 줄을 빠뜨리면 여기서만 잡힌다. 마침표로 끝나는 문장에서는 안 드러난다.
            assertEquals(List.of("검은", "고양이"), tokenizer.tokenize("검은 고양이"));
            assertEquals(List.of("cat"), tokenizer.tokenize("cat"));
            assertEquals(List.of("검은", "고양이"), tokenizer.tokenize("검은 고양이."));
        }

        @Test
        @DisplayName("숫자는 남기고 밑줄은 자른다")
        void keepsDigits() {
            assertEquals(List.of("t007", "2026"), tokenizer.tokenize("t007 2026"));
            assertEquals(List.of("a", "b"), tokenizer.tokenize("a_b"));
        }

        @Test
        @DisplayName("빈 문자열과 부호뿐인 문자열은 빈 목록")
        void emptyInputs() {
            assertEquals(List.of(), tokenizer.tokenize(""));
            assertEquals(List.of(), tokenizer.tokenize("   ...!!!  "));
            assertThrows(IllegalArgumentException.class, () -> tokenizer.tokenize(null));
        }
    }

    @Nested
    @DisplayName("StandardAnalyzer")
    class Analyzing {

        @Test
        @DisplayName("소문자로 바꾼 뒤에 불용어를 본다")
        void lowercasesBeforeRemovingStopwords() {
            // 순서를 뒤집으면 The 가 살아남아 the 와 다른 항이 된다.
            Analyzer analyzer = new StandardAnalyzer(Set.of("the"));
            assertEquals(List.of("black", "cat"), analyzer.analyze("The Black CAT"));
            assertEquals(List.of("black", "cat"), analyzer.analyze("the black cat"));
        }

        @Test
        @DisplayName("불용어를 지우면 뒤가 당겨진다")
        void positionsShiftAfterRemoval() {
            Analyzer analyzer = new StandardAnalyzer(Set.of("the", "of"));
            assertEquals(List.of("king", "france"), analyzer.analyze("the king of the France"),
                    "위치 0 과 1 이 된다. 원문에서 떨어져 있던 둘이 붙는다");
        }

        @Test
        @DisplayName("불용어 집합이 비면 아무것도 안 버린다")
        void emptyStopwordsKeepEverything() {
            Analyzer analyzer = new StandardAnalyzer(Set.of());
            assertEquals(List.of("the", "and", "the"), analyzer.analyze("The and THE"));
        }

        @Test
        @DisplayName("기본 불용어에는 영어 기능어가 들어 있다")
        void defaultStopwords() {
            assertTrue(StandardAnalyzer.DEFAULT_STOPWORDS.contains("the"));
            assertTrue(StandardAnalyzer.DEFAULT_STOPWORDS.contains("and"));
            assertEquals(List.of("black", "cat"),
                    new StandardAnalyzer().analyze("The black and the cat"));
        }
    }

    @Nested
    @DisplayName("Posting")
    class Postings {

        @Test
        @DisplayName("빈도는 위치 개수다")
        void frequencyIsPositionCount() {
            Posting posting = new Posting(3);
            assertEquals(0, posting.frequency());
            posting.addPosition(0);
            posting.addPosition(4);
            posting.addPosition(9);
            assertEquals(3, posting.frequency(), "두 값을 따로 들면 어긋날 자리가 생긴다");
            assertEquals(List.of(0, 4, 9), posting.positions());
            assertEquals(3, posting.docId());
        }

        @Test
        @DisplayName("위치는 오름차순이어야 한다")
        void positionsMustAscend() {
            Posting posting = new Posting(1);
            posting.addPosition(5);
            assertThrows(IllegalArgumentException.class, () -> posting.addPosition(5),
                    "같은 자리에 두 번 나올 수 없다");
            assertThrows(IllegalArgumentException.class, () -> posting.addPosition(2),
                    "구문 검색이 이 목록의 오름차순을 그대로 믿는다");
            assertThrows(IllegalArgumentException.class, () -> posting.addPosition(-1));
        }

        @Test
        @DisplayName("위치 목록은 밖에서 못 고친다")
        void positionsAreReadOnly() {
            Posting posting = new Posting(1);
            posting.addPosition(0);
            assertThrows(UnsupportedOperationException.class, () -> posting.positions().add(7));
        }

        @Test
        @DisplayName("같은 문서 번호라도 위치가 다르면 다른 포스팅이다")
        void equality() {
            Posting a = new Posting(2);
            a.addPosition(1);
            Posting b = new Posting(2);
            b.addPosition(1);
            Posting c = new Posting(2);
            c.addPosition(3);
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
            assertNotEquals(a, c);
        }
    }
}
