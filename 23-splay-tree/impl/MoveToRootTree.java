package com.datastructure.splay;

public class MoveToRootTree {

    static final class Node {
        final int key;
        Node left;
        Node right;

        Node(int key) {
            this.key = key;
        }
    }

    Node root;
    private long rotations;

    /**
     * 0..n-1 을 정렬 순서로 put 한 스플레이 트리와 같은 모양을 splay 없이 만든다.
     * 왼쪽으로 한 줄이고 뿌리가 n-1 이다. 회전은 한 번도 하지 않는다.
     */
    public static MoveToRootTree spine(int n) {
        MoveToRootTree t = new MoveToRootTree();
        for (int i = 0; i < n; i++) {
            Node x = new Node(i);
            x.left = t.root;
            t.root = x;
        }
        return t;
    }

    private Node rotateRight(Node h) {
        rotations++;
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        return x;
    }

    private Node rotateLeft(Node h) {
        rotations++;
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        return x;
    }

    private Node moveToRoot(Node h, int key) {
        if (h == null) {
            return null;
        }
        if (key < h.key) {
            if (h.left == null) {
                return h;
            }
            if (key < h.left.key) {
                // zig-zig 인데 부모를 먼저 돈다. 이 한 곳만 SplayTree 와 다르다.
                h.left.left = moveToRoot(h.left.left, key);
                if (h.left.left != null) {
                    h.left = rotateRight(h.left);
                }
            } else if (key > h.left.key) {
                h.left.right = moveToRoot(h.left.right, key);
                if (h.left.right != null) {
                    h.left = rotateLeft(h.left);
                }
            }
            if (h.left == null) {
                return h;
            }
            return rotateRight(h);
        } else if (key > h.key) {
            if (h.right == null) {
                return h;
            }
            if (key > h.right.key) {
                // zig-zig 인데 부모를 먼저 돈다.
                h.right.right = moveToRoot(h.right.right, key);
                if (h.right.right != null) {
                    h.right = rotateLeft(h.right);
                }
            } else if (key < h.right.key) {
                h.right.left = moveToRoot(h.right.left, key);
                if (h.right.left != null) {
                    h.right = rotateRight(h.right);
                }
            }
            if (h.right == null) {
                return h;
            }
            return rotateLeft(h);
        } else {
            return h;
        }
    }

    public boolean get(int key) {
        root = moveToRoot(root, key);
        return root != null && root.key == key;
    }

    public long rotations() {
        return rotations;
    }

    public int height() {
        return height(root);
    }

    private int height(Node h) {
        return h == null ? 0 : 1 + Math.max(height(h.left), height(h.right));
    }

    public int depthOf(int key) {
        Node cur = root;
        int depth = 0;
        while (cur != null) {
            if (key < cur.key) {
                cur = cur.left;
            } else if (key > cur.key) {
                cur = cur.right;
            } else {
                return depth;
            }
            depth++;
        }
        return -1;
    }
}
