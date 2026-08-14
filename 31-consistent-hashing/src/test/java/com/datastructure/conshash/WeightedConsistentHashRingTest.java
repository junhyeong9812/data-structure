package com.datastructure.conshash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("가중치 있는 링")
class WeightedConsistentHashRingTest extends HashRingContractTest {

    @Override
    protected HashRing newRing() {
        return new WeightedConsistentHashRing();
    }

    @Nested
    @DisplayName("가중치가 자리 수로 바뀐다")
    class WeightWiring {

        @Test
        @DisplayName("가중치 1은 그냥 addNode 와 완전히 같다")
        void weightOneIsPlainAdd() {
            // 가중치를 얹으면서 기본 배치가 달라지면 안 된다.
            // 자리 이름 규칙을 바꿔버리는 구현이 여기서 걸린다.
            ConsistentHashRing plain = new ConsistentHashRing(100);
            WeightedConsistentHashRing weighted = new WeightedConsistentHashRing(100);
            for (int i = 0; i < 5; i++) {
                plain.addNode("node-" + i);
                weighted.addNode("node-" + i, 1);
            }
            assertEquals(plain.ringView(), weighted.ringView());
            assertEquals(0, RingMetrics.moved(RingMetrics.assign(plain, KEYS),
                    RingMetrics.assign(weighted, KEYS)));
        }

        @Test
        @DisplayName("가중치만큼 자리를 더 준다")
        void weightMultipliesSlots() {
            WeightedConsistentHashRing ring = new WeightedConsistentHashRing(100);
            ring.addNode("small", 1);
            ring.addNode("big", 3);
            assertEquals(100, ring.slotsOf("small"));
            assertEquals(300, ring.slotsOf("big"));
            assertEquals(400, ring.slotCount());
        }

        @Test
        @DisplayName("무거운 노드를 빼면 자기 자리를 전부 반납한다")
        void removingAHeavyNodeTakesAllItsSlots() {
            // 노드마다 자리 수가 다르므로 제거가 "가상 노드 수만큼" 지우면 300개 중 100개만 지워진다.
            // 남은 200개는 죽은 노드 이름을 그대로 들고 있게 되고, 키가 그리로 간다.
            WeightedConsistentHashRing ring = new WeightedConsistentHashRing(100);
            ring.addNode("small", 1);
            ring.addNode("big", 3);
            ring.removeNode("big");
            assertEquals(100, ring.slotCount());
            assertEquals(1, ring.nodeCount());
            for (String key : KEYS) {
                assertEquals("small", ring.getNode(key), key);
            }
        }

        @Test
        @DisplayName("가중치는 1 이상이어야 한다")
        void weightIsValidated() {
            WeightedConsistentHashRing ring = new WeightedConsistentHashRing(10);
            assertThrows(IllegalArgumentException.class, () -> ring.addNode("a", 0));
            assertThrows(IllegalArgumentException.class, () -> ring.addNode("a", -2));
            assertEquals(0, ring.nodeCount());
        }
    }
}
