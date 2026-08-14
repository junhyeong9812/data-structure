package com.datastructure.filesystem;

import org.junit.jupiter.api.DisplayName;

/**
 * 같은 계약을 평면 맵 구현으로 돌린다.
 *
 * 여기가 전부 통과한다는 것이 이 박스의 출발점이다. 트리가 없어도 답은 같다.
 * 그러면 트리는 무엇을 위한 것인가. MeasurementTest 가 답한다.
 */
@DisplayName("평면 경로 맵 파일 시스템")
class FlatPathFileSystemTest extends FileSystemContractTest {

    @Override
    protected FileSystem create() {
        return new FlatPathFileSystem();
    }
}
