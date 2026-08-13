package com.datastructure.bst;

/**
 * 이진 탐색 트리로 푸는 문제들.
 *
 * 전부 SortedMap 인터페이스만 받는다. 12번 스킵 리스트, 15번 B-트리, 16번 레드블랙 트리도
 * 같은 계약을 구현하므로, 여기서 짠 코드가 그대로 돌아간다.
 *
 * **정렬된 구조가 필요한 상황에는 공통점이 있다. "가장 가까운 것"을 물어야 할 때다.**
 * 정확히 일치하는 것만 찾으면 해시맵이 낫다.
 */
public final class BSTProblems {

    private BSTProblems() {
    }

    /**
     * 문제 1. 가장 가까운 키
     *
     * target 과 차이가 가장 작은 키를 반환한다. 비었으면 NoSuchElementException.
     * 양쪽 차이가 같으면 **작은 쪽**을 반환한다.
     *
     *   {1, 5, 9}, target=6  ->  5
     *   {1, 5, 9}, target=7  ->  5      (차이 2 대 2 이므로 작은 쪽)
     *   {1, 5, 9}, target=0  ->  1
     *
     * 생각할 것
     *   - 전부 훑어서 최소 차이를 찾으면 O(n) 이다. floorKey 와 ceilingKey 를 쓰면?
     *   - 둘 중 하나가 없는 경우를 잊지 마라.
     *
     * TODO(10): 구현하라. O(log n) 이어야 한다.
     */
    public static Integer closestKey(SortedMap<Integer, ?> map, int target) {
        throw new UnsupportedOperationException("TODO(10): closestKey");
    }

    /**
     * 문제 2. 구간 합
     *
     * from 이상 to 이하인 키들의 **값**을 더한다. 값은 정수다.
     *
     *   {1=10, 5=50, 9=90}, from=1, to=5  ->  60
     *
     * 생각할 것
     *   - keysInRange 를 쓰면 볼 필요 없는 가지를 건너뛴다.
     *   - 답이 k 개면 전체 비용이 얼마인가?
     *
     * TODO(11): 구현하라.
     */
    public static long rangeSum(SortedMap<Integer, Integer> map, int from, int to) {
        throw new UnsupportedOperationException("TODO(11): rangeSum");
    }

    /**
     * 문제 3. k 번째로 작은 키 (1부터 센다)
     *
     *   {1, 5, 9}, k=2  ->  5
     *
     * k 가 범위를 벗어나면 IndexOutOfBoundsException.
     *
     * 생각할 것
     *   - 정렬 순회를 하다가 k 번째에서 멈추면 된다. 전부 다 순회할 필요가 있는가?
     *   - (참고: 노드마다 "내 아래에 몇 개 있는지"를 들고 있으면 O(log n) 이 된다.
     *     그건 17번 펜윅 트리에서 다시 만난다. 여기서는 순회로 충분하다.)
     *
     * TODO(12): 구현하라.
     */
    public static Integer kthSmallest(SortedMap<Integer, ?> map, int k) {
        throw new UnsupportedOperationException("TODO(12): kthSmallest");
    }
}
