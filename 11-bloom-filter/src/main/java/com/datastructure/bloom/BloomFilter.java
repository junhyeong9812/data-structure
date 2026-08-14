package com.datastructure.bloom;

/**
 * 비트 배열 하나로 만든 블룸 필터.
 *
 * 원소를 담지 않는다. 원소가 켜야 할 비트 자리만 기억한다.
 * 그래서 꺼낼 수 없고, 그래서 작다.
 *
 * 넣기: 원소를 k 개의 자리로 해싱해 그 비트를 전부 켠다.
 * 묻기: 그 k 개 자리가 전부 켜져 있으면 "아마 있다", 하나라도 꺼져 있으면 "확실히 없다".
 *
 * 오탐이 나는 이유가 여기 있다. 다른 원소들이 켜놓은 비트가 우연히 k 개를 다 덮을 수 있다.
 * 누락이 없는 이유도 같다. 한 번 켠 비트는 끄지 않으므로 넣은 원소는 반드시 통과한다.
 *
 * 크기와 해시 개수는 감으로 정하는 것이 아니다. 공식이 있다.
 *
 *   비트 수     m = -n * ln(p) / (ln2)^2
 *   해시 개수   k = (m/n) * ln2
 *
 * n 은 넣을 개수, p 는 목표 오탐률이다.
 * 1% 오탐이면 원소당 9.6비트, 해시 7개다. 원소가 무엇이든 상관없이 9.6비트다.
 * 이 "원소 크기와 무관하다"가 블룸 필터가 이기는 지점이다.
 *
 * k 를 늘리면 좋을 것 같지만 아니다. 너무 많으면 비트가 빨리 차서 오히려 오탐이 는다.
 * 위 공식의 k 가 정확히 그 최적점이다.
 */
public class BloomFilter<T> implements ProbabilisticSet<T> {

    private final int bits;
    private final int hashCount;
    private final int capacity;
    private final long[] words;
    private long inserted;

    public BloomFilter(int expectedInsertions, double falsePositiveRate) {
        if (expectedInsertions < 1) {
            throw new IllegalArgumentException("예상 원소 수는 1 이상이어야 한다: " + expectedInsertions);
        }
        if (!(falsePositiveRate > 0.0 && falsePositiveRate < 1.0)) {
            throw new IllegalArgumentException("오탐률은 0 과 1 사이여야 한다: " + falsePositiveRate);
        }
        this.capacity = expectedInsertions;
        this.bits = optimalBits(expectedInsertions, falsePositiveRate);
        this.hashCount = optimalHashCount(bits, expectedInsertions);
        this.words = new long[(bits + 63) / 64];
    }

    /** 목표 오탐률 p 로 원소 n 개를 담으려면 비트가 몇 개 필요한가. */
    static int optimalBits(int n, double p) {
        // TODO 1: m = -n * ln(p) / (ln2)^2
        //
        // p 가 1 보다 작으니 ln(p) 는 음수다. 그래서 앞에 마이너스가 붙는다.
        // 올림해야 하고(모자라면 오탐률이 목표를 넘는다), 최소 1 이어야 한다.
        throw new UnsupportedOperationException("TODO 1: optimalBits");
    }

    /** 비트 m 개에 원소 n 개를 담을 때 해시를 몇 개 쓰는 것이 최적인가. */
    static int optimalHashCount(int m, int n) {
        // TODO 2: k = (m/n) * ln2, 반올림, 최소 1
        //
        // **정수 나눗셈을 조심하라.** m/n 을 int 로 나누면 여기서 0 이 나온다.
        //
        // 왜 이게 최적인가. k 가 작으면 비트가 덜 켜져서 우연한 일치가 쉽고,
        // k 가 크면 비트가 빨리 차서 역시 일치가 쉬워진다. 그 사이 어딘가에 골짜기가 있다.
        throw new UnsupportedOperationException("TODO 2: optimalHashCount");
    }

    static long mix64(long z) {
        z += 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    /** 이 원소가 켜야 할 비트 자리 k 개. */
    int[] indexes(T item) {
        // TODO 3: **해시 함수를 k 개 만들 필요가 없다.**
        //
        // 서로 다른 해시 함수를 7개 구현하는 대신, 64비트 해시 하나를 반으로 갈라
        //   idx_i = h1 + i * h2   (i = 0..k-1)
        // 로 만들면 이론상 같은 성능이 나온다. (이중 해싱 - Kirsch & Mitzenmacher, 2006)
        //
        // 함정 셋.
        //   - h1 + i*h2 는 int 범위를 넘어 **음수가 된다.** Math.floorMod 를 써라.
        //     05번 음수 해시와 같은 함정이 또 나온다.
        //   - h2 가 0 이면 모든 i 가 같은 자리를 가리켜 해시가 하나로 줄어든다. 0 이면 1 로 바꿔라.
        //     (이 방어선은 **어떤 테스트로도 못 잡는다.** hashCode 가 int 라 입력이 42억 가지뿐인데,
        //      그중 mix64 의 상위 32비트가 0 이 되는 값이 하나도 없다 - 전수로 확인했다.
        //      그래도 남긴다. mix64 를 다른 해시로 바꾸는 순간 살아나는 방어선이기 때문이다.
        //      **테스트가 잡지 못하는 코드도 있다.**)
        //   - hashCode() 를 그대로 쓰면 안 된다. Integer 의 hashCode 는 값 그대로라
        //     연속된 수가 연속된 자리로 몰린다. mix64 로 섞고 시작하라.
        throw new UnsupportedOperationException("TODO 3: indexes");
    }

    @Override
    public void add(T item) {
        // TODO 4: 자리 k 개의 비트를 전부 켠다.
        //
        // 비트 idx 는 words[idx / 64] 의 (idx % 64) 번째 비트다.
        // 나눗셈 대신 idx >>> 6, 나머지 대신 idx & 63 을 쓴다(64 가 2의 거듭제곱이라 가능하다).
        //
        // 이미 켜져 있어도 그냥 켠다. **그래서 같은 원소를 두 번 넣어도 상태가 같다.**
        // insertedCount 만 는다.
        throw new UnsupportedOperationException("TODO 4: add");
    }

    @Override
    public boolean mightContain(T item) {
        // TODO 5: 자리 k 개가 **전부** 켜져 있으면 true.
        //
        // 하나라도 꺼져 있으면 즉시 false 다. 그 원소를 넣었다면 켜져 있어야 하기 때문이다.
        // **"확실히 없다"를 말할 수 있는 근거가 이 한 줄이다.**
        throw new UnsupportedOperationException("TODO 5: mightContain");
    }

    boolean bit(int index) {
        return (words[index >>> 6] & (1L << (index & 63))) != 0;
    }

    int hashCount() {
        return hashCount;
    }

    int capacity() {
        return capacity;
    }

    @Override
    public long insertedCount() {
        return inserted;
    }

    @Override
    public long bitSize() {
        return bits;
    }

    @Override
    public double expectedFalsePositiveRate() {
        return Math.pow(1 - Math.exp(-(double) hashCount * inserted / bits), hashCount);
    }

    @Override
    public void clear() {
        java.util.Arrays.fill(words, 0L);
        inserted = 0;
    }
}
