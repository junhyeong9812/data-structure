package com.datastructure.sparsetable;

/**
 * 희소 테이블의 뼈대. 무엇으로 접을지만 자식이 정한다. 13번 SegmentTree 와 같은 모양이다.
 *
 * 다른 것은 하나다. 갱신이 없다. 그 대신 조회가 O(1) 이다.
 *
 * <h2>어떻게 O(1) 이 되는가</h2>
 *
 * 길이가 2의 거듭제곱인 구간의 답을 전부 미리 계산해 둔다.
 *
 *   table[k][i] = [i, i + 2^k - 1] 구간의 답        <- 길이 2^k 짜리 창
 *
 * 그러면 아무 구간 [l, r] 이나 딱 두 조각으로 덮인다.
 *
 *   len = r - l + 1, k = floor(log2(len)) 일 때
 *   왼쪽 창  [l, l + 2^k - 1]
 *   오른쪽 창 [r - 2^k + 1, r]
 *
 *   l ...................... r
 *   [---- 2^k ----]
 *          [---- 2^k ----]
 *              ^^^^ 여기가 겹친다
 *
 * 13번 세그먼트 트리는 구간을 O(log n) 개 조각으로 덮었다. 여기는 언제나 두 조각이다.
 * 그래서 combine 을 딱 한 번 부르고 끝난다.
 *
 * <h2>대가</h2>
 *
 * 1. 겹친다. 그래서 combine 이 멱등(f(x, x) = x)해야 한다.
 *    min, max, gcd, 비트 AND/OR 은 된다. 합과 곱은 안 된다. 겹친 부분을 두 번 센다.
 *    (IdempotenceTest 가 합으로 만들면 어떤 답이 나오는지 숫자로 보여준다)
 * 2. 메모리가 n log n 이다. 13번의 4n 보다 크다. n=16 부터 역전된다.
 * 3. 갱신이 불가능하다. 값 하나가 바뀌면 그 값을 포함하는 칸이 층마다 있어서
 *    사실상 전체를 다시 지어야 한다.
 *
 * | | 조회 | 갱신 | 전처리 | 메모리 | 조건 |
 * |---|---|---|---|---|---|
 * | 세그먼트 트리(13번) | O(log n) | O(log n) | O(n) | 4n | 결합법칙 |
 * | 펜윅 트리(17번) | O(log n) | O(log n) | O(n) | n+1 | 역연산 |
 * | 희소 테이블 | O(1) | 없다 | O(n log n) | n log n | 멱등성 |
 */
public abstract class SparseTable implements StaticRangeQuery {

    protected final int n;
    protected final int levels;
    protected final long[][] table;
    protected final int[] log;
    private final long[] values;

    protected SparseTable(long[] initial) {
        if (initial == null || initial.length == 0) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다");
        }
        this.n = initial.length;
        this.values = initial.clone();
        this.log = buildLogTable(n);
        this.levels = log[n] + 1;
        this.table = new long[levels][n];
        build();
    }

    protected abstract long combine(long a, long b);

    protected abstract long identity();

    /**
     * log[len] = floor(log2(len)) 을 1..n 까지 미리 채운 배열. log[0] 은 쓰지 않는다.
     */
    static int[] buildLogTable(int n) {
        // TODO 1: 길이 n+1 짜리 배열에 floor(log2(i)) 를 채운다.
        //
        //   log[1] = 0 이고, 그 뒤로는 **바로 앞 절반의 답에 1 을 더한 것**이다.
        //   log[i] = log[i / 2] + 1.  i / 2 는 i >> 1 로 쓴다.
        //
        // **Math.log 를 조회할 때마다 부르지 마라.** 두 가지 이유가 있다.
        //   - 비싸다. 같은 기계에서 1천만 번 재보면 비트 연산의 16배가 걸렸다.
        //     조회 한 번에 log 를 부르면 "O(1)" 이라는 말이 무색해진다.
        //   - 명세가 1 ulp 오차를 허용한다. 우리 JDK 에서 20만까지는 안 틀렸지만
        //     (SparseTableStructureTest 가 그걸 확인한다) 기대서는 안 되는 성질이다.
        //
        // 테이블 없이 즉석에서 구해야 하면 `31 - Integer.numberOfLeadingZeros(len)` 을 써라.
        // 정수 연산 한 번이고 오차가 없다.
        throw new UnsupportedOperationException("TODO 1: buildLogTable");
    }

    private void build() {
        // TODO 2: table[k][i] = [i, i + 2^k - 1] 구간의 답을 채운다.
        //
        //   0층은 원본 그대로다.            table[0][i] = values[i]
        //   k층은 **k-1층 두 개를 이어붙인 것**이다.
        //
        //     table[k][i] = combine(table[k-1][i], table[k-1][i + 2^(k-1)])
        //                          [i .. i+2^(k-1)-1]  [i+2^(k-1) .. i+2^k-1]
        //
        //   앞 절반과 뒤 절반이다. **여기는 겹치지 않는다.** 겹치는 것은 조회 쪽이다.
        //
        // 안쪽 루프의 범위가 함정이다. 길이 2^k 짜리 창이 배열 안에 들어와야 하므로
        // **i + 2^k <= n** 인 i 까지만 채운다. 조건을 i < n 으로 쓰면 배열 끝을 넘거나
        // (혹은 넘지 않더라도) 없는 구간의 답을 채워 넣게 된다.
        //
        // 층을 바깥, 인덱스를 안쪽에 두어라. 순서를 뒤집으면 k-1층이 아직 안 채워져 있다.
        throw new UnsupportedOperationException("TODO 2: build");
    }

    @Override
    public long query(int from, int to) {
        requireIndex(from);
        requireIndex(to);
        if (from > to) {
            return identity();
        }
        // TODO 3: 구간을 **두 조각으로 덮어** combine 한 번으로 답한다.
        //
        //   1. 길이를 구한다.            len = to - from + 1
        //   2. 그 길이에 맞는 층을 고른다. k = log[len]      <- 미리 채워둔 테이블
        //   3. 두 창을 합친다.
        //        왼쪽 창의 시작 = from
        //        오른쪽 창의 시작 = to - 2^k + 1
        //
        // **+1 을 빠뜨리는 것이 여기서 제일 흔한 실수다.** 오른쪽 창은 to 에서 끝나야 하므로
        // 시작점이 to - 2^k + 1 이다. 이 하나가 틀리면 길이가 2의 거듭제곱인 구간에서만
        // 우연히 맞고 나머지가 전부 한 칸씩 밀린다. (13번 lazy 의 hi - lo + 1 과 같은 함정이다)
        //
        // 두 창이 겹쳐도 되는 이유를 다시 보라. **combine 이 멱등이기 때문이다.**
        // 여기에 합을 넣으면 겹친 부분을 두 번 센다. 컴파일도 되고 예외도 안 나고 조용히 틀린다.
        throw new UnsupportedOperationException("TODO 3: query");
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

    /** 층 수. floor(log2 n) + 1 이다. */
    public int levels() {
        return levels;
    }

    /** 저장한 칸 수. 13번의 4n 과 비교해보라는 뜻으로 열어둔 접근자다. */
    public int unitCount() {
        return levels * n;
    }

    long node(int level, int index) {
        return table[level][index];
    }

    int logOf(int length) {
        return log[length];
    }

    protected void requireIndex(int index) {
        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException("인덱스 " + index + " 가 범위를 벗어났다 (크기 " + n + ")");
        }
    }
}
