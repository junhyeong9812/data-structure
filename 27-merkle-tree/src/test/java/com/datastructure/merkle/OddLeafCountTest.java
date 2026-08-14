package com.datastructure.merkle;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 잎이 2의 거듭제곱이 아닐 때의 규칙을 못 박는다.
 *
 * 이 문제집은 짝 없는 마지막 노드를 그대로 올린다(승격).
 * 다른 방법은 마지막 노드를 자기 자신과 짝지어 해시하는 것이고(비트코인),
 * 그쪽에는 CVE-2012-2459 가 있다.
 *
 * 규칙을 고른 것만으로는 아무것도 못 박은 것이 아니다. 값이 어떻게 갈리는지를 적어야
 * 나중에 누가 반대쪽으로 고쳤을 때 테스트가 운다.
 */
@DisplayName("홀수 잎 규칙과 CVE-2012-2459")
class OddLeafCountTest {

    private static final HashFunction SHA = new Sha256Hash();
    private static final MerkleHashing HASHING = new PrefixedHashing(SHA);

    /** [block-0, block-1, block-2] 와 그 마지막을 한 번 더 붙인 [.., block-2]. */
    private static List<byte[]> three() {
        return TestSupport.blocks(3);
    }

    private static List<byte[]> threePlusDuplicate() {
        List<byte[]> out = new ArrayList<>(three());
        out.add(TestSupport.bytes("block-2"));
        return out;
    }

    @Nested
    @DisplayName("승격 규칙")
    class Promotion {

        @Test
        @DisplayName("짝 없는 노드는 값이 안 바뀐 채 위로 간다")
        void promotedValueIsUnchanged() {
            MerkleTree tree = new MerkleTree(three(), SHA);
            assertArrayEquals(tree.hashAt(0, 2), tree.hashAt(1, 1));
            assertArrayEquals(HASHING.nodeHash(tree.hashAt(1, 0), tree.hashAt(1, 1)), tree.rootHash());
        }

        @Test
        @DisplayName("승격된 자리는 증명이 한 걸음 짧다")
        void promotionShortensTheProof() {
            // 높이는 2인데 2번 잎의 증명은 한 걸음뿐이다. 0층에 형제가 없기 때문이다.
            // 증명 길이가 잎마다 다르다는 뜻이고, 그 길이 자체가 어느 잎인지를 조금 흘린다.
            MerkleTree tree = new MerkleTree(three(), SHA);
            assertEquals(2, tree.height());
            assertEquals(2, tree.proofFor(0).size());
            assertEquals(2, tree.proofFor(1).size());
            assertEquals(1, tree.proofFor(2).size(), "승격된 잎은 한 걸음");
            assertTrue(tree.proofFor(2).verify(tree.leafHash(2), tree.rootHash()));
        }

        @Test
        @DisplayName("승격이 있어도 모든 잎의 증명이 통과한다 (잎 1..64)")
        void allShapesVerify() {
            TestSupport.Dice dice = new TestSupport.Dice(99L);
            for (int n = 1; n <= 64; n++) {
                MerkleTree tree = new MerkleTree(dice.blocks(n), SHA);
                byte[] root = tree.rootHash();
                for (int i = 0; i < n; i++) {
                    assertTrue(tree.proofFor(i).verify(tree.leafHash(i), root),
                            "잎 " + n + "개 중 " + i + "번이 실패했다");
                }
            }
        }
    }

    @Nested
    @DisplayName("CVE-2012-2459: 비트코인 방식이면 서로 다른 파일이 같은 뿌리를 낸다")
    class DuplicationAttack {

        @Test
        @DisplayName("자기 자신과 짝지어 올리면 [a,b,c] 와 [a,b,c,c] 가 구별되지 않는다")
        void duplicatingRuleCollides() {
            // 참조 구현으로 그 규칙을 그대로 계산해 본다. 우리 트리는 이 규칙을 쓰지 않는다.
            byte[] threeUnderDuplication = TestSupport.duplicatingRoot(three(), HASHING);
            byte[] fourUnderDuplication = TestSupport.duplicatingRoot(threePlusDuplicate(), HASHING);
            assertArrayEquals(threeUnderDuplication, fourUnderDuplication,
                    "블록 3개와 4개가 같은 뿌리를 낸다. 이것이 CVE-2012-2459 다");
            assertEquals("75eed16aa139f33cced2aa6ca403fec3f4cbdc261658986bec97d3eb8524d55f",
                    TestSupport.hex(threeUnderDuplication));
        }

        @Test
        @DisplayName("승격 규칙에서는 둘이 다르다")
        void promotionRuleSeparatesThem() {
            byte[] three = new MerkleTree(three(), SHA).rootHash();
            byte[] four = new MerkleTree(threePlusDuplicate(), SHA).rootHash();
            assertFalse(Arrays.equals(three, four), "블록 수가 다르면 뿌리도 달라야 한다");
            assertEquals("ff3f4439151a46df84d8baaa28e4b8b6dff312f76f82920a4b68c8712e2ec0a6",
                    TestSupport.hex(three));
            assertEquals("75eed16aa139f33cced2aa6ca403fec3f4cbdc261658986bec97d3eb8524d55f",
                    TestSupport.hex(four),
                    "4블록 트리의 뿌리는 비트코인 방식 3블록의 뿌리와 같다. 규칙 하나가 그 자리를 옮긴 것뿐이다");
        }

        @Test
        @DisplayName("잎 수가 홀수인 모든 모양에서 두 규칙이 갈린다 (3, 5, 7, 9, 11)")
        void theTwoRulesDisagreeWhereverThereIsAnOddLevel() {
            TestSupport.Dice dice = new TestSupport.Dice(5150L);
            for (int n : new int[] {3, 5, 6, 7, 9, 11}) {
                List<byte[]> blocks = dice.blocks(n);
                byte[] ours = new MerkleTree(blocks, SHA).rootHash();
                byte[] bitcoin = TestSupport.duplicatingRoot(blocks, HASHING);
                assertFalse(Arrays.equals(ours, bitcoin), "n=" + n + " 에서 두 규칙이 같은 값을 냈다");
            }
        }

        @Test
        @DisplayName("잎 수가 2의 거듭제곱이면 두 규칙이 같다")
        void thePowerOfTwoCaseIsIdentical() {
            // 승격이 한 번도 안 일어나므로 규칙이 갈릴 자리가 없다.
            // 그래서 8블록짜리 테스트만 돌리면 이 취약점이 보이지 않는다.
            TestSupport.Dice dice = new TestSupport.Dice(31L);
            for (int n : new int[] {1, 2, 4, 8, 16}) {
                List<byte[]> blocks = dice.blocks(n);
                assertArrayEquals(TestSupport.duplicatingRoot(blocks, HASHING),
                        new MerkleTree(blocks, SHA).rootHash(), "n=" + n);
            }
        }
    }
}
