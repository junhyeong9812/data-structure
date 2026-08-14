package com.datastructure.rope;

import java.util.ArrayList;
import java.util.List;

public final class Rope implements CharSequenceStore {

    public static final int DEFAULT_LEAF_MAX = 32;

    static final class Node {
        final String text;
        final Node left;
        final Node right;
        final int weight;
        final int length;
        final int depth;

        Node(String text) {
            this.text = text;
            this.left = null;
            this.right = null;
            this.weight = text.length();
            this.length = text.length();
            this.depth = 0;
        }

        Node(Node left, Node right) {
            this.text = null;
            this.left = left;
            this.right = right;
            this.weight = left.length;
            this.length = left.length + right.length;
            this.depth = 1 + Math.max(left.depth, right.depth);
        }

        boolean isLeaf() {
            return text != null;
        }
    }

    static final Node EMPTY = new Node("");

    private final Node root;
    private final int leafMax;
    private final long copiedByLastOp;
    private final long copiedTotal;

    private long charAtVisits;

    public Rope(String text) {
        this(text, DEFAULT_LEAF_MAX);
    }

    public Rope(String text, int leafMax) {
        if (text == null) {
            throw new IllegalArgumentException("문자열이 null 이다");
        }
        if (leafMax < 1) {
            throw new IllegalArgumentException("leafMax 는 1 이상이어야 한다: " + leafMax);
        }
        this.root = buildLeaves(text, leafMax, new long[1]);
        this.leafMax = leafMax;
        this.copiedByLastOp = 0;
        this.copiedTotal = 0;
    }

    private Rope(Node root, int leafMax, long copied, long previousTotal) {
        this.root = root;
        this.leafMax = leafMax;
        this.copiedByLastOp = copied;
        this.copiedTotal = previousTotal + copied;
    }

    static Node buildLeaves(String text, int leafMax, long[] copied) {
        if (text.isEmpty()) {
            return EMPTY;
        }
        if (text.length() <= leafMax) {
            return new Node(text);
        }
        List<Node> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += leafMax) {
            chunks.add(new Node(text.substring(i, Math.min(text.length(), i + leafMax))));
        }
        copied[0] += text.length();
        return balancedOver(chunks, 0, chunks.size());
    }

    static Node balancedOver(List<Node> nodes, int from, int to) {
        if (from >= to) {
            return EMPTY;
        }
        if (to - from == 1) {
            return nodes.get(from);
        }
        int mid = (from + to) >>> 1;
        return new Node(balancedOver(nodes, from, mid), balancedOver(nodes, mid, to));
    }

    static Node concatNodes(Node a, Node b) {
        if (a.length == 0) {
            return b;
        }
        if (b.length == 0) {
            return a;
        }
        return new Node(a, b);
    }

    static Node[] splitNode(Node node, int index, long[] copied) {
        if (index == 0) {
            return new Node[]{EMPTY, node};
        }
        if (index == node.length) {
            return new Node[]{node, EMPTY};
        }
        if (node.isLeaf()) {
            copied[0] += node.length;
            return new Node[]{new Node(node.text.substring(0, index)),
                    new Node(node.text.substring(index))};
        }
        if (index < node.weight) {
            Node[] parts = splitNode(node.left, index, copied);
            return new Node[]{parts[0], concatNodes(parts[1], node.right)};
        }
        if (index > node.weight) {
            Node[] parts = splitNode(node.right, index - node.weight, copied);
            return new Node[]{concatNodes(node.left, parts[0]), parts[1]};
        }
        return new Node[]{node.left, node.right};
    }

    @Override
    public int length() {
        return root.length;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= root.length) {
            throw new IndexOutOfBoundsException("index " + index + " (길이 " + root.length + ")");
        }
        Node node = root;
        int i = index;
        long visits = 0;
        while (!node.isLeaf()) {
            visits++;
            if (i < node.weight) {
                node = node.left;
            } else {
                i -= node.weight;
                node = node.right;
            }
        }
        visits++;
        charAtVisits += visits;
        return node.text.charAt(i);
    }

    @Override
    public String substring(int from, int to) {
        checkRange(from, to);
        StringBuilder out = new StringBuilder(to - from);
        appendRange(root, from, to, out);
        return out.toString();
    }

    private static void appendRange(Node node, int from, int to, StringBuilder out) {
        if (from >= to) {
            return;
        }
        if (node.isLeaf()) {
            out.append(node.text, from, to);
            return;
        }
        if (from < node.weight) {
            appendRange(node.left, from, Math.min(to, node.weight), out);
        }
        if (to > node.weight) {
            appendRange(node.right, Math.max(0, from - node.weight), to - node.weight, out);
        }
    }

    @Override
    public Rope concat(CharSequenceStore other) {
        if (other == null) {
            throw new IllegalArgumentException("붙일 저장소가 null 이다");
        }
        long copied = 0;
        Node otherRoot;
        if (other instanceof Rope rope) {
            otherRoot = rope.root;
        } else {
            otherRoot = buildLeaves(other.toString(), leafMax, new long[1]);
            copied = other.length();
        }
        return new Rope(concatNodes(root, otherRoot), leafMax, copied, copiedTotal);
    }

    @Override
    public Rope insert(int index, String s) {
        if (s == null) {
            throw new IllegalArgumentException("넣을 문자열이 null 이다");
        }
        if (index < 0 || index > root.length) {
            throw new IndexOutOfBoundsException("index " + index + " (길이 " + root.length + ")");
        }
        if (s.isEmpty()) {
            return new Rope(root, leafMax, 0, copiedTotal);
        }
        long[] copied = new long[1];
        Node[] parts = splitNode(root, index, copied);
        Node middle = buildLeaves(s, leafMax, copied);
        Node next = concatNodes(concatNodes(parts[0], middle), parts[1]);
        return new Rope(next, leafMax, copied[0], copiedTotal);
    }

    @Override
    public Rope delete(int from, int to) {
        checkRange(from, to);
        if (from == to) {
            return new Rope(root, leafMax, 0, copiedTotal);
        }
        long[] copied = new long[1];
        Node[] first = splitNode(root, from, copied);
        Node[] second = splitNode(first[1], to - from, copied);
        return new Rope(concatNodes(first[0], second[1]), leafMax, copied[0], copiedTotal);
    }

    @Override
    public Split split(int index) {
        if (index < 0 || index > root.length) {
            throw new IndexOutOfBoundsException("index " + index + " (길이 " + root.length + ")");
        }
        long[] copied = new long[1];
        Node[] parts = splitNode(root, index, copied);
        return new Split(new Rope(parts[0], leafMax, copied[0], copiedTotal),
                new Rope(parts[1], leafMax, copied[0], copiedTotal));
    }

    public Rope rebalance() {
        List<Node> leaves = new ArrayList<>();
        collectLeaves(root, leaves);
        Node next = leaves.isEmpty() ? EMPTY : balancedOver(leaves, 0, leaves.size());
        return new Rope(next, leafMax, 0, copiedTotal);
    }

    private static void collectLeaves(Node node, List<Node> out) {
        if (node.isLeaf()) {
            if (node.length > 0) {
                out.add(node);
            }
            return;
        }
        collectLeaves(node.left, out);
        collectLeaves(node.right, out);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(root.length);
        appendAll(root, out);
        return out.toString();
    }

    private static void appendAll(Node node, StringBuilder out) {
        if (node.isLeaf()) {
            out.append(node.text);
            return;
        }
        appendAll(node.left, out);
        appendAll(node.right, out);
    }

    @Override
    public long charsCopiedByLastOp() {
        return copiedByLastOp;
    }

    @Override
    public long charsCopiedTotal() {
        return copiedTotal;
    }

    public int leafMax() {
        return leafMax;
    }

    public int depth() {
        return root.depth;
    }

    public int leafCount() {
        return countLeaves(root);
    }

    private static int countLeaves(Node node) {
        return node.isLeaf() ? 1 : countLeaves(node.left) + countLeaves(node.right);
    }

    public int nodeCount() {
        return countNodes(root);
    }

    private static int countNodes(Node node) {
        return node.isLeaf() ? 1 : 1 + countNodes(node.left) + countNodes(node.right);
    }

    public List<String> leaves() {
        List<Node> nodes = new ArrayList<>();
        collectLeaves(root, nodes);
        List<String> out = new ArrayList<>(nodes.size());
        for (Node n : nodes) {
            out.add(n.text);
        }
        return out;
    }

    public long charAtVisits() {
        return charAtVisits;
    }

    public void resetCharAtVisits() {
        charAtVisits = 0;
    }

    Node root() {
        return root;
    }

    private void checkRange(int from, int to) {
        if (from < 0 || to > root.length || from > to) {
            throw new IndexOutOfBoundsException(
                    "[" + from + ", " + to + ") (길이 " + root.length + ")");
        }
    }
}
