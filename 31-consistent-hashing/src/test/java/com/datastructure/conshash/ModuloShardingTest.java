package com.datastructure.conshash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("모듈로 샤딩")
class ModuloShardingTest extends HashRingContractTest {

    @Override
    protected HashRing newRing() {
        return new ModuloSharding();
    }

    @Nested
    @DisplayName("모듈로만의 성질")
    class ModuloOnly {

        @Test
        @DisplayName("자리를 위해 들고 있는 것이 노드 이름뿐이다")
        void slotCountEqualsNodeCount() {
            HashRing ring = withNodes(10);
            assertEquals(10, ring.slotCount());
        }

        @Test
        @DisplayName("분포는 고르다. 나쁜 것은 분포가 아니다")
        void distributionIsFine() {
            // 모듈로를 버리는 이유가 분포라고 오해하기 쉬운데 아니다.
            // 해시가 고르면 나눗셈 나머지도 고르다. 문제는 오직 N 이 바뀔 때다.
            HashRing ring = withNodes(10);
            Map<String, Integer> counts = ring.keyCounts(RingMetrics.keys(100_000));
            assertTrue(RingMetrics.imbalance(counts) < 1.05,
                    "불균형 비 " + RingMetrics.imbalance(counts));
        }

        @Test
        @DisplayName("노드를 넣은 순서가 배정을 바꾼다")
        void orderOfNodesMatters() {
            // 목록의 인덱스가 곧 배정이라 같은 노드 집합이라도 순서가 다르면 다른 링이다.
            // 원 방식에는 이 성질이 없다. 이름만으로 자리가 정해지기 때문이다.
            ModuloSharding forward = new ModuloSharding();
            ModuloSharding backward = new ModuloSharding();
            for (int i = 0; i < 5; i++) {
                forward.addNode("node-" + i);
                backward.addNode("node-" + (4 - i));
            }
            assertEquals(forward.nodes().size(), backward.nodes().size());
            int same = 0;
            for (String key : KEYS) {
                if (forward.getNode(key).equals(backward.getNode(key))) {
                    same++;
                }
            }
            assertEquals(376, same, "2000개 중 우연히 같은 것만 남는다");
        }
    }
}
