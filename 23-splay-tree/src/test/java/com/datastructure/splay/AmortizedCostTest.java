package com.datastructure.splay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 이 박스의 한계 측정. 시간을 재지 않는다. 회전 수와 방문 노드 수를 센다.
 * 둘 다 결정적이라 정확히 단언할 수 있다.
 */
@DisplayName("상환 비용")
class AmortizedCostTest {

    private static SplayTree<Integer, String> sortedTree(int n) {
        SplayTree<Integer, String> t = new SplayTree<>();
        for (int i = 0; i < n; i++) {
            t.put(i, "v");
        }
        return t;
    }

    @Nested
    @DisplayName("정렬 입력에서 죽지 않는다")
    class SortedInputFoldsItself {

        @Test
        @DisplayName("넣은 직후에는 한 줄이다. 회전은 한 번도 없었다")
        void buildIsAStraightLine() {
            SplayTree<Integer, String> t = sortedTree(1000);
            assertEquals(1000, t.height(), "정렬 입력은 한 줄이 된다. 06번과 똑같다");
            assertEquals(999, t.depthOf(0), "가장 작은 키가 맨 밑에 있다");
            assertEquals(0L, t.rotations(),
                    "정렬 순서로 넣는 동안은 splay 가 할 일이 없다. 회전이 0 이어야 한다");
        }

        @Test
        @DisplayName("조회 세 번에 높이가 1000 에서 252 로 접힌다")
        void accessesFoldThePath() {
            SplayTree<Integer, String> t = sortedTree(1000);
            assertEquals(1000, t.height());

            t.get(0);
            assertEquals(501, t.height(), "가장 깊은 키를 한 번 조회하면 경로가 절반으로 접힌다");
            assertEquals(999L, t.rotations());

            t.get(999);
            assertEquals(502, t.height());
            assertEquals(1001L, t.rotations());

            t.get(500);
            assertEquals(252, t.height());
            assertEquals(1252L, t.rotations());

            // 16번 레드블랙 트리라면 넣는 내내 높이가 20 을 안 넘었다. 여기는 1000 에서 시작해
            // 접근이 스스로 내려온다. 균형을 보장받는 것과 사후에 접히는 것의 차이다.
        }

        @Test
        @DisplayName("오름차순 전체 조회는 회전 5259번이다. n log n 보다 작다")
        void sequentialAccessIsCheap() {
            int n = 1000;
            SplayTree<Integer, String> t = sortedTree(n);
            for (int i = 0; i < n; i++) {
                assertEquals("v", t.get(i));
            }
            long rot = t.rotations();
            assertEquals(5259L, rot, "오름차순 전체 조회의 총 회전 수");

            long nLogN = (long) (n * (Math.log(n) / Math.log(2)));
            assertTrue(rot < nLogN,
                    "총 회전 " + rot + " 이 n log n = " + nLogN + " 을 넘었다");
            // 실제로는 n log n 도 아니고 n 규모다(5259 대 1000).
            // 이것이 "순차 접근 정리(sequential access theorem)"다.
        }
    }

    @Nested
    @DisplayName("zig-zig 의 순서가 전부다")
    class ZigZigOrderIsEverything {

        @Test
        @DisplayName("할아버지 먼저 5,259번 대 부모 먼저 500,499번")
        void grandparentFirstVersusParentFirst() {
            int n = 1000;

            // 올바른 순서. 할아버지를 먼저 돌린다.
            SplayTree<Integer, String> splay = sortedTree(n);
            assertEquals(0L, splay.rotations(), "두 트리가 같은 모양에서 출발해야 한다");
            assertEquals(n, splay.height());
            for (int i = 0; i < n; i++) {
                assertEquals("v", splay.get(i));
            }
            long splayRotations = splay.rotations();

            // 잘못된 순서. 부모를 먼저 돌린다. 그냥 회전을 반복하는 것과 같아진다.
            MoveToRootTree naive = MoveToRootTree.spine(n);
            assertEquals(0L, naive.rotations(), "spine 은 회전 없이 지어야 한다");
            assertEquals(n, naive.height(), "splay 트리의 정렬 입력 결과와 같은 모양이어야 한다");
            for (int i = 0; i < n; i++) {
                assertTrue(naive.get(i), "키 " + i);
            }
            long naiveRotations = naive.rotations();

            assertEquals(5259L, splayRotations, "할아버지 먼저 (splay)");
            assertEquals(500_499L, naiveRotations, "부모 먼저 (move-to-root)");

            // 경로가 절반으로 접히지 않으면 다음 조회가 또 밑바닥까지 내려간다.
            // n(n-1)/2 = 499,500 이 그 대가다. n 을 두 배로 하면 네 배가 된다.
            assertTrue(naiveRotations > (long) n * (n - 1) / 2,
                    "부모 먼저는 n^2/2 규모여야 한다: " + naiveRotations);
            assertTrue(naiveRotations > splayRotations * 90,
                    "두 순서의 차이가 " + (naiveRotations / splayRotations) + "배밖에 안 난다");
        }

        @Test
        @DisplayName("두 순서는 답은 똑같이 낸다. 비용만 다르다")
        void bothOrdersAreCorrect() {
            // 그래서 계약 테스트로는 절대 못 잡는다. 걸음 수를 세야만 갈린다.
            int n = 300;
            MoveToRootTree naive = MoveToRootTree.spine(n);
            for (int i = 0; i < n; i++) {
                assertTrue(naive.get(i), "키 " + i + " 를 못 찾았다");
                assertEquals(0, naive.depthOf(i), "찾은 키가 뿌리에 없다");
            }
            for (int i = n; i < n + 20; i++) {
                assertTrue(!naive.get(i), "없는 키 " + i + " 를 찾았다고 한다");
            }
        }
    }

    @Nested
    @DisplayName("단일 연산 최악은 O(n) 이다")
    class WorstCaseIsLinear {

        @Test
        @DisplayName("조회 한 번이 노드 2000개를 지나고 회전 1999번을 한다")
        void oneAccessCanTouchEverything() {
            int n = 2000;
            SplayTree<Integer, String> t = sortedTree(n);
            assertEquals(n - 1, t.depthOf(0), "이 조회는 노드 " + n + "개를 지나야 한다");

            long before = t.rotations();
            assertEquals("v", t.get(0));
            assertEquals(n - 1, t.rotations() - before, "조회 한 번에 회전 " + (n - 1) + "번");

            // 16번 레드블랙 트리는 이 자리에서 2*log2(n+1) = 21 을 보장했다.
            // 스플레이 트리는 보장하지 않는다. 상환일 뿐이다. 이건 숨길 수 없는 성질이다.
            assertEquals(0, t.depthOf(0), "대신 다음 조회는 공짜다");
            assertEquals(1001, t.height(), "그리고 경로가 절반으로 접혔다");
        }
    }

    @Nested
    @DisplayName("최근에 쓴 것이 얕은 곳에 있다")
    class RecentlyUsedIsShallow {

        private static final int N = 100_000;
        private static final int HOT = 100;
        private static final int ROUNDS = 100;

        /** 완전히 균형 잡힌 BST 에서 key 의 깊이. 정렬 배열 이분 탐색이 그 모양이다. */
        private int balancedDepth(int key, int n) {
            int lo = 0;
            int hi = n - 1;
            int depth = 0;
            while (lo <= hi) {
                int mid = (lo + hi) >>> 1;
                if (key == mid) {
                    return depth;
                }
                if (key < mid) {
                    hi = mid - 1;
                } else {
                    lo = mid + 1;
                }
                depth++;
            }
            throw new IllegalStateException("없는 키");
        }

        @Test
        @DisplayName("10만 개 중 100개만 반복 조회하면 방문 노드가 균형 트리의 1/2.7 이다")
        void hotKeysStayNearTheRoot() {
            List<Integer> keys = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                keys.add(i);
            }
            Collections.shuffle(keys, new Random(20260814L));
            SplayTree<Integer, String> t = new SplayTree<>();
            for (int k : keys) {
                t.put(k, "v");
            }
            assertEquals(59, t.height(), "섞어 넣으면 높이가 log n 규모로 시작한다");

            int[] hot = new int[HOT];
            for (int j = 0; j < HOT; j++) {
                hot[j] = j * (N / HOT);
            }
            for (int k : hot) {
                t.get(k);
            }

            long splayVisits = 0;
            long balancedVisits = 0;
            for (int round = 0; round < ROUNDS; round++) {
                for (int k : hot) {
                    splayVisits += t.depthOf(k) + 1;
                    balancedVisits += balancedDepth(k, N) + 1;
                    t.get(k);
                }
            }

            int accesses = ROUNDS * HOT;
            assertEquals(59_653L, splayVisits, "스플레이 트리가 지나간 노드 수");
            assertEquals(159_800L, balancedVisits, "완전 균형 BST 라면 지나갔을 노드 수");

            // 조회 한 번당 5.96 대 15.98 이다.
            assertTrue(splayVisits * 2 < balancedVisits,
                    "스플레이 " + splayVisits + " 대 균형 " + balancedVisits);

            // 여기가 요점이다. 5.96 은 log2(100,000) = 16.6 이 아니라 log2(100) = 6.64 근처다.
            // 비용이 트리의 크기가 아니라 **최근에 쓴 것의 개수**로 정해진다.
            // 10번 LRU 캐시가 리스트를 손으로 관리해 얻던 성질을 구조가 저절로 갖는다.
            double perAccess = (double) splayVisits / accesses;
            assertTrue(perAccess < Math.log(HOT) / Math.log(2) + 1,
                    "조회당 " + perAccess + " 노드. log2(" + HOT + ") 근처여야 한다");
            assertTrue(perAccess < Math.log(N) / Math.log(2) / 2,
                    "조회당 " + perAccess + " 노드. log2(" + N + ") 의 절반보다 작아야 한다");
        }
    }
}
