package com.datastructure.hashmap;

/**
 * 해시맵으로 푸는 응용 문제들.
 *
 * 전부 Map 인터페이스만 받는다. 세 구현 어느 것으로도 돌아간다.
 *
 * 해시맵이 필요한 상황에는 공통점이 있다.
 * "이미 본 것인지"를 되풀이해서 물어야 할 때다. 그 질문이 O(1) 이 되면 알고리즘 전체가 한 단계 빨라진다.
 */
public final class MapProblems {

    private MapProblems() {
    }

    /**
     * 문제 1. 빈도 세기
     *
     * 각 값이 몇 번 나오는지 counts 에 담는다.
     *
     *   [1, 2, 2, 3, 3, 3]  ->  {1=1, 2=2, 3=3}
     *
     * 생각할 것
     *   - 처음 보는 값과 이미 본 값을 어떻게 구분하는가?
     *
     * TODO(15): 구현하라.
     */
    public static void countFrequencies(int[] values, Map<Integer, Integer> counts) {
        throw new UnsupportedOperationException("TODO(15): countFrequencies");
    }

    /**
     * 문제 2. 두 수의 합 (이 문제집의 함정)
     *
     * 더해서 target 이 되는 서로 다른 두 인덱스를 찾아 `[작은인덱스, 큰인덱스]` 로 반환한다.
     * 없으면 빈 배열. 답이 여러 개면 두 번째 인덱스가 가장 작은 것을 반환한다.
     *
     *   [2, 7, 11, 15], target=9  ->  [0, 1]
     *   [3, 3], target=6          ->  [0, 1]
     *   [1, 2], target=99         ->  []
     *
     * 함정
     *   모든 쌍을 다 보면 O(n^2) 이다. 테스트에 20만 개짜리 케이스와 시간 제한이 있다.
     *
     * 생각할 것
     *   - 지금 값이 v 라면 짝은 무엇인가? 그 짝을 이미 봤는지 물을 수 있으면 한 번만 훑어도 된다.
     *   - seen 에 무엇을 키로, 무엇을 값으로 담아야 하는가?
     *   - 같은 값이 두 번 나오는 경우([3,3])를 어떻게 다루는가?
     *
     * TODO(16): 구현하라. O(n) 이어야 한다.
     */
    public static int[] twoSum(int[] values, int target, Map<Integer, Integer> seen) {
        throw new UnsupportedOperationException("TODO(16): twoSum");
    }

    /**
     * 문제 3. 처음으로 한 번만 나온 문자의 인덱스
     *
     * 문자열 전체에서 딱 한 번만 나오는 문자 중 가장 앞의 것의 인덱스. 없으면 -1.
     *
     *   "leetcode"  ->  0   (l)
     *   "aabb"      -> -1
     *   "abac"      ->  1   (b)
     *
     * 04번에서는 같은 문제를 큐로 풀었다. 거기서는 "스트림을 흘려보내며 매 시점의 답"이 필요했고
     * 여기서는 "전체를 다 보고 난 뒤의 답"이 필요하다.
     * 무엇이 필요한지가 자료구조를 정한다.
     *
     * TODO(17): 구현하라. 두 번 훑어도 O(n) 이다.
     */
    public static int firstUniqueChar(String input, Map<Character, Integer> counts) {
        throw new UnsupportedOperationException("TODO(17): firstUniqueChar");
    }
}
