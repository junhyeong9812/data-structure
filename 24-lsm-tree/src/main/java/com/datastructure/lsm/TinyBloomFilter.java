package com.datastructure.lsm;

/**
 * 11번에서 만든 그 블룸 필터다. 모듈이 달라 가져다 쓸 수 없어서 최소한만 다시 만든다.
 *
 * 11번과 다른 점은 둘뿐이다. 제네릭을 안 쓰고(Object 를 받는다), 삭제와 확장이 없다.
 * SSTable 은 한 번 만들면 안 바뀌므로 담은 뒤에 늘어날 일이 아예 없기 때문이다.
 *
 * <h2>여기서 왜 필요한가</h2>
 *
 * LSM 의 조회는 최신 층부터 차례로 뒤진다. 없는 키를 확인하려면 모든 층을 다 봐야 한다.
 * SSTable 이 10장이면 10번이다.
 *
 * 그런데 블룸 필터의 대답은 비대칭이다.
 *
 *   mightContain 이 false  ->  확실히 없다
 *   mightContain 이 true   ->  아마 있다
 *
 * "확실히 없다" 쪽이 공짜로 층을 건너뛰게 해준다. 1% 오탐이면 안 뒤져도 될 층의 99% 를
 * 안 뒤진다. 이 문제의 측정이 정확히 그 숫자다(ReadAmplificationTest).
 *
 * 이 클래스에는 TODO 가 하나 있다. 11번을 이미 풀었다면 그대로 옮기면 된다.
 */
public final class TinyBloomFilter {

    private final int bits;
    private final int hashCount;
    private final long[] words;

    public TinyBloomFilter(int expectedInsertions, double falsePositiveRate) {
        if (expectedInsertions < 1) {
            throw new IllegalArgumentException("예상 원소 수는 1 이상이어야 한다: " + expectedInsertions);
        }
        if (!(falsePositiveRate > 0.0 && falsePositiveRate < 1.0)) {
            throw new IllegalArgumentException("오탐률은 0 과 1 사이여야 한다: " + falsePositiveRate);
        }
        this.bits = optimalBits(expectedInsertions, falsePositiveRate);
        this.hashCount = optimalHashCount(bits, expectedInsertions);
        this.words = new long[(bits + 63) / 64];
    }

    /** m = -n ln(p) / (ln2)^2. 1% 오탐이면 원소당 9.586 비트다. */
    static int optimalBits(int n, double p) {
        double m = -n * Math.log(p) / (Math.log(2) * Math.log(2));
        return (int) Math.max(1, Math.ceil(m));
    }

    /** k = (m/n) ln2. 더 늘리면 비트가 빨리 차서 오히려 오탐이 는다. */
    static int optimalHashCount(int m, int n) {
        int k = (int) Math.round((double) m / n * Math.log(2));
        return Math.max(1, k);
    }

    /** splitmix64. hashCode 를 그대로 쓰면 연속된 수가 몰려서 한 번 섞는다. */
    static long mix64(long z) {
        z += 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    int[] indexes(Object item) {
        // TODO 2: 이 원소가 켜야 할 비트 자리 hashCount 개를 구한다.
        //
        //   해시 함수를 k 개 만들 필요가 없다. 64비트 해시 하나를 반으로 갈라
        //   이중 해싱(Kirsch & Mitzenmacher, 2006) 으로 만든다.
        //
        //     h  = mix64(item.hashCode())      <- null 이면 0 으로 본다
        //     h1 = (int) h                     <- 아래 32비트
        //     h2 = (int) (h >>> 32)            <- 위 32비트
        //     i 번째 자리 = floorMod(h1 + i * h2, bits)
        //
        // 함정이 셋이다. 11번과 같은 것들이다.
        //   - h1 + i*h2 는 int 를 넘쳐 음수가 된다. 그냥 % 를 쓰면 음수 인덱스가 나온다.
        //     Math.floorMod 를 써라(05번 해시맵과 같은 함정이다).
        //   - h2 가 0 이면 모든 i 가 같은 자리를 가리켜 해시가 하나로 줄어든다. 0 이면 1 로 바꿔라.
        //   - hashCode() 를 안 섞고 쓰면 연속된 수가 몰린다. mix64 가 그걸 막는다.
        //
        // 11번에서 확인했듯이 두 번째 방어선은 어떤 테스트로도 못 잡는다.
        // hashCode 가 int 라 입력이 42억 가지인데 그중 mix64 의 상위 32비트가 0이 되는 값이
        // 하나도 없다. 그래도 남긴다. 해시 함수를 바꾸는 순간 살아나는 줄이다.
        throw new UnsupportedOperationException("TODO 2: indexes");
    }

    public void add(Object item) {
        for (int idx : indexes(item)) {
            words[idx >>> 6] |= 1L << (idx & 63);
        }
    }

    public boolean mightContain(Object item) {
        for (int idx : indexes(item)) {
            if ((words[idx >>> 6] & (1L << (idx & 63))) == 0) {
                return false;
            }
        }
        return true;
    }

    public int hashCount() {
        return hashCount;
    }

    public long bitSize() {
        return bits;
    }
}
