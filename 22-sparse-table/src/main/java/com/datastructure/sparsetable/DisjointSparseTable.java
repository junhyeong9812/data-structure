package com.datastructure.sparsetable;

import java.util.Arrays;
import java.util.function.LongBinaryOperator;

/**
 * 겹치지 않게 덮는 희소 테이블. 그래서 합도 곱도 된다. 조회는 여전히 O(1) 이다.
 *
 * SparseTable 이 멱등성을 요구한 이유는 하나였다. 두 창이 겹쳐서.
 * 겹치지 않게 덮을 수 있으면 그 조건이 통째로 사라진다. 13번과 같은 모노이드면 된다.
 *
 * <h2>어떻게 겹치지 않게 덮는가</h2>
 *
 * 층 k 에서 배열을 길이 2^(k+1) 짜리 블록으로 자르고, 각 블록의 한가운데를 기준으로
 * 왼쪽은 오른쪽에서 왼쪽으로, 오른쪽은 왼쪽에서 오른쪽으로 누적해 둔다.
 *
 *   블록 [center - range, center + range - 1], range = 2^k
 *
 *   table[k][i] = i 부터 center-1 까지의 답      (i < center 일 때)
 *   table[k][i] = center 부터 i 까지의 답        (i >= center 일 때)
 *
 * 그러면 가운데를 사이에 끼고 있는 구간 [l, r] 은
 *
 *   combine(table[k][l], table[k][r]) = ([l..center-1]) + ([center..r])
 *
 * 로 한 번에 나온다. 두 조각이 center 에서 딱 맞물리고 겹치지 않는다.
 *
 * <h2>어느 층을 쓰는가</h2>
 *
 * l 과 r 이 처음으로 갈라지는 비트, 즉 `l ^ r` 의 최상위 1비트 자리다.
 * 그 자리에서 l 은 0, r 은 1 이므로 둘 사이에 그 층의 블록 경계(center)가 정확히 하나 있다.
 *
 * l == r 이면 갈라지는 비트가 없다. 따로 처리해야 한다. 여기가 제일 틀리기 쉽다.
 *
 * <h2>대가</h2>
 *
 * 층마다 배열 전체를 채우므로 칸 수가 n log n 이고, 게다가 2의 거듭제곱으로 올림한 폭을
 * 쓴다. 늘린 칸은 항등원으로 메운다. 갱신이 안 되는 것은 SparseTable 과 똑같다.
 */
public class DisjointSparseTable implements StaticRangeQuery {

    private final int n;
    private final int width;
    private final int levels;
    private final long[][] table;
    private final long[] values;
    private final long identity;
    private final LongBinaryOperator combine;

    /** 합. 희소 테이블로는 못 하던 것이다. */
    public DisjointSparseTable(long[] initial) {
        this(initial, 0L, Long::sum);
    }

    /**
     * 결합 함수를 인자로 받는다. 13번 GenericSegmentTree 와 같은 방식이다.
     * combine 은 결합법칙을 만족해야 하고 identity 는 그 항등원이어야 한다.
     * 멱등성은 필요 없다.
     */
    public DisjointSparseTable(long[] initial, long identity, LongBinaryOperator combine) {
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        if (combine == null) {
            throw new IllegalArgumentException("결합 함수가 필요하다");
        }
        this.n = initial.length;
        this.values = initial.clone();
        this.identity = identity;
        this.combine = combine;

        int w = 1;
        int lv = 0;
        while (w < n) {
            w <<= 1;
            lv++;
        }
        this.width = w;
        this.levels = lv;
        this.table = new long[levels][width];
        build();
    }

    private void build() {
        long[] padded = new long[width];
        Arrays.fill(padded, identity);
        System.arraycopy(values, 0, padded, 0, n);

        // TODO 1: 층마다 블록의 가운데를 기준으로 좌우로 누적한다.
        //
        //   층 level 의 폭 range = 2^level, 블록 길이 = 2 * range.
        //   블록의 가운데는 center = range, 3*range, 5*range, ... 다.
        //   (center 를 range 부터 시작해 2*range 씩 늘리면 된다)
        //
        //   각 center 에 대해
        //     왼쪽: center-1 에서 center-range 까지 **오른쪽에서 왼쪽으로**
        //           row[center-1] = padded[center-1]
        //           row[i] = combine(padded[i], row[i+1])
        //     오른쪽: center 에서 center+range-1 까지 **왼쪽에서 오른쪽으로**
        //           row[center] = padded[center]
        //           row[i] = combine(row[i-1], padded[i])
        //
        // **누적 방향과 combine 의 인자 순서가 둘 다 중요하다.**
        // 왼쪽은 padded[i] 가 앞, 오른쪽은 row[i-1] 이 앞이다.
        // 순서를 뒤집으면 교환법칙이 있는 연산(합, 최소)에서는 우연히 맞고
        // 없는 연산에서 틀린다. **우연히 맞는 코드가 제일 위험하다**(13번의 평균과 같다).
        //
        // padded 를 쓰는 이유: 배열을 2의 거듭제곱으로 늘려두면 블록이 항상 꽉 차서
        // 경계 검사를 안 해도 된다. 늘린 자리는 항등원으로 채운다.
        //
        // 다만 정직하게 적어둔다. **그 채우기는 우리 테스트가 못 잡는다.**
        // 조회가 인덱스를 검사해서 패딩 칸을 읽는 경로가 아예 없기 때문이다.
        // 0 으로 남겨도 90개가 다 통과한다. 그래도 채운다(README 의 한계 항목을 보라).
        throw new UnsupportedOperationException("TODO 1: build");
    }

    @Override
    public long query(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return identity;
        }
        // TODO 2: 두 조각을 합친다. **겹치지 않으므로 combine 이 무엇이든 된다.**
        //
        //   1. from == to 면? **갈라지는 비트가 없다.** from ^ to == 0 이고
        //      31 - Integer.numberOfLeadingZeros(0) 은 -1 이라 배열 인덱스로 쓰면 터진다.
        //      값을 그대로 돌려주면 된다. **여기를 빼먹는 것이 이 구조 최대의 함정이다.**
        //   2. 아니면 갈라지는 자리를 찾는다. level = 31 - Integer.numberOfLeadingZeros(from ^ to)
        //   3. combine(table[level][from], table[level][to])
        //
        // 3번의 순서를 뒤집지 마라. 왼쪽 조각이 앞이다.
        throw new UnsupportedOperationException("TODO 2: query");
    }

    @Override
    public long get(int index) {
        requireIndex(index);
        return values[index];
    }

    @Override
    public int size() {
        return n;
    }

    /** 층 수. n=1 이면 0 이다. */
    public int levels() {
        return levels;
    }

    /** 저장한 칸 수. 2의 거듭제곱으로 올림한 폭 x 층 수다. */
    public int unitCount() {
        return levels * width;
    }

    long node(int level, int index) {
        return table[level][index];
    }

    private void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
