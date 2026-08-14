package com.datastructure.rope;

import org.junit.jupiter.api.DisplayName;

/** 기준선도 같은 계약을 지킨다. 다른 것은 값이 아니라 비용뿐이다. */
@DisplayName("StringBuilderStore 계약")
class StringBuilderStoreTest extends CharSequenceStoreContractTest {

    @Override
    CharSequenceStore of(String text) {
        return new StringBuilderStore(text);
    }
}
