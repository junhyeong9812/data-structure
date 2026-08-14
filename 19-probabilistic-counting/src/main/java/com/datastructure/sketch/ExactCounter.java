package com.datastructure.sketch;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap 하나로 만든 정확한 계수기. 기준선이다.
 *
 * 먼저 이것을 만들어보고 무엇이 문제인지를 본다.
 * 어려울 것이 없다는 점이 요점이다. 18번 BooleanArrayBitSet 과 같은 자리다.
 *
 * 답은 늘 정확하다. 대가는 하나뿐인데, 그 하나가 크다.
 *
 *   원소 종류가 하나 늘 때마다 엔트리가 하나 는다.
 *
 * 개수가 아니라 종류다. 같은 것을 1억 번 넣어도 엔트리는 하나다.
 * 서로 다른 것이 1억 개면 엔트리가 1억 개고 수 GB 다.
 *
 * 그리고 대부분의 스트림에서 원소의 대다수는 한두 번 나오고 끝난다.
 * 궁금한 것은 많이 나온 소수인데 메모리는 안 궁금한 다수가 먹는다.
 */
public class ExactCounter implements FrequencyEstimator {

    /**
     * 엔트리 하나가 대략 몇 바이트인가.
     *
     * HashMap.Node 32 + 박싱된 Integer 16 + 박싱된 Long 16 = 64.
     * 테이블 슬롯과 Integer 캐시는 뺐다. 정확한 값이 아니라 자릿수를 보려는 것이다.
     */
    static final long BYTES_PER_ENTRY = 64;

    private final Map<Integer, Long> counts = new HashMap<>();
    private long total;

    @Override
    public void add(int item) {
        add(item, 1);
    }

    @Override
    public void add(int item, long count) {
        // TODO 1: count 를 검사하고 맵에 더한다. total 도 갱신한다.
        //
        // **음수 count 를 허용하면 안 된다.** 여기서는 빼도 답이 맞지만
        // 같은 인터페이스를 구현하는 CountMinSketch 에서는 즉시 계약이 깨진다.
        // 남의 계수를 깎아 자기 추정치를 실제보다 작게 만들 수 있기 때문이다.
        // (11번에서 CountingBloomFilter 가 삭제를 얻고 "누락 없음"을 잃은 것과 같은 자리다.)
        // 계약은 인터페이스 전체의 것이므로 **정확한 구현도 같이 거부해야 한다.**
        //
        // count 가 0 이면 아무 일도 일어나지 않아야 한다. 엔트리를 만들면 안 된다.
        // Map.merge 를 쓰면 한 줄이다.
        throw new UnsupportedOperationException("TODO 1: add");
    }

    @Override
    public long estimateCount(int item) {
        return counts.getOrDefault(item, 0L);
    }

    @Override
    public long totalCount() {
        return total;
    }

    @Override
    public long memoryBytes() {
        return (long) counts.size() * BYTES_PER_ENTRY;
    }

    /** 서로 다른 원소가 몇 종류였나. 스케치는 이것을 알 방법이 없다. */
    public int distinctCount() {
        return counts.size();
    }
}
