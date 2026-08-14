package com.datastructure.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 경로 규칙만 따로 본다.
 *
 * 파일 시스템 두 구현이 이 규칙을 공유한다. 여기가 틀리면 두 구현이 똑같이 틀리고,
 * 그러면 서로 대조해도 안 잡힌다. 대조로 못 잡는 것은 따로 봐야 한다.
 */
@DisplayName("경로 규칙")
class PathsTest {

    @Nested
    @DisplayName("정규화")
    class Normalizing {

        @Test
        @DisplayName("빈 조각과 후행 슬래시는 버린다")
        void emptyPartsGo() {
            assertEquals("/a/b", Paths.normalize("//a///b"));
            assertEquals("/a/b", Paths.normalize("/a/b/"));
            assertEquals("/a/b", Paths.normalize("/a/b//"));
            assertEquals("/", Paths.normalize("/"));
            assertEquals("/", Paths.normalize("///"));
        }

        @Test
        @DisplayName("점 하나는 제자리다")
        void singleDotStaysPut() {
            assertEquals("/a/b", Paths.normalize("/a/./b"));
            assertEquals("/a", Paths.normalize("/./a/."));
            assertEquals("/", Paths.normalize("/."));
        }

        @Test
        @DisplayName("점 둘은 한 칸 올라간다")
        void doubleDotGoesUp() {
            assertEquals("/a", Paths.normalize("/a/b/.."));
            assertEquals("/", Paths.normalize("/a/.."));
            assertEquals("/c", Paths.normalize("/a/b/../../c"));
        }

        @Test
        @DisplayName("루트 위로는 못 올라간다")
        void cannotGoAboveRoot() {
            assertEquals("/", Paths.normalize("/.."));
            assertEquals("/", Paths.normalize("/../../.."));
            assertEquals("/a", Paths.normalize("/../a"));
            assertEquals("/a", Paths.normalize("/../../a"));
            // 이것을 안 막으면 경로가 루트 밖을 가리킨다.
            // 실제 시스템에서는 그 자리가 취약점이고 여기서는 조용한 오답이다.
            assertEquals("/etc", Paths.normalize("/a/../../../etc"));
        }

        @Test
        @DisplayName("점이 이름의 일부인 것은 건드리지 않는다")
        void dotsInsideNamesAreJustNames() {
            assertEquals("/a.txt", Paths.normalize("/a.txt"));
            assertEquals("/...", Paths.normalize("/..."));
            assertEquals("/..a", Paths.normalize("/..a"));
            assertEquals("/a..", Paths.normalize("/a.."));
        }

        @Test
        @DisplayName("null 과 상대 경로는 던진다")
        void badInputThrows() {
            assertThrows(IllegalArgumentException.class, () -> Paths.normalize(null));
            assertThrows(IllegalArgumentException.class, () -> Paths.normalize("a/b"));
            assertThrows(IllegalArgumentException.class, () -> Paths.normalize("./a"));
            assertThrows(IllegalArgumentException.class, () -> Paths.normalize(""));
        }
    }

    @Nested
    @DisplayName("쪼개고 잇기")
    class SplittingAndJoining {

        @Test
        @DisplayName("루트는 빈 목록이다")
        void rootSplitsToNothing() {
            assertEquals(List.of(), Paths.split("/"));
            assertEquals(List.of(), Paths.split("/.."));
            assertEquals(List.of("a", "b"), Paths.split("//a//b/"));
        }

        @Test
        @DisplayName("부모는 정규화한 뒤의 부모다")
        void parentIsComputedAfterNormalizing() {
            assertEquals("/a", Paths.parent("/a/b"));
            assertEquals("/", Paths.parent("/a"));
            assertEquals("/", Paths.parent("/"), "루트의 부모는 루트다");
            // 문자열을 그냥 자르면 여기가 /a/b 로 나온다. 정규화가 먼저다.
            assertEquals("/", Paths.parent("/a/b/.."));
        }

        @Test
        @DisplayName("이름은 마지막 조각이다")
        void nameIsTheLastPart() {
            assertEquals("b", Paths.name("/a/b"));
            assertEquals("b", Paths.name("/a/b/"));
            assertEquals("a", Paths.name("/a/b/.."));
            assertEquals("", Paths.name("/"));
        }

        @Test
        @DisplayName("루트에 이으면 슬래시가 겹치지 않는다")
        void joiningRootDoesNotDoubleTheSlash() {
            assertEquals("/a", Paths.join("/", "a"));
            assertEquals("/a/b", Paths.join("/a", "b"));
            assertEquals("/a", Paths.join("/a/", ""));
            assertEquals("/a", Paths.join("/a", null));
        }
    }

    @Nested
    @DisplayName("조상 판정")
    class Ancestry {

        @Test
        @DisplayName("조각 단위로 본다. 접두사 문자열이 아니다")
        void prefixStringIsNotEnough() {
            assertTrue(Paths.isAncestorOrSame("/a", "/a/b"));
            assertTrue(Paths.isAncestorOrSame("/a", "/a"), "자기 자신도 참이다");
            assertTrue(Paths.isAncestorOrSame("/", "/anything/deep"));

            // 여기가 이 메서드의 존재 이유다. 문자열로 보면 /ab 가 /a 로 시작한다.
            assertFalse(Paths.isAncestorOrSame("/a", "/ab"));
            assertFalse(Paths.isAncestorOrSame("/a", "/ab/c"));
            assertFalse(Paths.isAncestorOrSame("/a/b", "/a"));
        }

        @Test
        @DisplayName("정규화를 거친 뒤에 본다")
        void formDoesNotMatter() {
            assertTrue(Paths.isAncestorOrSame("/a/", "//a//b"));
            assertTrue(Paths.isAncestorOrSame("/a/b/..", "/a/c"));
        }
    }
}
