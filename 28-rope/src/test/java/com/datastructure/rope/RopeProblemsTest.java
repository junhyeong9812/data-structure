package com.datastructure.rope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.datastructure.rope.RopeProblems.Lcp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("Rope 응용 문제")
class RopeProblemsTest {

    private static List<Edit> scatteredInserts(int count, int startLength) {
        List<Edit> edits = new ArrayList<>();
        int length = startLength;
        for (int i = 0; i < count; i++) {
            edits.add(new Edit.Insert((int) ((i * 7919L) % (length + 1)), "x"));
            length++;
        }
        return edits;
    }

    private static String applyToString(String doc, List<Edit> edits) {
        StringBuilder sb = new StringBuilder(doc);
        for (Edit e : edits) {
            if (e instanceof Edit.Insert i) {
                sb.insert(i.index(), i.text());
            } else if (e instanceof Edit.Delete d) {
                sb.delete(d.from(), d.to());
            }
        }
        return sb.toString();
    }

    @Nested
    @DisplayName("문제 1: 편집 목록 적용")
    class ApplyEdits {

        @Test
        @DisplayName("삽입과 삭제를 순서대로 적용한다")
        void appliesInOrder() {
            List<Edit> edits = List.of(
                    new Edit.Insert(0, "안녕"),
                    new Edit.Insert(2, "하세요"),
                    new Edit.Delete(0, 2),
                    new Edit.Insert(3, "!"));
            String expected = applyToString("", edits);
            assertEquals("하세요!", expected, "참조 계산이 맞는지부터 본다");
            assertEquals(expected, RopeProblems.applyEdits(new Rope("", 4), edits).toString());
            assertEquals(expected,
                    RopeProblems.applyEdits(new StringBuilderStore(""), edits).toString());
        }

        @Test
        @DisplayName("두 구현이 같은 문서를 만든다")
        void bothImplementationsAgree() {
            Random rnd = new Random(28L);
            String body = TestText.of(400);
            List<Edit> edits = new ArrayList<>();
            int length = body.length();
            for (int i = 0; i < 2000; i++) {
                if (rnd.nextBoolean() || length < 2) {
                    int at = rnd.nextInt(length + 1);
                    String s = "ABC".substring(0, 1 + rnd.nextInt(3));
                    edits.add(new Edit.Insert(at, s));
                    length += s.length();
                } else {
                    int from = rnd.nextInt(length);
                    int to = Math.min(length, from + 1 + rnd.nextInt(3));
                    edits.add(new Edit.Delete(from, to));
                    length -= to - from;
                }
            }
            String expected = applyToString(body, edits);
            assertEquals(expected, RopeProblems.applyEdits(new Rope(body, 8), edits).toString());
            assertEquals(expected,
                    RopeProblems.applyEdits(new StringBuilderStore(body), edits).toString());
        }

        @Test
        @DisplayName("빈 목록은 문서를 그대로 돌려준다")
        void emptyEditList() {
            CharSequenceStore doc = new Rope(TestText.of(50), 8);
            assertSame(doc, RopeProblems.applyEdits(doc, List.of()));
            assertThrows(IllegalArgumentException.class, () -> RopeProblems.applyEdits(null, List.of()));
            assertThrows(IllegalArgumentException.class,
                    () -> RopeProblems.applyEdits(new Rope("a"), null));
        }

        @Test
        @Timeout(30)
        @DisplayName("한계 측정: 같은 편집에 두 구현이 옮긴 글자 수")
        void copyCostOfTheSameEdits() {
            // **이 박스의 한계 측정이다.** 편집 1000번을 두 저장소에 똑같이 준다.
            // 답은 같고 옮긴 글자 수만 다르다.
            String body = TestText.of(4096);
            List<Edit> edits = scatteredInserts(1000, body.length());

            CharSequenceStore rope = RopeProblems.applyEdits(new Rope(body, 32), edits);
            CharSequenceStore sb = RopeProblems.applyEdits(new StringBuilderStore(body), edits);

            assertEquals(applyToString(body, edits), rope.toString());
            assertEquals(rope.toString(), sb.toString());
            assertEquals(12_502, rope.charsCopiedTotal());
            assertEquals(4_595_500, sb.charsCopiedTotal());
            assertEquals(367, sb.charsCopiedTotal() / rope.charsCopiedTotal(),
                    "문서가 클수록 이 배수가 커진다. 로프 쪽은 문서 크기와 상관이 없다");
        }
    }

    @Nested
    @DisplayName("문제 2: 공통 접두사 길이")
    class LongestCommonPrefix {

        @Test
        @DisplayName("길이가 맞는다")
        void basicAnswers() {
            assertEquals(3, RopeProblems.longestCommonPrefixLength(
                    new Rope("abcXYZ", 2), new Rope("abcQQQ", 2)));
            assertEquals(0, RopeProblems.longestCommonPrefixLength(
                    new Rope("abc", 2), new Rope("zbc", 2)));
            assertEquals(3, RopeProblems.longestCommonPrefixLength(
                    new Rope("abc", 2), new Rope("abcdef", 2)));
            assertEquals(0, RopeProblems.longestCommonPrefixLength(
                    new Rope("", 2), new Rope("abc", 2)));
            assertEquals(0, RopeProblems.longestCommonPrefixLength(
                    new Rope("", 2), new Rope("", 2)));
            assertEquals(6, RopeProblems.longestCommonPrefixLength(
                    new StringBuilderStore("abcdef"), new StringBuilderStore("abcdef")));
        }

        @Test
        @DisplayName("잎 경계가 어긋나도 맞는다")
        void leafBoundariesDoNotMatter() {
            // 같은 내용을 다른 잎 크기로 담으면 조각이 어긋난다. 그래도 답은 같아야 한다.
            String a = TestText.of(300) + "1" + TestText.of(50);
            String b = TestText.of(300) + "2" + TestText.of(50);
            for (int leafA : new int[]{1, 7, 32, 500}) {
                for (int leafB : new int[]{1, 7, 32, 500}) {
                    assertEquals(300, RopeProblems.longestCommonPrefixLength(
                            new Rope(a, leafA), new Rope(b, leafB)), leafA + " 대 " + leafB);
                }
            }
        }

        @Test
        @DisplayName("나이브와 답이 같다")
        void matchesNaive() {
            Random rnd = new Random(7L);
            for (int t = 0; t < 300; t++) {
                int n = 1 + rnd.nextInt(200);
                StringBuilder a = new StringBuilder();
                for (int i = 0; i < n; i++) {
                    a.append((char) ('a' + rnd.nextInt(3)));
                }
                StringBuilder b = new StringBuilder(a);
                if (rnd.nextBoolean() && n > 1) {
                    int at = rnd.nextInt(n);
                    b.setCharAt(at, (char) ('a' + rnd.nextInt(3)));
                }
                Rope ra = new Rope(a.toString(), 1 + rnd.nextInt(16));
                Rope rb = new Rope(b.toString(), 1 + rnd.nextInt(16));
                assertEquals(RopeProblems.naiveLongestCommonPrefix(ra, rb).length(),
                        RopeProblems.longestCommonPrefix(ra, rb).length(), "시행 " + t);
            }
        }

        @Test
        @DisplayName("한계 측정: 공유한 부분트리는 안 본다")
        void sharedSubtreesAreSkipped() {
            // **참조가 같으면 내용도 같다.** 불변이라 그 추론이 성립한다.
            // 같은 base 에서 갈라진 두 문서는 앞부분이 통째로 같은 객체다.
            String body = TestText.of(4096);
            Rope base = new Rope(body, 32);

            Rope tailA = base.insert(base.length(), "1");
            Rope tailB = base.insert(base.length(), "2");
            Lcp tail = RopeProblems.longestCommonPrefix(tailA, tailB);
            assertEquals(4096, tail.length());
            assertEquals(33, tail.comparedChars(), "4096 글자 중 33 개만 봤다");
            assertEquals(4097, RopeProblems.naiveLongestCommonPrefix(tailA, tailB).comparedChars(),
                    "나이브는 전부 본다. 124배다");

            Rope midA = base.insert(2048, "X");
            Rope midB = base.insert(2048, "Y");
            Lcp mid = RopeProblems.longestCommonPrefix(midA, midB);
            assertEquals(2048, mid.length());
            assertEquals(33, mid.comparedChars());
            assertEquals(2049, RopeProblems.naiveLongestCommonPrefix(midA, midB).comparedChars());

            Rope headA = base.insert(0, "X");
            Rope headB = base.insert(0, "Y");
            Lcp head = RopeProblems.longestCommonPrefix(headA, headB);
            assertEquals(0, head.length());
            assertEquals(1, head.comparedChars(), "첫 글자에서 갈린다");
        }

        @Test
        @DisplayName("공유가 없으면 건너뛸 것도 없다")
        void withoutSharingItIsNaive() {
            // 정직하게 적는다. 같은 내용이라도 따로 만든 두 로프는 노드가 남남이다.
            // 그때는 나이브와 똑같이 전부 비교한다. 이 최적화는 편집 이력이 있을 때만 듣는다.
            String body = TestText.of(4096);
            Rope a = new Rope(body + "1", 32);
            Rope b = new Rope(body + "2", 32);
            Lcp lcp = RopeProblems.longestCommonPrefix(a, b);
            assertEquals(4096, lcp.length());
            assertEquals(4097, lcp.comparedChars(), "한 글자도 못 건너뛴다");
            assertEquals(RopeProblems.naiveLongestCommonPrefix(a, b).comparedChars(),
                    lcp.comparedChars());
        }

        @Test
        @DisplayName("기준선에는 건너뛸 구조가 없다")
        void baselineHasNoStructure() {
            String body = TestText.of(1000);
            CharSequenceStore a = new StringBuilderStore(body).insert(1000, "1");
            CharSequenceStore b = new StringBuilderStore(body).insert(1000, "2");
            Lcp lcp = RopeProblems.longestCommonPrefix(a, b);
            assertEquals(1000, lcp.length());
            assertEquals(1001, lcp.comparedChars(),
                    "배열은 통째로 복사해 만들었으므로 공유하는 것이 없다");
            assertTrue(lcp.comparedChars() > 33,
                    "같은 문서 쌍인데 로프는 33 글자만 봤다");
        }

        @Test
        @DisplayName("null 은 거부한다")
        void rejectsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> RopeProblems.longestCommonPrefix(null, new Rope("a")));
            assertThrows(IllegalArgumentException.class,
                    () -> RopeProblems.longestCommonPrefix(new Rope("a"), null));
        }
    }
}
