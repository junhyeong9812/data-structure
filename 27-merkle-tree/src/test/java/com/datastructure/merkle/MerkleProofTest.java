package com.datastructure.merkle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * 포함 증명. 이 문제집에서 가장 중요한 테스트가 여기 있다.
 *
 * "맞는 증명이 통과한다"는 절반이다. 나머지 절반은 "틀린 증명이 거부된다"이고,
 * 그게 안 되면 이 자료구조는 아무 쓸모가 없다. 통과만 시키는 verify 는
 * `return true;` 한 줄로도 만들 수 있다.
 */
@DisplayName("포함 증명")
class MerkleProofTest {

    private static final HashFunction SHA = new Sha256Hash();

    private static MerkleTree treeOf(int n) {
        return new MerkleTree(TestSupport.blocks(n), SHA);
    }

    private static MerkleProof rebuilt(MerkleProof proof, List<MerkleProof.Step> steps) {
        return new MerkleProof(steps, proof.hashing());
    }

    @Nested
    @DisplayName("맞는 증명은 통과한다")
    class Accepts {

        @Test
        @DisplayName("파이썬으로 계산한 걸음과 값도 방향도 같다")
        void matchesPython() {
            MerkleProof proof = treeOf(4).proofFor(1);
            assertEquals(2, proof.size());
            assertEquals("0b2757c993480daf890041bdfca455aa881e13b978812267045f74dc45a7f23c",
                    TestSupport.hex(proof.steps().get(0).siblingHash()));
            assertTrue(proof.steps().get(0).siblingIsLeft(), "1번 잎의 형제(0번) 는 왼쪽이다");
            assertEquals("d3b112c58cbc8d182654c2f12ffd87f17d2504df313809354858a527bade23d0",
                    TestSupport.hex(proof.steps().get(1).siblingHash()));
            assertFalse(proof.steps().get(1).siblingIsLeft(), "1층의 형제는 오른쪽이다");
        }

        @Test
        @DisplayName("모든 잎의 증명이 통과한다 (잎 1..40)")
        void everyLeaf() {
            TestSupport.Dice dice = new TestSupport.Dice(2718L);
            for (int n = 1; n <= 40; n++) {
                MerkleTree tree = new MerkleTree(dice.blocks(n), SHA);
                byte[] root = tree.rootHash();
                for (int i = 0; i < n; i++) {
                    assertTrue(tree.proofFor(i).verify(tree.leafHash(i), root),
                            "잎 " + n + "개 중 " + i + "번");
                }
            }
        }

        @Test
        @DisplayName("잎이 하나면 걸음이 0개이고 잎 해시가 곧 뿌리다")
        void singleLeafProofIsEmpty() {
            MerkleTree tree = treeOf(1);
            MerkleProof proof = tree.proofFor(0);
            assertEquals(0, proof.size());
            assertTrue(proof.verify(tree.leafHash(0), tree.rootHash()));
        }

        @Test
        @DisplayName("검증하는 쪽은 트리가 없어도 된다. 블록에서 잎 해시를 직접 계산한다")
        void verifierNeedsOnlyTheBlock() {
            MerkleTree tree = treeOf(16);
            byte[] root = tree.rootHash();
            MerkleProof proof = tree.proofFor(11);

            // 검증자가 가진 것: 블록 하나, 증명, 뿌리. 파일 나머지 15블록은 안 본다.
            MerkleHashing rule = new PrefixedHashing(SHA);
            byte[] leafHash = rule.leafHash(TestSupport.bytes("block-11"));
            assertTrue(proof.verify(leafHash, root));
        }
    }

    @Nested
    @DisplayName("틀린 증명은 거부한다 (이게 안 되면 전부 무의미하다)")
    class Rejects {

        private final MerkleTree tree = treeOf(16);
        private final byte[] root = tree.rootHash();
        private final MerkleProof proof = tree.proofFor(5);
        private final byte[] leaf = tree.leafHash(5);

        @Test
        @DisplayName("형제 해시를 한 비트 바꾸면 거부한다")
        void tamperedSiblingHash() {
            List<MerkleProof.Step> steps = new ArrayList<>(proof.steps());
            byte[] hacked = steps.get(1).siblingHash();
            hacked[0] ^= 1;
            steps.set(1, new MerkleProof.Step(hacked, steps.get(1).siblingIsLeft()));
            assertFalse(rebuilt(proof, steps).verify(leaf, root), "한 비트만 달라도 뿌리가 안 맞는다");
        }

        @Test
        @DisplayName("좌우 방향을 뒤집으면 거부한다")
        void flippedDirection() {
            for (int i = 0; i < proof.size(); i++) {
                List<MerkleProof.Step> steps = new ArrayList<>(proof.steps());
                steps.set(i, steps.get(i).flipped());
                assertFalse(rebuilt(proof, steps).verify(leaf, root),
                        i + "번 걸음의 방향을 뒤집었는데 통과했다");
            }
        }

        @Test
        @DisplayName("걸음을 하나 빼면 거부한다")
        void truncatedProof() {
            List<MerkleProof.Step> steps = new ArrayList<>(proof.steps());
            steps.remove(steps.size() - 1);
            assertEquals(3, steps.size());
            assertFalse(rebuilt(proof, steps).verify(leaf, root), "짧은 증명은 다른 층의 값을 뿌리라고 주장한다");
        }

        @Test
        @DisplayName("걸음을 하나 더 붙여도 거부한다")
        void extendedProof() {
            List<MerkleProof.Step> steps = new ArrayList<>(proof.steps());
            steps.add(new MerkleProof.Step(tree.leafHash(0), false));
            assertFalse(rebuilt(proof, steps).verify(leaf, root));
        }

        @Test
        @DisplayName("걸음 순서를 바꾸면 거부한다")
        void reorderedProof() {
            List<MerkleProof.Step> steps = new ArrayList<>(proof.steps());
            MerkleProof.Step first = steps.get(0);
            steps.set(0, steps.get(1));
            steps.set(1, first);
            assertFalse(rebuilt(proof, steps).verify(leaf, root), "순서가 곧 경로다");
        }

        @Test
        @DisplayName("남의 잎으로는 통과하지 않는다")
        void wrongLeaf() {
            for (int i = 0; i < 16; i++) {
                if (i == 5) {
                    continue;
                }
                assertFalse(proof.verify(tree.leafHash(i), root), i + "번 잎이 5번 자리를 통과했다");
            }
        }

        @Test
        @DisplayName("남의 뿌리로는 통과하지 않는다")
        void wrongRoot() {
            assertFalse(proof.verify(leaf, treeOf(16, "other-").rootHash()));
            assertFalse(proof.verify(leaf, tree.hashAt(1, 2)), "중간층 노드를 뿌리라고 줘도 안 된다");
        }

        @Test
        @DisplayName("뿌리의 어느 한 바이트가 달라도 거부한다")
        void everyByteOfTheRootMatters() {
            // 검증은 전부 보거나 안 보거나 둘 중 하나다.
            // 앞 몇 바이트만 비교하는 verify 는 나머지 위조 테스트를 전부 통과한다.
            // 변종으로 확인했다. 앞 4바이트만 보는 구현이 이 테스트 없이는 78개를 다 통과한다.
            assertEquals(32, root.length);
            for (int i = 0; i < root.length; i++) {
                byte[] almost = root.clone();
                almost[i] ^= 1;
                assertFalse(proof.verify(leaf, almost), i + "번 바이트만 달랐는데 통과했다");
            }
        }

        @Test
        @DisplayName("블록을 한 바이트 고치면 옛 증명이 죽는다")
        void tamperedBlock() {
            MerkleHashing rule = new PrefixedHashing(SHA);
            byte[] tampered = rule.leafHash(TestSupport.bytes("block-5!"));
            assertFalse(proof.verify(tampered, root));
        }

        @Test
        @DisplayName("null 은 예외다")
        void nullArguments() {
            assertThrows(IllegalArgumentException.class, () -> proof.verify(null, root));
            assertThrows(IllegalArgumentException.class, () -> proof.verify(leaf, null));
            assertThrows(IllegalArgumentException.class, () -> new MerkleProof(null, proof.hashing()));
            assertThrows(IllegalArgumentException.class, () -> new MerkleProof(proof.steps(), null));
        }
    }

    private static MerkleTree treeOf(int n, String prefix) {
        return new MerkleTree(TestSupport.blocks(n, prefix), SHA);
    }

    @Nested
    @DisplayName("한계 측정: 증명 크기는 log n 이다")
    class ProofSize {

        @Test
        @DisplayName("잎 8개면 3걸음, 1024개면 10걸음")
        void grows() {
            MerkleTree eight = treeOf(8);
            for (int i = 0; i < 8; i++) {
                assertEquals(3, eight.proofFor(i).size());
            }

            MerkleTree big = treeOf(1024);
            for (int i = 0; i < 1024; i++) {
                assertEquals(10, big.proofFor(i).size(), i + "번 잎");
            }

            int bytes = 0;
            for (MerkleProof.Step step : big.proofFor(0).steps()) {
                bytes += step.siblingHash().length;
            }
            assertEquals(320, bytes, "SHA-256 이니 10 * 32 바이트다. 파일은 안 보낸다");
        }

        @Test
        @DisplayName("잎이 128배가 되는 동안 증명은 7걸음 는다")
        void logarithmic() {
            assertEquals(3, treeOf(8).proofFor(0).size());
            assertEquals(6, treeOf(64).proofFor(0).size());
            assertEquals(9, treeOf(512).proofFor(0).size());
            assertEquals(10, treeOf(1024).proofFor(0).size());
        }

        @Test
        @Timeout(60)
        @DisplayName("잎 100만개에서도 20걸음이면 끝난다")
        void oneMillionLeaves() {
            // 여기서는 ToyHash 를 쓴다. 재는 것이 걸음 수라 해시의 품질이 필요 없고,
            // SHA-256 을 200만 번 부르면 이 테스트만 몇 초를 먹는다.
            List<byte[]> blocks = new ArrayList<>(1_000_000);
            for (int i = 0; i < 1_000_000; i++) {
                blocks.add(TestSupport.bytes("b" + i));
            }
            MerkleTree tree = new MerkleTree(blocks, new ToyHash());
            assertEquals(1_000_000, tree.leafCount());
            assertEquals(20, tree.height());

            int max = 0;
            for (int i = 0; i < 1_000_000; i += 9973) {
                max = Math.max(max, tree.proofFor(i).size());
            }
            // SHA-256 이었다면 20 * 32 = 640바이트다. 블록이 4KB 라면 파일은 4GB 인데
            // 그중 한 블록이 들어 있다는 증명이 640바이트다. 파일 크기와 무관하다.
            assertEquals(20, max, "블록 100만 개짜리 파일의 포함 증명이 해시 20개다");
        }
    }
}
