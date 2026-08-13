# pop/Trie.java

배열 기반 트라이 (a-z 26자). insert/search/startsWith/delete + 카운트 + 자동완성 + 와일드카드 + LCP.

```java
package com.datastructure.trie.pop;

import java.util.*;

public class Trie {
    private static class Node {
        Node[] children = new Node[26];
        int wordCount = 0;
        int prefixCount = 0;
    }

    private final Node root;
    private int size;

    public Trie() {
        this.root = new Node();
        this.size = 0;
    }

    public void insert(String word) {
        validate(word);
        Node cur = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (cur.children[idx] == null) cur.children[idx] = new Node();
            cur = cur.children[idx];
            cur.prefixCount++;
        }
        cur.wordCount++;
        size++;
    }

    public boolean search(String word) {
        Node node = traverse(word);
        return node != null && node.wordCount > 0;
    }

    public boolean startsWith(String prefix) {
        return traverse(prefix) != null;
    }

    public int countWordsEqualTo(String word) {
        Node node = traverse(word);
        return node == null ? 0 : node.wordCount;
    }

    public int countWordsStartingWith(String prefix) {
        Node node = traverse(prefix);
        return node == null ? 0 : node.prefixCount;
    }

    public boolean delete(String word) {
        if (!search(word)) return false;
        Node cur = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            cur = cur.children[idx];
            cur.prefixCount--;
        }
        cur.wordCount--;
        size--;
        // 빈 노드 정리는 별도로 - prefixCount=0 노드는 부모에서 끊기
        cleanup(root, word, 0);
        return true;
    }

    private boolean cleanup(Node node, String word, int depth) {
        if (depth == word.length()) {
            return node.wordCount == 0 && node.prefixCount == 0;
        }
        int idx = word.charAt(depth) - 'a';
        Node child = node.children[idx];
        if (child != null && cleanup(child, word, depth + 1)) {
            node.children[idx] = null;
        }
        return node.wordCount == 0 && node.prefixCount == 0;
    }

    private Node traverse(String s) {
        if (s == null) return null;
        Node cur = root;
        for (char c : s.toCharArray()) {
            int idx = c - 'a';
            if (cur.children[idx] == null) return null;
            cur = cur.children[idx];
        }
        return cur;
    }

    public List<String> autocomplete(String prefix) {
        return autocomplete(prefix, Integer.MAX_VALUE);
    }

    public List<String> autocomplete(String prefix, int limit) {
        List<String> result = new ArrayList<>();
        Node node = traverse(prefix);
        if (node == null) return result;
        collect(node, new StringBuilder(prefix), result, limit);
        return result;
    }

    private void collect(Node node, StringBuilder path, List<String> result, int limit) {
        if (result.size() >= limit) return;
        if (node.wordCount > 0) {
            for (int i = 0; i < node.wordCount && result.size() < limit; i++) {
                result.add(path.toString());
            }
        }
        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                path.append((char) ('a' + i));
                collect(node.children[i], path, result, limit);
                path.deleteCharAt(path.length() - 1);
                if (result.size() >= limit) return;
            }
        }
    }

    public List<String> searchWithWildcard(String pattern) {
        List<String> result = new ArrayList<>();
        wildcardDFS(root, pattern, 0, new StringBuilder(), result);
        return result;
    }

    private void wildcardDFS(Node node, String pattern, int idx,
                             StringBuilder path, List<String> result) {
        if (idx == pattern.length()) {
            if (node.wordCount > 0) result.add(path.toString());
            return;
        }
        char c = pattern.charAt(idx);
        if (c == '.') {
            for (int i = 0; i < 26; i++) {
                if (node.children[i] != null) {
                    path.append((char) ('a' + i));
                    wildcardDFS(node.children[i], pattern, idx + 1, path, result);
                    path.deleteCharAt(path.length() - 1);
                }
            }
        } else {
            int i = c - 'a';
            if (node.children[i] != null) {
                path.append(c);
                wildcardDFS(node.children[i], pattern, idx + 1, path, result);
                path.deleteCharAt(path.length() - 1);
            }
        }
    }

    public String longestCommonPrefix() {
        StringBuilder sb = new StringBuilder();
        Node cur = root;
        while (true) {
            int childIdx = -1;
            int childCount = 0;
            for (int i = 0; i < 26; i++) {
                if (cur.children[i] != null) {
                    childCount++;
                    childIdx = i;
                }
            }
            if (childCount != 1 || cur.wordCount > 0) break;
            sb.append((char) ('a' + childIdx));
            cur = cur.children[childIdx];
        }
        return sb.toString();
    }

    public List<String> getAllWords() {
        return autocomplete("");
    }

    public int size() {
        return size;
    }

    private void validate(String word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("Word must be non-null and non-empty");
        }
        for (char c : word.toCharArray()) {
            if (c < 'a' || c > 'z') {
                throw new IllegalArgumentException("Only a-z allowed: " + word);
            }
        }
    }
}
```
