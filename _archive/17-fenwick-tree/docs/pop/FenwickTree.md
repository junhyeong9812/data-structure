# pop/FenwickTree.java

1-indexed Fenwick Tree. update/query/rangeQuery/set/get/findKth.

```java
package com.datastructure.fenwicktree.pop;

public class FenwickTree {
    private final long[] tree;
    private final long[] arr; // get 지원용 원본 값 저장
    private final int n;

    public FenwickTree(int n) {
        if (n < 1) throw new IllegalArgumentException("n >= 1");
        this.n = n;
        this.tree = new long[n + 1];
        this.arr = new long[n + 1];
    }

    public FenwickTree(int[] data) {
        this(data.length);
        for (int i = 0; i < data.length; i++) {
            arr[i + 1] = data[i];
        }
        // O(n) build
        for (int i = 1; i <= n; i++) {
            tree[i] += arr[i];
            int parent = i + lsb(i);
            if (parent <= n) tree[parent] += tree[i];
        }
    }

    private static int lsb(int i) {
        return i & -i;
    }

    public void update(int i, long delta) {
        validate(i);
        arr[i] += delta;
        while (i <= n) {
            tree[i] += delta;
            i += lsb(i);
        }
    }

    public void set(int i, long value) {
        validate(i);
        update(i, value - arr[i]);
    }

    public long get(int i) {
        validate(i);
        return arr[i];
    }

    public long query(int i) {
        if (i < 0 || i > n) throw new IndexOutOfBoundsException();
        long sum = 0;
        while (i > 0) {
            sum += tree[i];
            i -= lsb(i);
        }
        return sum;
    }

    public long rangeQuery(int l, int r) {
        if (l < 1 || r > n || l > r) throw new IndexOutOfBoundsException();
        return query(r) - query(l - 1);
    }

    /** 누적 합이 k 이상인 최소 인덱스. (모든 값이 비음수일 때) */
    public int findKth(long k) {
        int idx = 0;
        int logN = Integer.numberOfTrailingZeros(Integer.highestOneBit(n));
        for (int p = 1 << logN; p > 0; p >>= 1) {
            int next = idx + p;
            if (next <= n && tree[next] < k) {
                idx = next;
                k -= tree[next];
            }
        }
        return idx + 1;
    }

    public int size() {
        return n;
    }

    private void validate(int i) {
        if (i < 1 || i > n) throw new IndexOutOfBoundsException(String.valueOf(i));
    }
}
```
