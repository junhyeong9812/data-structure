package com.datastructure.persistent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("VersionedStore: 시간 축")
class VersionedStoreTest {

    @Nested
    @DisplayName("아무 시점이나 되돌아본다")
    class TimeTravel {

        @Test
        @DisplayName("빈 저장소는 0번 버전이다")
        void startsAtZero() {
            VersionedStore<String, String> store = new VersionedStore<>();
            assertEquals(0, store.currentVersion());
            assertEquals(1, store.versionCount());
            assertNull(store.get("a"));
            assertEquals(0L, store.nodesCreated());
            assertTrue(store.snapshot(0).isEmpty());
        }

        @Test
        @DisplayName("커밋할 때마다 버전이 하나씩 늘어난다")
        void versionsGrow() {
            VersionedStore<String, String> store = new VersionedStore<>();
            assertEquals(1, store.put("a", "1"));
            assertEquals(2, store.put("b", "2"));
            assertEquals(3, store.put("a", "3"));
            assertEquals(3, store.currentVersion());
            assertEquals(4, store.versionCount());
        }

        @Test
        @DisplayName("그 시점의 값을 그대로 준다")
        void readsAnyVersion() {
            VersionedStore<String, String> store = new VersionedStore<>();
            store.put("a", "처음");
            store.put("b", "둘째");
            store.put("a", "고침");
            store.remove("b");

            assertNull(store.get(0, "a"));
            assertEquals("처음", store.get(1, "a"));
            assertNull(store.get(1, "b"));
            assertEquals("둘째", store.get(2, "b"));
            assertEquals("처음", store.get(2, "a"));
            assertEquals("고침", store.get(3, "a"));
            assertEquals("둘째", store.get(3, "b"));
            assertNull(store.get(4, "b"));
            assertEquals("고침", store.get("a"));
            assertEquals(List.of("a"), store.snapshot(4).keys());
        }

        @Test
        @DisplayName("없는 버전은 거부한다")
        void rejectsUnknownVersion() {
            VersionedStore<String, String> store = new VersionedStore<>();
            store.put("a", "1");
            assertThrows(IndexOutOfBoundsException.class, () -> store.get(2, "a"));
            assertThrows(IndexOutOfBoundsException.class, () -> store.get(-1, "a"));
            assertThrows(IndexOutOfBoundsException.class, () -> store.snapshot(99));
        }

        @Test
        @DisplayName("바뀐 것이 없으면 버전도 늘지 않는다")
        void noopDoesNotCommit() {
            VersionedStore<String, String> store = new VersionedStore<>();
            store.put("a", "1");
            assertEquals(1, store.remove("없는키"), "없는 키를 지우면 상태가 같다");
            assertEquals(1, store.currentVersion());
            assertEquals(2, store.versionCount());
        }
    }

    @Nested
    @DisplayName("undo 와 redo")
    class UndoRedo {

        @Test
        @DisplayName("뒤로 갔다가 앞으로 온다")
        void backAndForward() {
            VersionedStore<String, String> store = new VersionedStore<>();
            store.put("a", "1");
            store.put("a", "2");
            store.put("a", "3");

            assertTrue(store.undo());
            assertEquals("2", store.get("a"));
            assertTrue(store.undo());
            assertEquals("1", store.get("a"));
            assertTrue(store.undo());
            assertNull(store.get("a"));

            assertFalse(store.undo(), "0번 버전보다 앞은 없다");
            assertEquals(0, store.currentVersion());

            assertTrue(store.redo());
            assertEquals("1", store.get("a"));
            assertTrue(store.redo());
            assertTrue(store.redo());
            assertEquals("3", store.get("a"));
            assertFalse(store.redo(), "마지막 버전보다 뒤는 없다");
        }

        @Test
        @DisplayName("되돌린 뒤 새로 쓰면 redo 가지가 버려진다")
        void writingAfterUndoDropsTheRedoBranch() {
            VersionedStore<String, String> store = new VersionedStore<>();
            store.put("a", "1");
            store.put("a", "2");
            store.undo();

            assertEquals(1, store.currentVersion());
            store.put("a", "새 가지");
            assertEquals(3, store.currentVersion(), "버전 번호는 재사용하지 않는다");
            assertFalse(store.redo(), "버려진 가지로는 redo 할 수 없다");
            assertEquals("새 가지", store.get("a"));

            // 여기서 뒤로 가면 2번이 아니라 1번이어야 한다. 2번은 이 이력에 없는 가지다.
            assertTrue(store.undo());
            assertEquals(1, store.currentVersion(), "버려진 가지로 걸어 들어갔다");
            assertEquals("1", store.get("a"));
            assertTrue(store.redo());
            assertEquals(3, store.currentVersion(), "redo 가 버려진 가지로 갔다");
            assertEquals("새 가지", store.get("a"));

            // 그런데 버려진 버전도 조회는 된다. 아무도 안 가리킬 뿐 메모리에는 살아 있다.
            // git 에서 브랜치를 옮긴 뒤에도 옛 커밋이 해시로 남아 있는 것과 같다.
            assertEquals("2", store.get(2, "a"));
            assertEquals(4, store.versionCount());
        }

        @Test
        @DisplayName("undo 는 버전을 지우지 않는다")
        void undoKeepsHistory() {
            VersionedStore<String, String> store = new VersionedStore<>();
            for (int i = 1; i <= 10; i++) {
                store.put("k", "v" + i);
            }
            for (int i = 0; i < 5; i++) {
                assertTrue(store.undo());
            }
            assertEquals(5, store.currentVersion());
            assertEquals(11, store.versionCount());
            assertEquals("v10", store.get(10, "k"), "undo 로 지나온 미래도 그대로 있다");
        }
    }

    @Nested
    @DisplayName("버전 하나가 O(log n) 메모리다")
    class VersionCost {

        @Test
        @DisplayName("1023개짜리 저장소에 100번 쓰면 노드 1,100개가 는다")
        void hundredVersionsCostElevenNodesEach() {
            VersionedStore<Integer, String> store = new VersionedStore<>();
            for (int x : TestTrees.balancedOrder(1023)) {
                store.put(2 * x, "v");
            }
            long afterBuild = store.nodesCreated();
            assertEquals(9_217L, afterBuild, "1023개를 균형 순서로 지은 비용");

            for (int i = 0; i < 100; i++) {
                store.put(2 * i + 1, "v");
            }
            assertEquals(1_100L, store.nodesCreated() - afterBuild, "버전당 11개");

            // 가변 맵으로 같은 100 시점을 남기려면 시점마다 통째로 복사해야 한다.
            long mutableSnapshots = 0;
            for (int i = 0; i < 100; i++) {
                mutableSnapshots += 1023 + i + 1;
            }
            assertEquals(107_350L, mutableSnapshots);
            assertTrue(mutableSnapshots > 97 * 1_100L);

            // 그리고 그 100개 버전이 전부 살아 있다.
            assertEquals(1123, store.versionCount() - 1);
            assertEquals(1023, store.snapshot(1023).size());
            assertEquals(1123, store.snapshot(1123).size());
            assertNull(store.get(1023, 1), "1023번 버전은 홀수 키를 모른다");
            assertEquals("v", store.get(1024, 1));
        }

        @Test
        @DisplayName("스냅샷은 그냥 그 시점의 맵이다. 복사가 아니다")
        void snapshotIsTheMapItself() {
            VersionedStore<Integer, String> store = new VersionedStore<>();
            store.put(1, "a");
            PersistentTreeMap<Integer, String> first = store.snapshot(1);
            store.put(2, "b");
            store.put(3, "c");

            assertSame(first, store.snapshot(1), "스냅샷을 부를 때마다 복사하고 있다");
            assertEquals(1, first.size(), "1번 버전이 나중 쓰기에 끌려 다녔다");
            assertEquals(3, store.snapshot(3).size());

            // 불변이므로 스냅샷을 밖으로 그냥 넘겨도 안전하다. 받은 쪽이 고칠 방법이 없다.
            // 가변 맵이었다면 여기서 방어적 복사를 해야 했고, 그 복사가 O(n) 이다.
            assertSame(store.snapshot(2), store.snapshot(2));
        }
    }
}
