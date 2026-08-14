package com.datastructure.spatial;

import org.junit.jupiter.api.DisplayName;

/** KdTree 가 계약을 지키는지. 트리 구조 자체는 KdTreeStructureTest 가 본다. */
@DisplayName("KdTree (축을 번갈아 가른다)")
class KdTreeTest extends SpatialIndexContractTest {

    @Override
    protected SpatialIndex create() {
        return new KdTree();
    }
}
