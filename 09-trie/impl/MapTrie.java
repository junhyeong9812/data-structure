package com.datastructure.trie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapTrie implements Trie {

    static final class Node {
        final Map<Character, Node> children = new TreeMap<>();
        boolean end;
        int wordsBelow;
    }

    Node root = new Node();

    @Override
    public void insert(String word) {
        requireWord(word);
        if (contains(word)) {
            return;
        }
        Node cur = root;
        cur.wordsBelow++;
        for (int i = 0; i < word.length(); i++) {
            cur = cur.children.computeIfAbsent(word.charAt(i), c -> new Node());
            cur.wordsBelow++;
        }
        cur.end = true;
    }

    @Override
    public boolean remove(String word) {
        requireWord(word);
        if (!contains(word)) {
            return false;
        }
        root.wordsBelow--;
        Node cur = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            Node next = cur.children.get(c);
            next.wordsBelow--;
            if (next.wordsBelow == 0) {
                cur.children.remove(c);
                return true;
            }
            cur = next;
        }
        cur.end = false;
        return true;
    }

    @Override
    public List<String> keysWithPrefix(String prefix) {
        requireWord(prefix);
        List<String> out = new ArrayList<>();
        Node start = findNode(prefix);
        if (start != null) {
            collect(start, new StringBuilder(prefix), out);
        }
        return out;
    }

    static void collect(Node node, StringBuilder path, List<String> out) {
        if (node.end) {
            out.add(path.toString());
        }
        for (Map.Entry<Character, Node> e : node.children.entrySet()) {
            path.append(e.getKey().charValue());
            collect(e.getValue(), path, out);
            path.deleteCharAt(path.length() - 1);
        }
    }

    @Override
    public int countWithPrefix(String prefix) {
        requireWord(prefix);
        Node n = findNode(prefix);
        return n == null ? 0 : n.wordsBelow;
    }

    Node findNode(String s) {
        Node cur = root;
        for (int i = 0; i < s.length(); i++) {
            cur = cur.children.get(s.charAt(i));
            if (cur == null) {
                return null;
            }
        }
        return cur;
    }

    @Override
    public boolean contains(String word) {
        requireWord(word);
        Node n = findNode(word);
        return n != null && n.end;
    }

    @Override
    public boolean startsWith(String prefix) {
        requireWord(prefix);
        // 노드가 있느냐로는 부족하다. **뿌리는 단어가 하나도 없어도 존재한다.**
        // 빈 트라이에 startsWith("") 를 물으면 그것만으로는 true 가 나온다.
        Node n = findNode(prefix);
        return n != null && n.wordsBelow > 0;
    }

    @Override
    public int size() {
        return root.wordsBelow;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        root = new Node();
    }

    private static void requireWord(String s) {
        if (s == null) {
            throw new IllegalArgumentException("null 은 단어가 아니다");
        }
    }
}
