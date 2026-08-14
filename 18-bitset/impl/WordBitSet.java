package com.datastructure.bitset;

import java.util.ArrayList;
import java.util.List;

public class WordBitSet implements BitVector {

    static final int BITS_PER_WORD = 64;

    private final int size;
    private final long[] words;

    public WordBitSet(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("크기는 1 이상이어야 한다: " + size);
        }
        this.size = size;
        this.words = new long[wordCountFor(size)];
    }

    static int wordCountFor(int size) {
        return (size + BITS_PER_WORD - 1) / BITS_PER_WORD;
    }

    static int wordIndex(int bit) {
        return bit >>> 6;
    }

    static long mask(int bit) {
        return 1L << (bit & 63);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean get(int index) {
        requireIndex(index);
        return (words[wordIndex(index)] & mask(index)) != 0;
    }

    @Override
    public void set(int index) {
        requireIndex(index);
        words[wordIndex(index)] |= mask(index);
    }

    @Override
    public void set(int index, boolean value) {
        if (value) {
            set(index);
        } else {
            clear(index);
        }
    }

    @Override
    public void clear(int index) {
        requireIndex(index);
        words[wordIndex(index)] &= ~mask(index);
    }

    @Override
    public void flip(int index) {
        requireIndex(index);
        words[wordIndex(index)] ^= mask(index);
    }

    @Override
    public void clearAll() {
        java.util.Arrays.fill(words, 0L);
    }

    @Override
    public void flipAll() {
        for (int i = 0; i < words.length; i++) {
            words[i] = ~words[i];
        }
        trimTail();
    }

    @Override
    public int cardinality() {
        int n = 0;
        for (long w : words) {
            n += Long.bitCount(w);
        }
        return n;
    }

    @Override
    public boolean isEmpty() {
        for (long w : words) {
            if (w != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int nextSetBit(int from) {
        if (from < 0) {
            throw new IndexOutOfBoundsException("시작 자리가 음수다: " + from);
        }
        if (from >= size) {
            return -1;
        }
        int w = wordIndex(from);
        long word = words[w] & (-1L << (from & 63));
        while (true) {
            if (word != 0) {
                int bit = w * BITS_PER_WORD + Long.numberOfTrailingZeros(word);
                return bit < size ? bit : -1;
            }
            w++;
            if (w >= words.length) {
                return -1;
            }
            word = words[w];
        }
    }

    @Override
    public void and(BitVector other) {
        requireSameSize(other);
        if (other instanceof WordBitSet w) {
            for (int i = 0; i < words.length; i++) {
                words[i] &= w.words[i];
            }
            return;
        }
        for (int i = 0; i < size; i++) {
            if (get(i) && !other.get(i)) {
                clear(i);
            }
        }
    }

    @Override
    public void or(BitVector other) {
        requireSameSize(other);
        if (other instanceof WordBitSet w) {
            for (int i = 0; i < words.length; i++) {
                words[i] |= w.words[i];
            }
            trimTail();
            return;
        }
        for (int i = 0; i < size; i++) {
            if (other.get(i)) {
                set(i);
            }
        }
    }

    @Override
    public void xor(BitVector other) {
        requireSameSize(other);
        if (other instanceof WordBitSet w) {
            for (int i = 0; i < words.length; i++) {
                words[i] ^= w.words[i];
            }
            trimTail();
            return;
        }
        for (int i = 0; i < size; i++) {
            if (other.get(i)) {
                flip(i);
            }
        }
    }

    @Override
    public void andNot(BitVector other) {
        requireSameSize(other);
        if (other instanceof WordBitSet w) {
            for (int i = 0; i < words.length; i++) {
                words[i] &= ~w.words[i];
            }
            return;
        }
        for (int i = 0; i < size; i++) {
            if (other.get(i)) {
                clear(i);
            }
        }
    }

    /** 마지막 워드의 남는 비트를 꺼둔다. 안 그러면 cardinality 가 부풀 수 있다. */
    private void trimTail() {
        int used = size & 63;
        if (used != 0) {
            words[words.length - 1] &= (1L << used) - 1;
        }
    }

    @Override
    public List<Integer> toList() {
        List<Integer> out = new ArrayList<>();
        for (int i = nextSetBit(0); i >= 0; i = i + 1 < size ? nextSetBit(i + 1) : -1) {
            out.add(i);
        }
        return out;
    }

    @Override
    public int unitCount() {
        return words.length;
    }

    @Override
    public long memoryBytes() {
        return (long) words.length * 8;
    }

    long word(int i) {
        return words[i];
    }

    private void requireIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "비트 " + index + " 가 범위를 벗어났다 (크기 " + size + ")");
        }
    }

    private void requireSameSize(BitVector other) {
        if (other == null) {
            throw new IllegalArgumentException("상대가 필요하다");
        }
        if (other.size() != size) {
            throw new IllegalArgumentException("크기가 다르다: " + size + " 대 " + other.size());
        }
    }
}
