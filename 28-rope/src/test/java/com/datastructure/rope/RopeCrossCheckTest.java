package com.datastructure.rope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.datastructure.rope.CharSequenceStore.Split;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * String 을 참조 구현으로 삼은 무작위 대조. 이 박스에서 버그를 가장 많이 잡는 테스트다.
 *
 * 로프의 버그는 대부분 "가끔만" 틀린다. weight 비교를 &lt;= 로 쓰면 경계에서만,
 * split 의 오른쪽 재조립을 빠뜨리면 잘린 조각이 어디에 붙느냐에 따라서만 갈린다.
 * 그래서 매 스텝마다 통째로 대조한다.
 *
 * 잎 크기를 1, 8, 32 로 바꿔 가며 돈다. 1 이면 글자마다 노드라 트리가 깊고,
 * 32 면 대부분의 편집이 잎 하나 안에서 끝난다. 두 극단에서 답이 같아야 한다.
 */
@DisplayName("Rope 무작위 대조")
class RopeCrossCheckTest {

    @Nested
    @DisplayName("String 과 2만 스텝")
    class AgainstString {

        @Test
        @DisplayName("삽입, 삭제, 쪼개기를 섞어 돌린다")
        void randomEdits() {
            for (int leafMax : new int[]{1, 8, 32}) {
                Random rnd = new Random(2806L + leafMax);
                StringBuilder ref = new StringBuilder(TestText.of(300));
                Rope rope = new Rope(ref.toString(), leafMax);

                for (int step = 0; step < 20_000; step++) {
                    int n = ref.length();
                    int op = rnd.nextInt(100);
                    String where = "잎 " + leafMax + " step " + step;
                    if (op < 50) {
                        int i = rnd.nextInt(n + 1);
                        String s = "xyz".substring(0, 1 + rnd.nextInt(3));
                        ref.insert(i, s);
                        rope = rope.insert(i, s);
                    } else if (op < 90) {
                        int from = rnd.nextInt(n + 1);
                        int to = Math.min(n, from + rnd.nextInt(5));
                        ref.delete(from, to);
                        rope = rope.delete(from, to);
                    } else if (op < 96) {
                        int i = rnd.nextInt(n + 1);
                        Split parts = rope.split(i);
                        assertEquals(ref.substring(0, i), parts.left().toString(), where + " 왼쪽");
                        assertEquals(ref.substring(i), parts.right().toString(), where + " 오른쪽");
                        rope = ((Rope) parts.left()).concat(parts.right());
                    } else {
                        rope = rope.rebalance();
                    }
                    assertEquals(ref.toString(), rope.toString(), where);
                    assertEquals(ref.length(), rope.length(), where);
                }

                String finalText = ref.toString();
                for (int i = 0; i < finalText.length(); i += 3) {
                    assertEquals(finalText.charAt(i), rope.charAt(i), "잎 " + leafMax + " charAt " + i);
                }
                for (int t = 0; t < 300; t++) {
                    int a = rnd.nextInt(finalText.length() + 1);
                    int b = rnd.nextInt(finalText.length() + 1);
                    int from = Math.min(a, b);
                    int to = Math.max(a, b);
                    assertEquals(finalText.substring(from, to), rope.substring(from, to),
                            "잎 " + leafMax + " substring [" + from + ", " + to + ")");
                }
            }
        }

        @Test
        @DisplayName("기준선도 같은 답을 낸다")
        void baselineAgrees() {
            Random rnd = new Random(99L);
            StringBuilder ref = new StringBuilder(TestText.of(200));
            CharSequenceStore sb = new StringBuilderStore(ref.toString());
            CharSequenceStore rope = new Rope(ref.toString(), 4);

            for (int step = 0; step < 20_000; step++) {
                int n = ref.length();
                int op = rnd.nextInt(100);
                if (op < 50) {
                    int i = rnd.nextInt(n + 1);
                    String s = "가나다".substring(0, 1 + rnd.nextInt(3));
                    ref.insert(i, s);
                    sb = sb.insert(i, s);
                    rope = rope.insert(i, s);
                } else {
                    int from = rnd.nextInt(n + 1);
                    int to = Math.min(n, from + rnd.nextInt(4));
                    ref.delete(from, to);
                    sb = sb.delete(from, to);
                    rope = rope.delete(from, to);
                }
                assertEquals(ref.toString(), sb.toString(), "기준선 step " + step);
                assertEquals(ref.toString(), rope.toString(), "로프 step " + step);
            }
            assertTrue(rope.charsCopiedTotal() < sb.charsCopiedTotal(),
                    "답은 같고 옮긴 글자 수만 다르다: 로프 " + rope.charsCopiedTotal()
                            + " 대 기준선 " + sb.charsCopiedTotal());
        }

        @Test
        @DisplayName("잎 하나짜리 문서와 빈 문서에서도 같다")
        void degenerateSizes() {
            Random rnd = new Random(5L);
            for (String start : new String[]{"", "a", "ab"}) {
                StringBuilder ref = new StringBuilder(start);
                Rope rope = new Rope(start, 2);
                for (int step = 0; step < 2000; step++) {
                    int n = ref.length();
                    if (rnd.nextBoolean()) {
                        int i = rnd.nextInt(n + 1);
                        ref.insert(i, "q");
                        rope = rope.insert(i, "q");
                    } else if (n > 0) {
                        int i = rnd.nextInt(n);
                        ref.delete(i, i + 1);
                        rope = rope.delete(i, i + 1);
                    }
                    assertEquals(ref.toString(), rope.toString(), "시작 [" + start + "] step " + step);
                }
            }
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(20)
        @DisplayName("10만 자 문서에 2만 번 편집")
        void manyEditsOnALargeDocument() {
            // 기준선으로 같은 일을 하면 글자 20억 개를 옮겨야 한다.
            // 여기서 재는 것은 "빠르다" 가 아니라 "편집 비용이 문서 크기와 무관하다" 이다.
            Random rnd = new Random(1234L);
            Rope rope = new Rope(TestText.of(100_000), 32);
            for (int i = 0; i < 20_000; i++) {
                int at = rnd.nextInt(rope.length() + 1);
                rope = rope.insert(at, "z");
            }
            assertEquals(120_000, rope.length());
            assertTrue(rope.charsCopiedTotal() < 1_000_000,
                    "옮긴 글자 " + rope.charsCopiedTotal() + " 개. 기준선이면 20억 개다");
            String text = rope.toString();
            assertEquals(120_000, text.length());
            for (int i = 0; i < text.length(); i += 997) {
                assertEquals(text.charAt(i), rope.charAt(i), "index " + i);
            }
        }

        @Test
        @Timeout(20)
        @DisplayName("부분 문자열은 꺼내는 만큼만 든다")
        void substringCostsWhatItReturns() {
            // 100만 자 문서에서 8글자를 2만 번 꺼낸다.
            // 안 겹치는 부분트리를 걸러내지 않고 전부 내려가면 꺼낼 때마다 100만 걸음이라
            // 여기서 시간이 다 간다. 답은 맞는데 O(n) 인 구현을 이 테스트가 잡는다.
            Random rnd = new Random(31L);
            String body = TestText.of(1_000_000);
            Rope rope = new Rope(body, 32);
            for (int i = 0; i < 20_000; i++) {
                int from = rnd.nextInt(body.length() - 8);
                assertEquals(body.substring(from, from + 8), rope.substring(from, from + 8),
                        "index " + from);
            }
        }

        @Test
        @Timeout(20)
        @DisplayName("앞에만 10만 번 붙여도 charAt 이 산다")
        void deepRopeStillAnswers() {
            // 앞에만 이어붙이면 트리가 한쪽으로 10만 층 기운다.
            // charAt 은 반복문이라 살고, 재귀로 훑는 toString 은 이 깊이에서 스택이 터진다.
            // (README 한계 3번에 터지는 지점을 적어뒀다. 여기서는 부르지 않는다)
            Rope rope = new Rope("", 32);
            for (int i = 0; i < 100_000; i++) {
                rope = new Rope(String.valueOf((char) ('a' + i % 26)), 32).concat(rope);
            }
            assertEquals(100_000, rope.length());
            assertEquals(99_999, rope.depth(), "한 번에 한 층씩 기운다");
            assertEquals(0, rope.charsCopiedTotal(), "기울어도 복사는 0 이다");
            assertEquals('a' + (99_999 % 26), rope.charAt(0));
            assertEquals('a', rope.charAt(99_999));
        }
    }
}
