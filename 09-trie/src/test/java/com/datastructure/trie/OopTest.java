package com.datastructure.trie;

import com.datastructure.trie.oop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("트라이 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("ArrayTrie")
    class ArrayTrieTest {

        private TrieInterface trie;

        @BeforeEach
        void setUp() {
            trie = new ArrayTrie();
        }

        @Test
        @DisplayName("01. 기본 삽입/검색")
        void test01_insertSearch() {
            // TODO: insert(), search() 구현 후 테스트
            // trie.insert("apple");
            // assertThat(trie.search("apple")).isTrue();
        }

        @Test
        @DisplayName("02. startsWith")
        void test02_startsWith() {
            // TODO: startsWith() 구현 후 테스트
            // trie.insert("apple");
            // assertThat(trie.startsWith("app")).isTrue();
        }

        @Test
        @DisplayName("03. 삭제")
        void test03_delete() {
            // TODO: delete() 구현 후 테스트
            // trie.insert("apple");
            // trie.delete("apple");
            // assertThat(trie.search("apple")).isFalse();
        }

        @Test
        @DisplayName("04. 자동완성")
        void test04_autocomplete() {
            // TODO: autocomplete() 구현 후 테스트
            // trie.insert("car");
            // trie.insert("card");
            // assertThat(trie.autocomplete("car")).contains("car", "card");
        }

        @Test
        @DisplayName("05. 빠른 성능 (배열 기반)")
        void test05_performance() {
            // TODO: 성능 테스트
            // for (int i = 0; i < 100000; i++) {
            //     trie.insert("word" + i);
            // }
            // assertThat(trie.search("word50000")).isTrue();
        }
    }

    @Nested
    @DisplayName("MapTrie")
    class MapTrieTest {

        private TrieInterface trie;

        @BeforeEach
        void setUp() {
            trie = new MapTrie();
        }

        @Test
        @DisplayName("06. 기본 삽입/검색")
        void test06_insertSearch() {
            // TODO: insert(), search() 구현 후 테스트
            // trie.insert("apple");
            // assertThat(trie.search("apple")).isTrue();
        }

        @Test
        @DisplayName("07. 유니코드 지원")
        void test07_unicodeSupport() {
            // TODO: 유니코드 테스트 (Map 기반)
            // trie.insert("한글");
            // trie.insert("日本語");
            // assertThat(trie.search("한글")).isTrue();
            // assertThat(trie.search("日本語")).isTrue();
        }

        @Test
        @DisplayName("08. 대소문자 구분")
        void test08_caseSensitive() {
            // TODO: 대소문자 테스트
            // trie.insert("Apple");
            // trie.insert("apple");
            // assertThat(trie.search("Apple")).isTrue();
            // assertThat(trie.search("apple")).isTrue();
            // assertThat(trie.search("APPLE")).isFalse();
        }

        @Test
        @DisplayName("09. 메모리 효율 (희소 데이터)")
        void test09_memoryEfficiency() {
            // TODO: 희소 데이터 테스트
            // 다양한 문자로 시작하는 단어들
            // trie.insert("apple");
            // trie.insert("banana");
            // trie.insert("zebra");
        }

        @Test
        @DisplayName("10. 정렬된 자동완성 (TreeMap)")
        void test10_sortedAutocomplete() {
            // TODO: 정렬된 결과 테스트
            // trie.insert("car");
            // trie.insert("card");
            // trie.insert("care");
            // List<String> results = trie.autocomplete("car");
            // assertThat(results).isSorted();
        }
    }

    @Nested
    @DisplayName("WordDictionary (와일드카드)")
    class WordDictionaryTest {

        private WordDictionary dict;

        @BeforeEach
        void setUp() {
            dict = new WordDictionary();
        }

        @Test
        @DisplayName("11. 기본 추가/검색")
        void test11_addSearch() {
            // TODO: addWord(), search() 구현 후 테스트
            // dict.addWord("bad");
            // assertThat(dict.search("bad")).isTrue();
        }

        @Test
        @DisplayName("12. 와일드카드 검색")
        void test12_wildcardSearch() {
            // TODO: 와일드카드 테스트
            // dict.addWord("bad");
            // dict.addWord("dad");
            // dict.addWord("mad");
            // assertThat(dict.search(".ad")).isTrue();
            // assertThat(dict.search("b..")).isTrue();
        }

        @Test
        @DisplayName("13. 전체 와일드카드")
        void test13_allWildcard() {
            // TODO: 전체 와일드카드 테스트
            // dict.addWord("abc");
            // assertThat(dict.search("...")).isTrue();
            // assertThat(dict.search("....")).isFalse();
        }

        @Test
        @DisplayName("14. 혼합 검색")
        void test14_mixedSearch() {
            // TODO: 혼합 테스트
            // dict.addWord("apple");
            // assertThat(dict.search("a.ple")).isTrue();
            // assertThat(dict.search("ap.le")).isTrue();
            // assertThat(dict.search("appl.")).isTrue();
        }

        @Test
        @DisplayName("15. 빈 패턴")
        void test15_emptyPattern() {
            // TODO: 빈 패턴 테스트
            // dict.addWord("");
            // assertThat(dict.search("")).isTrue();
            // assertThat(dict.search(".")).isFalse();
        }
    }

    @Nested
    @DisplayName("인터페이스 공통")
    class InterfaceCommonTest {

        @Test
        @DisplayName("16. 다형성")
        void test16_polymorphism() {
            // TODO: 다형성 테스트
            // TrieInterface[] tries = {new ArrayTrie(), new MapTrie()};
            // for (TrieInterface trie : tries) {
            //     trie.insert("test");
            //     assertThat(trie.search("test")).isTrue();
            // }
        }

        @Test
        @DisplayName("17. Iterator 지원")
        void test17_iterator() {
            // TODO: Iterator 테스트
            // TrieInterface trie = new MapTrie();
            // trie.insert("apple");
            // trie.insert("banana");
            // int count = 0;
            // for (String word : trie) {
            //     count++;
            // }
            // assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("18. Stream 지원")
        void test18_stream() {
            // TODO: stream() 테스트
            // long count = trie.stream().count();
            // assertThat(count).isEqualTo(expectedCount);
        }

        @Test
        @DisplayName("19. equals")
        void test19_equals() {
            // TODO: equals() 테스트
            // TrieInterface t1 = new ArrayTrie();
            // TrieInterface t2 = new ArrayTrie();
            // t1.insert("apple");
            // t2.insert("apple");
            // assertThat(t1).isEqualTo(t2);
        }

        @Test
        @DisplayName("20. hashCode")
        void test20_hashCode() {
            // TODO: hashCode() 테스트
            // assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        }
    }

    @Nested
    @DisplayName("카운팅 기능")
    class CountingTest {

        @Test
        @DisplayName("21. 중복 단어 카운팅")
        void test21_duplicateCount() {
            // TODO: 중복 카운팅 테스트
            // trie.insert("apple");
            // trie.insert("apple");
            // assertThat(trie.countWordsEqualTo("apple")).isEqualTo(2);
        }

        @Test
        @DisplayName("22. 접두사 카운팅")
        void test22_prefixCount() {
            // TODO: 접두사 카운팅 테스트
            // trie.insert("apple");
            // trie.insert("app");
            // trie.insert("application");
            // assertThat(trie.countWordsStartingWith("app")).isEqualTo(3);
        }

        @Test
        @DisplayName("23. 삭제 후 카운트 감소")
        void test23_countAfterDelete() {
            // TODO: 삭제 후 카운트 테스트
            // trie.insert("apple");
            // trie.insert("apple");
            // trie.delete("apple");  // 하나만 삭제
            // assertThat(trie.countWordsEqualTo("apple")).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("24. 최장 공통 접두사")
        void test24_longestCommonPrefix() {
            // TODO: longestCommonPrefix() 테스트
            // trie.insert("flower");
            // trie.insert("flow");
            // trie.insert("flight");
            // assertThat(trie.longestCommonPrefix()).isEqualTo("fl");
        }

        @Test
        @DisplayName("25. 모든 단어 반환")
        void test25_getAllWords() {
            // TODO: getAllWords() 테스트
            // trie.insert("a");
            // trie.insert("b");
            // assertThat(trie.getAllWords()).containsExactlyInAnyOrder("a", "b");
        }

        @Test
        @DisplayName("26. clear")
        void test26_clear() {
            // TODO: clear() 테스트
            // trie.insert("apple");
            // trie.clear();
            // assertThat(trie.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("27. toString")
        void test27_toString() {
            // TODO: toString() 테스트
            // trie.insert("apple");
            // assertThat(trie.toString()).isNotEmpty();
        }

        @Test
        @DisplayName("28. 단일 문자 단어")
        void test28_singleChar() {
            // TODO: 단일 문자 테스트
            // trie.insert("a");
            // assertThat(trie.search("a")).isTrue();
            // assertThat(trie.startsWith("a")).isTrue();
        }

        @Test
        @DisplayName("29. 긴 단어")
        void test29_longWord() {
            // TODO: 긴 단어 테스트
            // String longWord = "a".repeat(1000);
            // trie.insert(longWord);
            // assertThat(trie.search(longWord)).isTrue();
        }

        @Test
        @DisplayName("30. 자동완성 정렬")
        void test30_sortedAutocomplete() {
            // TODO: 정렬된 자동완성 테스트
            // trie.insert("zebra");
            // trie.insert("zoo");
            // trie.insert("zone");
            // List<String> results = trie.autocomplete("zo");
            // assertThat(results).isSorted();
        }
    }
}
