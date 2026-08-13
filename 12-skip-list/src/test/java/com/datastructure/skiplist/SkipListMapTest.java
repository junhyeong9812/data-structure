package com.datastructure.skiplist;

import org.junit.jupiter.api.DisplayName;

@DisplayName("SkipListMap")
class SkipListMapTest extends OrderedMapContractTest {

    @Override
    protected OrderedMap<Integer, String> create() {
        // seed 를 고정해 실패를 재현할 수 있게 한다.
        return new SkipListMap<>(20260813L);
    }
}
