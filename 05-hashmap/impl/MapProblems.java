package com.datastructure.hashmap;

/**
 * [구현] 해시맵 응용 문제.
 *
 * 세 문제 전부 "이미 본 것인지"를 O(1) 로 묻는 것이 핵심이다.
 */
public final class MapProblems {

    private MapProblems() {
    }

    /** 처음 보면 1, 이미 봤으면 +1. containsKey 로 구분한다(값이 null 일 수 있으므로 get 만으로는 부족하다). */
    public static void countFrequencies(int[] values, Map<Integer, Integer> counts) {
        for (int v : values) {
            counts.put(v, counts.containsKey(v) ? counts.get(v) + 1 : 1);
        }
    }

    /**
     * 한 번만 훑는다.
     *
     * 지금 값이 v 면 짝은 target - v 다. 그 짝을 앞에서 이미 봤는지만 물으면 된다.
     * 모든 쌍을 다 볼 필요가 없다. O(n^2) 이 O(n) 이 되는 지점이 여기다.
     *
     * 같은 값이 여러 번 나오면 처음 위치만 담는다. 그래야 첫 인덱스가 가장 작아진다.
     * 짝을 먼저 확인하고 나서 담는 순서라야 [5], target=10 처럼 자기 자신과 짝짓는 일이 없다.
     */
    public static int[] twoSum(int[] values, int target, Map<Integer, Integer> seen) {
        for (int i = 0; i < values.length; i++) {
            int need = target - values[i];
            if (seen.containsKey(need)) {
                return new int[]{seen.get(need), i};
            }
            if (!seen.containsKey(values[i])) {
                seen.put(values[i], i);
            }
        }
        return new int[0];
    }

    /**
     * 두 번 훑는다. 한 번은 세고, 한 번은 앞에서부터 1인 것을 찾는다.
     * 두 번 훑어도 O(n) 이다. 상수배는 복잡도를 바꾸지 않는다.
     */
    public static int firstUniqueChar(String input, Map<Character, Integer> counts) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            counts.put(c, counts.containsKey(c) ? counts.get(c) + 1 : 1);
        }
        for (int i = 0; i < input.length(); i++) {
            if (counts.get(input.charAt(i)) == 1) {
                return i;
            }
        }
        return -1;
    }
}
