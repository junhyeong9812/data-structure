package com.datastructure.segment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * 결합 함수를 **생성자로 받는** 세그먼트 트리.
 *
 * SegmentTree 는 상속으로 combine 을 정했다. 여기서는 인자로 받는다.
 * 하는 일은 같은데 **언제 정하느냐**가 다르다.
 *
 *   상속: 컴파일 시점에 정해진다. 타입이 하나 늘어난다
 *   인자: 실행 시점에 정해진다. 같은 클래스로 합/최소/문자열 이어붙이기를 다 한다
 *
 * 어느 쪽이 낫다기보다 **같은 추상화를 두 방법으로 표현할 수 있다**는 것이 요점이다.
 * 05번 LinkedHashMap(상속)과 12번 SkipListSet(포함)의 대비와 같은 이야기다.
 *
 * T 가 long 이 아니어도 되므로 문자열 이어붙이기, 행렬 곱, 집합 합집합도 담긴다.
 * **결합법칙과 항등원만 있으면 된다.**
 */
public class GenericSegmentTree<T> {

    private final int n;
    private final Object[] tree;
    private final Object[] values;
    private final T identity;
    private final BinaryOperator<T> combine;

    public GenericSegmentTree(List<T> initial, T identity, BinaryOperator<T> combine) {
        if (initial == null || initial.isEmpty()) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        if (identity == null || combine == null) {
            throw new IllegalArgumentException("항등원과 결합 함수가 필요하다");
        }
        this.n = initial.size();
        this.identity = identity;
        this.combine = combine;
        this.values = initial.toArray();
        this.tree = new Object[4 * n];
        build(1, 0, n - 1);
    }

    @SuppressWarnings("unchecked")
    private T at(Object[] arr, int i) {
        return (T) arr[i];
    }

    private void build(int node, int lo, int hi) {
        if (lo == hi) {
            tree[node] = values[lo];
            return;
        }
        int mid = (lo + hi) >>> 1;
        build(node * 2, lo, mid);
        build(node * 2 + 1, mid + 1, hi);
        tree[node] = combine.apply(at(tree, node * 2), at(tree, node * 2 + 1));
    }

    public void update(int index, T value) {
        requireIndex(index);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        values[index] = value;
        update(1, 0, n - 1, index, value);
    }

    private void update(int node, int lo, int hi, int index, T value) {
        if (lo == hi) {
            tree[node] = value;
            return;
        }
        int mid = (lo + hi) >>> 1;
        if (index <= mid) {
            update(node * 2, lo, mid, index, value);
        } else {
            update(node * 2 + 1, mid + 1, hi, index, value);
        }
        tree[node] = combine.apply(at(tree, node * 2), at(tree, node * 2 + 1));
    }

    public T query(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return identity;
        }
        return query(1, 0, n - 1, from, to);
    }

    private T query(int node, int lo, int hi, int from, int to) {
        // TODO 1: SegmentTree.query 와 똑같다. combine 을 어디서 얻느냐만 다르다.
        throw new UnsupportedOperationException("TODO 1: query");
    }

    public T get(int index) {
        requireIndex(index);
        return at(values, index);
    }

    public int size() {
        return n;
    }

    public List<T> toList() {
        List<T> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            out.add(at(values, i));
        }
        return out;
    }

    private void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
