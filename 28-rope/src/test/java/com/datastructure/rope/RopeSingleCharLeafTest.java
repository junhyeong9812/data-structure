package com.datastructure.rope;

import org.junit.jupiter.api.DisplayName;

/** 잎 하나에 글자 하나. 가장 깊은 트리에서도 계약이 지켜지는지 본다. */
@DisplayName("Rope 계약 (잎 1)")
class RopeSingleCharLeafTest extends CharSequenceStoreContractTest {

    @Override
    CharSequenceStore of(String text) {
        return new Rope(text, 1);
    }
}
