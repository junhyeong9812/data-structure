package com.datastructure.conshash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 네 구현이 공통으로 지켜야 하는 계약. 구현마다 3줄짜리 서브클래스가 이걸 상속한다.
 *
 * 여기 있는 것은 "어느 방식이든 이건 지켜야 한다"뿐이다.
 * 이동량이 적다거나 분포가 고르다는 것은 계약이 아니라 성질이라 MovementTest 와 BalanceTest 가 잰다.
 * 모듈로 샤딩도 이 계약은 전부 지킨다. 지키면서 못 쓰게 되는 것이 이 박스의 이야기다.
 */
abstract class HashRingContractTest {

    /** 노드가 하나도 없는 새 링. */
    protected abstract HashRing newRing();

    /**
     * 가운데 노드를 뺄 수 있는가. 점프 해시만 false 다.
     *
     * 못 하는 것을 테스트에서 건너뛰지 않고 "거부한다"를 계약으로 적는다.
     * 조용히 넘어가면 어느 구현이 무엇을 못 하는지 통과 개수만 봐서는 알 수 없다.
     */
    protected boolean canRemoveMiddle() {
        return true;
    }

    protected static final List<String> KEYS = RingMetrics.keys(2_000);

    protected HashRing withNodes(int count) {
        HashRing ring = newRing();
        for (int i = 0; i < count; i++) {
            ring.addNode("node-" + i);
        }
        return ring;
    }

    @Nested
    @DisplayName("빈 링과 노드 하나")
    class EmptyAndSingle {

        @Test
        @DisplayName("노드가 없으면 담당도 없다")
        void emptyRingHasNoOwner() {
            HashRing ring = newRing();
            assertEquals(0, ring.nodeCount());
            assertEquals(0, ring.slotCount());
            assertTrue(ring.nodes().isEmpty());
            assertNull(ring.getNode("key-0"));
            assertNull(ring.getNode("key-99999"));
        }

        @Test
        @DisplayName("노드가 하나면 모든 키가 그리로 간다")
        void singleNodeOwnsEverything() {
            HashRing ring = newRing();
            ring.addNode("only");
            for (String key : KEYS) {
                assertEquals("only", ring.getNode(key), key + " 의 담당");
            }
            assertEquals(1, ring.nodeCount());
        }

        @Test
        @DisplayName("마지막 노드를 빼면 다시 담당이 없어진다")
        void removingTheLastNodeEmptiesTheRing() {
            HashRing ring = newRing();
            ring.addNode("only");
            ring.removeNode("only");
            assertEquals(0, ring.nodeCount());
            assertEquals(0, ring.slotCount());
            assertNull(ring.getNode("key-0"));
        }
    }

    @Nested
    @DisplayName("배정 계약")
    class Assignment {

        @Test
        @DisplayName("같은 키는 언제나 같은 노드로 간다")
        void assignmentIsDeterministic() {
            HashRing ring = withNodes(5);
            for (String key : KEYS) {
                assertEquals(ring.getNode(key), ring.getNode(key), key);
            }
        }

        @Test
        @DisplayName("모든 키가 살아 있는 노드로 간다")
        void everyKeyLandsOnALiveNode() {
            // 원의 끝을 넘어간 키를 되돌리지 않으면 여기서 null 이 나온다.
            // 노드 5개에 가상 노드가 많으면 그런 키가 몇 개 안 되지만, 0 이 아니다.
            HashRing ring = withNodes(5);
            List<String> live = ring.nodes();
            for (String key : KEYS) {
                String owner = ring.getNode(key);
                assertNotNull(owner, key + " 가 갈 곳이 없다. 원의 끝을 넘어간 키를 되돌렸는가");
                assertTrue(live.contains(owner), key + " -> " + owner + " 는 살아 있는 노드가 아니다");
            }
        }

        @Test
        @DisplayName("키를 나눠 가지면 합이 전체 키 수다")
        void keyCountsAddUp() {
            HashRing ring = withNodes(5);
            Map<String, Integer> counts = ring.keyCounts(KEYS);
            assertEquals(5, counts.size());
            assertEquals(KEYS.size(), counts.values().stream().mapToInt(Integer::intValue).sum());
        }

        @Test
        @DisplayName("null 은 거부한다")
        void rejectsNull() {
            HashRing ring = withNodes(3);
            assertThrows(IllegalArgumentException.class, () -> ring.addNode(null));
            assertThrows(IllegalArgumentException.class, () -> ring.getNode(null));
        }

        @Test
        @DisplayName("같은 노드를 두 번 넣거나 없는 노드를 빼면 거부한다")
        void rejectsDuplicateAndUnknown() {
            HashRing ring = withNodes(3);
            assertThrows(IllegalArgumentException.class, () -> ring.addNode("node-1"));
            assertThrows(IllegalArgumentException.class, () -> ring.removeNode("node-9"));
            assertEquals(3, ring.nodeCount());
        }
    }

    @Nested
    @DisplayName("노드 증감")
    class Membership {

        @Test
        @DisplayName("뺀 노드로는 아무 키도 가지 않는다")
        void removedNodeGetsNothing() {
            HashRing ring = withNodes(5);
            ring.removeNode("node-4");
            assertEquals(4, ring.nodeCount());
            for (String key : KEYS) {
                assertTrue(!"node-4".equals(ring.getNode(key)), key + " 가 아직 node-4 로 간다");
            }
        }

        @Test
        @DisplayName("뺐다가 그대로 다시 넣으면 원래 배정으로 돌아온다")
        void removeThenAddRestoresTheMapping() {
            // 배정이 상태 이력이 아니라 이름에서만 계산된다는 뜻이다.
            // 이게 성립해야 죽었던 서버가 돌아왔을 때 캐시가 다시 맞는다.
            HashRing ring = withNodes(5);
            Map<String, String> before = RingMetrics.assign(ring, KEYS);
            ring.removeNode("node-4");
            ring.addNode("node-4");
            Map<String, String> after = RingMetrics.assign(ring, KEYS);
            assertEquals(0, RingMetrics.moved(before, after));
        }

        @Test
        @DisplayName("가운데 노드 제거: 되거나, 거부하거나")
        void middleRemovalIsEitherSupportedOrRefused() {
            HashRing ring = withNodes(5);
            if (!canRemoveMiddle()) {
                UnsupportedOperationException thrown = assertThrows(
                        UnsupportedOperationException.class, () -> ring.removeNode("node-2"));
                assertTrue(thrown.getMessage().contains("node-4"),
                        "뺄 수 있는 것이 무엇인지 메시지에 있어야 한다: " + thrown.getMessage());
                assertEquals(5, ring.nodeCount(), "거부했으면 아무것도 안 바뀌어야 한다");
                return;
            }
            ring.removeNode("node-2");
            assertEquals(4, ring.nodeCount());
            assertTrue(!ring.nodes().contains("node-2"));
            for (String key : KEYS) {
                assertNotNull(ring.getNode(key));
            }
        }

        @Test
        @DisplayName("노드가 늘면 slotCount 도 는다")
        void slotCountFollowsNodes() {
            HashRing ring = newRing();
            int empty = ring.slotCount();
            ring.addNode("a");
            int one = ring.slotCount();
            ring.addNode("b");
            int two = ring.slotCount();
            assertEquals(0, empty);
            assertEquals(2 * one, two, "노드 하나가 차지하는 자리 수는 같아야 한다");
        }
    }
}
