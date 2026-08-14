package com.datastructure.sketch;

/**
 * 레지스터 m = 2^p 개로 세는 카디널리티 추정기.
 *
 * 발상이 하나다. 무작위 비트열을 여러 개 봤는데 그중 하나가 "앞에 0이 17개 연달아" 였다면,
 * 그런 일은 2^17 번에 한 번 일어나므로 대략 2^17 개쯤 봤을 것이다.
 * 가장 희귀한 사건이 표본 크기를 말해준다. 그리고 그 판단에는 원소가 필요 없다.
 *
 * 문제는 흔들림이다. 운 나쁘게 첫 원소가 0을 30개 달고 오면 추정치가 10억이 된다.
 * 그래서 레지스터 m 개로 나눈다.
 *
 *   해시 64비트를 만들고
 *   상위 p 비트로 레지스터 번호를 정하고
 *   나머지 비트의 선행 0 개수 + 1 을 그 레지스터에 max 로 남긴다
 *
 * 그리고 m 개의 추정치를 조화평균으로 모은다.
 *
 *   추정치 = alpha_m * m^2 / sum(2^-M[j])
 *
 * 왜 산술평균이 아닌가. 각 레지스터의 추정치는 2^M[j] 라 한 칸이 크게 튀면 평균을 끌고 간다.
 * 조화평균은 큰 값의 영향을 눌러준다. alpha_m 은 그 과정에서 생기는 치우침을 보정하는 상수다.
 *
 * 표준 오차가 1.04 / sqrt(m) 이 된다. p = 14 면 m = 16384 이고 0.81% 다.
 * 레지스터 하나가 1바이트니 16KB 로 카디널리티 수억을 센다.
 * (실무 구현은 랭크가 최대 51이라 6비트로 눌러 담아 12KB 를 쓴다. 여기서는 안 한다)
 *
 * 이 구조에 무작위 씨앗이 없다는 점을 보라. 해시가 곧 무작위다.
 * 12번 스킵 리스트는 동전을 던졌지만 여기서는 원소 자체가 동전이다.
 * 그래서 같은 집합이면 순서를 어떻게 넣어도 레지스터가 같고, 그것이 merge 를 가능하게 한다.
 */
public class HyperLogLog implements CardinalityEstimator {

    /** m = 16. 이보다 적으면 오차가 26% 라 의미가 없다. */
    static final int MIN_PRECISION = 4;

    /** m = 65536. 이보다 크면 int 키 42억 개에 비해 과하다. */
    static final int MAX_PRECISION = 16;

    private final int p;
    private final int m;
    private final byte[] registers;

    public HyperLogLog(int p) {
        if (p < MIN_PRECISION || p > MAX_PRECISION) {
            throw new IllegalArgumentException(
                    "정밀도는 " + MIN_PRECISION + " 과 " + MAX_PRECISION + " 사이여야 한다: " + p);
        }
        this.p = p;
        this.m = 1 << p;
        this.registers = new byte[m];
    }

    /**
     * 조화평균의 치우침을 잡는 상수. 실험과 적분으로 얻은 값이라 유도할 수 있는 것이 아니다.
     * m 이 작을 때만 표를 쓰고, 128 이상은 식으로 간다.
     */
    static double alpha(int m) {
        return switch (m) {
            case 16 -> 0.673;
            case 32 -> 0.697;
            case 64 -> 0.709;
            default -> 0.7213 / (1 + 1.079 / m);
        };
    }

    @Override
    public void add(int item) {
        // TODO 1: 해시를 내고 레지스터 번호와 랭크를 뽑아 max 로 남긴다.
        //
        //   h = CountMinSketch.mix64(item)                    64비트 해시
        //   레지스터 번호 = h 의 **상위 p 비트**               h >>> (64 - p)
        //   랭크 = 남은 64-p 비트의 **선행 0 개수 + 1**
        //
        // 남은 비트를 어떻게 위로 올리는가. h << p 를 하면 상위 p 비트가 밀려나고
        // 남은 비트가 맨 위로 온다. 거기에 Long.numberOfLeadingZeros 를 쓰면 된다.
        //
        // 함정 셋.
        //   - **+1 을 빠뜨리는 것이 여기서 제일 흔하다.** 랭크 0 은 "아직 안 쓴 레지스터"라
        //     쓰면 안 되는 값이다. 첫 비트가 1이어도 랭크는 1 이어야 한다
        //   - **max 로 남겨야 한다.** 그냥 대입하면 나중에 온 원소가 앞의 기록을 지운다.
        //     max 여야 순서에 안 흔들리고, 순서에 안 흔들려야 merge 가 성립한다
        //   - h << p 가 0 이면 선행 0 이 64개로 세어져 랭크가 64-p+1 을 넘는다.
        //     Math.min 으로 잘라라. (다만 이 방어선은 **어떤 테스트도 못 잡는다.**
        //     그 일이 일어나려면 mix64 의 하위 64-p 비트가 전부 0 이어야 하는데
        //     p=14 면 2^50 분의 1 이고 int 입력 42억 개로는 닿지 않는다.
        //     지워도 96개가 다 통과한다. 11번 h2 == 0, 18번 nextSetBit 범위검사에 이어 세 번째다)
        //
        // 정직하게 덧붙인다. **하위 p 비트로 번호를 정해도 96개가 다 통과한다.**
        // mix64 가 잘 섞어서 어느 쪽 p 비트든 고르게 퍼지기 때문이다.
        // 그래도 상위 비트를 쓰는 것이 관례다. 랭크가 쓰는 비트와 겹치지 않는 쪽이
        // 번호와 랭크의 독립성을 눈으로 보장해주기 때문이다.
        throw new UnsupportedOperationException("TODO 1: add");
    }

    /**
     * 보정 없는 조화평균 추정치.
     *
     * 이것만 쓰면 작은 카디널리티에서 완전히 무너진다. 얼마나 틀리는지 직접 재보라.
     * rawEstimateIsHopelesslyWrong 테스트가 그 숫자를 박아두고 있다.
     */
    public long rawEstimate() {
        // TODO 2: alpha(m) * m * m / sum(2^-M[j]) 를 반올림해 돌려준다.
        //
        // 안 쓴 레지스터도 합에 들어간다. 2^-0 = 1 이다. **빼면 안 된다.**
        // 그 1들이 분모를 키워 추정치를 낮추는 것이 정상 동작이다.
        //
        // 함정: m * m 을 int 로 계산하면 p=16 에서 넘친다(65536^2 = 2^32). double 로 올려라.
        throw new UnsupportedOperationException("TODO 2: rawEstimate");
    }

    @Override
    public long estimate() {
        // TODO 3: raw 가 작으면 **다른 방법**으로 센다 (linear counting).
        //
        //   raw <= 2.5 * m 이고 빈 레지스터가 있으면   ->  m * ln(m / 빈레지스터수)
        //   아니면                                     ->  raw
        //
        // 왜 필요한가. 레지스터 16384개 중 10개만 켜져 있으면 분모에 2^0 이 16374번 더해져
        // **분모가 카디널리티와 거의 무관해진다.** 그래서 10개를 넣어도 1만 넘게 나온다.
        //
        // 대신 쓰는 식은 HyperLogLog 가 아니다. 상자 m 개에 공을 무작위로 던져
        // 빈 상자가 z 개 남았다면 던진 공이 몇 개인지 역산하는 것이다.
        // **이 국면에서는 이 자료구조를 아예 안 쓰는 셈이다.** 그래서 "보정"이라 부른다.
        //
        // 빈 레지스터가 0개면 ln(m/0) 이 무한대라 쓸 수 없다. 그 경우가 조건에 들어 있다.
        // 다만 그 검사도 **테스트가 못 잡는다.** 레지스터가 전부 차려면 원소가 m 보다 훨씬
        // 많아야 하고, 그러면 raw 가 이미 2.5m 을 넘어 앞 조건에서 걸러진다. 앞의 불변식이
        // 뒤의 검사를 여벌로 만드는 경우다. 지우지 않되 못 잡는다는 것을 알고 남긴다.
        throw new UnsupportedOperationException("TODO 3: estimate");
    }

    /**
     * 다른 HyperLogLog 를 흡수한다. 이것이 이 자료구조의 킬러 기능이다.
     *
     * 서버 100대가 각자 세고 16KB 씩 보내면 원본을 하나도 안 옮기고 전체 순 방문자를 안다.
     * 레디스의 PFMERGE 가 이것이다.
     */
    public void merge(HyperLogLog other) {
        // TODO 4: 레지스터별로 max 를 취한다.
        //
        // 한 줄 반이다. **이 단순함이 요점이다.**
        //
        // 왜 되는가. 레지스터 j 의 값은 "j 로 간 원소들의 랭크 최댓값"이다.
        // 합집합의 최댓값은 각 집합 최댓값의 최댓값이다. max 가 결합법칙과 교환법칙을 지키니까.
        // 그래서 결과가 **한 번에 넣은 것과 비슷한 정도가 아니라 바이트 단위로 같다.**
        //
        // 정밀도가 다르면 거부하라. 상위 p 비트로 번호를 정했으므로 p 가 다르면
        // 같은 원소가 다른 칸에 있다. 섞으면 조용히 틀린 답이 나온다.
        //
        // 그리고 **교집합은 이 방법으로 안 된다.** min 을 취하면 될 것 같지만 틀린다.
        // 레지스터가 "어느 원소가 남긴 값인지"를 기억하지 못하기 때문이다.
        throw new UnsupportedOperationException("TODO 4: merge");
    }

    @Override
    public long memoryBytes() {
        return registers.length;
    }

    public int precision() {
        return p;
    }

    public int registerCount() {
        return m;
    }

    int register(int index) {
        return registers[index];
    }

    int zeroRegisters() {
        int zeros = 0;
        for (byte r : registers) {
            if (r == 0) {
                zeros++;
            }
        }
        return zeros;
    }
}
