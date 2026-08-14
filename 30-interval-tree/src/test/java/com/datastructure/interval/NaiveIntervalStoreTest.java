package com.datastructure.interval;

/** 기준선도 같은 계약을 지켜야 한다. 안 그러면 대조가 의미 없다. */
class NaiveIntervalStoreTest extends IntervalStoreContractTest {

    @Override
    protected IntervalStore create() {
        return new NaiveIntervalStore();
    }
}
