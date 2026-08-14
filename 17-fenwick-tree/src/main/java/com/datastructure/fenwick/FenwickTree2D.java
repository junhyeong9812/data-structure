package com.datastructure.fenwick;

/**
 * 2차원 펜윅 트리. 직사각형 영역의 합을 O(log^2 n) 에 답한다.
 *
 * 1차원 코드를 행에 한 번, 열에 한 번 겹쳐 쓰면 된다. 루프가 이중이 될 뿐이다.
 * 이런 식으로 차원을 올릴 수 있다는 것이 이 구조의 예쁜 점이다.
 * (3차원도 같은 방식으로 된다. 비용은 O(log^3 n))
 *
 * 직사각형 합은 포함-배제로 구한다.
 *
 *   rangeSum(r1,c1,r2,c2) = P(r2,c2) - P(r1-1,c2) - P(r2,c1-1) + P(r1-1,c1-1)
 *
 * 큰 사각형에서 위쪽과 왼쪽을 빼면 왼쪽 위 귀퉁이를 두 번 뺀 셈이라 한 번 되돌려준다.
 * 마지막 항의 부호를 빠뜨리는 것이 여기서 제일 흔한 실수다.
 *
 * 실무 대응물: 이미지의 적분 영상(integral image), 히트맵 집계, OLAP 큐브의 부분 합.
 */
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
        // TODO 1: 1차원 add 를 두 번 겹친다.
        //
        // 바깥 루프는 행을 x += x & -x 로, 안쪽 루프는 열을 y += y & -y 로 올린다.
        // **안쪽 루프의 시작값을 매번 다시 잡아야 한다.** 바깥이 한 칸 갈 때마다
        // 열은 처음부터 다시 훑는다.
        throw new UnsupportedOperationException("TODO 1: add");
    }

    /** (0,0) 부터 (row,col) 까지의 직사각형 합. row 나 col 이 음수면 0. */
    /** (0,0) 부터 (row,col) 까지의 직사각형 합. row 나 col 이 음수면 0. */
    public long prefixSum(int row, int col) {
        if (row < 0 || col < 0) {
            return 0L;
        }
        requireCell(row, col);
        // TODO 2: 1차원 prefixSum 을 두 번 겹친다. 양쪽 다 x -= x & -x 로 내려간다.
        //
        // 음수 경계에서 0 을 반환하는 것이 rangeSum 의 포함-배제를 성립시킨다.
        // 그 처리가 위에 이미 있다.
        throw new UnsupportedOperationException("TODO 2: prefixSum");
    }

    /** (r1,c1) 부터 (r2,c2) 까지의 직사각형 합. 뒤집힌 범위면 0. */
    public long rangeSum(int r1, int c1, int r2, int c2) {
        requireCell(r1, c1);
        requireCell(r2, c2);
        if (r1 > r2 || c1 > c2) {
            return 0L;
        }
        // TODO 3: 포함-배제. 네 번 부르고 부호를 맞춘다.
        //
        // 큰 사각형에서 위와 왼쪽을 빼면 왼쪽 위 귀퉁이를 두 번 뺀 셈이다. 한 번 되돌려준다.
        throw new UnsupportedOperationException("TODO 3: rangeSum");
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
