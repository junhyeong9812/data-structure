package com.datastructure.lsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** KeyValueStore 계약. 층이 몇 개든, 언제 flush 하든 답은 하나여야 한다. */
@DisplayName("LsmTree")
class LsmTreeTest {

    private static LsmTree<Integer, String> tree(int threshold) {
        return new LsmTree<>(threshold);
    }

    private static List<Map.Entry<Integer, String>> pairs(Object... kv) {
        List<Map.Entry<Integer, String>> out = new ArrayList<>();
        for (int i = 0; i < kv.length; i += 2) {
            out.add(Map.entry((Integer) kv[i], (String) kv[i + 1]));
        }
        return out;
    }

    @Nested
    @DisplayName("빈 저장소")
    class Empty {

        @Test
        @DisplayName("아무것도 없다")
        void nothing() {
            LsmTree<Integer, String> t = tree(4);
            assertEquals(0, t.size());
            assertTrue(t.isEmpty());
            assertNull(t.get(1));
            assertFalse(t.containsKey(1));
            assertEquals(List.of(), t.keys());
            assertEquals(List.of(), t.rangeScan(0, 100));
            assertEquals(0, t.sstableCount());
            assertEquals(0, t.storedEntryCount());
            assertEquals(0, t.diskReads());
            assertEquals(0, t.sequentialBytesWritten());
        }

        @Test
        @DisplayName("빈 memtable 을 flush 해도 SSTable 이 안 생긴다")
        void flushOfEmptyMemtableIsNothing() {
            LsmTree<Integer, String> t = tree(4);
            t.flush();
            t.flush();
            assertEquals(0, t.sstableCount());
            assertEquals(0, t.sequentialBytesWritten(), "안 썼으면 0 바이트다");
            t.compact();
            assertEquals(0, t.sstableCount());
        }

        @Test
        @DisplayName("없는 키를 지워도 tombstone 은 쓰인다")
        void deleteOfAbsentKeyStillWrites() {
            // 지울 것이 있는지 확인하려면 읽어야 한다. LSM 은 그 확인을 안 한다.
            LsmTree<Integer, String> t = tree(100);
            t.delete(7);
            assertNull(t.get(7));
            assertEquals(0, t.size(), "산 키는 없다");
            assertEquals(1, t.storedEntryCount(), "그래도 tombstone 하나를 들고 있다");
        }
    }

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("넣고 꺼낸다")
        void putGet() {
            LsmTree<Integer, String> t = tree(100);
            t.put(5, "e");
            t.put(3, "c");
            t.put(8, "h");
            assertEquals("e", t.get(5));
            assertEquals("c", t.get(3));
            assertNull(t.get(4));
            assertEquals(3, t.size());
            assertTrue(t.containsKey(8));
            assertEquals(0, t.diskReads(), "전부 memtable 에 있으면 디스크를 안 읽는다");
        }

        @Test
        @DisplayName("정렬 순서로 나온다")
        void sorted() {
            LsmTree<Integer, String> t = tree(3);
            for (int k : new int[]{9, 1, 7, 3, 5, 2}) {
                t.put(k, "v" + k);
            }
            assertEquals(List.of(1, 2, 3, 5, 7, 9), t.keys());
        }

        @Test
        @DisplayName("null 은 거부한다")
        void rejectsNull() {
            LsmTree<Integer, String> t = tree(4);
            assertThrows(IllegalArgumentException.class, () -> t.put(null, "a"));
            assertThrows(IllegalArgumentException.class, () -> t.put(1, null),
                    "값의 null 은 tombstone 과 구별이 안 된다");
            assertThrows(IllegalArgumentException.class, () -> t.get(null));
            assertThrows(IllegalArgumentException.class, () -> t.delete(null));
            assertThrows(IllegalArgumentException.class, () -> t.rangeScan(null, 3));
        }

        @Test
        @DisplayName("임계치는 1 이상이다")
        void thresholdMustBePositive() {
            assertThrows(IllegalArgumentException.class, () -> new LsmTree<Integer, String>(0));
            assertThrows(IllegalArgumentException.class, () -> new LsmTree<Integer, String>(-1));
        }

        @Test
        @DisplayName("rangeScan 은 양끝을 포함한다")
        void rangeScanIsInclusive() {
            LsmTree<Integer, String> t = tree(2);
            for (int i = 0; i < 10; i++) {
                t.put(i, "v" + i);
            }
            assertEquals(pairs(3, "v3", 4, "v4", 5, "v5"), t.rangeScan(3, 5));
            assertEquals(pairs(9, "v9"), t.rangeScan(9, 100));
            assertEquals(List.of(), t.rangeScan(5, 3), "뒤집힌 범위는 빈 결과다");
        }
    }

    @Nested
    @DisplayName("자동 flush")
    class AutoFlush {

        @Test
        @DisplayName("임계치에 닿으면 SSTable 이 생긴다")
        void flushesAtThreshold() {
            LsmTree<Integer, String> t = tree(4);
            for (int i = 0; i < 3; i++) {
                t.put(i, "v" + i);
            }
            assertEquals(0, t.sstableCount(), "아직 3개다");
            assertEquals(3, t.memtableSize());

            t.put(3, "v3");
            assertEquals(1, t.sstableCount(), "4개가 되는 순간 쏟아진다");
            assertEquals(0, t.memtableSize(), "memtable 은 비워진다");
            assertEquals(4, t.storedEntryCount());
            for (int i = 0; i < 4; i++) {
                assertEquals("v" + i, t.get(i), "flush 뒤에도 읽혀야 한다");
            }
        }

        @Test
        @DisplayName("12개를 임계 4로 넣으면 3장이 된다")
        void threeTables() {
            LsmTree<Integer, String> t = tree(4);
            for (int i = 0; i < 12; i++) {
                t.put(i, "v" + i);
            }
            assertEquals(3, t.sstableCount());
            assertEquals(3, t.flushCount());
            assertEquals(12, t.storedEntryCount());
            assertEquals(136, t.sequentialBytesWritten(), "파이썬 참조로 검산한 값이다");
            assertEquals(12, t.size());
        }

        @Test
        @DisplayName("같은 키만 넣으면 flush 가 안 일어난다")
        void sameKeyNeverFills() {
            // memtable 은 맵이라 같은 키를 덮어쓴다. 임계는 키 개수로 잰다.
            LsmTree<Integer, String> t = tree(4);
            for (int i = 0; i < 100; i++) {
                t.put(42, "v" + i);
            }
            assertEquals(0, t.sstableCount());
            assertEquals(1, t.memtableSize());
            assertEquals("v99", t.get(42));
        }
    }

    @Nested
    @DisplayName("층 순서가 정확성을 만든다")
    class LayerOrder {

        @Test
        @DisplayName("최신 층의 값이 이긴다")
        void newestWins() {
            LsmTree<Integer, String> t = tree(100);
            for (int i = 0; i < 5; i++) {
                t.put(1, "v" + i);
                t.flush();
            }
            assertEquals(5, t.sstableCount());
            assertEquals("v4", t.get(1), "오래된 층부터 보면 v0 이 나온다");
            assertEquals(pairs(1, "v4"), t.rangeScan(0, 10), "rangeScan 도 최신 우선이다");

            t.put(1, "memtable");
            assertEquals("memtable", t.get(1), "memtable 이 SSTable 보다 최신이다");
            assertEquals(pairs(1, "memtable"), t.rangeScan(0, 10));
        }

        @Test
        @DisplayName("삭제한 뒤 다시 넣으면 살아난다")
        void deleteThenPut() {
            LsmTree<Integer, String> t = tree(100);
            t.put(1, "a");
            t.flush();
            t.delete(1);
            t.flush();
            assertNull(t.get(1));
            t.put(1, "b");
            t.flush();
            assertEquals("b", t.get(1), "tombstone 보다 최신인 값이 이겨야 한다");
            assertEquals(List.of(1), t.keys());
            assertEquals(3, t.storedEntryCount(), "층에는 세 개가 다 남아 있다");
        }

        @Test
        @DisplayName("층마다 다른 키를 갖고 있어도 합쳐 보인다")
        void keysSpreadOverLayers() {
            LsmTree<Integer, String> t = tree(100);
            t.put(1, "a");
            t.flush();
            t.put(3, "c");
            t.flush();
            t.put(2, "b");
            assertEquals(List.of(1, 2, 3), t.keys());
            assertEquals(pairs(1, "a", 2, "b", 3, "c"), t.rangeScan(0, 9));
            assertEquals(3, t.size());
        }
    }

    @Nested
    @DisplayName("compact")
    class Compaction {

        @Test
        @DisplayName("전부 하나로 합친다")
        void mergesIntoOne() {
            LsmTree<Integer, String> t = tree(4);
            for (int i = 0; i < 40; i++) {
                t.put(i, "v" + i);
            }
            assertEquals(10, t.sstableCount());
            List<Integer> before = t.keys();

            t.compact();
            assertEquals(1, t.sstableCount());
            assertEquals(1, t.compactionCount());
            assertEquals(before, t.keys(), "합쳐도 내용은 같다");
            assertEquals(40, t.storedEntryCount());
        }

        @Test
        @DisplayName("memtable 도 먼저 굳힌다")
        void compactFlushesFirst() {
            LsmTree<Integer, String> t = tree(100);
            t.put(1, "a");
            assertEquals(0, t.sstableCount());
            t.compact();
            assertEquals(1, t.sstableCount());
            assertEquals(0, t.memtableSize());
            assertEquals("a", t.get(1));
        }

        @Test
        @DisplayName("전부 지운 뒤 compact 하면 SSTable 이 사라진다")
        void everythingDeleted() {
            LsmTree<Integer, String> t = tree(4);
            for (int i = 0; i < 20; i++) {
                t.put(i, "v" + i);
            }
            for (int i = 0; i < 20; i++) {
                t.delete(i);
            }
            assertTrue(t.storedEntryCount() > 0);
            t.compact();
            assertEquals(0, t.sstableCount(), "tombstone 도 산 값도 없으면 남길 것이 없다");
            assertEquals(0, t.storedEntryCount());
            assertEquals(0, t.size());
        }

        @Test
        @DisplayName("compact 를 두 번 해도 같다")
        void idempotent() {
            LsmTree<Integer, String> t = tree(4);
            for (int i = 0; i < 30; i++) {
                t.put(i % 7, "v" + i);
            }
            t.compact();
            List<Map.Entry<Integer, String>> once = t.rangeScan(0, 100);
            long stored = t.storedEntryCount();
            t.compact();
            assertEquals(once, t.rangeScan(0, 100));
            assertEquals(stored, t.storedEntryCount());
            assertEquals(1, t.sstableCount());
        }
    }
}
