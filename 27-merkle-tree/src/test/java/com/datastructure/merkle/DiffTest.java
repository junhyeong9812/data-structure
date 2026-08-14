package com.datastructure.merkle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 한계 측정. 차이 찾기가 정말 O(log n) 인지를 걸음 수로 잰다.
 *
 * 시간을 재지 않는다. 비교한 노드 수를 센다. 결정적이고, 같은 수가 언제나 나온다.
 * 전수 비교는 잎 n 개를 다 본다. 트리가 그보다 훨씬 적으면 건너뛰기가 먹은 것이다.
 *
 * 여기가 이 자료구조를 쓰는 이유가 통째로 들어 있는 자리다.
 * 답만 보면 전수 비교와 구별할 수 없다. 숫자로만 갈린다.
 */
@DisplayName("차이 찾기와 걸음 수")
class DiffTest {

    private static final HashFunction SHA = new Sha256Hash();

    private static List<byte[]> copyOf(List<byte[]> blocks) {
        List<byte[]> out = new ArrayList<>(blocks.size());
        for (byte[] b : blocks) {
            out.add(b.clone());
        }
        return out;
    }

    @Nested
    @DisplayName("findFirstDifference")
    class FirstDifference {

        @Test
        @DisplayName("같은 트리면 -1 이고 비교는 한 번이다")
        void identicalTreesCostOneComparison() {
            MerkleTree local = new MerkleTree(TestSupport.blocks(1024), SHA);
            MerkleTree remote = new MerkleTree(TestSupport.blocks(1024), SHA);
            local.resetComparisons();
            assertEquals(-1, local.findFirstDifference(remote));
            assertEquals(1, local.comparisons(),
                    "블록 1024개가 같다는 것을 뿌리 비교 한 번으로 답한다");
        }

        @Test
        @DisplayName("한 곳이 다르면 1 + 높이 번 비교로 찾는다")
        void oneDifferenceCostsLogN() {
            List<byte[]> remoteBlocks = TestSupport.blocks(1024);
            remoteBlocks.set(777, TestSupport.bytes("corrupted"));
            MerkleTree local = new MerkleTree(TestSupport.blocks(1024), SHA);
            MerkleTree remote = new MerkleTree(remoteBlocks, SHA);

            local.resetComparisons();
            assertEquals(777, local.findFirstDifference(remote));
            assertEquals(11, local.comparisons(), "뿌리 1번 + 10층 각 1번. 전수라면 1024번이다");
            assertEquals(10, local.height());
        }

        @Test
        @DisplayName("맨 앞이 달라도 맨 뒤가 달라도 걸음 수가 같다")
        void costDoesNotDependOnWhere() {
            for (int at : new int[] {0, 1, 511, 512, 1023}) {
                List<byte[]> remoteBlocks = TestSupport.blocks(1024);
                remoteBlocks.set(at, TestSupport.bytes("x"));
                MerkleTree local = new MerkleTree(TestSupport.blocks(1024), SHA);
                MerkleTree remote = new MerkleTree(remoteBlocks, SHA);
                local.resetComparisons();
                assertEquals(at, local.findFirstDifference(remote));
                assertEquals(11, local.comparisons(), at + "번이 다를 때");
            }
        }

        @Test
        @DisplayName("여러 곳이 달라도 가장 왼쪽을 준다")
        void returnsTheLeftmost() {
            TestSupport.Dice dice = new TestSupport.Dice(4242L);
            for (int round = 0; round < 200; round++) {
                int n = 1 + dice.next(60);
                List<byte[]> localBlocks = dice.blocks(n);
                List<byte[]> remoteBlocks = copyOf(localBlocks);
                int changes = dice.next(4);
                for (int c = 0; c < changes; c++) {
                    remoteBlocks.set(dice.next(n), dice.block());
                }
                int expected = -1;
                for (int i = 0; i < n; i++) {
                    if (!Arrays.equals(localBlocks.get(i), remoteBlocks.get(i))) {
                        expected = i;
                        break;
                    }
                }
                MerkleTree local = new MerkleTree(localBlocks, SHA);
                MerkleTree remote = new MerkleTree(remoteBlocks, SHA);
                assertEquals(expected, local.findFirstDifference(remote),
                        "n=" + n + " round=" + round);
            }
        }

        @Test
        @DisplayName("승격이 섞인 모양에서도 맞는다 (잎 1..70, 잎마다 한 곳씩)")
        void oddShapes() {
            TestSupport.Dice dice = new TestSupport.Dice(1234L);
            for (int n = 1; n <= 70; n++) {
                List<byte[]> localBlocks = dice.blocks(n);
                MerkleTree local = new MerkleTree(localBlocks, SHA);
                for (int at = 0; at < n; at++) {
                    List<byte[]> remoteBlocks = copyOf(localBlocks);
                    remoteBlocks.set(at, TestSupport.bytes("changed-" + at));
                    MerkleTree remote = new MerkleTree(remoteBlocks, SHA);
                    assertEquals(at, local.findFirstDifference(remote), "n=" + n + " at=" + at);
                }
            }
        }

        @Test
        @DisplayName("잎 개수가 다르면 거부한다")
        void differentLeafCounts() {
            MerkleTree local = new MerkleTree(TestSupport.blocks(8), SHA);
            MerkleTree remote = new MerkleTree(TestSupport.blocks(9), SHA);
            assertThrows(IllegalArgumentException.class, () -> local.findFirstDifference(remote));
            assertThrows(IllegalArgumentException.class, () -> local.findFirstDifference(null));
        }
    }

    @Nested
    @DisplayName("diffBlocks: 다른 블록 전부")
    class AllDifferences {

        @Test
        @DisplayName("1024블록에서 3개가 다를 때 몇 개를 보는가")
        void threeOutOfAThousand() {
            // 이것이 rsync 와 카산드라가 하는 일이다.
            // 다른 블록만 보내려면 어느 블록이 다른지를 먼저 싸게 알아야 한다.
            List<byte[]> remoteBlocks = TestSupport.blocks(1024);
            remoteBlocks.set(17, TestSupport.bytes("a"));
            remoteBlocks.set(600, TestSupport.bytes("b"));
            remoteBlocks.set(1023, TestSupport.bytes("c"));
            MerkleTree local = new MerkleTree(TestSupport.blocks(1024), SHA);
            MerkleTree remote = new MerkleTree(remoteBlocks, SHA);

            local.resetComparisons();
            assertEquals(List.of(17, 600, 1023), MerkleProblems.diffBlocks(local, remote));
            assertEquals(55, local.comparisons(),
                    "노드 55개를 봤다. 전수 비교는 1024개다");
            assertTrue(local.comparisons() * 15 < 1024, "적어도 15배는 적어야 한다");
        }

        @Test
        @DisplayName("전부 다르면 건너뛸 것이 없다")
        void everythingDiffers() {
            // 정직하게 적어둔다. 차이가 흩어져 많아지면 트리를 거의 다 내려간다.
            // 노드 수가 2n-1 이므로 전수 비교보다 오히려 더 많이 본다.
            // 머클 트리가 이기는 것은 "대부분 같다"일 때뿐이다.
            List<byte[]> local = TestSupport.blocks(64);
            List<byte[]> remote = TestSupport.blocks(64, "other-");
            MerkleTree a = new MerkleTree(local, SHA);
            MerkleTree b = new MerkleTree(remote, SHA);

            a.resetComparisons();
            List<Integer> diff = MerkleProblems.diffBlocks(a, b);
            assertEquals(64, diff.size());
            assertEquals(127, a.comparisons(), "노드 127개를 다 봤다. 전수 비교는 64번이면 됐다");
        }

        @Test
        @DisplayName("전수 비교와 답이 같다 (무작위 300회)")
        void matchesBruteForce() {
            TestSupport.Dice dice = new TestSupport.Dice(987654L);
            long treeComparisons = 0;
            long bruteForce = 0;
            for (int round = 0; round < 300; round++) {
                int n = 1 + dice.next(80);
                List<byte[]> localBlocks = dice.blocks(n);
                List<byte[]> remoteBlocks = copyOf(localBlocks);
                LinkedHashSet<Integer> changed = new LinkedHashSet<>();
                int changes = dice.next(4);
                for (int c = 0; c < changes; c++) {
                    int at = dice.next(n);
                    byte[] replacement = dice.block();
                    if (!Arrays.equals(remoteBlocks.get(at), replacement)) {
                        remoteBlocks.set(at, replacement);
                        changed.add(at);
                    }
                }
                MerkleTree local = new MerkleTree(localBlocks, SHA);
                MerkleTree remote = new MerkleTree(remoteBlocks, SHA);
                local.resetComparisons();
                assertEquals(TestSupport.naiveDiff(localBlocks, remoteBlocks),
                        MerkleProblems.diffBlocks(local, remote),
                        "n=" + n + " round=" + round + " 바꾼 자리 " + changed);
                treeComparisons += local.comparisons();
                bruteForce += n;
            }
            assertTrue(treeComparisons < bruteForce,
                    "누적 " + treeComparisons + " 대 전수 " + bruteForce);
        }
    }

    @Nested
    @DisplayName("PathCopyTest: 잎 하나를 바꾸면 경로만 바뀐다")
    class PathCopy {

        @Test
        @DisplayName("새 값은 층마다 하나씩, 나머지 노드는 같은 객체를 그대로 쓴다")
        void onlyThePathIsNew() {
            MerkleTree before = new MerkleTree(TestSupport.blocks(1024), SHA);
            MerkleTree after = before.withLeafReplaced(300, TestSupport.bytes("new block"));

            int changed = 0;
            int shared = 0;
            for (int level = 0; level <= before.height(); level++) {
                for (int i = 0; i < before.levelSize(level); i++) {
                    if (before.hashAt(level, i) == after.hashAt(level, i)) {
                        shared++;
                    } else {
                        changed++;
                    }
                }
            }
            assertEquals(11, changed, "잎 1개 + 10개의 내부 노드. 높이 + 1 이다");
            assertEquals(2036, shared, "나머지 2036개는 원본의 배열을 그대로 가리킨다");
        }

        @Test
        @DisplayName("원본은 안 바뀐다")
        void originalSurvives() {
            MerkleTree before = new MerkleTree(TestSupport.blocks(64), SHA);
            byte[] rootBefore = before.rootHash();
            MerkleTree after = before.withLeafReplaced(7, TestSupport.bytes("new"));

            assertEquals(TestSupport.hex(rootBefore), TestSupport.hex(before.rootHash()));
            assertTrue(!Arrays.equals(rootBefore, after.rootHash()), "새 트리의 뿌리는 달라야 한다");
        }

        @Test
        @DisplayName("해시 계산이 2047번에서 11번이 된다")
        void hashCallsDropToLogN() {
            TestSupport.CountingHash counter = new TestSupport.CountingHash(SHA);
            List<byte[]> blocks = TestSupport.blocks(1024);
            MerkleTree tree = new MerkleTree(blocks, counter);
            assertEquals(2047, counter.calls(), "잎 1024 + 내부 1023");

            counter.reset();
            tree.withLeafReplaced(500, TestSupport.bytes("new"));
            assertEquals(11, counter.calls(), "잎 1 + 경로 10");

            counter.reset();
            List<byte[]> rebuilt = new ArrayList<>(blocks);
            rebuilt.set(500, TestSupport.bytes("new"));
            new MerkleTree(rebuilt, counter);
            assertEquals(2047, counter.calls(), "다시 지으면 또 2047번이다");
        }

        @Test
        @DisplayName("바꾼 결과가 처음부터 다시 지은 것과 같다 (잎 1..40, 자리마다)")
        void sameAsRebuilding() {
            TestSupport.Dice dice = new TestSupport.Dice(555L);
            for (int n = 1; n <= 40; n++) {
                List<byte[]> blocks = dice.blocks(n);
                MerkleTree tree = new MerkleTree(blocks, SHA);
                for (int at = 0; at < n; at++) {
                    byte[] replacement = TestSupport.bytes("replaced-" + at);
                    List<byte[]> rebuilt = copyOf(blocks);
                    rebuilt.set(at, replacement);
                    assertEquals(TestSupport.hex(new MerkleTree(rebuilt, SHA).rootHash()),
                            TestSupport.hex(tree.withLeafReplaced(at, replacement).rootHash()),
                            "n=" + n + " at=" + at);
                }
            }
        }

        @Test
        @DisplayName("연달아 바꿔도 쌓인다")
        void chained() {
            List<byte[]> blocks = TestSupport.blocks(33);
            MerkleTree tree = new MerkleTree(blocks, SHA);
            List<byte[]> expected = copyOf(blocks);
            TestSupport.Dice dice = new TestSupport.Dice(6L);
            for (int step = 0; step < 50; step++) {
                int at = dice.next(33);
                byte[] replacement = dice.block();
                expected.set(at, replacement);
                tree = tree.withLeafReplaced(at, replacement);
            }
            assertEquals(TestSupport.hex(new MerkleTree(expected, SHA).rootHash()),
                    TestSupport.hex(tree.rootHash()));
        }

        @Test
        @DisplayName("범위 밖이거나 null 이면 예외다")
        void guards() {
            MerkleTree tree = new MerkleTree(TestSupport.blocks(8), SHA);
            assertThrows(IndexOutOfBoundsException.class,
                    () -> tree.withLeafReplaced(8, TestSupport.bytes("x")));
            assertThrows(IllegalArgumentException.class, () -> tree.withLeafReplaced(0, null));
        }
    }
}
