package com.datastructure.trie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ArrayTrie")
class ArrayTrieTest extends TrieContractTest {

    @Override
    protected Trie create() {
        return new ArrayTrie();
    }

    private ArrayTrie arrayTrie(String... words) {
        ArrayTrie t = new ArrayTrie();
        for (String w : words) {
            t.insert(w);
        }
        return t;
    }

    @Nested
    @DisplayName("indexOf")
    class IndexOf {

        @Test
        @DisplayName("a~z 는 0~25")
        void inRange() {
            assertEquals(0, ArrayTrie.indexOf('a'));
            assertEquals(25, ArrayTrie.indexOf('z'));
            assertEquals(12, ArrayTrie.indexOf('m'));
        }

        @Test
        @DisplayName("아래로 벗어나면 -1")
        void belowRange() {
            assertEquals(-1, ArrayTrie.indexOf('A'), "'a' 만 빼면 -32 가 된다");
            assertEquals(-1, ArrayTrie.indexOf('Z'));
            assertEquals(-1, ArrayTrie.indexOf('0'));
            assertEquals(-1, ArrayTrie.indexOf(' '));
        }

        @Test
        @DisplayName("위로 벗어나도 -1")
        void aboveRange() {
            // 'z' 는 122, '{' 는 123 이다. 'a' 를 빼면 26 이 되어 음수 검사만으로는 안 걸린다.
            assertEquals(-1, ArrayTrie.indexOf('{'));
            assertEquals(-1, ArrayTrie.indexOf('~'));
            assertEquals(-1, ArrayTrie.indexOf('가'));
        }
    }

    @Nested
    @DisplayName("담을 수 없는 문자")
    class OutOfAlphabet {

        @Test
        @DisplayName("insert 는 예외를 던진다")
        void insertThrows() {
            ArrayTrie t = new ArrayTrie();
            assertThrows(IllegalArgumentException.class, () -> t.insert("Apple"));
            assertThrows(IllegalArgumentException.class, () -> t.insert("ab3"));
            assertThrows(IllegalArgumentException.class, () -> t.insert("사과"));
            assertEquals(0, t.size(), "예외를 던졌으면 아무것도 안 들어가야 한다");
        }

        @Test
        @DisplayName("remove 도 예외를 던진다")
        void removeThrows() {
            ArrayTrie t = arrayTrie("apple");
            assertThrows(IllegalArgumentException.class, () -> t.remove("Apple"));
            assertEquals(1, t.size());
        }

        @Test
        @DisplayName("조회는 예외 대신 '없다'로 답한다")
        void queriesAnswerNo() {
            ArrayTrie t = arrayTrie("apple");
            assertFalse(t.contains("Apple"));
            assertFalse(t.startsWith("App"));
            assertEquals(List.of(), t.keysWithPrefix("App"));
            assertEquals(0, t.countWithPrefix("App"));
        }

        @Test
        @DisplayName("insert 는 한 글자만 벗어나도 통째로 거부한다")
        void allOrNothing() {
            ArrayTrie t = new ArrayTrie();
            assertThrows(IllegalArgumentException.class, () -> t.insert("appleZ"));
            assertFalse(t.startsWith("a"), "앞부분만 넣어두고 예외를 던지면 반쯤 들어간 상태가 남는다");
            assertEquals(0, t.size());
        }
    }

    @Nested
    @DisplayName("내부 구조")
    class Structure {

        private int verify(ArrayTrie.Node node, String path, boolean isRoot) {
            int actual = node.end ? 1 : 0;
            for (int i = 0; i < ArrayTrie.ALPHABET; i++) {
                if (node.children[i] != null) {
                    actual += verify(node.children[i], path + (char) ('a' + i), false);
                }
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
            ArrayTrie t = arrayTrie("car", "card", "care", "careful", "cars", "cat", "dog");
            assertEquals(7, verify(t.root, "", true));
            t.remove("care");
            assertEquals(6, verify(t.root, "", true));
            t.remove("cat");
            assertEquals(5, verify(t.root, "", true));
            t.insert("cab");
            assertEquals(6, verify(t.root, "", true));
        }

        @Test
        @DisplayName("지운 칸에 null 이 들어간다")
        void slotBecomesNull() {
            ArrayTrie t = arrayTrie("car", "cat");
            t.remove("car");
            ArrayTrie.Node ca = t.findNode("ca");
            assertNull(ca.children[ArrayTrie.indexOf('r')], "r 칸이 안 비었다");
            assertTrue(ca.children[ArrayTrie.indexOf('t')] != null);
        }

        @Test
        @DisplayName("배열 순회 자체가 사전순이다")
        void arrayOrderIsLexicographic() {
            // TreeMap 같은 장치가 없는데도 정렬돼 나온다. 인덱스가 곧 문자이기 때문이다.
            ArrayTrie t = arrayTrie("zebra", "apple", "mango", "banana");
            assertEquals(List.of("apple", "banana", "mango", "zebra"), t.keysWithPrefix(""));
        }
    }

    @Nested
    @DisplayName("한계: 자식 배열이 늘 26칸이다")
    class SquareCost {

        @Test
        @DisplayName("자식이 하나뿐인 노드도 26칸을 쓴다")
        void allocatesTwentySixRegardlessOfChildren() {
            ArrayTrie one = arrayTrie("abcdefghij");
            int nodes = countNodes(one.root);
            assertEquals(10, nodes, "글자 수만큼 노드가 생긴다");

            // 노드마다 자식이 최대 하나인데 배열은 26칸씩이다.
            int slots = nodes * ArrayTrie.ALPHABET;
            int used = nodes - 1;   // 마지막 노드는 자식이 없다
            assertEquals(260, slots, "10개 노드 x 26칸");
            assertEquals(9, used, "실제로 쓰는 자식 참조는 9개뿐이다");
        }

        @Test
        @DisplayName("MapTrie 와 나란히 세어보면")
        void comparedWithMapTrie() {
            Random rnd = new Random(42L);
            TreeSet<String> words = new TreeSet<>();
            while (words.size() < 100) {
                StringBuilder sb = new StringBuilder(20);
                for (int i = 0; i < 20; i++) {
                    sb.append((char) ('a' + rnd.nextInt(26)));
                }
                words.add(sb.toString());
            }

            ArrayTrie at = new ArrayTrie();
            MapTrie mt = new MapTrie();
            for (String w : words) {
                at.insert(w);
                mt.insert(w);
            }
            assertEquals(at.keysWithPrefix(""), mt.keysWithPrefix(""), "두 구현의 답은 같다");

            int nodes = countNodes(at.root);
            int arraySlots = nodes * ArrayTrie.ALPHABET;
            int mapEntries = countEntries(mt.root);

            assertEquals(nodes, countMapNodes(mt.root), "두 구현이 같은 모양의 트라이를 만든다");
            assertTrue(arraySlots > mapEntries * 20,
                    "ArrayTrie 참조 슬롯 " + arraySlots + " 대 MapTrie 맵 엔트리 " + mapEntries
                            + " - 26배가 나야 한다");
        }

        private int countNodes(ArrayTrie.Node node) {
            int n = 0;
            for (ArrayTrie.Node c : node.children) {
                if (c != null) {
                    n += 1 + countNodes(c);
                }
            }
            return n;
        }

        private int countMapNodes(MapTrie.Node node) {
            int n = 0;
            for (MapTrie.Node c : node.children.values()) {
                n += 1 + countMapNodes(c);
            }
            return n;
        }

        private int countEntries(MapTrie.Node node) {
            int n = node.children.size();
            for (Map.Entry<Character, MapTrie.Node> e : node.children.entrySet()) {
                n += countEntries(e.getValue());
            }
            return n;
        }
    }
}
