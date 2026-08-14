package com.datastructure.segment;

/**
 * 구간 전체를 한 번에 갱신하는 트리. 미루기(lazy propagation)를 쓴다.
 *
 * 지금까지의 update 는 원소 하나짜리였다. "0번부터 99999번까지 전부 5를 더해라"를
 * 그 방식으로 하면 10만 번 갱신이라 O(n log n) 이다.
 *
 * 해법은 안 하는 것이다.
 * 어떤 노드의 구간이 요청 구간에 통째로 들어가면, 그 노드의 합만 고치고
 * "아래에도 5를 더해야 한다"는 쪽지(lazy)를 붙여둔 채 내려가지 않는다.
 *
 * 나중에 그 아래로 내려갈 일이 생기면 그때 쪽지를 자식에게 넘긴다(push).
 * 필요할 때까지 미룬다. 그래서 구간 갱신도 O(log n) 이 된다.
 *
 * 지켜야 할 규칙이 둘이다.
 *
 *   1. 노드의 tree 값은 자기 쪽지가 이미 반영된 상태다. 쪽지는 자식에게만 밀린 것이다.
 *   2. 자식을 보러 내려가기 직전에 push 해야 한다. 안 하면 옛 값을 읽는다.
 *
 * 1번을 헷갈리면 값을 두 번 더하거나 아예 안 더한다. 가장 흔한 실수다.
 *
 * (실무의 대응물: 데이터베이스의 지연 인덱스 갱신, 렌더링의 dirty flag,
 *  React 의 배치 업데이트가 전부 "미뤘다가 필요할 때 한다"는 같은 발상이다)
 */
public class LazySegmentTree {

    private final int n;
    private final long[] tree;
    private final long[] lazy;

    public LazySegmentTree(long[] initial) {
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        this.n = initial.length;
        this.tree = new long[4 * n];
        this.lazy = new long[4 * n];
        build(1, 0, n - 1, initial);
    }

    private void build(int node, int lo, int hi, long[] src) {
        if (lo == hi) {
            tree[node] = src[lo];
            return;
        }
        int mid = (lo + hi) >>> 1;
        build(node * 2, lo, mid, src);
        build(node * 2 + 1, mid + 1, hi, src);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    /** 미뤄둔 갱신을 자식에게 내린다. */
    private void push(int node, int lo, int hi) {
        // TODO 1: 쪽지를 자식 둘에게 넘기고 자기 쪽지를 지운다.
        //
        // 쪽지가 0 이면 할 일이 없다.
        // **자기 tree 값은 건드리지 않는다.** 이미 반영돼 있기 때문이다.
        // 여기서 tree[node] 를 또 고치면 값이 두 번 더해진다.
        throw new UnsupportedOperationException("TODO 1: push");
    }

    /** 이 노드가 덮는 구간 전체에 delta 를 더한 효과를 즉시 반영한다. */
    private void apply(int node, int lo, int hi, long delta) {
        // TODO 2: 이 구간의 모든 원소에 delta 를 더한 효과를 즉시 반영한다.
        //
        // 합은 **원소 하나당 delta 씩** 늘어난다. 구간 길이를 곱해야 한다.
        // 길이는 hi - lo + 1 이다. (+1 을 빠뜨리는 것이 여기서 제일 흔한 실수다)
        //
        // 그리고 쪽지를 **누적**한다. 대입이 아니라 더하기다.
        // 이미 미뤄둔 것이 있으면 같이 내려가야 하기 때문이다.
        throw new UnsupportedOperationException("TODO 2: apply");
    }

    public void rangeAdd(int from, int to, long delta) {
        requireRange(from, to);
        if (from > to) {
            return;
        }
        rangeAdd(1, 0, n - 1, from, to, delta);
    }

    private void rangeAdd(int node, int lo, int hi, int from, int to, long delta) {
        // TODO 3: query 와 같은 세 경우인데, 두 번째에서 **멈추고 쪽지를 남긴다.**
        //
        //   1. 안 겹친다        -> 아무것도 안 한다
        //   2. 통째로 들어간다  -> apply 하고 **내려가지 않는다.** 이게 미루기다
        //   3. 걸쳐 있다        -> push 하고 양쪽으로 내려간 뒤 자기를 다시 계산한다
        //
        // 3번에서 push 를 빠뜨리면 자식이 옛 값을 들고 있는 채로 갱신돼 조용히 틀린다.
        throw new UnsupportedOperationException("TODO 3: rangeAdd");
    }

    public long rangeSum(int from, int to) {
        requireRange(from, to);
        if (from > to) {
            return 0L;
        }
        return rangeSum(1, 0, n - 1, from, to);
    }

    private long rangeSum(int node, int lo, int hi, int from, int to) {
        // TODO 4: 보통의 구간 합인데, **내려가기 전에 push 해야 한다.**
        //
        // 조회인데 자료구조를 바꾼다. 10번 LRU 의 get 과 같은 성질이다.
        // (그래서 이것도 동시성이 까다롭다)
        throw new UnsupportedOperationException("TODO 4: rangeSum");
    }

    public long get(int index) {
        return rangeSum(index, index);
    }

    public int size() {
        return n;
    }

    long lazyAt(int node) {
        return lazy[node];
    }

    private void requireRange(int from, int to) {
        if (from < 0 || from >= n || to < 0 || to >= n) {
            throw new IndexOutOfBoundsException(
                    "구간 [" + from + ", " + to + "] 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
