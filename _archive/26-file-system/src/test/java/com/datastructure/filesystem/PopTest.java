package com.datastructure.filesystem;

import com.datastructure.filesystem.pop.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("파일 시스템 - POP 구현 테스트")
class PopTest {

    private FileSystem fs;

    @BeforeEach
    void setUp() {
        // TODO: FileSystem 생성
        // fs = new FileSystem();
    }

    @Nested
    @DisplayName("디렉토리 연산")
    class DirectoryOperationsTest {

        @Test
        @DisplayName("01. mkdir 기본")
        void test01_mkdir() {
            // TODO: mkdir 테스트
        }

        @Test
        @DisplayName("02. mkdir 중첩")
        void test02_mkdirNested() {
            // TODO: 중첩 디렉토리 테스트
        }

        @Test
        @DisplayName("03. mkdirp (부모 포함)")
        void test03_mkdirp() {
            // TODO: mkdirp 테스트
        }

        @Test
        @DisplayName("04. ls 기본")
        void test04_ls() {
            // TODO: ls 테스트
        }

        @Test
        @DisplayName("05. ls 정렬")
        void test05_lsSorted() {
            // TODO: 정렬된 ls 테스트
        }
    }

    @Nested
    @DisplayName("파일 연산")
    class FileOperationsTest {

        @Test
        @DisplayName("06. touch 파일 생성")
        void test06_touch() {
            // TODO: touch 테스트
        }

        @Test
        @DisplayName("07. write 파일 쓰기")
        void test07_write() {
            // TODO: write 테스트
        }

        @Test
        @DisplayName("08. read 파일 읽기")
        void test08_read() {
            // TODO: read 테스트
        }

        @Test
        @DisplayName("09. append 내용 추가")
        void test09_append() {
            // TODO: append 테스트
        }

        @Test
        @DisplayName("10. 파일 덮어쓰기")
        void test10_overwrite() {
            // TODO: 덮어쓰기 테스트
        }
    }

    @Nested
    @DisplayName("경로 탐색")
    class PathNavigationTest {

        @Test
        @DisplayName("11. 절대 경로")
        void test11_absolutePath() {
            // TODO: 절대 경로 테스트
        }

        @Test
        @DisplayName("12. 상대 경로 (.)")
        void test12_currentDir() {
            // TODO: 현재 디렉토리 테스트
        }

        @Test
        @DisplayName("13. 부모 경로 (..)")
        void test13_parentDir() {
            // TODO: 부모 디렉토리 테스트
        }

        @Test
        @DisplayName("14. cd 디렉토리 이동")
        void test14_cd() {
            // TODO: cd 테스트
        }

        @Test
        @DisplayName("15. pwd 현재 경로")
        void test15_pwd() {
            // TODO: pwd 테스트
        }
    }

    @Nested
    @DisplayName("삭제 연산")
    class DeleteOperationsTest {

        @Test
        @DisplayName("16. rm 파일 삭제")
        void test16_rmFile() {
            // TODO: 파일 삭제 테스트
        }

        @Test
        @DisplayName("17. rm 빈 디렉토리")
        void test17_rmEmptyDir() {
            // TODO: 빈 디렉토리 삭제 테스트
        }

        @Test
        @DisplayName("18. rm -r 재귀 삭제")
        void test18_rmRecursive() {
            // TODO: 재귀 삭제 테스트
        }

        @Test
        @DisplayName("19. rm 비어있지 않은 디렉토리 실패")
        void test19_rmNonEmptyFails() {
            // TODO: 비어있지 않은 디렉토리 테스트
        }
    }

    @Nested
    @DisplayName("복사/이동")
    class CopyMoveTest {

        @Test
        @DisplayName("20. cp 파일 복사")
        void test20_cpFile() {
            // TODO: 파일 복사 테스트
        }

        @Test
        @DisplayName("21. cp -r 디렉토리 복사")
        void test21_cpRecursive() {
            // TODO: 디렉토리 복사 테스트
        }

        @Test
        @DisplayName("22. mv 파일 이동")
        void test22_mvFile() {
            // TODO: 파일 이동 테스트
        }

        @Test
        @DisplayName("23. mv 이름 변경")
        void test23_mvRename() {
            // TODO: 이름 변경 테스트
        }
    }

    @Nested
    @DisplayName("검색")
    class SearchTest {

        @Test
        @DisplayName("24. find 이름 검색")
        void test24_find() {
            // TODO: 이름 검색 테스트
        }

        @Test
        @DisplayName("25. find 패턴 검색")
        void test25_findPattern() {
            // TODO: 패턴 검색 테스트
        }
    }

    @Nested
    @DisplayName("유틸리티")
    class UtilityTest {

        @Test
        @DisplayName("26. exists 존재 확인")
        void test26_exists() {
            // TODO: 존재 확인 테스트
        }

        @Test
        @DisplayName("27. isDirectory")
        void test27_isDirectory() {
            // TODO: 디렉토리 여부 테스트
        }

        @Test
        @DisplayName("28. isFile")
        void test28_isFile() {
            // TODO: 파일 여부 테스트
        }

        @Test
        @DisplayName("29. size 크기 계산")
        void test29_size() {
            // TODO: 크기 계산 테스트
        }

        @Test
        @DisplayName("30. 루트 삭제 방지")
        void test30_preventRootDelete() {
            // TODO: 루트 삭제 방지 테스트
        }
    }
}
