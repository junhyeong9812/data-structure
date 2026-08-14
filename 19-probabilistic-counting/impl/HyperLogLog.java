package com.datastructure.sketch;

public class HyperLogLog implements CardinalityEstimator {

    static final int MIN_PRECISION = 4;
    static final int MAX_PRECISION = 16;

    private final int p;
    private final int m;
    private final byte[] registers;

    public HyperLogLog(int p) {
        if (p < MIN_PRECISION || p > MAX_PRECISION) {
            throw new IllegalArgumentException(
                    "정밀도는 " + MIN_PRECISION + " 과 " + MAX_PRECISION + " 사이여야 한다: " + p);
        }
        this.p = p;
        this.m = 1 << p;
        this.registers = new byte[m];
    }

    static double alpha(int m) {
        return switch (m) {
            case 16 -> 0.673;
            case 32 -> 0.697;
            case 64 -> 0.709;
            default -> 0.7213 / (1 + 1.079 / m);
        };
    }

    @Override
    public void add(int item) {
        long h = CountMinSketch.mix64(item);
        int index = (int) (h >>> (64 - p));
        int rank = Math.min(Long.numberOfLeadingZeros(h << p) + 1, 64 - p + 1);
        if (rank > registers[index]) {
            registers[index] = (byte) rank;
        }
    }

    public long rawEstimate() {
        double sum = 0.0;
        for (byte r : registers) {
            sum += Math.pow(2.0, -r);
        }
        return Math.round(alpha(m) * (double) m * m / sum);
    }

    @Override
    public long estimate() {
        long raw = rawEstimate();
        int zeros = zeroRegisters();
        if (raw <= 2.5 * m && zeros > 0) {
            return Math.round(m * Math.log((double) m / zeros));
        }
        return raw;
    }

    public void merge(HyperLogLog other) {
        if (other == null) {
            throw new IllegalArgumentException("병합할 상대가 없다");
        }
        if (other.p != p) {
            throw new IllegalArgumentException("정밀도가 다르면 병합할 수 없다: " + p + " 대 " + other.p);
        }
        for (int i = 0; i < m; i++) {
            if (other.registers[i] > registers[i]) {
                registers[i] = other.registers[i];
            }
        }
    }

    @Override
    public long memoryBytes() {
        return registers.length;
    }

    public int precision() {
        return p;
    }

    public int registerCount() {
        return m;
    }

    int register(int index) {
        return registers[index];
    }

    int zeroRegisters() {
        int zeros = 0;
        for (byte r : registers) {
            if (r == 0) {
                zeros++;
            }
        }
        return zeros;
    }
}
