package com.datastructure.trie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Trie 계약. MapTrie 와 ArrayTrie 가 똑같이 지켜야 하는 것만 여기 있다.
 *
 * a~z 만 쓴다. ArrayTrie 가 그것만 담을 수 있기 때문이다.
 * 그 제약 자체를 확인하는 테스트는 ArrayTrieTest 에 있다.
 */
abstract class TrieContractTest {

    protected abstract Trie create();

    protected Trie of(String... words) {
        Trie t = create();
        for (String w : words) {
            t.insert(w);
        }
        return t;
    }

    @Nested
    @DisplayName("빈 트라이")
    class Empty {

        @Test
        @DisplayName("아무것도 없다")
        void nothing() {
            Trie t = create();
            assertEquals(0, t.size());
            assertTrue(t.isEmpty());
            assertFalse(t.contains("a"));
            assertFalse(t.contains(""));
            assertFalse(t.startsWith("a"));
            assertEquals(List.of(), t.keysWithPrefix("a"));
            assertEquals(0, t.countWithPrefix("a"));
        }

        @Test
        @DisplayName("빈 접두사도 빈 결과다")
        void emptyPrefixOnEmptyTrie() {
            Trie t = create();
            assertEquals(List.of(), t.keysWithPrefix(""));
            assertEquals(0, t.countWithPrefix(""));
            assertFalse(t.startsWith(""));
        }

        @Test
        @DisplayName("없는 것은 못 지운다")
        void removeMissing() {
            assertFalse(create().remove("a"));
        }
    }

    @Nested
    @DisplayName("insert 와 contains")
    class Insert {

        @Test
        @DisplayName("넣으면 있다")
        void insertThenContains() {
            Trie t = of("apple");
            assertTrue(t.contains("apple"));
            assertEquals(1, t.size());
        }

        @Test
        @DisplayName("같은 단어를 두 번 넣어도 하나다")
        void duplicateInsert() {
            Trie t = of("apple", "apple", "apple");
            assertEquals(1, t.size());
            assertTrue(t.contains("apple"));
        }

        @Test
        @DisplayName("접두사는 단어가 아니다")
        void prefixIsNotWord() {
            Trie t = of("apple");
            assertFalse(t.contains("app"));
            assertFalse(t.contains("appl"));
            assertFalse(t.contains("a"));
            assertTrue(t.contains("apple"));
        }

        @Test
        @DisplayName("접두사가 단어이기도 하면 둘 다 있다")
        void bothPrefixAndWord() {
            Trie t = of("app", "apple");
            assertEquals(2, t.size());
            assertTrue(t.contains("app"));
            assertTrue(t.contains("apple"));
            assertFalse(t.contains("appl"));
        }

        @Test
        @DisplayName("더 긴 것을 넣어도 원래 단어는 남는다")
        void longerDoesNotEraseShorter() {
            Trie t = of("app");
            t.insert("apple");
            assertTrue(t.contains("app"), "app 의 end 표시가 살아 있어야 한다");
            assertTrue(t.contains("apple"));
        }

        @Test
        @DisplayName("빈 문자열도 단어다")
        void emptyStringIsAWord() {
            Trie t = of("");
            assertEquals(1, t.size());
            assertTrue(t.contains(""));
            assertTrue(t.startsWith(""));
            assertEquals(List.of(""), t.keysWithPrefix(""));
            assertEquals(1, t.countWithPrefix(""));
        }

        @Test
        @DisplayName("한 글자짜리들")
        void singleChars() {
            Trie t = of("a", "b", "c");
            assertEquals(3, t.size());
            assertTrue(t.contains("a"));
            assertTrue(t.contains("b"));
            assertTrue(t.contains("c"));
            assertFalse(t.contains("d"));
        }
    }

    @Nested
    @DisplayName("startsWith")
    class StartsWith {

        @Test
        @DisplayName("단어의 모든 접두사가 true 다")
        void everyPrefix() {
            Trie t = of("apple");
            for (String p : List.of("", "a", "ap", "app", "appl", "apple")) {
                assertTrue(t.startsWith(p), "접두사 '" + p + "' 는 true 여야 한다");
            }
        }

        @Test
        @DisplayName("단어보다 길면 false 다")
        void longerThanWord() {
            Trie t = of("apple");
            assertFalse(t.startsWith("apples"));
            assertFalse(t.startsWith("b"));
        }

        @Test
        @DisplayName("contains 와 다르다")
        void differsFromContains() {
            Trie t = of("apple");
            assertTrue(t.startsWith("app"));
            assertFalse(t.contains("app"));
        }
    }

    @Nested
    @DisplayName("keysWithPrefix")
    class KeysWithPrefix {

        private Trie sample() {
            return of("car", "card", "care", "careful", "cars", "cat", "dog");
        }

        @Test
        @DisplayName("사전순으로 나온다")
        void lexicographic() {
            assertEquals(List.of("car", "card", "care", "careful", "cars"),
                    sample().keysWithPrefix("car"));
        }

        @Test
        @DisplayName("접두사 자신이 단어면 포함된다")
        void includesPrefixItself() {
            assertTrue(sample().keysWithPrefix("car").contains("car"));
        }

        @Test
        @DisplayName("빈 접두사는 전부다")
        void emptyPrefixIsAll() {
            assertEquals(List.of("car", "card", "care", "careful", "cars", "cat", "dog"),
                    sample().keysWithPrefix(""));
        }

        @Test
        @DisplayName("없는 접두사는 빈 리스트다")
        void missingPrefix() {
            assertEquals(List.of(), sample().keysWithPrefix("z"));
            assertEquals(List.of(), sample().keysWithPrefix("carz"));
        }

        @Test
        @DisplayName("삽입 순서와 무관하다")
        void orderIndependent() {
            Trie a = of("cat", "car", "card");
            Trie b = of("card", "cat", "car");
            assertEquals(a.keysWithPrefix("ca"), b.keysWithPrefix("ca"));
            assertEquals(List.of("car", "card", "cat"), a.keysWithPrefix("ca"));
        }

        @Test
        @DisplayName("자기 자신만 맞는 접두사")
        void exactOnly() {
            assertEquals(List.of("dog"), sample().keysWithPrefix("dog"));
        }
    }

    @Nested
    @DisplayName("countWithPrefix")
    class CountWithPrefix {

        private Trie sample() {
            return of("car", "card", "care", "careful", "cars", "cat", "dog");
        }

        @Test
        @DisplayName("keysWithPrefix 의 개수와 같다")
        void agreesWithKeys() {
            Trie t = sample();
            for (String p : List.of("", "c", "ca", "car", "care", "dog", "z", "carz")) {
                assertEquals(t.keysWithPrefix(p).size(), t.countWithPrefix(p),
                        "접두사 '" + p + "' 에서 두 답이 갈린다");
            }
        }

        @Test
        @DisplayName("구체적인 값")
        void concreteValues() {
            Trie t = sample();
            assertEquals(7, t.countWithPrefix(""));
            assertEquals(6, t.countWithPrefix("ca"));
            assertEquals(5, t.countWithPrefix("car"));
            assertEquals(2, t.countWithPrefix("care"));
            assertEquals(1, t.countWithPrefix("cat"));
            assertEquals(0, t.countWithPrefix("z"));
        }

        @Test
        @DisplayName("빈 접두사는 size 와 같다")
        void emptyPrefixEqualsSize() {
            Trie t = sample();
            assertEquals(t.size(), t.countWithPrefix(""));
        }
    }

    @Nested
    @DisplayName("remove")
    class Remove {

        @Test
        @DisplayName("있으면 지우고 true")
        void removeExisting() {
            Trie t = of("apple");
            assertTrue(t.remove("apple"));
            assertFalse(t.contains("apple"));
            assertEquals(0, t.size());
            assertTrue(t.isEmpty());
        }

        @Test
        @DisplayName("두 번 지우면 두 번째는 false")
        void removeTwice() {
            Trie t = of("apple");
            assertTrue(t.remove("apple"));
            assertFalse(t.remove("apple"));
            assertEquals(0, t.size());
        }

        @Test
        @DisplayName("접두사인 단어를 지워도 긴 단어는 남는다")
        void removingPrefixKeepsLonger() {
            Trie t = of("app", "apple");
            assertTrue(t.remove("app"));
            assertFalse(t.contains("app"));
            assertTrue(t.contains("apple"), "a-p-p 노드를 끊으면 apple 이 같이 사라진다");
            assertTrue(t.startsWith("app"));
            assertEquals(1, t.size());
        }

        @Test
        @DisplayName("긴 단어를 지워도 접두사 단어는 남는다")
        void removingLongerKeepsPrefix() {
            Trie t = of("app", "apple");
            assertTrue(t.remove("apple"));
            assertTrue(t.contains("app"));
            assertFalse(t.contains("apple"));
            assertEquals(1, t.size());
        }

        @Test
        @DisplayName("마지막 단어를 지우면 경로가 사라진다")
        void removingLastClearsPath() {
            Trie t = of("apple");
            t.remove("apple");
            assertFalse(t.startsWith("a"), "아무 단어도 없으면 접두사도 없어야 한다");
            assertEquals(List.of(), t.keysWithPrefix(""));
        }

        @Test
        @DisplayName("가지 하나만 지워도 다른 가지는 그대로다")
        void removingOneBranch() {
            Trie t = of("car", "cat", "dog");
            assertTrue(t.remove("car"));
            assertEquals(List.of("cat"), t.keysWithPrefix("ca"));
            assertEquals(List.of("cat", "dog"), t.keysWithPrefix(""));
            assertEquals(2, t.size());
            assertFalse(t.startsWith("car"));
            assertTrue(t.startsWith("ca"));
        }

        @Test
        @DisplayName("빈 문자열도 지워진다")
        void removeEmptyString() {
            Trie t = of("", "a");
            assertTrue(t.remove(""));
            assertFalse(t.contains(""));
            assertTrue(t.contains("a"));
            assertEquals(1, t.size());
        }

        @Test
        @DisplayName("지운 뒤 countWithPrefix 가 따라간다")
        void countFollowsRemove() {
            Trie t = of("car", "card", "care");
            assertEquals(3, t.countWithPrefix("car"));
            t.remove("card");
            assertEquals(2, t.countWithPrefix("car"));
            assertEquals(2, t.countWithPrefix(""));
            t.remove("car");
            assertEquals(1, t.countWithPrefix("car"));
            t.remove("care");
            assertEquals(0, t.countWithPrefix("car"));
            assertEquals(0, t.countWithPrefix(""));
        }

        @Test
        @DisplayName("지웠다 다시 넣기")
        void removeThenReinsert() {
            Trie t = of("apple", "app");
            t.remove("apple");
            t.insert("apple");
            assertEquals(2, t.size());
            assertTrue(t.contains("apple"));
            assertTrue(t.contains("app"));
            assertEquals(List.of("app", "apple"), t.keysWithPrefix("app"));
        }
    }

    @Nested
    @DisplayName("clear")
    class Clear {

        @Test
        @DisplayName("전부 사라진다")
        void clearsEverything() {
            Trie t = of("car", "cat", "dog");
            t.clear();
            assertEquals(0, t.size());
            assertTrue(t.isEmpty());
            assertFalse(t.contains("car"));
            assertFalse(t.startsWith("c"));
            assertEquals(List.of(), t.keysWithPrefix(""));
        }

        @Test
        @DisplayName("비운 뒤에도 쓸 수 있다")
        void usableAfterClear() {
            Trie t = of("car");
            t.clear();
            t.insert("dog");
            assertEquals(1, t.size());
            assertTrue(t.contains("dog"));
            assertFalse(t.contains("car"));
        }
    }

    @Nested
    @DisplayName("null 인자")
    class NullArgs {

        @Test
        @DisplayName("전부 IllegalArgumentException")
        void allReject() {
            Trie t = create();
            assertThrows(IllegalArgumentException.class, () -> t.insert(null));
            assertThrows(IllegalArgumentException.class, () -> t.contains(null));
            assertThrows(IllegalArgumentException.class, () -> t.startsWith(null));
            assertThrows(IllegalArgumentException.class, () -> t.remove(null));
            assertThrows(IllegalArgumentException.class, () -> t.keysWithPrefix(null));
            assertThrows(IllegalArgumentException.class, () -> t.countWithPrefix(null));
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class RandomCrossCheck {

        @Test
        @DisplayName("TreeSet 과 계속 같은 답을 낸다")
        void matchesTreeSet() {
            Random rnd = new Random(20260813L);
            Trie trie = create();
            TreeSet<String> ref = new TreeSet<>();

            for (int step = 0; step < 4000; step++) {
                String w = randomWord(rnd, 1 + rnd.nextInt(5));
                if (rnd.nextInt(3) == 0) {
                    assertEquals(ref.remove(w), trie.remove(w), "remove(\"" + w + "\") 가 갈렸다");
                } else {
                    trie.insert(w);
                    ref.add(w);
                }
                assertEquals(ref.size(), trie.size(), "size 가 갈렸다 (step " + step + ")");
            }

            assertEquals(new ArrayList<>(ref), trie.keysWithPrefix(""));
            for (char c = 'a'; c <= 'e'; c++) {
                String p = String.valueOf(c);
                List<String> expected = new ArrayList<>();
                for (String s : ref) {
                    if (s.startsWith(p)) {
                        expected.add(s);
                    }
                }
                assertEquals(expected, trie.keysWithPrefix(p), "접두사 '" + p + "'");
                assertEquals(expected.size(), trie.countWithPrefix(p), "접두사 '" + p + "' 개수");
            }
        }

        private String randomWord(Random rnd, int len) {
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                sb.append((char) ('a' + rnd.nextInt(5)));
            }
            return sb.toString();
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(15)
        @DisplayName("조회는 단어 수가 아니라 길이에만 의존한다")
        void lookupDependsOnLengthNotSize() {
            Trie t = create();
            List<String> words = new ArrayList<>();
            for (int i = 0; i < 100_000; i++) {
                words.add(encode(i));
            }
            for (String w : words) {
                t.insert(w);
            }
            assertEquals(100_000, t.size());

            Collections.shuffle(words, new Random(7L));
            for (String w : words) {
                assertTrue(t.contains(w));
            }
            for (String w : words) {
                assertFalse(t.contains(w + "zzz"));
            }
        }

        @Test
        @Timeout(15)
        @DisplayName("countWithPrefix 는 부분 트리를 훑지 않는다")
        void countDoesNotWalkSubtree() {
            // 10만 개가 전부 "a" 로 시작한다. 아래를 훑어 세면 질의 하나가 10만 걸음이다.
            Trie t = create();
            for (int i = 0; i < 100_000; i++) {
                t.insert("a" + encode(i));
            }
            for (int q = 0; q < 100_000; q++) {
                assertEquals(100_000, t.countWithPrefix("a"));
            }
        }

        /** 0 이상의 정수를 a~z 문자열로. 길이가 균일하도록 6자리 고정. */
        private static String encode(int n) {
            char[] buf = new char[6];
            for (int i = 5; i >= 0; i--) {
                buf[i] = (char) ('a' + (n % 26));
                n /= 26;
            }
            return new String(buf);
        }
    }
}
