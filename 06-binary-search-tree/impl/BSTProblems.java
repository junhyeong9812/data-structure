package com.datastructure.bst;

import java.util.NoSuchElementException;

/**
 * [구현] 이진 탐색 트리 응용 문제.
 *
 * 셋 다 "가장 가까운 것"을 묻는다. 정확히 일치하는 것만 찾을 거면 해시맵이 낫다.
 */
public final class BSTProblems {

    private BSTProblems() {
    }

    /**
     * floor 와 ceiling 을 각각 한 번씩만 부른다. 전부 훑을 필요가 없다.
     *
     * 둘 다 없으면 트리가 비어 있다는 뜻이다.
     * 차이가 같을 때 작은 쪽을 고르므로 비교가 `<=` 다.
     * 뺄셈은 long 으로 한다. target 이 Integer.MIN_VALUE 근처면 int 로는 넘친다.
     */
    public static Integer closestKey(SortedMap<Integer, ?> map, int target) {
        Integer lower = map.floorKey(target);
        Integer upper = map.ceilingKey(target);

        if (lower == null && upper == null) {
            throw new NoSuchElementException("비어 있다");
        }
        if (lower == null) return upper;
        if (upper == null) return lower;

        long toLower = (long) target - lower;
        long toUpper = (long) upper - target;
        return toLower <= toUpper ? lower : upper;
    }

    /** keysInRange 가 가지치기를 하므로 범위 밖은 아예 보지 않는다. */
    public static long rangeSum(SortedMap<Integer, Integer> map, int from, int to) {
        long sum = 0;
        for (int key : map.keysInRange(from, to)) {
            Integer value = map.get(key);
            if (value != null) sum += value;
        }
        return sum;
    }

    /**
     * 정렬 순회를 하다가 k 번째에서 멈춘다.
     *
     * 노드마다 "내 아래에 몇 개 있는지"를 들고 있으면 O(log n) 으로 줄일 수 있다.
     * 그 아이디어는 17번 펜윅 트리에서 본격적으로 다룬다.
     */
    public static Integer kthSmallest(SortedMap<Integer, ?> map, int k) {
        if (k < 1 || k > map.size()) {
            throw new IndexOutOfBoundsException("k=" + k + ", 크기=" + map.size());
        }
        int seen = 0;
        for (Integer key : map.keys()) {
            if (++seen == k) return key;
        }
        throw new IndexOutOfBoundsException("k=" + k);
    }
}
