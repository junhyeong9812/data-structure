package com.datastructure.lsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 한계 측정 3: 지운 것이 사라지지 않는다.
 *
 * 그리고 이 박스에서 제일 위험한 함정이 여기 있다.
 * 부분 compaction 에서 tombstone 을 지우면 삭제가 되살아난다.
 */
@DisplayName("한계 측정: tombstone")
class TombstoneTest {

    /** 여러 층을 최신부터 훑는 조회. LsmTree.get 이 하는 일을 테스트 안에서 그대로 재현한다. */
    private static String layeredGet(List<SSTable<Integer, String>> newestFirst, int key) {
        for (SSTable<Integer, String> table : newestFirst) {
            Object found = table.rawValue(key);
            if (found != null) {
                return Tombstone.is(found) ? null : (String) found;
            }
        }
        return null;
    }

    @Nested
    @DisplayName("지워도 자리를 차지한다")
    class StillThere {

        @Test
        @DisplayName("삭제가 저장 공간을 늘린다")
        void deleteGrowsStorage() {
            LsmTree<Integer, String> t = new LsmTree<>(1000);
            for (int i = 0; i < 10; i++) {
                t.put(i, "v" + i);
            }
            t.flush();
            assertEquals(10, t.storedEntryCount());

            t.delete(3);
            t.flush();
            assertEquals(11, t.storedEntryCount(), "지웠는데 하나 늘었다");
            assertEquals(9, t.size());
            assertNull(t.get(3));
            assertEquals(1, t.sstableAt(0).tombstoneCount());
            assertTrue(t.spaceAmplification() > 1.0);
        }

        @Test
        @DisplayName("compact 해야 없어진다")
        void compactionRemovesThem() {
            LsmTree<Integer, String> t = new LsmTree<>(1000);
            for (int i = 0; i < 10; i++) {
                t.put(i, "v" + i);
            }
            t.flush();
            for (int i = 0; i < 10; i += 2) {
                t.delete(i);
            }
            t.flush();
            assertEquals(15, t.storedEntryCount(), "10개 + tombstone 5개");

            t.compact();
            assertEquals(5, t.storedEntryCount());
            assertEquals(0, t.sstableAt(0).tombstoneCount(), "맨 아래층까지 합쳤으니 지워도 된다");
            assertEquals(List.of(1, 3, 5, 7, 9), t.keys());
        }

        @Test
        @DisplayName("tombstone 도 순차 쓰기 바이트를 낸다")
        void tombstonesCostBytes() {
            LsmTree<Integer, String> t = new LsmTree<>(1000);
            t.delete(7);
            t.flush();
            assertEquals(9, t.sequentialBytesWritten(), "머리 8 + 키 1 + 값 0");
            assertEquals(1, t.storedEntryCount());
            assertEquals(0, t.size());
            assertEquals(1.0, t.spaceAmplification(), 1e-9, "산 키가 없으면 분모를 1 로 본다");
        }
    }

    @Nested
    @DisplayName("함정: 부분 compaction 에서 tombstone 을 지우면 삭제가 되살아난다")
    class Resurrection {

        /** 층 3장. 0번(최신) = {1:a}, 1번 = {7:tombstone}, 2번(가장 오래됨) = {7:old} */
        private LsmTree<Integer, String> threeLayers() {
            LsmTree<Integer, String> t = new LsmTree<>(1000);
            t.put(7, "old");
            t.flush();
            t.delete(7);
            t.flush();
            t.put(1, "a");
            t.flush();
            return t;
        }

        @Test
        @DisplayName("맨 아래층을 안 합쳤으면 tombstone 을 남겨야 한다")
        void partialCompactionKeepsTombstone() {
            LsmTree<Integer, String> t = threeLayers();
            assertEquals(3, t.sstableCount());
            assertNull(t.get(7));

            t.compactNewest(2);

            assertEquals(2, t.sstableCount());
            assertEquals(1, t.sstableAt(0).tombstoneCount(),
                    "아래에 옛 값이 남아 있으므로 tombstone 을 데리고 가야 한다");
            assertNull(t.get(7), "지운 키가 되살아났다");
            assertEquals(List.of(1), t.keys());
        }

        @Test
        @DisplayName("거기서 tombstone 을 지우면 실제로 옛 값이 돌아온다")
        void droppingItResurrectsTheOldValue() {
            // **왜 이 함정이 무서운가.** 컴파일도 되고 예외도 안 나고, 합친 직후에는
            // 최신 두 장만 보면 멀쩡해 보인다. 아래층이 살아 있는 동안만 조용히 틀린다.
            LsmTree<Integer, String> t = threeLayers();
            SSTable<Integer, String> newest = t.sstableAt(0);
            SSTable<Integer, String> middle = t.sstableAt(1);
            SSTable<Integer, String> oldest = t.sstableAt(2);
            assertEquals("old", oldest.rawValue(7), "가장 오래된 층에 옛 값이 그대로 있다");

            SSTable<Integer, String> wrong =
                    Compactor.compact(List.of(newest, middle), true, false);
            assertNull(wrong.rawValue(7), "tombstone 을 지우면 그 키의 흔적이 통째로 없어진다");
            assertEquals("old", layeredGet(List.of(wrong, oldest), 7),
                    "그래서 조회가 아래층까지 내려가 죽은 값을 물어온다");

            SSTable<Integer, String> right =
                    Compactor.compact(List.of(newest, middle), false, false);
            assertNotNull(right.rawValue(7));
            assertTrue(Tombstone.is(right.rawValue(7)));
            assertNull(layeredGet(List.of(right, oldest), 7), "남겨두면 삭제가 유지된다");
        }

        @Test
        @DisplayName("맨 아래층까지 합치면 그때 지운다")
        void bottommostCompactionDropsIt() {
            LsmTree<Integer, String> t = threeLayers();
            t.compactNewest(3);

            assertEquals(1, t.sstableCount());
            assertEquals(0, t.sstableAt(0).tombstoneCount(),
                    "아래에 아무것도 없으면 되살아날 옛 값도 없다");
            assertEquals(1, t.storedEntryCount(), "키 1 만 남는다");
            assertNull(t.get(7));
            assertEquals(List.of(1), t.keys());
        }

        @Test
        @DisplayName("두 번에 나눠 합쳐도 답이 같다")
        void twoStepCompactionIsStillCorrect() {
            LsmTree<Integer, String> t = threeLayers();
            t.compactNewest(2);
            assertNull(t.get(7));
            t.compactNewest(2);
            assertNull(t.get(7));
            assertEquals(1, t.sstableCount());
            assertEquals(0, t.sstableAt(0).tombstoneCount());
            assertEquals(List.of(1), t.keys());
        }

        @Test
        @DisplayName("여러 키가 섞여 있어도 같다")
        void manyKeys() {
            LsmTree<Integer, String> t = new LsmTree<>(1000);
            for (int i = 0; i < 20; i++) {
                t.put(i, "old" + i);
            }
            t.flush();
            for (int i = 0; i < 20; i += 2) {
                t.delete(i);
            }
            t.flush();
            for (int i = 0; i < 20; i += 4) {
                t.put(i, "new" + i);
            }
            t.flush();
            assertEquals(3, t.sstableCount());

            List<Map.Entry<Integer, String>> before = t.rangeScan(0, 100);
            t.compactNewest(2);
            assertEquals(before, t.rangeScan(0, 100), "부분 compaction 이 답을 바꾸면 안 된다");
            t.compact();
            assertEquals(before, t.rangeScan(0, 100), "전체 compaction 도 마찬가지다");
            assertEquals(before.size(), t.storedEntryCount(), "이제 산 것만 남는다");
        }
    }
}
