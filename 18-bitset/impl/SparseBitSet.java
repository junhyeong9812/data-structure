package com.datastructure.bitset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SparseBitSet implements BitVector {

    /** 맵 엔트리 하나가 대략 이만큼 든다(키 박싱 + 값 박싱 + 노드). */
    static final int BYTES_PER_ENTRY = 48;

    private final int size;
    private final TreeMap<Integer, Long> words = new TreeMap<>();

    public SparseBitSet(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("크기는 1 이상이어야 한다: " + size);
        }
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean get(int index) {
        requireIndex(index);
        Long w = words.get(WordBitSet.wordIndex(index));
        return w != null && (w & WordBitSet.mask(index)) != 0;
    }

    @Override
    public void set(int index) {
        requireIndex(index);
        int w = WordBitSet.wordIndex(index);
        words.merge(w, WordBitSet.mask(index), (a, b) -> a | b);
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
        int w = WordBitSet.wordIndex(index);
        Long cur = words.get(w);
        if (cur == null) {
            return;
        }
        long next = cur & ~WordBitSet.mask(index);
        if (next == 0) {
            words.remove(w);
        } else {
            words.put(w, next);
        }
    }

    @Override
    public void flip(int index) {
        set(index, !get(index));
    }

    @Override
    public void clearAll() {
        words.clear();
    }

    @Override
    public void flipAll() {
        // 희소 구조의 약점이 그대로 드러나는 연산이다.
        // 뒤집으면 대부분이 켜지므로 **거의 모든 워드를 만들게 된다.**
        // 희소하다는 전제가 무너지는 자리라, 실무 구현은 "뒤집힘" 플래그를 따로 두기도 한다.
        for (int i = 0; i < size; i++) {
            set(i, !get(i));
        }
    }

    @Override
    public int cardinality() {
        int n = 0;
        for (long w : words.values()) {
            n += Long.bitCount(w);
        }
        return n;
    }

    @Override
    public boolean isEmpty() {
        return words.isEmpty();
    }

    @Override
    public int nextSetBit(int from) {
        if (from < 0) {
            throw new IndexOutOfBoundsException("시작 자리가 음수다: " + from);
        }
        if (from >= size) {
            return -1;
        }
        int startWord = WordBitSet.wordIndex(from);
        for (Map.Entry<Integer, Long> e : words.tailMap(startWord, true).entrySet()) {
            long w = e.getValue();
            if (e.getKey() == startWord) {
                w &= -1L << (from & 63);
            }
            if (w != 0) {
                int bit = e.getKey() * WordBitSet.BITS_PER_WORD + Long.numberOfTrailingZeros(w);
                return bit < size ? bit : -1;
            }
        }
        return -1;
    }

    @Override
    public void and(BitVector other) {
        requireSameSize(other);
        for (int i = nextSetBit(0); i >= 0; i = i + 1 < size ? nextSetBit(i + 1) : -1) {
            if (!other.get(i)) {
                clear(i);
            }
        }
    }

    @Override
    public void or(BitVector other) {
        requireSameSize(other);
        for (int i = other.nextSetBit(0); i >= 0; i = i + 1 < size ? other.nextSetBit(i + 1) : -1) {
            set(i);
        }
    }

    @Override
    public void xor(BitVector other) {
        requireSameSize(other);
        for (int i = other.nextSetBit(0); i >= 0; i = i + 1 < size ? other.nextSetBit(i + 1) : -1) {
            flip(i);
        }
    }

    @Override
    public void andNot(BitVector other) {
        requireSameSize(other);
        for (int i = other.nextSetBit(0); i >= 0; i = i + 1 < size ? other.nextSetBit(i + 1) : -1) {
            clear(i);
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
        return words.size();
    }

    @Override
    public long memoryBytes() {
        return (long) words.size() * BYTES_PER_ENTRY;
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
