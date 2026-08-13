package com.datastructure.cache;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LRUCacheProblems {

    private LRUCacheProblems() {
    }

    public static double hitRatio(int capacity, int[] accesses) {
        if (accesses == null || accesses.length == 0) {
            return 0.0;
        }
        Cache<Integer, Boolean> cache = new LRUCache<>(capacity);
        for (int key : accesses) {
            if (cache.get(key) == null) {
                cache.put(key, Boolean.TRUE);
            }
        }
        return (double) cache.hits() / accesses.length;
    }

    public static double optimalHitRatio(int capacity, int[] accesses) {
        if (accesses == null || accesses.length == 0) {
            return 0.0;
        }
        if (capacity < 1) {
            throw new IllegalArgumentException("용량은 1 이상이어야 한다: " + capacity);
        }
        Map<Integer, Deque<Integer>> nextUse = new HashMap<>();
        for (int i = accesses.length - 1; i >= 0; i--) {
            nextUse.computeIfAbsent(accesses[i], k -> new ArrayDeque<>()).addFirst(i);
        }

        Set<Integer> cache = new HashSet<>();
        int hits = 0;
        for (int key : accesses) {
            nextUse.get(key).pollFirst();
            if (cache.contains(key)) {
                hits++;
                continue;
            }
            if (cache.size() == capacity) {
                Integer victim = null;
                int farthest = -1;
                for (Integer c : cache) {
                    Deque<Integer> q = nextUse.get(c);
                    int next = q.isEmpty() ? Integer.MAX_VALUE : q.peekFirst();
                    if (next > farthest) {
                        farthest = next;
                        victim = c;
                    }
                }
                cache.remove(victim);
            }
            cache.add(key);
        }
        return (double) hits / accesses.length;
    }

    public static List<Integer> deduplicateStream(int capacity, int[] stream) {
        List<Integer> out = new ArrayList<>();
        if (stream == null || stream.length == 0) {
            return out;
        }
        Cache<Integer, Boolean> seen = new LRUCache<>(capacity);
        for (int x : stream) {
            if (seen.get(x) != null) {
                continue;
            }
            out.add(x);
            seen.put(x, Boolean.TRUE);
        }
        return out;
    }
}
