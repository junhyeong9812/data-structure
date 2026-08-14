package com.datastructure.fenwick;

/**
 * 펜윅 트리(= 이진 인덱스 트리, Binary Indexed Tree). 같은 자료구조의 두 이름이다.
 *
 * 트리라고 부르지만 배열 하나뿐이다. 노드도 링크도 없다.
 * 인덱스의 비트 패턴이 트리 구조를 대신한다.
 *
 * 규칙은 하나다. tree[i] 는 i 에서 왼쪽으로 (i & -i) 개를 덮는다.
 *
 *   i    i&-i   덮는 구간
 *   1     1     [1..1]
 *   2     2     [1..2]
 *   3     1     [3..3]
 *   4     4     [1..4]
 *   6     2     [5..6]
 *   8     8     [1..8]
 *
 * `i & -i` 는 최하위 1비트다. 2의 보수라 -i 는 i 의 비트를 뒤집고 1을 더한 것이라,
 * and 를 하면 맨 아래 1비트만 남는다. (6 = 110, -6 = ...010 -> 6 & -6 = 2)
 *
 * 그래서 누적합은 이렇게 된다. 7까지의 합이 필요하면 7 = 4 + 2 + 1 로 쪼개
 *   tree[7] ([7..7]) + tree[6] ([5..6]) + tree[4] ([1..4])
 * 세 칸만 더하면 된다. 걸음 수가 곧 1비트의 개수라 log n 이하다.
 *
 * 갱신은 반대로 올라간다. i 를 덮는 칸들을 찾아 다니는데, 그게 i += i & -i 다.
 *
 * 안쪽에서 1부터 세는 이유가 이것이다. 0 & -0 은 0 이라 루프가 안 끝난다.
 * 바깥 API 는 0부터 쓰고 안에서 +1 한다.
 */
public class FenwickTree implements PrefixSumTree {

    private final int n;
    private final long[] tree;

    public FenwickTree(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("크기는 1 이상이어야 한다: " + size);
        }
        this.n = size;
        this.tree = new long[size + 1];
    }

    public FenwickTree(long[] initial) {
        this(initial == null || initial.length == 0 ? 1 : initial.length);
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        buildFrom(initial);
    }

    /** 하나씩 add 하면 O(n log n) 이다. 한 번 훑어 O(n) 으로 만든다. */
    private void buildFrom(long[] values) {
        // TODO 1: 1부터 n 까지 한 번 훑으며 자기 값을 넣고 **부모에게 넘긴다.**
        //
        //   tree[i] 에 values[i-1] 을 더한다.
        //   i 를 덮는 다음 칸은 i + (i & -i) 다. 범위 안이면 tree[i] 를 통째로 넘긴다.
        //
        // 왜 되는가. i 를 처리할 때 tree[i] 는 이미 완성돼 있다.
        // 자기 구간의 부분합을 다 받았기 때문이다. 그래서 그대로 부모에게 넘기면 된다.
        // **각 칸이 정확히 한 번씩만 위로 흘러가므로 O(n) 이다.**
        throw new UnsupportedOperationException("TODO 1: buildFrom");
    }

    @Override
    public void add(int index, long delta) {
        requireIndex(index);
        // TODO 2: 이 인덱스를 덮는 칸을 전부 찾아 delta 를 더한다.
        //
        //   1-based 로 바꾸고(+1), **x += x & -x** 로 올라간다. n 을 넘으면 끝.
        //
        // 최하위 1비트를 **더하면** 그 비트가 올라가면서 더 큰 구간을 덮는 칸으로 간다.
        // 1 -> 2 -> 4 -> 8 ... 이라 걸음이 log n 이하다.
        throw new UnsupportedOperationException("TODO 2: add");
    }

    @Override
    public long prefixSum(int index) {
        if (index < 0) {
            return 0L;
        }
        requireIndex(index);
        // TODO 3: 0 번부터 index 번까지의 합.
        //
        //   1-based 로 바꾸고, **x -= x & -x** 로 내려가며 tree[x] 를 더한다. 0 이 되면 끝.
        //
        // add 와 정확히 반대 방향이다. 더하면 올라가고 빼면 내려간다.
        // 빼면 최하위 1비트가 하나씩 사라지므로 **1비트 개수만큼** 걷는다.
        //
        // index 가 -1 이면 "빈 접두"라 0 이다. rangeSum 이 그 경우에 기댄다.
        throw new UnsupportedOperationException("TODO 3: prefixSum");
    }

    @Override
    public long rangeSum(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return 0L;
        }
        // TODO 4: 한 줄이다. **그리고 이 한 줄이 이 자료구조의 한계를 정한다.**
        //
        // 앞쪽을 통째로 구해놓고 **빼서** 가운데를 얻는다.
        // 최소값에는 이 뺄셈이 없다. 그래서 펜윅 트리로는 구간 최소를 못 한다.
        // 13번 세그먼트 트리가 여전히 필요한 이유다.
        throw new UnsupportedOperationException("TODO 4: rangeSum");
    }

    @Override
    public long get(int index) {
        return rangeSum(index, index);
    }

    @Override
    public void set(int index, long value) {
        // TODO 5: 펜윅 트리는 "대입"을 직접 못 한다. **차이를 더하는 것만 한다.**
        //
        // 지금 값을 읽어 차이를 구하고 그걸 더하면 된다.
        // 이런 구조라 값을 바꾸는 비용이 두 배(읽기 + 쓰기)다. 그것도 대가다.
        throw new UnsupportedOperationException("TODO 5: set");
    }

    /**
     * 누적합이 target 이상이 되는 가장 작은 인덱스. 없으면 -1.
     * 모든 값이 0 이상일 때만 뜻이 있다.
     */
    /**
     * 누적합이 target 이상이 되는 가장 작은 인덱스. 없으면 -1.
     * 모든 값이 0 이상일 때만 뜻이 있다.
     */
    public int findPrefixIndex(long target) {
        // TODO 6: 이진 탐색을 **트리 위에서 직접** 한다.
        //
        // 큰 2의 거듭제곱부터 내려오며 "여기까지 가도 아직 모자라나"를 묻는다.
        //   pos 에서 pw 만큼 더 갈 수 있고, 그 칸의 합이 남은 목표보다 작으면 간다.
        //   가면 그만큼을 목표에서 뺀다.
        //
        // prefixSum 을 부르며 이진 탐색하면 O(log^2 n) 이다. 이건 **O(log n)** 이다.
        // 트리의 칸 구조가 이미 이진 탐색의 모양이라 그걸 그대로 탄다.
        //
        // 쓰임새: 빈도표를 들고 "정렬했을 때 k 번째는 무엇인가"를 O(log n) 에 답한다.
        // 다 끝났을 때 pos 가 n 이면 그런 자리가 없다는 뜻이다.
        throw new UnsupportedOperationException("TODO 6: findPrefixIndex");
    }

    @Override
    public int size() {
        return n;
    }

    int treeSize() {
        return tree.length;
    }

    long node(int i) {
        return tree[i];
    }

    private void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
