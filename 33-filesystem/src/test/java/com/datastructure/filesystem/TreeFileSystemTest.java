package com.datastructure.filesystem;

import org.junit.jupiter.api.DisplayName;

/**
 * 계약을 트리 구현으로 돌린다.
 *
 * 이 파일에는 @Nested 클래스를 새로 만들지 않는다. 상위와 이름이 겹치면
 * 상위 테스트가 실패가 아니라 조용히 사라지기 때문이다.
 * 트리에만 있는 이야기는 TreeOnlyTest 가 따로 맡는다.
 */
@DisplayName("트리 파일 시스템")
class TreeFileSystemTest extends FileSystemContractTest {

    @Override
    protected FileSystem create() {
        return new TreeFileSystem();
    }
}
