package com.datastructure.persistent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 이 박스의 한계 측정. 시간을 재지 않는다. 새로 만든 노드의 수를 센다.
 *
 * 여기 있는 단언은 대부분 assertSame 이다. 값이 아니라 객체가 같은지를 묻는다.
 * assertEquals 로 바꾸면 전부 통과한다. 그러면 통째 복사 구현과 구별할 수 없다.
 */
@DisplayName("구조 공유와 그 비용")
class StructuralSharingTest {

    @Nested
    @DisplayName("바뀐 길목만 새로 만든다")
    class PathCopying {

        @Test
        @DisplayName("안 지나간 부분트리는 옛 버전과 같은 객체다")
        void untouchedSubtreesAreShared() {
            PersistentTreeMap<Integer, String> before = TestTrees.balanced(1023);
            PersistentTreeMap<Integer, String> after = before.put(-1, "새 키");

            assertNotSame(before.root, after.root, "뿌리는 새로 만들어야 한다");
            assertEquals(before.root.key, after.root.key, "뿌리의 키는 그대로다");
            assertSame(before.root.right, after.root.right,
                    "키 -1 은 왼쪽으로만 내려간다. 오른쪽 절반은 손댈 이유가 없다");

            // 내려가는 길 왼쪽만 새로 만들고, 매 층에서 반대쪽 가지는 그대로 넘겨받는다.
            PersistentTreeMap.Node<Integer, String> oldNode = before.root;
            PersistentTreeMap.Node<Integer, String> newNode = after.root;
            int depth = 0;
            while (oldNode != null) {
                assertNotSame(oldNode, newNode, depth + "층의 노드를 새로 만들지 않았다");
                assertEquals(oldNode.key, newNode.key, depth + "층의 키");
                assertSame(oldNode.right, newNode.right, depth + "층의 오른쪽 가지를 복사했다");
                oldNode = oldNode.left;
                newNode = newNode.left;
                depth++;
            }
            assertEquals(10, depth, "완전 균형 트리의 왼쪽 끝까지는 10층이다");
        }

        @Test
        @DisplayName("옛 버전은 그대로 살아 있다")
        void oldVersionIsUntouched() {
            PersistentTreeMap<Integer, String> before = TestTrees.balanced(1023);
            PersistentTreeMap<Integer, String> after = before.put(1, "새 키");

            assertEquals(1023, before.size());
            assertEquals(1024, after.size());
            assertEquals(10, before.height());
            assertEquals(11, after.height());
            assertNull(before.get(1), "옛 버전이 새 키를 알고 있다");
            assertEquals("새 키", after.get(1));
        }

        @Test
        @DisplayName("assertEquals 로는 이 계약을 검증할 수 없다")
        void equalsCannotSeeSharing() {
            PersistentTreeMap<Integer, String> a = TestTrees.balanced(1023);
            PersistentTreeMap<Integer, String> b = TestTrees.balanced(1023);

            // 내용이 같고 모양까지 같다.
            assertEquals(a.keys(), b.keys());
            assertEquals(a.height(), b.height());
            // 그런데 노드를 하나도 공유하지 않는다. 따로 지은 두 트리이기 때문이다.
            assertEquals(0L, PersistentProblems.countSharedNodes(a, b));
            // 통째 복사 구현이 만드는 것이 정확히 이 상태다. 답은 맞고 메모리만 n 배다.
            assertEquals(1023L, PersistentProblems.countSharedNodes(a, a));
        }
    }

    @Nested
    @DisplayName("한 번 수정에 새로 만드는 노드가 O(log n) 이다")
    class NodesPerUpdate {

        @Test
        @DisplayName("완전 균형에서는 정확히 height + 1 개다")
        void exactlyHeightPlusOne() {
            // n = 2^k - 1 이면 완전 균형이라 모든 잎이 같은 층에 있다.
            // 새 키는 그 밑에 붙으므로 만드는 노드가 경로 k 개에 새 잎 하나다.
            int[] expectedCreated = {3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
            for (int k = 2; k <= 12; k++) {
                int n = (1 << k) - 1;
                PersistentTreeMap<Integer, String> map = TestTrees.balanced(n);
                assertEquals(k, map.height(), "n=" + n + " 의 높이");

                PersistentTreeMap<Integer, String> next = map.put(1, "새 키");
                assertEquals(expectedCreated[k - 2], next.nodesCreatedByLastPut(),
                        "n=" + n + " 에서 put 이 만든 노드 수");
                assertEquals(k + 1, next.nodesCreatedByLastPut(), "height + 1 이어야 한다");
            }
            // n 이 4095 로 1365배가 되는 동안 put 하나의 비용은 3 에서 13 이 됐다.
            // 통째 복사였다면 3 에서 4096 이 된다.
        }

        @Test
        @DisplayName("1023개짜리 맵에 하나를 넣으면 1013개를 공유한다")
        void sharesAlmostEverything() {
            PersistentTreeMap<Integer, String> before = TestTrees.balanced(1023);
            PersistentTreeMap<Integer, String> after = before.put(1, "새 키");

            assertEquals(1024, after.size());
            assertEquals(11, after.nodesCreatedByLastPut());
            assertEquals(1013L, PersistentProblems.countSharedNodes(before, after));
            assertEquals(after.size(), PersistentProblems.countSharedNodes(before, after)
                    + after.nodesCreatedByLastPut(), "새로 만든 것과 공유한 것의 합이 전체다");
        }

        @Test
        @DisplayName("지울 때도 경로만 새로 만든다")
        void removeCopiesPathOnly() {
            PersistentTreeMap<Integer, String> before = TestTrees.balanced(1023);
            PersistentTreeMap<Integer, String> after = before.remove(2 * 511);   // 뿌리를 지운다

            assertEquals(1022, after.size());
            assertEquals(9, after.nodesCreatedByLastPut());
            assertEquals(1013L, PersistentProblems.countSharedNodes(before, after));
            assertEquals(1023, before.size(), "옛 버전이 줄었다");
        }
    }

    @Nested
    @DisplayName("균형을 안 잡는 대가")
    class NoBalancing {

        @Test
        @DisplayName("정렬 입력에서는 공유가 0 이 된다")
        void sortedInputDestroysSharing() {
            int n = 1000;
            PersistentTreeMap<Integer, String> map = PersistentTreeMap.empty();
            long created = 0;
            for (int i = 0; i < n; i++) {
                map = map.put(i, "v");
                created += map.nodesCreatedByLastPut();
            }
            assertEquals(n, map.height(), "06번과 똑같이 한 줄이 된다");
            assertEquals(500_500L, created, "n(n+1)/2 개를 만들었다");

            PersistentTreeMap<Integer, String> next = map.put(n, "v");
            assertEquals(n + 1, next.nodesCreatedByLastPut(), "put 하나가 트리 전체를 새로 만든다");
            assertEquals(0L, PersistentProblems.countSharedNodes(map, next),
                    "경로가 곧 트리 전체라 공유할 것이 하나도 없다");

            // 06번에서는 정렬 입력이 O(n) 조회를 뜻했다. 여기서는 O(n) **메모리**까지 뜻한다.
            // 버전 하나가 n 개의 노드를 쓰므로 영속 자료구조의 이점이 통째로 사라진다.
            PersistentTreeMap<Integer, String> balanced = TestTrees.balanced(n);
            assertEquals(10, balanced.height());
            assertEquals(11, balanced.put(1, "v").nodesCreatedByLastPut(),
                    "같은 크기라도 균형이 잡혀 있으면 11 개다");
        }
    }

    @Nested
    @DisplayName("가변과의 대비")
    class VersusMutable {

        @Test
        @DisplayName("스냅샷 100개: 노드 1,100개 대 107,350개")
        void snapshotsAreAlmostFree() {
            PersistentTreeMap<Integer, String> map = TestTrees.balanced(1023);
            long persistent = 0;
            long mutableCopies = 0;
            for (int i = 0; i < 100; i++) {
                map = map.put(2 * i + 1, "v");
                persistent += map.nodesCreatedByLastPut();
                // 가변 맵이라면 시점을 남기기 위해 이 자리에서 통째로 복사해야 한다.
                mutableCopies += map.size();
            }
            assertEquals(1123, map.size());
            assertEquals(1_100L, persistent, "영속: 버전당 11개");
            assertEquals(107_350L, mutableCopies, "가변: 버전당 맵 전체");
            assertTrue(mutableCopies > persistent * 97,
                    "차이가 " + (mutableCopies / persistent) + "배밖에 안 난다");
        }

        @Test
        @DisplayName("버전이 필요 없으면 영속이 11배 손해다")
        void singleVersionIsPureOverhead() {
            PersistentTreeMap<Integer, String> map = TestTrees.balanced(1023);
            long persistent = 0;
            int updates = 1000;
            for (int i = 0; i < updates; i++) {
                map = map.put(2 * i + 1, "v");
                persistent += map.nodesCreatedByLastPut();
            }
            assertEquals(11_000L, persistent);
            // 가변 BST 는 put 하나에 잎 노드 하나만 만든다. 1000번이면 1000개다.
            assertEquals(11.0, (double) persistent / updates, 0.001,
                    "같은 일을 하는데 노드를 11배 만든다");

            // 이것이 이 자료구조의 대가다. 옛 버전을 아무도 안 볼 것이라면
            // 만든 노드 10,000개가 전부 GC 로 갈 쓰레기다.
            // 영속이 이기는 것은 버전이 필요할 때뿐이다.
        }
    }

    @Nested
    @DisplayName("불변이라는 것을 구조로 못 박는다")
    class ImmutabilityIsStructural {

        @Test
        @DisplayName("노드의 필드가 전부 final 이다")
        void nodeFieldsAreFinal() {
            for (Field f : PersistentTreeMap.Node.class.getDeclaredFields()) {
                assertTrue(Modifier.isFinal(f.getModifiers()),
                        "Node." + f.getName() + " 이 final 이 아니다. 고칠 수 있으면 공유가 위험해진다");
            }
            for (Field f : PersistentTreeMap.class.getDeclaredFields()) {
                assertTrue(Modifier.isFinal(f.getModifiers()),
                        "PersistentTreeMap." + f.getName() + " 이 final 이 아니다");
            }
            for (Field f : ConsList.class.getDeclaredFields()) {
                assertTrue(Modifier.isFinal(f.getModifiers()),
                        "ConsList." + f.getName() + " 이 final 이 아니다");
            }
        }

        @Test
        @DisplayName("public 메서드 중 void 를 반환하는 것이 하나도 없다")
        void noMutators() {
            for (Class<?> type : new Class<?>[]{PersistentTreeMap.class, ConsList.class}) {
                for (Method m : type.getDeclaredMethods()) {
                    if (!Modifier.isPublic(m.getModifiers())) {
                        continue;
                    }
                    assertNotSame(void.class, m.getReturnType(),
                            type.getSimpleName() + "." + m.getName() + " 이 void 다. "
                                    + "고치는 메서드는 이 자료구조에 있을 수 없다");
                }
            }
            // 02번 List 에는 void 가 여덟 개 있었다. add, remove, clear, reverse ...
            // 여기서는 모든 연산이 새 객체를 반환한다. 그것이 계약이다.
        }

        @Test
        @DisplayName("synchronized 가 한 군데도 없다")
        void noLocks() {
            for (Class<?> type : new Class<?>[]{PersistentTreeMap.class, ConsList.class,
                    PersistentTreeMap.Node.class}) {
                for (Method m : type.getDeclaredMethods()) {
                    assertTrue(!Modifier.isSynchronized(m.getModifiers()),
                            type.getSimpleName() + "." + m.getName() + " 에 잠금이 있다");
                }
            }
            // 10번 LRU 캐시, 14번 유니온파인드, 23번 스플레이 트리는 조회가 쓰기라 잠금이 필요했다.
            // 아무도 고치지 않으면 경쟁 자체가 성립하지 않는다. LockFreeReadTest 가 그것을 돌려본다.
        }
    }
}
