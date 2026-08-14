package com.datastructure.sparsetable;

/**
 * 희소 테이블로 푸는 문제 둘.
 */
public final class SparseTableProblems {

    private SparseTableProblems() {
    }

    /**
     * 문제 1: 크기 k 인 창을 왼쪽부터 한 칸씩 옮기며 각 창의 최솟값을 모은다.
     * 결과 길이는 n - k + 1 이다.
     *
     * 04번에서 같은 문제를 덱으로 풀었다. 그쪽이 O(n) 이라 더 빠르다.
     * 그런데 덱 풀이는 창이 한 방향으로만 움직이고 k 가 고정이라는 조건에 기대고 있다.
     * k 가 바뀌면 처음부터 다시 훑어야 한다.
     *
     * 희소 테이블은 전처리 O(n log n) 을 먼저 내고 그 뒤로는 아무 구간이나 O(1) 이다.
     * 창 크기가 여럿이거나 구간이 제멋대로면 이쪽이 이긴다.
     * 같은 문제라도 질의 패턴이 자료구조를 정한다.
     */
    public static int[] slidingWindowMin(int[] values, int k) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        if (k <= 0 || k > values.length) {
            throw new IllegalArgumentException("창 크기가 범위를 벗어났다: " + k + " (원소 " + values.length + ")");
        }
        // TODO 1: MinSparseTable 을 한 번 짓고 창마다 query 를 한 번씩 부른다.
        //
        //   i 번째 창은 [i, i + k - 1] 이다. **여기서도 -1 이 함정이다.**
        //   i 가 갈 수 있는 마지막 자리는 n - k 이므로 결과 길이가 n - k + 1 이다.
        //
        // int[] 을 long[] 으로 옮겨 담아야 한다. 답도 int 범위 안이므로 캐스팅해서 담는다.
        throw new UnsupportedOperationException("TODO 1: slidingWindowMin");
    }

    /**
     * 문제 2: 질의가 아주 많을 때의 구간 gcd.
     * queries[i] = {from, to} 이고 결과의 i번째가 그 구간의 gcd 다.
     *
     * 여기가 이 자료구조를 고르는 이유가 드러나는 자리다.
     * 세그먼트 트리로 하면 질의마다 O(log n) 이라 전체가 O(q log n) 이다.
     * 희소 테이블은 O(n log n + q) 다. q 가 n 보다 훨씬 크면 그 차이가 그대로 남는다.
     * (SparseTableProblemsTest 가 걸음 수를 세어 보여준다. n=4096, q=2000 에서 18배다)
     *
     * 값이 바뀌지 않는다는 전제가 깔려 있다. 하나라도 바뀌면 전부 다시 지어야 한다.
     */
    public static long[] rangeGcdQueries(int[] values, int[][] queries) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        if (queries == null) {
            throw new IllegalArgumentException("질의 배열이 필요하다");
        }
        // TODO 2: GcdSparseTable 을 **한 번만** 짓고 질의를 하나씩 답한다.
        //
        //   질의마다 새로 지으면 O(q n log n) 이다. 20만 질의에서 그대로 멈춘다.
        //   **전처리를 루프 밖으로 빼는 것이 이 문제의 전부다.**
        //
        // 질의 검증도 해야 한다. 다음이면 IllegalArgumentException 이다.
        //   - q 가 null 이거나 길이가 2 가 아니다
        //   - q[0] < 0, q[1] >= n, q[0] > q[1]
        //
        // 검증을 빼먹으면 잘못된 질의가 IndexOutOfBoundsException 으로 새어 나가거나
        // (from > to 인 경우) 항등원 0 을 조용히 답으로 돌려준다. **후자가 더 나쁘다.**
        throw new UnsupportedOperationException("TODO 2: rangeGcdQueries");
    }
}
