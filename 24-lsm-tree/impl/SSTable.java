package com.datastructure.lsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SSTable<K extends Comparable<K>, V> {

    static final int HEADER_BYTES = 8;

    private final Object[] keys;
    private final Object[] values;
    private final long bytes;
    private final TinyBloomFilter bloom;

    public SSTable(List<Map.Entry<K, Object>> sortedEntries, boolean withBloom) {
        if (sortedEntries == null) {
            throw new IllegalArgumentException("엔트리 목록이 null 이다");
        }
        int n = sortedEntries.size();
        Object[] k = new Object[n];
        Object[] v = new Object[n];
        TinyBloomFilter filter = withBloom ? new TinyBloomFilter(Math.max(1, n), 0.01) : null;
        long total = 0;
        for (int i = 0; i < n; i++) {
            Map.Entry<K, Object> e = sortedEntries.get(i);
            if (e.getKey() == null || e.getValue() == null) {
                throw new IllegalArgumentException("키와 값에 null 을 담을 수 없다");
            }
            if (i > 0 && compare(k[i - 1], e.getKey()) >= 0) {
                throw new IllegalArgumentException(
                        "SSTable 은 정렬된 입력만 받는다: " + k[i - 1] + " 다음에 " + e.getKey());
            }
            k[i] = e.getKey();
            v[i] = e.getValue();
            total += entryBytes(e.getKey(), e.getValue());
            if (filter != null) {
                filter.add(e.getKey());
            }
        }
        this.keys = k;
        this.values = v;
        this.bytes = total;
        this.bloom = filter;
    }

    int indexOf(K key) {
        int lo = 0;
        int hi = keys.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int cmp = compare(keys[mid], key);
            if (cmp == 0) {
                return mid;
            }
            if (cmp < 0) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private static int compare(Object a, Object b) {
        return ((Comparable<Object>) a).compareTo(b);
    }

    public static long entryBytes(Object key, Object value) {
        long k = String.valueOf(key).length();
        long v = Tombstone.is(value) ? 0 : String.valueOf(value).length();
        return HEADER_BYTES + k + v;
    }

    public static <K extends Comparable<K>> Map.Entry<K, Object> cell(K key, Object value) {
        return Map.entry(key, value);
    }

    public Object rawValue(K key) {
        int idx = indexOf(key);
        return idx < 0 ? null : values[idx];
    }

    public boolean mightContain(K key) {
        return bloom == null || bloom.mightContain(key);
    }

    public boolean hasBloom() {
        return bloom != null;
    }

    public int size() {
        return keys.length;
    }

    public long byteSize() {
        return bytes;
    }

    @SuppressWarnings("unchecked")
    public K keyAt(int index) {
        return (K) keys[index];
    }

    public Object valueAt(int index) {
        return values[index];
    }

    public boolean isTombstoneAt(int index) {
        return Tombstone.is(values[index]);
    }

    public List<Map.Entry<K, Object>> entries() {
        List<Map.Entry<K, Object>> out = new ArrayList<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            out.add(cell(keyAt(i), values[i]));
        }
        return out;
    }

    public int tombstoneCount() {
        int c = 0;
        for (Object v : values) {
            if (Tombstone.is(v)) {
                c++;
            }
        }
        return c;
    }
}
