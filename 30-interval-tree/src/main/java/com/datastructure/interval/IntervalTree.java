package com.datastructure.interval;

import java.util.ArrayList;
import java.util.List;

/**
 * 인터벌 트리. 06번 이진 탐색 트리에 필드 하나를 더한 것이다.
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
     * 붙일 자리를 찾아 새 노드를 만들고, 바뀐 부분트리의 뿌리를 반환한다.
     *
     * 생각할 것
     *   - 내려가는 길은 06번 put 과 같다. 무엇으로 비교하는가. Interval 이 이미 Comparable 이다.
     *   - 같은 구간이 이미 있으면 두 번 담지 않는다. size 도 안 는다.
     *   - 새 노드가 아래 어딘가에 붙으면 그 길에 있던 조상들의 maxEnd 가 전부 낡는다.
     *     그것을 언제 고칠 수 있는가. 내려가는 길에는 아직 무엇이 붙을지 모른다.
     *
     * TODO 4: 구현하라. size 는 새 노드를 만들 때만 늘린다.
     */
    private Node insertInto(Node node, Interval iv) {
        // 조상 갱신을 빠뜨려도 예외는 안 난다. 트리도 성해 보인다.
        // 낡은 maxEnd 는 "여기 아래엔 볼 게 없다"는 거짓말이 되어 질의가 그 가지를 건너뛴다.
        // 즉 답이 조용히 빠진다. IntervalTreeStructureTest 가 매 삽입 뒤에 그것만 본다.
        throw new UnsupportedOperationException("TODO 4: insertInto");
    }

    /**
     * 겹치는 것 하나. 없으면 null.
     *
     * 한 갈래로만 내려간다. 되돌아오지 않으므로 O(트리 높이) 다.
     * 어느 자식으로 갈지 고르는 조건이 이 메서드의 전부다.
     *
     * 생각할 것
     *   - 지금 노드가 겹치면 거기서 끝이다. 아무거나 하나면 되기 때문이다.
     *   - 안 겹치면 어디로 가야 하는가. 왼쪽에 답이 있을 수 있는 조건이 maxEnd 로 쓰인다.
     *   - 왼쪽으로 갔다가 못 찾으면 오른쪽에도 없다는 것을 납득해야 이 한 갈래가 성립한다.
     *     왼쪽에 "질의 시작보다 늦게 끝나는 구간"이 있는데 그것마저 안 겹친다면
     *     그 구간의 시작이 질의 끝보다 뒤라는 뜻이고, 오른쪽은 전부 그보다 더 뒤다.
     *
     * TODO 5: 구현하라. 노드를 하나 볼 때마다 visitedNodes 를 하나 올린다.
     */
    @Override
    public Interval findAny(Interval query) {
        if (query == null) {
            throw new IllegalArgumentException("질의 구간이 null 이다");
        }
        visitedNodes = 0;
        // 조건 없이 왼쪽부터 내려가면 답이 있는데도 null 이 나온다. 대조 테스트가 그것을 잡는다.
        throw new UnsupportedOperationException("TODO 5: findAny");
    }

    /**
     * 겹치는 것 전부.
     *
     * 답이 k 개면 O(트리 높이 + k) 를 지향한다.
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

    /**
     * 이 부분트리에서 겹치는 것을 out 에 모은다.
     *
     * 가지치기가 둘이다. 하나는 왼쪽으로 내려갈지, 하나는 오른쪽으로 내려갈지 정한다.
     *   - 왼쪽 부분트리의 모든 end 가 질의 시작 이하면 거기엔 겹치는 것이 없다. 무엇을 보면 아는가.
     *   - 시작점으로 정렬했으므로 오른쪽의 start 는 전부 이 노드의 start 이상이다.
     *     이 노드의 start 만 보고 오른쪽 전체를 버릴 수 있는 조건은 무엇인가.
     *
     * TODO 6: 구현하라. 노드를 하나 볼 때마다 visitedNodes 를 하나 올린다.
     */
    private void collectFrom(Node node, Interval query, List<Interval> out) {
        // 두 조건을 다 지우고 전체 순회를 해도 답은 똑같이 맞다. 걸음 수만 전수 조사가 된다.
        // 그게 이 박스에서 제일 위험한 실패다. PruningTest 가 방문 수로만 그것을 잡는다.
        // (측정: 질의 200번에 5,229 번이 정상이고, 한쪽만 지워도 40 만 번대로 뛴다)
        throw new UnsupportedOperationException("TODO 6: collectFrom");
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
     * 지우고 바뀐 부분트리의 뿌리를 반환한다. 06번 remove 의 세 경우와 같다.
     *
     * 생각할 것
     *   - 자식이 둘일 때 누구를 끌어올려야 순서가 유지되는가. 06번에서 이미 답한 질문이다.
     *   - 끌어올린 뒤 그 구간을 오른쪽에서 다시 지우면 size 는 몇 번 주는가.
     *   - 삽입과 같은 이유로 돌아오는 길에 maxEnd 를 다시 계산해야 한다.
     *     삭제 쪽이 더 고약하다. 틀리는 방향이 둘이기 때문이다.
     *     지워진 구간이 그 부분트리에서 제일 늦게 끝나던 것이면 조상의 maxEnd 가 실제보다 커진 채 남고,
     *     그때는 답이 안 빠지고 방문 수만 는다. 반대 방향이면 답이 빠진다.
     *
     * TODO 7: 구현하라. 없는 구간이면 아무것도 바꾸지 않는다.
     */
    private Node removeFrom(Node node, Interval iv) {
        throw new UnsupportedOperationException("TODO 7: removeFrom");
    }
}
