# oop/FenwickTree.java

OOP 버전 Fenwick Tree. 인터페이스 + 구현 분리.

```java
package com.datastructure.fenwicktree.oop;

public interface FenwickTree {
    void update(int i, long delta);
    void set(int i, long value);
    long get(int i);
    long query(int i);              // [1, i] 합
    long rangeQuery(int l, int r);  // [l, r] 합
    int size();
}
```

---

# oop/SumFenwickTree.java

```java
package com.datastructure.fenwicktree.oop;

public class SumFenwickTree implements FenwickTree {
    private final long[] tree;
    private final long[] arr;
    private final int n;

    public SumFenwickTree(int n) {
        if (n < 1) throw new IllegalArgumentException();
        this.n = n;
        this.tree = new long[n + 1];
        this.arr = new long[n + 1];
    }

    public SumFenwickTree(long[] data) {
        this(data.length);
        for (int i = 0; i < data.length; i++) arr[i + 1] = data[i];
        for (int i = 1; i <= n; i++) {
            tree[i] += arr[i];
            int parent = i + (i & -i);
            if (parent <= n) tree[parent] += tree[i];
        }
    }

    @Override
    public void update(int i, long delta) {
        validate(i);
        arr[i] += delta;
        while (i <= n) {
            tree[i] += delta;
            i += (i & -i);
        }
    }

    @Override
    public void set(int i, long value) {
        validate(i);
        update(i, value - arr[i]);
    }

    @Override
    public long get(int i) {
        validate(i);
        return arr[i];
    }

    @Override
    public long query(int i) {
        if (i < 0 || i > n) throw new IndexOutOfBoundsException();
        long sum = 0;
        while (i > 0) {
            sum += tree[i];
            i -= (i & -i);
        }
        return sum;
    }

    @Override
    public long rangeQuery(int l, int r) {
        if (l < 1 || r > n || l > r) throw new IndexOutOfBoundsException();
        return query(r) - query(l - 1);
    }

    @Override
    public int size() {
        return n;
    }

    private void validate(int i) {
        if (i < 1 || i > n) throw new IndexOutOfBoundsException(String.valueOf(i));
    }
}
```

---

# oop/FenwickTree2D.java

```java
package com.datastructure.fenwicktree.oop;

public class FenwickTree2D {
    private final long[][] tree;
    private final int rows, cols;

    public FenwickTree2D(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.tree = new long[rows + 1][cols + 1];
    }

    public void update(int r, int c, long delta) {
        for (int i = r; i <= rows; i += (i & -i)) {
            for (int j = c; j <= cols; j += (j & -j)) {
                tree[i][j] += delta;
            }
        }
    }

    public long query(int r, int c) {
        long sum = 0;
        for (int i = r; i > 0; i -= (i & -i)) {
            for (int j = c; j > 0; j -= (j & -j)) {
                sum += tree[i][j];
            }
        }
        return sum;
    }

    public long rangeQuery(int r1, int c1, int r2, int c2) {
        return query(r2, c2) - query(r1 - 1, c2)
                - query(r2, c1 - 1) + query(r1 - 1, c1 - 1);
    }
}
```
