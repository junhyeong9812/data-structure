package com.datastructure.sketch;

/**
 * 계수기 d x w 개로 만든 빈도 스케치.
 *
 * 원소를 담지 않는다. 원소가 더해야 할 칸 자리만 기억한다. 11번과 같은 발상이다.
 *
 * 더하기: 행마다 다른 해시로 칸 하나씩, 전부 d 개의 칸에 count 를 더한다.
 * 묻기:   그 d 개 칸을 보고 가장 작은 값을 답한다.
 *
 * 왜 최소인가. 칸은 남의 계수까지 같이 받아 부풀어 있다. 절대 깎이지는 않는다.
 * 그러니 모든 칸이 실제 이상이고, 그중 가장 덜 부푼 칸이 가장 실제에 가깝다.
 * 여기서 계약이 나온다.
 *
 *   추정치 >= 실제                          예외 없음
 *   추정치 <= 실제 + epsilon x 전체개수     확률 (1 - delta) 로
 *
 * 크기는 감으로 정하지 않는다. 오차 목표가 크기를 정한다.
 *
 *   칸 수  w = ceil(e / epsilon)      e 는 자연상수 2.718
 *   행 수  d = ceil(ln(1 / delta))
 *
 * epsilon 0.1% 에 delta 1% 면 2719 x 5 = 13595칸, long 으로 108KB 다.
 * 원소가 100만 종류든 1억 종류든 108KB 다. 이것이 이기는 지점이다.
 *
 * 오차가 전체 개수에 비례한다는 점을 놓치면 안 된다.
 * 전체 100만 개에 epsilon 0.1% 면 오차가 1000 이다.
 * 빈도 10만짜리 원소에는 1% 지만 빈도 3짜리 원소에는 통째로 잡음이다.
 */
public class CountMinSketch implements FrequencyEstimator {

    /** 이보다 넓으면 배열 하나가 수백 MB 다. 설정 실수로 본다. */
    static final int MAX_WIDTH = 1 << 22;

    /** 행 64개면 delta 가 1e-28 이다. 이보다 낮출 이유가 없다. */
    static final int MAX_DEPTH = 64;

    private final int width;
    private final int depth;
    private final long seed;
    private final long[][] table;
    private long total;

    /** 오차율 epsilon 과 실패 확률 delta 로 크기를 정한다. seed 는 0 이라 결과가 재현된다. */
    public CountMinSketch(double epsilon, double delta) {
        this(widthFor(epsilon), depthFor(delta), 0L);
    }

    /**
     * 크기와 seed 를 직접 준다. 테스트가 최악의 스케치(칸 2개)를 만들 때 쓴다.
     *
     * seed 를 주입받는 이유는 12번 스킵 리스트와 같다. 무작위가 결과를 바꾸는 자료구조는
     * 무작위를 밖에서 넣을 수 있어야 테스트가 결정적이 된다.
     */
    public CountMinSketch(int width, int depth, long seed) {
        if (width < 1) {
            throw new IllegalArgumentException("칸 수는 1 이상이어야 한다: " + width);
        }
        if (depth < 1) {
            throw new IllegalArgumentException("행 수는 1 이상이어야 한다: " + depth);
        }
        this.width = width;
        this.depth = depth;
        this.seed = seed;
        this.table = new long[depth][width];
    }

    /** 오차율 epsilon 을 지키려면 행 하나에 칸이 몇 개 필요한가. */
    static int widthFor(double epsilon) {
        // TODO 1: w = ceil(e / epsilon). Math.E 가 자연상수다.
        //
        // epsilon 이 0 과 1 사이가 아니면 IllegalArgumentException.
        //
        // **올림해야 한다.** 내리면 칸이 모자라 오차가 목표를 넘는다.
        // 그리고 결과가 MAX_WIDTH 를 넘으면 거부하라. epsilon 이 1e-9 면 칸이 27억 개인데
        // int 로 자르면 **음수 배열 크기**가 되어 엉뚱한 예외가 난다.
        // (11번 optimalBits 와 같은 모양의 공식이다. 거기서는 -n ln(p) / (ln2)^2 였다)
        throw new UnsupportedOperationException("TODO 1: widthFor");
    }

    /** 실패 확률 delta 를 지키려면 행이 몇 개 필요한가. */
    static int depthFor(double delta) {
        // TODO 2: d = ceil(ln(1 / delta)). Math.log 가 자연로그다.
        //
        // delta 가 0 과 1 사이가 아니면, 결과가 MAX_DEPTH 를 넘으면 IllegalArgumentException.
        //
        // **행을 늘려도 오차 크기는 안 줄어든다.** 줄어드는 것은 "오차 한계를 넘을 확률"이다.
        // 행 하나가 운 나쁘게 크게 부풀 확률이 1/e 이고, 최소를 취하니 d 행이 전부 부풀어야
        // 한계를 넘는다. 그래서 e^-d 다. 뒤집으면 위 식이다.
        throw new UnsupportedOperationException("TODO 2: depthFor");
    }

    static long mix64(long z) {
        z += 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    /** 이 원소가 행마다 건드릴 칸 번호. 길이가 depth 다. */
    int[] indexes(int item) {
        // TODO 3: 11번 블룸 필터와 **똑같은 이중 해싱**이다.
        //
        //   h = mix64(item ^ seed) 로 64비트를 만들고 반으로 가른다
        //   h1 = 아래 32비트, h2 = 위 32비트
        //   자리_i = floorMod(h1 + i * h2, width)   (i = 0..depth-1)
        //
        // 함정도 그대로 셋이다.
        //   - h1 + i*h2 는 int 를 넘어 **음수가 된다.** Math.floorMod 를 써라
        //   - h2 가 0 이면 모든 행이 같은 칸을 본다. 행이 하나인 것과 같아진다. 0 이면 1 로
        //   - item 을 그대로 쓰면 연속된 수가 연속된 칸으로 줄을 선다. mix64 로 섞고 시작하라
        //
        // 두 번째 방어선을 11번에서는 **어떤 테스트로도 못 잡았다.** 입력이 int 42억 가지라
        // 상위 32비트가 0 이 되는 값이 하나도 없었기 때문이다.
        // 여기서는 seed 를 섞어 mix64 의 입력이 64비트가 되므로 그런 짝을 만들 수 있고,
        // h2ZeroCollapsesRows 테스트가 실제로 그 짝으로 찌른다.
        throw new UnsupportedOperationException("TODO 3: indexes");
    }

    @Override
    public void add(int item) {
        add(item, 1);
    }

    @Override
    public void add(int item, long count) {
        // TODO 4: 행마다 자기 칸에 count 를 더한다. total 도 갱신한다.
        //
        // count 검사는 ExactCounter 와 같다. **음수를 허용하면 계약이 즉시 깨진다.**
        // A 와 B 가 같은 칸을 공유하는데 A 를 빼면 B 의 추정치가 실제보다 작아진다.
        // 그러면 "절대 과소평가하지 않는다"가 사라지고, 그것이 이 자료구조의 전부다.
        //
        // 행 r 이 볼 칸은 indexes(item)[r] 이다. **행마다 다른 칸이라는 점**이 핵심이다.
        // 전부 같은 칸에 더하면 행을 여러 개 둔 의미가 없다.
        throw new UnsupportedOperationException("TODO 4: add");
    }

    @Override
    public long estimateCount(int item) {
        // TODO 5: 행별 칸 값 중 **가장 작은 것**을 답한다.
        //
        // 최대나 합, 평균이 아니다. 왜 최소여야 하는지 한 문장으로 말할 수 있어야 한다.
        // 모든 칸이 실제 이상이므로 **최소도 실제 이상**이고, 가장 덜 부푼 값이다.
        //
        // 평균을 쓰면 어떻게 되는가. 여전히 실제보다 크지만 더 크다. 계약은 안 깨지고
        // 정확도만 나빠진다. 최대를 쓰면 훨씬 더 나빠진다. **셋 다 과소평가는 안 한다.**
        // 이 자료구조에서 방향과 정확도는 다른 문제다.
        throw new UnsupportedOperationException("TODO 5: estimateCount");
    }

    @Override
    public long totalCount() {
        return total;
    }

    @Override
    public long memoryBytes() {
        return (long) width * depth * Long.BYTES;
    }

    public int width() {
        return width;
    }

    public int depth() {
        return depth;
    }

    /** 칸 수가 실제로 지켜주는 오차율. 올림했으니 요청한 값보다 작거나 같다. */
    public double epsilon() {
        return Math.E / width;
    }

    /** 행 수가 실제로 지켜주는 실패 확률. */
    public double delta() {
        return Math.exp(-depth);
    }

    /** 지금 시점의 오차 한계. 추정치는 실제 + 이 값을 넘지 않아야 한다. */
    public long errorBound() {
        return (long) Math.ceil(epsilon() * total);
    }

    long cell(int row, int column) {
        return table[row][column];
    }
}
