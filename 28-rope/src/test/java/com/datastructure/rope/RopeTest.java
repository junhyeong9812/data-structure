package com.datastructure.rope;

import org.junit.jupiter.api.DisplayName;

/**
 * 잎을 4 글자로 잡는다. 기본값 32 로 하면 계약 테스트의 짧은 문자열이 잎 하나에 다 들어가서
 * 트리를 한 번도 안 타고 통과해 버린다.
 */
@DisplayName("Rope 계약 (잎 4)")
class RopeTest extends CharSequenceStoreContractTest {

    @Override
    CharSequenceStore of(String text) {
        return new Rope(text, 4);
    }
}
