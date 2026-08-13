package com.datastructure.btree;

import org.junit.jupiter.api.DisplayName;

@DisplayName("BTree: 값이 모든 노드에")
class BTreeTest extends SearchTreeContractTest {

    @Override
    protected SearchTree<Integer, String> create(int degree) {
        return new BTree<>(Math.max(2, degree));
    }

    @Override
    protected int smallestDegree() {
        return 2;
    }
}
