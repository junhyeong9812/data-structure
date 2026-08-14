package com.datastructure.interval;

/**
 * 트리가 계약을 지키는지. 구조 자체는 IntervalTreeStructureTest 가 본다.
 *
 * 여기에 @Nested 를 새로 만들면 부모의 같은 이름이 가려져 조용히 안 돌아간다.
 * 그래서 이 파일에는 create 만 둔다.
 */
class IntervalTreeTest extends IntervalStoreContractTest {

    @Override
    protected IntervalStore create() {
        return new IntervalTree();
    }
}
