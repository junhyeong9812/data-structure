package com.datastructure.bitset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 켜진 워드만 맵에 담는다. 아주 드문드문 켜질 때 쓴다.
 *
 * WordBitSet 은 크기를 미리 잡는다. 1000만 비트면 켜진 것이 5개여도 156,250 워드를 잡는다.
 * 여기서는 실제로 쓰는 워드만 담으므로 5개면 5개다.
 *
 * 대신 빽빽하면 진다. 맵 엔트리 하나가 워드 하나(8바이트)보다 훨씬 비싸다
 * (키 박싱 + 값 박싱 + 트리 노드). 1/6 보다 촘촘해지면 손해다.
 *
 * | 밀도 | 유리한 쪽 |
 * |---|---|
 * | 워드의 1/6 미만이 켜짐 | SparseBitSet |
 * | 그 이상 | WordBitSet |
 *
 * 공짜로 좋아지는 것은 없다. 05번(체이닝 대 개방주소), 09번(맵 트라이 대 배열 트라이),
 * 14번(맵 유니온파인드 대 배열 유니온파인드)에서 본 거래가 또 나온다.
 *
 * TreeMap 을 쓰는 이유는 nextSetBit 때문이다. 워드를 순서대로 훑어야 한다.
 * HashMap 이면 정렬을 따로 해야 한다(05번에서 본 이야기다).
 */
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
        // TODO 1: 그 워드가 없으면 만들고, 있으면 비트를 켠다.
        //
        // `merge` 를 쓰면 한 줄이다. 없으면 두 번째 인자를 넣고, 있으면 셋째로 합친다.
        throw new UnsupportedOperationException("TODO 1: set");
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
        // TODO 2: 비트를 끄고, **워드가 비면 맵에서 지운다.**
        //
        // 안 지우면 0 인 워드가 맵에 계속 쌓인다.
        // 그러면 켜진 것이 하나도 없는데 엔트리가 수천 개인 상태가 되고,
        // **희소하다는 성질 자체가 사라진다.** 이 구조의 존재 이유가 없어진다.
        throw new UnsupportedOperationException("TODO 2: clear");
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
        // TODO 3: 시작 워드부터 **있는 워드만** 순서대로 본다.
        //
        // TreeMap 의 tailMap 이 "이 키 이상"을 정렬 순서로 준다.
        // WordBitSet 은 빈 워드도 하나씩 넘겨야 하는데 여기서는 **아예 없다.**
        // 1000만 비트 중 5개만 켜져 있으면 5번만 돈다.
        //
        // 첫 워드에서는 from 앞쪽 비트를 지워야 한다. 나머지 워드는 통째로 본다.
        throw new UnsupportedOperationException("TODO 3: nextSetBit");
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
