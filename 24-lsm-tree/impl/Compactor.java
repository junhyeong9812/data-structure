package com.datastructure.lsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Compactor {

    private Compactor() {
    }

    public static <K extends Comparable<K>, V> List<Map.Entry<K, Object>> mergeEntries(
            List<SSTable<K, V>> newestFirst, boolean dropTombstones) {
        int n = newestFirst.size();
        int[] cursor = new int[n];
        List<Map.Entry<K, Object>> out = new ArrayList<>();

        while (true) {
            K best = null;
            int winner = -1;
            for (int i = 0; i < n; i++) {
                SSTable<K, V> t = newestFirst.get(i);
                if (cursor[i] >= t.size()) {
                    continue;
                }
                K k = t.keyAt(cursor[i]);
                if (best == null || k.compareTo(best) < 0) {
                    best = k;
                    winner = i;
                }
            }
            if (winner < 0) {
                break;
            }

            Object value = newestFirst.get(winner).valueAt(cursor[winner]);
            for (int i = 0; i < n; i++) {
                SSTable<K, V> t = newestFirst.get(i);
                if (cursor[i] < t.size() && t.keyAt(cursor[i]).compareTo(best) == 0) {
                    cursor[i]++;
                }
            }

            if (dropTombstones && Tombstone.is(value)) {
                continue;
            }
            out.add(SSTable.cell(best, value));
        }
        return out;
    }

    public static <K extends Comparable<K>, V> SSTable<K, V> compact(
            List<SSTable<K, V>> newestFirst, boolean dropTombstones, boolean withBloom) {
        if (newestFirst == null || newestFirst.isEmpty()) {
            throw new IllegalArgumentException("합칠 SSTable 이 없다");
        }
        return new SSTable<>(mergeEntries(newestFirst, dropTombstones), withBloom);
    }
}
