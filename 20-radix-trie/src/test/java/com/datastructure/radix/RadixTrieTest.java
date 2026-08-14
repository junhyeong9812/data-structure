package com.datastructure.radix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * PrefixMap 계약.
 *
 * 09번 TrieContractTest 와 겹치는 것이 많다. 겹치는 것이 요점이다.
 * 밖에서 보면 같은 자료구조여야 한다. 안이 압축돼 있다는 것은 밖에서 안 보여야 한다.
 * 압축이 밖으로 새는 지점(간선 중간에서 끝나는 접두사)만 여기 따로 있다.
 */
@DisplayName("RadixTrie")
class RadixTrieTest {

    /** 압축 트라이 교과서의 예시 단어들. 갈림길이 여러 층으로 생긴다. */
    static final List<String> ROMAN = List.of(
            "romane", "romanus", "romulus", "rubens", "ruber", "rubicon", "rubicundus");

    static RadixTrie<String> of(Iterable<String> keys) {
        RadixTrie<String> t = new RadixTrie<>();
        for (String k : keys) {
            t.put(k, "v:" + k);
        }
        return t;
    }

    static RadixTrie<String> of(String... keys) {
        return of(List.of(keys));
    }

    /** 0 이상의 정수를 a~z 여섯 글자로. 길이가 균일해야 대비가 깨끗하다. */
    static String encode(int n) {
        char[] buf = new char[6];
        for (int i = 5; i >= 0; i--) {
            buf[i] = (char) ('a' + (n % 26));
            n /= 26;
        }
        return new String(buf);
    }

    @Nested
    @DisplayName("빈 맵")
    class Empty {

        @Test
        @DisplayName("아무것도 없다")
        void nothing() {
            RadixTrie<String> t = new RadixTrie<>();
            assertEquals(0, t.size());
            assertTrue(t.isEmpty());
            assertNull(t.get("a"));
            assertNull(t.get(""));
            assertFalse(t.containsKey("a"));
            assertEquals(List.of(), t.keys());
            assertEquals(List.of(), t.keysWithPrefix(""));
            assertEquals(0, t.countWithPrefix(""));
            assertNull(t.longestPrefixOf("anything"));
        }

        @Test
        @DisplayName("없는 것은 못 지운다")
        void removeMissing() {
            assertNull(new RadixTrie<String>().remove("a"));
        }
    }

    @Nested
    @DisplayName("put 과 get")
    class Put {

        @Test
        @DisplayName("넣으면 있다")
        void putThenGet() {
            RadixTrie<String> t = of("apple");
            assertEquals("v:apple", t.get("apple"));
            assertTrue(t.containsKey("apple"));
            assertEquals(1, t.size());
        }

        @Test
        @DisplayName("처음 넣으면 null, 덮어쓰면 예전 값")
        void returnsOldValue() {
            RadixTrie<String> t = new RadixTrie<>();
            assertNull(t.put("apple", "one"));
            assertEquals("one", t.put("apple", "two"));
            assertEquals("two", t.get("apple"));
            assertEquals(1, t.size(), "덮어쓰기는 개수를 늘리지 않는다");
        }

        @Test
        @DisplayName("접두사는 키가 아니다")
        void prefixIsNotKey() {
            RadixTrie<String> t = of("apple");
            assertFalse(t.containsKey("app"));
            assertFalse(t.containsKey("appl"));
            assertFalse(t.containsKey("a"));
            assertTrue(t.containsKey("apple"));
        }

        @Test
        @DisplayName("접두사가 키이기도 하면 둘 다 있다")
        void bothPrefixAndKey() {
            RadixTrie<String> t = of("app", "apple");
            assertEquals(2, t.size());
            assertEquals("v:app", t.get("app"));
            assertEquals("v:apple", t.get("apple"));
            assertFalse(t.containsKey("appl"));
        }

        @Test
        @DisplayName("긴 것을 먼저 넣고 짧은 것을 넣어도 같다")
        void insertionOrderDoesNotMatter() {
            RadixTrie<String> a = of("apple", "app");
            RadixTrie<String> b = of("app", "apple");
            assertEquals(a.keys(), b.keys());
            assertEquals(List.of("app", "apple"), a.keys());
            assertEquals("v:app", a.get("app"));
            assertEquals("v:apple", a.get("apple"));
        }

        @Test
        @DisplayName("빈 문자열도 키다")
        void emptyStringIsAKey() {
            RadixTrie<String> t = of("");
            assertEquals(1, t.size());
            assertEquals("v:", t.get(""));
            assertTrue(t.containsKey(""));
            assertEquals(List.of(""), t.keys());
            assertEquals(1, t.countWithPrefix(""));
            assertEquals("", t.longestPrefixOf("anything"));
        }

        @Test
        @DisplayName("null 값은 받지 않는다")
        void rejectsNullValue() {
            RadixTrie<String> t = new RadixTrie<>();
            assertThrows(IllegalArgumentException.class, () -> t.put("a", null));
            assertEquals(0, t.size(), "거부한 put 이 개수를 건드리면 안 된다");
        }

        @Test
        @DisplayName("한 글자짜리 여럿")
        void singleChars() {
            RadixTrie<String> t = of("a", "b", "c");
            assertEquals(3, t.size());
            assertEquals(List.of("a", "b", "c"), t.keys());
            assertFalse(t.containsKey("d"));
        }

        @Test
        @DisplayName("어떤 문자든 담는다")
        void anyCharacter() {
            RadixTrie<String> t = of("사과", "사자", "바나나", "a1!");
            assertEquals(4, t.size());
            assertEquals(List.of("사과", "사자"), t.keysWithPrefix("사"));
            assertEquals(List.of("a1!"), t.keysWithPrefix("a"));
        }
    }

    @Nested
    @DisplayName("keys 와 keysWithPrefix")
    class Keys {

        @Test
        @DisplayName("사전순으로 나온다")
        void lexicographic() {
            assertEquals(List.of("romane", "romanus", "romulus",
                            "rubens", "ruber", "rubicon", "rubicundus"),
                    of(ROMAN).keys());
        }

        @Test
        @DisplayName("접두사로 거른다")
        void byPrefix() {
            RadixTrie<String> t = of(ROMAN);
            assertEquals(List.of("romane", "romanus", "romulus"), t.keysWithPrefix("ro"));
            assertEquals(List.of("romane", "romanus"), t.keysWithPrefix("roman"));
            assertEquals(List.of("rubicon", "rubicundus"), t.keysWithPrefix("rubic"));
            assertEquals(List.of("rubicundus"), t.keysWithPrefix("rubicundus"));
        }

        @Test
        @DisplayName("접두사가 간선 중간에서 끝나도 된다")
        void prefixEndsInsideAnEdge() {
            // 여기가 09번과 갈리는 지점이다.
            // "romane" 만 있으면 뿌리의 유일한 자식이 간선 'romane' 하나다.
            // "rom" 에서 멈출 노드가 **없다.** 그래도 답은 나와야 한다.
            RadixTrie<String> t = of("romane");
            assertEquals(List.of("romane"), t.keysWithPrefix("r"));
            assertEquals(List.of("romane"), t.keysWithPrefix("rom"));
            assertEquals(List.of("romane"), t.keysWithPrefix("roman"));
            assertEquals(List.of("romane"), t.keysWithPrefix("romane"));
            assertEquals(List.of(), t.keysWithPrefix("rome"));
            assertEquals(List.of(), t.keysWithPrefix("romanes"));
        }

        @Test
        @DisplayName("간선 중간의 접두사가 여럿을 덮기도 한다")
        void midEdgePrefixCoversMany() {
            RadixTrie<String> t = of(ROMAN);
            assertEquals(t.keys(), t.keysWithPrefix("r"));
            assertEquals(List.of("romane", "romanus", "romulus"), t.keysWithPrefix("rom"));
            assertEquals(List.of("rubens", "ruber"), t.keysWithPrefix("rube"));
        }

        @Test
        @DisplayName("빈 접두사는 전부다")
        void emptyPrefixIsAll() {
            RadixTrie<String> t = of(ROMAN);
            assertEquals(t.keys(), t.keysWithPrefix(""));
        }

        @Test
        @DisplayName("없는 접두사는 빈 리스트다")
        void missingPrefix() {
            RadixTrie<String> t = of(ROMAN);
            assertEquals(List.of(), t.keysWithPrefix("z"));
            assertEquals(List.of(), t.keysWithPrefix("rz"));
            assertEquals(List.of(), t.keysWithPrefix("romanez"));
        }

        @Test
        @DisplayName("접두사 자신이 키면 포함된다")
        void includesPrefixItself() {
            RadixTrie<String> t = of("test", "tester", "testify");
            assertEquals(List.of("test", "tester", "testify"), t.keysWithPrefix("test"));
        }
    }

    @Nested
    @DisplayName("countWithPrefix")
    class CountWithPrefix {

        @Test
        @DisplayName("keysWithPrefix 의 개수와 같다")
        void agreesWithKeys() {
            RadixTrie<String> t = of(ROMAN);
            for (String p : List.of("", "r", "ro", "rom", "roman", "romane", "rub",
                    "rubic", "rubicundus", "z", "romanez")) {
                assertEquals(t.keysWithPrefix(p).size(), t.countWithPrefix(p),
                        "접두사 '" + p + "' 에서 두 답이 갈린다");
            }
        }

        @Test
        @DisplayName("구체적인 값")
        void concreteValues() {
            RadixTrie<String> t = of(ROMAN);
            assertEquals(7, t.countWithPrefix(""));
            assertEquals(7, t.countWithPrefix("r"));
            assertEquals(3, t.countWithPrefix("rom"));
            assertEquals(2, t.countWithPrefix("roman"));
            assertEquals(4, t.countWithPrefix("rub"));
            assertEquals(2, t.countWithPrefix("rubic"));
            assertEquals(0, t.countWithPrefix("z"));
        }

        @Test
        @DisplayName("빈 접두사는 size 와 같다")
        void emptyPrefixEqualsSize() {
            RadixTrie<String> t = of(ROMAN);
            assertEquals(t.size(), t.countWithPrefix(""));
        }
    }

    @Nested
    @DisplayName("longestPrefixOf")
    class LongestPrefixOf {

        private RadixTrie<String> sample() {
            return of("", "a", "ab", "abcd", "b");
        }

        @Test
        @DisplayName("가장 긴 것이 이긴다")
        void longestWins() {
            RadixTrie<String> t = sample();
            assertEquals("", t.longestPrefixOf(""));
            assertEquals("a", t.longestPrefixOf("a"));
            assertEquals("ab", t.longestPrefixOf("ab"));
            assertEquals("ab", t.longestPrefixOf("abc"));
            assertEquals("abcd", t.longestPrefixOf("abcd"));
            assertEquals("abcd", t.longestPrefixOf("abcde"));
            assertEquals("b", t.longestPrefixOf("bx"));
            assertEquals("", t.longestPrefixOf("c"));
        }

        @Test
        @DisplayName("빈 키가 없으면 못 찾을 수 있다")
        void nullWhenNothingMatches() {
            RadixTrie<String> t = of("ab", "abcd");
            assertNull(t.longestPrefixOf(""));
            assertNull(t.longestPrefixOf("a"));
            assertNull(t.longestPrefixOf("z"));
            assertEquals("ab", t.longestPrefixOf("ab"));
            assertEquals("ab", t.longestPrefixOf("abc"));
            assertEquals("abcd", t.longestPrefixOf("abcdz"));
        }

        @Test
        @DisplayName("간선을 반만 타고는 답이 될 수 없다")
        void halfEdgeIsNotAKey() {
            // 'roman' 은 간선 중간이다. 키가 끝나는 자리가 아니다.
            RadixTrie<String> t = of("romane", "romanus");
            assertNull(t.longestPrefixOf("romanx"));
            assertNull(t.longestPrefixOf("roman"));
            assertEquals("romane", t.longestPrefixOf("romanes"));
        }

        @Test
        @DisplayName("containsKey 와 다르다")
        void differsFromContainsKey() {
            RadixTrie<String> t = of("ab");
            assertFalse(t.containsKey("abc"));
            assertEquals("ab", t.longestPrefixOf("abc"));
        }

        @Test
        @DisplayName("지우면 그 다음으로 긴 것이 답이 된다")
        void fallsBackAfterRemove() {
            RadixTrie<String> t = of("a", "ab", "abc");
            assertEquals("abc", t.longestPrefixOf("abcd"));
            t.remove("abc");
            assertEquals("ab", t.longestPrefixOf("abcd"));
            t.remove("ab");
            assertEquals("a", t.longestPrefixOf("abcd"));
            t.remove("a");
            assertNull(t.longestPrefixOf("abcd"));
        }
    }

    @Nested
    @DisplayName("remove")
    class Remove {

        @Test
        @DisplayName("있으면 지우고 값을 준다")
        void removeExisting() {
            RadixTrie<String> t = of("apple");
            assertEquals("v:apple", t.remove("apple"));
            assertFalse(t.containsKey("apple"));
            assertEquals(0, t.size());
            assertTrue(t.isEmpty());
        }

        @Test
        @DisplayName("두 번 지우면 두 번째는 null")
        void removeTwice() {
            RadixTrie<String> t = of("apple");
            assertEquals("v:apple", t.remove("apple"));
            assertNull(t.remove("apple"));
            assertEquals(0, t.size());
        }

        @Test
        @DisplayName("접두사인 키를 지워도 긴 키는 남는다")
        void removingPrefixKeepsLonger() {
            RadixTrie<String> t = of("test", "tester");
            assertEquals("v:test", t.remove("test"));
            assertFalse(t.containsKey("test"));
            assertEquals("v:tester", t.get("tester"), "test 노드를 끊으면 tester 가 같이 사라진다");
            assertEquals(1, t.size());
        }

        @Test
        @DisplayName("긴 키를 지워도 접두사 키는 남는다")
        void removingLongerKeepsPrefix() {
            RadixTrie<String> t = of("test", "tester");
            assertEquals("v:tester", t.remove("tester"));
            assertEquals("v:test", t.get("test"));
            assertEquals(1, t.size());
        }

        @Test
        @DisplayName("가지 하나만 지워도 다른 가지는 그대로다")
        void removingOneBranch() {
            RadixTrie<String> t = of(ROMAN);
            assertEquals("v:romulus", t.remove("romulus"));
            assertEquals(List.of("romane", "romanus"), t.keysWithPrefix("ro"));
            assertEquals(6, t.size());
            assertEquals(2, t.countWithPrefix("rom"));
            assertEquals(4, t.countWithPrefix("rub"));
        }

        @Test
        @DisplayName("마지막 키를 지우면 경로가 사라진다")
        void removingLastClearsPath() {
            RadixTrie<String> t = of("romane");
            t.remove("romane");
            assertEquals(0, t.countWithPrefix("r"));
            assertEquals(List.of(), t.keys());
            assertTrue(t.root.children.isEmpty(), "뿌리 아래가 통째로 끊겨야 한다");
        }

        @Test
        @DisplayName("빈 문자열도 지워진다")
        void removeEmptyString() {
            RadixTrie<String> t = of("", "a");
            assertEquals("v:", t.remove(""));
            assertFalse(t.containsKey(""));
            assertTrue(t.containsKey("a"));
            assertEquals(1, t.size());
            assertNull(t.longestPrefixOf("zzz"));
        }

        @Test
        @DisplayName("지운 뒤 countWithPrefix 가 따라간다")
        void countFollowsRemove() {
            RadixTrie<String> t = of("car", "card", "care");
            assertEquals(3, t.countWithPrefix("car"));
            t.remove("card");
            assertEquals(2, t.countWithPrefix("car"));
            t.remove("car");
            assertEquals(1, t.countWithPrefix("car"));
            assertEquals(1, t.countWithPrefix("ca"));
            t.remove("care");
            assertEquals(0, t.countWithPrefix("car"));
            assertEquals(0, t.countWithPrefix(""));
        }

        @Test
        @DisplayName("지웠다 다시 넣기")
        void removeThenReinsert() {
            RadixTrie<String> t = of("test", "tester", "testify");
            t.remove("tester");
            t.put("tester", "again");
            assertEquals(3, t.size());
            assertEquals("again", t.get("tester"));
            assertEquals(List.of("test", "tester", "testify"), t.keys());
        }
    }

    @Nested
    @DisplayName("clear")
    class Clear {

        @Test
        @DisplayName("전부 사라진다")
        void clearsEverything() {
            RadixTrie<String> t = of(ROMAN);
            t.clear();
            assertEquals(0, t.size());
            assertTrue(t.isEmpty());
            assertNull(t.get("romane"));
            assertEquals(List.of(), t.keys());
            assertEquals(0, t.nodeCount());
        }

        @Test
        @DisplayName("비운 뒤에도 쓸 수 있다")
        void usableAfterClear() {
            RadixTrie<String> t = of("romane");
            t.clear();
            t.put("dog", "d");
            assertEquals(1, t.size());
            assertEquals("d", t.get("dog"));
            assertNull(t.get("romane"));
        }
    }

    @Nested
    @DisplayName("null 인자")
    class NullArgs {

        @Test
        @DisplayName("전부 IllegalArgumentException")
        void allReject() {
            RadixTrie<String> t = new RadixTrie<>();
            assertThrows(IllegalArgumentException.class, () -> t.put(null, "v"));
            assertThrows(IllegalArgumentException.class, () -> t.get(null));
            assertThrows(IllegalArgumentException.class, () -> t.containsKey(null));
            assertThrows(IllegalArgumentException.class, () -> t.remove(null));
            assertThrows(IllegalArgumentException.class, () -> t.keysWithPrefix(null));
            assertThrows(IllegalArgumentException.class, () -> t.countWithPrefix(null));
            assertThrows(IllegalArgumentException.class, () -> t.longestPrefixOf(null));
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class RandomCrossCheck {

        @Test
        @DisplayName("TreeMap 과 6000 스텝을 대조한다")
        void matchesTreeMap() {
            Random rnd = new Random(20260814L);
            RadixTrie<String> trie = new RadixTrie<>();
            TreeMap<String, String> ref = new TreeMap<>();

            for (int step = 0; step < 6000; step++) {
                String k = randomKey(rnd);
                if (rnd.nextInt(4) == 0) {
                    assertEquals(ref.remove(k), trie.remove(k),
                            "remove(\"" + k + "\") 가 갈렸다 (step " + step + ")");
                } else {
                    String v = "v" + step;
                    assertEquals(ref.put(k, v), trie.put(k, v),
                            "put(\"" + k + "\") 의 예전 값이 갈렸다 (step " + step + ")");
                }
                assertEquals(ref.size(), trie.size(), "size 가 갈렸다 (step " + step + ")");
                assertEquals(ref.get(k), trie.get(k), "get(\"" + k + "\") 가 갈렸다");
            }

            assertEquals(new ArrayList<>(ref.keySet()), trie.keys());
            for (String p : List.of("", "a", "ab", "abc", "b", "cc", "z")) {
                List<String> expected = new ArrayList<>();
                for (String s : ref.keySet()) {
                    if (s.startsWith(p)) {
                        expected.add(s);
                    }
                }
                assertEquals(expected, trie.keysWithPrefix(p), "접두사 '" + p + "'");
                assertEquals(expected.size(), trie.countWithPrefix(p), "접두사 '" + p + "' 개수");
            }
        }

        @Test
        @DisplayName("longestPrefixOf 를 전수 조사와 대조한다")
        void longestPrefixMatchesBruteForce() {
            Random rnd = new Random(4242L);
            RadixTrie<String> trie = new RadixTrie<>();
            TreeMap<String, String> ref = new TreeMap<>();
            for (int i = 0; i < 400; i++) {
                String k = randomKey(rnd);
                trie.put(k, "v");
                ref.put(k, "v");
            }
            for (int q = 0; q < 2000; q++) {
                String s = randomKey(rnd);
                String expected = null;
                for (int len = s.length(); len >= 0; len--) {
                    if (ref.containsKey(s.substring(0, len))) {
                        expected = s.substring(0, len);
                        break;
                    }
                }
                assertEquals(expected, trie.longestPrefixOf(s), "longestPrefixOf(\"" + s + "\")");
            }
        }

        private static String randomKey(Random rnd) {
            int len = rnd.nextInt(7);
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                sb.append((char) ('a' + rnd.nextInt(3)));
            }
            return sb.toString();
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(20)
        @DisplayName("10만 개를 넣고 전부 찾는다")
        void manyKeys() {
            RadixTrie<String> t = new RadixTrie<>();
            List<String> keys = new ArrayList<>();
            for (int i = 0; i < 100_000; i++) {
                keys.add(encode(i));
            }
            for (String k : keys) {
                t.put(k, k);
            }
            assertEquals(100_000, t.size());

            Collections.shuffle(keys, new Random(7L));
            for (String k : keys) {
                assertEquals(k, t.get(k));
                assertNull(t.get(k + "zzz"));
            }
        }

        @Test
        @Timeout(20)
        @DisplayName("countWithPrefix 는 부분 트리를 훑지 않는다")
        void countDoesNotWalkSubtree() {
            // 10만 개가 전부 "a" 로 시작한다. 아래를 훑어 세면 질의 하나가 10만 걸음이다.
            RadixTrie<String> t = new RadixTrie<>();
            for (int i = 0; i < 100_000; i++) {
                t.put("a" + encode(i), "v");
            }
            for (int q = 0; q < 100_000; q++) {
                assertEquals(100_000, t.countWithPrefix("a"));
            }
        }

        @Test
        @Timeout(20)
        @DisplayName("longestPrefixOf 는 키 수가 아니라 길이에만 의존한다")
        void longestPrefixIsCheap() {
            RadixTrie<String> t = new RadixTrie<>();
            for (int i = 0; i < 100_000; i++) {
                t.put(encode(i), "v");
            }
            for (int q = 0; q < 200_000; q++) {
                assertEquals("aaaaaa", t.longestPrefixOf("aaaaaazzz"));
            }
        }
    }
}
