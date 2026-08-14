package com.datastructure.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 두 구현이 어디서 갈라지는지 숫자로 적는다.
 *
 * 계약 테스트 66개는 양쪽 다 통과한다. 답이 같다는 뜻이다.
 * 답이 같으면 더 쉬운 쪽을 쓰는 것이 맞고, 평면 맵이 훨씬 쉽다.
 * 그런데도 실제 파일 시스템이 전부 트리인 이유가 이 파일에 있다.
 */
@DisplayName("트리와 평면 맵의 비용 측정")
class MeasurementTest {

    /** 폭 w, 깊이 d 로 채운다. 파일은 잎에만 둔다. */
    private static void fill(FileSystem fs, String base, int width, int depth) {
        if (depth == 0) {
            for (int i = 0; i < width; i++) {
                fs.write(base + "/f" + i + ".txt", "x");
            }
            return;
        }
        for (int i = 0; i < width; i++) {
            String child = base + "/d" + i;
            fs.mkdir(child);
            fill(fs, child, width, depth - 1);
        }
    }

    @Nested
    @DisplayName("측정 1: 디렉터리를 옮길 때")
    class MovingADirectory {

        private void build(FileSystem fs) {
            fs.mkdir("/src");
            fill(fs, "/src", 4, 3);       // 4^3 개 디렉터리에 파일 4개씩
            fs.mkdir("/dst");
        }

        @Test
        @DisplayName("트리는 링크 하나, 평면 맵은 서브트리 전부를 다시 쓴다")
        void treeRewritesOneEntry() {
            TreeFileSystem tree = new TreeFileSystem();
            FlatPathFileSystem flat = new FlatPathFileSystem();
            build(tree);
            build(flat);

            tree.mv("/src", "/dst/moved");
            flat.mv("/src", "/dst/moved");

            System.out.printf("  /src 아래 항목 %,d개를 /dst/moved 로 옮긴다%n",
                    flat.entryCount() - 2);
            System.out.printf("    트리      키 다시 쓰기 %,7d%n", tree.rewrittenEntries());
            System.out.printf("    평면 맵   키 다시 쓰기 %,7d%n", flat.rewrittenEntries());

            assertEquals(1, tree.rewrittenEntries(), "트리는 부모의 링크 하나다");
            assertEquals(341, flat.rewrittenEntries(), "평면 맵은 자기 자신과 자손 전부다");

            // 답은 같다. 그것이 요점이다.
            assertEquals(tree.ls("/dst/moved"), flat.ls("/dst/moved"));
            assertEquals(tree.size("/dst/moved"), flat.size("/dst/moved"));
        }

        @Test
        @DisplayName("서브트리가 커질수록 격차가 그대로 벌어진다")
        void theGapGrowsWithTheSubtree() {
            for (int depth : new int[] {1, 2, 3}) {
                TreeFileSystem tree = new TreeFileSystem();
                FlatPathFileSystem flat = new FlatPathFileSystem();
                for (FileSystem fs : List.of(tree, flat)) {
                    fs.mkdir("/src");
                    fill(fs, "/src", 4, depth);
                    fs.mkdir("/dst");
                }
                tree.mv("/src", "/dst/m");
                flat.mv("/src", "/dst/m");
                System.out.printf("    깊이 %d   트리 %d   평면 맵 %,6d%n",
                        depth, tree.rewrittenEntries(), flat.rewrittenEntries());
                assertEquals(1, tree.rewrittenEntries(), "깊이와 무관하다");
            }
        }

        @Test
        @DisplayName("파일 하나를 옮기면 둘이 같다. 트리의 이점은 서브트리에서만 나온다")
        void movingASingleFileIsTheSame() {
            TreeFileSystem tree = new TreeFileSystem();
            FlatPathFileSystem flat = new FlatPathFileSystem();
            for (FileSystem fs : List.of(tree, flat)) {
                fs.write("/a.txt", "x");
            }
            tree.mv("/a.txt", "/b.txt");
            flat.mv("/a.txt", "/b.txt");

            assertEquals(1, tree.rewrittenEntries());
            assertEquals(1, flat.rewrittenEntries());
        }
    }

    @Nested
    @DisplayName("측정 2: 경로 하나를 열 때")
    class OpeningAPath {

        @Test
        @DisplayName("한계 - 여기서는 평면 맵이 이긴다. 트리는 조각 수만큼 내려간다")
        void deepLookupFavoursTheFlatMap() {
            TreeFileSystem tree = new TreeFileSystem();
            FlatPathFileSystem flat = new FlatPathFileSystem();
            StringBuilder path = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                path.append("/d").append(i);
            }
            for (FileSystem fs : List.of(tree, flat)) {
                fs.mkdirs(path.toString());
                fs.write(path + "/f.txt", "x");
            }

            tree.read(path + "/f.txt");
            flat.read(path + "/f.txt");

            System.out.printf("  깊이 21 인 경로 하나 읽기%n");
            System.out.printf("    트리      방문 %,5d%n", tree.visitedNodes());
            System.out.printf("    평면 맵   방문 %,5d%n", flat.visitedNodes());

            assertEquals(22, tree.visitedNodes(), "루트 + 조각 21개");
            assertEquals(1, flat.visitedNodes(), "해시 한 번");
            assertTrue(tree.visitedNodes() > flat.visitedNodes(),
                    "이 연산만 보면 트리가 진다");
        }
    }

    @Nested
    @DisplayName("측정 3: 자식을 물을 때")
    class ListingChildren {

        @Test
        @DisplayName("트리는 자기 자식만 본다. 평면 맵은 전체를 훑는다")
        void lsCostsTheWholeMapWhenFlat() {
            TreeFileSystem tree = new TreeFileSystem();
            FlatPathFileSystem flat = new FlatPathFileSystem();
            for (FileSystem fs : List.of(tree, flat)) {
                fs.mkdir("/a");
                fill(fs, "/a", 4, 3);
            }

            List<String> byTree = tree.ls("/a");
            List<String> byFlat = flat.ls("/a");

            System.out.printf("  항목 %,d개 중 /a 의 자식 %d개를 얻는다%n",
                    flat.entryCount(), byTree.size());
            System.out.printf("    트리      방문 %,7d%n", tree.visitedNodes());
            System.out.printf("    평면 맵   방문 %,7d%n", flat.visitedNodes());

            assertEquals(byTree, byFlat, "답은 같다");
            assertEquals(4, byTree.size());
            assertEquals(2, tree.visitedNodes(), "루트와 /a 만 본다");
            assertEquals(343, flat.visitedNodes(), "부모 관계가 안 적혀 있어 전부 훑는다");
        }

        @Test
        @DisplayName("find 도 같은 이야기다. 트리는 서브트리만 훑는다")
        void findStaysInsideTheSubtree() {
            TreeFileSystem tree = new TreeFileSystem();
            FlatPathFileSystem flat = new FlatPathFileSystem();
            for (FileSystem fs : List.of(tree, flat)) {
                fs.mkdir("/big");
                fill(fs, "/big", 4, 3);
                fs.mkdir("/small");
                fs.write("/small/target.txt", "x");
            }

            List<String> a = tree.find("/small", "target.txt");
            long treeVisits = tree.visitedNodes();
            List<String> b = flat.find("/small", "target.txt");

            System.out.printf("  큰 서브트리 옆에서 작은 서브트리를 찾는다%n");
            System.out.printf("    트리      방문 %,7d%n", treeVisits);
            System.out.printf("    평면 맵   방문 %,7d%n", flat.visitedNodes());

            assertEquals(a, b);
            assertEquals(List.of("/small/target.txt"), a);
            assertEquals(3, treeVisits, "/small 로 내려가고 자식 하나를 본다");
            assertEquals(345, flat.visitedNodes(), "관계없는 /big 까지 전부 본다");
        }
    }

    @Nested
    @DisplayName("측정 4: 복사는 양쪽 다 서브트리 크기다")
    class CopyingCostsBoth {

        @Test
        @DisplayName("cp 에서는 구조가 아무것도 안 구해준다")
        void copyIsLinearInBothStructures() {
            TreeFileSystem tree = new TreeFileSystem();
            FlatPathFileSystem flat = new FlatPathFileSystem();
            for (FileSystem fs : List.of(tree, flat)) {
                fs.mkdir("/a");
                fill(fs, "/a", 4, 2);
            }

            tree.cp("/a", "/b");
            flat.cp("/a", "/b");

            System.out.printf("  서브트리 복사%n");
            System.out.printf("    트리      만든 항목 %,6d%n", tree.rewrittenEntries());
            System.out.printf("    평면 맵   만든 항목 %,6d%n", flat.rewrittenEntries());

            // mv 와 대비된다. mv 는 관계만 바꾸므로 트리가 이기고,
            // cp 는 내용을 새로 만들어야 하므로 아무도 못 피한다.
            assertEquals(85, tree.rewrittenEntries());
            assertEquals(85, flat.rewrittenEntries());
            assertEquals(tree.size("/b"), flat.size("/b"));
        }
    }
}
