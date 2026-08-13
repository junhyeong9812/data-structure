package com.datastructure.trie;

public class WordDictionary {

    private final MapTrie trie = new MapTrie();

    public void addWord(String word) {
        trie.insert(word);
    }

    public int size() {
        return trie.size();
    }

    public boolean search(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("null 은 패턴이 아니다");
        }
        return search(trie.root, pattern, 0);
    }

    private static boolean search(MapTrie.Node node, String pattern, int i) {
        if (node == null) {
            return false;
        }
        if (i == pattern.length()) {
            return node.end;
        }
        char c = pattern.charAt(i);
        if (c == '.') {
            for (MapTrie.Node child : node.children.values()) {
                if (search(child, pattern, i + 1)) {
                    return true;
                }
            }
            return false;
        }
        return search(node.children.get(c), pattern, i + 1);
    }
}
