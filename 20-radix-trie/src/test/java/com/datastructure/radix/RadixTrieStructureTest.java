package com.datastructure.radix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 내부 구조를 직접 본다.
 *
 * 계약만으로는 압축이 풀렸는지를 못 잡는다.
 * 간선을 안 쪼개고 한 글자씩 노드를 만들어도, 삭제 후 안 합쳐도 답은 전부 맞다.
 * 그래서 09번으로 조용히 퇴화한다. 여기서 그것을 잡는다.
 */
@DisplayName("RadixTrie 내부 구조")
class RadixTrieStructureTest {

    static RadixTrie<String> of(String... keys) {
        RadixTrie<String> t = new RadixTrie<>();
        for (String k : keys) {
            t.put(k, "v:" + k);
        }
        return t;
    }

    /** 트리를 "깊이:간선(키면 *)" 줄로 편다. 자식은 사전순, 뿌리 포함. */
    static List<String> shape(RadixTrie<String> t) {
        List<String> out = new ArrayList<>();
        walk(t.root, 0, out);
        return out;
    }

    private static void walk(RadixTrie.Node<String> node, int depth, List<String> out) {
        out.add(depth + ":" + node.edge + (node.value != null ? "*" : ""));
        for (RadixTrie.Node<String> c : node.children.values()) {
            walk(c, depth + 1, out);
        }
    }

    @Nested
    @DisplayName("간선 쪼개기")
    class Splitting {

        @Test
        @DisplayName("같은 키를 다시 넣으면 아무것도 안 쪼갠다")
        void exactMatch() {
            RadixTrie<String> t = of("test", "test");
            assertEquals(List.of("0:", "1:test*"), shape(t));
            assertEquals(1, t.nodeCount());
            assertEquals(1, t.size());
        }

        @Test
        @DisplayName("기존 간선이 새 키의 접두사면 그 아래에 붙는다")
        void existingEdgeIsPrefixOfNewKey() {
            // test 로 내려간 뒤 남은 "er" 로 새 자식을 만든다. test 노드는 그대로다.
            assertEquals(List.of("0:", "1:test*", "2:er*"), shape(of("test", "tester")));
        }

        @Test
        @DisplayName("새 키가 기존 간선의 접두사면 간선을 잘라 그 자리에 값을 둔다")
        void newKeyIsPrefixOfExistingEdge() {
            // "tester" 간선을 "test" + "er" 로 자르고 **자른 자리에** 값이 선다.
            // 넣는 순서가 반대여도 모양이 같아야 한다.
            assertEquals(List.of("0:", "1:test*", "2:er*"), shape(of("tester", "test")));
            assertEquals(shape(of("test", "tester")), shape(of("tester", "test")));
        }

        @Test
        @DisplayName("일부만 겹치면 잘린 자리에 갈림길이 생긴다")
        void partialMatchSplits() {
            // romane 와 romanus 는 "roman" 까지 같다.
            // 간선 'romane' 를 'roman' + 'e' 로 자르고 'us' 를 형제로 붙인다.
            assertEquals(List.of("0:", "1:roman", "2:e*", "2:us*"),
                    shape(of("romane", "romanus")),
                    "자른 자리 자체는 키가 아니다. 별표가 붙으면 roman 이 키가 돼버린다");
        }

        @Test
        @DisplayName("첫 글자부터 다르면 뿌리에서 갈라진다")
        void differentFirstChar() {
            assertEquals(List.of("0:", "1:abc*", "1:xyz*"), shape(of("abc", "xyz")));
            assertEquals(2, of("abc", "xyz").nodeCount());
        }

        @Test
        @DisplayName("자른 자리를 다시 자를 수 있다")
        void splitTheSplit() {
            // roman -> rom -> r 로 세 번 잘린다.
            assertEquals(List.of("0:", "1:roman", "2:e*", "2:us*"),
                    shape(of("romane", "romanus")));
            assertEquals(List.of("0:", "1:rom", "2:an", "3:e*", "3:us*", "2:ulus*"),
                    shape(of("romane", "romanus", "romulus")));
            assertEquals(List.of("0:", "1:r", "2:om", "3:an", "4:e*", "4:us*", "3:ulus*",
                            "2:ubens*"),
                    shape(of("romane", "romanus", "romulus", "rubens")));
        }

        @Test
        @DisplayName("교과서 예시 7개의 최종 모양")
        void romanShape() {
            RadixTrie<String> t = new RadixTrie<>();
            for (String k : RadixTrieTest.ROMAN) {
                t.put(k, k);
            }
            assertEquals(List.of(
                    "0:",
                    "1:r",
                    "2:om",
                    "3:an",
                    "4:e*",
                    "4:us*",
                    "3:ulus*",
                    "2:ub",
                    "3:e",
                    "4:ns*",
                    "4:r*",
                    "3:ic",
                    "4:on*",
                    "4:undus*"), shape(t));
            assertEquals(13, t.nodeCount());
        }

        @Test
        @DisplayName("삽입 순서가 달라도 모양이 같다")
        void shapeIsOrderIndependent() {
            List<String> forward = new ArrayList<>(RadixTrieTest.ROMAN);
            List<String> backward = new ArrayList<>(RadixTrieTest.ROMAN);
            java.util.Collections.reverse(backward);
            java.util.Collections.shuffle(forward, new Random(11L));

            RadixTrie<String> a = new RadixTrie<>();
            RadixTrie<String> b = new RadixTrie<>();
            for (String k : forward) {
                a.put(k, k);
            }
            for (String k : backward) {
                b.put(k, k);
            }
            assertEquals(shape(a), shape(b));
            assertEquals(13, a.nodeCount());
        }
    }

    @Nested
    @DisplayName("삭제 후 병합")
    class Merging {

        @Test
        @DisplayName("자식이 하나만 남으면 다시 합친다")
        void mergesWhenOneChildLeft() {
            RadixTrie<String> t = of("romane", "romanus", "romulus");
            assertEquals(5, t.nodeCount());
            t.remove("romulus");
            // 'rom' 에 자식이 'an' 하나만 남았다. 합쳐서 'roman' 으로 돌아가야 한다.
            assertEquals(List.of("0:", "1:roman", "2:e*", "2:us*"), shape(t),
                    "합치지 않으면 rom -> an 이 그대로 남아 09번으로 퇴화한다");
            assertEquals(3, t.nodeCount());
        }

        @Test
        @DisplayName("값을 지운 노드도 자식이 하나면 합친다")
        void mergesAfterValueCleared() {
            RadixTrie<String> t = of("test", "tester");
            t.remove("test");
            assertEquals(List.of("0:", "1:tester*"), shape(t),
                    "test 는 더 이상 키가 아니다. 간선을 끊어둘 이유가 없다");
            assertEquals(1, t.nodeCount());
        }

        @Test
        @DisplayName("값이 남아 있으면 합치지 않는다")
        void keepsNodeThatIsStillAKey() {
            RadixTrie<String> t = of("test", "tester", "testify");
            t.remove("tester");
            // test 는 여전히 키다. 자식이 하나여도 흡수하면 test 가 사라진다.
            assertEquals(List.of("0:", "1:test*", "2:ify*"), shape(t));
        }

        @Test
        @DisplayName("자식이 둘이면 합치지 않는다")
        void keepsBranchingNode() {
            RadixTrie<String> t = of("romane", "romanus", "romulus", "rubens");
            t.remove("rubens");
            assertEquals(List.of("0:", "1:rom", "2:an", "3:e*", "3:us*", "2:ulus*"), shape(t));
        }

        @Test
        @DisplayName("뿌리는 절대 합치지 않는다")
        void rootIsNeverMerged() {
            RadixTrie<String> t = of("abc", "xyz");
            t.remove("xyz");
            // 뿌리에 자식이 'abc' 하나만 남았다. 여기서 합치면 뿌리의 간선이 'abc' 가 되고
            // 뿌리가 빈 문자열을 가리킨다는 전제가 무너진다.
            assertEquals(List.of("0:", "1:abc*"), shape(t));
            assertEquals("", t.root.edge);
        }

        @Test
        @DisplayName("넣었다 지우면 모양이 되돌아온다")
        void roundTrip() {
            RadixTrie<String> before = new RadixTrie<>();
            for (String k : RadixTrieTest.ROMAN) {
                before.put(k, k);
            }
            List<String> original = shape(before);

            before.put("rubicundusx", "x");
            assertNotEquals(original, shape(before), "넣었으면 모양이 달라져야 한다");
            before.remove("rubicundusx");
            assertEquals(original, shape(before), "지우면 원래 모양으로 돌아와야 한다");
        }
    }

    @Nested
    @DisplayName("불변식")
    class Invariants {

        /**
         * 압축 불변식 셋.
         *   1. 뿌리가 아닌 노드의 간선은 비어 있지 않다
         *   2. 뿌리가 아닌 노드는 키이거나 갈림길이다 (자식 하나짜리 사슬이 없다)
         *   3. keysBelow 가 실제 아래 키 수와 같다
         */
        static int verify(RadixTrie.Node<String> node, String path, boolean isRoot) {
            if (!isRoot) {
                assertFalse(node.edge.isEmpty(), "'" + path + "' 의 간선이 비었다");
                assertTrue(node.value != null || node.children.size() >= 2,
                        "'" + path + "' 는 키도 아니고 갈림길도 아니다. 압축이 풀렸다");
            }
            int actual = node.value != null ? 1 : 0;
            for (RadixTrie.Node<String> c : node.children.values()) {
                actual += verify(c, path + c.edge, false);
            }
            assertEquals(actual, node.keysBelow, "'" + path + "' 의 keysBelow 가 실제와 다르다");
            if (!isRoot) {
                assertTrue(actual > 0, "'" + path + "' 아래에 키가 없는데 노드가 남아 있다");
            }
            return actual;
        }

        @Test
        @DisplayName("무작위 3000 스텝 내내 유지된다")
        void heldThroughRandomOps() {
            Random rnd = new Random(777L);
            RadixTrie<String> t = new RadixTrie<>();
            TreeSet<String> ref = new TreeSet<>();
            for (int step = 0; step < 3000; step++) {
                String k = key(rnd);
                if (rnd.nextInt(3) == 0) {
                    t.remove(k);
                    ref.remove(k);
                } else {
                    t.put(k, "v");
                    ref.add(k);
                }
                if (step % 50 == 0) {
                    assertEquals(ref.size(), verify(t.root, "", true), "step " + step);
                }
            }
            assertEquals(ref.size(), verify(t.root, "", true));
            assertEquals(new ArrayList<>(ref), t.keys());
        }

        @Test
        @DisplayName("노드 수는 키 수의 2배를 넘지 않는다")
        void nodeCountBound() {
            Random rnd = new Random(999L);
            RadixTrie<String> t = new RadixTrie<>();
            for (int i = 0; i < 2000; i++) {
                t.put(key(rnd), "v");
            }
            assertTrue(t.nodeCount() <= 2 * t.size() - 1,
                    "키 " + t.size() + "개에 노드 " + t.nodeCount() + "개 - 2n-1 을 넘었다");
        }

        private static String key(Random rnd) {
            int len = rnd.nextInt(8);
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                sb.append((char) ('a' + rnd.nextInt(3)));
            }
            return sb.toString();
        }
    }

    /**
     * 한계 측정.
     *
     * 09번식 트라이의 노드 수 = 서로 다른 접두사 개수다(뿌리 제외).
     * 같은 키 집합으로 둘을 세서 나란히 놓는다. 이득이 어디서 오고 어디서 사라지는지 숫자로 보인다.
     */
    @Nested
    @DisplayName("한계 측정: 노드 수")
    class NodeCountMeasurement {

        /** 09번 MapTrie 가 만들었을 노드 수. */
        static int charTrieNodes(List<String> keys) {
            TreeSet<String> prefixes = new TreeSet<>();
            for (String k : keys) {
                for (int i = 1; i <= k.length(); i++) {
                    prefixes.add(k.substring(0, i));
                }
            }
            return prefixes.size();
        }

        static RadixTrie<String> build(List<String> keys) {
            RadixTrie<String> t = new RadixTrie<>();
            for (String k : keys) {
                t.put(k, "v");
            }
            return t;
        }

        @Test
        @DisplayName("긴 단어에 공유 접두사가 없으면 20배 차이가 난다")
        void longUnrelatedWords() {
            List<String> words = List.of(
                    "internationalization", "counterrevolutionary", "electroencephalogram");
            assertEquals(60, charTrieNodes(words), "글자당 노드 하나면 20 글자 곱하기 3 이다");
            assertEquals(3, build(words).nodeCount(), "압축하면 단어당 노드 하나다");
        }

        @Test
        @DisplayName("교과서 예시 7개")
        void roman() {
            assertEquals(27, charTrieNodes(RadixTrieTest.ROMAN));
            assertEquals(13, build(RadixTrieTest.ROMAN).nodeCount());
        }

        @Test
        @DisplayName("짧고 접두사를 많이 공유하면 이득이 작다")
        void shortSharedWords() {
            List<String> words = List.of("car", "card", "care", "careful", "cars", "cat", "dog");
            assertEquals(13, charTrieNodes(words));
            assertEquals(8, build(words).nodeCount());
        }

        @Test
        @DisplayName("한 글자 키만 있으면 이득이 아예 없다")
        void singleCharKeys() {
            List<String> words = new ArrayList<>();
            for (char c = 'a'; c <= 'z'; c++) {
                words.add(String.valueOf(c));
            }
            // 누를 사슬이 없다. 압축 트라이가 항상 이기는 것이 아니다.
            assertEquals(26, charTrieNodes(words));
            assertEquals(26, build(words).nodeCount());
        }

        @Test
        @DisplayName("노드는 줄지만 저장하는 글자 수는 그대로다")
        void charactersAreNotSaved() {
            // 압축은 **노드 개수**를 줄이는 것이지 글자를 줄이는 것이 아니다.
            // 간선 라벨의 글자를 전부 더하면 09번의 노드 수와 같다.
            RadixTrie<String> t = build(RadixTrieTest.ROMAN);
            assertEquals(charTrieNodes(RadixTrieTest.ROMAN), totalEdgeChars(t.root));
        }

        private static int totalEdgeChars(RadixTrie.Node<String> node) {
            int n = node.edge.length();
            for (RadixTrie.Node<String> c : node.children.values()) {
                n += totalEdgeChars(c);
            }
            return n;
        }
    }
}
