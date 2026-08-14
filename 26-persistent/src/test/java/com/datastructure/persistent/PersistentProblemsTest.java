package com.datastructure.persistent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("응용 둘")
class PersistentProblemsTest {

    private static List<String[]> commands(String... flat) {
        List<String[]> out = new ArrayList<>();
        for (String line : flat) {
            out.add(line.split(" "));
        }
        return out;
    }

    @Nested
    @DisplayName("replay: 매 시점의 스냅샷")
    class Replay {

        @Test
        @DisplayName("명령 하나마다 스냅샷 하나, 그리고 실행 전 상태 하나")
        void snapshotPerCommand() {
            List<PersistentMap<String, Integer>> shots =
                    PersistentProblems.replay(commands("put a 1", "put b 2", "remove a", "put b 9"));

            assertEquals(5, shots.size(), "명령 4개면 스냅샷은 5개다");
            assertTrue(shots.get(0).isEmpty());
            assertEquals(1, shots.get(1).get("a"));
            assertNull(shots.get(1).get("b"));
            assertEquals(List.of("a", "b"), shots.get(2).keys());
            assertEquals(List.of("b"), shots.get(3).keys());
            assertEquals(2, shots.get(3).get("b"));
            assertEquals(9, shots.get(4).get("b"));

            // 마지막 상태를 만든 뒤에도 1번 시점은 그대로다.
            assertEquals(1, shots.get(1).get("a"), "옛 스냅샷이 나중 명령에 끌려 다녔다");
            assertEquals(1, shots.get(1).size());
        }

        @Test
        @DisplayName("빈 명령 목록")
        void empty() {
            List<PersistentMap<String, Integer>> shots = PersistentProblems.replay(List.of());
            assertEquals(1, shots.size());
            assertTrue(shots.get(0).isEmpty());
        }

        @Test
        @DisplayName("없는 키를 지우면 그 시점 스냅샷이 직전과 같은 객체다")
        void removingAbsentKeyReusesTheSnapshot() {
            List<PersistentMap<String, Integer>> shots =
                    PersistentProblems.replay(commands("put a 1", "remove zzz"));
            assertSame(shots.get(1), shots.get(2), "바뀐 것이 없으면 새 맵을 만들 이유가 없다");
        }

        @Test
        @DisplayName("잘못된 명령은 거부한다")
        void rejectsBadCommands() {
            assertThrows(IllegalArgumentException.class, () -> PersistentProblems.replay(null));
            assertThrows(IllegalArgumentException.class,
                    () -> PersistentProblems.replay(commands("drop a")));
            assertThrows(IllegalArgumentException.class,
                    () -> PersistentProblems.replay(commands("put a")));
            assertThrows(IllegalArgumentException.class,
                    () -> PersistentProblems.replay(commands("remove a 1")));
            assertThrows(IllegalArgumentException.class,
                    () -> PersistentProblems.replay(List.<String[]>of(new String[0])));
        }

        @Test
        @DisplayName("명령 1123개, 스냅샷 1124개를 남기는 데 노드 10,317개를 쓴다")
        void snapshotsAreCheap() {
            // 키를 네 자리로 맞춰 문자열 순서와 숫자 순서를 같게 만든다.
            // 그래야 트리 모양이 정수 키로 잰 앞 박스의 측정과 같아진다.
            List<String[]> script = new ArrayList<>();
            for (int x : TestTrees.balancedOrder(1023)) {
                script.add(new String[]{"put", String.format("k%04d", 2 * x), "1"});
            }
            for (int i = 0; i < 100; i++) {
                script.add(new String[]{"put", String.format("k%04d", 2 * i + 1), "1"});
            }

            List<PersistentMap<String, Integer>> shots = PersistentProblems.replay(script);
            assertEquals(1124, shots.size());

            long created = 0;
            for (PersistentMap<String, Integer> shot : shots) {
                created += ((PersistentTreeMap<String, Integer>) shot).nodesCreatedByLastPut();
            }
            assertEquals(10_317L, created, "영속 맵이 실제로 만든 노드의 총합");

            // 가변 맵으로 같은 스냅샷을 남기려면 시점마다 통째로 복사해야 한다.
            long mutableCopies = 0;
            for (PersistentMap<String, Integer> shot : shots) {
                mutableCopies += shot.size();
            }
            assertEquals(631_126L, mutableCopies);
            assertTrue(mutableCopies > created * 61,
                    "차이가 " + (mutableCopies / created) + "배밖에 안 난다");

            assertEquals(1123, shots.get(1123).size());
            assertEquals(1023, shots.get(1023).size(), "1023번 시점의 크기");
        }
    }

    @Nested
    @DisplayName("countSharedNodes: 실제로 몇 개를 공유하는가")
    class CountSharedNodes {

        @Test
        @DisplayName("자기 자신과는 전부 공유한다")
        void withItself() {
            PersistentTreeMap<Integer, String> map = TestTrees.balanced(1023);
            assertEquals(1023L, PersistentProblems.countSharedNodes(map, map));
            assertEquals(0L, PersistentProblems.countSharedNodes(
                    PersistentTreeMap.<Integer, String>empty(),
                    PersistentTreeMap.<Integer, String>empty()));
        }

        @Test
        @DisplayName("1000개짜리 맵에 하나를 넣으면 1013개를 공유한다")
        void oneInsertSharesAlmostEverything() {
            PersistentTreeMap<Integer, String> before = TestTrees.balanced(1023);
            PersistentTreeMap<Integer, String> after = before.put(1, "새 키");

            long shared = PersistentProblems.countSharedNodes(before, after);
            assertEquals(1013L, shared);
            assertTrue(shared > 990, "1000개 중 990개는 공유해야 한다. 실제 " + shared);
            assertEquals(11, after.size() - (int) shared, "새로 만든 것은 경로뿐이다");
        }

        @Test
        @DisplayName("따로 지은 두 맵은 내용이 같아도 하나도 공유하지 않는다")
        void separatelyBuiltMapsShareNothing() {
            PersistentTreeMap<Integer, String> a = TestTrees.balanced(255);
            PersistentTreeMap<Integer, String> b = TestTrees.balanced(255);
            assertEquals(a.keys(), b.keys());
            assertEquals(0L, PersistentProblems.countSharedNodes(a, b));
            // 통째 복사 구현이라면 put 한 번마다 이 상태가 된다.
        }

        @Test
        @DisplayName("연속한 스냅샷들이 서로 겹쳐 쌓인다")
        void consecutiveSnapshotsOverlap() {
            PersistentTreeMap<Integer, String> base = TestTrees.balanced(255);
            PersistentTreeMap<Integer, String> cur = base;
            long totalShared = 0;
            for (int i = 0; i < 10; i++) {
                PersistentTreeMap<Integer, String> next = cur.put(2 * i + 1, "v");
                totalShared += PersistentProblems.countSharedNodes(cur, next);
                cur = next;
            }
            assertEquals(2_515L, totalShared);
            // 열 번을 고쳤는데 노드는 255 + 새로 만든 것 몇십 개뿐이다.
            assertEquals(265, cur.size());
            assertEquals(239L, PersistentProblems.countSharedNodes(base, cur),
                    "열 번을 고친 뒤에도 첫 버전과 노드를 나눠 쓴다");
        }

        @Test
        @DisplayName("null 은 거부한다")
        void rejectsNull() {
            PersistentTreeMap<Integer, String> map = TestTrees.balanced(3);
            assertThrows(IllegalArgumentException.class,
                    () -> PersistentProblems.countSharedNodes(map, null));
            assertThrows(IllegalArgumentException.class,
                    () -> PersistentProblems.countSharedNodes(null, map));
        }
    }
}
