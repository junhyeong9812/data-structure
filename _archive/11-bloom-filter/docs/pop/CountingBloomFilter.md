# pop/CountingBloomFilter.java

카운팅 블룸 필터. 비트 대신 정수 카운터 배열 사용 → 삭제(remove) 가능.

```java
package com.datastructure.bloomfilter.pop;

import java.nio.charset.StandardCharsets;

public class CountingBloomFilter {
    private final byte[] counters; // 0~255
    private final int m;
    private final int k;

    public CountingBloomFilter(int expectedInsertions, double fpp) {
        this.m = (int) Math.ceil(-expectedInsertions * Math.log(fpp) / (Math.log(2) * Math.log(2)));
        this.k = Math.max(1, (int) Math.round((double) m / expectedInsertions * Math.log(2)));
        this.counters = new byte[m];
    }

    public void add(String element) {
        forEachIndex(element, idx -> {
            int c = counters[idx] & 0xff;
            if (c < 255) counters[idx] = (byte) (c + 1);
        });
    }

    public boolean remove(String element) {
        if (!mightContain(element)) return false;
        forEachIndex(element, idx -> {
            int c = counters[idx] & 0xff;
            if (c > 0 && c < 255) counters[idx] = (byte) (c - 1);
        });
        return true;
    }

    public boolean mightContain(String element) {
        byte[] data = element.getBytes(StandardCharsets.UTF_8);
        int h1 = hash(data, 0);
        int h2 = hash(data, h1);
        for (int i = 0; i < k; i++) {
            int idx = Math.floorMod(h1 + i * h2, m);
            if ((counters[idx] & 0xff) == 0) return false;
        }
        return true;
    }

    private void forEachIndex(String element, java.util.function.IntConsumer fn) {
        byte[] data = element.getBytes(StandardCharsets.UTF_8);
        int h1 = hash(data, 0);
        int h2 = hash(data, h1);
        for (int i = 0; i < k; i++) {
            fn.accept(Math.floorMod(h1 + i * h2, m));
        }
    }

    public void clear() {
        java.util.Arrays.fill(counters, (byte) 0);
    }

    public int size() {
        return m;
    }

    public int hashFunctionCount() {
        return k;
    }

    private static int hash(byte[] data, int seed) {
        // 간단한 FNV-1a 변형 + seed
        int h = 0x811c9dc5 ^ seed;
        for (byte b : data) {
            h ^= (b & 0xff);
            h *= 0x01000193;
        }
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        return h;
    }
}
```
