package com.datastructure.radix;

import java.util.ArrayList;
import java.util.List;

public final class RadixTrieProblems {

    private RadixTrieProblems() {
    }

    public static String longestCommonPrefix(String[] words) {
        if (words == null || words.length == 0) {
            return "";
        }
        RadixTrie<Boolean> trie = new RadixTrie<>();
        for (String w : words) {
            trie.put(w, Boolean.TRUE);
        }
        if (trie.root.value != null || trie.root.children.size() != 1) {
            return "";
        }
        return trie.root.children.values().iterator().next().edge;
    }

    public static List<String> autocomplete(RadixTrie<String> trie, String prefix, int k) {
        List<String> out = new ArrayList<>();
        if (k <= 0) {
            return out;
        }
        StringBuilder path = new StringBuilder();
        RadixTrie.Node<String> start = trie.prefixRoot(prefix, path);
        if (start != null) {
            collectUpTo(start, path, out, k);
        }
        return out;
    }

    private static void collectUpTo(RadixTrie.Node<String> node, StringBuilder path,
            List<String> out, int k) {
        if (out.size() >= k) {
            return;
        }
        if (node.value != null) {
            out.add(path.toString());
            if (out.size() >= k) {
                return;
            }
        }
        for (RadixTrie.Node<String> child : node.children.values()) {
            path.append(child.edge);
            collectUpTo(child, path, out, k);
            path.setLength(path.length() - child.edge.length());
            if (out.size() >= k) {
                return;
            }
        }
    }
}
