package com.datastructure.spatial;

import org.junit.jupiter.api.DisplayName;

/**
 * QuadTree 가 계약을 지키는지. 경계와 용량은 QuadTreeStructureTest 가 본다.
 *
 * 경계를 2의 거듭제곱 크기(4096)로 잡는다. 그래야 칸이 계속 정사각형으로 쪼개진다.
 */
@DisplayName("QuadTree (공간을 넷으로 쪼갠다)")
class QuadTreeTest extends SpatialIndexContractTest {

    @Override
    protected SpatialIndex create() {
        return new QuadTree(new Rectangle(-1024, -1024, 3071, 3071), 3);
    }
}
