package com.datastructure.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 두 구현이 똑같이 지켜야 하는 것.
 *
 * 하위 클래스가 create 만 바꿔 끼운다. 여기에 적힌 것이 곧 "파일 시스템이란 무엇인가" 다.
 *
 * 주의: 하위 클래스에서 @Nested 클래스 이름을 여기와 같게 지으면
 * 상위의 테스트가 실패가 아니라 조용히 사라진다. run.sh --lint 가 그것을 검사한다.
 */
abstract class FileSystemContractTest {

    protected FileSystem fs;

    protected abstract FileSystem create();

    @BeforeEach
    void setUp() {
        fs = create();
    }

    @Nested
    @DisplayName("계약: 만들기")
    class Creating {

        @Test
        @DisplayName("mkdir 는 부모가 있어야 한다. mkdir -p 가 아니다")
        void mkdirNeedsParent() {
            fs.mkdir("/home");
            assertTrue(fs.isDirectory("/home"));
            assertThrows(IllegalArgumentException.class, () -> fs.mkdir("/a/b/c"));
        }

        @Test
        @DisplayName("mkdirs 는 없는 부모를 만들면서 내려간다")
        void mkdirsCreatesTheWholeChain() {
            fs.mkdirs("/a/b/c/d");
            assertTrue(fs.isDirectory("/a"));
            assertTrue(fs.isDirectory("/a/b"));
            assertTrue(fs.isDirectory("/a/b/c/d"));
        }

        @Test
        @DisplayName("mkdirs 를 두 번 불러도 된다. 이미 있는 것은 그냥 지나간다")
        void mkdirsIsIdempotent() {
            fs.mkdirs("/a/b");
            fs.mkdirs("/a/b/c");
            assertEquals(List.of("b"), fs.ls("/a"));
            assertEquals(List.of("c"), fs.ls("/a/b"));
        }

        @Test
        @DisplayName("같은 디렉터리를 두 번 mkdir 하면 던진다")
        void mkdirTwiceThrows() {
            fs.mkdir("/a");
            assertThrows(IllegalArgumentException.class, () -> fs.mkdir("/a"));
        }

        @Test
        @DisplayName("touch 는 이미 있는 파일의 내용을 지우지 않는다")
        void touchDoesNotTruncate() {
            fs.write("/a.txt", "내용");
            fs.touch("/a.txt");
            assertEquals("내용", fs.read("/a.txt"));
        }

        @Test
        @DisplayName("write 는 없으면 만들고 있으면 덮어쓴다")
        void writeCreatesOrOverwrites() {
            fs.write("/a.txt", "처음");
            assertEquals("처음", fs.read("/a.txt"));
            fs.write("/a.txt", "나중");
            assertEquals("나중", fs.read("/a.txt"));
        }

        @Test
        @DisplayName("파일 밑으로는 못 내려간다")
        void cannotDescendThroughAFile() {
            fs.write("/a.txt", "x");
            assertThrows(IllegalArgumentException.class, () -> fs.mkdir("/a.txt/b"));
            assertFalse(fs.exists("/a.txt/b"));
        }

        @Test
        @DisplayName("mkdirs 도 중간이 파일이면 멈춘다")
        void mkdirsStopsAtAFile() {
            fs.write("/a.txt", "x");
            // 이 검사를 빼도 어차피 다른 예외가 난다. 그래서 "던지기만 하면 통과" 인 테스트로는
            // 검사를 지운 것을 못 잡는다. 예외의 종류까지 못 박아야 잡힌다.
            assertThrows(IllegalArgumentException.class, () -> fs.mkdirs("/a.txt/b/c"));
            assertEquals("x", fs.read("/a.txt"), "실패한 mkdirs 가 원본을 건드리면 안 된다");
        }
    }

    @Nested
    @DisplayName("계약: 읽기")
    class Reading {

        @Test
        @DisplayName("디렉터리는 못 읽고 파일은 ls 못 한다")
        void kindsAreNotInterchangeable() {
            fs.mkdir("/d");
            fs.write("/f.txt", "x");
            assertThrows(IllegalArgumentException.class, () -> fs.read("/d"));
            assertThrows(IllegalArgumentException.class, () -> fs.ls("/f.txt"));
        }

        @Test
        @DisplayName("ls 는 늘 이름 오름차순이다")
        void lsIsSorted() {
            fs.mkdir("/d");
            for (String name : List.of("zebra", "apple", "Mango", "banana", "10")) {
                fs.touch("/d/" + name);
            }
            assertEquals(List.of("10", "Mango", "apple", "banana", "zebra"), fs.ls("/d"));
        }

        @Test
        @DisplayName("빈 디렉터리의 ls 는 빈 목록이다")
        void lsOfEmptyDirectory() {
            fs.mkdir("/d");
            assertEquals(List.of(), fs.ls("/d"));
        }

        @Test
        @DisplayName("루트도 디렉터리다")
        void rootIsADirectory() {
            assertTrue(fs.exists("/"));
            assertTrue(fs.isDirectory("/"));
            assertEquals(List.of(), fs.ls("/"));
            fs.mkdir("/a");
            assertEquals(List.of("a"), fs.ls("/"));
        }

        @Test
        @DisplayName("size 는 파일이면 길이, 디렉터리면 서브트리 합이다")
        void sizeSumsTheSubtree() {
            fs.mkdirs("/a/b");
            fs.write("/a/x.txt", "12345");
            fs.write("/a/b/y.txt", "123");
            assertEquals(5, fs.size("/a/x.txt"));
            assertEquals(3, fs.size("/a/b"));
            assertEquals(8, fs.size("/a"));
            assertEquals(8, fs.size("/"));
        }

        @Test
        @DisplayName("find 는 이름이 정확히 같은 것만, 경로 오름차순으로")
        void findMatchesExactNames() {
            fs.mkdirs("/a/b");
            fs.mkdirs("/c");
            fs.touch("/a/target");
            fs.touch("/a/b/target");
            fs.touch("/c/target");
            fs.touch("/c/target2");
            assertEquals(List.of("/a/b/target", "/a/target", "/c/target"),
                    fs.find("/", "target"));
            assertEquals(List.of("/a/b/target", "/a/target"), fs.find("/a", "target"));
        }
    }

    @Nested
    @DisplayName("계약: 지우기")
    class Removing {

        @Test
        @DisplayName("rm 은 파일만, rmdir 은 빈 디렉터리만")
        void removalIsTyped() {
            fs.mkdir("/d");
            fs.write("/d/f.txt", "x");
            assertThrows(IllegalArgumentException.class, () -> fs.rm("/d"));
            assertThrows(IllegalArgumentException.class, () -> fs.rmdir("/d"));
            fs.rm("/d/f.txt");
            fs.rmdir("/d");
            assertFalse(fs.exists("/d"));
        }

        @Test
        @DisplayName("rmr 은 서브트리째 지운다")
        void recursiveRemoveTakesEverything() {
            fs.mkdirs("/a/b/c");
            fs.write("/a/b/c/deep.txt", "x");
            fs.write("/a/top.txt", "y");
            fs.rmr("/a");
            assertFalse(fs.exists("/a"));
            assertFalse(fs.exists("/a/b/c/deep.txt"));
            assertEquals(List.of(), fs.ls("/"));
        }

        @Test
        @DisplayName("없는 것을 지우면 던진다")
        void removingWhatIsNotThereThrows() {
            assertThrows(IllegalArgumentException.class, () -> fs.rm("/nope"));
            assertThrows(IllegalArgumentException.class, () -> fs.rmdir("/nope"));
            assertThrows(IllegalArgumentException.class, () -> fs.rmr("/nope"));
        }

        @Test
        @DisplayName("루트는 못 지운다")
        void rootIsNotRemovable() {
            assertThrows(IllegalArgumentException.class, () -> fs.rmdir("/"));
            assertThrows(IllegalArgumentException.class, () -> fs.rmr("/"));
        }
    }

    @Nested
    @DisplayName("계약: 옮기고 복사하기")
    class MovingAndCopying {

        @Test
        @DisplayName("mv 는 서브트리를 통째로 데려간다")
        void moveTakesTheSubtree() {
            fs.mkdirs("/a/b/c");
            fs.write("/a/b/c/deep.txt", "안녕");
            fs.mkdir("/z");
            fs.mv("/a", "/z/moved");

            assertFalse(fs.exists("/a"));
            assertTrue(fs.exists("/z/moved/b/c"));
            assertEquals("안녕", fs.read("/z/moved/b/c/deep.txt"));
        }

        @Test
        @DisplayName("mv 는 이름 바꾸기이기도 하다")
        void moveIsAlsoRename() {
            fs.write("/old.txt", "x");
            fs.mv("/old.txt", "/new.txt");
            assertFalse(fs.exists("/old.txt"));
            assertEquals("x", fs.read("/new.txt"));
        }

        @Test
        @DisplayName("자기 자신 안으로는 못 옮긴다. 서브트리가 통째로 사라지기 때문이다")
        void cannotMoveIntoItself() {
            fs.mkdirs("/a/b");
            assertThrows(IllegalArgumentException.class, () -> fs.mv("/a", "/a/b/inside"));
            assertThrows(IllegalArgumentException.class, () -> fs.mv("/a", "/a"));
            // 던지고 나서 원래 모양이 그대로여야 한다. 반쯤 옮기고 던지면 더 나쁘다.
            assertTrue(fs.exists("/a/b"));
        }

        @Test
        @DisplayName("접두사가 같을 뿐인 형제는 딸려가지 않는다")
        void siblingWithSharedPrefixStaysPut() {
            fs.mkdir("/a");
            fs.mkdir("/ab");
            fs.write("/ab/keep.txt", "그대로");
            fs.write("/a/move.txt", "옮김");

            fs.mv("/a", "/z");

            assertTrue(fs.exists("/ab/keep.txt"), "/ab 는 /a 의 자손이 아니다");
            assertEquals("그대로", fs.read("/ab/keep.txt"));
            assertEquals("옮김", fs.read("/z/move.txt"));
            assertFalse(fs.exists("/zb"), "/ab 가 /zb 로 끌려가면 안 된다");
        }

        @Test
        @DisplayName("목적지가 이미 있으면 던진다")
        void moveOntoSomethingThrows() {
            fs.write("/a.txt", "1");
            fs.write("/b.txt", "2");
            assertThrows(IllegalArgumentException.class, () -> fs.mv("/a.txt", "/b.txt"));
            assertEquals("1", fs.read("/a.txt"));
            assertEquals("2", fs.read("/b.txt"));
        }

        @Test
        @DisplayName("cp 는 서브트리를 복사한다")
        void copyTakesTheSubtree() {
            fs.mkdirs("/a/b");
            fs.write("/a/b/deep.txt", "안녕");
            fs.cp("/a", "/copy");
            assertEquals("안녕", fs.read("/copy/b/deep.txt"));
            assertTrue(fs.exists("/a/b/deep.txt"), "원본이 남아야 한다");
        }

        @Test
        @DisplayName("사본에 쓴 것이 원본에 비치면 안 된다")
        void copyIsDeep() {
            fs.mkdir("/a");
            fs.write("/a/f.txt", "원본");
            fs.cp("/a", "/b");

            fs.write("/b/f.txt", "사본만 바뀜");

            assertEquals("원본", fs.read("/a/f.txt"), "얕은 복사면 여기가 바뀐다");
            assertEquals("사본만 바뀜", fs.read("/b/f.txt"));
        }

        @Test
        @DisplayName("cp 로 만든 것은 링크가 아니다")
        void copyIsNotALink() {
            fs.write("/a.txt", "x");
            fs.cp("/a.txt", "/b.txt");
            assertEquals(1, fs.linkCount("/a.txt"));
            assertEquals(1, fs.linkCount("/b.txt"));
        }
    }

    @Nested
    @DisplayName("계약: 하드 링크")
    class HardLinks {

        @Test
        @DisplayName("이름 둘이 같은 내용을 가리킨다")
        void twoNamesOneContent() {
            fs.write("/a.txt", "하나");
            fs.link("/a.txt", "/b.txt");

            assertEquals("하나", fs.read("/b.txt"));
            assertEquals(2, fs.linkCount("/a.txt"));
            assertEquals(2, fs.linkCount("/b.txt"));

            fs.write("/b.txt", "바뀜");
            assertEquals("바뀜", fs.read("/a.txt"), "같은 내용을 가리키므로 같이 보인다");
        }

        @Test
        @DisplayName("이름 하나를 지워도 내용은 남는다")
        void removingOneNameKeepsTheContent() {
            fs.write("/a.txt", "내용");
            fs.link("/a.txt", "/b.txt");
            fs.rm("/a.txt");

            assertFalse(fs.exists("/a.txt"));
            assertEquals("내용", fs.read("/b.txt"));
            assertEquals(1, fs.linkCount("/b.txt"));
        }

        @Test
        @DisplayName("디렉터리에는 못 건다. 고리가 생겨 재귀가 안 끝난다")
        void directoriesCannotBeHardLinked() {
            fs.mkdir("/d");
            assertThrows(IllegalArgumentException.class, () -> fs.link("/d", "/e"));
        }

        @Test
        @DisplayName("링크가 없으면 개수가 1 이다")
        void plainFileHasOneLink() {
            fs.write("/a.txt", "x");
            assertEquals(1, fs.linkCount("/a.txt"));
        }
    }

    @Nested
    @DisplayName("계약: 경로 표기가 달라도 같은 자리다")
    class PathForms {

        @Test
        @DisplayName("중복 슬래시, 후행 슬래시, 점 하나")
        void sloppyFormsResolveTheSame() {
            fs.mkdirs("/a/b");
            fs.write("/a/b/f.txt", "x");

            assertEquals("x", fs.read("//a///b/f.txt"));
            assertEquals("x", fs.read("/a/./b/./f.txt"));
            assertEquals(List.of("f.txt"), fs.ls("/a/b/"));
        }

        @Test
        @DisplayName("점 둘은 한 칸 올라간다")
        void dotDotGoesUp() {
            fs.mkdirs("/a/b/c");
            fs.write("/a/f.txt", "x");
            assertEquals("x", fs.read("/a/b/../f.txt"));
            assertEquals("x", fs.read("/a/b/c/../../f.txt"));
        }

        @Test
        @DisplayName("루트 위로는 못 올라간다. 올라가려 하면 루트에 머문다")
        void cannotEscapeAboveRoot() {
            fs.mkdir("/a");
            assertTrue(fs.isDirectory("/.."));
            assertTrue(fs.isDirectory("/../../.."));
            assertEquals(List.of("a"), fs.ls("/a/../.."));
        }

        @Test
        @DisplayName("상대 경로는 안 받는다")
        void relativePathsAreRejected() {
            assertThrows(IllegalArgumentException.class, () -> fs.mkdir("a"));
            assertThrows(IllegalArgumentException.class, () -> fs.exists("./a"));
        }
    }
}
