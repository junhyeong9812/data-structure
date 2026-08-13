# pop/RedBlackTree.java

int 키 Red-Black Tree. CLRS 스타일. NIL sentinel + 부모 포인터.

```java
package com.datastructure.redblacktree.pop;

public class RedBlackTree {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    static class Node {
        int key;
        Node left, right, parent;
        boolean color;

        Node(int key, boolean color) {
            this.key = key;
            this.color = color;
        }
    }

    private final Node NIL;
    private Node root;
    private int size;

    public RedBlackTree() {
        this.NIL = new Node(0, BLACK);
        this.NIL.left = this.NIL.right = this.NIL.parent = NIL;
        this.root = NIL;
    }

    public boolean contains(int key) {
        return search(key) != NIL;
    }

    private Node search(int key) {
        Node x = root;
        while (x != NIL) {
            if (key == x.key) return x;
            x = (key < x.key) ? x.left : x.right;
        }
        return NIL;
    }

    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != NIL) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == NIL) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != NIL) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == NIL) root = y;
        else if (x == x.parent.right) x.parent.right = y;
        else x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    public void insert(int key) {
        Node z = new Node(key, RED);
        z.left = z.right = z.parent = NIL;

        Node y = NIL;
        Node x = root;
        while (x != NIL) {
            y = x;
            if (z.key == x.key) return; // 중복 무시
            x = (z.key < x.key) ? x.left : x.right;
        }
        z.parent = y;
        if (y == NIL) root = z;
        else if (z.key < y.key) y.left = z;
        else y.right = z;

        size++;
        insertFixup(z);
    }

    private void insertFixup(Node z) {
        while (z.parent.color == RED) {
            if (z.parent == z.parent.parent.left) {
                Node u = z.parent.parent.right;
                if (u.color == RED) {
                    z.parent.color = BLACK;
                    u.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                Node u = z.parent.parent.left;
                if (u.color == RED) {
                    z.parent.color = BLACK;
                    u.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    private void transplant(Node u, Node v) {
        if (u.parent == NIL) root = v;
        else if (u == u.parent.left) u.parent.left = v;
        else u.parent.right = v;
        v.parent = u.parent;
    }

    private Node minimum(Node x) {
        while (x.left != NIL) x = x.left;
        return x;
    }

    public boolean delete(int key) {
        Node z = search(key);
        if (z == NIL) return false;

        Node y = z;
        boolean yOriginalColor = y.color;
        Node x;
        if (z.left == NIL) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == NIL) {
            x = z.left;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        if (yOriginalColor == BLACK) deleteFixup(x);
        size--;
        return true;
    }

    private void deleteFixup(Node x) {
        while (x != root && x.color == BLACK) {
            if (x == x.parent.left) {
                Node w = x.parent.right;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (w.left.color == BLACK && w.right.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    if (w.right.color == BLACK) {
                        w.left.color = BLACK;
                        w.color = RED;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.right.color = BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                Node w = x.parent.left;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (w.right.color == BLACK && w.left.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    if (w.left.color == BLACK) {
                        w.right.color = BLACK;
                        w.color = RED;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.left.color = BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }

    public int getMin() {
        if (root == NIL) throw new java.util.NoSuchElementException();
        return minimum(root).key;
    }

    public int getMax() {
        if (root == NIL) throw new java.util.NoSuchElementException();
        Node x = root;
        while (x.right != NIL) x = x.right;
        return x.key;
    }

    public int getHeight() {
        return height(root);
    }

    private int height(Node x) {
        if (x == NIL) return 0;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    public int getBlackHeight() {
        int h = 0;
        Node x = root;
        while (x != NIL) {
            if (x.color == BLACK) h++;
            x = x.left;
        }
        return h;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
```
