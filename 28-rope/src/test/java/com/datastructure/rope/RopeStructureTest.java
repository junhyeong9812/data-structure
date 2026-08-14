package com.datastructure.rope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * 계약만으로는 안 잡히는 것들. 트리의 모양을 직접 본다.
 *
 * 답이 맞는 로프는 얼마든지 만들 수 있다. 잎 하나에 문서 전체를 담아도 답은 맞는다.
 * 여기서 보는 것은 "그래서 그게 로프인가" 다.
 *
 * 필드 이름이 계약이다. root, leafMax, charAtVisits 를 바꾸면 이 파일이 깨진다.
 */
@DisplayName("Rope 구조")
class RopeStructureTest {

    private static Set<Rope.Node> nodesOf(Rope rope) {
        IdentityHashMap<Rope.Node, Boolean> seen = new IdentityHashMap<>();
        collect(rope.root(), seen);
        return seen.keySet();
    }

    private static void collect(Rope.Node node, IdentityHashMap<Rope.Node, Boolean> seen) {
        if (seen.put(node, Boolean.TRUE) != null) {
            return;
        }
        if (!node.isLeaf()) {
            collect(node.left, seen);
            collect(node.right, seen);
        }
    }

    private static int sharedNodeCount(Rope a, Rope b) {
        Set<Rope.Node> left = nodesOf(a);
        int shared = 0;
        for (Rope.Node node : nodesOf(b)) {
            if (left.contains(node)) {
                shared++;
            }
        }
        return shared;
    }

    @Nested
    @DisplayName("잎에 조각으로 담는다")
    class LeafShape {

        @Test
        @DisplayName("문서가 leafMax 크기로 잘려 잎에 들어간다")
        void chunksIntoLeaves() {
            Rope rope = new Rope("abcdefghij", 4);
            assertEquals(List.of("abcd", "efgh", "ij"), rope.leaves());
            assertEquals(3, rope.leafCount());
            assertEquals(5, rope.nodeCount(), "잎 셋을 묶는 내부 노드가 둘이다");
            assertEquals(2, rope.depth());
        }

        @Test
        @DisplayName("어떤 크기로 지어도 잎이 leafMax 를 안 넘는다")
        void neverExceedsLeafMax() {
            for (int leafMax : new int[]{1, 2, 7, 32, 1000}) {
                Rope rope = new Rope(TestText.of(1000), leafMax);
                for (String leaf : rope.leaves()) {
                    assertTrue(leaf.length() <= leafMax,
                            "잎 " + leafMax + " 인데 조각이 " + leaf.length());
                    assertFalse(leaf.isEmpty(), "빈 잎은 자리만 차지한다");
                }
                assertEquals(1000, rope.length());
            }
        }

        @Test
        @DisplayName("빈 문서도 잎 하나다")
        void emptyRope() {
            Rope empty = new Rope("", 32);
            assertEquals(0, empty.length());
            assertEquals(0, empty.depth());
            assertEquals(1, empty.nodeCount());
            assertEquals(List.of(), empty.leaves(), "빈 잎은 안 센다");
        }

        @Test
        @DisplayName("leafMax 는 1 이상이어야 한다")
        void rejectsBadLeafMax() {
            assertThrows(IllegalArgumentException.class, () -> new Rope("abc", 0));
            assertThrows(IllegalArgumentException.class, () -> new Rope("abc", -1));
            assertThrows(IllegalArgumentException.class, () -> new Rope(null, 4));
            assertEquals(32, Rope.DEFAULT_LEAF_MAX);
            assertEquals(32, new Rope("abc").leafMax());
        }
    }

    @Nested
    @DisplayName("잎 크기의 절충")
    class LeafMaxTradeoff {

        /**
         * leafMax 를 다섯 값으로 두고 같은 일을 시킨다.
         * 4096자 문서를 짓고, 정해진 자리 200곳에 한 글자씩 넣고, 전체를 한 번 훑는다.
         *
         * 작으면 노드가 많고 조회가 비싸다. 크면 편집마다 그 조각을 통째로 옮긴다.
         * 어느 쪽도 공짜가 아니라는 것이 이 표의 전부다.
         */
        @Test
        @Timeout(30)
        @DisplayName("작으면 노드가 폭발하고 크면 복사가 폭발한다")
        void theTable() {
            int[] leafMaxes = {1, 4, 32, 256, 4096};
            int[] nodesBefore = {8191, 2047, 255, 31, 1};
            int[] depthBefore = {12, 10, 7, 4, 0};
            int[] nodesAfter = {8591, 2719, 1029, 815, 785};
            long[] copied = {0, 536, 4902, 17834, 48931};
            long[] sweepVisits = {53_248, 45_056, 32_768, 20_480, 4096};

            String body = TestText.of(4096);
            for (int k = 0; k < leafMaxes.length; k++) {
                int leafMax = leafMaxes[k];
                Rope rope = new Rope(body, leafMax);
                assertEquals(nodesBefore[k], rope.nodeCount(), "잎 " + leafMax + " 노드 수");
                assertEquals(depthBefore[k], rope.depth(), "잎 " + leafMax + " 깊이");

                for (int i = 0; i < rope.length(); i++) {
                    rope.charAt(i);
                }
                assertEquals(sweepVisits[k], rope.charAtVisits(),
                        "잎 " + leafMax + ": 4096번 조회에 방문한 노드");

                for (int i = 0; i < 200; i++) {
                    int at = (int) ((i * 7919L) % (rope.length() + 1));
                    rope = rope.insert(at, "x");
                }
                assertEquals(nodesAfter[k], rope.nodeCount(), "잎 " + leafMax + " 편집 후 노드 수");
                assertEquals(copied[k], rope.charsCopiedTotal(), "잎 " + leafMax + " 옮긴 글자");
                assertEquals(4296, rope.length());
            }
        }

        @Test
        @DisplayName("leafMax 1 은 복사가 0 이고 노드가 8191 개다")
        void bothEndsOfTheTradeoff() {
            // 글자마다 잎이면 쪼갤 일이 없다. 복사가 아예 0 이다.
            // 대신 4096 글자에 노드가 8191 개이고 조회 한 번에 13 노드다.
            Rope perChar = new Rope(TestText.of(4096), 1);
            assertEquals(8191, perChar.nodeCount());
            assertEquals(4096, perChar.leafCount());

            // 문서 전체가 잎 하나면 노드가 1개다. 대신 편집 한 번이 4096 글자 복사다.
            Rope oneLeaf = new Rope(TestText.of(4096), 4096);
            assertEquals(1, oneLeaf.nodeCount());
            assertEquals(0, oneLeaf.depth());
            assertEquals(4096, oneLeaf.insert(2048, "x").charsCopiedByLastOp(),
                    "잎 하나짜리 로프는 배열과 같은 값을 낸다");
            assertEquals(0, perChar.insert(2048, "x").charsCopiedByLastOp());
        }
    }

    @Nested
    @DisplayName("편집을 반복하면 기운다")
    class Leaning {

        @Test
        @DisplayName("앞에만 1000번 붙이면 깊이가 999 가 된다")
        void prependingLeans() {
            Rope rope = new Rope("", 32);
            for (int i = 0; i < 1000; i++) {
                rope = new Rope(TestText.of(4), 32).concat(rope);
            }
            assertEquals(4000, rope.length());
            assertEquals(999, rope.depth(), "이어붙이기가 공짜인 대가를 여기서 낸다");
            assertEquals(1000, rope.leafCount());
            assertEquals(1999, rope.nodeCount());
            assertEquals(0, rope.charsCopiedTotal());

            Rope balanced = rope.rebalance();
            assertEquals(10, balanced.depth(), "1000개 잎의 균형 트리 높이는 10 이다");
            assertEquals(1000, balanced.leafCount(), "잎은 그대로다");
            assertEquals(1999, balanced.nodeCount(), "노드 수도 그대로다. 모양만 바뀐다");
            assertEquals(rope.toString(), balanced.toString(), "내용은 절대 안 바뀐다");
            assertEquals(0, balanced.charsCopiedByLastOp(),
                    "잎을 그대로 다시 매달기만 한다. 글자는 한 개도 안 옮긴다");
        }

        @Test
        @DisplayName("rebalance 는 잎 객체를 그대로 재사용한다")
        void rebalanceReusesLeaves() {
            Rope rope = new Rope("", 32);
            for (int i = 0; i < 1000; i++) {
                rope = new Rope(TestText.of(4), 32).concat(rope);
            }
            Rope balanced = rope.rebalance();
            assertEquals(1000, sharedNodeCount(rope, balanced),
                    "잎 1000개가 같은 객체다. 새로 만든 것은 내부 노드 999개뿐이다");
        }

        @Test
        @DisplayName("가운데만 계속 고쳐도 기운다")
        void middleEditsLeanToo() {
            Rope rope = new Rope(TestText.of(100_000), 32);
            for (int i = 0; i < 1000; i++) {
                rope = rope.insert(rope.length() / 2, "x");
            }
            assertEquals(513, rope.depth(), "한 자리를 계속 치면 그 자리에 층이 쌓인다");
            assertEquals(4126, rope.leafCount());
            assertEquals(13, rope.rebalance().depth());
            assertEquals(rope.toString(), rope.rebalance().toString());
        }

        @Test
        @DisplayName("rebalance 는 여러 번 불러도 같다")
        void rebalanceIsIdempotent() {
            Rope rope = new Rope(TestText.of(500), 8);
            for (int i = 0; i < 200; i++) {
                rope = rope.insert((int) ((i * 7919L) % (rope.length() + 1)), "q");
            }
            Rope once = rope.rebalance();
            Rope twice = once.rebalance();
            assertEquals(once.depth(), twice.depth());
            assertEquals(once.nodeCount(), twice.nodeCount());
            assertEquals(once.toString(), twice.toString());
            assertEquals(rope.toString(), twice.toString());
        }
    }

    @Nested
    @DisplayName("불변이라 옛 버전이 산다")
    class Immutability {

        @Test
        @DisplayName("편집해도 옛 로프가 그대로다")
        void oldVersionsSurvive() {
            String body = TestText.of(4096);
            Rope base = new Rope(body, 32);
            Rope edited = base.insert(2050, "X");
            Rope deleted = base.delete(100, 200);
            Rope joined = base.concat(new Rope("!", 32));

            assertEquals(body, base.toString(), "26번 영속 자료구조와 같은 성질이다");
            assertEquals(4096, base.length());
            assertEquals(255, base.nodeCount(), "옛 로프의 모양도 안 바뀐다");
            assertEquals(body.substring(0, 2050) + "X" + body.substring(2050), edited.toString());
            assertEquals(body.substring(0, 100) + body.substring(200), deleted.toString());
            assertEquals(body + "!", joined.toString());
        }

        @Test
        @DisplayName("새 버전은 노드 12개만 새로 만든다")
        void editSharesAlmostEverything() {
            Rope base = new Rope(TestText.of(4096), 32);
            Rope edited = base.insert(2050, "X");
            assertEquals(255, base.nodeCount());
            assertEquals(259, edited.nodeCount());
            assertEquals(247, sharedNodeCount(base, edited),
                    "새 로프의 노드 259개 중 247개가 옛 로프와 같은 객체다");
            assertEquals(32, edited.charsCopiedByLastOp(), "쪼갠 잎 하나만큼만 복사했다");
        }

        @Test
        @Timeout(30)
        @DisplayName("1000 버전을 다 들고 있어도 복사가 12502 글자다")
        void keepingEveryVersionIsCheap() {
            // 편집기의 실행 취소가 이렇게 만들어진다. 26번에서 본 것과 같은 이야기다.
            String body = TestText.of(4096);
            List<Rope> versions = new ArrayList<>();
            List<CharSequenceStore> baselineVersions = new ArrayList<>();
            Rope rope = new Rope(body, 32);
            CharSequenceStore sb = new StringBuilderStore(body);
            versions.add(rope);
            baselineVersions.add(sb);
            for (int i = 0; i < 1000; i++) {
                int at = (int) ((i * 7919L) % (rope.length() + 1));
                rope = rope.insert(at, "x");
                sb = sb.insert(at, "x");
                versions.add(rope);
                baselineVersions.add(sb);
            }
            assertEquals(12_502, rope.charsCopiedTotal(), "로프가 옮긴 글자");
            assertEquals(4_595_500, sb.charsCopiedTotal(), "기준선이 옮긴 글자. 367배다");

            assertEquals(body, versions.get(0).toString(), "맨 처음 버전이 살아 있다");
            for (int i = 0; i <= 1000; i += 100) {
                assertEquals(baselineVersions.get(i).toString(), versions.get(i).toString(),
                        "버전 " + i);
                assertEquals(4096 + i, versions.get(i).length(), "버전 " + i);
            }
        }

        @Test
        @DisplayName("내용을 담는 필드가 전부 final 이다")
        void contentFieldsAreFinal() {
            assertTrue(Modifier.isFinal(Rope.class.getModifiers()), "Rope 는 final 클래스다");
            for (Field f : Rope.class.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                if (f.getName().equals("charAtVisits")) {
                    assertFalse(Modifier.isFinal(f.getModifiers()),
                            "charAtVisits 는 계측용이라 유일하게 가변이다");
                    continue;
                }
                assertTrue(Modifier.isFinal(f.getModifiers()),
                        "필드 " + f.getName() + " 이 final 이 아니다. 옛 버전이 조용히 바뀔 수 있다");
            }
            for (Field f : Rope.Node.class.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                assertTrue(Modifier.isFinal(f.getModifiers()),
                        "노드 필드 " + f.getName() + " 이 final 이 아니다. 공유한 순간 남의 버전을 고친다");
            }
        }

        @Test
        @DisplayName("계측기는 내용을 안 건드린다")
        void meterIsNotState() {
            Rope rope = new Rope(TestText.of(100), 8);
            rope.resetCharAtVisits();
            rope.charAt(50);
            assertTrue(rope.charAtVisits() > 0);
            assertEquals(TestText.of(100), rope.toString());
            rope.resetCharAtVisits();
            assertEquals(0, rope.charAtVisits());
            assertEquals(TestText.of(100), rope.toString(), "계측기를 지워도 내용은 그대로다");
        }
    }
}
