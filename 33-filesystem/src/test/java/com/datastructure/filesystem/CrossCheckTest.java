package com.datastructure.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 무작위 연산을 두 구현에 똑같이 넣고 상태를 맞대본다.
 *
 * 계약 테스트는 내가 생각해낸 경우만 본다. 트리를 걷는 코드의 버그는
 * "특정한 모양에서만 특정한 자식을 빠뜨린다" 로 나타나는 일이 많은데,
 * 그 모양을 손으로 떠올리기가 어렵다. 무작위가 대신 떠올린다.
 *
 * 대조가 성립하려면 기준선이 맞아야 한다. 평면 맵이 그 역할이다.
 * 평면 맵은 경로가 키라서 트리를 잘못 걸을 여지 자체가 없다.
 *
 * 32번 역색인의 전수 조사 대조와 같은 설계다. 거기서도 쉬운 쪽이 기준선이었다.
 */
@DisplayName("무작위 연산 대조")
class CrossCheckTest {

    /** 결정적 난수. Random 을 쓰면 JDK 판이 바뀔 때 재현이 안 된다. */
    private static final class Dice {
        private long state;

        Dice(long seed) {
            this.state = seed;
        }

        int next(int bound) {
            state = state * 6364136223846793005L + 1442695040888963407L;
            return (int) Math.floorMod(state >>> 33, bound);
        }
    }

    /**
     * 파일 시스템 전체를 문자열 하나로 적는다.
     *
     * 이 문자열이 같으면 두 구현의 상태가 같다. 연산 하나마다 이것을 비교하므로
     * 어긋난 순간의 연산을 바로 짚을 수 있다. 끝에서 한 번만 비교하면
     * 어디서 갈렸는지 찾느라 다시 처음부터 재현해야 한다.
     */
    private static String snapshot(FileSystem fs) {
        StringBuilder out = new StringBuilder();
        walk(fs, "/", out);
        return out.toString();
    }

    private static void walk(FileSystem fs, String path, StringBuilder out) {
        if (!fs.isDirectory(path)) {
            out.append(path).append(" = ").append(fs.read(path))
                    .append(" (링크 ").append(fs.linkCount(path)).append(")\n");
            return;
        }
        out.append(path).append("/\n");
        for (String child : fs.ls(path)) {
            walk(fs, Paths.join(path, child), out);
        }
    }

    /** 지금 있는 경로 전부. 다음 연산의 대상을 여기서 고른다. */
    private static List<String> allPaths(FileSystem fs) {
        List<String> out = new ArrayList<>();
        gather(fs, "/", out);
        return out;
    }

    private static void gather(FileSystem fs, String path, List<String> out) {
        out.add(path);
        if (fs.isDirectory(path)) {
            for (String child : fs.ls(path)) {
                gather(fs, Paths.join(path, child), out);
            }
        }
    }

    /**
     * 두 구현에 같은 연산을 넣는다.
     *
     * 한쪽만 던지면 그 자체가 불일치다. 둘 다 던지면 정상이고 그냥 넘어간다.
     * "둘 다 던졌다" 를 성공으로 세지 않으려고 실제로 수행된 연산 수를 따로 센다.
     */
    private static boolean applyBoth(FileSystem a, FileSystem b, Runnable onA, Runnable onB) {
        boolean threwA = false;
        boolean threwB = false;
        try {
            onA.run();
        } catch (RuntimeException e) {
            threwA = true;
        }
        try {
            onB.run();
        } catch (RuntimeException e) {
            threwB = true;
        }
        assertEquals(threwA, threwB, "한쪽만 던졌다");
        return !threwA;
    }

    private static void run(long seed, int operations) {
        TreeFileSystem tree = new TreeFileSystem();
        FlatPathFileSystem flat = new FlatPathFileSystem();
        Dice dice = new Dice(seed);
        int applied = 0;

        for (int step = 0; step < operations; step++) {
            List<String> paths = allPaths(tree);
            String target = paths.get(dice.next(paths.size()));
            String other = paths.get(dice.next(paths.size()));
            String fresh = Paths.join(target, "n" + dice.next(4));
            String value = "v" + step;

            boolean did = switch (dice.next(9)) {
                case 0 -> applyBoth(tree, flat,
                        () -> tree.mkdir(fresh), () -> flat.mkdir(fresh));
                case 1 -> applyBoth(tree, flat,
                        () -> tree.mkdirs(fresh + "/deep"), () -> flat.mkdirs(fresh + "/deep"));
                case 2 -> applyBoth(tree, flat,
                        () -> tree.write(fresh, value), () -> flat.write(fresh, value));
                case 3 -> applyBoth(tree, flat,
                        () -> tree.rm(target), () -> flat.rm(target));
                case 4 -> applyBoth(tree, flat,
                        () -> tree.rmr(target), () -> flat.rmr(target));
                case 5 -> applyBoth(tree, flat,
                        () -> tree.mv(target, Paths.join(other, "moved")),
                        () -> flat.mv(target, Paths.join(other, "moved")));
                case 6 -> applyBoth(tree, flat,
                        () -> tree.cp(target, Paths.join(other, "copied")),
                        () -> flat.cp(target, Paths.join(other, "copied")));
                case 7 -> applyBoth(tree, flat,
                        () -> tree.link(target, Paths.join(other, "linked")),
                        () -> flat.link(target, Paths.join(other, "linked")));
                default -> applyBoth(tree, flat,
                        () -> tree.rmdir(target), () -> flat.rmdir(target));
            };
            if (did) {
                applied++;
            }

            assertEquals(snapshot(flat), snapshot(tree),
                    "seed " + seed + " 의 " + step + "번째 연산 뒤에 갈렸다");
        }

        // 전부 예외로 튕겨나가면 아무것도 대조하지 않은 것이다. 그린 위장을 막는다.
        assertTrue(applied > operations / 5,
                "실제로 수행된 연산이 " + applied + "개뿐이다. 대조가 성립하지 않는다");
    }

    @Nested
    @DisplayName("두 구현의 상태가 연산마다 같다")
    class SameStateEveryStep {

        @Test
        @DisplayName("seed 1 부터 12 까지, 각 200 연산")
        void randomSequencesAgree() {
            for (long seed = 1; seed <= 12; seed++) {
                run(seed, 200);
            }
        }

        @Test
        @DisplayName("긴 한 판. 800 연산")
        void oneLongRun() {
            run(20_260_814L, 800);
        }
    }

    @Nested
    @DisplayName("대조가 실제로 무언가를 보고 있나")
    class TheCrossCheckHasTeeth {

        @Test
        @DisplayName("스냅샷은 내용과 링크 수까지 본다")
        void snapshotSeesContentAndLinks() {
            TreeFileSystem fs = new TreeFileSystem();
            fs.write("/a.txt", "하나");
            String before = snapshot(fs);

            fs.write("/a.txt", "둘");
            assertTrue(!before.equals(snapshot(fs)), "내용이 바뀌면 스냅샷이 바뀌어야 한다");

            String afterWrite = snapshot(fs);
            fs.link("/a.txt", "/b.txt");
            assertTrue(!afterWrite.equals(snapshot(fs)), "링크가 늘면 스냅샷이 바뀌어야 한다");
        }

        @Test
        @DisplayName("한쪽만 던지면 잡는다")
        void oneSidedThrowIsCaught() {
            TreeFileSystem tree = new TreeFileSystem();
            FlatPathFileSystem flat = new FlatPathFileSystem();
            flat.mkdir("/only-in-flat");

            // 한쪽에만 있는 것을 지우면 한쪽만 성공한다. applyBoth 가 그것을 잡아야 한다.
            org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class,
                    () -> applyBoth(tree, flat,
                            () -> tree.rmdir("/only-in-flat"),
                            () -> flat.rmdir("/only-in-flat")));
        }
    }
}
