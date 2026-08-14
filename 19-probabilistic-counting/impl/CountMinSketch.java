package com.datastructure.sketch;

public class CountMinSketch implements FrequencyEstimator {

    static final int MAX_WIDTH = 1 << 22;
    static final int MAX_DEPTH = 64;

    private final int width;
    private final int depth;
    private final long seed;
    private final long[][] table;
    private long total;

    public CountMinSketch(double epsilon, double delta) {
        this(widthFor(epsilon), depthFor(delta), 0L);
    }

    public CountMinSketch(int width, int depth, long seed) {
        if (width < 1) {
            throw new IllegalArgumentException("칸 수는 1 이상이어야 한다: " + width);
        }
        if (depth < 1) {
            throw new IllegalArgumentException("행 수는 1 이상이어야 한다: " + depth);
        }
        this.width = width;
        this.depth = depth;
        this.seed = seed;
        this.table = new long[depth][width];
    }

    static int widthFor(double epsilon) {
        if (!(epsilon > 0.0 && epsilon < 1.0)) {
            throw new IllegalArgumentException("오차율은 0 과 1 사이여야 한다: " + epsilon);
        }
        double w = Math.ceil(Math.E / epsilon);
        if (w > MAX_WIDTH) {
            throw new IllegalArgumentException("오차율이 너무 작다. 칸이 " + w + "개 필요하다: " + epsilon);
        }
        return (int) Math.max(1, w);
    }

    static int depthFor(double delta) {
        if (!(delta > 0.0 && delta < 1.0)) {
            throw new IllegalArgumentException("실패 확률은 0 과 1 사이여야 한다: " + delta);
        }
        double d = Math.ceil(Math.log(1.0 / delta));
        if (d > MAX_DEPTH) {
            throw new IllegalArgumentException("실패 확률이 너무 작다. 행이 " + d + "개 필요하다: " + delta);
        }
        return (int) Math.max(1, d);
    }

    static long mix64(long z) {
        z += 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    int[] indexes(int item) {
        long h = mix64(item ^ seed);
        int h1 = (int) h;
        int h2 = (int) (h >>> 32);
        if (h2 == 0) {
            h2 = 1;
        }
        int[] out = new int[depth];
        for (int i = 0; i < depth; i++) {
            out[i] = Math.floorMod(h1 + i * h2, width);
        }
        return out;
    }

    @Override
    public void add(int item) {
        add(item, 1);
    }

    @Override
    public void add(int item, long count) {
        if (count < 0) {
            throw new IllegalArgumentException("count 는 0 이상이어야 한다: " + count);
        }
        if (count == 0) {
            return;
        }
        int[] idx = indexes(item);
        for (int r = 0; r < depth; r++) {
            table[r][idx[r]] += count;
        }
        total += count;
    }

    @Override
    public long estimateCount(int item) {
        int[] idx = indexes(item);
        long min = Long.MAX_VALUE;
        for (int r = 0; r < depth; r++) {
            min = Math.min(min, table[r][idx[r]]);
        }
        return min;
    }

    @Override
    public long totalCount() {
        return total;
    }

    @Override
    public long memoryBytes() {
        return (long) width * depth * Long.BYTES;
    }

    public int width() {
        return width;
    }

    public int depth() {
        return depth;
    }

    public double epsilon() {
        return Math.E / width;
    }

    public double delta() {
        return Math.exp(-depth);
    }

    public long errorBound() {
        return (long) Math.ceil(epsilon() * total);
    }

    long cell(int row, int column) {
        return table[row][column];
    }
}
