package com.datastructure.allocator;

import org.junit.jupiter.api.DisplayName;

/** 계약을 BestFit 으로 돌린다. 이 전략만의 이야기는 StrategyTest 가 맡는다. */
@DisplayName("BestFit")
class BestFitAllocatorTest extends AllocatorContractTest {

    @Override
    protected Allocator create(int capacity) {
        return new BestFitAllocator(capacity);
    }
}
