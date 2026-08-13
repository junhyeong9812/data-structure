package com.datastructure.btree;

import org.junit.jupiter.api.DisplayName;

@DisplayName("BPlusTree: 값이 잎에만")
class BPlusTreeTest extends SearchTreeContractTest {

    @Override
    protected SearchTree<Integer, String> create(int degree) {
        return new BPlusTree<>(Math.max(3, degree));
    }

    @Override
    protected int smallestDegree() {
        return 3;
    }
}
