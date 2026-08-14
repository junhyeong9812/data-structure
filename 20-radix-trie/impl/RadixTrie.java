package com.datastructure.radix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RadixTrie<V> implements PrefixMap<V> {

    static final class Node<V> {
        String edge;
        final Map<Character, Node<V>> children = new TreeMap<>();
        V value;
        int keysBelow;

        Node(String edge) {
            this.edge = edge;
        }
    }

    Node<V> root = new Node<>("");

    static int commonPrefixLength(String edge, String s, int from) {
        int n = Math.min(edge.length(), s.length() - from);
        int i = 0;
        while (i < n && edge.charAt(i) == s.charAt(from + i)) {
            i++;
        }
        return i;
    }

    @Override
    public V put(String key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("null 값은 담지 않는다");
        }
        V old = get(key);
        boolean isNew = old == null;

        Node<V> cur = root;
        if (isNew) {
            cur.keysBelow++;
        }
        int pos = 0;
        while (true) {
            if (pos == key.length()) {
                cur.value = value;
                return old;
            }
            char c = key.charAt(pos);
            Node<V> child = cur.children.get(c);

            if (child == null) {
                Node<V> leaf = new Node<V>(key.substring(pos));
                leaf.value = value;
                leaf.keysBelow = 1;
                cur.children.put(c, leaf);
                return old;
            }

            int common = commonPrefixLength(child.edge, key, pos);

            if (common == child.edge.length()) {
                pos += common;
                cur = child;
                if (isNew) {
                    cur.keysBelow++;
                }
                continue;
            }

            Node<V> mid = new Node<V>(child.edge.substring(0, common));
            mid.keysBelow = child.keysBelow;
            child.edge = child.edge.substring(common);
            mid.children.put(child.edge.charAt(0), child);
            cur.children.put(c, mid);
            mid.keysBelow++;

            if (pos + common == key.length()) {
                mid.value = value;
            } else {
                Node<V> leaf = new Node<V>(key.substring(pos + common));
                leaf.value = value;
                leaf.keysBelow = 1;
                mid.children.put(leaf.edge.charAt(0), leaf);
            }
            return old;
        }
    }

    Node<V> findNode(String key) {
        Node<V> cur = root;
        int pos = 0;
        while (pos < key.length()) {
            Node<V> child = cur.children.get(key.charAt(pos));
            if (child == null || !key.startsWith(child.edge, pos)) {
                return null;
            }
            pos += child.edge.length();
            cur = child;
        }
        return cur;
    }

    Node<V> prefixRoot(String prefix, StringBuilder path) {
        Node<V> cur = root;
        int pos = 0;
        while (pos < prefix.length()) {
            Node<V> child = cur.children.get(prefix.charAt(pos));
            if (child == null) {
                return null;
            }
            if (prefix.length() - pos < child.edge.length()) {
                if (!child.edge.startsWith(prefix.substring(pos))) {
                    return null;
                }
                path.append(child.edge);
                return child;
            }
            if (!prefix.startsWith(child.edge, pos)) {
                return null;
            }
            path.append(child.edge);
            pos += child.edge.length();
            cur = child;
        }
        return cur;
    }

    static <V> void collect(Node<V> node, StringBuilder path, List<String> out) {
        if (node.value != null) {
            out.add(path.toString());
        }
        for (Node<V> child : node.children.values()) {
            path.append(child.edge);
            collect(child, path, out);
            path.setLength(path.length() - child.edge.length());
        }
    }

    @Override
    public V remove(String key) {
        requireKey(key);
        V old = get(key);
        if (old == null) {
            return null;
        }
        root.keysBelow--;

        Node<V> cur = root;
        int pos = 0;
        while (pos < key.length()) {
            char c = key.charAt(pos);
            Node<V> child = cur.children.get(c);
            child.keysBelow--;
            if (child.keysBelow == 0) {
                cur.children.remove(c);
                compress(cur);
                return old;
            }
            pos += child.edge.length();
            cur = child;
        }
        cur.value = null;
        compress(cur);
        return old;
    }

    void compress(Node<V> node) {
        if (node == root || node.value != null || node.children.size() != 1) {
            return;
        }
        Node<V> only = node.children.values().iterator().next();
        node.edge = node.edge + only.edge;
        node.value = only.value;
        node.children.clear();
        node.children.putAll(only.children);
    }

    @Override
    public String longestPrefixOf(String s) {
        requireKey(s);
        int best = root.value != null ? 0 : -1;
        Node<V> cur = root;
        int pos = 0;
        while (pos < s.length()) {
            Node<V> child = cur.children.get(s.charAt(pos));
            if (child == null || !s.startsWith(child.edge, pos)) {
                break;
            }
            pos += child.edge.length();
            cur = child;
            if (cur.value != null) {
                best = pos;
            }
        }
        return best < 0 ? null : s.substring(0, best);
    }

    @Override
    public V get(String key) {
        requireKey(key);
        Node<V> n = findNode(key);
        return n == null ? null : n.value;
    }

    @Override
    public boolean containsKey(String key) {
        return get(key) != null;
    }

    @Override
    public List<String> keysWithPrefix(String prefix) {
        requireKey(prefix);
        List<String> out = new ArrayList<>();
        StringBuilder path = new StringBuilder();
        Node<V> start = prefixRoot(prefix, path);
        if (start != null) {
            collect(start, path, out);
        }
        return out;
    }

    @Override
    public int countWithPrefix(String prefix) {
        requireKey(prefix);
        Node<V> start = prefixRoot(prefix, new StringBuilder());
        return start == null ? 0 : start.keysBelow;
    }

    @Override
    public List<String> keys() {
        return keysWithPrefix("");
    }

    @Override
    public int size() {
        return root.keysBelow;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        root = new Node<>("");
    }

    int nodeCount() {
        return countNodes(root) - 1;
    }

    private static <V> int countNodes(Node<V> node) {
        int n = 1;
        for (Node<V> child : node.children.values()) {
            n += countNodes(child);
        }
        return n;
    }

    private static void requireKey(String s) {
        if (s == null) {
            throw new IllegalArgumentException("null 은 키가 아니다");
        }
    }
}
