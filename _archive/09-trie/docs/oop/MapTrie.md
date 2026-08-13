# oop/MapTrie.java

`Map<Character, Node>` 기반 트라이 구현. 유니코드 문자 지원.

```java
package com.datastructure.trie.oop;

import java.util.*;

public class MapTrie implements Trie {
    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        int wordCount = 0;
        int prefixCount = 0;
    }

    private final Node root;
    private int size;

    public MapTrie() {
        this.root = new Node();
        this.size = 0;
    }

    @Override
    public void insert(String word) {
        Objects.requireNonNull(word);
        if (word.isEmpty()) throw new IllegalArgumentException("Empty word");
        Node cur = root;
        for (char c : word.toCharArray()) {
            cur = cur.children.computeIfAbsent(c, k -> new Node());
            cur.prefixCount++;
        }
        cur.wordCount++;
        size++;
    }

    @Override
    public boolean search(String word) {
        Node node = traverse(word);
        return node != null && node.wordCount > 0;
    }

    @Override
    public boolean startsWith(String prefix) {
        return traverse(prefix) != null;
    }

    @Override
    public boolean delete(String word) {
        if (!search(word)) return false;
        Node cur = root;
        for (char c : word.toCharArray()) {
            cur = cur.children.get(c);
            cur.prefixCount--;
        }
        cur.wordCount--;
        size--;
        cleanup(root, word, 0);
        return true;
    }

    private boolean cleanup(Node node, String word, int depth) {
        if (depth == word.length()) {
            return node.wordCount == 0 && node.prefixCount == 0;
        }
        char c = word.charAt(depth);
        Node child = node.children.get(c);
        if (child != null && cleanup(child, word, depth + 1)) {
            node.children.remove(c);
        }
        return node.wordCount == 0 && node.prefixCount == 0 && node.children.isEmpty();
    }

    @Override
    public int countWordsEqualTo(String word) {
        Node node = traverse(word);
        return node == null ? 0 : node.wordCount;
    }

    @Override
    public int countWordsStartingWith(String prefix) {
        Node node = traverse(prefix);
        return node == null ? 0 : node.prefixCount;
    }

    @Override
    public List<String> autocomplete(String prefix) {
        return autocomplete(prefix, Integer.MAX_VALUE);
    }

    @Override
    public List<String> autocomplete(String prefix, int limit) {
        List<String> result = new ArrayList<>();
        Node node = traverse(prefix);
        if (node == null) return result;
        collect(node, new StringBuilder(prefix), result, limit);
        return result;
    }

    private void collect(Node node, StringBuilder path, List<String> result, int limit) {
        if (result.size() >= limit) return;
        for (int i = 0; i < node.wordCount && result.size() < limit; i++) {
            result.add(path.toString());
        }
        List<Character> keys = new ArrayList<>(node.children.keySet());
        Collections.sort(keys);
        for (char c : keys) {
            path.append(c);
            collect(node.children.get(c), path, result, limit);
            path.deleteCharAt(path.length() - 1);
            if (result.size() >= limit) return;
        }
    }

    @Override
    public List<String> searchWithWildcard(String pattern) {
        List<String> result = new ArrayList<>();
        wildcard(root, pattern, 0, new StringBuilder(), result);
        return result;
    }

    private void wildcard(Node node, String pattern, int idx,
                          StringBuilder path, List<String> result) {
        if (idx == pattern.length()) {
            if (node.wordCount > 0) result.add(path.toString());
            return;
        }
        char c = pattern.charAt(idx);
        if (c == '.') {
            for (Map.Entry<Character, Node> e : node.children.entrySet()) {
                path.append(e.getKey());
                wildcard(e.getValue(), pattern, idx + 1, path, result);
                path.deleteCharAt(path.length() - 1);
            }
        } else {
            Node child = node.children.get(c);
            if (child != null) {
                path.append(c);
                wildcard(child, pattern, idx + 1, path, result);
                path.deleteCharAt(path.length() - 1);
            }
        }
    }

    @Override
    public List<String> getAllWords() {
        return autocomplete("");
    }

    @Override
    public String longestCommonPrefix() {
        StringBuilder sb = new StringBuilder();
        Node cur = root;
        while (cur.children.size() == 1 && cur.wordCount == 0) {
            Map.Entry<Character, Node> e = cur.children.entrySet().iterator().next();
            sb.append(e.getKey());
            cur = e.getValue();
        }
        return sb.toString();
    }

    private Node traverse(String s) {
        if (s == null) return null;
        Node cur = root;
        for (char c : s.toCharArray()) {
            cur = cur.children.get(c);
            if (cur == null) return null;
        }
        return cur;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        root.children.clear();
        root.wordCount = 0;
        root.prefixCount = 0;
        size = 0;
    }
}
```
