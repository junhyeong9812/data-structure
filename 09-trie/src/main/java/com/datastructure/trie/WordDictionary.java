package com.datastructure.trie;

/**
 * 와일드카드 검색을 지원하는 사전. '.' 은 아무 글자 하나와 맞는다.
 *
 * 지금까지의 조회는 전부 길이 한 줄이었다. 한 글자에 갈 곳이 하나로 정해져 있었다.
 * 여기서는 '.' 을 만나는 순간 갈 곳이 여러 갈래가 된다. 그래서 되돌아오기(백트래킹)가 필요해진다.
 *
 * | 패턴 | 비용 |
 * |---|---|
 * | "apple" | O(L) |
 * | "a...e" | 최악 O(자식수^점의개수) |
 * | "....." | 그 길이의 모든 단어를 훑는 것과 같다 |
 *
 * 이게 트라이가 정규식 검색의 토대가 되는 이유이자, 동시에 그 한계다.
 * 접두사가 고정될수록 싸고, 앞쪽부터 점이면 가지치기가 안 된다.
 * ("....e" 를 빠르게 하려면 뒤집은 문자열로 트라이를 하나 더 만드는 식이 된다.)
 *
 * MapTrie 를 그대로 쓴다. 저장 구조는 같고 묻는 방법만 다르다.
 */
public class WordDictionary {

    private final MapTrie trie = new MapTrie();

    public void addWord(String word) {
        // TODO 1: MapTrie 에 그대로 넣는다. 한 줄이다.
        //
        // 새 자료구조를 만드는 게 아니라는 것이 요점이다.
        // 저장은 트라이가 이미 다 한다.
        throw new UnsupportedOperationException("TODO 1: addWord");
    }

    public int size() {
        return trie.size();
    }

    /** '.' 은 아무 글자 하나와 맞는다. 패턴 전체가 단어 하나와 정확히 맞아야 true. */
    public boolean search(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("null 은 패턴이 아니다");
        }
        return search(trie.root, pattern, 0);
    }

    private static boolean search(MapTrie.Node node, String pattern, int i) {
        // TODO 2: 재귀로 훑는다.
        //
        //   - node 가 null 이면 그 길은 막혔다.
        //   - 패턴을 다 썼으면 **여기서 끝나는 단어가 있는지**(end)가 답이다.
        //     자식이 있느냐가 아니다. "app" 을 찾는데 "apple" 만 있으면 false 여야 한다.
        //   - '.' 이면 자식 전부를 시도하고 **하나라도 되면 true**.
        //   - 아니면 그 글자 자식으로만 내려간다.
        //
        // 되돌아오는 코드를 따로 쓰지 않는 것에 주목하라.
        // 재귀 호출이 false 를 반환하는 것 자체가 되돌아오기다.
        throw new UnsupportedOperationException("TODO 2: search");
    }
}
