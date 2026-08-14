package com.datastructure.rope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * 한계 측정: 옮긴 글자 수를 센다.
 *
 * 시간을 재지 않는다. 기계와 GC 에 따라 흔들리는 값으로는 "왜 이 자료구조인가" 를 말할 수 없다.
 * 여기 숫자는 전부 파이썬 참조 구현으로 먼저 계산한 것이고 결정적이다.
 */
@DisplayName("한계 측정: 복사한 글자 수")
class CopyCostTest {

    @Nested
    @DisplayName("이어붙이기는 공짜다")
    class ConcatIsFree {

        @Test
        @DisplayName("로프는 0 글자, 기준선은 n + m 글자")
        void concatCopiesNothing() {
            Rope left = new Rope("abcdefghij", 4);
            Rope right = new Rope("KLMNO", 4);
            Rope joined = left.concat(right);

            assertEquals(0, joined.charsCopiedByLastOp(),
                    "뿌리 하나를 만들고 양쪽을 자식으로 삼는다. 글자는 제자리에 있다");
            assertEquals(15, joined.length());
            assertEquals(3, joined.depth());
            assertEquals(5, joined.leafCount(), "잎 셋에 잎 둘이 그대로 붙었다");

            CharSequenceStore a = new StringBuilderStore("abcdefghij");
            CharSequenceStore b = new StringBuilderStore("KLMNO");
            assertEquals(15, a.concat(b).charsCopiedByLastOp(),
                    "배열은 붙일 자리가 없어서 둘 다 새 버퍼로 옮긴다");
        }

        @Test
        @DisplayName("1000번 이어붙이면 0 대 500만이 된다")
        void repeatedAppend() {
            String piece = TestText.of(10);
            Rope rope = new Rope("", 32);
            CharSequenceStore sb = new StringBuilderStore("");
            for (int i = 0; i < 1000; i++) {
                rope = rope.concat(new Rope(piece, 32));
                sb = sb.concat(new StringBuilderStore(piece));
            }
            assertEquals(10_000, rope.length());
            assertEquals(rope.toString(), sb.toString());
            assertEquals(0, rope.charsCopiedTotal(), "1000번 붙이는 동안 한 글자도 안 옮겼다");
            assertEquals(5_005_000, sb.charsCopiedTotal(),
                    "10 + 20 + ... + 10000. 붙일 때마다 지금까지 쓴 것을 통째로 다시 쓴다");
            assertEquals(999, rope.depth(), "대신 트리가 999층으로 기운다. 그 대가는 rebalance 가 낸다");
        }
    }

    @Nested
    @DisplayName("가운데 편집: 이 박스의 존재 이유")
    class MiddleEdits {

        @Test
        @Timeout(30)
        @DisplayName("10만 자 문서 가운데에 1000번 타자를 친다")
        void thousandKeystrokesInTheMiddle() {
            // **이 박스에서 가장 중요한 숫자다.**
            // 한 사람이 문서 한가운데에 앉아 1000글자를 치는 장면이다.
            String body = TestText.of(100_000);
            Rope rope = new Rope(body, 32);
            CharSequenceStore sb = new StringBuilderStore(body);
            for (int i = 0; i < 1000; i++) {
                int at = rope.length() / 2;
                rope = rope.insert(at, "x");
                sb = sb.insert(at, "x");
            }
            assertEquals(rope.toString(), sb.toString(), "답은 같아야 한다");
            assertEquals(101_000, rope.length());

            assertEquals(100_499_500, sb.charsCopiedTotal(),
                    "타자 한 번에 문서 전체다. 100000 + 100001 + ... + 100999");
            assertEquals(32, rope.charsCopiedTotal(),
                    "잎 하나(32글자)를 처음 한 번 쪼갠 것이 전부다. "
                            + "그 뒤로는 커서 자리가 이미 잎 경계라 쪼갤 것이 없다");
        }

        @Test
        @Timeout(30)
        @DisplayName("한 자리가 아니라 흩뿌려 쳐도 3421배다")
        void scatteredKeystrokes() {
            // 위 숫자는 커서가 한 자리에 머무는, 로프에 가장 유리한 장면이다.
            // 커서를 문서 전체에 흩뿌리면 잎을 매번 새로 쪼개야 한다. 그래도 이만큼 차이가 난다.
            String body = TestText.of(100_000);
            Rope rope = new Rope(body, 32);
            CharSequenceStore sb = new StringBuilderStore(body);
            for (int i = 0; i < 1000; i++) {
                int at = (int) ((i * 7919L) % (rope.length() + 1));
                rope = rope.insert(at, "x");
                sb = sb.insert(at, "x");
            }
            assertEquals(rope.toString(), sb.toString());
            assertEquals(100_499_500, sb.charsCopiedTotal(), "기준선은 커서가 어디든 같다");
            assertEquals(29_376, rope.charsCopiedTotal(), "잎을 1000번 가까이 쪼갠 값이다");
            assertEquals(3421, sb.charsCopiedTotal() / rope.charsCopiedTotal());
            assertEquals(41, rope.depth(), "흩뿌린 편집은 트리를 별로 안 기울인다");
        }

        @Test
        @DisplayName("작은 문서에서는 로프가 진다")
        void ropeLosesOnSmallDocuments() {
            // 정직하게 적는다. 10글자짜리 문서에서 세 글자를 지우면
            // 로프는 잎 둘을 쪼개느라 8글자를 옮기고 기준선은 7글자만 옮긴다.
            Rope rope = new Rope("abcdefghij", 4);
            CharSequenceStore sb = new StringBuilderStore("abcdefghij");
            assertEquals(8, rope.delete(3, 6).charsCopiedByLastOp(),
                    "잎 abcd 와 efgh 를 각각 쪼갠다");
            assertEquals(7, sb.delete(3, 6).charsCopiedByLastOp(), "남는 글자만 옮기면 된다");
            assertTrue(rope.delete(3, 6).charsCopiedByLastOp() > sb.delete(3, 6).charsCopiedByLastOp(),
                    "문서가 잎 몇 개 크기면 트리를 유지하는 값이 이득보다 크다");
        }

        @Test
        @DisplayName("잎보다 긴 문자열을 넣으면 그만큼 복사한다")
        void longInsertsGetChunked() {
            // 붙여넣기 장면이다. 넣는 문자열이 잎에 들어가면 참조로 들고만 있으면 되는데,
            // 잎보다 길면 조각을 내야 하고 그 순간 새 문자열이 생긴다.
            Rope rope = new Rope(TestText.of(1000), 32);
            assertEquals(0, rope.insert(512, TestText.of(32)).charsCopiedByLastOp(),
                    "잎 크기에 딱 맞으면 받은 문자열을 그대로 잎에 넣는다");
            assertEquals(33, rope.insert(512, TestText.of(33)).charsCopiedByLastOp(),
                    "한 글자만 길어도 조각내야 한다");
            assertEquals(500, rope.insert(512, TestText.of(500)).charsCopiedByLastOp());
            assertEquals(532, rope.insert(500, TestText.of(500)).charsCopiedByLastOp(),
                    "잎 한가운데에 넣으면 쪼갠 잎 32 글자가 더 붙는다");
        }

        @Test
        @DisplayName("잎 경계에 걸리면 쪼갤 것도 없다")
        void editingOnALeafBoundaryIsFree() {
            Rope rope = new Rope("abcdefghij", 4);
            assertEquals(0, rope.insert(4, "Z").charsCopiedByLastOp(), "잎 경계다");
            assertEquals(4, rope.insert(5, "Z").charsCopiedByLastOp(), "잎 한가운데다");
            assertEquals(0, rope.insert(10, "Z").charsCopiedByLastOp(), "맨 뒤도 경계다");
            assertEquals(0, rope.split(4).left().charsCopiedByLastOp());
            assertEquals(4, rope.split(5).left().charsCopiedByLastOp());
            assertEquals(10, new StringBuilderStore("abcdefghij").split(5).left()
                    .charsCopiedByLastOp(), "기준선은 어디서 쪼개든 통째로 옮긴다");
        }
    }

    @Nested
    @DisplayName("대신 임의 접근이 진다")
    class RandomAccessLoses {

        @Test
        @Timeout(30)
        @DisplayName("10만 번 조회에 노드를 126만 번 방문한다")
        void charAtVisitsNodes() {
            // 배열은 주소 계산 한 번이다. 로프는 뿌리에서 잎까지 내려간다.
            // 이 숫자가 "읽기가 압도적으로 많으면 배열이 이긴다" 의 근거다.
            Rope rope = new Rope(TestText.of(100_000), 32);
            assertEquals(12, rope.depth());
            assertEquals(3125, rope.leafCount(), "10만 글자를 32씩 나눠 담았다");

            rope.resetCharAtVisits();
            for (int i = 0; i < rope.length(); i++) {
                rope.charAt(i);
            }
            assertEquals(1_268_928, rope.charAtVisits(), "조회 한 번에 12.7 노드다");
            assertEquals(12, rope.charAtVisits() / 100_000, "배열이었으면 1 이다");
        }

        @Test
        @DisplayName("깊어지면 조회도 같이 비싸진다")
        void deeperMeansSlowerReads() {
            // 앞에만 100번 붙인 로프는 깊이가 99 다. 그 상태로 조회하면 방문 노드가 폭증한다.
            Rope leaning = new Rope("", 32);
            for (int i = 0; i < 100; i++) {
                leaning = new Rope("abcd", 32).concat(leaning);
            }
            assertEquals(99, leaning.depth());
            leaning.resetCharAtVisits();
            for (int i = 0; i < leaning.length(); i++) {
                leaning.charAt(i);
            }
            long leaningVisits = leaning.charAtVisits();

            Rope balanced = leaning.rebalance();
            assertEquals(7, balanced.depth());
            balanced.resetCharAtVisits();
            for (int i = 0; i < balanced.length(); i++) {
                balanced.charAt(i);
            }
            assertEquals(20_596, leaningVisits, "글자 400개를 읽는데 조회마다 평균 51.5 노드다");
            assertEquals(3_088, balanced.charAtVisits(), "균형을 잡으면 7.7 노드다. 6.7배 싸졌다");
            assertEquals(leaning.toString(), balanced.toString(), "내용은 그대로다");
        }
    }
}
