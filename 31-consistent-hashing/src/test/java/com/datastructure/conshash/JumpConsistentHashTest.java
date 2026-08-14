package com.datastructure.conshash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("점프 일관 해시")
class JumpConsistentHashTest extends HashRingContractTest {

    @Override
    protected HashRing newRing() {
        return new JumpConsistentHash();
    }

    @Override
    protected boolean canRemoveMiddle() {
        return false;
    }

    @Nested
    @DisplayName("알고리즘 자체")
    class JumpAlgorithm {

        @Test
        @DisplayName("논문 의사코드와 같은 값을 낸다")
        void knownVectors() {
            // 파이썬으로 같은 식을 다시 계산해 검산한 값이다.
            assertEquals(0, JumpConsistentHash.jumpHash(0L, 1));
            assertEquals(0, JumpConsistentHash.jumpHash(0L, 10));
            assertEquals(6, JumpConsistentHash.jumpHash(1L, 10));
            assertEquals(1, JumpConsistentHash.jumpHash(12_345L, 10));
            assertEquals(8, JumpConsistentHash.jumpHash(Long.MAX_VALUE, 10));
            assertEquals(87, JumpConsistentHash.jumpHash(0xdeadbeefL, 100));
        }

        @Test
        @DisplayName("버킷이 0개면 -1 이다")
        void zeroBucketsGivesMinusOne() {
            // 반복문이 한 번도 안 돌아서 b 의 초깃값이 그대로 나온다.
            // 그래서 getNode 가 빈 목록을 따로 막아야 한다. 안 막으면 nodes.get(-1) 이다.
            assertEquals(-1, JumpConsistentHash.jumpHash(0L, 0));
            assertEquals(-1, JumpConsistentHash.jumpHash(12_345L, 0));
        }

        @Test
        @DisplayName("언제나 0 이상 N 미만이다")
        void alwaysInRange() {
            for (long key = 0; key < 500; key++) {
                for (int n = 1; n <= 64; n++) {
                    int bucket = JumpConsistentHash.jumpHash(key * 0x9E3779B97F4A7C15L, n);
                    assertTrue(bucket >= 0 && bucket < n,
                            "key=" + key + " n=" + n + " -> " + bucket);
                }
            }
        }

        @Test
        @DisplayName("버킷을 늘리면 옮겨가는 키는 반드시 새 버킷으로만 간다")
        void growingOnlyMovesKeysToTheNewBucket() {
            // 이게 이 알고리즘이 "일관"인 이유다. 기존 버킷끼리는 절대 주고받지 않는다.
            // 원 방식도 같은 성질을 갖지만 그쪽은 자리를 저장해서 얻고, 여기는 계산으로 얻는다.
            int violations = 0;
            for (long key = 0; key < 2_000; key++) {
                for (int n = 1; n < 30; n++) {
                    int before = JumpConsistentHash.jumpHash(key, n);
                    int after = JumpConsistentHash.jumpHash(key, n + 1);
                    if (before != after && after != n) {
                        violations++;
                    }
                }
            }
            assertEquals(0, violations, "기존 버킷끼리 키가 오갔다");
        }

        @Test
        @DisplayName("자리를 하나도 안 들고 있다")
        void memoryIsZero() {
            HashRing ring = withNodes(1_000);
            assertEquals(1_000, ring.nodeCount());
            assertEquals(0, ring.slotCount(), "점프 해시는 원도 가상 노드도 없다");
        }

        @Test
        @DisplayName("맨 뒤 노드는 뺄 수 있다")
        void lastNodeCanBeRemoved() {
            HashRing ring = withNodes(5);
            ring.removeNode("node-4");
            ring.removeNode("node-3");
            assertEquals(3, ring.nodeCount());
            assertEquals("node-2", ring.nodes().get(2));
        }
    }
}
