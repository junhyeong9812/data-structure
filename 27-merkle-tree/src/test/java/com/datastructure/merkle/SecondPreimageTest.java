package com.datastructure.merkle;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 한계 측정. 이 문제집에서 값이 제일 나가는 테스트다.
 *
 * 머클 트리의 증명은 확정적이라고 말한다. 그 말에는 조건 두 개가 숨어 있다.
 *
 * <pre>
 *   1. 잎과 내부 노드를 구별한다      -> 안 하면 두 번째 원상 공격이 성립한다
 *   2. 해시 충돌을 찾을 수 없다       -> 찾히면 전부 무너진다
 * </pre>
 *
 * 둘 다 여기서 실제로 깨본다. 코드는 그대로인데 규칙 하나, 해시 하나만 바꾼다.
 */
@DisplayName("두 번째 원상 공격과 해시 충돌")
class SecondPreimageTest {

    private static final HashFunction SHA = new Sha256Hash();

    /** 공격자가 만드는 위조 블록: 내부 노드의 입력(자식 해시 둘을 이어붙인 것) 을 블록인 척 낸다. */
    private static List<byte[]> forgedBlocks(MerkleTree honest) {
        List<byte[]> out = new ArrayList<>();
        out.add(TestSupport.concat(honest.leafHash(0), honest.leafHash(1)));
        out.add(TestSupport.concat(honest.leafHash(2), honest.leafHash(3)));
        return out;
    }

    @Nested
    @DisplayName("접두사가 없으면 공격이 성립한다")
    class WithoutPrefixes {

        private final MerkleHashing unsafe = new UnprefixedHashing(SHA);

        @Test
        @DisplayName("블록 4개짜리 파일과 블록 2개짜리 위조 파일의 뿌리가 같다")
        void differentFilesSameRoot() {
            MerkleTree honest = new MerkleTree(TestSupport.blocks(4), unsafe);
            MerkleTree forged = new MerkleTree(forgedBlocks(honest), unsafe);

            assertEquals(4, honest.leafCount());
            assertEquals(2, forged.leafCount());
            assertArrayEquals(honest.rootHash(), forged.rootHash(),
                    "블록 수도 내용도 다른데 뿌리가 같다");
            assertEquals("925a61bb3ca2f6ab3f841f0abcca50a4fa8b6c79344472a6642cba718a637126",
                    TestSupport.hex(honest.rootHash()));
        }

        @Test
        @DisplayName("있지도 않은 블록의 포함 증명이 통과한다")
        void forgedInclusionProofVerifies() {
            MerkleTree honest = new MerkleTree(TestSupport.blocks(4), unsafe);
            byte[] realRoot = honest.rootHash();
            List<byte[]> forgedList = forgedBlocks(honest);
            MerkleTree forged = new MerkleTree(forgedList, unsafe);

            // 원래 파일에 저런 64바이트 블록은 없다.
            for (byte[] real : TestSupport.blocks(4)) {
                assertFalse(Arrays.equals(real, forgedList.get(0)));
            }

            MerkleProof proof = forged.proofFor(0);
            assertTrue(proof.verify(forged.leafHash(0), realRoot),
                    "진짜 뿌리에 대해 가짜 블록의 증명이 통과했다. 이 자료구조의 유일한 보장이 깨졌다");
            assertTrue(MerkleProblems.verifyBatch(realRoot, List.of(proof),
                    List.of(forged.leafHash(0))));
        }

        @Test
        @DisplayName("근원은 한 줄이다. leafHash(x || y) 와 nodeHash(x, y) 가 같다")
        void leafAndNodeLiveInTheSameSpace() {
            byte[] x = SHA.hash(TestSupport.bytes("왼쪽"));
            byte[] y = SHA.hash(TestSupport.bytes("오른쪽"));
            assertArrayEquals(unsafe.nodeHash(x, y), unsafe.leafHash(TestSupport.concat(x, y)),
                    "내부 노드와 잎이 같은 값의 공간에 산다");
        }
    }

    @Nested
    @DisplayName("접두사 한 바이트가 그것을 막는다")
    class WithPrefixes {

        private final MerkleHashing safe = new PrefixedHashing(SHA);

        @Test
        @DisplayName("같은 위조를 시도하면 뿌리가 다르다")
        void forgeryFails() {
            MerkleTree honest = new MerkleTree(TestSupport.blocks(4), safe);
            MerkleTree forged = new MerkleTree(forgedBlocks(honest), safe);

            assertFalse(Arrays.equals(honest.rootHash(), forged.rootHash()),
                    "0x00 과 0x01 이 두 공간을 갈라놓는다");
            assertEquals("5949564baf1cfbc22db821e7734df0d7f3e7decd69ad842f2e0a22ed8af13b1b",
                    TestSupport.hex(honest.rootHash()));
            assertEquals("9dd377cc5081350a272d0e8af54a90b8b07103d2ad776b4935e5ff1c35868c26",
                    TestSupport.hex(forged.rootHash()));
        }

        @Test
        @DisplayName("위조 증명이 진짜 뿌리를 통과하지 못한다")
        void forgedProofIsRejected() {
            MerkleTree honest = new MerkleTree(TestSupport.blocks(4), safe);
            MerkleTree forged = new MerkleTree(forgedBlocks(honest), safe);
            assertFalse(forged.proofFor(0).verify(forged.leafHash(0), honest.rootHash()));
        }

        @Test
        @DisplayName("잎 공간과 내부 공간이 겹치지 않는다")
        void spacesAreDisjoint() {
            byte[] x = SHA.hash(TestSupport.bytes("왼쪽"));
            byte[] y = SHA.hash(TestSupport.bytes("오른쪽"));
            assertNotEquals(TestSupport.hex(safe.nodeHash(x, y)),
                    TestSupport.hex(safe.leafHash(TestSupport.concat(x, y))),
                    "같은 64바이트를 넣어도 앞의 한 바이트가 달라 값이 갈린다");
        }
    }

    @Nested
    @DisplayName("해시가 무너지면 규칙은 아무것도 못 막는다")
    class BrokenHash {

        private final MerkleHashing toy = new PrefixedHashing(new ToyHash());

        @Test
        @DisplayName("ToyHash 에서는 손으로 충돌을 만든다")
        void collisionsAreTrivial() {
            HashFunction toyFunction = new ToyHash();
            assertArrayEquals(toyFunction.hash(new byte[] {1, 2}), toyFunction.hash(new byte[] {2, 1}));
            assertArrayEquals(toyFunction.hash(new byte[] {1, 2}), toyFunction.hash(new byte[] {3, 0}));
        }

        @Test
        @DisplayName("서로 다른 파일이 같은 뿌리를 갖는다")
        void differentContentSameRoot() {
            List<byte[]> local = List.of(new byte[] {1, 2}, new byte[] {7});
            List<byte[]> remote = List.of(new byte[] {2, 1}, new byte[] {7});
            MerkleTree a = new MerkleTree(local, toy);
            MerkleTree b = new MerkleTree(remote, toy);

            assertFalse(Arrays.equals(local.get(0), remote.get(0)), "블록은 분명히 다르다");
            assertArrayEquals(a.rootHash(), b.rootHash(), "그런데 뿌리가 같다");
        }

        @Test
        @DisplayName("차이 찾기가 '같다'고 답한다")
        void diffGoesBlind() {
            List<byte[]> local = List.of(new byte[] {1, 2}, new byte[] {7});
            List<byte[]> remote = List.of(new byte[] {2, 1}, new byte[] {7});
            MerkleTree a = new MerkleTree(local, toy);
            MerkleTree b = new MerkleTree(remote, toy);

            assertEquals(-1, a.findFirstDifference(b), "다른 파일인데 -1 이 나온다");
            assertEquals(List.of(), MerkleProblems.diffBlocks(a, b));
            assertEquals(List.of(0), TestSupport.naiveDiff(local, remote),
                    "전수 비교는 0번이 다르다고 정확히 답한다");
        }

        @Test
        @DisplayName("남의 블록에 대한 증명이 통과한다")
        void proofsBecomeMeaningless() {
            List<byte[]> local = List.of(new byte[] {1, 2}, new byte[] {7});
            MerkleTree a = new MerkleTree(local, toy);
            MerkleProof proof = a.proofFor(0);
            byte[] otherLeaf = toy.leafHash(new byte[] {3, 0});
            assertTrue(proof.verify(otherLeaf, a.rootHash()),
                    "파일에 없는 블록이 들어 있다고 증명된다");
        }

        @Test
        @DisplayName("SHA-256 에서는 같은 시도가 통하지 않는다")
        void sha256Separates() {
            MerkleHashing safe = new PrefixedHashing(SHA);
            MerkleTree a = new MerkleTree(List.of(new byte[] {1, 2}, new byte[] {7}), safe);
            MerkleTree b = new MerkleTree(List.of(new byte[] {2, 1}, new byte[] {7}), safe);
            assertFalse(Arrays.equals(a.rootHash(), b.rootHash()));
            assertEquals(0, a.findFirstDifference(b));
        }
    }
}
