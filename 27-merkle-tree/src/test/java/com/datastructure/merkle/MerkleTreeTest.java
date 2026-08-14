package com.datastructure.merkle;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
 * 트리를 짓는 계약.
 *
 * 기댓값 hex 는 파이썬 hashlib 으로 계산한 것이다. 규칙은 이렇다.
 *
 * <pre>
 *   leaf(block)       = sha256(0x00 || block)
 *   node(left, right) = sha256(0x01 || left || right)
 *   홀수 층의 마지막 노드는 그대로 올린다
 * </pre>
 *
 * 자바가 이 값을 그대로 내야 한다. 자바가 낸 값을 기댓값으로 적으면
 * 자기 자신과 비교하는 것이라 아무것도 검증하지 않는다.
 */
@DisplayName("머클 트리 짓기")
class MerkleTreeTest {

    private static final HashFunction SHA = new Sha256Hash();

    private static MerkleTree treeOf(int n) {
        return new MerkleTree(TestSupport.blocks(n), SHA);
    }

    @Nested
    @DisplayName("생성 계약")
    class Construction {

        @Test
        @DisplayName("블록이 없으면 만들 수 없다")
        void rejectsEmpty() {
            // 잎이 0개인 트리는 뿌리가 없다. "빈 것의 해시"를 자료구조가 몰래 정하지 않는다.
            assertThrows(IllegalArgumentException.class,
                    () -> new MerkleTree(Collections.emptyList(), SHA));
            assertThrows(IllegalArgumentException.class, () -> new MerkleTree(null, SHA));
        }

        @Test
        @DisplayName("블록 하나가 null 이면 거부한다")
        void rejectsNullBlock() {
            List<byte[]> blocks = TestSupport.blocks(4);
            blocks.set(2, null);
            assertThrows(IllegalArgumentException.class, () -> new MerkleTree(blocks, SHA));
        }

        @Test
        @DisplayName("해시 규칙이 없으면 거부한다")
        void rejectsNullHashing() {
            assertThrows(IllegalArgumentException.class,
                    () -> new MerkleTree(TestSupport.blocks(4), (MerkleHashing) null));
            assertThrows(IllegalArgumentException.class,
                    () -> new MerkleTree(TestSupport.blocks(4), (HashFunction) null));
        }
    }

    @Nested
    @DisplayName("뿌리 해시 (파이썬 대조)")
    class RootHash {

        @Test
        @DisplayName("잎이 하나면 뿌리가 곧 잎 해시다")
        void singleLeaf() {
            MerkleTree tree = treeOf(1);
            assertEquals(1, tree.leafCount());
            assertEquals(0, tree.height());
            assertArrayEquals(tree.leafHash(0), tree.rootHash());
            assertEquals("0b2757c993480daf890041bdfca455aa881e13b978812267045f74dc45a7f23c",
                    TestSupport.hex(tree.rootHash()));
        }

        @Test
        @DisplayName("잎 1, 2, 3, 4, 5, 7, 8 개의 뿌리")
        void knownRoots() {
            assertEquals("0b2757c993480daf890041bdfca455aa881e13b978812267045f74dc45a7f23c",
                    TestSupport.hex(treeOf(1).rootHash()), "n=1");
            assertEquals("c75852b44920001ea7989d913afd9d1a7807a6302ba1301cfc35ebdcd9a04f00",
                    TestSupport.hex(treeOf(2).rootHash()), "n=2");
            assertEquals("ff3f4439151a46df84d8baaa28e4b8b6dff312f76f82920a4b68c8712e2ec0a6",
                    TestSupport.hex(treeOf(3).rootHash()), "n=3 (홀수라 승격이 있다)");
            assertEquals("5949564baf1cfbc22db821e7734df0d7f3e7decd69ad842f2e0a22ed8af13b1b",
                    TestSupport.hex(treeOf(4).rootHash()), "n=4");
            assertEquals("46de7f90e1691588fd5db2829716b30cd32b171451ec5b7b487d41b9345d5059",
                    TestSupport.hex(treeOf(5).rootHash()), "n=5");
            assertEquals("933f3e6303558d949c1dab9109c944e7b17ca7678665689a096607e4ae04a06c",
                    TestSupport.hex(treeOf(7).rootHash()), "n=7");
            assertEquals("59bc3f55d7a133fc9793735aedbeec0816add1e0333bd584fc96774825d67bdc",
                    TestSupport.hex(treeOf(8).rootHash()), "n=8");
        }

        @Test
        @DisplayName("잎 해시는 블록에 0x00 을 붙여 해시한 값이다")
        void leafHashesArePrefixed() {
            MerkleTree tree = treeOf(4);
            byte[] expected = SHA.hash(TestSupport.concat(
                    new byte[] {MerkleHashing.LEAF_PREFIX}, TestSupport.bytes("block-2")));
            assertArrayEquals(expected, tree.leafHash(2));
            assertEquals("30f234610b8acfc107b5cd5e33268ae8e9617cc3af1eb6a516fe76d2a340f4cc",
                    TestSupport.hex(tree.leafHash(2)));
        }

        @Test
        @DisplayName("같은 블록이면 언제나 같은 뿌리, 한 바이트만 달라도 다른 뿌리")
        void deterministicAndSensitive() {
            assertArrayEquals(treeOf(16).rootHash(), treeOf(16).rootHash());

            List<byte[]> changed = TestSupport.blocks(16);
            changed.set(9, TestSupport.bytes("block-9x"));
            assertFalse(Arrays.equals(treeOf(16).rootHash(), new MerkleTree(changed, SHA).rootHash()),
                    "블록 하나가 바뀌면 뿌리가 바뀐다");
        }

        @Test
        @DisplayName("순서를 바꾸면 다른 파일이다")
        void orderIsPartOfTheValue() {
            List<byte[]> blocks = TestSupport.blocks(8);
            List<byte[]> swapped = new ArrayList<>(blocks);
            Collections.swap(swapped, 2, 5);
            assertFalse(Arrays.equals(new MerkleTree(blocks, SHA).rootHash(),
                            new MerkleTree(swapped, SHA).rootHash()),
                    "같은 블록이라도 자리가 다르면 다른 뿌리다");
        }

        @Test
        @DisplayName("돌려받은 뿌리 해시를 고쳐도 트리는 안 바뀐다")
        void rootHashIsACopy() {
            MerkleTree tree = treeOf(8);
            byte[] root = tree.rootHash();
            byte[] before = root.clone();
            Arrays.fill(root, (byte) 0);
            assertArrayEquals(before, tree.rootHash(), "내부 배열을 그대로 내주면 밖에서 뿌리를 갈 수 있다");
        }
    }

    @Nested
    @DisplayName("모양")
    class Shape {

        @Test
        @DisplayName("높이는 잎 수의 올림 log2 다")
        void height() {
            assertEquals(0, treeOf(1).height());
            assertEquals(1, treeOf(2).height());
            assertEquals(2, treeOf(3).height());
            assertEquals(2, treeOf(4).height());
            assertEquals(3, treeOf(5).height());
            assertEquals(3, treeOf(8).height());
            assertEquals(4, treeOf(9).height());
            assertEquals(10, treeOf(1024).height());
        }

        @Test
        @DisplayName("층마다 노드 수가 절반씩 올림으로 준다")
        void levelSizes() {
            MerkleTree tree = treeOf(5);
            assertEquals(5, tree.levelSize(0));
            assertEquals(3, tree.levelSize(1), "5 를 둘씩 묶으면 2쌍과 나머지 1개다");
            assertEquals(2, tree.levelSize(2));
            assertEquals(1, tree.levelSize(3), "마지막 층은 언제나 뿌리 하나다");
        }

        @Test
        @DisplayName("범위 밖 접근은 예외다")
        void bounds() {
            MerkleTree tree = treeOf(5);
            assertThrows(IndexOutOfBoundsException.class, () -> tree.hashAt(4, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> tree.hashAt(0, 5));
            assertThrows(IndexOutOfBoundsException.class, () -> tree.hashAt(-1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> tree.leafHash(5));
            assertThrows(IndexOutOfBoundsException.class, () -> tree.proofFor(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> tree.levelSize(9));
        }

        @Test
        @DisplayName("짝 없는 노드는 다시 해시하지 않고 그대로 올라간다")
        void promotedNodeIsTheSameObject() {
            // 참조가 같다는 것이 계약이다. 값을 다시 계산해 넣어도 값은 같지만
            // withLeafReplaced 가 "무엇을 새로 만들었나"를 참조로 세는 것이 불가능해진다.
            MerkleTree tree = treeOf(3);
            assertSame(tree.hashAt(0, 2), tree.hashAt(1, 1), "2번 잎이 1층으로 그대로 올라간다");
            assertEquals(2, tree.levelSize(1));
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("느린 참조 구현과 뿌리가 같다 (잎 1..200)")
        void againstNaiveRoot() {
            TestSupport.Dice dice = new TestSupport.Dice(20260814L);
            MerkleHashing hashing = new PrefixedHashing(SHA);
            for (int n = 1; n <= 200; n++) {
                List<byte[]> blocks = dice.blocks(n);
                assertArrayEquals(TestSupport.naiveRoot(blocks, hashing),
                        new MerkleTree(blocks, hashing).rootHash(),
                        "잎 " + n + "개에서 갈렸다");
            }
        }

        @Test
        @DisplayName("층 배열의 모든 노드가 자식 둘로 설명된다")
        void everyNodeIsExplainedByItsChildren() {
            // 구조 테스트다. 뿌리만 맞아도 중간층이 엉뚱할 수 있으므로 전 노드를 다시 계산해 본다.
            MerkleHashing hashing = new PrefixedHashing(SHA);
            TestSupport.Dice dice = new TestSupport.Dice(7L);
            for (int n : new int[] {1, 2, 3, 5, 9, 17, 100}) {
                MerkleTree tree = new MerkleTree(dice.blocks(n), hashing);
                for (int level = 1; level <= tree.height(); level++) {
                    for (int i = 0; i < tree.levelSize(level); i++) {
                        int left = 2 * i;
                        int right = left + 1;
                        byte[] expected = right < tree.levelSize(level - 1)
                                ? hashing.nodeHash(tree.hashAt(level - 1, left), tree.hashAt(level - 1, right))
                                : tree.hashAt(level - 1, left);
                        assertArrayEquals(expected, tree.hashAt(level, i),
                                "n=" + n + " 의 " + level + "층 " + i + "번");
                    }
                }
                assertTrue(tree.levelSize(tree.height()) == 1);
                assertNotEquals(0, tree.rootHash().length);
            }
        }
    }
}
