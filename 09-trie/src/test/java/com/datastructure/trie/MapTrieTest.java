package com.datastructure.trie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MapTrie")
class MapTrieTest extends TrieContractTest {

    @Override
    protected Trie create() {
        return new MapTrie();
    }

    private MapTrie mapTrie(String... words) {
        MapTrie t = new MapTrie();
        for (String w : words) {
            t.insert(w);
        }
        return t;
    }

    @Nested
    @DisplayName("어떤 문자든 담는다")
    class AnyCharacter {

        @Test
        @DisplayName("한글")
        void korean() {
            MapTrie t = mapTrie("사과", "사자", "바나나");
            assertTrue(t.contains("사과"));
            assertEquals(List.of("사과", "사자"), t.keysWithPrefix("사"));
            assertEquals(2, t.countWithPrefix("사"));
        }

        @Test
        @DisplayName("숫자와 기호가 섞여도 된다")
        void mixed() {
            MapTrie t = mapTrie("a1!", "a1?", "A1!");
            assertEquals(3, t.size());
            assertEquals(List.of("a1!", "a1?"), t.keysWithPrefix("a1"));
            assertEquals(List.of("A1!"), t.keysWithPrefix("A"));
        }

        @Test
        @DisplayName("사전순은 문자 코드 순이다")
        void ordinalOrder() {
            MapTrie t = mapTrie("b", "A", "a", "1");
            assertEquals(List.of("1", "A", "a", "b"), t.keysWithPrefix(""));
        }
    }

    @Nested
    @DisplayName("내부 구조")
    class Structure {

        /** 저장된 wordsBelow 가 실제로 그 아래의 단어 수와 같은가. 뿌리가 아닌 노드는 비어 있으면 안 된다. */
        private int verify(MapTrie.Node node, String path, boolean isRoot) {
            int actual = node.end ? 1 : 0;
            for (Map.Entry<Character, MapTrie.Node> e : node.children.entrySet()) {
                actual += verify(e.getValue(), path + e.getKey(), false);
            }
            assertEquals(actual, node.wordsBelow,
                    "'" + path + "' 노드의 wordsBelow 가 실제와 다르다");
            if (!isRoot) {
                assertTrue(actual > 0, "'" + path + "' 아래에 단어가 없는데 노드가 남아 있다");
            }
            return actual;
        }

        @Test
        @DisplayName("wordsBelow 가 실제 단어 수와 일치한다")
        void wordsBelowIsAccurate() {
            MapTrie t = mapTrie("car", "card", "care", "careful", "cars", "cat", "dog");
            assertEquals(7, verify(t.root, "", true));
            t.remove("care");
            assertEquals(6, verify(t.root, "", true));
            t.remove("cat");
            assertEquals(5, verify(t.root, "", true));
            t.insert("cab");
            assertEquals(6, verify(t.root, "", true));
        }

        @Test
        @DisplayName("마지막 단어를 지우면 노드가 실제로 사라진다")
        void nodesAreActuallyDetached() {
            MapTrie t = mapTrie("car", "cat");
            t.remove("car");
            MapTrie.Node ca = t.findNode("ca");
            assertEquals(1, ca.children.size(), "r 로 가는 가지가 남아 있다");
            assertNull(ca.children.get('r'));

            t.remove("cat");
            assertTrue(t.root.children.isEmpty(), "뿌리 아래가 통째로 끊겨야 한다");
        }

        @Test
        @DisplayName("공유 접두사는 노드를 공유한다")
        void prefixesShareNodes() {
            // c-a-r 을 셋이 공유하므로 노드는 c,a,r,d,e 다섯 개다.
            // 공유하지 않고 단어마다 따로 만들면 3+4+4 = 11 개가 된다.
            MapTrie t = mapTrie("car", "card", "care");
            assertEquals(5, countNodes(t.root));
            t.insert("cat");
            assertEquals(6, countNodes(t.root));
        }

        private int countNodes(MapTrie.Node node) {
            int n = 0;
            for (MapTrie.Node c : node.children.values()) {
                n += 1 + countNodes(c);
            }
            return n;
        }

        @Test
        @DisplayName("clear 는 뿌리를 갈아치운다")
        void clearReplacesRoot() {
            MapTrie t = mapTrie("car");
            MapTrie.Node before = t.root;
            t.clear();
            assertFalse(before == t.root, "예전 뿌리를 그대로 두면 아래가 GC 되지 않는다");
            assertEquals(0, t.root.wordsBelow);
        }
    }

    @Nested
    @DisplayName("keysWithPrefix 의 path 관리")
    class PathHandling {

        @Test
        @DisplayName("형제 가지로 넘어갈 때 글자를 떼어낸다")
        void backtracksProperly() {
            // ab, ac, ad 를 넣으면 collect 가 a 로 내려갔다가
            // b 를 붙였다 떼고, c 를 붙였다 떼고, d 를 붙인다.
            // 떼는 것을 빠뜨리면 abcd 같은 것이 나온다.
            MapTrie t = mapTrie("ab", "ac", "ad");
            assertEquals(List.of("ab", "ac", "ad"), t.keysWithPrefix("a"));
        }

        @Test
        @DisplayName("깊은 가지 뒤에 얕은 가지가 와도 맞다")
        void deepThenShallow() {
            MapTrie t = mapTrie("abcdef", "b");
            assertEquals(List.of("abcdef", "b"), t.keysWithPrefix(""));
        }

        @Test
        @DisplayName("접두사가 결과에 그대로 붙는다")
        void prefixIsPrepended() {
            MapTrie t = mapTrie("abcx", "abcy");
            List<String> got = t.keysWithPrefix("abc");
            assertEquals(List.of("abcx", "abcy"), got);
            for (String s : got) {
                assertTrue(s.startsWith("abc"));
            }
        }

        @Test
        @DisplayName("collect 를 직접 불러도 같다")
        void collectDirectly() {
            MapTrie t = mapTrie("car", "cat");
            List<String> out = new ArrayList<>();
            MapTrie.collect(t.findNode("ca"), new StringBuilder("ca"), out);
            assertEquals(List.of("car", "cat"), out);
        }
    }
}
