package com.datastructure.bitset;

import java.util.ArrayList;
import java.util.List;

public class BooleanArrayBitSet implements BitVector {

    private final boolean[] bits;

    public BooleanArrayBitSet(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("크기는 1 이상이어야 한다: " + size);
        }
        this.bits = new boolean[size];
    }

    @Override
    public int size() {
        return bits.length;
    }

    @Override
    public boolean get(int index) {
        requireIndex(index);
        return bits[index];
    }

    @Override
    public void set(int index) {
        set(index, true);
    }

    @Override
    public void set(int index, boolean value) {
        requireIndex(index);
        bits[index] = value;
    }

    @Override
    public void clear(int index) {
        set(index, false);
    }

    @Override
    public void flip(int index) {
        requireIndex(index);
        bits[index] = !bits[index];
    }

    @Override
    public void clearAll() {
        java.util.Arrays.fill(bits, false);
    }

    @Override
    public void flipAll() {
        for (int i = 0; i < bits.length; i++) {
            bits[i] = !bits[i];
        }
    }

    @Override
    public int cardinality() {
        int n = 0;
        for (boolean b : bits) {
            if (b) {
                n++;
            }
        }
        return n;
    }

    @Override
    public boolean isEmpty() {
        return cardinality() == 0;
    }

    @Override
    public int nextSetBit(int from) {
        if (from < 0) {
            throw new IndexOutOfBoundsException("시작 자리가 음수다: " + from);
        }
        for (int i = from; i < bits.length; i++) {
            if (bits[i]) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void and(BitVector other) {
        requireSameSize(other);
        for (int i = 0; i < bits.length; i++) {
            bits[i] = bits[i] && other.get(i);
        }
    }

    @Override
    public void or(BitVector other) {
        requireSameSize(other);
        for (int i = 0; i < bits.length; i++) {
            bits[i] = bits[i] || other.get(i);
        }
    }

    @Override
    public void xor(BitVector other) {
        requireSameSize(other);
        for (int i = 0; i < bits.length; i++) {
            bits[i] = bits[i] ^ other.get(i);
        }
    }

    @Override
    public void andNot(BitVector other) {
        requireSameSize(other);
        for (int i = 0; i < bits.length; i++) {
            bits[i] = bits[i] && !other.get(i);
        }
    }

    @Override
    public List<Integer> toList() {
        List<Integer> out = new ArrayList<>();
        for (int i = nextSetBit(0); i >= 0; i = i + 1 <= size() - 1 ? nextSetBit(i + 1) : -1) {
            out.add(i);
        }
        return out;
    }

    @Override
    public int unitCount() {
        return bits.length;
    }

    @Override
    public long memoryBytes() {
        return bits.length;
    }

    private void requireIndex(int index) {
        if (index < 0 || index >= bits.length) {
            throw new IndexOutOfBoundsException(
                    "비트 " + index + " 가 범위를 벗어났다 (크기 " + bits.length + ")");
        }
    }

    private void requireSameSize(BitVector other) {
        if (other == null) {
            throw new IllegalArgumentException("상대가 필요하다");
        }
        if (other.size() != size()) {
            throw new IllegalArgumentException(
                    "크기가 다르다: " + size() + " 대 " + other.size());
        }
    }
}
