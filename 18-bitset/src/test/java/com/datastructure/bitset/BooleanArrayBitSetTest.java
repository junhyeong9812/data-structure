package com.datastructure.bitset;

import org.junit.jupiter.api.DisplayName;

@DisplayName("BooleanArrayBitSet: 나이브 기준선")
class BooleanArrayBitSetTest extends BitVectorContractTest {

    @Override
    protected BitVector create(int size) {
        return new BooleanArrayBitSet(size);
    }
}
