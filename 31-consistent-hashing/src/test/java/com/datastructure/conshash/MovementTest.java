package com.datastructure.conshash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 한계 측정 1. 이 박스의 중심이다.
 *
 * 키 100,000개를 노드 10개에 나눈 뒤 노드 하나를 빼고, 자리가 바뀐 키를 센다.
 * 시간을 재지 않는다. 매핑을 두 번 구해서 다른 것을 세면 되고, 그 수는 언제나 같다.
 *
 * 기댓값은 전부 파이썬 참조 구현으로 다시 계산해 맞춰본 값이다.
 */
@DisplayName("이동량 측정")
class MovementTest {

    private static final List<String> KEYS = RingMetrics.keys(100_000);
    private static final int NODES = 10;

    private static HashRing filled(HashRing ring) {
        for (int i = 0; i < NODES; i++) {
            ring.addNode("node-" + i);
        }
        return ring;
    }

    /** 노드 하나를 뺐을 때 자리가 바뀐 키 수. */
    private static int movedOnRemoval(HashRing ring, String victim) {
        Map<String, String> before = RingMetrics.assign(filled(ring), KEYS);
        ring.removeNode(victim);
        return RingMetrics.moved(before, RingMetrics.assign(ring, KEYS));
    }

    /** 그 노드가 빠지기 전에 맡고 있던 키 수. */
    private static int shareOf(HashRing ring, String node) {
        return filled(ring).keyCounts(KEYS).get(node);
    }

    @Nested
    @DisplayName("측정 1: 노드 하나가 죽으면 몇 개가 옮겨가는가")
    class NodeRemoval {

        @Test
        @DisplayName("가운데 노드(node-7) 제거")
        void removingAMiddleNode() {
            int modulo = movedOnRemoval(new ModuloSharding(), "node-7");
            int ring1 = movedOnRemoval(new ConsistentHashRing(1), "node-7");
            int ring100 = movedOnRemoval(new ConsistentHashRing(100), "node-7");
            int ring500 = movedOnRemoval(new ConsistentHashRing(500), "node-7");

            System.out.printf("  가운데 노드 제거 (키 %,d, 노드 %d)%n", KEYS.size(), NODES);
            System.out.printf("    %-28s %,8d  (%.1f%%)%n", "ModuloSharding", modulo, modulo / 1000.0);
            System.out.printf("    %-28s %,8d  (%.1f%%)%n", "ConsistentHashRing v=1", ring1, ring1 / 1000.0);
            System.out.printf("    %-28s %,8d  (%.1f%%)%n", "ConsistentHashRing v=100", ring100, ring100 / 1000.0);
            System.out.printf("    %-28s %,8d  (%.1f%%)%n", "ConsistentHashRing v=500", ring500, ring500 / 1000.0);
            System.out.printf("    %-28s %s%n", "JumpConsistentHash", "불가 (가운데를 못 뺀다)");

            assertEquals(89_905, modulo, "모듈로");
            assertEquals(11_161, ring1, "가상 노드 1개짜리 원");
            assertEquals(12_044, ring100, "가상 노드 100개짜리 원");
            assertEquals(10_288, ring500, "가상 노드 500개짜리 원");
            assertTrue(modulo > 7 * ring100, modulo + " 대 " + ring100);

            assertThrows(UnsupportedOperationException.class,
                    () -> filled(new JumpConsistentHash()).removeNode("node-7"));
        }

        @Test
        @DisplayName("맨 뒤 노드(node-9) 제거. 점프 해시는 여기서만 잴 수 있다")
        void removingTheLastNode() {
            int modulo = movedOnRemoval(new ModuloSharding(), "node-9");
            int ring100 = movedOnRemoval(new ConsistentHashRing(100), "node-9");
            int jump = movedOnRemoval(new JumpConsistentHash(), "node-9");

            System.out.printf("  맨 뒤 노드 제거%n");
            System.out.printf("    %-28s %,8d  (%.1f%%)%n", "ModuloSharding", modulo, modulo / 1000.0);
            System.out.printf("    %-28s %,8d  (%.1f%%)%n", "ConsistentHashRing v=100", ring100, ring100 / 1000.0);
            System.out.printf("    %-28s %,8d  (%.1f%%)%n", "JumpConsistentHash", jump, jump / 1000.0);

            assertEquals(89_941, modulo, "모듈로");
            assertEquals(10_039, ring100, "원");
            assertEquals(9_827, jump, "점프");
            assertTrue(modulo > 8 * jump, modulo + " 대 " + jump);
        }

        @Test
        @DisplayName("옮겨간 키는 죽은 노드가 맡던 것뿐이다. 모듈로만 아니다")
        void onlyTheVictimsKeysMove() {
            // 이 등식이 일관된 해싱의 정의다. 이동량이 "적다"가 아니라 "그것뿐이다".
            assertEquals(shareOf(new ConsistentHashRing(100), "node-7"),
                    movedOnRemoval(new ConsistentHashRing(100), "node-7"),
                    "원: 옮겨간 수와 맡던 수가 같아야 한다");
            assertEquals(shareOf(new JumpConsistentHash(), "node-9"),
                    movedOnRemoval(new JumpConsistentHash(), "node-9"),
                    "점프: 옮겨간 수와 맡던 수가 같아야 한다");

            int moduloShare = shareOf(new ModuloSharding(), "node-9");
            int moduloMoved = movedOnRemoval(new ModuloSharding(), "node-9");
            System.out.printf("  모듈로: node-9 가 맡던 키 %,d 개인데 %,d 개가 움직였다 (%.1f 배)%n",
                    moduloShare, moduloMoved, (double) moduloMoved / moduloShare);
            assertEquals(9_996, moduloShare);
            assertTrue(moduloMoved > 8 * moduloShare,
                    "모듈로는 남의 키까지 흔든다: " + moduloShare + " 대 " + moduloMoved);
        }
    }

    @Nested
    @DisplayName("측정 1b: 노드가 늘어날 때")
    class NodeAddition {

        private int movedOnAdd(HashRing ring) {
            Map<String, String> before = RingMetrics.assign(filled(ring), KEYS);
            ring.addNode("node-10");
            return RingMetrics.moved(before, RingMetrics.assign(ring, KEYS));
        }

        @Test
        @DisplayName("서버 증설도 같은 이야기다")
        void addingANode() {
            int modulo = movedOnAdd(new ModuloSharding());
            int ring1 = movedOnAdd(new ConsistentHashRing(1));
            int ring100 = movedOnAdd(new ConsistentHashRing(100));
            int jump = movedOnAdd(new JumpConsistentHash());

            System.out.printf("  노드 추가 (10 -> 11)%n");
            System.out.printf("    %-28s %,8d%n", "ModuloSharding", modulo);
            System.out.printf("    %-28s %,8d%n", "ConsistentHashRing v=1", ring1);
            System.out.printf("    %-28s %,8d%n", "ConsistentHashRing v=100", ring100);
            System.out.printf("    %-28s %,8d%n", "JumpConsistentHash", jump);

            assertEquals(90_793, modulo);
            assertEquals(9_597, ring1);
            assertEquals(9_244, ring100);
            assertEquals(9_060, jump);
        }

        @Test
        @DisplayName("움직인 키는 전부 새 노드로 간다. 모듈로만 아니다")
        void movedKeysAllGoToTheNewNode() {
            for (HashRing ring : List.of(new ConsistentHashRing(100), new JumpConsistentHash())) {
                Map<String, String> before = RingMetrics.assign(filled(ring), KEYS);
                ring.addNode("node-10");
                Map<String, String> after = RingMetrics.assign(ring, KEYS);
                int strayed = 0;
                for (String key : KEYS) {
                    if (!before.get(key).equals(after.get(key)) && !"node-10".equals(after.get(key))) {
                        strayed++;
                    }
                }
                assertEquals(0, strayed, ring.getClass().getSimpleName()
                        + ": 새 노드와 무관한 이동이 " + strayed + "건 있다");
            }

            ModuloSharding modulo = new ModuloSharding();
            Map<String, String> before = RingMetrics.assign(filled(modulo), KEYS);
            modulo.addNode("node-10");
            Map<String, String> after = RingMetrics.assign(modulo, KEYS);
            int strayed = 0;
            for (String key : KEYS) {
                if (!before.get(key).equals(after.get(key)) && !"node-10".equals(after.get(key))) {
                    strayed++;
                }
            }
            System.out.printf("  모듈로는 새 노드와 상관없는 이동만 %,d 건이다%n", strayed);
            assertEquals(81_566, strayed);
        }
    }

    @Nested
    @DisplayName("측정 1c: 점프 해시로 가운데 노드를 빼려고 목록을 밀면")
    class JumpListShift {

        @Test
        @DisplayName("번호가 당겨져서 28.7% 가 움직인다")
        void shiftingTheListBreaksTheGuarantee() {
            // 점프 해시는 번호가 0..N-1 로 이어져야 한다. 가운데가 죽으면 뒤를 당길 수밖에 없고,
            // 당기는 순간 그 뒤 노드들의 번호가 전부 바뀐다. 1/N 보장은 여기서 사라진다.
            JumpConsistentHash ten = new JumpConsistentHash();
            filled(ten);
            Map<String, String> before = RingMetrics.assign(ten, KEYS);

            JumpConsistentHash nine = new JumpConsistentHash();
            List<String> shifted = new ArrayList<>();
            for (int i = 0; i < NODES; i++) {
                if (i != 7) {
                    shifted.add("node-" + i);
                }
            }
            shifted.forEach(nine::addNode);
            int moved = RingMetrics.moved(before, RingMetrics.assign(nine, KEYS));

            System.out.printf("  목록을 밀어 9개로: %,d 개 이동 (%.1f%%). 1/N 인 %,d 개가 아니다%n",
                    moved, moved / 1000.0, 10_000);
            assertEquals(28_682, moved);
            assertTrue(moved > 25_000, "가운데 제거를 흉내내면 1/N 이 아니다: " + moved);
        }
    }
}
