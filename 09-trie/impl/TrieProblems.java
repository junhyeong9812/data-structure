package com.datastructure.trie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TrieProblems {

    private TrieProblems() {
    }

    public static String longestCommonPrefix(String[] words) {
        if (words == null || words.length == 0) {
            return "";
        }
        MapTrie trie = new MapTrie();
        for (String w : words) {
            trie.insert(w);
        }
        StringBuilder sb = new StringBuilder();
        MapTrie.Node cur = trie.root;
        while (cur.children.size() == 1 && !cur.end) {
            Map.Entry<Character, MapTrie.Node> only = cur.children.entrySet().iterator().next();
            sb.append(only.getKey().charValue());
            cur = only.getValue();
        }
        return sb.toString();
    }

    public static List<String> autocomplete(MapTrie trie, String prefix, int k) {
        List<String> out = new ArrayList<>();
        if (k <= 0) {
            return out;
        }
        MapTrie.Node start = trie.findNode(prefix);
        if (start != null) {
            collectUpTo(start, new StringBuilder(prefix), out, k);
        }
        return out;
    }

    private static void collectUpTo(MapTrie.Node node, StringBuilder path, List<String> out, int k) {
        if (out.size() >= k) {
            return;
        }
        if (node.end) {
            out.add(path.toString());
            if (out.size() >= k) {
                return;
            }
        }
        for (Map.Entry<Character, MapTrie.Node> e : node.children.entrySet()) {
            path.append(e.getKey().charValue());
            collectUpTo(e.getValue(), path, out, k);
            path.deleteCharAt(path.length() - 1);
            if (out.size() >= k) {
                return;
            }
        }
    }

    public static int countDistinctSubstrings(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        MapTrie.Node root = new MapTrie.Node();
        int nodes = 0;
        for (int i = 0; i < s.length(); i++) {
            MapTrie.Node cur = root;
            for (int j = i; j < s.length(); j++) {
                char c = s.charAt(j);
                MapTrie.Node next = cur.children.get(c);
                if (next == null) {
                    next = new MapTrie.Node();
                    cur.children.put(c, next);
                    nodes++;
                }
                cur = next;
            }
        }
        return nodes;
    }
}
