package com.datastructure.interval;

import java.util.ArrayList;
import java.util.List;

/**
 * [구현] 인터벌 트리. 06번 이진 탐색 트리에 필드 하나를 더한 것이다.
 *
 * 시작점으로 정렬한 BST 다. 시작점이 같으면 끝점으로 정렬한다. 거기까지는 06번과 같다.
 * 더한 것은 maxEnd 하나다. 이 부분트리에 있는 구간들의 끝점 최댓값이다.
 *
 * 그 한 필드가 가지치기를 만든다.
 *   왼쪽 부분트리의 maxEnd 가 질의 시작 이하  -> 왼쪽에는 답이 없다
 *   지금 노드의 시작이 질의 끝 이상            -> 오른쪽에는 답이 없다
 *
 * 균형은 잡지 않는다. 06번과 같은 약점이 그대로 있고, 정렬된 순서로 넣으면 연결 리스트가 된다.
 * 고치지 않고 한계 측정으로 못 박는다. 16번 레드블랙 트리가 무엇을 고치는지 알아야
 * 그 복잡한 회전이 복잡하기만 한 것으로 안 보인다.
 *
 * 참고: 필드 이름 root, size 와 Node 의 interval, maxEnd, left, right 는 테스트가 직접 들여다본다.
 */
public class IntervalTree implements IntervalStore, VisitCounting {

    static final class Node {
        Interval interval;
        long maxEnd;               // 이 부분트리에 있는 구간들의 end 최댓값
        Node left;
        Node right;

        Node(Interval interval) {
            this.interval = interval;
            this.maxEnd = interval.end;
        }
    }

    Node root;
    int size;
    private long visitedNodes;

    // ------------------------------------------------------------------
    // 채워져 있는 부분
    // ------------------------------------------------------------------

    /**
     * 빈 부분트리의 maxEnd. 최댓값의 항등원이다.
     *
     * 13번 세그먼트 트리에서 범위 밖 구간에 항등원을 돌려준 것과 같은 자리다.
     * 여기서 0 을 돌려주면 끝점이 음수인 구간에서 조용히 틀린다.
     */
    static long maxEndOf(Node node) {
        return node == null ? Long.MIN_VALUE : node.maxEnd;
    }

    /** 자식 둘과 자기 구간의 끝점 중 최댓값. 자식의 maxEnd 가 이미 맞다고 전제한다. */
    static void recomputeMaxEnd(Node node) {
        node.maxEnd = Math.max(node.interval.end,
                Math.max(maxEndOf(node.left), maxEndOf(node.right)));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;      // 트리는 부모만 끊으면 아래가 통째로 GC 대상이 된다
        size = 0;
    }

    @Override
    public boolean anyOverlaps(Interval query) {
        return findAny(query) != null;
    }

    /**
     * 트리의 높이. 비었으면 0, 노드 하나면 1.
     *
     * 균형이 깨졌는지 눈으로 보려고 열어둔다. 06번과 같다.
     */
    public int height() {
        return heightOf(root);
    }

    private int heightOf(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(heightOf(node.left), heightOf(node.right));
    }

    @Override
    public long visitedNodes() {
        return visitedNodes;
    }

    @Override
    public List<Interval> toList() {
        List<Interval> out = new ArrayList<>(size);
        inOrder(root, out);
        return out;
    }

    private void inOrder(Node node, List<Interval> out) {
        if (node == null) return;
        inOrder(node.left, out);
        out.add(node.interval);
        inOrder(node.right, out);
    }

    @Override
    public String toString() {
        return toList().toString();
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    @Override
    public boolean insert(Interval iv) {
        if (iv == null) {
            throw new IllegalArgumentException("구간이 null 이다");
        }
        int before = size;
        root = insertInto(root, iv);
        return size != before;
    }

    /**
     * 06번 put 과 같은 하강이다. 다른 것은 돌아오는 길뿐이다.
     *
     * 새 구간이 아래 어딘가에 붙으면 그 길에 있던 조상들의 maxEnd 가 전부 낡는다.
     * 그래서 재귀에서 돌아오면서 recomputeMaxEnd 를 부른다.
     * 이걸 빼면 예외도 안 나고 답만 조용히 빠진다.
     */
    private Node insertInto(Node node, Interval iv) {
        if (node == null) {
            size++;
            return new Node(iv);
        }
        int cmp = iv.compareTo(node.interval);
        if (cmp < 0) {
            node.left = insertInto(node.left, iv);
        } else if (cmp > 0) {
            node.right = insertInto(node.right, iv);
        } else {
            return node;              // 같은 구간은 두 번 담지 않는다. maxEnd 도 안 바뀐다
        }
        recomputeMaxEnd(node);
        return node;
    }

    @Override
    public boolean remove(Interval iv) {
        if (iv == null) {
            throw new IllegalArgumentException("구간이 null 이다");
        }
        int before = size;
        root = removeFrom(root, iv);
        return size != before;
    }

    /**
     * 06번 remove 의 세 경우와 같다. 자식이 둘이면 후속자를 끌어올린다.
     *
     * 삽입과 같은 이유로 돌아오는 길에 maxEnd 를 다시 계산한다.
     * 삭제 쪽이 더 위험하다. 지워진 구간이 그 부분트리에서 제일 늦게 끝나던 것이면
     * 조상들의 maxEnd 가 실제보다 커진 채로 남는다. 그러면 답은 안 빠지고 방문 수만 는다.
     * 반대로 크던 값을 못 올리면 답이 빠진다.
     */
    private Node removeFrom(Node node, Interval iv) {
        if (node == null) {
            return null;
        }
        int cmp = iv.compareTo(node.interval);
        if (cmp < 0) {
            node.left = removeFrom(node.left, iv);
        } else if (cmp > 0) {
            node.right = removeFrom(node.right, iv);
        } else {
            if (node.left == null) {
                size--;
                return node.right;
            }
            if (node.right == null) {
                size--;
                return node.left;
            }
            Node successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            node.interval = successor.interval;
            node.right = removeFrom(node.right, successor.interval);   // 여기서 size 가 준다
        }
        recomputeMaxEnd(node);
        return node;
    }

    /**
     * 겹치는 것 하나. 없으면 null.
     *
     * 한 갈래로만 내려간다. 되돌아오지 않으므로 O(트리 높이) 다.
     *
     * 왜 이게 맞는가. 왼쪽으로 갔다가 못 찾았다면 오른쪽에도 없다.
     * 왼쪽으로 갔다는 것은 왼쪽 어딘가에 end 가 질의 시작보다 큰 구간 i 가 있다는 뜻인데,
     * 그런 i 가 질의와 안 겹친다면 i 의 start 가 질의 끝 이상이라는 뜻이다.
     * 오른쪽 구간들의 start 는 전부 i 의 start 이상이므로 그것들도 전부 안 겹친다.
     */
    @Override
    public Interval findAny(Interval query) {
        if (query == null) {
            throw new IllegalArgumentException("질의 구간이 null 이다");
        }
        visitedNodes = 0;
        Node node = root;
        while (node != null) {
            visitedNodes++;
            if (node.interval.overlaps(query)) {
                return node.interval;
            }
            if (node.left != null && node.left.maxEnd > query.start) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return null;
    }

    /**
     * 겹치는 것 전부.
     *
     * 답이 k 개면 O(트리 높이 + k) 를 지향한다. 여기서 가지치기가 없으면 그냥 전체 순회다.
     * 답은 똑같이 맞고 방문 수만 n 이 된다. 그게 더 위험해서 걸음 수를 센다.
     */
    @Override
    public List<Interval> findAll(Interval query) {
        if (query == null) {
            throw new IllegalArgumentException("질의 구간이 null 이다");
        }
        visitedNodes = 0;
        List<Interval> out = new ArrayList<>();
        collectFrom(root, query, out);
        return out;
    }

    private void collectFrom(Node node, Interval query, List<Interval> out) {
        if (node == null) {
            return;
        }
        visitedNodes++;

        // 가지치기 1. 왼쪽 부분트리의 모든 end 가 질의 시작 이하면 거기엔 겹치는 것이 없다.
        if (node.left != null && node.left.maxEnd > query.start) {
            collectFrom(node.left, query, out);
        }

        if (node.interval.overlaps(query)) {
            out.add(node.interval);
        }

        // 가지치기 2. 시작점으로 정렬했으므로 오른쪽의 start 는 전부 이 노드의 start 이상이다.
        // 이 노드의 start 가 이미 질의 끝 이상이면 오른쪽도 전부 그렇다.
        if (node.interval.start < query.end) {
            collectFrom(node.right, query, out);
        }
    }
}
