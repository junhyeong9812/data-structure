# pop/BloomFilter.java

문자열 전용 블룸 필터. BitSet 기반, Kirsch-Mitzenmacher 이중 해시.

```java
package com.datastructure.bloomfilter.pop;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public class BloomFilter {
    private final BitSet bits;
    private final int m;     // 비트 수
    private final int k;     // 해시 함수 수
    private int approximateCount;

    public BloomFilter(int expectedInsertions, double falsePositiveRate) {
        if (expectedInsertions <= 0) throw new IllegalArgumentException("n > 0");
        if (falsePositiveRate <= 0 || falsePositiveRate >= 1) {
            throw new IllegalArgumentException("0 < p < 1");
        }
        this.m = optimalNumOfBits(expectedInsertions, falsePositiveRate);
        this.k = optimalNumOfHashFunctions(expectedInsertions, m);
        this.bits = new BitSet(m);
        this.approximateCount = 0;
    }

    public void add(String element) {
        byte[] data = element.getBytes(StandardCharsets.UTF_8);
        int h1 = murmur3(data, 0);
        int h2 = murmur3(data, h1);
        boolean newBitSet = false;
        for (int i = 0; i < k; i++) {
            int combined = h1 + i * h2;
            int idx = Math.floorMod(combined, m);
            if (!bits.get(idx)) {
                bits.set(idx);
                newBitSet = true;
            }
        }
        if (newBitSet) approximateCount++;
    }

    public boolean mightContain(String element) {
        byte[] data = element.getBytes(StandardCharsets.UTF_8);
        int h1 = murmur3(data, 0);
        int h2 = murmur3(data, h1);
        for (int i = 0; i < k; i++) {
            int combined = h1 + i * h2;
            int idx = Math.floorMod(combined, m);
            if (!bits.get(idx)) return false;
        }
        return true;
    }

    public void clear() {
        bits.clear();
        approximateCount = 0;
    }

    public int size() {
        return m;
    }

    public int hashFunctionCount() {
        return k;
    }

    public int bitCount() {
        return bits.cardinality();
    }

    public int approximateCount() {
        return approximateCount;
    }

    public double expectedFpp() {
        // (1 - e^(-k*n/m))^k, n: 추가된 원소 수
        return Math.pow(1 - Math.exp(-(double) k * approximateCount / m), k);
    }

    private static int optimalNumOfBits(int n, double p) {
        return (int) Math.ceil(-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    private static int optimalNumOfHashFunctions(int n, int m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    // MurmurHash3 32-bit
    private static int murmur3(byte[] data, int seed) {
        int c1 = 0xcc9e2d51;
        int c2 = 0x1b873593;
        int h1 = seed;
        int len = data.length;
        int roundedEnd = (len & ~0x3);

        for (int i = 0; i < roundedEnd; i += 4) {
            int k1 = (data[i] & 0xff)
                    | ((data[i + 1] & 0xff) << 8)
                    | ((data[i + 2] & 0xff) << 16)
                    | (data[i + 3] << 24);
            k1 *= c1;
            k1 = Integer.rotateLeft(k1, 15);
            k1 *= c2;
            h1 ^= k1;
            h1 = Integer.rotateLeft(h1, 13);
            h1 = h1 * 5 + 0xe6546b64;
        }

        int k1 = 0;
        switch (len & 3) {
            case 3:
                k1 = (data[roundedEnd + 2] & 0xff) << 16;
                // fallthrough
            case 2:
                k1 |= (data[roundedEnd + 1] & 0xff) << 8;
                // fallthrough
            case 1:
                k1 |= (data[roundedEnd] & 0xff);
                k1 *= c1;
                k1 = Integer.rotateLeft(k1, 15);
                k1 *= c2;
                h1 ^= k1;
        }

        h1 ^= len;
        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;
        return h1;
    }
}
```
