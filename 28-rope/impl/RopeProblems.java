package com.datastructure.rope;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public final class RopeProblems {

    private RopeProblems() {
    }

    public record Lcp(int length, long comparedChars) {
    }

    public static CharSequenceStore applyEdits(CharSequenceStore doc, List<Edit> edits) {
        if (doc == null || edits == null) {
            throw new IllegalArgumentException("문서와 편집 목록이 필요하다");
        }
        CharSequenceStore current = doc;
        for (Edit edit : edits) {
            current = switch (edit) {
                case Edit.Insert i -> current.insert(i.index(), i.text());
                case Edit.Delete d -> current.delete(d.from(), d.to());
            };
        }
        return current;
    }

    public static int longestCommonPrefixLength(CharSequenceStore a, CharSequenceStore b) {
        return longestCommonPrefix(a, b).length();
    }

    public static Lcp longestCommonPrefix(CharSequenceStore a, CharSequenceStore b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("두 문서가 필요하다");
        }
        if (a instanceof Rope ra && b instanceof Rope rb) {
            return sharedAwarePrefix(ra, rb);
        }
        return naiveLongestCommonPrefix(a, b);
    }

    private static Lcp sharedAwarePrefix(Rope a, Rope b) {
        Deque<Rope.Node> stackA = new ArrayDeque<>();
        Deque<Rope.Node> stackB = new ArrayDeque<>();
        if (a.root().length > 0) {
            stackA.push(a.root());
        }
        if (b.root().length > 0) {
            stackB.push(b.root());
        }
        String bufA = "";
        String bufB = "";
        int offA = 0;
        int offB = 0;
        int matched = 0;
        long compared = 0;

        while (true) {
            if (offA == bufA.length() && offB == bufB.length()
                    && !stackA.isEmpty() && !stackB.isEmpty() && stackA.peek() == stackB.peek()) {
                Rope.Node shared = stackA.pop();
                stackB.pop();
                matched += shared.length;
                continue;
            }
            if (offA == bufA.length()) {
                if (stackA.isEmpty()) {
                    break;
                }
                Rope.Node node = stackA.pop();
                if (!node.isLeaf()) {
                    stackA.push(node.right);
                    stackA.push(node.left);
                    continue;
                }
                bufA = node.text;
                offA = 0;
                if (bufA.isEmpty()) {
                    continue;
                }
            }
            if (offB == bufB.length()) {
                if (stackB.isEmpty()) {
                    break;
                }
                Rope.Node node = stackB.pop();
                if (!node.isLeaf()) {
                    stackB.push(node.right);
                    stackB.push(node.left);
                    continue;
                }
                bufB = node.text;
                offB = 0;
                if (bufB.isEmpty()) {
                    continue;
                }
            }
            int step = Math.min(bufA.length() - offA, bufB.length() - offB);
            for (int k = 0; k < step; k++) {
                compared++;
                if (bufA.charAt(offA + k) != bufB.charAt(offB + k)) {
                    return new Lcp(matched + k, compared);
                }
            }
            matched += step;
            offA += step;
            offB += step;
        }
        return new Lcp(matched, compared);
    }

    public static Lcp naiveLongestCommonPrefix(CharSequenceStore a, CharSequenceStore b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("두 문서가 필요하다");
        }
        int n = Math.min(a.length(), b.length());
        long compared = 0;
        for (int i = 0; i < n; i++) {
            compared++;
            if (a.charAt(i) != b.charAt(i)) {
                return new Lcp(i, compared);
            }
        }
        return new Lcp(n, compared);
    }
}
