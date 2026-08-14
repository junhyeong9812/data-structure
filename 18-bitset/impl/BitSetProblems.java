package com.datastructure.bitset;

import java.util.ArrayList;
import java.util.List;

public final class BitSetProblems {

    private BitSetProblems() {
    }

    public static BitVector sieve(int n) {
        if (n < 2) {
            throw new IllegalArgumentException("n 은 2 이상이어야 한다: " + n);
        }
        WordBitSet primes = new WordBitSet(n + 1);
        for (int i = 2; i <= n; i++) {
            primes.set(i);
        }
        for (int p = 2; (long) p * p <= n; p++) {
            if (!primes.get(p)) {
                continue;
            }
            for (int multiple = p * p; multiple <= n; multiple += p) {
                primes.clear(multiple);
            }
        }
        return primes;
    }

    public static double jaccard(BitVector a, BitVector b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("두 집합이 모두 필요하다");
        }
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("크기가 다르다: " + a.size() + " 대 " + b.size());
        }
        int intersection = 0;
        int union = 0;
        for (int i = 0; i < a.size(); i++) {
            boolean x = a.get(i);
            boolean y = b.get(i);
            if (x && y) {
                intersection++;
            }
            if (x || y) {
                union++;
            }
        }
        return union == 0 ? 1.0 : (double) intersection / union;
    }

    public static List<Integer> enumerateSubsets(int mask) {
        if (mask < 0) {
            throw new IllegalArgumentException("마스크는 0 이상이어야 한다: " + mask);
        }
        List<Integer> out = new ArrayList<>();
        for (int s = mask; ; s = (s - 1) & mask) {
            out.add(s);
            if (s == 0) {
                break;
            }
        }
        return out;
    }
}
