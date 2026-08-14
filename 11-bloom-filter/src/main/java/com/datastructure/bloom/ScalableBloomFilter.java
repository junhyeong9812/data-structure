package com.datastructure.bloom;

import java.util.ArrayList;
import java.util.List;

/**
 * 몇 개가 들어올지 모를 때 쓰는 블룸 필터. 차면 새 필터를 잇는다.
 *
 * 기본형의 진짜 약점은 오탐이 아니라 용량을 미리 정해야 한다는 것이다.
 * 100만 개로 잡아뒀는데 1000만 개가 들어오면 오탐률이 1% 에서 60% 로 뛴다.
 * 그때는 이미 늦었다. 원소를 담지 않았으니 다시 만들 수도 없다.
 * (해시맵은 리사이즈로 다시 배치하면 됐다. 여기서는 배치할 원소가 없다)
 *
 * 해법은 늘리는 것이 아니라 잇는 것이다.
 * 지금 필터가 차면 더 큰 필터를 새로 만들어 뒤에 붙인다.
 * 조회는 전부에게 물어 하나라도 true 면 true.
 *
 * 그런데 필터가 늘수록 오탐 기회도 는다. 그래서 새 필터일수록 더 엄격하게 만든다.
 *
 *   필터 0: 용량 n,  오탐률 p
 *   필터 1: 용량 2n, 오탐률 p/2
 *   필터 2: 용량 4n, 오탐률 p/4
 *
 * 전체 오탐률은 1 - (1-p)(1-p/2)(1-p/4)... 인데 2p 를 넘지 않는다.
 * 등비급수가 수렴하기 때문이다. 필터를 무한히 이어도 오탐률은 유계다.
 *
 * 대가는 조회 비용이다. 필터 수만큼 물어야 하니 O(필터 수)다.
 * 용량이 두 배씩 커지므로 필터 수는 원소 수의 로그다.
 */
public class ScalableBloomFilter<T> implements ProbabilisticSet<T> {

    static final double TIGHTENING = 0.5;
    static final int GROWTH = 2;

    private final List<BloomFilter<T>> filters = new ArrayList<>();
    private final int initialCapacity;
    private final double initialFpr;

    private int nextCapacity;
    private double nextFpr;
    private long inserted;

    public ScalableBloomFilter(int initialCapacity, double falsePositiveRate) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("초기 용량은 1 이상이어야 한다: " + initialCapacity);
        }
        if (!(falsePositiveRate > 0.0 && falsePositiveRate < 1.0)) {
            throw new IllegalArgumentException("오탐률은 0 과 1 사이여야 한다: " + falsePositiveRate);
        }
        this.initialCapacity = initialCapacity;
        this.initialFpr = falsePositiveRate;
        this.nextCapacity = initialCapacity;
        this.nextFpr = falsePositiveRate;
    }

    private void grow() {
        // TODO 1: 다음 필터를 만들어 붙이고, 그 다음을 위한 값을 갱신한다.
        //
        // 용량은 GROWTH 배로, 오탐률은 TIGHTENING 배로.
        // **오탐률을 안 조이면** 필터가 늘 때마다 전체 오탐률이 계속 올라 유계가 아니게 된다.
        throw new UnsupportedOperationException("TODO 1: grow");
    }

    @Override
    public void add(T item) {
        // TODO 2: 마지막 필터가 없거나 꽉 찼으면 새로 만들고, 거기에 넣는다.
        //
        // "꽉 찼다"의 기준은 그 필터의 insertedCount 가 capacity 에 닿았을 때다.
        // 그 위로 더 넣으면 오탐률이 설계값을 넘는다.
        //
        // 이전 필터들은 건드리지 않는다. **새 원소는 늘 마지막 필터에만 들어간다.**
        throw new UnsupportedOperationException("TODO 2: add");
    }

    @Override
    public boolean mightContain(T item) {
        // TODO 3: 하나라도 true 면 true.
        //
        // 어느 필터에 들어갔는지 모르니 전부 물어야 한다.
        // **그래서 오탐 기회가 필터 수만큼 늘어난다.** 위에서 오탐률을 조이는 이유다.
        throw new UnsupportedOperationException("TODO 3: mightContain");
    }

    public int filterCount() {
        return filters.size();
    }

    @Override
    public long insertedCount() {
        return inserted;
    }

    @Override
    public long bitSize() {
        long total = 0;
        for (BloomFilter<T> f : filters) {
            total += f.bitSize();
        }
        return total;
    }

    @Override
    public double expectedFalsePositiveRate() {
        // TODO 4: 전부가 false 라고 할 확률의 여집합이다.
        //
        // 필터별 오탐률을 더하면 안 된다. **1 을 넘을 수 있기 때문이다.**
        // "모두 빗나갈 확률"을 곱으로 구하고 1 에서 빼는 것이 맞다.
        throw new UnsupportedOperationException("TODO 4: expectedFalsePositiveRate");
    }

    @Override
    public void clear() {
        filters.clear();
        nextCapacity = initialCapacity;
        nextFpr = initialFpr;
        inserted = 0;
    }
}
