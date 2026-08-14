package com.datastructure.splay;

/**
 * 잘못된 순서로 도는 스플레이 트리. 비교하려고 두었다.
 *
 * zig-zig 에서 할아버지가 아니라 부모를 먼저 돌린다. 그러면 목표 노드를 한 층씩
 * 끌어올리는 것과 정확히 같아진다. 1978년에 나온 move-to-root 휴리스틱이 이것이다.
 *
 * <h2>이것도 답은 맞다</h2>
 *
 * 탐색 성질은 그대로 지켜지고, 찾는 키는 뿌리로 올라오고, 없는 키도 제대로 없다고 한다.
 * 계약 테스트로는 스플레이 트리와 구별할 방법이 없다.
 *
 * <h2>갈리는 것은 비용뿐이다</h2>
 *
 * 부모를 먼저 돌리면 경로에 있던 노드들이 다시 한 줄로 남는다.
 * 절반으로 접히지 않으니 다음 조회가 또 밑바닥까지 내려간다.
 * AmortizedCostTest 가 회전 수를 세서 그 차이를 못 박는다.
 *
 * 이 클래스는 int 키만 다루고 값을 담지 않는다. 회전 수를 세는 것 말고는 할 일이 없다.
 */
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
     *
     * 두 트리를 같은 출발선에 세우려고 둔 것이다. 여기에 splay 가 끼면 비교가 무의미해진다.
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
        // TODO 1: SplayTree.splay 를 먼저 끝내라. 그것과 **한 곳만** 다르다.
        //
        // 같은 구조로 쓰되 zig-zig 에서
        //
        //   splay:       h = rotateRight(h)                할아버지를 돌린다
        //   여기:        h.left = rotateRight(h.left)      부모를 돌린다
        //
        // 오른쪽 zig-zig 도 마찬가지로 h.right 를 돌린다.
        // 그러면 zig-zig 과 zig-zag 의 코드가 똑같아진다. 방향만 다르고 하는 일이 같다.
        // 그게 바로 "그냥 한 층씩 끌어올리기"라는 뜻이다.
        //
        // 손자 자리가 null 이면 돌릴 것이 없으니 건너뛴다. splay 의 zig-zag 쪽과 같다.
        //
        // 다 짜고 나면 AmortizedCostTest 의 회전 수를 보라.
        // 두 구현이 같은 답을 내는데 하나는 5,259번, 하나는 500,499번 돈다.
        // 이 정도 차이가 **주석 한 줄 위치**에서 나온다.
        throw new UnsupportedOperationException("TODO 1: moveToRoot");
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
