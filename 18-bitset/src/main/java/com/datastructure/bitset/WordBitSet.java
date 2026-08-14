package com.datastructure.bitset;

import java.util.ArrayList;
import java.util.List;

/**
 * 비트를 long 배열에 눌러 담는다. **이 문제의 본체다.**
 *
 * 비트 i 는 `words[i / 64]` 의 `i % 64` 번째 자리에 있다.
 * 64가 2의 거듭제곱이라 나눗셈 대신 `i >>> 6`, 나머지 대신 `i & 63` 을 쓴다.
 * 11번 블룸 필터에서 이미 쓴 계산이다.
 *
 * **얻는 것이 셋이다.**
 *
 *   1. 메모리 8분의 1   (boolean 하나가 1바이트, 비트 하나가 1/8바이트)
 *   2. 집합 연산 64분의 1 걸음   (`a[i] & b[i]` 하나가 비트 64개를 처리한다)
 *   3. 개수 세기가 popcount 명령 하나   (`Long.bitCount`)
 *
 * 2번이 진짜 이유다. 데이터베이스의 비트맵 인덱스가 이것으로 산다.
 * "성별=남 AND 지역=서울 AND 등급=VIP" 를 비트맵 셋을 and 하는 것으로 답한다.
 *
 * **꼬리 비트를 조심해야 한다.** 크기 70 이면 워드가 2개(128비트)라 58비트가 남는다.
 * 남는 자리가 켜지면 cardinality 가 부풀고 nextSetBit 가 없는 비트를 준다.
 * 워드 단위 연산(or, xor)이 그 자리를 켤 수 있으므로 뒤에 정리해야 한다.
 */
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

    /** size 비트를 담으려면 워드가 몇 개 필요한가. */
    static int wordCountFor(int size) {
        // TODO 1: 올림 나눗셈이다. 65비트면 워드 2개다.
        //
        // `size / 64` 로 쓰면 65가 1이 되어 **마지막 비트를 담을 자리가 없다.**
        // 내림으로 잘리는 것을 막는 흔한 관용구가 있다.
        throw new UnsupportedOperationException("TODO 1: wordCountFor");
    }

    /** 이 비트가 몇 번째 워드에 있는가. */
    static int wordIndex(int bit) {
        // TODO 2: 64로 나눈 몫이다. 2의 거듭제곱이니 시프트로 쓴다.
        throw new UnsupportedOperationException("TODO 2: wordIndex");
    }

    /** 이 비트만 1인 마스크. */
    static long mask(int bit) {
        // TODO 3: 워드 안에서 몇 번째 자리인지를 구해 1을 그만큼 민다.
        //
        // **자바의 long 시프트는 시프트 수를 자동으로 64로 나눈 나머지를 쓴다.**
        // 그래서 `1L << bit` 만 써도 우연히 맞는다.
        // 그래도 `bit & 63` 을 명시하라. 우연에 기대는 코드는 읽는 사람이 확신할 수 없다.
        // (11번에서 이 자동 나머지 때문에 변종 하나가 동치가 됐다)
        //
        // 63번 자리는 부호 비트라 Long.MIN_VALUE 가 된다. 그게 정상이다.
        throw new UnsupportedOperationException("TODO 3: mask");
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
        // TODO 8: 워드를 통째로 뒤집고 **꼬리를 정리한다.**
        //
        // `~words[i]` 한 줄이면 64비트가 한 번에 뒤집힌다. 그것이 이 구조의 값이다.
        // 그런데 **마지막 워드의 남는 자리까지 켜진다.** 반드시 trimTail 을 불러야 한다.
        // 안 부르면 cardinality 가 크기보다 커지고 nextSetBit 이 없는 자리를 준다.
        throw new UnsupportedOperationException("TODO 8: flipAll");
    }

    @Override
    public int cardinality() {
        // TODO 4: 워드마다 켜진 비트 수를 더한다.
        //
        // `Long.bitCount` 는 CPU 의 popcount 명령 하나로 내려간다.
        // 비트를 하나씩 세는 것과 64배 차이다.
        throw new UnsupportedOperationException("TODO 4: cardinality");
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
        // TODO 5: from 이상에서 처음 켜진 비트. 없으면 -1.
        //
        // **꺼진 워드는 통째로 건너뛴다.** 이게 이 메서드의 존재 이유다.
        // 100만 비트 중 10개만 켜져 있어도 워드 15,625개만 보면 되고,
        // 실제로는 0 인 워드를 한 번의 비교로 넘긴다.
        //
        //   1. from 이 있는 워드를 가져와 **from 앞쪽 비트를 지운다.**
        //      `-1L << (from & 63)` 이 from 자리부터 위쪽만 1인 마스크다.
        //   2. 0 이 아니면 `Long.numberOfTrailingZeros` 로 몇 번째인지 바로 안다
        //   3. 0 이면 다음 워드로. 배열 끝까지 가면 -1
        //
        // 마지막에 **크기를 넘는 자리인지 확인**해야 한다.
        // 꼬리 비트가 켜져 있으면 size 이상의 자리를 반환할 수 있다.
        throw new UnsupportedOperationException("TODO 5: nextSetBit");
    }

    @Override
    public void and(BitVector other) {
        requireSameSize(other);
        // TODO 6: 상대도 WordBitSet 이면 **워드 단위로** 접는다. 아니면 비트 단위로.
        //
        // 같은 계약인데 **상대가 누구냐에 따라 비용이 다르다.**
        // 워드 경로는 words.length 번, 비트 경로는 size 번 돈다. 64배 차이다.
        // 실무 라이브러리가 instanceof 로 빠른 길을 여는 것이 이 모양이다.
        //
        // and 는 **1을 끄기만** 하므로 꼬리 비트가 새로 켜지지 않는다. 정리가 필요 없다.
        // (or, xor 는 다르다. 아래를 보라)
        throw new UnsupportedOperationException("TODO 6: and");
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
    /** 마지막 워드의 남는 비트를 꺼둔다. 안 그러면 cardinality 가 부풀 수 있다. */
    private void trimTail() {
        // TODO 7: size 가 64의 배수가 아니면 마지막 워드에 남는 자리가 있다.
        //
        // 그 자리를 전부 0으로 만든다. **쓰는 자리만 1인 마스크**를 만들어 and 하면 된다.
        // 예: size 가 70 이면 마지막 워드는 아래 6비트만 쓴다. 마스크는 0b111111 이다.
        //
        // size 가 64의 배수면 남는 자리가 없으므로 아무것도 하지 않는다.
        // **그 경우를 안 빼면 마스크가 0 이 되어 마지막 워드를 통째로 지운다.**
        throw new UnsupportedOperationException("TODO 7: trimTail");
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
