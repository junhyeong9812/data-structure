# oop/StandardBloomFilter.java

`BloomFilter<T>` 표준 구현. `Function<T, byte[]>`을 통해 직렬화 추상화.

```java
package com.datastructure.bloomfilter.oop;

import java.util.BitSet;
import java.util.function.Function;

public class StandardBloomFilter<T> implements BloomFilter<T> {
    private final BitSet bits;
    private final int m;
    private final int k;
    private final Function<T, byte[]> serializer;
    private int approximateCount;

    public StandardBloomFilter(int expectedInsertions, double fpp,
                               Function<T, byte[]> serializer) {
        if (expectedInsertions <= 0) throw new IllegalArgumentException();
        if (fpp <= 0 || fpp >= 1) throw new IllegalArgumentException();
        this.m = (int) Math.ceil(-expectedInsertions * Math.log(fpp) / (Math.log(2) * Math.log(2)));
        this.k = Math.max(1, (int) Math.round((double) m / expectedInsertions * Math.log(2)));
        this.bits = new BitSet(m);
        this.serializer = serializer;
        this.approximateCount = 0;
    }

    public static StandardBloomFilter<String> ofStrings(int n, double fpp) {
        return new StandardBloomFilter<>(n, fpp, s -> s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    @Override
    public void add(T element) {
        byte[] data = serializer.apply(element);
        int h1 = hash(data, 0);
        int h2 = hash(data, h1);
        boolean novel = false;
        for (int i = 0; i < k; i++) {
            int idx = Math.floorMod(h1 + i * h2, m);
            if (!bits.get(idx)) {
                bits.set(idx);
                novel = true;
            }
        }
        if (novel) approximateCount++;
    }

    @Override
    public boolean mightContain(T element) {
        byte[] data = serializer.apply(element);
        int h1 = hash(data, 0);
        int h2 = hash(data, h1);
        for (int i = 0; i < k; i++) {
            int idx = Math.floorMod(h1 + i * h2, m);
            if (!bits.get(idx)) return false;
        }
        return true;
    }

    @Override
    public void clear() {
        bits.clear();
        approximateCount = 0;
    }

    @Override
    public int size() {
        return m;
    }

    @Override
    public int bitCount() {
        return bits.cardinality();
    }

    @Override
    public int hashFunctionCount() {
        return k;
    }

    @Override
    public int approximateCount() {
        return approximateCount;
    }

    @Override
    public double expectedFpp() {
        return Math.pow(1 - Math.exp(-(double) k * approximateCount / m), k);
    }

    private static int hash(byte[] data, int seed) {
        int h = 0x811c9dc5 ^ seed;
        for (byte b : data) {
            h ^= (b & 0xff);
            h *= 0x01000193;
        }
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;
        return h;
    }
}
```
