package com.datastructure.conshash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("일관된 해시 링")
class ConsistentHashRingTest extends HashRingContractTest {

    @Override
    protected HashRing newRing() {
        return new ConsistentHashRing();
    }

    @Nested
    @DisplayName("원의 구조")
    class RingStructure {

        @Test
        @DisplayName("자리 수는 노드 수 곱하기 가상 노드 수다")
        void slotCountIsNodesTimesVirtualNodes() {
            ConsistentHashRing ring = new ConsistentHashRing(100);
            for (int i = 0; i < 10; i++) {
                ring.addNode("node-" + i);
            }
            assertEquals(1_000, ring.slotCount(), "충돌이 있었다면 이 수가 줄어든다");
            assertEquals(100, ring.slotsOf("node-3"));
            assertEquals(0, ring.slotsOf("없는노드"));
        }

        @Test
        @DisplayName("모든 자리가 0 이상 2^32 미만이다")
        void positionsStayOnTheCircle() {
            ConsistentHashRing ring = new ConsistentHashRing(50);
            for (int i = 0; i < 10; i++) {
                ring.addNode("node-" + i);
            }
            for (long position : ring.ringView().keySet()) {
                assertTrue(position >= 0 && position < Hashing.RING_SIZE, "자리 " + position);
            }
        }

        @Test
        @DisplayName("가상 이름이 자리를 정한다")
        void virtualNameDecidesThePosition() {
            // 파이썬 참조 구현으로 검산한 값이다. 이름 규칙(node#i)이 바뀌면 배치가 통째로 바뀐다.
            ConsistentHashRing ring = new ConsistentHashRing(1);
            ring.addNode("node-0");
            assertEquals(1, ring.slotCount());
            assertEquals(3_075_327_781L, ring.ringView().firstKey());
            assertEquals(3_075_327_781L, Hashing.MIXED.position("node-0#0"));
        }

        @Test
        @DisplayName("노드를 빼면 자기 자리만 사라진다")
        void removingTakesOnlyItsOwnSlots() {
            ConsistentHashRing ring = new ConsistentHashRing(100);
            for (int i = 0; i < 10; i++) {
                ring.addNode("node-" + i);
            }
            SortedMap<Long, String> before = new TreeMap<>(ring.ringView());
            ring.removeNode("node-7");

            assertEquals(900, ring.slotCount());
            for (Map.Entry<Long, String> entry : before.entrySet()) {
                if ("node-7".equals(entry.getValue())) {
                    assertNull(ring.ringView().get(entry.getKey()),
                            "node-7 의 자리 " + entry.getKey() + " 가 남아 있다");
                } else {
                    assertEquals(entry.getValue(), ring.ringView().get(entry.getKey()),
                            "남의 자리 " + entry.getKey() + " 가 사라졌거나 바뀌었다");
                }
            }
        }

        @Test
        @DisplayName("가장 큰 자리보다 뒤에 있는 키는 원을 돌아 첫 자리로 간다")
        void keysPastTheLastSlotWrapAround() {
            // 이 되돌아감을 빠뜨리는 것이 이 문제에서 제일 흔한 실수다.
            // 자리를 6개만 두어 그런 키가 100,000개 중 270개나 되게 만들었다.
            // 가상 노드가 1000개면 같은 실수가 18개짜리 구멍이 되어 눈에 안 띈다.
            ConsistentHashRing ring = new ConsistentHashRing(3);
            ring.addNode("node-0");
            ring.addNode("node-1");
            assertEquals(6, ring.slotCount());

            long lastPosition = ring.ringView().lastKey();
            String firstOwner = ring.ringView().get(ring.ringView().firstKey());
            assertEquals("node-0", ring.ringView().get(lastPosition));
            assertEquals("node-1", firstOwner);

            List<String> keys = RingMetrics.keys(100_000);
            int past = 0;
            for (String key : keys) {
                if (Hashing.MIXED.position(key) > lastPosition) {
                    past++;
                    assertEquals(firstOwner, ring.getNode(key),
                            key + " 는 마지막 자리보다 뒤에 있다. 원을 한 바퀴 돌아야 한다");
                }
            }
            assertEquals(270, past, "되돌아감이 필요한 키의 수");
        }

        @Test
        @DisplayName("키가 자리와 정확히 같으면 그 자리가 맡는다")
        void keyExactlyOnASlotBelongsToThatSlot() {
            // 자리를 손으로 정하는 해시를 넣어 "정확히 겹치는" 경우를 만든다.
            // MIXED 로는 확률이 2^32 분의 1 이라 키 100,000개를 던져도 한 번도 안 나온다.
            // 그래서 "이상"과 "초과"를 바꿔 써도 무작위 키로는 영원히 안 걸린다.
            RingHash fixed = name -> switch (name) {
                case "node-0#0" -> 100L;
                default -> 200L;
            };
            ConsistentHashRing ring = new ConsistentHashRing(1, fixed);
            ring.addNode("node-0");
            ring.addNode("node-1");
            assertEquals(2, ring.slotCount());
            assertEquals("node-1", ring.getNode("key-0"),
                    "자리 200 에 정확히 떨어진 키는 그 자리의 노드 것이다");
        }

        @Test
        @DisplayName("자리가 겹친 노드를 빼도 남의 자리는 안 가져간다")
        void removingDoesNotStealACollidedSlot() {
            // 두 가상 이름이 같은 자리에 떨어지면 TreeMap 이 뒤에 온 것으로 덮어쓴다.
            // 그 상태에서 앞의 노드를 뺄 때 자리를 확인 없이 지우면 남은 노드가 원에서 사라진다.
            // 노드는 하나 살아 있는데 담당은 없는 상태가 된다.
            RingHash collide = name -> name.startsWith("node-") ? 500L : 700L;
            ConsistentHashRing ring = new ConsistentHashRing(1, collide);
            ring.addNode("node-a");
            ring.addNode("node-b");
            assertEquals(1, ring.slotCount(), "자리가 겹쳐서 하나로 덮였다");

            ring.removeNode("node-a");
            assertEquals(1, ring.nodeCount());
            assertEquals(1, ring.slotCount(), "node-b 의 자리를 node-a 가 가져갔다");
            assertEquals("node-b", ring.getNode("key-0"));
        }

        @Test
        @DisplayName("가상 노드 수는 1 이상이어야 한다")
        void virtualNodeCountIsValidated() {
            assertThrows(IllegalArgumentException.class, () -> new ConsistentHashRing(0));
            assertThrows(IllegalArgumentException.class, () -> new ConsistentHashRing(-1));
            assertThrows(IllegalArgumentException.class, () -> new ConsistentHashRing(10, null));
        }
    }
}
