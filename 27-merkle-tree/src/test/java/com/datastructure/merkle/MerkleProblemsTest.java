package com.datastructure.merkle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 응용 둘. 실무가 이 자료구조를 쓰는 두 모양이다.
 *
 * <pre>
 *   diffBlocks    다른 블록만 골라낸다        rsync 의 델타 전송, 카산드라의 anti-entropy 복구
 *   verifyBatch   증명 여러 개를 한 번에      인증서 투명성 로그를 감시하는 쪽
 * </pre>
 */
@DisplayName("머클 트리로 푸는 문제")
class MerkleProblemsTest {

    private static final HashFunction SHA = new Sha256Hash();

    private static MerkleTree treeOf(int n) {
        return new MerkleTree(TestSupport.blocks(n), SHA);
    }

    @Nested
    @DisplayName("문제 1: diffBlocks")
    class DiffBlocks {

        @Test
        @DisplayName("같으면 빈 목록이다")
        void identical() {
            assertEquals(List.of(), MerkleProblems.diffBlocks(treeOf(32), treeOf(32)));
        }

        @Test
        @DisplayName("답이 오름차순이다")
        void ascending() {
            List<byte[]> remote = TestSupport.blocks(32);
            for (int at : new int[] {31, 0, 16, 3}) {
                remote.set(at, TestSupport.bytes("x" + at));
            }
            assertEquals(List.of(0, 3, 16, 31),
                    MerkleProblems.diffBlocks(treeOf(32), new MerkleTree(remote, SHA)),
                    "왼쪽 자식을 먼저 내려가면 정렬이 공짜다");
        }

        @Test
        @DisplayName("승격이 섞인 모양에서도 맞는다 (잎 1..50)")
        void oddShapes() {
            TestSupport.Dice dice = new TestSupport.Dice(76543L);
            for (int n = 1; n <= 50; n++) {
                List<byte[]> localBlocks = dice.blocks(n);
                List<byte[]> remoteBlocks = new ArrayList<>(localBlocks);
                for (int at = 0; at < n; at += 3) {
                    remoteBlocks.set(at, TestSupport.bytes("changed-" + at));
                }
                assertEquals(TestSupport.naiveDiff(localBlocks, remoteBlocks),
                        MerkleProblems.diffBlocks(new MerkleTree(localBlocks, SHA),
                                new MerkleTree(remoteBlocks, SHA)),
                        "n=" + n);
            }
        }

        @Test
        @DisplayName("잎 개수가 다르거나 null 이면 예외다")
        void guards() {
            MerkleTree local = treeOf(8);
            assertThrows(IllegalArgumentException.class,
                    () -> MerkleProblems.diffBlocks(local, treeOf(7)));
            assertThrows(IllegalArgumentException.class,
                    () -> MerkleProblems.diffBlocks(local, null));
            assertThrows(IllegalArgumentException.class,
                    () -> MerkleProblems.diffBlocks(null, local));
        }
    }

    @Nested
    @DisplayName("문제 2: verifyBatch")
    class VerifyBatch {

        private final MerkleTree tree = treeOf(64);
        private final byte[] root = tree.rootHash();

        private List<MerkleProof> proofsFor(int... indexes) {
            List<MerkleProof> out = new ArrayList<>();
            for (int i : indexes) {
                out.add(tree.proofFor(i));
            }
            return out;
        }

        private List<byte[]> leavesOf(int... indexes) {
            List<byte[]> out = new ArrayList<>();
            for (int i : indexes) {
                out.add(tree.leafHash(i));
            }
            return out;
        }

        @Test
        @DisplayName("맞는 증명 여럿이면 true")
        void allValid() {
            assertTrue(MerkleProblems.verifyBatch(root, proofsFor(0, 7, 31, 63),
                    leavesOf(0, 7, 31, 63)));
        }

        @Test
        @DisplayName("64개 전부를 한 번에")
        void everyLeafAtOnce() {
            int[] all = new int[64];
            for (int i = 0; i < 64; i++) {
                all[i] = i;
            }
            assertTrue(MerkleProblems.verifyBatch(root, proofsFor(all), leavesOf(all)));
        }

        @Test
        @DisplayName("하나만 틀려도 false")
        void oneBadApple() {
            List<byte[]> leaves = leavesOf(0, 7, 31, 63);
            leaves.set(2, tree.leafHash(30));
            assertFalse(MerkleProblems.verifyBatch(root, proofsFor(0, 7, 31, 63), leaves),
                    "세 개가 맞아도 통과가 아니다. 이 계약에는 부분 점수가 없다");
        }

        @Test
        @DisplayName("짝이 어긋나면 false")
        void mismatchedPairing() {
            assertFalse(MerkleProblems.verifyBatch(root, proofsFor(0, 7), leavesOf(7, 0)),
                    "증명과 잎을 뒤바꿔 넣으면 안 맞는다");
        }

        @Test
        @DisplayName("남의 뿌리면 false")
        void wrongRoot() {
            byte[] otherRoot = new MerkleTree(TestSupport.blocks(64, "other-"), SHA).rootHash();
            assertFalse(MerkleProblems.verifyBatch(otherRoot, proofsFor(0, 7), leavesOf(0, 7)));
        }

        @Test
        @DisplayName("걸음을 고친 증명이 섞이면 false")
        void tamperedProof() {
            List<MerkleProof> proofs = proofsFor(0, 7);
            List<MerkleProof.Step> steps = new ArrayList<>(proofs.get(1).steps());
            steps.set(0, steps.get(0).flipped());
            proofs.set(1, new MerkleProof(steps, tree.hashing()));
            assertFalse(MerkleProblems.verifyBatch(root, proofs, leavesOf(0, 7)));
        }

        @Test
        @DisplayName("빈 배치는 true 다. 아무것도 증명하지 않았을 뿐이다")
        void emptyBatch() {
            // 정직하게 적어둔다. "전부 통과했다"와 "검증할 것이 없었다"가 같은 값으로 나온다.
            // 부르는 쪽이 개수를 따로 확인해야 한다. 자료구조가 대신 정해줄 수 없다.
            assertTrue(MerkleProblems.verifyBatch(root, List.of(), List.of()));
        }

        @Test
        @DisplayName("개수가 다르거나 null 이 섞이면 예외다")
        void guards() {
            assertThrows(IllegalArgumentException.class,
                    () -> MerkleProblems.verifyBatch(root, proofsFor(0, 7), leavesOf(0)));
            assertThrows(IllegalArgumentException.class,
                    () -> MerkleProblems.verifyBatch(null, proofsFor(0), leavesOf(0)));
            assertThrows(IllegalArgumentException.class,
                    () -> MerkleProblems.verifyBatch(root, null, leavesOf(0)));
            assertThrows(IllegalArgumentException.class,
                    () -> MerkleProblems.verifyBatch(root, proofsFor(0), null));
            assertThrows(IllegalArgumentException.class, () -> MerkleProblems.verifyBatch(
                    root, Collections.singletonList(null), leavesOf(0)));
            assertThrows(IllegalArgumentException.class, () -> MerkleProblems.verifyBatch(
                    root, proofsFor(0), Collections.singletonList(null)));
        }

        @Test
        @DisplayName("배치라고 해시 계산이 줄지는 않는다")
        void batchingSavesRoundTripsNotHashes() {
            // 이름 때문에 뭔가 합쳐지는 것처럼 들리는데 아니다.
            // 증명 하나당 log n 번씩 그대로 든다. 줄어드는 것은 왕복이지 계산이 아니다.
            TestSupport.CountingHash counter = new TestSupport.CountingHash(SHA);
            MerkleTree counted = new MerkleTree(TestSupport.blocks(64), counter);
            List<MerkleProof> proofs = new ArrayList<>();
            List<byte[]> leaves = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                proofs.add(counted.proofFor(i));
                leaves.add(counted.leafHash(i));
            }
            byte[] countedRoot = counted.rootHash();

            counter.reset();
            assertTrue(MerkleProblems.verifyBatch(countedRoot, proofs, leaves));
            assertEquals(48, counter.calls(), "증명 8개 * 6걸음 = 48번. 하나씩 부를 때와 같다");
        }
    }

    @Nested
    @DisplayName("둘을 이어 붙이면 동기화 한 판이 된다")
    class SyncScenario {

        @Test
        @DisplayName("다른 블록만 받아 고치면 뿌리가 맞는다")
        void repairOnlyWhatDiffers() {
            // 상대(remote) 가 정본이고 내(local) 것이 세 군데 상했다.
            // 1. 다른 블록을 찾는다      -> 노드 몇 개만 본다
            // 2. 그 블록만 받아 고친다   -> 경로만 다시 계산한다
            // 3. 뿌리가 맞으면 끝났다
            List<byte[]> truth = TestSupport.blocks(256);
            List<byte[]> mine = new ArrayList<>(truth);
            int[] broken = {5, 100, 255};
            for (int at : broken) {
                mine.set(at, TestSupport.bytes("corrupted-" + at));
            }

            MerkleTree remote = new MerkleTree(truth, SHA);
            MerkleTree local = new MerkleTree(mine, SHA);
            local.resetComparisons();
            List<Integer> diff = MerkleProblems.diffBlocks(local, remote);
            assertEquals(List.of(5, 100, 255), diff);
            long lookups = local.comparisons();

            TestSupport.CountingHash counter = new TestSupport.CountingHash(SHA);
            MerkleTree repaired = new MerkleTree(mine, counter);
            counter.reset();
            for (int at : diff) {
                repaired = repaired.withLeafReplaced(at, truth.get(at));
            }
            assertTrue(Arrays.equals(remote.rootHash(), repaired.rootHash()), "이제 같은 파일이다");
            assertEquals(27, counter.calls(), "고치는 데 든 해시 계산. 3블록 * (1 + 8층)");
            assertTrue(lookups < 60, "찾는 데 본 노드 " + lookups + "개. 전수는 256개다");
        }
    }
}
