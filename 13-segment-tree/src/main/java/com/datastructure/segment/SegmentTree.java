package com.datastructure.segment;

/**
 * 구간 질의의 뼈대. 무엇으로 접을지만 자식이 정한다.
 *
 * 배열을 반씩 쪼개 트리로 만든다. 각 노드가 자기 구간의 답을 미리 들고 있다.
 *
 *   노드 1        [0..7]  전체
 *   노드 2, 3     [0..3] [4..7]
 *   노드 4~7      [0..1] [2..3] [4..5] [6..7]
 *   ...
 *   잎             원소 하나씩
 *
 * 자식은 2i 와 2i+1 이다. 07번 힙에서 쓴 그 계산이다.
 * 구조가 규칙적이면 그 규칙을 계산으로 대신할 수 있다.
 *
 * 조회는 요청 구간을 미리 계산된 노드 몇 개로 덮는다. 아무리 넓어도 O(log n) 개면 덮인다.
 * 갱신은 잎에서 뿌리까지 한 줄만 다시 계산하면 된다. 역시 O(log n) 이다.
 *
 * 배열 크기가 왜 4n 인가. n 이 2의 거듭제곱이면 2n 이면 되는데, 아니면 트리가
 * 한 층 더 깊어져 최대 4n 까지 간다. 정확히 계산하는 대신 넉넉히 잡는 것이 관행이다.
 *
 * combine 은 결합법칙을 만족해야 한다. (a·b)·c = a·(b·c).
 * 구간을 어떤 순서로 쪼개 합치든 답이 같아야 하기 때문이다.
 * 합, 최소, 최대, 곱, GCD, 비트 AND/OR 은 된다. 평균은 안 된다.
 * 그리고 항등원이 있어야 한다. 범위 밖 구간을 "없는 것처럼" 만들 값이 필요하기 때문이다.
 * (수학에서 이런 것을 모노이드라고 부른다)
 */
public abstract class SegmentTree implements RangeQuery {

    protected final int n;
    protected final long[] tree;
    private final long[] values;

    protected SegmentTree(long[] initial) {
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        this.n = initial.length;
        this.values = initial.clone();
        this.tree = new long[4 * n];
        build(1, 0, n - 1);
    }

    protected abstract long combine(long a, long b);

    protected abstract long identity();

    private void build(int node, int lo, int hi) {
        // TODO 1: [lo, hi] 를 덮는 node 를 채운다.
        //
        //   구간이 하나면(lo == hi) 그 원소가 곧 답이다.
        //   아니면 반으로 쪼개 양쪽을 먼저 만들고, **자식 둘을 combine 해서 자기를 채운다.**
        //
        // mid 는 (lo + hi) >>> 1 로 구한다. (lo + hi) / 2 는 큰 인덱스에서 넘칠 수 있다.
        // 여기서는 n 이 작아 문제가 안 되지만 습관이 중요하다.
        throw new UnsupportedOperationException("TODO 1: build");
    }

    @Override
    public void update(int index, long value) {
        requireIndex(index);
        values[index] = value;
        update(1, 0, n - 1, index, value);
    }

    private void update(int node, int lo, int hi, int index, long value) {
        // TODO 2: index 가 있는 쪽으로만 내려가고, 돌아오면서 자기를 다시 계산한다.
        //
        // **한 쪽만 내려간다.** 양쪽 다 내려가면 O(n) 이다.
        // 그리고 **돌아오는 길에 반드시 다시 combine 해야 한다.**
        // 그걸 빠뜨리면 잎만 바뀌고 위쪽은 옛 답을 들고 있는다. 조용히 틀린다.
        throw new UnsupportedOperationException("TODO 2: update");
    }

    @Override
    public long query(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return identity();
        }
        return query(1, 0, n - 1, from, to);
    }

    private long query(int node, int lo, int hi, int from, int to) {
        // TODO 3: 경우가 셋이다. **이 셋을 정확히 나누는 것이 이 자료구조의 전부다.**
        //
        //   1. 이 노드 구간이 요청과 **겹치지 않는다**   -> 항등원. 없는 것처럼 취급한다
        //   2. 이 노드 구간이 요청에 **완전히 들어간다** -> 미리 계산해둔 tree[node] 를 그대로 준다
        //   3. **걸쳐 있다**                            -> 반으로 쪼개 양쪽에 물어 combine 한다
        //
        // 2번이 O(log n) 을 만든다. 여기서 멈추지 않고 계속 내려가면 그냥 전수 조사다.
        // 1번에 항등원이 필요한 이유도 보라. 0 을 반환하면 최소 트리에서 틀린다.
        throw new UnsupportedOperationException("TODO 3: query");
    }

    @Override
    public long get(int index) {
        requireIndex(index);
        return values[index];
    }

    @Override
    public int size() {
        return n;
    }

    int treeSize() {
        return tree.length;
    }

    long node(int i) {
        return tree[i];
    }

    protected void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
