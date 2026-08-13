package com.datastructure.fenwick;

public class FenwickTree2D {

    private final int rows;
    private final int cols;
    private final long[][] tree;

    public FenwickTree2D(int rows, int cols) {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("행과 열은 1 이상이어야 한다: " + rows + " x " + cols);
        }
        this.rows = rows;
        this.cols = cols;
        this.tree = new long[rows + 1][cols + 1];
    }

    public void add(int row, int col, long delta) {
        requireCell(row, col);
        for (int x = row + 1; x <= rows; x += x & -x) {
            for (int y = col + 1; y <= cols; y += y & -y) {
                tree[x][y] += delta;
            }
        }
    }

    /** (0,0) 부터 (row,col) 까지의 직사각형 합. row 나 col 이 음수면 0. */
    public long prefixSum(int row, int col) {
        if (row < 0 || col < 0) {
            return 0L;
        }
        requireCell(row, col);
        long sum = 0;
        for (int x = row + 1; x > 0; x -= x & -x) {
            for (int y = col + 1; y > 0; y -= y & -y) {
                sum += tree[x][y];
            }
        }
        return sum;
    }

    /** (r1,c1) 부터 (r2,c2) 까지의 직사각형 합. 뒤집힌 범위면 0. */
    public long rangeSum(int r1, int c1, int r2, int c2) {
        requireCell(r1, c1);
        requireCell(r2, c2);
        if (r1 > r2 || c1 > c2) {
            return 0L;
        }
        return prefixSum(r2, c2)
                - prefixSum(r1 - 1, c2)
                - prefixSum(r2, c1 - 1)
                + prefixSum(r1 - 1, c1 - 1);
    }

    public long get(int row, int col) {
        return rangeSum(row, col, row, col);
    }

    public void set(int row, int col, long value) {
        add(row, col, value - get(row, col));
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    private void requireCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException(
                    "칸 (" + row + ", " + col + ") 이 범위를 벗어났다 (" + rows + " x " + cols + ")");
        }
    }
}
