package com.datastructure.suffix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 테스트가 대조에 쓰는 느린 기준 구현들.
 *
 * 여기 있는 것은 전부 "확실히 맞지만 못 쓰는" 코드다.
 * 접미사를 통째로 만들어 정렬하고, 부분 문자열을 집합에 다 넣는다.
 * 그게 가능한 크기(길이 40 안팎)에서 무작위로 수백 번 대조하는 것이 이 문제의 주 검증 수단이다.
 */
final class SuffixFixtures {

    private SuffixFixtures() {
    }

    /** 접미사 문자열을 실제로 만들어 정렬한다. 시간 O(n^2 log n), 메모리 O(n^2). */
    static int[] sortedSuffixIndexes(String text) {
        int n = text.length();
        Integer[] box = new Integer[n];
        for (int i = 0; i < n; i++) {
            box[i] = i;
        }
        Arrays.sort(box, (a, b) -> text.substring(a).compareTo(text.substring(b)));
        int[] out = new int[n];
        for (int i = 0; i < n; i++) {
            out[i] = box[i];
        }
        return out;
    }

    /** 이웃한 접미사끼리 앞에서부터 직접 세는 LCP. Kasai 와 대조하는 상대다. */
    static int[] naiveLcp(String text, int[] sa) {
        int n = sa.length;
        int[] out = new int[n];
        for (int i = 1; i < n; i++) {
            int a = sa[i - 1];
            int b = sa[i];
            int h = 0;
            while (a + h < n && b + h < n && text.charAt(a + h) == text.charAt(b + h)) {
                h++;
            }
            out[i] = h;
        }
        return out;
    }

    /** 부분 문자열을 집합에 전부 넣어 센다. 파이썬의 {s[i:j]} 와 같은 방식이다. */
    static long distinctByBruteForce(String s) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j <= s.length(); j++) {
                set.add(s.substring(i, j));
            }
        }
        return set.size();
    }

    /**
     * 09번 접미사 트라이. 만들어진 노드 수가 곧 서로 다른 부분 문자열 수다.
     *
     * 같은 부분 문자열은 같은 경로라 노드를 다시 안 만들기 때문이다.
     * 답은 맞지만 노드가 최대 n(n+1)/2 개라 길이 1000 이 실질 한계다.
     */
    static long suffixTrieNodes(String s) {
        Map<Character, Object> root = new HashMap<>();
        long nodes = 0;
        for (int i = 0; i < s.length(); i++) {
            Map<Character, Object> cur = root;
            for (int j = i; j < s.length(); j++) {
                char c = s.charAt(j);
                @SuppressWarnings("unchecked")
                Map<Character, Object> next = (Map<Character, Object>) cur.get(c);
                if (next == null) {
                    next = new HashMap<>();
                    cur.put(c, next);
                    nodes++;
                }
                cur = next;
            }
        }
        return nodes;
    }

    /** 2번 이상 나오는 가장 긴 부분 문자열. 동률이면 사전순 최소. */
    static String longestRepeatedByBruteForce(String s) {
        Map<String, Integer> seen = new TreeMap<>();
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j <= s.length(); j++) {
                seen.merge(s.substring(i, j), 1, Integer::sum);
            }
        }
        String best = "";
        for (Map.Entry<String, Integer> e : seen.entrySet()) {
            if (e.getValue() >= 2 && e.getKey().length() > best.length()) {
                best = e.getKey();
            }
        }
        return best;
    }

    /** a 의 부분 문자열 중 b 에도 있는 가장 긴 것. 동률이면 사전순 최소. */
    static String longestCommonByBruteForce(String a, String b) {
        String best = "";
        for (int i = 0; i < a.length(); i++) {
            for (int j = i + 1; j <= a.length(); j++) {
                String t = a.substring(i, j);
                if (!b.contains(t)) {
                    continue;
                }
                if (t.length() > best.length()
                        || (t.length() == best.length() && t.compareTo(best) < 0)) {
                    best = t;
                }
            }
        }
        return best;
    }

    /** 패턴이 나오는 모든 위치를 오름차순으로. indexOf 로 직접 훑는다. */
    static List<Integer> occurrences(String text, String pattern) {
        List<Integer> out = new ArrayList<>();
        for (int i = text.indexOf(pattern); i >= 0; i = text.indexOf(pattern, i + 1)) {
            out.add(i);
        }
        return out;
    }

    /**
     * 64비트 LCG. 파이썬 참조 구현과 글자 그대로 같은 수열을 낸다.
     *
     * 기댓값을 파이썬으로 먼저 계산하려면 양쪽이 같은 문자열을 만들어야 한다.
     * java.util.Random 대신 이걸 쓰는 이유가 그것이다.
     */
    static String pseudoRandom(int n, int alphabet, long seed) {
        long x = seed;
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            x = x * 6364136223846793005L + 1442695040888963407L;
            sb.append((char) ('a' + (int) ((x >>> 33) % alphabet)));
        }
        return sb.toString();
    }
}
