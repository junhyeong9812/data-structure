package com.datastructure.bloom;

/**
 * 비트 대신 계수기를 쓰는 블룸 필터. 삭제가 된다.
 *
 * 기본형에서 왜 삭제를 못 하는지부터 보라.
 * A 와 B 가 3번 비트를 함께 켜놓았는데 A 를 지운다고 3번을 끄면 B 가 같이 사라진다.
 * 그러면 "넣은 것은 반드시 통과한다"는 계약이 깨진다. 누락이 생긴다.
 *
 * 계수기를 두면 이 문제가 풀린다. 켤 때 1 올리고 지울 때 1 내린다.
 * 3번 자리가 2 였다가 1 이 되므로 B 는 그대로 통과한다.
 *
 * 대가는 메모리 8배다. 비트 하나가 바이트 하나가 된다.
 * (실무 구현은 4비트 계수기를 쓴다. 4배로 줄지만 최대 15까지밖에 못 센다)
 *
 * 그리고 두 가지 한계가 남는다.
 *
 *   1. 계수기가 넘치면(여기서는 255) 더 못 올린다. 그 자리는 영원히 안 내려간다.
 *      "포화"라고 부르고, 넘친 뒤로는 그 자리를 쓰는 원소를 못 지운다.
 *   2. 오탐인 원소를 지우면 남의 계수기를 깎는다. 넣은 적 없는 원소가
 *      우연히 통과하면 remove 가 그걸 진짜로 여기고 내린다.
 *      그러면 누락이 생긴다. 블룸 필터가 절대 안 한다던 그 일이다.
 *
 * 2번이 특히 중요하다. 삭제를 얻는 대신 "누락 없음" 보장을 잃는다.
 * 공짜로 기능이 늘어난 것이 아니다.
 */
public class CountingBloomFilter<T> implements ProbabilisticSet<T> {

    static final int MAX_COUNT = 255;

    private final int bits;
    private final int hashCount;
    private final byte[] counters;
    private long inserted;
    private long saturations;

    public CountingBloomFilter(int expectedInsertions, double falsePositiveRate) {
        if (expectedInsertions < 1) {
            throw new IllegalArgumentException("예상 원소 수는 1 이상이어야 한다: " + expectedInsertions);
        }
        if (!(falsePositiveRate > 0.0 && falsePositiveRate < 1.0)) {
            throw new IllegalArgumentException("오탐률은 0 과 1 사이여야 한다: " + falsePositiveRate);
        }
        this.bits = BloomFilter.optimalBits(expectedInsertions, falsePositiveRate);
        this.hashCount = BloomFilter.optimalHashCount(bits, expectedInsertions);
        this.counters = new byte[bits];
    }

    private int[] indexes(T item) {
        long h = BloomFilter.mix64(item == null ? 0 : item.hashCode());
        int h1 = (int) h;
        int h2 = (int) (h >>> 32);
        if (h2 == 0) {
            h2 = 1;
        }
        int[] out = new int[hashCount];
        for (int i = 0; i < hashCount; i++) {
            out[i] = Math.floorMod(h1 + i * h2, bits);
        }
        return out;
    }

    @Override
    public void add(T item) {
        // TODO 1: 자리 k 개의 계수기를 1씩 올린다.
        //
        // byte 는 자바에서 **부호 있는 8비트**라 128 이 넘으면 음수가 된다.
        // & 0xFF 로 읽어 0~255 로 다루고 (byte) 로 캐스팅해 쓴다.
        //
        // 이미 255 면 더 못 올린다. 그때는 saturations 를 세어둔다
        // (그 자리는 이제 영원히 안 내려간다는 뜻이라 알아야 한다).
        throw new UnsupportedOperationException("TODO 1: add");
    }

    /** 지웠으면 true. 애초에 없었으면(확실히) false. */
    public boolean remove(T item) {
        // TODO 2: 있을 때만 계수기를 1씩 내린다.
        //
        // 먼저 mightContain 으로 걸러야 한다. 안 그러면 넣은 적 없는 원소가
        // 남의 계수기를 깎는다.
        //
        // **그런데 mightContain 자체가 틀릴 수 있다.** 그 경우는 막을 방법이 없다.
        // 이 자료구조가 삭제를 얻는 대신 치르는 값이다. 위 클래스 주석 2번을 보라.
        //
        // 포화된 계수기(255)는 내리면 안 된다. 그 자리가 몇 번 켜졌는지 모르기 때문이다.
        throw new UnsupportedOperationException("TODO 2: remove");
    }

    @Override
    public boolean mightContain(T item) {
        // TODO 3: 자리 k 개의 계수기가 **전부 0 보다 크면** true.
        //
        // 기본형의 "비트가 켜져 있다"가 여기서는 "계수기가 0 이 아니다"다.
        throw new UnsupportedOperationException("TODO 3: mightContain");
    }

    int count(int index) {
        return counters[index] & 0xFF;
    }

    long saturations() {
        return saturations;
    }

    @Override
    public long insertedCount() {
        return inserted;
    }

    @Override
    public long bitSize() {
        return (long) bits * 8;
    }

    /** 계수기 하나가 몇 비트인지. 비트 배열 대비 몇 배를 쓰는지가 여기서 나온다. */
    public int bitsPerSlot() {
        return 8;
    }

    @Override
    public double expectedFalsePositiveRate() {
        return Math.pow(1 - Math.exp(-(double) hashCount * inserted / bits), hashCount);
    }

    @Override
    public void clear() {
        java.util.Arrays.fill(counters, (byte) 0);
        inserted = 0;
        saturations = 0;
    }
}
