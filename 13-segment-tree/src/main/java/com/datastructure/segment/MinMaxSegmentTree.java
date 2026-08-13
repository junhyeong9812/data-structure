package com.datastructure.segment;

/**
 * 최소와 최대를 **한 번에** 구하는 트리.
 *
 * 최소 트리 하나와 최대 트리 하나를 따로 두면 메모리가 두 배고 순회도 두 번이다.
 * 노드 하나가 **두 값을 같이** 들고 있으면 한 번에 끝난다.
 *
 * 여기서 배울 것은 **접어 넣는 값이 스칼라일 필요가 없다**는 것이다.
 * 결합법칙만 지키면 무엇이든 된다. 그러면 아래 같은 것도 가능해진다.
 *
 *   (합, 개수)   -> 평균을 구할 수 있게 된다. 평균 자체는 결합법칙이 없지만
 *                   합과 개수는 있고, 마지막에 나누면 되기 때문이다
 *   (최소, 최소가 몇 개)
 *   (최대 구간합, 왼쪽 최대, 오른쪽 최대, 전체 합)   -> 최대 부분합 문제
 *
 * **"결합법칙이 없다"고 포기하기 전에 무엇을 같이 들고 다니면 생기는지 보라.**
 */
public class MinMaxSegmentTree {

    public record MinMax(long min, long max) {
        static final MinMax IDENTITY = new MinMax(Long.MAX_VALUE, Long.MIN_VALUE);

        static MinMax of(long v) {
            return new MinMax(v, v);
        }

        MinMax merge(MinMax other) {
            // TODO 1: 두 구간의 최소끼리, 최대끼리 접는다.
            throw new UnsupportedOperationException("TODO 1: merge");
        }
    }

    private final int n;
    private final MinMax[] tree;
    private final long[] values;

    public MinMaxSegmentTree(long[] initial) {
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        this.n = initial.length;
        this.values = initial.clone();
        this.tree = new MinMax[4 * n];
        build(1, 0, n - 1);
    }

    private void build(int node, int lo, int hi) {
        if (lo == hi) {
            tree[node] = MinMax.of(values[lo]);
            return;
        }
        int mid = (lo + hi) >>> 1;
        build(node * 2, lo, mid);
        build(node * 2 + 1, mid + 1, hi);
        tree[node] = tree[node * 2].merge(tree[node * 2 + 1]);
    }

    public void update(int index, long value) {
        requireIndex(index);
        values[index] = value;
        update(1, 0, n - 1, index, value);
    }

    private void update(int node, int lo, int hi, int index, long value) {
        if (lo == hi) {
            tree[node] = MinMax.of(value);
            return;
        }
        int mid = (lo + hi) >>> 1;
        if (index <= mid) {
            update(node * 2, lo, mid, index, value);
        } else {
            update(node * 2 + 1, mid + 1, hi, index, value);
        }
        tree[node] = tree[node * 2].merge(tree[node * 2 + 1]);
    }

    public MinMax query(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return MinMax.IDENTITY;
        }
        return query(1, 0, n - 1, from, to);
    }

    private MinMax query(int node, int lo, int hi, int from, int to) {
        // TODO 2: SegmentTree.query 와 같은 세 경우다.
        //
        // 항등원이 (MAX_VALUE, MIN_VALUE) 인 것을 보라. **뒤집혀 있다.**
        // 최소 자리에는 가장 큰 값을, 최대 자리에는 가장 작은 값을 둬야
        // 어느 쪽과 merge 해도 영향을 안 준다.
        throw new UnsupportedOperationException("TODO 2: query");
    }

    public int size() {
        return n;
    }

    private void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
